package unrn.martina;

import java.io.*;
import java.net.Socket;

//Barcos:
public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 3457;
    private int id; //para tener una referencia de que barco es
    private String direction; //para saber en que direccion esta el barco

    public static void main(String[] args) {
        int shipsNumber = 8; // ocho barcos para probar 4 cruces
        for (int i = 0; i < shipsNumber; i++) {
            String direction;
            if (i % 2 == 1) { // para distribuir equitativamente las direcciones y que la ejecucion no pinche por diferente cantidad de barcos
                direction = "OESTE";
            } else {
                direction = "ESTE";
            }
            int id = i;
            //instancio el cliente (barco) como Threads y les hago start para que no tenga que hacer 8 ejecuciones por terminal
            new Thread(() -> new Client(id, direction).navigate()).start();
        }
    }

    private Client(int id, String direction) {
        //para crear el barco como el Cliente del Servidor
        this.id = id;
        this.direction = direction;
        System.out.println("DIRECCION DEL BARCO #" + id + ": " + direction);
    }

    private void navigate() {
        try {
            //Instancio el Socket en el HOST y PUERTO correspondientes al Servidor
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Barco #" + id + direction + ": Conectado al servidor de esclusas.");

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // Envio solicitud para cruzar la esclusa indicando la direccion del barco
            writer.println("SOLICITUD_CRUCE_DESDE_DIRECCION_" + id + ": " + direction);

            //El barco queda esperando el permiso para navegar
            System.out.println("Estado Barco #" + id + ": Aún no se permitió la navegación. Esperando otro barco...");

            // Espero la respuesta de la esclusa para navegar
            String line = "";
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            line = reader.readLine();

            //Si el paso la respuesta es igual a "NAVEGACION_PERMITIDA" entonces el Barco navega
            if (line.equals("NAVEGACION_PERMITIDA"))
                System.out.println("Estado Barco #" + id + ": Navegacion permitida. El barco navegó exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
