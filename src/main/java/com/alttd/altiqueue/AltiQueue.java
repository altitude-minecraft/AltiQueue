package com.alttd.altiqueue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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
    }

    public void saveDefaultConfig()
    {
        try
        {
            File file = new File(getDataFolder(), "config.yml");

            // check if the file exists
            if (!file.exists())
            {
                // if it doesn't, save it
                InputStream in = getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static Configuration getConfig()
    {
        return instance.config;
    }

    public static AltiQueue getInstance()
    {
        return instance;
    }
}
