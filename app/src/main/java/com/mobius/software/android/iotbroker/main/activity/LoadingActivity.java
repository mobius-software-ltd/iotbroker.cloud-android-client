package com.mobius.software.android.iotbroker.main.activity;

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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.base.DaoObject;
import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao;
import com.mobius.software.android.iotbroker.main.dal.DaoType;
import com.mobius.software.android.iotbroker.main.listeners.StatusChangedListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;
import com.mobius.software.android.iotbroker.main.managers.NetworkManager;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class LoadingActivity extends Activity implements StatusChangedListener {

	private ProgressBar progresLoading;
	private String infoMessage;

	private final int max = 100;
	private TextView tbxLoadingInfo;

	private BroadcastReceiver stReceiver;
	private IntentFilter intFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_loading);

		progresLoading = (ProgressBar) findViewById(R.id.loading_progres_bar);
		progresLoading.setMax(max);
		progresLoading.setProgress(0);

		tbxLoadingInfo = (TextView) findViewById(R.id.tbx_loading_info);

		stReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equalsIgnoreCase(ApplicationSettings.NETWORK_CHANGED)) {
					String status = intent.getStringExtra("status");
					statusChanged(Enum.valueOf(ConnectionState.class, status));
				}
				else if (intent.getAction().equalsIgnoreCase(ApplicationSettings.NETWORK_UP)) {
					networkUp();
				}
				else if (intent.getAction().equals(ApplicationSettings.NETWORK_STATUS_CHANGE)) {
					String status = intent.getStringExtra("status");
					statusChanged(Enum.valueOf(ConnectionState.class, status));
				}
			}
		};

		intFilter = new IntentFilter(ApplicationSettings.ACTION_MESSAGE_RECEIVED);
		intFilter.addAction(ApplicationSettings.NETWORK_UP);
		intFilter.addAction(ApplicationSettings.NETWORK_CHANGED);
		intFilter.addAction(ApplicationSettings.NETWORK_STATUS_CHANGE);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(stReceiver, intFilter);
		checkState();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(stReceiver);
	};

	@Override
	public void networkUp() {
		Intent intent;
		int requestCode = 1;
		intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, requestCode);
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
		if (resultCode == RESULT_CANCELED)
			finish();

		if (!NetworkManager.hasNetworkAccess(this)) {
			progresLoading.setProgress(0);
			infoMessage = getResources().getString(R.string.no_network_access);
			tbxLoadingInfo.setText(infoMessage);
		}
	}

	public class StatusChangedRunnable implements Runnable {
		private ConnectionState currentState;
		private LoadingActivity activity;

		public StatusChangedRunnable(LoadingActivity activity, ConnectionState state) {
			this.activity = activity;
			this.currentState = state;
		}

		public void run() {
			startService(new Intent(activity, NetworkService.class));

			Intent intent = null;
			Integer requestCode = null;

			if (!NetworkManager.hasNetworkAccess(activity)) {
				progresLoading.setProgress(0);
				infoMessage = getResources().getString(R.string.no_network_access);
				tbxLoadingInfo.setText(infoMessage);
			} else {

				if (currentState == ConnectionState.NONE) {
					requestCode = 1;
					intent = new Intent(activity, LoginActivity.class);
				} else if (currentState == ConnectionState.CONNECTION_ESTABLISHED) {
					requestCode = 2;
					intent = new Intent(activity, TopicsMessagesActivity.class);
				} else {

					AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(LoadingActivity.this, DaoType.AccountsDao));
					List<Accounts> accountsList;
					Accounts account;

					switch (currentState) {

						case CHANNEL_CREATING:
						{
							progresLoading.setProgress(0);
							infoMessage = getResources().getString(R.string.loading_creating_channel);
							tbxLoadingInfo.setText(infoMessage);
						} break;

						case CHANNEL_ESTABLISHED:
						{
							progresLoading.setProgress(33);
							infoMessage = getResources().getString(R.string.loading_channel_opened);
							tbxLoadingInfo.setText(infoMessage);
						} break;

						case CHANNEL_FAILED:
						{
							accountsList = accountDao
									.queryBuilder()
									.where(com.mobius.software.android.iotbroker.main.dal.AccountsDao.Properties.IsDefault
											.eq(true)).list();

							if (accountsList != null && accountsList.size() > 0) {
								account = accountsList.get(0);
								account.setIsDefault(false);
								account.update();
							}

							requestCode = 1;
							intent = new Intent(activity, LoginActivity.class);
							intent.putExtra("FAILED", true);
						} break;

						case CONNECTING:
						{
							progresLoading.setProgress(66);
							infoMessage = getResources().getString(R.string.loading_connecting);
							tbxLoadingInfo.setText(infoMessage);
						} break;

						case CONNECTION_FAILED:
						{
							accountsList = accountDao
									.queryBuilder()
									.where(com.mobius.software.android.iotbroker.main.dal.AccountsDao.Properties.IsDefault
											.eq(true)).list();

							if (accountsList != null && accountsList.size() > 0) {
								account = accountsList.get(0);
								account.setIsDefault(false);
								account.update();
							}

							requestCode = 1;
							intent = new Intent(activity, LoginActivity.class);
							intent.putExtra("FAILED", true);
						} break;

						case CONNECTION_LOST:
						{
							progresLoading.setProgress(0);
							infoMessage = getResources().getString(R.string.loading_connection_lost);
							tbxLoadingInfo.setText(infoMessage);
							if (NetworkManager.hasNetworkAccess(activity) && !NetworkService.reactivate()) {

								requestCode = 1;
								intent = new Intent(activity, LoginActivity.class);
								intent.putExtra("FAILED", true);

							}
						} break;
					}
				}
			}

			if (intent != null) {
				startActivityForResult(intent, requestCode);
			}
		}
	}

	private void checkState() {
		ConnectionState currentState = NetworkService.getStatus();
		statusChanged(currentState);
	}
}
