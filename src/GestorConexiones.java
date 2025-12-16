import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GestorConexiones {
    private BufferedReader in; // Recibe mensajes del servidor
    private PrintWriter out; // Envía mensajes al servidor
    private Socket cliente; // Representa la conexión con el servidor

    public String getIdCliente() {
        return idCliente;
    }

    private String idCliente; // Identificador asignado por el servidor tras el connect
    private GestorMineros gestorMineros; // Podremos iniciar o parar minado según mensajes del servidor

    public void setGestorMineros(GestorMineros gestorMineros) {
        this.gestorMineros = gestorMineros;
    }

    public void conectarAServidor() throws IOException {
        cliente = new Socket();
        cliente.connect(new InetSocketAddress("localhost", 3000));
        in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        out = new PrintWriter(cliente.getOutputStream(), true); // autoFlush permite a mensajes enviarse automáticamente

        out.println("connect"); // cliente envía mensaje de login al servidor
        String msg;
        msg = in.readLine(); // Espera respuesta del servidor

        if (msg.startsWith("ack")){
            idCliente = msg.split(":")[1];
            System.out.println("[Servidor]: ack.  Nuevo idCliente:" + idCliente);
        } else {
            System.out.println("Connection error");
        }
    }

    // Mantiene escucha activa del servidor para reaccionar a los mensajes recibidos
    public void escucharServidor() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("new_request:")) { // Servidor envía trabajo
                    out.println("ack"); // Cliente confirma recepción
                    parsearTrabajo(msg);
                } else if (msg.startsWith("end:")) { // Servidor indica que el trabajo ha terminado
                    System.out.println("[Servidor]: end");
                    parsearEnd(msg);
                } else {
                    System.out.println("[Servidor]: Mensaje desconocido: " + msg); // Control básico de errores
                }
            }
        } catch (IOException e) {
            System.out.println("[Cliente]: Error leyendo al servidor: " + e.getMessage());
        }
    }

    // new_request:idTrabajo:data:min:max:ceros
    // Extrae parámetros del trabajo de minado
    public void parsearTrabajo(String msg) {
        String[] partes = msg.split(":");
        int idTrabajo = Integer.parseInt(partes[1]);
        String data = partes[2];
        int min = Integer.parseInt(partes[3]);
        int max = Integer.parseInt(partes[4]);
        int ceros = Integer.parseInt(partes[5]);
        System.out.println("[Servidor]: new_request:" + idTrabajo + " trama: " + data + ", min: " + min + ", max: " + max + ", ceros: " + ceros);
        gestorMineros.distribuirTrabajo(data, min, max, idTrabajo, ceros);
    }
    // end:idTrabajo
    // Identifica trabajo que debe detenerse y llama al gestor de mineros para interrumpir hilos
    public void parsearEnd(String msg) {
        String [] parts = msg.split(":");
        int idTrabajo = Integer.parseInt(parts[1]);
        gestorMineros.pararMinado(idTrabajo);
    }

    public void enviarSolucion(int solucion, int idTrabajo) {
        out.println("sol:" + idTrabajo + ":" + solucion);
    }

    // Funcionalidad opcional que permite al cliente enviar operaciones para generar nuevas tramas
    public void enviarTrama() {
      String trama = GestorTramas.crearTrama();
        out.println("op:" + trama);
    }
}
