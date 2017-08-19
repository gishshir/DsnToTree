package fr.tsadeo.app.dsntotree.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.InputEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.sun.glass.events.KeyEvent;

import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractFrame extends JFrame implements IGuiConstants, IConstants, DocumentListener {

    private static final long serialVersionUID = 1L;

    protected JTextArea textArea;

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

        textArea = new JTextArea(5, 100);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        container.add(textArea, layout);
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
