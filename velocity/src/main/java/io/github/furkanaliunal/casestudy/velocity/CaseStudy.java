package io.github.furkanaliunal.casestudy.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.furkanaliunal.casestudy.velocity.Commands.TPACommand;
import io.github.furkanaliunal.casestudy.velocity.Commands.TPADenyCommand;
import io.github.furkanaliunal.casestudy.velocity.Commands.TPAcceptCommand;
import io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportManager;

import io.github.furkanaliunal.casestudy.velocity.Utils.Configuration;
import io.github.furkanaliunal.casestudy.velocity.Utils.Messages;
import io.github.furkanaliunal.casestudy.velocity.Utils.MongoUtils;
import io.github.furkanaliunal.casestudy.velocity.Utils.RedisUtils;
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

/**
 * Created by Onwexrys
 * Main class of the velocity plugin
 */
public class CaseStudy {

    /**
     * Instance of the ProxyServer.
     */
    public static ProxyServer server;
    /**
     * Instance of the plugin to use in other classes and plugins.
     */
    public static CaseStudy instance;

    /**
     * Instance of the logger
     */
    @Getter
    private final Logger logger;
    /**
     * Instance of the plugin data directory.
     */
    @Getter
    private final Path dataDirectory;

    /**
     * Default configuration object of the plugin
     * @see Configuration for more information.
     */
    private final Configuration config;
    /**
     * Object to manage the teleport requests.
     * @see TeleportManager for more information.
     */
    @Getter
    private TeleportManager teleportManager;

    /**
     * Constructor for the main class of the plugin.
     * @param server Instance of the ProxyServer.
     * @param logger Instance of the logger.
     * @param dataDirectory Instance of the plugin data directory.
     */
    @Inject
    public CaseStudy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        CaseStudy.server = server;
        CaseStudy.instance = this;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.config = new Configuration(dataDirectory, "config.yml");
    }

    /**
     * Initializer of the plugin.
     * Used to initialize the plugin and its components.
     * @param event Event of the plugin initialization.
     */
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

    /**
     * Cleanup of the plugin on disable.
     * Used to clean up the plugin and its components.
     * @param event Event of the plugin shutdown.
     */
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