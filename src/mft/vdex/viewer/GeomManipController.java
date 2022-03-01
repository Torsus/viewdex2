/* @(#) GeomManipController.java 01/28/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 * This code is based on code written by Lawrence Rodriges.
 */

package mft.vdex.viewer;
//package com.vistech.imageviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.beans.*;


public interface GeomManipController{
    /** Sets the flip mode. This is a bound property . The input parameter can be
      * one of NORMAL, LEFT_RIGHT, TOP_BOTTOM, LEFT_RIGHT_TOP_BOTTON. These constants are
      * defined in the FlipMode class.
      * Call to this method would reset the viewport and the image is redraw with new flip
      * mode.
      * @param flipMode the flip mode
      */
    public void setFlipMode(int flipMode);

    /** Returns the flip mode.
      * @return the current flip mode.
      */
    public int getFlipMode();

    /** Returns the rotation angle.
      * @return the current rotation angle.
      */
    public double getRotationAngle();

    /** Sets the rotation angle.
      * @param the rotation angle.
      */
    public void setRotationAngle(double theta);

    /** Returns the shear factor.
      * @return the current shear factor.
      */
     public double getShearFactor();

    /** Sets the shear factor.
      * @param the shear factor.
      */
    public void setShearFactor(double shear);

    /** Rotates the currently displayed image. The rotation center is the mid point
      * of the image.
      * @param theta the rotation angle in radians.
      */
    public void rotate(double theta);

    /** Rotates the currently displayed image at a specified pivot point.
      * @param theta the rotation angle in radians.
      * @param rotCenterX the X rotation center.
      * @param rotCenterY the Y rotation center.
      */
    public void rotate(double theta, int rotCenterX, int rotCenterY);

    /** Flips the image.
      * @param flipMode the flip mode.
      * Four flip modes are: NORMAL, LEFT_RIGHT, TOP_BOTTOM,
      * LEFT_RIGHT_TOP_BOTTOM
      */
    public void flip(int flipMode);

    /** Shears the currently displayed image.
      * @param shx the shear in the x direction.
      * @param shy the shear in the y direction.
      */
    public void shear(double shx, double shy);

    /** Resets manipulation **/
    public void resetManipulation();

 }