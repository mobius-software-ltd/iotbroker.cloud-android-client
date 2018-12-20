# iotbroker.cloud-android-client

### Project description

IoTBroker.cloud Android Client is an application that allows you to connect to the server using MQTT, MQTT-SN, 
AMQP or COAP protocols. IoTBroker.cloud Android Client gives the opportunity to exchange messages using protocols 
mentioned above. Your data can be also encrypted with **TLS** or **DTLS** secure protocols.   

Below you can find a brief description of each protocol that can help you make your choice. 
If you need to get more information, you can find it in our [blog](https://www.iotbroker.cloud/clientApps/Android/MQTT).
 
**MQTT** is a lightweight publish-subscribe based messaging protocol built for use over TCP/IP.  
MQTT was designed to provide devices with limited resources an easy way to communicate effectively. 
You need to familiarize yourself with the following MQTT features such as frequent communication drops, low bandwidth, 
low storage and processing capabilities of devices. 

Frankly, **MQTT-SN** is very similar to MQTT, but it was created for avoiding the potential problems that may occur at WSNs. 

Creating large and complex systems is always associated with solving data exchange problems between their various nodes. 
Additional difficulties are brought by such factors as the requirements for fault tolerance, 
he geographical diversity of subsystems, the presence a lot of nodes interacting with each others. 
The **AMQP** protocol was developed to solve all these problems, which has three basic concepts: 
exchange, queue and routing key. 

If you need to find a simple solution, it is recommended to choose the **COAP** protocol. 
The CoAP is a specialized web transfer protocol for use with constrained nodes and constrained (e.g., low-power, lossy) 
networks. It was developed to be used in very simple electronics devices that allows them to communicate interactively 
over the Internet. It is particularly targeted for small low power sensors, switches, valves and similar components 
that need to be controlled or supervised remotely, through standard Internet networks.   
 
### Prerequisites 
[Android Studio 3.2.1](https://developer.android.com/studio/#Other) should be installed before starting to clone 
iotbroker.cloud-android-client.. 

### Installation 

* First, you have to clone IoTBroker.Cloud-android-client;

* Next you should open Android Studio. Then you should go **File > New > Import Prodject** and choose 
the path to the cloned project in the root directory.

* Note that Android Studio program guides you, which programs should be installed for development and 
will suggest to install them. If you have any recommendation to install programs, you should do it. 

* Then press the "Run" button, where you have to click "Run app" and choose "Virtual Device" option.  
You also can create virtual device, to create it click "Create Virtual Device"; 

* Be patient! It may take a few MINUTES to download the emulator (virtual device, which was chosen); 

 In case of successful emulator installation, the Login Page in the form of pop-up window is appeared.
 This Form must be filled in according to your requirements. Now you are able to log in and start exchanging messages 
 with server.  

Please note that at this stage it is not possible to register as a client. You can only log in to your existing account. 

IoTBroker.Cloud C Client is developed by [Mobius Software](http://mobius-software.com/).
