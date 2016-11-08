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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.android.iotbroker.mqtt.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

public class SendMessageFragment extends Fragment {

	private EditText tbxContent;
	private EditText tbxTopic;
	private Spinner spnrQos;
	private Switch swtchRetain;
	private Switch swtchDublicate;
	private String currentContentValue;
	private EditText tbxInputContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		
		return inflater.inflate(R.layout.activity_send_message, container,
				false);
	}

	private void cleanSendMessagheForm() {
		tbxContent.setText("");
		tbxTopic.setText("");
		swtchRetain.setChecked(false);
		swtchDublicate.setChecked(false);
		spnrQos.setSelection(0);
	
		currentContentValue = "";
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		tbxContent = (EditText) view.findViewById(R.id.tbx_content);
		tbxTopic = (EditText) view.findViewById(R.id.tbx_topic);
		spnrQos = (Spinner) view.findViewById(R.id.spnr_qos);
		swtchRetain = (Switch) view.findViewById(R.id.swtr_retain);
		swtchDublicate = (Switch) view.findViewById(R.id.swtch_dublicate);

		currentContentValue = "";
		
		Button btnSendMessage = (Button) view
				.findViewById(R.id.btn_send_message);
		btnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String errorTitle = getActivity().getString(
						R.string.sm_error_dialog_title);

				String content = tbxContent.getText().toString();
				if (isEmpty(content)) {
					MessageDialog.showMessage(
							getActivity(),
							errorTitle,
							getActivity().getString(
									R.string.sm_error_content_required));

					return;
				}

				String topic = tbxTopic.getText().toString();
				if (isEmpty(topic)) {
					MessageDialog.showMessage(
							getActivity(),
							errorTitle,
							getActivity().getString(
									R.string.sm_error_topic_required));
					return;
				}

				int qos = Integer
						.parseInt(spnrQos.getSelectedItem().toString());

				String packedId = null;

				boolean isRetain = swtchRetain.isChecked();
				boolean isDublicate = swtchDublicate.isChecked();

				SendMessage(content, topic, qos, packedId, isRetain,
						isDublicate);

			}
		});

		EditText tbxTopic = (EditText) view.findViewById(R.id.tbx_content);

		tbxTopic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAddContnentDialog();
			}
		});	
	
	}

	private void SendMessage(String content, String topic, int qos,
			String packedId, boolean isRetain, boolean isDublicate) {

		if (!NetworkManager.hasNetworkAccess(getActivity())) {
			MessageDialog.showMessage(getActivity(),
					getString(R.string.no_network_error),
					getString(R.string.no_network_error_message));
			return;
		}

		NetworkService.publish(content, topic, qos, packedId, isRetain,
				isDublicate);
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	public void showAddContnentDialog() {

		final AlertDialog.Builder inputContentDialog = new AlertDialog.Builder(
				getActivity());
		inputContentDialog.setTitle(R.string.content_input_dialog_title);

		View linearlayout = getActivity().getLayoutInflater().inflate(
				R.layout.input_content_dialog, null);

		inputContentDialog.setView(linearlayout);

		tbxInputContent = (EditText) linearlayout
				.findViewById(R.id.tbx_content);

		tbxInputContent.setText(currentContentValue);

		inputContentDialog.setPositiveButton(
				R.string.content_input_dialog_btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						currentContentValue = tbxInputContent.getText()
								.toString();
						tbxContent.setText(currentContentValue);
						dialog.dismiss();
					}
				})

		.setNegativeButton(R.string.content_input_dialog_btn_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		inputContentDialog.create();
		inputContentDialog.show();
	}

	public void showAddContent(View v) {
		showAddContnentDialog();
	}

	public void update() {
		MessageDialog.showMessage(getActivity(),
				getString(R.string.sm_dialog_mewss_was_subsribed),
				getString(R.string.sm_dialog_succes_text));

		cleanSendMessagheForm();
	}
}
