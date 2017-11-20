package com.iotbroker.cloud;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class GreenDaoGenerator {

    private static Entity topicEntitty;
    private static Entity messageEntity;

    public static void main(String[] args) {

        Schema schema = new Schema(3, "com.mobius.software.android.iotbroker.main.dal");

        createMessageEntity(schema);
        createTopicEntity(schema);
        createAccoutEntity(schema);

        try {
            new DaoGenerator().generateAll(schema, "./app/src/main/java");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createMessageEntity(Schema schema) {

        messageEntity = schema.addEntity("Messages");

        messageEntity.addIdProperty().autoincrement().primaryKey();
        messageEntity.addStringProperty("messageItem");
        messageEntity.addIntProperty("qos");
        messageEntity.addBooleanProperty("isIncoming");
        messageEntity.addStringProperty("topicName");
    }

    private static void createTopicEntity(Schema schema) {

        topicEntitty = schema.addEntity("Topics");

        topicEntitty.addIdProperty().autoincrement().primaryKey();
        topicEntitty.addIntProperty("qos");
        topicEntitty.addStringProperty("topicName");
    }

    private static void createAccoutEntity(Schema schema) {

        Entity accountEntity = schema.addEntity("Accounts");

        accountEntity.addIdProperty().autoincrement().primaryKey();
        accountEntity.addIntProperty("protocolType");
        accountEntity.addStringProperty("userName");
        accountEntity.addStringProperty("password");
        accountEntity.addStringProperty("clientID");
        accountEntity.addStringProperty("serverHost");
        accountEntity.addIntProperty("port");
        accountEntity.addBooleanProperty("cleanSession");
        accountEntity.addIntProperty("keepAlive");
        accountEntity.addStringProperty("will");
        accountEntity.addStringProperty("willTopic");
        accountEntity.addIntProperty("qos");
        accountEntity.addBooleanProperty("isDefault");
        accountEntity.addBooleanProperty("isRetain");

        Property topicAccountID = topicEntitty.addLongProperty("accountID").notNull().getProperty();
        ToMany topicAccount = accountEntity.addToMany(topicEntitty, topicAccountID);
        topicAccount.setName("topics");             // Optional

        Property messageAccountID = messageEntity.addLongProperty("accountID").notNull().getProperty();
        ToMany messageAccount = accountEntity.addToMany(messageEntity, messageAccountID);
        messageAccount.setName("messageAccount");   // Optional

    }

}
