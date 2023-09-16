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
            // Skip title line
            System.out.println(sc.nextLine());
            }

            if (!sc.hasNextLine()) {
                xlsx.close();
                sc.close();
                return null;
            }{
            // Skip first line
            System.out.println(sc.nextLine());
            }

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

    // Must call to change lists
    public ArrayList<customer> crossReferenceAll(ArrayList<customer> allCustomers,
            ArrayList<customer> customersFromLog) {

        if(allCustomers == null | customersFromLog == null)
                return null;

        for (customer customer : allCustomers) {

            if (customer.isReturn())
                customer.location = "Return";

            for (Iterator<customer> it = customersFromLog.iterator(); it.hasNext();) {
                if (customer.isReturn()) {
                    break;
                }

                customer customer2 = it.next();
                if (customer.compareTo(customer2) == 0) {
                    customer.location = customer2.location;
                    customer.carts = customer2.carts;
                    it.remove();
                    break;
                }
            }
        }

        // ORDERS NOT FOUND ON ANY MANIFEST
        return customersFromLog;
    }

    public ArrayList<customer> getAllInformationOneList(String sourceDir) {
        ArrayList<customer> allCustomers = new ArrayList<customer>();

        File dir = new File(sourceDir);
        for (File file : dir.listFiles())
            if (!file.isDirectory() && file.getName().endsWith("pdf")) {

                /// Make this a thread
                createTextManifest c = new createTextManifest();
                ArrayList<customer> customers = c.relevantText(file.getPath());
                
                if(customers == null)
                    return null;

                allCustomers.addAll(customers);
                // Make this a thread
            }

        return allCustomers;
    }
}
