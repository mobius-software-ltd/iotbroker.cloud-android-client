package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;

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

public class CountableMessage implements Message {

    protected Integer packetID;

    public CountableMessage() {
        this.packetID = 0;
    }

    public CountableMessage(Integer packetID) {
        this.packetID = packetID;
    }

    public Integer getPacketID() {
        return this.packetID;
    }

    public void setPacketID(Integer packetID) {
        this.packetID = packetID;
    }

    @Override
    public int getLength() {
        throw new Error("abstract method 'CountableMessage.getLength()' is called");
    }

    @Override
    public int getType() {
        throw new Error("abstract method 'CountableMessage.getType()' is called");
    }

    @Override
    public Protocols getProtocol() {
        throw new Error("abstract method 'CountableMessage.getProtocol()' is called");
    }

    @Override
    public void processBy(Device device) {
        throw new Error("abstract method 'CountableMessage.processBy(Device device)' is called");
    }
}
