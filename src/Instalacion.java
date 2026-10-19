import java.util.Locale;

public class Instalacion extends OrdenServicio {
    private int puntos;
    private static final double horasPorPunto = 0.75;
    private static final double tarifaPorHora = 90;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        Validador.mayorQueCero(puntos, "Puntos");
        // Valida el rango representable sin invocar métodos sobrescribibles desde el constructor.
        Validador.mayorQueCero(zona.getTarifaBase()
                + Math.ceil(Math.max(horasEstimadas, puntos * horasPorPunto)) * tarifaPorHora, "Costo");
        this.puntos = puntos;
    }

    public double horasPorPuntos() { return puntos * horasPorPunto; }
    public double horasFacturables() { return Math.ceil(Math.max(getHorasEstimadas(), horasPorPuntos())); }
    @Override public double calcularCosto() { return getZona().getTarifaBase() + tarifaPorHora * horasFacturables(); }
    @Override public int tiempoAtencionDias() { return getZona() == Zona.METROPOLITANA ? 3 : 5; }
    @Override public int visitasMaximas() { return 3; }
    @Override public String getCategoria() { return "Instalación"; }
    @Override protected String detalleEspecifico() {
        return String.format(Locale.US, "%d puntos; %.2f h por puntos; %.0f h facturables",
                puntos, horasPorPuntos(), horasFacturables());
    }
    public int getPuntos() { return puntos; }
}
