package com.alttd.altiqueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    private LinkedList<UUID> queue;

    private LinkedList<UUID> priorityQueue;

    public ServerWrapper(ServerInfo serverInfo, Configuration configuration)
    {
        this.serverInfo = serverInfo;

        this.maxPlayers = configuration.getInt("servers." + serverInfo.getName() + ".max-players");
        this.hasPriorityQueue = configuration.getBoolean("servers." + serverInfo.getName() + ".priority-queue");
        this.lobby = configuration.getBoolean("servers." + serverInfo.getName() + ".lobby");

        this.queue = new LinkedList<>();

        if (this.hasPriorityQueue)
        {
            this.priorityQueue = new LinkedList<>();
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
        if (hasPriorityQueue() && player.hasPermission(Permission.PRIORITY_QUEUE.getPermission()))
        {
            priorityQueue.add(player.getUniqueId());
            return QueueResponse.ADDED_PRIORITY;
        }

        queue.add(player.getUniqueId());
        return QueueResponse.ADDED_STANDARD;
    }

    /**
     * Returns the position that the given player is in the server's queue.
     *
     * @param uuid the uuid of the player.
     *
     * @return the position that the given player is in the server's queue.
     */
    public int getPosition(UUID uuid)
    {
        if (priorityQueue.contains(uuid))
        {
            return priorityQueue.indexOf(uuid) + 1;
        }
        else if (queue.contains(uuid))
        {
            return priorityQueue.size() + queue.indexOf(uuid) + 1;
        }
        else
        {
            return -1;
        }
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

    /**
     * Returns the maximum number of players the server supports.
     *
     * @return the maximum number of players the server supports.
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    /**
     * Returns whether or not this server has a priority queue.
     *
     * @return whether or not this server has a priority queue.
     */
    public boolean hasPriorityQueue()
    {
        return hasPriorityQueue;
    }

    /**
     * Returns the {@link ServerInfo} for this {@link ServerWrapper}.
     *
     * @return the {@link ServerInfo} for this {@link ServerWrapper}.
     */
    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }

    /**
     * Returns whether or not this server is a lobby.
     *
     * @return whether or not this server is a lobby.
     */
    public boolean isLobby()
    {
        return lobby;
    }
}
