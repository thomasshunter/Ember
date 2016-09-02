package org.ihie.des.ember.resource.util;

import java.util.Map.Entry;

public class PairEntry<K, V> implements Entry<K, V>
{
    private final K key;
    private V value;

    public PairEntry(final K key, final V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public final K getKey()
    {
        return this.key;
    }

    @Override
    public final V getValue()
    {
        return this.value;
    }

    @Override
    public final V setValue(final V value)
    {
        final V ret = this.value;
        this.value = value;
        return ret;
    }

    @Override
    public int hashCode()
    {
        return Util.hashCode(this.key) ^ Util.hashCode(this.value);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (!(o instanceof Entry))
        {
            return false;
        }
        final Entry<?, ?> e = (Entry<?, ?>) o;
        return Util.equals(this.key, e.getKey()) && Util.equals(this.value, e.getValue());
    }
}
