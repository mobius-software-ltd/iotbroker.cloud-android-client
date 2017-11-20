
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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.mobius.software.android.iotbroker.main.listeners.NetworkStateListener;

public class NetworkManager {

	private static NetworkStateListener networkStateListener = null;

	private static Boolean hasWifi = false;
	private static Boolean hasMobile = false;
	private static Boolean isInitialized = false;

	public static boolean hasNetworkAccess(Context context) {
		if (!isInitialized)
			update(context);

		return hasWifi || hasMobile;
	}

	public static boolean hasWifi(Context context) {
		if (!isInitialized)
			update(context);

		return hasWifi;
	}

	public static boolean hasMobile(Context context) {
		if (!isInitialized)
			update(context);

		return hasMobile;
	}

	public static boolean mobileNetworkExists(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE
				|| (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT && telephonyManager
						.getPhoneType() != TelephonyManager.PHONE_TYPE_SIP))
			return false;

		return true;
	}

	public static void setNetworkListener(NetworkStateListener listener) {
		networkStateListener = listener;
	}

	public static void updateNetworkInfo(Context context) {
		Boolean oldHasWifi = hasWifi;
		Boolean oldHasMobile = hasMobile;

		update(context);

		if (networkStateListener != null) {
			Boolean hadNetwork = oldHasWifi || oldHasMobile;
			Boolean hasNetwork = hasWifi || hasMobile;

			if (hadNetwork && hasNetwork)
				networkStateListener.networkChanged();
			else if (hadNetwork && !hasNetwork)
				networkStateListener.networkDown();
			else if (!hadNetwork && hasNetwork)
				networkStateListener.networkUp();		
		}

	}

	private static void update(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (networkInfo != null)
			hasWifi = networkInfo.isConnected();
		else
			hasWifi = false;

		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null)
			hasMobile = networkInfo.isConnected();
		else
			hasMobile = false;		
	}
}
