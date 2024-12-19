#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <DHT.h>
#include <espnow.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

// WiFi credentials
const char* ssid = "";
const char* password = "";

// NTP Client setup
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

// Function to generate ISO 8601 timestamp
String getISOTimestamp() {
  time_t now = timeClient.getEpochTime();
  struct tm* p_tm = gmtime(&now);

  char buffer[30];
  strftime(buffer, 30, "%Y-%m-%dT%H:%M:%S.000+00:00", p_tm);

  return String(buffer);
}

// Server URL
const char* serverURL = "http://ec2-13-61-40-192.eu-north-1.compute.amazonaws.com:8080/nodes/3c:71:bf:37:4d:e8/readings";

// DHT sensor configuration
#define DHTPIN 4      // Use GPIO 4 (D2) for the DHT sensor
#define DHTTYPE DHT11 // DHT 11
DHT dht(DHTPIN, DHTTYPE);

// Gas sensor pin (MQ2)
int mq2Pin = A0;  // MQ2 gas sensor connected to analog pin A0

// Camera sensor pin
#define cameraPin 5  // Replace with the GPIO pin connected to the camera input (D1 for example)

// Peer MAC address (replace with the other ESP's MAC address)
uint8_t peerAddress[] = {0x5C, 0xCF, 0x7F, 0x89, 0x31, 0xCD};

// Structure for token message
typedef struct struct_token {
  bool hasToken;  // Token ownership flag
} token_message;

token_message tokenData;
bool hasToken = false;  // Initial token ownership flag

// Ping interval in milliseconds
unsigned long PING_INTERVAL = 60000;
unsigned long lastActionTime = 0;

// Callback for when data is sent
void onDataSent(uint8_t *mac_addr, uint8_t sendStatus) {
  Serial.print("Token Delivery Status: ");
  if (sendStatus == 0) {
    Serial.println("Success");
  } else {
    Serial.println("Fail");
  }
}

// Callback for when data is received
void onDataReceived(uint8_t *mac_addr, uint8_t *incomingData, uint8_t len) {
  token_message receivedToken;
  memcpy(&receivedToken, incomingData, sizeof(token_message));

  if (receivedToken.hasToken) {
    Serial.println("Token received!");
    hasToken = true;
  }
}

void setup() {
  Serial.begin(9600);
  delay(1000);

  dht.begin();
  WiFi.mode(WIFI_STA);

  pinMode(cameraPin, INPUT);  // Set the camera pin as input

  Serial.print("Connecting to WiFi...");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi!");

  timeClient.begin();
  timeClient.setTimeOffset(0);

  if (esp_now_init() != 0) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }

  esp_now_set_self_role(ESP_NOW_ROLE_COMBO);
  esp_now_register_send_cb(onDataSent);
  esp_now_register_recv_cb(onDataReceived);

  esp_now_add_peer(peerAddress, ESP_NOW_ROLE_COMBO, 1, NULL, 0);

  hasToken = (ESP.getChipId() % 2 == 0);  // Assign token ownership based on chip ID
}

void loop() {
  timeClient.update();

  if (hasToken && millis() - lastActionTime >= PING_INTERVAL) {
    // Read sensor data
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();
    int gasSensorReading = analogRead(mq2Pin);
    int cameraReading = digitalRead(cameraPin);  // Read the camera pin (0 or 1)

    if (isnan(temperature) || isnan(humidity)) {
      Serial.println("Failed to read from DHT sensor!");
      delay(5000);
      return;
    }

    // Print debug information
    Serial.print("Temperature: ");
    Serial.print(temperature);
    Serial.print("°C, Humidity: ");
    Serial.print(humidity);
    Serial.print("%, Gas Reading: ");
    Serial.print(gasSensorReading);
    Serial.print(", Camera Reading: ");
    Serial.println(cameraReading);

    // Create JSON payload
    String isoTimestamp = getISOTimestamp();
    String payload = "{";
    payload += "\"temperature\":" + String(temperature) + ",";
    payload += "\"humidity\":" + String(humidity) + ",";
    payload += "\"gasSensorReading\":" + String(gasSensorReading) + ",";
    payload += "\"cameraReading\":" + String(cameraReading) + ",";
    payload += "\"timestamp\":\"" + isoTimestamp + "\"";
    payload += "}";

    // Send POST request
    PING_INTERVAL = sendPostRequest(payload);

    // Pass the token to the other ESP
    tokenData.hasToken = true;
    esp_now_send(peerAddress, (uint8_t *)&tokenData, sizeof(token_message));
    hasToken = false;

    lastActionTime = millis();
  }
}

unsigned long sendPostRequest(const String& payload) {
  unsigned long refreshRate = 60000;
  if (WiFi.status() == WL_CONNECTED) {
    WiFiClient client;
    HTTPClient http;

    http.begin(client, serverURL);
    http.addHeader("Content-Type", "application/json");

    int httpCode = http.POST(payload);

    if (httpCode > 0) {
      Serial.printf("POST request sent. Response Code: %d\n", httpCode);
      String response = http.getString();
      refreshRate = response.toInt();
      Serial.println("Response:");
      Serial.println(refreshRate);
    } else {
      Serial.printf("POST request failed, error: %s\n", http.errorToString(httpCode).c_str());
    }

    http.end();
  } else {
    Serial.println("WiFi not connected!");
  }

  return refreshRate;
}
