/* @(#) ZoomGUI.java 03/02/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 * This code is based on code written by Lawrence Rodriges.
 */

package mft.vdex.viewer;

import java.awt.event.*;
import javax.swing.*;


/* Implements a GUI for zooming an image drawn on a canvas.
 **/
public class ZoomGUI implements MouseListener, KeyListener, MouseWheelListener{
    protected ZoomInterface zoomController;
    protected ImageCanvasInterface canvas;
    protected final static double baseZoomFactor = 1.0;
    protected double zoomFactor = 1.0;
    protected double zoomMagAbsoluteValue = 1.0;
    protected double increment = 0.1;
    protected boolean mousePressed = false;
    private boolean localizationActive = false;
    private boolean keyZEnable = false;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;
    private boolean keyQEnable = false;
    private boolean keySpaceEnable = false;
    static final String NEWLINE = System.getProperty("line.separator");
    
    // MouseListner interface
    // mouseClicked(), mouseEntered(), mouseExited(), mousePressed(),mouseReleased().
    
    public ZoomGUI(ZoomInterface zoomcontroller, ImageCanvasInterface imagemanipulator){
        this.zoomController = zoomcontroller;
        this.canvas = imagemanipulator;
    }
    
    /**
     * zoomIn
     */
    public void zoomIn(int x, int y, boolean renderStatus){
        zoomController.paintImage(x, y, baseZoomFactor + increment, renderStatus);
        zoomMagAbsoluteValue *= baseZoomFactor + increment;
        zoomController.setZoomMagAbsoluteValue(zoomMagAbsoluteValue);
        //zoomController.setLocalizationMarks();
    }
    
    /** zoomOut
     */
    public void zoomOut(int x, int y, boolean renderStatus){
        zoomController.paintImage(x, y, baseZoomFactor - increment, renderStatus);
        zoomMagAbsoluteValue *= baseZoomFactor - increment;
        zoomController.setZoomMagAbsoluteValue(zoomMagAbsoluteValue);
    }
    
    /** 
     * mousedPressed
     * mouseListner interface
     * If the 'a' key is active zoom is inhibit
     */
    @Override
    public void mousePressed(MouseEvent e){
        if(SwingUtilities.isLeftMouseButton(e) &&
                !localizationActive &&
                !keyAEnable &&
                !keyVEnable &&
                !keyQEnable &&
                (zoomController.getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_IN))
            zoomIn(e.getX(), e.getY(), false);
        if(SwingUtilities.isLeftMouseButton(e) &&
                !localizationActive &&
                !keyAEnable &&
                !keyVEnable &&
                !keyQEnable &&
                (zoomController.getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_OUT))
            zoomOut(e.getX(), e.getY(), false);
    }
    
    /** mousedReleased
     */
    @Override
    public void mouseReleased(MouseEvent e){
        mousePressed = false;
    }
    
    /** mousedClicked
     */
    @Override
    public void mouseClicked(MouseEvent e){
    }
    
    /** mousedEntered
     */
    @Override
    public void mouseEntered(MouseEvent e){
    }
    
    /** mousedExited
     */
    @Override
    public void mouseExited(MouseEvent e){
    }
    
    
    
    /** NOT IN USE */
    public void setZoomfactor(double mag){
        zoomFactor = mag;
    }
    
    public double getZoomFactor(){
        return zoomFactor;
    }
    
    public void setZoomIncrement(double incr){
        increment = incr;
    }
    
    public double getZoomIncrement(){
        return increment;
    }
    
    public void reset() {
        zoomFactor = 1.0;
    }
    // end NOT IN USE
    
    /*
     * KeyListener interface
     */
    @Override
    public void keyPressed(KeyEvent e){
        //System.out.println("Keypressed ZoomGUI");
        int keyCode = e.getKeyCode();
        
        if(keyCode == KeyEvent.VK_CONTROL)
            localizationActive = true;
        else if(keyCode == KeyEvent.VK_Z)
            keyZEnable = true;
        else if (keyCode == KeyEvent.VK_A)
            keyAEnable = true;
        else if (keyCode == KeyEvent.VK_V)
            keyVEnable = true;
        else if (keyCode == KeyEvent.VK_Q)
            keyQEnable = true;
        else if(keyCode == KeyEvent.VK_SPACE)
            keySpaceEnable = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("KeyReleased ZoomGUI");
        int keyCode = e.getKeyCode();
        
        if(keyCode == KeyEvent.VK_CONTROL)
            localizationActive = false;
        else if (keyCode == KeyEvent.VK_Z)
            keyZEnable = false;
        else if (keyCode == KeyEvent.VK_A)
            keyAEnable = false;
        else if (keyCode == KeyEvent.VK_V)
            keyVEnable = false;
        else if(keyCode == KeyEvent.VK_Q)
            keyQEnable = false;
        else if(keyCode == KeyEvent.VK_SPACE)
            keySpaceEnable = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e){
    }

    
    /**********************************************************
     * MouseWheelListener interface
     *********************************************************/
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //String message;
        //System.out.println("ZoomGUI:mouseWheelMoved");
        
        if(keyZEnable && !keyAEnable && !keyQEnable){
            int notches = e.getWheelRotation();
            if (notches < 0) {
                //message = "Mouse wheel moved UP "
                  //      + -notches + " notch(es)" + NEWLINE;
                
                //int units = e.getUnitsToScroll();
                zoomIn(e.getX(), e.getY(), true);
                //scrollStackController.scrollStackUp(units);
            } else {
                //message = "Mouse wheel moved DOWN "
                  //      + notches + " notch(es)" + NEWLINE;
                //int units = e.getUnitsToScroll();
                zoomOut(e.getX(), e.getY(), true);
                //scrollStackController.scrollStackDown(units);
            }
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                //message += "    Scroll type: WHEEL_UNIT_SCROLL" + NEWLINE;
                //message += "    Scroll amount: " + e.getScrollAmount()
                //+ " unit increments per notch" + NEWLINE;
                //message += "    Units to scroll: " + e.getUnitsToScroll()
                //+ " unit increments" + NEWLINE;
            } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
                //message += "    Scroll type: WHEEL_BLOCK_SCROLL" + NEWLINE;
            }
        }
    }
}
