/* @(#) LoginDialog.java 01/28/2003
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
import java.awt.*;
import javax.swing.*;
import mft.vdex.app.AppMainLogin;
import mft.vdex.app.ViewDex;


public class LoginDialog extends javax.swing.JDialog {
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    protected java.awt.Frame parent;
    private String[] users = new String[100];
    protected ViewDex viewDEX;
    AppMainLogin appMainLogin;
    
    /**
     * Constructor.
     *
     */
    public LoginDialog(){
        initComponents();
    }
    
    /**
     * Constructor.
     *
     */
    public LoginDialog(ViewDex viewdex,  boolean modal){
        super(viewdex, modal);
        this.viewDEX = viewdex;
        initComponents();
    }
    
    /**
     * Constructor.
     *
     */
    public LoginDialog(AppMainLogin appmainlogin, ViewDex viewdex, boolean modal){
        super(viewdex, modal);
        this.viewDEX = viewdex;
        this.users = users;
        this.appMainLogin = appmainlogin;
        initComponents();
    }
    
    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents(){
        int y1, y2, y4, y5;
        
        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        int sizeY = Math.round(70 * yScale);
        
        //System.out.println("xScale: " + xScale);
        //System.out.println("yScale: " + yScale);
        
        int x1 = Math.round(35 * xScale);
        int x2 = Math.round(180 * xScale);
        int x3 = Math.round(30 * xScale);
        int x4 = Math.round(40 * xScale);
        int x5 = Math.round(10 * xScale);
        
        y1 = Math.round(10 * yScale);
        y2 = Math.round(25 * yScale);
        int y3 = Math.round(23 * yScale);
        y4 = Math.round(8 * yScale);
        y5 = Math.round(30 * yScale);
        
        //if(xMedicalViewer.getRunMode() == "EXTENDED"){
        if(false){
            y1 = Math.round(8 * yScale);
            y2 = Math.round(22 * yScale);
            y4 = Math.round(4 * yScale);
            y5 = Math.round(28 * yScale);
        }
           
        // Panel
        pwPanel = new javax.swing.JPanel();
        double size[][] = {{x1,x2,x3,x4,x5}, {y1,y2,y3,y4,y5,y1}};
        pwPanel.setLayout(new TableLayout(size));
        
        // Password
        passwordField = new javax.swing.JPasswordField();
        pwLabel = new javax.swing.JLabel();
        //okButton = new javax.swing.JButton();
        //cancelButton = new javax.swing.JButton();
        
        // panel size
        //int h = Math.round(350 * xScale);
        //pwPanel.setMaximumSize(new java.awt.Dimension(h, 70));
        //pwPanel.setMinimumSize(new java.awt.Dimension(h, 70));
        //pwPanel.setPreferredSize(new java.awt.Dimension(h, 70));
        
        //password
        passwordField.setColumns(5);
        passwordField.setAlignmentX(20.0F);
        //passwordField.setMaximumSize(new java.awt.Dimension(400, 30));
        passwordField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        Font echoCharFont = getScaledFont("Lucida Sans", Font.PLAIN, 12);
        //Font echoCharFont = new Font("Lucida Sans", Font.PLAIN, 12);
        passwordField.setFont(echoCharFont);
        passwordField.setEchoChar('\u2022');
        
        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String user = new String(passwordField.getPassword());
                    if(appMainLogin.userOK(user)){
                        doClose(RET_CANCEL);
                    }
                    else{
                        // beep
                        passwordField.setText("");
                        System.out.print('a');
                    }
                }
            });
        
        // Login
        pwLabel.setText("Login");
        //pwLabel.setFont(new java.awt.Font("Albany", 0, 12));
        Font dialogFont = getScaledFont("Dialog", Font.PLAIN, 20);
        pwLabel.setFont(dialogFont);
        pwLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        //pwLabel.setMaximumSize(new java.awt.Dimension(250, 20));
        
        // Exit
        JButton exitButton = new JButton("Exit");
        Font buttonFont = getScaledFont("Dialog", Font.PLAIN, 14);
        exitButton.setFont(buttonFont);
        exitButton.setFocusable(false);
        exitButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                exitButtonActionPerformed(evt);
            }
        });
        
        // Listener
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        pwPanel.add(exitButton, "2,1,3,1");
        pwPanel.add(pwLabel, "1,2,2,2");
        pwPanel.add(passwordField, "1,4,2,4");
        //okButton.setText("OK");
    /*okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });*/
        
        //pwPanel.add(okButton);
        
    /*cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });*/
        
        //buttonPanel.add(cancelButton);
        
        getContentPane().add(pwPanel, java.awt.BorderLayout.SOUTH);
        //pack();
    }
    
    /**
     * Check if the "show" study name exist.
     */
    private boolean showStudyNameExist(String userName) {
        if (userName.contains("show")) {
            return true;
        } else {
            return false;
        }
    }
    
     /**
     * Check if the "edit" study name exist.
     */
    private boolean editStudyNameExist(String userName) {
        if (userName.contains("edit")) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Create the option GUI.
     */
    private void createOptionGUI(){
        
    }
    
  /*private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
    doClose(RET_OK);
  }*/
    
  /*private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    doClose(RET_CANCEL);
  }*/
    
    /** Closes the dialog */
    public void closeDialog(java.awt.event.WindowEvent evt) {
        doClose(RET_CANCEL);
    }
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    /** exitButtonActionPerformed
     * Actions then the Exit button  is pressed.
     */
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt){
        if(viewDEX.appMainAdmin.vgControl != null)
            viewDEX.appMainAdmin.viewDex.eyeTracking.createEyeTrackingExitMsg();
        System.exit(0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //new CatLoginDialog(new javax.swing.JFrame(), true).show();
    }
    
    // -------------------------------------------------------------------
    // getScaledFont
    // -------------------------------------------------------------------
    /**
     *  Gets font scaled for screen resolution
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
    
    // Variables declaration - do not modify
    //private javax.swing.JButton okButton;
    //private javax.swing.JButton cancelButton;
    private javax.swing.JPanel pwPanel;
    private javax.swing.JLabel pwLabel;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration
    
    private int returnStatus = RET_CANCEL;
}
