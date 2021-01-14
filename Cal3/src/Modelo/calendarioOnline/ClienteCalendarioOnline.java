/**
 * ClienteCalendsarioOnline.java
 * @author ccatalan (02/2018)
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 *
 */

package Modelo.calendarioOnline;

import Modelo.calendarioOnline.PrimitivaComunicacion;
import Modelo.OyenteServidor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente para enviar solicitudes al servidor de partidas online
 *
 */
public class ClienteCalendarioOnline {
    // Tiempos en ms  

    public static int TIEMPO_ESPERA_LONG_POLLING = 0;         // infinito
    public static int TIEMPO_ESPERA_SERVIDOR = 2000;
    public static int TIEMPO_ESPERA_JUGADOR_ONLINE = 0;       // infinito
    public static int TIEMPO_REINTENTO_CONEXION_SERVIDOR = 10 * 1000;

    private String URLServidor;
    private int puertoServidor;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    /**
     * Construye cliente
     *
     */
    public ClienteCalendarioOnline(String URLServidor, int puertoServidor) {
        this.URLServidor = URLServidor;
        this.puertoServidor = puertoServidor;
    }

    /**
     * Envía solicitud al servidor
     *
     */
    private synchronized void enviar(
            PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros) throws Exception {
        socket = new Socket(URLServidor, puertoServidor);
        socket.setSoTimeout(tiempoEspera);

        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);

        salida.println(solicitud.toString());

        if (parametros != null) {
            salida.println(parametros);
        }
    }

    /**
     * Envía una solicitud al servidor
     *
     * Devuelve primitiva resultado y resultados
     *
     */
    public synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros, List<String> resultados)
            throws Exception {
        String resultado;

        enviar(solicitud, tiempoEspera, parametros);

        // Esperamos respuesta
        PrimitivaComunicacion respuesta = PrimitivaComunicacion.nueva(
                new Scanner(new StringReader(entrada.readLine())));

        // leemos, si los hay, resultados en líneas siguiente
        resultado = entrada.readLine();

        while ((resultado != null)
                && !resultado.equals(PrimitivaComunicacion.FIN.toString())) {

            resultados.add(resultado);
            resultado = entrada.readLine();
        }

        entrada.close();
        salida.close();
        socket.close();

        return respuesta;
    }

    /**
     * Envía una solicitud al servidor
     *
     * Devuelve primitiva resultado y resultados
     *
     */
    public synchronized void enviarSolicitudLongPolling(
            PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros, List<String> resultados,
            OyenteServidor oyenteServidor) throws Exception {
        String resultado;

        enviar(solicitud, tiempoEspera, parametros);

        do {
            // Esperamos solicitud servidor

            PrimitivaComunicacion respuesta = PrimitivaComunicacion.nueva(
                    new Scanner(new StringReader(entrada.readLine())));

            // Servidor está comprobando conexión
            if (respuesta == PrimitivaComunicacion.TEST) {
                entrada.readLine();
                salida.println(PrimitivaComunicacion.OK);
                salida.println(PrimitivaComunicacion.FIN);
                continue;
            }

            // leemos, si los hay, resultados en líneas siguiente
            resultados.clear();
            resultado = entrada.readLine();
            while ((resultado != null)
                    && !resultado.equals(PrimitivaComunicacion.FIN.toString())) {
                resultados.add(resultado);
                resultado = entrada.readLine();
            }

            oyenteServidor.solicitudServidorProducida(respuesta, resultados, salida);
            salida.println(PrimitivaComunicacion.FIN);
        } while (true);
    }

    /**
     * Envía una solicitud con parámetros al servidor
     *
     * Devuelve primitiva resultado y resultados
     *
     */
    public synchronized PrimitivaComunicacion enviarSolicitud(
            PrimitivaComunicacion solicitud,
            int tiempoEspera,
            String parametros) throws Exception {
        return enviarSolicitud(solicitud, tiempoEspera, parametros, null);
    }
}
