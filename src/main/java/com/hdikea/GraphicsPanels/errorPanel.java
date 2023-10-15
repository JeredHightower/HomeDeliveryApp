package com.hdikea.GraphicsPanels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;


/*
 * Panel shown when an error occurs
 */
public class errorPanel extends JPanel {

    public errorPanel() {
        setLayout(new BorderLayout());
        String output = "An error occured";

        JTextArea info = new JTextArea(output);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        add(info);
    }
}