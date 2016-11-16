package com.mobius.software.android.iotbroker.mqtt.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.fragments.MessagesListFragment;
import com.mobius.software.android.iotbroker.mqtt.fragments.SendMessageFragment;
import com.mobius.software.android.iotbroker.mqtt.fragments.TopicsListFragment;
import com.mobius.software.android.iotbroker.mqtt.listeners.MessageListener;
import com.mobius.software.android.iotbroker.mqtt.managers.AppBroadcastManager;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class TopicsMessagesActivity extends SherlockActivity implements
		MessageListener {


	private Fragment tlFragment, smFragment, mlFragment;
	FragmentManager fragmentManager;

	private final String[] TAB_TAGS = new String[] { "tl", "sm", "ml" };
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topics_messages);

		actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		
		fragmentManager = getFragmentManager();
		tlFragment = new TopicsListFragment();
		smFragment = new SendMessageFragment();
		mlFragment = new MessagesListFragment();
		tab_tl_click(findViewById(R.id.fragment_container));

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!NetworkService.hasInstance()) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		AppBroadcastManager.setMessageListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppBroadcastManager.setMessageListener(null);
	}


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {    	
		MenuInflater inflater = getSupportMenuInflater();
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

			NetworkService.deActivateService();
			AccountManager manager = new AccountManager(this);
			manager.open();
			manager.changeIsDefaultForActiveUser(false);
			manager.close();

			setResult(RESULT_OK);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void messageReceived(MessageType valueOf) {
		switch (valueOf) {
		case SUBACK:
		case UNSUBACK:
			if (fragmentManager.findFragmentByTag(TAB_TAGS[0]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((TopicsListFragment) tlFragment).update();
					}
				});
			}
			break;
		case PUBACK:

			if (fragmentManager.findFragmentByTag(TAB_TAGS[1]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((SendMessageFragment) smFragment).update();

					}
				});
			}

		case PUBCOMP:
			if (fragmentManager.findFragmentByTag(TAB_TAGS[1]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((SendMessageFragment) smFragment).update();
					}
				});
			}
			break;
		case PUBLISH:
			if (fragmentManager.findFragmentByTag(TAB_TAGS[2]) != null) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						((MessagesListFragment) mlFragment).update();

					}
				});
			} else
				AppBroadcastManager.showNotification(getApplicationContext());

			break;
		default:
			break;
		}

	}

	public void tab_tl_click(View view) {
		actionBar.setTitle(R.string.tsm_title_topics_list);
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, tlFragment,
				TAB_TAGS[0]);
		fragmentTransaction.commit();

		changeImageForselectedTab(0);
	}

	public void tab_sm_click(View view) {
		actionBar.setTitle(R.string.tsm_title_send_message);
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, smFragment,
				TAB_TAGS[1]);
		fragmentTransaction.commit();

		changeImageForselectedTab(1);
	}

	public void tab_ml_click(View view) {
		actionBar.setTitle(R.string.tsm_title_messages_list);
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, mlFragment,
				TAB_TAGS[2]);
		fragmentTransaction.commit();
		changeImageForselectedTab(2);
	}

	private void changeImageForselectedTab(int selectedTabIndex) {

		TextView tbx_tl = (TextView) findViewById(R.id.tbx_tl);
		TextView tbx_sm = (TextView) findViewById(R.id.tbx_sm);
		TextView tbx_ml = (TextView) findViewById(R.id.tbx_ml);

		switch (selectedTabIndex) {
		case 0: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_tl_selected, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_sm, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_ml, 0, 0);
			break;
		}
		case 1: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_tl, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_sm_selected, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_ml, 0, 0);
			break;
		}
		case 2: {
			tbx_tl.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_tl, 0, 0);
			tbx_sm.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_sm, 0, 0);
			tbx_ml.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					R.drawable.ic_tab_ml_selected, 0, 0);
			break;
		}
		}

	}

	@Override
	public void networkDown() {
		setResult(RESULT_OK);
		finish();
	}
}
