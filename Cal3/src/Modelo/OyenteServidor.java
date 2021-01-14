/**
 * OyenteServidor.java
 * ccatalan (01/2018)  
 */

package Modelo;

import Modelo.calendarioOnline.PrimitivaComunicacion;
import java.io.PrintWriter;
import java.util.List;

/**
 *  Interfaz de oyente para recibir solicitudes del servidor partidas online
 * 
 */
public interface OyenteServidor {
   public enum Solicitud { NUEVO_RECORDATORIO }
     
   /**
    *  Llamado para notificar una solicitud del servidor
    * 
    */ 
   public void solicitudServidorProducida(
           PrimitivaComunicacion solicitud, 
           List<String> parametros, PrintWriter salida) throws Exception;
}
