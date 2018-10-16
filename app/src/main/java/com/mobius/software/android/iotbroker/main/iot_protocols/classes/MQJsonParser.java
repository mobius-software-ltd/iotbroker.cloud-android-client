package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mobius.software.android.iotbroker.main.MalformedMessageException;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connack;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingresp;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Puback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrec;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrel;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Suback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsuback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsubscribe;

public class MQJsonParser {

    private ObjectMapper mapper = new ObjectMapper();

    public MQJsonParser() {
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public byte[] encode(Message message) throws JsonProcessingException {
        String json = this.mapper.writeValueAsString(message);
        return json.getBytes();
    }

    public String jsonString(Message message) throws JsonProcessingException {
        return new String(this.encode(message));
    }

    public Message decode(byte[] data) throws Exception {
        String json = new String(data);
        ObjectNode node = this.mapper.readValue(json, ObjectNode.class);
        if (node.has(Message.JSON_MESSAGE_TYPE_PROPERTY_NAME)) {
            JsonNode packetProperty = node.get(Message.JSON_MESSAGE_TYPE_PROPERTY_NAME);
            MessageType packet = MessageType.valueOf(packetProperty.asInt());
            switch (packet) {
                case CONNECT:
                    return mapper.readValue(json, Connect.class);
                case CONNACK:
                    return mapper.readValue(json, Connack.class);
                case PUBLISH:
                    return mapper.readValue(json, Publish.class);
                case PUBACK:
                    return mapper.readValue(json, Puback.class);
                case PUBREC:
                    return mapper.readValue(json, Pubrec.class);
                case PUBREL:
                    return mapper.readValue(json, Pubrel.class);
                case PUBCOMP:
                    return mapper.readValue(json, Pubcomp.class);
                case SUBSCRIBE:
                    return mapper.readValue(json, Subscribe.class);
                case SUBACK:
                    return mapper.readValue(json, Suback.class);
                case UNSUBSCRIBE:
                    return mapper.readValue(json, Unsubscribe.class);
                case UNSUBACK:
                    return mapper.readValue(json, Unsuback.class);
                case PINGREQ:
                    return mapper.readValue(json, Pingreq.class);
                case PINGRESP:
                    return mapper.readValue(json, Pingresp.class);
                case DISCONNECT:
                    return mapper.readValue(json, Disconnect.class);
                default:
                    throw new MalformedMessageException("Wrong packet type while decoding message from json.");
            }
        }
        return null;
    }

    public Message messageObject(String json) throws Exception {
        return this.decode(json.getBytes());
    }

}