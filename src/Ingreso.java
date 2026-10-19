/** Movimiento inmutable: el total se deriva del historial de movimientos. */
public final class Ingreso {
    private final TipoIngreso tipo;
    private final double monto;
    private final String categoria;
    private final String codigoOrden;

    public Ingreso(TipoIngreso tipo, double monto, String categoria, String codigoOrden) {
        if (tipo == null) throw new OperacionInvalidaException("Debe indicar el tipo de ingreso.");
        Validador.mayorQueCero(monto, "Monto");
        Validador.textoNoVacio(categoria, "Categoría");
        Validador.textoNoVacio(codigoOrden, "Código de orden");
        this.tipo = tipo;
        this.monto = monto;
        this.categoria = categoria.trim();
        this.codigoOrden = codigoOrden.trim();
    }
    public TipoIngreso getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public String getCodigoOrden() { return codigoOrden; }
}
