/*
 * LocalizationGUI.java
 *
 * Created on den 16 juli 2007, 13:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package mft.vdex.viewer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;
import mft.vdex.modules.vg.VgRunMode;

/**
 * @author Sune Svensson
 */
public class LocalizationGUI implements MouseListener, MouseMotionListener, KeyListener {

    LocalizationInterface localization;
    //boolean keyCtrlEnable = false;
    boolean lesionMarkActive = false;
    boolean lesionMarkExist = false;

    public LocalizationGUI(LocalizationInterface local) {
        this.localization = local;
    }

    /*
     * MouseListener interface
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    // Go to field
    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("LocalizationGUI.MouseEntered");
        localization.setGotoInputField();
        localization.setFocus();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("LocalizationGUI.MouseExited");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("LocalizationGUI.MousePressed");
        Point2D p = e.getPoint();

        //(localization.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_IN) &&
        //(localization.getCanvasControlMode() != CanvasControlMode.MANIP_ZOOM_OUT) &&
        //(localization.getCanvasControlMode() != CanvasControlMode.MANIP_PAN)){

        if (SwingUtilities.isLeftMouseButton(e)) {
            // create
            if (localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.CREATE_EXIST)) {
                localization.mousePressedCreateAction(e.getX(), e.getY());
            } else if (!localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.CREATE_EXIST)) {
                localization.mousePressedShowSelectAction(e.getX(), e.getY());
            } else if (localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.DEMO_EXIST)) {
                localization.mousePressedCreateAction(e.getX(), e.getY());
            } else if (!localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.DEMO_EXIST)) {
                localization.mousePressedShowSelectAction(e.getX(), e.getY());
            } else if (localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.SHOW_EXIST)) {
                Toolkit.getDefaultToolkit().beep();
                return;
            } else if (!localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.SHOW_EXIST)) {
                localization.mousePressedShowSelectAction(e.getX(), e.getY());
            } else if (!localization.getKeyCtrlEnable() && ((localization.getRunModeStatus() == VgRunMode.EDIT_EXIST))) {
                //localization.mousePressedEditSelectAction(p);
                localization.mousePressedShowSelectAction(e.getX(), e.getY());
            } else if (localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.EDIT_EXIST)) {
                localization.mousePressedCreateAction(e.getX(), e.getY());
            }
        }

        // What is this for..
        if (SwingUtilities.isRightMouseButton(e)) {
            if (!localization.getKeyCtrlEnable() && (localization.getRunModeStatus() == VgRunMode.EDIT_EXIST)) {
                //localization.mousePressedRightSelectAction(e.getX(), e.getY());
            } else if ((localization.getRunModeStatus() == VgRunMode.CREATE_EXIST)) {
                //localization.mousePressedRightCreateAction(p);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /*
     * KeyListener interface
     */
    @SuppressWarnings("static-access")
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("LocalizationGUI.keyPressed");
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL) {
            localization.setKeyCtrlEnable(true);
            //keyCtrlEnable = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("LocalizationGUI.keyReleased");

        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL) {
            localization.setKeyCtrlEnable(false);
            //keyCtrlEnable = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /************************************************
     * MouseMotion listener interface
     ***********************************************/
    /** 
     * mouseDragged
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("PanGUI:mouseDragged:");
        //if(SwingUtilities.isLeftMouseButton(e) &&
        // (e.getX(), e.getY());
    }

    /**
     * mouseMoved
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //Point2D p1=null;
        //Point2D pos = e.getPoint();
        //int x = (int) pos.getX();
        //int y = (int) pos.getY();
        //if(localization.getShowStudyExist() || localization.getEditStudyExist())
        //  localization.mouseMovedAction(pos);
        // convert the cordinats from userspace to imagespace
        /*
        try{
        atx = canvas.getTransform();
        p1 = atx.inverseTransform((Point2D)(new Point(x,y)), p1);
        }catch (Exception exp){
        System.out.println(exp);
        }
         * */
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
