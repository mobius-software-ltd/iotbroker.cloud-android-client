package com.mobius.software.android.iotbroker.mqtt.adapters;

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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.mqtt.dal.Messages;
import com.mobius.software.iotbroker.androidclient.R;

public class MessagesListAdapter extends BaseAdapter {

	private final String QOS_TITLE = "QOS : ";

	private static String MESSAGE_IN_TEXT = "in";
	private static String MESSAGE_OUT_TEXT = "out";

	Context ctx;
	LayoutInflater lInflater;
	List<Messages> objects;

	public MessagesListAdapter(Context context, List<Messages> messageArray) {
		ctx = context;
		objects = messageArray;
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = lInflater.inflate(R.layout.messages_list_item, parent, false);
		}

		Messages messageItem = getMessage(position);

		String qosStr = Integer.toString(messageItem.getQos());
		String qosLine = QOS_TITLE.concat(qosStr);

		String isIncominText = messageItem.getIsIncoming() > 0 ? MESSAGE_OUT_TEXT : MESSAGE_IN_TEXT;

		((TextView) view.findViewById(R.id.lblQos)).setText(qosLine);

		((TextView) view.findViewById(R.id.lbl_incmomming)).setText(isIncominText);
		((TextView) view.findViewById(R.id.lbl_TopicName)).setText(messageItem.getTopicName());
		((TextView) view.findViewById(R.id.lbl_messages_item)).setText(messageItem.getMessageItem());

		return view;
	}

	Messages getMessage(int position) {
		return ((Messages) getItem(position));
	}

	public void updateList(List<Messages> messagesArray) {
		this.objects = messagesArray;
	}
}
