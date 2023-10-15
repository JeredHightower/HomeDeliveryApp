package com.hdikea.GraphicsPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.hdikea.compareToLog;
import com.hdikea.customer;

public class TwoPanel extends JPanel {

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