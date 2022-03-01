/* @(#) ScrollStackGUI.java 08/24/2007
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.viewer;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

/**
 *
 * @author sune
 */
public class ScrollStackGUI implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    protected ScrollStackInterface scrollStackInterface;
    private boolean keyCtrlEnable = false;
    private boolean keyShiftEnable = false;
    private boolean keyZEnable = false;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;
    private boolean keyQEnable = false;
    private int anchorX = 0;
    private int anchorY = 0;
    private int x_tot = 0;
    static final String NEWLINE = System.getProperty("line.separator");
    private boolean cineActiveStatus = false;

    /** Creates a new instance of ScrollStackGUI */
    public ScrollStackGUI(ScrollStackInterface scrollstackcontroller) {
        this.scrollStackInterface = scrollstackcontroller;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("ScrolStackGUI:MousePressed");
        
        if (SwingUtilities.isLeftMouseButton(e)){
            
            boolean imageLoadingWorkerStatus = scrollStackInterface.getImageLoadingWorkerStatus();
            boolean imageCineLoopStatus = scrollStackInterface.getCineLoopRunningStatus();
            
            // Modified 20150224
            //if(imageLoadingWorkerStatus || imageCineLoopStatus){
            if(imageCineLoopStatus){
                scrollStackInterface.stopStackLoadInBackground();
                scrollStackInterface.stopStudyCineLoop();
                
                /*
                //test
                if(imageLoadingWorkerStatus == true){
                    System.out.println("imageLoadngWorkerStatus = true");
                }else{
                    if(imageLoadingWorkerStatus == false){
                        System.out.println("imageLoadngWorkerStatus = false");
                    }
                }
                
                if(imageCineLoopStatus == true){
                    System.out.println("imageCineLoopStatus = true");
                }else{
                    if(imageCineLoopStatus == false){
                        System.out.println("imageCineLoopStatus = false");
                    }
                }
                // end test
                */
                //System.out.println("ScrollStackGUI.mousePressed()");
                cineActiveStatus = true;
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e))
            if(cineActiveStatus){
                //System.out.println("ScrollStackGUI.mouseReleased()");
                scrollStackInterface.runStudyAsCineLoop();
            }
        cineActiveStatus = false;
    }
    
    /**********************************************************
     * MouseWheelListener interface
     *********************************************************/
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        String message;

        //System.out.println("ScrollStackGUI:mouseWheelMoved:");

        if ((!keyCtrlEnable && !keyZEnable && !keyAEnable && !keyVEnable && !keyQEnable) &&
                (scrollStackInterface.getCanvasControlMode() == CanvasControlMode.MANIP_SCROLL_STACK)) {
            int notches = e.getWheelRotation();
            if (notches < 0) {
                //message = "Mouse wheel moved UP "
                //      + -notches + " notch(es)" + NEWLINE;

                int units = e.getUnitsToScroll();
                scrollStackInterface.scrollStackUp(units);
            } else {
                //message = "Mouse wheel moved DOWN "
                //      + notches + " notch(es)" + NEWLINE;
                int units = e.getUnitsToScroll();
                scrollStackInterface.scrollStackDown(units);
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

    /**********************************************************
     * KeyListener interface
     *********************************************************/
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("ScrollStackGUI:keyPressed:");
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_CONTROL) {
            keyCtrlEnable = true;
        } else if (keyCode == KeyEvent.VK_SHIFT) {
            keyShiftEnable = true;
        } else if (keyCode == KeyEvent.VK_Z) {
            keyZEnable = true;
        } else if (keyCode == KeyEvent.VK_A) {
            keyAEnable = true;
        } else if (keyCode == KeyEvent.VK_V) {
            keyVEnable = true;
        } else if (keyCode == KeyEvent.VK_Q){
            keyQEnable = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("KeyReleased Localization");
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_CONTROL) {
            keyCtrlEnable = false;
        } else if (keyCode == KeyEvent.VK_SHIFT) {
            keyShiftEnable = false;
        } else if (keyCode == KeyEvent.VK_Z) {
            keyZEnable = false;
            //System.out.println("ScrollStackGUI.keyReleased keyZEnable = false");
        } else if (keyCode == KeyEvent.VK_A) {
            keyAEnable = false;
        } else if (keyCode == KeyEvent.VK_V) {
            keyVEnable = false;
        } else if (keyCode == KeyEvent.VK_Q){
            keyQEnable = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    // end KeyListener interface


    /**********************************************************
     * MouseListener interface
     **********************************************************/
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    /*********************************************************
     * MouseMotionListener interface
     ********************************************************/
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D p = e.getPoint();

        int x = e.getX();
        int y = e.getY();

        //System.out.println("ScrollStackGUI.mouseDragged");

        /*System.out.println("ScrollStackGUI:mouseDragged: " + "x=" + x);
        System.out.println("ScrollStackGUI:mouseDragged: " + "y=" + y);
        System.out.println("ScrolStackGUI:mouseDragged: "  + "anchorX" + anchorX);
        System.out.println("ScrolStackGUI:mouseDragged: "  + "anchorY" + anchorY);
        */

        if (SwingUtilities.isLeftMouseButton(e) && !keyAEnable && !keyQEnable && !keyVEnable) {
            if (scrollStackInterface.getCanvasControlMode() == CanvasControlMode.MANIP_SCROLL_STACK) {
                boolean status = scrollStackInterface.localizationInsideShapeExist(p);
                boolean selStatus = scrollStackInterface.getLocalizationSelectStatus();
                if (!status && !selStatus) {
                    if (y > anchorY) {
                        //System.out.println("ScrollStackGUI:mouseMoved:1 " + "x=" + x);
                        scrollStackInterface.scrollStackDown(0);
                    }

                    if (y < anchorY) {
                        //System.out.println("ScrollStackGUI:mouseMoved:2 " + "x=" + x);
                        scrollStackInterface.scrollStackUp(0);
                    }
                    anchorY = y;
                }
            }
        }
    }

    /***************************************************
     * NOT IN USE
     **************************************************/
    //double dXmod = (double) (dX * 1);
    //int winNew = width_old + (int)Math.round(dXmod);
    //if((int)Math.abs((x - anchorX)) > 8)
    //  anchorX = x;
    /**
     * Display the mouse position values.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
    /*Point2D p1=null;
    Point pos = e.getPoint();
    int x = (int) pos.getX();
    int y = (int) pos.getY();
     */

    /*
    if(SwingUtilities.isLeftMouseButton(e)){
    if(scrollStackInterface.getCanvasControlMode() == CanvasControlMode.MANIP_SCROLL_STACK)
    //if(shiftActive){
    //if(x < 0 )
    //  return;
    if(x > anchorX){
    //System.out.println("ScrollStackGUI:mouseMoved:1 " + "x=" + x);
    scrollStackInterface.scrollStackDown(0);
    }
    if(x < anchorX){
    //System.out.println("ScrollStackGUI:mouseMoved:2 " + "x=" + x);
    scrollStackInterface.scrollStackUp(0);
    }
    anchorX = x;
    }*/
    }

    /**
     * Display the mouse position values.
     * NOT IN USE
     */
    public void mouseMoved2(MouseEvent e) {
        Point2D p1 = null;
        Point pos = e.getPoint();
        int x = (int) pos.getX();
        int y = (int) pos.getY();

        //System.out.println("ScrollStackGUI:mouseMoved: " + "x=" + x);

        if (keyShiftEnable) {
            if (x < 0) {
                return;
            }

            /*
            if(x > anchorX)
            scrollStackInterface.scrollStackDown(0);
            if(x < anchorX)
            scrollStackInterface.scrollStackUp(0);
             */
            int diff = Math.abs((x - anchorX) / 30);
            if (diff > 200) {
                anchorX = x;
                return;
            }

            //System.out.println("ScrollStackGUI:mouseMoved: " + "diff=" + diff);
            for (int i = 0; i <= diff; i++) {
                /*try{
                Thread.sleep(20);
                } catch (InterruptedException ea){
                }*/
                //System.out.println("loop: i= " + i);
                if (x > anchorX) {
                    scrollStackInterface.scrollStackDown(0);
                }
                if (x < anchorX) {
                    scrollStackInterface.scrollStackUp(0);
                }
            }

            x_tot = x_tot + 1;
            //int x2 = x / 10;
            /*
            System.out.println("ScrollStackGUI:mouseMoved: " + "x_tot=" + x_tot);
            if(x_tot > 10){
            if(x > anchorX)
            scrollStackInterface.scrollStackDown(0);
            if(x < anchorX)
            scrollStackInterface.scrollStackUp(0);
            x_tot = 0;
            }*/
            anchorX = x;
        }

    // convert the cordinats from userspace to imagespace
        /*
    try{
    atx = canvas.getTransform();
    p1 = atx.inverseTransform((Point2D)(new Point(x,y)), p1);
    }catch (Exception exp){
    System.out.println(exp);
    }
     **/

    //boolean mode = catMain.getMousePositionDisplay();
            /*if(mode){
    int x1 = (int) p1.getX();
    int y1 = (int) p1.getY();
    String str = Integer.toString(x1) + ", " + Integer.toString(y1);
    //catMain.mousePosDialog.setText(str);
    //catMain.catStudyAdmin.setDebugMousePositionValue(p1);
    }*/
    }
}
