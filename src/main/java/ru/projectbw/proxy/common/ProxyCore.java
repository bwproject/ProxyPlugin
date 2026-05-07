package ru.projectbw.proxy.common;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.function.Consumer;

public class ProxyCore {

    public static void init(
            String host,
            int port,
            String user,
            String pass,
            List<String> domains,
            boolean debug,
            Consumer<String> logger
    ) {

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
                            logger.accept("[PROXY] " + requestHost);
                        }

                        return List.of(proxy);
                    }
                }

                if (debug) {
                    logger.accept("[DIRECT] " + requestHost);
                }

                return List.of(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                logger.accept("Proxy failed: " + uri);
            }
        });
    }
}