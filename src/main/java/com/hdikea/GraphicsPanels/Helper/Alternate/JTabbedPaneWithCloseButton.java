package com.hdikea.GraphicsPanels.Helper.Alternate;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/*
 * Panel to add a close button to the Compare Manifests (Manual) screen
 * (for the tabs with the truck numbers on them)
 */
public class JTabbedPaneWithCloseButton {

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