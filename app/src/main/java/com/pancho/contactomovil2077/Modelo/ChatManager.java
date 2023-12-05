package com.pancho.contactomovil2077.Modelo;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pancho.contactomovil2077.Controlador.MqttManager;

public class ChatManager {
    private static ChatManager instance;
    private MqttManager mqttManager;
    private Context context;

    private ChatManager(Context context) {
        this.context = context;
        // Inicializar el objeto MqttManager
        mqttManager = new MqttManager();
        mqttManager.connectToMqttServer(new MqttManager.MqttConnectionListener() {
            @Override
            public void onSuccess() {
                showToast("Conexión exitosa con MQTT");
            }

            @Override
            public void onFailure(Throwable exception) {
                showToast("Error en la conexión con MQTT");
            }
        });
    }

    public static synchronized ChatManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatManager(context);
        }
        return instance;
    }

    private void showToast(String message) {
        // Mostrar un Toast con el mensaje proporcionado
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void subscribeToChat(String chatTopic) {
        mqttManager.subscribeToTopic(chatTopic);
    }

    public void sendMessage(String topic, String message) {
        mqttManager.publishMessage(topic, message);
        saveMessageToFirebase(topic, message); // Guardar el mensaje en Firebase al enviarlo
    }

    public void disconnect() {
        mqttManager.disconnect();
    }

    private void saveMessageToFirebase(String chatTopic, String message) {
        // Aquí debes implementar la lógica para guardar el mensaje en Firebase Realtime Database
        // Puedes usar la referencia a la base de datos de Firebase y agregar el mensaje al nodo correspondiente.

        // Ejemplo:
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("messages").child(chatTopic);
        String messageId = messagesRef.push().getKey();
        messagesRef.child(messageId).setValue(message);
    }
}
