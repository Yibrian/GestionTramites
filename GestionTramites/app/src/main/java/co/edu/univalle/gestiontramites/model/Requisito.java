package co.edu.univalle.gestiontramites.model;

public class Requisito {
    private int idRequisito;
    private String descripcionRequisito;
    private int idTramite;

    public Requisito() {}

    public Requisito(int idRequisito, String descripcionRequisito, int idTramite) {
        this.idRequisito = idRequisito;
        this.descripcionRequisito = descripcionRequisito;
        this.idTramite = idTramite;
    }

    public int getIdRequisito() { return idRequisito; }
    public void setIdRequisito(int idRequisito) { this.idRequisito = idRequisito; }

    public String getDescripcionRequisito() { return descripcionRequisito; }
    public void setDescripcionRequisito(String descripcionRequisito) { this.descripcionRequisito = descripcionRequisito; }

    public int getIdTramite() { return idTramite; }
    public void setIdTramite(int idTramite) { this.idTramite = idTramite; }
}
