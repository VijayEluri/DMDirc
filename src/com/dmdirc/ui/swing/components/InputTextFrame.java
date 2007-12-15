/*
 * Copyright (c) 2006-2007 Chris Smith, Shane Mc Cormack, Gregory Holmes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dmdirc.ui.swing.components;

import com.dmdirc.WritableFrameContainer;
import com.dmdirc.commandparser.PopupManager;
import com.dmdirc.commandparser.PopupMenu;
import com.dmdirc.commandparser.PopupMenuItem;
import com.dmdirc.commandparser.PopupType;
import com.dmdirc.config.ConfigManager;
import com.dmdirc.interfaces.AwayStateListener;
import com.dmdirc.logger.ErrorLevel;
import com.dmdirc.logger.Logger;
import com.dmdirc.ui.input.InputHandler;
import com.dmdirc.ui.interfaces.InputWindow;
import com.dmdirc.ui.swing.UIUtilities;
import com.dmdirc.ui.swing.actions.CopyAction;
import com.dmdirc.ui.swing.actions.CutAction;
import com.dmdirc.ui.swing.actions.InputTextFramePasteAction;
import com.dmdirc.ui.swing.dialogs.paste.PasteDialog;
import static com.dmdirc.ui.swing.UIUtilities.SMALL_BORDER;
import com.dmdirc.ui.swing.actions.CommandAction;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * Frame with an input field.
 */
public abstract class InputTextFrame extends TextFrame implements InputWindow,
        InternalFrameListener, MouseListener, ActionListener, KeyListener,
        Serializable, AwayStateListener {

    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 2;
    /** Input field panel. */
    protected JPanel inputPanel;
    /** Away label. */
    protected JLabel awayLabel;
    /** The container that owns this frame. */
    private final WritableFrameContainer parent;
    /** The InputHandler for our input field. */
    private InputHandler inputHandler;
    /** Frame input field. */
    private SwingInputField inputField;
    /** Popupmenu for this frame. */
    private JPopupMenu inputFieldPopup;
    /** Robot for the frame. */
    private Robot robot;
    /** Nick popup menu. */
    protected JPopupMenu nickPopup;

    /**
     * Creates a new instance of InputFrame.
     *
     * @param owner WritableFrameContainer owning this frame.
     */
    public InputTextFrame(final WritableFrameContainer owner) {
        super(owner);

        parent = owner;

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.userError(ErrorLevel.LOW, "Error creating robot");
        }

        initComponents();

        final ConfigManager config = owner.getConfigManager();

        getInputField().setBackground(config.getOptionColour("ui",
                "inputbackgroundcolour",
                config.getOptionColour("ui", "backgroundcolour", Color.WHITE)));
        getInputField().setForeground(config.getOptionColour("ui",
                "inputforegroundcolour",
                config.getOptionColour("ui", "foregroundcolour", Color.BLACK)));
        getInputField().setCaretColor(config.getOptionColour("ui",
                "inputforegroundcolour",
                config.getOptionColour("ui", "foregroundcolour", Color.BLACK)));

        config.addChangeListener("ui", "inputforegroundcolour", this);
        config.addChangeListener("ui", "inputbackgroundcolour", this);
        if (getContainer().getServer() != null) {
            getContainer().getServer().addAwayStateListener(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        if (getConfigManager().getOptionBool("ui", "awayindicator", false) && getContainer().
                getServer() != null) {
            awayLabel.setVisible(getContainer().getServer().isAway());
        }
        super.open();

        inputField.requestFocus();
    }

    /**
     * Initialises the components for this frame.
     */
    private void initComponents() {
        setInputField(new SwingInputField());
        getInputField().setBorder(
                BorderFactory.createCompoundBorder(
                getInputField().getBorder(),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        getInputField().addKeyListener(this);
        getInputField().addMouseListener(this);

        initPopupMenu();
        nickPopup = new JPopupMenu();

        awayLabel = new JLabel();
        awayLabel.setText("(away)");
        awayLabel.setVisible(false);

        inputPanel = new JPanel(new BorderLayout(SMALL_BORDER, SMALL_BORDER));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(SMALL_BORDER, 0,
                0, 0));
        inputPanel.add(awayLabel, BorderLayout.LINE_START);
        inputPanel.add(inputField, BorderLayout.CENTER);

        initInputField();
    }

    /** Initialises the popupmenu. */
    private void initPopupMenu() {
        inputFieldPopup = new JPopupMenu();

        inputFieldPopup.add(new CutAction(getInputField()));
        inputFieldPopup.add(new CopyAction(getInputField()));
        inputFieldPopup.add(new InputTextFramePasteAction(this));
        inputFieldPopup.setOpaque(true);
        inputFieldPopup.setLightWeightPopupEnabled(true);
    }

    /**
     * Initialises the input field.
     */
    private void initInputField() {
        UIUtilities.addUndoManager(getInputField());

        getInputField().getActionMap().put("PasteAction",
                new InputTextFramePasteAction(this));
        getInputField().getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("shift INSERT"),
                "PasteAction");
        getInputField().getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl V"),
                "PasteAction");
    }

    /**
     * Returns the container associated with this frame.
     *
     * @return This frame's container.
     */
    @Override
    public WritableFrameContainer getContainer() {
        return parent;
    }

    /**
     * Returns the input handler associated with this frame.
     *
     * @return Input handlers for this frame
     */
    @Override
    public final InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * Sets the input handler for this frame.
     *
     * @param newInputHandler input handler to set for this frame
     */
    public final void setInputHandler(final InputHandler newInputHandler) {
        this.inputHandler = newInputHandler;
    }

    /**
     * Returns the input field for this frame.
     *
     * @return SwingInputField input field for the frame.
     */
    public final SwingInputField getInputField() {
        return inputField;
    }

    /**
     * Sets the frames input field.
     *
     * @param newInputField new input field to use
     */
    protected final void setInputField(final SwingInputField newInputField) {
        this.inputField = newInputField;
    }

    /**
     * Returns the away label for this server connection.
     *
     * @return JLabel away label
     */
    public JLabel getAwayLabel() {
        return awayLabel;
    }

    /**
     * Sets the away indicator on or off.
     *
     * @param awayState away state
     */
    @Override
    public void setAwayIndicator(final boolean awayState) {
        final boolean awayIndicator = getConfigManager().
                getOptionBool("ui", "awayindicator", false);
        if (awayIndicator || !awayState) {
            if (awayState) {
                inputPanel.add(awayLabel, BorderLayout.LINE_START);
                awayLabel.setVisible(true);
            } else {
                awayLabel.setVisible(false);
            }
        }
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void internalFrameOpened(final InternalFrameEvent event) {
        super.internalFrameOpened(event);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void internalFrameClosing(final InternalFrameEvent event) {
        super.internalFrameClosing(event);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void internalFrameClosed(final InternalFrameEvent event) {
        super.internalFrameClosed(event);
    }

    /**
     * Makes the internal frame invisible. {@inheritDoc}
     */
    @Override
    public void internalFrameIconified(final InternalFrameEvent event) {
        super.internalFrameIconified(event);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void internalFrameDeiconified(final InternalFrameEvent event) {
        super.internalFrameDeiconified(event);
    }

    /**
     * Activates the input field on frame focus. {@inheritDoc}
     */
    @Override
    public void internalFrameActivated(final InternalFrameEvent event) {
        super.internalFrameActivated(event);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void internalFrameDeactivated(final InternalFrameEvent event) {
        super.internalFrameDeactivated(event);
    }

    /** {@inheritDoc} */
    @Override
    public void keyTyped(final KeyEvent event) {
        //Ignore.
        super.keyTyped(event);
    }

    /** {@inheritDoc} */
    @Override
    public void keyPressed(final KeyEvent event) {
        if (event.getSource() == getTextPane() && (getConfigManager().
                getOptionBool("ui", "quickCopy", false) ||
                (event.getModifiers() & KeyEvent.CTRL_MASK) == 0)) {
            event.setSource(getInputField());
            getInputField().requestFocus();
            if (robot != null && event.getKeyCode() != KeyEvent.VK_UNDEFINED) {
                robot.keyPress(event.getKeyCode());
                if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                    robot.keyRelease(event.getKeyCode());
                }
            }
        }
        super.keyPressed(event);
    }

    /** {@inheritDoc} */
    @Override
    public void keyReleased(final KeyEvent event) {
        //Ignore.
        super.keyReleased(event);
    }

    /**
     * Checks for url's, channels and nicknames. {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == getTextPane()) {
            processMouseEvent(mouseEvent);
        }
        super.mouseClicked(mouseEvent);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        processMouseEvent(mouseEvent);
        super.mousePressed(mouseEvent);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        processMouseEvent(mouseEvent);
        super.mouseReleased(mouseEvent);
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
    //Ignore.
    }

    /**
     * Not needed for this class. {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
    //Ignore.
    }

    /**
     * Processes every mouse button event to check for a popup trigger.
     *
     * @param e mouse event
     */
    @Override
    public void processMouseEvent(final MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource() == getInputField()) {
            final Point point = getInputField().getMousePosition();

            if (point != null) {
                initPopupMenu();
                inputFieldPopup.show(this, (int) point.getX(),
                        (int) point.getY() + getTextPane().getHeight() +
                        SMALL_BORDER);
            }
        }
        super.processMouseEvent(e);
    }

    /** Checks and pastes text. */
    public void doPaste() {
        String clipboard = null;
        String[] clipboardLines = new String[]{"",
         } 
            ;







        
         
               
              
            
             if (!Toolkit.getDefaultToolkit().getSystemClipboard().
                isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return;
        }
        
        try {
            //get the contents of the input field and combine it with the clipboard
            clipboard = (String) Toolkit.getDefaultToolkit().
                    getSystemClipboard().getData(DataFlavor.stringFlavor);
            //split the text
            clipboardLines = getSplitLine(clipboard);
        } catch (IOException ex) {
            Logger.userError(ErrorLevel.LOW, "Unable to get clipboard contents: " +
                    ex.getMessage());
        } catch (UnsupportedFlavorException ex) {
            Logger.appError(ErrorLevel.LOW, "Unable to get clipboard contents",
                    ex);
        }

        //check theres something to paste
        if (clipboard != null && clipboardLines.length > 1) {
            clipboard = getInputField().getText() + clipboard;
            //check the limit
            final int pasteTrigger = getConfigManager().getOptionInt("ui",
                    "pasteProtectionLimit", 1);
            //check whether the number of lines is over the limit
            if (parent.getNumLines(clipboard) > pasteTrigger) {
                //show the multi line paste dialog
                new PasteDialog(this, clipboard).setVisible(true);
                inputField.setText("");
            } else {
                //send the lines
                for (String clipboardLine : clipboardLines) {
                    parent.sendLine(clipboardLine);
                }
            }
        } else {
            inputField.replaceSelection(clipboard);
        }
    }

    /**
     * Splits the line on all line endings.
     *
     * @param line Line that will be split
     *
     * @return Split line array
     */
    private String[] getSplitLine(final String line) {
        return line.replace("\r\n", "\n").replace('\r', '\n').split("\n");
    }

    /** {@inheritDoc} */
    @Override
    public void configChanged(final String domain, final String key) {
        super.configChanged(domain, key);

        if ("ui".equals(domain) && getInputField() != null &&
                getConfigManager() != null) {
            if ("inputbackgroundcolour".equals(key) ||
                    "backgroundcolour".equals(key)) {
                getInputField().setBackground(getConfigManager().getOptionColour("ui",
                        "inputbackgroundcolour",
                        getConfigManager().getOptionColour("ui",
                        "backgroundcolour", Color.WHITE)));
            } else if ("inputforegroundcolour".equals(key) ||
                    "foregroundcolour".equals(key)) {
                getInputField().setForeground(getConfigManager().getOptionColour("ui",
                        "inputforegroundcolour",
                        getConfigManager().getOptionColour("ui",
                        "foregroundcolour", Color.BLACK)));
                getInputField().setCaretColor(getConfigManager().getOptionColour("ui",
                        "inputforegroundcolour",
                        getConfigManager().getOptionColour("ui",
                        "foregroundcolour", Color.BLACK)));

            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void nickNameClicked(final String nickname, final MouseEvent event) {
        if (event.isPopupTrigger()) {
            final Point point = getMousePosition();
            popuplateNicklistPopup(nickname);
            nickPopup.show(this, (int) point.getX(), (int) point.getY());
        }
    }

    /** 
     * Popuplates the nicklist popup. 
     *
     * @param nickname Nickname for the popup
     */
    protected final void popuplateNicklistPopup(final String nickname) {
        final PopupMenu popups = PopupManager.getMenu(PopupType.CHAN_NICKLIST,
                getConfigManager());
        
        nickPopup = (JPopupMenu) populatePopupMenu(new JPopupMenu(), popups, 
                nickname);
    }

    /**
     * Populates the specified popupmenu
     * 
     * @param menu Menu component
     * @param popup Popup to get info from
     * @param arguments Arguments for the command
     * 
     * @return Populated popup
     */
    private JComponent populatePopupMenu(final JComponent menu, final PopupMenu popup, 
            final Object... arguments) {
        for (PopupMenuItem menuItem : popup.getItems()) {
            if (menuItem.isDivider()) {
                menu.add(new JSeparator());
            } else if (menuItem.isSubMenu()) {
                menu.add(populatePopupMenu(new JMenu(menuItem.getName()), 
                        menuItem.getSubMenu(), arguments));
            } else {
                menu.add(new JMenuItem(new CommandAction(getCommandParser(), 
                        this, menuItem.getName(), menuItem.getCommand(arguments))));
            }
        }
        return menu;
    }

    /** Request input field focus. */
    public void requestInputFieldFocus() {
        if (inputField != null) {
            inputField.requestFocus();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onAway(final String reason) {
        setAwayIndicator(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onBack() {
        setAwayIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        super.close();

        if (getContainer() != null && getContainer().getServer() != null) {
            getContainer().getServer().removeAwayStateListener(this);
        }
    }
}
