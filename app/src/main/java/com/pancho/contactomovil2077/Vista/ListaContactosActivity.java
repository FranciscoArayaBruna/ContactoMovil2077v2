package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pancho.contactomovil2077.Modelo.Contacto;
import com.pancho.contactomovil2077.R;

import java.util.ArrayList;
import java.util.List;

public class ListaContactosActivity extends AppCompatActivity implements ContactosAdapter.OnContactoClickListener {

    private List<Contacto> listaDeContactos;
    private ContactosAdapter adapter;
    private EditText txtBuscarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        txtBuscarUsuario = findViewById(R.id.txtBuscarUsuario);

        listaDeContactos = new ArrayList<>();
        cargarTodosLosUsuarios();
        configurarRecyclerView();

        ImageButton btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(view -> buscarUsuario());

        Button btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> {
            Intent i = new Intent(ListaContactosActivity.this, EditarPerfil.class);
            startActivity(i);
        });

        Button btnChat = findViewById(R.id.btnChat);
        btnChat.setOnClickListener(v -> {
            Intent i = new Intent(ListaContactosActivity.this, MainActivity.class);
            startActivity(i);
        });

        RecyclerView recyclerView = findViewById(R.id.rvLista);
        recyclerView.setOnTouchListener((v, event) -> {
            adapter.deseleccionarItem();
            return false;
        });
    }

    private void configurarRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvLista);
        adapter = new ContactosAdapter(listaDeContactos, this, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void cargarTodosLosUsuarios() {
        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuario");

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Contacto> listaDeContactos = new ArrayList<>();
                    FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Contacto contacto = userSnapshot.getValue(Contacto.class);

                        // Verificar que el contacto no sea el usuario actual
                        if (contacto != null && usuarioActual != null
                                && !TextUtils.equals(contacto.getCorreo(), usuarioActual.getEmail())) {
                            listaDeContactos.add(contacto);
                        }
                    }

                    // Mostrar todos los usuarios en el RecyclerView
                    adapter.actualizarLista(listaDeContactos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores, si es necesario
                Toast.makeText(ListaContactosActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarUsuario() {
        EditText txtBuscarUsuario = findViewById(R.id.txtBuscarUsuario);
        String nombreApellidoBuscado = txtBuscarUsuario.getText().toString().trim();

        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuario");

        // Filtrar usuarios por nombre y opcionalmente por apellido
        Query query;
        if (nombreApellidoBuscado.contains(" ")) {
            // Si hay espacio, asumimos que se proporcionó un nombre y un apellido
            String[] partesNombreApellido = nombreApellidoBuscado.split("\\s+");
            String nombreBuscado = partesNombreApellido[0];
            String apellidoBuscado = partesNombreApellido[1];

            // Hacer la consulta insensible a mayúsculas y minúsculas para nombre y apellido
            query = usuarioRef.orderByChild("nombre").startAt(nombreBuscado).endAt(nombreBuscado + "\uf8ff")
                    .orderByChild("apellido").startAt(apellidoBuscado).endAt(apellidoBuscado + "\uf8ff");
        } else {
            // Si no hay espacio, asumimos que solo se proporcionó un nombre
            // Hacer la consulta insensible a mayúsculas y minúsculas para el nombre
            query = usuarioRef.orderByChild("nombre").startAt(nombreApellidoBuscado).endAt(nombreApellidoBuscado + "\uf8ff");
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Contacto> resultados = new ArrayList<>();
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Contacto contacto = userSnapshot.getValue(Contacto.class);
                        if (contacto != null) {
                            resultados.add(contacto);
                        }
                    }

                    // Obtener la lista completa de usuarios
                    List<Contacto> listaCompleta = adapter.getListaDeContactos();

                    // Limpiar la lista antes de agregar los resultados de la búsqueda
                    listaCompleta.clear();

                    // Agregar los resultados de la búsqueda a la lista
                    listaCompleta.addAll(resultados);

                    // Actualizar el RecyclerView con la lista combinada
                    adapter.actualizarLista(listaCompleta);

                    // Vaciar el contenido del EditText
                    txtBuscarUsuario.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores, si es necesario
                Toast.makeText(ListaContactosActivity.this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarUsuarioDesdeBusqueda(Contacto contacto) {
        // Obtener el contacto seleccionado en el RecyclerView
        Contacto contactoSeleccionado = contacto;

        if (contactoSeleccionado != null) {
            // Obtener la referencia a la ubicación de la lista de contactos en la base de datos
            DatabaseReference listaContactosRef = FirebaseDatabase.getInstance().getReference().child("ListaDeContactos");

            listaContactosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Contacto> listaDeContactosFirebase = new ArrayList<>();

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot contactoSnapshot : dataSnapshot.getChildren()) {
                            Contacto contactoFirebase = contactoSnapshot.getValue(Contacto.class);
                            if (contactoFirebase != null) {
                                listaDeContactosFirebase.add(contactoFirebase);
                            }
                        }
                    }

                    if (listaDeContactosFirebase.contains(contactoSeleccionado)) {
                        // El contacto ya está en la lista, mostrar mensaje
                        Toast.makeText(ListaContactosActivity.this, "El contacto ya está en la lista", Toast.LENGTH_SHORT).show();
                    } else {
                        // Agregar el nuevo contacto a la lista local
                        listaDeContactos.add(contactoSeleccionado);

                        // Agregar el nuevo contacto a la lista de Firebase
                        listaDeContactosFirebase.add(contactoSeleccionado);

                        // Actualizar la lista de contactos en Firebase
                        listaContactosRef.setValue(listaDeContactosFirebase);

                        // Notificar al usuario sobre el éxito
                        Toast.makeText(ListaContactosActivity.this, "Contacto agregado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores, si es necesario
                    Toast.makeText(ListaContactosActivity.this, "Error al agregar usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ListaContactosActivity.this, "Seleccione un usuario de la lista", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onContactoClick(Contacto contacto) {
        Log.d("ListaContactosActivity", "Contacto clickeado: " + contacto.getNombre());

        // Obtener la posición del contacto clickeado
        int position = listaDeContactos.indexOf(contacto);

        // Cambiar la posición seleccionada y notificar al adaptador
        adapter.actualizarPosicionSeleccionada(position);
        abrirPerfilContacto(contacto);
    }

    @Override
    public void onAgregarContactoClick(Contacto contacto) {
        // Lógica para manejar el clic del botón "Agregar contacto"
        // Esto se ejecutará cuando el botón se presiona en el adaptador
        agregarUsuarioDesdeBusqueda(contacto);
    }

    private void abrirPerfilContacto(Contacto contacto) {
        // Implementa esta función para abrir el perfil del contacto
        // Puede ser una nueva actividad o fragmento que muestra detalles del contacto
        // También puedes implementar aquí la opción de agregar nuevos contactos
    }

    // Función para obtener el correo del usuario (reemplaza con la lógica real)
    private String obtenerCorreoUsuario() {
        // Lógica para obtener el correo del usuario desde Firebase Auth
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            return usuarioActual.getEmail();
        } else {
            // Manejo de la situación en la que no hay usuario autenticado
            return null;
        }
    }
}
