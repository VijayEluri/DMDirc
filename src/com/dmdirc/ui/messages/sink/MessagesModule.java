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

package com.dmdirc.ui.messages.sink;

import com.dmdirc.DMDircMBassador;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module for dependency injecting the {@link MessageSinkManager} and its default sinks.
 */
@Module(library = true, complete = false)
public class MessagesModule {

    @Provides
    @Singleton
    public MessageSinkManager getMessageSinkManager(final DMDircMBassador eventBus,
            final Set<MessageSink> defaultSinks) {
        final MessageSinkManager messageSinkManager = new MessageSinkManager(eventBus);
        defaultSinks.forEach(messageSinkManager::addSink);
        return messageSinkManager;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final AllMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final ChannelMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final CommonChannelsMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final CustomWindowMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final ForkMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final FormatMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final GroupMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final LastCommandMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final NullMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final SelfMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final ServerMessageSink sink) {
        return sink;
    }

    @Provides(type = Provides.Type.SET)
    public MessageSink getMessageSink(final StatusBarMessageSink sink) {
        return sink;
    }

}