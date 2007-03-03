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

package uk.org.ownage.dmdirc.ui.input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import uk.org.ownage.dmdirc.Config;

/**
 * Handles events generated by a user typing into a textfield. Allows the user
 * to use shortcut keys for control characters (ctrl+b, etc), to tab complete
 * nicknames/channel names/etc, and to scroll through their previously issued
 * commands.
 * @author chris
 */
public class InputHandler implements KeyListener, ActionListener {
    
    /**
     * The current position in the buffer (where the user has scrolled back
     * to).
     */
    private int bufferPosition;
    /**
     * The maximum size of the buffer
     */
    private int bufferSize;
    /**
     * The maximum position we've got to in the buffer. This will be the
     * position that is inserted to next. Note that it will wrap around once
     * we hit the maximum size.
     */
    private int bufferMaximum;
    /**
     * The lowest entry we've used in the buffer
     */
    private int bufferMinimum;
    /**
     * The buffer itself
     */
    private String[] buffer;
    /**
     * The textfield that we're handling input for
     */
    private JTextField target;
    /**
     * The TabCompleter to use for tab completion
     */
    private TabCompleter tabCompleter;
    
    /**
     * Creates a new instance of InputHandler. Adds listeners to the target
     * that we need to operate.
     * @param target The text field this input handler is dealing with.
     */
    public InputHandler(JTextField target) {
        bufferSize = Integer.parseInt(Config.getOption("ui","inputbuffersize"));
        
        this.target = target;
        this.buffer = new String[bufferSize];
        bufferPosition = 0;
        bufferMinimum = 0;
        bufferMaximum = 0;
        
        target.addKeyListener(this);
        target.addActionListener(this);
        target.setFocusTraversalKeysEnabled(false);
    }
    
    /**
     * Sets this input handler's tab completer
     * @param tabCompleter The new tab completer
     */
    public void setTabCompleter(TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }
    
    /**
     * Called when the user types a normal character
     * @param keyEvent The event that was fired
     */
    public void keyTyped(KeyEvent keyEvent) {
        
    }
    
    /**
     * Called when the user presses down any key. Handles the insertion of
     * control codes, tab completion, and scrolling the back buffer.
     * @param keyEvent The event that was fired
     */
    public void keyPressed(KeyEvent keyEvent) {
        // Formatting codes
        if ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_B) {
                append("" + (char)2);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_U) {
                append("" + (char)31);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_O) {
                append("" + (char)15);
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_K) {
                append("" + (char)3);
            }
        }
        
        // Back buffer scrolling
        if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (bufferPosition != bufferMinimum) {
                bufferPosition = normalise(bufferPosition-1);
                retrieveBuffer();
            } else {
                // TODO: Beep, or something.
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            if (bufferPosition != bufferMaximum) {
                bufferPosition = normalise(bufferPosition+1);
                retrieveBuffer();
            } else {
                // TODO: Beep, or something
            }
        }
        
        // Tab completion
        if (keyEvent.getKeyCode() == KeyEvent.VK_TAB && tabCompleter != null) {
            String text = target.getText();
            
            if (text.equals("")) {
                return;
            }
            
            int pos = target.getCaretPosition()-1;
            int start = (pos < 0) ? 0 : pos;
            int end = (pos < 0) ? 0 : pos;
            
            // Traverse backwards
            while (start > 0 && text.charAt(start) != ' ') {
                start--;
            }
            if (text.charAt(start) == ' ') { start++; }
            
            // And forwards
            while (end < text.length() && text.charAt(end) != ' ') {
                end++;
            }
            
            if (start > end) {
                return;
            }
            
            String word = text.substring(start, end);
            
            TabCompleterResult res = tabCompleter.complete(word);
            
            if (res.getResultCount() == 0) {
                // TODO: Beep, or something
            } else if (res.getResultCount() == 1) {
                // One result, just replace it
                String result = res.getResults().get(0);
                text = text.substring(0,start)+result+text.substring(end);
                target.setText(text);
                target.setCaretPosition(start+result.length());
            } else {
                // Multiple results
                String sub = res.getBestSubstring();
                if (sub == word) {
                    // TODO: Beep, display possible answers, etc
                } else {
                    text = text.substring(0,start)+sub+text.substring(end);
                    target.setText(text);
                    target.setCaretPosition(start+sub.length());
                }
            }
            
        }
    }
    
    /**
     * Called when the user releases any key
     * @param keyEvent The event that was fired
     */
    public void keyReleased(KeyEvent keyEvent) {
    }
    
    /**
     * Called when the user presses return in the text area. The line they
     * typed is added to the buffer for future use.
     * @param actionEvent The event that was fired
     */
    public void actionPerformed(ActionEvent actionEvent) {
        buffer[bufferMaximum] = actionEvent.getActionCommand();
        bufferMaximum = normalise(bufferMaximum + 1);
        bufferPosition = bufferMaximum;
        
        if (buffer[bufferSize-1] != null) {
            bufferMinimum = normalise(bufferMaximum + 1);
            buffer[bufferMaximum] = null;
        }
    }
    
    /**
     * Appends the specified string to the end of the textbox
     * @param string The string to be appeneded
     */
    private void append(String string) {
        target.setText(target.getText()+string);
    }
    
    /**
     * Retrieves the buffered text stored at the position indicated by
     * bufferPos, and replaces the current textbox content with it
     */
    private void retrieveBuffer() {
        target.setText(buffer[bufferPosition]);
    }
    
    /**
     * Normalises the input so that it is in the range 0 <= x < bufferSize.
     * @param input The number to normalise
     * @return The normalised number
     */
    private int normalise(int input) {
        while (input < 0) {
            input = input + bufferSize;
        }
        return (input % bufferSize);
    }
    
}
