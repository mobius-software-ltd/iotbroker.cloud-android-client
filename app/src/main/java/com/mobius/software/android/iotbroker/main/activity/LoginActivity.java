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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.mobius.software.android.iotbroker.main.adapters.AccountsArrayAdapter;
import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.base.ClientInfoParcel;
import com.mobius.software.android.iotbroker.main.base.DaoObject;
import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao.Properties;
import com.mobius.software.android.iotbroker.main.dal.DaoType;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.managers.NetworkManager;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.android.iotbroker.main.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;
import java.net.InetSocketAddress;
import java.util.List;
import de.greenrobot.dao.query.QueryBuilder;
import de.mxapplications.openfiledialog.OpenFileDialog;

public class LoginActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private EditText tbx_will;
	private String currentWill;
	private BroadcastReceiver startServiceReceiver;

	Boolean activityVisible;
	AlertDialog.Builder accountsDialogBuilder;
	AlertDialog accountDialog;

	private Switch isSecure;
	private EditText editSecureKey;
	private LinearLayout secureKeyCell;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Spinner spinnerProtocolType = (Spinner) findViewById(R.id.tbx_protocol_type);
		spinnerProtocolType.setOnItemSelectedListener(this);

		isSecure = (Switch) findViewById(R.id.swtch_secure_connection);
		isSecure.setOnCheckedChangeListener(this);
		findViewById(R.id.security_cell).setVisibility(View.GONE);
		findViewById(R.id.secure_key_cell).setVisibility(View.GONE);
		findViewById(R.id.secure_key_pass_cell).setVisibility(View.GONE);

		secureKeyCell = (LinearLayout) findViewById(R.id.secure_key_cell);
		secureKeyCell.setOnClickListener(this);

		editSecureKey = (EditText) findViewById(R.id.edit_secure_key);

		startServiceReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(ApplicationSettings.ACTION_CHANNEL_CREATING)) {

					ClientInfoParcel clientInfo = intent.getParcelableExtra(ClientInfoParcel.class.getCanonicalName());
					channelWasCreated(clientInfo.getProtocol().getValue(), clientInfo.getHost(), clientInfo.getUsername(),
							clientInfo.getPassword(), clientInfo.getClientId(), clientInfo.isCleanSession(), clientInfo.getKeepalive(), clientInfo.getWill(),
							clientInfo.isShouldUpdate(), clientInfo.getPort(), clientInfo.getSecure(), clientInfo.getCertificatePath(), clientInfo.getCertificatePassword());
				} else if (intent.getAction().equals(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL)) {
					openChanelError();
				}
			}
		};

		IntentFilter intFilter = new IntentFilter(ApplicationSettings.ACTION_CHANNEL_CREATING);
		intFilter.addAction(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL);
		registerReceiver(startServiceReceiver, intFilter);

		tbx_will = (EditText) findViewById(R.id.tbx_will);

		showAvailableAccountDialog();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean value = extras.getBoolean("FAILED");
			if (value) {
				MessageDialog.showMessage(this, getString(R.string.connecting_error_title_dialog), getString(R.string.action_settings));
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

	public void showAvailableAccountDialog() {

		accountsDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		accountsDialogBuilder.setTitle(R.string.available_accounts);

		final AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(LoginActivity.this, DaoType.AccountsDao));

		final List<Accounts> defaultAccountsList = accountDao.queryBuilder().list();//where(Properties.IsDefault.eq(true)).list();

		if (defaultAccountsList.size() <= 0) {
			return;
		}

		View linearlayout = getLayoutInflater().inflate(R.layout.accounts_list, null);
		accountsDialogBuilder.setView(linearlayout);

		ListView accountsListView = (ListView) linearlayout.findViewById(R.id.accoutsList);

		AccountsArrayAdapter sizeArrayAdapter = new AccountsArrayAdapter(LoginActivity.this, defaultAccountsList);
		accountsListView.setAdapter(sizeArrayAdapter);

		accountsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				QueryBuilder<Accounts> queryBuilder = accountDao.queryBuilder();
				String account = defaultAccountsList.get(position).getClientID();
				String serverHost = defaultAccountsList.get(position).getServerHost();

				queryBuilder.where(AccountsDao.Properties.ClientID.eq(account));
				queryBuilder.where(AccountsDao.Properties.ServerHost.eq(serverHost));

				List<Accounts> currAccountList = queryBuilder.list();
				Accounts choiseAccount;

				if (currAccountList != null && currAccountList.size() > 0) {
					choiseAccount = currAccountList.get(0);
					login(choiseAccount.getProtocolType(), 	choiseAccount.getServerHost(), 	choiseAccount.getUserName(),
							choiseAccount.getPassword(), 	choiseAccount.getClientID(), 	choiseAccount.getCleanSession(),
							choiseAccount.getKeepAlive(), 	choiseAccount.getWillTopic(), 	choiseAccount.getWill(),
							choiseAccount.getIsRetain(), 	choiseAccount.getQos(), 		true,
							choiseAccount.getPort(), 		choiseAccount.getIsSecureConnection(), choiseAccount.getCertificatePath(),
							choiseAccount.getCertificatePassword());
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
		}).setNegativeButton(R.string.will_input_dialog_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		inputWillDialog.create();
		inputWillDialog.show();
	}

	public void btn_login_click(View view) {

		Spinner spnrProtocolType = (Spinner) findViewById(R.id.tbx_protocol_type);
		int protocolTypeIndex = spnrProtocolType.getSelectedItemPosition();
		String username = "";
		String password = "";
		String clientID = "";
		String serverHost = "";
		int currentPort = 0;
		boolean isCleanSession = false;
		int keepAlive = 0;
		String willMessage = "";
		String willTopic = "";
		boolean isRetain = false;
		int qos = 0;
		boolean isSecure = false;
		String certificatePath = "";
		String certificatePassword = "";

		final String errorTitle = getString(R.string.error_message_Title);

		if (findViewById(R.id.username_cell).getVisibility() == View.VISIBLE) {
			EditText tbxUserName = (EditText) findViewById(R.id.tbx_user_name);
			username = tbxUserName.getText().toString();

			if (isEmpty(username)) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_user_name));
				return;
			}
		}

		if (findViewById(R.id.password_cell).getVisibility() == View.VISIBLE) {
			EditText tbxPass = (EditText) findViewById(R.id.tbxpass);
			password = tbxPass.getText().toString();

			if (isEmpty(password)) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_password));
				return;
			}
		}

		if (findViewById(R.id.client_id_cell).getVisibility() == View.VISIBLE) {
			EditText tbxClientID = (EditText) findViewById(R.id.tbx_client_id);
			clientID = tbxClientID.getText().toString();

			if (isEmpty(clientID)) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_client_id));
				return;
			}
		}

		if (findViewById(R.id.host_cell).getVisibility() == View.VISIBLE) {
			EditText tbxServerHost = (EditText) findViewById(R.id.txt_server_host);
			serverHost = tbxServerHost.getText().toString();

			if (isEmpty(serverHost)) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.error_required_server_host));
				return;
			}
		}

		if (findViewById(R.id.port_cell).getVisibility() == View.VISIBLE) {
			EditText txt_server_port = (EditText) findViewById(R.id.txt_server_port);
			String portString = txt_server_port.getText().toString();

			if (portString.length() < 0) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.will_topic_can_not_be_null));
				return;
			}
			currentPort = Integer.parseInt(portString);
		}

		if (findViewById(R.id.clean_session_cell).getVisibility() == View.VISIBLE) {
			Switch swtrCleanSession = (Switch) findViewById(R.id.swthr_clean_session);
			isCleanSession = swtrCleanSession.isChecked();
		}

		if (findViewById(R.id.keepalive_cell).getVisibility() == View.VISIBLE) {
			EditText tbx_keep_alive = (EditText) findViewById(R.id.tbx_keep_alive);
			String keepAliveString = tbx_keep_alive.getText().toString();
			keepAlive = 0;

			if (!isEmpty(keepAliveString)) {
				keepAlive = Integer.parseInt(keepAliveString);
			}

			if (keepAlive < 1) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.keep_alive_must_more_zero));
				return;
			}
		}

		if (findViewById(R.id.will_cell).getVisibility() == View.VISIBLE) {
			tbx_will = (EditText) findViewById(R.id.tbx_will);
			willMessage = tbx_will.getText().toString();
		}

		if (findViewById(R.id.will_topic_cell).getVisibility() == View.VISIBLE) {
			EditText tbx_will_topic = (EditText) findViewById(R.id.tbx_will_topic);
			willTopic = tbx_will_topic.getText().toString();
		}

		if (findViewById(R.id.retain_cell).getVisibility() == View.VISIBLE) {
			Switch swtrRetain = (Switch) findViewById(R.id.swtch_retain);
			isRetain = swtrRetain.isChecked();
		}

		if (findViewById(R.id.qos_cell).getVisibility() == View.VISIBLE) {
			Spinner spnrQos = (Spinner) findViewById(R.id.tbx_qos);
			qos = Integer.parseInt(spnrQos.getSelectedItem().toString());
		}

		if (findViewById(R.id.secure_connection_cell).getVisibility() == View.VISIBLE) {
			Switch swtrIsSecure = (Switch) findViewById(R.id.swtch_secure_connection);
			isSecure = swtrIsSecure.isChecked();
		}

		if (findViewById(R.id.secure_key_cell).getVisibility() == View.VISIBLE) {
			EditText etCertificatePath = (EditText) findViewById(R.id.edit_secure_key);
			certificatePath = etCertificatePath.getText().toString();
		}

		if (findViewById(R.id.secure_key_pass_cell).getVisibility() == View.VISIBLE) {
			EditText eyCertificatePassword = (EditText) findViewById(R.id.edit_text_key_key);
			certificatePassword = eyCertificatePassword.getText().toString();
		}

		if (findViewById(R.id.will_cell).getVisibility() == View.VISIBLE && findViewById(R.id.will_topic_cell).getVisibility() == View.VISIBLE) {
			if (willMessage.length() > 0 && willTopic.length() == 0) {
				MessageDialog.showMessage(this, errorTitle, getString(R.string.will_topic_can_not_be_null));
				return;
			}
		}

		login(protocolTypeIndex, serverHost, username, password, clientID, isCleanSession, keepAlive, willTopic, willMessage, isRetain, qos, true, currentPort, isSecure, certificatePath, certificatePassword);
	}

	private void login(int protocol, String serverHost, String username, String password, String clientID, boolean isCleanSession,
					   int keepAlive, String willTopic, String willMessage, boolean isRetain, int qosNumb, Boolean updateAccount, int port,
					   boolean isSecure, String crtPath, String crtPassword) {

		if (!NetworkManager.hasNetworkAccess(this)) {
			MessageDialog.showMessage(this, getString(R.string.no_network_error), getString(R.string.no_network_error_message));
			return;
		}

		Will will = null;
		if (willMessage != null && willMessage.length() > 0) {
			will = new Will();
			will.setContent(willMessage.getBytes());
			will.setRetain(isRetain);
			MQTopic willTopicData = new MQTopic();
			willTopicData.setName(new Text(willTopic));
			willTopicData.setQos(QoS.valueOf(qosNumb));
			will.setTopic(willTopicData);
		}

		ConnectThread connectThread = new ConnectThread(this, protocol, serverHost, username, password, clientID, isCleanSession, keepAlive, will, updateAccount, port, isSecure, crtPath, crtPassword);
		connectThread.execute(protocol, serverHost, username, password, clientID, isCleanSession, keepAlive, willTopic, willMessage, isRetain, qosNumb, updateAccount, port);
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
		return str == null || str.length() == 0;
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

	private void channelWasCreated(Integer protocol, String hostname, String username, String userpass,
								   String clientID, Boolean isCleanSession, Integer keepAlive, Will will, Boolean shouldUpdate, int port,
								   Boolean isSecure, String certificatePath, String certificatePassword) {

		if (shouldUpdate) {
			AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(this, DaoType.AccountsDao));

			try {

				QueryBuilder<Accounts> queryBuilder = accountDao.queryBuilder();
				queryBuilder.where(Properties.ClientID.eq(clientID));
				queryBuilder.where(Properties.ServerHost.eq(hostname));

				List<Accounts> accountsList = queryBuilder.list();
				Accounts account = null;

				boolean existUserHost = false;
				if (accountsList.size() > 0) {
					existUserHost = true;
					account = accountsList.get(0);
					account.setIsDefault(true);
				}

				if (existUserHost) {

					account.setPassword(userpass);
					account.setClientID(clientID);
					account.setCleanSession(isCleanSession);
					account.setKeepAlive(keepAlive);

					if (will == null) {
						accountDao.update(account);
					} else {
						account.setWill(null);
						account.setWillTopic(null);
						account.setIsRetain(false);
						account.setQos(null);

						accountDao.update(account);
					}
				} else {
					if (will != null) {
						int qosInt = will.getTopic().getQos().getValue();
						Accounts looginAccount = new Accounts(null, protocol, username, userpass, clientID, hostname, port, isCleanSession, keepAlive, new String(will.getContent()), will.getTopic().getName().toString(), qosInt, true, will.getRetain(), isSecure, certificatePath, certificatePassword);
						accountDao.insert(looginAccount);
					} else {
						Accounts loginAccount = new Accounts(null, protocol, username, userpass, clientID, hostname, port, isCleanSession, keepAlive, null, null, 0, true, false, isSecure, certificatePath, certificatePassword);
						accountDao.insert(loginAccount);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (Protocols.MQTT_PROTOCOL.getValue() == position || Protocols.WEBSOCKET_PROTOCOL.getValue() == position) {
			findViewById(R.id.username_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.password_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.client_id_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.host_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.port_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.settings_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.clean_session_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.keepalive_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.will_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.will_topic_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.retain_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.qos_cell).setVisibility(View.VISIBLE);
		} else if (Protocols.MQTT_SN_PROTOCOL.getValue() == position) {
			findViewById(R.id.username_cell).setVisibility(View.GONE);
			findViewById(R.id.password_cell).setVisibility(View.GONE);
			findViewById(R.id.client_id_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.host_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.port_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.settings_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.clean_session_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.keepalive_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.will_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.will_topic_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.retain_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.qos_cell).setVisibility(View.VISIBLE);
		} else if (Protocols.COAP_PROTOCOL.getValue() == position) {
			findViewById(R.id.username_cell).setVisibility(View.GONE);
			findViewById(R.id.password_cell).setVisibility(View.GONE);
			findViewById(R.id.client_id_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.host_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.port_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.settings_cell).setVisibility(View.GONE);
			findViewById(R.id.clean_session_cell).setVisibility(View.GONE);
			findViewById(R.id.keepalive_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.will_cell).setVisibility(View.GONE);
			findViewById(R.id.will_topic_cell).setVisibility(View.GONE);
			findViewById(R.id.retain_cell).setVisibility(View.GONE);
			findViewById(R.id.qos_cell).setVisibility(View.GONE);
		} else if (Protocols.AMQP_PROTOCOL.getValue() == position) {
			findViewById(R.id.username_cell).setVisibility(View.GONE);
			findViewById(R.id.password_cell).setVisibility(View.GONE);
			findViewById(R.id.client_id_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.host_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.port_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.settings_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.clean_session_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.keepalive_cell).setVisibility(View.GONE);
			findViewById(R.id.will_cell).setVisibility(View.GONE);
			findViewById(R.id.will_topic_cell).setVisibility(View.GONE);
			findViewById(R.id.retain_cell).setVisibility(View.GONE);
			findViewById(R.id.qos_cell).setVisibility(View.GONE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onClick(View v) {
		if (v.equals(this.secureKeyCell)) {
			String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
			int res = checkCallingOrSelfPermission(permission);
			if (res == PackageManager.PERMISSION_GRANTED) {
				this.showOpenFileDialog();
			} else {
				ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
			if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
				if (grantResults[0] == 0) {
					this.showOpenFileDialog();
				}
			}
		}
	}

	private void showOpenFileDialog() {
		final OpenFileDialog openFileDialog = new OpenFileDialog(this);
		openFileDialog.setOnCloseListener(new OpenFileDialog.OnCloseListener() {
			@Override
			public void onCancel() {
			}
			@Override
			public void onOk(String selectedFile) {
				try {
					editSecureKey.setText(selectedFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		openFileDialog.test();
		openFileDialog.setFolderSelectable(true);
		openFileDialog.show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			findViewById(R.id.security_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.secure_key_cell).setVisibility(View.VISIBLE);
			findViewById(R.id.secure_key_pass_cell).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.security_cell).setVisibility(View.GONE);
			findViewById(R.id.secure_key_cell).setVisibility(View.GONE);
			findViewById(R.id.secure_key_pass_cell).setVisibility(View.GONE);
		}
	}

	private static class ConnectThread extends AsyncTask<Object, Object, Object> {
		private LoginActivity activity;
		private ClientInfoParcel clientInfo;

		public ConnectThread(LoginActivity activity, Integer protocol, String serverHost, String username, String password,
							 String clientID, Boolean isCleanSession, Integer keepAlive, Will will, Boolean shouldUpdate, int port,
							 boolean isSecure, String crtPath, String crtPassword) {
			this.activity = activity;
			this.clientInfo = new ClientInfoParcel(protocol, username, password, clientID, serverHost, isCleanSession, keepAlive, will, port, isSecure, crtPath, crtPassword, shouldUpdate);
		}

		@Override
		protected void onPostExecute(Object result) {
		}

		@Override
		protected String doInBackground(Object... param) {
			@SuppressWarnings("unused")
			InetSocketAddress address = null;
			try {
				address = InetSocketAddress.createUnresolved(clientInfo.getHost(), clientInfo.getPort());
			}
			catch (Exception ex) {
				activity.showMessage(R.string.error_opening_channel_title, R.string.error_opening_channel_message);
				return "";
			}

			Intent startServiceIntent = new Intent(activity, NetworkService.class);
			startServiceIntent.putExtra(clientInfo.getClass().getCanonicalName(), clientInfo);
			startServiceIntent.setAction(ApplicationSettings.ACTION_CHANNEL_CREATING);
			activity.startService(startServiceIntent);

			return "";
		}
	}
}
