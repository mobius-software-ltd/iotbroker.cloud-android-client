package com.mobius.software.android.iotbroker.mqtt.fragments;

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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobius.software.android.iotbroker.mqtt.adapters.MessagesListAdapter;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.dal.MessageDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.MessagesManager;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

public class MessagesListFragment extends Fragment {

	List<MessageDAO> messagesArray;

	MessagesListAdapter messagesAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		

		return inflater.inflate(R.layout.messages_list_activity, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		refresh();
		messagesAdapter = new MessagesListAdapter(view.getContext(),
				messagesArray);

		ListView lvMain = (ListView) view.findViewById(R.id.list_messages);
		lvMain.setAdapter(messagesAdapter);
	}

	private void refresh() {
		if (!NetworkManager.hasNetworkAccess(getActivity())) {
			MessageDialog.showMessage(getActivity(),
					getString(R.string.no_network_error),
					getString(R.string.no_network_error_message));
			return;
		}

		AccountManager accountMngr = new AccountManager(getActivity()
				.getApplicationContext());
		accountMngr.open();
		AccountDAO currAccount = accountMngr.getCurrentAccount();
		accountMngr.close();

		MessagesManager mesManager = new MessagesManager(getActivity()
				.getApplicationContext());
		mesManager.open();

		messagesArray = mesManager.getReverseListByAccount(currAccount.getId());
		mesManager.close();
	}

	public void update() {
		refresh();
		messagesAdapter.updateList(messagesArray);
		messagesAdapter.notifyDataSetChanged();
	}
}
