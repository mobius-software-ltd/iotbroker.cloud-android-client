package com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.utility.ConvertorUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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

public class CoapHeader extends CountableMessage {

	private Integer version;
	private CoapType type;
	private byte[] token;
	private CoapCode code;
	private int messageID;
	private byte[] payload;
	private List<CoapOption> options;

    @Override
    public String toString() {
        return "CoapMessage [version=" + version + ", type=" + type + ", code=" + code + ", messageID=" + messageID + ", token=" + Arrays.toString(token) + ", options=" + options + ", payload=" + Arrays.toString(payload) + "]";
    }

    private CoapHeader(Integer version, CoapType type, CoapCode code, Integer messageID, byte[] token, List<CoapOption> options, byte[] payload) {
        this.version = version;
        this.type = type;
        this.code = code;
        this.messageID = messageID;
        this.token = token;
        this.options = options;
        this.payload = payload;
    }

	public CoapHeader() {
		this.version = 1;
		this.options = new ArrayList<>();
	}

	public CoapHeader(CoapCode method, boolean isCon, String payload) {
		this();
		this.code = method;
		this.type = isCon ? CoapType.CONFIRMABLE : CoapType.NON_CONFIRMABLE;
		this.payload = payload.getBytes();
	}

    public static Builder builder() {
        return new Builder();
    }

	public void addOption(CoapOptionType option, String value) {
		this.options.add(new CoapOption(option.getType(), value.length(), value.getBytes()));
	}

	public String getOptionValue(CoapOptionType option) {
        for (int i = 0; i < this.options.size(); i++) {
            CoapOption item = this.options.get(i);
            if (item.getNumber() == option.getType()) {
                return new String(item.getValue());
            }
        }
        return null;
    }

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int getType() {
		return this.type.getType();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.COAP_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public Integer getVersion() {
		return version;
	}

	public CoapType getCoapType() {
		return type;
	}

	public void setCoapType(CoapType type) {
		this.type = type;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public CoapCode getCode() {
		return code;
	}

	public void setCode(CoapCode code) {
		this.code = code;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

    public List<CoapOption> getOptions() {
        List<CoapOption> copy = this.options;
        Collections.sort(copy, new CoapOptionsComparator());
        return copy;
    }

    public void setPacketID(Integer packetID) {
		this.messageID = packetID;
		this.token = ConvertorUtil.intToByte(packetID);
		this.packetID = packetID;
	}

    public static class Builder
    {
        private Integer version = 1;
        private CoapType type;
        private CoapCode code;
        private Integer messageID;
        private byte[] token;
        private List<CoapOption> options = new ArrayList<>();
        private byte[] payload;
        private CoapOptionsComparator comparator=new CoapOptionsComparator();

        public CoapHeader build()
        {
            Collections.sort(options,comparator);
            return new CoapHeader(version, type, code, messageID, token, options, payload);
        }

        public Builder version(Integer version)
        {
            this.version = version;
            return this;
        }

        public Builder type(CoapType type)
        {
            this.type = type;
            return this;
        }

        public Builder code(CoapCode code)
        {
            this.code = code;
            return this;
        }

        public Builder messageID(Integer messageID)
        {
            this.messageID = messageID;
            return this;
        }

        public Builder token(byte[] token)
        {
            this.token = token;
            return this;
        }

        public Builder option(CoapOption option)
        {
            this.options.add(option);
            return this;
        }

        public Builder option(CoapOptionType type, Object value)
        {
            this.options.add(OptionParser.encode(type, value));
            return this;
        }

        public Builder payload(byte[] payload)
        {
            this.payload = payload;
            return this;
        }
    }

    public class Options
    {
        public Integer fetchAccept()
        {
            Short value = fetchSingleValue(CoapOptionType.ACCEPT, Short.class);
            return value != null ? value.intValue() : null;
        }

        public Integer fetchUriPort()
        {
            Short value = fetchSingleValue(CoapOptionType.URI_PORT, Short.class);
            return value != null ? value.intValue() : null;
        }

        public Integer fetchContentFormat()
        {
            Short value = fetchSingleValue(CoapOptionType.CONTENT_FORMAT, Short.class);
            return value != null ? value.intValue() : null;
        }

        public Integer fetchMaxAge()
        {
            return fetchSingleValue(CoapOptionType.MAX_AGE, Integer.class);
        }

        public Integer fetchSize1()
        {
            return fetchSingleValue(CoapOptionType.SIZE_1, Integer.class);
        }

        public Integer fetchObserve()
        {
            return fetchSingleValue(CoapOptionType.OBSERVE, Integer.class);
        }

        public boolean fetchIfNoneMatch()
        {
            return fetchSingleValue(CoapOptionType.IF_NONE_MATCH, byte[].class) != null;
        }

        public String fetchNodeID()
        {
            return fetchSingleValue(CoapOptionType.NODE_ID, String.class);
        }

        public String fetchIfMatch()
        {
            return fetchSingleValue(CoapOptionType.IF_MATCH, String.class);
        }

        public String fetchUriHost()
        {
            return fetchSingleValue(CoapOptionType.URI_HOST, String.class);
        }

        public String fetchEtag()
        {
            return fetchSingleValue(CoapOptionType.ETAG, String.class);
        }

        public String fetchUriPath()
        {
            return fetchSingleValue(CoapOptionType.URI_PATH, String.class);
        }

        public String fetchLocationPath()
        {
            return fetchSingleValue(CoapOptionType.LOCATION_PATH, String.class);
        }

        public String fetchUriQuery()
        {
            return fetchSingleValue(CoapOptionType.URI_QUERY, String.class);
        }

        public String fetchLocationQuery()
        {
            return fetchSingleValue(CoapOptionType.LOCATION_QUERY, String.class);
        }

        public String fetchProxyScheme()
        {
            return fetchSingleValue(CoapOptionType.PROXY_SCHEME, String.class);
        }

        public String fetchProxyUri()
        {
            return fetchSingleValue(CoapOptionType.PROXY_URI, String.class);
        }

        private <T> T fetchSingleValue(CoapOptionType targetType, Class<T> expectedClazz)
        {
            T value = null;
            if (options != null)
            {
                for (CoapOption option : options)
                {
                    value = fetchOptionValue(targetType, option, expectedClazz);
                    if (value != null)
                        break;
                }
            }
            return value;
        }

        @SuppressWarnings("unchecked")
        private <T> T fetchOptionValue(CoapOptionType targetType, CoapOption option, Class<T> expectedClazz)
        {
            T value = null;
            CoapOptionType optionType = CoapOptionType.valueOf(option.getNumber());
            if (optionType == targetType)
                value = (T) OptionParser.decode(optionType, option.getValue());
            return value;
        }
    }

}