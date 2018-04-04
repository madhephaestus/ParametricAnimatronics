#include <WiiChuck.h>
#include <WiFi.h>
#include <SimplePacketComs.h>
#include <EspWii.h>
Classic classic;
Servo pan;
Servo tilt;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(23, INPUT);           // set pin to input
  //classic.enableEncryption(true);
  classic.begin();
  pan.attach(2);
  tilt.attach(15);
}

void loop() {
  // put your main code here, to run repeatedly:
  classic.readData(); 
  //classic. printInputs();
  
  int panval = map(classic.getJoyXLeft(), 0, 63, 125,76 );
  int tiltval = map(classic.getJoyYLeft(), 0, 63, 125,65 );

  pan.write(panval);
  tilt.write(tiltval);
  Serial.println("Eye values "+String(panval)+" "+String(tiltval));
  //Serial.println(joyLeftY, DEC);
}
