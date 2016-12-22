# IoTBroker.Cloud Android Client

IoTBroker.Cloud is an Android client which allows to connect to MQTT server via smartphone. IoTBroker.Cloud sticks to [MQTT 3.1.1](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.pdf) standards. 

## Features

* Clean / persistent session. When the client disconnects, its session state can be stored (if you set Clean session flag to false) or removed (if you set Clean session flag to true). The session state includes subscriptions and incoming QoS 1 and QoS 2 messages while the client is off.
* Last Will and Testament. This feature implies that if a client goes offline without sending DISCONNECT message (due to some failure), other clients will be notified about that.
* Keep Alive. If Keep Alive is higher than 0, the client and the server is constantly exchanging PING messages to make sure whether the opposite side is still available. 
* Retain messages. It allows to "attach" a message to a particular topic, so the new subscribers become immediately aware of the last known state of a topic.
* Assured message delivery. Each message is sent according to the level of Quality of Service (QoS). QoS has 3 levels:
- QoS 0 (At most once) — a message is sent only one time. The fastest message flow, but message loss may take place. 
- QoS 1 (At least once) — a message is sent at least one time. The message duplication may occur.  
- QoS 2 (Exactly once) — a message is sent exactly one time.  But QoS 2 message flow is the slowest one. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

We are going to consider an option of work with Android SDK in Eclipse since it remains a popular IDE among developers. Yet still you can work with Android Studio as well. 
Pay attention that supported version of Android is 4.2.2 or higher. 

The following programs should be installed before starting to clone IoTBroker.Cloud Android Client:

* JVM;
* JDK 8;
* Eclipse 4.6 (Neon);
* Android SDK;
* Android Development Tools (ADT).

### Installing

* First you have to clone [IotBroker.Cloud Android Client](https://github.com/mobius-software-ltd/iotbroker.cloud-android-client)
* USB debugging should be enabled on your cellphone. Dealing with most smartphones you can do it as follows: in Settings you can find About phone section, you should tap the version of your phone 7 times. Then you will be notified that Developer settings are available now. In Developer settings you can enable USB debugging.
* Next you should connect the smartphone to computer via USB cable. Make sure that it is not just charging but a storage is available as well.
* In order to open IoTBroker.Cloud in Eclipse you should go to File > New > Project... > Android Project From Existing Code.
* Then you can press Browse button to indicate the path to the root directory. In this case the root directory is iotbroker.cloud-android-client-master folder.
You should check all projects in Project to import. It is better to leave Copy projects into workspace option unchecked, because you do not need two copies of the project in this case.
If you already have working sets, you can choose whether to add this project to certain working set or not. If you do not have any working sets, you can leave Add project to working sets option unchecked.
* You should press Finish to complete the procedure. If you still cannot see your project in Eclipse, you should go to Window > Show view > Project Explorer.
* To install IoTBroker.Cloud on your smartphone, you should right-click on IoTBroker.Cloud and choose Run as Android application. In a few minutes IotBroker Android Client will be available for use as any other mobile application.
* When you finished with installation, you can open IoTBroker.Cloud on your smartphone and log in.
Please note that at this stage it is not possible to register as a client. You can log in to your existing account.
Now you are able to start exchanging messages with MQTT server.
Below you can find the explanation of MQTT part in IoTBroker.Cloud. It might be worth reading if you want to build your own app that would connect to MQTT server. 

## License

This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this software; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site: http://www.fsf.org



