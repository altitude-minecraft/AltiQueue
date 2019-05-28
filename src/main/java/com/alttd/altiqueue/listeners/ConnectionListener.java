package com.alttd.altiqueue.listeners;

import com.alttd.altiqueue.QueueResponse;
import com.alttd.altiqueue.ServerManager;
import com.alttd.altiqueue.ServerWrapper;
import com.alttd.altiqueue.configuration.Lang;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener implements Listener
{
    @EventHandler
    public void onConnect(ServerConnectEvent event)
    {
        ServerWrapper currentServer = ServerManager.getServer(event.getPlayer().getServer());
        ServerWrapper wrapper = ServerManager.getServer(event.getTarget());

        if (currentServer == null && wrapper.hasQueue())
        {
            event.setTarget(ServerManager.getLobby());
            Lang.DIRECT_CONNECT_FULL.sendInfo(event.getPlayer(),
                                              "{server}", wrapper.getServerInfo().getName(),
                                              "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
            return;
        }

        // if it's not a lobby, or it is a lobby but it's full...
        if (!wrapper.isLobby())
        {
            // if they try to connect to the server they're already on, we don't care
            if (currentServer == wrapper)
            {
                return;
            }
            ServerWrapper previousQueue = ServerManager.getQueuedServer(event.getPlayer().getUniqueId());

            QueueResponse response = wrapper.addQueue(event.getPlayer());
            if (response == QueueResponse.NOT_FULL || response == QueueResponse.SKIP_QUEUE)
            {
                return;
            }

            // check if they're already in queue
            if (response == QueueResponse.ALREADY_ADDED)
            {
                Lang.ALREADY_QUEUED.sendInfo(event.getPlayer(),
                                             "{server}", wrapper.getServerInfo().getName(),
                                             "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
                event.setCancelled(true);
                return;
            }

            // if they're a new connection, send them to the lobby no matter what
            if (currentServer == null)
            {
                event.setTarget(ServerManager.getLobby());
                Lang.DIRECT_CONNECT_FULL.sendInfo(event.getPlayer(),
                                                  "{server}", wrapper.getServerInfo().getName(),
                                                  "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
                return;
            }

            // cancel the join event
            event.setCancelled(true);

            // if they had a queue before, let them know
            if (previousQueue != null)
            {
                Lang.LEFT_QUEUE.sendInfo(event.getPlayer(),
                                         "{server}", wrapper.getServerInfo().getName());
            }

            // tell them they were added to the queue
            Lang.JOINED_QUEUE.sendInfo(event.getPlayer(),
                                       "{server}", wrapper.getServerInfo().getName(),
                                       "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event)
    {
        ServerWrapper wrapper = ServerManager.getQueuedServer(event.getPlayer().getUniqueId());

        if (wrapper != null)
        {
            wrapper.removeFromQueue(event.getPlayer().getUniqueId());
        }
    }
}
