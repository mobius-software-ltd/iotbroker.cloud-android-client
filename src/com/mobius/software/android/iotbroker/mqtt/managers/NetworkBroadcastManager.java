package com.mobius.software.android.iotbroker.mqtt.managers;

import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class NetworkBroadcastManager extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION)) 
		{
			NetworkUpdaterThread current=new NetworkUpdaterThread();
			Context[] params=new Context[1];
			params[0]=context;
			NetworkService.executeAsyncTask(current,(Object[])params);			
	    }
	}	
	
	private static class NetworkUpdaterThread extends AsyncTask<Context, String, String>
	 {
	   	public NetworkUpdaterThread()
	    {  
	    }  
	        
	    @Override
	    protected void onPostExecute(String result) 
	    {
	    }
			
	    @Override
	    protected String doInBackground(Context... param) 
	    {
	    	Thread.currentThread().setName("Network Updater");
	    	NetworkManager.updateNetworkInfo(param[0]);		
	    	return "";
		}
	}	
}

