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

import com.google.common.collect.Lists;
import net.canarymod.Canary;
import net.canarymod.hook.Dispatcher;
import net.canarymod.hook.Hook;
import net.canarymod.hook.HookExecutionException;
import net.canarymod.hook.HookHandler;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * Allows for the searching of hooks in gplugins, and the registration thereof.
 */
public final class HookProcessor {

    public static List<HookReference> scan(Plugin plugin, Object pluginObject) {
        final List<HookReference> references = Lists.newArrayList();

        Arrays.stream(pluginObject.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(HookHandler.class)).forEach(m -> {
            final HookHandler handler = m.getDeclaredAnnotation(HookHandler.class);

            // We first need to check the parameter count
            if (m.getParameterCount() != 1) {
                final String hookReference = String.format("%s#%s", m.getDeclaringClass().getSimpleName(), m.getName());
                Canary.log.error(String.format("Failed to process HookHandler: %s. Expected 1 parameters, found %d!", hookReference, m.getParameterCount()));
                return;
            }

            final Class<?> hookClass = m.getParameterTypes()[0];

            // Check the hookClass is assignable from Hook
            if (!Hook.class.isAssignableFrom(hookClass)) {
                final String hookReference = String.format("%s#%s", m.getDeclaringClass().getSimpleName(), m.getName());
                Canary.log.error(String.format("Failed to process HookHandler: %s. Parameter is not assignable from Hook!", hookReference));
                return;
            }

            // Create Dispatcher
            final Dispatcher dispatcher = new GDispatcher(pluginObject) {
                @Override
                public void execute(Object pluginObject, Hook hook) {
                    try {
                        m.invoke(pluginObject, hook);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new HookExecutionException(e.getMessage(), e);
                    }
                }
            };
            dispatcher.ignoreCanceled = handler.ignoreCanceled();
        });

        return references;
    }

    public static void registerListener(Plugin plugin, Object pluginObject) {
        final PluginListener listener = new FakeListener(plugin);
        scan(plugin, pluginObject).forEach(h -> h.register(listener));
    }

    private HookProcessor() {
    }

}
