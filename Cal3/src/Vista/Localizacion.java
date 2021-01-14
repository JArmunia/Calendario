/**
 * Localizacion.java
 * @author ccatalan (02/2018) *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */

package Vista;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.UIManager;

/**
 * Localizacion recursos de la vista
 *
 */
public class Localizacion {

    private static Localizacion instancia = null; // es singleton
    private ResourceBundle recursos;

    public static String LENGUAJE_ESPANOL = "es";
    public static String PAIS_ESPANA = "ES";
    public static String LENGUAJE_INGLES = "en";
    public static String PAIS_USA = "US";

    private static final String TEXTOS_LOCALIZADOS = "/textos_localizados";
    public static final String RUTA_RECURSOS = "/RecursosVista";
    public static final String FORMATO_FECHA = "FORMATO_FECHA";
    public static final String FORMATO_FECHA_RECORDATORIO
            = "FORMATO_FECHA_RECORDATORIO";
    public static final String FORMATO_HORA_RECORDATORIO
            = "FORMATO_HORA_RECORDATORIO";
    public static final String ERROR_CONEXION_SERVIDOR_CALENDARIO
            = "ERROR_CONEXION_SERVIDOR_CALENDARIO";
    public static final String FALLO_INICIO_SESION = "FALLO_INICIO_SESION";
    public static final String REGISTRO_EXITOSO = "REGISTRO_EXITOSO";
    public static final String FALLO_REGISTRO = "FALLO_REGISTRO";
    public static final String SIN_PERMISOS = "SIN_PERMISOS";
    public static final String FALLO_PASS = "FALLO_PASS";
    public static final String REGISTRO = "REGISTRO";
    public static final String INICIO_SESION = "INICIO_SESION";
    public static final String NUEVO_RECORDATORIO_SINCRONIZADO
            = "NUEVO_RECORDATORIO_SINCRONIZADO";
    public static final String TEXTO_PERMISOS = "TEXTO_PERMISOS";
    public static final String LECTURA = "LECTURA";
    public static final String LECTURA_ESCRITURA = "LECTURA_ESCRITURA";
    public static final String SINCRONIZAR = "SINCRONIZAR";
    public static final String CAMBIAR_PERMISOS = "CAMBIAR_PERMISOS";
    public static final String PASSWORD = "PASSWORD";
    public static final String CONFIRMA_PASSWORD = "CONFIRMA_PASSWORD";
    public static final String USER = "USER";
    public static final String TEXTO_MENU_NUEVO = "TEXTO_MENU_NUEVO";
    public static final String TEXTO_MENU_EDITAR = "TEXTO_MENU_EDITAR";
    public static final String TEXTO_MENU_ELIMINAR = "TEXTO_MENU_ELIMINAR";
    public static final String TEXTO_MENU_MOSTRAR = "TEXTO_MENU_MOSTRAR";
    public static final String TEXTO_MENU_GUARDAR_RECORDATORIOS
            = "TEXTO_MENU_GUARDAR_RECORDATORIOS";
    public static final String TEXTO_MENU_GUARDAR_RECORDATORIOS_COMO
            = "TEXTO_MENU_GUARDAR_RECORDATORIOS_COMO";
    public static final String TEXTO_MENU_GUARDAR_CONTACTOS
            = "TEXTO_MENU_GUARDAR_CONTACTOS";
    public static final String TEXTO_MENU_GUARDAR_CONTACTOS_COMO
            = "TEXTO_MENU_GUARDAR_CONTACTOS_COMO";
    public static final String TEXTO_MENU_FICHERO = "TEXTO_MENU_FICHERO";
    public static final String TEXTO_MENU_CARGAR_RECORDATORIOS
            = "TEXTO_MENU_CARGAR_RECORDATORIOS";
    public static final String TEXTO_MENU_CARGAR_CONTACTOS
            = "TEXTO_MENU_CARGAR_CONTACTOS";
    public static final String TEXTO_MENU_SALIR = "TEXTO_MENU_SALIR";
    public static final String TEXTO_MENU_CONTACTOS = "TEXTO_MENU_CONTACTOS";
    public static final String TEXTO_MENU_RECORDATORIOS
            = "TEXTO_MENU_RECORDATORIOS";
    public static final String TEXTO_MENU_AYUDA = "TEXTO_MENU_AYUDA";
    public static final String ATAJO_ITEM_DEBUG = "ATAJO_ITEM_DEBUG";
    public static final String ATAJO_ITEM_ACERCA_DE = "ATAJO_ITEM_ACERCA_DE";
    public static final String TITULO = "TITULO";
    public static final String TODO = "TODO";
    public static final String PERSONAL = "PERSONAL";
    public static final String TRABAJO = "TRABAJO";
    public static final String ESTUDIOS = "ESTUDIOS";
    public static final String TEXTO_DEFECTO_RECORDATORIO
            = "TEXTO_DEFECTO_RECORDATORIO";
    public static final String TITULO_FORMULARIO_RECORDATORIO
            = "TITULO_FORMULARIO_RECORDATORIO";
    public static final String TITULO_FORMULARIO_CONTACTO
            = "TITULO_FORMULARIO_CONTACTO";
    public static final String TEXTO_BOTON_ACEPTAR = "TEXTO_BOTON_ACEPTAR";
    public static final String TEXTO_BOTON_CANCELAR = "TEXTO_BOTON_CANCELAR";
    public static final String ETIQUETA_FECHA = "ETIQUETA_FECHA";
    public static final String ETIQUETA_HORA = "ETIQUETA_HORA";
    public static final String MENU_ITEM_OPCIONES = "MENU_ITEM_OPCIONES";
    public static final String MENU_ITEM_LENGUAJE = "MENU_ITEM_LENGUAJE";
    public static final String MENU_ITEM_ESPANOL = "MENU_ITEM_ESPANOL";
    public static final String MENU_ITEM_INGLES = "MENU_ITEM_INGLES";
    public static final String MENU_ITEM_SALIR = "MENU_ITEM_SALIR";
    public static final String MENU_ITEM_CERRAR_SESION = "MENU_ITEM_CERRAR_SESION";
    public static final String ATAJO_MENU_ITEM_SALIR = "ATAJO_MENU_ITEM_SALIR";
    public static final String CONFIRMACION_LENGUAJE = "CONFIRMACION_LENGUAJE";
    public static final String CONFIGURACION_NO_GUARDADA
            = "CONFIGURACION_NO_GUARDADA";
    public static final String TEXTO_NOMBRE = "TEXTO_NOMBRE";
    public static final String TEXTO_ID = "TEXTO_ID";
    public static final String TEXTO_APELLIDO_1 = "TEXTO_APELLIDO_1";
    public static final String TEXTO_APELLIDO_2 = "TEXTO_APELLIDO_2";
    public static final String TEXTO_TELEFONO = "TEXTO_TELEFONO";
    public static final String TEXTO_CORREO = "TEXTO_CORREO";
    public static final String MENSAJE_GUARDAR_CONTACTOS
            = "MENSAJE_GUARDAR_CONTACTOS";
    public static final String MENSAJE_GUARDAR_RECORDATORIOS
            = "MENSAJE_GUARDAR_RECORDATORIOS";
    public static final String MENU_ITEM_DEBUG = "MENU_ITEM_DEBUG";
    public static final String MENU_ITEM_ACERCA_DE = "MENU_ITEM_ACERCA_DE";
    public static final String RECORDATORIOS_NO_GUARDADOS
            = "RECORDATORIOS_NO_GUARDADOS";
    public static final String CONTACTOS_NO_GUARDADOS = "CONTACTOS_NO_GUARDADOS";
    public static final String FICHERO_RECORDATORIOS_NO_ENCONTRADO
            = "FICHERO_RECORDATORIOS_NO_ENCONTRADO";
    public static final String FICHERO_CONTACTOS_NO_ENCONTRADO
            = "FICHERO_CONTACTOS_NO_ENCONTRADO";
    public static final String FICHERO_X_NO_ENCONTRADO = "FICHERO_X_NO_ENCONTRADO";
    public static final String RECORDATORIOS_NO_LEIDOS
            = "RECORDATORIOS_NO_LEIDOS";
    public static final String CONTACTOS_NO_LEIDOS = "CONTACTOS_NO_LEIDOS";
    public static final String RECORDATORIO_NO_GUARDADO
            = "RECORDATORIO_NO_GUARDADO";
    public static final String RECORDATORIO_NO_EDITADO
            = "RECORDATORIO_NO_EDITADO";
    public static final String RECORDATORIO_NO_ELIMINADO
            = "RECORDATORIO_NO_ELIMINADO";
    public static final String CONTACTO_NO_GUARDADO = "CONTACTO_NO_GUARDADO";
    public static final String CONTACTO_NO_EDITADO = "CONTACTO_NO_EDITADO";
    public static final String CONTACTO_NO_ELIMINADO = "CONTACTO_NO_ELIMINADO";
    public static final String FICHERO_TEMAS_NO_ENCONTRADO
            = "FICHERO_TEMAS_NO_ENCONTRADO";

    public static final String LUNES = "LUNES";
    public static final String MARTES = "MARTES";
    public static final String MIERCOLES = "MIERCOLES";
    public static final String JUEVES = "JUEVES";
    public static final String VIERNES = "VIERNES";
    public static final String SABADO = "SABADO";
    public static final String DOMINGO = "DOMINGO";

    public static final String FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO
            = "Locale file not found or wrong";
    public static final String FICHERO_LOCALIZACION_NO_GUARDADO
            = "Locale file not saved";

    String[] sistema = {
        "OptionPane.cancelButtonText",
        "OptionPane.okButtonText",
        "OptionPane.noButtonText",
        "OptionPane.yesButtonText",
        "OptionPane.yesButtonText",
        "FileChooser.openDialogTitleText",
        "FileChooser.saveDialogTitleText",
        "FileChooser.lookInLabelText",
        "FileChooser.saveInLabelText",
        "FileChooser.openButtonText",
        "FileChooser.saveButtonText",
        "FileChooser.cancelButtonText",
        "FileChooser.fileNameLabelText",
        "FileChooser.filesOfTypeLabelText",
        "FileChooser.openButtonToolTipText",
        "FileChooser.cancelButtonToolTipText",
        "FileChooser.fileNameHeaderText",
        "FileChooser.upFolderToolTipText",
        "FileChooser.homeFolderToolTipText",
        "FileChooser.newFolderToolTipText",
        "FileChooser.listViewButtonToolTipText",
        "FileChooser.newFolderButtonText",
        "FileChooser.renameFileButtonText",
        "FileChooser.deleteFileButtonText",
        "FileChooser.filterLabelText",
        "FileChooser.detailsViewButtonToolTipText",
        "FileChooser.fileSizeHeaderText",
        "FileChooser.fileDateHeaderText",
        "FileChooser.acceptAllFileFilterText"};

    /**
     * Construye localizacion
     *
     */
    private Localizacion(String lenguaje, String pais) {
        try {
            Locale locale = new Locale(lenguaje, pais);
            recursos = ResourceBundle
                    .getBundle(RUTA_RECURSOS.substring(1).replace('/', '.')
                            + TEXTOS_LOCALIZADOS, locale);

            // localiza textos sistema si no son los de defecto
            if (!locale.equals(Locale.getDefault())) {
                for (int i = 0; i < sistema.length; i++) {
                    UIManager.put(sistema[i],
                            recursos.getString(sistema[i]));
                }
            }
        } catch (MissingResourceException e) {
            System.err.println("SIn recursos");
//       if (vista.esModoDebug()) {
//          DebugVista.devolverInstancia().mostrar(
//                   FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO, e);
//       }
            //else {
//          mensajeDialogo(FICHERO_LOCALIZACION_NO_ENCONTRADO_O_ERRONEO);
//       }        
        }
    }

    /**
     * Devuelve la instancia de la localizacion
     *
     */
    public static synchronized Localizacion devolverInstancia(
            String lenguaje, String pais) {
        if (instancia == null) {
            instancia = new Localizacion(lenguaje, pais);
        }
        return instancia;
    }

    /**
     * Localiza recurso
     *
     */
    public String devuelve(String texto) {
        return recursos.getString(texto);
    }
}
