/* @(#) AppFrameBranding.java 8/31/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.app;

import info.clearthought.layout.TableLayout;
import java.awt.*;
import javax.swing.*;


/**
 *
 * @author  sune
 */
public class AppFrameBranding extends JPanel{
    
    /** Creates a new instance of AppFrameBranding */
    public AppFrameBranding() {
        createUI();
    }
    
    /**
      * Creates the GUI.
     */
    public void createUI(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        double sizea[][] = {{f,p,f},{f,27,f}};
        this.setLayout(new TableLayout(sizea));
        
        //TitledBorder tborder = BorderFactory.createTitledBorder("");
        //tborder.setTitleJustification(TitledBorder.LEFT);
        //tborder.setTitlePosition(1);
        //tborder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        //this.setBorder(tborder);
        //setBackground(new java.awt.Color(124,0,0));
        
        // brandLAbel
        JLabel brandLabel= new JLabel("ExperimentalMedicalViewer");
        brandLabel.setBackground(new Color(128,0,0));
        brandLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        this.add(brandLabel, "1,1");
       
    }
}
