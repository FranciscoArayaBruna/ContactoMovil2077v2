package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class ListaContactosActivity extends AppCompatActivity {

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

        ImageButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(view -> agregarUsuarioDesdeBusqueda());

        ImageButton btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(view -> irAActivityEditarPerfil());

        // Agregar el código para cargar la imagen de perfil y configurar el clic en el botón de perfil
        configurarPerfil();
    }

    private void configurarRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvLista);
        adapter = new ContactosAdapter(listaDeContactos, contacto -> abrirPerfilContacto(contacto));
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
        String nombreApellidoBuscado = txtBuscarUsuario.getText().toString().trim().toLowerCase();

        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuario");

        // Dividir la entrada en nombre y apellido (si es proporcionado)
        String[] partesNombreApellido = nombreApellidoBuscado.split("\\s+");
        String nombreBuscado = partesNombreApellido[0];

        // Filtrar usuarios por nombre y opcionalmente por apellido
        Query query;
        if (partesNombreApellido.length > 1) {
            String apellidoBuscado = partesNombreApellido[1];
            query = usuarioRef.orderByChild("nombreApellido").startAt(nombreApellidoBuscado).endAt(nombreApellidoBuscado + "\uf8ff");
        } else {
            query = usuarioRef.orderByChild("nombre").startAt(nombreBuscado).endAt(nombreBuscado + "\uf8ff");
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

                    // Agregar los resultados de la búsqueda al principio de la lista
                    listaCompleta.addAll(0, resultados);

                    // Actualizar el RecyclerView con la lista combinada
                    adapter.actualizarLista(listaCompleta);
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
        int posicionSeleccionada = adapter.getPosicionSeleccionada();
        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            Contacto nuevoContacto = adapter.getItem(posicionSeleccionada);

            // Verificar si el usuario ya está en la lista
            if (!listaDeContactos.contains(nuevoContacto)) {
                // Agregar el nuevo contacto a la lista y actualizar el RecyclerView
                agregarContacto(nuevoContacto);
                txtBuscarUsuario.setText(""); // Limpiar el EditText después de agregar
                Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show();
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

    private void configurarPerfil() {
        // Obtener la referencia al botón de perfil
        ImageButton btnPerfil = findViewById(R.id.btnPerfil);

        // Obtener el correo del usuario
        String correoUsuario = obtenerCorreoUsuario(); // Reemplaza con la lógica real

        // Obtener la referencia al usuario en la base de datos
        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuario");
        usuarioRef.orderByChild("correo").equalTo(correoUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Obtener la URL de la imagen de perfil desde la base de datos
                        String urlImagenPerfil = userSnapshot.child("urlImagenPerfil").getValue(String.class);

                        // Imprimir la URL en los logs para verificar
                        Log.d("ListaContactosActivity", "URL de la imagen de perfil: " + urlImagenPerfil);

                        if (urlImagenPerfil != null && !urlImagenPerfil.isEmpty()) {
                            // Cargar la imagen de perfil si está disponible
                            Glide.with(ListaContactosActivity.this).load(urlImagenPerfil).into(btnPerfil);
                        } else {
                            // Mostrar un icono predeterminado si no hay imagen de perfil
                            btnPerfil.setImageResource(R.drawable.baseline_person_outline_24);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores, si es necesario
                Toast.makeText(ListaContactosActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el clic en el botón de perfil
        btnPerfil.setOnClickListener(v -> {
            // Aquí puedes abrir la actividad del perfil o realizar otras acciones según tus necesidades
            // Por ejemplo, puedes abrir una actividad de perfil pasando el usuario actual como parámetro
            Intent editarPerfilIntent = new Intent(ListaContactosActivity.this, EditarPerfil.class);
            startActivity(editarPerfilIntent);
        });
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

    private String obtenerUrlImagenPerfil(String correoUsuario) {
        // Lógica para obtener la URL de la imagen de perfil desde Firebase Storage
        // Reemplaza "tu_carpeta_en_storage" con la carpeta específica donde almacenas las imágenes de perfil
        // y "nombre_de_la_imagen.jpg" con el nombre real de la imagen en Storage
        String rutaImagen = "imagenes_perfil/" + correoUsuario + "/nombre_de_la_imagen.jpg";

        // Recuerda que esto es un ejemplo y necesitas adaptarlo a tu implementación real en Firebase Storage
        // Puedes utilizar métodos como FirebaseStorage.getInstance().getReference(rutaImagen).getDownloadUrl()
        // para obtener la URL de la imagen desde Storage

        // Devuelve la URL de la imagen o null si no hay imagen
        return "url_de_la_imagen_de_perfil";
    }

}
