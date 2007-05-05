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

package uk.org.ownage.dmdirc.plugins.plugins.systray;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.ImageIcon;

import uk.org.ownage.dmdirc.Main;
import uk.org.ownage.dmdirc.plugins.Plugin;
import uk.org.ownage.dmdirc.ui.MainFrame;

/**
 * The Systray plugin shows DMDirc in the user's system tray, and allows
 * notifications to be disabled.
 * @author chris
 */
public final class SystrayPlugin implements Plugin, ActionListener, MouseListener {
    
    /** Is this plugin active? */
    private boolean isActive = false;
    
    /** The tray icon we're currently using. */
    private TrayIcon icon;
    
    /** The menu to use for the tray icon. */
    private final PopupMenu menu;
    
    /** Creates a new system tray plugin. */
    public SystrayPlugin() {
        final MenuItem show = new MenuItem("Show/hide");
        final MenuItem quit = new MenuItem("Quit");
        
        menu = new PopupMenu();
        menu.add(show);
        menu.add(quit);
        
        show.addActionListener(this);
        quit.addActionListener(this);
    }
    
    /**
     * Sends a notification via the system tray icon.
     * @param title The title of the notification
     * @param message The contents of the notification
     * @param type The type of notification
     */
    public void notify(final String title, final String message, final TrayIcon.MessageType type) {
        icon.displayMessage(title, message, type);
    }
    
    /**
     * Sends a notification via the system tray icon.
     * @param title The title of the notification
     * @param message The contents of the notification
     */
    public void notify(final String title, final String message) {
        notify(title, message, TrayIcon.MessageType.NONE);
    }
    
    /** {@inheritDoc} */
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("Show/hide")) {
            MainFrame.getMainFrame().setVisible(!MainFrame.getMainFrame().isVisible());
        } else if (e.getActionCommand().equals("Quit")) {
            Main.quit();
        }
    }
    
    /** {@inheritDoc} */
    public boolean onLoad() {
        if (SystemTray.isSupported()) {
            final ClassLoader cldr = this.getClass().getClassLoader();
            final URL imageURL = cldr.getResource("uk/org/ownage/dmdirc/res/logo.png");
            icon = new TrayIcon(new ImageIcon(imageURL).getImage(), "DMDirc", menu);
            icon.setImageAutoSize(true);
            icon.addMouseListener(this);
            
            try {
                SystemTray.getSystemTray().add(icon);
            } catch (AWTException ex) {
                return false;
            }
            
            new PopupCommand(this);
        } else {
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    public void onUnload() {
    }
    
    /** {@inheritDoc} */
    public void onActivate() {
        isActive = true;
    }
    
    /** {@inheritDoc} */
    public boolean isActive() {
        return isActive;
    }
    
    /** {@inheritDoc}. */
    public void onDeactivate() {
        SystemTray.getSystemTray().remove(icon);
        isActive = false;
    }
    
    /** {@inheritDoc} */
    public boolean isConfigurable() {
        return false;
    }
    
    /** {@inheritDoc} */
    public void showConfig() {
    }
    
    /** {@inheritDoc} */
    public int getVersion() {
        return 0;
    }
    
    /** {@inheritDoc} */
    public String getAuthor() {
        return "Chris 'MD87' Smith - chris@dmdirc.com";
    }
    
    /** {@inheritDoc} */
    public String getDescription() {
        return "Adds a system tray icon";
    }
    
    /** {@inheritDoc}. */
    public String toString() {
        return "Systray Plugin";
    }
    
    /** {@inheritDoc} */
    public void mouseClicked(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (MainFrame.getMainFrame().isVisible()) {
                // TODO: Uncomment the code below, with an appropriate replacement
                //       for requestFocus() that does something more than flash.
                
                //if (MainFrame.getMainFrame().isActive()) {
                MainFrame.getMainFrame().setVisible(false);
                //} else {
                //    MainFrame.getMainFrame().requestFocus();
                //}
            } else {
                MainFrame.getMainFrame().setVisible(true);
            }
        }
    }
    
    /** {@inheritDoc} */
    public void mousePressed(final MouseEvent e) {
    }
    
    /** {@inheritDoc} */
    public void mouseReleased(final MouseEvent e) {
    }
    
    /** {@inheritDoc} */
    public void mouseEntered(final MouseEvent e) {
    }
    
    /** {@inheritDoc} */
    public void mouseExited(final MouseEvent e) {
    }
    
}
