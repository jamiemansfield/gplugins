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

package uk.jamierocks.canary.gplugins.mcstats;

import com.google.common.base.Throwables;
import net.canarymod.Canary;
import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;
import net.minecrell.mcstats.StatsLite;
import net.visualillusionsent.utils.TaskManager;

import java.nio.file.Paths;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CanaryStatsLite extends StatsLite {

    private final Plugin plugin;
    private ScheduledFuture<?> task;

    public CanaryStatsLite(Plugin plugin) {
        super(Paths.get(Canary.getWorkingPath(), "config", plugin.getName()));
        this.plugin = plugin;
    }

    @Override
    protected void register(int interval, TimeUnit unit) {
        this.task = TaskManager.scheduleDelayedTask(this, interval, unit);
    }

    @Override
    protected void log(String message) {
        this.plugin.getLogman().info(message);
    }

    @Override
    protected void handleException(String message, Exception e) {
        this.plugin.getLogman().warn(message, e);
    }

    @Override
    protected void handleSubmitException(Exception e) {
        this.plugin.getLogman().debug("Failed to submit plugin statistics: {}", Throwables.getRootCause(e).toString());
    }

    @Override
    protected void cancel() {
        this.task.cancel(true);
        this.task = null;
    }

    @Override
    protected String getPluginName() {
        return this.plugin.getName();
    }

    @Override
    protected String getPluginVersion() {
        return this.plugin.getVersion();
    }

    @Override
    protected String getServerVersion() {
        return Canary.getServer().getServerVersion();
    }

    @Override
    protected int getOnlinePlayerCount() {
        return Canary.getServer().getNumPlayersOnline();
    }

    @Override
    protected boolean isOnlineMode() {
        return Configuration.getServerConfig().isOnlineMode();
    }

}
