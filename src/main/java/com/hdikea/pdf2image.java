package com.hdikea;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class pdf2image {

    public static ArrayList<String> convertManifest(String sourceDir) {
        try {
            File sourceFile = new File(sourceDir);
            ArrayList<String> createdFiles = new ArrayList<>();

            if (sourceFile.exists() && sourceFile.getName().endsWith("pdf")) {
                PDDocument document = PDDocument.load(sourceFile);
                
                // System.out.println("Total files to be converted -> " + (document.getNumberOfPages() - 1));

                PDFRenderer pdfRenderer = new PDFRenderer(document);

                int max_pages = 6;
                if(document.getNumberOfPages() < max_pages){
                    max_pages = document.getNumberOfPages();
                }
                for (int pageCounter = 1; pageCounter < max_pages; pageCounter++) {

                    // note that the page number parameter is zero based
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);

                    // suffix in filename will be used as the file format
                    ImageIOUtil.writeImage(bim, sourceFile.getName() + "-" + (pageCounter) + ".png", 300);
                    createdFiles.add(sourceFile.getName() + "-" + (pageCounter) + ".png");
                }
                document.close();

                return createdFiles;

            } else {
                System.err.println(sourceFile.getName() + " File not exists or Not PDF");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteTempFiles(ArrayList<String> files) {
        File dir = new File(System.getProperty("user.dir"));

        if(files == null)
            return;
            
        for(File file: dir.listFiles()) 
            if (!file.isDirectory() && files.contains(file.getName())) 
                file.delete();
    }
}
