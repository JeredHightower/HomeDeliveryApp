package com.hdikea.GraphicsPanels.Tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.hdikea.Backend.compareToLog;
import com.hdikea.Backend.createTextManifest;
import com.hdikea.Backend.customer;
import com.hdikea.GraphicsPanels.Helper.Alternate.JTabbedPaneWithCloseButton;
import com.hdikea.GraphicsPanels.Helper.Alternate.errorPanel;
import com.hdikea.GraphicsPanels.Tables.AddedMissingPanel;

/*
 * Panel Intended to for the Compare Manifests (Manual) Screen
 */
public class TwoPanelManual extends JPanel {

    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    JPanel tab3 = new JPanel(new FlowLayout());
    JPanel tabGen = new JPanel(new FlowLayout());
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

        JCheckBox check = new JCheckBox("Sort by location (reverse)");
        check.setSelected(true);

        tabGen.add(check);
        tabGen.add(generate);

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
        tabGen.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(tab3);
        buttons.add(tabGen);

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

                compareToLog cLog = new compareToLog();

                //////
                ArrayList<customer> logCustomers = null;
                ArrayList<customer> logCustCopy = null;
                if (!logLoc.getText().isEmpty()) {
                    logCustomers = cLog.customersFromLog(logSourceDir);

                    if (logCustomers != null)
                        logCustCopy = new ArrayList<>(logCustomers);

                    if (logCustomers == null) {
                        JPanel err = new errorPanel("Error: Issue with Log");
                        TabbedPane.add("Error", err);
                        TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(err),
                                close.getTitlePanel(TabbedPane, err, "Error"));
                        logLoc.setText("");
                        return;
                    }
                }
                /////

                createTextManifest c = new createTextManifest();
                ArrayList<customer> first = c.relevantText(preManifest);
                ArrayList<customer> second = c.relevantText(finalManifest);

                if (first == null || second == null) {
                    JPanel err = new errorPanel("Error: Issue Getting Manifest Information");
                    TabbedPane.add("Error", err);
                    TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(err),
                            close.getTitlePanel(TabbedPane, err, "Error"));
                    return;
                }

                ////////
                if (!logLoc.getText().isEmpty()) {
                    cLog.crossReferenceAll(first, logCustomers);
                    cLog.crossReferenceAll(second, logCustCopy);
                }
                ////////

                ArrayList<customer> removedFromPre = intersection(first, second);
                ArrayList<customer> addedtoFinal = intersection(second, first);

                ArrayList<customer> stillMissing = new ArrayList<>();
                if (!logLoc.getText().isEmpty()) {
                    stillMissing = new ArrayList<>(second);
                    stillMissing.removeIf(cust -> !cust.location.equals("Missing"));
                    stillMissing = intersection(stillMissing, addedtoFinal);
                }
                else{
                    stillMissing.add(new customer("", "", "", "", "", "", "No log used"));
                }

                ArrayList<customer> managers = new ArrayList<>();
                managers = new ArrayList<>(second);
                managers.removeIf(cust -> !cust.isReturn());


                JPanel panel = new AddedMissingPanel(removedFromPre, addedtoFinal, stillMissing, managers, check.isSelected());
                TabbedPane.add(second.get(0).truckNumber, panel);

                TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(panel),
                        close.getTitlePanel(TabbedPane, panel, second.get(0).truckNumber));

                if (TabbedPane.getTabCount() <= 0) {
                    JPanel err = new errorPanel("Error: Unknown Error");
                    TabbedPane.add("Error", err);
                    TabbedPane.setTabComponentAt(TabbedPane.indexOfComponent(err),
                            close.getTitlePanel(TabbedPane, err, "Error"));
                }
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