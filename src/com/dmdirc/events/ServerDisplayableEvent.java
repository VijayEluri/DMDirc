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

package com.dmdirc.events;

import com.dmdirc.interfaces.Connection;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Base type for events that occur on a connection.
 */
public abstract class ServerDisplayableEvent extends ServerEvent implements DisplayableEvent {

    /** The display format to use for this event. */
    private final AtomicReference<String> displayFormatRef = new AtomicReference<>("");

    public ServerDisplayableEvent(final long timestamp, final Connection connection) {
        super(timestamp, connection);
    }

    public ServerDisplayableEvent(final Connection connection) {
        super(connection);
    }

    @Override
    public String getDisplayFormat() {
        return displayFormatRef.get();
    }

    @Override
    public void setDisplayFormat(String format) {
        this.displayFormatRef.set(format);
    }
}