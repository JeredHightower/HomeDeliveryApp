package com.hdikea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class HDGraphics extends JFrame {

    JTabbedPane TabbedPane = new JTabbedPane();

    public HDGraphics(String title) {
        super(title);
        // Sample 01: Set Size and Position
        setBounds(100, 100, 1000, 500);

        TabbedPane.addTab("Compare Manifests To Log", new LogPanel());
        TabbedPane.addTab("Compare Manifests", new TwoPanel());
        TabbedPane.addTab("View Manifests", new ViewManifestPanel());
        add(TabbedPane);
    }
}

class ViewManifestPanel extends JPanel {
    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public ViewManifestPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnFolder = new JButton("Select Manifest Folder");
        JTextField folderLoc = new JTextField("", 20);
        folderLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        buttons.add(BtnFolder);
        buttons.add(folderLoc);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));

        add(buttons, BorderLayout.PAGE_START);
        add(TabbedPane, BorderLayout.CENTER);

        BtnFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    folderLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    prefs.put("LAST_USED_FOLDER", fileChooser.getSelectedFile().getParent());
                }
                // if the user cancelled the operation
                else
                    return;
            }
        });

        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sourceDir = folderLoc.getText();

                if (sourceDir.isEmpty())
                    return;

                TabbedPane.removeAll();

                compareToLog c = new compareToLog();
                ArrayList<customer> allCustomers = c.getAllInformationOneList(sourceDir);
                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                if (allCustomers == null | trucks == null) {
                    System.out.println("Error, empty lists");
                    TabbedPane.removeAll();
                    TabbedPane.add("Error", new errorPanel());
                    return;
                }

                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), false));
                }
            }
        });

    }
}

class LogPanel extends JPanel {

    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public LogPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnLog = new JButton("Select Log (.csv)");
        JTextField logLoc = new JTextField("", 20);
        logLoc.setEditable(false);
        JButton BtnFolder = new JButton("Select Manifest Folder");
        JTextField folderLoc = new JTextField("", 20);
        folderLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        buttons.add(BtnFolder);
        buttons.add(folderLoc);
        buttons.add(BtnLog);
        buttons.add(logLoc);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));

        add(buttons, BorderLayout.PAGE_START);
        add(TabbedPane, BorderLayout.CENTER);

        BtnFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    folderLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    prefs.put("LAST_USED_FOLDER", fileChooser.getSelectedFile().getParent());
                }
                // if the user cancelled the operation
                else
                    return;
            }
        });

        BtnLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    logLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    prefs.put("LAST_USED_FOLDER", fileChooser.getSelectedFile().getParent());
                }
                // if the user cancelled the operation
                else
                    return;
            }
        });

        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sourceDir = folderLoc.getText();
                String logSourceDir = logLoc.getText();

                if (sourceDir.isEmpty() || logSourceDir.isEmpty())
                    return;

                TabbedPane.removeAll();

                compareToLog c = new compareToLog();
                ArrayList<customer> allCustomers = c.getAllInformationOneList(sourceDir);
                ArrayList<customer> extraOrders = getExtraOrders(allCustomers, logSourceDir);
                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                if (allCustomers == null | extraOrders == null | trucks == null) {
                    System.out.println("Error, empty lists");
                    TabbedPane.removeAll();
                    TabbedPane.add("Error", new errorPanel());
                    return;
                }

                TabbedPane.add("Extra Orders", new ManifestPanel(extraOrders, true));

                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), false));
                }
            }
        });

    }

    public ArrayList<customer> getExtraOrders(ArrayList<customer> allCustomers, String logSourceDir) {
        compareToLog c = new compareToLog();
        return c.crossReferenceAll(allCustomers, c.customersFromLog(logSourceDir));
    }
}

class TwoPanel extends JPanel {

    JPanel buttons = new JPanel(new FlowLayout());
    JTabbedPane TabbedPane = new JTabbedPane();
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public TwoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnPre = new JButton("Select Pre Folder");
        JTextField preLoc = new JTextField("", 20);
        preLoc.setEditable(false);
        JButton BtnFinal = new JButton("Select Final Folder");
        JTextField finaLoc = new JTextField("", 20);
        finaLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        buttons.add(BtnPre);
        buttons.add(preLoc);
        buttons.add(BtnFinal);
        buttons.add(finaLoc);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));

        add(buttons, BorderLayout.PAGE_START);
        add(TabbedPane, BorderLayout.CENTER);

        BtnPre.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    preLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    prefs.put("LAST_USED_FOLDER", fileChooser.getSelectedFile().getParent());
                }
                // if the user cancelled the operation
                else
                    return;
            }
        });

        BtnFinal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    finaLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    prefs.put("LAST_USED_FOLDER", fileChooser.getSelectedFile().getParent());
                }
                // if the user cancelled the operation
                else
                    return;
            }
        });

        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String preManifest = preLoc.getText();
                String finalManifest = finaLoc.getText();

                if (preManifest.isEmpty() || finalManifest.isEmpty())
                    return;

                TabbedPane.removeAll();

                ////// New
                compareToLog c = new compareToLog();
                ArrayList<customer> allPreCustomers = c.getAllInformationOneList(preManifest);
                ArrayList<customer> allFinalCustomers = c.getAllInformationOneList(finalManifest);
                HashMap<String, ArrayList<customer>> pretrucks = c.getTrucks(allPreCustomers);
                HashMap<String, ArrayList<customer>> finaltrucks = c.getTrucks(allFinalCustomers);

                for (String truckNumber : finaltrucks.keySet()) {
                    ArrayList<customer> first = pretrucks.get(truckNumber);
                    ArrayList<customer> second = finaltrucks.get(truckNumber);

                    if (first == null || second == null) {
                        System.out.println("Error, empty lists");
                        TabbedPane.add("Error", new errorPanel());
                    } else {
                        ArrayList<customer> removedFromPre = intersection(first, second);
                        ArrayList<customer> addedtoFinal = intersection(second, first);

                        TabbedPane.add(first.get(0).truckNumber, new AddedMissingPanel(removedFromPre, addedtoFinal));
                    }
                }
                //////////

                /*
                createTextManifest c = new createTextManifest();
                ArrayList<customer> first = c.relevantText(preManifest);
                ArrayList<customer> second = c.relevantText(finalManifest);

                if (first == null || second == null) {
                    System.out.println("Error, empty lists");
                    TabbedPane.removeAll();
                    TabbedPane.add("Error", new errorPanel());
                    return;
                }

                ArrayList<customer> removedFromPre = intersection(first, second);
                ArrayList<customer> addedtoFinal = intersection(second, first);

                TabbedPane.add(first.get(0).truckNumber, new AddedMissingPanel(removedFromPre, addedtoFinal));
                */
            }
        });

    }

    public ArrayList<customer> intersection(ArrayList<customer> first, ArrayList<customer> second) {
        ArrayList<customer> intersection = new ArrayList<customer>();

        for (customer customer : first) {
            if (!containsOrderNumber(second, customer.orderNumber))
                intersection.add(customer);
        }

        return intersection;
    }

    public boolean containsOrderNumber(final List<customer> list, final String order) {
        return list.stream().anyMatch(cust -> order.equals(cust.orderNumber));
    }
}

class ManifestPanel extends JPanel {

    public ManifestPanel(ArrayList<customer> customers, boolean reverse) {
        setLayout(new BorderLayout());

        String output = String.format(
                "%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s" + "%" + -10 + "s" + "%" + -10 + "s",
                "Order Number", "Name", "Carts", "Location", "Stop") + "\n\n";

        // HEY THIS IS VERY IMPORTANT RIGHT HERE
        if(reverse){
        Collections.sort(customers,
                (o1, o2) -> o1.location.compareTo(o2.location));
        Collections.sort(customers, Collections.reverseOrder());
        }
        else{
            output = String.format(
                "%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                "Order Number", "Name", "Stop") + "\n";
        }
        ///////////////////

        for (customer customer : customers) {

            if(reverse)
                output += customer + "\n";
            else
                output += String.format("%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                    customer.orderNumber, customer.name, customer.stop) + "\n";
        }

        output += "\n";

        JTextArea info = new JTextArea(output);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);
    }

}

class AddedMissingPanel extends JPanel {

    public AddedMissingPanel(ArrayList<customer> removedFromPre, ArrayList<customer> addedtoFinal) {
        setLayout(new BorderLayout());
        String output = "";

        output += "Added to Final Manifest\n";
        output += String.format(
                "%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                "Order Number", "Name", "Stop") + "\n";

        for (customer customer : addedtoFinal) {
            output += String.format("%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                    customer.orderNumber, customer.name, customer.stop) + "\n";
        }

        if (addedtoFinal.isEmpty())
            output += "Nothing Changed\n";

        output += "\nRemoved from Pre Manifest\n";
        output += String.format(
                "%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                "Order Number", "Name", "Stop") + "\n";
        for (customer customer : removedFromPre) {
            output += String.format("%" + -20 + "s" + "%" + -20 + "s" + "%" + -10 + "s",
                    customer.orderNumber, customer.name, customer.stop) + "\n";
        }

        if (removedFromPre.isEmpty())
            output += "Nothing Changed";

        JTextArea info = new JTextArea(output);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);
    }
}

class errorPanel extends JPanel {

    public errorPanel() {
        setLayout(new BorderLayout());
        String output = "An error occured";

        JTextArea info = new JTextArea(output);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        add(info);
    }
}