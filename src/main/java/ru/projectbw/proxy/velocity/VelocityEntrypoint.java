package ru.projectbw.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;
import ru.projectbw.proxy.common.ProxyCore;

import java.util.List;

@Plugin(
        id = "proxyplugin",
        name = "ProxyPlugin",
        version = "1.0"
)
public class VelocityEntrypoint {

    private final Logger logger;

    @Inject
    public VelocityEntrypoint(Logger logger) {
        this.logger = logger;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {

        ProxyCore.init(
                "127.0.0.1",
                1080,
                null,
                null,
                List.of("maven", "github", "papermc"),
                true,
                msg -> logger.info(msg)
        );
    }
}