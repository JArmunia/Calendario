/**
 * FactoriaModelo.java
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 *
 */
package Modelo;


import Control.Calendario;
import Vista.Localizacion;


public class FactoriaModelo {
    /**
   *  Devuelve nueva partida de tres en raya
   * 
   */    
  public static CalendarioModelo nuevoCalendario(
          Calendario calendario, Localizacion localizacion) {
      
    return new CalendarioModelo(calendario, localizacion);    
  }
  
  
}
