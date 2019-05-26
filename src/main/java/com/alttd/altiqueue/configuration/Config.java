package com.alttd.altiqueue.configuration;

import java.util.Objects;

import com.alttd.altiqueue.AltiQueue;
import com.alttd.altiqueue.utils.MutableValue;
import net.md_5.bungee.config.Configuration;

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
        Configuration config = AltiQueue.getConfig();

        // checks whether or not there is a need to save the config file
        MutableValue<Boolean> save = new MutableValue<>(false);

        updateValue(config, save, "version", VERSION);

        // options related to connecting players to a lobby
        updateValue(config, save, "lobby-strategy", LOBBY_STRATEGY);
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
    public static <E> E loadValue(Configuration config, Class<E> clazz, String location)
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
            return (E) Integer.valueOf(config.getInt(location));
        }
        else if (clazz == String.class)
        {
            return (E) config.getString(location);
        }
        else if (clazz == Boolean.class)
        {
            return (E) Boolean.valueOf(config.getBoolean(location));
        }
        else if (clazz == Double.class)
        {
            return (E) Double.valueOf(config.getDouble(location));
        }
        else if (Enum.class.isAssignableFrom(clazz))
        {
            return (E) Enum.valueOf((Class<? extends Enum>) clazz, Objects.requireNonNull(config.getString(location)));
        }

        // TODO throw exception since the type is weird
        return null;
    }

}
