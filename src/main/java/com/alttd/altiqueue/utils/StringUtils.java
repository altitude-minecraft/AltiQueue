package com.alttd.altiqueue.utils;

import java.text.DecimalFormat;

public class StringUtils
{

    public static String implode(String[] strings, int start, int end)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < end; i++)
        {
            sb.append(strings[i] + " ");
        }

        return sb.toString().trim();
    }

    public static String[] add(String[] array, String add)
    {
        String[] values = new String[array.length + 1];
        for (int i = 0; i < array.length; i++)
        {
            values[i] = array[i];
        }
        values[array.length] = add;
        return values;
    }

    public static String compile(String[] strings)
    {
        return implode(strings, 0, strings.length);
    }

    public static String capitalize(final String str)
    {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
        {
            return str;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint)
        {
            // already capitalized
            return str;
        }

        final int[] newCodePoints = new int[strLen]; // cannot be longer than
        // the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; )
        {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint; // copy the remaining ones
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static boolean contains(String[] values, String search)
    {
        for (String val : values)
        {
            if (val.equalsIgnoreCase(search))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.length() == 0;
    }

    public static boolean isWhitespace(String str)
    {
        if (str == null)
        {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAny(String search, String... strings)
    {
        if (isNullOrEmpty(search))
        {
            return false;
        }
        for (String searchCharSequence : strings)
        {
            if (indexOf(search, searchCharSequence, 0) >= 0)
            {
                return true;
            }
        }
        return false;
    }

    private static int indexOf(CharSequence cs, CharSequence searchChar, int start)
    {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    public static String formatNumber(Number number, int decimalPlaces, boolean useCommas)
    {
        StringBuilder sb = new StringBuilder();
        if (useCommas)
        {
            sb.append("#,##0");
        }
        else
        {
            sb.append("0");
        }
        if (decimalPlaces > 0)
        {
            sb.append('.');
            for (int i = 0; i < decimalPlaces; i++)
            {
                sb.append('0');
            }
        }
        return new DecimalFormat(sb.toString()).format(number);
    }

    public static String doubleFormat(double number)
    {
        String formatted;

        if (number % 1 == 0)
        {
            formatted = Integer.toString((int) number);
        }
        else
        {
            formatted = Double.toString(number);
        }
        return formatted;
    }

}
