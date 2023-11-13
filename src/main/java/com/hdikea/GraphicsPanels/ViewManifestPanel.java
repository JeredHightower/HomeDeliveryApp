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
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.hdikea.compareToLog;
import com.hdikea.createTextManifest;
import com.hdikea.customer;

/*
 * Panel for just viewing the manifests as they are
 */
public class ViewManifestPanel extends JPanel {
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

                createTextManifest cT = new createTextManifest();
                compareToLog c = new compareToLog();
                ArrayList<customer> allCustomers = cT.getAllInformationOneList(sourceDir);
                HashMap<String, ArrayList<customer>> trucks = c.getTrucks(allCustomers);

                TabbedPane.removeAll();
                if (allCustomers == null | trucks == null) {
                    TabbedPane.add("Error", new errorPanel("Error: Issue Getting Manifest Information"));
                    return;
                }

                for (String truckNumber : trucks.keySet()) {
                    TabbedPane.add(truckNumber, new ManifestPanel(trucks.get(truckNumber), 0, false));
                }

                if(TabbedPane.getTabCount() <= 0){
                    TabbedPane.add("Error", new errorPanel("Error: Unknown Error"));
                }
            }
        });

    }
}