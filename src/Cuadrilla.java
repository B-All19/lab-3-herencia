import java.util.Locale;

public class Cuadrilla {
    private String codigo;
    private double capacidadHoras;
    private boolean certificadaRefrigeracion;
    private Jornada jornadaActual = null;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        Validador.textoNoVacio(codigo, "Código de cuadrilla");
        Validador.mayorQueCero(capacidadHoras, "Capacidad de horas");
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }
    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificadaRefrigeracion() { return certificadaRefrigeracion; }
    public Jornada getJornadaActual() { return jornadaActual; }
    public boolean estaLibre() { return jornadaActual == null; }

    void ocupar(Jornada jornada) {
        if (jornada == null || jornada.getCuadrilla() != this || !estaLibre()) {
            throw new OperacionInvalidaException("La cuadrilla " + codigo + " ya está ocupada o la jornada no corresponde.");
        }
        jornadaActual = jornada;
    }

    void liberar() {
        if (jornadaActual == null || !jornadaActual.estaCerrada()) {
            throw new OperacionInvalidaException("La cuadrilla solo se libera al cerrar su jornada.");
        }
        jornadaActual = null;
    }

    @Override public String toString() {
        return String.format(Locale.US, "%s | Capacidad: %.2f h | Refrigeración: %s | Jornada: %s",
                codigo, capacidadHoras, certificadaRefrigeracion ? "sí" : "no",
                jornadaActual == null ? "libre" : jornadaActual.getNumero());
    }
}
