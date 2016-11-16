package com.mobius.software.android.iotbroker.mqtt.managers;

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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobius.software.android.iotbroker.mqtt.activity.LoadingActivity;
import com.mobius.software.android.iotbroker.mqtt.listeners.MessageListener;
import com.mobius.software.android.iotbroker.mqtt.listeners.StatusChangedListener;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

public class AppBroadcastManager extends BroadcastReceiver  {

	public static final String NETWORK_STATUS_CHANGE ="com.mobuis.software.mqtt.managers.AppBroadcastManager.NETWORK_STATUS_CHANGE";
	public static final String MESSAGE_STATUS_UPDATED ="com.mobuis.software.mqtt.managers.AppBroadcastManager.MESSAGE_STATUS_UPDATED";
                         
	public static final String NETWORK_UP ="com.mobuis.software.mqtt.managers.AppBroadcastManager.NETWORK_UP";
	public static final String NETWORK_DOWN ="com.mobuis.software.mqtt.managers.AppBroadcastManager.NETWORK_DOWN";
	public static final String NETWORK_CHANGED ="com.mobuis.software.mqtt.managers.AppBroadcastManager.NETWORK_CHANGED";	
	
	public static StatusChangedListener statusChangedListener=null;
	public static MessageListener messageListener = null;
	
	public static void setStatusChangedListener(StatusChangedListener listener)
	{
		statusChangedListener=listener;
	}
	
	public  static void  setMessageListener(MessageListener listener){
		messageListener = listener;
	}
		
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(intent.getAction().equals(NETWORK_STATUS_CHANGE))
		{
			if(statusChangedListener!=null)
			{
				String status=intent.getStringExtra("status");				
				statusChangedListener.statusChanged(Enum.valueOf(ConnectionState.class, status));
			}
		}
		else if(intent.getAction().equals(NETWORK_UP))
		{
			if(statusChangedListener!=null)
				statusChangedListener.networkUp();		
		}
		else if(intent.getAction().equals(MESSAGE_STATUS_UPDATED))
		{
			if(messageListener!=null)
			{				
				MessageType messageType = (MessageType)intent.getSerializableExtra(NetworkService.MESSAGETYPE_PARAM);
				messageListener.messageReceived(messageType);
			}
			else
			{
				MessageType messageType = (MessageType)intent.getSerializableExtra(NetworkService.MESSAGETYPE_PARAM);
				if(messageType==MessageType.PUBLISH)
					showNotification(context);
			}
		}		
		else if(intent.getAction().equals(NETWORK_DOWN))
		{
			if(messageListener!=null)
				messageListener.networkDown();		
		}
		else if(intent.getAction().equals(NETWORK_CHANGED))
		{
			if(messageListener!=null)
				messageListener.networkDown();		
		}
	}	
	
	public static void showNotification(Context contextItem)
	{			
		String message = contextItem.getResources().getString(R.string.notification_new_message);
		String title = contextItem.getResources().getString(R.string.notification_message);
		String  notificationID_str = contextItem.getResources().getString(R.string.notification_id);
		int notificationID = Integer.parseInt(notificationID_str);
		
        Intent notificationIntent = new Intent(contextItem, LoadingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(contextItem,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
     
        Notification.Builder builder = new Notification.Builder(contextItem);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.iotbroker_icon)             
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)                     
                .setContentTitle(title)                    
                .setContentText(message); 

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) contextItem
                .getSystemService(Context.NOTIFICATION_SERVICE);        
        notificationManager.notify(notificationID, notification);
	}
}
