/* @(#) ZoomInterface.java 05/12/2003
 *
 * Copyright (c) 2003 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 * This code is based on code written by Lawrence Rodriges.
 */

package mft.vdex.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.beans.*;


/* Specifies methods to zoom an image.
 **/
public interface ZoomInterface {
    /** Sets the magFactor property.
     * @param magFactor the magnification factor.
     */
    public void setMagFactor(double magFactor);
    
    
    /** Gets the magFactor property.
     * @return the magFactor.
     */
    public double getMagFactor();
    
    
    /** This method magnifies the image by mag with (magCenterX, magCenterY) as the
     * center of magnification. As a side effect, the magFactor property is set to mag.
     * @param magCenterX the x coordiante of the mag center.
     * @param magCenterY the y coordiante of the mag center.
     * @param mag the mag factor.
     */
    public void magnify(int  magCenterX, int magCenterY, double mag);
    
    
    /** This method magnifies the image with (magCenterX, magCenterY) as the
     * center of magnification. The mag value is obtained from the magFactor property.
     * @param magCenterX the x coordiante of the mag center.
     * @param magCenterY the y coordiante of the mag center.
     */
    public void magnify(int  magCenterX, int magCenterY);
    
    
    /** This method magnifies the image with (magCenterX, magCenterY) as the
     * center of magnification. As a side effect, the magFactor is set to mag.
     * The magnified image becomes the new current image.
     * Subsequent calls to this method will magnify this current image.
     * The difference between magnify() and this method is that magnify()
     * doesnot change the current image. This means that subsequent calls to
     * magnify() will produce the same result if it is invoked with the same arguments.
     * On the other hand, subsequent calls to paintImage()
     * with the same mag value will result in "zoom in" or "zoom out" depending on
     * whether the mag is greater or less than 1.0.
     * @param magCenterX the x coordiante of the mag center.
     * @param magCenterY the y coordiante of the mag center.
     * @param mag the mag factor.
     * @param renderStatus tells if the image is rendered.
     */
    public void paintImage(int  magCenterX, int magCenterY, double mag, boolean renderStatus);
    
    
    /** Set the context menu constants.
     */
    public void setZoomMagAbsoluteValue(double mag);
    
    /** Get the context menu constants.
     *@return the context menu constants.
     */
    public int getCanvasControlMode();
    
    /**
     */
    public void setLocalizationMarks();
}
