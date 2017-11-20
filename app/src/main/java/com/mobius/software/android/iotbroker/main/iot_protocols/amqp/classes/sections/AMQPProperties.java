package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.MessageID;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.messageid.BinaryID;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.messageid.LongID;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.messageid.StringID;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.messageid.UuidID;

import java.util.Date;

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

public class AMQPProperties extends AMQPSection {

	private MessageID messageId;
	private byte[] userId;
	private String to;
	private String subject;
	private String replyTo;
	private byte[] correlationId;
	private String contentType;
	private String contentEncoding;
	private Date absoluteExpiryTime;
	private Date creationTime;
	private String groupId;
	private Long groupSequence;
	private String replyToGroupId;

	@Override
	public SectionCodes getCode() {
		return SectionCodes.PROPERTIES;
	}

	@Override
	public TLVAmqp getValue() {

		TLVList list = new TLVList();

		if (messageId != null) {
			Object value = null;
			if (messageId.getBinary() != null)
				value = messageId.getBinary();
			else if (messageId.getLong() != null)
				value = messageId.getLong();
			else if (messageId.getString() != null)
				value = messageId.getString();
			else if (messageId.getUuid() != null)
				value = messageId.getUuid();
			list.addElement(0, AMQPWrapper.wrap(value));
		}
		if (userId != null)
			list.addElement(1, AMQPWrapper.wrap(userId));
		if (to != null)
			list.addElement(2, AMQPWrapper.wrap(to));
		if (subject != null)
			list.addElement(3, AMQPWrapper.wrap(subject));
		if (replyTo != null)
			list.addElement(4, AMQPWrapper.wrap(replyTo));
		if (correlationId != null)
			list.addElement(5, AMQPWrapper.wrap(correlationId));
		if (contentType != null)
			list.addElement(6, AMQPWrapper.wrap(contentType));
		if (contentEncoding != null)
			list.addElement(7, AMQPWrapper.wrap(contentEncoding));
		if (absoluteExpiryTime != null)
			list.addElement(8, AMQPWrapper.wrap(absoluteExpiryTime));
		if (creationTime != null)
			list.addElement(9, AMQPWrapper.wrap(creationTime));
		if (groupId != null)
			list.addElement(10, AMQPWrapper.wrap(groupId));
		if (groupSequence != null)
			list.addElement(11, AMQPWrapper.wrap(groupSequence));
		if (replyToGroupId != null)
			list.addElement(12, AMQPWrapper.wrap(replyToGroupId));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x73 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fill(TLVAmqp value) {
		TLVList list = (TLVList) value;
		if (list.getList().size() > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull()) {
				switch (element.getCode()) {
				case ULONG_0:
				case SMALL_ULONG:
				case ULONG:
					messageId = new LongID(AMQPUnwrapper.unwrapULong(element));
					break;
				case STRING_8:
				case STRING_32:
					messageId = new StringID(AMQPUnwrapper.unwrapString(element));
					break;
				case BINARY_8:
				case BINARY_32:
					messageId = new BinaryID(AMQPUnwrapper.unwrapBinary(element));
					break;
				case UUID:
					messageId = new UuidID(AMQPUnwrapper.unwrapUuid(element));
					break;
				default:
					throw new IllegalArgumentException("Expected type 'MessageID' - received: "
							+ element.getCode());
				}
			}
		}
		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				userId = AMQPUnwrapper.unwrapBinary(element);
		}
		if (list.getList().size() > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				to = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				subject = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				replyTo = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull())
				correlationId = AMQPUnwrapper.unwrapBinary(element);
		}
		if (list.getList().size() > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull())
				contentType = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 7) {
			TLVAmqp element = list.getList().get(7);
			if (!element.isNull())
				contentEncoding = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 8) {
			TLVAmqp element = list.getList().get(8);
			if (!element.isNull())
				absoluteExpiryTime = AMQPUnwrapper.unwrapTimestamp(element);
		}
		if (list.getList().size() > 9) {
			TLVAmqp element = list.getList().get(9);
			if (!element.isNull())
				creationTime = AMQPUnwrapper.unwrapTimestamp(element);
		}
		if (list.getList().size() > 10) {
			TLVAmqp element = list.getList().get(10);
			if (!element.isNull())
				groupId = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 11) {
			TLVAmqp element = list.getList().get(11);
			if (!element.isNull())
				groupSequence = AMQPUnwrapper.unwrapUInt(element);
		}
		if (list.getList().size() > 12) {
			TLVAmqp element = list.getList().get(12);
			if (!element.isNull())
				replyToGroupId = AMQPUnwrapper.unwrapString(element);
		}
	}

	public MessageID getMessageId() {
		return messageId;
	}

	public void setMessageId(MessageID messageId) {
		this.messageId = messageId;
	}

	public byte[] getUserId() {
		return userId;
	}

	public void setUserId(byte[] userId) {
		this.userId = userId;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public byte[] getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(byte[] correlationId) {
		this.correlationId = correlationId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public Date getAbsoluteExpiryTime() {
		return absoluteExpiryTime;
	}

	public void setAbsoluteExpiryTime(Date absoluteExpiryTime) {
		this.absoluteExpiryTime = absoluteExpiryTime;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Long getGroupSequence() {
		return groupSequence;
	}

	public void setGroupSequence(Long groupSequence) {
		this.groupSequence = groupSequence;
	}

	public String getReplyToGroupId() {
		return replyToGroupId;
	}

	public void setReplyToGroupId(String replyToGroupId) {
		this.replyToGroupId = replyToGroupId;
	}

}
