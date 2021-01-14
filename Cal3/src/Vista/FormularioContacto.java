/**
 * FormularioContactos.java
 * Formulario para crear o ediatar contactos
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import Modelo.Contacto;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Javie
 */
public class FormularioContacto extends JDialog {

    public static final int NUEVO_CONTACTO = 1;
    public static final int EDITAR_CONTACTO = 2;

    private static final String REGEX_NUMEROS = "^[0-9]*$";
    private static final int LARGO_FORMULARIO = 300;
    private static final int ANCHO_FORMULARIO = 500;

    private CalendarioVista vista;
    private Localizacion localizacion;
    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoApellido1;
    private JTextField campoApellido2;
    private JTextField campoTfno;
    private JTextField campoCorreo;
    private JButton aceptar;
    private JButton cancelar;
    private Contacto contactoFinalizado;

    /**
     * Construye el formulario de contacto
     *
     * @param vista
     */
    public FormularioContacto(CalendarioVista vista, Localizacion localizacion) {
        super(vista, true);
        this.vista = vista;
        this.localizacion = localizacion;
        construyeFormulario(NUEVO_CONTACTO);

        setVisible(true);
    }

    /**
     * Construye el formulario de contacto para editar el contacto seleccionado
     *
     * @param vista
     * @param contacto a editar
     */
    public FormularioContacto(CalendarioVista vista, Contacto contacto,
            Localizacion localizacion) {
        super(vista, true);
        this.localizacion = localizacion;
        this.vista = vista;
        construyeFormulario(EDITAR_CONTACTO);
        ponerValoresPorDefecto(contacto);

        setVisible(true);

    }

    /**
     * Construye el formulario según la opción seleccionada
     *
     * @param opcion nuevo contacto o editar
     */
    private void construyeFormulario(int opcion) {
        setLayout(new BorderLayout());
        setTitle(localizacion.devuelve(localizacion.TITULO_FORMULARIO_CONTACTO));
        JPanel formulario = new JPanel(new GridBagLayout());
        add(formulario, BorderLayout.CENTER);
        setSize(ANCHO_FORMULARIO, LARGO_FORMULARIO);
        GridBagConstraints etiqueta = new GridBagConstraints();
        GridBagConstraints campo = new GridBagConstraints();
        etiqueta.anchor = GridBagConstraints.FIRST_LINE_START;

        campo.weightx = 1.0;
        etiqueta.gridx = 0;
        etiqueta.insets = new Insets(10, 10, 10, 10);
        campo.insets = new Insets(0, 0, 0, 10);
        campo.fill = GridBagConstraints.HORIZONTAL;
        campo.gridx = 1;

        campoId = new JTextField();
        campoNombre = new JTextField();
        campoApellido1 = new JTextField();
        campoApellido2 = new JTextField();
        campoTfno = new JTextField();
        campoCorreo = new JTextField();
        campoTfno.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                Pattern regex = Pattern.compile(REGEX_NUMEROS);
                Matcher matcher = regex.matcher(campoTfno.getText());

                aceptar.setEnabled(matcher.matches());
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                Pattern regex = Pattern.compile(REGEX_NUMEROS);
                Matcher matcher = regex.matcher(campoTfno.getText());

                aceptar.setEnabled(matcher.matches());
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                
            }
        });

        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_ID)),
                etiqueta);

        formulario.add(campoId, campo);
        if (opcion == EDITAR_CONTACTO) {
            campoId.setEnabled(false);
        }

        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_NOMBRE)),
                etiqueta);
        formulario.add(campoNombre, campo);
        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_APELLIDO_1)),
                etiqueta);
        formulario.add(campoApellido1, campo);
        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_APELLIDO_2)),
                etiqueta);
        formulario.add(campoApellido2, campo);
        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_TELEFONO)),
                etiqueta);
        formulario.add(campoTfno, campo);
        formulario.add(new JLabel(localizacion.devuelve(localizacion.TEXTO_CORREO)),
                etiqueta);
        formulario.add(campoCorreo, campo);

        JPanel panelBotones = new JPanel();
        aceptar = new JButton(localizacion.devuelve(localizacion.TEXTO_BOTON_ACEPTAR));
        aceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String tfno;
                if (campoTfno.getText().equals("")) {
                    tfno = "0";
                } else {
                    tfno = campoTfno.getText();
                }

                contactoFinalizado = new Contacto(campoId.getText(),
                        campoNombre.getText(), campoApellido1.getText(),
                        campoApellido2.getText(), Integer.parseInt(tfno),
                        campoCorreo.getText());
                setVisible(false);

            }
        });

        cancelar = new JButton(localizacion.devuelve(localizacion.TEXTO_BOTON_CANCELAR));
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
            }
        });
        panelBotones.add(aceptar);
        panelBotones.add(cancelar);
        add(panelBotones, BorderLayout.SOUTH);
        setLocationRelativeTo(null);

    }

    /**
     * Pone la información del contacto en los campos del formulario
     *
     * @param contacto
     */
    private void ponerValoresPorDefecto(Contacto contacto) {
        campoId.setText(contacto.devuelveId());
        campoNombre.setText(contacto.devuelveNombre());
        campoApellido1.setText(contacto.devuelveApellido1());
        campoApellido2.setText(contacto.devuelveApellido2());
        campoTfno.setText("" + contacto.devuelveTfno());
        campoCorreo.setText(contacto.devuelveCorreo());
    }

    /**
     * Devuelve el contacto finalizado
     *
     * @return contactoFinalizado
     */
    public Contacto devuelveContacto() {
        return contactoFinalizado;
    }
}
