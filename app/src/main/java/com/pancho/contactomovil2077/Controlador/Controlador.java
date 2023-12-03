// Controlador.java
package com.pancho.contactomovil2077.Controlador;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pancho.contactomovil2077.Modelo.UsuarioModel;
import com.pancho.contactomovil2077.Vista.ListaContactosActivity;
import com.pancho.contactomovil2077.Vista.MainActivity;
import com.pancho.contactomovil2077.Vista.Registro;

public class Controlador {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    public Controlador(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void iniciarSesion(String correo, String contrasena) {
        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(context, "Ingresa correo electrónico y contraseña para iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(context, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            obtenerDetallesUsuario(user.getEmail());
                        }
                    } else {
                        Toast.makeText(context, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("InicioSesion", "Error en el inicio de sesión: " + e.getMessage());
                    Toast.makeText(context, "Error en el inicio de sesión. Consulta el LogCat para más detalles.", Toast.LENGTH_SHORT).show();
                });
    }

    private void obtenerDetallesUsuario(String correo) {
        // Obtener detalles del usuario desde Firebase usando el correo electrónico
        mDatabase.child("Usuario").orderByChild("correo").equalTo(correo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UsuarioModel usuario = snapshot.getValue(UsuarioModel.class);
                            abrirMainActivity(usuario);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores, si es necesario
                    }
                });
    }

    private void abrirMainActivity(UsuarioModel usuario) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("usuario", usuario);
        context.startActivity(intent);
        // Cerrar la actividad actual para evitar que el usuario regrese al inicio de sesión presionando el botón "Atrás"
        if (context instanceof Registro) {
            ((Registro) context).finish();
        }
    }
}
