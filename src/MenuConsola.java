import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Entrada/salida: crea categorías concretas y delega las reglas al modelo. */
public class MenuConsola {
    private Scanner scanner;
    private TecniHogar sistema;

    public MenuConsola(Scanner scanner, TecniHogar sistema) {
        if (scanner == null || sistema == null) throw new OperacionInvalidaException("Faltan la entrada o el sistema.");
        this.scanner = scanner;
        this.sistema = sistema;
    }

    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n=== TECNIHOGAR ===");
            System.out.println("1. Cotizar orden              2. Registrar orden");
            System.out.println("3. Registrar cuadrilla        4. Crear jornada");
            System.out.println("5. Asignar orden              6. Retirar orden");
            System.out.println("7. Iniciar jornada            8. Registrar resultado");
            System.out.println("9. Consultar órdenes         10. Consultar cuadrillas");
            System.out.println("11. Reporte de órdenes       12. Reporte de ingresos");
            System.out.println("13. Jornadas abiertas         0. Salir");
            try {
                int opcion = leerEntero("Opción: ");
                switch (opcion) {
                    case 0:
                        continuar = false;
                        System.out.println("Hasta pronto.");
                        break;
                    case 1:
                        System.out.println(sistema.cotizar(crearOrden()));
                        break;
                    case 2: {
                        OrdenServicio orden = crearOrden();
                        sistema.registrarOrden(orden);
                        System.out.printf(Locale.US, "Orden %s registrada. Cobro: Q%.2f.%n", orden.getCodigo(), orden.calcularCosto());
                        break;
                    }
                    case 3: {
                        String codigo = leerTexto("Código de cuadrilla: ");
                        double capacidad = leerDecimal("Capacidad de horas: ");
                        int certificada = leerEntero("Certificada en refrigeración (1=sí, 0=no): ");
                        if (certificada != 0 && certificada != 1) throw new OperacionInvalidaException("Use 1 para sí o 0 para no.");
                        sistema.registrarCuadrilla(new Cuadrilla(codigo, capacidad, certificada == 1));
                        System.out.println("Cuadrilla registrada.");
                        break;
                    }
                    case 4:
                        System.out.println("Creada: " + sistema.crearJornada(leerTexto("Código de cuadrilla: ")));
                        break;
                    case 5: {
                        int numero = leerEntero("Número de jornada: ");
                        sistema.asignarOrden(numero, leerTexto("Código de orden: "));
                        System.out.println("Orden asignada; no se generó un cobro.");
                        break;
                    }
                    case 6: {
                        int numero = leerEntero("Número de jornada: ");
                        sistema.retirarOrden(numero, leerTexto("Código de orden: "));
                        System.out.println("Orden retirada y pendiente; no se modificaron los ingresos.");
                        break;
                    }
                    case 7:
                        sistema.iniciarJornada(leerEntero("Número de jornada: "));
                        System.out.println("Jornada iniciada. Se cobraron las visitas adicionales que correspondían.");
                        break;
                    case 8: {
                        int numero = leerEntero("Número de jornada: ");
                        String codigo = leerTexto("Código de orden: ");
                        int resultado = leerEntero("Resultado (1=completada, 2=fallida): ");
                        if (resultado != 1 && resultado != 2) throw new OperacionInvalidaException("El resultado debe ser 1 o 2.");
                        sistema.registrarResultado(numero, codigo, resultado == 1 ? ResultadoVisita.COMPLETADA : ResultadoVisita.FALLIDA);
                        System.out.println("Resultado registrado: " + sistema.buscarOrden(codigo).getEstado());
                        break;
                    }
                    case 9: {
                        String codigo = leerTexto("Código de orden o * para listar todas: ");
                        if (codigo.equals("*")) sistema.listarOrdenes().forEach(System.out::println);
                        else System.out.println(sistema.buscarOrden(codigo));
                        break;
                    }
                    case 10: {
                        String codigo = leerTexto("Código de cuadrilla o * para listar todas: ");
                        if (codigo.equals("*")) sistema.listarCuadrillas().forEach(System.out::println);
                        else System.out.println(sistema.buscarCuadrilla(codigo));
                        break;
                    }
                    case 11:
                        System.out.println("ÓRDENES POR CATEGORÍA Y ESTADO");
                        sistema.conteoPorCategoriaYEstado().forEach((categoria, estados) -> {
                            System.out.println(categoria + ":");
                            estados.forEach((estado, cantidad) -> System.out.println("  " + estado + ": " + cantidad));
                        });
                        break;
                    case 12: {
                        RegistroIngresos ingresos = sistema.getIngresos();
                        System.out.printf(Locale.US, "Ingreso total: Q%.2f%nRegistro: Q%.2f%nVisitas adicionales: Q%.2f%n",
                                ingresos.total(), ingresos.totalPorTipo(TipoIngreso.REGISTRO), ingresos.totalPorTipo(TipoIngreso.VISITA_ADICIONAL));
                        System.out.println("Por categoría (registro + visitas adicionales):");
                        Map<String, Double> totales = ingresos.totalPorCategoria();
                        for (String categoria : sistema.conteoPorCategoriaYEstado().keySet()) {
                            System.out.printf(Locale.US, "  %s: Q%.2f%n", categoria, totales.getOrDefault(categoria, 0.0));
                        }
                        break;
                    }
                    case 13:
                        if (sistema.jornadasAbiertas().isEmpty()) System.out.println("No hay jornadas abiertas.");
                        else sistema.jornadasAbiertas().forEach(System.out::println);
                        break;
                    default:
                        System.out.println("Opción inválida. Elija una opción del menú.");
                }
            } catch (OperacionInvalidaException ex) {
                System.out.println("Operación rechazada: " + ex.getMessage());
            } catch (NoSuchElementException ex) {
                System.out.println("\nEntrada finalizada. Hasta pronto.");
                continuar = false;
            }
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try { return Integer.parseInt(texto); }
            catch (NumberFormatException ex) { System.out.println("Formato inválido: ingrese un número entero."); }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isFinite(valor)) return valor;
            } catch (NumberFormatException ex) {
                // Repite la lectura sin terminar el programa.
            }
            System.out.println("Formato inválido: ingrese un número decimal finito (por ejemplo, 2.5).");
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) throw new NoSuchElementException("Fin de entrada");
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            System.out.println("El texto no puede estar vacío.");
        }
    }

    private Zona leerZona(String mensaje) {
        while (true) {
            int opcion = leerEntero(mensaje + " (1=metropolitana, 2=periférica): ");
            if (opcion == 1) return Zona.METROPOLITANA;
            if (opcion == 2) return Zona.PERIFERICA;
            System.out.println("Zona inválida: elija 1 o 2.");
        }
    }

    private OrdenServicio crearOrden() {
        System.out.println("Categorías: 1=diagnóstico, 2=instalación, 3=eléctrica certificada, 4=aire acondicionado");
        int categoria = leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) throw new OperacionInvalidaException("Categoría inválida.");
        String codigo = leerTexto("Código de orden: ");
        String cliente = leerTexto("Cliente: ");
        String direccion = leerTexto("Dirección: ");
        double horas = leerDecimal("Horas estimadas: ");
        Zona zona = leerZona("Zona");
        // Esta selección solo construye objetos; el modelo decide costo, tiempo y restricciones.
        switch (categoria) {
            case 1: return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2: return new Instalacion(codigo, cliente, direccion, horas, zona, leerEntero("Puntos: "));
            case 3: {
                int puntos = leerEntero("Puntos: ");
                return new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona,
                        puntos, leerDecimal("Valor de materiales: Q"));
            }
            case 4: return new MantenimientoAire(codigo, cliente, direccion, horas, zona, leerEntero("Capacidad en BTU: "));
            default: throw new OperacionInvalidaException("Categoría inválida.");
        }
    }
}
