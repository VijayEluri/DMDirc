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

package com.dmdirc.addons.dcc;

import com.dmdirc.IconManager;
import com.dmdirc.Server;
import com.dmdirc.WritableFrameContainer;
import com.dmdirc.commandparser.CommandManager;
import com.dmdirc.commandparser.commands.Command;
import com.dmdirc.commandparser.commands.GlobalCommand;
import com.dmdirc.commandparser.parsers.CommandParser;
import com.dmdirc.commandparser.parsers.GlobalCommandParser;
import com.dmdirc.config.ConfigManager;
import com.dmdirc.config.IdentityManager;
import com.dmdirc.ui.WindowManager;
import com.dmdirc.ui.interfaces.InputWindow;
import com.dmdirc.ui.swing.components.InputTextFrame;

/**
 * This class links DCC objects to a window.
 *
 * @author Shane 'Dataforce' McCormack
 * @version $Id: DCC.java 969 2007-04-30 18:38:20Z ShaneMcC $
 */
public abstract class DCCFrame extends WritableFrameContainer {
	/**
	 * Empty Frame.
	 */
	private class EmptyFrame extends InputTextFrame {
			/** A version number for this class. */
			private static final long serialVersionUID = 200711271;
			
			/**
			 * Creates a new instance of EmptyFrame.
			 *
			 * @param owner The frame container that owns this frame
			 */
			public EmptyFrame(final WritableFrameContainer owner) {
					super(owner);
					setTextPane(null);
					pack();
			}
			
			/**
			 * Retrieves the command Parser for this input window.
			 *
			 * @return This window's command parser
			 */
			public final CommandParser getCommandParser() {
				return GlobalCommandParser.getGlobalCommandParser();
			}
	}
	
	/**
	 * DCC CommandParser
	 */
	private static class DCCCommandParser extends CommandParser {
		/** A version number for this class. */
		private static final long serialVersionUID = 200711271;
		
		/** Loads the relevant commands into the parser. */
        @Override
		protected void loadCommands() { CommandManager.loadGlobalCommands(this); }
		
		/**
		 * Executes the specified command with the given arguments.
		 *
		 * @param origin The window in which the command was typed
		 * @param isSilent Whether the command is being silenced or not
		 * @param command The command to be executed
		 * @param args The arguments to the command
		 */
        @Override
		protected void executeCommand(final InputWindow origin, final boolean isSilent, final Command command, final String... args) {
			((GlobalCommand) command).execute(origin, isSilent, args);
		}
		
		/**
		 * Called when the input was a line of text that was not a command.
		 * This normally means it is sent to the server/channel/user as-is, with
		 * no further processing.
		 *
		 * @param origin The window in which the command was typed
		 * @param line The line input by the user
		 */
        @Override
		protected void handleNonCommand(final InputWindow origin, final String line) {
			if (origin.getContainer() instanceof WritableFrameContainer) {
				((WritableFrameContainer)origin.getContainer()).sendLine(line);
			}
		}
	}
	
	/** The singleton instance of the DCC command parser. */
	private static DCCCommandParser myDCCCommandParser;

	/** The window title. */
	protected final String title;
	/** The Window we're using. */
	protected InputWindow myWindow = null;
	/** The dcc plugin that owns this frame */
	protected final DCCPlugin plugin;
	
	/**
	 * Creates a new instance of DCCFrame with an empty window.
	 *
	 * @param plugin The DCCPlugin that owns this frame
	 * @param title The title of this window
	 */
	public DCCFrame(final DCCPlugin plugin, final String title) {
		this(plugin, title, true);
	}
	
	/**
	 * Creates a new instance of DCCFrame.
	 *
	 * @param plugin The DCCPlugin that owns this frame
	 * @param title The title of this window
	 * @param defaultWindow Create default (empty) window.
	 */
	public DCCFrame(final DCCPlugin plugin, final String title, final boolean defaultWindow) {
		super();
		this.title = title;
		this.plugin = plugin;
		
		icon = IconManager.getIconManager().getIcon("raw");

		if (defaultWindow) {
			myWindow = new EmptyFrame(this);
			myWindow.setTitle(title);
			myWindow.setFrameIcon(icon);
			myWindow.setVisible(true);
		}
	}
	
	/**
	 * Retrieves a singleton instance of the DCC command parser.
	 *
	 * @return A DCCCommandParser
	 */
	public static synchronized DCCCommandParser getDCCCommandParser() {
		if (myDCCCommandParser == null) {
			myDCCCommandParser = new DCCCommandParser();
		}
		return myDCCCommandParser;
	}
	
	/**
	 * Sends a line of text to this container's source.
	 *
	 * @param line The line to be sent
	 */
	@Override
	public void sendLine(final String line) {
		return;
	}
	
	/**
	 * Returns the maximum length that a line passed to sendLine() should be,
	 * in order to prevent it being truncated or causing protocol violations.
	 *
	 * @return The maximum line length for this container
	 */
	@Override
	public int getMaxLineLength() {
		return 512;
	}
	
	/**
	 * Returns the internal frame associated with this object.
	 *
	 * @return The internal frame associated with this object
	 */
	@Override
	public InputWindow getFrame() {
		return myWindow;
	}
	
	/**
	 * Returns a string identifier for this object/its frame.
	 *
	 * @return String identifier
	 */
	@Override
	public String toString() {
		return title;
	}
	
	/**
	 * Retrieves the config manager for this command window.
	 *
	 * @return This window's config manager
	 */
	@Override
	public ConfigManager getConfigManager() {
		return IdentityManager.getGlobalConfig();
	}
	
	/**
	 * Returns the server instance associated with this container.
	 *
	 * @return the associated server connection
	 */
	@Override
	public Server getServer() {
		return null;
	}
	
	/** {@inheritDoc} */
	@Override
	public void windowClosing() {
                // 1: Make the window non-visible
                myWindow.setVisible(false);

                // 2: Remove any callbacks or listeners
                // 3: Trigger any actions neccessary
                // 4: Trigger action for the window closing
                
                // 5: Inform any parents that the window is closing
                plugin.delWindow(this);

                // 6: Remove the window from the window manager
                WindowManager.removeWindow(myWindow);

                // 7: Remove any references to the window and parents
                myWindow = null; // NOPMD
	}   
}
