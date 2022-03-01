/* @(#) WindowLevelGUI.java 09/09/2002
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.viewer;

import java.awt.event.*;
import javax.swing.*;

/**
 * Implements a GUI for window/level operations on an image on the imagecanvas.
 */


public class WindowLevelGUI implements MouseListener, MouseMotionListener{
    protected boolean wlOn = false;
    protected boolean mousePressed = false;
    protected WindowLevel wl;
    protected ImageCanvasInterface canvas;
    
    private int canvasControlMode = 0;
    private String runMode;
    
    public WindowLevelGUI(ImageCanvasInterface imagemanipulator, WindowLevel windowLevel){
        this.canvas= imagemanipulator;
        this.wl = windowLevel;
        
        //init();
    }
    
    public void init(){
    }
    
    public void setWLON(boolean wlOn){
        this.wlOn = wlOn;
    }
    
    /*
    public void setWindowLevelMapFactor(){
        Dimension size = new Dimension();
        size = im.getImageCanvasSize();
        double a = size.getWidth();
        double b = size.getHeight();
        float winf = (float) (MAX_SCREEN_VALUE / size.getWidth());
        float levf = (float) (MAX_SCREEN_VALUE / size.getHeight());
        float winfd = (float) (MAX_SCREEN_VALUE / size.getWidth());
        float levfd = (float) (MAX_SCREEN_VALUE / size.getHeight());
        int c = 10;
    }
     */
    
    @Override
    public void mousePressed(java.awt.event.MouseEvent e){
        //System.out.println("WindowLevelGUI.mousePressed");
        
        wlOn = false;
        if(SwingUtilities.isRightMouseButton(e) && (wl.getCanvasControlMode() == CanvasControlMode.WINDOW_LEVEL)){
            //wl.setWindowLevelMapFactor();
            wl.startWL(e.getX(), e.getY());
        }
        /*
        else{
            if(SwingUtilities.isLeftMouseButton(e)){
                wl.setWindowLevelMapFactor();
              wl.startWL(e.getX(), e.getY());
            }
        }*/
    }

    @Override
    public void mouseDragged(MouseEvent e){
        //System.out.println("CatWindowLevelGUI: mouseDragged");
        
        if(SwingUtilities.isRightMouseButton(e) && (wl.getCanvasControlMode() == CanvasControlMode.WINDOW_LEVEL)){
            wlOn = true;
            wl.doWL(e.getX(), e.getY());
        }
        /*
        else{
            if(canvasControlMode == 1){   // window/level
                if(SwingUtilities.isLeftMouseButton(e)){
                    wlOn= true;
                    wl.doWL(e.getX(), e.getY());
                }
            }
        }*/
    }
    
    /**
     * mouseRelese
     * @param e
     */
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e){
        //System.out.println("CatWindowLevelGUI: mouseReleased");
        //wlOn = false;
        //wl.stopWL();
        mousePressed = false;
    }
    
    
    @Override
    public void mouseMoved(MouseEvent e){
        //System.out.println("CatWindowLevelGUI: mouseMoved");
        //int d=10;
    }
    
    @Override
    public void mouseClicked(MouseEvent e){
        //System.out.println("CatWindowLevelGUI: mouseClicked");
        //int a = 10;
    }
    
    @Override
    public void mouseEntered(MouseEvent e){
        //System.out.println("WindowLevelGUI.mouseEntered");
        //int b =10;
    }
    
    @Override
    public void mouseExited(MouseEvent e){
        //System.out.println("CatWindowLevelGUI: mouseExited");
        //int c=10;
    }
    
    // Returns the wlOn status.
    // If true a window/level operation is initiated.
    // This is for the canvas context meny not to popup when the window/level
    // operaion is ended.
    public boolean getWLStatus(){
        return wlOn;
    }
    
    // NOT IN USE
    public void setWindowLevel(int windowWidth, int windowCenter){
        //System.out.println("CatWindowLevelGUI: setWindowLevel");
        
        if(windowWidth == 0 && windowCenter == 0)
            wl.setWindowLevel(250, 127);
        else{
            //long msecs = System.currentTimeMillis();
            wl.setWindowLevel(windowWidth, windowCenter);
            //System.out.println("Time WL10 " + (System.currentTimeMillis()-msecs));
        }
    }
    
    public void setWindowLevel(){
        wl.setWindowLevel();
    }
}
