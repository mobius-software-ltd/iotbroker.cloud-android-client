package com.mobius.software.android.iotbroker.main.base;

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

import android.os.Parcel;
import android.os.Parcelable;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;

public class WillParcel extends Will implements Parcelable {

	public int describeContents() {
		return 0;
	}

	public WillParcel(MQTopic topic, byte[] content, Boolean retain) {
		super(topic, content, retain);
	}
	
	public WillParcel(Text topicName, QoS qos, byte[] content, Boolean retain) { 
		super(new MQTopic(topicName, qos), content, retain);
	}
	
	public WillParcel(Will will) { 
		super(new MQTopic(will.getTopic().getName(), will.getTopic().getQos()), will.getContent(), will.getRetain());
	}

	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		
		parcel.writeString(this.getTopic().getName().toString());
		parcel.writeInt(this.getTopic().getQos().getValue());
		parcel.writeInt(this.getContent().length); 
		
		parcel.writeByteArray(this.getContent());

		int isretainInt = 0;
		
		if (this.getRetain()) {
			isretainInt = 1;
		}
		parcel.writeInt(isretainInt);
		
	}

	public static final Parcelable.Creator<WillParcel> CREATOR = new Parcelable.Creator<WillParcel>() {
		public WillParcel createFromParcel(Parcel in) {
			return new WillParcel(in);
		}

		public WillParcel[] newArray(int size) {
			return new WillParcel[size];
		}
	};

	private WillParcel(Parcel data) {
		
		Text topicName = new Text(data.readString());
		QoS qos = QoS.valueOf(data.readInt());
		MQTopic topic =  new MQTopic(topicName, qos);
		this.setTopic(topic);

		byte[] content  = new byte[data.readInt()]; 
		data.readByteArray(content);
		this.setContent(content);

		this.setRetain(data.readInt() != 0);
	}	

}
