/**
 * ConexionPush.java
 * ccatalan (02/2018)
 * 
 */

package Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

  /**
   *  Conexión con jugador online
   * 
   */
public class ConexionPush {
  private String usuario;
  private Socket socket;
  private BufferedReader entrada;
  private PrintWriter salida;
  private CountDownLatch cierreConexion;
  
  /**
   *  Construye conexión 
   * 
   */  
  public ConexionPush(String usuario, Socket socket,
          CountDownLatch cierreConexion) throws IOException {
    this.usuario = usuario;
    this.socket = socket;
    this.cierreConexion = cierreConexion;
    
    entrada = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    
    // Autoflush!!
    salida = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream())), true); 
  } 
  
  /**
   *  Devuelve nombre
   * 
   */  
  public String devuelveUsuario() {
    return usuario;
  }
  
  /**
   *  Envía solicitud a jugador
   * 
   */
  public synchronized PrimitivaComunicacion enviarSolicitud(
          PrimitivaComunicacion solicitud,
          int tiempoEspera,
          String parametros, List<String> resultados) throws IOException {
    String resultado = null; 
    PrimitivaComunicacion respuesta = PrimitivaComunicacion.NOK;
    
    socket.setSoTimeout(tiempoEspera);
    
    salida.println(solicitud.toString());
    
    if (parametros != null) {
      salida.println(parametros);
    }
    salida.println(PrimitivaComunicacion.FIN);
    
    String linea = entrada.readLine();
    if (linea != null) {
      respuesta = PrimitivaComunicacion.nueva(
              new Scanner(new StringReader(linea)));
      
      resultados.clear();
      resultado = entrada.readLine();
      while ((resultado != null) &&  
             !resultado.equals(PrimitivaComunicacion.FIN.toString())) {
        resultados.add(resultado);  
        resultado = entrada.readLine();
      }
    }
    return respuesta;
  }
  
  /**
   *  Envía solicitud a jugador sin esperar resultados
   * 
   */
  public synchronized PrimitivaComunicacion enviarSolicitud(PrimitivaComunicacion solicitud,
          String parametros) throws Exception {  
    return enviarSolicitud(solicitud, 0, parametros, new ArrayList());
  }
  
  /**
   *  Envía solicitud sin parámetros a jugador sin esperar resultados
   * 
   */
  public synchronized PrimitivaComunicacion enviarSolicitud(PrimitivaComunicacion solicitud)
          throws Exception {  
    return enviarSolicitud(solicitud, 0, null, new ArrayList());
  }  
  
  /**
   *  Envía solicitud a jugador sin esperar resultados
   * 
   */
  public synchronized PrimitivaComunicacion enviarSolicitud(PrimitivaComunicacion solicitud, 
          int tiempoEspera,
          String parametros) throws Exception {  
    return enviarSolicitud(solicitud, tiempoEspera, parametros, new ArrayList());
  }
  
  /**
   *  Envía solicitud sin parámetros a jugador sin esperar resultados
   * 
   */
  public synchronized PrimitivaComunicacion enviarSolicitud(PrimitivaComunicacion solicitud,
          int tiempoEspera)
          throws Exception {  
    return enviarSolicitud(solicitud, tiempoEspera, null, new ArrayList());
  }  
  
  /**
   *  Cierra la conexión
   * 
   */  
  public synchronized void cerrar() throws IOException {
    entrada.close();
    salida.close();
    socket.close(); 
    
    cierreConexion.countDown();
  }
}
