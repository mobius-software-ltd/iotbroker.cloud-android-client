package com.mobius.software.android.iotbroker.mqtt.activity;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.listeners.StatusChangedListener;
import com.mobius.software.android.iotbroker.mqtt.managers.AppBroadcastManager;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectionState;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class LoadingActivity extends Activity implements StatusChangedListener {

	ProgressBar progresLoading;
	String infoMessage;

	final int max = 100;
	TextView tbxLoadingInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_loading);
		
		progresLoading = (ProgressBar) findViewById(R.id.loading_progres_bar);
		progresLoading.setMax(max);
		progresLoading.setProgress(0);

		tbxLoadingInfo = (TextView) findViewById(R.id.tbx_loading_info);
	}

	@Override
	public void onResume() {
		super.onResume();
		checkState();
	}

	private void checkState() {
		ConnectionState currentState = NetworkService.getStatus();
		statusChanged(currentState);
	}

	@Override
	public void statusChanged(ConnectionState currentState) {
		Runnable stateRunnable = new StatusChangedRunnable(this, currentState);
		this.runOnUiThread(stateRunnable);
	}
	
	@Override
	public void onBackPressed() {		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_CANCELED)
			finish();
		
		if(!NetworkManager.hasNetworkAccess(this))
		{
			progresLoading.setProgress(0);
			infoMessage = getResources().getString(R.string.no_network_access);
			tbxLoadingInfo.setText(infoMessage);
		}
	}

	public class StatusChangedRunnable implements Runnable {
		private ConnectionState currentState;
		private LoadingActivity activity;

		public StatusChangedRunnable(LoadingActivity activity,
				ConnectionState state) {
			this.activity = activity;
			this.currentState = state;
		}

		public void run() {
			if (!NetworkService.hasInstance())
				startService(new Intent(activity, NetworkService.class));

			Intent intent = null;
			Integer requestCode=null;	
			if(!NetworkManager.hasNetworkAccess(activity))
			{
				progresLoading.setProgress(0);
				infoMessage = getResources().getString(R.string.no_network_access);
				tbxLoadingInfo.setText(infoMessage);
			}
			else
			{
				if (currentState == ConnectionState.NONE) {
					requestCode=1;
					intent = new Intent(activity, LoginActivity.class);
				} else if (currentState == ConnectionState.CONNECTION_ESTABLISHED) {
					requestCode=2;
					intent = new Intent(activity, TopicsMessagesActivity.class);
				}
				else {
					switch (currentState) {
					case CHANNEL_CREATING:					
						progresLoading.setProgress(0);
						infoMessage = getResources().getString(
								R.string.loading_creating_channel);
						tbxLoadingInfo.setText(infoMessage);
						break;
					case CHANNEL_ESTABLISHED:
						progresLoading.setProgress(33);
						infoMessage = getResources().getString(
								R.string.loading_channel_opened);
						tbxLoadingInfo.setText(infoMessage);
						break;
					case CHANNEL_FAILED:
						AccountManager manager = new AccountManager(activity);
						manager.open();
						manager.changeIsDefaultForActiveUser(false);
						manager.close();
						
						requestCode=1;						
						intent = new Intent(activity, LoginActivity.class);	
						intent.putExtra("FAILED", true);
						break;
					case CONNECTING:
						progresLoading.setProgress(66);
						infoMessage = getResources().getString(
								R.string.loading_connecting);
						tbxLoadingInfo.setText(infoMessage);
						break;
					case CONNECTION_FAILED:
						manager = new AccountManager(activity);
						manager.open();
						manager.changeIsDefaultForActiveUser(false);
						manager.close();
						
						requestCode=1;
						intent = new Intent(activity, LoginActivity.class);	
						intent.putExtra("FAILED", true);
						break;
					case CONNECTION_LOST:
						progresLoading.setProgress(0);
						infoMessage = getResources().getString(
								R.string.loading_connection_lost);
						tbxLoadingInfo.setText(infoMessage);
						if(NetworkManager.hasNetworkAccess(activity) && !NetworkService.reactivate())
						{						
							if (NetworkService.hasInstance())								
							{
								requestCode=1;
								intent = new Intent(activity, LoginActivity.class);	
								intent.putExtra("FAILED", true);
							}
						}
						break;
					default:
						break;
					}
				}
			}
			
			if (intent != null) {
				AppBroadcastManager.setStatusChangedListener(null);
				startActivityForResult(intent, requestCode);				
			} else
				AppBroadcastManager.setStatusChangedListener(activity);
		}
	}

	@Override
	public void networkUp() {
		Intent intent = null;
		int requestCode=1;
		intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, requestCode);	
	}	
}
