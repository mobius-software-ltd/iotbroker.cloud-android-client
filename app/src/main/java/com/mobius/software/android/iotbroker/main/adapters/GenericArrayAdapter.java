package com.mobius.software.android.iotbroker.main.adapters;

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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobius.software.iotbroker.androidclient.R;

public abstract class GenericArrayAdapter<T> extends ArrayAdapter<T> {

	// Vars
	private LayoutInflater mInflater;

	public GenericArrayAdapter(Context context, List<T> objects) {
		super(context, 0, objects);
		init(context);
	}

	// Headers
	public abstract void drawText(TextView textView,TextView textView2 ,T object);

	private void init(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.accounts_list_item, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		drawText(vh.tbxUserName, vh.tbxServerHost, getItem(position));

		return convertView;
	}

	static class ViewHolder {

		TextView tbxUserName;
		TextView tbxServerHost;

		private ViewHolder(View rootView) {
			tbxUserName = (TextView) rootView.findViewById(R.id.tbx_account_name);
			tbxServerHost = (TextView) rootView.findViewById(R.id.tbx_server_host);
		}
	}
}