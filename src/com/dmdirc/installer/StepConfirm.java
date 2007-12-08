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

package com.dmdirc.installer;

import com.dmdirc.installer.Installer.ShortcutType;
import com.dmdirc.ui.swing.dialogs.wizard.Step;
import com.dmdirc.ui.swing.dialogs.wizard.StepListener;
import com.dmdirc.ui.swing.dialogs.wizard.WizardFrame;
import static com.dmdirc.ui.swing.UIUtilities.LARGE_BORDER;
import static com.dmdirc.ui.swing.UIUtilities.SMALL_BORDER;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * This confirms the settings chosen in the previous step
 */
public final class StepConfirm extends Step implements StepListener {
	/**
	 * A version number for this class. It should be changed whenever the class
	 * structure is changed (or anything else that would prevent serialized
	 * objects being unserialized with the new class).
	 */
	private static final long serialVersionUID = 2;
	
	/** Text area showing the install information */
	private JTextArea infoLabel = new JTextArea("");
	
	/**
	* Creates a new instance of StepConfirm.
	* @param dialog parent wizard dialog
	*/
	public StepConfirm(final WizardFrame dialog) {
		super();
		dialog.addStepListener(this);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(LARGE_BORDER, LARGE_BORDER, SMALL_BORDER, LARGE_BORDER));
		
		infoLabel.setEditable(false);
		infoLabel.setWrapStyleWord(true);
		infoLabel.setLineWrap(true);
		infoLabel.setHighlighter(null);
		infoLabel.setOpaque(false);
//		infoLabel.setBackground(getBackground());
		infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SMALL_BORDER, 0));
			
		add(infoLabel, BorderLayout.CENTER);
	}

	/** {@inheritDoc} */
	@Override
	public void stepAboutToDisplay(final Step step) {
		if (step != this) { return; }
		String shortcutText = "";
		
		StepSettings settings = ((StepSettings) Main.getWizardFrame().getStep(1));
		
		if (Main.getInstaller().supportsShortcut(ShortcutType.MENU)) {
			if (settings.getShortcutMenuState()) {
				shortcutText = shortcutText + " - Do set up Menu shortcuts"+ "\n";
			} else {
				shortcutText = shortcutText + " - Do not set up Menu shortcuts"+ "\n";
			}
		}
		
		if (Main.getInstaller().supportsShortcut(ShortcutType.DESKTOP)) {
			if (settings.getShortcutDesktopState()) {
				shortcutText = shortcutText + " - Do set up Desktop shortcuts"+ "\n";
			} else {
				shortcutText = shortcutText + " - Do not set up Desktop shortcuts"+ "\n";
			}
		}
		
		if (Main.getInstaller().supportsShortcut(ShortcutType.QUICKLAUNCH)) {
			if (settings.getShortcutQuickState()) {
				shortcutText = shortcutText + " - Do set up Quick Launch shortcuts"+ "\n";
			} else {
				shortcutText = shortcutText + " - Do not set up Quick Launch shortcuts"+ "\n";
			}
		}
		
		if (Main.getInstaller().supportsShortcut(ShortcutType.PROTOCOL)) {
			if (settings.getShortcutProtocolState()) {
				shortcutText = shortcutText + " - Do set up DMDirc to handle irc:// links"+ "\n";
			} else {
				shortcutText = shortcutText + " - Do not set up DMDirc to handle irc:// links"+ "\n";
			}
		}
		
		
		
		infoLabel.setText("Please check that these settings are correct:\n\n"
		                + " - Install Location:\n"
		                + "    " +((StepSettings) Main.getWizardFrame().getStep(1)).getInstallLocation() + "\n"
		                + shortcutText + "\n"
		                + "If these are correct, press Next to begin the installation, else press Previous to change them");
    }

	/** {@inheritDoc} */
	@Override
	public void stepHidden(final Step step ) {
	//Ignore
	}
}
