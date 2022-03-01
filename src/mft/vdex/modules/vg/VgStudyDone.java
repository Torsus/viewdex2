/* @(#) VgStudyDone.java 01/28/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.vg;

import java.awt.*;
import mft.vdex.dialog.LoginDialog;
import mft.vdex.app.*;
import mft.vdex.dialog.StudyDoneDialog;


/**
 * The <code>VgStudyDone</code> class create a modal study done dialog.
 * 
 */
public class VgStudyDone {
    private ViewDex vdex;
    private StudyDoneDialog dialog;
    
    /**
     * Constructor.
     */
    public VgStudyDone() {
    }
    
    /**
     * Constructor.
     */
    public VgStudyDone(ViewDex viewdex){
        this.vdex = viewdex;
        createUI();
    }
    
    /**
     * Create the UI.
     */
    public void createUI(){
        dialog = new StudyDoneDialog(vdex, true);
        dialog.setResizable(false);
        dialog.setUndecorated(true);
        //loginDialog.setDefaultLookAndFeelDecorated(false);
        //loginDialog.setUndecorated(true);
        //loginDialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        //loginDialog.setLocationRelativeTo(im);
        //int xloc = (int)imagemanip2d.getLocation().getX();
        //int xwidth = (int)imagemanip2d.getSize().getWidth();
        //int width = (int)loginDialog.getSize().getWidth();
       dialog.pack();
        
        // Set the location
        int canvasWidth = (int) vdex.canvas.getSize().getWidth();
        int canvasHeight = (int) vdex.canvas.getSize().getHeight();
        
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
        
        Point p = vdex.getLocationOnScreen();
        
        if(d.width == 1600 && d.height == 1200)
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        else if(d.width == 1280 && d.height == 1024)
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else if (d.width == 1024)
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 125);
        else 
            dialog.setLocation((int)p.getX() + xloc, (int)p.getY() + yloc - 150);
        
        // old
        /*
        if(d.width == 1280)
            dialog.setLocation(xloc, yloc);
        else
            dialog.setLocation(xloc, yloc2);
        */
        
        //loginDialog.pack();
        //loginDialog.setVisible(true);
        //loginDialog.pack();
       
        dialog.setVisible(true);
        //startButton.setEnabled(false);
    }
}
