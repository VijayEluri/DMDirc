/*
 * Copyright (c) 2006-2014 DMDirc Developers
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

import com.dmdirc.commandparser.parsers.CommandParser;
import com.dmdirc.events.ClientLineAddedEvent;
import com.dmdirc.interfaces.Connection;
import com.dmdirc.interfaces.FrameCloseListener;
import com.dmdirc.interfaces.FrameComponentChangeListener;
import com.dmdirc.interfaces.FrameInfoListener;
import com.dmdirc.interfaces.NotificationListener;
import com.dmdirc.interfaces.config.AggregateConfigProvider;
import com.dmdirc.interfaces.config.ConfigChangeListener;
import com.dmdirc.messages.MessageSinkManager;
import com.dmdirc.parser.common.CompositionState;
import com.dmdirc.ui.Colour;
import com.dmdirc.ui.IconManager;
import com.dmdirc.ui.input.TabCompleter;
import com.dmdirc.ui.messages.Formatter;
import com.dmdirc.ui.messages.IRCDocument;
import com.dmdirc.ui.messages.Styliser;
import com.dmdirc.util.URLBuilder;
import com.dmdirc.util.collections.ListenerList;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkState;

/**
 * The frame container implements basic methods that should be present in all objects that handle a
 * frame.
 */
public abstract class FrameContainer {

    /** Listeners not yet using ListenerSupport. */
    protected final ListenerList listeners = new ListenerList();
    /** The colour of our frame's notifications. */
    private Optional<Colour> notification = Optional.absent();
    /** The document used to store this container's content. */
    private IRCDocument document;
    /** The children of this frame. */
    private final Collection<FrameContainer> children = new CopyOnWriteArrayList<>();
    /** The parent of this frame. */
    private final Optional<FrameContainer> parent;
    /** The name of the icon being used for this container's frame. */
    private String icon;
    /** The name of this container. */
    private String name;
    /** The title of this container. */
    private String title;
    /** The config manager for this container. */
    private final AggregateConfigProvider configManager;
    /** The IconChanger for this container. */
    private final IconChanger changer = new IconChanger();
    /** The UI components that this frame requires. */
    private final Set<String> components;
    /** The styliser used by this container. */
    private Styliser styliser;
    /** Object used to synchronise styliser access. */
    private final Object styliserSync = new Object();
    /** Object used to synchronise styliser access. */
    private final Object documentSync = new Object();
    /** Event bus to despatch events to. */
    private final EventBus eventBus;
    /** The icon manager to use for this container. */
    private final IconManager iconManager;
    /** Whether or not this container is writable. */
    private final boolean writable;
    /**
     * The command parser used for commands in this container.
     * <p>
     * Only defined if this container is {@link #writable}.
     */
    private final Optional<CommandParser> commandParser;
    /**
     * The manager to use to despatch messages to sinks.
     * <p>
     * Only defined if this container is {@link #writable}.
     */
    private final Optional<MessageSinkManager> messageSinkManager;
    /**
     * The tab completer to use.
     * <p>
     * Only defined if this container is {@link #writable}.
     */
    private final Optional<TabCompleter> tabCompleter;

    /**
     * Instantiate new frame container.
     *
     * @param parent     The parent of this frame container, if any.
     * @param icon       The icon to use for this container
     * @param name       The name of this container
     * @param title      The title of this container
     * @param config     The config manager for this container
     * @param urlBuilder The URL builder to use when finding icons.
     * @param eventBus   The bus to despatch events on.
     * @param components The UI components that this frame requires
     *
     * @since 0.6.4
     */
    protected FrameContainer(
            @Nullable final FrameContainer parent,
            final String icon,
            final String name,
            final String title,
            final AggregateConfigProvider config,
            final URLBuilder urlBuilder,
            final EventBus eventBus,
            final Collection<String> components) {
        this.parent = Optional.fromNullable(parent);
        this.configManager = config;
        this.name = name;
        this.title = title;
        this.components = new HashSet<>(components);
        this.iconManager = new IconManager(configManager, urlBuilder);
        this.eventBus = eventBus;
        this.writable = false;
        this.commandParser = Optional.absent();
        this.tabCompleter = Optional.absent();
        this.messageSinkManager = Optional.absent();

        setIcon(icon);
    }

    /**
     * Instantiate new frame container that accepts user input.
     *
     * @param parent             The parent of this frame container, if any.
     * @param icon               The icon to use for this container
     * @param name               The name of this container
     * @param title              The title of this container
     * @param config             The config manager for this container
     * @param urlBuilder         The URL builder to use when finding icons.
     * @param commandParser      The command parser to use for input.
     * @param tabCompleter       The tab completer to use.
     * @param messageSinkManager The manager to use to despatch notifications.
     * @param eventbus           The bus to despatch events on.
     * @param components         The UI components that this frame requires
     *
     * @since 0.6.4
     */
    protected FrameContainer(
            @Nullable final FrameContainer parent,
            final String icon,
            final String name,
            final String title,
            final AggregateConfigProvider config,
            final URLBuilder urlBuilder,
            final CommandParser commandParser,
            final TabCompleter tabCompleter,
            final MessageSinkManager messageSinkManager,
            final EventBus eventbus,
            final Collection<String> components) {
        this.parent = Optional.fromNullable(parent);
        this.configManager = config;
        this.name = name;
        this.title = title;
        this.components = new HashSet<>(components);
        this.iconManager = new IconManager(configManager, urlBuilder);
        this.eventBus = eventbus;
        this.writable = true;
        this.commandParser = Optional.of(commandParser);
        this.tabCompleter = Optional.of(tabCompleter);
        this.messageSinkManager = Optional.of(messageSinkManager);
        commandParser.setOwner(this);

        setIcon(icon);
    }

    public Optional<Colour> getNotification() {
        return notification;
    }

    public Optional<FrameContainer> getParent() {
        return parent;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public AggregateConfigProvider getConfigManager() {
        return configManager;
    }

    public boolean isWritable() {
        return writable;
    }

    /**
     * Returns a collection of direct children of this frame.
     *
     * @return This frame's children
     *
     * @since 0.6.4
     */
    public Collection<FrameContainer> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    /**
     * Adds a new child window to this frame.
     *
     * @param child The window to be added
     *
     * @since 0.6.4
     */
    public void addChild(final FrameContainer child) {
        children.add(child);
    }

    /**
     * Removes a child window from this frame.
     *
     * @param child The window to be removed
     *
     * @since 0.6.4
     */
    public void removeChild(final FrameContainer child) {
        children.remove(child);
    }

    /**
     * Gets an icon manager for this container.
     *
     * @return An icon manager for this container.
     */
    public IconManager getIconManager() {
        return iconManager;
    }

    /**
     * Retrieves the {@link IRCDocument} used to store this frame's content.
     *
     * @return This frame's document
     *
     * @since 0.6.4
     */
    public IRCDocument getDocument() {
        synchronized (documentSync) {
            if (document == null) {
                document = new IRCDocument(getConfigManager(), getStyliser());
            }
            return document;
        }
    }

    /**
     * Changes the name of this container, and notifies any {@link FrameInfoListener}s of the
     * change.
     *
     * @param name The new name for this frame.
     */
    protected void setName(final String name) {
        this.name = name;

        listeners.getCallable(FrameInfoListener.class).nameChanged(this, name);
    }

    /**
     * Changes the title of this container, and notifies any {@link FrameInfoListener}s of the
     * change.
     *
     * @param title The new title for this frame.
     */
    public void setTitle(final String title) {
        this.title = title;

        listeners.getCallable(FrameInfoListener.class).titleChanged(this, title);
    }

    /**
     * Returns the collection of UI component identifiers that this frame container requires for its
     * display.
     *
     * @since 0.6.6
     * @return Collection of UI component identifiers
     */
    public Set<String> getComponents() {
        return Collections.unmodifiableSet(components);
    }

    /**
     * Adds a new component to this container.
     *
     * @since 0.6.6
     * @param component The component to be added
     */
    public void addComponent(final String component) {
        components.add(component);

        for (FrameComponentChangeListener listener
                : listeners.get(FrameComponentChangeListener.class)) {
            listener.componentAdded(this, component);
        }
    }

    /**
     * Removes a component from this container.
     *
     * @since 0.6.6
     * @param component The component to be removed
     */
    public void removeComponent(final String component) {
        components.remove(component);

        for (FrameComponentChangeListener listener
                : listeners.get(FrameComponentChangeListener.class)) {
            listener.componentRemoved(this, component);
        }
    }

    /**
     * Closes this container (and its associated frame).
     */
    public void close() {
        for (FrameCloseListener listener : listeners.get(FrameCloseListener.class)) {
            listener.windowClosing(this);
            listeners.remove(FrameCloseListener.class, listener);
        }
    }

    /**
     * Returns the connection that this container is associated with.
     *
     * @return the associated connection, or {@code null}.
     */
    public abstract Connection getConnection();

    /**
     * Sets the icon to be used by this frame container.
     *
     * @param icon The new icon to be used
     */
    public final void setIcon(final String icon) {
        this.icon = icon;

        iconUpdated();

        configManager.removeListener(changer);
        configManager.addChangeListener("icon", icon, changer);
    }

    /**
     * Called when this container's icon is updated.
     */
    private void iconUpdated() {
        listeners.getCallable(FrameInfoListener.class).iconChanged(this, icon);
    }

    /**
     * Retrieves the styliser which should be used by this container.
     *
     * @return this container's styliser
     */
    public Styliser getStyliser() {
        synchronized (styliserSync) {
            if (styliser == null) {
                styliser = new Styliser(getConnection(), getConfigManager());
            }
            return styliser;
        }
    }

    /**
     * Clears any outstanding notifications this frame has set.
     */
    public void clearNotification() {
        notification = Optional.absent();
        listeners.getCallable(NotificationListener.class).notificationCleared(this);
    }

    /**
     * Sends a notification to the frame manager if this fame isn't active.
     *
     * @param colour The colour to use for the notification
     */
    public void sendNotification(final Colour colour) {
        if (!notification.isPresent() || !colour.equals(notification.get())) {
            notification = Optional.of(colour);
            listeners.getCallable(NotificationListener.class).notificationSet(this, colour);
        }
    }

    /**
     * Adds a line to this container's window. If the window is null for some reason, the line is
     * silently discarded.
     *
     * @param type      The message type to use
     * @param timestamp The timestamp to use for this line
     * @param args      The message's arguments
     *
     * @since 0.6.4
     */
    public void addLine(final String type, final Date timestamp,
            final Object... args) {
        if (type != null && !type.isEmpty()) {
            addLine(Formatter.formatMessage(getConfigManager(), type, args),
                    timestamp);
        }
    }

    /**
     * Adds a line to this container's window. If the window is null for some reason, the line is
     * silently discarded.
     *
     * @param type The message type to use
     * @param args The message's arguments
     */
    public void addLine(final String type, final Object... args) {
        addLine(type, new Date(), args);
    }

    /**
     * Adds a line to this container's window. If the window is null for some reason, the line is
     * silently discarded.
     *
     * @param type      The message type to use
     * @param timestamp The timestamp to use for this line
     * @param args      The message's arguments
     *
     * @since 0.6.4
     */
    public void addLine(final StringBuffer type, final Date timestamp,
            final Object... args) {
        if (type != null) {
            addLine(type.toString(), timestamp, args);
        }
    }

    /**
     * Adds a line to this container's window. If the window is null for some reason, the line is
     * silently discarded.
     *
     * @param type The message type to use
     * @param args The message's arguments
     */
    public void addLine(final StringBuffer type, final Object... args) {
        addLine(type, new Date(), args);
    }

    /**
     * Adds the specified raw line to the window, without using a formatter.
     *
     * @param line      The line to be added
     * @param timestamp Whether or not to display the timestamp for this line
     */
    public void addLine(final String line, final boolean timestamp) {
        addLine(line, timestamp ? new Date() : null);
    }

    /**
     * Adds the specified raw line to the window, without using a formatter, and using the specified
     * timestamp. If the timestamp is <code>null</code>, no timestamp is added.
     *
     * @param line      The line to be added
     * @param timestamp The timestamp to use for the line
     *
     * @since 0.6.4
     */
    public void addLine(final String line, final Date timestamp) {
        final List<String[]> lines = new LinkedList<>();
        for (final String myLine : line.split("\n")) {
            if (timestamp != null) {
                lines.add(new String[]{
                    Formatter.formatMessage(getConfigManager(), "timestamp",
                    timestamp),
                    myLine,});
            } else {
                lines.add(new String[]{myLine});
            }

            eventBus.post(new ClientLineAddedEvent(this, myLine));
        }

        getDocument().addText(lines);
    }

    /**
     * Adds a close listener for this frame container. Close listeners will only be called once,
     * even if the container is closed, re-added, and closed again.
     *
     * @param listener The listener to be added
     */
    public void addCloseListener(final FrameCloseListener listener) {
        listeners.add(FrameCloseListener.class, listener);
    }

    /**
     * Removes a close listener from this frame container.
     *
     * @since 0.6.5
     * @param listener The listener to be removed
     */
    public void removeCloseListener(final FrameCloseListener listener) {
        listeners.remove(FrameCloseListener.class, listener);
    }

    /**
     * Adds a component listener to this container.
     *
     * @since 0.6.6
     * @param listener The listener to be added
     */
    public void addComponentListener(final FrameComponentChangeListener listener) {
        listeners.add(FrameComponentChangeListener.class, listener);
    }

    /**
     * Removes a component listener from this container.
     *
     * @since 0.6.6
     * @param listener The listener to be removed
     */
    public void removeComponentListener(final FrameComponentChangeListener listener) {
        listeners.remove(FrameComponentChangeListener.class, listener);
    }

    /**
     * Adds a notification listener to this container.
     *
     * @param listener The listener to inform of notification events.
     */
    public void addNotificationListener(final NotificationListener listener) {
        listeners.add(NotificationListener.class, listener);
    }

    /**
     * Removes a notification listener from this container.
     *
     * @param listener The listener to be removed.
     */
    public void removeNotificationListener(final NotificationListener listener) {
        listeners.remove(NotificationListener.class, listener);
    }

    /**
     * Adds a frame info listener to this container.
     *
     * @param listener The listener to be informed of frame information changes.
     */
    public void addFrameInfoListener(final FrameInfoListener listener) {
        listeners.add(FrameInfoListener.class, listener);
    }

    /**
     * Removes a frame info listener from this container.
     *
     * @param listener The listener to be removed.
     */
    public void removeFrameInfoListener(final FrameInfoListener listener) {
        listeners.remove(FrameInfoListener.class, listener);
    }

    /**
     * Sends a line of text to this container's source.
     *
     * @param line The line to be sent
     */
    public void sendLine(final String line) {
        throw new UnsupportedOperationException("Container doesn't override sendLine");
    }

    /**
     * Retrieves the command parser to be used for this container.
     *
     * @return This container's command parser
     */
    public CommandParser getCommandParser() {
        checkState(writable);
        return commandParser.get();
    }

    /**
     * Retrieves the tab completer which should be used for this cotnainer.
     *
     * @return This container's tab completer
     */
    public TabCompleter getTabCompleter() {
        checkState(writable);
        return tabCompleter.get();
    }

    /**
     * Returns the maximum length that a line passed to sendLine() should be, in order to prevent it
     * being truncated or causing protocol violations.
     *
     * @return The maximum line length for this container
     */
    public int getMaxLineLength() {
        throw new UnsupportedOperationException("Container doesn't override getMaxLineLength");
    }

    /**
     * Splits the specified line into chunks that contain a number of bytes less than or equal to
     * the value returned by {@link #getMaxLineLength()}.
     *
     * @param line The line to be split
     *
     * @return An ordered list of chunks of the desired length
     */
    protected List<String> splitLine(final String line) {
        final List<String> result = new ArrayList<>();

        if (line.indexOf('\n') > -1) {
            for (String part : line.split("\n")) {
                result.addAll(splitLine(part));
            }
        } else {
            final StringBuilder remaining = new StringBuilder(line);

            while (getMaxLineLength() > -1 && remaining.toString().getBytes().length
                    > getMaxLineLength()) {
                int number = Math.min(remaining.length(), getMaxLineLength());

                while (remaining.substring(0, number).getBytes().length > getMaxLineLength()) {
                    number--;
                }

                result.add(remaining.substring(0, number));
                remaining.delete(0, number);
            }

            result.add(remaining.toString());
        }

        return result;
    }

    /**
     * Returns the number of lines that the specified string would be sent as.
     *
     * @param line The string to be split and sent
     *
     * @return The number of lines required to send the specified string
     */
    public final int getNumLines(final String line) {
        final String[] splitLines = line.split("(\n|\r\n|\r)", Integer.MAX_VALUE);
        int lines = 0;

        for (String splitLine : splitLines) {
            if (getMaxLineLength() <= 0) {
                lines++;
            } else {
                lines += (int) Math.ceil(splitLine.getBytes().length
                        / (double) getMaxLineLength());
            }
        }

        return lines;
    }

    /**
     * Processes and displays a notification.
     *
     * @param messageType The name of the formatter to be used for the message
     * @param args        The arguments for the message
     *
     * @return True if any further behaviour should be executed, false otherwise
     */
    public boolean doNotification(final String messageType, final Object... args) {
        return doNotification(new Date(), messageType, args);
    }

    /**
     * Processes and displays a notification.
     *
     * @param date        The date/time at which the event occured
     * @param messageType The name of the formatter to be used for the message
     * @param args        The arguments for the message
     *
     * @return True if any further behaviour should be executed, false otherwise
     */
    public boolean doNotification(final Date date, final String messageType, final Object... args) {
        final List<Object> messageArgs = new ArrayList<>();
        final List<Object> actionArgs = new ArrayList<>();
        final StringBuffer buffer = new StringBuffer(messageType);

        actionArgs.add(this);

        for (Object arg : args) {
            actionArgs.add(arg);

            if (!processNotificationArg(arg, messageArgs)) {
                messageArgs.add(arg);
            }
        }

        modifyNotificationArgs(actionArgs, messageArgs);

        handleNotification(date, buffer.toString(), messageArgs.toArray());

        return true;
    }

    /**
     * Allows subclasses to modify the lists of arguments for notifications.
     *
     * @param actionArgs  The list of arguments to be passed to the actions system
     * @param messageArgs The list of arguments to be passed to the formatter
     */
    protected void modifyNotificationArgs(final List<Object> actionArgs,
            final List<Object> messageArgs) {
        // Do nothing
    }

    /**
     * Allows subclasses to process specific types of notification arguments.
     *
     * @param arg  The argument to be processed
     * @param args The list of arguments that any data should be appended to
     *
     * @return True if the arg has been processed, false otherwise
     */
    protected boolean processNotificationArg(final Object arg, final List<Object> args) {
        return false;
    }

    /**
     * Handles general server notifications (i.e., ones not tied to a specific window). The user can
     * select where the notifications should go in their config.
     *
     * @param messageType The type of message that is being sent
     * @param args        The arguments for the message
     */
    public void handleNotification(final String messageType, final Object... args) {
        handleNotification(new Date(), messageType, args);
    }

    /**
     * Handles general server notifications (i.e., ones not tied to a specific window). The user can
     * select where the notifications should go in their config.
     *
     * @param date        The date/time at which the event occured
     * @param messageType The type of message that is being sent
     * @param args        The arguments for the message
     */
    public void handleNotification(final Date date, final String messageType, final Object... args) {
        checkState(writable);
        messageSinkManager.get().despatchMessage(this, date, messageType, args);
    }

    /**
     * Sets the composition state for the local user for this chat.
     *
     * @param state The new composition state
     */
    public void setCompositionState(final CompositionState state) {
        // Default implementation does nothing. Subclasses that support
        // composition should override this.
    }

    /**
     * Updates the icon of this frame if its config setting is changed.
     */
    private class IconChanger implements ConfigChangeListener {

        @Override
        public void configChanged(final String domain, final String key) {
            iconUpdated();
        }

    }

}
