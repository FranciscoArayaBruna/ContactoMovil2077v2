package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pancho.contactomovil2077.Modelo.RegistroModel;
import com.pancho.contactomovil2077.Modelo.UsuarioModel;
import com.pancho.contactomovil2077.R;

public class Registro extends AppCompatActivity {

    private EditText txtNombre, txtApellido, txtCorreo, txtPass, txtUsuario;
    private Button btnRegistro;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar FirebaseAuth y DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener referencias a los componentes de la interfaz de usuario
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        txtUsuario = findViewById(R.id.txtUsuario);
        btnRegistro = findViewById(R.id.btnRegistro);

        // Asignar listener al botón de registro
        btnRegistro.setOnClickListener(view -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Obtener valores de los campos de texto
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtPass.getText().toString();
        String nombreUsuario = txtUsuario.getText().toString();

        // Validar los campos
        if (camposRegistroSonValidos(nombre, apellido, correo, contrasena, nombreUsuario)) {
            // Crear instancia de RegistroModel
            RegistroModel nuevoUsuario = new RegistroModel(nombre, apellido, correo, contrasena, nombreUsuario);

            // Registrar nuevo usuario en Firebase
            mAuth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String usuarioId = mDatabase.child("Usuario").push().getKey();
                                mDatabase.child("Usuario").child(usuarioId).setValue(nuevoUsuario);

                                // Manejar el registro exitoso, si es necesario
                                Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                                // Redirigir a la clase de inicio de sesión
                                Intent intent = new Intent(Registro.this, Usuario.class);
                                startActivity(intent);
                                finish(); // Opcional: cierra la actividad actual si no deseas que el usuario regrese al registro
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Manejar errores durante el registro
                                Toast.makeText(Registro.this, "Error en el registro: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private boolean camposRegistroSonValidos(String nombre, String apellido, String correo, String contrasena, String nombreUsuario) {
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(correo) ||
                TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(nombreUsuario)) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar el formato del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            // El correo electrónico no es válido, muestra un mensaje de error
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
