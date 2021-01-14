/**
 * CalendarioModelo.java
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 *
 */
package Modelo;

import Control.Calendario;
import Modelo.calendarioOnline.PrimitivaComunicacion;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.List;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Modelo.calendarioOnline.ClienteCalendarioOnline;
import Vista.CalendarioVista;
import Vista.DebugVista;
import Vista.Localizacion;
import static java.lang.Thread.sleep;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class CalendarioModelo extends Observable implements OyenteServidor {

    public static final String URL_SERVIDOR = "URLServer";
    private String URLServidor = "localhost";
    public static final String PUERTO_SERVIDOR = "portServer";
    private int puertoServidor = 15000;
    private Calendario calendario;
    private ClienteCalendarioOnline clienteCalendarioOnline;
    private String usuario = "";
    private CalendarioModelo calendarioModelo;
    private Localizacion localizacion;

    /**
     * Construye un nuevo calendario, su capacidad ser� la indicada por
     * MAX_CONTACTOS y MAX_RECORDATORIOS
     *
     */
    public CalendarioModelo(Calendario calendario, Localizacion localizacion) {

        calendarioModelo = this;
        this.calendario = calendario;
        this.localizacion = localizacion;
        clienteCalendarioOnline = new ClienteCalendarioOnline(URLServidor, puertoServidor);
        conexionLongPollingServidorCalendarioOnline();
    }

    /**
     * Pone el identificador del usuario en el calendario
     *
     * @param usuario
     */
    public void ponerUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Quita el identificador del usuario del calendario
     */
    public void quitarUsuario() {
        this.usuario = "";
    }

    /**
     * Recibe respuestas de servidor mediante long polling
     *
     */
    private void conexionLongPollingServidorCalendarioOnline() {

        new Thread() {
            @Override
            public void run() {
                ClienteCalendarioOnline clienteCalendarioOnline
                        = new ClienteCalendarioOnline(URLServidor, puertoServidor);

                while (true) {
                    try {//                       

                        clienteCalendarioOnline.enviarSolicitudLongPolling(
                                PrimitivaComunicacion.CONECTAR,
                                ClienteCalendarioOnline.TIEMPO_ESPERA_LONG_POLLING,
                                usuario, new ArrayList<String>(), calendarioModelo);

                    } catch (Exception e1) {

                        if (calendario.esModoDebug()) {
                            DebugVista.devolverInstancia().mostrar(
                                    localizacion.devuelve(
                                            Localizacion.ERROR_CONEXION_SERVIDOR_CALENDARIO), e1);
                        }
                        try {
                            sleep(ClienteCalendarioOnline.TIEMPO_REINTENTO_CONEXION_SERVIDOR);
                        } catch (InterruptedException e2) {
                            // Propagamos a la máquina virtual  
                            new RuntimeException();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Recibe solicitudes servidor partidas online
     *
     */
    @Override
    public void solicitudServidorProducida(
            PrimitivaComunicacion solicitud,
            List<String> parametros, PrintWriter salida) throws Exception {
        switch (solicitud) {
            case NUEVO_RECORDATORIO:
                nuevoRecordatorioSincronizado(parametros.get(0));
                break;
        }
    }

    /**
     * Notifica que hay un nuevo recordatorio
     *
     * @param parametro
     */
    public void nuevoRecordatorioSincronizado(String parametro) {
        Recordatorio recordatorio
                = new Gson().fromJson(parametro, Recordatorio.class);

        JOptionPane.showMessageDialog(CalendarioVista.
                devolverInstancia(calendario, "", this, "", ""),
                localizacion.devuelve(
                        localizacion.NUEVO_RECORDATORIO_SINCRONIZADO));
    }

    /**
     * Comprueba la contraseña
     *
     * @param usuario
     * @param pass
     * @return
     */
    public boolean comprobarPass(String usuario, String pass) {
        boolean resultado = false;
        try {
            Password hash = new Password(pass);
            resultado = realizarOperacion(
                    PrimitivaComunicacion.COMPROBAR_PASS,
                    usuario, hash.toHexStringHash());
        } catch (NoSuchAlgorithmException ex) {
            if (calendario.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                        CalendarioModelo.class.getName(), ex);
            }

        }
        return resultado;
    }

    /**
     * Crea un nuevo usuario
     *
     * @param usuario
     * @param pass
     * @return
     */
    public boolean nuevoUsuario(String usuario, String pass) {
        boolean resultado = false;
        try {
            Password hash = new Password(pass);
            resultado = realizarOperacion(
                    PrimitivaComunicacion.CREAR_USUARIO,
                    usuario, hash.toHexStringHash());

        } catch (NoSuchAlgorithmException ex) {
            if (calendario.esModoDebug()) {
                DebugVista.devolverInstancia().mostrar(
                        CalendarioModelo.class.getName(), ex);
            }

        }
        return resultado;
    }

    /**
     * Crea un nuevo contacto
     *
     * @param contacto
     * @return
     */
    public boolean nuevo(Contacto contacto) {

        return realizarOperacion(PrimitivaComunicacion.NUEVO_CONTACTO, contacto);
    }

    /**
     * Comunica la operacion a realizar con el servidor
     * @param operacion
     * @param usuario
     * @param pass
     * @return 
     */
    private boolean realizarOperacion(PrimitivaComunicacion operacion,
            String usuario, String pass) {
        boolean resultado = false;
        List<String> resultados = new ArrayList<>();
        try {

            String parametros = usuario + "\n" + pass;
            PrimitivaComunicacion respuesta = clienteCalendarioOnline.enviarSolicitud(
                    operacion,
                    ClienteCalendarioOnline.TIEMPO_ESPERA_JUGADOR_ONLINE,
                    parametros, resultados);

            if (respuesta == PrimitivaComunicacion.OK) {
                resultado = true;

            }
        } catch (Exception e) {

            if (calendario.esModoDebug()) {

                DebugVista.devolverInstancia().mostrar(
                        CalendarioModelo.class.getName(), e);
            }
        }
        return resultado;
    }

    
     /**
     * Comunica la operacion a realizar con el servidor
     * @param operacion
     * @param usuario
     * @param pass
     * @return 
     */
    private boolean realizarOperacion(
            PrimitivaComunicacion operacion, Identificable elemento) {
        List<String> resultados = new ArrayList<>();
        boolean resultado = false;
        try {

            String parametros = usuario + "\n" + elemento.devuelveJson();
            PrimitivaComunicacion respuesta = clienteCalendarioOnline.enviarSolicitud(
                    operacion,
                    ClienteCalendarioOnline.TIEMPO_ESPERA_JUGADOR_ONLINE,
                    parametros, resultados);

            if (respuesta == PrimitivaComunicacion.OK) {
                resultado = true;

            }
         } catch (Exception e) {

            if (calendario.esModoDebug()) {

                DebugVista.devolverInstancia().mostrar(
                        CalendarioModelo.class.getName(), e);
            }
        }
        setChanged();
        notifyObservers(elemento);
        return resultado;
    }

    /**
     * Crea un nuevo recordatorio
     *
     * @param recordatorio: Recordatorio a introducir
     * @return Resultado de la operaci�n
     */
    public boolean nuevo(Recordatorio recordatorio) {
        return realizarOperacion(PrimitivaComunicacion.NUEVO_RECORDATORIO, recordatorio);
    }

    /**
     * Edita el Contacto indicado.
     *
     * @param contacto: Contacto a editar
     * @return Resultado de la operaci�n
     */
    public boolean editar(Contacto contacto) {


        return realizarOperacion(PrimitivaComunicacion.EDITAR_CONTACTO, contacto);
    }

    /**
     * Edita el recordatorio indicado.
     *
     * @param recordatorio: Recordatorio a editar
     * @return Resultado de la operaci�n
     */
    public boolean editar(Recordatorio recordatorio) {
        return realizarOperacion(PrimitivaComunicacion.EDITAR_RECORDATORIO, recordatorio);
    }

    /**
     * Elimina el contacto indicado
     *
     * @param contacto: Contacto que se quiere eliminar
     * @return Resultado de la operaci�n
     */
    public boolean eliminar(Contacto contacto) {
        return realizarOperacion(PrimitivaComunicacion.ELIMINAR_CONTACTO, contacto);
    }

    /**
     * Elimina el recordatorio indicado
     *
     * @param recordatorio: Recordatorio que se quiere eliminar
     * @return Resultado de la operaci�n
     */
    public boolean eliminar(Recordatorio recordatorio) {
        return realizarOperacion(
                PrimitivaComunicacion.ELIMINAR_RECORDATORIO, recordatorio);
    }

    /**
     * Devuelve una lista con los recordatorios que hay en el día seleccionado
     *
     * @param dia seleccionado
     * @return Lista de recordatorios
     */
    public List<Recordatorio> devuelveRecordatoriosEnDia(
            GregorianCalendar dia, String filtro) {

        List<String> resultados = new ArrayList<String>();
        Type tipo = new TypeToken<ArrayList<Recordatorio>>() {
        }.getType();

        List<String> parametros = new ArrayList<String>();
        parametros.add(new Gson().toJson(dia));
        parametros.add(filtro);
        devuelveJSONElementos(
                PrimitivaComunicacion.PEDIR_RECORDATORIOS_EN_DIA,
                parametros, resultados);

        return new Gson().fromJson(resultados.get(0), tipo);
    }

    /**
     * Devuelve una lista con los recordatorios que hay en el mes pasado por
     * parametro
     *
     * @param dia del que se obtiene el mes
     * @return Lista de recordatorios
     */
    public List<Recordatorio> devuelveRecordatoriosEnMes(
            GregorianCalendar dia, String filtro) {

        List<String> resultados = new ArrayList<String>();
        Type tipo = new TypeToken<ArrayList<Recordatorio>>() {
        }.getType();

        List<String> parametros = new ArrayList<String>();
        parametros.add(new Gson().toJson(dia));
        parametros.add(filtro);

        devuelveJSONElementos(
                PrimitivaComunicacion.PEDIR_RECORDATORIOS_EN_MES,
                parametros, resultados);

        return new Gson().fromJson(resultados.get(0), tipo);
    }

    public String devuelveJSONElementos(
            PrimitivaComunicacion operacion, List<String> listaParametros,
            List<String> resultados) {

        Gson gson = new GsonBuilder().create();

        boolean resultado = false;
        List<Identificable> listaElementos = new ArrayList<Identificable>();
        String json = null;

        try {

            String parametros = usuario;

            if (listaParametros != null) {
                for (String parametro : listaParametros) {
                    parametros = parametros + "\n" + parametro;
                }
            }

            PrimitivaComunicacion respuesta = clienteCalendarioOnline.
                    enviarSolicitud(operacion,
                            ClienteCalendarioOnline.TIEMPO_ESPERA_JUGADOR_ONLINE,
                            parametros, resultados);

            if (respuesta == PrimitivaComunicacion.OK) {

                json = (String) resultados.get(0);

            }
         } catch (Exception e) {

            if (calendario.esModoDebug()) {

                DebugVista.devolverInstancia().mostrar(
                        CalendarioModelo.class.getName(), e);
            }
        }
        return json;
    }

    /**
     * Devuelve la colección de contactos
     *
     * @return contactos
     */
    public Map<String, Contacto> devuelveContactos() {

        List<Contacto> listaContacto;
        List<String> resultados = new ArrayList<String>();
        Type tipo = new TypeToken<ArrayList<Contacto>>() {
        }.getType();

        Map<String, Contacto> contactos = new TreeMap<String, Contacto>();
        devuelveJSONElementos(PrimitivaComunicacion.PEDIR_CONTACTOS,
                null, resultados);

        listaContacto = new Gson().fromJson(resultados.get(0), tipo);

        for (Contacto contacto : listaContacto) {
            contactos.put(contacto.devuelveId(), contacto);
        };
        return contactos;

    }

    /** 
     * Devuelve el usuario del calendario
     * @return 
     */
    public String devuelveUsuario() {
        return usuario;
    }
}
