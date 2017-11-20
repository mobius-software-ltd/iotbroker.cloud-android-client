package com.mobius.software.android.iotbroker.main.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;

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

public class ClientInfoParcel implements Parcelable {

    private Protocols protocol;

    private String clientId;
    private String username;
    private String password;

    private String host;
    private Integer port;

    private Boolean cleanSession;
    private Integer keepalive;

    private Will will;

    private Boolean shouldUpdate;
    private Boolean isVisible;

    private Integer messageType;

    private Integer qos;
    private String topicName;

    private Boolean isDublicate;
    private Boolean isRetain;
    private String packetId;
    //private String topicName;
    private ConnectionState connectionState;

    public ClientInfoParcel(Integer protocol, String username, String password, String clientId, String host, Boolean cleanSession, Integer keepalive, Will will, Integer port, Boolean shouldUpdate) {
        this.protocol = Protocols.valueOf(protocol);
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.host = host;
        this.cleanSession = cleanSession;
        this.keepalive = keepalive;
        this.will = will;
        this.port = port;
        this.shouldUpdate = shouldUpdate;
    }

    private ClientInfoParcel(Parcel in) {

        Integer protocolValue = readInt(in);
        if (protocolValue != null) {
            protocol = Protocols.valueOf(protocolValue);
        }

        String clientIdValue = readString(in);
        if (clientIdValue != null) {
            clientId = clientIdValue;
        }

        String usernameValue = readString(in);
        if (usernameValue != null) {
            username = usernameValue;
        }

        String passwordValue = readString(in);
        if (passwordValue != null) {
            password = passwordValue;
        }

        String hostValue = readString(in);
        if (hostValue != null) {
            host = hostValue;
        }

        Integer portValue = readInt(in);
        if (portValue != null) {
            port = portValue;
        }

        Boolean cleanSessionValue = readBoolean(in);
        if (cleanSessionValue != null) {
            cleanSession = cleanSessionValue;
        }

        Integer keepaliveValue = readInt(in);
        if (keepaliveValue != null) {
            keepalive = keepaliveValue;
        }

        Parcelable parcelable = readParcelable(in, WillParcel.class.getClassLoader());
        if (parcelable != null) {
            WillParcel willParcel = (WillParcel)parcelable;
            will = new Will(willParcel.getTopic(), willParcel.getContent(), willParcel.getRetain());
        }

        Boolean shouldUpdateValue = readBoolean(in);
        if (shouldUpdateValue != null) {
            shouldUpdate = shouldUpdateValue;
        }

        Boolean isVisibleValue = readBoolean(in);
        if (isVisibleValue != null) {
            isVisible = isVisibleValue;
        }

        Integer messageTypeValue = readInt(in);
        if (messageTypeValue != null) {
            messageType = messageTypeValue;
        }

        Integer qosValue = readInt(in);
        if (qosValue != null) {
            qos = qosValue;
        }

        String topicNameValue = readString(in);
        if (topicNameValue != null) {
            topicName = topicNameValue;
        }

        Boolean isDublicateValue = readBoolean(in);
        if (isDublicateValue != null) {
            isDublicate = isDublicateValue;
        }

        Boolean isRetainValue = readBoolean(in);
        if (isRetainValue != null) {
            isRetain = isRetainValue;
        }

        String packetIdValue = readString(in);
        if (packetIdValue != null) {
            packetId = packetIdValue;
        }

        String connectionStateValue = readString(in);
        if (connectionStateValue != null) {
            connectionState = ConnectionState.valueOf(connectionStateValue);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        this.writeInt(protocol.getValue(), dest);

        this.writeString(clientId, dest);
        this.writeString(username, dest);
        this.writeString(password, dest);

        this.writeString(host, dest);
        this.writeInt(port, dest);

        this.writeBoolean(cleanSession, dest);
        this.writeInt(keepalive, dest);

        if (will != null) {
            WillParcel willParcer = new WillParcel(will.getTopic(), will.getContent(), will.getRetain());
            this.writeParcelable(willParcer, dest, flags);
        } else {
            dest.writeInt(0);
        }

        this.writeBoolean(shouldUpdate, dest);
        this.writeBoolean(isVisible, dest);

        this.writeInt(messageType, dest);

        this.writeInt(qos, dest);
        this.writeString(topicName, dest);

        this.writeBoolean(isDublicate, dest);
        this.writeBoolean(isRetain, dest);
        this.writeString(packetId, dest);

        if (connectionState != null) {
            this.writeString(connectionState.toString(), dest);
        } else {
            dest.writeInt(0);
        }
    }

    public static final Creator<ClientInfoParcel> CREATOR = new Creator<ClientInfoParcel>() {
        @Override
        public ClientInfoParcel createFromParcel(Parcel in) {
            return new ClientInfoParcel(in);
        }

        @Override
        public ClientInfoParcel[] newArray(int size) {
            return new ClientInfoParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // private methods

    private void writeInt(Integer value, Parcel dest) {

        if (value != null) {
            dest.writeInt(1);
            dest.writeInt(value);
            return;
        }
        dest.writeInt(0);
    }

    private void writeBoolean(Boolean value, Parcel dest) {

        if (value != null) {
            dest.writeInt(1);
            dest.writeInt(value ? 1 : 0);
            return;
        }
        dest.writeInt(0);
    }

    private void writeString(String value, Parcel dest) {

        if (value != null) {
            dest.writeInt(1);
            dest.writeString(value);
            return;
        }
        dest.writeInt(0);
    }

    private void writeParcelable(Parcelable value, Parcel dest, int flags) {

        if (value != null) {
            dest.writeInt(1);
            dest.writeParcelable(value, flags);
            return;
        }
        dest.writeInt(0);
    }

    private Integer readInt(Parcel in) {

        boolean flag = (in.readInt() != 0);
        if (flag) {
            return in.readInt();
        }
        return null;
    }

    private Boolean readBoolean(Parcel in) {

        boolean flag = (in.readInt() != 0);
        if (flag) {
            return (in.readInt() != 0);
        }
        return null;
    }

    private String readString(Parcel in) {

        boolean flag = (in.readInt() != 0);
        if (flag) {
            return in.readString();
        }
        return null;
    }

    private Parcelable readParcelable(Parcel in, ClassLoader classLoader) {

        boolean flag = (in.readInt() != 0);
        if (flag) {
            return in.readParcelable(classLoader);
        }
        return null;
    }

    // setters & getters

    public Protocols getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocols protocol) {
        this.protocol = protocol;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Integer getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(Integer keepalive) {
        this.keepalive = keepalive;
    }

    public Will getWill() {
        return will;
    }

    public void setWill(Will will) {
        this.will = will;
    }

    public boolean isShouldUpdate() {
        return shouldUpdate;
    }

    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public boolean isDublicate() {
        return isDublicate;
    }

    public void setDublicate(boolean dublicate) {
        isDublicate = dublicate;
    }

    public boolean isRetain() {
        return isRetain;
    }

    public void setRetain(boolean retain) {
        isRetain = retain;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }
}
