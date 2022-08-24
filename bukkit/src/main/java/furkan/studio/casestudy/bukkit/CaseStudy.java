package furkan.studio.casestudy.bukkit;

import furkan.studio.casestudy.bukkit.Listeners.TPAcceptListener;
import furkan.studio.casestudy.bukkit.Utils.MongoUtils;
import furkan.studio.casestudy.bukkit.Utils.RedisUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CaseStudy extends JavaPlugin {
    @Getter
    private static CaseStudy instance;
    @Getter
    private TPAcceptListener tpAcceptListener;

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

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        RedisUtils.close();
        MongoUtils.close();
    }
}
