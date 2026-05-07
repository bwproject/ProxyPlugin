package ru.projectbw.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import ru.projectbw.proxy.common.ProxyCore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

            // Создаем папку плагина
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configFile = dataDirectory.resolve("config.yml");

            // Копируем дефолтный config.yml
            if (!Files.exists(configFile)) {

                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {

                    if (in != null) {
                        Files.copy(in, configFile);
                    } else {
                        logger.error("config.yml not found inside jar!");
                        return;
                    }
                }
            }

            // Загружаем config.yml
            try (InputStream in = Files.newInputStream(configFile)) {

                Yaml yaml = new Yaml();

                Map<String, Object> config =
                        yaml.load(in);

                Map<String, Object> proxy =
                        (Map<String, Object>) config.get("proxy");

                String host =
                        (String) proxy.get("host");

                int port =
                        ((Number) proxy.get("port")).intValue();

                String username =
                        (String) proxy.get("username");

                String password =
                        (String) proxy.get("password");

                List<String> domains =
                        (List<String>) config.get("domains");

                boolean debug =
                        (Boolean) config.get("debug");

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
