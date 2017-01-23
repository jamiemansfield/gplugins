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

package uk.jamierocks.canary.gplugins.hook;

import net.canarymod.Canary;
import net.canarymod.hook.Dispatcher;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.plugin.Priority;

/**
 * Represents an individual hook listener, with associated data.
 */
public class HookReference {

    private Plugin plugin;
    private Class<?> hookClass;
    private Dispatcher dispatcher;
    private Priority priority;

    public HookReference(Plugin plugin, Class<?> hookClass, Dispatcher dispatcher, Priority priority) {
        this.plugin = plugin;
        this.hookClass = hookClass;
        this.dispatcher = dispatcher;
        this.priority = priority;
    }

    public void register(PluginListener listener) {
        Canary.hooks().registerHook(listener, this.plugin, this.hookClass, this.dispatcher, this.priority);
    }

}
