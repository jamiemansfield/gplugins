/*
 * This file is part of gplugins, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017, Jamie Mansfield <https://www.jamierocks.uk/>
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.jamierocks.canary.gplugins.command;

import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.CanaryCommand;
import net.canarymod.commandsys.CommandOwner;
import net.canarymod.commandsys.DynamicCommandAnnotation;

/**
 * A wrapper around {@link CanaryCommand} to create them from {@link CommandCallable}s.
 */
public final class CanaryCommandWrapper extends CanaryCommand {

    private final CommandCallable callable;

    public CanaryCommandWrapper(CommandOwner owner, CommandCallable callable, String... aliases) {
        super(
                new DynamicCommandAnnotation(
                    aliases,                   // aliases
                    callable.getPermissions(), // permissions
                    callable.getDescription(), // descriptions
                    callable.getToolTip(),     // toolTip
                    "",                 // parent
                    "",             // helpLookup
                    new String[]{ " " },       // searchTerms
                    0,                    // min
                    0,                   // max
                    "",      // tabCompleteMethod
                    2                  // version
                ),             // meta
                owner,         // owner
                null  // translator
        );
        this.callable = callable;
    }

    @Override
    protected void execute(MessageReceiver caller, String[] parameters) {
        this.callable.process(caller, parameters);
    }

}
