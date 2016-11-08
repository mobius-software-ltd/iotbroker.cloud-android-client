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
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.mobius.software.android.iotbroker.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.parser.QoS;
import com.mobius.software.android.iotbroker.mqtt.parser.Text;
import com.mobius.software.android.iotbroker.mqtt.parser.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.Will;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.android.iotbroker.mqtt.utility.MessageDialog;
import com.mobius.software.iotbroker.androidclient.R;

public class LoginActivity extends Activity   {

	private List<String> accountsLists;
	private EditText tbx_will;
	private String currentWill;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		tbx_will = (EditText) findViewById(R.id.tbx_will);

		AccountManager acMngr = new AccountManager(getApplicationContext());
		acMngr.open();
		AccountDAO account = acMngr.getDefaultAccount();
			
		acMngr.close();
		if (account != null) 
		{
				
			login(account.getServerHost(), account.getUserName(),
					account.getPass(), account.getClientID(),
					account.isCleanSession(), account.getKeepAlive(),
					account.getWill(), account.getWillTopic(), account.isRetain(), account.getQos().getValue(),false);						
		}
		else
		{					
			bindAccountList();
	
			if (accountsLists.size() > 0) 
				showAvailableAccountDialog();			
		}
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean value = extras.getBoolean("FAILED");

			if (value) {
				MessageDialog.showMessage(this,
						getString(R.string.connecting_error_title_dialog),
						getString(R.string.action_settings));
			}
		}				
	}

	private void bindAccountList() {
		AccountManager accountMngr = new AccountManager(getBaseContext());
		accountMngr.open();

		accountsLists = accountMngr.getList();
		accountMngr.close();
	}

	public void showAvailableAccountDialog() {

		final AlertDialog.Builder accountsDialog = new AlertDialog.Builder(this);
		accountsDialog.setTitle(R.string.available_accounts);

		View linearlayout = getLayoutInflater().inflate(
				R.layout.choise_account_dialog, null);
		accountsDialog.setView(linearlayout);

		final Spinner spnrAccountName = (Spinner) linearlayout
				.findViewById(R.id.spnr_current_accounts);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item,
				accountsLists);

		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnrAccountName.setAdapter(spinnerArrayAdapter);

		accountsDialog.setPositiveButton(R.string.topics_btn_OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						
						String username = spnrAccountName.getSelectedItem()
								.toString();

						String[] usernameParts = username.split(" ");
						
						username = usernameParts[0];
						
						AccountManager accountMngr = new AccountManager(
								getBaseContext());
						accountMngr.open();

						AccountDAO account = accountMngr
								.getAccountByUserName(username);
						accountMngr.close();

						String serverHost = account.getServerHost();

						int qosNumb = account.getQos().getValue();
						login(serverHost, account.getUserName(),
								account.getPass(), account.getClientID(),
								account.isCleanSession(),
								account.getKeepAlive(), account.getWill(),
								account.getWillTopic(), account.isRetain(), qosNumb,false);

						dialog.dismiss();
					}
				})

		.setNegativeButton(R.string.topics_btn_Cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		accountsDialog.create();
		accountsDialog.show();
	}

	public void showAddWillDialog() {

		final AlertDialog.Builder inputWillDialog = new AlertDialog.Builder(
				this);
		inputWillDialog.setTitle(R.string.will_input_dialog_title);

		View linearlayout = getLayoutInflater().inflate(
				R.layout.input_will_dialog, null);
		inputWillDialog.setView(linearlayout);

		final EditText tbxWill = (EditText) linearlayout
				.findViewById(R.id.tbx_will);
		tbxWill.setText(currentWill);

		inputWillDialog.setPositiveButton(R.string.will_input_dialog_btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						currentWill = tbxWill.getText().toString();
						tbx_will.setText(currentWill);

						dialog.dismiss();
					}
				})

		.setNegativeButton(R.string.will_input_dialog_btn_cancel,
				new DialogInterface.OnClickListener() {
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
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.error_required_user_name));
			return;
		}	

		EditText tbxPass = (EditText) findViewById(R.id.tbxpass);
		String userpass = tbxPass.getText().toString();

		if (isEmpty(userpass)) {
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.error_required_password));
			return;
		}

		EditText tbxClientID = (EditText) findViewById(R.id.tbx_client_id);
		String clientID = tbxClientID.getText().toString();

		if (isEmpty(clientID)) {
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.error_required_client_id));
			return;
		}

		EditText tbxServerHost = (EditText) findViewById(R.id.txt_server_host);
		String serverHost = tbxServerHost.getText().toString();

		if (isEmpty(serverHost)) {
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.error_required_server_host));
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
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.keep_alive_must_more_zero));
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
		
		if(willMessage.length()>0 && willTopic.length()==0){
			MessageDialog.showMessage(this, errorTitle,
					getString(R.string.will_topic_can_not_be_null));
			return;
		}
	 	
		login(serverHost, username, userpass, clientID, isCleanSession,keepAlive,willTopic, willMessage, isRetain, qosNumb,true);
	}

	private void login(String serverHost, String username, String userpass,
			String clientID, boolean isCleanSession, int keepAlive, String willTopic,
			String willMessage, boolean isRetain, int qosNumb,Boolean updateAccount) {
		
		if(!NetworkManager.hasNetworkAccess(this))
		{
			MessageDialog.showMessage(this,
					getString(R.string.no_network_error),
					getString(R.string.no_network_error_message));
			return;
		}
		
		Will will=null;
		if(willMessage!=null && willMessage.length()>0)
		{
			will=new Will();		
			will.setContent(willMessage.getBytes());
			will.setRetain(isRetain);
			Topic willTopicData=new Topic();
			willTopicData.setName(new Text(willTopic));
			willTopicData.setQos(QoS.valueOf(qosNumb));
			will.setTopic(willTopicData);
		}
		ConnectThread connectThread=new ConnectThread(this, serverHost, username, userpass, clientID, isCleanSession, keepAlive, will,updateAccount);
		NetworkService.executeAsyncTask(connectThread);
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
	
	private void showMessage(final Integer title,final Integer messsage)
	{
		final Activity activity=this;
		this.runOnUiThread(new Runnable() {
			public void run() {
				MessageDialog.showMessage(activity,
						getString(title),
						getString(messsage));
			}
		});
	}
	
	private static class ConnectThread extends AsyncTask<Object, Object, Object>
	{
		private LoginActivity activity;
		private String hostname,username,password,clientID;
		private Boolean cleanSession;
		private Integer keepalive;
		private Will will;
		private Boolean shouldUpdate;
		
	   	public ConnectThread(LoginActivity activity,String serverHost,String username,String userpass,String clientID,Boolean isCleanSession,Integer keepAlive,Will will,Boolean shouldUpdate)
	    {  
	   		this.activity=activity;
	   		this.hostname=serverHost;
	   		this.username=username;
	   		this.password=userpass;
	   		this.clientID=clientID;
	   		this.cleanSession=isCleanSession;
	   		this.will=will;
	   		this.keepalive=keepAlive;
	   		this.shouldUpdate=shouldUpdate;
	    }  
	        
	    @Override
	    protected void onPostExecute(Object result) 
	    {
	    }
			
	    @Override
	    protected String doInBackground(Object... param) 
	    {
	    	InetSocketAddress address=null;
	    	try
	    	{
	    		address=InetSocketAddress.createUnresolved(hostname, MqttClient.SERVER_PORT);
	    	}
	    	catch(Exception ex)
	    	{
	    		activity.showMessage(R.string.error_opening_channel_title,R.string.error_opening_channel_message);
	    		return "";
	    	}
	    	
	    	Boolean channelOpened = false;
			try{
			channelOpened = NetworkService.activateService(address,
					username, password, clientID, cleanSession, keepalive, will);
			}
			catch(Exception ex){
				ex.printStackTrace();
				activity.showMessage(R.string.error_opening_channel_title,
						R.string.error_opening_channel_message);
			}
			if (!channelOpened)
				activity.showMessage(R.string.error_opening_channel_title,
						R.string.error_opening_channel_message);
			else {
				if(shouldUpdate)
				{
					AccountManager manager = new AccountManager(activity);
					try
					{
						manager.open();

						boolean existUserHost = manager.existUserNameAndHost(username,hostname);
						AccountDAO account = 	manager.getAccountByUserName(username, hostname);
						
						if(existUserHost){				
							
							account.setPass(password);
							account.setClientID(clientID);
							account.setCleanSession(cleanSession);
							account.setKeepAlive(keepalive);						
							
							
							if(will==null)
							{	
								manager.update(account);
							}
							else
							{
								
								account.setWill(null);
								account.setWillTopic(null);
								account.setRetain(false);
								account.setQos(null);
								
								manager.update(account);
							}
						}
						else{
							
							if(will!=null)
							{
								AccountDAO loginAccount = new AccountDAO(username,password,clientID,hostname,cleanSession,keepalive,  new String(will.getContent())
								,will.getTopic().getName().toString(),will.getRetain(),will.getTopic().getQos(),1);
								
								manager.insert(loginAccount);
							} 
							else {
								
								AccountDAO loginAccount = new AccountDAO(username,password,clientID,hostname,cleanSession,keepalive,
										null,null,false,null,1);
								
								manager.insert(loginAccount);
							}
						}
					}
					catch(Exception ex ){				
						ex.printStackTrace();													
					}
					finally{				
						if(manager!=null){
							manager.close();
						}
					}
				}
				
				activity.setResult(RESULT_OK);
				activity.finish();
			}
			
			return "";
		}	    	    
	}	
}
