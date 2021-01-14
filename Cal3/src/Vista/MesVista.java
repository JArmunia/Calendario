/**
 * MesVista.java
 * Vista del mes
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import Modelo.CalendarioModelo;
import Modelo.Identificable;
import Modelo.Recordatorio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MesVista extends JPanel {

    private final static int COLUMNAS = 7;
    private final static int FILAS = 6;
    private final static Color COLOR_FONDO = Color.WHITE;

    private CalendarioModelo modelo;
    private CalendarioVista vista;
    private Localizacion localizacion;
    private JPanel panelDias;
    private int mes;
    private int anyo;

    /**
     * Constructor de VistaMes, construye la representación del mes
     *
     * @param mes
     * @param anyo
     * @param calendario
     * @param vista
     */
    public MesVista(
            int mes, int anyo, CalendarioModelo calendario,
            CalendarioVista vista, Localizacion localizacion) {
        this.modelo = calendario;
        this.vista = vista;
        this.localizacion = localizacion;
        this.mes = mes;
        this.anyo = anyo;
        setLayout(new BorderLayout());
        DiaVista nuevoDia;
        setBackground(COLOR_FONDO);

        panelDias = new JPanel();
        add(panelDias, BorderLayout.CENTER);
        panelDias.setLayout(new GridLayout(FILAS, COLUMNAS));

        for (int i = 0; i < FILAS * COLUMNAS; i++) {
            nuevoDia = new DiaVista();
            panelDias.add(nuevoDia);

            nuevoDia.addActionListener(vista);
            nuevoDia.setActionCommand(CalendarioVista.SELECCION_DIA);
        }

        pintaMes();

        JPanel panelSemana = new JPanel(new GridLayout(1, 7));

        add(panelSemana, BorderLayout.NORTH);
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.LUNES), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.MARTES), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.MIERCOLES), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.JUEVES), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.VIERNES), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.SABADO), SwingConstants.CENTER));
        panelSemana.add(new JLabel(
                localizacion.devuelve(localizacion.DOMINGO), SwingConstants.CENTER));

        for (Component componente : panelSemana.getComponents()) {
            ((JLabel) componente).setSize(panelDias.getSize().width / 7, 20);
            componente.setFont(componente.getFont().deriveFont(Font.CENTER_BASELINE));
        }

    }

    /**
     * Devuelve el mes representado.
     *
     * @return Mes representado
     */
    public int devuelveMes() {
        return mes;
    }

    /**
     * Devuelve el año del mes representado.
     *
     * @return Año del mes representado
     */
    public int devuelveAnyo() {
        return anyo;
    }

    /**
     * Representa los dias del mes y marca el día actual.
     */
    public void pintaMes() {

        Component[] componentes = panelDias.getComponents();
        GregorianCalendar fecha = new GregorianCalendar(anyo, mes, 1);
        GregorianCalendar hoy = new GregorianCalendar();
        int diasEnMes = fecha.getActualMaximum(Calendar.DAY_OF_MONTH);

        /*
        En la clase Calendar, el domingo es el día 1, por lo que para calcular
        la cantidad de espacios que hay que dejar en la primera semana para que 
        el calendario se muestre correctamente, restamos 2 al número que nos 
        devuelva Calendar. Al restar 2 al domingo (1), queda -1, en ese caso 
        ponemos 6 espacios en la primera semana.        
         */
        int espaciosPrimeraSemana = (fecha.get(Calendar.DAY_OF_WEEK) - 2);
        if (espaciosPrimeraSemana == -1) {
            espaciosPrimeraSemana = 6;
        }

        GregorianCalendar nuevoDia;
        int numeroDia = 1;
        int casilla = 0;
        DiaVista dia;
        for (Component componente : componentes) {
            dia = (DiaVista) componente;
            if (casilla >= espaciosPrimeraSemana && numeroDia <= diasEnMes) {
                nuevoDia = new GregorianCalendar(anyo, mes, numeroDia);

                dia.nuevoDia(nuevoDia, false);

                if (numeroDia == hoy.get(Calendar.DAY_OF_MONTH)
                        && mes == hoy.get(Calendar.MONTH)
                        && anyo == hoy.get(Calendar.YEAR)) {
                    dia.esHoy(true);
                } else {
                    dia.esHoy(false);
                }
                numeroDia++;
            } else {
                dia.nuevoDia();
            }
            casilla++;

        }
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                List<Recordatorio> recordatoriosEnMes = modelo.devuelveRecordatoriosEnMes(
                        new GregorianCalendar(anyo, mes, 1), vista.devuelveFiltro());

                pintaRecordatorios(recordatoriosEnMes);
            }
        }.start();

    }

    /**
     * Representa en el mes la lista de recordatorios proporcionada
     *
     * @param recordatorios
     */
    public void pintaRecordatorios(List<Recordatorio> recordatorios) {
        Component[] componentes = panelDias.getComponents();
        DiaVista dia;
        for (Component comp : componentes) {
            dia = (DiaVista) comp;
            dia.tieneRecordatorio(false);
        }

        for (Component comp : componentes) {
            dia = (DiaVista) comp;
            if (dia.devuelveFecha() != null) {

                for (Recordatorio recordatorio : recordatorios) {

                    if (recordatorio.mismoDia(dia.devuelveFecha())) {
                        dia.tieneRecordatorio(true);
                    }
                }
            }
        }
    }

    /**
     * Representa el mes siguiente
     */
    public void siguiente() {
        mes = mes + 1;
        pintaMes();
    }

    /**
     * Representa el mes anterior
     */
    public void anterior() {
        mes = mes - 1;
        pintaMes();
    }
}
