/*
 * StandardDialog.java
 *
 * Created on 06 March 2007, 17:44
 *
 */

package uk.org.ownage.dmdirc.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * Provides common methods for dialogs.
 * @author chris
 */
public class StandardDialog extends JDialog  {
    
    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 1;
    
    /**
     * The OK button for this frame
     */
    protected JButton okButton;
    /**
     * The cancel button for this frame
     */
    protected JButton cancelButton;
    
    /**
     * Creates a new instance of StandardDialog
     * @param owner The frame that owns this dialog
     * @param modal Whether to display modally or not
     */
    public StandardDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }
    
    /**
     * Sets the specified button up as the OK button
     * @param button The target button
     */
    protected void setOkButton(JButton button) {
        okButton = button;
        button.setText("OK");
        button.setDefaultCapable(true);
    }
    
    /**
     * Sets the specified button up as the Cancel button
     * @param button The target button
     */
    protected void setCancelButton(JButton button) {
        cancelButton = button;
        button.setText("Cancel");
        button.setDefaultCapable(false);
    }
    
    /**
     * Orders the OK and Cancel buttons in an appropriate order for the current
     * operating system.
     * @param leftButton The left-most button
     * @param rightButton The right-most button
     */
    protected void orderButtons(JButton leftButton, JButton rightButton) {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            // Windows - put the OK button on the left
            setOkButton(leftButton);
            setCancelButton(rightButton);
        } else {
            // Everything else - adhere to usability guidelines and put it on
            // the right.
            setOkButton(rightButton);
            setCancelButton(leftButton);
        }
    }
    
    /**
     * Creates the root pane of this dialog. We hook in two keylisteners
     * to send enter/escape events to our buttons.
     * @return The new root pane
     */
    protected JRootPane createRootPane() {
        ActionListener escapeListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeAction(StandardDialog.this.cancelButton);
            }
        };
        
        ActionListener enterListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeAction(StandardDialog.this.okButton);
            }
        };
        
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(escapeListener, escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(enterListener, enter, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        return rootPane;
    }
    
    /**
     * Retrieves the OK button for this form
     * @return The form's OK button
     */
    protected JButton getOkButton() {
        return okButton;
    }
    
    /**
     * Retrieves the Cancel button for this form
     * @return The form's cancel button
     */
    protected JButton getCancelButton() {
        return cancelButton;
    }
    
    /**
     * Simulates the user clicking on the specified target button.
     * @param target The button to use
     */
    protected void executeAction(JButton target) {
        if (target != null) {
            target.doClick();
        }
    }
    
}
