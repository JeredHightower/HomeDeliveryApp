package com.hdikea.GraphicsPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.hdikea.compareToLog;
import com.hdikea.createTextManifest;
import com.hdikea.customer;

/*
 * Panel inteded for the Compare Manifests to Log screen
 */
public class LogPanel extends JPanel {

    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    JPanel tabGen = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public LogPanel() {

        // UI Setup
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnLog = new JButton("Select Log (.xlsx)");
        JTextField logLoc = new JTextField("", 40);
        // logLoc.setEditable(false);
        JButton BtnFolder = new JButton("Select Manifest Folder");
        JTextField folderLoc = new JTextField("", 40);
        folderLoc.setEditable(false);
        JButton generate = new JButton("Generate");

        JCheckBox check = new JCheckBox("Sort by location (reverse)");
        check.setSelected(true);

        tab1.add(BtnLog);
        tab1.add(logLoc);
        tab2.add(BtnFolder);
        tab2.add(folderLoc);
        tabGen.add(check);
        tabGen.add(generate);

        tab1.setBackground(new Color(255, 255, 153));
        tab2.setBackground(new Color(255, 255, 153));
        tabGen.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(tabGen);

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

                // Check if input is empty (If no manifest folder given, don't generate. If only manifest
                // folder is given, apply logic for just viewing manifests without locations)
                if (sourceDir.isEmpty())
                    return;

                // Remove Current Tabs
                TabbedPane.removeAll();

                // Get customers from Log
                compareToLog c = new compareToLog();
                ArrayList<customer> logCustomers= c.customersFromLog(logSourceDir);
                if(logCustomers == null && !logSourceDir.isEmpty()){
                    TabbedPane.add("Error", new errorPanel("Error: Issue with log"));
                    return;
                }

                // Get customers from manifest
                createTextManifest cT = new createTextManifest();
                ArrayList<customer> allCustomers = cT.getAllInformationOneList(sourceDir);
                if (allCustomers == null) {
                    TabbedPane.add("Error", new errorPanel("Error: Issue Getting Manifest Information"));
                    return;
                }
                
                // Cross reference
                int code = 0;
                if(!logSourceDir.isEmpty()){
                    ArrayList<customer> extraOrders = getExtraOrders(allCustomers, logCustomers);
                    TabbedPane.add("Extra Orders", new ManifestPanel(extraOrders, 1, check.isSelected()));
                    code = 2;
                }

                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), code, check.isSelected()));
                }

                if(TabbedPane.getTabCount() <= 0){
                    TabbedPane.add("Error", new errorPanel("Error: Unknown Error"));
                }
            }
        });

    }

    /*
     * Takes two list of customers and transfers locations from logCustomers to all Customers
     * Returns list of customers from logCustomers that weren't in allCustomers
     */
    public ArrayList<customer> getExtraOrders(ArrayList<customer> allCustomers, ArrayList<customer> logCustomers) {
        compareToLog c = new compareToLog();
        return c.crossReferenceAll(allCustomers, logCustomers);
    }
}