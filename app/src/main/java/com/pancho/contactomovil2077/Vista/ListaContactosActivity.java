package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

        listaDeContactos = obtenerListaDeContactos();
        cargarTodosLosUsuarios();
        configurarRecyclerView();

        ImageButton btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(view -> buscarUsuario());

        Button btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> {
            Intent i = new Intent(ListaContactosActivity.this, EditarPerfil.class);
            startActivity(i);
        });

        ImageButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Contacto contactoSeleccionado = adapter.getItemSeleccionado();
            if (contactoSeleccionado != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("nuevoContacto", contactoSeleccionado);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Ningún contacto seleccionado", Toast.LENGTH_SHORT).show();
            }
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
        adapter = new ContactosAdapter(listaDeContactos, this);
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

    private void agregarUsuarioDesdeBusqueda() {
        // Obtener el contacto seleccionado en el RecyclerView
        Contacto contactoSeleccionado = adapter.getItemSeleccionado();

        if (contactoSeleccionado != null) {
            // Verificar si el usuario ya está en la lista
            if (!listaDeContactos.contains(contactoSeleccionado)) {
                // Establecer el resultado y finalizar la actividad
                Intent resultIntent = new Intent();
                resultIntent.putExtra("nuevoContacto", contactoSeleccionado);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "El usuario ya está en la lista", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Seleccione un usuario de la lista", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarContacto(Contacto nuevoContacto) {
        listaDeContactos.add(nuevoContacto);
        adapter.actualizarLista(listaDeContactos);
    }


    private void irAActivityEditarPerfil() {
        Intent intent = new Intent(this, EditarPerfil.class);
        startActivity(intent);
    }

    private List<Contacto> obtenerListaDeContactos() {
        List<Contacto> contactos = new ArrayList<>();

        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuario");

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Contacto contacto = userSnapshot.getValue(Contacto.class);
                        if (contacto != null) {
                            contactos.add(contacto);
                        }
                    }
                    // Notificar al adaptador que los datos han cambiado
                    adapter.actualizarLista(contactos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores, si es necesario
                Toast.makeText(ListaContactosActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });

        return contactos;
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

    @Override
    public void onContactoClick(Contacto contacto) {
        Log.d("ListaContactosActivity", "Contacto clickeado: " + contacto.getNombre());

        // Obtener la posición del contacto clickeado
        int position = listaDeContactos.indexOf(contacto);

        // Cambiar la posición seleccionada y notificar al adaptador
        adapter.actualizarPosicionSeleccionada(position);
    }
}
