package com.mobius.software.android.iotbroker.main.fragments;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobius.software.android.iotbroker.main.adapters.MessagesListAdapter;
import com.mobius.software.android.iotbroker.main.base.DaoObject;
import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao.Properties;
import com.mobius.software.android.iotbroker.main.dal.DaoType;
import com.mobius.software.android.iotbroker.main.dal.Messages;
import com.mobius.software.android.iotbroker.main.dal.MessagesDao;
import com.mobius.software.android.iotbroker.main.managers.NetworkManager;
import com.mobius.software.android.iotbroker.main.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

public class MessagesListFragment extends Fragment {

	List<Messages> messagesArray;

	MessagesListAdapter messagesAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.messages_list_activity, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		refresh();
		messagesAdapter = new MessagesListAdapter(view.getContext(), messagesArray);

		ListView lvMain = (ListView) view.findViewById(R.id.list_messages);
		lvMain.setAdapter(messagesAdapter);
	}

	private void refresh() {
		if (!NetworkManager.hasNetworkAccess(getActivity())) {
			MessageDialog.showMessage(getActivity(), getString(R.string.no_network_error),
					getString(R.string.no_network_error_message));
			return;
		}
		
		AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(getActivity(), DaoType.AccountsDao));
		List<Accounts> accountsList = accountDao.queryBuilder().where(Properties.IsDefault.eq(1)).list();

		Accounts account = null;

		if (accountsList != null && accountsList.size() > 0) {
			account = accountsList.get(0);
		}

		messagesArray = new ArrayList<Messages>();
		if (account != null) {
			MessagesDao messageDao = ((MessagesDao) DaoObject.getDao(getActivity(), DaoType.MessagesDao));

			messagesArray = messageDao.queryBuilder().where(
					com.mobius.software.android.iotbroker.main.dal.MessagesDao.Properties.AccountID.eq(account.getId())).list();

			Collections.reverse(messagesArray);
		}
	}

	 public void update() {
		refresh();
		messagesAdapter.updateList(messagesArray);
		messagesAdapter.notifyDataSetChanged();
	}
}
