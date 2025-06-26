package co.edu.univalle.gestiontramites.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import co.edu.univalle.gestiontramites.model.Notificacion;
import co.edu.univalle.gestiontramites.model.Requisito;
import co.edu.univalle.gestiontramites.model.TipoTramite;
import co.edu.univalle.gestiontramites.model.Tramite;
import co.edu.univalle.gestiontramites.model.Usuario;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD extends SQLiteOpenHelper {

    private static ConexionBD myInstance;
    private static String DB_PATH = "";
    private static final String DB_NAME = "gestiontramites.db";
    private static final int DATABASE_VERSION = 1;
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    public static synchronized ConexionBD getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new ConexionBD(context.getApplicationContext());
        }
        return myInstance;
    }

    private ConexionBD(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e("ConexionBD", "createDataBase: " + e);
                throw new IOException("Error copiando Base de Datos");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e("ConexionBD", "checkDataBase: " + e);
        } finally {
            if (checkDB != null) checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void open() throws SQLException {
        try {
            createDataBase();
        } catch (IOException e) {
            Log.e("ConexionBD", "open: " + e);
            throw new Error("No se pudo crear la base de datos");
        }
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) myDataBase.close();
        super.close();
    }

    public Cursor ejecutarConsulta(String sql, String[] args) {
        Cursor cursor = null;
        try {
            open();
            cursor = myDataBase.rawQuery(sql, args);
        } catch (Exception e) {
            Log.e("ConexionBD", "ejecutarConsulta: " + e);
        }
        return cursor;
    }

    public long ejecutarInsert(String tabla, ContentValues values) {
        long result = -1;
        try {
            open();
            result = myDataBase.insert(tabla, null, values);
        } catch (Exception e) {
            Log.e("ConexionBD", "ejecutarInsert: " + e);
        } finally {
            close();
        }
        return result;
    }

    public Usuario getUsuarioPorCorreo(String correo) {
        Usuario usuario = null;
        String sql = "SELECT * FROM Usuario WHERE correo = ?";
        Cursor cursor = null;
        try {
            Log.d("DEBUG", "Buscando usuario con correo: " + correo); // <-- Aquí
            cursor = ejecutarConsulta(sql, new String[]{correo});
            if (cursor != null && cursor.moveToFirst()) {
                usuario = new Usuario(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
                );
            }
        } catch (Exception e) {
            Log.e("ConexionBD", "getUsuarioPorCorreo: " + e);
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return usuario;
    }

    public int ejecutarUpdate(String tabla, ContentValues values, String whereClause, String[] whereArgs) {
        int result = 0;
        try {
            open();
            result = myDataBase.update(tabla, values, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e("ConexionBD", "ejecutarUpdate: " + e);
        } finally {
            close();
        }
        return result;
    }

    public int ejecutarDelete(String tabla, String whereClause, String[] whereArgs) {
        int result = 0;
        try {
            open();
            result = myDataBase.delete(tabla, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e("ConexionBD", "ejecutarDelete: " + e);
        } finally {
            close();
        }
        return result;
    }

    public long insertarTramite(Tramite tramite) {
        ContentValues values = new ContentValues();
        values.put("nombre_tramite", tramite.getNombreTramite());
        values.put("frecuencia", tramite.getFrecuencia());
        values.put("fecha", tramite.getFecha());
        values.put("hora", tramite.getHora());
        values.put("descripcion", tramite.getDescripcion());
        values.put("ciudad_id", tramite.getCiudadId());
        values.put("lugar", tramite.getLugar());
        values.put("tiene_valor", tramite.isTieneValor() ? 1 : 0);
        values.put("valor_monetario", tramite.getValorMonetario());
        values.put("id_usuario", tramite.getIdUsuario());
        values.put("id_tipo_tramite", tramite.getIdTipoTramite());

        return ejecutarInsert("Tramite", values);
    }

    public List<Tramite> obtenerTramitesPorUsuario(int idUsuario) {
        List<Tramite> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tramite WHERE id_usuario = ?";
        Cursor cursor = ejecutarConsulta(sql, new String[]{String.valueOf(idUsuario)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Tramite t = new Tramite();
                t.setIdTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tramite")));
                t.setNombreTramite(cursor.getString(cursor.getColumnIndexOrThrow("nombre_tramite")));
                t.setFrecuencia(cursor.getString(cursor.getColumnIndexOrThrow("frecuencia")));
                t.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                t.setHora(cursor.getString(cursor.getColumnIndexOrThrow("hora")));
                t.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                t.setCiudadId(cursor.getInt(cursor.getColumnIndexOrThrow("ciudad_id")));
                t.setLugar(cursor.getString(cursor.getColumnIndexOrThrow("lugar")));
                t.setTieneValor(cursor.getInt(cursor.getColumnIndexOrThrow("tiene_valor")) == 1);
                t.setValorMonetario(cursor.getDouble(cursor.getColumnIndexOrThrow("valor_monetario")));
                t.setIdUsuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                t.setIdTipoTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_tramite")));
                lista.add(t);
            }
            cursor.close();
        }
        close();
        return lista;
    }

    public List<Requisito> obtenerRequisitosPorTramite(int idTramite) {
        List<Requisito> lista = new ArrayList<>();
        String sql = "SELECT * FROM Requisito WHERE id_tramite = ?";
        Cursor cursor = ejecutarConsulta(sql, new String[]{String.valueOf(idTramite)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Requisito r = new Requisito();
                r.setIdRequisito(cursor.getInt(cursor.getColumnIndexOrThrow("id_requisito")));
                r.setDescripcionRequisito(cursor.getString(cursor.getColumnIndexOrThrow("descripcion_requisito")));
                r.setIdTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tramite")));
                lista.add(r);
            }
            cursor.close();
        }
        close();
        return lista;
    }

    public int actualizarTramite(Tramite tramite) {
        ContentValues values = new ContentValues();
        values.put("nombre_tramite", tramite.getNombreTramite());
        values.put("frecuencia", tramite.getFrecuencia());
        values.put("fecha", tramite.getFecha());
        values.put("hora", tramite.getHora());
        values.put("descripcion", tramite.getDescripcion());
        values.put("ciudad_id", tramite.getCiudadId());
        values.put("lugar", tramite.getLugar());
        values.put("tiene_valor", tramite.isTieneValor() ? 1 : 0);
        values.put("valor_monetario", tramite.getValorMonetario());
        values.put("id_tipo_tramite", tramite.getIdTipoTramite());
        return ejecutarUpdate("Tramite", values, "id_tramite = ?", new String[]{String.valueOf(tramite.getIdTramite())});
    }

    public int eliminarTramite(int idTramite) {
        ejecutarDelete("Requisito", "id_tramite = ?", new String[]{String.valueOf(idTramite)});
        return ejecutarDelete("Tramite", "id_tramite = ?", new String[]{String.valueOf(idTramite)});
    }

    public long insertarRequisito(Requisito requisito) {
        ContentValues values = new ContentValues();
        values.put("descripcion_requisito", requisito.getDescripcionRequisito());
        values.put("id_tramite", requisito.getIdTramite());
        return ejecutarInsert("Requisito", values);
    }

    public int actualizarRequisito(Requisito requisito) {
        ContentValues values = new ContentValues();
        values.put("descripcion_requisito", requisito.getDescripcionRequisito());
        return ejecutarUpdate("Requisito", values, "id_requisito = ?", new String[]{String.valueOf(requisito.getIdRequisito())});
    }

    public int eliminarRequisito(int idRequisito) {
        return ejecutarDelete("Requisito", "id_requisito = ?", new String[]{String.valueOf(idRequisito)});
    }

    public List<TipoTramite> obtenerTiposTramite() {
        List<TipoTramite> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tipo_Tramite";
        Cursor cursor = ejecutarConsulta(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                TipoTramite tipo = new TipoTramite();
                tipo.setIdTipoTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_tramite")));
                tipo.setNombreTipo(cursor.getString(cursor.getColumnIndexOrThrow("nombre_tipo")));
                lista.add(tipo);
            }
            cursor.close();
        }
        close();
        return lista;
    }
    public List<String> obtenerNombresCiudades() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_ciudad FROM Ciudad";
        Cursor cursor = ejecutarConsulta(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                lista.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre_ciudad")));
            }
            cursor.close();
        }
        close();
        return lista;
    }
    public long insertarNotificacion(Notificacion notificacion) {
        ContentValues values = new ContentValues();
        values.put("id_tramite", notificacion.getIdTramite());
        values.put("fecha_hora_programada", notificacion.getFechaHoraProgramada());
        values.put("mensaje", notificacion.getMensaje());
        values.put("enviada", notificacion.isEnviada() ? 1 : 0);
        return ejecutarInsert("Notificacion", values);
    }
    // Insertar un nuevo tipo de trámite
    public long insertarTipoTramite(TipoTramite tipo) {
        ContentValues values = new ContentValues();
        values.put("nombre_tipo", tipo.getNombreTipo());
        return ejecutarInsert("Tipo_Tramite", values);
    }

    // Actualizar un tipo de trámite existente
    public int actualizarTipoTramite(TipoTramite tipo) {
        ContentValues values = new ContentValues();
        values.put("nombre_tipo", tipo.getNombreTipo());
        return ejecutarUpdate("Tipo_Tramite", values, "id_tipo_tramite = ?", new String[]{String.valueOf(tipo.getIdTipoTramite())});
    }

    // Eliminar un tipo de trámite
    public int eliminarTipoTramite(int idTipoTramite) {
        return ejecutarDelete("Tipo_Tramite", "id_tipo_tramite = ?", new String[]{String.valueOf(idTipoTramite)});
    }
    public List<Tramite> buscarTramites(int idUsuario, String filtro) {
        List<Tramite> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tramite " +
                "WHERE id_usuario = ? AND (" +
                "nombre_tramite LIKE ? OR " +
                "fecha LIKE ? OR " +
                "ciudad_id IN (SELECT id_ciudad FROM Ciudad WHERE nombre_ciudad LIKE ?) OR " +
                "id_tipo_tramite IN (SELECT id_tipo_tramite FROM Tipo_Tramite WHERE nombre_tipo LIKE ?))";

        String like = "%" + filtro + "%";
        String[] args = new String[]{
                String.valueOf(idUsuario), like, like, like, like
        };

        Cursor cursor = ejecutarConsulta(sql, args);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Tramite t = new Tramite();
                t.setIdTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tramite")));
                t.setNombreTramite(cursor.getString(cursor.getColumnIndexOrThrow("nombre_tramite")));
                t.setFrecuencia(cursor.getString(cursor.getColumnIndexOrThrow("frecuencia")));
                t.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                t.setHora(cursor.getString(cursor.getColumnIndexOrThrow("hora")));
                t.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                t.setCiudadId(cursor.getInt(cursor.getColumnIndexOrThrow("ciudad_id")));
                t.setLugar(cursor.getString(cursor.getColumnIndexOrThrow("lugar")));
                t.setTieneValor(cursor.getInt(cursor.getColumnIndexOrThrow("tiene_valor")) == 1);
                t.setValorMonetario(cursor.getDouble(cursor.getColumnIndexOrThrow("valor_monetario")));
                t.setIdUsuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
                t.setIdTipoTramite(cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_tramite")));
                lista.add(t);
            }
            cursor.close();
        }
        close();
        return lista;
    }
}
