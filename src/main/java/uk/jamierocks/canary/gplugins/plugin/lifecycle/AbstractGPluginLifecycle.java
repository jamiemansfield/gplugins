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

package uk.jamierocks.canary.gplugins.plugin.lifecycle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.canarymod.exceptions.PluginLoadFailedException;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.plugin.lifecycle.PluginLifecycleBase;
import uk.jamierocks.canary.gplugins.guice.PluginGuiceModule;
import uk.jamierocks.canary.gplugins.hook.HookProcessor;
import uk.jamierocks.canary.gplugins.plugin.GPluginWrapper;

public abstract class AbstractGPluginLifecycle extends PluginLifecycleBase {

    protected ClassLoader classLoader;

    public AbstractGPluginLifecycle(final PluginDescriptor desc) {
        super(desc);
    }

    protected abstract ClassLoader getClassLoader() throws PluginLoadFailedException;

    @Override
    protected void _load() throws PluginLoadFailedException {
        this.classLoader = this.getClassLoader();

        try {
            final Class<?> pluginClass = this.classLoader.loadClass(this.desc.getCanaryInf().getString("main-class"));

            // mad haks bro
            Plugin.threadLocalName.set(this.desc.getName());
            final Injector injector = Guice.createInjector(new PluginGuiceModule(this.desc, pluginClass));
            final Object pluginInstance = injector.getInstance(pluginClass);
            final Plugin plugin = new GPluginWrapper(pluginInstance);

            // gotta be certain
            plugin.setName(this.desc.getName());
            plugin.setPriority(this.desc.getPriority());
            this.desc.setPlugin(plugin);

            // hook listener
            HookProcessor.registerListener(plugin, pluginInstance);
        }
        catch (final ClassNotFoundException ex) {
            throw new PluginLoadFailedException("Failed to load plugin", ex);
        }
    }

    @Override
    protected void _unload() {
    }

}
