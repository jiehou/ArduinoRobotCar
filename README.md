Android controlled Arduino Robot Car

In this post I will write my hands-on experiences about how to use an Android phone to control a robot car, which is based on an Arduino Uno board. After a couple of weeks I assembled a robot car and programmed a very simple App that can send commands to the Arduino with the help of Bluetooth. Let’s dive in!

Hardware Requirements:
•	Arduino Uno board
•	L298N Dual H-Bridge Motor Driver Shield
•	Bluetooth Transceiver Chip (Serial Bluetooth Modem)
•	Vehicle kit
•	A breadboard and some jumper wires

After some googling I found a good choice from the DX website. Actually these components can be ordered on some other websites ex. Sparkfun, Adafruit or Amazon.
Software Requirements: 
•	Arduino IDE (http://arduino.cc/en/main/software)

The complete project will be explained step by step: in the first part I will introduce Arduino very shortly. L298N Motor Driver Shield will be explained in the second part. Next I will describe how to use our Bluetooth module. After we know how to program Motor and Bluetooth, we can put them together to achieve the final goal.
