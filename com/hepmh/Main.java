package com.hepmh;

// AWT
import java.awt.Dimension;

// FlatLaf
import com.formdev.flatlaf.FlatDarculaLaf;

import com.hepmh.Counter;

class Main {
    public static void main(String args[]) {
        // Set Look & Feel
        FlatDarculaLaf.setup();

        Counter sCounter = new Counter();
        //sCounter.setExtendedState(JFrame.MAXIMIZED_BOTH);
        sCounter.setMinimumSize(new Dimension(1400, 800));
        sCounter.setVisible(true);
     }
}