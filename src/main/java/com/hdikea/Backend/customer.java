package com.hdikea.Backend;

public class customer implements Comparable<customer> {

    public String header;
    public String orderNumber;
    public String name;
    public String stop;
    public String truckNumber;
    public String location = "Missing";
    public String carts = "";

    public customer(String orderNumber, String name, String stop, String truckNumber, String header) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.stop = stop;
        this.truckNumber = truckNumber;
        this.header = header;
    }

    public customer(String orderNumber, String name, String stop, String truckNumber, String carts, String location, String header) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.stop = stop;
        this.truckNumber = truckNumber;
        this.carts = carts;
        this.location = location;
        this.header = header;
    }

    public boolean isReturn() {
        if (name.equals("Manager"))
            return true;

        return false;
    }

    /*
    public boolean isReturn() {
        if (header.contains("RTV"))
            return true;

        return false;
    }
    */

    public boolean isCCD(){
        if(header.contains("CCD"))
            return true;
        
        return false;
    }

    public boolean isXChange() {
        if (header.contains("EXC"))
            return true;

        return false;
    }

    public boolean isRemoval() {
        if (header.contains("RMV"))
            return true;

        return false;
    }

    public String toString() {
        return String.format("%" + -30 + "s" + "%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s" + "%" + -10 + "s" + "%" + -10 + "s", header, orderNumber, name, carts, location, stop);

        // return orderNumber + " " + name + " " + carts + " " + location + " Stop:" + stop;
    }

    @Override
    public int compareTo(customer o) {
        if (orderNumber.equals(o.orderNumber))
            return 0;
        else
            return -1;
    }

}
