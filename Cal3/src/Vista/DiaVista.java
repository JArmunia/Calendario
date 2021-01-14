/**
 * DiaVista.java
 * Vista del dia
 *
 * @author Javier Armunia Hinojosa
 * @version 25-8-2018
 */
package Vista;

import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JButton;



public class DiaVista extends JButton{
    
    private GregorianCalendar fecha;
    private boolean tieneRecordatorio = false;
    
    private static final float FUENTE = 50f;
    
    /**
     * Construye la vista del dia
     * 
     * @param dia que representa
     */
    public DiaVista(GregorianCalendar dia){
        this.fecha = dia;
        setText(dia.get(Calendar.DAY_OF_MONTH)+"");
        darEstiloBoton();        
    }
    
    /**
     * Construye un dia vacio
     */
    public DiaVista(){        
        darEstiloBoton();       
    }
    
    /**
     * Da estilo al botón 
     */
    public void darEstiloBoton(){
        
        setFont(getFont().deriveFont(FUENTE));
        setBorderPainted(false);
        setBackground(Color.white);
        setFont(getFont().deriveFont(Font.PLAIN));
    }
    
    /**
     * Marca el dia si tiene recordatorio, lo desmarca si no.
     * 
     * @param tieneRecordatorio 
     */
    public void tieneRecordatorio(boolean tieneRecordatorio){
        this.tieneRecordatorio = tieneRecordatorio;
        if (tieneRecordatorio){
            setBackground(Color.LIGHT_GRAY);
        }else{
            setBackground(Color.white);
        }
    }
    
    /**
     * Devuelve si el día tiene recordatorio
     * 
     * @return tieneRecordatorio
     */
    public boolean devuelveTieneRecordatorio(){
        return tieneRecordatorio;
    }
    
    /**
     * Devuelve la fecha del día
     * 
     * @return fecha
     */
    public GregorianCalendar devuelveFecha(){
        return fecha;
    }
    
    /**
     * Configura un día vacio
     */
    public void nuevoDia(){
        setText("");
        setEnabled(false);
        tieneRecordatorio(false);
        setBorderPainted(false);
    }
    
    /**
     * Configura un día
     * 
     * @param fecha
     * @param tieneRecordatorio 
     */
    public void nuevoDia(GregorianCalendar fecha, Boolean tieneRecordatorio){
        this.fecha = fecha;
        setText(fecha.get(Calendar.DAY_OF_MONTH)+"");
        setEnabled(true);
        tieneRecordatorio(tieneRecordatorio);
    }
    
    /**
     * Marca el día si es hoy
     * 
     * @param hoy 
     */
    public void esHoy(Boolean hoy){
        
        this.setBorderPainted(hoy);
        if (hoy){
            setFont(getFont().deriveFont(Font.BOLD));
        }
        else{
            setFont(getFont().deriveFont(Font.PLAIN));
        }        
    }    
}
