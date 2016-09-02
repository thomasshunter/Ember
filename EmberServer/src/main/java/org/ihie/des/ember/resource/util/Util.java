package org.ihie.des.ember.resource.util;

import java.util.Iterator;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.primitive.IdDt;

public class Util
{
   
    public final static String SP_LAST_UPDATED = "_lastUpdated";

    
    /**
     * Retrieves the hash code of the given Object
     * 
     * @param o the Object
     * @return the hash code
     **/
    public final static int hashCode(final Object o) 
    {
        return o == null ? 0 : o.hashCode();
    }

    /**
     * Determines if the given Iterable contains the given Object
     * 
     * @param c
     *            the Iterable
     * @param o
     *            the Object
     * @return true if c contains o, false otherwise
     **/
    public final static boolean contains(final Iterable<?> c, final Object o)
    {
        for (final Object i : c)
        {
            if (equals(i, o))
            {
                return true;
            }
        }

        return false;
    }
    
    public static <T extends IResource> ResourceReferenceDt prepareContainedResource(final IBaseResource container, final T contained) 
    {
        if (contained == null) 
        {
            return null;
        }
        
        contained.setId((IdDt) null);
        
        // Might be able to preserve original id if we explicitly indicate that it should be a contained resource
        //container.getContained().getContainedResources().add(contained);
        
        return new ResourceReferenceDt(contained);
    }


    /**
     * Determines if a String and a char are equal
     * 
     * @param s
     *            the String
     * @param c
     *            the char
     * @return whether or not s equals c
     **/
    public final static boolean equals(final String s, final char c)
    {
        return (length(s) == 1) && (s.charAt(0) == c);
    }

    /**
     * Determines if two objects are equal (true if both are null)
     * 
     * @param o1
     *            Object 1
     * @param o2
     *            Object 2
     * @return whether or not o1 equals o2
     **/
    public final static boolean equals(final Object o1, final Object o2)
    {
        return o1 == null ? o2 == null : o2 == null ? false : o1.equals(o2) || o2.equals(o1);
        // .equals is not always symmetric;
        // for example, if a java.sql.Timestamp t contains the same date as a
        // java.util.Date d,
        // t.equals(d) will be true but d.equals(t) will be false
    }

    /**
     * Determines if two arrays contain the same elements in the same order
     * 
     * @param a1
     *            the first Object[] array
     * @param a2
     *            the second Object[] array
     * @return whether or not a1 contains the same elements as a2
     **/
    public final static boolean equals(final Object[] a1, final Object[] a2)
    {
        if (a1 == a2)
        {
            return true;
        }

        if ((a1 == null) || (a2 == null))
        { // If both are null, wouldn't get past a1 == a2
            return false;
        }
        else if (a1.length != a2.length)
        {
            return false;
        }

        for (int i = 0; i < a1.length; i++)
        {
            if (!equals(a1[i], a2[i]))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if two Iterables contain the same elements in the same order
     * 
     * @param i1
     *            the first Iterable
     * @param i2
     *            the second Iterable
     * @return whether or not i1 contains the same elements as i2
     **/
    public final static boolean equals(final Iterable<?> i1, final Iterable<?> i2)
    {
        return equals(i1.iterator(), i2.iterator());
    }

    /**
     * Determines if two Iterators contain the same elements in the same order
     * 
     * @param iter1
     *            the first Iterator
     * @param iter2
     *            the second Iterator
     * @return whether or not iter1 contains the same elements as iter2
     **/
    public final static boolean equals(final Iterator<?> iter1, final Iterator<?> iter2)
    {
        while (iter1.hasNext())
        {
            if (!iter2.hasNext())
            {
                return false;
            }
            else if (!equals(iter1.next(), iter2.next()))
            {
                return false;
            }
        }

        return !iter2.hasNext();
    }

    /**
     * Returns the length of the given array
     * 
     * @param array
     *            the array for which to return the length
     * @return the length
     **/
    public final static int length(final Object array)
    {
        if (array == null)
        {
            return 0;
        }
        else if (array instanceof byte[])
        {
            return ((byte[]) array).length;
        }
        else if (array instanceof short[])
        {
            return ((short[]) array).length;
        }
        else if (array instanceof int[])
        {
            return ((int[]) array).length;
        }
        else if (array instanceof long[])
        {
            return ((long[]) array).length;
        }
        else if (array instanceof float[])
        {
            return ((float[]) array).length;
        }
        else if (array instanceof double[])
        {
            return ((double[]) array).length;
        }
        else if (array instanceof boolean[])
        {
            return ((boolean[]) array).length;
        }
        else if (array instanceof char[])
        {
            return ((char[]) array).length;
        }
        return ((Object[]) array).length;
    }

    /**
     * Returns the length of the given array
     * 
     * @param array
     *            the array for which to return the length
     * @return the length
     **/
    public final static int length(final Object[] array)
    {
        return array == null ? 0 : array.length;
    }

    /**
     * Returns the length of the given array
     * 
     * @param array
     *            the array for which to return the length
     * @return the length
     **/
    public final static int length(final byte[] array)
    {
        return array == null ? 0 : array.length;
    }

    /**
     * Returns the length of the given array
     * 
     * @param array
     *            the array for which to return the length
     * @return the length
     **/
    public final static int length(final int[] array)
    {
        return array == null ? 0 : array.length;
    }

}
