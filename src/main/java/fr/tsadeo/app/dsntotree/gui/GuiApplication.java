package fr.tsadeo.app.dsntotree.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.MySwingUtilities;

import org.apache.commons.lang3.math.NumberUtils;

import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ApplicationManager;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class GuiApplication implements IConstants {
	

    private static void createAndShowGUI(MyFrame frame) {

        // Display the window.
        frame.pack();

        centerFrame(frame, 0.65f, 0.85f);
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

    public static void start(final String[] args) {

        // Create and set up the window.
        final MyFrame frame = new MyFrame();
    	
        readSettings(frame);
    		new Thread() {
    			public void run() {
    				try {
                    
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
                manageArguments(frame, args);
            }
        });

    }
    
    private static void manageArguments(MyFrame myFrame, String[] args) {
    	
    	if (args != null && args.length >= 1) {
    		
    		if (NumberUtils.isNumber(args[0])){
    			
    			loadDsn(myFrame, args);
    		} else {
    			loadFile(myFrame, args[0]);
    		}
    	}
    }
    
    private static void loadDsn (MyFrame myFrame, String[] args) {
    	
    	Long chronoMessage = Long.valueOf(args[0]);
    	String bddConnectionName = null;
    	if (args.length == 2) {
    		bddConnectionName = args[1];
    	}
    	myFrame.loadChronoMessageAtOpening(chronoMessage, bddConnectionName);
    	
    }
    private static void loadFile(MyFrame myFrame, String arg) {
    		String fileName = arg;
    		if (fileName != null) {
    			myFrame.loadFileDsnAtOpening(new File(fileName));
    		}
    }

    public static void main(String[] args) {

        start(args);
    }

}
