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

package com.dmdirc.updater;

import com.dmdirc.Main;
import com.dmdirc.logger.ErrorLevel;
import com.dmdirc.logger.Logger;
import com.dmdirc.ui.MainFrame;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * The update checker contacts the DMDirc website to check to see if there
 * are any updates available.
 * @author chris
 */
public final class UpdateChecker implements Runnable {
    
    private JLabel label;
    
    /** {@inheritDoc} */
    public void run() {
        MainFrame.getMainFrame().getStatusBar().setMessage("Checking for updates");
        
        URL url;
        URLConnection urlConn;
        DataOutputStream printout;
        BufferedReader printin;
        try {
            url = new URL("http://www.dmdirc.com/update.php");
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            printout = new DataOutputStream(urlConn.getOutputStream());
            final String content = "component=client&channel="
                    + Main.UPDATE_CHANNEL + "&date=" + Main.RELEASE_DATE;
            printout.writeBytes(content);
            printout.flush();
            printout.close();
            printin = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            
            String line = null;
            do {
                if (line != null && line.length() > 0) {
                    checkLine(line);
                }
                
                line = printin.readLine();
            } while (line != null);
        } catch (MalformedURLException ex) {
            Logger.error(ErrorLevel.WARNING, "Unable to check for updates", ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.error(ErrorLevel.WARNING, "Unable to check for updates", ex);
        } catch (IOException ex) {
            Logger.error(ErrorLevel.WARNING, "Unable to check for updates", ex);
        }
    }
    
    private void checkLine(final String line) {
        if (line.equals("uptodate")) {
            MainFrame.getMainFrame().getStatusBar().setMessage("No updates available");
        } else if (line.startsWith("outofdate")) {
            doUpdateAvailable();
        } else {
            Logger.error(ErrorLevel.WARNING, "Unknown response from update server: " + line);
        }
    }
    
    private void doUpdateAvailable() {
        if (label == null) {
            final ClassLoader classLoader = getClass().getClassLoader();
            final ImageIcon icon = new ImageIcon(classLoader.getResource("com/dmdirc/res/update.png"));
            
            label = new JLabel();
            label.setBorder(BorderFactory.createEtchedBorder());
            label.setIcon(icon);
            MainFrame.getMainFrame().getStatusBar().addComponent(label);
        }
    }
    
}