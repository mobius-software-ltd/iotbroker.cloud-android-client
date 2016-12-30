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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.mobius.software.android.iotbroker.mqtt.adapters.AccountsArrayAdapter;
import com.mobius.software.android.iotbroker.mqtt.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.mqtt.base.DaoObject;
import com.mobius.software.android.iotbroker.mqtt.base.WillParcel;
import com.mobius.software.android.iotbroker.mqtt.dal.Accounts;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao.Properties;
import com.mobius.software.android.iotbroker.mqtt.dal.DaoType;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.QoS;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.android.iotbroker.mqtt.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

import de.greenrobot.dao.query.QueryBuilder;

public class LoginActivity extends Activity {

	protected static final String LOG_TAG = "LOG_TAG";
	private List<String> accountsLists;
	private EditText tbx_will;
	private String currentWill;
	private BroadcastReceiver startServiceReceiver;
	private IntentFilter intFilter;

	Context context;

	Boolean activityVisible;
	AlertDialog.Builder accountsDialogBuilder;
	AlertDialog accountDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		startServiceReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(ApplicationSettings.ACTION_CHANNEL_CREATING)) {

					String userName = intent.getStringExtra(ApplicationSettings.PARAM_USERNAME);
					String serverHost = intent.getStringExtra(ApplicationSettings.PARAM_SERVER_HOST);
					String serverHostName = intent.getStringExtra(ApplicationSettings.PARAM_HOSTNAME);

					if (serverHostName == null) {
						serverHostName = serverHost;
					}
					String userPass = intent.getStringExtra(ApplicationSettings.PARAM_PASSWORD);
					String clientID = intent.getStringExtra(ApplicationSettings.PARAM_CLIENTID);

					String cleanSessionStr = intent.getStringExtra(ApplicationSettings.PARAM_ISCLEAN_SESSION);
					Boolean isCleanSession = null;

					if (!cleanSessionStr.equals(null)) {
						isCleanSession = Boolean.parseBoolean(cleanSessionStr);
					}

					Integer keepAlive = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_KEEPALIVE));

					WillParcel will = (WillParcel) intent.getParcelableExtra(WillParcel.class.getCanonicalName());

					Boolean shouldUpdate = Boolean.parseBoolean(intent
							.getStringExtra(ApplicationSettings.PARAM_SHOULD_UPDATE));
					int port = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_PORT));

					channelWasCreated(serverHostName, serverHost, userName, userPass, clientID, isCleanSession,
							keepAlive, will, shouldUpdate, port);
				}

				else if (intent.getAction().equals(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL)) {
					openChanelError();

				}
			}
		};

		intFilter = new IntentFilter(ApplicationSettings.ACTION_CHANNEL_CREATING);
		intFilter.addAction(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL);
		registerReceiver(startServiceReceiver, intFilter);

		tbx_will = (EditText) findViewById(R.id.tbx_will);

		AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(LoginActivity.this, DaoType.AccountsDao));

		List<Accounts> accountsList = accountDao.queryBuilder().where(Properties.IsDefault.eq(1)).list();
		Accounts account = null;

		if (accountsList != null && accountsList.size() == 1) {
			account = accountsList.get(0);
		}

		bindAccountList();

		if (accountsLists.size() > 0)
			showAvailableAccountDialog();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean value = extras.getBoolean("FAILED");

			if (value) {

				MessageDialog.showMessage(this, getString(R.string.connecting_error_title_dialog),
						getString(R.string.action_settings));
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(startServiceReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityVisible = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (accountDialog != null)
			accountDialog.dismiss();

		activityVisible = false;
		if (accountDialog != null)
			accountDialog.dismiss();
	}

	public boolean getActivityVisible() {
		return activityVisible;
	}

	private void bindAccountList() {
		accountsLists = new ArrayList<String>();
		AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(LoginActivity.this, DaoType.AccountsDao));
		List<Accounts> currAccountList = accountDao.queryBuilder().where(Properties.IsDefault.eq(1)).list();

		if (currAccountList != null) {
			for (Accounts account : currAccountList) {
				accountsLists.add(account.getUserName());
			}
		}
	}

	public void showAvailableAccountDialog() {

		accountsDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		accountsDialogBuilder.setTitle(R.string.available_accounts);

		final AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(LoginActivity.this, DaoType.AccountsDao));

		final List<Accounts> defaultAccountsList = accountDao.queryBuilder().where(Properties.IsDefault.eq(1)).list();

		View linearlayout = getLayoutInflater().inflate(R.layout.accounts_list, null);
		accountsDialogBuilder.setView(linearlayout);

		ListView accountsListView = (ListView) linearlayout.findViewById(R.id.accoutsList);

		AccountsArrayAdapter sizeArrayAdapter = new AccountsArrayAdapter(LoginActivity.this, defaultAccountsList);
		accountsListView.setAdapter(sizeArrayAdapter);

		accountsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				QueryBuilder<Accounts> queryBuilder = accountDao.queryBuilder();
				String account = defaultAccountsList.get(position).getUserName();
				String serverHost = defaultAccountsList.get(position).getServerHost();

				queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao.Properties.UserName
						.eq(account));

				queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao.Properties.ServerHost
						.eq(serverHost));

				List<Accounts> currAccountList = queryBuilder.list();
				Accounts choiseAccount;

				if (currAccountList != null && currAccountList.size() > 0) {
					choiseAccount = currAccountList.get(0);

					login(choiseAccount.getServerHost(), choiseAccount.getUserName(), choiseAccount.getPassword(),
							choiseAccount.getClientID(), choiseAccount.getCleanSession() > 0 ? true : false,
							choiseAccount.getKeepAlive(), choiseAccount.getWillTopic(), choiseAccount.getWill(),
							choiseAccount.getIsRetain() > 0 ? true : false, choiseAccount.getQos(), true,
							choiseAccount.getPort());
				}

				accountDialog.dismiss();
				accountDialog.cancel();
			}
		});

		accountDialog = accountsDialogBuilder.create();
		accountDialog.show();

	}

	public void showAddWillDialog() {

		final AlertDialog.Builder inputWillDialog = new AlertDialog.Builder(this);
		inputWillDialog.setTitle(R.string.will_input_dialog_title);

		View linearlayout = getLayoutInflater().inflate(R.layout.input_will_dialog, null);
		inputWillDialog.setView(linearlayout);

		final EditText tbxWill = (EditText) linearlayout.findViewById(R.id.tbx_will);
		tbxWill.setText(currentWill);

		inputWillDialog.setPositiveButton(R.string.will_input_dialog_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				currentWill = tbxWill.getText().toString();
				tbx_will.setText(currentWill);

				dialog.dismiss();
			}
		})

		.setNegativeButton(R.string.will_input_dialog_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		inputWillDialog.create();
		inputWillDialog.show();
	}

	public void btn_login_click(View view) {

		final String errorTitle = getString(R.string.error_message_Title);

		EditText tbxUserName = (EditText) findViewById(R.id.tbx_user_name);
		String username = tbxUserName.getText().toString();

		if (isEmpty(username)) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_user_name));
			return;
		}

		EditText tbxPass = (EditText) findViewById(R.id.tbxpass);
		String userpass = tbxPass.getText().toString();

		if (isEmpty(userpass)) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_password));
			return;
		}

		EditText tbxClientID = (EditText) findViewById(R.id.tbx_client_id);
		String clientID = tbxClientID.getText().toString();

		if (isEmpty(clientID)) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_client_id));
			return;
		}

		EditText tbxServerHost = (EditText) findViewById(R.id.txt_server_host);
		String serverHost = tbxServerHost.getText().toString();

		if (isEmpty(serverHost)) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_server_host));
			return;
		}

		Switch swtrCleanSession = (Switch) findViewById(R.id.swthr_clean_session);
		boolean isCleanSession = swtrCleanSession.isChecked();

		EditText tbx_keep_alive = (EditText) findViewById(R.id.tbx_keep_alive);
		String keepALiveStr = tbx_keep_alive.getText().toString();

		int keepAlive = 0;
		if (!isEmpty(keepALiveStr)) {
			keepAlive = Integer.parseInt(keepALiveStr);
		}

		if (keepAlive < 1) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.keep_alive_must_more_zero));
			return;
		}

		tbx_will = (EditText) findViewById(R.id.tbx_will);
		String willMessage = tbx_will.getText().toString();

		Switch swtrRetain = (Switch) findViewById(R.id.swtch_retain);
		boolean isRetain = swtrRetain.isChecked();

		Spinner spnrQos = (Spinner) findViewById(R.id.tbx_qos);
		int qosNumb = Integer.parseInt(spnrQos.getSelectedItem().toString());

		EditText tbx_will_topic = (EditText) findViewById(R.id.tbx_will_topic);
		String willTopic = tbx_will_topic.getText().toString();

		if (willMessage.length() > 0 && willTopic.length() == 0) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.will_topic_can_not_be_null));
			return;
		}

		EditText txt_server_port = (EditText) findViewById(R.id.txt_server_port);
		String portStr = txt_server_port.getText().toString();

		if (portStr.length() < 0) {
			MessageDialog.showMessage(this, errorTitle, getString(R.string.will_topic_can_not_be_null));
			return;
		}

		int currentPort = Integer.parseInt(portStr);

		login(serverHost, username, userpass, clientID, isCleanSession, keepAlive, willTopic, willMessage, isRetain,
				qosNumb, true, currentPort);
	}

	private void login(String serverHost, String username, String userpass, String clientID, boolean isCleanSession,
			int keepAlive, String willTopic, String willMessage, boolean isRetain, int qosNumb, Boolean updateAccount,
			int port) {

		if (!NetworkManager.hasNetworkAccess(this)) {
			MessageDialog.showMessage(this, getString(R.string.no_network_error),
					getString(R.string.no_network_error_message));
			return;
		}

		Will will = null;
		if (willMessage != null && willMessage.length() > 0) {
			will = new Will();
			will.setContent(willMessage.getBytes());
			will.setRetain(isRetain);
			Topic willTopicData = new Topic();
			willTopicData.setName(new Text(willTopic));
			willTopicData.setQos(QoS.valueOf(qosNumb));
			will.setTopic(willTopicData);
		}
		ConnectThread connectThread = new ConnectThread(this, serverHost, username, userpass, clientID, isCleanSession,
				keepAlive, will, updateAccount, port);

		connectThread.execute(new Object[] { serverHost, username, userpass, clientID, isCleanSession, keepAlive,
				willTopic, willMessage, isRetain, qosNumb, updateAccount, port });
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void showAddWill(View view) {
		showAddWillDialog();
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	private void showMessage(final Integer title, final Integer messsage) {
		final Activity activity = this;
		this.runOnUiThread(new Runnable() {
			public void run() {
				MessageDialog.showMessage(activity, getString(title), getString(messsage));
			}
		});
	}

	private void openChanelError() {
		showMessage(R.string.error_opening_channel_title, R.string.error_opening_channel_message);
	}

	private void channelWasCreated(String hostname, String serverHost, String username, String userpass,
			String clientID, Boolean isCleanSession, Integer keepAlive, Will will, Boolean shouldUpdate, int port) {

		if (shouldUpdate) {
			AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(this, DaoType.AccountsDao));

			try {

				QueryBuilder<Accounts> queryBuilder = accountDao.queryBuilder();

				queryBuilder.where(Properties.UserName.eq(username));
				queryBuilder.where(Properties.UserName.eq(hostname));

				List<Accounts> accountsList = queryBuilder.list();
				Accounts account = null;

				boolean existUserHost = false;
				if (accountsList != null && accountsList.size() > 0) {
					existUserHost = true;
					account = accountsList.get(0);
				}

				if (existUserHost) {

					account.setPassword(userpass);
					account.setClientID(clientID);
					account.setCleanSession(isCleanSession ? 1 : 0);
					account.setKeepAlive(keepAlive);

					if (will == null) {
						accountDao.update(account);
					}
					else {

						account.setWill(null);
						account.setWillTopic(null);
						account.setIsRetain(0);
						account.setQos(null);

						accountDao.update(account);
					}
				}
				else {

					if (will != null) {

						int cleanSessionInt = isCleanSession ? 1 : 0;
						int qosInt = will.getTopic().getQos().getValue();
						int isRetainInt = will.getRetain() ? 1 : 0;
						Accounts looginAccount = new Accounts(null, username, userpass, clientID, hostname,
								cleanSessionInt, keepAlive, new String(will.getContent()), will.getTopic().getName()
										.toString(), isRetainInt, qosInt, 1, port);

						accountDao.insert(looginAccount);

					}
					else {
						Accounts loginAccount = new Accounts(null, username, userpass, clientID, hostname,
								isCleanSession ? 1 : 0, keepAlive, null, null, 0, 1, 0, port);

						accountDao.insert(loginAccount);
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		setResult(RESULT_OK);
		finish();

	}

	private static class ConnectThread extends AsyncTask<Object, Object, Object> {
		private LoginActivity activity;
		private String hostname, username, password, clientID;
		private Boolean cleanSession;
		private Integer keepalive;
		private Will will;
		private Boolean shouldUpdate;
		private int port;

		public ConnectThread(LoginActivity activity, String serverHost, String username, String userpass,
				String clientID, Boolean isCleanSession, Integer keepAlive, Will will, Boolean shouldUpdate, int port) {
			this.activity = activity;
			this.hostname = serverHost;
			this.username = username;
			this.password = userpass;
			this.clientID = clientID;
			this.cleanSession = isCleanSession;
			this.will = will;
			this.keepalive = keepAlive;
			this.shouldUpdate = shouldUpdate;
			this.port = port;
		}

		@Override
		protected void onPostExecute(Object result) {
		}

		@Override
		protected String doInBackground(Object... param) {

			@SuppressWarnings("unused")
			InetSocketAddress address = null;
			try {
				address = InetSocketAddress.createUnresolved(hostname, port);
			}
			catch (Exception ex) {
				activity.showMessage(R.string.error_opening_channel_title, R.string.error_opening_channel_message);
				return "";
			}

			Intent startServiceIntent = new Intent(activity, NetworkService.class);
			startServiceIntent.putExtra(ApplicationSettings.PARAM_SERVER_HOST, hostname);
			startServiceIntent.putExtra(ApplicationSettings.PARAM_PORT, Integer.toString(port));
			startServiceIntent.putExtra(ApplicationSettings.PARAM_USERNAME, username);
			startServiceIntent.putExtra(ApplicationSettings.PARAM_PASSWORD, password);
			startServiceIntent.putExtra(ApplicationSettings.PARAM_CLIENTID, clientID);

			String cleanSessionStr = Boolean.toString(false);

			if (cleanSession)
				cleanSessionStr = Boolean.toString(true);

			startServiceIntent.putExtra(ApplicationSettings.PARAM_ISCLEAN_SESSION, cleanSessionStr);
			startServiceIntent.putExtra(ApplicationSettings.PARAM_KEEPALIVE, keepalive.toString());
			startServiceIntent.putExtra(ApplicationSettings.PARAM_SHOULD_UPDATE, shouldUpdate.toString());

			if (will != null) {
				WillParcel willParcel = new WillParcel(will);
				startServiceIntent.putExtra(WillParcel.class.getCanonicalName(), willParcel);
			}

			startServiceIntent.setAction(ApplicationSettings.ACTION_CHANNEL_CREATING);
			activity.startService(startServiceIntent);

			return "";
		}
	}
}
