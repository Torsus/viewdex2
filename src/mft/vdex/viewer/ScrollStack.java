/* @(#) ScrollStack.java 08/24/2007
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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import mft.vdex.app.ViewDex;

/**
 *
 * @author sune
 */
public class ScrollStack implements ScrollStackInterface{
    protected ViewDex viewDex;
    protected ImageCanvasInterface canvas;
    private int canvasControlMode;
    
    /** Creates a new instance of ScrollStack */
    public ScrollStack(ViewDex viewdex, ImageCanvasInterface imagemanipulator){
        this.viewDex = viewdex;
        this.canvas = imagemanipulator;
    }
    
    /**
     */
    @Override
    public void scrollStackDown(int units){
        //viewDex.appMainAdmin.vgControl.setImageNextInStack();
        //viewDex.appMainAdmin.vgControl.setStackLoadInBackgroundStatus(true);
        //viewDex.appMainAdmin.vgControl.stopLoadStackInBackground();
        //viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        viewDex.appMainAdmin.vgControl.setCineLoopDirection(0);
        viewDex.appMainAdmin.vgControl.setImageNextPrevInStack(0);
    }
    
    /**
     */
    @Override
    public void scrollStackUp(int units){
        //viewDex.appMainAdmin.vgControl.setStackLoadInBackgroundStatus(true);
        //viewDex.appMainAdmin.vgControl.stopLoadStackInBackground();
        //viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        viewDex.appMainAdmin.vgControl.setCineLoopDirection(1);
        viewDex.appMainAdmin.vgControl.setImageNextPrevInStack(1);
    }
    
    /**
     * Set the canvas control mode.
     * @param the <code>CanvasControlMode<code/> constant.
     */
    @Override
    public void setCanvasControlMode(int mode){
        canvasControlMode = mode;
    }
    
    /**
     * Get the canvas control mode.
     * @return the <code>CanvasControlMode<code/> constant.
     */
    @Override
    public int getCanvasControlMode(){
        return canvasControlMode;
    }

    /**
     * Check if there is a <code>Localization.SELECT<code/> mark insde
     * a predefine circle with the mouse hot-point as the center of the circle.
     */
    @Override
    public boolean localizationInsideShapeExist(Point2D p){
        AffineTransform atx;
        Point2D xy = null;
        Point2D setPoint = null;
        Point2D p2 = null;
        boolean selStatus = false;
        boolean status = false;
        
        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = atx.inverseTransform((new Point((int) p.getX(), (int) p.getY())), p2);
        } catch (Exception exp) {
            System.out.println(exp);
        }
        // See if there is a localization mark within a circle, created with the
        // mouse hot-point as the center of the circle.
        int r1 = 20;
        Shape ellipse = new Ellipse2D.Double((xy.getX() - r1), (xy.getY() - r1), (r1 * 2), (r1 * 2));

        //changed
        Point2D localizationInsideShape = viewDex.localization.getLocalizationInsideShape(ellipse);
        if(localizationInsideShape != null)
            status = true;
        //selStatus = viewDex.localization.getLocalizationSelectStatus(localizationInsideShape);
        return status;
    }
    
    /**
     * Check if there is a <code>Localization.SELECT<code/> on the image.
     */
    @Override
    public boolean getLocalizationSelectStatus(){
        return viewDex.localization.localizationSelectStatusExist();
    }
    
     /**
      * test
     * setCineLoopStatus.
     */
    @Override
    public void setCineLoopStatus(boolean status){
        viewDex.appMainAdmin.vgControl.setCineLoopStatus(status);
    }
    
    /**
     * test
     */
    @Override
    public void runStudyAsCineLoop(){
        viewDex.vgCineLoopPanel.setLoopValueButtonSelected();
        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        viewDex.appMainAdmin.vgControl.runStudyAsCineLoop();
    }
    
    /*
     * test
     */
    @Override
    public void stopStudyCineLoop(){
        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
    }
    
     /*
     * test
     */
    @Override
    public void stopStackLoadInBackground(){
        viewDex.appMainAdmin.vgControl.setStackLoadInBackgroundStatus(true);
        viewDex.appMainAdmin.vgControl.stopLoadStackInBackground();
    }
    
    /*
     */
    @Override
    public boolean getImageLoadingWorkerStatus(){
        return viewDex.appMainAdmin.vgControl.getImageLoadingWorkerStatus();
    }
    
    /*
     */
    @Override
    public boolean getCineLoopRunningStatus(){
        return viewDex.appMainAdmin.vgControl.getCineLoopRunningStatus();
    }
}
