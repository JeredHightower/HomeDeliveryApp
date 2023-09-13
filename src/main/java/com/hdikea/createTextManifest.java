package com.hdikea;

import org.bytedeco.javacpp.*;
import org.bytedeco.leptonica.*;
import org.bytedeco.tesseract.*;
import static org.bytedeco.leptonica.global.leptonica.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class createTextManifest {

    public static ArrayList<customer> relevantText(String sourceDir) {

        Scanner relText = new Scanner(getText(sourceDir));

        if(!relText.hasNextLine()){
            System.out.println("Error, nothing found");
            return null;
        }

        relText.nextLine();

        int counter = 1;
        boolean getTruckNumber = true;
        boolean getOrderNumber = false;
        boolean getName = false;

        String orderNumber = "";
        String name = "";
        String truckNumber = "";

        ArrayList<customer> customers = new ArrayList<>();

        while (relText.hasNextLine()) {
            String currentLine = relText.nextLine();

            if (getTruckNumber) {
                Pattern p = Pattern.compile(" (\\d{4})");
                Matcher m = p.matcher(currentLine);

                if (m.find()) {
                    truckNumber = m.group(1);

                    getTruckNumber = false;
                    getOrderNumber = true;
                    getName = true;
                }
            } else if (getOrderNumber) {
                Pattern p = Pattern.compile("(?:LCD|CCD).*?(\\d{9})");
                Matcher m = p.matcher(currentLine);

                if (m.find()) {
                    orderNumber = m.group(1);

                    getOrderNumber = false;
                    getName = true;
                }
            } else if (getName) {
                Pattern p = Pattern.compile("Missing");
                Matcher m = p.matcher(currentLine);

                if (m.find()) {
                    name = currentLine.substring(0, m.start()).trim();
                    customers.add(new customer(orderNumber, name, counter++, truckNumber));

                    getName = false;
                    getOrderNumber = true;
                }
            }
        }

        return customers;
    }

    private static String getText(String sourceDir) {
        ArrayList<String> createdFiles = pdf2image.convertManifest(sourceDir);

        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init("language", "eng") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        String allText = "";
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();
        Arrays.sort(files);
        for (File file : files)
            if (!file.isDirectory() && file.getName().endsWith("png")) {
                // Open input image with leptonica library
                PIX image = pixRead(file.getName());
                api.SetImage(image);

                // Get OCR result
                outText = api.GetUTF8Text();
                String newText = outText.getString();

                // System.out.println("OCR output:\n" + newText);
                allText += newText;

                pixDestroy(image);
                outText.deallocate();
            }

        // Destroy used object and release memory
        api.close();
        pdf2image.deleteTempFiles(createdFiles);

        return allText;
    }
}