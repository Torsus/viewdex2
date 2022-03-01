/* @(#) AppFrameInfoDialog.java 08/31/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.app;

import javax.swing.*;


/**
 *
 * @author  sune
 */
public class AppFrameInfoDialog extends javax.swing.JDialog{
    JEditorPane infoPane;
    
    /** Creates a new instance of AdminInfoDialog */
    public AppFrameInfoDialog() {
    }
    
    public AppFrameInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void initComponents(){
        infoPane = new javax.swing.JEditorPane();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        infoPane.setBackground(new java.awt.Color(204, 204, 204));
        infoPane.setBorder(null);
        infoPane.setEditable(false);
        infoPane.setPreferredSize(new java.awt.Dimension(730, 700));
        infoPane.setAutoscrolls(true);
        //this.getContentPane().add(infoPane);
    }
    
     /**
     * Closes the dialog
     * @param evt 
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }
    
}
