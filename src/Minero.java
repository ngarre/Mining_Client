// Clases necesarias para calcular el hash MD5 y representarlo en formato hexadecimal
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

// Clase que representa un minero que ejecuta el proceso de minado en un hilo independiente
public class Minero implements Runnable {

    private final String data;
    private final int min;
    private final int max;
    private final int ceros;
    private final int idTrabajo;
    private final GestorConexiones gestorConexiones; // Referencia al gestor de conexiones para ENVIAR LA SOLUCIÓN

    public Minero(String data, int min, int max, int ceros, int idTrabajo, GestorConexiones gestorConexiones) {
        this.data = data;
        this.min = min;
        this.max = max;
        this.ceros = ceros;
        this.idTrabajo = idTrabajo;
        this.gestorConexiones = gestorConexiones;
    }

    @Override
    public void run() {
        try {
            int solucion = minar();
            if (solucion != -1) {
                System.out.println("[Cliente]: Solución: " + solucion);
                gestorConexiones.enviarSolucion(solucion, idTrabajo);
            } else {
                System.out.println("[Cliente]: Minero no encontró solución en el rango para el trabajo ID: " + idTrabajo);
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error en el algoritmo MD5: " + e.getMessage());
        }
    }

    private int minar() throws NoSuchAlgorithmException {
        // Obtenemos el algoritmo de hashing que vamos a utilizar
        MessageDigest digest = MessageDigest.getInstance("md5");

        for (int i = min; i <= max; i++) {
            // Comprobamos si el hilo ha sido interrumpido (en GestorMineros y así no seguimos en el bucle innecesariamente)
            if (Thread.interrupted()) {
                // Finaliza el minado de forma controlada
                return -1;
            }
            // Concatena número de prueba dentro del rango (con 3 con tres dígitos) y los datos del bloque
            String msg = String.format("%03d%s", i, data);
            // Introduce el mensaje en el algoritmo de hash MD5
            digest.update(msg.getBytes());
            // Calcula el hash y lo convierte a formato hexadecimal
            String result = HexFormat.of().formatHex(digest.digest());
            // Comprobamos si el hash cumple la dificultad (número de ceros iniciales)
            if (result.startsWith("0".repeat(ceros))) {
                return i;
            }
        }
        // Si el minero no encuentra solución en el rango, devuelve -1
        return -1;
    }
}

