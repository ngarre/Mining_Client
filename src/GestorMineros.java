import java.util.HashMap;

public class GestorMineros {
    // Se pasa a cada minero para que pueda enviar la solución al servidor
    private final GestorConexiones gestorConexiones;

    public GestorMineros(GestorConexiones gestorConexiones) {
        this.gestorConexiones = gestorConexiones;
    }

    // Clave: idTrabajo, Valor: array de hilos que están minando ese trabajo
    private final HashMap<Integer, Thread[]> minerosActivos = new HashMap<>();

    // Divide trabajo de minado entre 4 mineros (hilos) para ejecutarlos de forma concurrente
    public void distribuirTrabajo(String data, int min, int max, int idTrabajo, int ceros) { // data es grupoTramas
        // Número fijo de mineros (hilos) a crear
        int numeroMineros = 4;
        // Guardamos hilos para poder interrumpirlos después
        Thread[] hilos = new Thread[numeroMineros];
        // Dividimos equitativamente espacio de búsqueda
        int rango = (max - min + 1) / numeroMineros;
       // Calculamos rango para cada minero
        for (int i = 0; i < numeroMineros; i++) {
            int minRango = min + i * rango;
            int maxRango = (i == numeroMineros - 1)
                    ? max                  // el último minero llega hasta el final
                    : minRango + rango - 1;
            // Iniciamos el minero (hilo) con su rango correspondiente
            hilos[i] = iniciarMinado(data, minRango, maxRango, idTrabajo, ceros);
        }
        // Guardamos los hilos activos para este trabajo
        minerosActivos.put(idTrabajo, hilos);
    }

    // Crea un minero, lo ejecuta en un hilo independiente y devuelve la referencia al hilo
    private Thread iniciarMinado(String data, int minRango, int maxRango, int idTrabajo, int ceros) {
        // Se crea el hilo y se lanza con start()
        Minero minero = new Minero(data, minRango, maxRango, ceros, idTrabajo, gestorConexiones);
        Thread hiloMinero = new Thread(minero);
        hiloMinero.start();
        // Devolvemos el hilo para guardarlo en la lista de minerosActivos
        return hiloMinero;
    }

    // Interrumpe hilos de minado asociados a un trabajo específico cuando se encuentra la solución
    public void pararMinado(int idTrabajo) { // Identificador del trabajo que viene desde servidor
        Thread[] hilos = minerosActivos.get(idTrabajo); // Obtener los hilos asociados al trabajo
        if (hilos != null) { // Comprobar si existen hilos para ese trabajo
            for (Thread hilo : hilos) {
                hilo.interrupt(); // Interrumpimos el hilo
                System.out.println("[Cliente " + gestorConexiones.getIdCliente() + "] Minero interrumpido para el trabajo ID: " + idTrabajo);
            }
            minerosActivos.remove(idTrabajo); // Eliminamos el trabajo de la lista de activos
        }

    }
}

