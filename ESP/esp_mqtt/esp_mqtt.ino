#include <Arduino.h>
#include <ESP8266WiFi.h>  // Include this for ESP8266
// #include <WiFi.h>       // Include this for ESP32
#include <PubSubClient.h>

// WiFi credentials
const char *ssid = "Tenda_6B8D00";
// const char *password = "eoghan123";

// MQTT Broker settings
const char* mqtt_server = "930094acb7da4acfbf5761b3ac2c7c90.s1.eu.hivemq.cloud";
const int mqtt_port = 8883;
const char* mqtt_user = "";
const char* mqtt_password = "";

// MQTT Topics
const char* light_topic = "a00287845/device/android/sensors/light";
const char* proximity_topic = "a00287845/device/android/sensors/proximity";

WiFiClient espClient;
PubSubClient client(espClient);

const int ledPin = LED_BUILTIN;  // Use built-in LED pin

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to WiFi SSID: ");
  Serial.println(ssid);

  WiFi.begin(ssid);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void blinkLED(int count, int duration) {
  for (int i = 0; i < count; i++) {
    digitalWrite(ledPin, LOW);  // Turn on LED (active low)
    Serial.println("LED ON");
    delay(duration);
    digitalWrite(ledPin, HIGH); // Turn off LED
    Serial.println("LED OFF");
    delay(duration);
  }
}

void reconnect() {
  while (!client.connected()) {
    Serial.println("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect("ArduinoClient", mqtt_user, mqtt_password)) {
      Serial.println("Connected to MQTT Broker!");
      client.subscribe(light_topic);
      client.subscribe(proximity_topic);
      Serial.println("Subscribed to topics:");
      Serial.println(light_topic);
      Serial.println(proximity_topic);
      blinkLED(3, 1000);  // Blink 3 times with 1000ms on and 1000ms off
    } else {
      Serial.print("Failed, rc=");
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
  Serial.print("Payload: ");
  Serial.println(messageTemp);

  if (String(topic) == light_topic) {
    int lightValue = messageTemp.toInt();
    Serial.print("Light value: ");
    Serial.println(lightValue);
    if (lightValue > 400) {  // Example threshold
      digitalWrite(ledPin, LOW);  // Turn on LED (active low)
      Serial.println("LED ON due to light value above threshold");
    } else {
      digitalWrite(ledPin, HIGH);  // Turn off LED
      Serial.println("LED OFF as light value below threshold");
    }
  } else if (String(topic) == proximity_topic) {
    int proximityValue = messageTemp.toInt();
    Serial.print("Proximity value: ");
    Serial.println(proximityValue);
    if (proximityValue < 5) {  // Example proximity near threshold
      digitalWrite(ledPin, LOW);  // Turn on LED (active low)
      Serial.println("LED ON due to proximity close");
    } else {
      digitalWrite(ledPin, HIGH);  // Turn off LED
      Serial.println("LED OFF as proximity not close");
    }
  }
}

void setup() {
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH); // Turn off LED initially (active low)

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
