package com.mobius.software.android.iotbroker.main.iot_protocols;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;
import com.mobius.software.android.iotbroker.main.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;

import java.util.TimerTask;

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

public interface IotProtocol extends Device, ConnectionListener {

    Integer workerThreads = 4;

    void setListener(ClientStateListener listener);
    void setDbListener(DataBaseListener dbListener);

    ConnectionState getConnectionState();

    void reinit();
    Boolean createChannel();
    boolean checkConnected();
    void closeConnection();
    void closeChannel();

    void send(Message message);

    void connect();
    void subscribe(String topicName, QoS qos);
    void unsubscribe(String topicName, QoS qos);
    void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup);
    void disconnect();

    Message getPingreqMessage();

    boolean checkCreated();
    void executeTimer(TimerTask task, long period);

    void timeout();
}
