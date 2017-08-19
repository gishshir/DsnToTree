/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.accessibility.Accessible;

/**
 * Vesion modifiée pour gerer le focus sur le JTextfield...
 */
public class MyJOptionPane extends JOptionPane implements Accessible {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "OptionPaneUI";

    /**
     * Brings up a dialog with a specified icon, where the initial choice is
     * determined by the <code>initialValue</code> parameter and the number of
     * choices is determined by the <code>optionType</code> parameter.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or
     * <code>YES_NO_CANCEL_OPTION</code> and the <code>options</code> parameter
     * is <code>null</code>, then the options are supplied by the look and feel.
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply a
     * default icon from the look and feel.
     *
     * @param parentComponent
     *            determines the <code>Frame</code> in which the dialog is
     *            displayed; if <code>null</code>, or if the
     *            <code>parentComponent</code> has no <code>Frame</code>, a
     *            default <code>Frame</code> is used
     * @param message
     *            the <code>Object</code> to display
     * @param title
     *            the title string for the dialog
     * @param optionType
     *            an integer designating the options available on the dialog:
     *            <code>DEFAULT_OPTION</code>, <code>YES_NO_OPTION</code>,
     *            <code>YES_NO_CANCEL_OPTION</code>, or
     *            <code>OK_CANCEL_OPTION</code>
     * @param messageType
     *            an integer designating the kind of message this is, primarily
     *            used to determine the icon from the pluggable Look and Feel:
     *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
     *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
     *            or <code>PLAIN_MESSAGE</code>
     * @param icon
     *            the icon to display in the dialog
     * @param options
     *            an array of objects indicating the possible choices the user
     *            can make; if the objects are components, they are rendered
     *            properly; non-<code>String</code> objects are rendered using
     *            their <code>toString</code> methods; if this parameter is
     *            <code>null</code>, the options are determined by the Look and
     *            Feel
     * @param initialValue
     *            the object that represents the default selection for the
     *            dialog; only meaningful if <code>options</code> is used; can
     *            be <code>null</code>
     * @return an integer indicating the option chosen by the user, or
     *         <code>CLOSED_OPTION</code> if the user closed the dialog
     * @exception HeadlessException
     *                if <code>GraphicsEnvironment.isHeadless</code> returns
     *                <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType,
            int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException {
        MyJOptionPane pane = new MyJOptionPane(message, messageType, optionType, icon, options, initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(
                ((parentComponent == null) ? getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
            return CLOSED_OPTION;
        if (options == null) {
            if (selectedValue instanceof Integer)
                return ((Integer) selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    private JDialog createDialog(Component parentComponent, String title, int style) throws HeadlessException {

        final JDialog dialog;

        Window window = MyJOptionPane.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, title, true);
        } else {
            dialog = new JDialog((Dialog) window, title, true);
        }
        if (window instanceof MySwingUtilities.SharedOwnerFrame) {
            WindowListener ownerShutdownListener = MySwingUtilities.getSharedOwnerFrameShutdownListener();
            dialog.addWindowListener(ownerShutdownListener);
        }
        initDialog(dialog, style, parentComponent);
        return dialog;
    }

    private void initDialog(final JDialog dialog, int style, Component parentComponent) {
        dialog.setComponentOrientation(this.getComponentOrientation());
        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.setUndecorated(true);
                getRootPane().setWindowDecorationStyle(style);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);

        final PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case). Otherwise, close the dialog.
                if (dialog.isVisible() && event.getSource() == MyJOptionPane.this
                        && (event.getPropertyName().equals(VALUE_PROPERTY)) && event.getNewValue() != null
                        && event.getNewValue() != MyJOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        };

        WindowAdapter adapter = new WindowAdapter() {
            private boolean gotFocus = false;

            public void windowClosing(WindowEvent we) {
                setValue(null);
            }

            public void windowClosed(WindowEvent e) {
                removePropertyChangeListener(listener);
                dialog.getContentPane().removeAll();
            }

            public void windowGainedFocus(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        };
        dialog.addWindowListener(adapter);
        dialog.addWindowFocusListener(adapter);
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                // reset value to ensure closing works properly
                setValue(MyJOptionPane.UNINITIALIZED_VALUE);
            }
        });

        addPropertyChangeListener(listener);
    }

    /**
     * Returns the specified component's <code>Frame</code>.
     *
     * @param parentComponent
     *            the <code>Component</code> to check for a <code>Frame</code>
     * @return the <code>Frame</code> that contains the component, or
     *         <code>getRootFrame</code> if the component is <code>null</code>,
     *         or does not have a valid <code>Frame</code> parent
     * @exception HeadlessException
     *                if <code>GraphicsEnvironment.isHeadless</code> returns
     *                <code>true</code>
     * @see #getRootFrame
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Frame getFrameForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame)
            return (Frame) parentComponent;
        return MyJOptionPane.getFrameForComponent(parentComponent.getParent());
    }

    /**
     * Returns the specified component's toplevel <code>Frame</code> or
     * <code>Dialog</code>.
     *
     * @param parentComponent
     *            the <code>Component</code> to check for a <code>Frame</code>
     *            or <code>Dialog</code>
     * @return the <code>Frame</code> or <code>Dialog</code> that contains the
     *         component, or the default frame if the component is
     *         <code>null</code>, or does not have a valid <code>Frame</code> or
     *         <code>Dialog</code> parent
     * @exception HeadlessException
     *                if <code>GraphicsEnvironment.isHeadless</code> returns
     *                <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window) parentComponent;
        return MyJOptionPane.getWindowForComponent(parentComponent.getParent());
    }

    private static final Object sharedFrameKey = MyJOptionPane.class;

    /**
     * Returns the <code>Frame</code> to use for the class methods in which a
     * frame is not provided.
     *
     * @return the default <code>Frame</code> to use
     * @exception HeadlessException
     *                if <code>GraphicsEnvironment.isHeadless</code> returns
     *                <code>true</code>
     * @see #setRootFrame
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Frame getRootFrame() throws HeadlessException {
        Frame sharedFrame = (Frame) MySwingUtilities.appContextGet(sharedFrameKey);
        if (sharedFrame == null) {
            sharedFrame = MySwingUtilities.getSharedOwnerFrame();
            MySwingUtilities.appContextPut(sharedFrameKey, sharedFrame);
        }
        return sharedFrame;
    }

    /**
     * Creates an instance of <code>JOptionPane</code> to display a message with
     * the specified message type, icon, and options, with the
     * initially-selected option specified.
     *
     * @param message
     *            the <code>Object</code> to display
     * @param messageType
     *            the type of message to be displayed:
     *            <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
     *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
     *            or <code>PLAIN_MESSAGE</code>
     * @param optionType
     *            the options to display in the pane:
     *            <code>DEFAULT_OPTION</code>, <code>YES_NO_OPTION</code>,
     *            <code>YES_NO_CANCEL_OPTION</code>,
     *            <code>OK_CANCEL_OPTION</code>
     * @param icon
     *            the Icon image to display
     * @param options
     *            the choices the user can select
     * @param initialValue
     *            the choice that is initially selected; if <code>null</code>,
     *            then nothing will be initially selected; only meaningful if
     *            <code>options</code> is used
     */
    public MyJOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options,
            Object initialValue) {

        this.message = message;
        this.options = options;
        this.initialValue = initialValue;
        this.icon = icon;
        setMessageType(messageType);
        setOptionType(optionType);
        value = UNINITIALIZED_VALUE;
        inputValue = UNINITIALIZED_VALUE;
        updateUI();
    }

    /**
     * Returns the name of the UI class that implements the {@literal L&F} for
     * this component.
     *
     * @return the string "OptionPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Requests that the initial value be selected, which will set focus to the
     * initial value. This method should be invoked after the window containing
     * the option pane is made visible.
     */
    public void selectInitialValue() {
        // OptionPaneUI ui = getUI();
        // if (ui != null) {
        // ui.selectInitialValue(this);
        // }
    }

    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
        case ERROR_MESSAGE:
            return JRootPane.ERROR_DIALOG;
        case QUESTION_MESSAGE:
            return JRootPane.QUESTION_DIALOG;
        case WARNING_MESSAGE:
            return JRootPane.WARNING_DIALOG;
        case INFORMATION_MESSAGE:
            return JRootPane.INFORMATION_DIALOG;
        case PLAIN_MESSAGE:
        default:
            return JRootPane.PLAIN_DIALOG;
        }
    }

}
