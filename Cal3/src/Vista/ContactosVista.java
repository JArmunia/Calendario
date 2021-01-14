/**
 * ContactosVista.java
 * Vista de los contactos
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import Modelo.CalendarioModelo;
import Modelo.Contacto;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ContactosVista extends JDialog {

    private final static int ANCHO_VENTANA = 500;
    private final static int LARGO_VENTANA = 400;

    private CalendarioModelo modelo;
    private CalendarioVista vista;
    private Localizacion localizacion;
    private JList list;
    private JButton nuevo;
    private JButton editar;
    private JButton eliminar;
    private static ContactosVista instancia = null;
    private JLabel campoId;
    private JLabel campoNombre;
    private JLabel campoApellido1;
    private JLabel campoApellido2;
    private JLabel campoTfno;
    private JLabel campoCorreo;
    private JPanel panelInfoContacto;

    /**
     * Construye la vista de los contactos
     *
     * @param localizacion
     * @param contactos
     */
    private ContactosVista(CalendarioVista vista, Localizacion localizacion, CalendarioModelo modelo) {
        super(vista, false);

        this.vista = vista;
        this.localizacion = localizacion;
        this.modelo = modelo;

        setLayout(new BorderLayout());
        setSize(ANCHO_VENTANA, LARGO_VENTANA);
        setTitle(localizacion.devuelve(localizacion.TEXTO_MENU_CONTACTOS));

        nuevo = new JButton(localizacion.devuelve(localizacion.TEXTO_MENU_NUEVO));
        nuevo.addActionListener(vista);
        nuevo.setActionCommand(vista.NUEVO_CONTACTO);
        editar = new JButton(localizacion.devuelve(localizacion.TEXTO_MENU_EDITAR));
        editar.addActionListener(vista);
        editar.setActionCommand(vista.EDITAR_CONTACTO);
        editar.setEnabled(false);
        eliminar = new JButton(localizacion.devuelve(localizacion.TEXTO_MENU_ELIMINAR));
        eliminar.setActionCommand(vista.ELIMINAR_CONTACTO);
        eliminar.addActionListener(vista);
        eliminar.setEnabled(false);

        JPanel panelBotones = new JPanel();
        panelBotones.add(nuevo);
        panelBotones.add(editar);
        panelBotones.add(eliminar);
        add(panelBotones, BorderLayout.NORTH);

        panelInfoContacto = new JPanel(new GridBagLayout());
        panelInfoContacto.setVisible(false);
        add(panelInfoContacto, BorderLayout.EAST);

        GridBagConstraints etiqueta = new GridBagConstraints();
        GridBagConstraints campo = new GridBagConstraints();
        etiqueta.anchor = GridBagConstraints.FIRST_LINE_START;

        campo.weightx = 1.0;
        etiqueta.gridx = 0;
        etiqueta.insets = new Insets(10, 10, 10, 10);
        campo.insets = new Insets(0, 0, 0, 10);
        campo.fill = GridBagConstraints.HORIZONTAL;
        campo.gridx = 1;

        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_ID)), etiqueta);
        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_NOMBRE)), etiqueta);
        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_APELLIDO_1)), etiqueta);
        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_APELLIDO_1)), etiqueta);
        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_TELEFONO)), etiqueta);
        panelInfoContacto.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_CORREO)), etiqueta);

        campoId = new JLabel();
        campoNombre = new JLabel();
        campoApellido1 = new JLabel();
        campoApellido2 = new JLabel();
        campoTfno = new JLabel();
        campoCorreo = new JLabel();

        panelInfoContacto.add(campoId, campo);
        panelInfoContacto.add(campoNombre, campo);
        panelInfoContacto.add(campoApellido1, campo);
        panelInfoContacto.add(campoApellido2, campo);
        panelInfoContacto.add(campoTfno, campo);
        panelInfoContacto.add(campoCorreo, campo);

        list = new JList();
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                value = (((Contacto) value).devuelveId());

                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
            }
        });
        listarContactos();

        add(new JScrollPane(list), BorderLayout.CENTER);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                mostrarInfoContacto();
                editar.setEnabled(list.getSelectedValue() != null);
                eliminar.setEnabled(list.getSelectedValue() != null);
            }
        });

        setLocationRelativeTo(null);
        setVisible(false);
    }

    /**
     * Lista los contactos
     *
     */
    public void listarContactos() {
        DefaultListModel<Contacto> listaModelo = new DefaultListModel();
        for (Map.Entry<String, Contacto> entrada : modelo.devuelveContactos().entrySet()) {
            listaModelo.addElement(entrada.getValue());
            listaModelo.get(listaModelo.getSize() - 1);
        }
        list.setModel(listaModelo);
    }

    /**
     * Devuelve la instancia de la vista de contactos (Es singleton)
     *
     * @param vista
     * @param contactos
     * @return
     */
    public static synchronized ContactosVista devolverInstancia(
            CalendarioVista vista, Localizacion localizacion, CalendarioModelo modelo) {
        if (instancia == null) {
            instancia = new ContactosVista(vista, localizacion, modelo);
        }
        return instancia;
    }

    /**
     * Hace visible la vista de contactos
     */
    public void mostrar() {
        setVisible(true);
    }

    /**
     * Devuelve el contacto seleccionado
     *
     * @return contacto seleccionado
     */
    public Contacto devuelveContacto() {
        return (Contacto) list.getSelectedValue();
    }

    /**
     * Hace visible la información del contacto seleccionado
     */
    public void mostrarInfoContacto() {
        Contacto contactoSeleccionado = devuelveContacto();
        if (contactoSeleccionado != null) {
            panelInfoContacto.setVisible(true);
            campoId.setText(contactoSeleccionado.devuelveId());
            campoNombre.setText(contactoSeleccionado.devuelveNombre());
            campoApellido1.setText(contactoSeleccionado.devuelveApellido1());
            campoApellido2.setText(contactoSeleccionado.devuelveApellido2());
            campoTfno.setText("" + contactoSeleccionado.devuelveTfno());
            campoCorreo.setText(contactoSeleccionado.devuelveCorreo());
        } else {
            panelInfoContacto.setVisible(false);
        }
    }
}
