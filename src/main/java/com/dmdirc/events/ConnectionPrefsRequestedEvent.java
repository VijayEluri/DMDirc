/*
 * Copyright (c) 2006-2017 DMDirc Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dmdirc.events;

import com.dmdirc.config.prefs.PreferencesCategory;
import com.dmdirc.config.provider.AggregateConfigProvider;
import com.dmdirc.config.provider.ConfigProvider;

/**
 * Raised when the connection preferences are requested.
 */
public class ConnectionPrefsRequestedEvent extends PreferencesEvent {

    private final PreferencesCategory category;
    private final AggregateConfigProvider config;
    private final ConfigProvider identity;

    public ConnectionPrefsRequestedEvent(final PreferencesCategory category,
            final AggregateConfigProvider config, final ConfigProvider identity) {
        this.category = category;
        this.config = config;
        this.identity = identity;
    }

    public PreferencesCategory getCategory() {
        return category;
    }

    public ConfigProvider getIdentity() {
        return identity;
    }

    public AggregateConfigProvider getConfig() {
        return config;
    }

}
