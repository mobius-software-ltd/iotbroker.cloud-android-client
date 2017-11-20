package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.util;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

public class ValuesValidator
{
	private static final Set<Integer> RESERVED_MESSAGE_IDS = new HashSet<Integer>(Arrays.asList(new Integer[]
	{ 0x0000 }));
	private static final Set<Integer> RESERVED_TOPIC_IDS = new HashSet<Integer>(Arrays.asList(new Integer[]
	{ 0x0000, 0xFFFF }));

	public static boolean validatePacketID(int packetID)
	{
		return packetID > 0 && !RESERVED_MESSAGE_IDS.contains(packetID);
	}

	public static boolean validateTopicID(int topicID)
	{
		return topicID > 0 && !RESERVED_TOPIC_IDS.contains(topicID);
	}

	public static boolean validateRegistrationTopicID(int topicID)
	{
		return topicID >= 0;
	}

	public static boolean canRead(ByteBuf buf, int bytesLeft)
	{
		return buf.isReadable() && bytesLeft > 0;
	}

	public static boolean validateClientID(String clientID)
	{
		return clientID != null && !clientID.isEmpty();
	}
}
