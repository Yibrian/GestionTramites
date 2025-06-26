package co.edu.univalle.gestiontramites;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Calendar;
import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.Tramite;
import co.edu.univalle.gestiontramites.model.TipoTramite;
import co.edu.univalle.gestiontramites.model.Requisito;

public class EditarTramiteActivity extends AppCompatActivity {

    private EditText etNombreTramite, etFecha, etHora, etLugar, etDescripcion, etValor, etFrecuencia, etRequisito;
    private Spinner spTipoTramite, spCiudad;
    private CheckBox cbTieneValor;
    private Button btnGuardarCambios, btnEliminarTramite;
    private int tramiteId, usuarioId;
    private ConexionBD conexionBD;
    private Tramite tramite;
    private Requisito requisitoActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tramite);

        // Inicializar vistas
        etNombreTramite = findViewById(R.id.etNombreTramite);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        etValor = findViewById(R.id.etValor);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etRequisito = findViewById(R.id.etRequisito);
        spTipoTramite = findViewById(R.id.spTipoTramite);
        spCiudad = findViewById(R.id.spCiudad);
        cbTieneValor = findViewById(R.id.cbTieneValor);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminarTramite = findViewById(R.id.btnEliminarTramite);

        tramiteId = getIntent().getIntExtra("tramite_id", -1);
        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        conexionBD = ConexionBD.getInstance(this);

        cargarSpinners();
        cargarDatosTramite();

        // Listener para fecha
        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, y, m, d) -> {
                String fecha = String.format("%02d/%02d/%04d", d, m + 1, y);
                etFecha.setText(fecha);
            }, year, month, day).show();
        });

        // Listener para hora
        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, h, m) -> {
                String hora = String.format("%02d:%02d", h, m);
                etHora.setText(hora);
            }, hour, minute, true).show();
        });

        // Habilitar/deshabilitar campo valor según el checkbox
        cbTieneValor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etValor.setEnabled(isChecked);
            if (!isChecked) etValor.setText("");
        });

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
        btnEliminarTramite.setOnClickListener(v -> eliminarTramite());

        TextView tvCancelar = findViewById(R.id.tvCancelar);
        tvCancelar.setOnClickListener(v -> finish());
    }

    private void cargarSpinners() {
        List<TipoTramite> tipos = conexionBD.obtenerTiposTramite();
        List<String> nombresTipos = new java.util.ArrayList<>();
        for (TipoTramite t : tipos) nombresTipos.add(t.getNombreTipo());
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresTipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTramite.setAdapter(adapterTipo);

        List<String> nombresCiudades = conexionBD.obtenerNombresCiudades();
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCiudades);
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapterCiudad);
    }

    private void cargarDatosTramite() {
        List<Tramite> lista = conexionBD.obtenerTramitesPorUsuario(usuarioId);
        for (Tramite t : lista) {
            if (t.getIdTramite() == tramiteId) {
                tramite = t;
                break;
            }
        }
        if (tramite != null) {
            etNombreTramite.setText(tramite.getNombreTramite());
            etFecha.setText(tramite.getFecha());
            etHora.setText(tramite.getHora());
            etLugar.setText(tramite.getLugar());
            etDescripcion.setText(tramite.getDescripcion());
            etFrecuencia.setText(tramite.getFrecuencia() != null ? tramite.getFrecuencia() : "");
            cbTieneValor.setChecked(tramite.isTieneValor());
            etValor.setText(String.valueOf(tramite.getValorMonetario()));
            spTipoTramite.setSelection(tramite.getIdTipoTramite() - 1);
            spCiudad.setSelection(tramite.getCiudadId() - 1);
            etValor.setEnabled(tramite.isTieneValor());

            // Cargar requisito si existe
            List<Requisito> requisitos = conexionBD.obtenerRequisitosPorTramite(tramiteId);
            if (!requisitos.isEmpty()) {
                requisitoActual = requisitos.get(0);
                etRequisito.setText(requisitoActual.getDescripcionRequisito());
            } else {
                requisitoActual = null;
                etRequisito.setText("");
            }
        }
    }

    private void guardarCambios() {
        tramite.setNombreTramite(etNombreTramite.getText().toString().trim());
        tramite.setFecha(etFecha.getText().toString().trim());
        tramite.setHora(etHora.getText().toString().trim());
        tramite.setLugar(etLugar.getText().toString().trim());
        tramite.setDescripcion(etDescripcion.getText().toString().trim());
        tramite.setFrecuencia(etFrecuencia.getText().toString().trim());
        tramite.setTieneValor(cbTieneValor.isChecked());
        tramite.setValorMonetario(cbTieneValor.isChecked() && !etValor.getText().toString().isEmpty()
                ? Double.parseDouble(etValor.getText().toString()) : 0);
        tramite.setIdTipoTramite(spTipoTramite.getSelectedItemPosition() + 1);
        tramite.setCiudadId(spCiudad.getSelectedItemPosition() + 1);

        int res = conexionBD.actualizarTramite(tramite);

        // Guardar o actualizar requisito
        String descRequisito = etRequisito.getText().toString().trim();
        if (!descRequisito.isEmpty()) {
            if (requisitoActual != null) {
                requisitoActual.setDescripcionRequisito(descRequisito);
                conexionBD.actualizarRequisito(requisitoActual);
            } else {
                Requisito nuevo = new Requisito();
                nuevo.setDescripcionRequisito(descRequisito);
                nuevo.setIdTramite(tramiteId);
                conexionBD.insertarRequisito(nuevo);
            }
        } else if (requisitoActual != null) {
            // Si se borra el texto, elimina el requisito
            conexionBD.eliminarRequisito(requisitoActual.getIdRequisito());
        }

        if (res > 0) {
            Toast.makeText(this, "Trámite actualizado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar trámite", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarTramite() {
        int res = conexionBD.eliminarTramite(tramiteId);
        if (res > 0) {
            Toast.makeText(this, "Trámite eliminado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar trámite", Toast.LENGTH_SHORT).show();
        }
    }
}