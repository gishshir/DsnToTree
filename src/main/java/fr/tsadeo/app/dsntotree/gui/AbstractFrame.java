package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractFrame extends JFrame implements IGuiConstants, IConstants {

    private static final long serialVersionUID = 1L;

    protected JTextArea processTextArea;
    protected final JFileChooser fc = new JFileChooser();
    protected File currentDirectory;


    // --------------------------------------- constructor
    protected AbstractFrame(String title, int closeOperation) {
        super(title);
        this.setDefaultCloseOperation(closeOperation);
    }

    protected void actionClose() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    protected void createTextArea(Container container, String layout) {

        processTextArea = new JTextArea(5, 80);
        processTextArea.setEditable(false);
        processTextArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane taPanel = new JScrollPane(processTextArea);
        taPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        container.add(taPanel, layout);
    }

    protected void createSplitPanel(Container container, JComponent leftComponent, JComponent rightComponent,
            String layout, int dividerLocation) {

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComponent, rightComponent);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(dividerLocation);

        Dimension minimumSize = new Dimension(100, 50);
        rightComponent.setMinimumSize(minimumSize);
        leftComponent.setMinimumSize(minimumSize);

        container.add(splitPane, layout);
    }

}
