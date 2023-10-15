package com.hdikea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JTable.PrintMode;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;

public class HDGraphics extends JFrame {

    JTabbedPane TabbedPane = new JTabbedPane();

    public HDGraphics(String title) {
        super(title);
        // Sample 01: Set Size and Position
        setBounds(100, 100, 900, 700);

        TabbedPane.addTab("Compare Manifests To Log", new LogPanel());
        TabbedPane.addTab("Compare Manifests (Auto)", new TwoPanel());
        TabbedPane.addTab("Compare Manifests (Manual)", new TwoPanelManual());
        TabbedPane.addTab("View Manifests", new ViewManifestPanel());
        add(TabbedPane);
    }
}

class ViewManifestPanel extends JPanel {
    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel(new FlowLayout());
    JPanel tab1 = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public ViewManifestPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnFolder = new JButton("Select Manifest Folder");
        JTextField folderLoc = new JTextField("", 40);
        folderLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        tab1.add(BtnFolder);
        tab1.add(folderLoc);

        tab1.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));
        BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
        buttons.setLayout(boxlayout);

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

                compareToLog c = new compareToLog();
                ArrayList<customer> allCustomers = c.getAllInformationOneList(sourceDir);
                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                if (allCustomers == null | trucks == null) {
                    System.out.println("Error, empty lists");
                    TabbedPane.removeAll();
                    TabbedPane.add("Error", new errorPanel());
                    return;
                }

                TabbedPane.removeAll();
                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), false, false));
                }
            }
        });

    }
}

class LogPanel extends JPanel {

    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public LogPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnLog = new JButton("Select Log (.xlsx)");
        JTextField logLoc = new JTextField("", 40);
        logLoc.setEditable(false);
        JButton BtnFolder = new JButton("Select Manifest Folder");
        JTextField folderLoc = new JTextField("", 40);
        folderLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        tab1.add(BtnLog);
        tab1.add(logLoc);
        tab2.add(BtnFolder);
        tab2.add(folderLoc);

        tab1.setBackground(new Color(255, 255, 153));
        tab2.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));
        BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
        buttons.setLayout(boxlayout);

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

                compareToLog c = new compareToLog();
                ArrayList<customer> allCustomers = c.getAllInformationOneList(sourceDir);
                ArrayList<customer> extraOrders = getExtraOrders(allCustomers, logSourceDir);
                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                TabbedPane.removeAll();
                if (allCustomers == null | extraOrders == null | trucks == null) {
                    System.out.println("Error, empty lists");
                    TabbedPane.add("Error", new errorPanel());
                    return;
                }

                TabbedPane.add("Extra Orders", new ManifestPanel(extraOrders, true, true));

                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), true, false));
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

    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    JPanel tab3 = new JPanel(new FlowLayout());
    JTabbedPane TabbedPane = new JTabbedPane();
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public TwoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnPre = new JButton("Select Pre Folder");
        JTextField preLoc = new JTextField("", 40);
        preLoc.setEditable(false);
        preLoc.setMaximumSize(preLoc.getPreferredSize());
        tab1.add(BtnPre);
        tab1.add(preLoc);

        JButton BtnFinal = new JButton("Select Final Folder");
        JTextField finaLoc = new JTextField("", 40);
        finaLoc.setEditable(false);
        finaLoc.setMaximumSize(finaLoc.getPreferredSize());
        tab2.add(BtnFinal);
        tab2.add(finaLoc);

        JButton generate = new JButton("Generate");

        /// NEWLY ADDED
        JButton BtnLog = new JButton("Select Log (.xlsx)");
        JTextField logLoc = new JTextField("", 40);
        logLoc.setEditable(false);
        logLoc.setMaximumSize(logLoc.getPreferredSize());
        tab3.add(BtnLog);
        tab3.add(logLoc);
        ///////////////////

        tab1.setBackground(new Color(255, 255, 153));
        tab2.setBackground(new Color(255, 255, 153));
        tab3.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(tab3);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));
        BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
        buttons.setLayout(boxlayout);

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

                String preManifest = preLoc.getText();
                String finalManifest = finaLoc.getText();
                String logSourceDir = logLoc.getText();

                if (preManifest.isEmpty() || finalManifest.isEmpty())
                    return;

                compareToLog c = new compareToLog();

                ArrayList<customer> allPreCustomers = c.getAllInformationOneList(preManifest);
                ArrayList<customer> allFinalCustomers = c.getAllInformationOneList(finalManifest);

                /// ADD COMPARE TO LOG TO GET LOCATIONS
                ArrayList<customer> logCustomers = null;
                ArrayList<customer> logCustCopy = null;
                if (!logLoc.getText().isEmpty()) {
                    logCustomers = c.customersFromLog(logSourceDir);
                    logCustCopy = new ArrayList<>(logCustomers);
                    c.crossReferenceAll(allPreCustomers, logCustomers);
                    c.crossReferenceAll(allFinalCustomers, logCustCopy);
                }
                ///////////////////

                HashMap<String, ArrayList<customer>> pretrucks = c.getTrucks(allPreCustomers);
                HashMap<String, ArrayList<customer>> finaltrucks = c.getTrucks(allFinalCustomers);

                TabbedPane.removeAll();
                for (String truckNumber : finaltrucks.keySet()) {
                    ArrayList<customer> first = pretrucks.get(truckNumber);
                    ArrayList<customer> second = finaltrucks.get(truckNumber);

                    if (first == null || second == null) {
                        System.out.println("Error, empty lists");
                        TabbedPane.add("Error", new errorPanel());
                    } else {
                        ArrayList<customer> removedFromPre = intersection(first, second);
                        ArrayList<customer> addedtoFinal = intersection(second, first);

                        if (logCustCopy != null)
                            logCustCopy = intersection(logCustCopy, removedFromPre);

                        ArrayList<customer> stillMissing = new ArrayList<>();
                        if (!logLoc.getText().isEmpty()) {
                            stillMissing = new ArrayList<>(second);
                            stillMissing.removeIf(cust -> !cust.location.equals("Missing"));
                            stillMissing = intersection(stillMissing, addedtoFinal);
                        }

                        TabbedPane.add(first.get(0).truckNumber, new AddedMissingPanel(removedFromPre, addedtoFinal, stillMissing));
                    }
                }

                //////////
                if (!logLoc.getText().isEmpty()) {

                    if (logCustCopy == null) {
                        System.out.println("Error, empty lists");
                        TabbedPane.add("Error", new errorPanel());
                        return;
                    }

                    TabbedPane.add("Not On Pre or Final", new ManifestPanel(logCustCopy, true, true));
                }
                /////////
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

class TwoPanelManual extends JPanel {

    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    JPanel tab3 = new JPanel(new FlowLayout());
    JTabbedPane TabbedPane = new JTabbedPane();
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public TwoPanelManual() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnPre = new JButton("Select Pre Manifest");
        JTextField preLoc = new JTextField("", 40);
        preLoc.setEditable(false);
        preLoc.setMaximumSize(preLoc.getPreferredSize());
        tab1.add(BtnPre);
        tab1.add(preLoc);

        JButton BtnFinal = new JButton("Select Final Manifest");
        JTextField finaLoc = new JTextField("", 40);
        finaLoc.setEditable(false);
        finaLoc.setMaximumSize(finaLoc.getPreferredSize());
        tab2.add(BtnFinal);
        tab2.add(finaLoc);

        JButton generate = new JButton("Generate");

        /// NEWLY ADDED
        JButton BtnLog = new JButton("Select Log (.xlsx)");
        JTextField logLoc = new JTextField("", 40);
        logLoc.setEditable(false);
        logLoc.setMaximumSize(logLoc.getPreferredSize());
        tab3.add(BtnLog);
        tab3.add(logLoc);
        ///////////////////

        tab1.setBackground(new Color(255, 255, 153));
        tab2.setBackground(new Color(255, 255, 153));
        tab3.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(tab3);
        buttons.add(generate);

        buttons.setBackground(new Color(255, 255, 153));
        BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
        buttons.setLayout(boxlayout);

        add(buttons, BorderLayout.PAGE_START);
        add(TabbedPane, BorderLayout.CENTER);

        BtnPre.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

                String preManifest = preLoc.getText();
                String finalManifest = finaLoc.getText();
                String logSourceDir = logLoc.getText();
                JTabbedPaneWithCloseButton close = new JTabbedPaneWithCloseButton();

                if (preManifest.isEmpty() || finalManifest.isEmpty())
                    return;

                createTextManifest c = new createTextManifest();
                ArrayList<customer> first = c.relevantText(preManifest);
                ArrayList<customer> second = c.relevantText(finalManifest);


                if (first == null || second == null) {
                    System.out.println("Error, empty lists");
                    JPanel err = new errorPanel();
                    TabbedPane.add("Error", err);
                    TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(err),
                            close.getTitlePanel(TabbedPane, err, "Error"));
                    return;
                }

                compareToLog cLog = new compareToLog();

                if (!logLoc.getText().isEmpty()) {
                    ArrayList<customer> logCustomers = cLog.customersFromLog(logSourceDir);
                    ArrayList<customer> logCustCopy = new ArrayList<>(logCustomers);
                    cLog.crossReferenceAll(first, logCustomers);
                    cLog.crossReferenceAll(second, logCustCopy);
                }

                ArrayList<customer> removedFromPre = intersection(first, second);
                ArrayList<customer> addedtoFinal = intersection(second, first);

                ArrayList<customer> stillMissing = new ArrayList<>();
                if (!logLoc.getText().isEmpty()) {
                    stillMissing = new ArrayList<>(second);
                    stillMissing.removeIf(cust -> !cust.location.equals("Missing"));
                    stillMissing = intersection(stillMissing, addedtoFinal);
                }

                JPanel panel = new AddedMissingPanel(removedFromPre, addedtoFinal, stillMissing);
                TabbedPane.add(second.get(0).truckNumber, panel);

                TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(panel),
                        close.getTitlePanel(TabbedPane, panel, second.get(0).truckNumber));
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

    public ManifestPanel(ArrayList<customer> customers, boolean reverse, boolean extra) {
        setLayout(new BorderLayout());
        float[] columnWidthPercentage;

        // Remove empty elements from log list
        if (reverse && extra)
            customers.removeIf(c -> c.orderNumber.isEmpty());

        /// false, false: View Manifest | true, true: Extra Orders | true, false:
        /// ComparingtoLog
        /////////
        String[] column_names = { "Header", "Order Number", "Name", "Carts", "Location", "Stop" };
        if (extra)
            column_names = new String[] { "Order Number", "Name", "Carts", "Location" };
        else if (!reverse)
            column_names = new String[] { "Header", "Order Number", "Name", "Stop" };

        String[][] data = new String[customers.size() + 1][column_names.length];
        /////////

        // HEY THIS IS VERY IMPORTANT RIGHT HERE
        if (reverse) {
            Collections.sort(customers,
                    (o1, o2) -> o1.location.compareTo(o2.location));
            Collections.sort(customers, Collections.reverseOrder());
        }
        ////////

        for (int i = 0; i < customers.size(); i++) {
            customer customer = customers.get(i);

            String[] item = { customer.header, customer.orderNumber, customer.name, customer.carts, customer.location,
                    "" + customer.stop };
            if (extra)
                item = new String[] { customer.orderNumber, customer.name, customer.carts, customer.location };
            else if (!reverse)
                item = new String[] { customer.header, customer.orderNumber, customer.name, "" + customer.stop };

            data[i] = item;
        }

        JTable info = new JTable(data, column_names);
        // info.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTable printTable = new JTable();
        printTable.setModel(info.getModel());

        if (extra) {
            // { customer.orderNumber, customer.name, customer.carts, customer.location };
            // printTable.getColumnModel().getColumn(0).setWidth(100);
            printTable.getColumnModel().getColumn(1).setWidth(150);
            printTable.getColumnModel().getColumn(2).setWidth(35);
            printTable.getColumnModel().getColumn(3).setWidth(75);

            columnWidthPercentage = new float[] { .175f * (1.0f / .61f), .25f * (1.0f / .61f), .06f * (1.0f / .61f),
                    .125f * (1.0f / .61f) };
        } else if (!reverse) {
            // { customer.header, customer.orderNumber, customer.name, "" + customer.stop };
            printTable.getColumnModel().getColumn(0).setWidth(200);
            // printTable.getColumnModel().getColumn(1).setWidth(200);
            printTable.getColumnModel().getColumn(2).setWidth(150);
            printTable.getColumnModel().getColumn(3).setWidth(35);

            columnWidthPercentage = new float[] { .33f * (1.0f / .815f), .175f * (1.0f / .815f), .25f * (1.0f / .815f),
                    .06f * (1.0f / .815f) };
        } else {
            // {.33f, .175f, .25f, .06f, .125f, .06f}
            // Header, Order Number, Name, Carts, Location, Stop
            printTable.getColumnModel().getColumn(0).setWidth(200);
            // printTable.getColumnModel().getColumn(1).setWidth(105);
            printTable.getColumnModel().getColumn(2).setWidth(150);
            printTable.getColumnModel().getColumn(3).setWidth(35);
            printTable.getColumnModel().getColumn(4).setWidth(75);
            printTable.getColumnModel().getColumn(5).setWidth(35);

            columnWidthPercentage = new float[] { .33f, .175f, .25f, .06f, .125f, .06f };
        }

        printTable.setSize(printTable.getPreferredSize());
        printTable.getTableHeader().setSize(720, 20);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "Print");
        getActionMap().put("Print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MessageFormat header = new MessageFormat("");
                    if (extra)
                        header = new MessageFormat("Extra Orders");
                    else
                        header = new MessageFormat(customers.get(0).truckNumber);

                    printTable.print(PrintMode.FIT_WIDTH, header, null);
                } catch (PrinterException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int tw = info.getColumnModel().getTotalColumnWidth();

                TableColumnModel jTableColumnModel = info.getColumnModel();
                int cantCols = jTableColumnModel.getColumnCount();
                for (int i = 0; i < cantCols; i++) {
                    int pWidth = Math.round(columnWidthPercentage[i] * tw);
                    jTableColumnModel.getColumn(i).setPreferredWidth(pWidth);
                }
            }
        });

    }
}

class AddedMissingPanel extends JPanel {

    public AddedMissingPanel(ArrayList<customer> removedFromPre, ArrayList<customer> addedtoFinal,
            ArrayList<customer> stillMissing) {
        setLayout(new BorderLayout());

        Collections.sort(removedFromPre,
                (o1, o2) -> o1.location.compareTo(o2.location));
        Collections.sort(removedFromPre, Collections.reverseOrder());

        Collections.sort(addedtoFinal,
                (o1, o2) -> o1.location.compareTo(o2.location));
        Collections.sort(addedtoFinal, Collections.reverseOrder());

        String[] column_names = { "Header", "Order Number", "Name", "Carts", "Location", "Stop" };
        int total_size = removedFromPre.size() + addedtoFinal.size() + stillMissing.size();
        String[][] data = new String[total_size + 6 + 3][column_names.length];

        data[0] = new String[] { "Added to Final Manifest", "", "", "", "", "", "" };
        if (addedtoFinal.isEmpty())
            data[1] = new String[] { "Nothing Changed", "", "", "", "", "", "" };

        int offset = 1;
        for (int c = 0; c < addedtoFinal.size(); c++) {
            customer customer = addedtoFinal.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        data[addedtoFinal.size() + 3] = new String[] { "Removed from Pre Manifest", "", "", "", "", "", "" };
        if (removedFromPre.isEmpty())
            data[addedtoFinal.size() + 4] = new String[] { "Nothing Changed", "", "", "", "", "", "" };

        offset = addedtoFinal.size() + 4;
        for (int c = 0; c < removedFromPre.size(); c++) {
            customer customer = removedFromPre.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        /////////
        data[addedtoFinal.size() + 3 + removedFromPre.size() + 3] = new String[] { "Still Missing From Pre Manifest", "", "", "", "", "",
                "" };
        if (stillMissing.isEmpty())
            data[addedtoFinal.size() + 3 + removedFromPre.size() + 4] = new String[] { "Nothing Changed", "", "", "",
                    "", "", "" };

        offset = addedtoFinal.size() + 3 + removedFromPre.size() + 4;
        for (int c = 0; c < stillMissing.size(); c++) {
            customer customer = stillMissing.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        JTable info = new JTable(data, column_names);

        JTable printTable = new JTable();
        printTable.setModel(info.getModel());

        printTable.getColumnModel().getColumn(0).setWidth(200);
        // printTable.getColumnModel().getColumn(1).setWidth(200);
        printTable.getColumnModel().getColumn(2).setWidth(150);
        printTable.getColumnModel().getColumn(3).setWidth(35);
        printTable.getColumnModel().getColumn(4).setWidth(75);
        printTable.getColumnModel().getColumn(5).setWidth(35);

        printTable.setSize(printTable.getPreferredSize());
        printTable.getTableHeader().setSize(720, 20);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "Print");
        getActionMap().put("Print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printTable.print();
                } catch (PrinterException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);

        float[] columnWidthPercentage = new float[] { .33f, .175f, .25f, .06f, .125f, .06f };
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int tw = info.getColumnModel().getTotalColumnWidth();

                TableColumnModel jTableColumnModel = info.getColumnModel();
                int cantCols = jTableColumnModel.getColumnCount();
                for (int i = 0; i < cantCols; i++) {
                    int pWidth = Math.round(columnWidthPercentage[i] * tw);
                    jTableColumnModel.getColumn(i).setPreferredWidth(pWidth);
                }
            }
        });
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

class JTabbedPaneWithCloseButton {
    public JPanel getTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titlePanel.add(titleLbl);
        JButton closeButton = new JButton("❌");
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.remove(panel);
            }
        });
        titlePanel.add(closeButton);

        return titlePanel;
    }
}