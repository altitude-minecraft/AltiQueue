package com.alttd.altiqueue;

import java.io.File;
import java.io.FileInputStream;

import com.alttd.altiqueue.configuration.Config;
import com.alttd.altiqueue.configuration.Lang;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AltiQueue.class, ServerManager.class, Config.class, Lang.class })
public class AltiQueueTest
{
    private AltiQueue altiQueue;

    @Before
    public void test_on_enable() throws Exception
    {
        altiQueue = mock(AltiQueue.class);

        // set up the data folder
        File dataFolder = new File("test-output/");
        when(altiQueue.getDataFolder()).thenReturn(dataFolder);

        // set up the config.yml stream from the build folder
        File configFile = new File("src/main/resources/config.yml");
        when(altiQueue.getResourceAsStream("config.yml")).thenReturn(new FileInputStream(configFile));
        when(altiQueue.getResourceAsStream("lang.yml")).thenReturn(new FileInputStream(configFile));

        // use the original methods for the mocked instance
        doCallRealMethod().when(altiQueue).onEnable();
        doCallRealMethod().when(altiQueue).saveDefaultConfig();

        // create a mocked proxy server and PluginManager to ignore registerListener
        ProxyServer proxyServer = mock(ProxyServer.class);
        PluginManager pluginManager = mock(PluginManager.class);
        when(proxyServer.getPluginManager()).thenReturn(pluginManager);
        when(altiQueue.getProxy()).thenReturn(proxyServer);

        // stop external methods from running, that's for another test to handle
        mockStatic(ServerManager.class);
        doNothing().when(ServerManager.class, "initialize");
        mockStatic(Config.class);
        doNothing().when(Config.class, "update");
        mockStatic(Lang.class);
        doNothing().when(Lang.class, "update");

        // enable the plugin. this is a test in and of itself, but it HAS to run before.
        altiQueue.onEnable();
    }

    @Test
    public void test_files_created()
    {
        Assert.assertTrue(new File(altiQueue.getDataFolder(), "config.yml").exists());
        Assert.assertTrue(new File(altiQueue.getDataFolder(), "lang.yml").exists());
    }

    @Test
    public void test_get_instance()
    {
        Assert.assertNotNull(AltiQueue.getInstance());
    }

    @After
    public void cleanup()
    {
        File configFile = new File(altiQueue.getDataFolder(), "config.yml");
        if (configFile.exists())
        {
            configFile.delete();
        }

        File langFile = new File(altiQueue.getDataFolder(), "lang.yml");
        if (langFile.exists())
        {
            configFile.delete();
        }
    }


}
