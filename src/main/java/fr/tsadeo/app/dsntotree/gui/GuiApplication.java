package fr.tsadeo.app.dsntotree.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.MySwingUtilities;

import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class GuiApplication implements IConstants {
	

    private static void createAndShowGUI() {
        // Create and set up the window.
        MyFrame frame = new MyFrame();

        // Display the window.
        frame.pack();

        centerFrame(frame, 0.60f, 0.85f);
        frame.setVisible(true);
    }
    
    private static void readSettings() throws Exception{
    	SettingsUtils.get().readApplicationSettings(new File(SETTINGS_XML));
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
    	
    	try {
			readSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
