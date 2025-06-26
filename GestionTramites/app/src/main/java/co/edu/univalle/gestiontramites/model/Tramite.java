package co.edu.univalle.gestiontramites.model;

public class Tramite {
    private int idTramite;
    private String nombreTramite;
    private String frecuencia;
    private String fecha;
    private String hora;
    private String descripcion;
    private int ciudadId;
    private String lugar;
    private boolean tieneValor;
    private double valorMonetario;
    private int idUsuario;
    private int idTipoTramite;

    public Tramite() {}

    public Tramite(int idTramite, String nombreTramite, String frecuencia, String fecha, String hora,
                   String descripcion, int ciudadId, String lugar, boolean tieneValor,
                   double valorMonetario, int idUsuario, int idTipoTramite) {
        this.idTramite = idTramite;
        this.nombreTramite = nombreTramite;
        this.frecuencia = frecuencia;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.ciudadId = ciudadId;
        this.lugar = lugar;
        this.tieneValor = tieneValor;
        this.valorMonetario = valorMonetario;
        this.idUsuario = idUsuario;
        this.idTipoTramite = idTipoTramite;
    }

    public int getIdTramite() { return idTramite; }
    public void setIdTramite(int idTramite) { this.idTramite = idTramite; }

    public String getNombreTramite() { return nombreTramite; }
    public void setNombreTramite(String nombreTramite) { this.nombreTramite = nombreTramite; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCiudadId() { return ciudadId; }
    public void setCiudadId(int ciudadId) { this.ciudadId = ciudadId; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public boolean isTieneValor() { return tieneValor; }
    public void setTieneValor(boolean tieneValor) { this.tieneValor = tieneValor; }

    public double getValorMonetario() { return valorMonetario; }
    public void setValorMonetario(double valorMonetario) { this.valorMonetario = valorMonetario; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdTipoTramite() { return idTipoTramite; }
    public void setIdTipoTramite(int idTipoTramite) { this.idTipoTramite = idTipoTramite; }
}
