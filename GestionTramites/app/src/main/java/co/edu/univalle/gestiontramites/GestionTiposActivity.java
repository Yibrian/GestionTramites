package co.edu.univalle.gestiontramites;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.TipoTramite;

public class GestionTiposActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnAgregarTipo, btnCancelar;
    private ArrayAdapter<String> adapter;
    private List<TipoTramite> tipos;
    private ConexionBD conexionBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_tipos);
        setTitle("Gestionar Tipos de Trámite");

        listView = findViewById(R.id.listViewTipos);
        btnAgregarTipo = findViewById(R.id.btnAgregarTipo);
        btnCancelar = findViewById(R.id.btnCancelar);

        conexionBD = ConexionBD.getInstance(this);
        cargarTipos();

        listView.setOnItemClickListener((parent, view, position, id) -> mostrarDialogoEditar(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            mostrarDialogoEliminar(position);
            return true;
        });

        btnAgregarTipo.setOnClickListener(v -> mostrarDialogoAgregar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarTipos() {
        tipos = conexionBD.obtenerTiposTramite();
        List<String> nombres = new ArrayList<>();
        for (TipoTramite t : tipos) nombres.add(t.getNombreTipo());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        listView.setAdapter(adapter);
    }

    private void mostrarDialogoAgregar() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle("Nuevo tipo de trámite")
                .setView(input)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = input.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        TipoTramite tipo = new TipoTramite();
                        tipo.setNombreTipo(nombre);
                        conexionBD.insertarTipoTramite(tipo);
                        cargarTipos();
                        Toast.makeText(this, "Tipo agregado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditar(int position) {
        TipoTramite tipo = tipos.get(position);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(tipo.getNombreTipo());
        new AlertDialog.Builder(this)
                .setTitle("Editar tipo de trámite")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = input.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        tipo.setNombreTipo(nombre);
                        conexionBD.actualizarTipoTramite(tipo);
                        cargarTipos();
                        Toast.makeText(this, "Tipo actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEliminar(int position) {
        TipoTramite tipo = tipos.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tipo")
                .setMessage("¿Seguro que deseas eliminar este tipo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    conexionBD.eliminarTipoTramite(tipo.getIdTipoTramite());
                    cargarTipos();
                    Toast.makeText(this, "Tipo eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}