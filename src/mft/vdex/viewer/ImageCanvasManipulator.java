/* @(#) ImageCanvasManipulator.java 05/12/2003
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
import java.awt.geom.*;
import javax.media.jai.PlanarImage;


public interface ImageCanvasManipulator{
    /** Type AWT Image */
    public static final int TYPE_AWT_IMAGE = 1;
    
    /** Type BufferedImage */
    public static final int TYPE_BUFFERED_IMAGE = 2;
    
    /** Returns the image size.
     * @return the image size.
     */
    //public Dimension getImageSize();
    
    /** Returns the canvas size.
     * @return the canvas size.
     */
    public Dimension getImageCanvasSize();
    
    
    /** Sets the OriginalImage property.
     * @param BufferedImage the image object.
     */
    //public void setOriginalImage(BufferedImage image);
    
    
    /** Returns the OffScreenImage property.
     * @return the BufferedImage
     */
    public PlanarImage getOffScreenImage();
    
    
    /** Returns the OriginalImage property.
     * @return the BufferedImage
     */
    //public BufferedImage getOriginalImage();
    
    
    /** Sets the image type property.
     * @param imageType the image type.
     */
    //public void setImageType(int imageType);
    
    
    /** Returns the imageType property.
     *@return the image type.
     */
    //public int getImageTYpe();
    
    
    /** Sets the displaymode
     *@ The image is updated.
     * @param mode the display mode
    */
    //public void setDisplayMode(int mode);
    
    
    /** Returns the display mode.
      * @return the current display mode.
      */
    //public int getDisplayMode();
    
    
    /** This methods paints the current image.
     */
   //public void paintImage();
    
    
    /** Adds a mouse listener object.
     *@param ml the MouseListener.
     */
    public void addMouseListener(MouseListener ml);
    
    
    /** Adds a mouse motion listener object.
     *@param ml the MouseListener.
     */
    public void addMouseMotionListener(MouseMotionListener e);
    
    
    /** Removes a mouse listener object.
     *@param ml the MouseListener.
     */
    public void removeMouseListener(MouseListener ml);
    
    
    /** Removes a mouse motion listener object.
     *@param ml the MouseMotionListener.
     */
    public void removeMouseMotionListener(MouseMotionListener ml);
    
    
    /** Sets the cursor
     *@param cursor the Cursor object.
     */
    public void setCursor(Cursor cursor);
    
    
    /** Gets the transform property.
      * @return the current transform object.
      */
    public AffineTransform getTransform();

    
    /** Sets the transform property.
      * @param the transform.
      */
    public void setTransform(AffineTransform at);

    
    /** Applies the transform
     * @param the transform.
      */
    public void applyTransform(AffineTransform atx);
    
    
    /** Set the interpolation mode property.
      * @param the interpolation mode.
      */
    public void setInterpolationMode(int interpolationmode);

    /** Return the interpolation mode.
      * @return the interpolation mode
      */ 
    public int getInterpolationMode();
    
    
    /** Gets the panOffset property.
      * @param panOffset the offset by which the currently displayed image is moved
      * from the previous position.
      */
    public Point getPanOffset();
    
    
    /** Sets the panOffset property.
      * @param panOffset the offset by which the currently displayed image is moved
      * from the previous position.
      */
   public void setPanOffset(Point panOffset);
    
    
    /* Sets the  display Mode and update the image */
    //public void setDisplayModeUpdate(int mode);
    
    
    // Zoom
    //public void setMagFactor(double magFactor);
    //public double getMagFactor();
    //public double getZoomMagAbsoluteValue();
   
   
   /** Set the display mode.
    * Set one of DISPLAY_TO_FIT, DISPLAY_SCALED or DISPLAY_ORIG.
    * @param the displayMode.
    */
   public void setDisplayMode(int displaymode);
}
