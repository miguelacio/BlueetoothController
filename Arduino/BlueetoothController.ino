int pin1A = 10;
int pin2A = 9;

int pin1B = 6;
int pin2B = 7;

int pinArma = 4;
boolean estadoArma = false;

int estado;

void setup() {
  Serial.begin(9600);
  pinMode (pin1A, OUTPUT);
  pinMode (pin1B, OUTPUT);
  pinMode (pin2A, OUTPUT);
  pinMode (pin2B, OUTPUT);
  pinMode (pinArma, OUTPUT);
}


void loop()
{
  if (Serial.available()) {
    estado = Serial.read();
  }

  switch (estado) {
    case 2:
      if (estadoArma) {
        digitalWrite(pinArma, HIGH);
        delay (20);
        estadoArma = false;
        Serial.println("ARMA ACT");
      } else {
        digitalWrite(pinArma, LOW);
        delay (20);
        estadoArma = true;
        Serial.println("ARMA DESACT");
      }

      break;
    case 9:
      digitalWrite(pin1A, HIGH);
      digitalWrite(pin2A, HIGH);
      digitalWrite(pin1B, LOW);
      digitalWrite(pin2B, HIGH);
      delay (20);
      Serial.println("izquierda");
      break;
    case 3:
      digitalWrite(pin1A, LOW);
      digitalWrite(pin2A, HIGH);
      digitalWrite(pin1B, HIGH);
      digitalWrite(pin2B, HIGH);
      delay (20);
      Serial.println("Derecha");
      break;
    case 12:
      digitalWrite(pin1A, LOW);
      digitalWrite(pin2A, HIGH);
      digitalWrite(pin1B, LOW);
      digitalWrite(pin2B, HIGH);
      delay (20);
      Serial.println("Ariba");
      break;
    case 6:
      digitalWrite(pin1A, HIGH);
      digitalWrite(pin2A, LOW);
      digitalWrite(pin1B, HIGH);
      digitalWrite(pin2B, LOW);
      delay (20);
      Serial.println("Abajo");
      break;
    case 1:
      digitalWrite(pin1A, HIGH);
      digitalWrite(pin2A, HIGH);
      digitalWrite(pin1B, HIGH);
      digitalWrite(pin2B, HIGH);
      delay (20);
      Serial.println("STOP");
      break;

  }
  estado = 100;


}
