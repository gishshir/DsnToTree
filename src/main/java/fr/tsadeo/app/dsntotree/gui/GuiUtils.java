package fr.tsadeo.app.dsntotree.gui;

import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class GuiUtils {

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MyFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static void createButton(JButton button, Action action, String actionName, int keyStrokeWhenFocusedWindow,
            String iconPath, String name, String tooltip, boolean enabled, Container container, String layout) {

        container.add(button, layout);

        InputMap im = button.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = button.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), actionName);
        am.put(actionName, action);

        im = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(keyStrokeWhenFocusedWindow, InputEvent.ALT_DOWN_MASK), actionName);

        button.setAction(action);
        button.setIcon(GuiUtils.createImageIcon(iconPath));
        button.setText(name== null?"":name);
        button.setToolTipText(tooltip);
        button.setMnemonic(keyStrokeWhenFocusedWindow);

        button.setVerticalTextPosition(AbstractButton.BOTTOM);
        button.setHorizontalTextPosition(AbstractButton.CENTER);
        button.setEnabled(enabled);
    }

}
