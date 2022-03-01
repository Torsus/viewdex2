/*
 * StudyDbROIV.java
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/* Created 20160216 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.ds;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author sune
 */
public class StudyDbROIV {
    private Point2D userSpaceStartPointDouble;
    private Shape shapeUser;
    private Shape shapeImage;
    private int pixelCount = 0;
    private double area = 0;
    private int mean = 0;

    /**
     * 
     * @param sU
     * @param sI
     * @param usspi
     */
    public StudyDbROIV(Shape sU, Shape sI, Point2D usspi, int pc, double a, int m) {
        this.shapeUser = sU;
        this.shapeImage = sI;
        this.userSpaceStartPointDouble = usspi;
        this.pixelCount = pc;
        this.area = a;
        this.mean = m;
    }

    /**
     *
     * @return userSpaceStartPointDouble
     */
    public Point2D getUserSpaceStartPointDouble() {
        return userSpaceStartPointDouble;
    }

    /**
     *
     * @return shapeUser
     */
    public Shape getShapeUser() {
        return shapeUser;
    }

    /**
     *
     * @return shapeImage
     */
    public Shape getShapeImage() {
        return shapeImage;
    }

    /**
     *
     * @return pixelCount
     */
    public int getPixelCount() {
        return pixelCount;
    }

    /**
     *
     * @param n
     */
    public void setPixelCount(int n) {
        pixelCount = n;
    }

    /**
     *
     * @return area
     */
    public double getArea(){
        return area;
    }

    /**
     *
     * @return mean
     */
    public int getMean(){
        return mean;
    }
}
