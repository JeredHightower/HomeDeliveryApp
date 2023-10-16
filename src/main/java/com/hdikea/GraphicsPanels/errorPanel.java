package com.hdikea.GraphicsPanels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;


/*
 * Panel shown when an error occurs
 */
public class errorPanel extends JPanel {

    public errorPanel(String message) {
        setLayout(new BorderLayout());

        JTextArea info = new JTextArea(message);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        System.out.println("errorPanel: " + message);

        add(info);
    }
}