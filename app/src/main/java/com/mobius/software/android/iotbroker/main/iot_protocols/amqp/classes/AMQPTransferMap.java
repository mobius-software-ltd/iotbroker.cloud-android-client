package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPTransfer;

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

public class AMQPTransferMap {

    private int index;
    private Map<Integer, AMQPTransfer> map;

    public AMQPTransferMap()
    {
        index = 0;
        map = new HashMap<Integer, AMQPTransfer>();
    }

    public AMQPTransfer addTransfer(AMQPTransfer item)
    {
        int num = index;
        map.put(num, item);

        newIndex();
        item.setDeliveryId((long) num);

        return item;
    }

    public AMQPTransfer removeTransfer(int key)
    {
        return map.remove(key);
    }

    private void newIndex()
    {
        index += 1;

        if (index == 65535) {
            index = 0;
        }
    }
}
