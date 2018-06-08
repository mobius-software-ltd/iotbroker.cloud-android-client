package com.mobius.software.android.iotbroker.main.base;

import android.content.Intent;
import android.os.AsyncTask;

import com.mobius.software.android.iotbroker.main.activity.LoginActivity;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.iotbroker.androidclient.R;

import java.net.InetSocketAddress;

/**
 * Mobius Software LTD
 * Copyright 2015-2017, Mobius Software LTD
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

public class ConnectingTask extends AsyncTask<Object, Object, Object> {
    private NetworkService service;
    private ClientInfoParcel clientInfo;

    public ConnectingTask(NetworkService service, ClientInfoParcel clientInfo) {
        this.service = service;
        this.clientInfo = clientInfo;
    }

    @Override
    protected void onPostExecute(Object result) {
    }

    @Override
    protected String doInBackground(Object... param) {
        @SuppressWarnings("unused")

        InetSocketAddress address = new InetSocketAddress(clientInfo.getHost(), clientInfo.getPort());

        Intent brodcastIntent = new Intent();
        Boolean res = service.activateService(address, clientInfo.getProtocol().getValue(), clientInfo.getUsername(), clientInfo.getPassword(),
                clientInfo.getClientId(), clientInfo.isCleanSession(), clientInfo.getKeepalive(), clientInfo.getWill(), clientInfo.getSecure(),
                clientInfo.getCertificatePath(), clientInfo.getCertificatePassword());

        if (res) {
            brodcastIntent.setAction(ApplicationSettings.ACTION_CHANNEL_CREATING);
        } else {
            brodcastIntent.setAction(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL);
        }

        brodcastIntent.putExtra(ClientInfoParcel.class.getCanonicalName(), clientInfo);
        brodcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        service.sendBroadcast(brodcastIntent);

        return "";
    }
}
