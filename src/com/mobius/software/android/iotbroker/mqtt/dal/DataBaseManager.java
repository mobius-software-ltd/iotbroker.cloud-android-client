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

import java.util.List;

import com.mobius.software.android.iotbroker.mqtt.base.DaoObject;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao.Properties;
import com.mobius.software.android.iotbroker.mqtt.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Text;

import android.content.Context;
import de.greenrobot.dao.query.QueryBuilder;

public class DataBaseManager implements DataBaseListener {

	private Context context;

	public DataBaseManager(Context context) {
		this.context = context;
	}

	@Override
	public void clearTopicByActiveAccount() {
		Accounts activeAccounts = getActiveAccount();
		TopicsDao topicDao = ((TopicsDao) DaoObject.getDao(context, DaoType.TopicsDao));

		List<Topics> topicsList = topicDao.queryBuilder()
				.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.AccountID
						.eq(activeAccounts.getId()))
				.list();

		for (Topics topic : topicsList) {
			topicDao.delete(topic);
		}
	}

	@Override
	public Accounts getActiveAccount() {
		AccountsDao accountDao = ((AccountsDao) DaoObject.getDao(context, DaoType.AccountsDao));
		List<Accounts> accountsList = accountDao.queryBuilder().where(Properties.IsDefault.eq(1)).list();
		Accounts account = null;

		if (accountsList != null && accountsList.size() > 0) {
			account = accountsList.get(0);
		}

		return account;
	}

	@Override
	public void addMessage(String contentMessage, int qos, int j, String topicName) {

		MessagesDao messageDao = ((MessagesDao) DaoObject.getDao(context, DaoType.MessagesDao));
		Messages message = new Messages(null, contentMessage, qos, 1, topicName, getActiveAccount().getId());
		messageDao.insert(message);
	}

	@Override
	public void deleteTopics(Text topicName) {
		Accounts activeAccounts = getActiveAccount();
		TopicsDao topicDao = ((TopicsDao) DaoObject.getDao(context, DaoType.TopicsDao));

		QueryBuilder<Topics> queryBuilder = topicDao.queryBuilder();
		queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.TopicName.eq(topicName));
		queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.AccountID
				.eq(activeAccounts.getId()));

		List<Topics> topicsList = queryBuilder.list();

		if (topicsList != null) {
			for (Topics entity : topicsList) {
				topicDao.delete(entity);
			}
		}
	}

	@Override
	public boolean writeTopics(String topicName, int qos) {
		Accounts currAccount = getActiveAccount();

		TopicsDao topicDao = ((TopicsDao) DaoObject.getDao(context, DaoType.TopicsDao));

		if (currAccount != null) {

			Topics oldTopic = null;
			QueryBuilder<Topics> queryBuilder = topicDao.queryBuilder();

			queryBuilder
					.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.TopicName.eq(topicName));

			queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.AccountID
					.eq(currAccount.getId()));

			List<Topics> topicsList = queryBuilder.list();

			if (topicsList != null && topicsList.size() > 0) {
				oldTopic = topicsList.get(0);
			}

			if (oldTopic != null) {
				oldTopic.setQos(qos);
				topicDao.update(oldTopic);
			}

			else {
				Topics topic = new Topics(null, qos, topicName, currAccount.getId());
				topicDao.insert(topic);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isTopicExist(String topicName) {

		Accounts currAccount = getActiveAccount();
		// String topicName = topic.getName().toString();
		TopicsDao topicDao = ((TopicsDao) DaoObject.getDao(context, DaoType.TopicsDao));

		QueryBuilder<Topics> queryBuilder = topicDao.queryBuilder();

		queryBuilder.where(com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.TopicName.eq(topicName));

		queryBuilder.where(
				com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao.Properties.AccountID.eq(currAccount.getId()));

		List<Topics> topicsList = queryBuilder.list();

		if (topicsList != null && topicsList.size() > 0) {
			return true;
		}

		return false;

	}
}
