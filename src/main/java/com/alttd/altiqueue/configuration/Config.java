package com.alttd.altiqueue.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.alttd.altiqueue.AltiQueue;
import com.alttd.altiqueue.utils.MutableValue;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class Config
{
    /**
     * The plugin's version
     */
    public static final MutableValue<String> VERSION = new MutableValue<>("${project.version}");

    public static final MutableValue<String> LOBBY_STRATEGY = new MutableValue<>("LOWEST");

    public static final MutableValue<Integer> QUEUE_FREQUENCY = new MutableValue<>(5);

    public static void update()
    {
        File configFile = new File(AltiQueue.getInstance().getDataFolder(), "config.yml");
        Configuration config;
        try
        {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            // checks whether or not there is a need to save the config file
            final MutableValue<Boolean> save = new MutableValue<>(false);

            updateValue(config, save, "version", VERSION);

            updateValue(config, save, "lobby-strategy", LOBBY_STRATEGY);
            updateValue(config, save, "queue-frequency", QUEUE_FREQUENCY);

            if (save.getValue())
            {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the configuration with the given information. If the value fails to load from the config because it does
     * not exist or it is in an invalid format, the system will notify the console.
     *
     * @param config   the config file to load/update.
     * @param location the location in the config.
     * @param mutable  the mutable value to update.
     */
    private static <T> void updateValue(Configuration config, MutableValue<Boolean> save, String location, MutableValue<T> mutable)
    {
        if (!config.contains(location) || !successful(() -> mutable.setValue(loadValue(config, mutable.getType(), location))))
        {
            error(location);
            config.set(location, mutable.getValue().toString());
            if (!save.getValue())
            {
                save.setValue(true);
            }
        }
    }

    /**
     * Used to check if an operation throws an exception with ease.
     *
     * @param runnable the operation to run.
     *
     * @return {@code true} if the operation does NOT throw an exception.<br>
     * {@code false} if the operation DOES throw an exception.
     */
    protected static boolean successful(Runnable runnable)
    {
        try
        {
            runnable.run();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Alerts the console that there was an error loading a config value.
     *
     * @param location the location that caused an error.
     */
    private static void error(String location)
    {
        AltiQueue.getInstance().getLogger().severe("Error loading the config value '" + location + "'. Reverted it to default.");
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadValue(Configuration config, Class<? super T> clazz, String location)
    {
        if (config == null)
        {
            throw new IllegalArgumentException("Config parameter can't be null.");
        }
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class parameter can't be null.");
        }

        if (clazz == Integer.class)
        {
            return (T) Integer.valueOf(config.getInt(location));
        }
        else if (clazz == String.class)
        {
            return (T) config.getString(location);
        }
        else if (clazz == Boolean.class)
        {
            return (T) Boolean.valueOf(config.getBoolean(location));
        }
        else if (clazz == Double.class)
        {
            return (T) Double.valueOf(config.getDouble(location));
        }
        else if (Enum.class.isAssignableFrom(clazz))
        {
            return (T) Enum.valueOf((Class<? extends Enum>) clazz, Objects.requireNonNull(config.getString(location)));
        }

        // TODO throw exception since the type is weird
        return null;
    }

}
