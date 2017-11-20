package com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap;

import java.util.HashMap;
import java.util.Map;

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

public enum CoapContentFormat {

    PLAIN_CONTENT_FORMAT(0), LINK_CONTENT_FORMAT(40), XML_CONTENT_FORMAT(41), OCTET_STREAM_CONTENT_FORMAT(42), EXI_CONTENT_FORMAT(47), JSON_CONTENT_FORMAT(50);

    private int type;

    private static Map<Integer, CoapContentFormat> map = new HashMap<Integer, CoapContentFormat>();

    static {
        for (CoapContentFormat legEnum : CoapContentFormat.values()) {
            map.put(legEnum.type, legEnum);
        }
    }

    private CoapContentFormat(final int leg) {
        type = leg;
    }

    public static CoapContentFormat valueOf(int type) {
        return map.get(type);
    }

    public int getType() {
        return type;
    }
}
