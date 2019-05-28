package com.alttd.altiqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.reflections.Reflections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class RegisteredCommandsTest
{
    @Test
    public void test_commands_registered()
    {
        AltiQueue altiQueue = mock(AltiQueue.class);

        // create a mocked proxy server and PluginManager to ignore registerListener
        ProxyServer proxyServer = mock(ProxyServer.class);

        // create a list of commands
        List<Command> commands = new ArrayList<>();

        // hijack the register method
        PluginManager pluginManager = mock(PluginManager.class);
        doAnswer((invocationOnMock) ->
                 {
                     commands.add(invocationOnMock.getArgument(1));
                     return null;
                 }).when(pluginManager).registerCommand(eq(altiQueue), any());

        // when we get the plugin manager, return our mock
        when(proxyServer.getPluginManager()).thenReturn(pluginManager);

        // when we get the proxy, return our mock
        when(altiQueue.getProxy()).thenReturn(proxyServer);

        // when we try to register commands, run the real command
        doCallRealMethod().when(altiQueue).registerCommands();

        // register the commands
        altiQueue.registerCommands();

        // get all the commands that are part of the project
        Set<Class<? extends Command>> commandClasses = new Reflections("com.alttd.altiqueue").getSubTypesOf(Command.class);

        // check if the registered commands contains all written commands
        boolean contains;
        for (Class<? extends Command> clazz : commandClasses)
        {
            contains = false;
            for (Command command : commands)
            {
                if (command.getClass().isAssignableFrom(clazz))
                {
                    contains = true;
                }
            }
            if (!contains)
            {
                Assert.fail("Did not register command: " + clazz.getName());
            }
        }
    }
}
