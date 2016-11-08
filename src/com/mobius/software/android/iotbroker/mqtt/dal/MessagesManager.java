package com.mobius.software.android.iotbroker.mqtt.dal;

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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MessagesManager {

	private DataBaseHelper dbHelper;
	private String[] MESSAGES_COLUMNS = { DataBaseHelper.MESSAGE_ID,
			DataBaseHelper.MESSAGE_MESSAGE, DataBaseHelper.MESSAGE_QOS, DataBaseHelper.MESSAGE_TOPIC ,DataBaseHelper.MESSAGE_ACCOUNT_ID};
	
	
	private SQLiteDatabase database;

	public MessagesManager(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public long insert(String  topicName,String messageText, int qos , int accountID) {

		long res = -1;

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(DataBaseHelper.MESSAGE_MESSAGE, messageText);
		values.put(DataBaseHelper.MESSAGE_QOS, qos);
		values.put(DataBaseHelper.MESSAGE_TOPIC, topicName);
		values.put(DataBaseHelper.MESSAGE_ACCOUNT_ID, accountID);
		
		
		res = db.insert(DataBaseHelper.MESSAGE_TABLE_NAME, null, values);

		return res;
	}

	
	public int deleteByAccount(int id, int accountID) {
		int result = -1;

		result = database.delete(DataBaseHelper.MESSAGE_TABLE_NAME,
				DataBaseHelper.MESSAGE_ID + " = " + id + " AND  "+ DataBaseHelper.MESSAGE_ACCOUNT_ID + " = " + accountID, null);

		return result;
	}
	
	
	public List<MessageDAO> getReverseListByAccount(int accountID) {
		final String orderBy =  DataBaseHelper.MESSAGE_ID + " DESC";
		
		List<MessageDAO> messageList = new ArrayList<MessageDAO>();

		Cursor cursor = database.query(DataBaseHelper.MESSAGE_TABLE_NAME,
				MESSAGES_COLUMNS, DataBaseHelper.MESSAGE_ACCOUNT_ID + " = " + accountID , null, null, null, orderBy);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			MessageDAO messageItem = parseMessage(cursor);
			messageList.add(messageItem);
			cursor.moveToNext();
		}

		cursor.close();
		return messageList;
	}
	

	public boolean deleteAll(){
		return database.delete(DataBaseHelper.MESSAGE_TABLE_NAME,null,null) > 0;
	}
	
	public int update(long id, String messageItem, int qos,String topicName,int accountID) {
		int result = -1;

		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.MESSAGE_MESSAGE, messageItem);
		values.put(DataBaseHelper.MESSAGE_QOS, qos);
		values.put(DataBaseHelper.MESSAGE_TOPIC, topicName);
		values.put(DataBaseHelper.MESSAGE_ACCOUNT_ID, accountID);
		

		result = database.update(DataBaseHelper.MESSAGE_TABLE_NAME, values,
				DataBaseHelper.MESSAGE_ID + "=" + id, null);
		return result;
	}

	private MessageDAO parseMessage(Cursor cursor) {
		MessageDAO messageItem = new MessageDAO();

		if (cursor == null)
			return messageItem;

		messageItem.setId(cursor.getInt(0));
		messageItem.setMessageItem(cursor.getString(1));

		int qos = cursor.getInt(2);	
		messageItem.setQos(qos);
		messageItem.setTopicName(cursor.getString(3));
		messageItem.setAccountId(cursor.getInt(4));

		return messageItem;
	}
	

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
}
