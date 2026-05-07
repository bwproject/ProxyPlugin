package ru.projectbw.proxy.paper;

import org.bukkit.plugin.java.JavaPlugin;
import ru.projectbw.proxy.common.ProxyCore;

public class PaperEntrypoint extends JavaPlugin {

    @Override
    public void onLoad() {

        saveDefaultConfig();

        ProxyCore.init(
                getConfig().getString("proxy.host"),
                getConfig().getInt("proxy.port"),
                getConfig().getString("proxy.username"),
                getConfig().getString("proxy.password"),
                getConfig().getStringList("domains"),
                getConfig().getBoolean("debug"),
                msg -> getLogger().info(msg)
        );
    }
}