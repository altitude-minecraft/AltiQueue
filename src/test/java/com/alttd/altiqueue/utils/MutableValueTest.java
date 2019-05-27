package com.alttd.altiqueue.utils;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class MutableValueTest
{
    @Test
    public void test_get_class()
    {
        MutableValue<String> value = new MutableValue<>("Hello, World!");

        assertSame(String.class, value.getType());
    }


}
