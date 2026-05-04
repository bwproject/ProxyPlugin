package ru.projectbw.proxy;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ProxyPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        saveDefaultConfig();

        String host = getConfig().getString("proxy.host");
        int port = getConfig().getInt("proxy.port");
        String user = getConfig().getString("proxy.username");
        String pass = getConfig().getString("proxy.password");
        List<String> domains = getConfig().getStringList("domains");
        boolean debug = getConfig().getBoolean("debug");

        getLogger().info("SOCKS5 proxy enabled: " + host + ":" + port);

        // Авторизация SOCKS5
        if (user != null && !user.isEmpty()) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass.toCharArray());
                }
            });
        }

        ProxySelector.setDefault(new ProxySelector() {

            private final Proxy proxy = new Proxy(
                    Proxy.Type.SOCKS,
                    new InetSocketAddress(host, port)
            );

            @Override
            public List<Proxy> select(URI uri) {
                if (uri == null || uri.getHost() == null) {
                    return List.of(Proxy.NO_PROXY);
                }

                String requestHost = uri.getHost();

                for (String domain : domains) {
                    if (requestHost.contains(domain)) {
                        if (debug) {
                            getLogger().info("[PROXY] " + requestHost);
                        }
                        return List.of(proxy);
                    }
                }

                if (debug) {
                    getLogger().info("[DIRECT] " + requestHost);
                }

                return List.of(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                getLogger().warning("Proxy failed: " + uri + " -> " + ioe.getMessage());
            }
        });
    }
}
