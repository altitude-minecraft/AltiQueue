package com.alttd.altiqueue;

import java.io.File;
import java.io.FileInputStream;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AltiQueue.class, ServerManager.class })
public class AltiQueueTest
{
    private AltiQueue altiQueue;

    @Before
    public void test_on_enable() throws Exception
    {
        altiQueue = mock(AltiQueue.class);

        // set up the data folder
        File dataFolder = new File("test-output/");
        dataFolder.mkdir();
        when(altiQueue.getDataFolder()).thenReturn(dataFolder);

        // set up the config.yml stream from the build folder
        File configFile = new File("src/main/resources/config.yml");
        when(altiQueue.getResourceAsStream("config.yml")).thenReturn(new FileInputStream(configFile));

        // use the original methods for the mocked instance
        doCallRealMethod().when(altiQueue).onEnable();
        doCallRealMethod().when(altiQueue).saveDefaultConfig();

        // create a mocked proxy server and PluginManager to ignore registerListener
        ProxyServer proxyServer = mock(ProxyServer.class);
        PluginManager pluginManager = mock(PluginManager.class);
        when(proxyServer.getPluginManager()).thenReturn(pluginManager);
        when(altiQueue.getProxy()).thenReturn(proxyServer);

        // allows getConfig() to be tested
        mockStatic(AltiQueue.class);
        when(AltiQueue.getConfig()).thenCallRealMethod();
        when(AltiQueue.getInstance()).thenCallRealMethod();

        // stop initialize from running in ServerManager, that's for another test to handle
        mockStatic(ServerManager.class);
        doNothing().when(ServerManager.class, "initialize");

        // enable the plugin. this is a test in and of itself, but it HAS to run before.
        altiQueue.onEnable();
    }

    @Test
    public void test_config_created()
    {
        Assert.assertTrue(new File(altiQueue.getDataFolder(), "config.yml").exists());
    }

    @Test
    public void test_get_instance()
    {
        Assert.assertNotNull(AltiQueue.getInstance());
    }

    @Test
    public void test_get_config()
    {
        Assert.assertNotNull(AltiQueue.getConfig());
    }

    @After
    public void cleanup()
    {
        File configFile = new File(altiQueue.getDataFolder(), "config.yml");

        if (configFile.exists())
        {
            configFile.delete();
        }
    }


}
