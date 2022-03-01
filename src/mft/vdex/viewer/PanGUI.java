/* @(#) PanGUI.java 05/12/2005
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
import java.awt.event.KeyListener;
import javax.swing.*;


/** Implements a GUI for scrolling images on a canvas.
 */
public class PanGUI implements MouseListener, MouseMotionListener, KeyListener, FocusListener{
    protected PanInterface panInterface;
    protected boolean mousePressed = false;
    //private int canvasControlMode = 0;
    //private String runMode;
    private boolean ctrlEnable = false;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;
    private boolean keyQEnable = false;
    private int currentX;
    private int currentY;
    
    public PanGUI(PanInterface paninterface){
        this.panInterface = paninterface;    
    }
    
    /** 
     * mousePressed
     */
    @Override
    public void mousePressed(MouseEvent e){
        //System.out.println("PanGUI.MousePressed");
        //panInterface.setFocus();
        
        //if((!ctrlEnable && !keyAEnable) && SwingUtilities.isLeftMouseButton(e) &&
          if(SwingUtilities.isLeftMouseButton(e) &&
            (panInterface.getCanvasControlMode() == CanvasControlMode.MANIP_PAN)){
            panInterface.startScroll(e.getX(), e.getY());
        }
    }
    
    /** mouseDragged
     */
    @Override
    public void mouseDragged(MouseEvent e){
        //System.out.println("PanGUI:mouseDragged:");
        currentX = e.getX();
        currentY = e.getY();
        panInterface.setFocus();
        if((!ctrlEnable && !keyAEnable && !keyVEnable && !keyQEnable) && SwingUtilities.isLeftMouseButton(e) &&
            (panInterface.getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
            panInterface.scroll(e.getX(), e.getY());
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        //panInterface.stopScroll();
        //mousePressed = false;
    }
    
    @Override
    public void mouseClicked(MouseEvent e){
        //panInterface.setFocus();
        //int a = 10;
    }
    
    @Override
    public void mouseEntered(MouseEvent e){
        //System.out.println("PanGUI.MouseEntered");
        //int b =10;
    }
    
    @Override
    public void mouseExited(MouseEvent e){
        //int c=10;
    }
    
    /**
     * Set the mouse x,y position.
     */
    @Override
    public void mouseMoved(MouseEvent e){
        //System.out.println("PanGUI:mouseMoved:");
        panInterface.setXYPosition(e.getX(), e.getY());
    }
    
    /*
     * KeyListener interface
     */
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
         //System.out.println("Keypressed Localization");
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL)
            ctrlEnable = true;
        else{
            if (keyCode == KeyEvent.VK_A)
                keyAEnable = true;
            else{
                if(keyCode == KeyEvent.VK_Q)
                    keyQEnable = true;
                else{
                    if(keyCode == KeyEvent.VK_V)
                    keyVEnable = true;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("KeyReleased Localization");
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL){
            ctrlEnable = false;
            panInterface.startScroll(currentX, currentY);
        }
        else
            if ((keyCode == KeyEvent.VK_A) || (keyCode == KeyEvent.VK_Q) ||
                keyCode == KeyEvent.VK_V){
                keyAEnable = false;
                keyQEnable = false;
                keyVEnable = false;
                // to handle the drawing case: A + lmb + drag -> release A ->
                // drag -> transformation (unwanted)
                panInterface.startScroll(currentX, currentY);
            }
    }
    
    /**************************************************
     * FocusListener interface
     *************************************************/
    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("PanGUI.focusGained");
        //viewdex.requestFocusInWindow();
    }

    @Override
    public void focusLost(FocusEvent e) {
        //System.out.println("PanGUI.focusLost");
    }
}
