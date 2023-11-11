package com.hdikea;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacpp.*;
import org.bytedeco.leptonica.*;
import org.bytedeco.tesseract.*;
import static org.bytedeco.leptonica.global.leptonica.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class createTextManifest {
    
    /*
     * Calls getText using a filepath pointing to a pdf and uses regex to extract relevant information for manifest
     * returns a list of customers objects
     * Returns null if no text is returned from getText
     */
    public ArrayList<customer> relevantText(String sourceDir) {

        Scanner relText = new Scanner(getText(sourceDir));

        if (!relText.hasNextLine()) {
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
        String header = "";

        ArrayList<customer> customers = new ArrayList<>();

        while (relText.hasNextLine()) {
            String currentLine = relText.nextLine();
            // System.out.println(currentLine);

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
                Pattern p = Pattern.compile("((?:LCD|CCD|LNK).*?)(\\d{9})");
                Matcher m = p.matcher(currentLine);

                if (m.find()) {
                    header = m.group(1);
                    orderNumber = m.group(2);

                    getOrderNumber = false;
                    getName = true;
                }
            } else if (getName) {
                Pattern p = Pattern.compile("Missing");
                Matcher m = p.matcher(currentLine);

                if (m.find()) {
                    name = currentLine.substring(0, m.start()).trim();
                    customers.add(new customer(orderNumber, name, counter++, truckNumber, header));

                    getName = false;
                    getOrderNumber = true;
                }
            }
        }

        return customers;
    }

    /*
     * Takes a filepath pointing to a pdf and calls convertManifest to change it into a png
     * Then using Tesseract OCR, converts the png to text and returns all text as a single string
     */
    private String getText(String sourceDir) {
        pdf2image p = new pdf2image();
        ArrayList<File> createdFiles = p.convertManifest(sourceDir);

        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();

        // Initialize tesseract-ocr with English by creating a temp file
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("eng.traineddata");
        File somethingFile;
        try {
            somethingFile = File.createTempFile("eng", ".traineddata");
            FileUtils.copyInputStreamToFile(inputStream, somethingFile);
        } catch (IOException e) {
            somethingFile = null;
            System.out.println("file not found");
            System.exit(1);
            e.printStackTrace();
        }

        if (api.Init(somethingFile.getParent(),
                FilenameUtils.getBaseName(somethingFile.getName())) != 0) {
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

                allText += newText;

                pixDestroy(image);
                outText.deallocate();
            }

        // Destroy used object and release memory
        api.close();
        p.deleteTempFiles(createdFiles);

        return allText;
    }
}