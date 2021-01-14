package Modelo;

import java.io.PrintWriter;

/**
 * Guardable.java
 * 
 * @author Javier Armunia Hinojosa
 * @version 12-3-2018
 */
public interface Guardable {
	/**
	 * Guarda el elemento en el fichero indicado
	 * 
	 * @param pw: PrintWriter del fichero en el que se quiere guardar
	 */
	void guardar(PrintWriter pw);
}
