/** Validaciones compartidas; nunca modifica los objetos recibidos. */
public final class Validador {
    private Validador() { }

    public static void textoNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new OperacionInvalidaException(campo + " no puede estar vacío.");
        }
    }

    public static void mayorQueCero(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new OperacionInvalidaException(campo + " debe ser un número finito mayor que cero.");
        }
    }

    public static void mayorQueCero(int valor, String campo) {
        if (valor <= 0) {
            throw new OperacionInvalidaException(campo + " debe ser un entero mayor que cero.");
        }
    }
}
