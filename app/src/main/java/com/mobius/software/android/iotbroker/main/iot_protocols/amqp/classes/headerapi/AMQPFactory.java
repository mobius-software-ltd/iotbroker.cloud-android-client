package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.StateCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPAttach;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPBegin;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPClose;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPDetach;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPDisposition;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPEnd;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPFlow;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPOpen;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPTransfer;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLChallenge;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLInit;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLMechanisms;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLOutcome;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLResponse;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPData;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPFooter;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPProperties;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPSection;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPSequence;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPValue;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.ApplicationProperties;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.DeliveryAnnotations;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.MessageAnnotations;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.MessageHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVFactory;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPAccepted;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPModified;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPOutcome;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPReceived;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPRejected;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPReleased;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPState;

import io.netty.buffer.ByteBuf;

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

public class AMQPFactory {

	public static AMQPHeader getAMQP(ByteBuf buf) {
		TLVAmqp list = TLVFactory.getTlv(buf);
		if (!list.getCode().equals(AMQPType.LIST_0) && list.getCode().equals(AMQPType.LIST_8)
				&& list.getCode().equals(AMQPType.LIST_32))
			throw new MalformedHeaderException("Received amqp-header with malformed arguments");

		AMQPHeader header = null;

		Byte byteCode = list.getConstructor().getDescriptorCode();
		HeaderCodes code = HeaderCodes.valueOf(byteCode);
		switch (code) {
		case ATTACH:
			header = new AMQPAttach();
			break;
		case BEGIN:
			header = new AMQPBegin();
			break;
		case CLOSE:
			header = new AMQPClose();
			break;
		case DETACH:
			header = new AMQPDetach();
			break;
		case DISPOSITION:
			header = new AMQPDisposition();
			break;
		case END:
			header = new AMQPEnd();
			break;
		case FLOW:
			header = new AMQPFlow();
			break;
		case OPEN:
			header = new AMQPOpen();
			break;
		case TRANSFER:
			header = new AMQPTransfer();
			break;
		default:
			throw new MalformedHeaderException("Received amqp-header with unrecognized performative");
		}

		header.fillArguments((TLVList) list);

		return header;
	}

	public static AMQPHeader getSASL(ByteBuf buf) {

		TLVAmqp list = TLVFactory.getTlv(buf);
		if (!list.getCode().equals(AMQPType.LIST_0) && list.getCode().equals(AMQPType.LIST_8)
				&& list.getCode().equals(AMQPType.LIST_32))
			throw new MalformedHeaderException("Received sasl-header with malformed arguments");

		AMQPHeader header = null;

		Byte byteCode = list.getConstructor().getDescriptorCode();
		HeaderCodes code = HeaderCodes.valueOf(byteCode);
		switch (code) {
		case CHALLENGE:
			header = new SASLChallenge();
			break;
		case INIT:
			header = new SASLInit();
			break;
		case MECHANISMS:
			header = new SASLMechanisms();
			break;
		case OUTCOME:
			header = new SASLOutcome();
			break;
		case RESPONSE:
			header = new SASLResponse();
			break;
		default:
			throw new MalformedHeaderException("Received sasl-header with unrecognized arguments code");
		}

		header.fillArguments((TLVList) list);

		return header;

	}

	public static AMQPSection getSection(ByteBuf buf) {

		TLVAmqp value = TLVFactory.getTlv(buf);

		AMQPSection section = null;

		Byte byteCode = value.getConstructor().getDescriptorCode();
		SectionCodes code = SectionCodes.valueOf(byteCode);
		switch (code) {
		case APPLICATION_PROPERTIES:
			section = new ApplicationProperties();
			break;
		case DATA:
			section = new AMQPData();
			break;
		case DELIVERY_ANNOTATIONS:
			section = new DeliveryAnnotations();
			break;
		case FOOTER:
			section = new AMQPFooter();
			break;
		case HEADER:
			section = new MessageHeader();
			break;
		case MESSAGE_ANNOTATIONS:
			section = new MessageAnnotations();
			break;
		case PROPERTIES:
			section = new AMQPProperties();
			break;
		case SEQUENCE:
			section = new AMQPSequence();
			break;
		case VALUE:
			section = new AMQPValue();
			break;
		default:
			throw new MalformedHeaderException("Received header with unrecognized message section code");
		}

		section.fill(value);

		return section;
	}

	public static AMQPState getState(TLVList list) {

		AMQPState state = null;

		Byte byteCode = list.getConstructor().getDescriptorCode();
		StateCodes code = StateCodes.valueOf(byteCode);
		switch (code) {
		case ACCEPTED:
			state = new AMQPAccepted();
			break;
		case MODIFIED:
			state = new AMQPModified();
			break;
		case RECEIVED:
			state = new AMQPReceived();
			break;
		case REJECTED:
			state = new AMQPRejected();
			break;
		case RELEASED:
			state = new AMQPReleased();
			break;
		default:
			throw new MalformedHeaderException("Received header with unrecognized state code");
		}

		return state;
	}

	public static AMQPOutcome getOutcome(TLVList list) {

		AMQPOutcome outcome = null;

		Byte byteCode = list.getConstructor().getDescriptorCode();
		StateCodes code = StateCodes.valueOf(byteCode);
		switch (code) {
		case ACCEPTED:
			outcome = new AMQPAccepted();
			break;
		case MODIFIED:
			outcome = new AMQPModified();
			break;
		case REJECTED:
			outcome = new AMQPRejected();
			break;
		case RELEASED:
			outcome = new AMQPReleased();
			break;
		default:
			throw new MalformedHeaderException("Received header with unrecognized outcome code");
		}
		return outcome;
	}

}
