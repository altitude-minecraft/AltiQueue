package com.alttd.altiqueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AltiQueue.class, ProxyServer.class })
public class ServerManagerTest
{
    private AltiQueue altiQueue;

    @Before
    public void setup() throws FileNotFoundException
    {
        // create ServerInfo mock
        ServerInfo serverInfo = mock(ServerInfo.class);
        when(serverInfo.getName()).thenReturn("server-one");

        // create server map
        Map<String, ServerInfo> serverInfoMap = new HashMap<>();
        serverInfoMap.put("server-one", serverInfo);

        // create scheduler mock. we dont need to do anything with it since we dont want things to run at all
        TaskScheduler taskScheduler = mock(TaskScheduler.class);

        // mock the proxy server return our mocked server map and scheduler
        ProxyServer proxyServer = mock(ProxyServer.class);
        when(proxyServer.getServersCopy()).thenReturn(serverInfoMap);
        when(proxyServer.getScheduler()).thenReturn(taskScheduler);

        // return our mocked ProxyServer
        mockStatic(ProxyServer.class);
        when(ProxyServer.getInstance()).thenReturn(proxyServer);

        // mock the altiqueue static methods
        mockStatic(AltiQueue.class);

        // mock altiqueue
        altiQueue = mock(AltiQueue.class);

        // set output folder
        File dataFolder = new File("test-output/");
        dataFolder.mkdir();
        when(altiQueue.getDataFolder()).thenReturn(dataFolder);

        // set the server demo file
        File configFile = new File("src/main/resources/servers.yml");
        when(altiQueue.getResourceAsStream("servers.yml")).thenReturn(new FileInputStream(configFile));

        // return the mocked instance
        when(AltiQueue.getInstance()).thenReturn(altiQueue);
    }

    @Test
    public void test_initialize()
    {
        ServerManager.initialize();

        List<ServerWrapper> servers = Whitebox.getInternalState(ServerManager.class, "servers");

        Assert.assertNotNull(servers);
        Assert.assertEquals(1, servers.size());
    }

    @Test
    public void test_default_server_save()
    {
        ServerManager.saveDefaultServerConfig();

        Assert.assertTrue(new File(altiQueue.getDataFolder(), "servers.yml").exists());
    }

    @Test
    public void test_get_server_by_name()
    {
        if (!ServerManager.isInitialized())
        {
            test_initialize();
        }

        Assert.assertNotNull(ServerManager.getServer("server-one"));
    }

    @After
    public void cleanup()
    {
        File configFile = new File(altiQueue.getDataFolder(), "servers.yml");
        configFile.delete();
    }

}
