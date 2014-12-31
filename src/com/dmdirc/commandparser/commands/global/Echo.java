/*
 * Copyright (c) 2006-2015 DMDirc Developers
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

package com.dmdirc.commandparser.commands.global;

import com.dmdirc.CustomWindow;
import com.dmdirc.FrameContainer;
import com.dmdirc.commandparser.BaseCommandInfo;
import com.dmdirc.commandparser.CommandArguments;
import com.dmdirc.commandparser.CommandInfo;
import com.dmdirc.commandparser.CommandType;
import com.dmdirc.commandparser.commands.Command;
import com.dmdirc.commandparser.commands.IntelligentCommand;
import com.dmdirc.commandparser.commands.context.CommandContext;
import com.dmdirc.commandparser.commands.flags.CommandFlag;
import com.dmdirc.commandparser.commands.flags.CommandFlagHandler;
import com.dmdirc.commandparser.commands.flags.CommandFlagResult;
import com.dmdirc.interfaces.CommandController;
import com.dmdirc.interfaces.Connection;
import com.dmdirc.ui.WindowManager;
import com.dmdirc.ui.input.AdditionalTabTargets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * The echo commands simply echos text to the current window.
 */
public class Echo extends Command implements IntelligentCommand {

    /** A command info object for this command. */
    public static final CommandInfo INFO = new BaseCommandInfo("echo",
            "echo [--ts <timestamp>] [--target <window>] <line> "
            + "- echos the specified line to the window",
            CommandType.TYPE_GLOBAL);
    /** The flag used to specify a timestamp for the echo command. */
    private final CommandFlag timeStampFlag = new CommandFlag("ts", true, 1, 0);
    /** The flag used to specify a target for the echo command. */
    private final CommandFlag targetFlag = new CommandFlag("target", true, 1, 0);
    /** The command flag handler for this command. */
    private final CommandFlagHandler handler;
    /** Window management. */
    private final WindowManager windowManager;

    /**
     * Creates a new instance of Echo.
     *
     * @param controller    Command controller
     * @param windowManager Window management
     */
    @Inject
    public Echo(final CommandController controller, final WindowManager windowManager) {
        super(controller);

        this.windowManager = windowManager;
        handler = new CommandFlagHandler(timeStampFlag, targetFlag);
    }

    @Override
    public void execute(@Nonnull final FrameContainer origin,
            final CommandArguments args, final CommandContext context) {
        @Nullable final CommandFlagResult results = handler.process(origin, args);

        if (results == null) {
            return;
        }

        Date time = new Date();
        if (results.hasFlag(timeStampFlag)) {
            try {
                time = new Date(Long.parseLong(results.getArgumentsAsString(timeStampFlag)));
            } catch (NumberFormatException ex) {
                sendLine(origin, args.isSilent(), FORMAT_ERROR, "Unable to process timestamp");
                return;
            }
        }

        if (results.hasFlag(targetFlag)) {
            FrameContainer frame = null;
            Optional<FrameContainer> target = Optional.ofNullable(origin);

            while (frame == null && target.isPresent()) {
                frame = windowManager.findCustomWindow(target.get(),
                        results.getArgumentsAsString(targetFlag));
                target = target.get().getParent();
            }

            if (frame == null) {
                frame = windowManager.findCustomWindow(results.getArgumentsAsString(targetFlag));
            }

            if (frame == null) {
                sendLine(origin, args.isSilent(), FORMAT_ERROR,
                        "Unable to find target window");
            } else if (!args.isSilent()) {
                frame.addLine(FORMAT_OUTPUT, time, results.getArgumentsAsString());
            }
        } else if (origin != null && !args.isSilent()) {
            origin.addLine(FORMAT_OUTPUT, time, results.getArgumentsAsString());
        }
    }

    @Override
    public AdditionalTabTargets getSuggestions(final int arg,
            final IntelligentCommandContext context) {
        final AdditionalTabTargets targets = new AdditionalTabTargets();

        if (arg == 0) {
            targets.add("--target");
            targets.add("--ts");
        } else if ((arg == 1 && context.getPreviousArgs().get(0).equals("--target"))
                || (arg == 3 && context.getPreviousArgs().get(2).equals("--target")
                && context.getPreviousArgs().get(0).equals("--ts"))) {

            final Collection<FrameContainer> windowList = new ArrayList<>();
            final Optional<Connection> connection = context.getWindow().getConnection();

            //Active window's Children
            windowList.addAll(context.getWindow().getChildren());

            //Children of Current Window's server
            connection
                    .map(Connection::getWindowModel)
                    .map(FrameContainer::getChildren)
                    .ifPresent(windowList::addAll);

            //Global Windows
            windowList.addAll(windowManager.getRootWindows());
            targets.addAll(
                    windowList.stream().filter(customWindow -> customWindow instanceof CustomWindow)
                            .map(FrameContainer::getName).collect(Collectors.toList()));

            targets.excludeAll();
        } else if (arg == 1 && context.getPreviousArgs().get(0).equals("--ts")) {
            targets.add(String.valueOf(new Date().getTime()));
            targets.excludeAll();
        }

        return targets;
    }

}
