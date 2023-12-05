package com.pancho.contactomovil2077.Vista;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pancho.contactomovil2077.Controlador.MqttManager;
import com.pancho.contactomovil2077.R;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class ChatActivity extends AppCompatActivity {

    private MqttManager mqttManager;
    private MqttClient mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializar el gestor MQTT
        mqttManager = new MqttManager();

        // Conectar al servidor MQTT
        mqttManager.connectToMqttServer();

        // Suscribirse a un tema específico (ajusta el tema según tus necesidades)
        mqttManager.subscribeToTopic("chat/messages");

        // Configurar la interfaz de usuario y los eventos
        setUpUI();
    }

    private void setUpUI() {
        RecyclerView rvChatMensajes = findViewById(R.id.rvChatMensajes);
        EditText inputMensaje = findViewById(R.id.inputMensaje);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        // Configurar el RecyclerView y su adaptador (si aún no lo has hecho)
        // ...

        // Configurar el evento de clic para el botón de enviar
        btnEnviar.setOnClickListener(view -> {
            // Obtener el mensaje del EditText
            String mensaje = inputMensaje.getText().toString();

            // Publicar el mensaje en el tema MQTT
            mqttManager.publishMessage("chat/messages", mensaje);

            // Limpiar el EditText después de enviar el mensaje
            inputMensaje.setText("");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Desconectar del servidor MQTT al salir de la actividad
        mqttManager.disconnect();
    }
}
