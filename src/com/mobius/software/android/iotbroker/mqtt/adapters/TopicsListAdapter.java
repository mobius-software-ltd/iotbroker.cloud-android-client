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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.mqtt.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.mqtt.dal.Topics;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class TopicsListAdapter extends BaseAdapter {

	private final String QOS_TITLE = "QOS : ";

	Context ctx;
	LayoutInflater lInflater;
	List<Topics> objects;

	public TopicsListAdapter(Context context, List<Topics> topics) {
		ctx = context;
		objects = topics;
		lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void updateList(List<Topics> topics) {
		this.objects = topics;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = lInflater.inflate(R.layout.topics_list_item, parent, false);
		}

		final View delete_View = view.findViewById(R.id.delete_block);
		final View message_View = view.findViewById(R.id.message_layout);

		Topics topic = objects.get(position);
		String qosStr = Integer.toString(topic.getQos());

		String qosLine = QOS_TITLE.concat(qosStr);
		((TextView) view.findViewById(R.id.tbxQos)).setText(qosLine);
		((TextView) view.findViewById(R.id.tbxTopicsTitle)).setText(topic.getTopicName());

		Button btn_Cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_Cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				delete_View.setVisibility(View.GONE);
				message_View.setVisibility(View.VISIBLE);
			}
		});

		Button btn_delete = (Button) view.findViewById(R.id.btn_delete_topics);
		btn_delete.setTag(topic.getId());

		btn_delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (v.getTag() != null) {
					delete_View.setVisibility(View.GONE);
					message_View.setVisibility(View.GONE);
					Topics topic = objects.get(position);
				
					Intent startServiceIntent = new Intent(ctx, NetworkService.class);

					startServiceIntent.putExtra(ApplicationSettings.PARAM_TOPIC_NAME, topic.getTopicName());
					startServiceIntent.putExtra(ApplicationSettings.PARAM_QOS, topic.getQos());

					startServiceIntent.setAction(ApplicationSettings.ACTION_SUBSCRIBE);
					ctx.startService(startServiceIntent);

				}
			}
		});

		LinearLayout delete_Block = (LinearLayout) view.findViewById(R.id.message_layout);
		delete_Block.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				delete_View.setVisibility(View.VISIBLE);
				message_View.setVisibility(View.GONE);
			}
		});

		return view;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
