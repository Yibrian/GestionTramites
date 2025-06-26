package co.edu.univalle.gestiontramites.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import co.edu.univalle.gestiontramites.R;
import co.edu.univalle.gestiontramites.bd.ConexionBD;
import co.edu.univalle.gestiontramites.model.Tramite;

public class TramiteAdapter extends RecyclerView.Adapter<TramiteAdapter.TramiteViewHolder> {

    private List<Tramite> lista;
    private List<String> nombresCiudades;
    private List<String> nombresTipos;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tramite tramite);
    }

    public TramiteAdapter(Context context, List<Tramite> lista, List<String> nombresCiudades, List<String> nombresTipos) {
        this.context = context;
        this.lista = lista;
        this.nombresCiudades = nombresCiudades;
        this.nombresTipos = nombresTipos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TramiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tramite, parent, false);
        return new TramiteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TramiteViewHolder holder, int position) {
        Tramite t = lista.get(position);
        holder.tvNombre.setText(t.getNombreTramite());
        holder.tvFechaHora.setText(t.getFecha() + " - " + t.getHora());
        holder.tvDescripcion.setText(t.getDescripcion());

        // Ciudad y tipo (ajusta el índice si es necesario)
        String ciudad = (t.getCiudadId() > 0 && t.getCiudadId() <= nombresCiudades.size())
                ? nombresCiudades.get(t.getCiudadId() - 1) : "Desconocida";
        String tipo = (t.getIdTipoTramite() > 0 && t.getIdTipoTramite() <= nombresTipos.size())
                ? nombresTipos.get(t.getIdTipoTramite() - 1) : "Desconocido";
        holder.tvCiudad.setText("Ciudad: " + ciudad);
        holder.tvTipo.setText("Tipo: " + tipo);

        // Valor monetario
        if (t.isTieneValor()) {
            holder.tvValor.setText("Valor: $" + String.format("%.0f", t.getValorMonetario()));
        } else {
            holder.tvValor.setText("Sin valor monetario");
        }

        // Requisitos
        ConexionBD conexionBD = ConexionBD.getInstance(context);
        boolean tieneRequisitos = !conexionBD.obtenerRequisitosPorTramite(t.getIdTramite()).isEmpty();
        holder.tvRequisitos.setText("Requisitos: " + (tieneRequisitos ? "Sí" : "No"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Tramite> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    static class TramiteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFechaHora, tvDescripcion, tvCiudad, tvTipo, tvValor, tvRequisitos;

        TramiteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTramite);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvCiudad = itemView.findViewById(R.id.tvCiudad);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvRequisitos = itemView.findViewById(R.id.tvRequisitos);
        }
    }
}
