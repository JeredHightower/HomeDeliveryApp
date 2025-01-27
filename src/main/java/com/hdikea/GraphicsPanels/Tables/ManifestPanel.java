package com.hdikea.GraphicsPanels.Tables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.JTable.PrintMode;
import javax.swing.table.TableColumnModel;

import com.hdikea.Backend.customer;

/*
 * Panel that sets up table and columns widths to view data
 */
public class ManifestPanel extends JPanel {

    JPanel buttons = new JPanel(new FlowLayout());

    public ManifestPanel(ArrayList<customer> customers, int code, boolean reverse) {
        setLayout(new BorderLayout());
        float[] columnWidthPercentage;

        /// false, false: View Manifest - 0 | true, true: Extra Orders - 1 | true, false: ComparingtoLog - 2
        // Setup Column Names
        String[] column_names = { "Header", "Order Number", "Name", "Carts", "Location", "Stop" };
        if (code == 1)
            column_names = new String[] { "Order Number", "Name", "Carts", "Location" };
        else if (code == 0)
            column_names = new String[] { "Header", "Order Number", "Name", "Stop" };

        // Generate Matrix
        String[][] data = generateTableMatrix(customers, code, reverse);

        // Create Table
        JTable info = new JTable(data, column_names);
        info.setCellSelectionEnabled(true);

        JTable printTable = new JTable();
        printTable.setModel(info.getModel());


        // Setup Column Widths for Print Dialog
        if (code == 1) {
            // { customer.orderNumber, customer.name, customer.carts, customer.location };
            // printTable.getColumnModel().getColumn(0).setWidth(100);
            printTable.getColumnModel().getColumn(1).setWidth(150);
            printTable.getColumnModel().getColumn(2).setWidth(35);
            printTable.getColumnModel().getColumn(3).setWidth(75);

            columnWidthPercentage = new float[] { .175f * (1.0f / .61f), .25f * (1.0f / .61f), .06f * (1.0f / .61f),
                    .125f * (1.0f / .61f) };
        } else if (code == 0) {
            // { customer.header, customer.orderNumber, customer.name, "" + customer.stop };
            printTable.getColumnModel().getColumn(0).setWidth(200);
            // printTable.getColumnModel().getColumn(1).setWidth(200);
            printTable.getColumnModel().getColumn(2).setWidth(150);
            printTable.getColumnModel().getColumn(3).setWidth(35);

            columnWidthPercentage = new float[] { .33f * (1.0f / .815f), .175f * (1.0f / .815f), .25f * (1.0f / .815f),
                    .06f * (1.0f / .815f) };
        } else {
            // {.33f, .175f, .25f, .06f, .125f, .06f}
            // Header, Order Number, Name, Carts, Location, Stop
            printTable.getColumnModel().getColumn(0).setWidth(200);
            // printTable.getColumnModel().getColumn(1).setWidth(105);
            printTable.getColumnModel().getColumn(2).setWidth(150);
            printTable.getColumnModel().getColumn(3).setWidth(35);
            printTable.getColumnModel().getColumn(4).setWidth(75);
            printTable.getColumnModel().getColumn(5).setWidth(35);

            columnWidthPercentage = new float[] { .33f, .175f, .25f, .06f, .125f, .06f };
        }

        printTable.setSize(printTable.getPreferredSize());
        printTable.getTableHeader().setSize(720, 20);
        
        // Print Dialog
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "Print");
        getActionMap().put("Print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MessageFormat header = new MessageFormat("");
                    if (code == 1)
                        header = new MessageFormat("Extra Orders");
                    else
                        header = new MessageFormat(customers.get(0).truckNumber);

                    printTable.print(PrintMode.FIT_WIDTH, header, null);
                } catch (PrinterException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });


        JButton orange = new JButton("Orange");
        orange.setBackground(new Color(254, 186, 79));;
        buttons.add(orange);

        JButton yellow = new JButton("Yellow");
        yellow.setBackground(new Color(253, 253, 130));
        buttons.add(yellow);

        JButton green = new JButton("Green");
        green.setBackground(new Color(190, 229, 176));
        buttons.add(green);

        JButton blue = new JButton("Blue");
        blue.setBackground(new Color(174, 198, 230));
        buttons.add(blue);

        JButton purple = new JButton("Purple");
        purple.setBackground(new Color(177, 156, 217));
        buttons.add(purple);

        JButton white = new JButton("White");
        white.setBackground(Color.WHITE);
        buttons.add(white);

        JButton pink = new JButton("Pink");
        pink.setBackground(Color.PINK);
        buttons.add(pink);

        JButton black = new JButton("Black");
        black.setBackground(Color.LIGHT_GRAY);
        buttons.add(black);

        orange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(new Color(254, 186, 79));
            }
        });

        yellow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(new Color(253, 253, 130));
            }
        });

        green.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(new Color(190, 229, 176));
            }
        });

        blue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(new Color(174, 198, 230));
            }
        });

        purple.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(new Color(177, 156, 217));
            }
        });

        white.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(Color.WHITE);
            }
        });

        pink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(Color.pink);
            }
        });

        black.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.setBackground(Color.LIGHT_GRAY);
            }
        });

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);
        add(buttons, BorderLayout.PAGE_END);

        // Auto Adjust Column Size when Viewing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int tw = info.getColumnModel().getTotalColumnWidth();

                TableColumnModel jTableColumnModel = info.getColumnModel();
                int cantCols = jTableColumnModel.getColumnCount();
                for (int i = 0; i < cantCols; i++) {
                    int pWidth = Math.round(columnWidthPercentage[i] * tw);
                    jTableColumnModel.getColumn(i).setPreferredWidth(pWidth);
                }
            }
        });
    }

    /*
     * Generate matrix of customer data to be put in Jtable
     */
    public String[][] generateTableMatrix(ArrayList<customer> customers, int code, boolean reverse){

        // Remove empty elements from log list
        if (code == 1)
            customers.removeIf(c -> c.orderNumber.isEmpty());

        /// false, false: View Manifest - 0 | true, true: Extra Orders - 1 | true, false: ComparingtoLog - 2
        /////////
        String[] column_names = { "Header", "Order Number", "Name", "Carts", "Location", "Stop" };
        if (code == 1)
            column_names = new String[] { "Order Number", "Name", "Carts", "Location" };
        else if (code == 0)
            column_names = new String[] { "Header", "Order Number", "Name", "Stop" };

        String[][] data = new String[customers.size() + 1][column_names.length];
        /////////

        // HEY THIS IS VERY IMPORTANT RIGHT HERE
        if (reverse) {
            Collections.sort(customers,
                    (o1, o2) -> o1.location.compareTo(o2.location));
            Collections.sort(customers, Collections.reverseOrder());
        }
        ////////

        for (int i = 0; i < customers.size(); i++) {
            customer customer = customers.get(i);

            String[] item = { customer.header, customer.orderNumber, customer.name, customer.carts, customer.location,
                    "" + customer.stop };
            if (code == 1)
                item = new String[] { customer.orderNumber, customer.name, customer.carts, customer.location };
            else if (code == 0)
                item = new String[] { customer.header, customer.orderNumber, customer.name, "" + customer.stop };

            data[i] = item;
        }

        return data;
    }
}