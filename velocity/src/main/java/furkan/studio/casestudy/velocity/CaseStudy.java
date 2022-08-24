package furkan.studio.casestudy.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import furkan.studio.casestudy.velocity.Commands.TPACommand;
import furkan.studio.casestudy.velocity.Commands.TPADenyCommand;
import furkan.studio.casestudy.velocity.Commands.TPAcceptCommand;
import furkan.studio.casestudy.velocity.Teleports.TeleportManager;
import furkan.studio.casestudy.velocity.Utils.*;

import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "casestudy",
    name = "CaseStudy",
    version = "1.0",
    url = "https://github.com/furkanaliunal",
    authors = {"OnwexryS"}
)
public class CaseStudy {


    public static ProxyServer server;
    public static CaseStudy instance;


    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    private final Configuration config;
    @Getter
    private TeleportManager teleportManager;

    @Inject
    public CaseStudy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        CaseStudy.server = server;
        CaseStudy.instance = this;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.config = new Configuration(dataDirectory, "config.yml");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config.reload();
        TeleportRules.initializeFromConfig(config);
        RedisUtils.initializeFromConfig(logger, config);
        MongoUtils.initializeFromConfig(config);
        Messages.reload();
        this.teleportManager = new TeleportManager();
        server.getCommandManager().register("tpa", new TPACommand());
        server.getCommandManager().register("tpaccept", new TPAcceptCommand());
        server.getCommandManager().register("tpadeny", new TPADenyCommand());
        logger.info("CaseStudy started successfully");
    }


    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event){
        teleportManager.getAcceptedTeleportsProcessScheduler().cancel();
        teleportManager.getPendingTeleportsTimeOutScheduler().cancel();
        teleportManager.getCountdownPerTeleportRequestsScheduler().cancel();
        RedisUtils.close();
        MongoUtils.close();
        logger.info("CaseStudy stopped");
    }
}