import java.util.Locale;

public class InstalacionElectricaCertificada extends Instalacion {
    private double valorMateriales;
    private static final double cargoCertificado = 120;
    private static final double porcentajeGarantia = 0.05;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
            double horasEstimadas, Zona zona, int puntos, double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        Validador.mayorQueCero(valorMateriales, "Valor de materiales");
        Validador.mayorQueCero(super.calcularCosto() + cargoCertificado
                + valorMateriales * porcentajeGarantia, "Costo");
        this.valorMateriales = valorMateriales;
    }

    public double calcularGarantia() { return valorMateriales * porcentajeGarantia; }
    @Override public double calcularCosto() { return super.calcularCosto() + cargoCertificado + calcularGarantia(); }
    @Override public int tiempoAtencionDias() { return super.tiempoAtencionDias() + 2; }
    @Override public int visitasMaximas() { return 2; }
    @Override public String getCategoria() { return "Instalación eléctrica certificada"; }
    @Override protected String detalleEspecifico() {
        return super.detalleEspecifico() + String.format(Locale.US,
                "; materiales Q%.2f; certificado Q%.2f; garantía Q%.2f",
                valorMateriales, cargoCertificado, calcularGarantia());
    }
    public double getValorMateriales() { return valorMateriales; }
}
