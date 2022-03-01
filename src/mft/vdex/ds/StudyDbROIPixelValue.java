/*
 * StudyDbROIPixelValue.java
 *
 * Copyright (c) 2014 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 25 mars 2014, 11:45.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author sune
 */
public class StudyDbROIPixelValue implements Serializable {

    private boolean roiActiveStatus = false;
    private boolean roiSelectStatus = false;
    
    private Point2D userSpaceStartPointInt;
    private Point2D userSpaceStartPointIntAdj;
    private Point2D userSpaceStartPointDouble;
    
    private Point2D userSpaceCurrentPointInt;
    private Point2D userSpaceCurrentPointDouble;
    
    private Point2D imageSpaceStartPointInt;
    private Point2D imageSpaceStartPointAdj;
    private Point2D imageSpaceStartPointDouble;
    private Point2D imageSpaceCurrentPointInt;
    private Point2D imageSpaceCurrentPointDouble;
    
    private Shape shape;
    private int width, height;
    private int mean;
    private int itemCnt = 0;
    private Line2D drawingLine;

    /** Creates a new instace */
    public StudyDbROIPixelValue(Point2D userSpaceP, Point2D imageSpaceP, boolean sta) {
        this.userSpaceStartPointInt = userSpaceP;
        this.imageSpaceStartPointInt = imageSpaceP;
        this.roiActiveStatus = sta;
    }
    
    /** Creates a new instance */
    public StudyDbROIPixelValue(Point2D userSpaceInt, Point2D imageSpaceInt,
            Point2D userSpaceDouble, Point2D imageSpaceDouble, boolean sta) {
        this.userSpaceStartPointInt = userSpaceInt;
        this.imageSpaceStartPointInt = imageSpaceInt;
        this.userSpaceStartPointDouble = userSpaceDouble;
        this.imageSpaceStartPointDouble = imageSpaceDouble;
        this.roiActiveStatus = sta;
    }
    
    public void setUserSpaceCurrentPointInt(Point currentPoint){
        userSpaceCurrentPointInt = currentPoint;
    }
    
    public Point2D getUserSpaceCurrentPointInt(){
        return userSpaceCurrentPointInt;
    }
    
    /*
     */
    public void setWidthHeight(int w, int h){
        width = w;
        height = h;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public void setUserSpaceStartPointAdj(Point p){
        userSpaceStartPointIntAdj = p;
    }
    
    public void setImageSpaceStartPointAdj(Point2D p){
        imageSpaceStartPointAdj = p;
    }
    
    public void setImageSpaceCurrentPoint(Point2D p){
        imageSpaceCurrentPointInt = p;
    }
    
    public Point2D getUserSpaceStartPointAdj(){
        return userSpaceStartPointIntAdj;
    }
    
    public Point2D getImageSpaceStartPointAdj(){
        return imageSpaceStartPointAdj;
    }
    
    public void setMean(int m){
        mean = m;
    }
    
    public int getMean(){
        return mean;
    }
    
     /**
     */
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }
    
    /**
     */
    public int getItemCnt() {
        return itemCnt;
    }
    
    /**
     */
    public void setROILineObject(Line2D l){
        drawingLine = l;
    }

    /**
     */
    public void setROIActiveStatus(boolean sta) {
        roiActiveStatus = sta;
    }
    
     /**
     */
    public boolean getROIActiveStatus() {
        return roiActiveStatus;
    }
    
     /** 
     */
    public void setROISelectStatus(boolean sta) {
        roiSelectStatus = sta;
    }
    
    /**
     */
    public boolean getROISelectStatus() {
        return roiSelectStatus;
    }
    
    /**
     */
    public void updateROI(Point userSpaceInt, Point imageSpaceInt,
            Point userSpaceDouble, Point imageSpaceDouble){
        userSpaceCurrentPointInt = userSpaceInt;
        imageSpaceCurrentPointInt = imageSpaceInt;
        userSpaceCurrentPointDouble = userSpaceDouble;
        imageSpaceCurrentPointDouble = imageSpaceDouble;
    }
    
     /**
     */
    public Point2D getUserSpaceStartPointInt() {
        return userSpaceStartPointInt;
    }
    
    /**
     */
    public Point2D getImageSpaceStartPointInt() {
        return imageSpaceStartPointInt;
    }
    
     /**
     */
    public Point2D getImageSpaceCurrentPointInt() {
        return imageSpaceCurrentPointInt;
    }
    
    /**
     */
    public Point2D getImageSpaceStartPointDouble() {
        return imageSpaceStartPointDouble;
    }
    
     /**
     */
    public Point2D getImageSpaceCurrentPointDouble() {
        return imageSpaceCurrentPointDouble;
    }
    
    /**
     */
    public void setShape(Shape s) {
        shape = s;
    }

    /**
     */
    public Shape getShape() {
        return shape;
    }
}