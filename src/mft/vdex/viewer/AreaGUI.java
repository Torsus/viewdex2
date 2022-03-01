/* VolumeGUI.java 20160216
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * Sune Svensson
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
public class AreaGUI implements MouseListener, MouseMotionListener, KeyListener {

    private Area area;
    private boolean keyVEnable = false;
    private boolean keyAEnable = false;
    private boolean keyQEnable = false;
    private boolean mouseDragged = false;
    private boolean mouseInCanvas = false;
    private int cntMousePressed = 0;
    private int cntMouseDragged = 0;
    private int cntMouseReleased = 0;
    public static final boolean dev_debug = false;

    public AreaGUI(Area area) {
        this.area = area;
    }

    /**
     * Called when the area drawing starts.
     * @param e the MouseEvent state objec.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();
        if (dev_debug) {
            System.out.println("VolumeGUI.mousePressed " + e.getX() + "," + e.getY() + cntMousePressed);
        }

        if (SwingUtilities.isLeftMouseButton(e) && area.getKeyVEnableStatus()) {
            //init
            //distance.viewDex.appMainAdmin.vgControl.deleteROIListAndNoRender();
            //distance.viewDex.canvas.setCanvasROIDistanceDrawingStatus(false);
            area.viewDex.canvas.setCanvasROIAreaUpdateStatus(true);
            area.viewDex.canvas.setCanvasROIAreaUpdateTextStatus(false);
            area.viewDex.canvas.setCanvasROIAreaUpdateValue(null);
            //distance.viewDex.windowLevel.setWindowLevel();
            area.setROIGrabSymbols(true);
            area.startDraw(e.getX(), e.getY());
        }
    }

    // MouseMotionListener interface
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDragged = true;
        if (dev_debug) {
            System.out.println("VolumeGUI.mouseDragged" + " " + e.getX() + " " + e.getY() + " " + cntMouseDragged);
        }

        if (!area.getKeyVEnableStatus()) {
            //System.out.println("VolumeGUI.mouseDragged" + "  return");
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            area.draw(e.getX(), e.getY());
        }
        //System.out.println("VolumeGUI.mouseDragged end");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dev_debug) {
            System.out.println("VolumeGUI.mouseReleased" + " " + e.getX() + " " + e.getY() + " " + cntMouseReleased);
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && area.getKeyVEnableStatus()) {
                //&& !(((area.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                //^ (area.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            area.setROIGrabSymbols(true);
            area.stopDraw(e.getX(), e.getY());
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyVEnable == false
                && keyQEnable == false
                && !mouseDragged
                && !(((area.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                ^ (area.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            area.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
            area.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
            area.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();
            area.viewDex.windowLevel.setWindowLevel();
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyVEnable
                && !mouseDragged) {
            area.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
        }

        mouseDragged = false;
        //System.out.println("VolumeGUI.mouseReleased end");
}

    // MouseListener interface
    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("VolumeGUI.mouseEntered" + "  " + cnt);
        //distance.viewDex.requestFocusInWindow(); //20151222
        //distance.viewDex.toggleVisible();
        //distance.viewDex.setApplicationFocus();
        //distance.viewDex.toFront();
        //distance.viewDex.repaint();
        //distance.viewDex.setAlwaysOnTop(true);
        mouseInCanvas = true;

        //System.out.println("VolumeGUI.mouseEntered end");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("VolumeGUI.mouseExited" + "  " + cnt);
        mouseInCanvas = false;
        area.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        //System.out.println("VolumeGUI.mouseExited end");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("DistanceGUI.mouseMoved");
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
        //System.out.println("VolumeGUI.keypressed" + "  " + cnt);
        int keyCode = e.getKeyCode();

        // Set the crosshair cursor
        if ((keyCode == e.VK_V) && mouseInCanvas) {
            //System.out.println("Key pressed and mouse in canvas");
            area.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        if ((keyCode == e.VK_V) && mouseInCanvas) {
            //System.out.println("keyVEnable = true");
            keyVEnable = true;
            area.setKeyVEnableStatus(true);
        } else {
            if (keyCode == e.VK_A) {
                keyAEnable = true;
                area.setKeyAEnableStatus(true);
            } else {
                if (keyCode == e.VK_Q) {
                    keyQEnable = true;
                    area.setKeyQEnableStatus(true);
                }
            }
        }
        //System.out.println("VolumeGUI.keypressed end");
    }

    @SuppressWarnings("static-access")
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("VolumeGUI.keyReleased" + "  " + cnt);

        if (e.getKeyCode() == e.VK_V) {
            //System.out.println("keyVEnable = false");
            keyVEnable = false;
            area.setKeyVEnableStatus(false);
            area.setROIItemActiveStatus();
            area.setROIGrabSymbols(true);
            area.viewDex.windowLevel.setWindowLevel();
        } else {
            if (e.getKeyCode() == e.VK_Q) {
                //System.out.println("keyVEnable = false");
                keyQEnable = false;
                area.setKeyQEnableStatus(false);
            }
        }
        area.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        //System.out.println("VolumeGUI.keyReleased end");
    }
}
