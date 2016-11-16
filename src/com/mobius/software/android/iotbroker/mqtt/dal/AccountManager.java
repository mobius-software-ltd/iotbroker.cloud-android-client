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

import com.mobius.software.android.iotbroker.mqtt.parser.avps.QoS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AccountManager {

	private DataBaseHelper dbHelper;
	private String[] ACCOUNT_COLUMNS = {
			DataBaseHelper.ACCOUNT_ID,
			DataBaseHelper.ACCOUNT_USER_NAME,
			DataBaseHelper.ACCOUNT_PASS,
			DataBaseHelper.ACCOUNT_CLIENT_ID,
			DataBaseHelper.ACCOUNT_SERVER_HOST,
			DataBaseHelper.ACCOUNT_SESSION,
			DataBaseHelper.ACCOUNT_KEEP_ALIVE,
			DataBaseHelper.ACCOUNT_WILL,
			DataBaseHelper.ACCOUNT_WILL_TOPIC,
			DataBaseHelper.ACCOUNT_RETAIN,
			DataBaseHelper.ACCOUNT_QOS,
			DataBaseHelper.ACCOUNT_IS_DEFAULT, };

	private SQLiteDatabase database;

	public AccountManager(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insert(AccountDAO account) {

		long res = -1;

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(DataBaseHelper.ACCOUNT_USER_NAME, account.getUserName());
		values.put(DataBaseHelper.ACCOUNT_PASS, account.getPass());
		values.put(DataBaseHelper.ACCOUNT_PASS, account.getPass());
		values.put(DataBaseHelper.ACCOUNT_CLIENT_ID, account.getClientID());
		values.put(DataBaseHelper.ACCOUNT_SERVER_HOST, account.getServerHost());
		values.put(DataBaseHelper.ACCOUNT_SESSION,
				account.isCleanSession());
		values.put(DataBaseHelper.ACCOUNT_KEEP_ALIVE, account.getKeepAlive());
		values.put(DataBaseHelper.ACCOUNT_WILL_TOPIC, account.getWillTopic());
		values.put(DataBaseHelper.ACCOUNT_WILL, account.getWill());
		values.put(DataBaseHelper.ACCOUNT_RETAIN, account.isRetain());
		values.put(DataBaseHelper.ACCOUNT_QOS, account.getQos().getValue());
		values.put(DataBaseHelper.ACCOUNT_IS_DEFAULT, account.getIsDefault());

		res = db.insert(DataBaseHelper.ACCOUNT_TABLE, null, values);

		db.close();
		
		return res;
	}

	public AccountDAO get(int id) {
		AccountDAO result = new AccountDAO();

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS,
				DataBaseHelper.ACCOUNT_ID + "=" + id, null, null,
				null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			result = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return result;
	}

	public List<String> getList() {
		List<String> accountList = new ArrayList<String>();

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			AccountDAO account = parseConnectionSettings(cursor);
			accountList.add(account.getUserName());
			cursor.moveToNext();
		}

		cursor.close();
		return accountList;
	}

	public int update(AccountDAO account) {
		int result = -1;

		ContentValues values = new ContentValues();

		values.put(DataBaseHelper.ACCOUNT_ID, account.getId());
		values.put(DataBaseHelper.ACCOUNT_USER_NAME, account.getUserName());
		values.put(DataBaseHelper.ACCOUNT_PASS, account.getPass());
		values.put(DataBaseHelper.ACCOUNT_CLIENT_ID, account.getClientID());
		values.put(DataBaseHelper.ACCOUNT_SERVER_HOST, account.getServerHost());
		values.put(DataBaseHelper.ACCOUNT_SESSION,
				account.isCleanSession());
		values.put(DataBaseHelper.ACCOUNT_KEEP_ALIVE, account.getKeepAlive());
		values.put(DataBaseHelper.ACCOUNT_WILL_TOPIC, account.getWillTopic());
		values.put(DataBaseHelper.ACCOUNT_WILL, account.getWill());
		values.put(DataBaseHelper.ACCOUNT_RETAIN, account.isRetain());
		values.put(DataBaseHelper.ACCOUNT_QOS, account.getQos().getValue());
		values.put(DataBaseHelper.ACCOUNT_IS_DEFAULT, account.getIsDefault());

		result = database.update(DataBaseHelper.ACCOUNT_TABLE,
				values, DataBaseHelper.ACCOUNT_ID + "=" + account.getId(), null);
		return result;
	}

	public boolean deleteAll() {
		return database.delete(DataBaseHelper.ACCOUNT_TABLE, null,
				null) > 0;
	}

	private AccountDAO parseConnectionSettings(Cursor cursor) {
		AccountDAO account = new AccountDAO();

		if (cursor == null)
			return account;

		account.setId(cursor.getInt(0));
		account.setUserName(cursor.getString(1));
		account.setPass(cursor.getString(2));
		account.setClientID(cursor.getString(3));
		account.setServerHost(cursor.getString(4));
		account.setCleanSession(cursor.getInt(5) != 0);
		account.setKeepAlive(cursor.getInt(6));
		account.setWill(cursor.getString(7));
		account.setRetain(cursor.getInt(8) != 0);

		int qosStr = cursor.getInt(9);
		QoS qos = QoS.valueOf(qosStr);
		account.setQos(qos);

		return account;
	}

	public String getCurrentUserName() {
		final int IS_DEFAULT = 1;

		AccountDAO account = new AccountDAO();

		Cursor cursor = database
				.query(DataBaseHelper.ACCOUNT_TABLE,
						ACCOUNT_COLUMNS,
						DataBaseHelper.ACCOUNT_IS_DEFAULT + "="
								+ IS_DEFAULT, null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			account = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return account.getUserName();

	}

	public boolean existUserName(String userName) {
		boolean result = false;

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS,
				DataBaseHelper.ACCOUNT_USER_NAME + " = '" + userName + "'",
				null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			result = true;
			break;
		}

		cursor.close();

		return result;
	}

	public AccountDAO getCurrentAccount() {
		final int IS_DEFAULT = 1;

		AccountDAO account = new AccountDAO();

		Cursor cursor = database
				.query(DataBaseHelper.ACCOUNT_TABLE,
						ACCOUNT_COLUMNS,
						DataBaseHelper.ACCOUNT_IS_DEFAULT + "="
								+ IS_DEFAULT, null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			account = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return account;
	}
	
	public boolean existUserNameAndHost(String userName, String serverHost) {
		boolean result = false;

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS, DataBaseHelper.ACCOUNT_USER_NAME + " = '"
						+ userName + "'  AND "
						+ DataBaseHelper.ACCOUNT_SERVER_HOST + " ='"
						+ serverHost + "' ", null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			result = true;
			break;
		}

		cursor.close();

		return result;
	}

	public AccountDAO getAccountByUserName(String username) {
		AccountDAO result = new AccountDAO();

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS,
				DataBaseHelper.ACCOUNT_USER_NAME + "=" + "'"+username+"'" , null, null,
				null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			result = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return result;	
	}
	

	public AccountDAO getAccountByUserName(String username,String serverHost ) {
		AccountDAO result = null;

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS, DataBaseHelper.ACCOUNT_USER_NAME + "=" + "'"
						+ username + "' AND "+  DataBaseHelper.ACCOUNT_SERVER_HOST + " ='" + serverHost + "' ", null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			result = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return result;
	}

	

	public int changeIsDefaultForActiveUser(boolean status) {
		int result = -1;	 

		int activeUserId = getCurrentAccount().getId();

		if (activeUserId < 1) {
			return result;
		}

		ContentValues values = new ContentValues();

		values.put(DataBaseHelper.ACCOUNT_IS_DEFAULT, status);

		result = database.update(DataBaseHelper.ACCOUNT_TABLE, values,
				DataBaseHelper.ACCOUNT_ID + "=" + activeUserId, null);
		return result;
	}
	
	public List<AccountDAO> getAll() {
		List<AccountDAO> accountList = new ArrayList<AccountDAO>();

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			AccountDAO account = parseConnectionSettings(cursor);
			accountList.add(account);
			cursor.moveToNext();
		}

		cursor.close();
		return accountList;
	}
	
	public AccountDAO getDefaultAccount() {
		final int IS_DEFAULT = 1;

		AccountDAO account =null;

		Cursor cursor = database.query(DataBaseHelper.ACCOUNT_TABLE,
				ACCOUNT_COLUMNS, DataBaseHelper.ACCOUNT_IS_DEFAULT + "="
						+ IS_DEFAULT, null, null, null, null, "1");

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			account = parseConnectionSettings(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return account;
	}


}
