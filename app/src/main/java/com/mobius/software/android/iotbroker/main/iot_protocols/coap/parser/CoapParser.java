package com.mobius.software.android.iotbroker.main.iot_protocols.coap.parser;

import java.util.List;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.math.BigInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapOptionType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapContentFormat;

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

    static private int OPTION_13_FAILD_CONSTANT = 13;
    static private int OPTION_14_FAILD_CONSTANT = 14;
    static private int OPTION_15_FAILD_CONSTANT = 15;

    public ByteBuf next(ByteBuf buf)  {
        return buf;
    }

    public Message decode(ByteBuf buf)  {

        CoapHeader message = new CoapHeader();

        byte firstByte = buf.readByte();

        byte version = (byte)((firstByte >> 6));

        if (version != message.getVersion()) {
            return null;
        }

        int type = ((firstByte >> 4) & 0x3);
        message.setCoapType(CoapType.valueOf(type));

        byte tokenLength = (byte)(firstByte & 0xF);
        message.setTokenExist(tokenLength != 0);

        byte code = buf.readByte();
        message.setCode(CoapCode.valueOf(code));

        int messageId = buf.readShort();
        message.setMessageID(messageId);

        if (message.isTokenExist()) {
            byte[] tokenValue = new byte[tokenLength];
            buf.readBytes(tokenValue, 0, tokenLength);
            long value1 = 0;
            for (int i = 0; i < tokenValue.length; i++) {
                value1 = (value1 << 8) + (tokenValue[i] & 0xff);
            }
            message.setToken((int) value1);
        }

        int previousOptionDelta = 0;

        while ((buf.array().length - buf.readableBytes()) < buf.array().length) {
            byte optionByte = buf.readByte();
            byte optionDelta = (byte) ((optionByte >> 4) & 0xF);
            byte optionLength = (byte) (optionByte & 0xF);

            if (optionDelta == OPTION_15_FAILD_CONSTANT) {
                if (optionLength != OPTION_15_FAILD_CONSTANT) {
                    return null;
                }
                break;
            }

            int extendedDelta = 0;
            byte optionIndexOffset = 1;

            if (optionDelta == OPTION_13_FAILD_CONSTANT) {
                optionIndexOffset += 1;
            } else if (optionDelta == OPTION_14_FAILD_CONSTANT) {
                optionIndexOffset += 2;
            }

            if ((buf.array().length - buf.readableBytes()) + optionIndexOffset <= buf.array().length) {
                int length = optionIndexOffset - 1;
                if (length > 0) {
                    byte[] extendedDeltaBytes = new byte[length];
                    buf.readBytes(extendedDeltaBytes);
                    extendedDelta = new BigInteger(extendedDeltaBytes).intValue();
                }
            } else {
                return null;
            }

            int optionLengthExtendedOffsetIndex = optionIndexOffset;
            if (optionLength == OPTION_13_FAILD_CONSTANT) {
                optionIndexOffset += 1;
            } else if (optionLength == OPTION_14_FAILD_CONSTANT) {
                optionIndexOffset += 2;
            } else if (optionLength == OPTION_15_FAILD_CONSTANT) {
                return null;
            }

            int length = optionIndexOffset - optionLengthExtendedOffsetIndex;
            if (length > 0) {
                byte[] optionLengthBytes = new byte[length];
                buf.readBytes(optionLengthBytes);
                optionLength += (byte) ByteBuffer.wrap(optionLengthBytes).getInt();
            }
            if ((buf.array().length - buf.readableBytes()) + optionIndexOffset + optionLength > buf.array().length) {
                return null;
            }

            int newOptionNumber = optionDelta + extendedDelta + previousOptionDelta;

            byte[] optionValueBytes = new byte[optionLength];
            buf.readBytes(optionValueBytes, 0, optionLength);
            String optionValue = new String(optionValueBytes);

            message.addOption(CoapOptionType.valueOf(newOptionNumber), optionValue);

            previousOptionDelta += optionDelta + extendedDelta;
        }

        if (buf.readableBytes() > 0) {
            byte[] payload = new byte[buf.readableBytes()];
            buf.readBytes(payload);
            message.setPayload(new String(payload));
        }

        return message;
    }

    public ByteBuf encode(Message header)  {

        CoapHeader message = (CoapHeader)header;

        String finalString = "";
        String tokenAsString = hexStringFrom(message.getToken());

        byte firstByte = 0;

        firstByte |= (1 << 6);
        firstByte |= (message.getType() << 4);
        firstByte |= tokenAsString.length() / 2;

        finalString += String.format("%02X", firstByte);
        finalString += String.format("%02X", message.getCode().getType());
        finalString += String.format("%04X", message.getMessageID());
        finalString += tokenAsString;

        List<CoapOptionType> sortedArray = new ArrayList<CoapOptionType>(message.getOptions().keySet());

        int previousDelta = 0;

        for (CoapOptionType key: sortedArray) {
            List<String> value = message.getOptions().get(key);

            for (int i = 0; i < value.size(); i++) {
                int delta = key.getType() - previousDelta;
                String valueForKey;

                if (key == CoapOptionType.ETAG || key == CoapOptionType.IF_MATCH) {
                    valueForKey = value.get(i);
                } else if (key == CoapOptionType.BLOCK_2 || key == CoapOptionType.URI_PORT ||
                        key == CoapOptionType.CONTENT_FORMAT || key == CoapOptionType.MAX_AGE ||
                        key == CoapOptionType.ACCEPT || key == CoapOptionType.SIZE_1 || key == CoapOptionType.SIZE_2 ||
                        key == CoapOptionType.OBSERVE) {
                    valueForKey = hexStringFrom(Integer.parseInt(value.get(i)));
                } else {
                    valueForKey = hexStringFrom(value.get(i));
                }

                int length = valueForKey.length() / 2;

                String extendedDelta = "";
                String extendedLength = "";

                if (delta >= 269) {
                    finalString += String.format("%01X", OPTION_14_FAILD_CONSTANT);
                    extendedDelta = String.format("%04X", delta - 269);
                } else if (delta >= OPTION_13_FAILD_CONSTANT) {
                    finalString += String.format("%01X", OPTION_13_FAILD_CONSTANT);
                    extendedDelta = String.format("%02X", delta - OPTION_13_FAILD_CONSTANT);
                } else {
                    finalString += String.format("%01X", delta);
                }

                if (length >= 269) {
                    finalString += String.format("%01X", OPTION_14_FAILD_CONSTANT);
                    extendedLength = String.format("%04X", length - 269);
                } else if (length >= OPTION_13_FAILD_CONSTANT) {
                    finalString += String.format("%01X", OPTION_13_FAILD_CONSTANT);
                    extendedLength = String.format("%02X", length - OPTION_13_FAILD_CONSTANT);
                } else {
                    finalString += String.format("%01X", length);
                }

                finalString += extendedDelta;
                finalString += extendedLength;
                finalString += valueForKey;

                previousDelta += delta;
            }
        }

        if (message.getPayload().length() > 0) {
            if (payloadDecodeFor(message)) {
                finalString += String.format("%02X", 255);
                finalString += hexStringFrom(message.getPayload());
            } else {
                finalString += String.format("%02X", 255);
                finalString += message.getPayload();
            }
        }

        return getHexDataFrom(finalString);
    }

    // private methods

    private String hexStringFrom(int value) {

        if (value == 0) {
            return "";
        } else if (value < 255) {
            return String.format("%02X", value);
        } else if (value < 65535) {
            return String.format("%04X", value);
        } else if (value < 16777215) {
            return String.format("%06X", value);
        } else {
            return String.format("%08X", value);
        }
    }

    private boolean payloadDecodeFor(Message header) {

        CoapHeader message = (CoapHeader)header;

        List<String> list = message.getOptions().get(CoapOptionType.CONTENT_FORMAT);

        if (list == null) {
            return true;
        } else {
            boolean plain = Integer.parseInt(list.get(0)) == CoapContentFormat.PLAIN_CONTENT_FORMAT.getType();
            boolean link = Integer.parseInt(list.get(0)) == CoapContentFormat.LINK_CONTENT_FORMAT.getType();
            boolean xml = Integer.parseInt(list.get(0)) == CoapContentFormat.XML_CONTENT_FORMAT.getType();
            boolean json = Integer.parseInt(list.get(0)) == CoapContentFormat.JSON_CONTENT_FORMAT.getType();
            if (plain || link || xml || json) {
                return  true;
            }
        }
        return false;
    }

    public ByteBuf getHexDataFrom(String string) {

        if(string.length() % 2 != 0)
            return null;

        byte[] result = new byte[string.length() / 2];
        for(int i = 0; i<result.length; i++) {
            char currChar = string.charAt(i * 2);
            int highValue, lowValue;

            if(currChar >= 'A' && currChar <= 'F')
                highValue = ((currChar - 'A' + 10) << 4);
            else if (currChar >= 'a' && currChar <= 'f')
                highValue = ((currChar-'a'+10)<<4);
            else if (currChar >= '0' && currChar <= '9')
                highValue = ((currChar-'0') << 4);
            else
                return null;

            currChar = string.charAt(i * 2 + 1);
            if (currChar >= 'A' && currChar <= 'F')
                lowValue = (currChar - 'A'+10);
            else if (currChar >= 'a' && currChar <= 'f')
                lowValue = (currChar - 'a'+10);
            else if (currChar >= '0' && currChar <= '9')
                lowValue = (currChar - '0');
            else
                return null;

            result[i] = (byte)((highValue & 0xF0) | (lowValue & 0x0F));
        }

        return Unpooled.wrappedBuffer(result);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            hexChars[j * 2] = hexArray[v >>> 4];
        }

        return new String(hexChars);
    }

    private String hexStringFrom(String string) {
        byte[] bytes = bytesToHex(string.getBytes()).getBytes();
        return this.stringFromDataWithHex(bytes);
    }

    private String stringFromDataWithHex(byte[] bytes) {
        String result = new String(bytes).replaceAll("\\s+","");
        //result = result.toLowerCase();
        return result;
    }
}
