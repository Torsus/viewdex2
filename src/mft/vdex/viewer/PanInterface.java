/* @(#) PanInterface.java 05/12/2005
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
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.beans.*;


/** Specifies methods to scroll an image on a canvas.
  **/
 public interface PanInterface {
    /** Gets the panOffset property.
      * @param panOffset the offset by which the currently displayed image is moved
      * from the previous position. 
      */
    public void setPanOffset(Point panOffset);

    /** Returns the panOffset property. 
      * @return the panOffset.
      */
    public Point getPanOffset();
    
      /**Starts the scroll and sets the anchor point.
     * @param x the x coordinate of the scroll anchor.
     * @param y the y coordinate of the scroll anchor.
     **/
    public void startScroll(int x, int y);

    /**Scrolls the image.
     * @param x the x coordinate of the current position.
     * @param y the y coordinate of the current position.
     **/
    public void scroll(int x, int y);

    /** Stops scroll.
      */
    public void stopScroll();
    
    /** Set the context menu constants.
     */
    public void setCanvasControlMode(int mode);
 
    /** Get the context menu constants.
      *@return the context menu constants.
      */
    public int getCanvasControlMode();
    
    /**
     * Set the mouse x,y position.
     */
    public void setXYPosition(int x, int y);
    
    /**
     * setFocus
     */
    public void setFocus();
    
    /*
     */ 
    public void resetScrollAnchor();
 }