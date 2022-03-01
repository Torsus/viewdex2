/* @(#) AppMainLogin.java 01/28/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.app;

import java.awt.*;
import java.util.ArrayList;

import mft.vdex.dialog.LoginOptionDialog;

/**
 * The <code>AppMainLogin</code> class create a modal login dialog.
 * The dialog check if entered username is defined in the user properties. If not
 * the input field is reset for a new try. The dialog cotains an Exit
 * button to exit the whole application. 
 */
public class AppMainOptionLogin {
    private ViewDex viewDex;
    private LoginOptionDialog loginOptionDialog;
    private String userName;
    private int historyOption;
    
    /**
     * Constructor.
     */
    public AppMainOptionLogin() {
    }
    
    /**
     * Constructor.
     */
    public AppMainOptionLogin(ViewDex viewDex, String userName){
        this.viewDex = viewDex;
        this.userName = userName;
        createUI();
    }
    
    /**
     * Create the UI.
     */
    private void createUI(){
        loginOptionDialog = new LoginOptionDialog(this, viewDex, true, userName);
        loginOptionDialog.setResizable(false);
        loginOptionDialog.setUndecorated(true);
        //loginDialog.setDefaultLookAndFeelDecorated(false);
        //loginDialog.setUndecorated(true);
        //loginDialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        //loginDialog.setLocationRelativeTo(im);
        //int xloc = (int)imagemanip2d.getLocation().getX();
        //int xwidth = (int)imagemanip2d.getSize().getWidth();
        //int width = (int)loginDialog.getSize().getWidth();
        loginOptionDialog.pack();
        
        // Set the location
        int canvasWidth = (int) viewDex.canvas.getSize().getWidth();
        int canvasHeight = (int) viewDex.canvas.getSize().getHeight();
        
        int dialogWidth = (int)loginOptionDialog.getSize().getWidth();
        int dialogHeight = (int)loginOptionDialog.getSize().getHeight();
        
        int xloc = canvasWidth/2 - dialogWidth/2;
        int yloc = canvasHeight/2 - dialogHeight/2;
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        //float xScale = (float) d.width / 1280;
        //float yScale = (float) d.height / 1024;
        //int xloc2 = Math.round(340 * xScale);
        //int yloc2 = Math.round(280 * yScale);
        
        //GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //loginDialog.setSize(dialogWidth, dialogHeight);
        
        Point p = viewDex.getLocationOnScreen();
        
        if(d.width == 1600 && d.height == 1200)
            loginOptionDialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        else if(d.width == 1280 && d.height == 1024)
            loginOptionDialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else if (d.width == 1024)
            loginOptionDialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else 
            loginOptionDialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        
        loginOptionDialog.setVisible(true);
        
        // NOT IN USE
        /*
        // Set the location
        int canvasWidth = (int) xMedicalViewer.canvas.getSize().getWidth();
        int canvasHeight = (int) xMedicalViewer.canvas.getSize().getHeight();
        
        int dialogWidth = (int)loginOptionDialog.getSize().getWidth();
        int dialogHeight = (int)loginOptionDialog.getSize().getHeight();
        
        int xloc = canvasWidth/2 - dialogWidth/2;
        int yloc = canvasHeight/2 - dialogHeight/2 - 150;
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        
        int xloc2 = Math.round(340 * xScale);
        int yloc2 = Math.round(280 * yScale);
        
        if(d.width == 1280)
            loginOptionDialog.setLocation(xloc, yloc);
        else
            loginOptionDialog.setLocation(xloc, yloc2);
        
        //loginDialog.pack();
        //loginDialog.setVisible(true);
        //loginDialog.pack();
       
        loginOptionDialog.setVisible(true);
        //startButton.setEnabled(false);
        */
    }
    
    /**
     * Set the <code/>VgHistoryOptionType</code> value.
     */
    public void userGo(int option){
        historyOption = option;
    }
    
    /**
     * Get the <code/>VgHistoryOptionType</code> value.
     */
    public int getHistoryOption(){
        return historyOption ;
    }
}
