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

import java.awt.geom.Point2D;

/**
 *
 * @author sune
 */
public class StudyDbROID {
    private Point2D startPointUserSpace;
    private Point2D endPointUserSpace;
    private Point2D startPointImageSpace;
    private Point2D endPointImageSpace;
    private double distance;
    
    public StudyDbROID(Point2D startUserSpace, Point2D currentUserSpace,
            Point2D startPointImageSpace, Point2D currentPointImageSpace,
            double distance){
        this.startPointUserSpace = startUserSpace;
        this.endPointUserSpace = currentUserSpace;
        this.startPointImageSpace = startPointImageSpace;
        this.endPointImageSpace = currentPointImageSpace;
        this.distance = distance;
    }
   
    /*
     */
    public Point2D getStartPointUserSpace(){
        return startPointUserSpace;
    }
    
    /*
     */
    public Point2D getEndPointUserSpace(){
        return endPointUserSpace;
    }
    
    /*
     */
    public Point2D getStartPointImageSpace(){
        return startPointImageSpace;
    }
    
    /*
     */
    public Point2D getEndPointImageSpace(){
        return endPointImageSpace;
    }
    
    /*
     */
    public double getDistance(){
        return distance;
    }
}
