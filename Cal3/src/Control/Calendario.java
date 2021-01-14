/**
 * Calendario.java
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */
package Control;

import Modelo.calendarioOnline.PrimitivaComunicacion;
import Modelo.CalendarioModelo;
import Modelo.Contacto;
import Modelo.FactoriaModelo;
import Vista.CalendarioVista;
import Modelo.Recordatorio;
import Modelo.Tupla;
import Vista.DebugVista;
import Vista.FactoriaVista;
import com.google.gson.Gson;
import Modelo.calendarioOnline.ClienteCalendarioOnline;
import Vista.InicioSesionVista;
import Vista.Localizacion;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Calendario implements OyenteVista {

    /**
     * Configuración
     */
    private static String FICHERO_CONFIG_WRONG
            = "Config file is wrong. Set default values";
    private static String COMENTARIO_CONFIG
            = "country = ES|US, language = es|en";
    public static String VERSION = "2.0";

    private static final String REGEX_RECORDATORIOS = ".*\\.rec$";
    private static final String REGEX_CONTACTOS = ".*\\.cont$";

    private Properties configuracion;
    private static final String FICHERO_CONFIG = "config.properties";

    public static final String LENGUAJE = "language";
    private String lenguaje;
    public static final String PAIS = "country";
    public String pais;

    private CalendarioVista vista;
    private CalendarioModelo modelo;

    private Calendario calendario;

    private String ficheroRecordatorios = null;
    private String ficheroContactos = null;
    private boolean modoDebug = false;
    private Localizacion localizacion;

    private static String ARG_DEBUG = "-d";

    /**
     * Constructor del Calendario.
     *
     * @param args: Argumentos pasados por consola
     */
    public Calendario(String[] args) {
        calendario = this;

        procesarArgsMain(args);
        leerConfiguracion();
        localizacion = Localizacion.devolverInstancia(LENGUAJE, PAIS);

        modelo = FactoriaModelo.nuevoCalendario(this, localizacion);
        vista = FactoriaVista.calendarioVista(
                this, VERSION, modelo, lenguaje, pais);

        modelo.addObserver(vista);
        new InicioSesionVista(this, localizacion, InicioSesionVista.INICIO_SESION);

    }
    
    /**
     * Main
     * @param args 
     */
    
    public static void main(String[] args) {
        new Calendario(args);
    }

    /**
     * Inicio de sesion del usuario
     * 
     * @param credenciales 
     */
    private void iniciarSesion(Tupla<String, String> credenciales) {

        boolean seguir = true;
        while (seguir) {

            if (modelo.comprobarPass(credenciales.a, credenciales.b)) {

                seguir = false;
                modelo.ponerUsuario(credenciales.a);
                vista.mostrar(true);

            } else {
                InicioSesionVista inicioSesion
                        = new InicioSesionVista(this, localizacion,
                                InicioSesionVista.INICIO_SESION,
                                localizacion.devuelve(localizacion.FALLO_INICIO_SESION));

            }
        }

    }
    
    /**
     * Cierre de sesion del usuario
     */
    private void cerrarSesion(){
        modelo.quitarUsuario();
        vista.mostrar(false);
        new InicioSesionVista(this, localizacion, InicioSesionVista.INICIO_SESION);
    }

    
    /**
     * Registro del usuario
     * @param credenciales 
     */
    private void registrarUsuario(Tupla<String, String> credenciales) {
        boolean seguir = true;
        while (seguir) {

            if (modelo.nuevoUsuario(credenciales.a, credenciales.b)) {

                seguir = false;
                InicioSesionVista inicioSesion
                        = new InicioSesionVista(this, localizacion,
                                InicioSesionVista.INICIO_SESION,
                                localizacion.devuelve(localizacion.REGISTRO_EXITOSO));

            } else {
                InicioSesionVista registro
                        = new InicioSesionVista(this, localizacion,
                                InicioSesionVista.REGISTRO_USUARIO,
                                localizacion.devuelve(localizacion.FALLO_REGISTRO));

            }
        }
    }

    /**
     * Lee configuración
     *
     */
    private void leerConfiguracion() {
        // valores por defecto de localización;  
        lenguaje = Locale.getDefault().getLanguage();
        pais = Locale.getDefault().getCountry();

        try {
            configuracion = new Properties();
            configuracion.load(new FileInputStream(FICHERO_CONFIG));

            lenguaje = configuracion.getProperty(LENGUAJE);
            pais = configuracion.getProperty(PAIS);
            // si falta lenguaje o país ponemos valores por defecto
            if ((lenguaje == null) || (pais == null)) {
                lenguaje = Locale.getDefault().getLanguage();
                configuracion.setProperty(LENGUAJE, lenguaje);
                pais = Locale.getDefault().getCountry();
                configuracion.setProperty(PAIS, pais);
            } else {
                Locale.setDefault(new Locale(lenguaje, pais));
            }
        } catch (Exception e) {
            configuracion.setProperty(LENGUAJE, lenguaje);
            configuracion.setProperty(PAIS, pais);

            if (esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(FICHERO_CONFIG_WRONG, e);
            }
        }
    }

    /**
     * Añade un nuevo recordatorio en el dia indicado
     *
     * @param recordatorio a introducir
     */
    public void nuevo(Recordatorio recordatorio) {
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                if (!modelo.nuevo(recordatorio)) {
                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.RECORDATORIO_NO_GUARDADO));
                } else {
                    vista.habilitarEvento(Evento.GUARDAR_RECORDATORIOS, true);
                }
            }
        }.start();
    }

    /**
     * Edita el recordatorio seleccionado
     *
     * @param recordatorio con la información que sustituirá al antiguo
     */
    public void editar(Recordatorio recordatorio) {

        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                boolean resultado = modelo.editar(recordatorio);
                if (!resultado) {
                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.RECORDATORIO_NO_EDITADO));

                } else {
                    vista.habilitarEvento(Evento.GUARDAR_RECORDATORIOS, true);
                }
            }
        }.start();
    }

    /**
     * Elimina el recordatorio seleccionado
     *
     */
    public void eliminar() {
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                boolean resultado = modelo.eliminar(
                        vista.devuelveVistaRecordatorios().
                                devuelveRecordatorioSeleccionado());
                if (!resultado) {
                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.RECORDATORIO_NO_ELIMINADO));

                } else {
                    vista.habilitarEvento(Evento.GUARDAR_RECORDATORIOS, true);
                }
            }
        }.start();
    }

    /**
     * Introduce un nuevo contacto
     *
     * @param contacto
     */
    public void nuevo(Contacto contacto) {
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                if (!modelo.nuevo(contacto)) {

                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.CONTACTO_NO_GUARDADO));
                } else {

                    vista.habilitarEvento(Evento.GUARDAR_CONTACTOS, true);
                }
            }

        }.start();
    }

    /**
     * Edita el contacto seleccionado
     *
     * @param contacto
     */
    public void editar(Contacto contacto) {
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                boolean resultado = modelo.editar(contacto);
                if (!resultado) {
                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.CONTACTO_NO_EDITADO));

                } else {
                    vista.habilitarEvento(Evento.GUARDAR_CONTACTOS, true);
                }
            }
        }.start();
    }

    /**
     * Elimina el contacto seleccionado
     *
     * @param contacto
     */
    public void eliminar(Contacto contacto) {
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                boolean resultado = modelo.eliminar(contacto);
                if (!resultado) {
                    vista.mensajeDialogo(
                            localizacion.devuelve(
                                    localizacion.CONTACTO_NO_ELIMINADO));

                } else {
                    vista.habilitarEvento(Evento.GUARDAR_CONTACTOS, true);
                }
            }

        }.start();
    }

    /**
     * Cierra el programa dando opción a guardar el calendario
     */
    public void salir() {

        // guarda configuración
        try {
            FileOutputStream fichero = new FileOutputStream(FICHERO_CONFIG);
            configuracion.store(fichero, COMENTARIO_CONFIG);
            fichero.close();
        } catch (Exception e) {
            if (esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                        localizacion.CONFIGURACION_NO_GUARDADA, e);
            } else {
                vista.mensajeDialogo(
                        localizacion.devuelve(
                                localizacion.CONFIGURACION_NO_GUARDADA));
            }
        }
        System.exit(0);

    }

    /**
     * Cambia lenguaje
     *
     * @param tupla
     */
    private void cambiarLenguaje(Tupla tupla) {
        configuracion.setProperty(LENGUAJE, (String) tupla.a);
        configuracion.setProperty(PAIS, (String) tupla.b);
        salir();
    }

    /**
     * Indica si aplicación está en modo debug
     *
     */
    public boolean esModoDebug() {
        return modoDebug;
    }

    /**
     * Procesa argumentos de main. Si está ARG_DEBUG como argumento, pone el
     * calendario en modo debug. Si se pasan ficheros de contactos o
     * recordatorios, los carga en el calendario.
     *
     */
    private void procesarArgsMain(String[] args) {
        List<String> argumentos = new ArrayList<String>(Arrays.asList(args));

        if (argumentos.contains(ARG_DEBUG)) {
            modoDebug = true;
        }

    }

    /**
     * Recibe eventos de vista
     *
     * @param evento
     * @param obj
     */
    @Override
    public void eventoProducido(Evento evento, Object obj) {

        switch (evento) {

            case INICIAR_SESION:
                iniciarSesion((Tupla<String, String>) obj);
                break;

            case CERRAR_SESION:
                cerrarSesion();
                break;
                
            case REGISTRAR_USUARIO:
                registrarUsuario((Tupla<String, String>) obj);
                break;

            case NUEVO_RECORDATORIO:
                nuevo((Recordatorio) obj);
                break;

            case EDITAR_RECORDATORIO:
                Calendario.this.editar((Recordatorio) obj);
                break;

            case ELIMINAR_RECORDATORIO:
                eliminar();
                break;

            case NUEVO_CONTACTO:
                nuevo((Contacto) obj);
                break;

            case EDITAR_CONTACTO:
                editar((Contacto) obj);
                break;

            case ELIMINAR_CONTACTO:
                Calendario.this.eliminar((Contacto) obj);
                break;

            case SALIR:
                salir();
                break;

            case CAMBIAR_LENGUAJE:
                cambiarLenguaje((Tupla) obj);
                break;

        }
    }

}
