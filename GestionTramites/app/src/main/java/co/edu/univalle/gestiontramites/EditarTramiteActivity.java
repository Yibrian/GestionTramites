package co.edu.univalle.gestiontramites;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.provider.Settings;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditarTramiteActivity extends AppCompatActivity {

    private EditText etNombreTramite, etFecha, etHora, etLugar, etDescripcion, etValor;
    private Spinner spTipoTramite, spCiudad, spRequisito;
    private CheckBox cbTieneValor;
    private Button btnGuardarCambios, btnEliminarTramite;
    private Notificacion notificacionPendiente = null;
    private int tramiteId, usuarioId;
    private ConexionBD conexionBD;
    private List<Requisito> listaRequisitos = new ArrayList<>();

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
        spTipoTramite = findViewById(R.id.spTipoTramite);
        spCiudad = findViewById(R.id.spCiudad);
        spRequisito = findViewById(R.id.spRequisito);
        cbTieneValor = findViewById(R.id.cbTieneValor);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminarTramite = findViewById(R.id.btnEliminarTramite);
        Button btnAgregarNotificacion = findViewById(R.id.btnAgregarNotificacion);

        conexionBD = ConexionBD.getInstance(this);

        tramiteId = getIntent().getIntExtra("tramite_id", -1);
        usuarioId = getIntent().getIntExtra("usuario_id", -1);

        cargarSpinners();
        cargarDatosTramite();

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

        cbTieneValor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etValor.setEnabled(isChecked);
            if (!isChecked) etValor.setText("");
        });

        btnAgregarNotificacion.setOnClickListener(v -> mostrarDialogoNotificacion());

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        btnEliminarTramite.setOnClickListener(v -> {
            conexionBD.eliminarTramite(tramiteId);
            Toast.makeText(this, "Trámite eliminado", Toast.LENGTH_SHORT).show();
            finish();
        });

        TextView tvCancelar = findViewById(R.id.tvCancelar);
        tvCancelar.setOnClickListener(v -> finish());
    }

    private void cargarSpinners() {
        // Tipos
        List<TipoTramite> tipos = conexionBD.obtenerTiposTramite();
        List<String> nombresTipos = new ArrayList<>();
        for (TipoTramite t : tipos) nombresTipos.add(t.getNombreTipo());
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresTipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTramite.setAdapter(adapterTipo);

        // Ciudades
        List<String> nombresCiudades = conexionBD.obtenerNombresCiudades();
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCiudades);
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapterCiudad);

        // Requisitos
        listaRequisitos = conexionBD.obtenerRequisitos();
        List<String> nombresRequisitos = new ArrayList<>();
        for (Requisito r : listaRequisitos) nombresRequisitos.add(r.getDescripcionRequisito());
        if (nombresRequisitos.isEmpty()) nombresRequisitos.add("Sin requisitos");
        ArrayAdapter<String> adapterRequisito = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresRequisitos);
        adapterRequisito.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequisito.setAdapter(adapterRequisito);
    }

    private void cargarDatosTramite() {
        // Cargar los datos del trámite a editar y setear en los campos
        List<Tramite> tramites = conexionBD.obtenerTramitesPorUsuario(usuarioId);
        Tramite tramite = null;
        for (Tramite t : tramites) {
            if (t.getIdTramite() == tramiteId) {
                tramite = t;
                break;
            }
        }
        if (tramite == null) return;

        etNombreTramite.setText(tramite.getNombreTramite());
        etFecha.setText(tramite.getFecha());
        etHora.setText(tramite.getHora());
        etLugar.setText(tramite.getLugar());
        etDescripcion.setText(tramite.getDescripcion());
        cbTieneValor.setChecked(tramite.isTieneValor());
        etValor.setEnabled(tramite.isTieneValor());
        if (tramite.isTieneValor()) etValor.setText(String.valueOf(tramite.getValorMonetario()));
        spTipoTramite.setSelection(tramite.getIdTipoTramite() - 1);
        spCiudad.setSelection(tramite.getCiudadId() - 1);

        // Seleccionar el requisito asociado si existe
        List<Requisito> requisitosAsociados = conexionBD.obtenerRequisitosPorTramite(tramiteId);
        if (!requisitosAsociados.isEmpty()) {
            int pos = 0;
            for (int i = 0; i < listaRequisitos.size(); i++) {
                if (listaRequisitos.get(i).getIdRequisito() == requisitosAsociados.get(0).getIdRequisito()) {
                    pos = i;
                    break;
                }
            }
            spRequisito.setSelection(pos);
        } else {
            spRequisito.setSelection(0);
        }
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
        builder.setPositiveButton("Guardar", null);
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
                sdf.setLenient(false);
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
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(this, "Formato de fecha y hora inválido. Usa dd/MM/yyyy y HH:mm", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

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

    private void guardarCambios() {
        String nombre = etNombreTramite.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
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

        // Validar y programar notificación antes de guardar
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

        if (notificacionPendiente != null && !tienePermisoAlarmasExactas()) {
            Toast.makeText(this, "Debes permitir alarmas exactas en Ajustes", Toast.LENGTH_LONG).show();
            solicitarPermisoAlarmasExactas();
            return;
        }

        // Actualizar trámite
        Tramite tramite = new Tramite();
        tramite.setIdTramite(tramiteId);
        tramite.setNombreTramite(nombre);
        tramite.setFrecuencia(""); // No se usa
        tramite.setFecha(fecha);
        tramite.setHora(hora);
        tramite.setDescripcion(descripcion);
        tramite.setCiudadId(idCiudad);
        tramite.setLugar(lugar);
        tramite.setTieneValor(tieneValor);
        tramite.setValorMonetario(valorMonetario);
        tramite.setIdUsuario(usuarioId);
        tramite.setIdTipoTramite(idTipoTramite);

        int res = conexionBD.actualizarTramite(tramite);
        if (res > 0) {
            // --- ASOCIAR/DESASOCIAR REQUISITO ---
            int posReq = spRequisito.getSelectedItemPosition();
            if (!listaRequisitos.isEmpty() && posReq >= 0 && posReq < listaRequisitos.size()) {
                Requisito reqSeleccionado = listaRequisitos.get(posReq);
                // Desasociar requisitos anteriores de este trámite
                List<Requisito> requisitosAnteriores = conexionBD.obtenerRequisitosPorTramite(tramiteId);
                for (Requisito r : requisitosAnteriores) {
                    r.setIdTramite(0);
                    conexionBD.actualizarRequisito(r);
                }
                // Asociar el nuevo requisito
                reqSeleccionado.setIdTramite(tramiteId);
                conexionBD.actualizarRequisito(reqSeleccionado);
            }
            // --- FIN ASOCIACIÓN REQUISITO ---

            // Guardar y programar notificación si fue creada
            if (notificacionPendiente != null) {
                notificacionPendiente.setIdTramite(tramiteId);
                conexionBD.insertarNotificacion(notificacionPendiente);

                String fechaHora = notificacionPendiente.getFechaHoraProgramada();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                try {
                    Date date = sdf.parse(fechaHora);
                    long triggerAtMillis = date.getTime();

                    Intent intent = new Intent(this, NotificacionReceiver.class);
                    intent.putExtra("mensaje", notificacionPendiente.getMensaje());
                    intent.putExtra("id", tramiteId);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this, tramiteId, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    }
                } catch (SecurityException se) {
                    Toast.makeText(this, "No tienes permiso para programar alarmas exactas", Toast.LENGTH_SHORT).show();
                    return;
                } catch (Exception e) {
                    Toast.makeText(this, "Error al programar la notificación", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "Trámite actualizado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar trámite", Toast.LENGTH_SHORT).show();
        }
    }
}