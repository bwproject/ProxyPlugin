package ru.projectbw.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;
import ru.projectbw.proxy.common.ProxyCore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

@Plugin(
        id = "proxyplugin",
        name = "ProxyPlugin",
        version = "1.0"
)
public class VelocityEntrypoint {

    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public VelocityEntrypoint(Logger logger,
                              @DataDirectory Path dataDirectory) {

        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {

        try {

            // Создаем папку
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configFile = dataDirectory.resolve("config.yml");

            // Копируем дефолтный config.yml
            if (!Files.exists(configFile)) {

                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {

                    if (in != null) {
                        Files.copy(in, configFile);
                    }
                }
            }

            // Читаем config.yml
            Yaml yaml = new Yaml();

            try (InputStream in = Files.newInputStream(configFile)) {

                var config = yaml.load(in);

                var proxy = (java.util.Map<String, Object>) config.get("proxy");

                String host = (String) proxy.get("host");
                int port = (Integer) proxy.get("port");
                String username = (String) proxy.get("username");
                String password = (String) proxy.get("password");

                List<String> domains = (List<String>) config.get("domains");

                boolean debug = (Boolean) config.get("debug");

                ProxyCore.init(
                        host,
                        port,
                        username,
                        password,
                        domains,
                        debug,
                        msg -> logger.info(msg)
                );

                logger.info("ProxyPlugin initialized");

            }

        } catch (IOException e) {

            logger.error("Failed to load config", e);
        }
    }
}
