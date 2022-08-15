package furkan.studio.casestudy;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
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


    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    @Inject
    public CaseStudy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        CaseStudy.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("CaseStudy started successfully");
    }


    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event){
    }
}