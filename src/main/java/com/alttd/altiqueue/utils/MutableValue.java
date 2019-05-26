package com.alttd.altiqueue.utils;

import java.lang.reflect.ParameterizedType;

/**
 * Represents a mutable data type for a type that may not normally be mutable, either because it is final, primitive, or sealed.
 *
 * @param <E> the type of this mutable value.
 */
public class MutableValue<E>
{
    private E value;

    /**
     * Constructs a new MutableValue with the given object.
     *
     * @param e the value to be stored.
     */
    public MutableValue(E e)
    {
        this.value = e;
    }

    /**
     * Returns the value that is currently stored. If there is no value, returns null.
     *
     * @return the value that is currently stored.
     */
    public E getValue()
    {
        return value;
    }

    /**
     * Sets the value that is currently stored.
     *
     * @param e the new value to be stored.
     */
    public void setValue(E e)
    {
        this.value = e;
    }

    public Class<? extends E> getType()
    {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
