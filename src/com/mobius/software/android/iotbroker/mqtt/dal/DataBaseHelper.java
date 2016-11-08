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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "iotBrocker.db";
	private static final int DATABASE_VERSION = 2;

	public static final String TOPICS_TABLE_NAME = "Topics";

	public static final String TOPICS_ID = "id";
	public static final String TOPICS_NAME = "Name";
	public static final String TOPICS_QOS = "Qos";
	public static final String TOPICS_ACCOUNT_ID = "AccountId";

	private static final String CREATE_TABLE_TOPICS = " CREATE TABLE "
			+ TOPICS_TABLE_NAME + "  (" + " " + TOPICS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + " " + TOPICS_NAME
			+ " TEXT NOT NULL," + " " + TOPICS_QOS + " INTEGER NOT NULL" + " ,"+TOPICS_ACCOUNT_ID +  " INTEGER  NOT NULL);";

	public static final String MESSAGE_TABLE_NAME = "MESSAGES";

	public static final String MESSAGE_ID = "id";
	public static final String MESSAGE_MESSAGE = "MessageItem";
	public static final String MESSAGE_QOS = "Qos";
	public static final String MESSAGE_TOPIC = "Topic";
	public static final String MESSAGE_ACCOUNT_ID = "AccountID";
	
	private static final String CREATE_TABLE_MESSAGES_SCRIPT = " CREATE TABLE "
			+ MESSAGE_TABLE_NAME + "  (" + " " + MESSAGE_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + " " + MESSAGE_MESSAGE
			+ " TEXT NOT NULL, " + "" + MESSAGE_QOS + " INTEGER NOT NULL, "
			+ MESSAGE_TOPIC + " TEXT NOT NULL , " + MESSAGE_ACCOUNT_ID +  " INTEGER NOT NULL   " + " );";

	// Connection setting
	public static final String ACCOUNT_TABLE = "ConnectionSetting";

	public static final String ACCOUNT_ID = "id";
	public static final String ACCOUNT_USER_NAME = "UserName";
	public static final String ACCOUNT_PASS = "Pass";
	public static final String ACCOUNT_CLIENT_ID = "ClientID";
	public static final String ACCOUNT_SERVER_HOST = "ServerHost";
	public static final String ACCOUNT_SESSION = "CleanSession";
	public static final String ACCOUNT_KEEP_ALIVE = "KeepAlive";
	public static final String ACCOUNT_WILL = "Will";
	public static final String ACCOUNT_WILL_TOPIC = "WillTopic";
	public static final String ACCOUNT_RETAIN = "Retain";
	public static final String ACCOUNT_QOS = "QoS";
	public static final String ACCOUNT_IS_DEFAULT = "IsDefault";

	private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "
			+ ACCOUNT_TABLE
			+ " ( "
			+ ACCOUNT_ID
			+ "  INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ACCOUNT_USER_NAME
			+ " TEXT NOT NULL, "
			+ ACCOUNT_PASS
			+ " TEXT NOT NULL, "
			+ ACCOUNT_CLIENT_ID
			+ " TEXT NOT NULL, "
			+ ACCOUNT_SERVER_HOST
			+ " TEXT NOT NULL, "
			+ ACCOUNT_SESSION
			+ " INTEGER, "
			+ ACCOUNT_KEEP_ALIVE
			+ " INTEGER, "
			+ ACCOUNT_WILL
			+ " TEXT, "
			+ ACCOUNT_WILL_TOPIC
			+ " TEXT, "
			+ ACCOUNT_RETAIN
			+ " INTEGER, "
			+ ACCOUNT_QOS
			+ " TEXT , "
			+ ACCOUNT_IS_DEFAULT + " INTEGER);";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TOPICS);
		db.execSQL(CREATE_TABLE_ACCOUNT);
		db.execSQL(CREATE_TABLE_MESSAGES_SCRIPT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TOPICS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
	}
}
