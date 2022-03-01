/* @(#) DistanceMesaurement.java 03/15/2017
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Author Sune Svensson
 */
package mft.vdex.viewer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbROI;
import mft.vdex.ds.StudyDbROID;

public class DistanceMeasurement {

    private ViewDex viewDex;

    public DistanceMeasurement(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     * Create a new ROI object and set the (ROI.ACTIVE) status to true.
     */
    public void setROIDistanceItemStartPoint(Point2D userSpaceInt, Point2D imageSpaceInt,
            Point2D userSpaceDouble, Point2D imageSpaceDouble) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        StudyDbROI roi = new StudyDbROI(userSpaceInt, imageSpaceInt, userSpaceDouble, imageSpaceDouble, true);
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();

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
     * Update the ROI item current point.
     */
    public void setROIDistanceItemCurrentPoint(Point2D userSpaceInt, Point2D imageSpaceInt,
            Point2D userSpaceDouble, Point2D imageSpaceDouble) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROI roi = roiList.get(i);
                    roi.updateROI(userSpaceInt, imageSpaceInt, userSpaceDouble, imageSpaceDouble);
                }
            }
        }
    }

    /**
     * Update the ROI item stop point. NOT IN USE
     */
    public void setROIItemStopPoint(Point2D userSpaceP, Point2D imageSpaceP) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();

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
     * Update the ROI item stop point.
     */
    public void setROIDistanceItemActiveStatus() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();

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
     * Create the Line object. NOT IN USE
     */
    public void createROIItemLineObject() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROI roi = roiList.get(i);
                    Point2D startPointImageSpaceDouble = roi.getImageSpaceStartPointDouble();
                    Point2D currentPointImageSpaceDouble = roi.getImageSpaceCurrentPointDouble();
                    if (startPointImageSpaceDouble != null && currentPointImageSpaceDouble != null) {
                        Line2D l = new Line2D.Double(startPointImageSpaceDouble, currentPointImageSpaceDouble);
                        //roi.setROILineObject(l);
                        //roi.updateROI(userSpaceP, imageSpaceP);
                    }
                    roi.setROIActiveStatus(false);
                }
            }
        }
    }

    /**
     * Set the ROI (drawing) overlay item in canvas. Transform cordinates from
     * image- to userspace.
     */
    public void setROIDistanceInCanvasAndNoRender() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROI> roiList = imageNode.getROIDistanceList();
        ArrayList<StudyDbROID> dList = new ArrayList<StudyDbROID>();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                // init
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

                // convert coordinats from imagespace to userspace
                try {
                    AffineTransform atx = viewDex.canvas.getTransform();
                    //userSpaceStartPointInt = atx.transform(imageSpaceStartPointInt, userSpaceStartPointInt);
                    //userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, userSpaceStartPointDouble);
                    userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, null);
                    //userSpaceCurrentPointInt = atx.transform(imageSpaceCurrentPointInt, userSpaceCurrentPointInt);
                    //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, userSpaceCurrentPointDouble);
                    userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, null);

                } catch (Exception e) {
                    System.out.println("VgControl.setROIInCanvasAndRender: Error");
                }
                //dList.add(new StudyDbROID(userSpaceStartPointInt, userSpaceCurrentPointInt,
                //imageSpaceStartPointInt, imageSpaceCurrentPointInt));

                // calculate the distance
                //20160405
                double distance = 0;
                if ((imageNode != null) && imageSpaceStartPointDouble != null && imageSpaceCurrentPointDouble != null) {
                    distance = calculateROIDistance(imageNode, imageSpaceStartPointDouble, imageSpaceCurrentPointDouble);
                }

                //double distance = calculateROIDistance(imageNode, imageSpaceStartPointDouble, imageSpaceCurrentPointDouble);
                dList.add(new StudyDbROID(userSpaceStartPointDouble, userSpaceCurrentPointDouble,
                        imageSpaceStartPointDouble, imageSpaceCurrentPointDouble, distance));
            }
            viewDex.distance.drawROIDistanceOnCanvasAndRender(dList, false);
        }
    }

    /**
     * Calculate the distance.
     * @param imageNode
     * @param startImageSpace
     * @param currentImageSpace
     * @return 
     */
    private double calculateROIDistance(StudyDbImageNode imageNode,
            Point2D startImageSpace, Point2D currentImageSpace) {
        double distance = 0;
        double Rs = 0.0;    // Row spacing
        double Cs = 0.0;    // Column spacing

        double c1 = currentImageSpace.getX();
        double r1 = currentImageSpace.getY();

        double c2 = startImageSpace.getX();
        double r2 = startImageSpace.getY();

        String modality = imageNode.getModality();
        double[] pixelSpacing = imageNode.getPixelSpacing();
        double[] imagerPixelSpacing = imageNode.getImagerPixelSpacing();
        double[] nominalScannedPixelSpacing = imageNode.getNominalScannedPixelSpacing();
        double estimatedRadiographicMagnificationFactor = imageNode.getEstimatedRadiographicMagnificationFactor();
        //int imagePlanePixelSpacing = imageNode.getImagePlanePixelSpacing();
        
        // 0018,1164 ImagerPixelSpacing
        // 0028,0030 PixelSpacing
        // 0018,2010 NominalScannedPixelSpacing
        
        // PixelSpacing is used if defined.
        // ImagerPixelSpacing or alternativt NominalScannedPixelSpacing is used
        // if PixelSpacing is not defined.
        
        if (pixelSpacing != null) {
            Rs = pixelSpacing[0];
            Cs = pixelSpacing[1];
        } else if (imagerPixelSpacing != null && nominalScannedPixelSpacing == null) {
            Rs = imagerPixelSpacing[0];
            Cs = imagerPixelSpacing[1];
        } else if (imagerPixelSpacing == null && nominalScannedPixelSpacing != null) {
            Rs = nominalScannedPixelSpacing[0];
            Cs = nominalScannedPixelSpacing[1];
        }
        
        // zzzzz
        // test DSA & MRA images
        /*
        if(estimatedRadiographicMagnificationFactor != 0){
            Rs = Rs * 1 / estimatedRadiographicMagnificationFactor;
            Cs = Cs * 1 / estimatedRadiographicMagnificationFactor;
        }*/
     
        distance = Math.sqrt(Math.pow(((c2 - c1) * Cs), 2) + Math.pow(((r2 - r1) * Rs), 2));

        return distance;
    }

    /**
     * Calculate the distance.
     * NOT IN USE
     */
    private double calculateROIDistance2(StudyDbImageNode imageNode,
            Point2D startImageSpace, Point2D currentImageSpace) {
        double distance = 0;
        double Rs = 0.0;    // Row spacing
        double Cs = 0.0;    // Column spacing

        double c1 = currentImageSpace.getX();
        double r1 = currentImageSpace.getY();

        double c2 = startImageSpace.getX();
        double r2 = startImageSpace.getY();

        String modality = imageNode.getModality();
        double[] pixelSpacing = imageNode.getPixelSpacing();
        double[] imagerPixelSpacing = imageNode.getImagerPixelSpacing();
        //int imagePlanePixelSpacing = imageNode.getImagePlanePixelSpacing();
        //int nominalScannedPixelSpacing = imageNode.getNominalScannedPixelSpacing();

        if (modality != null) {
            if (modality.equalsIgnoreCase("CR")
                    || modality.equalsIgnoreCase("DX")
                    || modality.equalsIgnoreCase("XA")
                    || modality.equalsIgnoreCase("XA/XRF")
                    || modality.equalsIgnoreCase("MG")) {
                if (imagerPixelSpacing != null) {
                    Rs = imagerPixelSpacing[0];
                    Cs = imagerPixelSpacing[1];
                }
            }

            if (modality.equalsIgnoreCase("CT")) {
                if (pixelSpacing != null) {
                    Rs = pixelSpacing[0];
                    Cs = pixelSpacing[1];
                }
            }
        }

        // If both Pixel Spacing (0028, 0030) and
        // Imager Pixel Spacing (0018,1164) exist,
        // Imager Pixel Spacing take priority.
        if (imagerPixelSpacing != null && pixelSpacing != null) {
            Rs = imagerPixelSpacing[0];
            Cs = imagerPixelSpacing[1];
        }

        // ImagePlanePixelSpacing
        /*
         if(modality.equalsIgnoreCase("RT")){
         if(pixelSpacing != null){
         Rs = pixelSpacing[0];
         Cs = pixelSpacing[1];
         }
         }*/
        // multifarme secondary capture
        // NominalScannedPixelSpacing (0018, 2010)
        //distance = Math.sqrt(((c2 - c1) * Cs)**2 + ((r2 - r1) * Rs)**2);
        distance = Math.sqrt(Math.pow(((c2 - c1) * Cs), 2) + Math.pow(((r2 - r1) * Rs), 2));

        return distance;
    }

    /**
     * Delete ROI distance list
     */
    public void deleteROIDistanceListAndNoRender() {
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:1");
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:2");
        imageNode.deleteROIDistanceList();
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:3");
        viewDex.canvas.setCanvasROIDistanceUpdateValue(null);
        //System.out.println("VgControl.deleteROIDistanceListAndNoRender:4");
    }

    /**
     * getROIDistanceStatus
     */
    public String getROIDistanceStatus() {
        return null;
    }

    /**
     * getROIDistanceValue()
     */
    public String getROIDistanceValue() {
        return null;
    }
}
