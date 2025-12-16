import java.util.Random;
// GestorTramas no implementa Runnable porque no representa una tarea concurrente, sino una utilidad.
public class GestorTramas {
    public static String crearTrama() {
        // mv|cantidad|user1|user2 ejemplo: mv|50|a1|b2

        Random aleatorio = new Random();
        int cantidad = aleatorio.nextInt(1, 101); // cantidad entre 1 y 100

        String bancoLetras = "abcdefghijklmnopqrstuvwxyz";
        int indiceAleatorio1 = aleatorio.nextInt(bancoLetras.length());
        int indiceAleatorio2 = aleatorio.nextInt(bancoLetras.length());
        char letra1 = bancoLetras.charAt(indiceAleatorio1);
        char letra2 = bancoLetras.charAt(indiceAleatorio2);

        int numeroUsuario1 = aleatorio.nextInt(1, 10);
        int numeroUsuario2 = aleatorio.nextInt(1, 10);
        String user1 = letra1 + Integer.toString(numeroUsuario1);
        String user2 = letra2 + Integer.toString(numeroUsuario2);

        return "mv|" + cantidad + "|" + user1 + "|" + user2;
    }
}
