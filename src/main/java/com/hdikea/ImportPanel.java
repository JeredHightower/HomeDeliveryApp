package com.hdikea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class ImportPanel extends JPanel {

    JTabbedPane TabbedPane = new JTabbedPane();
    JPanel buttons = new JPanel();
    JPanel tab1 = new JPanel(new FlowLayout());
    JPanel tab2 = new JPanel(new FlowLayout());
    Preferences prefs = Preferences.userRoot().node(getClass().getName());

    public ImportPanel() {

        // UI Setup
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        setBorder(new LineBorder(Color.BLUE));

        JButton BtnLog = new JButton("Select Log (.xlsx)");
        JTextField logLoc = new JTextField("", 40);
        logLoc.setEditable(false);
        JButton BtnCSV = new JButton("Select CSV (.csv)");
        JTextField csvLoc = new JTextField("", 40);
        csvLoc.setEditable(false);
        JButton importCSV = new JButton("Import");

        tab1.add(BtnLog);
        tab1.add(logLoc);
        tab2.add(BtnCSV);
        tab2.add(csvLoc);

        tab1.setBackground(new Color(255, 255, 153));
        tab2.setBackground(new Color(255, 255, 153));

        buttons.add(tab1);
        buttons.add(tab2);
        buttons.add(importCSV);

        buttons.setBackground(new Color(255, 255, 153));
        BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
        buttons.setLayout(boxlayout);

        add(buttons, BorderLayout.PAGE_START);
        add(TabbedPane, BorderLayout.CENTER);

        BtnCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(
                        prefs.get("LAST_USED_FOLDER", new File(".").getAbsolutePath()));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int r = fileChooser.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    // set the label to the path of the selected directory
                    csvLoc.setText(fileChooser.getSelectedFile().getAbsolutePath());
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

        importCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String csvsourceDir = csvLoc.getText();
                String logSourceDir = logLoc.getText();

                // Check if input is empty
                if (csvsourceDir.isEmpty() || logSourceDir.isEmpty())
                    return;
            }
        });
    }
}
