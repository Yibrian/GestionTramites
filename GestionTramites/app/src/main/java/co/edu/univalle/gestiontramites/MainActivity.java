package co.edu.univalle.gestiontramites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import java.io.IOException;

import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private ConexionBD conexionBD;
    private EditText etCorreo, etContrasena;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexionBD = ConexionBD.getInstance(this);

        // Asegura que la base de datos esté lista antes de cualquier consulta
        try {
            conexionBD.createDataBase();
        } catch (IOException e) {
            Toast.makeText(this, "Error al preparar la base de datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = conexionBD.getUsuarioPorCorreo(correo);
            if (usuario == null) {
                Toast.makeText(this, "Correo incorrecto", Toast.LENGTH_SHORT).show();
            } else if (!usuario.getContrasena().equals(contrasena)) {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            } else {
                // Login exitoso
                Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                intent.putExtra("usuario_id", usuario.getIdUsuario());
                startActivity(intent);
                finish();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                }
            }
        });
    }
}