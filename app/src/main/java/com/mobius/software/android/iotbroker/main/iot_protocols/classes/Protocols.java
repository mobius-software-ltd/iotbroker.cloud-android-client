package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
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

import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.exceptions.MalformedMessageException;

import java.util.HashMap;
import java.util.Map;

public enum Protocols {

    MQTT_PROTOCOL(0), MQTT_SN_PROTOCOL(1), COAP_PROTOCOL(2), AMQP_PROTOCOL(3), WEBSOCKET_PROTOCOL(4);

    private int value;

    private static final Map<Integer, Protocols> intToTypeMap = new HashMap<Integer, Protocols>();
    private static final Map<String, Protocols> strToTypeMap = new HashMap<String, Protocols>();

    static
    {
        for (Protocols type : Protocols.values())
        {
            intToTypeMap.put(type.value, type);
            strToTypeMap.put(type.name(), type);
        }
    }

    public int getValue()
    {
        return value;
    }

    private Protocols(final int leg)
    {
        value = leg;
    }

    public static Protocols valueOf(int type) throws MalformedMessageException
    {
        return intToTypeMap.get(type);
    }
}
