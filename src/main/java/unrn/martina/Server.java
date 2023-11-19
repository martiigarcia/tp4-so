package unrn.martina;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


//Esclusa:
public class Server {

    private static final int PORT = 3457;
    private static final Queue<Socket> westQueue = new ConcurrentLinkedQueue<>();
    private static final Queue<Socket> eastQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        // Se crea el ServerSocket con el PORT indicado
        try (var server = new ServerSocket(PORT)) {
            System.out.println("Listo para recibir conexiones");
            // Bucle infinito para aceptar conexiones continuamente:
            while (true) {
                Socket socket = server.accept(); // Se acepta la conexión del cliente
                handleSeaLocks(socket); // Se manejan las esclusas para el barco (cliente) conectado
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleSeaLocks(Socket socket) {
        try {
            // Espero la solicitud de barco
            String line = "";
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            line = reader.readLine();
            System.out.println(line);
            // Divido el mensaje que llega desde el cliente para saber en que direccion esta y asi encolarlo donde corresponda
            String direction = line.split(":")[1].trim();
            //Depende de si es OESTE o ESTE se agrega el socket a la cola respectivamente
            if (direction.equals("OESTE")) {
                westQueue.add(socket);
                System.out.println("Un barco entró a la esclusa OESTE. Barcos en esclusa OESTE: " + westQueue.size());
            } else {
                eastQueue.add(socket);
                System.out.println("Un barco entró a la esclusa ESTE. Barcos en esclusa ESTE: " + eastQueue.size());
            }

            //Para verificar si la cola OESTE o ESTE tiene dos barcos esperando
            if (westQueue.size() == 2) {
                //Si hay dos barcos los desencola y envia mensaje de permiso para navegar
                System.out.println("------ Permitiendo el paso de barcos del OESTE... ------");
                Socket ship1 = westQueue.poll();
                Socket ship2 = westQueue.poll();
                reportPermissionToNavigate(ship1);
                reportPermissionToNavigate(ship2);
            } else if (eastQueue.size() == 2) {
                //Si hay dos barcos los desencola y envia mensaje de permiso para navegar
                System.out.println("------ Permitiendo el paso de barcos del ESTE... ------");
                Socket ship1 = eastQueue.poll();
                Socket ship2 = eastQueue.poll();
                reportPermissionToNavigate(ship1);
                reportPermissionToNavigate(ship2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Para mandarle mensaje al barco (Cliente) que se permitió la navegación
    private static void reportPermissionToNavigate(Socket shipSocket) throws IOException {
        OutputStream output = shipSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println("NAVEGACION_PERMITIDA");
    }
}
