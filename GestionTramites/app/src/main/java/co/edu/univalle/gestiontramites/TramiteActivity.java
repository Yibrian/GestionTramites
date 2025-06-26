package co.edu.univalle.gestiontramites;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.provider.Settings;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.Notificacion;
import co.edu.univalle.gestiontramites.model.Requisito;
import co.edu.univalle.gestiontramites.model.TipoTramite;
import co.edu.univalle.gestiontramites.model.Tramite;

public class TramiteActivity extends AppCompatActivity {

    private EditText etNombreTramite, etFecha, etHora, etLugar, etDescripcion, etValor, etRequisito, etFrecuencia;
    private Spinner spTipoTramite, spCiudad;
    private CheckBox cbTieneValor;
    private Button btnGuardarTramite;
    private Notificacion notificacionPendiente = null;
    private int usuarioId;
    private ConexionBD conexionBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramite);

        // Inicializar vistas
        etNombreTramite = findViewById(R.id.etNombreTramite);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        etValor = findViewById(R.id.etValor);
        etRequisito = findViewById(R.id.etRequisito);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        spTipoTramite = findViewById(R.id.spTipoTramite);
        spCiudad = findViewById(R.id.spCiudad);
        cbTieneValor = findViewById(R.id.cbTieneValor);
        btnGuardarTramite = findViewById(R.id.btnGuardarTramite);
        Button btnAgregarNotificacion = findViewById(R.id.btnAgregarNotificacion);
        btnAgregarNotificacion.setOnClickListener(v -> mostrarDialogoNotificacion());
        conexionBD = ConexionBD.getInstance(this);

        // Permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // Obtener usuario_id del intent
        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Listeners para fecha y hora
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

        // Cancelar: volver a InicioActivity
        TextView tvCancelar = findViewById(R.id.tvCancelar);
        tvCancelar.setOnClickListener(v -> finish());

        // Borrar todo
        TextView tvBorrarTodo = findViewById(R.id.tvBorrarTodo);
        tvBorrarTodo.setOnClickListener(v -> limpiarCampos());

        // Cargar datos en los spinners
        cargarSpinners();

        // Guardar trámite
        btnGuardarTramite.setOnClickListener(v -> guardarTramite());
    }

    private void limpiarCampos() {
        etNombreTramite.setText("");
        etFecha.setText("");
        etHora.setText("");
        etLugar.setText("");
        etDescripcion.setText("");
        etValor.setText("");
        etRequisito.setText("");
        etFrecuencia.setText("");
        cbTieneValor.setChecked(false);
        spTipoTramite.setSelection(0);
        spCiudad.setSelection(0);
    }

    private void cargarSpinners() {
        // Cargar tipos de trámite
        List<TipoTramite> tipos = conexionBD.obtenerTiposTramite();
        List<String> nombresTipos = new ArrayList<>();
        for (TipoTramite t : tipos) {
            nombresTipos.add(t.getNombreTipo());
        }
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresTipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTramite.setAdapter(adapterTipo);

        // Cargar ciudades
        List<String> nombresCiudades = conexionBD.obtenerNombresCiudades();
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCiudades);
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapterCiudad);
    }

    private void mostrarDialogoNotificacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notificacion, null);
        builder.setView(dialogView);

        EditText etMensaje = dialogView.findViewById(R.id.etMensajeNotificacion);
        EditText etFecha = dialogView.findViewById(R.id.etFechaNotificacion);
        EditText etHora = dialogView.findViewById(R.id.etHoraNotificacion);

        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                etFecha.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) -> {
                etHora.setText(String.format("%02d:%02d", h, m));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        builder.setTitle("Agregar recordatorio");
        builder.setPositiveButton("Guardar", null); // No cerrar automáticamente
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnGuardar.setOnClickListener(v -> {
                String mensaje = etMensaje.getText().toString().trim();
                String fecha = etFecha.getText().toString().trim();
                String hora = etHora.getText().toString().trim();
                if (mensaje.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos del recordatorio", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setLenient(false); // Forzar formato exacto
                try {
                    Date date = sdf.parse(fecha + " " + hora);
                    if (date == null || date.getTime() <= System.currentTimeMillis()) {
                        Toast.makeText(this, "La fecha y hora del recordatorio deben ser futuras y válidas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    notificacionPendiente = new Notificacion();
                    notificacionPendiente.setMensaje(mensaje);
                    notificacionPendiente.setFechaHoraProgramada(fecha + " " + hora);
                    notificacionPendiente.setEnviada(false);
                    dialog.dismiss(); // Solo cerrar si todo es válido
                } catch (Exception e) {
                    Toast.makeText(this, "Formato de fecha y hora inválido. Usa dd/MM/yyyy y HH:mm", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    // --- Métodos para permiso de alarmas exactas ---
    private boolean tienePermisoAlarmasExactas() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void solicitarPermisoAlarmasExactas() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
    // ------------------------------------------------

    private void guardarTramite() {
        String nombre = etNombreTramite.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String frecuencia = etFrecuencia.getText().toString().trim();
        String requisito = etRequisito.getText().toString().trim();
        boolean tieneValor = cbTieneValor.isChecked();
        String valorStr = etValor.getText().toString().trim();

        if (nombre.isEmpty() || fecha.isEmpty() || hora.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int idTipoTramite = spTipoTramite.getSelectedItemPosition() + 1;
        int idCiudad = spCiudad.getSelectedItemPosition() + 1;

        double valorMonetario = 0;
        if (tieneValor && !valorStr.isEmpty()) {
            try {
                valorMonetario = Double.parseDouble(valorStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor monetario inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validar y programar notificación antes de guardar el trámite
        if (notificacionPendiente != null) {
            String fechaHora = notificacionPendiente.getFechaHoraProgramada();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setLenient(false);
            try {
                Date date = sdf.parse(fechaHora);
                long triggerAtMillis = date.getTime();
                if (date == null || triggerAtMillis <= System.currentTimeMillis()) {
                    Toast.makeText(this, "La fecha y hora del recordatorio deben ser futuras y válidas", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Formato de fecha y hora inválido. Usa dd/MM/yyyy y HH:mm", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // --- Validar permiso de alarmas exactas antes de guardar y programar ---
        if (notificacionPendiente != null && !tienePermisoAlarmasExactas()) {
            Toast.makeText(this, "Debes permitir alarmas exactas en Ajustes", Toast.LENGTH_LONG).show();
            solicitarPermisoAlarmasExactas();
            return;
        }
        // ----------------------------------------------------------------------

        // Guardar trámite
        Tramite tramite = new Tramite();
        tramite.setNombreTramite(nombre);
        tramite.setFrecuencia(frecuencia);
        tramite.setFecha(fecha);
        tramite.setHora(hora);
        tramite.setDescripcion(descripcion);
        tramite.setCiudadId(idCiudad);
        tramite.setLugar(lugar);
        tramite.setTieneValor(tieneValor);
        tramite.setValorMonetario(valorMonetario);
        tramite.setIdUsuario(usuarioId);
        tramite.setIdTipoTramite(idTipoTramite);

        long res = conexionBD.insertarTramite(tramite);
        if (res > 0) {
            // Guardar requisito si se ingresó
            if (!requisito.isEmpty()) {
                Requisito req = new Requisito();
                req.setDescripcionRequisito(requisito);
                req.setIdTramite((int) res);
                conexionBD.insertarRequisito(req);
            }

            // Guardar y programar notificación si fue creada
            if (notificacionPendiente != null) {
                notificacionPendiente.setIdTramite((int) res);
                conexionBD.insertarNotificacion(notificacionPendiente);

                String fechaHora = notificacionPendiente.getFechaHoraProgramada();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                try {
                    Date date = sdf.parse(fechaHora);
                    long triggerAtMillis = date.getTime();

                    Intent intent = new Intent(this, NotificacionReceiver.class);
                    intent.putExtra("mensaje", notificacionPendiente.getMensaje());
                    intent.putExtra("id", (int) res);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this, (int) res, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    }
                } catch (SecurityException se) {
                    Toast.makeText(this, "No tienes permiso para programar alarmas exactas", Toast.LENGTH_SHORT).show();
                    conexionBD.eliminarTramite((int) res);
                    return;
                } catch (Exception e) {
                    Toast.makeText(this, "Error al programar la notificación", Toast.LENGTH_SHORT).show();
                    conexionBD.eliminarTramite((int) res);
                    return;
                }
            }
            Toast.makeText(this, "Trámite guardado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar trámite", Toast.LENGTH_SHORT).show();
        }
    }
}