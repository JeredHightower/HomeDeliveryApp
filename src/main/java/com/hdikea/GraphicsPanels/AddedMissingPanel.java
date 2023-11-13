package com.hdikea.GraphicsPanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumnModel;

import com.hdikea.customer;

/*
 * Panel is intended for the Compare Manifests Screens
 * Shows orders that were removed from pre and final manifest along with any
 * still missing orders
 */
public class AddedMissingPanel extends JPanel {

    public AddedMissingPanel(ArrayList<customer> removedFromPre, ArrayList<customer> addedtoFinal,
            ArrayList<customer> stillMissing, boolean reverse) {
        setLayout(new BorderLayout());
        
        if (reverse){
        Collections.sort(removedFromPre,
                (o1, o2) -> o1.location.compareTo(o2.location));
        Collections.sort(removedFromPre, Collections.reverseOrder());

        Collections.sort(addedtoFinal,
                (o1, o2) -> o1.location.compareTo(o2.location));
        Collections.sort(addedtoFinal, Collections.reverseOrder());
        }

        String[] column_names = { "Header", "Order Number", "Name", "Carts", "Location", "Stop" };
        int total_size = removedFromPre.size() + addedtoFinal.size() + stillMissing.size();
        String[][] data = new String[total_size + 6 + 3][column_names.length];

        data[0] = new String[] { "Added to Final Manifest", "", "", "", "", "", "" };
        if (addedtoFinal.isEmpty())
            data[1] = new String[] { "Nothing Changed", "", "", "", "", "", "" };

        int offset = 1;
        for (int c = 0; c < addedtoFinal.size(); c++) {
            customer customer = addedtoFinal.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        data[addedtoFinal.size() + 3] = new String[] { "Removed from Pre Manifest", "", "", "", "", "", "" };
        if (removedFromPre.isEmpty())
            data[addedtoFinal.size() + 4] = new String[] { "Nothing Changed", "", "", "", "", "", "" };

        offset = addedtoFinal.size() + 4;
        for (int c = 0; c < removedFromPre.size(); c++) {
            customer customer = removedFromPre.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        /////////
        data[addedtoFinal.size() + 3 + removedFromPre.size() + 3] = new String[] { "Still Missing From Pre Manifest", "", "", "", "", "",
                "" };
        if (stillMissing.isEmpty())
            data[addedtoFinal.size() + 3 + removedFromPre.size() + 4] = new String[] { "Nothing Changed", "", "", "",
                    "", "", "" };

        offset = addedtoFinal.size() + 3 + removedFromPre.size() + 4;
        for (int c = 0; c < stillMissing.size(); c++) {
            customer customer = stillMissing.get(c);
            data[c + offset] = new String[] { customer.header, customer.orderNumber, customer.name, customer.carts,
                    customer.location, "" + customer.stop };
        }

        JTable info = new JTable(data, column_names);

        JTable printTable = new JTable();
        printTable.setModel(info.getModel());

        printTable.getColumnModel().getColumn(0).setWidth(200);
        // printTable.getColumnModel().getColumn(1).setWidth(200);
        printTable.getColumnModel().getColumn(2).setWidth(150);
        printTable.getColumnModel().getColumn(3).setWidth(35);
        printTable.getColumnModel().getColumn(4).setWidth(75);
        printTable.getColumnModel().getColumn(5).setWidth(35);

        printTable.setSize(printTable.getPreferredSize());
        printTable.getTableHeader().setSize(720, 20);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "Print");
        getActionMap().put("Print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printTable.print();
                } catch (PrinterException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(info);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll);

        float[] columnWidthPercentage = new float[] { .33f, .175f, .25f, .06f, .125f, .06f };
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
}