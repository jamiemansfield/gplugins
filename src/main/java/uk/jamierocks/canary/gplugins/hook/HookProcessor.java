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
import uk.jamierocks.canary.gplugins.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Allows for the searching of hooks in gplugins, and the registration thereof.
 */
public final class HookProcessor {

    public static List<HookReference> scan(Plugin plugin, Object pluginObject) {
        final List<HookReference> references = Lists.newArrayList();

        ReflectionUtil.getMethodsAnnotatedWith(pluginObject.getClass(), HookHandler.class).forEach(m -> {
            final HookHandler handler = m.getDeclaredAnnotation(HookHandler.class);

            // We first need to check the parameter count
            if (m.getParameterCount() != 1) {
                Canary.log.error("Failed to process HookHandler: {}#{}. Expected 1 parameter, found {}!",
                        m.getDeclaringClass().getSimpleName(), m.getName(), m.getParameterCount());
                return;
            }

            final Class<?> hookClass = m.getParameterTypes()[0];

            // Check the hookClass is assignable from Hook
            if (!Hook.class.isAssignableFrom(hookClass)) {
                Canary.log.error("Failed to process HookHandler: {}#{}. Parameter is not assignable from Hook!",
                        m.getDeclaringClass().getSimpleName(), m.getName());
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

            // Create hook reference
            references.add(new HookReference(plugin, hookClass, dispatcher, handler.priority()));
        });

        return references;
    }

    public static void registerListener(Plugin plugin, Object pluginObject) {
        final PluginListener listener = new FakeListener(plugin);
        Canary.hooks().registerListener(listener, plugin);
        scan(plugin, pluginObject).forEach(h -> h.register(listener));
    }

    private HookProcessor() {
    }

}
