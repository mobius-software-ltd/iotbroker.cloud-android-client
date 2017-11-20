package com.mobius.software.android.iotbroker.main.base;

/**
 * Mobius Software LTD
 * Copyright 2015-2017, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

public final class Scan
{
    public int offset;
    public String str;

    public Scan(String str)
    {
        this(str, 0);
    }

    public Scan(String str, int off)
    {
        this.str = str;
        this.offset = off;
    }

    public static int strtol(String s, int radix)
    {
        return new Scan(s).strtol(radix);
    }

    public int strtol(int radix)
    {
        int next, value;

        if (str.length() <= offset || radix < 2 || 36 < radix)
            return 0;

        int sign = 0;
        switch (str.charAt(offset)) {
            case '-': sign = -1; offset++; break;
            case '+': sign = +1; offset++; break;
        }

        for (value = 0, next = offset; next < str.length(); next++) {
            int digit = Character.digit(str.charAt(next), radix);
            if (digit < 0) break;
            value *= radix;
            value += digit;
        }

        if (offset < next)
            offset = next;
        else if (sign != 0)
            offset--;

        return sign == 0 ? value : value * sign;
    }
}
