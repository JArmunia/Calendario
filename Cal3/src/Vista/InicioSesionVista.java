/**
 * InicioSesionVista.java 
 *
 * @author Javier Armunia Hinojosa
 * @version 14-9-2018
 */
package Vista;

import Control.OyenteVista;
import Modelo.Tupla;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Javie
 */
public class InicioSesionVista extends JDialog {

    public static final int INICIO_SESION = 1;
    public static final int REGISTRO_USUARIO = 2;

    private JPanel panelPrincipal;
    private OyenteVista oyenteVista;
    private Localizacion localizacion;
    private JTextField user;
    private JTextField pass;
    private String usuario;
    private JTextField confirmaPass;
    private JLabel info;
    private GridBagConstraints etiqueta;
    private GridBagConstraints campo;
    private GridBagConstraints etiquetaInfo;
    private GridBagConstraints boton;
    private Tupla<String, String> credenciales;
    private JButton aceptar;
    private JButton secundario;
    private JLabel etiquetaTitulo;

    
    /**
     * Construye una ventana de inicio de sesión / registro
     * @param oyenteVista
     * @param localizacion
     * @param operacion que escoge entre inicio de sesion y registro
     */
    public InicioSesionVista(OyenteVista oyenteVista, Localizacion localizacion,
            int operacion){
        super(new JDialog(), true);
        creaFormulario(oyenteVista, localizacion, operacion);
        setVisible(true);
    }
    
    /**
     * Construye una ventana de inicio de sesión / registro con un mensaje
     * @param oyenteVista
     * @param localizacion
     * @param operacion
     * @param mensaje 
     */
    public InicioSesionVista(OyenteVista oyenteVista, Localizacion localizacion,
            int operacion, String mensaje){
        super(new JDialog(), true);
        creaFormulario(oyenteVista, localizacion, operacion);
        mostrarMensajeInfo(mensaje);
        setVisible(true);
    }
    
    /**
     * Crea el formulario
     * @param oyenteVista
     * @param localizacion
     * @param operacion 
     */
    public void creaFormulario(OyenteVista oyenteVista, Localizacion localizacion,
            int operacion) {

        
        this.oyenteVista = oyenteVista;
        this.localizacion = localizacion;
        etiqueta = new GridBagConstraints();
        campo = new GridBagConstraints();
        etiquetaInfo = new GridBagConstraints();
        boton = new GridBagConstraints();

        etiquetaInfo.gridx = 0;
        etiquetaInfo.gridwidth = 2;
        etiquetaInfo.fill = GridBagConstraints.HORIZONTAL;
        
        etiqueta.anchor = GridBagConstraints.FIRST_LINE_START;
        campo.weightx = 1.0;
        etiqueta.gridx = 0;
        etiqueta.insets = new Insets(10, 10, 10, 10);
        campo.insets = new Insets(0, 0, 0, 10);
        campo.fill = GridBagConstraints.HORIZONTAL;
        campo.gridx = 1;
        boton.insets = new Insets(10, 10, 10, 10);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                oyenteVista.eventoProducido(OyenteVista.Evento.SALIR, null);
            }

        });

        panelPrincipal = new JPanel(new GridBagLayout());
        setTitle(localizacion.devuelve(localizacion.TITULO));
        etiquetaTitulo 
                = new JLabel(localizacion.devuelve(localizacion.INICIO_SESION),
                        SwingConstants.CENTER);
        
        etiquetaTitulo.setFont(etiquetaTitulo.getFont().deriveFont(20f));
        add(etiquetaTitulo, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

        panelPrincipal.add(new Label(localizacion.devuelve(localizacion.USER)), etiqueta);
        panelPrincipal.add(new Label(localizacion.devuelve(localizacion.PASSWORD)), etiqueta);

        user = new JTextField();
        pass = new JPasswordField();

        panelPrincipal.add(user, campo);
        panelPrincipal.add(pass, campo);

        info = new JLabel(" ", SwingConstants.CENTER);

        JPanel panelBotones = new JPanel(new GridBagLayout());

        aceptar
                = new JButton(localizacion.devuelve(localizacion.TEXTO_BOTON_ACEPTAR));
        aceptar.setEnabled(false);
        secundario
                = new JButton();

        if (operacion == INICIO_SESION) {
            creaInicioSesion();
        } else if (operacion == REGISTRO_USUARIO) {
            creaRegistro();
        }
        panelPrincipal.add(info, etiquetaInfo);
        panelBotones.add(aceptar, boton);
        panelBotones.add(secundario, boton);
        add(panelBotones, BorderLayout.SOUTH);
        setLocationRelativeTo(null);

        
    }

    /**
     * Crea el formulario de inicio de sesion
     */
    private void creaInicioSesion() {
        secundario.setText(localizacion.devuelve(localizacion.INICIO_SESION));
        etiquetaTitulo.setText(localizacion.devuelve(localizacion.REGISTRO));
        aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                credenciales
                        = new Tupla<String, String>(user.getText(), pass.getText());
                setVisible(false);
                oyenteVista.eventoProducido(
                        OyenteVista.Evento.INICIAR_SESION, credenciales);

            }
        });
        user.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                aceptar.setEnabled(user.getText().length() != 0);
            }
        });

        secundario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                new InicioSesionVista(oyenteVista, localizacion,
                        InicioSesionVista.REGISTRO_USUARIO);
            }
        });
        setSize(350, 250);
    }

    /**
     * Crea el formulario de registro
     */
    private void creaRegistro() {

        etiquetaTitulo.setText(localizacion.devuelve(localizacion.REGISTRO));
        panelPrincipal.add(new Label(localizacion.devuelve(
                localizacion.CONFIRMA_PASSWORD)), etiqueta);

        secundario.setText(localizacion.devuelve(localizacion.INICIO_SESION));
        secundario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                new InicioSesionVista(oyenteVista, localizacion,
                        InicioSesionVista.INICIO_SESION);
            }
        });

        confirmaPass = new JPasswordField();

        KeyListener kl = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if ((user.getText().length() == 0)
                        || (!pass.getText().equals(confirmaPass.getText()))) {
                    aceptar.setEnabled(false);
                    if (!pass.getText().equals(confirmaPass.getText())) {
                        info.setText(localizacion.devuelve(localizacion.FALLO_PASS));
                    }else{
                        info.setText(" ");
                    }
                } else {
                    info.setText(" ");
                    aceptar.setEnabled(true);
                }

            }
        };
        user.addKeyListener(kl);
        pass.addKeyListener(kl);
        confirmaPass.addKeyListener(kl);
        panelPrincipal.add(confirmaPass, campo);
        
        aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                oyenteVista.eventoProducido(OyenteVista.Evento.REGISTRAR_USUARIO, 
                        new Tupla<String, String>(user.getText(), pass.getText()));
            }
        });

        setSize(350, 350);

    }

    public void mostrarMensajeInfo(String informacion) {
        info.setText(informacion);
    }

  
   
}
