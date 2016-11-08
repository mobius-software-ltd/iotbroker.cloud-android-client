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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.mqtt.adapters.TopicsListAdapter;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.dal.TopicDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.TopicsManager;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.android.iotbroker.mqtt.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

public class TopicsListFragment extends Fragment {

	protected static final String TOPICS_LIST_NAME = "MQTT_Client";
	Dialog dialog;
	public TextView txtView;

	List<TopicDAO> topicsList;
	TopicsListAdapter topicsListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		return inflater
				.inflate(R.layout.topics_list_activity, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refresh();
		topicsListAdapter = new TopicsListAdapter(view.getContext(), topicsList);
		ListView lvMain = (ListView) view.findViewById(R.id.list_view_topics);
		lvMain.setAdapter(topicsListAdapter);

		LinearLayout btnAddTopic = (LinearLayout) view
				.findViewById(R.id.add_topic_block);

		btnAddTopic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAddTopicDialog();
				topicsListAdapter.notifyDataSetChanged();
			}
		});
	}

	public void showAddTopicDialog() {

		final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(
				this.getActivity());
		ratingdialog.setTitle(R.string.topics_add_dialog_title);

		View linearlayout = getActivity().getLayoutInflater().inflate(
				R.layout.add_topic_dialog, null);
		ratingdialog.setView(linearlayout);

		final EditText tbxTopics = (EditText) linearlayout
				.findViewById(R.id.tbx_topics_name);

		final Spinner spnrQos = (Spinner) linearlayout
				.findViewById(R.id.spnr_qos);

		ratingdialog.setPositiveButton(R.string.topics_btn_OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (!NetworkManager.hasNetworkAccess(getActivity())) {
							MessageDialog
									.showMessage(
											getActivity(),
											getString(R.string.no_network_error),
											getString(R.string.no_network_error_message));
							return;
						}

						String topicsName = tbxTopics.getText().toString();

						if (isEmpty(topicsName)) {
							MessageDialog
									.showMessage(
											getActivity(),
											getString(R.string.tl_add_topic_error_title),
											getString(R.string.tl_new_required_field_name));
							return;
						}

						AccountManager accountMngr = new AccountManager(
								getActivity().getApplicationContext());
						accountMngr.open();
						accountMngr.close();

						String topicsQos = spnrQos.getSelectedItem().toString();
						int qos = Integer.parseInt(topicsQos);

						NetworkService.subscribe(topicsName, qos);
						dialog.dismiss();
					}
				})

		.setNegativeButton(R.string.topics_btn_Cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		ratingdialog.create();
		ratingdialog.show();
	}

	private void refresh() {
		AccountManager accountMngr = new AccountManager(getActivity()
				.getApplicationContext());
		accountMngr.open();
		AccountDAO currAccount = accountMngr.getCurrentAccount();
		accountMngr.close();

		TopicsManager topicManager = new TopicsManager(getActivity()
				.getApplicationContext());
		topicsList = topicManager.getListByAccountId(currAccount.getId());
	}

	public void update() {
		refresh();
		topicsListAdapter.updateList(topicsList);
		topicsListAdapter.notifyDataSetChanged();
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}	
}
