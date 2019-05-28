package com.alttd.altiqueue.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.alttd.altiqueue.AltiQueue;
import com.alttd.altiqueue.utils.CollectionUtils;
import com.alttd.altiqueue.utils.MutableValue;
import com.alttd.altiqueue.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * The system for processing and sending messages to players.
 *
 * @author Michael Ziluck
 */
public enum Lang
{
    /**
     * The prefix before most of the lang messages.
     */
    PREFIX("prefix", "&3[&bAltiQueue&3] &f{message}"),
    /**
     * When a player tries to queue for a server they are already queued for.
     */
    ALREADY_QUEUED("already-queued", "You are already in queue for &b{server}&f. You are at position &c{position}&f."),
    /**
     * When a user tries to directly connect to a server and it is full.
     */
    DIRECT_CONNECT_FULL("direct-connect-full", "&b{server}&f is full. You are at position &c{position}&f in queue. Purchase a donor rank to get a prioritized queue. Type /q leave to leave the queue."),
    /**
     * When a player leaves a queue.
     */
    LEFT_QUEUE("left-queue", "You have left queue for &b{server}."),
    /**
     * When a player joins a queue.
     */
    JOINED_QUEUE("joined-queue", "You have joined the queue for &b{server}&f. You are at position &c{position}&f. Purchase a donor rank to get a prioritized queue. Type /q leave to leave the queue."),
    /**
     * When a player is sent to the server they're queued for.
     */
    CONNECT("connect", "You have been connected to &b{server}&f."),
    /**
     * When a player tries to queue to the server they're current connected to.
     */
    ALREADY_CONNECTED("already-connected", "You are already connected to &b{server}&f."),
    /**
     * When a player's position in queue changes.
     */
    POSITION_UPDATE("position-update", "You are now at position &c{position}&f for &b{server}&f."),
    /**
     * When a player tries to check their queue status or leave queue but are not queue'd
     */
    NOT_QUEUED("not-queued", "&cYou are not queued for a server."),
    /**
     * When a non-player tries to run a player-only command.
     */
    ONLY_PLAYERS("only-players", "&cOnly players can run that command."),
    /**
     * When a player checks their queue status
     */
    CHECK_STATUS("check-status", "You are at position &c{position}&f for &b{server}&f. Purchase a donor rank to get a prioritized queue. Type /q leave to leave the queue.");


    private String[] message;

    private String path;

    Lang(String path, String... message)
    {
        this.path = path;
        this.message = message;
    }

    /**
     * Retrieves the message for this Lang object. This can be changed by editing the language configuration files, so
     * they should NOT be treated as constants. Additionally, their Strings should NOT be stored to reference anything.
     *
     * @return the message for this Lang object.
     */
    public String[] getRawMessage()
    {
        return message;
    }

    /**
     * Sets the message for this Lang object. This should not be done after startup to ensure data security.
     *
     * @param message the new message.
     */
    public void setRawMessage(String... message)
    {
        this.message = message;
    }

    /**
     * Retrieves the message for this Lang object. This can be changed by editing the language configuration files, so
     * they should NOT be treated as constants. Additionally, their Strings should NOT be stored to reference anything.
     * Lastly, this returns the combined version of the message in the case that there are multiple.
     *
     * @return the message for this Lang object.
     */
    public String getRawMessageCompiled()
    {
        return StringUtils.compile(message);
    }

    /**
     * @return the path of option in the lang.yml file.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sends this Lang object to the CommandSender target. The parameters replace all placeholders that exist in the
     * String as well.
     *
     * @param sender     the CommandSender receiving the message.
     * @param parameters all additional arguments to fill placeholders.
     */
    public void send(CommandSender sender, Object... parameters)
    {
        for (String message : getMessage(parameters))
        {
            sender.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    /**
     * Sends this Lang object but prepended with the PREFIX value as well.
     *
     * @param sender     the CommandSender receiving the message.
     * @param parameters all additional arguments to fill placeholders.
     */
    public void sendInfo(CommandSender sender, Object... parameters)
    {
        for (String line : getMessage(parameters))
        {
            PREFIX.send(sender, "{message}", line);
        }
    }

    /**
     * Renders this message and returns it. Similar behavior to {@link #send(CommandSender, Object...)}, but instead of sending the message, it simply returns it.
     *
     * @param parameters all additional arguments to fill placeholders.
     *
     * @return the compiled message.
     */
    public String[] getMessage(Object... parameters)
    {
        String[] args = Arrays.copyOf(message, message.length);
        for (int i = 0; i < args.length; i++)
        {
            args[i] = ChatColor.translateAlternateColorCodes('&', renderString(args[i], parameters));
        }
        return args;
    }

    /**
     * Render a string with the proper parameters.
     *
     * @param string the rendered string.
     * @param args   the placeholders and proper content.
     *
     * @return the rendered string.
     */
    protected String renderString(String string, Object... args)
    {
        if (args.length % 2 != 0)
        {
            throw new IllegalArgumentException("Message rendering requires arguments of an even number. " + Arrays.toString(args) + " given.");
        }

        for (int i = 0; i < args.length; i += 2)
        {
            string = string.replace(args[i].toString(), CollectionUtils.firstNonNull(args[i + 1], "").toString());
        }

        return string;
    }

    public static void update()
    {
        try
        {
            File langFile = new File(AltiQueue.getInstance().getDataFolder(), "lang.yml");
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(langFile);

            final MutableValue<Boolean> save = new MutableValue<>(false);

            for (Lang lang : values())
            {
                if (!config.contains(lang.getPath()) || !Config.successful(() -> lang.setRawMessage(config.getString(lang.getPath()))))
                {
                    config.set(lang.getPath(), lang.getRawMessage());
                    error(lang.getPath());
                    if (!save.getValue())
                    {
                        save.setValue(true);
                    }
                }
            }

            if (save.getValue())
            {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, langFile);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Alerts the console that there was an error loading a config value.
     *
     * @param location the location that caused an error.
     */
    private static void error(String location)
    {
        AltiQueue.getInstance().getLogger().severe("Error loading the lang value '" + location + "'. Reverted it to default.");
    }
}
