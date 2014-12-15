# Android controlled Arduino Robot Car

This project is about how to use an Android phone to control a robot car, which is based on an Arduino Uno board. After a couple of weeks I assembled a robot car and programmed a very simple App that can send commands to the Arduino with the help of Bluetooth. Let’s dive in!

Hardware Requirements:
*	Arduino Uno board
*	L298N Dual H-Bridge Motor Driver Shield
*	Bluetooth Transceiver Chip (Serial Bluetooth Modem)
*	Vehicle kit
*	A breadboard and some jumper wires

After some googling I found a good choice from the DX website. Actually these components can be ordered on some other websites ex. Sparkfun, Adafruit or Amazon.

Software Requirements: 
*	Arduino IDE (http://arduino.cc/en/main/software)

## Some notes about this project:
* ArduinoCarConsole: An Android APP to send commands from Android to Arduino.
* BluetoothCar: The complete source code of this robot car, it is written in C.
* L298N: Source code of Demos about how to use L298N motor shield.
* Documents: The complete document about this project and some program flowcharts.

## Screenshots about finished robot car and Android APP:
![Robot Car](/Images/ArduinoRobotCar.jpg "Arduino Robot Car")
![Android APP Screenshot 1](/Images/APP-01.png "Android APP Screenshot 1")
![Android APP Screenshot 2](/Images/APP-02.png "Android APP Screenshot 2")
