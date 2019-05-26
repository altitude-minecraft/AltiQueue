package com.alttd.altiqueue.listeners;

import com.alttd.altiqueue.ServerManager;
import com.alttd.altiqueue.ServerWrapper;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener implements Listener
{
    @EventHandler
    public void onPreLogin(ServerConnectEvent event)
    {
        ServerWrapper currentServer = ServerManager.getServer(event.getPlayer().getServer());
        ServerWrapper wrapper = ServerManager.getServer(event.getTarget());

        // if it's not a lobby, or it is a lobby but it's full...
        if (!wrapper.isLobby() || wrapper.isFull())
        {
            // check if there is no need for a queue
            if (!wrapper.addQueue(event.getPlayer()).isConnected())
            {
                // if they don't have a server or they aren't in a lobby
                if (currentServer == null || !currentServer.isLobby())
                {
                    event.setTarget(ServerManager.getLobby());
                }
            }
        }
    }

}
