/*
 * StudyDbLocalizationM.java
 *
 * Copyright (c) 2017 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 24 february 2017
 * Author Sune Svensson
 *
 */
package mft.vdex.ds;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class StudyDbLocalizationM implements Serializable {
    private int localizationStatus;
    private int itemCnt = 0;
    private Point2D userSpacePointDouble;

    /** Creates a new instance of StudyDbLocalization */
    public StudyDbLocalizationM(Point2D userPointDouble, int sta) {
        this.userSpacePointDouble = userPointDouble;
        this.localizationStatus = sta;
    }

    public void setLocalizationStatus(int sta) {
        localizationStatus = sta;
    }

    public int getLocalizationStatus() {
        return localizationStatus;
    }

    public int getItemCnt() {
        return itemCnt;
    }

    public void setItemCnt(int cnt) {
        itemCnt = cnt;
    }

    public Point2D getUserSpacePointDouble() {
        return userSpacePointDouble;
    }
}
