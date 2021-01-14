/**
 * CalendarioVista.java
 * Vista del calendario
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */
package Vista;

import Control.OyenteVista;
import Modelo.CalendarioModelo;
import Modelo.Contacto;
import Modelo.Identificable;
import Modelo.Recordatorio;
import Modelo.Tupla;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CalendarioVista extends JFrame implements ActionListener, Observer {

    public static final String TEXTO_BOTON_NUEVO = " + ";
    public static final String TEXTO_BOTON_ELIMINAR = " - ";
    public static final String TEXTO_BOTON_ANTERIOR = " < ";
    public static final String TEXTO_BOTON_SIGUIENTE = " > ";
    public static final String SELECCION_DIA = "SELECCION_DIA";
    public static final String SIGUIENTE = "SIGUIENTE";
    public static final String FILTRO = "FILTRO";
    public static final String ANTERIOR = "ANTERIOR";
    public static final String NUEVO_CONTACTO = "NUEVO_CONTACTO";
    public static final String EDITAR_CONTACTO = "EDITAR_CONTACTO";
    public static final String ELIMINAR_CONTACTO = "ELIMINAR_CONTACTO";
    public static final String MOSTRAR_CONTACTOS = "MOSTRAR_CONTACTOS";
    public static final String NUEVO_RECORDATORIO = "NUEVO_RECORDATORIO";
    public static final String NUEVO_RECORDATORIO_EN_DIA
            = "NUEVO_RECORDATORIO_EN_DIA";
    public static final String EDITAR_RECORDATORIO = "EDITAR_RECORDATORIO";
    public static final String ELIMINAR_RECORDATORIO = "ELIMINAR_RECORDATORIO";
    public static final String GUARDAR_RECORDATORIOS = "GUARDAR_RECORDATORIOS";
    public static final String GUARDAR_RECORDATORIOS_COMO
            = "GUARDAR_RECORDATORIOS_COMO";
    public static final String CARGAR_RECORDATORIOS = "CARGAR_RECORDATORIOS";
    public static final String MENU_ITEM_OPCIONES = "MENU_ITEM_OPCIONES";
    public static final String MENU_ITEM_LENGUAJE = "MENU_ITEM_LENGUAJE";
    public static final String MENU_ITEM_ESPANOL = "MENU_ITEM_ESPANOL";
    public static final String MENU_ITEM_INGLES = "MENU_ITEM_INGLES";
    public static final String MENU_ITEM_SALIR = "MENU_ITEM_SALIR";
    public static final String MENU_ITEM_DEBUG = "MENU_ITEM_DEBUG";
    public static final String MENU_ITEM_ACERCA_DE = "MENU_ITEM_ACERCA_DE";
    public static final String MENU_ITEM_CERRAR_SESION = "MENU_ITEM_CERRAR_SESION";

    public static final String GUARDAR_CONTACTOS = "GUARDAR_CONTACTOS";
    public static final String GUARDAR_CONTACTOS_COMO = "GUARDAR_CONTACTOS_COMO";
    public static final String CARGAR_CONTACTOS = "CARGAR_CONTACTOS";

    private static final String TEXTOS_LOCALIZADOS = "/textos_localizados";
    private static final String ICONO_APLICACION = "/icono.png";
    public static final String RUTA_RECURSOS = "/RecursosVista";

    public static final int ABRIR_FICHERO = 0;
    public static final int GUARDAR_FICHERO = 1;
    public static final int OPCION_SI = JOptionPane.YES_OPTION;

    public static final String EXT_FICHERO_RECORDATORIOS = ".rec";
    public static final String EXT_FICHERO_CONTACTOS = ".cont";
    public static final String FILTRO_CALENDARIOS = "FILTRO_CALENDARIOS";
    public static final String FICHERO_TEMAS = "temas.txt";

    private static final int ALTO_ETIQUETA_MES = 20;
    private static final int ANCHO_ETIQUETA_MES = 250;
    private static final float TAMANYO_FUENTE_ETIQUETA_MES = 20f;
    private static final int ALTO_VISTA_RECORDATORIOS = 350;
    private static final int ANCHO_VISTA_RECORDATORIOS = 200;
    private static final int ALTO_VENTANA = 600;
    private static final int ANCHO_VENTANA = 1200;

    private static CalendarioVista instancia = null;
    private static String version;

    private String lenguaje;
    private String pais;

    private JButton nuevoRecordatorio;
    private JButton eliminarRecordatorio;

    private JMenuItem menuRecordatoriosNuevo;
    private JMenuItem menuRecordatoriosEditar;
    private JMenuItem menuRecordatoriosEliminar;
    private JRadioButtonMenuItem radioBotonEspanol;
    private JRadioButtonMenuItem radioBotonIngles;
    private Map<String, JRadioButtonMenuItem> botonesLenguaje = new HashMap<>();

    private CalendarioModelo modelo;
    private MesVista vistaMes;
    private RecordatoriosVista vistaRecordatorios;
    private JLabel mesActual;
    private OyenteVista oyenteVista;
    private DiaVista diaSeleccionado;
    private JComboBox filtroTemas;
    private ContactosVista listaContactos;

    private Localizacion localizacion;
    private ImageIcon icono;

    /**
     * Construye la vista del calendario
     *
     * @param oyenteVista
     * @param version
     * @param calendario
     * @param lenguaje
     * @param pais
     */
    private CalendarioVista(OyenteVista oyenteVista, String version,
            CalendarioModelo calendario, String lenguaje, String pais) {

        this.oyenteVista = oyenteVista;
        this.modelo = calendario;
        this.lenguaje = lenguaje;
        this.pais = pais;
        this.version = version;

        localizacion = Localizacion.devolverInstancia(lenguaje, pais);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(localizacion.devuelve(localizacion.TITULO));
        setLayout(new BorderLayout());

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());

        add(panelNorte, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                oyenteVista.eventoProducido(OyenteVista.Evento.SALIR, null);
            }
        });

        GregorianCalendar hoy = new GregorianCalendar();

        mesActual = new JLabel("", SwingConstants.CENTER);
        mesActual.setPreferredSize(
                new Dimension(ANCHO_ETIQUETA_MES, ALTO_ETIQUETA_MES));
        mesActual.setFont(
                mesActual.getFont().deriveFont(TAMANYO_FUENTE_ETIQUETA_MES));
        ponerNombreMes(hoy);

        creaMenus(panelNorte);

        creaBarraHerramientas(panelNorte);

        vistaRecordatorios = new RecordatoriosVista(this, localizacion);
        JScrollPane js = new JScrollPane(vistaRecordatorios);

        listaContactos = ContactosVista.devolverInstancia(
                this, localizacion, modelo);

        js.setPreferredSize(
                new Dimension(ALTO_VISTA_RECORDATORIOS, ANCHO_VISTA_RECORDATORIOS));
        add(js, BorderLayout.EAST);

        vistaMes = new MesVista(
                hoy.get(Calendar.MONTH), hoy.get(Calendar.YEAR), calendario, this,
                localizacion);
        add(vistaMes, BorderLayout.CENTER);

        icono = new ImageIcon(
                this.getClass().getResource(RUTA_RECURSOS + ICONO_APLICACION));
        setIconImage(icono.getImage());

        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        pack();  // ajusta ventana y sus componentes
        setLocationRelativeTo(null);  // centra en la pantalla   
        setResizable(false);

    }

    public void mostrar(boolean mostrar) {

        if (mostrar) {
            repintarMes();
            listaContactos.listarContactos();
        }
        setVisible(mostrar);
    }

    /**
     * Escribe mensaje con diálogo modal
     *
     */
    public void mensajeDialogo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje,
                localizacion.devuelve(localizacion.TITULO) + " " + version,
                JOptionPane.INFORMATION_MESSAGE, icono);
    }

    /**
     * Cuadro diálogo de confirmación acción
     *
     */
    public int mensajeConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje,
                localizacion.devuelve(localizacion.TITULO) + " " + version,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Devuelve la instancia de la vista del calendario (Es singleton)
     *
     * @param oyenteVista
     * @param version
     * @param modelo
     * @param lenguaje
     * @param pais
     * @return vista del calendario
     */
    public static synchronized CalendarioVista devolverInstancia(
            OyenteVista oyenteVista, String version, CalendarioModelo modelo,
            String lenguaje, String pais) {
        if (instancia == null) {
            instancia = new CalendarioVista(
                    oyenteVista, version, modelo, lenguaje, pais);
        }
        return instancia;
    }

    /**
     * Crea la barra de menús
     *
     * @param panelNorte
     */
    public void creaMenus(JPanel panelNorte) {
        JMenuBar menus = new JMenuBar();

        JMenu menuFichero = new JMenu(localizacion.devuelve(
                localizacion.TEXTO_MENU_FICHERO));
        menus.add(menuFichero);

        JMenu menuOpciones
                = new JMenu(localizacion.devuelve(
                        localizacion.MENU_ITEM_OPCIONES));
        menuOpciones.addActionListener(this);
        menuFichero.add(menuOpciones);

        JMenu menuLenguaje
                = new JMenu(localizacion.devuelve(
                        localizacion.MENU_ITEM_LENGUAJE));
        menuLenguaje.addActionListener(this);
        menuLenguaje.setActionCommand(
                localizacion.devuelve(
                        localizacion.MENU_ITEM_LENGUAJE));

        menuOpciones.add(menuLenguaje);

        ButtonGroup grupoBotonesLenguaje = new ButtonGroup();

        radioBotonEspanol = new JRadioButtonMenuItem(
                localizacion.devuelve(
                        localizacion.MENU_ITEM_ESPANOL));

        radioBotonEspanol.addActionListener(this);
        radioBotonEspanol.setActionCommand(MENU_ITEM_ESPANOL);
        grupoBotonesLenguaje.add(radioBotonEspanol);
        menuLenguaje.add(radioBotonEspanol);
        botonesLenguaje.put(localizacion.LENGUAJE_ESPANOL, radioBotonEspanol);

        radioBotonIngles = new JRadioButtonMenuItem(
                localizacion.devuelve(
                        localizacion.MENU_ITEM_INGLES));
        radioBotonIngles.addActionListener(this);
        radioBotonIngles.setActionCommand(MENU_ITEM_INGLES);
        grupoBotonesLenguaje.add(radioBotonIngles);
        menuLenguaje.add(radioBotonIngles);
        botonesLenguaje.put(localizacion.LENGUAJE_INGLES, radioBotonIngles);

        // seleccionamos botón según lenguaje configurado
        botonesLenguaje.get(lenguaje).setSelected(true);

        menuFichero.addSeparator();

        JMenuItem cerrarSesion
                = new JMenuItem(localizacion.devuelve(
                        localizacion.MENU_ITEM_CERRAR_SESION));
        cerrarSesion.addActionListener(this);
        cerrarSesion.setActionCommand(MENU_ITEM_CERRAR_SESION);
        menuFichero.add(cerrarSesion);
        JMenuItem menuFicheroSalir
                = new JMenuItem(localizacion.devuelve(
                        localizacion.MENU_ITEM_SALIR),
                        localizacion.devuelve(
                                localizacion.ATAJO_MENU_ITEM_SALIR).charAt(0));
        menuFicheroSalir.addActionListener(this);
        menuFicheroSalir.setActionCommand(MENU_ITEM_SALIR);
        menuFichero.add(menuFicheroSalir);

        JMenu menuContactos
                = new JMenu(localizacion.devuelve(
                        localizacion.TEXTO_MENU_CONTACTOS));
        menus.add(menuContactos);

        JMenuItem menuContactosNuevo
                = new JMenuItem(localizacion.devuelve(
                        localizacion.TEXTO_MENU_NUEVO));
        menuContactos.add(menuContactosNuevo);

        menuContactosNuevo.addActionListener(this);
        menuContactosNuevo.setActionCommand(NUEVO_CONTACTO);

        JMenuItem menuContactosMostrar
                = new JMenuItem(localizacion.devuelve(
                        localizacion.TEXTO_MENU_MOSTRAR));
        menuContactosMostrar.addActionListener(this);
        menuContactosMostrar.setActionCommand(MOSTRAR_CONTACTOS);

        menuContactos.add(menuContactosMostrar);

        JMenu menuRecordatorios
                = new JMenu(localizacion.devuelve(
                        localizacion.TEXTO_MENU_RECORDATORIOS));
        menus.add(menuRecordatorios);

        menuRecordatoriosNuevo
                = new JMenuItem(localizacion.devuelve(
                        localizacion.TEXTO_MENU_NUEVO));
        menuRecordatorios.add(menuRecordatoriosNuevo);
        menuRecordatoriosNuevo.addActionListener(this);
        menuRecordatoriosNuevo.setActionCommand(NUEVO_RECORDATORIO);

        menuRecordatoriosEditar
                = new JMenuItem(localizacion.devuelve(
                        localizacion.TEXTO_MENU_EDITAR));
        menuRecordatoriosEditar.setEnabled(false);
        menuRecordatorios.add(menuRecordatoriosEditar);
        menuRecordatoriosEditar.addActionListener(this);
        menuRecordatoriosEditar.setActionCommand(EDITAR_RECORDATORIO);

        menuRecordatoriosEliminar
                = new JMenuItem(localizacion.devuelve(
                        localizacion.TEXTO_MENU_ELIMINAR));
        menuRecordatoriosEliminar.setEnabled(false);
        menuRecordatorios.add(menuRecordatoriosEliminar);
        menuRecordatoriosEliminar.addActionListener(this);
        menuRecordatoriosEliminar.setActionCommand(ELIMINAR_RECORDATORIO);

        JMenu menuAyuda = new JMenu(localizacion.devuelve(
                localizacion.TEXTO_MENU_AYUDA));

        if (oyenteVista.esModoDebug()) {
            JMenuItem menuDebug
                    = new JMenuItem(localizacion.devuelve(
                            localizacion.MENU_ITEM_DEBUG),
                            localizacion.devuelve(
                                    localizacion.ATAJO_ITEM_DEBUG).charAt(0));
            menuDebug.addActionListener(this);
            menuDebug.setActionCommand(MENU_ITEM_DEBUG);
            menuAyuda.add(menuDebug);
            menuAyuda.addSeparator();
        }

        JMenuItem menuAyudaAcercaDe
                = new JMenuItem(localizacion.devuelve(
                        localizacion.MENU_ITEM_ACERCA_DE),
                        localizacion.devuelve(
                                localizacion.ATAJO_ITEM_ACERCA_DE).charAt(0));
        menuAyudaAcercaDe.addActionListener(this);
        menuAyudaAcercaDe.setActionCommand(MENU_ITEM_ACERCA_DE);
        menuAyuda.add(menuAyudaAcercaDe);
        menus.add(menuAyuda);
        panelNorte.add(menus, BorderLayout.NORTH);
        pack();

    }

    /**
     * Crea la barra de herramientas
     *
     * @param panelNorte
     */
    public void creaBarraHerramientas(JPanel panelNorte) {

        JToolBar barra = new JToolBar();

        barra.setFloatable(false);

        panelNorte.add(barra, BorderLayout.SOUTH);

        nuevoRecordatorio = new JButton(TEXTO_BOTON_NUEVO);
        nuevoRecordatorio.setFont(nuevoRecordatorio.getFont().deriveFont(40f));
        nuevoRecordatorio.setEnabled(false);

        nuevoRecordatorio.addActionListener(this);
        nuevoRecordatorio.setActionCommand(NUEVO_RECORDATORIO_EN_DIA);
        eliminarRecordatorio = new JButton(TEXTO_BOTON_ELIMINAR);
        eliminarRecordatorio.setFont(nuevoRecordatorio.getFont());
        eliminarRecordatorio.setEnabled(false);
        eliminarRecordatorio.addActionListener(this);
        eliminarRecordatorio.setActionCommand(ELIMINAR_RECORDATORIO);

        JButton siguiente = new JButton(TEXTO_BOTON_SIGUIENTE);
        siguiente.setFont(nuevoRecordatorio.getFont());
        siguiente.addActionListener(this);
        siguiente.setActionCommand(SIGUIENTE);
        JButton anterior = new JButton(TEXTO_BOTON_ANTERIOR);

        anterior.setFont(nuevoRecordatorio.getFont());

        anterior.addActionListener(this);
        anterior.setActionCommand(ANTERIOR);

        nuevoRecordatorio.setBorderPainted(false);
        eliminarRecordatorio.setBorderPainted(false);
        siguiente.setBorderPainted(false);
        anterior.setBorderPainted(false);

        filtroTemas = new JComboBox();

        filtroTemas.addItem(localizacion.TODO);
        filtroTemas.addItem(localizacion.PERSONAL);
        filtroTemas.addItem(localizacion.TRABAJO);
        filtroTemas.addItem(localizacion.ESTUDIOS);
        filtroTemas.setRenderer(devuelveRenderizador());
        filtroTemas.addActionListener(this);
        filtroTemas.setActionCommand(FILTRO);
        barra.add(nuevoRecordatorio);
        barra.add(eliminarRecordatorio);
        barra.add(anterior);
        barra.add(mesActual);
        barra.add(siguiente);
        barra.add(filtroTemas);
    }

    /**
     * Pasa al siguiente mes
     */
    public void siguienteMes() {
        vistaMes.siguiente();
        ponerNombreMes(new GregorianCalendar(
                vistaMes.devuelveAnyo(), vistaMes.devuelveMes(), 1));
    }

    /**
     * Vuelve a representar el mes
     */
    public void repintarMes() {
        vistaMes.pintaMes();
    }

    /**
     * Pasa al mes anterior
     */
    public void anteriorMes() {
        vistaMes.anterior();
        ponerNombreMes(new GregorianCalendar(
                vistaMes.devuelveAnyo(), vistaMes.devuelveMes(), 1));
    }

    /**
     * Pone el nombre del mes en la barra de herramientas
     *
     * @param dia del que se obtiene el nombre del mes
     */
    public void ponerNombreMes(GregorianCalendar dia) {

        SimpleDateFormat sdf
                = new SimpleDateFormat(localizacion.devuelve(localizacion.FORMATO_FECHA));
        String fechaFormateada = sdf.format(dia.getTime());
        mesActual.setText("    " + fechaFormateada.substring(0, 1).toUpperCase()
                + fechaFormateada.substring(1) + "    ");
    }

    /**
     * Notifica al oyente
     *
     * @param evento
     * @param obj
     */
    public void notificacion(OyenteVista.Evento evento, Object obj) {
        oyenteVista.eventoProducido(evento, obj);
    }

    /**
     * Devuelve la vista de recordatorios
     *
     * @return vistaRecordatorios
     */
    public RecordatoriosVista devuelveVistaRecordatorios() {
        return vistaRecordatorios;
    }

    /**
     * Devuelve la vista del mes
     *
     * @return vistaMes
     */
    public MesVista devuelveVistaMes() {
        return vistaMes;
    }

    /**
     * Devuelve el filtro de temas seleccionado
     *
     * @return filtro
     */
    public String devuelveFiltro() {
        return (String) filtroTemas.getSelectedItem();
    }

    /**
     * Pone los recordatorios del dia seleccionado en la vista de recordatorios
     *
     * @param dia
     */
    public void seleccionDia(DiaVista dia) {
        vistaRecordatorios.limpiar();
        diaSeleccionado = dia;
        new Thread() {
            public boolean resultado;

            @Override
            public void run() {
                List<Recordatorio> lista
                        = modelo.devuelveRecordatoriosEnDia(
                                dia.devuelveFecha(), (String) filtroTemas.getSelectedItem());

                habilitarEvento(OyenteVista.Evento.NUEVO_RECORDATORIO, true);

                for (Recordatorio recordatorio : lista) {
                    vistaRecordatorios.nuevoRecordatorio(recordatorio);
                }
            }
        }.start();

    }

    /**
     * Filtra los recordatorios para que solo se muestren los que cumplan el
     * filtro seleccionado
     *
     * @param filtro a utilizar
     * @param lista a filtrar
     * @return lista filtrada
     */
    public List<Identificable> filtrarRecordatorios(String filtro, List<Identificable> lista) {

        if (!filtro.equals(localizacion.TODO)) {

            List<Identificable> listaFiltrada = new ArrayList();
            for (Identificable recordatorio : lista) {
                if (((Recordatorio) recordatorio).devuelveTema().equals(filtro)) {
                    listaFiltrada.add(recordatorio);
                }
            }
            return listaFiltrada;
        } else {

            return lista;
        }

    }

    /**
     * Selecciona fichero de partida
     *
     * @param operacion guardar o abrir fichero
     * @param extension del fichero
     * @return nombre del fichero seleccionado
     */
    public String seleccionarFichero(int operacion, String extension) {
        String nombreFichero = null;
        int resultado = 0;

        JFileChooser dialogoSeleccionar = new JFileChooser(new File("."));
        FileNameExtensionFilter filtro
                = new FileNameExtensionFilter(FILTRO_CALENDARIOS,
                        extension.substring(1));

        dialogoSeleccionar.setFileFilter(filtro);

        if (operacion == ABRIR_FICHERO) {
            resultado = dialogoSeleccionar.showOpenDialog(this);
        } else {
            resultado = dialogoSeleccionar.showSaveDialog(this);
        }

        if (resultado == JFileChooser.APPROVE_OPTION) {
            nombreFichero = dialogoSeleccionar.getSelectedFile().getName();

            // pone extensión si hace falta al guardar
            if (!nombreFichero.endsWith(extension)
                    && (operacion == GUARDAR_FICHERO)) {
                nombreFichero = nombreFichero + extension;
            }
        }

        return nombreFichero;
    }

    /**
     * Aplica el filtro
     */
    public void aplicarFiltro() {
        repintarMes();
        if (diaSeleccionado != null) {
            seleccionDia(diaSeleccionado);
        }
    }

    /**
     * Cambia al lenguaje indicado y sale del programa
     *
     * @param nuevoLenguaje
     * @param nuevoPais
     */
    private void cambiarLenguaje(String nuevoLenguaje, String nuevoPais) {
        // si opción es distinta de la actual preguntamos cambiar  
        if (!lenguaje.equals(nuevoLenguaje)) {
            // si cambiamos modificamos configuración y salimos  
            if (mensajeConfirmacion(
                    localizacion.devuelve(localizacion.CONFIRMACION_LENGUAJE))
                    == OPCION_SI) {

                oyenteVista.eventoProducido(OyenteVista.Evento.CAMBIAR_LENGUAJE,
                        new Tupla(nuevoLenguaje, nuevoPais));
            } // si no cambiamos volvemos a seleccionar botón del lenguaje actual
            else {
                botonesLenguaje.get(lenguaje).setSelected(true);
            }
        }
    }

    /**
     * Sobreescribe actionPerformed
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        switch (ae.getActionCommand()) {
            case SELECCION_DIA:
                seleccionDia((DiaVista) ae.getSource());
                break;

            case SIGUIENTE:
                siguienteMes();
                break;

            case ANTERIOR:
                anteriorMes();
                break;

            case NUEVO_RECORDATORIO:

                FormularioRecordatorio frNuevo = new FormularioRecordatorio(this,
                        modelo, localizacion, modelo.devuelveUsuario());
                Recordatorio nuevoRecordatorio = frNuevo.devuelveRecordatorio();
                // Si devuelve null es que se ha cancelado la introducción del recordatorio
                if (nuevoRecordatorio != null) {
                    notificacion(OyenteVista.Evento.NUEVO_RECORDATORIO,
                            nuevoRecordatorio);
                }
                break;

            case NUEVO_RECORDATORIO_EN_DIA:

                FormularioRecordatorio frNuevoEnDia
                        = new FormularioRecordatorio(
                                this, modelo, localizacion,
                                diaSeleccionado.devuelveFecha(),
                                modelo.devuelveUsuario());

                Recordatorio nuevoRecordatorioEnDia
                        = frNuevoEnDia.devuelveRecordatorio();
                // Si devuelve null es que se ha cancelado la introducción del recordatorio
                if (nuevoRecordatorioEnDia != null) {
                    notificacion(OyenteVista.Evento.NUEVO_RECORDATORIO,
                            nuevoRecordatorioEnDia);
                }
                break;

            case EDITAR_RECORDATORIO:

                FormularioRecordatorio frEditar
                        = new FormularioRecordatorio(this, modelo, localizacion,
                                vistaRecordatorios.
                                        devuelveRecordatorioSeleccionado(),
                                modelo.devuelveUsuario());
                Recordatorio recordatorioEditado
                        = frEditar.devuelveRecordatorio();
                // Si devuelve null es que se ha cancelado la introducción del recordatorio
                if (recordatorioEditado != null) {
                    notificacion(OyenteVista.Evento.EDITAR_RECORDATORIO,
                            recordatorioEditado);
                }

                break;

            case ELIMINAR_RECORDATORIO:
                notificacion(OyenteVista.Evento.ELIMINAR_RECORDATORIO, null);
                break;

            case NUEVO_CONTACTO:
                FormularioContacto fc = new FormularioContacto(this, localizacion);
                Contacto nuevoContacto = fc.devuelveContacto();
                if (nuevoContacto != null) {
                    notificacion(OyenteVista.Evento.NUEVO_CONTACTO,
                            nuevoContacto);
                }
                break;

            case EDITAR_CONTACTO:

                FormularioContacto fcEd = new FormularioContacto(this,
                        listaContactos.devuelveContacto(), localizacion);
                Contacto contactoEditar = fcEd.devuelveContacto();
                if (contactoEditar != null) {
                    notificacion(OyenteVista.Evento.EDITAR_CONTACTO,
                            contactoEditar);
                }
                break;

            case ELIMINAR_CONTACTO:
                notificacion(OyenteVista.Evento.ELIMINAR_CONTACTO,
                        listaContactos.devuelveContacto());
                break;

            case MOSTRAR_CONTACTOS:
                listaContactos.mostrar();
                break;

            case FILTRO:
                aplicarFiltro();
                break;

            case MENU_ITEM_ESPANOL:
                cambiarLenguaje(
                        localizacion.LENGUAJE_ESPANOL, localizacion.PAIS_ESPANA);
                break;

            case MENU_ITEM_INGLES:
                cambiarLenguaje(
                        localizacion.LENGUAJE_INGLES, localizacion.PAIS_USA);
                break;

            case MENU_ITEM_SALIR:
                oyenteVista.eventoProducido(OyenteVista.Evento.SALIR, null);
                break;

            case MENU_ITEM_DEBUG:
                DebugVista.devolverInstancia().mostrar();
                break;

            case MENU_ITEM_ACERCA_DE:
                JOptionPane.showMessageDialog(this,
                        localizacion.devuelve(localizacion.TITULO) + " " + version + "\n",
                        localizacion.devuelve(localizacion.MENU_ITEM_ACERCA_DE),
                        JOptionPane.INFORMATION_MESSAGE, icono);
                break;

            case MENU_ITEM_CERRAR_SESION:
                oyenteVista.eventoProducido(OyenteVista.Evento.CERRAR_SESION, null);
                break;
        }
    }

    /**
     * Sobreescribe update
     *
     * @param obj
     * @param arg
     */
    @Override
    public void update(Observable obj, Object arg) {

        if (arg instanceof Recordatorio) {
            vistaMes.pintaMes();

            if (diaSeleccionado != null) {
                seleccionDia(diaSeleccionado);

            }
        } else if (arg instanceof Contacto) {
            listaContactos.listarContactos();
        }

    }

    /**
     * Habilita o deshabilita eventos
     *
     * @param evento
     * @param habilitacion
     */
    public void habilitarEvento(OyenteVista.Evento evento, boolean habilitacion) {
        switch (evento) {
            case NUEVO_RECORDATORIO:
                nuevoRecordatorio.setEnabled(habilitacion);
                break;

            case ELIMINAR_RECORDATORIO:
                menuRecordatoriosEliminar.setEnabled(habilitacion);
                eliminarRecordatorio.setEnabled(habilitacion);
                break;

            case EDITAR_RECORDATORIO:
                menuRecordatoriosEditar.setEnabled(habilitacion);
                break;

        }
    }

    /**
     * Devuelve un renderizador
     *
     * @return
     */
    public Renderizador devuelveRenderizador() {
        return new Renderizador();
    }

    /**
     * Clase privada renderizador, extiende DefaultListCellRenderer para cambiar
     * la representación de los elementos.
     */
    private class Renderizador extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            value = localizacion.devuelve((String) value);

            return super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
        }

    }
}
