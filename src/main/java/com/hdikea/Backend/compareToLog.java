package com.hdikea.Backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class compareToLog {

    /*
     * Takes a list of customers
     * Returns a hashmap with each key as a truck number and its contents being the
     * list of customers for that truck
     */
    public HashMap<String, ArrayList<customer>> getTrucks(ArrayList<customer> allCustomers) {
        HashMap<String, ArrayList<customer>> trucks = new HashMap<String, ArrayList<customer>>();

        if (allCustomers == null)
            return null;

        for (customer customer : allCustomers) {
            if (!trucks.containsKey(customer.truckNumber))
                trucks.put(customer.truckNumber, new ArrayList<customer>());

            trucks.get(customer.truckNumber).add(customer);
        }

        return trucks;
    }

    /*
     * Takes a filepath to an excel file and converts excel rows into customer
     * objects
     * Returns a list of customers
     */
    public ArrayList<customer> customersFromLog(String logSourceDir) {
        ArrayList<customer> customers = new ArrayList<customer>();

        try {
            File fl = new File(logSourceDir);

            if (!fl.getName().endsWith("xlsx") || !fl.exists())
                return null;

            XSSFWorkbook input = new XSSFWorkbook(fl);

            int nameIndex = 0;
            int cartIndex = 1;
            int orderIndex = 2;
            int locaIndex = 3;

            Iterator<Row> rowIterator = input.getSheetAt(0).iterator();
            DataFormatter formatter = new DataFormatter();

            if (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                short maxCol = row.getLastCellNum();
                for (short i = 0; i < maxCol; i++) {
                    Cell cell = row.getCell(i);
                    String strCell = formatter.formatCellValue(cell).replace("\u00A0", " ").trim();
                    if (strCell.equals("LAST NAME")) {
                        nameIndex = i;
                    }
                    if (strCell.equals("C")) {
                        cartIndex = i;
                    }
                    if (strCell.equals("LOCATION")) {
                        locaIndex = i;
                    }
                    if (strCell.equals("ORDER NUMBER")) {
                        orderIndex = i;
                    }
                }
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String name = "";
                String carts = "";
                String orderNumber = "";
                String location = "";

                name = formatter.formatCellValue(row.getCell(nameIndex)).replace("\u00A0", " ").trim();
                carts = formatter.formatCellValue(row.getCell(cartIndex)).replace("\u00A0", " ").trim();
                orderNumber = formatter.formatCellValue(row.getCell(orderIndex)).replace("\u00A0", " ").trim();
                location = formatter.formatCellValue(row.getCell(locaIndex)).replace("\u00A0", " ").trim();

                if(!orderNumber.isBlank())
                    customers.add(new customer(orderNumber, name, "", "", carts, location, ""));

            }

            input.close();
            return customers;

        } catch (InvalidFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<customer> customersFromSheets(String logSourceDir) {
        ArrayList<customer> customers = new ArrayList<customer>();

        try {
            File fl = new File(logSourceDir);

            if (!fl.getName().endsWith("xlsx") || !fl.exists())
                return null;

            XSSFWorkbook input = new XSSFWorkbook(fl);

            int route = 0;
            int nameIndex = 0;
            int stopIndex = 0;
            int orderIndex = 0;
            int workIndex = 0;

            Iterator<Row> rowIterator = input.getSheetAt(0).iterator();
            DataFormatter formatter = new DataFormatter();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (!formatter.formatCellValue(row.getCell(0)).replace("\u00A0", " ").trim().equals("#")) {

                    String name = "";
                    String orderNumber = "";
                    String stop = "";
                    String workOrderNumber = "";

                    orderNumber = formatter.formatCellValue(row.getCell(orderIndex)).replace("\u00A0", " ").trim();
                    workOrderNumber = formatter.formatCellValue(row.getCell(workIndex)).replace("\u00A0", " ").trim();
                    name = formatter.formatCellValue(row.getCell(nameIndex)).replace("\u00A0", " ").trim();
                    stop = formatter.formatCellValue(row.getCell(stopIndex)).replace("\u00A0", " ").trim();

                    
                    customer newCust = new customer(orderNumber, name, stop, "Route " + route, "", "Missing",
                            workOrderNumber);

                    if (!containsOrderNumber(customers, orderNumber) && !orderNumber.isBlank())
                        customers.add(newCust);
                } else {
                    short maxCol = row.getLastCellNum();
                    for (short i = 0; i < maxCol; i++) {
                        Cell cell = row.getCell(i);
                        String strCell = formatter.formatCellValue(cell).replace("\u00A0", " ").trim();
                        System.out.println(strCell + "!");
                        if (strCell.equals("Customer Name")) {
                            nameIndex = i;
                        }
                        if (strCell.equals("Stop Number")) {
                            stopIndex = i;
                        }
                        if (strCell.equals("Work Order #")) {
                            workIndex = i;
                        }
                        if (strCell.equals("Sales Order #")
                                || formatter.formatCellValue(cell).equals("Sales Order Number")) {
                            orderIndex = i;
                        }
                    }

                    route++;
                }
            }

            input.close();
            return customers;

        } catch (InvalidFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public boolean containsOrderNumber(final List<customer> list, final String order) {
        return list.stream().anyMatch(cust -> order.equals(cust.orderNumber));
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

        if (allCustomers == null | customersFromLog == null)
            return null;

        for (customer customer : allCustomers) {

            if (customer.isRemoval()) {
                customer.location = "Removal";
            }

            if (customer.isReturn() && !customer.isXChange())
                customer.location = "Return";

            if (customer.isCCD()) {
                customer.location = "!CCD";
            }

            for (Iterator<customer> it = customersFromLog.iterator(); it.hasNext();) {
                if (customer.isReturn() && !customer.isXChange()) {
                    break;
                }

                if (customer.isRemoval()) {
                    break;
                }

                if (customer.isCCD()) {
                    break;
                }

                customer customer2 = it.next();
                if (customer.compareTo(customer2) == 0) {
                    customer.location = customer2.location;
                    customer.carts = customer2.carts;

                    // Set exchanges Manager to no carts
                    if (customer.isReturn()) {
                        customer.carts = "";
                    }

                    // Add exchanges to list to remove later
                    if (customer.isXChange())
                        exchanges.add(customer2);
                    else
                        it.remove();

                    break;
                }
            }
        }

        for (Iterator<customer> it = customersFromLog.iterator(); it.hasNext();) {
            if (exchanges.contains(it.next()))
                it.remove();
        }

        // ORDERS NOT FOUND ON ANY MANIFEST
        return customersFromLog;
    }
}
