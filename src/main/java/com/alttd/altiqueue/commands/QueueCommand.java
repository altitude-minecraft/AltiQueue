package com.alttd.altiqueue.commands;

import com.alttd.altiqueue.Permission;
import com.alttd.altiqueue.ServerManager;
import com.alttd.altiqueue.ServerWrapper;
import com.alttd.altiqueue.configuration.Lang;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command
{
    public QueueCommand()
    {
        super("queue", Permission.QUEUE_COMMAND.getPermission(), "q");
    }

    @Override
    public void execute(CommandSender sender, String[] strings)
    {
        if (!(sender instanceof ProxiedPlayer))
        {
            Lang.ONLY_PLAYERS.sendInfo(sender);
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        ServerWrapper serverWrapper = ServerManager.getQueuedServer(player.getUniqueId());
        if (serverWrapper == null)
        {
            Lang.NOT_QUEUED.sendInfo(sender);
            return;
        }

        if (strings.length != 0 && strings[0].equalsIgnoreCase("leave"))
        {
            serverWrapper.removeFromQueue(player.getUniqueId());
            Lang.LEFT_QUEUE.sendInfo(sender,
                                     "{server}", serverWrapper.getServerInfo().getName());
        }
        else
        {
            Lang.CHECK_STATUS.sendInfo(sender,
                                       "{server}", serverWrapper.getServerInfo().getName(),
                                       "{position}", serverWrapper.getPosition(player.getUniqueId()));
        }


    }
}
