/*
 * Copyright (c) 2006-2011 Chris Smith, Shane Mc Cormack, Gregory Holmes
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

package com.dmdirc.commandparser.commands.server;

import com.dmdirc.FrameContainer;
import com.dmdirc.Server;
import com.dmdirc.commandparser.CommandArguments;
import com.dmdirc.commandparser.CommandInfo;
import com.dmdirc.commandparser.CommandType;
import com.dmdirc.commandparser.commands.Command;
import com.dmdirc.commandparser.commands.CommandOptions;
import com.dmdirc.commandparser.commands.IntelligentCommand;
import com.dmdirc.commandparser.commands.context.CommandContext;
import com.dmdirc.commandparser.commands.context.ServerCommandContext;
import com.dmdirc.ui.input.AdditionalTabTargets;
import com.dmdirc.ui.input.TabCompletionType;

/**
 * Allows the user to send CTCP messages.
 * @author chris
 */
@CommandOptions(allowOffline=false)
public final class Ctcp extends Command implements IntelligentCommand,
        CommandInfo {
    
    /**
     * Creates a new instance of Ctcp.
     */
    public Ctcp() {
        super();
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute(final FrameContainer origin,
            final CommandArguments args, final CommandContext context) {
        final Server server = ((ServerCommandContext) context).getServer();
        if (args.getArguments().length < 2) {
            showUsage(origin, args.isSilent(), "ctcp", "<target> <type> [arguments]");
        } else {
            server.getParser().sendCTCP(args.getArguments()[0],
                    args.getArguments()[1], args.getArgumentsAsString(2));
            sendLine(origin, args.isSilent(), "selfCTCP", args.getArguments()[0],
                    args.getArgumentsAsString(1));
        }
    }
    
    
    /** {@inheritDoc}. */
    @Override
    public String getName() {
        return "ctcp";
    }
    
    /** {@inheritDoc}. */
    @Override
    public boolean showInHelp() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public CommandType getType() {
        return CommandType.TYPE_SERVER;
    }
    
    /** {@inheritDoc}. */
    @Override
    public String getHelp() {
        return "ctcp <target> <type> [arguments] - sends a CTCP message";
    }
    
    /** {@inheritDoc} */
    @Override
    public AdditionalTabTargets getSuggestions(final int arg,
            final IntelligentCommandContext context) {
        final AdditionalTabTargets res = new AdditionalTabTargets();
        
        if (arg == 0) {
            res.exclude(TabCompletionType.COMMAND);
        } else if (arg == 1) {
            res.add("VERSION");
            res.add("ACTION");
            res.add("USERINFO");
            res.add("CLIENTINFO");
            res.add("PING");
            res.add("FINGER");
            res.add("SOURCE");
            res.add("TIME");
            res.excludeAll();
        }
        
        return res;
    }
    
}
