package com.hdikea;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class compareToLog {

    /*
     * Takes a list of customers
     * Returns a hashmap with each key as a truck number and its contents being the list of customers for that truck
     */
    public HashMap<String, ArrayList<customer>> getTrucks(ArrayList<customer> allCustomers) {
        HashMap<String, ArrayList<customer>> trucks = new HashMap<String, ArrayList<customer>>();

        if(allCustomers == null)
            return null;

        for (customer customer : allCustomers) {
            if (!trucks.containsKey(customer.truckNumber))
                trucks.put(customer.truckNumber, new ArrayList<customer>());

            trucks.get(customer.truckNumber).add(customer);
        }

        return trucks;
    }


    /*
     * Takes a filepath to an excel file and converts excel rows into customer objects
     * Returns a list of customers
     */
    public ArrayList<customer> customersFromLog(String logSourceDir) {
        ArrayList<customer> customers = new ArrayList<customer>();

        try {
            File fl = new File(logSourceDir);

            if (!fl.getName().endsWith("xlsx") || !fl.exists())
                return null;

            XSSFWorkbook input = new XSSFWorkbook(fl);

            XSSFExcelExtractor xlsx = new XSSFExcelExtractor(input);
            String tsv = xlsx.getText();
            Scanner sc = new Scanner(tsv);

            if (!sc.hasNextLine()) {
                xlsx.close();
                sc.close();
                return null;
            }
            else{
            // Skip title line (Sheet name)
            System.out.println(sc.nextLine());
            }

            if (!sc.hasNextLine()) {
                xlsx.close();
                sc.close();
                return null;
            }

            // Skip first line (Column names)
            System.out.println(sc.nextLine());
            

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.isEmpty()) {
                    String[] item = line.split("\t");
                    String name = "";
                    String carts = "";
                    String orderNumber = "";
                    String location = "";

                    if (item.length > 0)
                        name = item[0].trim();
                    if (item.length > 1)
                        carts = item[1].trim();
                    if (item.length > 2)
                        orderNumber = item[2].trim();
                    if (item.length > 3)
                        location = item[3].trim();

                    customers.add(new customer(orderNumber, name, 0, "", carts, location, ""));
                }
            }

            xlsx.close();
            sc.close();
            return customers;

        } catch (InvalidFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    /*
     * Adds locations to allCustomers using information from customersFromLog
     * Each time a location is added, that entry is removed from customersFromLog
     * Both lists are changed by this
     * Returns the updated customersFromLog
     */
    public ArrayList<customer> crossReferenceAll(ArrayList<customer> allCustomers,
            ArrayList<customer> customersFromLog) {

        ArrayList<customer> exchanges = new ArrayList<>();

        if(allCustomers == null | customersFromLog == null)
                return null;

        for (customer customer : allCustomers) {

            if(customer.isRemoval()){
                customer.location = "Removal";
            }

            if (customer.isReturn() && !customer.isXChange())
                customer.location = "Return";

            if (customer.isCCD()){
                customer.location = "!CCD";
            }

            for (Iterator<customer> it = customersFromLog.iterator(); it.hasNext();) {
                if (customer.isReturn() && !customer.isXChange()) {
                    break;
                }

                if(customer.isRemoval()){
                    break;
                }

                if (customer.isCCD()){
                    break;
                }

                customer customer2 = it.next();
                if (customer.compareTo(customer2) == 0) {
                    customer.location = customer2.location;
                    customer.carts = customer2.carts;

                    // Add exchanges to list to remove later
                    if(customer.isXChange())
                        exchanges.add(customer2);
                    else
                        it.remove();

                    break;
                }
            }
        }

        for (Iterator<customer> it = customersFromLog.iterator(); it.hasNext();){
            if(exchanges.contains(it.next()))
                it.remove();
        }

        // ORDERS NOT FOUND ON ANY MANIFEST
        return customersFromLog;
    }

    /*
     * Takes a directory and extracts all customer information from all pdf files within the directory
     * Returns a list of all customers found from all manifests
     * Returns null if a pdf file is invalid
     */
    public ArrayList<customer> getAllInformationOneList(String sourceDir) {
        ArrayList<customer> allCustomers = new ArrayList<customer>();

        File dir = new File(sourceDir);
        for (File file : dir.listFiles())
            if (!file.isDirectory() && file.getName().endsWith("pdf")) {

                createTextManifest c = new createTextManifest();

                ArrayList<customer> customers = c.relevantText(file.getPath());
                if(customers == null)
                    return null;
                else
                    allCustomers.addAll(customers);
            }

        return allCustomers;
    }
}
