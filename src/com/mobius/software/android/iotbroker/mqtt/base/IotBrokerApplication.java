package com.mobius.software.android.iotbroker.mqtt.base;

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

import com.mobius.software.android.iotbroker.mqtt.dal.DaoMaster;
import com.mobius.software.android.iotbroker.mqtt.dal.DaoSession;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class IotBrokerApplication extends Application {
	 public DaoSession daoSession;

	    @Override
	    public void onCreate() {
	        super.onCreate();

	        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "dandp.db", null);
	        SQLiteDatabase db = helper.getWritableDatabase();
	        DaoMaster daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
	    }
		
		public DaoSession getDaoSession() {
	        return daoSession;
	    }
}
