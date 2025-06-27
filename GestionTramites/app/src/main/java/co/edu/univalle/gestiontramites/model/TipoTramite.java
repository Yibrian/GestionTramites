package co.edu.univalle.gestiontramites.model;

public class TipoTramite {
    private int idTipoTramite;
    private String nombreTipo;

    public TipoTramite() {}

    public TipoTramite(int idTipoTramite, String nombreTipo) {
        this.idTipoTramite = idTipoTramite;
        this.nombreTipo = nombreTipo;
    }

    public int getIdTipoTramite() { return idTipoTramite; }
    public void setIdTipoTramite(int idTipoTramite) { this.idTipoTramite = idTipoTramite; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
}
