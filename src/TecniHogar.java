import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Catálogos y coordinación. No decide reglas por tipo concreto de servicio. */
public class TecniHogar {
    private Map<String, OrdenServicio> ordenes;
    private Map<String, Cuadrilla> cuadrillas;
    private List<Jornada> jornadas;
    private RegistroIngresos ingresos;
    private int siguienteNumeroJornada = 1;

    public TecniHogar() {
        ordenes = new LinkedHashMap<>();
        cuadrillas = new LinkedHashMap<>();
        jornadas = new ArrayList<>();
        ingresos = new RegistroIngresos();
    }

    public String cotizar(OrdenServicio orden) {
        validarOrdenNueva(orden);
        return String.format(Locale.US, "Cotización %s | %s | Costo: Q%.2f | Atención: %d días hábiles. No registrada.",
                orden.getCodigo(), orden.getCategoria(), orden.calcularCosto(), orden.tiempoAtencionDias());
    }

    public void registrarOrden(OrdenServicio orden) {
        validarOrdenNueva(orden);
        // El movimiento se valida antes de añadir la orden al catálogo.
        ingresos.registrar(TipoIngreso.REGISTRO, orden.calcularCosto(), orden.getCategoria(), orden.getCodigo());
        ordenes.put(orden.getCodigo(), orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) throw new OperacionInvalidaException("Debe indicar una cuadrilla.");
        if (cuadrillas.containsKey(cuadrilla.getCodigo())) {
            throw new OperacionInvalidaException("Ya existe la cuadrilla " + cuadrilla.getCodigo() + ".");
        }
        if (!cuadrilla.estaLibre()) throw new OperacionInvalidaException("La cuadrilla nueva debe estar libre.");
        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        Jornada jornada = new Jornada(siguienteNumeroJornada, cuadrilla);
        jornadas.add(jornada);
        siguienteNumeroJornada++;
        return jornada;
    }

    public void asignarOrden(int numeroJornada, String codigoOrden) {
        buscarJornada(numeroJornada).asignar(buscarOrden(codigoOrden));
    }
    public void retirarOrden(int numeroJornada, String codigoOrden) {
        buscarJornada(numeroJornada).retirar(buscarOrden(codigoOrden));
    }
    public void iniciarJornada(int numeroJornada) { buscarJornada(numeroJornada).iniciar(ingresos); }
    public void registrarResultado(int numeroJornada, String codigoOrden, ResultadoVisita resultado) {
        buscarJornada(numeroJornada).registrarResultado(buscarOrden(codigoOrden), resultado);
    }

    public OrdenServicio buscarOrden(String codigo) {
        Validador.textoNoVacio(codigo, "Código de orden");
        OrdenServicio orden = ordenes.get(codigo.trim());
        if (orden == null) throw new OperacionInvalidaException("No existe la orden " + codigo.trim() + ".");
        return orden;
    }
    public Cuadrilla buscarCuadrilla(String codigo) {
        Validador.textoNoVacio(codigo, "Código de cuadrilla");
        Cuadrilla cuadrilla = cuadrillas.get(codigo.trim());
        if (cuadrilla == null) throw new OperacionInvalidaException("No existe la cuadrilla " + codigo.trim() + ".");
        return cuadrilla;
    }
    private Jornada buscarJornada(int numero) {
        for (Jornada jornada : jornadas) if (jornada.getNumero() == numero) return jornada;
        throw new OperacionInvalidaException("No existe la jornada " + numero + ".");
    }
    public List<OrdenServicio> listarOrdenes() { return Collections.unmodifiableList(new ArrayList<>(ordenes.values())); }
    public List<Cuadrilla> listarCuadrillas() { return Collections.unmodifiableList(new ArrayList<>(cuadrillas.values())); }

    public Map<String, Map<EstadoOrden, Integer>> conteoPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> conteo = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            Map<EstadoOrden, Integer> estados = conteo.computeIfAbsent(orden.getCategoria(), categoria -> {
                Map<EstadoOrden, Integer> fila = new EnumMap<>(EstadoOrden.class);
                for (EstadoOrden estado : EstadoOrden.values()) fila.put(estado, 0);
                return fila;
            });
            estados.put(orden.getEstado(), estados.get(orden.getEstado()) + 1);
        }
        conteo.replaceAll((categoria, estados) -> Collections.unmodifiableMap(estados));
        return Collections.unmodifiableMap(conteo);
    }
    public RegistroIngresos getIngresos() { return ingresos; }
    public List<Jornada> jornadasAbiertas() {
        List<Jornada> abiertas = new ArrayList<>();
        for (Jornada jornada : jornadas) if (!jornada.estaCerrada()) abiertas.add(jornada);
        return Collections.unmodifiableList(abiertas);
    }

    void cargarOrdenInicial(OrdenServicio orden) {
        validarOrdenNueva(orden);
        ordenes.put(orden.getCodigo(), orden);
    }

    private void validarOrdenNueva(OrdenServicio orden) {
        if (orden == null) throw new OperacionInvalidaException("Debe indicar una orden.");
        if (ordenes.containsKey(orden.getCodigo())) {
            throw new OperacionInvalidaException("Ya existe una orden con código " + orden.getCodigo() + ".");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE || orden.getVisitasRealizadas() != 0
                || orden.getJornadaAsignada() != null) {
            throw new OperacionInvalidaException("Una orden nueva debe estar pendiente y sin visitas.");
        }
        Validador.textoNoVacio(orden.getCategoria(), "Categoría");
        Validador.mayorQueCero(orden.calcularCosto(), "Costo");
    }
}
