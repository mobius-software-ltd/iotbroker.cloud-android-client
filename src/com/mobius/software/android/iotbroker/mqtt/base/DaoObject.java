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

import com.mobius.software.android.iotbroker.mqtt.dal.DaoType;

import android.content.Context;


import de.greenrobot.dao.AbstractDao;

public class DaoObject {
	public static AbstractDao<?, ?> getDao(Context context, DaoType type ){
		switch(type)
		{
		case  AccountsDao:
			return (((IotBrokerApplication) context.getApplicationContext()).getDaoSession()).getAccountsDao();
			
		case  MessagesDao:
			return (((IotBrokerApplication) context.getApplicationContext()).getDaoSession()).getMessagesDao();
			
		case  TopicsDao:
			return (((IotBrokerApplication) context.getApplicationContext()).getDaoSession()).getTopicsDao();
			
		default :
			return null;
		}
	}
}
