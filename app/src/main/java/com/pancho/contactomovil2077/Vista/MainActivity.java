package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pancho.contactomovil2077.Modelo.Contacto;
import com.pancho.contactomovil2077.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int AGREGAR_CONTACTO_REQUEST_CODE = 1;
    private List<Contacto> listaDeContactos;
    private ContactosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaDeContactos = new ArrayList<>();  // Inicializar la lista de contactos
        adapter = new ContactosAdapter(listaDeContactos, new ContactosAdapter.OnContactoClickListener() {
            @Override
            public void onContactoClick(Contacto contacto) {
                abrirChatActivity(contacto);
            }

            @Override
            public void onAgregarContactoClick(Contacto contacto) {
                // Manejar el clic del botón "Agregar Contacto"
            }
        }, false);


        RecyclerView recyclerView = findViewById(R.id.rvChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button btnPerfil2 = findViewById(R.id.btnPerfil2);
        Button btnAgregarContacto = findViewById(R.id.btnAgregarContacto);

        btnPerfil2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditarPerfil.class);
                startActivity(i);
            }
        });

        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar ListaContactosActivity y esperar un resultado
                Intent intent = new Intent(MainActivity.this, ListaContactosActivity.class);
                startActivityForResult(intent, AGREGAR_CONTACTO_REQUEST_CODE);
            }
        });

        // Cargar la lista de contactos desde Firebase
        cargarListaDeContactosDesdeFirebase();
    }

    private void abrirChatActivity(Contacto contacto) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        // Puedes pasar datos adicionales a ChatActivity si es necesario
        intent.putExtra("nombreContacto", contacto.getNombre());
        startActivity(intent);
    }

    private void cargarListaDeContactosDesdeFirebase() {
        DatabaseReference listaContactosRef = FirebaseDatabase.getInstance().getReference().child("ListaDeContactos");

        listaContactosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaDeContactos.clear();  // Limpiar la lista actual

                for (DataSnapshot contactoSnapshot : dataSnapshot.getChildren()) {
                    Contacto contacto = contactoSnapshot.getValue(Contacto.class);
                    if (contacto != null) {
                        listaDeContactos.add(contacto);
                    }
                }

                // Notificar al adaptador sobre el cambio en la lista
                adapter.actualizarLista(listaDeContactos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores, si es necesario
                Toast.makeText(MainActivity.this, "Error al cargar la lista de contactos desde Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AGREGAR_CONTACTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // No es necesario realizar cambios aquí, la lista se actualizará automáticamente desde Firebase
        }
    }
}
