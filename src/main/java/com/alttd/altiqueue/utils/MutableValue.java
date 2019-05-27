package com.alttd.altiqueue.utils;

/**
 * Represents a mutable data type for a type that may not normally be mutable, either because it is final, primitive, or sealed.
 *
 * @param <T> the type of this mutable value.
 */
public class MutableValue<T>
{
    private T value;

    /**
     * Constructs a new MutableValue with the given object.
     *
     * @param t the value to be stored.
     */
    public MutableValue(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Value can't be null.");
        }

        this.value = t;
    }

    /**
     * Returns the value that is currently stored. If there is no value, returns null.
     *
     * @return the value that is currently stored.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Sets the value that is currently stored.
     *
     * @param t the new value to be stored.
     */
    public void setValue(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Value can't be null.");
        }
        this.value = t;
    }

    public Class<T> getType()
    {
        if (value == null)
        {
            throw new IllegalStateException("Value can't be null.");
        }

        return (Class<T>) value.getClass();
    }
}
