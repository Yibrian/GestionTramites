package co.edu.univalle.gestiontramites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import co.edu.univalle.gestiontramites.adapter.TramiteAdapter;
import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.TipoTramite;
import co.edu.univalle.gestiontramites.model.Tramite;

public class InicioActivity extends AppCompatActivity {

    private RecyclerView rvTramites;
    private FloatingActionButton fabAgregarTramite;
    private TramiteAdapter tramiteAdapter;
    private ConexionBD conexionBD;
    private int usuarioId;
    private List<String> nombresCiudades;
    private List<String> nombresTipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = findViewById(R.id.toolbarInicio);
        setSupportActionBar(toolbar);

        rvTramites = findViewById(R.id.rvTramites);
        fabAgregarTramite = findViewById(R.id.fabAgregarTramite);
        rvTramites.setLayoutManager(new LinearLayoutManager(this));

        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        conexionBD = ConexionBD.getInstance(this);

        // Obtener nombres de ciudades y tipos
        nombresCiudades = conexionBD.obtenerNombresCiudades();
        nombresTipos = new ArrayList<>();
        for (TipoTramite t : conexionBD.obtenerTiposTramite()) {
            nombresTipos.add(t.getNombreTipo());
        }

        List<Tramite> listaTramites = conexionBD.obtenerTramitesPorUsuario(usuarioId);
        tramiteAdapter = new TramiteAdapter(this, listaTramites, nombresCiudades, nombresTipos);
        rvTramites.setAdapter(tramiteAdapter);

        tramiteAdapter.setOnItemClickListener(tramite -> {
            Intent intent = new Intent(InicioActivity.this, EditarTramiteActivity.class);
            intent.putExtra("tramite_id", tramite.getIdTramite());
            intent.putExtra("usuario_id", usuarioId);
            startActivity(intent);
        });

        fabAgregarTramite.setOnClickListener(view -> {
            Intent intent = new Intent(InicioActivity.this, TramiteActivity.class);
            intent.putExtra("usuario_id", usuarioId);
            startActivity(intent);
        });

        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarTramites(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recargarTramites();
    }

    private void recargarTramites() {
        List<Tramite> listaTramites = conexionBD.obtenerTramitesPorUsuario(usuarioId);
        tramiteAdapter.actualizarLista(listaTramites);
    }

    private void filtrarTramites(String filtro) {
        List<Tramite> listaFiltrada;
        if (filtro.isEmpty()) {
            listaFiltrada = conexionBD.obtenerTramitesPorUsuario(usuarioId);
        } else {
            listaFiltrada = conexionBD.buscarTramites(usuarioId, filtro);
        }
        tramiteAdapter.actualizarLista(listaFiltrada);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_gestionar_tipos) {
            Intent intent = new Intent(this, GestionTiposActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
