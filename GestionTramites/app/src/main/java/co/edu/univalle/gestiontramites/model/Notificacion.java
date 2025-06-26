package co.edu.univalle.gestiontramites.model;

public class Notificacion {
    private int idNotificacion;
    private int idTramite;
    private String fechaHoraProgramada;
    private String mensaje;
    private boolean enviada;

    public Notificacion() {}

    public Notificacion(int idNotificacion, int idTramite, String fechaHoraProgramada, String mensaje, boolean enviada) {
        this.idNotificacion = idNotificacion;
        this.idTramite = idTramite;
        this.fechaHoraProgramada = fechaHoraProgramada;
        this.mensaje = mensaje;
        this.enviada = enviada;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public int getIdTramite() { return idTramite; }
    public void setIdTramite(int idTramite) { this.idTramite = idTramite; }

    public String getFechaHoraProgramada() { return fechaHoraProgramada; }
    public void setFechaHoraProgramada(String fechaHoraProgramada) { this.fechaHoraProgramada = fechaHoraProgramada; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isEnviada() { return enviada; }
    public void setEnviada(boolean enviada) { this.enviada = enviada; }
}
