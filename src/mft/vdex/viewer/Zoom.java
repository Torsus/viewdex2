/* @(#) Zoom.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.viewer;

import java.awt.*;
import java.awt.geom.*;
import mft.vdex.app.ViewDex;

/** Zoom an image displayed on a canvas.
 * The canvas object must implement the CatImageCanvasManipulator interface
 */

public class Zoom implements ZoomInterface{
    protected ViewDex viewDex;
    protected ImageCanvasInterface canvas;
    protected AffineTransform atx = new AffineTransform();
    protected boolean magOn = true;
    protected double magFactor = 1.0;
    protected int magCenterX = 0;
    protected int magCenterY =0;
    protected Point zoomOffset = new Point(0,0);
    protected double zoomMagAbsoluteValue = 1.0;
    private int canvasControlMode;
    
    public Zoom(){}
    
    /** @param imagecanvas the component on which the image is drawn.
     */
    public Zoom(ViewDex viewdex, ImageCanvasInterface imagemanipulator){
        this.canvas = imagemanipulator;
        this.viewDex = viewdex;
    }
     
    /** Set the mag factor
     */
    @Override
     public void setMagFactor(double magFactor){
        this.magFactor = magFactor;
        //canvas.setMagFactor(magFactor);
    }
     
    
     /** Get the mag factor
      */
    @Override
    public double getMagFactor(){
        return magFactor;}
    
    /** Magnify
     * This method will reset any other transformation, except displaymode and flipmode.
     */
    @Override
    public void magnify(int magCenterX, int magCenterY, double magFac){
        setMagFactor(magFac);
        this.magCenterX = magCenterX;
        this.magCenterY = magCenterY;
        Point panOffset = canvas.getPanOffset();
        int x = (int)((magCenterX-panOffset.x)*magFactor)-magCenterX;
        int y = (int)((magCenterY-panOffset.y)*magFactor)-magCenterY;
        atx = canvas.getTransform();
        atx.setToTranslation(-x, -y);
        atx.scale(magFactor, magFactor);
        applyTransform(atx);
    }
    
    /**
     * Set the magnification factor. Get the transform from the canvas.
     * Called by zoom-in and zoom-out.
     */
    @Override
    public void paintImage(int magCenterX, int magCenterY, double mag, boolean render){
        setMagFactor(mag);
        int dx = this.magCenterX -magCenterX;
        int dy = this.magCenterY-magCenterY;
        this.magCenterX = magCenterX;
        this.magCenterY = magCenterY;
        //System.out.println("magCenterX = " + magCenterX);
        // convert cordinates form userspace to imagespace
        try {
            Point2D mgp = null;
            atx = canvas.getTransform();
            mgp = atx.inverseTransform((Point2D)(new Point(magCenterX, magCenterY)),(Point2D)mgp);
            double x = (mgp.getX()*mag) - mgp.getX();
            double y = (mgp.getY()*mag) - mgp.getY();
            scale(-x,-y, mag, render);
        }catch (Exception e) {System.out.println(e); }
    }
    
    /**
     * Translate and scale the image.
     */
    public void scale(double magOffsetX, double magOffsetY, double mag, boolean render){
        atx.translate(magOffsetX, magOffsetY);
        //double x = atx.getTranslateX();
        //double y = atx.getTranslateY();
        atx.scale(mag, mag);
        //applyTransform(atx);
        
        // fix UPDATE UPDATE
        //canvas.applyTransform(atx);
        canvas.setTransform(atx, false);
        
        // The rendering is done in the Localization class.
        //if(render){
        if(true){
            viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
            viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
            viewDex.windowLevel.setWindowLevel();
        }
   }
    
    /**
     * Apply the transformation to the canvas.
     */
    public void applyTransform(AffineTransform atx) {
        canvas.applyTransform(atx);
    }
    
    /** Set ZoomMagAbsoluteValue
     */
    @Override
    public void setZoomMagAbsoluteValue(double mag){
        zoomMagAbsoluteValue = mag;
    }
    
    @Override
     public void magnify(int magCenterX, int magCenterY){
        magnify(magCenterX, magCenterY, magFactor);
    }
     
    /** Set the context menu constants.
     */
    public void setCanvasControlMode(int mode){
        canvasControlMode = mode;
    }
    
     /** Get the context menu constants.
      *@return the context menu constants.
      */
    @Override
    public int getCanvasControlMode(){
        return canvasControlMode;
    }
    
    
    @Override
    public void setLocalizationMarks(){  
    }
    
    /**
     * Set the zoomIncrement default value.
     */
    public void setZoomIncrementDefault(double val){
        viewDex.zoomGUI.setZoomIncrement(val);
    }
}
