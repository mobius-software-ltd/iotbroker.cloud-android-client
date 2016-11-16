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
import android.database.sqlite.SQLiteDatabase;

import com.mobius.software.android.iotbroker.mqtt.parser.avps.Text;

public class TopicsManager {

	private DataBaseHelper dbHelper;
	private String[] TOPICS_COLUMNS = { DataBaseHelper.TOPICS_ID,
			DataBaseHelper.TOPICS_NAME, DataBaseHelper.TOPICS_QOS,
			DataBaseHelper.TOPICS_ACCOUNT_ID };

	public TopicsManager(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public long insert(String topicName, int qos, int accountID) {

		long res = -1;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(DataBaseHelper.TOPICS_NAME, topicName);
			values.put(DataBaseHelper.TOPICS_QOS, qos);
			values.put(DataBaseHelper.TOPICS_ACCOUNT_ID, accountID);
			res = db.insert(DataBaseHelper.TOPICS_TABLE_NAME, null, values);
		} finally {
			dbHelper.close();
		}
		return res;
	}

	public boolean deleteAll() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = 0;
		try {
			result = db.delete(DataBaseHelper.TOPICS_TABLE_NAME, null, null);
		} finally {
			dbHelper.close();
		}
		return result > 0;
	}

	public TopicDAO get(int id) {
		TopicDAO result = new TopicDAO();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			Cursor cursor = db.query(DataBaseHelper.TOPICS_TABLE_NAME,
					TOPICS_COLUMNS, DataBaseHelper.TOPICS_ID + "=" + id, null,
					null, null, null, null);

			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				result = parseTopic(cursor);
				cursor.moveToNext();
			}

			cursor.close();
		} finally {
			dbHelper.close();
		}
		return result;
	}

	public boolean isTopicExist(String topicName, int accountID) {

		boolean result = false;
		Cursor cursor = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.query(DataBaseHelper.TOPICS_TABLE_NAME, TOPICS_COLUMNS,
					DataBaseHelper.TOPICS_NAME + "='" + topicName + "' AND "
							+ DataBaseHelper.TOPICS_ACCOUNT_ID + " = "
							+ accountID, null, null, null, null, null);

			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				result = true;
				break;
			}
		} finally {
			if (cursor != null)
				cursor.close();
			dbHelper.close();
		}
		return result;
	}

	public TopicDAO getByName(String topicName, int accountID) {

		TopicDAO result = null;
		Cursor cursor = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			cursor = db.query(DataBaseHelper.TOPICS_TABLE_NAME, TOPICS_COLUMNS,
					DataBaseHelper.TOPICS_NAME + "='" + topicName + "' AND "
							+ DataBaseHelper.TOPICS_ACCOUNT_ID + " = "
							+ accountID, null, null, null, null, null);

			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				result = parseTopic(cursor);
				cursor.moveToNext();
				break;
			}
		} finally {
			if (cursor != null)
				cursor.close();
			dbHelper.close();
		}
		return result;
	}

	public int update(long id, String topicName, int qos, int accountID) {
		int result = -1;

		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.TOPICS_NAME, topicName);
		values.put(DataBaseHelper.TOPICS_QOS, qos);
		values.put(DataBaseHelper.TOPICS_ACCOUNT_ID, accountID);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			result = db.update(DataBaseHelper.TOPICS_TABLE_NAME, values,
					DataBaseHelper.TOPICS_ID + " = " + id, null);
		} finally {
			dbHelper.close();
		}
		return result;
	}

	private TopicDAO parseTopic(Cursor cursor) {
		TopicDAO topic = new TopicDAO();

		if (cursor == null)
			return topic;

		topic.setId(cursor.getInt(0));
		topic.setTopicName(cursor.getString(1));

		int qos = cursor.getInt(2);
		topic.setQos(qos);

		int accountId = cursor.getInt(3);
		topic.setAccountId(accountId);

		return topic;
	}

	public int deleteByName(Text topicName, int accountID) {
		int result = -1;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			result = db.delete(DataBaseHelper.TOPICS_TABLE_NAME,
					DataBaseHelper.TOPICS_NAME + " = '" + topicName
					+ "' AND " 		
					+ DataBaseHelper.TOPICS_ACCOUNT_ID + " = "
							+ accountID, null);
		} finally {
			dbHelper.close();
		}
		return result;

	}

	public int deleteByAccountId(int accountID) {
		int result = -1;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			result = db.delete(DataBaseHelper.TOPICS_TABLE_NAME,
					DataBaseHelper.TOPICS_ACCOUNT_ID + " = " + accountID, null);			
		} finally {
			dbHelper.close();
		}
		return result;
	}

	public List<TopicDAO> getListByAccountId(int accountID) {
		List<TopicDAO> topicsList = new ArrayList<TopicDAO>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DataBaseHelper.TOPICS_TABLE_NAME, TOPICS_COLUMNS,
					DataBaseHelper.TOPICS_ACCOUNT_ID + "=" + accountID, null,
					null, null, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				TopicDAO topic = parseTopic(cursor);
				topicsList.add(topic);
				cursor.moveToNext();
			}

			cursor.close();
		} finally {
			dbHelper.close();
			if (cursor != null)
				cursor.close();
		}
		return topicsList;
	}

}
