/**
 * OyenteVista.java
 *
 * @author ccatalan
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Control;

/**
 * Interfaz de oyente para recibir eventos de la interfaz de usuario
 *
 */
public interface OyenteVista {

    public enum Evento {
        INICIAR_SESION, CERRAR_SESION, REGISTRAR_USUARIO, NUEVO_RECORDATORIO, EDITAR_RECORDATORIO,
        ELIMINAR_RECORDATORIO, NUEVO_CONTACTO, ELIMINAR_CONTACTO, EDITAR_CONTACTO,
        ANTERIOR, SIGUIENTE, CARGAR_RECORDATORIOS, CARGAR_CONTACTOS, GUARDAR_RECORDATORIOS,
        GUARDAR_CONTACTOS, GUARDAR_RECORDATORIOS_COMO, GUARDAR_CONTACTOS_COMO,
        CAMBIAR_LENGUAJE, SALIR, SELECCION_DIA
    }

    /**
     * Llamado para notificar un evento de la interfaz de usuario
     *
     */
    public void eventoProducido(Evento evento, Object obj);

    /**
     * Devuelve si es modo debug
     *
     */
    public boolean esModoDebug();
}
