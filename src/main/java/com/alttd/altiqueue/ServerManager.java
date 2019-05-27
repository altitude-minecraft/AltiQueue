package com.alttd.altiqueue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alttd.altiqueue.configuration.Config;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class ServerManager
{
    private static List<ServerWrapper> servers;

    private static Configuration config;

    private static boolean initialized;

    private ServerManager()
    {
        throw new UnsupportedOperationException();
    }

    public static void initialize()
    {
        initialized = true;

        servers = new ArrayList<>();

        saveDefaultServerConfig();

        // go through the servers and add them to the list
        for (ServerInfo serverInfo : ProxyServer.getInstance().getServersCopy().values())
        {
            servers.add(new ServerWrapper(serverInfo, config));
        }

        // periodically connect players to their desired server
        ProxyServer.getInstance().getScheduler().schedule(AltiQueue.getInstance(), () ->
        {
            // go through the servers that are not lobbies...
            for (ServerWrapper serverWrapper : servers.stream().filter(wrapper -> !wrapper.isLobby()).collect(Collectors.toList()))
            {
                // check if they are not full...
                if (!serverWrapper.isFull())
                {
                    // go through the queued players that we have room for...
                    for (UUID playerUuid : serverWrapper.getQueuedPlayers(serverWrapper.getRoom()))
                    {
                        // and send them to that server!
                        ProxyServer.getInstance().getPlayer(playerUuid).connect(serverWrapper.getServerInfo());
                        // TODO let them know they were sent
                        // TODO remove them from the queue
                    }
                }
            }
        }, Config.QUEUE_FREQUENCY.getValue(), Config.QUEUE_FREQUENCY.getValue(), TimeUnit.SECONDS);
    }

    /**
     * Save the default servers.yml file from the plugin jar. This will not overwrite the existing file in the plugin
     * folder so it is safe to be called at any point.
     */
    public static void saveDefaultServerConfig()
    {
        try
        {
            File file = new File(AltiQueue.getInstance().getDataFolder(), "servers.yml");

            // check if the file exists
            if (!file.exists())
            {
                // if it doesn't, save it
                InputStream in = AltiQueue.getInstance().getResourceAsStream("servers.yml");
                Files.copy(in, file.toPath());
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(AltiQueue.getInstance().getDataFolder(), "servers.yml"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Looks up the {@link ServerWrapper} by name. If no server exists, this method will return {@code null}.
     *
     * @param serverName the server name to look for.
     *
     * @return the server if one exists.
     */
    public static ServerWrapper getServer(String serverName)
    {
        for (ServerWrapper server : servers)
        {
            if (server.getServerInfo().getName().equalsIgnoreCase(serverName))
            {
                return server;
            }
        }
        return null;
    }

    /**
     * Looks up the {@link ServerWrapper} by {@link ServerInfo}. If no server exists, this method will return
     * {@code null}. This method has the same behavior as {@link ServerManager#getServer(String)}.
     *
     * @param serverInfo the server info to look for.
     *
     * @return the server if one exists.
     */
    public static ServerWrapper getServer(ServerInfo serverInfo)
    {
        return getServer(serverInfo.getName());
    }

    /**
     * Looks up the {@link ServerWrapper} by {@link Server}. If the {@link Server} parameter is null or no server
     * exists, this method will return {@code null}.
     *
     * @param server the server to look for.
     *
     * @return the server if one exists.
     */
    public static ServerWrapper getServer(Server server)
    {
        if (server == null)
        {
            return null;
        }
        return getServer(server.getInfo());
    }

    public static ServerInfo getLobby()
    {
        List<ServerWrapper> lobbies = new ArrayList<>();

        for (ServerWrapper serverWrapper : servers)
        {
            if (serverWrapper.isLobby())
            {
                lobbies.add(serverWrapper);
            }
        }

        if (lobbies.size() <= 0)
        {
            throw new IllegalStateException("No registered lobbies.");
        }

        int targetIndex = 0;

        if (Config.LOBBY_STRATEGY.getValue().equalsIgnoreCase("LOWEST"))
        {
            int lowestCount = Integer.MAX_VALUE;
            int count;
            for (int i = 0; i < lobbies.size(); i++)
            {
                count = lobbies.get(i).getServerInfo().getPlayers().size();
                if (count < lowestCount)
                {
                    lowestCount = count;
                    targetIndex = i;
                }
            }
        }
        else
        {
            targetIndex = new Random().nextInt(lobbies.size());
        }

        return lobbies.get(targetIndex).getServerInfo();
    }

    /**
     * Returns the server the given player is queued in. If the player is not queued for a server, this method returns
     * -1.
     *
     * @param uuid the uuid of the player to look for.
     *
     * @return the server the given player is queued in.
     */
    public static ServerWrapper getQueuedServer(UUID uuid)
    {
        for (ServerWrapper serverWrapper : servers)
        {
            if (serverWrapper.getPosition(uuid) != -1)
            {
                return serverWrapper;
            }
        }
        return null;
    }

    public static boolean isInitialized()
    {
        return initialized;
    }

}
