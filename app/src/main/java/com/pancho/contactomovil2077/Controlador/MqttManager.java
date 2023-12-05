package com.pancho.contactomovil2077.Controlador;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttManager {
    private MqttClient mqttClient;

    public interface MqttConnectionListener {
        void onSuccess();
        void onFailure(Throwable exception);
    }

    public void connectToMqttServer(MqttConnectionListener connectionListener) {
        String broker = "tcp://localhost:1883";
        String clientId = "android-client";

        try {
            mqttClient = new MqttClient(broker, clientId, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttClient.connect();

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Manejar pérdida de conexión
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Manejar mensaje recibido
                    String payload = new String(message.getPayload());
                    // Hacer algo con el payload
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Manejar entrega completa (QoS 1 y 2)
                }
            });

            // Verificar si la conexión fue exitosa y notificar al listener
            if (mqttClient.isConnected() && connectionListener != null) {
                connectionListener.onSuccess();
                Log.d("MqttManager", "Conexión MQTT exitosa");
            } else if (connectionListener != null) {
                connectionListener.onFailure(new Exception("La conexión MQTT no fue exitosa"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (connectionListener != null) {
                connectionListener.onFailure(e);
            }
        }
    }

    public void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribe(topic);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Manejar pérdida de conexión
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Manejar mensaje recibido
                    String payload = new String(message.getPayload());
                    // Hacer algo con el payload
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Manejar entrega completa (QoS 1 y 2)
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                mqttClient.publish(topic, mqttMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
