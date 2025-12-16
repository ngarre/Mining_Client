import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws IOException {
        // El cliente inicia estableciendo conexión con el servidor y completando proceso de autenticación (connect + ack)
        GestorConexiones gestorConexiones = new GestorConexiones();
        gestorConexiones.conectarAServidor();
        // Se enlazan gestor de conexiones y gestor de mineros para coordinar comunicación y cálculo
        GestorMineros gestorMineros = new GestorMineros(gestorConexiones);
        gestorConexiones.setGestorMineros(gestorMineros);
        // Hilo para envío periódico de tramas (punto opcional)
        // Temporizador para enviar tramas periódicamente
        Timer temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gestorConexiones.enviarTrama();
            }
        }, 0, 15 * 1000); // cada 15 segundos

        // El hilo principal queda escuchando al servidor
        gestorConexiones.escucharServidor(); // El hilo principal queda bloqueado aquí, escuchando mensajes del servidor
        // para garantizar una respuesta inmediata a dichos mensajes
    }
}



