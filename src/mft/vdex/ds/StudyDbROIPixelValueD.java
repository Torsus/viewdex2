/*
 * StudyDbROI.java
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 26 november 2008, 11:16.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author sune
 */
public class StudyDbROIPixelValueD {
    private Point2D userSpaceStartPoint;
    private Point2D userSpaceStartPointAdj;
    private Point2D userSpaceCurrentPoint;
    private Point2D imageSpaceStartPoint;
    private Point2D imageSpaceEndPoint;
    private double width, height;
    private int mean;
    
    public StudyDbROIPixelValueD(Point2D ussp, Point2D usspa, Point2D uscp,
            double w, double h, int m){
        this.userSpaceStartPoint = ussp;
        this.userSpaceStartPointAdj = usspa;
        this.userSpaceCurrentPoint = uscp;
        this.width = w;
        this.height = h;
        this.mean = m;
    }
   
    /*
     */
    public Point2D getUserSpaceStartPoint(){
        return userSpaceStartPoint;
    }
    
    /*
     */
    public Point2D getUserSpaceStartPointAdj(){
        return userSpaceStartPointAdj;
    }
    
    public Point2D getUserSpaceCurrentPoint(){
        return userSpaceCurrentPoint;
    }
    
    public double getWidth(){
        return width;
    }
    
    /*
     */
    public double getHeight(){
        return height;
    }
    
    public int getMean(){
        return mean;
    }
    
    
    
    // NOT IN USE
   
    
    /*
     */
    public Point2D getImageSpaceStartPoint(){
        return imageSpaceStartPoint;
    }
    
    /*
     */
    public Point2D getImageSpaceEndPoint(){
        return imageSpaceEndPoint;
    }
}
