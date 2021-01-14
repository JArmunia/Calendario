package Modelo;

/**
 * Calendario.java
 * 
 * @author Javier Armunia Hinojosa
 * @version 12-3-2018
 * @param <E>: Clase del identificador
 */
public interface Identificable<E> {
	
	/**
	 * Identifica al objeto
	 * @return Un objeto de clase E que representa al objeto
	 */
	public E devuelveId();
        
        /**
         * Devuelve un JSON que representa al objeto.
         * @return json         * 
         */
        public String devuelveJson();
        
}
