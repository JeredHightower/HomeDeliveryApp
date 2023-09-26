package com.hdikea;

import javax.swing.WindowConstants;

public class HdApp {
    public static void main(String[] args) {
        HDGraphics frame = new HDGraphics("HDApp");

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
