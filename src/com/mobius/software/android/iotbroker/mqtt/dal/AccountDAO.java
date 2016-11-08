package com.mobius.software.android.iotbroker.mqtt.dal;

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

import com.mobius.software.android.iotbroker.mqtt.parser.QoS;

public class AccountDAO {

	private int id;
	private String userName;
	private String pass;
	private String clientID;
	private String serverHost;
	private boolean cleanSession;
	private int keepAlive;
	private String will;
	private String willTopic;
	private Boolean isRetain;
	private QoS qos;	
	private int isDefault;

	public AccountDAO() {

	}

	public AccountDAO(String userName, String pass, String clientID,
			String serverHost, boolean cleanSession, int keepAlive,
			String will, String willTopic,Boolean retain, QoS qos, int isDefault) {

		this.userName = userName;
		this.pass = pass;
		this.clientID = clientID;
		this.serverHost = serverHost;
		this.cleanSession = cleanSession;
		this.keepAlive = keepAlive;
		this.will = will;
		this.willTopic=willTopic;
		this.isRetain = retain;
		this.qos = qos;
		this.isDefault = isDefault;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
	}

	public String getWill() {
		return will;
	}

	public void setWill(String will) {
		this.will = will;
	}

	public Boolean isRetain() {
		return isRetain;
	}

	public void setRetain(Boolean retain) {
		this.isRetain = retain;
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public QoS getQos() {
		return qos;
	}

	public void setQos(QoS qos) {
		this.qos = qos;
	}
	
	public String getWillTopic() {
		return willTopic;
	}

	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int  isDefault) {
		this.isDefault = isDefault;
	}
	
}
