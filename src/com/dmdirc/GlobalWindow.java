/*
 * Copyright (c) 2006-2013 DMDirc Developers
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

package com.dmdirc;

import com.dmdirc.ClientModule.GlobalConfig;
import com.dmdirc.commandparser.CommandType;
import com.dmdirc.commandparser.parsers.CommandParser;
import com.dmdirc.commandparser.parsers.GlobalCommandParser;
import com.dmdirc.interfaces.CommandController;
import com.dmdirc.interfaces.config.AggregateConfigProvider;
import com.dmdirc.interfaces.config.ConfigChangeListener;
import com.dmdirc.messages.MessageSinkManager;
import com.dmdirc.ui.WindowManager;
import com.dmdirc.ui.core.components.WindowComponent;
import com.dmdirc.ui.input.TabCompleter;
import com.dmdirc.ui.input.TabCompleterFactory;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;

/**
 * A window which can be used to execute global commands.
 */
public class GlobalWindow extends WritableFrameContainer {

    /** The global window that's in use, if any. */
    private static GlobalWindow globalWindow;

    /** The tab completer we use. */
    @Getter
    @SuppressWarnings("PMD.UnusedPrivateField")
    private final TabCompleter tabCompleter;

    /**
     * Creates a new instance of GlobalWindow.
     *
     * @param config The ConfigManager to retrieve settings from.
     * @param parser The command parser to use to parse input.
     * @param windowManager The window manager.
     * @param tabCompleterFactory The factory to use to create tab completers.
     * @param messageSinkManager The sink manager to use to despatch messages.
     */
    public GlobalWindow(
            final AggregateConfigProvider config,
            final CommandParser parser,
            final WindowManager windowManager,
            final TabCompleterFactory tabCompleterFactory,
            final MessageSinkManager messageSinkManager) {
        super("icon", "Global", "(Global)", config, parser, messageSinkManager, windowManager,
                Arrays.asList(
                        WindowComponent.TEXTAREA.getIdentifier(),
                        WindowComponent.INPUTFIELD.getIdentifier()));

        tabCompleter = tabCompleterFactory.getTabCompleter(config, CommandType.TYPE_GLOBAL);

        windowManager.addWindow(this);
    }

    /** {@inheritDoc} */
    @Override
    public void windowClosing() {
        // 2: Remove any callbacks or listeners
        // 3: Trigger any actions neccessary
        // 4: Trigger action for the window closing
        // 5: Inform any parents that the window is closing
    }

    /** {@inheritDoc} */
    @Override
    public void windowClosed() {
        // 7: Remove any references to the window and parents
        globalWindow = null;
    }

    /** {@inheritDoc} */
    @Override
    public Server getServer() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void sendLine(final String line) {
        addLine("commandError", "You may only enter commands in the global window.");
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxLineLength() {
        return -1;
    }

    /** Handles the state of the global window. */
    @Singleton
    public static class GlobalWindowManager implements ConfigChangeListener {

        /** The global configuration to read settings from. */
        private final AggregateConfigProvider globalConfig;
        /** The factory to use to create tab completers. */
        private final TabCompleterFactory tabCompleterFactory;
        /** The provider to use to retrieve a command controller. */
        private final Provider<CommandController> commandControllerProvider;
        /** The provider to use to retrieve a window manager. */
        private final Provider<WindowManager> windowManagerProvider;
        /** The provider to use to retrieve message sink managers. */
        private final Provider<MessageSinkManager> messageSinkManagerProvider;

        /**
         * Creates a new instance of {@link GlobalWindowManager}.
         *
         * @param globalConfig Configuration provider to read settings from.
         * @param tabCompleterFactory Factory to use to create tab completers.
         * @param commandControllerProvider The provider to use to retrieve a command controller.
         * @param windowManagerProvider The provider to use to retrieve a window manager.
         * @param messageSinkManagerProvider The provider to use to retrieve a sink manager.
         */
        @Inject
        public GlobalWindowManager(
                @GlobalConfig final AggregateConfigProvider globalConfig,
                final TabCompleterFactory tabCompleterFactory,
                final Provider<CommandController> commandControllerProvider,
                final Provider<WindowManager> windowManagerProvider,
                final Provider<MessageSinkManager> messageSinkManagerProvider) {
            this.globalConfig = globalConfig;
            this.tabCompleterFactory = tabCompleterFactory;
            this.commandControllerProvider = commandControllerProvider;
            this.windowManagerProvider = windowManagerProvider;
            this.messageSinkManagerProvider = messageSinkManagerProvider;
        }

        /** {@inheritDoc} */
        @Override
        public void configChanged(final String domain, final String key) {
            updateWindowState();
        }

        /**
         * Initialises the global window if it's enabled in the config.
         */
        public void init() {
            globalConfig.addChangeListener("general", "showglobalwindow", this);
            updateWindowState();
        }

        /**
         * Updates the state of the global window in line with the
         * general.showglobalwindow config setting.
         */
        protected void updateWindowState() {
            synchronized (GlobalWindow.class) {
                if (globalConfig.getOptionBool("general", "showglobalwindow")) {
                    if (globalWindow == null) {
                        globalWindow = new GlobalWindow(globalConfig,
                                new GlobalCommandParser(globalConfig, commandControllerProvider.get()),
                                windowManagerProvider.get(), tabCompleterFactory,
                                messageSinkManagerProvider.get());
                    }
                } else {
                    if (globalWindow != null) {
                        globalWindow.close();
                    }
                }
            }
        }

    }

}
