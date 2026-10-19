/** Ocho órdenes pendientes sin ingresos y tres cuadrillas. */
public class DatosIniciales {
    private DatosIniciales() { }

    public static void cargar(TecniHogar sistema) {
        sistema.cargarOrdenInicial(new Diagnostico("D001", "Ana López", "Zona 10, Ciudad de Guatemala", 1, Zona.METROPOLITANA));
        sistema.cargarOrdenInicial(new Diagnostico("D002", "Luis Pérez", "San Lucas Sacatepéquez", 1.5, Zona.PERIFERICA));
        sistema.cargarOrdenInicial(new Instalacion("I001", "María García", "Zona 7, Ciudad de Guatemala", 2.5, Zona.METROPOLITANA, 5));
        sistema.cargarOrdenInicial(new Instalacion("I002", "Carlos Ruiz", "Villa Nueva", 2.2, Zona.PERIFERICA, 2));
        sistema.cargarOrdenInicial(new InstalacionElectricaCertificada("E001", "Elena Díaz", "Zona 14, Ciudad de Guatemala", 2.5, Zona.METROPOLITANA, 5, 2000));
        sistema.cargarOrdenInicial(new InstalacionElectricaCertificada("E002", "Pedro Soto", "Amatitlán", 3.2, Zona.PERIFERICA, 2, 3500));
        sistema.cargarOrdenInicial(new MantenimientoAire("A001", "Rosa Méndez", "Zona 9, Ciudad de Guatemala", 1.5, Zona.METROPOLITANA, 9000));
        sistema.cargarOrdenInicial(new MantenimientoAire("A002", "Jorge Paz", "Zona 4, Ciudad de Guatemala", 2.25, Zona.METROPOLITANA, 60000));
        sistema.registrarCuadrilla(new Cuadrilla("C001", 8, true));
        sistema.registrarCuadrilla(new Cuadrilla("C002", 6, false));
        sistema.registrarCuadrilla(new Cuadrilla("C003", 4, true));
    }
}
