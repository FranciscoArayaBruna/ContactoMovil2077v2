package com.pancho.contactomovil2077.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.pancho.contactomovil2077.Controlador.Controlador;
import com.pancho.contactomovil2077.Modelo.UsuarioModel;
import com.pancho.contactomovil2077.R;

public class Usuario extends AppCompatActivity {

    private EditText txtCorreo, txtPass;
    private Button btnRegistro, btnLogin;
    private Controlador loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        loginController = new Controlador(this);

        // Obtener referencias a los componentes de la interfaz de usuario
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnLogin = findViewById(R.id.btnLogin);

        // Asignar listeners a los botones
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Usuario.this, Registro.class);
                startActivity(i);
            }
        });
        btnLogin.setOnClickListener(view -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtPass.getText().toString().trim();

        // Llamar a la función de inicio de sesión del controlador
        loginController.iniciarSesion(correo, contrasena);
    }
}
