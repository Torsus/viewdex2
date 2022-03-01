/* @(#) PixelValueMeanGUI.java 25/03/2014
 *
 * Copyright (c) 2014 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
/**
 * @author Sune Svensson
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

public class PixelValueMeanGUI implements MouseListener, MouseMotionListener, KeyListener {

    private PixelValueMean pixelValueMean;
    private boolean keyAEnable = false;
    private boolean keyQEnable = false;
    private boolean keyVEnable = false;
    private boolean mouseDragged = false;
    private boolean mouseInCanvas = false;

    public PixelValueMeanGUI(PixelValueMean pixelvaluemean) {
        this.pixelValueMean = pixelvaluemean;
    }

    /**
     * Called when the pixel value drawing starts.
     * @param e the MouseEvent state objec.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();

        if (SwingUtilities.isLeftMouseButton(e) && pixelValueMean.getKeyQEnableStatus()) {
            // init
            //pixelvalue.viewDex.appMainAdmin.vgControl.deleteROIPixelValueListAndNoRender();
            //pixelvalue.viewDex.canvas.setCanvasROIPixelValueMeanDrawingStatus(false);
            pixelValueMean.viewDex.canvas.setCanvasROIPixelValueMeanDrawingValue(0, 0, 0, 0);
            pixelValueMean.viewDex.canvas.setCanvasROIPixelValueMeanUpdateStatus(true);
            pixelValueMean.viewDex.canvas.setCanvasROIPixelValueMeanUpdateValue(null);
            //pixelvalue.viewDex.windowLevel.setWindowLevel();

            //pixelvalue.setROIGrabSymbols(true);
            pixelValueMean.startDraw(e.getX(), e.getY());
        }
    }

    // MouseMotionListener interface
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDragged = true;

        if (!pixelValueMean.getKeyQEnableStatus()) {
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            pixelValueMean.draw(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)
                && pixelValueMean.getKeyQEnableStatus()
                && !(((pixelValueMean.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                ^ (pixelValueMean.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            pixelValueMean.setROIGrabSymbols(true);
            pixelValueMean.stopDraw(e.getX(), e.getY());
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyAEnable == false
                && keyQEnable == false
                && keyVEnable == false
                && !mouseDragged
                && !(((pixelValueMean.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN)
                ^ (pixelValueMean.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT)))) {

            pixelValueMean.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();
            pixelValueMean.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
            pixelValueMean.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
            pixelValueMean.viewDex.windowLevel.setWindowLevel();
        }

        if (SwingUtilities.isLeftMouseButton(e)
                && keyQEnable
                && !mouseDragged) {
            pixelValueMean.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();
        }

        mouseDragged = false;
    }

    // MouseListener interface
    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("DistanceGUI.MouseEntered");
        mouseInCanvas = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("DistanceGUI.mouseExited");
        mouseInCanvas = false;
        pixelValueMean.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //int x = e.getX();
        //int y = e.getY();
        //System.out.println("DistanceGUI.mouseMoved = " + "x=" + x + "y=" + y);
    }

    // KeyListener interface
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet
    }

    @SuppressWarnings("static-access")
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("DistanceGUI.Keypressed");
        int keyCode = e.getKeyCode();

        // Set the crosshair cursor
        if ((keyCode == e.VK_Q) && mouseInCanvas) {
            //System.out.println("DistanceGUI.Keypressed2");
            pixelValueMean.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        if (keyCode == e.VK_Q) {
            keyQEnable = true;
            pixelValueMean.setKeyQEnableStatus(true);
        } else {
            if (keyCode == e.VK_A) {
                keyAEnable = true;
                pixelValueMean.setKeyAEnableStatus(true);
            } else {
                if (keyCode == e.VK_V) {
                    keyVEnable = true;
                    pixelValueMean.setKeyVEnableStatus(true);
                }
            }
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("DistanceGUI.KeyReleased");

        if (e.getKeyCode() == e.VK_Q) {
            keyQEnable = false;
            pixelValueMean.setKeyQEnableStatus(false);
            pixelValueMean.setROIItemActiveStatus();
            pixelValueMean.setROIGrabSymbols(true);
            pixelValueMean.viewDex.windowLevel.setWindowLevel();
        } else {
            if (e.getKeyCode() == e.VK_A) {
                keyAEnable = false;
            }
        }
        pixelValueMean.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
