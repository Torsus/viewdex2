/* @(#) GeomManip.java 01/28/2005
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
import java.awt.image.*;
import java.awt.geom.*;
import mft.vdex.app.ViewDex;

public class GeomManip implements GeomManipController {

    protected ViewDex xMedicalViewer;
    protected AffineTransform atx = new AffineTransform();
    //Rotation variables
    protected double rotationAngle = 0.0;
    protected boolean rotateOn = true;
    protected int rotationCenterX = 0;
    protected int rotationCenterY = 0;
    //Shear variables
    protected boolean shearOn = true;
    protected double shearFactor = 0.0;
    protected double shearX = 0.0, shearY = 0.0;
    protected ImageCanvasInterface imageCanvas;
    protected int flipMode = 0;

    public GeomManip() {
    }

    /** @param imagecanvas the component on which the image is drawn.
     */
    public GeomManip(ViewDex xmedicalviewer, ImageCanvasInterface imageCanvas) {
        this.imageCanvas = imageCanvas;
        this.xMedicalViewer = xmedicalviewer;
    }

    /** @param imagecanvas the component on which the image is drawn.
     */
    public void setImageManipulator(ImageCanvasInterface imageCanvas) {
        this.imageCanvas = imageCanvas;
    }

    public synchronized void setFlipMode(int mode) {
        if (mode == flipMode) {
            return;
        }
        int oldmode = flipMode;
        flipMode = mode;
    }

    public int getFlipMode() {
        return flipMode;
    }

    public void setShearFactor(double shearFactor) {
        this.shearFactor = shearFactor;
        imageCanvas.setShearFactor(shearFactor);
    }

    public double getShearFactor() {
        return shearFactor;
    }

    public double getShearFactorX() {
        return shearX;
    }

    public double getShearFactorY() {
        return shearY;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
        imageCanvas.setRotationAngle(rotationAngle);
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void rotate(double theta) {
        double ang = this.rotationAngle - theta;
        Dimension dim = imageCanvas.getImageSize();
        int wid = dim.width;
        int ht = dim.height;
        setRotationAngle(theta);
        atx = imageCanvas.getTransform();
        atx.rotate(ang, wid / 2, ht / 2);
        imageCanvas.applyTransform(atx);
    }

    public void rotate(double theta, int rotCenterX, int rotCenterY) {
        double ang = this.rotationAngle - theta;
        setRotationAngle(theta);
        atx = imageCanvas.getTransform();
        atx.rotate(ang, rotCenterX, rotCenterY);
        imageCanvas.applyTransform(atx);
    }

    public void resetAndRotate(double theta) {
        BufferedImage image = imageCanvas.getOffScreenImage();
        int wid = image.getWidth();
        int ht = image.getHeight();
        setRotationAngle(theta);
        atx.setToRotation(theta, wid / 2, ht / 2);
        imageCanvas.applyTransform(atx);
    }

    public void shear(double shx, double shy) {
        double shxIncr = shearX - shx;
        double shyIncr = shearY - shy;
        setShearFactor(shx);
        this.shearX = shx;
        this.shearY = shy;
        atx = imageCanvas.getTransform();
        atx.shear(shxIncr, shyIncr);
        imageCanvas.applyTransform(atx);
    }

    public void shearIncr(double shxIncr, double shyIncr) {
        shearX += shxIncr;
        shearY += shyIncr;
        setShearFactor(shearX);
        atx.shear(shxIncr, shyIncr);
        imageCanvas.applyTransform(atx);
    }

    public void resetAndShear(double shx, double shy) {
        shearX = shx;
        shearY = shy;
        atx.setToShear(shx, shy);
        setShearFactor(shearX);
        imageCanvas.applyTransform(atx);
    }

    /** Creates a flip transform. It first creates a reflection and then translates to
     * the current quadrant.
     * @mode the specified flip mode.
     * @param imageWid the width of the BufferedImage.
     * @param imageHt  the height of the BufferedImage.
     */
    public static AffineTransform createFlipTransform(int mode,
            int imageWid,
            int imageHt) {
        AffineTransform at = new AffineTransform();
        switch (mode) {
            case CanvasControlMode.FLIP_NORMAL:
                break;
            case CanvasControlMode.FLIP_TOP_BOTTOM:
                at = new AffineTransform(new double[]{1.0, 0.0, 0.0, -1.0});
                at.translate(0.0, -imageHt);
                break;
            case CanvasControlMode.FLIP_LEFT_RIGHT:
                at = new AffineTransform(new double[]{-1.0, 0.0, 0.0, 1.0});
                at.translate(-imageWid, 0.0);
                break;
            case CanvasControlMode.FLIP_TOP_BOTTOM_LEFT_RIGHT:
                at = new AffineTransform(new double[]{-1.0, 0.0, 0.0, -1.0});
                at.translate(-imageWid, -imageHt);
                break;
            default:
        }
        return at;
    }

    public void flip(int mode) {
        Dimension dim = imageCanvas.getImageSize();
        int wid = dim.width;
        int ht = dim.height;
        AffineTransform flipTx = createFlipTransform(mode, wid, ht);
        atx = imageCanvas.getTransform();
        atx.concatenate(flipTx);
        imageCanvas.setTransform(atx, false);
        xMedicalViewer.windowLevel.setWindowLevel();
    }

    public void resetAndFlip(int mode) {
        atx = new AffineTransform();
        flip(mode);
    }

    public void resetManipulation() {
        shearX = 0.0;
        shearY = 0.0;
        rotationAngle = 0.0;
        atx = new AffineTransform();
    }
}
