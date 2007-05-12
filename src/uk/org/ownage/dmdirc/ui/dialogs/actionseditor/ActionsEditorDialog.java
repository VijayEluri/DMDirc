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

package uk.org.ownage.dmdirc.ui.dialogs.actionseditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import uk.org.ownage.dmdirc.actions.Action;
import uk.org.ownage.dmdirc.ui.MainFrame;
import uk.org.ownage.dmdirc.ui.components.StandardDialog;
import uk.org.ownage.dmdirc.ui.dialogs.ActionsManagerDialog;

import static uk.org.ownage.dmdirc.ui.UIUtilities.SMALL_BORDER;

/**
 * Actions editor dialog, used for adding and creating new actions.
 */
public final class ActionsEditorDialog extends StandardDialog implements
        ActionListener {
    
    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 1;
    
    /** Parent dialog, informed of changes on close. */
    private ActionsManagerDialog parent;
    /** Action being edited or null. */
    private Action action;
    /** Tabbed pane. */
    private JTabbedPane tabbedPane;
    /** Buttons panel. */
    private JPanel buttonsPanel;
    
    /** 
     * Creates a new instance of ActionsEditorDialog. 
     *
     * @param parent parent dialog
     */
    public ActionsEditorDialog(final ActionsManagerDialog parent) {
        this(parent, null);
    }
    
    /** 
     * Creates a new instance of ActionsEditorDialog. 
     *
     * @param parent parent dialog
     * @param action actions to be edited
     */
    public ActionsEditorDialog(final ActionsManagerDialog parent,
            final Action action) {
        super(MainFrame.getMainFrame(), false);
        
        this.parent = parent;
        this.action = action;
        
        this.setTitle("Actions Editor");
        
        initComponents();
        addListeners();
        layoutComponents();
        
        this.setLocationRelativeTo(MainFrame.getMainFrame());
        
        this.setVisible(true);
    }
    
    /** Initialises the components. */
    private void initComponents() {
        initButtonsPanel();
        
        tabbedPane = new JTabbedPane();
        
            tabbedPane.setBorder(BorderFactory.createEmptyBorder(SMALL_BORDER, 
                    SMALL_BORDER, SMALL_BORDER, SMALL_BORDER));
        
        tabbedPane.addTab("General", new GeneralTabPanel(action));
        
        tabbedPane.addTab("Conditions", new ConditionsTabPanel(action));
        
        tabbedPane.addTab("Response", new ResponseTabPanel(action));
    }
    
    /** Initialises the button panel. */
    private void initButtonsPanel() {
        orderButtons(new JButton(), new JButton());
        
        buttonsPanel = new JPanel();
        
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, SMALL_BORDER,
                SMALL_BORDER, SMALL_BORDER));
        
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(getLeftButton());
        buttonsPanel.add(Box.createHorizontalStrut(SMALL_BORDER));
        buttonsPanel.add(getRightButton());
    }
    
    /** Adds listeners to the components. */
    private void addListeners() {
        getOkButton().addActionListener(this);
        getCancelButton().addActionListener(this);
    }
    
    /** Lays out the components in the dialog. */
    private void layoutComponents() {
        this.setLayout(new BorderLayout());
        
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.PAGE_END);
        
        pack();
    }

    /** {@inheritDoc}. */
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource() == getOkButton()) {
            saveSettings();
            this.dispose();
        } else if (event.getSource() == getCancelButton()) {
            this.dispose();
        }
    }
    
    /** Saves this (new|edited) actions. */
    private void saveSettings() {
        //Save settings.
    }
}
