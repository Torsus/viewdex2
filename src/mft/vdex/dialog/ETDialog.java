/* @(#) AboutDialog.java 01/24/2006
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.dialog;

import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import mft.vdex.app.AppProperty;

/**
 *
 * @author sune
 */
public class ETDialog extends JDialog{
    AppProperty appProp;
    JTextPane detailTextPane;
    JTextPane detailTextPane2;
    JTextPane contactTextPane;
    JTextPane licenseTextPane;
    String productVersion;
    
    /** Creates a new instance of AboutDialog */
    public ETDialog(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        //this.appProp = prop;
        //this.productVersion = ver;
        createUI();
    }
    
    private void createUI(){
        JPanel mainPanel = createMainPanel();
        getContentPane().add(mainPanel);
        pack();
        
        // closeDialog
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
    }
    
    /**
     * Create the Detail panel.
     */
    private JPanel createMainPanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230,230,230));
        double size[][] = {{f},{f,p,f}};
        panel.setLayout(new TableLayout(size));
        
        return panel;
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }
    
    /**
     * Gets font scaled for screen resolution
     * @param fontName              Logical font name i.e. SansSerif
     * @param fontStyle             Font class style defines
     * @param pointSizeFor1280Mode  How big in 1280 * 1024 resolution
     * @return                      The scaledFont value
     */
    public Font getScaledFont(String fontName, int fontStyle, int pointSizeFor1280Mode){
        Font f = new Font(fontName, fontStyle, pointSizeFor1280Mode);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if(d.height == 1024)
            return f;
        else{
            int numerator = pointSizeFor1280Mode * d.height;
            float sizeForCurrentResolution = (float)numerator/1024;
            return f.deriveFont(sizeForCurrentResolution);
        }
    }
}