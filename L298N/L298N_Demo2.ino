/*##### Motor Shield (L298N) #####*/
/* Use PWM to control speed */
/*
* Arduino Pin --> L298N
* 5 --> ENA
* 6 --> ENB
* 2 --> IN1
* 3 --> IN2
* 4 --> IN3
* 7 --> IN4
*/
const int ENA = 5;
const int ENB = 6; 
/*
* IN1: HIGH; IN2: LOW --> Direction 1 
* IN1: LOW; IN2: HIGH --> Direction 2
* IN3: HIGH; IN4: LOW --> Direction 1
* IN3: LOW; IN4: HIGH --> Direction2
*/
const int IN1 = 2; 
const int IN2 = 3;
const int IN3 = 4;
const int IN4 = 7;

void setup()
{
  pinMode(ENA, OUTPUT);
  pinMode(ENB, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  Serial.begin(9600);
}

void loop()
{
  Serial.println("Motor A & B: Direction 1 with Speed 100");
  MotorAB_Direction1(1000, 100);
  Serial.println("Motor A & B: Brake");
  MotorAB_Brake(1000);
  Serial.println("Motor A & B: Direction 2 with Speed 200");
  MotorAB_Direction2(1000, 200);
  Serial.println("Motor A & B: Brake");
  MotorAB_Brake(1000);
}

/* Speed range: 0 ~ 255 */
void MotorAB_Direction1(int milliseconds, int speed)
{
  if(speed < 0)
  {
    digitalWrite(ENA, HIGH);
    digitalWrite(ENB, HIGH);
  }
  else
  {
    analogWrite(ENA, speed);
    analogWrite(ENB, speed);
  }
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
  if (milliseconds > 0)
    delay(milliseconds);
}

/* Speed range: 0 ~ 255 */
void MotorAB_Direction2(int milliseconds, int speed)
{
  if(speed < 0)
  {
    digitalWrite(ENA, HIGH);
    digitalWrite(ENB, HIGH);
  }
  else
  {
    analogWrite(ENA, speed);
    analogWrite(ENB, speed);
  }
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
  if(milliseconds > 0)
    delay(milliseconds);
}

void MotorAB_Brake(int milliseconds)
{
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, HIGH);
  if(milliseconds > 0)
    delay(milliseconds);  
}
