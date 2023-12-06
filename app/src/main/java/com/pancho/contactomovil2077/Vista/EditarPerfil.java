package com.pancho.contactomovil2077.Vista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pancho.contactomovil2077.Modelo.UsuarioModel;
import com.pancho.contactomovil2077.R;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgUsuario;
    private EditText nombreEditar, apellidoEditar, usuarioEditar;
    private TextView correoEditar;
    private Button btnCambiarImagen, btnGuardar, btnCerrarSesion;
    private Uri imageUri;
    private String imagenId;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        imgUsuario = findViewById(R.id.imgUsuario);
        nombreEditar = findViewById(R.id.nombreEditar);
        apellidoEditar = findViewById(R.id.apellidoEditar);
        correoEditar = findViewById(R.id.correoEditar);
        usuarioEditar = findViewById(R.id.usuarioEditar);
        btnCambiarImagen = findViewById(R.id.button);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });
        btnCambiarImagen.setOnClickListener(view -> seleccionarImagen());
        btnGuardar.setOnClickListener(view -> guardarCambios());

        // Obtener datos del usuario actual
        obtenerDatosUsuario();

    }

    private void obtenerDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.child("Usuario").orderByChild("correo").equalTo(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    UsuarioModel usuario = userSnapshot.getValue(UsuarioModel.class);
                                    if (usuario != null) {
                                        // Llenar los EditText con los datos del usuario
                                        nombreEditar.setText(usuario.getNombre());
                                        apellidoEditar.setText(usuario.getApellido());
                                        correoEditar.setText(usuario.getCorreo());
                                        usuarioEditar.setText(usuario.getNombreUsuario());

                                        // Cargar la imagen de perfil
                                        cargarImagenPerfil(user.getUid());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar errores, si es necesario
                            Toast.makeText(EditarPerfil.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // ...

    private void cargarImagenPerfil(String userId) {
        // Obtener la referencia al storage
        StorageReference storageRef = storage.getReference().child("imagenes_perfil/" + userId + ".jpg");

        // Obtener la URL de la imagen
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Invalidar la caché de Glide para cargar la nueva imagen
            Glide.with(this)
                    .load(uri)
                    .skipMemoryCache(true)  // Invalidar la caché en memoria
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Invalidar la caché en disco
                    .into(imgUsuario);
        }).addOnFailureListener(e -> {
            // Manejar errores en caso de fallo al obtener la URL de la imagen
            Toast.makeText(this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
        });
    }

// ...


    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void guardarCambios() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String nombre = nombreEditar.getText().toString().trim();
            String apellido = apellidoEditar.getText().toString().trim();
            String nombreUsuario = usuarioEditar.getText().toString().trim();

            if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(apellido) && !TextUtils.isEmpty(nombreUsuario)) {
                // Crear un nodo separado para almacenar las imágenes
                DatabaseReference imagenesPerfilRef = mDatabase.child("ImagenesPerfil");

                // Obtener la referencia a la ubicación del usuario actual en la base de datos
                DatabaseReference usuarioRef = mDatabase.child("Usuario");

                // Consultar el usuario específico por su correo electrónico
                Query query = usuarioRef.orderByChild("correo").equalTo(user.getEmail());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            // Mostrar en los logs los valores antes de la actualización
                            Log.d("EditarPerfil", "Valores antes de la actualización: " + userSnapshot.getValue());

                            // Obtener los datos actuales del usuario
                            UsuarioModel usuario = userSnapshot.getValue(UsuarioModel.class);

                            // Utilizar transacción para asegurar la atomicidad
                            userSnapshot.getRef().runTransaction(new Transaction.Handler() {
                                @Override
                                @NonNull
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    // Verificar si ya se actualizó la URL de la imagen
                                    if (!currentData.hasChild("urlImagenPerfil")) {
                                        // Actualizar con nuevos datos sin borrar los existentes
                                        currentData.child("nombre").setValue(nombre);
                                        currentData.child("apellido").setValue(apellido);
                                        currentData.child("nombreUsuario").setValue(nombreUsuario);

                                        // Subir la imagen si se seleccionó una nueva
                                        if (imageUri != null) {
                                            // Generar un ID único para la imagen si aún no existe
                                            if (imagenId == null) {
                                                imagenId = imagenesPerfilRef.push().getKey();
                                            }

                                            // Subir la imagen al nodo de ImagenesPerfil con el ID único
                                            subirImagenPerfil(imagenId);

                                            // Almacenar la URL de la imagen en el nodo del usuario
                                            currentData.child("urlImagenPerfil").setValue("imagenes_perfil/" + imagenId + ".jpg");
                                        }
                                    }

                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    // Mostrar en los logs los valores después de la actualización
                                    Log.d("EditarPerfil", "Valores después de la actualización: " + currentData.getValue());

                                    if (committed && error == null) {
                                        Toast.makeText(EditarPerfil.this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EditarPerfil.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(EditarPerfil.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar errores, si es necesario
                        Toast.makeText(EditarPerfil.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void subirImagenPerfil(String userId) {
        if (imageUri != null) {
            // Obtener la referencia al storage
            StorageReference storageRef = storage.getReference().child("imagenes_perfil/" + userId + ".jpg");

            // Subir la imagen al storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de la imagen después de subirla con éxito
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Actualizar la URL de la imagen en el nodo del usuario
                            DatabaseReference usuarioRef = mDatabase.child("Usuario").child(userId);
                            usuarioRef.child("urlImagenPerfil").setValue(uri.toString());
                            Toast.makeText(this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Manejar errores en caso de fallo al subir la imagen
                        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No hay imagen seleccionada, realizar acciones adicionales si es necesario
            Toast.makeText(this, "No has seleccionado una nueva imagen", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgUsuario.setImageURI(imageUri);
        }
    }
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        // Redirige a la pantalla de inicio de sesión
        startActivity(new Intent(EditarPerfil.this, Usuario.class));
        finishAffinity(); // Cierra la actividad actual para que el usuario no pueda volver atrás con el botón de retroceso
    }

}