/**
 * ServidorCalendario.java
 * @author ccatalan (02/2018)
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */

package Control;

import Modelo.BaseDeDatos;
import Modelo.Contacto;
import Modelo.Recordatorio;
import Modelo.Tupla;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servidor de juego
 *
 */
public class ServidorCalendario implements Runnable {

    public static String ERROR_USER_CONNECTION = "Closed user connection";
    private static String FORMATO_FECHA_CONEXION = "kk:mm:ss EEE d MMM yy";

    private static final int RECORDATORIOS_EN_MES = 1;
    private static final int RECORDATORIOS_EN_DIA = 2;

    private ServidorCalendarios servidorCalendario;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    /**
     * Construye servidor de juego
     *
     */
    public ServidorCalendario(ServidorCalendarios servidorJuegos, Socket socket)
            throws IOException {
        this.servidorCalendario = servidorJuegos;
        this.socket = socket;

        entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Autoflush!!
        salida = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
    }

    /**
     * Atiende solicitudes jugadores
     *
     */
    @Override
    public void run() {
        try {
            PrimitivaComunicacion solicitud = PrimitivaComunicacion.nueva(
                    new Scanner(new StringReader(entrada.readLine())));
            

            switch (solicitud) {
                case COMPROBAR_PASS:
                    comprobarPass();
                    break;

                case CREAR_USUARIO:
                    crearUsuario();
                    break;

                case CONECTAR:
                    conectarUsuario();
                    break;

                case DESCONECTAR:
                    desconectarUsuario();
                    break;

                case PEDIR_RECORDATORIOS_EN_DIA:
                    listarRecordatorios(RECORDATORIOS_EN_DIA);
                    break;
                case PEDIR_RECORDATORIOS_EN_MES:

                    listarRecordatorios(RECORDATORIOS_EN_MES);
                    break;
                case NUEVO_RECORDATORIO:
                    nuevoRecordatorio();

                    break;

                case EDITAR_RECORDATORIO:
                    editarRecordatorio();
                    break;

                case ELIMINAR_RECORDATORIO:
                    eliminarRecordatorio();
                    break;
                case PEDIR_CONTACTOS:
                    listarContactos();
                    break;

                case NUEVO_CONTACTO:
                    nuevoContacto();
                    break;

                case EDITAR_CONTACTO:
                    editarContacto();
                    break;

                case ELIMINAR_CONTACTO:
                    eliminarContacto();
                    break;
            }
        } catch (InputMismatchException e1) {
            if (ServidorCalendarios.esModoDebug()) {
                System.out.println("Request wrong from " + socket.getInetAddress()
                        + " at " + devuelveFechaHoy());
            }
        } catch (Exception e2) {
            System.out.println(ERROR_USER_CONNECTION + ": " + e2.toString());
            if (ServidorCalendarios.esModoDebug()) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Comprueba si la contraseña es correcta para el inicio de sesion
     */
    private void comprobarPass() {
        try {
            String usuario = entrada.readLine();
            String pass = entrada.readLine();

            String passReal
                    = BaseDeDatos.devolverInstancia().devuelvePass(usuario);

            if (passReal != null && passReal.equals(pass)) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Crea un nuevo usuario
     */
    private void crearUsuario() {
        try {
            String usuario = entrada.readLine();
            String pass = entrada.readLine();

            boolean resultado
                    = BaseDeDatos.devolverInstancia().introducir(usuario, pass);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lista los recordatorios segun la opción (en mes o en día)
     *
     * @param opcion
     */
    private void listarRecordatorios(int opcion) {
        try {

            String usuario = entrada.readLine();

            GregorianCalendar dia = new Gson().fromJson(
                    entrada.readLine(), GregorianCalendar.class);

            String tema = entrada.readLine();

            List<Recordatorio> recordatorios;

            if (opcion == RECORDATORIOS_EN_DIA) {

                recordatorios = BaseDeDatos.devolverInstancia().
                        devuelveRecordatoriosEnDia(usuario, dia, tema);
            } else {

                recordatorios = BaseDeDatos.devolverInstancia().
                        devuelveRecordatoriosEnMes(usuario, dia, tema);
            }

            if (recordatorios != null) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(new Gson().toJson(recordatorios));
            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lista los contactos
     */
    private void listarContactos() {
        Gson gson = new GsonBuilder().create();

        try {
            String usuario = entrada.readLine();
            List<Contacto> contactos
                    = BaseDeDatos.devolverInstancia().devuelveContactos(usuario);

            if (contactos != null) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(gson.toJson(contactos));
            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Crea un nuevo recordatorio
     */
    private void nuevoRecordatorio() {
        Gson gson = new Gson();
        boolean resultado = false;
        try {
            String usuario = entrada.readLine();
            String jsonRecordatorio = entrada.readLine();
            Recordatorio recordatorio
                    = gson.fromJson(jsonRecordatorio, Recordatorio.class);
            resultado
                    = BaseDeDatos.devolverInstancia().introducir(recordatorio);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            for (Tupla<Contacto, String> tupla
                    : recordatorio.devuelveSincronizados()) {
                ConexionPush conexionUsuarioSolicitado
                        = servidorCalendario.devuelveConexionUsuarioOnline(
                                ((Contacto) tupla.a).devuelveId());

                if (conexionUsuarioSolicitado != null) {
                    conexionUsuarioSolicitado.enviarSolicitud(
                            PrimitivaComunicacion.NUEVO_RECORDATORIO,
                            jsonRecordatorio);
                }
            }
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Edita el recordatorio
     */
    private void editarRecordatorio() {
        Gson gson = new Gson();
        boolean resultado = false;
        try {
            String usuario = entrada.readLine();
            Recordatorio recordatorio
                    = gson.fromJson(entrada.readLine(), Recordatorio.class);
            resultado
                    = BaseDeDatos.devolverInstancia().editar(recordatorio);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Elimina el recordatorio
     */
    private void eliminarRecordatorio() {
        Gson gson = new Gson();
        boolean resultado = false;
        try {
            String usuario = entrada.readLine();
            Recordatorio recordatorio
                    = gson.fromJson(entrada.readLine(), Recordatorio.class);
            resultado
                    = BaseDeDatos.devolverInstancia().
                            eliminar(usuario, recordatorio);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Crea un nuevo Contacto
     */
    private void nuevoContacto() {
        Gson gson = new Gson();
        boolean resultado = false;
        try {
            String usuario = entrada.readLine();
            Contacto contacto = gson.fromJson(entrada.readLine(), Contacto.class);
            resultado = BaseDeDatos.devolverInstancia().introducir(usuario, contacto);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Edita el contacto
     */
    private void editarContacto() {
        Gson gson = new Gson();
        boolean resultado;
        try {
            String usuario = entrada.readLine();
            Contacto contacto = gson.fromJson(entrada.readLine(), Contacto.class);
            resultado = BaseDeDatos.devolverInstancia().editar(usuario, contacto);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Elimina el contacto
     */
    private void eliminarContacto() {
        Gson gson = new Gson();
        boolean resultado = false;
        try {
            String usuario = entrada.readLine();
            Contacto contacto = gson.fromJson(entrada.readLine(), Contacto.class);
            resultado = BaseDeDatos.devolverInstancia().eliminar(usuario, contacto);

            if (resultado) {
                salida.println(PrimitivaComunicacion.OK);
            } else {
                salida.println(PrimitivaComunicacion.NOK);
            }

            salida.println(PrimitivaComunicacion.FIN);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCalendario.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Devuelve fecha de hoy
     *
     */
    private String devuelveFechaHoy() {
        return new SimpleDateFormat(FORMATO_FECHA_CONEXION,
                Locale.getDefault()).format(new Date());
    }

    /**
     * Atiende solicitud nuevo usuario conectado
     *
     */
    private void conectarUsuario() throws IOException, InterruptedException {
        try {
            String solicitante = entrada.readLine();

            if (ServidorCalendarios.esModoDebug()) {
                System.out.println("Request: "
                        + PrimitivaComunicacion.CONECTAR.toString() + " "
                        + solicitante + " at " + devuelveFechaHoy());
            }

            // creamos conexión push por long polling    
            CountDownLatch cierreConexion = new CountDownLatch(1);

            servidorCalendario.nuevaConexionPush(
                    new ConexionPush(solicitante, socket, cierreConexion));

            // y esperamos hasta cierre conexión 
            cierreConexion.await();
        } catch (Exception e) {
            if (ServidorCalendarios.esModoDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Atiende solicitud usuario desconectado
     *
     */
    private void desconectarUsuario() throws IOException {
        String solicitante = entrada.readLine();

        if (ServidorCalendarios.esModoDebug()) {
            System.out.println("Request: "
                    + PrimitivaComunicacion.DESCONECTAR.toString() + " "
                    + solicitante + " at " + devuelveFechaHoy());
        }

        servidorCalendario.cerrarConexionusuarioOnline(solicitante);

        entrada.close();
        salida.close();
        socket.close();
    }
}
