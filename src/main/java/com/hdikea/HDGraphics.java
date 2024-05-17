package com.hdikea;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.hdikea.GraphicsPanels.Tabs.LogPanel;

public class HDGraphics extends JFrame {

    // Entry
    public static void main(String[] args) {
        HDGraphics frame = new HDGraphics("HDApp");

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    JTabbedPane TabbedPane = new JTabbedPane();

    public HDGraphics(String title) {
        super(title);
        // Sample 01: Set Size and Position
        setBounds(100, 100, 900, 700);

        TabbedPane.addTab("Compare Manifests To Log / View", new LogPanel());
        add(TabbedPane);
    }
}