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

package com.dmdirc.ui.components;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.dmdirc.ui.MainFrame;
import com.dmdirc.ui.messages.ColourManager;
import com.dmdirc.ui.textpane.TextPane;

import static com.dmdirc.ui.UIUtilities.SMALL_BORDER;
import static com.dmdirc.ui.UIUtilities.layoutGrid;

/**
 * Status bar, shows message and info on the gui.
 */
public final class SearchBar extends JPanel implements ActionListener,
        KeyListener {
    
    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 4;
    
    /** Frame parent. */
    private final Frame parent;
    
    /** Close button. */
    private JButton closeButton;
    
    /** Next match button. */
    private JButton nextButton;
    
    /** Previous match button. */
    private JButton prevButton;
    
    /** Case sensitive checkbox. */
    private JCheckBox caseCheck;
    
    /** Search text field. */
    private JTextField searchBox;
    
    /** Direction used for searching. */
    private enum Direction {
        /** Move up through the document. */
        UP,
        /** Move down through the document. */
        DOWN,
    };
    
    /**
     * Creates a new instance of StatusBar.
     * @param newParent parent frame for the dialog
     */
    public SearchBar(final Frame newParent) {
        super();
        
        this.parent = newParent;
        
        initComponents();
        layoutComponents();
        addListeners();
    }
    
    /** Initialises components. */
    private void initComponents() {
        closeButton = new JButton();
        nextButton = new JButton();
        prevButton = new JButton();
        caseCheck = new JCheckBox();
        searchBox = new JTextField();
        
        nextButton.setText("Later");
        prevButton.setText("Earlier");
        caseCheck.setText("Case sensitive");
        
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setPreferredSize(new Dimension(50, 0));
        
        nextButton.setMargin(new Insets(0, 0, 0, 0));
        nextButton.setPreferredSize(new Dimension(50, 0));
        
        prevButton.setMargin(new Insets(0, 0, 0, 0));
        prevButton.setPreferredSize(new Dimension(50, 0));
        
        caseCheck.setPreferredSize(new Dimension(110, 0));
        
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                searchBox.getBorder(), new EmptyBorder(2, 2, 2, 2)));
        
        searchBox.setPreferredSize(new Dimension(300, searchBox.getFont().getSize() - SMALL_BORDER));
        
        closeButton.setIcon(new ImageIcon(this.getClass()
        .getClassLoader().getResource("com/dmdirc/res/close-inactive.png")));
        closeButton.setRolloverIcon(new ImageIcon(this.getClass()
        .getClassLoader().getResource("com/dmdirc/res/close-active.png")));
        closeButton.setPressedIcon(new ImageIcon(this.getClass()
        .getClassLoader().getResource("com/dmdirc/res/close-active.png")));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setPreferredSize(new Dimension(16, 16));
    }
    
    /** Lays out components. */
    private void layoutComponents() {
        this.setLayout(new SpringLayout());
        
        add(closeButton);
        add(searchBox);
        add(nextButton);
        add(prevButton);
        add(caseCheck);
        
        layoutGrid(this, 1, 5, SMALL_BORDER, 0, SMALL_BORDER, SMALL_BORDER);
    }
    
    /** Adds listeners to components. */
    private void addListeners() {
        closeButton.addActionListener(this);
        this.addKeyListener(this);
        searchBox.addKeyListener(this);
        nextButton.addActionListener(this);
        prevButton.addActionListener(this);
    }
    
    /** {@inheritDoc}. */
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == closeButton) {
            close();
        } else if (e.getSource() == nextButton) {
            search(Direction.DOWN, parent.getTextPane().getLastVisibleLine(),
                    searchBox.getText(), caseCheck.isSelected());
        } else if (e.getSource() == prevButton) {
            search(Direction.UP, parent.getTextPane().getLastVisibleLine(),
                    searchBox.getText(), caseCheck.isSelected());
        }
    }
    
    /** Shows the search bar in the frame. */
    public void open() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                searchBox.requestFocus();
            }
        }
        );
    }
    
    /** Hides the search bar in the frame. */
    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(false);
            }
        }
        );
    }
    
    /**
     * Searches the textpane for text.
     *
     * @param direction the direction to search from
     * @param line the line to search from
     * @param text the text to search for
     * @param caseSensitive whether the search is case sensitive
     */
    public void search(final Direction direction, final int line,
            final String text, final boolean caseSensitive) {
        boolean foundText;
        
        if (direction == Direction.UP) {
            foundText = searchUp(line, text);
        } else {
            foundText = searchDown(line, text);
        }
        
        if (foundText) {
            searchBox.setBackground(ColourManager.getColour("FFFFFF"));
        } else {
            searchBox.setBackground(ColourManager.getColour("FF0000"));
        }
    }
    
    /**
     * Searches up in the buffer for the text.
     *
     * @param line Line to start from
     * @param text Text to search for
     *
     * @return Whether the specified text was found
     */
    private boolean searchUp(final int line, final String text) {
        final TextPane textPane = parent.getTextPane();
        final int thisLine;
        boolean foundText = false;
        int i;
        
        if (line > textPane.getNumLines() - 1) {
            thisLine = textPane.getNumLines() - 1;
        } else {
            thisLine = line;
        }
        for (i = thisLine; i >= 0; i--) {
            final String lineText = textPane.getTextFromLine(i);
            final int position = lineText.indexOf(text);
            if (position != -1 && textPane.getSelectedRange()[0] != i) {
                textPane.setScrollBarPosition(i);
                textPane.setSelectedTexT(i, position, i, position + text.length());
                foundText = true;
                break;
            }
        }
        
        if (i >= 0) {
            return foundText;
        }
        
        //want to continue?
        if (JOptionPane.showConfirmDialog(MainFrame.getMainFrame(),
                "Do you want to continue searching from the end?",
                "Beginning reached", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
            foundText = searchUp(textPane.getNumLines() - 1, text);
        }
        
        return foundText;
    }
    
    /**
     * Searches down in the buffer for the text.
     *
     * @param line Line to start from
     * @param text Text to search for
     *
     * @return Whether the specified text was found
     */
    private boolean searchDown(final int line, final String text) {
        final TextPane textPane = parent.getTextPane();
        boolean foundText = false;
        int i;
        
        for (i = line; i < textPane.getNumLines() - 1; i++) {
            final String lineText = textPane.getTextFromLine(i);
            final int position = lineText.indexOf(text);
            if (position != -1 && textPane.getSelectedRange()[0] != i) {
                textPane.setScrollBarPosition(i);
                textPane.setSelectedTexT(i, position, i, position + text.length());
                foundText = true;
                break;
            }
        }
        
        if (i < textPane.getNumLines() - 1) {
            return foundText;
        }
        
        //want to continue?
        if (JOptionPane.showConfirmDialog(MainFrame.getMainFrame(),
                "Do you want to continue searching from the beginning?",
                "End reached", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
            foundText = searchDown(0, text);
        }
        
        return foundText;
    }
    
    /** {@inheritDoc}. */
    public void keyTyped(final KeyEvent event) {
        //Ignore
    }
    
    /** {@inheritDoc}. */
    public void keyPressed(final KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_F3
                || (event.getKeyCode() == KeyEvent.VK_F
                && (event.getModifiers() & KeyEvent.CTRL_MASK) !=  0)) {
            close();
        }
        
        if (event.getSource() == searchBox) {
            searchBox.setBackground(ColourManager.getColour("FFFFFF"));
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                close();
            } else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                search(Direction.UP, parent.getTextPane().getLastVisibleLine(),
                        searchBox.getText(), caseCheck.isSelected());
            }
        }
    }
    
    /** {@inheritDoc}. */
    public void keyReleased(final KeyEvent event) {
        //Ignore
    }
}
