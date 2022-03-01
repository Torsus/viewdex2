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
import mft.vdex.dialog.LoginETDialog;

/**
 * The <code>AppMainLogin</code> class create a modal login dialog.
 * The dialog check if entered loginName is defined in the user properties. If not
 * the input field is reset for a new try. The dialog cotains an Exit
 * button to exit the whole application. 
 */
public class AppMainET {
    private ViewDex viewDex;
    private LoginETDialog dialog;
    
    // textfield input
    private String id;
    private String age;
    private String sex;
    private String dominantEye;
    
    /**
     * Constructor.
     */
    public AppMainET() {
    }
    
    /**
     * Constructor.
     */
    public AppMainET(ViewDex viewDex){
        this.viewDex = viewDex;
        createUI();
    }
    
    /**
     * Create the UI.
     */
    public void createUI(){
        //dialog = new LoginDialog(this, viewDex, true);
        dialog = new LoginETDialog(this, viewDex, true);
        dialog.setResizable(false);
        dialog.setUndecorated(true);
        
        //dialog.setDefaultLookAndFeelDecorated(false);
        //dialog.setUndecorated(true);
        //dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        //dialog.setLocationRelativeTo(im);
        //int xloc = (int)imagemanip2d.getLocation().getX();
        //int xwidth = (int)imagemanip2d.getSize().getWidth();
        //int width = (int)loginDialog.getSize().getWidth();
        dialog.pack();
        dialog.setFocus();
        
        // Set the location
        int canvasWidth = (int) viewDex.canvas.getSize().getWidth();
        int canvasHeight = (int) viewDex.canvas.getSize().getHeight();
        
        int dialogWidth = (int)dialog.getSize().getWidth();
        int dialogHeight = (int)dialog.getSize().getHeight();
        
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
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        else if(d.width == 1280 && d.height == 1024)
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else if (d.width == 1024)
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else 
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        
        //old
        /*
        if(d.width == 1280)
            dialog.setLocation(xloc, yloc);
        else
            dialog.setLocation(xloc, yloc2);
         */
        
        //loginDialog.setLocationRelativeTo(viewDex.canvas);
        dialog.setVisible(true);
    }
    
    
    public void setID(String str){
        id = str;
    }
    
    public void setAge(String str){
        age = str;
    }
    
    public void setSex(String str){
        sex = str;
    }
    
    public void setDominantEye(String str){
        dominantEye = str;
    }
    
    public String getID(){
        return id;
    }
    
    public String getAge(){
        return age;
    }
    
    public String getSex(){
        return sex;
    }
    
    public String getDominantEye(){
        return dominantEye;
    }
}