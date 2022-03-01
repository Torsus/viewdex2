/*
 * StudyDbLocalization.java
 *
 * Copyright (c) 2007 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on den 3 augusti 2007, 11:10.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.Shape;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;


public class StudyDbLocalization implements Serializable {
    private int localizationStatus;
    private boolean localizationActiveStatus;
    private boolean localizationSelectStatus;
    private int itemCnt = 0;
    private Point userSpacePointInt;
    private Point imageSpacePointInt;
    private Point2D userSpacePointDouble;
    private Point2D imageSpacePointDouble;
    private Shape ancorShape;
    private long timeStampLocalization = 0;

    /** Creates a new instance of StudyDbLocalization */
    public StudyDbLocalization(Point userPointInt, Point imagePointInt,
            Point2D userPointDouble, Point2D imagePointDouble, int sta) {
        this.userSpacePointInt = userPointInt;
        this.imageSpacePointInt = imagePointInt;
        this.userSpacePointDouble = userPointDouble;
        this.imageSpacePointDouble = imagePointDouble;
        this.localizationStatus = sta;
        this.setTimeStampLocalization();
    }

    public void setLocalizationStatus(int sta) {
        localizationStatus = sta;
    }

   public int getLocalizationStatus(){
       return localizationStatus;
    }

    public int getItemCnt() {
        return itemCnt;
    }
    
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }

    public Point getUserSpacePointInt() {
        return userSpacePointInt;
    }

    public Point getImageSpacePointInt() {
        return imageSpacePointInt;
    }
    
    public Point2D getUserSpacePointDouble() {
        return userSpacePointDouble;
    }

    public Point2D getImageSpacePointDouble() {
        return imageSpacePointDouble;
    }

    public Shape getShape() {
        return ancorShape;
    }

    private void setTimeStampLocalization(){
        timeStampLocalization = System.currentTimeMillis();
        //System.out.println("TimeStampLocalization = " + timeStampLocalization);
    }

    public long getTimeStampLocalization(){
        return timeStampLocalization;
    }

    /******************************************************/
    // To be deleted

    
    public void setLocalizationActiveStatus(boolean sta){
        localizationActiveStatus = sta;
    }

    public boolean getLocalizationActiveStatus(){
        return true;
    }

    public void setLocalizationSelectStatus(boolean sta){
        localizationSelectStatus = sta;

    }
    public boolean getLocalizationSelectStatus(){
        return true;
    }
}