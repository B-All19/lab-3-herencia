import java.util.Scanner;

public class Main {
    private Main() { }

    public static void main(String[] args) {
        TecniHogar sistema = new TecniHogar();
        DatosIniciales.cargar(sistema);
        System.out.println("TecniHogar: 8 órdenes iniciales, 3 cuadrillas e ingresos Q0.00.");
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            new MenuConsola(scanner, sistema).ejecutar();
        }
    }
}
