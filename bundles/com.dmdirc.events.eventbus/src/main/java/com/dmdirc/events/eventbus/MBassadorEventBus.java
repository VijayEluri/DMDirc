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

package com.dmdirc.events.eventbus;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete event bus backed by {@link MBassador}.
 */
public class MBassadorEventBus implements EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(MBassadorEventBus.class);

    private final MBassador<BaseEvent> bus;

    public MBassadorEventBus() {
        bus = new MBassador<>(new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default(1, 1))
                .addFeature(Feature.AsynchronousMessageDispatch.Default().setNumberOfMessageDispatchers(1))
                .addPublicationErrorHandler(e -> LOG.error("Unhandled exception while publishing event", e)));
    }

    @Override
    public void subscribe(Object listener) {
        bus.subscribe(listener);
    }

    @Override
    public void unsubscribe(Object listener) {
        bus.unsubscribe(listener);
    }

    @Override
    public void publish(BaseEvent message) {
        bus.publish(message);
    }

    @Override
    public void publishAsync(BaseEvent message) {
        bus.publishAsync(message);
    }

}
