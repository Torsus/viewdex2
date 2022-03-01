
/* @(#) Pan.java 05/12/2005
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


/* Scrolls an image displayed on a canvas.
 * The canvas object must implement the ImageManipulatorInterface interface.
 **/

public class Pan implements PanInterface{
    protected ViewDex viewDex;
    protected ImageCanvasInterface canvas;
    protected AffineTransform atx = new AffineTransform();
    private int canvasControlMode;
    
    // Pan variables
    protected Point panOffset = new Point(0,0);
    private Point diff = new Point(0,0);
    private Point scrollAnchorImageSpace  = new Point(0,0);
    
    Point2D xy = null;
    
    // Canvas pixel information update.
    int mousePosX = 0;
    int mousePosY = 0;
    Point2D p1 = null;
    
    /** Constructor
     */
    public Pan(){
    }
    
    /** Constructor
     * @param imagecanvasmanipulator the interface that the canvas component
     * implements and on which the image is drawn.
     */
    public Pan(ViewDex viewdex, ImageCanvasInterface imagemanipulator){
        this.viewDex = viewdex;
        this.canvas = imagemanipulator;
    }
    
    /** Set the panOffset property.
     * @param panOffset the panOffset property.
     */
    @Override
    public void setPanOffset(Point panOffset){
        this.panOffset = panOffset;
        canvas.setPanOffset(panOffset);
    }
    
    /** Returns the panOffset property.
     * @return the panOffset property.
     */
    @Override
    public Point getPanOffset(){
        return panOffset;
    }
    
    /** Starts the scroll and sets the anchor point.
     * @param x the x coordinate of the scroll anchor.
     * @param y the y coordinate of the scroll anchor.
     **/
    @Override
    public void startScroll(int x, int y){
        atx = canvas.getTransform();
        // Create a new anchor point so that everytime the mouse button is
        // clicked, the image does not move instead the anchor point moves.
        
        // convert the cordinats from userspace to imagespace
        try {
            xy = null;
            xy = atx.inverseTransform((Point2D)(new Point(x,y)), xy);
            scrollAnchorImageSpace = new Point((int)(xy.getX()),(int)(xy.getY()));
            //setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }catch(Exception e) {System.out.println(e);}
    }
    
    /** Scrolls the image and starts the painting process
     * of the image at the new position.
     * @param x the x coordinate of the current position.
     * @param y the y coordinate of the current position.
     **/
    @Override
    public void scroll(int x, int y){
        if((x <0 )|| (y<0))
            return;
        
        // convert the cordinats from userspace to imagespace
        try {
            xy = null;
            xy = atx.inverseTransform((Point2D)(new Point(x,y)), xy);
            double ix  = (xy.getX()-scrollAnchorImageSpace.x);
            double iy =  (xy.getY()-scrollAnchorImageSpace.y);
            translateIncr(ix,iy);
            //translate(ix,iy);
        }catch(Exception e) {System.out.println(e);}
    }
    
    /** Translate the image and apply the tranform to the canvas.
     */
    public void translateIncr(double incrx, double incry){
        atx.translate(incrx, incry);
        
        //canvas.applyTransform(atx);
        //canvas.setTransform(incrx, incry);
        canvas.setTransform(atx, false);
        viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
        viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
        viewDex.canvas.setCanvasROIAreaUpdateTextStatus(true);
        viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
        viewDex.localization.setLocalizationOverlayListInCanvas();
        viewDex.windowLevel.setWindowLevel();
    }
    
    /** Stop the scrolling.
     */
    @Override
    public void stopScroll(){
        //setCursor(Cursor.getDefaultCursor());
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
     * Set the mouse x,y position.
     */
    @Override
    public void setXYPosition(int x, int y){
        atx = canvas.getTransform();
        if((x <0 )|| (y<0))
            return;
     
        //System.out.println("Pan:setXYPosition" + x + " " + y);
        canvas.setCanvasOverlayMousePositionPixelValue(x, y, true);
    }
    
    /**
     */
    @Override
    public void setFocus(){
        viewDex.requestFocusInWindow();
    }
    
    /**
     * NOT IN USE
     */
    @Override
    public void resetScrollAnchor(){
        scrollAnchorImageSpace  = new Point(0,0);
    }
}