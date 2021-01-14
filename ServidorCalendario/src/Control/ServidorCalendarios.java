/**
 * ServidorCalendarios.java
 *
 * @author ccatalan (02/2018)
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */

package Control;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static javax.ws.rs.client.Entity.json;

/**
 * Servidor de juegos online
 *
 */
public class ServidorCalendarios {

    private ServidorCalendarios servidorJuegos;
    public static String VERSION = "Calendar Server 1.0";
    private static String ARG_DEBUG = "-d";

    private static String FICHERO_CONFIG_WRONG
            = "Config file is wrong. Set default values";
    private static String WAITING_PLAYER_REQUEST
            = "Waiting for player requests...";
    private static String ERROR_SERVER_RUNNING = "Error: Server running in ";
    private static String ERROR_CREATING_PLAYER_CONNECTION
            = "Failed to create player connection";

    private static int TIEMPO_COMPROBACION_CONEXIONES = 10 * 1000;

    private static boolean modoDebug = false;

    private Map<String, ConexionPush> conexionesUsuariosOnline
            = new ConcurrentHashMap<>();

    /**
     * Configuración
     */
    private Properties propiedades;
    private static final String FICHERO_CONFIG = "config.properties";

    private static final String NUM_THREADS = "threadsNumber";
    private int numThreads = 16;
    private static final String PUERTO_SERVIDOR = "serverPort";
    private int puertoServidor = 15000;

    /**
     * Construye el servidor de juegos
     *
     */
    ServidorCalendarios() {
        servidorJuegos = this;
        leerConfiguracion();
        ejecutar();
    }

    /**
     * Devuelve verdadero si aplicación está en modo debug o falso en caso
     * contrario
     *
     */
    public static boolean esModoDebug() {
        return modoDebug;
    }

    /**
     * Lee configuración
     *
     */
    private void leerConfiguracion() {
        try {
            propiedades = new Properties();
            propiedades.load(new FileInputStream(FICHERO_CONFIG));

            numThreads = Integer.parseInt(propiedades.getProperty(NUM_THREADS));
            puertoServidor = Integer.parseInt(propiedades.getProperty(PUERTO_SERVIDOR));
        } catch (Exception e) {
            System.out.println(FICHERO_CONFIG_WRONG);
            System.out.println(NUM_THREADS + " = " + numThreads);
            System.out.println(PUERTO_SERVIDOR + " = " + puertoServidor);

            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ejecuta servidor juegos
     *
     */
    private void ejecutar() {
        System.out.println(VERSION);
        try {
            ExecutorService poolThreads = Executors.newFixedThreadPool(numThreads);
            comprobarConexionesAbiertas();

            ServerSocket ss = new ServerSocket(puertoServidor);
            while (true) {
                System.out.println(WAITING_PLAYER_REQUEST);
                Socket socket = ss.accept();
                poolThreads.execute(new ServidorCalendario(this, socket));
            }
        } catch (BindException e) {
            System.out.println(ERROR_SERVER_RUNNING + puertoServidor);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(ERROR_CREATING_PLAYER_CONNECTION);
            if (esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Nuevo jugador online
     *
     */
    public ConexionPush nuevaConexionPush(
            ConexionPush conexion) {
        return conexionesUsuariosOnline.put(conexion.devuelveUsuario(), conexion);
    }

    /**
     * Quita jugador online
     *
     */
    public void cerrarConexionusuarioOnline(String nombre) throws IOException {
        conexionesUsuariosOnline.get(nombre).cerrar();
        conexionesUsuariosOnline.remove(nombre);
    }

    /**
     * Devuelve jugador online
     *
     */
    public ConexionPush devuelveConexionUsuarioOnline(String nombre) {
        return conexionesUsuariosOnline.get(nombre);
    }

    /**
     * Devuelve nombres jugadores online
     *
     */
    public String[] devuelveNombreUsuariosOnline() {
        return conexionesUsuariosOnline.keySet().toArray(
                new String[conexionesUsuariosOnline.size()]);
    }

    /**
     * Comprueba conexiones abiertas
     *
     */
    private void comprobarConexionesAbiertas() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (ConexionPush conexion
                        : conexionesUsuariosOnline.values()) {
                    try {
                        PrimitivaComunicacion respuesta = conexion.enviarSolicitud(
                                PrimitivaComunicacion.TEST, TIEMPO_COMPROBACION_CONEXIONES);

                        if (respuesta == PrimitivaComunicacion.NOK) {
                            new Exception();
                        }
                    } catch (Exception e1) {
                        System.out.println(ServidorCalendario.ERROR_USER_CONNECTION);
                        conexionesUsuariosOnline.remove(conexion.devuelveUsuario());
                        try {
                            conexion.cerrar();
                        } catch (IOException e2) {
                            // No hacemos nada, ya hemos cerrado conexión 
                        }
                        if (ServidorCalendarios.esModoDebug()) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }, TIEMPO_COMPROBACION_CONEXIONES, TIEMPO_COMPROBACION_CONEXIONES);
    }

    /**
     * Procesa argumentos de main
     *
     */
    private static void procesarArgsMain(String[] args) {
        List<String> argumentos = new ArrayList<String>(Arrays.asList(args));

        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;
        }
    }

    /**
     * Método main
     *
     */
    public static void main(String args[]) {
        //procesarArgsMain(args);  

        new ServidorCalendarios();

    }
}
