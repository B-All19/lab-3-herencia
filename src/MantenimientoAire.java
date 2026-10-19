public class MantenimientoAire extends OrdenServicio {
    private int capacidadBtu;
    private static final int btuMinimo = 9000;
    private static final int btuMaximo = 60000;
    private static final double tarifaPorHora = 110;
    private static final double cargoGas = 80;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int capacidadBtu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new OperacionInvalidaException("El mantenimiento de aire solo se atiende en zona metropolitana.");
        }
        if (capacidadBtu < btuMinimo || capacidadBtu > btuMaximo) {
            throw new OperacionInvalidaException("La capacidad debe estar entre 9000 y 60000 BTU.");
        }
        Validador.mayorQueCero(zona.getTarifaBase() + horasEstimadas * tarifaPorHora + cargoGas, "Costo");
        this.capacidadBtu = capacidadBtu;
    }

    @Override public double calcularCosto() { return getZona().getTarifaBase() + getHorasEstimadas() * tarifaPorHora + cargoGas; }
    @Override public int tiempoAtencionDias() { return 2; }
    @Override public int visitasMaximas() { return 1; }
    @Override public String getCategoria() { return "Mantenimiento de aire acondicionado"; }
    @Override protected String detalleEspecifico() { return capacidadBtu + " BTU; gas Q80.00"; }
    @Override public boolean admiteCuadrilla(Cuadrilla cuadrilla) {
        return super.admiteCuadrilla(cuadrilla) && cuadrilla.isCertificadaRefrigeracion();
    }
    public int getCapacidadBtu() { return capacidadBtu; }
}
