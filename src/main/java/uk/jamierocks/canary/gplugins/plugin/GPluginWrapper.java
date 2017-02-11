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

import net.canarymod.plugin.Plugin;
import uk.jamierocks.canary.gplugins.mcstats.CanaryStatsLite;
import uk.jamierocks.canary.gplugins.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class GPluginWrapper extends Plugin {

    private final Object pluginObject;
    private final CanaryStatsLite stats;

    public GPluginWrapper(Object pluginObject) {
        this.pluginObject = pluginObject;
        this.stats = new CanaryStatsLite(this);
    }

    @Override
    public boolean enable() {
        if (this.pluginObject.getClass().isAnnotationPresent(uk.jamierocks.canary.gplugins.Plugin.MetricsEnabled.class)) {
            this.stats.start();
        }

        ReflectionUtil.getMethodsAnnotatedWith(this.pluginObject.getClass(), uk.jamierocks.canary.gplugins.Plugin.Enable.class).forEach(m -> {
            try {
                m.invoke(this.pluginObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                this.getLogman().error("Failed to invoke enable method: " + m.getName(), e);
            }
        });
        return true;
    }

    @Override
    public void disable() {
        ReflectionUtil.getMethodsAnnotatedWith(this.pluginObject.getClass(), uk.jamierocks.canary.gplugins.Plugin.Disable.class).forEach(m -> {
            try {
                m.invoke(this.pluginObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                this.getLogman().error("Failed to invoke disable method: " + m.getName(), e);
            }
        });
    }

}
