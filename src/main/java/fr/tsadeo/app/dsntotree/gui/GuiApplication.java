package fr.tsadeo.app.dsntotree.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.MySwingUtilities;

public class GuiApplication {

    private static void createAndShowGUI() {
        // Create and set up the window.
        MyFrame frame = new MyFrame();

        // Display the window.
        frame.pack();

        centerFrame(frame, 0.60f, 0.85f);
        frame.setVisible(true);
    }

    public static void centerFrame(JFrame frame, float pWidth, float pHeight) {

        // make the frame half the height and width
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize((int) Math.abs(width * pWidth), (int) Math.abs(height * pHeight));

        // here's the part where i center the jframe on screen
        frame.setLocationRelativeTo(null);
    }

    public static void start() {

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        MySwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }

    public static void main(String[] args) {

        start();
    }

}
