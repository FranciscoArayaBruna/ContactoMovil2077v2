package com.pancho.contactomovil2077.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pancho.contactomovil2077.Controlador.MqttManager;
import com.pancho.contactomovil2077.Modelo.ChatManager;
import com.pancho.contactomovil2077.Modelo.Contacto;
import com.pancho.contactomovil2077.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private MqttManager mqttManager;
    private ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mqttManager = new MqttManager();
        chatManager = ChatManager.getInstance(getApplicationContext()); // O pasa el contexto relevante

        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent != null) {
            Contacto contacto = intent.getParcelableExtra("contacto");
            String chatTopic = intent.getStringExtra("chatTopic");

            if (contacto != null && chatTopic != null) {
                // Resto del código...

                // Suscribirse al tema específico del chat
                chatManager.subscribeToChat(chatTopic);

                // Resto del código para manejar la interfaz de chat
            }
        }

        // Configurar la interfaz de usuario y los eventos
        setUpUI();
    }

    private void setUpUI() {
        RecyclerView rvChatMensajes = findViewById(R.id.rvChatMensajes);
        EditText inputMensaje = findViewById(R.id.inputMensaje);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        // Configurar el RecyclerView y su adaptador (si aún no lo has hecho)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatMensajes.setLayoutManager(layoutManager);

        // Configurar el evento de clic para el botón de enviar
        btnEnviar.setOnClickListener(view -> {
            // Obtener el mensaje del EditText
            String mensaje = inputMensaje.getText().toString();

            // Publicar el mensaje en el tema MQTT
            chatManager.sendMessage("chat/messages", mensaje);

            // Limpiar el EditText después de enviar el mensaje
            inputMensaje.setText("");
        });

        // Suscribirse a los mensajes en Firebase para actualizar el RecyclerView
        subscribeToFirebaseMessages("chat/messages", rvChatMensajes);
    }

    private void subscribeToFirebaseMessages(String chatTopic, RecyclerView recyclerView) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("messages").child(chatTopic);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lista para almacenar los mensajes
                    List<String> mensajes = new ArrayList<>();

                    // Recorrer los mensajes en Firebase
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        String mensaje = messageSnapshot.getValue(String.class);
                        if (mensaje != null) {
                            mensajes.add(mensaje);
                        }
                    }

                    // Actualizar el RecyclerView con los mensajes de Firebase
                    actualizarRecyclerView(mensajes, recyclerView);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar errores, si es necesario
                Toast.makeText(ChatActivity.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarRecyclerView(List<String> mensajes, RecyclerView recyclerView) {
        // Aquí debes implementar la lógica para actualizar el RecyclerView con los mensajes recibidos
        // Puedes usar el adaptador del RecyclerView y notificar los cambios en la lista de mensajes.

        // Ejemplo:
        // Crear un adaptador (debes tener uno personalizado que maneje los mensajes)
        // Aquí asumimos que tienes un adaptador llamado MensajesAdapter
        MensajesAdapter mensajesAdapter = new MensajesAdapter(mensajes);
        recyclerView.setAdapter(mensajesAdapter);
    }

}