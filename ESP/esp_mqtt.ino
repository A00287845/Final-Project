#include <Arduino.h>
#include <ESP8266WiFi.h>  // Include this for ESP8266
// #include <WiFi.h>       
#include <PubSubClient.h>

// WiFi credentials
const char *ssid = "Redmi 10";
const char *password = "*******";

// MQTT Broker settings
const char* mqtt_server = "930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud";
const int mqtt_port = 8883;
const char* mqtt_user = "******";
const char* mqtt_password = "*******";

// MQTT Topics
const char* light_topic = "a00287845/device/android/sensors/light";
const char* proximity_topic = "a00287845/device/android/sensors/proximity";

WiFiClient espClient;
PubSubClient client(espClient);

const int ledPin = LED_BUILTIN;  // Use built-in LED pin

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("WiFi connected - IP address: ");
  Serial.println(WiFi.localIP());
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("ArduinoClient", mqtt_user, mqtt_password)) {
      Serial.println("connected");
      client.subscribe(light_topic);
      client.subscribe(proximity_topic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");

  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    messageTemp += (char)payload[i];
  }
  Serial.println(messageTemp);

  if (String(topic) == light_topic) {
    int lightValue = messageTemp.toInt();
    if (lightValue > 1100) {  
      digitalWrite(ledPin, LOW);  
    } else {
      digitalWrite(ledPin, HIGH);  
    }
  } else if (String(topic) == proximity_topic) {
    int proximityValue = messageTemp.toInt();
    if (proximityValue < 5) { 
      digitalWrite(ledPin, LOW);  
    } else {
      digitalWrite(ledPin, HIGH); 
    }
  }
}

void setup() {
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH); 

  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
}
