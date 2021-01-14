/**
 * Contacto.java
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 *
 */
package Modelo;

import java.io.PrintWriter;
import java.util.Scanner;
import com.google.gson.Gson;

public class Contacto implements Guardable, Identificable<String> {

    private String id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private int tfno;
    private String correo;

    /**
     * Construye un contacto
     *
     * @param id: Identificador �nico
     * @param nombre: Nombre del contacto
     * @param apellido1: Primer apellido
     * @param apellido2: Segundo apellido
     * @param tfno: Telefono
     * @param correo: Correo Electr�nico
     */
    public Contacto(String id, String nombre, String apellido1, String apellido2, int tfno, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.tfno = tfno;
        this.correo = correo;
    }

    /**
     * Recupera un contacto de un fichero
     *
     * @param fichero: Scanner del fichero en el que se encuentra el contacto
     */
    public Contacto(Scanner fichero) {

        id = fichero.next();
        nombre = fichero.next();
        apellido1 = fichero.next();
        apellido2 = fichero.next();
        tfno = Integer.parseInt(fichero.next());
        correo = fichero.next();

    }

    /**
     * Guarda el contacto en memoria
     *
     * @param pw: PrintWriter del fichero en el que se quiere guardar el
     * contacto
     */
    public void guardar(PrintWriter pw) {

        Gson gson = new Gson();
        String json = gson.toJson(this);
        pw.println(json);

    }
    
     /**
     * Guarda el contacto en memoria
     *
     * @param pw: PrintWriter del fichero en el que se quiere guardar el
     * contacto
     */
    public String devuelveJson() {

        Gson gson = new Gson();        
       return gson.toJson(this);

    }

    /**
     * M�todo toString sobreescrito
     *
     * @return Cadena de texto que representa al contacto
     */
    public String toString() {
        String contacto = "Contacto: " + id + " Nombre: " + nombre + " Apellidos: " + apellido1 + " " + apellido2
                + " Telefono: " + tfno + " Correo: " + correo;

        return contacto;
    }

    /**
     * Devuelve el identificador �nico del contacto
     *
     * @return Identificador del contacto
     */
    public String devuelveId() {

        return id;
    }

    /**
     * Devuelve el nombre
     *
     * @return nombre
     */
    public String devuelveNombre() {
        return nombre;
    }

    /**
     * Devuelve el primer apellido
     *
     * @return apellido1
     */
    public String devuelveApellido1() {
        return apellido1;
    }

    /**
     * Devuelve el segundo apellido
     *
     * @return apellido2
     */
    public String devuelveApellido2() {
        return apellido2;
    }

    /**
     * Devuelve el numero de teléfono
     *
     * @return tfno
     */
    public int devuelveTfno() {
        return tfno;
    }

    /**
     * Devuelve la dirección de correo electrónico
     *
     * @return correo
     */
    public String devuelveCorreo() {
        return correo;
    }
}
