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

import com.mobius.software.android.iotbroker.mqtt.dal.Accounts;

import android.content.Context;
import android.widget.TextView;

public class AccountsArrayAdapter extends GenericArrayAdapter<Accounts> {

	public AccountsArrayAdapter(Context context, List<Accounts> objects) {
		super(context, objects);
	}

	@Override
	public void drawText(TextView tbxUserName, TextView tbxServerHost, Accounts object) {
		tbxUserName.setText(object.getUserName());
		tbxServerHost.setText(object.getServerHost());
	}

}
