package com.alttd.altiqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ServerWrapper
{
    private ServerInfo serverInfo;

    private int maxPlayers;

    private boolean hasPriorityQueue;

    private boolean lobby;

    private Queue<UUID> queue;

    private Queue<UUID> priorityQueue;

    public ServerWrapper(ServerInfo serverInfo, Configuration configuration)
    {
        this.serverInfo = serverInfo;

        this.maxPlayers = configuration.getInt("servers." + serverInfo.getName() + ".max-players");
        this.hasPriorityQueue = configuration.getBoolean("servers." + serverInfo.getName() + ".priority-queue");
        this.lobby = configuration.getBoolean("servers." + serverInfo.getName() + ".lobby");

        this.queue = new PriorityQueue<>();

        if (this.hasPriorityQueue)
        {
            this.priorityQueue = new PriorityQueue<>();
        }
    }

    /**
     * Adds the given player to the server queue. If the target server is not full this method will return {@code true} indicating that they can be safely connected to the target server.
     *
     * @param player the player trying to connect.
     *
     * @return {@code true} if there is room on the target server, otherwise, {@code false}.
     */
    public QueueResponse addQueue(ProxiedPlayer player)
    {
        // the server isn't full or they can skip the queue completely, send em through
        if (serverInfo.getPlayers().size() < maxPlayers)
        {
            return QueueResponse.NOT_FULL;
        }

        if (player.hasPermission(Permission.SKIP_QUEUE.getPermission()))
        {
            return QueueResponse.SKIP_QUEUE;
        }

        if (priorityQueue.contains(player.getUniqueId()) || queue.contains(player.getUniqueId()))
        {
            return QueueResponse.ALREADY_ADDED;
        }

        // add them to the appropriate queue
        if (player.hasPermission(Permission.PRIORITY_QUEUE.getPermission()))
        {
            priorityQueue.add(player.getUniqueId());
            return QueueResponse.ADDED_PRIORITY;
        }

        queue.add(player.getUniqueId());
        return QueueResponse.ADDED_STANDARD;
    }

    /**
     * Returns {@code true} if there are less players connected than the limit.
     *
     * @return {@code true} if there are less players connected than the limit.
     */
    public boolean isFull()
    {
        return serverInfo.getPlayers().size() >= maxPlayers;
    }

    /**
     * Returns how many more players can connect before the server becomes full.
     *
     * @return how many more players can connect before the server becomes full.
     */
    public int getRoom()
    {
        return maxPlayers - serverInfo.getPlayers().size();
    }

    /**
     * Returns the players who are at the beginning of the queue.
     *
     * @param amount the amount of players to return.
     *
     * @return the players who are at the beginning of the queue.
     */
    public List<UUID> getQueuedPlayers(int amount)
    {
        List<UUID> players = new ArrayList<>();
        while (amount > 0)
        {
            if (priorityQueue.size() > 0)
            {
                players.add(priorityQueue.remove());
            }
            else if (queue.size() > 0)
            {
                players.add(queue.remove());
            }
            amount--;
        }
        return players;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public boolean hasPriorityQueue()
    {
        return hasPriorityQueue;
    }

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }

    public boolean isLobby()
    {
        return lobby;
    }
}
