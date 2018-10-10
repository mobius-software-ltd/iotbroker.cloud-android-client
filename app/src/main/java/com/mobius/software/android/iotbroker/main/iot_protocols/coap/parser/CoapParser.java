package com.mobius.software.android.iotbroker.main.iot_protocols.coap.parser;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.math.BigInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.clasess.CoapParsingException;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapOption;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapOptionType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapContentFormat;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.exceptions.MalformedMessageException;

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

public class CoapParser extends AbstractParser {

    public ByteBuf next(ByteBuf buf)  {
        return buf;
    }

    public Message decode(ByteBuf buf)  {

        CoapHeader header = new CoapHeader();

        int firstByte = buf.readUnsignedByte();

        int version = firstByte >> 6;
        if (version != 1)
            throw new CoapParsingException("Invalid version:" + version);
        header.setVersion(version);

        int typeValue = (firstByte >> 4) & 3;
        header.setCoapType(CoapType.valueOf(typeValue));

        int tokenLength = firstByte & 0xf;
        if (tokenLength > 8)
            throw new CoapParsingException("Invalid token length:" + tokenLength);

        int codeByte = buf.readUnsignedByte();
        int codeValue = (codeByte >> 5) * 100;
        codeValue += codeByte & 0x1F;
        CoapCode code = CoapCode.valueOf(codeValue);
        if (code == null)
            throw new CoapParsingException("Unsupported code value:" + codeValue);

        header.setCode(code);
        header.setMessageID(buf.readUnsignedShort());

        if (tokenLength > 0)
        {
            byte[] token = new byte[tokenLength];
            buf.readBytes(token, 0, tokenLength);
            header.setToken(token);
        }

        int number = 0;

        while (buf.isReadable())
        {
            int nextByte = buf.readUnsignedByte();
            if (nextByte == 0xFF)
                break;

            int delta = (nextByte >> 4) & 15;
            if (delta == 13) {
                delta = buf.readByte() + 13;
            } else if (delta == 14) {
                delta = buf.readShort() + 269;
            } else if (delta > 14) {
                throw new CoapParsingException("Invalid option delta value:" + delta);
            }

            number += delta;

            int optionLength = nextByte & 15;
            if (optionLength == 13)
                optionLength = buf.readByte() + 13;
            else if (optionLength == 14)
                optionLength = buf.readShort() + 269;
            else if (optionLength > 14)
                throw new CoapParsingException("Invalid option length value:" + optionLength);

            byte[] optionValue = new byte[optionLength];
            if (optionLength > 0)
                buf.readBytes(optionValue, 0, optionLength);

            header.addOption(new CoapOption(number, optionLength, optionValue));
        }

        if (buf.isReadable())
        {
            byte[] payload = new byte[buf.readableBytes()];
            buf.readBytes(payload);
            header.setPayload(payload);
        }

        return header;
    }

    public ByteBuf encode(Message header)  {

        CoapHeader message = (CoapHeader)header;
        ByteBuf buf = Unpooled.buffer();

        byte firstByte = 0;

        firstByte += message.getVersion() << 6;
        firstByte += message.getType() << 4;

        if (message.getToken() != null)
            firstByte += message.getToken().length;

        buf.writeByte(firstByte);

        int codeMsb = (message.getCode().getType() / 100);
        int codeLsb = (byte) (message.getCode().getType() % 100);
        int codeByte = ((codeMsb << 5) + codeLsb);

        buf.writeByte(codeByte);

        buf.writeShort(message.getMessageID());

        if (message.getToken() != null)
            buf.writeBytes(message.getToken());

        int previousNumber = 0;
        for (CoapOption option : message.getOptions())
        {
            int delta = option.getNumber() - previousNumber;
            int nextByte = 0;

            Integer extendedDelta = null;
            if (delta < 13)
                nextByte += delta << 4;
            else
            {
                extendedDelta = delta;
                if (delta < 0xFF)
                    nextByte = 13 << 4;
                else
                    nextByte = 14 << 4;
            }


            Integer extendedLength = null;
            if (option.getLength() < 13)
                nextByte += option.getLength();
            else
            {
                extendedLength = option.getLength();
                if (option.getLength() < 0xFF)
                    nextByte += 13;
                else
                    nextByte += 14;
            }

            buf.writeByte(nextByte);
            if (extendedDelta != null)
            {
                if (extendedDelta < 0xFF) {
                    buf.writeByte(extendedDelta - 13);
                } else {
                    buf.writeShort(extendedDelta - 269);
                }
            }

            if (extendedLength != null)
            {
                if (extendedLength < 0xFF)
                    buf.writeByte(extendedLength - 13);
                else
                    buf.writeShort(extendedLength - 269);
            }

            buf.writeBytes(option.getValue());
            previousNumber = option.getNumber();
        }

        buf.writeByte((byte) 0xFF);

        if (message.getPayload() != null && message.getPayload().length > 0)
            buf.writeBytes(message.getPayload());

        return buf;
    }

}