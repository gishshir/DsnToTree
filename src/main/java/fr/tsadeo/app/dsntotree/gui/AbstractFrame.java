package fr.tsadeo.app.dsntotree.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractFrame extends JFrame implements IGuiConstants, IConstants, DocumentListener {

    private static final long serialVersionUID = 1L;

    protected JTextArea processTextArea;
    protected final JFileChooser fc = new JFileChooser();
    protected File currentDirectory;

    // ----------------------------------- implementing DocumentListener
    @Override
    public void insertUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

    // --------------------------------------- constructor
    protected AbstractFrame(String title, int closeOperation) {
        super(title);
        this.setDefaultCloseOperation(closeOperation);
    }

    protected void createTextArea(Container container, String layout) {

        processTextArea = new JTextArea(5, 100);
        processTextArea.setEditable(false);
        processTextArea.setMargin(new Insets(10, 10, 10, 10));
        container.add(processTextArea, layout);
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
