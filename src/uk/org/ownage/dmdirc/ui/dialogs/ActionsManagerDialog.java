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

package uk.org.ownage.dmdirc.ui.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import uk.org.ownage.dmdirc.actions.Action;
import uk.org.ownage.dmdirc.actions.ActionManager;
import uk.org.ownage.dmdirc.ui.MainFrame;
import uk.org.ownage.dmdirc.ui.components.ActionsGroupPanel;
import uk.org.ownage.dmdirc.ui.components.StandardDialog;
import uk.org.ownage.dmdirc.ui.dialogs.actionseditor.ActionsEditorDialog;

import static uk.org.ownage.dmdirc.ui.UIUtilities.LARGE_BORDER;
import static uk.org.ownage.dmdirc.ui.UIUtilities.SMALL_BORDER;

/**
 * Allows the user to manage actions.
 */
public final class ActionsManagerDialog extends StandardDialog 
        implements ActionListener {
    
    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 2;
    
    /** Previously created instance of ActionsManagerDialog. */
    private static ActionsManagerDialog me;
    
    /** Height of the buttons, in pixels. */
    private static final int BUTTON_HEIGHT = 25;
    /** Width of the ubttons, in pixels. */
    private static final int BUTTON_WIDTH = 100;
    
    /** The tapped pane used for displaying groups. */
    private JTabbedPane groups;
    /** Edit action button. */
    private JButton editAction;
    
    /** Creates a new instance of ActionsManagerDialog. */
    private ActionsManagerDialog() {
        super(MainFrame.getMainFrame(), false);
        
        initComponents();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Action Manager");
        setResizable(false);
        setLocationRelativeTo(MainFrame.getMainFrame());
        setVisible(true);
    }
    
    /** Creates the dialog if one doesn't exist, and displays it. */
    public static synchronized void showActionsManagerDialog() {
        if (me == null) {
            me = new ActionsManagerDialog();
        } else {
            me.loadGroups();
            me.setVisible(true);
            me.requestFocus();
        }
    }
    
    /** Initialiases the components for this dialog. */
    private void initComponents() {
        setLayout(new GridBagLayout());
        
        final GridBagConstraints constraints = new GridBagConstraints();
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 6;
        final JLabel blurb = new JLabel("Actions allow you to make DMDirc "
                + "respond automatically to events.");
        blurb.setBorder(BorderFactory.createEmptyBorder(LARGE_BORDER, LARGE_BORDER, 
                SMALL_BORDER, LARGE_BORDER));
        add(blurb, constraints);
        
        constraints.gridy++;
        groups = new JTabbedPane();
        groups.setPreferredSize(new Dimension(400, 200));
        groups.setBorder(BorderFactory.createEmptyBorder(SMALL_BORDER, LARGE_BORDER, 
                SMALL_BORDER, LARGE_BORDER));
        add(groups, constraints);
        
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.insets.set(SMALL_BORDER, LARGE_BORDER, SMALL_BORDER, 0);
        JButton myButton = new JButton("Add Group");
        myButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        myButton.setActionCommand("group.add");
        myButton.addActionListener(this);
        myButton.setMargin(new Insets(0, 0, 0, 0));
        add(myButton, constraints);
        
        constraints.gridx++;
        constraints.insets.set(SMALL_BORDER, SMALL_BORDER, SMALL_BORDER, 0);
        myButton = new JButton("Delete Group");
        myButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        myButton.setActionCommand("group.delete");
        myButton.addActionListener(this);
        myButton.setMargin(new Insets(0, 0, 0, 0));
        add(myButton, constraints);
        
        constraints.gridx++;
        constraints.insets.set(SMALL_BORDER, SMALL_BORDER, SMALL_BORDER, 0);
        myButton = new JButton("Rename Group");
        myButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        myButton.setActionCommand("group.rename");
        myButton.addActionListener(this);
        myButton.setMargin(new Insets(0, 0, 0, 0));
        add(myButton, constraints);
        
        constraints.gridx++;
        constraints.insets.set(SMALL_BORDER, LARGE_BORDER, SMALL_BORDER, 0);
        myButton = new JButton("New Action");
        myButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        myButton.setActionCommand("action.new");
        myButton.addActionListener(this);
        myButton.setMargin(new Insets(0, 0, 0, 0));
        add(myButton, constraints);
        
        constraints.gridx++;
        constraints.insets.set(SMALL_BORDER, SMALL_BORDER, SMALL_BORDER, 0);
        editAction = new JButton("Edit Action");
        editAction.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        editAction.setActionCommand("action.edit");
        editAction.addActionListener(this);
        editAction.setMargin(new Insets(0, 0, 0, 0));
        editAction.setEnabled(false);
        add(editAction, constraints);
        
        constraints.gridx++;
        constraints.insets.set(SMALL_BORDER, LARGE_BORDER, SMALL_BORDER, 
                LARGE_BORDER);
        myButton = new JButton("Close");
        myButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        myButton.setActionCommand("close");
        myButton.addActionListener(this);
        myButton.setMargin(new Insets(0, 0, 0, 0));
        add(myButton, constraints);
        
        loadGroups();
        
        pack();
    }
    
    /** Enable or disable the edit action button. */
    public void setEditState(final boolean state) {
        editAction.setEnabled(state);
    }
    
    /**
     * Retrieves known actions from the action manager and displays the
     * appropriate groups in the dialog.
     */
    public void loadGroups() {
        
        groups.removeAll();
        
        final Map<String, List<Action>> actionGroups = ActionManager.getGroups();
        
        final Object[] keys = actionGroups.keySet().toArray();
        
        Arrays.sort(keys);
        
        for (Object group : keys) {
            groups.addTab((String) group, 
                    new ActionsGroupPanel(this, actionGroups.get(group)));
        }
    }
    
    /** 
     * Returns the currently selected group.
     *
     * @return Selected groups name
     */
    public String getSelectedGroup() {
        return groups.getTitleAt(groups.getSelectedIndex());
                
    }
    
    /** {@inheritDoc} */
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("close")) {
            dispose();
        } else if (e.getActionCommand().equals("group.add")) {
            final String newGroup = JOptionPane.showInputDialog(this,
                    "Please enter the name of the group to be created.");
            if (newGroup != null && newGroup.length() > 0) {
                ActionManager.makeGroup(newGroup);
                loadGroups();
            }
        } else if (e.getActionCommand().equals("group.delete")
        && groups.getSelectedIndex() > -1) {
            final String group = groups.getTitleAt(groups.getSelectedIndex());
            final Map<String, List<Action>> actionGroups = ActionManager.getGroups();
            
            if (actionGroups.get(group).size() > 0) {
                final int response = JOptionPane.showConfirmDialog(this,
                        "Are you sure you wish to delete the '" + group
                        + "' group and all actions within it?",
                        "Confirm deletion", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    ActionManager.removeGroup(group);
                }
            } else {
                ActionManager.removeGroup(group);
            }
            
            loadGroups();
        } else if (e.getActionCommand().equals("action.edit")) {
            final int row = ((ActionsGroupPanel)
            groups.getSelectedComponent()).getTable().getSelectedRow();
            if (row != -1) {
                ActionsEditorDialog.showActionsEditorDialog(this,
                        ((ActionsGroupPanel)
                        groups.getSelectedComponent()).getAction(row));
            }
        } else if (e.getActionCommand().equals("action.new")) {
            ActionsEditorDialog.showActionsEditorDialog(this);
        }
    }
    
}
