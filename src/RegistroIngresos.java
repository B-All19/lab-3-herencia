import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistroIngresos {
    private List<Ingreso> movimientos;

    public RegistroIngresos() { movimientos = new ArrayList<>(); }

    public void registrar(TipoIngreso tipo, double monto, String categoria, String codigoOrden) {
        Ingreso ingreso = new Ingreso(tipo, monto, categoria, codigoOrden);
        // Evita que un desbordamiento numérico deje un total no representable.
        Validador.mayorQueCero(total() + monto, "Ingreso acumulado");
        movimientos.add(ingreso);
    }

    public double total() {
        BigDecimal suma = BigDecimal.ZERO;
        for (Ingreso ingreso : movimientos) suma = suma.add(BigDecimal.valueOf(ingreso.getMonto()));
        return suma.doubleValue();
    }

    public double totalPorTipo(TipoIngreso tipo) {
        if (tipo == null) throw new OperacionInvalidaException("Debe indicar el tipo de ingreso.");
        BigDecimal suma = BigDecimal.ZERO;
        for (Ingreso ingreso : movimientos) {
            if (ingreso.getTipo() == tipo) suma = suma.add(BigDecimal.valueOf(ingreso.getMonto()));
        }
        return suma.doubleValue();
    }

    public Map<String, Double> totalPorCategoria() {
        Map<String, BigDecimal> sumas = new LinkedHashMap<>();
        for (Ingreso ingreso : movimientos) {
            sumas.merge(ingreso.getCategoria(), BigDecimal.valueOf(ingreso.getMonto()), BigDecimal::add);
        }
        Map<String, Double> totales = new LinkedHashMap<>();
        sumas.forEach((categoria, monto) -> totales.put(categoria, monto.doubleValue()));
        return Collections.unmodifiableMap(totales);
    }
}
