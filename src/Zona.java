public enum Zona {
    METROPOLITANA(100), PERIFERICA(175);

    private final double tarifaBase;

    private Zona(double tarifaBase) { this.tarifaBase = tarifaBase; }
    public double getTarifaBase() { return tarifaBase; }
}
