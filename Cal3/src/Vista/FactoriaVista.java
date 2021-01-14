/**
 * FactoriaVista.java
 * 
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import Control.OyenteVista;
import Modelo.CalendarioModelo;


public class FactoriaVista {

    /**
     * Devuelve vista del calendario
     *
     */
    public static CalendarioVista calendarioVista(OyenteVista oyenteVista,
            String version, CalendarioModelo modelo, String lenguaje, String pais) {

        return CalendarioVista.devolverInstancia(
                oyenteVista, version, modelo, lenguaje, pais);
    }
}
