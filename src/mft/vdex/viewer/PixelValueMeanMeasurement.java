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

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbROIPixelValue;
import mft.vdex.ds.StudyDbROIPixelValueD;

/**
 *
 * @author sune
 */
public class PixelValueMeanMeasurement {
     private ViewDex viewDex;

    public PixelValueMeanMeasurement(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     * Create a new ROI object and set the (ROI.ACTIVE) status to true.
     */
    public void setROIPixelValueItemStartPoint(Point userSpaceInt, Point imageSpaceInt,
            Point userSpaceDouble, Point imageSpaceDouble) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        StudyDbROIPixelValue roi = new StudyDbROIPixelValue(userSpaceInt, imageSpaceInt, userSpaceDouble, imageSpaceDouble, true);
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();
        if (roiList != null) {
            roiList.add(roi);
        }

        // set cnt
        for (int i = 0; i < roiList.size(); i++) {
            StudyDbROIPixelValue item = roiList.get(i);
            item.setItemCnt(i);
        }
    }

     /**
     * Set the ROI width & height.
     */
    public void setROIPixelValueWidthHeight(int w, int h) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    roi.setWidthHeight(w, h);
                }
            }
        }
    }

    /**
     * Update the ROI adjusted start point.
     */
    public void setROIPixelValueStarttPointAdj(Point userSpaceStartPointAdj,
            Point userSpaceCurrentPointInt, Point2D imageSpaceStartPointAdj, Point2D imageSpaceCurrentPoint) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    roi.setUserSpaceStartPointAdj(userSpaceStartPointAdj);
                    roi.setUserSpaceCurrentPointInt(userSpaceCurrentPointInt);
                    roi.setImageSpaceStartPointAdj(imageSpaceStartPointAdj);
                    roi.setImageSpaceCurrentPoint(imageSpaceCurrentPoint);
                }
            }
        }
    }

    /**
     * Update the ROI mean value
     */
    public void setROIPixelValueMean(int mean) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    roi.setMean(mean);
                }
            }
        }
    }

    /**
     * Update the ROI mean value
     */
    public void setROIPixelValueShape(Shape s) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    roi.setShape(s);
                }
            }
        }
    }

    /**
     * Update the ROI pixel value item stop point.
     * NOT IN USE
     */
    public void setROIPixelValueItemStopPoint(Point userSpaceP, Point imageSpaceP) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    //roi.updateROI(userSpaceP, imageSpaceP);
                    roi.setROIActiveStatus(false);
                }
            }
        }
    }

    /**
     * Update the ROI pixel value item stop point.
     */
    public void setROIPixelValueItemActiveStatus() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    //roi.updateROI(userSpaceP, imageSpaceP);
                    roi.setROIActiveStatus(false);
                }
            }
        }
    }

     /**
     * Create the Line object.
     */
    public void createROIPixelValueItemLineObject() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                boolean status = roiList.get(i).getROIActiveStatus();
                if (status == true) {
                    StudyDbROIPixelValue roi = roiList.get(i);
                    Point2D startPointImageSpaceDouble = roi.getImageSpaceStartPointDouble();
                    Point2D currentPointImageSpaceDouble = roi.getImageSpaceCurrentPointDouble();
                    if (startPointImageSpaceDouble != null && currentPointImageSpaceDouble != null) {
                        Line2D l = new Line2D.Double(startPointImageSpaceDouble, currentPointImageSpaceDouble);
                        roi.setROILineObject(l);
                        //roi.updateROI(userSpaceP, imageSpaceP);
                    }
                    roi.setROIActiveStatus(false);
                }
            }
        }
    }

    /**
     * Set the ROI (drawing) overlay item in canvas.
     */
    public void setROIPixelValueInCanvasAndNoRender() {
        StudyDbImageNode imageNode =  viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbROIPixelValue> roiList = imageNode.getROIPixelValueList();
        ArrayList<StudyDbROIPixelValueD> rList = new ArrayList<StudyDbROIPixelValueD>();

        if (roiList != null) {
            for (int i = 0; i < roiList.size(); i++) {
                //init
                Point2D userSpaceStartPoint = null;
                Point2D userSpaceCurrentPoint = null;
                Point2D userSpaceStartPointAdj = null;
                Point2D imageSpaceStartPoint = null;
                Point2D imageSpaceStartPointAdj = null;
                Point2D userSpaceStartPointDouble = null;
                Point2D userSpaceCurrentPointDouble = null;
                Point2D imageSpaceStartPointDouble = null;
                Point2D imageSpaceCurrentPointInt = null;
                Point2D imageSpaceCurrentPointDouble = null;

                double width, height = 0;
                Point2D width2 = new Point(0, 0);
                Point2D height2 = new Point(0, 0);
                int mean = 0;
                Shape shape;

                //boolean status = roiList.get(i).getROIActiveStatus();

                StudyDbROIPixelValue roi = roiList.get(i);
                //userSpaceStartPoint = roi.getImageSpaceStartPointInt();
                imageSpaceStartPoint = roi.getImageSpaceStartPointInt();
                imageSpaceStartPointAdj = roi.getImageSpaceStartPointAdj();
                imageSpaceCurrentPointInt = roi.getImageSpaceCurrentPointInt();

                //userSpaceStartPointAdjIntAdj = roi.getUserSpaceStartPointAdj();
                //userSpaceCurrentPointInt = roi.getUserSpaceCurrentPointInt();
                //imageSpaceStartPointDouble = roi.getImageSpaceStartPointDouble();
                //imageSpaceCurrentPointDouble = roi.getImageSpaceCurrentPointDouble();
                width = (double) roi.getWidth();
                height = (double) roi.getHeight();
                mean = roi.getMean();
                //shape = roi.getShape();

                // convert cordinats from imagespace to userspace
                try {
                    AffineTransform atx = viewDex.canvas.getTransform();
                    userSpaceStartPoint = atx.transform(imageSpaceStartPoint, null);
                    userSpaceStartPointAdj = atx.transform(imageSpaceStartPointAdj, null);
                    userSpaceCurrentPoint = atx.transform(imageSpaceCurrentPointInt, null);
                    width2 = atx.transform(new Point2D.Double(width, height), null);
                    //userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, userSpaceStartPointDouble);
                    //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointInt, userSpaceCurrentPointDouble);

                } catch (Exception e) {
                    System.out.println("VgControl.setROIInCanvasAndRender: Error");
                }

                double d = getROIPixelValueDiameter(userSpaceStartPoint, userSpaceCurrentPoint);
                width = d;
                height = d;

                rList.add(new StudyDbROIPixelValueD(
                        userSpaceStartPoint,
                        userSpaceStartPointAdj,
                        userSpaceCurrentPoint,
                        width,
                        height,
                        mean));

                /*
                rList.add(new StudyDbROIPixelValueD(
                userSpaceStartPointInt,
                userSpaceCurrentPointInt,
                userSpaceStartPointIntAdj,
                imageSpaceStartPointInt,
                null,
                width,
                height,
                mean,
                shape
                ));
                 * */
            }
            viewDex.pixelValueMean.drawROIOnCanvasAndRender(rList, false);
        }
    }

    /*
     */
    public double getROIPixelValueDiameter(Point2D userSpaceStartPoint, Point2D userSpaceCurrentPoint) {

        double xd = userSpaceCurrentPoint.getX() - userSpaceStartPoint.getX();
        double yd = userSpaceCurrentPoint.getY() - userSpaceStartPoint.getY();
        double wid = xd;
        double ht = yd;

        if (xd < 0) {
            wid = -xd;
        }
        if (yd < 0) {
            ht = -yd;
        }
        double d;
        if (wid >= ht) {
            d = wid;
        } else {
            d = ht;
        }
        double x2 = Math.pow(userSpaceCurrentPoint.getX() - userSpaceStartPoint.getX(), 2);
        double y2 = Math.pow(userSpaceCurrentPoint.getY() - userSpaceStartPoint.getY(), 2);
        double dd = Math.sqrt(x2 + y2);
        d = (int) dd;

        return d;
    }

    /**
     * Calculate the distance.
     */
    private double calculateROIPixelValueDistance(StudyDbImageNode imageNode,
            Point2D startImageSpace, Point2D currentImageSpace) {
        double distance = 0;
        double Rs = 0.0;
        double Cs = 0.0;

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
        // NominalScannedPixelSpacing (0018, 1164)


        //distance = Math.sqrt(((c2 - c1) * Cs)**2 + ((r2 - r1) * Rs)**2);
        distance = Math.sqrt(Math.pow(((c2 - c1) * Cs), 2) + Math.pow(((r2 - r1) * Rs), 2));

        return distance;
    }

    /**
     * Delete ROIPixelValue list
     */
    public void deleteROIPixelValueListAndNoRender() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        if (imageNode != null) {
            imageNode.deleteROIPixelValueList();
        }
        viewDex.canvas.setCanvasROIPixelValueMeanUpdateValue(null);
    }

    /**
     */
    public String getROIPixelValueStatus() {
        return null;
    }

    /**
     */
    public String getROIPixelValueValue() {
        return null;
    }
}
