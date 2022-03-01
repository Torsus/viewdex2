/* VolumeMeasurement.java 20170315
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/**
 * @author Sune Svensson
 */
package mft.vdex.viewer;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbROI;
import mft.vdex.ds.StudyDbROIV;

/**
 *
 * @author sune
 */
public class AreaMeasurement {

    private ViewDex viewDex;

    public AreaMeasurement(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     * Create a new ROI object and set the (ROI.ACTIVE) status to true.
     */
    public void setROIAreaValueStart(GeneralPath pathUser, GeneralPath pathImage,
            Point2D userSpaceStartPointDouble, Point2D imageSpaceStartPointDouble) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        StudyDbROI roi = new StudyDbROI(pathUser, pathImage,
                userSpaceStartPointDouble, imageSpaceStartPointDouble, true);
        ArrayList<StudyDbROI> roiList = imageNode.getROIAreaList();
        if (roiList != null) {
            roiList.add(roi);
        }

        // set cnt
        for (int i = 0; i < roiList.size(); i++) {
            StudyDbROI item = roiList.get(i);
            item.setItemCnt(i);
        }
    }

    /**
     * Update the ROI shape value
     */
    public void updateROIAreaValue(GeneralPath pU, GeneralPath pI,
            int nPixels, double area, int mean) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIAreaList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROI roi = roiList.get(i);
                    roi.setGeneralPathUser(pU);
                    roi.setGeneralPathImage(pI);
                    roi.setPixelCnt(nPixels);
                    roi.setArea(area);
                    roi.setMean(mean);
                }
            }
        }
    }

    /**
     * Update the ROI item stop point.
     */
    public void setROIAreaItemActiveStatus() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIAreaList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROI roi = roiList.get(i);
                    //roi.updateROI(userSpaceP, imageSpaceP);
                    roi.setROIActiveStatus(false);
                }
            }
        }
    }

    /**
     * Set the ROI area overlay item in canvas.
     * Transform cordinates from image- to userspace.
     */
    public void setROIAreaInCanvasAndNoRender() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIAreaList();
        ArrayList<StudyDbROIV> vList = new ArrayList<StudyDbROIV>();
        Point2D userSpaceStartPointDouble = null;

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                StudyDbROI roi = roiList.get(i);
                GeneralPath pathImage = roi.getGeneralPathImage();
                //Point2D userSpaceStartPointDouble = roi.getUserSpaceStartPointDouble();
                int pixelCnt = roi.getPixelCnt();
                double area = roi.getArea();
                int mean = roi.getMean();
                //System.out.println("setROIAreaInCanvasAndNoRender() mean: " + i + " " + area + " " + mean);
                Shape shapeUser = null;

                // convert cordinats from imagespace to userspace
                try {
                    AffineTransform atx = viewDex.canvas.getTransform();
                    shapeUser = atx.createTransformedShape(pathImage);
                    userSpaceStartPointDouble = atx.transform(roi.getImageSpaceStartPointDouble(), null);
                } catch (Exception e) {
                    System.out.println("VgControl.setROIAreaInCanvasAndRender: Error");
                }
                vList.add(new StudyDbROIV(shapeUser, null, userSpaceStartPointDouble, pixelCnt, area, mean));
            }
        }
        viewDex.area.drawROIAreaOnCanvasAndRender(vList, false);

        /*
        if(roiList != null && false){
        for (int i = 0; i < roiList.size(); i++){
        //Point2D userSpaceStartPointInt = null;
        Point2D userSpaceStartPointDouble = null;
        //Point2D userSpaceCurrentPointInt = null;
        Point2D userSpaceCurrentPointDouble = null;
        //Point2D imageSpaceStartPointInt = null;
        Point2D imageSpaceStartPointDouble = null;
        //Point2D imageSpaceCurrentPointInt = null;
        Point2D imageSpaceCurrentPointDouble = null;

        //boolean status = roiList.get(i).getROIActiveStatus();
        StudyDbROI roi = roiList.get(i);
        //imageSpaceStartPointInt = roi.getImageSpaceStartPointInt();
        //imageSpaceCurrentPointInt = roi.getImageSpaceCurrentPointInt();
        imageSpaceStartPointDouble = roi.getImageSpaceStartPointDouble();
        imageSpaceCurrentPointDouble = roi.getImageSpaceCurrentPointDouble();

        // convert cordinats from imagespace to userspace
        try {
        AffineTransform  atx = appMainAdmin.viewDex.canvas.getTransform();
        //userSpaceStartPointInt = atx.transform(imageSpaceStartPointInt, userSpaceStartPointInt);
        //userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, userSpaceStartPointDouble);
        userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, null);
        //userSpaceCurrentPointInt = atx.transform(imageSpaceCurrentPointInt, userSpaceCurrentPointInt);
        //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, userSpaceCurrentPointDouble);
        userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, null);

        }catch(Exception e){
        System.out.println("VgControl.setROIVolumeInCanvasAndRender: Error");
        }
        //dList.add(new StudyDbROID(userSpaceStartPointInt, userSpaceCurrentPointInt,
        //imageSpaceStartPointInt, imageSpaceCurrentPointInt));

        // Modify
        // calculate the distance
        //double distance = calculateROIVolumeDistance(imageNode, imageSpaceStartPointDouble, imageSpaceCurrentPointDouble);
        //vList.add(new StudyDbROIV(userSpaceStartPointDouble, userSpaceCurrentPointDouble,
        //      imageSpaceStartPointDouble, imageSpaceCurrentPointDouble, distance));

        }
        //appMainAdmin.viewDex.area.drawROIVolumeOnCanvasAndRender(vList, false);
        }*/
    }

    /**
     * Delete ROI distance list
     */
    public void deleteROIAreaListAndNoRender() {
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:1");
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:2");
        imageNode.deleteROIAreaList();
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:3");
        viewDex.canvas.setCanvasROIAreaUpdateValue(null);
        viewDex.canvas.setCanvasROIAreaUpdateTextStatus(false);
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:4");
    }
}
