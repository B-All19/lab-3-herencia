import java.util.Locale;

/** Raíz de la jerarquía: información común y ciclo de vida de una orden. */
public abstract class OrdenServicio {
    private String codigo;
    private String cliente;
    private String direccion;
    private double horasEstimadas;
    private Zona zona;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitasRealizadas = 0;
    private Jornada jornadaAsignada = null;
    private static final double cargoVisitaAdicional = 40;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        Validador.textoNoVacio(codigo, "Código de orden");
        Validador.textoNoVacio(cliente, "Cliente");
        Validador.textoNoVacio(direccion, "Dirección");
        Validador.mayorQueCero(horasEstimadas, "Horas estimadas");
        if (zona == null) throw new OperacionInvalidaException("Debe indicar una zona.");
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
    }

    public abstract double calcularCosto();
    public abstract int tiempoAtencionDias();
    public abstract int visitasMaximas();
    public abstract String getCategoria();
    protected abstract String detalleEspecifico();

    public boolean admiteCuadrilla(Cuadrilla cuadrilla) { return cuadrilla != null; }

    // Visibilidad de paquete: las transiciones las coordinan las jornadas.
    void asignarA(Jornada jornada) {
        if (jornada == null || jornada.getEstado() != EstadoJornada.EN_PREPARACION) {
            throw new OperacionInvalidaException("La jornada debe estar en preparación.");
        }
        if (estado != EstadoOrden.PENDIENTE || jornadaAsignada != null
                || visitasRealizadas >= visitasMaximas()) {
            throw new OperacionInvalidaException("La orden " + codigo + " no está disponible para asignarse.");
        }
        jornadaAsignada = jornada;
        estado = EstadoOrden.ASIGNADA;
    }

    void liberar() {
        if (estado != EstadoOrden.ASIGNADA || jornadaAsignada == null
                || jornadaAsignada.getEstado() != EstadoJornada.EN_PREPARACION) {
            throw new OperacionInvalidaException("Solo se puede retirar una orden de una jornada en preparación.");
        }
        jornadaAsignada = null;
        estado = EstadoOrden.PENDIENTE;
    }

    double iniciarVisita() {
        if (estado != EstadoOrden.ASIGNADA || jornadaAsignada == null
                || visitasRealizadas >= visitasMaximas()) {
            throw new OperacionInvalidaException("No se puede iniciar una visita para " + codigo + ".");
        }
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
        return visitasRealizadas > 1 ? cargoVisitaAdicional : 0;
    }

    void registrarResultado(ResultadoVisita resultado) {
        if (resultado == null || estado != EstadoOrden.EN_ATENCION) {
            throw new OperacionInvalidaException("El resultado requiere una orden en atención y un valor válido.");
        }
        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else {
            estado = visitasRealizadas >= visitasMaximas()
                    ? EstadoOrden.CANCELADA : EstadoOrden.PENDIENTE;
        }
        jornadaAsignada = null;
    }

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getVisitasRealizadas() { return visitasRealizadas; }
    public Jornada getJornadaAsignada() { return jornadaAsignada; }

    @Override public String toString() {
        return String.format(Locale.US,
                "%s | %s | Cliente: %s | Dirección: %s | %s | %.2f h | Q%.2f | %d días hábiles"
                + " | %s | Visitas: %d/%d | Jornada: %s | %s",
                codigo, getCategoria(), cliente, direccion, zona, horasEstimadas,
                calcularCosto(), tiempoAtencionDias(), estado, visitasRealizadas, visitasMaximas(),
                jornadaAsignada == null ? "ninguna" : jornadaAsignada.getNumero(), detalleEspecifico());
    }
}
