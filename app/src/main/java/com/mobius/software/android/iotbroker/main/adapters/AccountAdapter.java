package com.mobius.software.android.iotbroker.main.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.iotbroker.androidclient.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private final Context context;
    private List<Accounts> items;
    private ButtonListener listener;

    public AccountAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public AccountAdapter(Context context, List<Accounts> accounts) {
        this.context = context;
        this.items = accounts;
    }

    public void setAccounts(List<Accounts> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public void setAccounts(Serializable items) {
        if (items instanceof List) {
            List list = (List)items;
            for (Object item : list) {
                if (item instanceof Accounts) {
                    this.items.add(((Accounts)item));

                }
            }
        }
        this.notifyDataSetChanged();
    }

    public ButtonListener getListener() {
        return listener;
    }

    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Accounts getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeItem(int i) {
        this.items.remove(i);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        Accounts item = this.items.get(i);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.activity_accounts_item, null);
        }

        TextView protocol = (TextView) convertView.findViewById(R.id.protocol_title);
        TextView clientId = (TextView) convertView.findViewById(R.id.client_id_title);
        TextView host = (TextView) convertView.findViewById(R.id.host_title);

        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.button);

        String protocolStr = Protocols.valueOf(item.getProtocolType()).toString();
        String hostStr = item.getServerHost() + " : " + String.valueOf(item.getPort());

        protocol.setText(protocolStr);
        clientId.setText(item.getClientID());
        host.setText(hostStr);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.buttonPressed(i);
            }
        });

        return convertView;
    }

}
