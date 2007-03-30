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

package uk.org.ownage.dmdirc;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import uk.org.ownage.dmdirc.commandparser.CommandWindow;
import uk.org.ownage.dmdirc.identities.ConfigManager;
import uk.org.ownage.dmdirc.logger.ErrorLevel;
import uk.org.ownage.dmdirc.logger.Logger;
import uk.org.ownage.dmdirc.parser.IRCParser;
import uk.org.ownage.dmdirc.parser.callbacks.interfaces.IDataIn;
import uk.org.ownage.dmdirc.parser.callbacks.interfaces.IDataOut;
import uk.org.ownage.dmdirc.ui.MainFrame;
import uk.org.ownage.dmdirc.ui.ServerFrame;
import uk.org.ownage.dmdirc.ui.messages.ColourManager;



/**
 * Handles the raw window (which shows the user raw data being sent and
 * received to/from the server).
 * @author chris
 */
public final class Raw implements IDataIn, IDataOut, InternalFrameListener,
        FrameContainer {
    
    /**
     * The server object that's being monitored.
     */
    private Server server;
    /**
     * A serverframe instance used for displaying the raw data.
     */
    private ServerFrame frame;
    /**
     * The icon being used for this raw frame.
     */
    private final ImageIcon imageIcon;
    
    /**
     * Creates a new instance of Raw.
     * @param newServer the server to monitor
     */
    public Raw(final Server newServer) {
        this.server = newServer;
        
        final ClassLoader cldr = this.getClass().getClassLoader();
        final URL imageURL = cldr.getResource("uk/org/ownage/dmdirc/res/raw.png");
        imageIcon = new ImageIcon(imageURL);
        
        frame = new ServerFrame(server);
        frame.setTitle("(Raw log)");
        frame.addInternalFrameListener(this);
        MainFrame.getMainFrame().addChild(frame);
        frame.setTabCompleter(server.getTabCompleter());
        frame.setFrameIcon(imageIcon);
        
        frame.open();
    }
    
    /**
     * Closes the raw window. Removes parser callbacks, removes the actual.
     * frame, and removes references to the frame and server.
     */
    public void close() {
        server.getParser().getCallbackManager().delCallback("OnDataIn", this);
        server.getParser().getCallbackManager().delCallback("OnDataOut", this);
        
        frame.setVisible(false);
        MainFrame.getMainFrame().delChild(frame);
        frame = null;
        
        server.delRaw();
        
        server = null;
    }
    
    /**
     * Returns the internal frame belonging to this object.
     * @return This object's internal frame
     */
    public CommandWindow getFrame() {
        return frame;
    }
    
    /**
     * Called when the parser receives a line of data. The data is simply
     * logged to the raw window.
     * @param parser A reference to the IRC parser
     * @param data The data that was received
     */
    public void onDataIn(final IRCParser parser, final String data) {
        frame.addLine("<<< " + data);
    }
    
    /**
     * Called when the parser receives a line of data. The data is simply
     * logged to the raw window.
     * @param parser A reference to the IRC parser
     * @param data The data that was received
     * @param fromParser true if sent from parser, false otherwise
     */
    public void onDataOut(final IRCParser parser, final String data,
            final boolean fromParser) {
        frame.addLine(">>> " + data);
    }
    
    /**
     * Determines if the specified frame is owned by this object.
     *
     * @param target JInternalFrame to check ownership of
     * @return boolean ownership of the frame
     */
    public boolean ownsFrame(final JInternalFrame target) {
        return frame.equals(target);
    }
    
    /**
     * Called when the raw frame is opened. Checks config settings to
     * determine if the window should be maximised.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameOpened(final InternalFrameEvent internalFrameEvent) {
        final Boolean pref = Boolean.parseBoolean(server.getConfigManager().getOption("ui", "maximisewindows"));
        if (pref || MainFrame.getMainFrame().getMaximised()) {
            try {
                frame.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.error(ErrorLevel.WARNING, "Unable to maximise raw", ex);
            }
        }
    }
    
    /**
     * Called when the raw frame is being closed. Removes callbacks and releases
     * resources.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
        close();
    }
    
    /**
     * Called when the raw frame is actually closed. Not implemented.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameClosed(final InternalFrameEvent internalFrameEvent) {
    }
    
    /**
     * Called when the raw frame is iconified. Not implemented.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameIconified(final InternalFrameEvent internalFrameEvent) {
    }
    
    /**
     * Called when the raw frame is deiconified. Not implemented.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent) {
    }
    
    /**
     * Called when the raw frame is activated. Maximises the frame if it
     * needs to be.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameActivated(final InternalFrameEvent internalFrameEvent) {
        if (MainFrame.getMainFrame().getMaximised()) {
            try {
                frame.setMaximum(true);
            } catch (PropertyVetoException ex) {
                Logger.error(ErrorLevel.WARNING, "Unable to maximise raw", ex);
            }
        }
        server.setActiveFrame(this);
        MainFrame.getMainFrame().getFrameManager().setSelected(this);
    }
    
    /**
     * Called when the raw frame is deactivated. Not implemented.
     * @param internalFrameEvent The event that triggered this callback
     */
    public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent) {
    }
    
    /**
     * Returns "Raw"...
     * @return A string representation of this raw object
     */
    public String toString() {
        return "Raw";
    }
    
    /**
     * Requests that this object's frame be activated.
     */
    public void activateFrame() {
        MainFrame.getMainFrame().setActiveFrame(frame);
    }
    
    /**
     * Adds a line of text to the main text area of the raw frame.
     * @param line The line to add
     */
    public void addLine(final String line) {
        frame.addLine(line);
    }
    
    /**
     * Retrieves the icon used by the raw frame.
     * @return The raw frame's icon
     */
    public ImageIcon getIcon() {
        return imageIcon;
    }
    
    /**
     * Sends a notification to the frame manager if this frame isn't active.
     */
    public void sendNotification() {
        if (!MainFrame.getMainFrame().getActiveFrame().equals(frame)) {
            final Color colour = ColourManager.getColour(4);
            MainFrame.getMainFrame().getFrameManager().showNotification(this, colour);
        }
    }
    
    /**
     * Returns the server instance associated with this frame.
     *
     * @return the associated server connection
     */
    public Server getServer() {
        return server;
    }
    
    /**
     * Returns this raw's config manager.
     * @return This raw's config manager
     */
    public ConfigManager getConfigManager() {
        return server.getConfigManager();
    }    
    
}
