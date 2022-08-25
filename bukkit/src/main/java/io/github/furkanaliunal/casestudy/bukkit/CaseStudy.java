package io.github.furkanaliunal.casestudy.bukkit;

import io.github.furkanaliunal.casestudy.bukkit.Listeners.TPAcceptListener;
import io.github.furkanaliunal.casestudy.bukkit.Utils.MongoUtils;
import io.github.furkanaliunal.casestudy.bukkit.Utils.RedisUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author OnwexryS
 * Main class of the bukkit plugin
 */
public final class CaseStudy extends JavaPlugin {
    /**
     * Instance of the plugin to use in other classes and plugins
     */
    @Getter
    private static CaseStudy instance;
    /**
     * Listener class to teleport players and clean redis data
     */
    @Getter
    private TPAcceptListener tpAcceptListener;

    /**
     * Initializer of the plugin
     * @see JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        CaseStudy.instance = this;
        saveDefaultConfig();
        reloadConfig();
        boolean success = RedisUtils.initializeFromConfig(getSLF4JLogger(), getConfig());
        if (!success) getPluginLoader().disablePlugin(this);
        success = MongoUtils.initializeFromConfig(getConfig());
        if (!success) getPluginLoader().disablePlugin(this);
        tpAcceptListener = new TPAcceptListener();
    }

    /**
     * Cleanup of the plugin on disable
     * @see JavaPlugin#onDisable()
     * @see JavaPlugin#setEnabled(boolean)
     */
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        RedisUtils.close();
        MongoUtils.close();
    }
}
