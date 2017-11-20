package com.mobius.software.android.iotbroker.main.managers;

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
			///NetworkService.executeAsyncTask(current,(Object[])params);	
			current.execute((Context[])params);
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

