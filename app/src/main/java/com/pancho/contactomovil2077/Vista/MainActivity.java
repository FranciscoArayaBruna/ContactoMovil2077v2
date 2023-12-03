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

import com.pancho.contactomovil2077.Modelo.Contacto;
import com.pancho.contactomovil2077.R;
import com.pancho.contactomovil2077.Vista.ContactosAdapter;

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
        adapter = new ContactosAdapter(listaDeContactos, null);  // Se actualizará más tarde

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AGREGAR_CONTACTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtener el contacto seleccionado desde el resultado
            Contacto nuevoContacto = data.getParcelableExtra("nuevoContacto");

            // Verificar si el contacto ya está en la lista
            if (!listaDeContactos.contains(nuevoContacto)) {
                // Agregar el nuevo contacto a la lista
                listaDeContactos.add(nuevoContacto);

                // Actualizar el RecyclerView
                adapter.actualizarLista(listaDeContactos);
            } else {
                Toast.makeText(this, "El usuario ya está en la lista", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


