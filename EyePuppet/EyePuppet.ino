#include <WiiChuck.h>
#include <WiFi.h>
#include <SimplePacketComs.h>
#include <EspWii.h>
Classic classic;
Servo pan;
Servo tilt;
Servo pan2;
Servo tilt2;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(23, INPUT);           // set pin to input
  //classic.enableEncryption(true);
  classic.begin();
  pan.attach(2);
  tilt.attach(15);
  pan2.attach(4);
  tilt2.attach(16);
}

void loop() {
  // put your main code here, to run repeatedly:
  classic.readData(); 
  //classic. printInputs();
  
  int panval = map(classic.getJoyXLeft(), 0, 63, 125,76 );
  int tiltval = map(classic.getJoyYLeft(), 0, 63, 125,65 );

  int panval2 = map(classic.getJoyXRight(), 0, 31, 125,76 );
  int tiltval2 = map(classic.getJoyYRight(), 0, 31, 125,65 );

  pan.write(panval);
  pan2.write(panval2);
  tilt.write(tiltval);
  tilt2.write(tiltval2);
  Serial.println("Eye values "+String(panval)+" "+String(tiltval)+" "+String(panval2)+" "+String(tiltval2));
  //Serial.println(joyLeftY, DEC);
}
