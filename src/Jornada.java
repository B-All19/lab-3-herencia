import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Programa trabajo y conserva las órdenes y los resultados de esta jornada. */
public class Jornada {
    private int numero;
    private Cuadrilla cuadrilla;
    private List<OrdenServicio> ordenes;
    private Map<OrdenServicio, ResultadoVisita> resultados;
    private EstadoJornada estado = EstadoJornada.EN_PREPARACION;

    public Jornada(int numero, Cuadrilla cuadrilla) {
        Validador.mayorQueCero(numero, "Número de jornada");
        if (cuadrilla == null) throw new OperacionInvalidaException("Debe indicar una cuadrilla.");
        if (!cuadrilla.estaLibre()) throw new OperacionInvalidaException("La cuadrilla ya tiene una jornada sin cerrar.");
        this.numero = numero;
        this.cuadrilla = cuadrilla;
        ordenes = new ArrayList<>();
        resultados = new LinkedHashMap<>();
        cuadrilla.ocupar(this);
    }

    public void asignar(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);
        if (orden == null) throw new OperacionInvalidaException("Debe indicar una orden.");
        if (orden.getEstado() != EstadoOrden.PENDIENTE || orden.getJornadaAsignada() != null
                || orden.getVisitasRealizadas() >= orden.visitasMaximas() || ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden no está pendiente o ya tiene una jornada.");
        }
        if (!orden.admiteCuadrilla(cuadrilla)) {
            throw new OperacionInvalidaException("La cuadrilla no cumple los requisitos de esta orden.");
        }
        // La capacidad usa horas estimadas. BigDecimal evita rechazar 0.1 + 0.2 en capacidad 0.3.
        BigDecimal suma = sumarHoras().add(BigDecimal.valueOf(orden.getHorasEstimadas()));
        if (suma.compareTo(BigDecimal.valueOf(cuadrilla.getCapacidadHoras())) > 0) {
            throw new OperacionInvalidaException("La asignación supera la capacidad de la cuadrilla.");
        }
        orden.asignarA(this);
        ordenes.add(orden);
    }

    public void retirar(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);
        if (orden == null || !ordenes.contains(orden) || orden.getJornadaAsignada() != this) {
            throw new OperacionInvalidaException("La orden no está asignada a esta jornada.");
        }
        orden.liberar();
        ordenes.remove(orden);
    }

    public void iniciar(RegistroIngresos ingresos) {
        exigirEstado(EstadoJornada.EN_PREPARACION);
        if (ordenes.isEmpty()) throw new OperacionInvalidaException("No se puede iniciar una jornada vacía.");
        if (ingresos == null) throw new OperacionInvalidaException("Falta el registro de ingresos.");
        // Primero valida el lote completo; después cambia estados y registra cargos.
        for (OrdenServicio orden : ordenes) {
            if (orden.getEstado() != EstadoOrden.ASIGNADA || orden.getJornadaAsignada() != this
                    || orden.getVisitasRealizadas() >= orden.visitasMaximas()) {
                throw new OperacionInvalidaException("Una orden no está disponible para iniciar la visita.");
            }
            Validador.textoNoVacio(orden.getCategoria(), "Categoría");
        }
        for (OrdenServicio orden : ordenes) {
            double cargo = orden.iniciarVisita();
            if (cargo > 0) ingresos.registrar(TipoIngreso.VISITA_ADICIONAL, cargo, orden.getCategoria(), orden.getCodigo());
        }
        estado = EstadoJornada.EN_CURSO;
    }

    public void registrarResultado(OrdenServicio orden, ResultadoVisita resultado) {
        exigirEstado(EstadoJornada.EN_CURSO);
        if (orden == null || !ordenes.contains(orden) || resultados.containsKey(orden)) {
            throw new OperacionInvalidaException("La orden no pertenece a esta jornada o ya tiene resultado.");
        }
        if (resultado == null || orden.getJornadaAsignada() != this || orden.getEstado() != EstadoOrden.EN_ATENCION) {
            throw new OperacionInvalidaException("La orden debe estar en atención y el resultado debe ser válido.");
        }
        orden.registrarResultado(resultado);
        resultados.put(orden, resultado);
        if (resultados.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
            cuadrilla.liberar();
        }
    }

    private void exigirEstado(EstadoJornada requerido) {
        if (estado != requerido) throw new OperacionInvalidaException("La jornada debe estar " + requerido + "; está " + estado + ".");
    }

    private BigDecimal sumarHoras() {
        BigDecimal suma = BigDecimal.ZERO;
        for (OrdenServicio orden : ordenes) suma = suma.add(BigDecimal.valueOf(orden.getHorasEstimadas()));
        return suma;
    }
    public double horasAsignadas() { return sumarHoras().doubleValue(); }
    public double capacidadDisponible() { return BigDecimal.valueOf(cuadrilla.getCapacidadHoras()).subtract(sumarHoras()).doubleValue(); }
    public int getNumero() { return numero; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public List<OrdenServicio> getOrdenes() { return Collections.unmodifiableList(new ArrayList<>(ordenes)); }
    public boolean estaCerrada() { return estado == EstadoJornada.CERRADA; }

    @Override public String toString() {
        StringBuilder detalle = new StringBuilder();
        for (OrdenServicio orden : ordenes) {
            if (detalle.length() > 0) detalle.append(", ");
            detalle.append(orden.getCodigo()).append(": ")
                    .append(resultados.containsKey(orden) ? resultados.get(orden) : "sin resultado");
        }
        return String.format(Locale.US, "Jornada %d | Cuadrilla: %s | %s | Asignadas: %.2f h | Disponibles: %.2f h | [%s]",
                numero, cuadrilla.getCodigo(), estado, horasAsignadas(), capacidadDisponible(), detalle);
    }
}
