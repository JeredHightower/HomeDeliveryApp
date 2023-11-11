package com.hdikea;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

// import com.hdikea.GraphicsPanels.ImportPanel;
import com.hdikea.GraphicsPanels.LogPanel;
import com.hdikea.GraphicsPanels.TwoPanel;
import com.hdikea.GraphicsPanels.TwoPanelManual;
import com.hdikea.GraphicsPanels.ViewManifestPanel;

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

        TabbedPane.addTab("Compare Manifests To Log", new LogPanel());
        TabbedPane.addTab("Compare Manifests (Auto)", new TwoPanel());
        TabbedPane.addTab("Compare Manifests (Manual)", new TwoPanelManual());
        // TabbedPane.addTab("Import Data to Log", new ImportPanel());
        TabbedPane.addTab("View Manifests", new ViewManifestPanel());
        add(TabbedPane);
    }
}