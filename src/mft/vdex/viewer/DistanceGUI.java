/* @(#) DistanceGUI.java 17/11/2008
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
package mft.vdex.viewer;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

/**
 * @author Sune Svensson
 */
public class DistanceGUI implements MouseListener, MouseMotionListener, KeyListener {

    private Distance distance;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;
    private boolean keyQEnable = false;
    private boolean mouseDragged = false;
    private boolean mouseInCanvas = false;
    private int cntMousePressed = 0;
    private int cntMouseDragged = 0;
    private int cntMouseReleased = 0;
    private int cntMouseMoved = 0;
    public static final boolean dev_debug = false;

    public DistanceGUI(Distance distance) {
        this.distance = distance;
    }

    /**
     * Called when the distance drawing starts.
     * @param e the MouseEvent state objec.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();
        if (dev_debug) {
            System.out.println("DistanceGUI.mousePressed " + e.getX() + " " + e.getY() + " " + cntMousePressed++);
        }

        if (SwingUtilities.isLeftMouseButton(e) && distance.getKeyAEnableStatus()) {
            //init
            //distance.viewDex.appMainAdmin.vgControl.deleteROIListAndNoRender();
            //distance.viewDex.canvas.setCanvasROIDistanceDrawingStatus(false);
            distance.viewDex.canvas.setCanvasROIDistanceDrawingValue(0, 0, 0, 0);
            distance.viewDex.canvas.setCanvasROIDistanceUpdateStatus(true);
            distance.viewDex.canvas.setCanvasROIDistanceUpdateValue(null);
            //distance.viewDex.windowLevel.setWindowLevel();

            distance.setROIGrabSymbols(true);
            distance.startDraw(e.getX(), e.getY());
        }
    }

    // MouseMotionListener interface
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDragged = true;
        if (dev_debug) {
            System.out.println("DistanceGUI.mouseDragged" + " " + e.getX() + " " + e.getY() + " " + cntMouseDragged++);
        }

        if (!distance.getKeyAEnableStatus()) {
            //System.out.println("DistanceGUI.mousemouseDragged" + "  return");
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            distance.draw(e.getX(), e.getY());
        }
        //System.out.println("DistanceGUI.mousemouseDragged end");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dev_debug) {
            System.out.println("DistanceGUI.mouseReleased" + " " + e.getX() + " " + e.getY() + " " + cntMouseReleased++);
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && distance.getKeyAEnableStatus()
                && !(((distance.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                ^ (distance.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            distance.setROIGrabSymbols(true);
            distance.stopDraw(e.getX(), e.getY());
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyAEnable == false
                && keyQEnable == false
                && keyVEnable == false
                && !mouseDragged
                && !(((distance.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                ^ (distance.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            distance.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
            distance.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
            distance.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();
            distance.viewDex.windowLevel.setWindowLevel();
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyAEnable
                && !mouseDragged) {
            distance.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
        }
        mouseDragged = false;
        //System.out.println("DistanceGUI.mouseReleased end");
    }

    // MouseListener interface
    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("DistanceGUI.mouseEntered" + "  " + cnt);
        //distance.viewDex.requestFocusInWindow(); //20151222
        //distance.viewDex.toggleVisible();
        //distance.viewDex.setApplicationFocus();
        //distance.viewDex.toFront();
        //distance.viewDex.repaint();
        //distance.viewDex.setAlwaysOnTop(true);
        mouseInCanvas = true;

        //System.out.println("DistanceGUI.mouseEntered end");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("DistanceGUI.mouseExited" + "  " + cnt);
        mouseInCanvas = false;
        distance.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        //System.out.println("DistanceGUI.mouseExited end");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (dev_debug) {
            System.out.println("DistanceGUI.mouseMoved " + e.getX() + " " + e.getY() + " " + cntMouseMoved++);
        }
        //int x = e.getX();
        //int y = e.getY();

        //System.out.println("DistanceGUI.mouseMoved = " + "x=" + x + "y=" + y);
        // System.out.println("DistanceGUI.mouseMoved end");
    }

    // KeyListener interface
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet
    }

    @SuppressWarnings("static-access")
    @Override
    public void keyPressed(KeyEvent e) {
        //distance.viewDex.toggleVisible();
        //System.out.println("DistanceGUI.keypressed" + "  " + cnt);
        int keyCode = e.getKeyCode();

        // Set the crosshair cursor
        if ((keyCode == e.VK_A) && mouseInCanvas) {
            //System.out.println("Key pressed and mouse in canvas");
            distance.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        if ((keyCode == e.VK_A) && mouseInCanvas) {
            //System.out.println("keyAEnable = true");
            keyAEnable = true;
            distance.setKeyAEnableStatus(true);
        } else {
            if (keyCode == e.VK_V) {
                keyVEnable = true;
                distance.setKeyVEnableStatus(true);
            } else {
                if (keyCode == e.VK_Q) {
                    keyQEnable = true;
                }
                distance.setKeyQEnableStatus(true);
            }
        }
        //System.out.println("DistanceGUI.keypressed end");
    }

    @SuppressWarnings("static-access")
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("DistanceGUI.keyReleased" + "  " + cnt);

        if (e.getKeyCode() == e.VK_A) {
            //System.out.println("keyAEnable = false");
            keyAEnable = false;
            distance.setKeyAEnableStatus(false);
            distance.setROIItemActiveStatus();
            distance.setROIGrabSymbols(true);
            distance.viewDex.windowLevel.setWindowLevel();
        } else {
            if (e.getKeyCode() == e.VK_Q) {
                //System.out.println("keyAEnable = false");
                keyQEnable = false;
                distance.setKeyQEnableStatus(false);
            }
        }
        distance.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        //System.out.println("DistanceGUI.keyReleased end");
    }
}
