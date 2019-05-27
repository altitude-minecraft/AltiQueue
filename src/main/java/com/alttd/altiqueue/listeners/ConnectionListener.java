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

        // if it's not a lobby, or it is a lobby but it's full...
        if (!wrapper.isLobby())
        {
            // TODO check if the current and target are the same server
            ServerWrapper previousQueue = ServerManager.getQueuedServer(event.getPlayer().getUniqueId());

            QueueResponse response = wrapper.addQueue(event.getPlayer());
            if (response == QueueResponse.NOT_FULL || response == QueueResponse.SKIP_QUEUE)
            {
                return;
            }

            // check if they are already in queue
            if (response == QueueResponse.ALREADY_ADDED)
            {
                Lang.ALREADY_QUEUED.sendInfo(event.getPlayer(),
                                             "{server}", wrapper.getServerInfo().getName(),
                                             "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
                event.setCancelled(true);
                return;
            }

            if (currentServer == null)
            {
                event.setTarget(ServerManager.getLobby());
                Lang.DIRECT_CONNECT_FULL.sendInfo(event.getPlayer(),
                                                  "{server}", wrapper.getServerInfo().getName(),
                                                  "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
                return;
            }

            event.setCancelled(true);

            if (previousQueue != null)
            {
                Lang.LEFT_QUEUE.sendInfo(event.getPlayer(),
                                         "{server}", wrapper.getServerInfo().getName());
            }

            Lang.JOINED_QUEUE.sendInfo(event.getPlayer(),
                                       "{server}", wrapper.getServerInfo().getName(),
                                       "{position}", wrapper.getPosition(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event)
    {
        // TODO remove them from queue
    }

}
