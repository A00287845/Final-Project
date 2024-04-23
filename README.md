# Final Project README

## Project Overview

JIRA - https://a00287845.atlassian.net/jira/software/projects/FAP/boards/2


This IoT system integrates various technologies including Node-RED on a Raspberry Pi, an ESP8266 module, and an Android application. It leverages MQTT for real-time data transfer, allowing monitoring and control over environmental and motion data through interconnected devices.

### System Architecture

- **Node-RED on Raspberry Pi**: Reads sensor data from the Sense HAT and publishes it to a cloud-based MQTT broker.
- **ESP8266 Module**: Subscribes to MQTT topics to react to motion data from the smartphone by controlling LED indicators.
- **Android Application**: Manages MQTT subscriptions and publications, displays sensor data, and sends text messages to the Sense HAT.

### Features

1. **MQTT Pub/Sub System**: Ensures seamless communication between devices.
2. **Node-RED Dashboard**: Visualizes real-time data from Sense HAT sensors.
3. **Android App Activities**:
    - Sensor management for light and proximity.
    - Text input for message display on the Sense HAT.
    - Data visualization using various UI components.

## Installation

### Prerequisites

- Raspberry Pi with Node-RED.
- ESP8266 module prepared for MQTT.
- Android Studio for app development.
- Access to a cloud-based MQTT broker.

### Setup and Configuration

1. **Raspberry Pi**:
    - Install and configure Node-RED.
    - Set up flows for sensor data and MQTT publication.
2. **ESP8266**:
    - Program with MQTT subscription capabilities.
3. **Android App**:
    - Configure MQTT connections.

## Usage

1. Launch Node-RED on the Raspberry Pi, import the flow and start it.
2. Power up the ESP8266.
3. Operate the Android application to interact with the system. Connect to MQTT.

## Contact Information

- **Maintainer**: [Eoghan Sullivan]
- **Email**: [A00287845@student.tus.ie]


## Architecture Diagram
![System_architecture_diagram.png](Docs%2FSystem_architecture_diagram.png)