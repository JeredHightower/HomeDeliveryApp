package com.hdikea;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class pdf2image {

    /*
     * Takes a pdf file and makes a new copy of each page as a png
     * Returns a list of the files it created
     * Returns null if it failed or ran into an error
     */
    public ArrayList<File> convertManifest(String sourceDir) {
        String targetPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        try {
            File sourceFile = new File(sourceDir);
            ArrayList<File> createdFiles = new ArrayList<>();

            // Check that file exists and is a PDF file
            if (sourceFile.exists() && sourceFile.getName().endsWith("pdf")) {
                PDDocument document = PDDocument.load(sourceFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                
                // Page Limit
                int max_pages = 6;
                if(document.getNumberOfPages() < max_pages){
                    max_pages = document.getNumberOfPages();
                }

                // Skips first page (not useful for manifest)
                for (int pageCounter = 1; pageCounter < max_pages; pageCounter++) {

                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);

                    File outputfile = new File(targetPath +  sourceFile.getName() + "-" + (pageCounter) + ".png");
                    ImageIO.write(bim, "png", outputfile);

                    createdFiles.add(outputfile);
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

    // deletes files created by convertManifest
    public void deleteTempFiles(ArrayList<File> files) {
        if(files == null)
            return;
            
        for(File file: files) 
            file.delete();
    }
}