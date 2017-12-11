package fr.tsadeo.app.dsntotree.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.MySwingUtilities;

import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ApplicationManager;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class GuiApplication implements IConstants {
	

    private static void createAndShowGUI(MyFrame frame) {

        // Display the window.
        frame.pack();

        centerFrame(frame, 0.55f, 0.85f);
        frame.setVisible(true);
    }
    
    private static void readSettings(MyFrame frame) {
        SettingsUtils.get().addListener(frame);
        ApplicationManager.get().readSettings();
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

        // Create and set up the window.
        final MyFrame frame = new MyFrame();
    	
    		new Thread() {
    			public void run() {
    				try {
                    readSettings(frame);
						ServiceFactory.getDictionnaryService().getDsnDictionnary();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
    			}
    		}.start();
			

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        MySwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(frame);
            }
        });

    }

    public static void main(String[] args) {

        start();
    }

}
