package com.alttd.altiqueue.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.alttd.altiqueue.AltiQueue;
import com.alttd.altiqueue.utils.CollectionUtils;
import com.alttd.altiqueue.utils.MutableValue;
import com.alttd.altiqueue.utils.StringUtils;
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
    PREFIX("prefix.text", "§3[§bFortuneBlocks§3] §f{message}"),
    /**
     * When a player misuses a command.
     */
    USAGE("usage_message", "§6§lUSAGE §e» §f{usage}"),
    /**
     * The prefix to an error message.
     */
    ERROR("error_message", "§4§lERROR §c» §7{message}"),
    /**
     * The prefix to a success message.
     */
    SUCCESS("success_message", "§2§lSUCCESS §a» §f{message}"),
    /**
     * When players do not have permission to do something.
     */
    NO_PERMS("no_permissions", "You don't have permission to do that."),
    /**
     * When a player does not have access to any sub-commands.
     */
    NO_SUBS("no_sub_access", "You don't have access to any sub-commands."),
    /**
     * When a player mines a block and does not have room in their inventory.
     */
    INVENTORY_FULL("inventory_full", "Your inventory is full."),
    /**
     * When the configuration files were successfully reloaded.
     */
    RELOAD("reload", "Reloaded the config and lang files."),
    /**
     * The header and footer for all commands.
     */
    HEADER_FOOTER("header_footer:", "&7&m-----------------------------------"),
    /**
     * When the console tries to run a player-only command.
     */
    ONLY_PLAYERS("only_players", "Only players can run that command."),
    /**
     * When a new material is tracked.
     */
    MATERIALS_ADD("materials.add", "The material §b{material}§f is now affected by fortune."),
    /**
     * When a material is untracked.
     */
    MATERIALS_REMOVE("materials.remove", "The material §b{material}§f no longer is affected by fortune."),
    /**
     * When a user tries to track an already tracked material.
     */
    MATERIALS_TRACKED("materials.tracked", "That material is already tracked."),
    /**
     * When a user tries to untrack a material that is not tracked.
     */
    MATERIALS_NOT_TRACKED("materials.not_tracked", "That material is not tracked."),
    /**
     * When an invalid item is entered by the user.
     */
    MATERIALS_INVALID_ITEM("materials.invalid_item", "Item not found."),
    /**
     * When the user tries to add a material that is not a block.
     */
    MATERIALS_NOT_BLOCK("materials.not_block", "That is not a block."),
    /**
     * When the user lists off the tracked materials.
     */
    MATERIALS_LIST("materials.list", "  §6- §e{material}");

    private String[] message;

    private String path;

    Lang(String path, String... message)
    {
        this.path = path;
        this.message = message;
    }

    /**
     * Retrieves the message for this Lang object. This can be changed by editing the language configuration files, so
     * they should NOT be treated as constants. Additionally their Strings should NOT be stored to reference anything.
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
        String message = StringUtils.compile(getMessage(parameters));
        sender.sendMessage(new TextComponent(message));
    }

    /**
     * Sends this Lang object but prepended with the ERROR value as well.
     *
     * @param sender     the CommandSender receiving the message.
     * @param parameters all additional arguments to fill placeholders.
     */
    public void sendError(CommandSender sender, Object... parameters)
    {
        for (String line : getMessage(parameters))
        {
            ERROR.send(sender, "{message}", line);
        }
    }

    /**
     * Sends this Lang object but prepended with the SUCCESS value as well.
     *
     * @param sender     the CommandSender receiving the message.
     * @param parameters all additional arguments to fill placeholders.
     */
    public void sendSuccess(CommandSender sender, Object... parameters)
    {
        for (String line : getMessage(parameters))
        {
            SUCCESS.send(sender, "{message}", line);
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
            args[i] = renderString(args[i], parameters);
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

    public static void sendUsageMessage(CommandSender sender, String[] label, String[] parameters)
    {
        StringBuilder args = new StringBuilder("/" + StringUtils.compile(label));
        for (String str : parameters)
        {
            args.append(" [").append(str).append("]");
        }
        USAGE.send(sender, "{usage}", args.toString());
    }
}
