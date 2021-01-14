/**
 * RecordatoriosVista.java
 * Vista de los recordatorios
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import Control.OyenteVista;
import Modelo.Recordatorio;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class RecordatoriosVista extends JPanel {


    private DefaultListModel<Recordatorio> listModel;
    private JList list;
    private Localizacion localizacion;
    private CalendarioVista vista;

    /**
     * Construye la vista de los recordatorios
     * 
     * @param vista 
     */
    public RecordatoriosVista(CalendarioVista vista, Localizacion localizacion) {
        super();
        this.vista = vista;
        listModel = new DefaultListModel();

        list = new JList(listModel);
        add(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Recordatorio recordatorio = (Recordatorio) value;
                SimpleDateFormat sdf = new SimpleDateFormat(
                        localizacion.devuelve(localizacion.FORMATO_HORA_RECORDATORIO));
                String tituloRecordatorio = 
                        sdf.format(recordatorio.devuelveFecha().getTime());
                tituloRecordatorio = tituloRecordatorio + " " +
                        localizacion.devuelve(recordatorio.devuelveTema()) + 
                        ": " + recordatorio.devuelveTexto();             
                
                return super.getListCellRendererComponent(
                        list, tituloRecordatorio, index, isSelected, cellHasFocus); 
            }
        });
        
        list.setBackground(this.getBackground());
        list.addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse
            ) {
                vista.habilitarEvento(OyenteVista.Evento.EDITAR_RECORDATORIO,
                        list.getSelectedValue() != null);
                vista.habilitarEvento(OyenteVista.Evento.ELIMINAR_RECORDATORIO,
                        list.getSelectedValue() != null);
            }
        }
        );

    }

    /**
     * Introduce un recordatorio en la vista
     * 
     * @param recordatorio 
     */
    public void nuevoRecordatorio(Recordatorio recordatorio) {
        listModel.addElement(recordatorio);
    }

    /**
     * Limpia la vista de recordatorios
     */
    public void limpiar() {
        listModel.clear();
    }

    /**
     * Devuelve el recordatorio seleccionado
     * 
     * @return recordatorio seleccionado 
     */
    public Recordatorio devuelveRecordatorioSeleccionado() {
        return (Recordatorio) list.getSelectedValue();
    }
}
