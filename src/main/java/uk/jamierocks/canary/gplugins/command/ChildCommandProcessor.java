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

import com.google.common.collect.Maps;
import net.canarymod.chat.MessageReceiver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A {@link CommandProcessor} for children commands.
 */
public final class ChildCommandProcessor implements CommandProcessor {

    private final CommandProcessor fallbackProcessor;
    private final Map<String, CommandCallable> childCommands;

    public ChildCommandProcessor(CommandProcessor fallbackProcessor, Map<List<String>, CommandCallable> childCommandMap) {
        this.fallbackProcessor = fallbackProcessor;
        this.childCommands = Maps.newHashMap();
        childCommandMap.entrySet().forEach(e -> e.getKey().forEach(alias -> this.childCommands.put(alias, e.getValue())));
    }

    @Override
    public void process(MessageReceiver caller, String[] parameters) {
        if (this.childCommands.containsKey(parameters[0])) {
            this.childCommands.get(parameters[0]).process(caller, Arrays.copyOfRange(parameters, 1, parameters.length));
        } else {
            this.fallbackProcessor.process(caller, parameters);
        }
    }

}
