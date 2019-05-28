package com.alttd.altiqueue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.alttd.altiqueue.commands.QueueCommand;
import com.alttd.altiqueue.configuration.Config;
import com.alttd.altiqueue.configuration.Lang;
import com.alttd.altiqueue.listeners.ConnectionListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class AltiQueue extends Plugin
{
    private static AltiQueue instance;

    private Configuration config;

    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();

        getProxy().getPluginManager().registerListener(this, new ConnectionListener());

        ServerManager.initialize();

        // check lang
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists())
        {
            saveResource("lang.yml");
        }
        Lang.update();

        // check config
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
        {
            saveResource("config.yml");
        }
        Config.update();

        registerCommands();
    }

    public void registerCommands()
    {
        getProxy().getPluginManager().registerCommand(this, new QueueCommand());
    }

    public void saveDefaultConfig()
    {
        try
        {
            saveResource("config.yml");

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void saveResource(String name)
    {
        File folder = instance.getDataFolder();
        if (!folder.exists())
        {
            folder.mkdir();
        }
        File file = new File(folder, name);
        if (!file.exists())
        {
            try (InputStream in = instance.getResourceAsStream(name))
            {
                Files.copy(in, file.toPath());
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static AltiQueue getInstance()
    {
        return instance;
    }
}
