/*
 * StudyDbROI.java
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 20 november 2008, 11:22.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author sune
 */
public class StudyDbROI implements Serializable {

    private boolean roiActiveStatus = false;
    private boolean roiSelectStatus = false;
    private int itemCnt = 0;
    private Point2D userSpaceStartPointInt;
    private Point2D userSpaceStartPointDouble;
    private Point2D userSpaceCurrentPointInt;
    private Point2D userSpaceCurrentPointDouble;
    
    private Point2D imageSpaceStartPointInt;
    private Point2D imageSpaceStartPointDouble;
    private Point2D imageSpaceCurrentPointInt;
    private Point2D imageSpaceCurrentPointDouble;

    private GeneralPath pathUser;
    private GeneralPath pathImage;
    private int pixelCount = 0;
    private double area = 0;
    private int mean = 0;


    /**
     *
     * @param userSpaceP
     * @param imageSpaceP
     * @param sta
     */
    public StudyDbROI(Point2D userSpaceP, Point2D imageSpaceP, boolean sta) {
        this.userSpaceStartPointInt = userSpaceP;
        this.imageSpaceStartPointInt = imageSpaceP;
        this.roiActiveStatus = sta;
    }
    
    /**
     *
     * @param userSpaceInt
     * @param imageSpaceInt
     * @param userSpaceDouble
     * @param imageSpaceDouble
     * @param sta
     */
    public StudyDbROI(Point2D userSpaceInt, Point2D imageSpaceInt,
            Point2D userSpaceDouble, Point2D imageSpaceDouble, boolean sta) {
        this.userSpaceStartPointInt = userSpaceInt;
        this.imageSpaceStartPointInt = imageSpaceInt;
        this.userSpaceStartPointDouble = userSpaceDouble;
        this.imageSpaceStartPointDouble = imageSpaceDouble;
        this.roiActiveStatus = sta;
    }

    /**
     *
     * @param pU
     * @param pI
     * @param usspd
     * @param sta
     */
    public StudyDbROI(GeneralPath pU, GeneralPath pI,
            Point2D userspacestartpointdouble, Point2D imagespacestartpointdouble,
            boolean sta){
        this.pathUser = pU;
        this.pathImage = pI;
        this.userSpaceStartPointDouble = userspacestartpointdouble;
        this.imageSpaceStartPointDouble = imagespacestartpointdouble;
        this.roiActiveStatus = sta;
    }
    
     /**
      *
      * @param cnt
      */
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }
    
    /**
     *
     * @return
     */
    public int getItemCnt() {
        return itemCnt;
    }

    /**
     *
     * @param sta
     */
    public void setROIActiveStatus(boolean sta) {
        roiActiveStatus = sta;
    }
    
     /**
      *
      * @return
      */
    public boolean getROIActiveStatus() {
        return roiActiveStatus;
    }
    
     /**
      *
      * @param sta
      */
    public void setROISelectStatus(boolean sta) {
        roiSelectStatus = sta;
    }
    
    /**
     *
     * @return
     */
    public boolean getROISelectStatus() {
        return roiSelectStatus;
    }

     /**
      *
      * @param sta
      */
    public void setPixelCnt(int n) {
        pixelCount = n;
    }

    /**
      *
      * @param sta
      */
    public int getPixelCnt() {
        return pixelCount;
    }
    
    /**
     * 
     * @param a
     */
    public void setArea(double a) {
        area = a;
    }
    
    /**
     * 
     * @return
     */
    public double getArea() {
        return area;
    }

    public void setMean(int m){
        mean = m;
    }

    public int getMean(){
        return mean;
    }

    /**
     *
     * @param userSpaceInt
     * @param imageSpaceInt
     * @param userSpaceDouble
     * @param imageSpaceDouble
     */
    public void updateROI(Point2D userSpaceInt, Point2D imageSpaceInt,
            Point2D userSpaceDouble, Point2D imageSpaceDouble){
        userSpaceCurrentPointInt = userSpaceInt;
        imageSpaceCurrentPointInt = imageSpaceInt;
        userSpaceCurrentPointDouble = userSpaceDouble;
        imageSpaceCurrentPointDouble = imageSpaceDouble;
    }
    
    /**
     *
     * @return
     */
    public Point2D getImageSpaceStartPointInt() {
        return imageSpaceStartPointInt;
    }

     /**
     *
     * @return
     */
    public Point2D getImageSpaceStartPointDouble() {
        return imageSpaceStartPointDouble;
    }
    
     /**
      *
      * @return
      */
    public Point2D getImageSpaceCurrentPointInt() {
        return imageSpaceCurrentPointInt;
    }

    /**
      *
      * @return
      */
    public Point2D getImageSpaceCurrentPointDouble() {
        return imageSpaceCurrentPointDouble;
    }

     /**
      *
      * @return
      */
    public Point2D getUserSpaceStartPointDouble() {
        return userSpaceStartPointDouble;
    }

    /********************************************
     * GeneralPath
     * *****************************************/

    /**
     *
     * @param p
     */
    public void setGeneralPathUser(GeneralPath p){
        pathUser= p;
    }

    /**
     *
     * @return
     */
    public GeneralPath getGeneralPathUser() {
        return pathUser;
    }

    /**
     *
     * @param p
     */
    public void setGeneralPathImage(GeneralPath p){
        pathImage = p;
    }

    /**
     * 
     * @return
     */
    public GeneralPath getGeneralPathImage() {
        return pathImage;
    }
}