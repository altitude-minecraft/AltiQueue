package com.alttd.altiqueue.configuration;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LangTest
{
    private Configuration config;

    @Before
    public void setup() throws IOException
    {
        File file = new File("src/main/resources/lang.yml");

        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    @Test
    public void test_file_contains_options()
    {
        for (Lang lang : Lang.values())
        {
            if (!config.contains(lang.getPath()))
            {
                Assert.fail("Value missing from lang.yml: " + lang.name());
            }
        }
    }

    @Test
    public void test_defaults_match()
    {
        for (Lang lang : Lang.values())
        {
            if (!config.getString(lang.getPath()).equals(lang.getRawMessageCompiled()))
            {
                Assert.fail("Lang values don't match: " + lang.name());
            }
        }
    }
}
