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

package uk.jamierocks.canary.gplugins.plugin;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.visualillusionsent.utils.LocaleHelper;
import uk.jamierocks.canary.gplugins.command.CanaryCommandWrapper;
import uk.jamierocks.canary.gplugins.command.CommandCallable;

/**
 * A helper class provided to gplugins, providing similar convenience methods as existed in {@link Plugin}.
 */
public final class PluginHelper {

    private final String name;

    public PluginHelper(String name) {
        this.name = name;
    }

    public void registerCommands(CommandListener listener, boolean force) throws CommandDependencyException {
        this.getPlugin().registerCommands(listener, force);
    }

    public void registerCommands(CommandListener listener, LocaleHelper translator, boolean force) throws CommandDependencyException {
        this.getPlugin().registerCommands(listener, translator, force);
    }

    public void registerCommand(CommandCallable callable, String... aliases) {
        try {
            Canary.commands().registerCommand(new CanaryCommandWrapper(this.getPlugin(), callable, aliases), this.getPlugin(), false);
        } catch (CommandDependencyException ignored) {
        }
    }

    public void registerListener(PluginListener listener) {
        this.getPlugin().registerListener(listener);
    }

    public Plugin getPlugin() {
        return Canary.pluginManager().getPlugin(this.name);
    }

}
