/*
 * Copyright (c) 2006-2010 Chris Smith, Shane Mc Cormack, Gregory Holmes
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

package com.dmdirc.commandparser.commands.context;

import com.dmdirc.Channel;
import com.dmdirc.commandparser.CommandInfo;
import com.dmdirc.ui.interfaces.Window;

/**
 * A specialised {@link CommandContext} for commands executed in channels.
 *
 * @since 0.6.4
 * @author chris
 */
public class ChannelCommandContext extends ChatCommandContext {

    /** The channel associated with the command. */
    private final Channel channel;

    /**
     * Creates a new channel command context.
     *
     * @param source The source of the command
     * @param commandInfo The command info object which associated the command with the input
     * @param channel The channel associated with the command
     */
    public ChannelCommandContext(Window source, CommandInfo commandInfo, Channel channel) {
        super(source, commandInfo, channel);
        this.channel = channel;
    }

    /**
     * Retrieves the channel associated with this context.
     *
     * @return This context's channel
     */
    public Channel getChannel() {
        return channel;
    }

}