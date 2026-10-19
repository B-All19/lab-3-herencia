public class Diagnostico extends OrdenServicio {
    private static final double horasMaximas = 1.5;
    private static final double tarifaMetropolitana = 150;
    private static final double tarifaPeriferica = 225;

    public Diagnostico(String codigo, String cliente, String direccion, double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > horasMaximas) {
            throw new OperacionInvalidaException("El diagnóstico no puede superar 1.5 horas.");
        }
    }

    @Override public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? tarifaMetropolitana : tarifaPeriferica;
    }
    @Override public int tiempoAtencionDias() { return getZona() == Zona.METROPOLITANA ? 1 : 2; }
    @Override public int visitasMaximas() { return 3; }
    @Override public String getCategoria() { return "Diagnóstico"; }
    @Override protected String detalleEspecifico() { return "Tarifa fija; máximo 1.5 horas"; }
}
