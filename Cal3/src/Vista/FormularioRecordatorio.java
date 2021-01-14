/**
 * FormularioRecordatorios.java
 * Formulario para crear o editar recordatorios
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */
package Vista;

import Modelo.CalendarioModelo;
import Modelo.Contacto;
import Modelo.Recordatorio;
import Modelo.Tupla;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FormularioRecordatorio extends JDialog {

    private static final int ANCHO_ZONA_TEXTO = 500;
    private static final int ALTO_ZONA_TEXTO = 400;

    public static final int NUEVO_RECORDATORIO = 0;
    public static final int RECORDATORIO_CON_FECHA = 1;
    public static final int EDITAR_RECORDATORIO = 2;

    private JSpinner spinnerHora;
    private JSpinner spinnerMinuto;
    private JSpinner spinnerDia;
    private JSpinner spinnerMes;
    private JSpinner spinnerAnyo;

    private CalendarioVista vista;
    private JComboBox seleccionTema;
    private Localizacion localizacion;
    private JTextArea areaTexto;
    private JPanel formulario;
    private Recordatorio recordatorioFinalizado = null;
    private GregorianCalendar fecha;
    private int tipoFormulario;
    private String usuario;
    private DefaultListModel<Tupla<Contacto, String>> modeloSincronizados;
    private JList listaSincronizados;
    //private List<Tupla<Contacto, String>> contactosSincronizados;
    private CalendarioModelo modelo;
    private JButton sincronizar;
    private JButton eliminar;
    private JButton cambiaPermisos;
    private JButton aceptar;
    private JButton cancelar;
    private JLabel info;

    /**
     * Construye el formulario con selección de fecha y hora
     *
     * @param vista
     */
    public FormularioRecordatorio(
            CalendarioVista vista, CalendarioModelo modelo,
            Localizacion localizacion, String usuario) {
        super(vista, true);
        this.modelo = modelo;
        this.usuario = usuario;
        this.localizacion = localizacion;
        fecha = new GregorianCalendar();
        modeloSincronizados = new DefaultListModel<Tupla<Contacto, String>>();
        this.vista = vista;
        construyeFormulario(NUEVO_RECORDATORIO);
        setVisible(true);
    }

    /**
     * Construye el formulario con selección de hora, usa el día pasado por
     * parámetro como fecha
     *
     * @param vista
     * @param dia seleccionado
     */
    public FormularioRecordatorio(
            CalendarioVista vista, CalendarioModelo modelo, Localizacion localizacion,
            GregorianCalendar dia, String usuario) {

        super(vista, true);
        this.modelo = modelo;
        this.usuario = usuario;
        this.localizacion = localizacion;
        fecha = dia;
        this.vista = vista;
        modeloSincronizados = new DefaultListModel<Tupla<Contacto, String>>();
        construyeFormulario(RECORDATORIO_CON_FECHA);
        setVisible(true);
    }

    /**
     * Construye el formulario sin selección de fecha ni hora, estas las obtiene
     * del recordatorio pasado por parámetro.
     *
     * @param vista
     * @param recordatorio a editar
     */
    public FormularioRecordatorio(
            CalendarioVista vista, CalendarioModelo modelo, Localizacion localizacion,
            Recordatorio recordatorio, String usuario) {

        super(vista, true);
        this.modelo = modelo;
        this.usuario = usuario;
        this.localizacion = localizacion;
        fecha = recordatorio.devuelveFecha();
        this.vista = vista;
        modeloSincronizados = new DefaultListModel<Tupla<Contacto, String>>();
        for (Tupla<Contacto, String> tupla : recordatorio.devuelveSincronizados()) {
            modeloSincronizados.addElement(tupla);
        }
        recordatorioFinalizado = recordatorio;
        construyeFormulario(EDITAR_RECORDATORIO);
        ponerTemaYTexto(recordatorio);

        habilitar(recordatorio.devuelvePermisos().equals(Recordatorio.LECTURA_ESCRITURA));

        setVisible(true);

    }

    /**
     * Construye los elementos del formulario
     *
     * @param tipoFormulario: Esta opción indica si va a haber selección de
     * fecha y selección de hora, solo de hora o ninguna.
     */
    public void construyeFormulario(int tipoFormulario) {
        setTitle(localizacion.devuelve(localizacion.TITULO_FORMULARIO_RECORDATORIO));
        this.tipoFormulario = tipoFormulario;
        JPanel panelSeleccionFechaYHora;
        JPanel panelPrincipal;
        JPanel panelTemaYTexto;
        JPanel panelBotones;
        SimpleDateFormat sdf = new SimpleDateFormat();

        formulario = new JPanel(new BorderLayout());
        add(formulario);

        panelSeleccionFechaYHora = new JPanel();
        formulario.add(panelSeleccionFechaYHora, BorderLayout.NORTH);

        if (tipoFormulario == NUEVO_RECORDATORIO) {
            panelSeleccionFechaYHora.add(
                    creaSeleccionFecha(new GregorianCalendar()));
        } else {
            sdf.applyPattern(localizacion.devuelve(localizacion.FORMATO_FECHA_RECORDATORIO));
            panelSeleccionFechaYHora.add(new JLabel(sdf.format(fecha.getTime())));
        }
        if (tipoFormulario == NUEVO_RECORDATORIO
                || tipoFormulario == RECORDATORIO_CON_FECHA) {
            panelSeleccionFechaYHora.add(creaSeleccionHora(
                    new GregorianCalendar()));
        } else {
            sdf.applyPattern(localizacion.devuelve(localizacion.FORMATO_HORA_RECORDATORIO));
            panelSeleccionFechaYHora.add(new JLabel(sdf.format(fecha.getTime())));
        }
        panelPrincipal = new JPanel(new GridLayout(1, 2));
        panelTemaYTexto = new JPanel(new BorderLayout());
        formulario.add(panelPrincipal, BorderLayout.CENTER);
        panelPrincipal.add(panelTemaYTexto);
        seleccionTema = new JComboBox();

        seleccionTema.setRenderer(vista.devuelveRenderizador());
        seleccionTema.addItem(localizacion.TODO);
        seleccionTema.addItem(localizacion.PERSONAL);
        seleccionTema.addItem(localizacion.TRABAJO);
        seleccionTema.addItem(localizacion.ESTUDIOS);

        panelTemaYTexto.add(seleccionTema, BorderLayout.NORTH);
        panelTemaYTexto.add(creaAreaTexto(), BorderLayout.CENTER);

        panelPrincipal.add(creaListaSincronizados());

        panelBotones = new JPanel();
        aceptar = new JButton(
                localizacion.devuelve(localizacion.TEXTO_BOTON_ACEPTAR));
        if (tipoFormulario == EDITAR_RECORDATORIO
                && recordatorioFinalizado.devuelvePermisos().equals(Recordatorio.LECTURA)) {
            aceptar.setEnabled(false);
        }
        cancelar = new JButton(
                localizacion.devuelve(localizacion.TEXTO_BOTON_CANCELAR));

        aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                List<Tupla<Contacto, String>> contactosSincronizados
                        = new ArrayList<>();
                for (Object objeto : modeloSincronizados.toArray()) {
                    Tupla<Contacto, String> tupla
                            = (Tupla<Contacto, String>) objeto;
                    contactosSincronizados.add(tupla);
                }

                recordatorioFinalizado = new Recordatorio(usuario,
                        devuelveFecha(), devuelveTema(), devuelveTexto(),
                        Recordatorio.LECTURA_ESCRITURA, contactosSincronizados);
                setVisible(false);
            }
        });

        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
            }
        });
        JPanel panelInfoYBotones = new JPanel(new BorderLayout());
        formulario.add(panelInfoYBotones, BorderLayout.SOUTH);
        info = new JLabel("", SwingConstants.CENTER);
        panelInfoYBotones.add(info, BorderLayout.NORTH);
        panelBotones.add(aceptar);
        panelBotones.add(cancelar);
        panelInfoYBotones.add(panelBotones, BorderLayout.SOUTH);
        setSize(new Dimension(ANCHO_ZONA_TEXTO, ALTO_ZONA_TEXTO));

        setLocationRelativeTo(null);

    }

    /**
     * Crea la selección de fecha
     *
     * @param dia
     * @return JPanel con selección de fecha
     */
    public JPanel creaSeleccionFecha(GregorianCalendar dia) {
        JPanel panelFecha = new JPanel();

        spinnerDia = creaSpinner(1, dia.
                getMaximum(Calendar.DAY_OF_MONTH) + 1);

        spinnerMes = creaSpinner(1, 13);
        spinnerMes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                //Guardo el dia que estaba seleccionado
                int diaSeleccionado = (int) spinnerDia.getValue();
                GregorianCalendar mesSeleccionado = new GregorianCalendar(
                        (int) spinnerAnyo.getValue(),
                        ((int) spinnerMes.getValue()) - 1, 1);

                //Obtenemos los dias que tiene el mes seleccionado y cambiamos el 
                //modelo del spinner
                int max = mesSeleccionado.getActualMaximum(Calendar.DAY_OF_MONTH);

                List elementos = new ArrayList();

                for (int i = 1; i <= max; i++) {
                    elementos.add(i);
                }
                SpinnerListModel modelo = new SpinnerListModel(elementos);

                spinnerDia.setModel(modelo);

                //Si el dia seleccionado era mayor que el numero de dias del mes,
                //se cambia por el ultimo dia del mes, si no se deja el dia que estaba.
                if (diaSeleccionado > max) {
                    spinnerDia.setValue(max);
                } else {
                    spinnerDia.setValue(diaSeleccionado);
                }
            }
        });

        spinnerAnyo = creaSpinner(dia.get(Calendar.YEAR),
                dia.get(Calendar.YEAR) + 10);

        spinnerAnyo.setValue(dia.get(Calendar.YEAR));
        spinnerMes.setValue(dia.get(Calendar.MONTH) + 1);
        spinnerDia.setValue(dia.get(Calendar.DAY_OF_MONTH));

        panelFecha.add(new JLabel(
                localizacion.devuelve(localizacion.ETIQUETA_FECHA)));
        panelFecha.add(spinnerDia);
        panelFecha.add(spinnerMes);
        panelFecha.add(spinnerAnyo);

        return panelFecha;
    }

    /**
     * Crea la selección de hora
     *
     * @param dia
     * @return JPanel con selección de hora
     */
    public JPanel creaSeleccionHora(GregorianCalendar dia) {
        JPanel panelHora = new JPanel();
        spinnerHora = creaSpinner(0, 24);
        spinnerMinuto = creaSpinner(0, 60);
        panelHora.add(new JLabel(
                localizacion.devuelve(localizacion.ETIQUETA_HORA)), BorderLayout.WEST);
        spinnerHora.setValue(dia.get(Calendar.HOUR_OF_DAY));
        spinnerMinuto.setValue(dia.get(Calendar.MINUTE));
        panelHora.add(spinnerHora);
        panelHora.add(spinnerMinuto);
        return panelHora;
    }

    /**
     * Crea el area de texto para poner el texto del recordatorio
     *
     * @return JTextArea areaTexto
     */
    public JTextArea creaAreaTexto() {
        areaTexto = new JTextArea(
                localizacion.devuelve(localizacion.TEXTO_DEFECTO_RECORDATORIO));
        areaTexto.setLineWrap(true);
        areaTexto.addFocusListener(new FocusListener() {
            private boolean textoPorDefecto = true;

            @Override
            public void focusGained(FocusEvent fe) {
                if (areaTexto.getText().equals(
                        localizacion.devuelve(localizacion.TEXTO_DEFECTO_RECORDATORIO))
                        && textoPorDefecto) {
                    areaTexto.setText("");
                    // De esta forma no borra si escribes lo mismo que en el texto por defecto
                    textoPorDefecto = false;
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (areaTexto.getText().equals("")) {
                    areaTexto.setText(
                            localizacion.devuelve(localizacion.TEXTO_DEFECTO_RECORDATORIO));
                    textoPorDefecto = true;
                }
            }
        });
        return areaTexto;
    }

    /**
     * Crea la vista de la lista de usuarios sincronizados
     * @return 
     */
    public JPanel creaListaSincronizados() {

        JPanel panelSincronizacion = new JPanel(new BorderLayout());
        sincronizar = new JButton(
                localizacion.devuelve(localizacion.SINCRONIZAR));
        eliminar = new JButton(
                localizacion.devuelve(localizacion.TEXTO_MENU_ELIMINAR));
        cambiaPermisos = new JButton(
                localizacion.devuelve(localizacion.CAMBIAR_PERMISOS));

        JPanel panelBotones = new JPanel();
        panelSincronizacion.add(panelBotones, BorderLayout.SOUTH);
        JDialog ventana = this;

        listaSincronizados = new JList(modeloSincronizados);

        panelSincronizacion.add(
                new JScrollPane(listaSincronizados), BorderLayout.CENTER);

        panelSincronizacion.add(sincronizar, BorderLayout.NORTH);

        sincronizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ListaConBusqueda lista = new ListaConBusqueda(ventana);
                Contacto contacto = lista.devuelveContacto();
                if (contacto != null) {

                    modeloSincronizados.addElement(
                            new Tupla<Contacto, String>(contacto, Recordatorio.LECTURA));
                }
            }
        });

        listaSincronizados.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Tupla<Contacto, String> tupla = (Tupla<Contacto, String>) value;
                String tituloContacto
                        = tupla.a.devuelveId()
                        + " " + localizacion.devuelve(localizacion.TEXTO_PERMISOS)
                        + " " + localizacion.devuelve(tupla.b);

                return super.getListCellRendererComponent(
                        list, tituloContacto, index, isSelected, cellHasFocus);
            }
        });

        panelBotones.add(cambiaPermisos);
        cambiaPermisos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Tupla<Contacto, String> tupla
                        = (Tupla<Contacto, String>) listaSincronizados.getSelectedValue();

                if (tupla.b.equals(Recordatorio.LECTURA)) {
                    Tupla<Contacto, String> nuevaTupla
                            = new Tupla<>(tupla.a, Recordatorio.LECTURA_ESCRITURA);
                    modeloSincronizados.removeElement(tupla);
                    modeloSincronizados.addElement(nuevaTupla);

                } else {
                    Tupla<Contacto, String> nuevaTupla
                            = new Tupla<>(tupla.a, Recordatorio.LECTURA);
                    modeloSincronizados.removeElement(tupla);
                    modeloSincronizados.addElement(nuevaTupla);

                }
            }
        });
        panelBotones.add(eliminar);

        eliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Tupla<Contacto, String> tupla
                        = (Tupla<Contacto, String>) listaSincronizados.getSelectedValue();
                modeloSincronizados.removeElement(tupla);

            }
        });

        return panelSincronizacion;

    }

    /**
     * Pone los valores por defecto a la fecha
     *
     * @param dia
     */
    public void ponerValoresFecha(GregorianCalendar dia) {
        spinnerAnyo.setValue(dia.get(Calendar.YEAR));
        spinnerDia.setValue(dia.get(Calendar.DAY_OF_MONTH));
        spinnerMes.setValue(dia.get(Calendar.MONTH) + 1);
    }

    /**
     * Pone los valores por defecto a la hora
     *
     * @param dia
     */
    public void ponerValoresHora(GregorianCalendar dia) {
        spinnerHora.setValue(dia.get(Calendar.HOUR));
        spinnerMinuto.setValue(dia.get(Calendar.MINUTE));
    }

    /**
     * Pone el valor del tema y del texto
     *
     * @param recordatorio
     */
    public void ponerTemaYTexto(Recordatorio recordatorio) {
        seleccionTema.setSelectedItem(recordatorio.devuelveTema());
        areaTexto.setText(recordatorio.devuelveTexto());
    }

    /**
     * Crea un nuevo spinner con los números proporcionados como valor máximo y
     * mínimo
     *
     * @param min
     * @param max
     * @return nuevo spinner
     */
    public JSpinner creaSpinner(int min, int max) {
        List elementos = new ArrayList();
        for (int i = min; i < max; i++) {
            elementos.add(i);
        }
        SpinnerListModel modelo = new SpinnerListModel(elementos);
        return new JSpinner(modelo);
    }

    /**
     * Devuelve el texto del campo de texto
     *
     * @return texto
     */
    public String devuelveTexto() {
        return areaTexto.getText();
    }

    /**
     * Devuelve el tema seleccionado
     *
     * @return tema
     */
    public String devuelveTema() {
        return (String) seleccionTema.getSelectedItem();
    }

    /**
     * Devuelve la fecha seleccionada
     *
     * @return fecha
     */
    public GregorianCalendar devuelveFecha() {
        if (tipoFormulario == NUEVO_RECORDATORIO) {
            return new GregorianCalendar(
                    (int) spinnerAnyo.getValue(), ((int) spinnerMes.getValue()) - 1,
                    (int) spinnerDia.getValue(), (int) spinnerHora.getValue(),
                    (int) spinnerMinuto.getValue());
        } else if (tipoFormulario == RECORDATORIO_CON_FECHA) {
            fecha.set(Calendar.HOUR_OF_DAY, (int) spinnerHora.getValue());
            fecha.set(Calendar.MINUTE, (int) spinnerMinuto.getValue());
        }
        return fecha;

    }

    /**
     * Devuelve el recordatorio finalizado
     *
     * @return recordatorioFinalizado
     */
    public Recordatorio devuelveRecordatorio() {

        return recordatorioFinalizado;
    }

    /**
     * Cierra la ventana
     */
    public void cerrarVentana() {
        setVisible(false);
    }

    public void habilitar(boolean habilitacion) {
        seleccionTema.setEnabled(habilitacion);
        areaTexto.setEnabled(habilitacion);
        sincronizar.setEnabled(habilitacion);
        cambiaPermisos.setEnabled(habilitacion);
        aceptar.setEnabled(habilitacion);
        eliminar.setEnabled(habilitacion);
        if (!habilitacion) {
            mostrarInfo(localizacion.devuelve(localizacion.SIN_PERMISOS));
        }

    }

    /**
     * Muestra un mensaje en la ventana
     * @param informacion 
     */
    public void mostrarInfo(String informacion) {
        info.setText(informacion);
    }

    /**
     * Clase privada que crea una lista de contactos con busqueda dinámica
     */
    private class ListaConBusqueda extends JDialog {

        private JTextField busqueda;
        private JList<Contacto> listaContactos;
        private DefaultListModel<Contacto> modeloContactos;
        private Contacto contactoEscogido = null;

        public ListaConBusqueda(JDialog ventana) {
            super(ventana, true);
            setLayout(new BorderLayout());

            busqueda = new JTextField();
            modeloContactos = listarContactos();
            listaContactos = new JList<>(listarContactos());
            busqueda.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {

                    String text = busqueda.getText();
                    if (text.trim().length() > 0) {

                        DefaultListModel<Contacto> tmp
                                = new DefaultListModel<Contacto>();
                        for (int i = 0; i < modeloContactos.getSize(); i++) {

                            if (modeloContactos.getElementAt(i).devuelveId()
                                    .toLowerCase().contains(text.toLowerCase())) {
                                tmp.addElement(modeloContactos.getElementAt(i));
                            }
                        }
                        //agrega nuevo modelo a JList
                        listaContactos.setModel(tmp);
                    } else {//si esta vacio muestra el Model original
                        listaContactos.setModel(modeloContactos);
                    }
                }
            });
            add(busqueda, BorderLayout.NORTH);
            add(listaContactos, BorderLayout.CENTER);

            JButton aceptar
                    = new JButton(
                            localizacion.devuelve(Localizacion.TEXTO_BOTON_ACEPTAR));
            aceptar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    contactoEscogido = listaContactos.getSelectedValue();
                    setVisible(false);
                }
            });
            add(aceptar, BorderLayout.SOUTH);

            listaContactos.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList list, Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {

                    String tituloContacto
                            = ((Contacto) value).devuelveId();

                    return super.getListCellRendererComponent(
                            list, tituloContacto, index, isSelected, cellHasFocus);
                }
            });
            pack();
            setLocationRelativeTo(null);
            setVisible(true);

        }

        /**
         * Lista los contactos
         *
         */
        public DefaultListModel<Contacto> listarContactos() {
            DefaultListModel<Contacto> listaModelo = new DefaultListModel();
            for (Map.Entry<String, Contacto> entrada
                    : modelo.devuelveContactos().entrySet()) {

                listaModelo.addElement(entrada.getValue());
                listaModelo.get(listaModelo.getSize() - 1);
            }

            return listaModelo;
        }

        /**
         * Devuelve el contacto escogido
         * @return 
         */
        public Contacto devuelveContacto() {
            return contactoEscogido;
        }

    }
}
