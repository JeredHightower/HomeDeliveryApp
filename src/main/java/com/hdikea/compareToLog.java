package com.hdikea;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class compareToLog {

    public HashMap<String, ArrayList<customer>> getTrucks(ArrayList<customer> allCustomers) {
        HashMap<String, ArrayList<customer>> trucks = new HashMap<String, ArrayList<customer>>();

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

            if(!fl.getName().endsWith("csv") || !fl.exists())
                return null;

            Scanner sc = new Scanner(new File(logSourceDir));

            if(!sc.hasNextLine())
                return null;

            sc.nextLine();

            while (sc.hasNextLine()) {
                String[] item = sc.nextLine().split(",");
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

                customers.add(new customer(orderNumber, name, 0, "", carts, location));
            }

            return customers;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    // Must call to change lists
    public ArrayList<customer> crossReferenceAll(ArrayList<customer> allCustomers,
            ArrayList<customer> customersFromLog) {

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
                allCustomers.addAll(customers);
                // Make this a thread
            }

        return allCustomers;
    }
}
