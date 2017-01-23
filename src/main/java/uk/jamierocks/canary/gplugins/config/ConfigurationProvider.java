/*
 * This file is part of NeptuneLib, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016-2017, Jamie Mansfield <https://www.jamierocks.uk/>
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

package uk.jamierocks.canary.gplugins.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.canarymod.config.WorldConfiguration;
import net.visualillusionsent.utils.PropertiesFile;

import java.io.File;

/**
 * A reimplemented configuration provider.
 */
public final class ConfigurationProvider {

    private static LoadingCache<String, ConfigurationProvider> configProviderCache = Caffeine.newBuilder()
            .build(ConfigurationProvider::new);

    public static ConfigurationProvider getConfigurationProvider(String pluginName) {
        return configProviderCache.get(pluginName);
    }

    public static void clearAllCaches() {
        configProviderCache.asMap().keySet().stream().map(ConfigurationProvider::getConfigurationProvider).forEach(ConfigurationProvider::clearCache);
    }

    private static LoadingCache<String, WorldConfiguration> worldConfigCache = Caffeine.newBuilder()
            .build(key -> {
                final String[] split = key.split("_");
                return new WorldConfiguration("config" + File.separatorChar + "worlds" + File.separatorChar + split[0], key);
            });

    public static WorldConfiguration getWorldConfig(String worldName) {
        return worldConfigCache.get(worldName);
    }

    public static void reloadWorldConfigs() {
        worldConfigCache.asMap().values().forEach(WorldConfiguration::reload);
    }

    public static boolean isWorldConfigCached(String worldName) {
        return worldConfigCache.asMap().containsKey(worldName);
    }

    private LoadingCache<String, PropertiesFile> pluginConfigCache = Caffeine.newBuilder()
            .build(key -> {
                final PropertiesFile propertiesFile = new PropertiesFile(key);
                propertiesFile.save();
                return propertiesFile;
            });

    private final String pluginName;

    private ConfigurationProvider(String pluginName) {
        this.pluginName = pluginName;
    }

    public PropertiesFile getPluginConfig() {
        return this.pluginConfigCache.get("config" + File.separatorChar + this.pluginName + File.separatorChar + this.pluginName + ".cfg");
    }

    public PropertiesFile getPluginModuleConfig(String moduleName) {
        return this.pluginConfigCache.get("config" + File.separatorChar + this.pluginName + File.separatorChar + this.pluginName + "." + moduleName + ".cfg");
    }

    public PropertiesFile getPluginWorldConfig(String worldName) {
        return this.pluginConfigCache.get("config" + File.separatorChar + this.pluginName + File.separatorChar + "worlds" + File.separatorChar + worldName + File.separatorChar + this.pluginName + ".cfg");
    }

    public PropertiesFile getPluginModuleWorldConfig(String moduleName, String worldName) {
        return this.pluginConfigCache.get("config" + File.separatorChar + this.pluginName + File.separatorChar + "worlds" + File.separatorChar + worldName + File.separatorChar + this.pluginName + "." + moduleName + ".cfg");
    }

    public void clearCache() {
        this.pluginConfigCache.invalidateAll();
    }

}
