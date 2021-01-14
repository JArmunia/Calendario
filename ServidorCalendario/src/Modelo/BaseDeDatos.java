/**
 * BaseDeDatos.java 
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */
package Modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BaseDeDatos {

    private static final String URL = "jdbc:mysql://web-ter.unizar.es:3306/u622877";
    private static final String USER = "u622877";
    private static final String PASSWORD = "u622877";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final int FALLO = -1;
    private static final SimpleDateFormat SDF
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TODO = "TODO";

    private static BaseDeDatos instancia = null;
    private Connection conexion;

    /**
     * Crea la conexión a la base de datos
     */
    private BaseDeDatos() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {

            Logger.getLogger(BaseDeDatos.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        try {
            conexion = DriverManager.getConnection(
                    URL, USER, PASSWORD);
            System.out.println("Conectado");

        } catch (SQLException ex) {

            Logger.getLogger(BaseDeDatos.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.out.println("Fallo al conectar");

        }
    }

    /**
     * Devuelve la instancia de la BD (Es singleton)
     *
     * @return
     */
    public static synchronized BaseDeDatos devolverInstancia() {
        if (instancia == null) {
            instancia = new BaseDeDatos();
        }
        return instancia;
    }

    /**
     * Devuelve un String formateado de forma que se pueda introducir a la BD
     *
     * @param fecha
     * @return
     */
    public String devuelveFechaFormateada(GregorianCalendar fecha) {

        return SDF.format(fecha.getTime());

    }

    /**
     * Devuelve una fecha a partir de un string
     *
     * @param fecha
     * @return
     */
    public GregorianCalendar parseaFechaFormateada(String fecha) {
        GregorianCalendar fechaParseada = new GregorianCalendar();
        try {
            fechaParseada.setTime(SDF.parse(fecha));
        } catch (ParseException ex) {
            //TODO gestionar excepción
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechaParseada;
    }

    /**
     * Introduce una tupla en la tabla "Usuarios". Se utiliza para introducir un
     * nuevo usuario.
     *
     * @param usuario
     * @param hashPassword
     * @return resultado de la operación
     */
    public synchronized boolean introducir(String usuario, String hashPassword) {
        String[] valores = new String[2];

        valores[0] = usuario;
        valores[1] = hashPassword;

        return introducir("Usuarios", valores);

    }

    /**
     * Introduce una tupla en la tabla "Sincroniza". Se utiliza para sincronizar
     * recordatorios con contactos
     *
     * @param idASincronizar
     * @param recordatorio
     * @return resultado de la operación
     */
    public synchronized boolean introducir(String idASincronizar,
            Recordatorio recordatorio, String permisos) {
        String[] valores = new String[4];

        valores[0] = idASincronizar;
        valores[1] = recordatorio.devuelveCreador();
        valores[2] = devuelveFechaFormateada(recordatorio.devuelveFecha());
        valores[3] = permisos;
        return introducir("Sincroniza", valores);

    }

    /**
     * Introduce una tupla en la tabla "Contactos". Se utiliza para introducir
     * un nuevo contacto.
     *
     * @param usuario
     * @param contacto
     * @return resultado de la operación
     */
    public synchronized boolean introducir(String usuario, Contacto contacto) {
        String[] valores = new String[7];
        valores[0] = usuario;
        valores[1] = contacto.devuelveId();
        valores[2] = contacto.devuelveNombre();
        valores[3] = contacto.devuelveApellido1();
        valores[4] = contacto.devuelveApellido2();
        valores[5] = contacto.devuelveTfno() + "";
        valores[6] = contacto.devuelveCorreo();

        return introducir("Contactos", valores);

    }

    /**
     * Introduce una tupla en la tabla "Recordatorios". Se utiliza para
     * introducir un nuevo recordatorio.
     *
     * @param recordatorio
     * @return
     */
    public synchronized boolean introducir(Recordatorio recordatorio) {
        String[] valores = new String[4];
        String[] valores2 = new String[4];
        boolean resultado;

        valores[0] = recordatorio.devuelveCreador();
        valores[1] = devuelveFechaFormateada(recordatorio.devuelveFecha());
        valores[2] = recordatorio.devuelveTema();
        valores[3] = recordatorio.devuelveTexto();

        resultado = introducir("Recordatorios", valores);

        valores2[0] = recordatorio.devuelveCreador();
        valores2[1] = recordatorio.devuelveCreador();
        valores2[2] = devuelveFechaFormateada(recordatorio.devuelveFecha());
        valores2[3] = Recordatorio.LECTURA_ESCRITURA;

        /**
         * Se sincroniza el recordatorio con el creador
         */
        if (resultado) {
            introducir("Sincroniza", valores2);
        }

        for (Tupla<Contacto, String> tupla : recordatorio.devuelveSincronizados()) {

            introducir(tupla.a.devuelveId(), recordatorio, tupla.b);
        }
        return resultado;

    }

    public synchronized boolean introducir(String tabla, String[] valores) {

        String sentenciaAEjecutar = "INSERT INTO " + tabla + " VALUES(";

        for (int i = 0; i < valores.length; i++) {
            sentenciaAEjecutar = sentenciaAEjecutar + "'" + valores[i] + "'";
            if (i + 1 < valores.length) {
                sentenciaAEjecutar = sentenciaAEjecutar + ",";
            } else {
                sentenciaAEjecutar = sentenciaAEjecutar + ")";
            }
        }

        try {
            conexion.createStatement().executeUpdate(sentenciaAEjecutar);

        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    /**
     * Devuelve los recordatorios en el intervalo seleccionado
     *
     * @param usuario
     * @param limiteInferior
     * @param limiteSuperior
     * @param filtro
     * @return
     */
    public synchronized List<Recordatorio> devuelveRecordatoriosEnIntervalo(
            String usuario, GregorianCalendar limiteInferior,
            GregorianCalendar limiteSuperior, String filtro) {
        List<Recordatorio> recordatorios = new ArrayList<Recordatorio>();
        String sentenciaAEjecutar
                = "SELECT  r.id_creador, r.id_recordatorio, r.tema, "
                + "r.texto, s.permisos "
                + "FROM Recordatorios r JOIN Sincroniza s "
                + "ON r.id_creador = s.id_creador "
                + "AND s.id_recordatorio = r.id_recordatorio "
                + "WHERE s.id_contacto ='" + usuario + "' "
                + "AND r.id_recordatorio BETWEEN '"
                + devuelveFechaFormateada(limiteInferior)
                + "' AND '" + devuelveFechaFormateada(limiteSuperior) + "'";

        if (!filtro.equals(TODO)) {
            sentenciaAEjecutar = sentenciaAEjecutar + " AND r.tema = "
                    + "'" + filtro + "'";
        }

        try {
            ResultSet rs = conexion.createStatement().executeQuery(sentenciaAEjecutar);

            ResultSetMetaData rsmd = rs.getMetaData();
            recordatorios = creaListaRecordatorios(rs);
            
            return recordatorios;
        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Devuelve los recordatorios en el mes seleccionado
     * @param usuario
     * @param dia
     * @param filtro
     * @return 
     */
    public synchronized List<Recordatorio> devuelveRecordatoriosEnMes(
            String usuario, GregorianCalendar dia, String filtro) {

        int anyo = dia.get(Calendar.YEAR);
        int mes = dia.get(Calendar.MONTH);

        GregorianCalendar limiteInferior
                = new GregorianCalendar(anyo, mes, 1, 0, 0);

        GregorianCalendar limiteSuperior = new GregorianCalendar(anyo, mes,
                limiteInferior.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59);

        return devuelveRecordatoriosEnIntervalo(
                usuario, limiteInferior, limiteSuperior, filtro);
    }

    /**
     * Devuelve los recordatorios en el dia seleccionado
     * @param usuario
     * @param dia
     * @param filtro
     * @return 
     */
    public synchronized List<Recordatorio> devuelveRecordatoriosEnDia(
            String usuario, GregorianCalendar dia, String filtro) {

        List<Recordatorio> recordatorios = new ArrayList<Recordatorio>();

        GregorianCalendar limiteInferior
                = (GregorianCalendar) dia.clone();
        limiteInferior.set(Calendar.HOUR_OF_DAY, 0);
        limiteInferior.set(Calendar.MINUTE, 0);

        GregorianCalendar limiteSuperior = (GregorianCalendar) dia.clone();
        limiteSuperior.set(Calendar.HOUR_OF_DAY, 23);
        limiteSuperior.set(Calendar.MINUTE, 59);

        return devuelveRecordatoriosEnIntervalo(
                usuario, limiteInferior, limiteSuperior, filtro);
    }

    /**
     * Crea una lista de recordatorios a partir de un ResultSet
     * @param rs
     * @return
     * @throws SQLException 
     */
    private List<Recordatorio> creaListaRecordatorios(ResultSet rs) 
            throws SQLException {
        List<Recordatorio> lista = new ArrayList<Recordatorio>();
        ResultSetMetaData rsmd = rs.getMetaData();
        Recordatorio nuevoRecordatorio;

        while (rs.next()) {
            

            nuevoRecordatorio = new Recordatorio(rs.getString(1),
                    parseaFechaFormateada(rs.getString(2)),
                    rs.getString(3), rs.getString(4), rs.getString(5),
                    devuelveContactosSincronizados(
                            rs.getString(1), parseaFechaFormateada(rs.getString(2))));
            
            lista.add(nuevoRecordatorio);

        }
        return lista;
    }

    /**
     * Devuelve los contactos
     * 
     * @param usuario
     * @return 
     */
    public List<Contacto> devuelveContactos(String usuario) {
        List<Contacto> contactos = new ArrayList<Contacto>();
        String sentenciaAEjecutar
                = "SELECT id_contacto, nombre, apellido1,"
                + " apellido2, tfno, correo "
                + "FROM Contactos "
                + "WHERE id_usuario ='" + usuario + "'";

        try {
            ResultSet rs = conexion.createStatement().executeQuery(sentenciaAEjecutar);

            return contactos = creaListaContactos(rs);

        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Devuelve los contactos sincronizados en un recordatorio
     * 
     * @param creador
     * @param fecha
     * @return 
     */
    public List<Tupla<Contacto, String>> devuelveContactosSincronizados(
            String creador, GregorianCalendar fecha) {

        List<Tupla<Contacto, String>> contactos
                = new ArrayList<Tupla<Contacto, String>>();

        String sentenciaAEjecutar
                = "SELECT c.id_contacto,c.nombre, c.apellido1,"
                + " c.apellido2, c.tfno, c.correo, s.permisos "
                + "FROM Contactos c "
                + "JOIN Sincroniza s "
                + "ON s.id_contacto = c.id_contacto "
                + "WHERE s.id_recordatorio = '" + devuelveFechaFormateada(fecha) + "' "
                + "AND s.id_creador = '" + creador + "' ";

        
        try {
            
            ResultSet rs = conexion.createStatement().executeQuery(sentenciaAEjecutar);

            return contactos = creaListaTuplaContactos(rs);

        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Crea una lista de tuplas de contactos y permisos
     * @param rs
     * @return
     * @throws SQLException 
     */
    private List<Tupla<Contacto, String>> creaListaTuplaContactos(ResultSet rs)
            throws SQLException {

        List<Tupla<Contacto, String>> lista
                = new ArrayList<Tupla<Contacto, String>>();

        ResultSetMetaData rsmd = rs.getMetaData();
        Contacto nuevoContacto;

        while (rs.next()) {

            nuevoContacto = new Contacto(
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), Integer.parseInt(rs.getString(5)),
                    rs.getString(6));

            
            lista.add(new Tupla(nuevoContacto, rs.getString(7)));

        }
        return lista;

    }

    /**
     * Devuelve el hash de la contraseña del usuario
     * @param usuario
     * @return 
     */
    public String devuelvePass(String usuario) {
        String sentenciaAEjecutar
                = "SELECT hash_password "
                + "FROM Usuarios "
                + "WHERE id_usuario = '" + usuario + "'";
        String pass = null;

        
        try {
            ResultSet rs
                    = conexion.createStatement().executeQuery(sentenciaAEjecutar);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            if (rs.next()) {
                pass = rs.getString(1);
            }

        } catch (SQLException ex) {
            System.out.println("Modelo.BaseDeDatos.devuelvePass()");
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pass;
    }

    /**
     * Crea una lista de contactos a partir de un ResultSet
     * @param rs
     * @return
     * @throws SQLException 
     */
    private List<Contacto> creaListaContactos(ResultSet rs)
            throws SQLException {

        List<Contacto> lista
                = new ArrayList<Contacto>();
        ResultSetMetaData rsmd = rs.getMetaData();
        Contacto nuevoContacto;

        while (rs.next()) {

            nuevoContacto = new Contacto(
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), Integer.parseInt(rs.getString(5)),
                    rs.getString(6));

            lista.add(nuevoContacto);

        }
        return lista;

    }

    /**
     * Elimina el contacto seleccionado
     * @param usuario
     * @param contacto
     * @return 
     */
    public synchronized boolean eliminar(String usuario, Contacto contacto) {
        int resultado = 0;
        String sentenciaAEjecutar = "DELETE FROM Contactos "
                + " WHERE id_usuario = '" + usuario + "' "
                + "AND id_contacto = '" + contacto.devuelveId() + "'";

        try {
            resultado = conexion.createStatement().executeUpdate(sentenciaAEjecutar);
        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return resultado > 0;
    }

 /**
  * Elimina el recordatorio seleccionado
  * @param usuario
  * @param recordatorio
  * @return 
  */
    public synchronized boolean eliminar(String usuario, Recordatorio recordatorio) {
        String sentenciaAEjecutar;
        int resultado = 0;

        if (usuario.equals(recordatorio.devuelveCreador())) {
            /**
             * Si el creador borra el recordatorio, lo borrará para todo el
             * mundo
             */
            sentenciaAEjecutar = "DELETE r, s FROM Recordatorios r "
                    + "JOIN Sincroniza s ON r.id_creador = s.id_creador "
                    + "AND r.id_recordatorio = s.id_recordatorio "
                    + "WHERE r.id_creador = '" + usuario + "' "
                    + "AND r.id_recordatorio = '"
                    + devuelveFechaFormateada(recordatorio.devuelveFecha()) + "'";

        } else {
            /**
             * Si no, se borra la referencia al recordatorio
             */
            sentenciaAEjecutar = "DELETE FROM Sincroniza "
                    + "WHERE id_contacto = '" + usuario + "'"
                    + "AND id_creador = '" + recordatorio.devuelveCreador() + "'"
                    + "AND id_recordatorio = '"
                    + devuelveFechaFormateada(recordatorio.devuelveFecha()) + "'";
        }

        try {
            resultado = conexion.createStatement().executeUpdate(sentenciaAEjecutar);
        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return resultado > 0;
    }

    /**
     * Elimina el usuario seleccionado, junto con todos sus datos
     * @param usuario
     * @return 
     */
    public synchronized boolean eliminar(String usuario) {
        Statement sentencia;
        String borrarContactos = "DELETE FROM Contactos "
                + " WHERE id_usuario = '" + usuario + "' ";

        String borrarRecordatorios = "DELETE r, s FROM Recordatorios r "
                + "JOIN Sincroniza s ON r.id_creador = s.id_creador "
                + "AND r.id_recordatorio = s.id_recordatorio "
                + "WHERE r.id_creador = '" + usuario + "'";

        String borrarUsuario = "DELETE FROM Usuarios"
                + "WHERE id_usuario = '" + usuario + "' ";

        try {
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(borrarContactos);
            sentencia.executeUpdate(borrarRecordatorios);
            sentencia.executeUpdate(borrarUsuario);
        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    /**
     * Edita el recordatorio seleccionado
     * @param recordatorio
     * @return 
     */
    public synchronized boolean editar(Recordatorio recordatorio) {
        int resultado;
        String editarRecordatorio = "UPDATE Recordatorios "
                + "SET tema = '" + recordatorio.devuelveTema() + "', "
                + "texto = '" + recordatorio.devuelveTexto() + "' "
                + "WHERE id_creador = '" + recordatorio.devuelveCreador() + "' "
                + "AND id_recordatorio = '"
                + devuelveFechaFormateada(recordatorio.devuelveFecha()) + "'";

        /**
         * Elimina los contactos sincronizados con el recordatorio
         */
        String eliminarSincronizados = "DELETE FROM Sincroniza "
                + "WHERE id_creador = '" + recordatorio.devuelveCreador() + "' "
                + "AND id_recordatorio = '"
                + devuelveFechaFormateada(recordatorio.devuelveFecha()) + "' "
                + "AND id_contacto != '" + recordatorio.devuelveCreador() + "'";

        try {
            conexion.createStatement().executeUpdate(editarRecordatorio);
            
            conexion.createStatement().executeUpdate(eliminarSincronizados);
            
            /**
             * Vuelve a introducir los contactos sincronizados
             */
            for (Tupla<Contacto, String> tupla : recordatorio.devuelveSincronizados()) {
                introducir(tupla.a.devuelveId(), recordatorio, tupla.b);
            }
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    /**
     * Edita el contacto seleccionado
     * @param usuario
     * @param contacto
     * @return 
     */
    public synchronized boolean editar(String usuario, Contacto contacto) {
        int resultado;

        String sentenciaAEjecutar = "UPDATE Contactos "
                + "SET "
                + "nombre = '" + contacto.devuelveNombre() + "',"
                + "apellido1 = '" + contacto.devuelveApellido1() + "',"
                + "apellido2 = '" + contacto.devuelveApellido2() + "',"
                + "tfno = '" + contacto.devuelveTfno() + "',"
                + "correo = '" + contacto.devuelveCorreo() + "' "
                + "WHERE id_usuario = '" + usuario + "' "
                + "AND id_contacto = '" + contacto.devuelveId() + "'";

        

        try {
            resultado = conexion.createStatement().executeUpdate(sentenciaAEjecutar);
        } catch (SQLException ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return resultado > 0;
    }
}
