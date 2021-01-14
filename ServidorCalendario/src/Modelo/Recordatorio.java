/**
 * Recordatorio.java Recordatorio del calendario
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */

package Modelo;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Scanner;

import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Recordatorio.java Recordatorio del calendario
 *
 * @author Javier Armunia Hinojosa
 * @version 12-3-2018
 */
public class Recordatorio implements Guardable, Identificable<GregorianCalendar> {
    
    public static final String LECTURA = "LECTURA";
    public static final String LECTURA_ESCRITURA = "LECTURA_ESCRITURA";

    private GregorianCalendar fecha;
    private String tema;
    private String texto;
    private String creador;
    private String permisos;
    private List<Tupla<Contacto, String>> usuariosSincronizados;
    

    /**
     * Construye el recordatorio
     *
     * @param fecha: Fecha del recordatorio
     * @param tema: Descripci�n breve del recordatorio
     * @param texto: Texto descriptivo del recordatorio
     * @param creador: Usuario creador del recordatorio
     */
    public Recordatorio(String creador, GregorianCalendar fecha, String tema,
            String texto, String permisos) {
        this.fecha = fecha;
        this.tema = tema;
        this.texto = texto;
        this.creador = creador;
        this.permisos = permisos;
        usuariosSincronizados = new ArrayList<Tupla<Contacto, String>>();
                
    }

    /**
     * Construye el recordatorio
     *
     * @param fecha: Fecha del recordatorio
     * @param tema: Descripci�n breve del recordatorio
     * @param texto: Texto descriptivo del recordatorio
     * @param creador: Usuario creador del recordatorio
     * @param usuariosSincronizados: Usuarios sincronizados en el recordatorio
     */
    public Recordatorio(String creador, GregorianCalendar fecha, String tema,
            String texto,  String permisos, 
            List<Tupla<Contacto, String>> usuariosSincronizados) {
        
        this.fecha = fecha;
        this.tema = tema;
        this.texto = texto;
        this.creador = creador;
        this.usuariosSincronizados = usuariosSincronizados;
        this.permisos = permisos;
    }

    /**
     * Recupera el recordatorio de un fichero
     *
     * @param fichero: Scanner del fichero en el que se guardar� el recordatorio
     */
    public Recordatorio(Scanner fichero) {
        int minuto, hora, dia, mes, anyo;

        minuto = fichero.nextInt();
        hora = fichero.nextInt();
        dia = fichero.nextInt();
        mes = fichero.nextInt();
        anyo = fichero.nextInt();
        fecha = new GregorianCalendar(anyo, mes, dia, hora, minuto);
        tema = fichero.next();
        texto = fichero.next();

    }

    /**
     * Guarda el recordatorio en un fichero
     *
     * @param pw: Printwriter del fichero donde se guardar� el recordatorio
     */
    public void guardar(PrintWriter pw) {

        Gson gson = new Gson();
        String json = gson.toJson(this);
        pw.println(json);

    }

    /**
     * Devuelve la fecha del recordatorio
     *
     * @return Fecha del recordatorio
     */
    public GregorianCalendar devuelveFecha() {
        return fecha;
    }

    /**
     * Devuelve el tema del recordatorio
     *
     * @return Tema del recordatorio
     */
    public String devuelveTema() {
        return tema;
    }

    public String devuelveTexto() {
        return texto;
    }

    /**
     * Sobreescribe toString
     *
     * @return Cadena de texto que representa el recordatorio
     */
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM 'de' yyyy 'a las' HH:mm");

        return "Fecha: " + sdf.format(fecha.getTime()) + " Tema: " + tema + " Texto: " + texto;
        
    }

    /**
     * Devuelve el identificador
     *
     * @return Identificador del recordatorio
     */
    public GregorianCalendar devuelveId() {

        return fecha;
    }

    public boolean mismoDia(GregorianCalendar dia) {
        return (fecha.get(Calendar.DAY_OF_MONTH) == dia.get(Calendar.DAY_OF_MONTH)
                && fecha.get(Calendar.MONTH) == dia.get(Calendar.MONTH)
                && fecha.get(Calendar.YEAR) == dia.get(Calendar.YEAR));

    }

    public boolean mismoMes(GregorianCalendar dia) {
        return (fecha.get(Calendar.YEAR) == dia.get(Calendar.YEAR)
                && fecha.get(Calendar.MONTH) == dia.get(Calendar.MONTH));
    }

    

    public String devuelveCreador() {
        return creador;
    }

    public List<Tupla<Contacto, String>> devuelveSincronizados() {
        return usuariosSincronizados;
    }
    
        @Override
    public String devuelveJson() {       
        return new Gson().toJson(this);
    }
}
