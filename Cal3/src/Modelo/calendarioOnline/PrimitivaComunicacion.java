/**
 * PrimitivaComunicacion.java
 * @author ccatalan (02/2018)
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 *
 */

package Modelo.calendarioOnline;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Primitiva de comunicación cliente-servidor
 *
 */
public enum PrimitivaComunicacion {
    COMPROBAR_PASS("log_in"),
    CREAR_USUARIO("sign_up"),
    CONECTAR("online"),
    DESCONECTAR("offline"),
    TEST("test"),
    PEDIR_RECORDATORIOS_EN_DIA("get_reminders_day"),
    PEDIR_RECORDATORIOS_EN_MES("get_reminders_month"),
    NUEVO_RECORDATORIO("new_reminder"),
    EDITAR_RECORDATORIO("edit_reminder"),
    ELIMINAR_RECORDATORIO("delete_reminder"),
    PEDIR_CONTACTOS("get_contacts"),
    NUEVO_CONTACTO("new_contact"),
    EDITAR_CONTACTO("edit_contact"),
    ELIMINAR_CONTACTO("delete_contact"),
    FIN("."),
    OK("ok"),
    NOK("nok");

    private String simbolo;
    private static final Pattern expresionRegular
            = Pattern.compile(
                    COMPROBAR_PASS.toString() + "|"
                    + CREAR_USUARIO.toString() + "|"
                    + CONECTAR.toString() + "|"
                    + DESCONECTAR.toString() + "|"
                    + TEST.toString() + "|"
                    + PEDIR_RECORDATORIOS_EN_DIA.toString() + "|"
                    + PEDIR_RECORDATORIOS_EN_MES.toString() + "|"
                    + NUEVO_RECORDATORIO.toString() + "|"
                    + EDITAR_RECORDATORIO.toString() + "|"
                    + ELIMINAR_RECORDATORIO.toString() + "|"
                    + PEDIR_CONTACTOS.toString() + "|"
                    + NUEVO_CONTACTO.toString() + "|"
                    + EDITAR_CONTACTO.toString() + "|"
                    + ELIMINAR_CONTACTO.toString() + "|"
                    + FIN.toString() + "|"
                    + OK.toString() + "|"
                    + NOK.toString());

    /**
     * Construye una primitiva
     *
     */
    PrimitivaComunicacion(String simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * Devuelve una nueva primitiva leída de un scanner
     *
     */
    public static PrimitivaComunicacion nueva(Scanner scanner) throws InputMismatchException {
        String s = scanner.next(expresionRegular);

        if (s.equals(CONECTAR.toString())) {
            return CONECTAR;
        } else if (s.equals(DESCONECTAR.toString())) {
            return DESCONECTAR;
        } else if (s.equals(COMPROBAR_PASS.toString())) {
            return COMPROBAR_PASS;
        } else if (s.equals(CREAR_USUARIO.toString())) {
            return CREAR_USUARIO;
        } else if (s.equals(TEST.toString())) {
            return TEST;
        } else if (s.equals(PEDIR_RECORDATORIOS_EN_MES.toString())) {
            return PEDIR_RECORDATORIOS_EN_MES;
        } else if (s.equals(PEDIR_RECORDATORIOS_EN_DIA.toString())) {
            return PEDIR_RECORDATORIOS_EN_DIA;
        } else if (s.equals(NUEVO_RECORDATORIO.toString())) {
            return NUEVO_RECORDATORIO;
        } else if (s.equals(EDITAR_RECORDATORIO.toString())) {
            return EDITAR_RECORDATORIO;
        } else if (s.equals(ELIMINAR_RECORDATORIO.toString())) {
            return ELIMINAR_RECORDATORIO;
        } else if (s.equals(PEDIR_CONTACTOS.toString())) {
            return PEDIR_CONTACTOS;
        } else if (s.equals(NUEVO_CONTACTO.toString())) {
            return NUEVO_CONTACTO;
        } else if (s.equals(EDITAR_CONTACTO.toString())) {
            return EDITAR_CONTACTO;
        } else if (s.equals(ELIMINAR_CONTACTO.toString())) {
            return ELIMINAR_CONTACTO;
        } else if (s.equals(FIN.toString())) {
            return FIN;
        } else if (s.equals(OK.toString())) {
            return OK;
        } else {
            return NOK;
        }
    }

    /**
     * toString
     *
     */
    @Override
    public String toString() {
        return simbolo;
    }
}
