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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.base.DaoObject;
import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao;
import com.mobius.software.android.iotbroker.main.dal.DaoType;
import com.mobius.software.android.iotbroker.main.fragments.MessagesListFragment;
import com.mobius.software.android.iotbroker.main.fragments.SendMessageFragment;
import com.mobius.software.android.iotbroker.main.fragments.TopicsListFragment;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class TopicsMessagesActivity extends Activity {

	private Fragment tlFragment, smFragment, mlFragment;
	FragmentManager fragmentManager;

	private final String[] TAB_TAGS = new String[] { "tl", "sm", "ml" };

	private BroadcastReceiver messagesReceiver;
	IntentFilter intFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topics_messages);

		fragmentManager = getFragmentManager();
		tlFragment = new TopicsListFragment();
		smFragment = new SendMessageFragment();
		mlFragment = new MessagesListFragment();
		tab_tl_click(findViewById(R.id.fragment_container));

		messagesReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equalsIgnoreCase(ApplicationSettings.ALERT_MESSAGE)) {
					final String content = intent.getStringExtra(ApplicationSettings.PARAM_CONTENT);

					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder builder;
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
								builder = new AlertDialog.Builder(TopicsMessagesActivity.this, android.R.style.Theme_Material_Light_Dialog);
							} else {
								builder = new AlertDialog.Builder(TopicsMessagesActivity.this);
							}
							builder.setTitle("Attention")
									.setMessage(content)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						}
					});

				} else if (intent.getAction().equals(ApplicationSettings.ACTION_MESSAGE_RECEIVED)) {
					Integer messageType = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_MESSAGETYPE));
					messageReceived(messageType);
				}
				else if (intent.getAction().equalsIgnoreCase(ApplicationSettings.NETWORK_DOWN)) {
					networkDown();
				}

				else if (intent.getAction().equalsIgnoreCase(ApplicationSettings.NETWORK_CHANGED)) {
					networkDown();
				}

			}

		};

		intFilter = new IntentFilter(ApplicationSettings.ACTION_MESSAGE_RECEIVED);
		intFilter.addAction(ApplicationSettings.ALERT_MESSAGE);
		intFilter.addAction(ApplicationSettings.NETWORK_DOWN);
		intFilter.addAction(ApplicationSettings.NETWORK_CHANGED);
		registerReceiver(messagesReceiver, intFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent startServiceIntent = new Intent(TopicsMessagesActivity.this, NetworkService.class);
		startServiceIntent.putExtra(ApplicationSettings.PARAM_IS_VISIBLE, Boolean.toString(true));
		startServiceIntent.setAction(ApplicationSettings.ACTION_CHANGE_TMACTIVITY_VISIBLE);
		startService(startServiceIntent);

		registerReceiver(messagesReceiver, intFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Intent startServiceIntent = new Intent(TopicsMessagesActivity.this, NetworkService.class);
		startServiceIntent.putExtra(ApplicationSettings.PARAM_IS_VISIBLE, Boolean.toString(false));
		startServiceIntent.setAction(ApplicationSettings.ACTION_CHANGE_TMACTIVITY_VISIBLE);
		startService(startServiceIntent);

		unregisterReceiver(messagesReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.topics_list, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.btn_logout:

			AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(TopicsMessagesActivity.this, DaoType.AccountsDao));
			Accounts account;
			List<Accounts> accountsList = accountDao.queryBuilder()
					.where(com.mobius.software.android.iotbroker.main.dal.AccountsDao.Properties.IsDefault.eq(1))
					.list();

			if (accountsList != null && accountsList.size() > 0) {
				account = accountsList.get(0);
				account.setIsDefault(false);
				account.update();
			}

			Intent startServiceIntent = new Intent(TopicsMessagesActivity.this, NetworkService.class);
			startServiceIntent.setAction(ApplicationSettings.ACTION_DEACTIVATE_SERVICE);
			startService(startServiceIntent);

			setResult(RESULT_OK);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void messageReceived(Integer valueOf) {

		if (valueOf == MessageType.SUBACK.getNum() || valueOf == MessageType.UNSUBACK.getNum()) {
			if (fragmentManager.findFragmentByTag(TAB_TAGS[0]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((TopicsListFragment) tlFragment).update();
					}
				});
			}

		} else if (valueOf == MessageType.PUBACK.getNum()) {
			if (fragmentManager.findFragmentByTag(TAB_TAGS[1]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((SendMessageFragment) smFragment).update();
					}
				});
			}

		} else if (valueOf == MessageType.PUBCOMP.getNum()) {
			if (fragmentManager.findFragmentByTag(TAB_TAGS[1]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((SendMessageFragment) smFragment).update();
					}
				});
			}
			else {
				Intent startServiceIntent = new Intent(TopicsMessagesActivity.this, NetworkService.class);
				startServiceIntent.setAction(ApplicationSettings.ACTION_SHOW_NOTIFICATION);
				startService(startServiceIntent);
			}
		}
	}

	public void tab_tl_click(View view) {
		setTitle(R.string.tsm_title_topics_list);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, tlFragment, TAB_TAGS[0]);
		fragmentTransaction.commit();

		changeImageForselectedTab(0);
	}

	public void tab_sm_click(View view) {
		setTitle(R.string.tsm_title_send_message);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, smFragment, TAB_TAGS[1]);
		fragmentTransaction.commit();

		changeImageForselectedTab(1);
	}

	public void tab_ml_click(View view) {
		setTitle(R.string.tsm_title_messages_list);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, mlFragment, TAB_TAGS[2]);
		fragmentTransaction.commit();
		changeImageForselectedTab(2);
	}

	private void changeImageForselectedTab(int selectedTabIndex) {

		TextView tbx_tl = (TextView) findViewById(R.id.tbx_tl);
		TextView tbx_sm = (TextView) findViewById(R.id.tbx_sm);
		TextView tbx_ml = (TextView) findViewById(R.id.tbx_ml);

		switch (selectedTabIndex) {
		case 0: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_tl_selected, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_sm, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_ml, 0, 0);
			break;
		}
		case 1: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_tl, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_sm_selected, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_ml, 0, 0);
			break;
		}
		case 2: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_tl, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_sm, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tab_ml_selected, 0, 0);
			break;
		}
		}

	}

	private void networkDown() {
		setResult(RESULT_OK);
		finish();
	}
}
