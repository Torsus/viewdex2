/* @(#) PixelValue.java 25/03/2014
 *
 * Copyright (c) 2014 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
/**
 * @author Sune Svensson
 */
package mft.vdex.viewer;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbROIPixelValueD;

public class PixelValueMean {

    ViewDex viewDex;
    ImageCanvasInterface canvas;
    private AffineTransform atx;
    public final static int FREEHAND = 1;
    public final static int RECTANGLE = 2;
    public final static int ELLIPSE = 4;
    public final static int LINE = 5;
    //protected int shapeType = LINE;
    protected int shapeType = ELLIPSE;
    private Point diff = new Point(0, 0);
    private Point shapeAnchor = new Point(0, 0);
    private Point prevPoint, curPoint;
    protected GeneralPath path;
    protected int nodeCnt = -1;
    protected Shape currentShape, prevShape;
    private int canvasControlMode = CanvasControlMode.NONE;
    private BasicStroke drawingLineStroke;
    // Symbol size % stroke
    private double alZ;
    private double seZ;
    private double sellXZ;
    private double sellYZ;
    private BasicStroke localizationSymbolStroke;
    // in use
    //private Point2D xy = null;
    private Point userSpaceStartPointInt;
    private Point2D userSpaceStartPointDouble;
    private Point2D userSpaceStartPointFloat;
    private Point2D userSpaceCurrentPointInt;
    private Point2D userSpaceCurrentPointDouble;
    private Point imageSpaceStartPointInt;
    private Point2D imageSpaceStartPointDouble;
    private Point2D imageSpaceCurrentPointInt;
    private Point2D imageSpaceCurrentPointDouble;
    private Point endPointUserSpace;
    private Point endPointImageSpace;
    private boolean keyQEnable = false;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;

    public PixelValueMean(ViewDex viewDex, ImageCanvasInterface canvas) {
        this.viewDex = viewDex;
        this.canvas = canvas;
    }

    /** Initiates shape drawing at the specified position.
     * @param x the coordinate of the starting position of the shape.
     * @param y the coordinate of the starting position of the shape.
     */
    public void startDraw(int x, int y) {
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;

        // convert cordinates form userspace to imagespace
        Point2D xy = null;
        try {
            atx = canvas.getTransform();
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
        } catch (Exception e) {
            System.out.println(e);
        }

        userSpaceStartPointInt = new Point(x, y);
        imageSpaceStartPointInt = new Point((int) (xy.getX()), (int) (xy.getY()));

        PlanarImage img = canvas.getImage();
        if (imageSpaceStartPointInt.getX() < img.getMinX()
                || imageSpaceStartPointInt.getY() < img.getMinY()
                || imageSpaceStartPointInt.getX() > img.getMaxX()
                || imageSpaceStartPointInt.getY() > img.getMaxY()) {
            return;
        }

        viewDex.pixelValueMeanMeasurement.setROIPixelValueItemStartPoint(
                userSpaceStartPointInt,
                imageSpaceStartPointInt,
                null,
                null);
    }

    /*
     * Called by mouseDragged
     */
    public void draw(int x, int y) {
        Point2D xy;
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;

        switch (shapeType) {
            case ELLIPSE:
                Point userSpaceCurrentPointInt = new Point(x, y);

                /*
                diff.x = x - userSpaceStartPointInt.x;
                diff.y = y - userSpaceStartPointInt.y;
                int wid = diff.x;
                int ht = diff.y;
                Point ulhc = new Point(userSpaceStartPointInt);
                if(diff.x < 0) {
                wid = -diff.x;
                ulhc.x = x;
                }
                if(diff.y <0){
                ht = -diff.y;
                ulhc.y = y;
                }

                int d;
                if(wid >= ht)
                d = wid;
                else d = ht;

                double x2 = Math.pow(x - userSpaceStartPointInt.x, 2);
                double y2 = Math.pow(y - userSpaceStartPointInt.y, 2);
                double dd = Math.sqrt(x2 + y2);
                d = (int) dd;
                 */

                int d = (int) viewDex.pixelValueMeanMeasurement.getROIPixelValueDiameter(userSpaceStartPointInt,
                        userSpaceCurrentPointInt);
                int userSpacelhcX2 = userSpaceStartPointInt.x - d;
                int userSpacelhcY2 = userSpaceStartPointInt.y - d;
                Point userSpaceStartPointAdj = new Point(userSpacelhcX2, userSpacelhcY2);

                viewDex.pixelValueMeanMeasurement.setROIPixelValueWidthHeight(d, d);

                // Creating a ROI object & calculate meanvalue
                Point2D xyROI = new Point(0, 0);
                Point2D xyCur = new Point(0, 0);
                // convert cordinates from userspace to imagespace
                try {
                    AffineTransform atx = canvas.getTransform();
                    xyROI = atx.inverseTransform((Point2D) (new Point(userSpaceStartPointAdj.x, userSpaceStartPointAdj.y)), xyROI);
                    xyCur = atx.inverseTransform((Point2D) (new Point(userSpaceCurrentPointInt.x, userSpaceCurrentPointInt.y)), null);
                } catch (Exception e) {
                    System.out.println(e);
                }

                Point2D imageSpaceStartPointAdj = new Point2D.Double(xyROI.getX(), xyROI.getY());
                Point2D imageSpaceCurrentPointInt = new Point2D.Double(xyCur.getX(), xyCur.getY());

                viewDex.pixelValueMeanMeasurement.setROIPixelValueStarttPointAdj(userSpaceStartPointAdj,
                        userSpaceCurrentPointInt, imageSpaceStartPointAdj, imageSpaceCurrentPointInt);

                Shape s = new Ellipse2D.Double(xyROI.getX(), xyROI.getY(), d, d);
                ROIShape roiShape = new ROIShape(s);
                PlanarImage img = canvas.getImage();
                //ROI[] rois = createROIFromImage(img);
                //double[] mean = getMean(img);
                double[] meanROI = getMeanValue(img, roiShape);
                //System.out.println("PixeValue.draw mean :" + meanROI[0]);

                viewDex.pixelValueMeanMeasurement.setROIPixelValueMean((int) meanROI[0]);
                viewDex.pixelValueMeanMeasurement.setROIPixelValueShape(s);
                viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
                viewDex.windowLevel.setWindowLevel();
                break;
        }
    }

    public ROI[] createROIFromImage(RenderedImage image) {
        int numbands = image.getSampleModel().getNumBands();
        ROI[] roi = new ROI[numbands];
        if (numbands == 1) {
            roi[0] = new ROI(image);
            return roi;
        }
        int[] bandindices = new int[1];
        for (int i = 0; i < numbands; i++) {
            bandindices[0] = i;
            RenderedOp opImage = JAI.create("bandselect", image, bandindices);
            roi[i] = new ROI((PlanarImage) opImage);
        }
        return roi;
    }

    public double[] getMean(PlanarImage image) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("mean", pb);
        double[] mean = (double[]) op.getProperty("mean");
        return mean;
    }

    public double[] getMeanValue(PlanarImage img, ROI roi) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(roi);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("mean", pb);
        double[] mean = (double[]) op.getProperty("mean");
        return mean;
    }

    public double[][] computeMinMax(PlanarImage image) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("extrema", pb);
        double[][] minmax = (double[][]) op.getProperty("extrema");
        return minmax;
    }

    public double[][] computeMinMax(PlanarImage image, ROIShape roi) {
        if (image == null) {
            return null;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(roi);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("extrema", pb);
        double[][] minmax = (double[][]) op.getProperty("extrema");
        return minmax;
    }

    /** Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     */
    public void stopDraw(int x, int y) {
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN)) {
            return;
        }

        Point2D xy = null;
        try {
            // convert the cordinats from userspace to imagespace
            atx = canvas.getTransform();
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
        } catch (Exception e) {
            System.out.println(e);
        }

        //endPointUserSpace = new Point(x,y);
        //endPointImageSpace = new Point((int)(xy.getX()),(int)(xy.getY()));

        //viewDex.appMainAdmin.vgControl.setROIItemStopPoint(endPointUserSpace,
        //endPointImageSpace);

        // create the Line2D object
        //viewDex.appMainAdmin.vgControl.createROIPixelValueItemLineObject();

        setKeyQEnableStatus(false);
        viewDex.windowLevel.setWindowLevel();
        //viewDex.pan.resetScrollAnchor();

        //viewDex.appMainAdmin.vgControl.setROIInCanvasAndNoRender();

        //canvas.setCanvasOverlayDistanceMeasurementStatus(false);
        //canvas.setCanvasOverlayDistanceMeasurementValue(0,0,0,0, drawingLineStroke);
        //canvas.draw(startPointImageSpace.x, startPointImageSpace.y, endPointImageSpace.x,
        //      endPointImageSpace.y, drawingLineStroke);
        //viewDex.windowLevel.setWindowLevel();

        // cursor
        //viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        //canvas.draw(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y);
        //prevPoint = curPoint;
        //path.lineTo((float)xy.getX(), (float)xy.getY());
        //path.closePath();

        // Render the image only if the same image is still displayed when the lmb is released.
        //if(nodeCnt == viewDex.appMainAdmin.vgControl.getSelectedImageNodeCount())
        //  canvas.draw(path, drawingLineStroke);
        //viewDex.windowLevel.setWindowLevel();
    }

    /**
     * Draw the ROI object on the image.
     */
    public void drawROIOnCanvasAndRender(ArrayList<StudyDbROIPixelValueD> pvList, boolean render) {
        //canvas.setCanvasROIPixelValueMeanDrawingStatus(false);
        canvas.setCanvasROIPixelValueMeanUpdateStatus(true);
        canvas.setCanvasROIPixelValueMeanUpdateValue(pvList);

        if (render) {
            viewDex.windowLevel.setWindowLevel();
        }
    }

    /**
     * Set the canvasControlMode.
     */
    public void setCanvasControlMode(int mode) {
        canvasControlMode = mode;
    }

    /** Get the cavasControlMode.
     *@return the context menu constants.
     */
    public int getCanvasControlMode() {
        return canvasControlMode;
    }

    /**
     * NOT IN USE
     */
    public void setShape() {
        /*
        prevShape = null;
        currentShape = (Shape)path;
        //test
        //viewDex.appMainAdmin.vgControl.setShapeItem(currentShape); works fine

        //viewDex.appMainAdmin.vgControl.setShapeItem(path);

        if(nodeCnt >= 0)
        viewDex.appMainAdmin.vgControl.setShapeItem(path, nodeCnt);

        // admin
        viewDex.vgLocalizationPanel.setHideText();
        viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(true);
        viewDex.vgLocalizationPanel.showHideButton.setEnabled(true);
         */
    }

    /**
     * Draw all shape object on the selected image.
     * NOT IN USE
     */
    public void drawAllShapeSymbolOnSelectedImageAndRender(boolean render) {
        /*
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.getSelectedImageNode();
        ArrayList<StudyDbShape> shapeList = imageNode.getShapeList();
        if (!shapeList.isEmpty()) {
        for (int i = 0; i < shapeList.size(); i++) {
        StudyDbShape shape = shapeList.get(i);
        Shape s = shape.getShape();
        drawShapeObjectOnImageAndRender(s, render);
        }
        }
         */
    }

    /**
     *                                      |
     * Draw the shape object on the image.
     *  NOT IN USE                                 |
     */
    public void drawShapeObjectOnImageAndRender(Shape s, boolean render) {
        if (s != null) {
            canvas.draw(s, drawingLineStroke);

            if (render) {
                viewDex.windowLevel.setWindowLevel();
            }
        }
    }

    /**
     * Set the font & color for the symbols.
     */
    //zzzzzzzzzzzzzzzzzzzzzzzz
    /*public void setTextProperties(int width, Color color) {
    //new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    drawingLineStroke = new BasicStroke(width);
    viewDex.canvas.setCanvasROIPixelValueMeanTextProperties(font, color);
    }*/
    /**
     * get the runMode status.
     * @return
     */
    public int getRunModeStatus() {
        return viewDex.appMainAdmin.vgControl.getRunModeStatus();
    }

    /**
     * get the userMode status.
     * @return
     */
    public int getUserModeStatus() {
        //return viewDex.appMainAdmin.vgControl.getUserModeStatus();
        return 999;
    }

    /*******************************************
     * keypress
     * ****************************************/
    /**
     * Set the keyQEnable status.
     */
    public void setKeyQEnableStatus(boolean status) {
        keyQEnable = status;
    }

    /**
     * Get the keyQEnable status.
     */
    public boolean getKeyQEnableStatus() {
        return keyQEnable;
    }

    /**
     * Set the keyAEnable status.
     */
    public void setKeyAEnableStatus(boolean status) {
        keyAEnable = status;
    }

    /**
     * Get the keyAEnable status.
     */
    public boolean getKeyAEnableStatus() {
        return keyAEnable;
    }

    /**
     * Set the keyVEnable status.
     */
    public void setKeyVEnableStatus(boolean status) {
        keyVEnable = status;
    }

    /**
     * Get the keyVEnable status.
     */
    public boolean getKeyVEnableStatus() {
        return keyVEnable;
    }
    // end

    /**
     * Set the ROIItemStatus
     */
    public void setROIItemActiveStatus() {
        viewDex.pixelValueMeanMeasurement.setROIPixelValueItemActiveStatus();
    }

    /**
     *
     */
    public void setROIGrabSymbols(boolean status) {
        viewDex.canvas.setCanvasROIPixelValueMeanGrabSymbols(status);
    }

    /*
     *
     */
    public void resetROIPixelValueMeanOverlay() {
        viewDex.canvas.setCanvasROIPixelValueMeanUpdateStatus(false);
        viewDex.canvas.setCanvasROIPixelValueMeanGrabSymbols(false);
        viewDex.canvas.setCanvasROIPixelValueMeanDrawingValue(0, 0, 0, 0);
        viewDex.canvas.setCanvasROIPixelValueMeanDrawingValue(null);
        viewDex.canvas.setCanvasROIPixelValueMeanUpdateValue(null);
        viewDex.canvas.setCanvasROIPixelValueMeanValue("");
    }

    //***************************************************
    // NOT IN USE
    //****************************************************
    /** Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     * ORIGINAL
     * NOT IN USE
     */
    public void draw2(int x, int y) {
        Point2D xy;
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN)) {
            return;
        }

        switch (shapeType) {
            case RECTANGLE:
            case ELLIPSE:
                diff.x = x - shapeAnchor.x;
                diff.y = y - shapeAnchor.y;
                diff.x = x - userSpaceStartPointInt.x;
                diff.y = y - userSpaceStartPointInt.y;
                int wid = diff.x;
                int ht = diff.y;
                //Point ulhc = new Point(shapeAnchor);
                Point ulhc = new Point(userSpaceStartPointInt);
                if (diff.x < 0) {
                    wid = -diff.x;
                    ulhc.x = x;
                }
                if (diff.y < 0) {
                    ht = -diff.y;
                    ulhc.y = y;
                }

                // Calulate a new starting point value
                //int userSpacelhcX2 = userSpaceStartPointInt.x - wid/2;
                //int userSpacelhcY2 = userSpaceStartPointInt.y - ht/2;

                int d;
                if (wid >= ht) {
                    d = wid;
                } else {
                    d = ht;
                }

                int userSpacelhcX2 = userSpaceStartPointInt.x - d / 2;
                int userSpacelhcY2 = userSpaceStartPointInt.y - d / 2;
                Point userSpaceStartPointAdj = new Point(userSpacelhcX2, userSpacelhcY2);
                //***************************
                // END NOT IN USE
                //***************************

                Point2D xy2 = null;
                try {
                    // convert the cordinats from userspace to imagespace
                    AffineTransform atx = canvas.getTransform();
                    xy2 = null;
                    xy2 = atx.inverseTransform((Point2D) (new Point(x, y)), xy2);
                } catch (Exception e) {
                    System.out.println(e);
                }

                Point diff2 = new Point(0, 0);
                diff2.x = (int) (xy2.getX() - imageSpaceStartPointInt.x);
                diff2.y = (int) (xy2.getY() - imageSpaceStartPointInt.y);
                int wid2 = diff2.x;
                int ht2 = diff2.y;
                Point ulhc2 = new Point(imageSpaceStartPointInt);
                if (diff2.x < 0) {
                    wid2 = -diff2.x;
                    ulhc2.x = (int) xy2.getX();
                }
                if (diff2.y < 0) {
                    ht2 = -diff2.y;
                    ulhc2.y = (int) xy2.getY();
                }

                int d2;
                if (wid2 >= ht2) {
                    d2 = wid2;
                } else {
                    d2 = ht2;
                }

                //zzzzzzzzzzzzzzzzz
                int newX = userSpaceStartPointInt.x - d2 / 2;
                int newY = userSpaceStartPointInt.y - d2 / 2;
                Point userSpaceStartPointAdj2 = new Point(newX, newY);

                /**********************/
                /***** NOT IN USE *****/
                /**********************/
                //if(shapeType == RECTANGLE)
                //  currentShape = new Rectangle(ulhc.x, ulhc.y, wid, ht);
                //else
                //  currentShape = new Ellipse2D.Double(ulhc.x, ulhc.y, wid, ht);
                xy = new Point(0, 0);
                try {
                    // convert the cordinats from userspace to imagespace
                    AffineTransform atx = canvas.getTransform();
                    xy = null;
                    xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
                } catch (Exception e) {
                    System.out.println(e);
                }

                Point2D xyStart = new Point(0, 0);
                try {
                    // convert the start cordinats from userspace to imagespace
                    AffineTransform atx = canvas.getTransform();
                    xyStart = atx.inverseTransform((Point2D) (new Point(userSpaceStartPointInt.x, userSpaceStartPointInt.y)), xyStart);
                } catch (Exception e) {
                    System.out.println(e);
                }

                //userSpace
                userSpaceCurrentPointInt = new Point(x, y);
                userSpaceCurrentPointDouble = new Point2D.Double((double) x, (double) y);

                // imageSpace
                //Point startPointImageSpaceInt2 = new Point((int)(userSpaceStartPointInt.getX()),(int)(userSpaceStartPointInt.getY()));
                //Point.Double startPointImageSpaceDouble2 = new Point2D.Double(userSpaceStartPointInt.getX(), userSpaceStartPointInt.getY());
                imageSpaceCurrentPointInt = new Point((int) (xy.getX()), (int) (xy.getY()));
                imageSpaceCurrentPointDouble = new Point2D.Double(xy.getX(), xy.getY());
                //**************************
                // END NOT IN USE
                //**************************

                //viewDex.appMainAdmin.vgControl.setROIPixelValueStarttPointAdj(userSpaceStartPointAdj2);
                viewDex.pixelValueMeanMeasurement.setROIPixelValueWidthHeight(d2, d2);
                //viewDex.appMainAdmin.vgControl.setROIPixelValueWidthHeight(d, d);

                // Creating a ROI object
                Point2D xyROI = new Point(0, 0);
                // convert cordinate from userspace to imagespace
                try {
                    AffineTransform atx = canvas.getTransform();
                    xyROI = atx.inverseTransform((Point2D) (new Point(userSpaceStartPointAdj.x, userSpaceStartPointAdj.y)), xyROI);
                } catch (Exception e) {
                    System.out.println(e);
                }

                // Create a shape
                Shape s = new Ellipse2D.Double(xyROI.getX(), xyROI.getY(), d2, d2);
                //Shape s = new Ellipse2D.Double(xyROI.getX(), xyROI.getY(), d, d);
                ROIShape roiShape = new ROIShape(s);
                PlanarImage img = canvas.getImage();
                //ROI[] rois = createROIFromImage(img);
                //double[] mean = getMean(img);
                double[] meanROI = getMeanValue(img, roiShape);

                viewDex.pixelValueMeanMeasurement.setROIPixelValueMean((int) meanROI[0]);
                viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
                viewDex.windowLevel.setWindowLevel();
                break;
            case FREEHAND:
                //g.setPaintMode();
                //g.setColor(drawingColor);
                //g.drawLine(prevPoint.x, prevPoint.y, x,y);

                //canvas.draw(drawingColor, prevPoint.x, prevPoint.y, x,y);

                xy = new Point(0, 0);
                try {
                    // convert the cordinats from userspace to imagespace
                    atx = canvas.getTransform();
                    xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
                    curPoint = new Point((int) (xy.getX()), (int) (xy.getY()));
                } catch (Exception e) {
                    System.out.println(e);
                }

                canvas.draw(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, drawingLineStroke);
                prevPoint = curPoint;
                path.lineTo((float) xy.getX(), (float) xy.getY());
                //path.closePath();
                viewDex.windowLevel.setWindowLevel();
                break;
            case LINE:

                xy = new Point(0, 0);
                try {
                    // convert the cordinats from userspace to imagespace
                    AffineTransform atx = canvas.getTransform();
                    xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
                } catch (Exception e) {
                    System.out.println(e);
                }

                // cursor
                //viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

                //userSpace
                userSpaceCurrentPointInt = new Point(x, y);
                userSpaceCurrentPointDouble = new Point2D.Double((double) x, (double) y);

                // imageSpace
                imageSpaceCurrentPointInt = new Point((int) (xy.getX()), (int) (xy.getY()));
                imageSpaceCurrentPointDouble = new Point2D.Double(xy.getX(), xy.getY());

                // create a line
                //Line2D l = new Line2D.Double(startPointUserSpaceDouble, currentPointUserSpaceDouble);
                //viewDex.appMainAdmin.vgControl.setSelectedImageAndRender(true);
                //canvas.setCanvasROIPixelValueMeanDrawingStatus(true);

                // Draw a Line
                // orginal
                //canvas.setCanvasROIDistanceDrawingValue(startPointUserSpaceInt.x, startPointUserSpaceInt.y, x, y);

                // Run this separate......
                viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();

                // test
                //canvas.setCanvasROIDistanceDrawingValue(l);

                //zzzzzzzzzzzzzzzzzzzzzzzzzzzz
                //viewDex.appMainAdmin.vgControl.getROIDistanceStatus();
                // returns a distance from 2 Points
                //double dist = startPointImageSpaceInt.distance(currentPointImageSpaceInt);

                // test
                //double dist2 = startPointImageSpaceDouble.distance(currentPointImageSpaceDouble);
                //System.out.println("dist = " + dist + "dist2 = " + dist2);

                //String distStr = Double.toString(dist);

                //String distanceStr = viewDex.appMainAdmin.vgControl.getROIDistanceValue();
                //viewDex.canvas.setCanvasROIDistanceValue("Length: " + distStr + " mm");
                viewDex.windowLevel.setWindowLevel();
        }
        //imageCanvas.repaint();
        //path = new GeneralPath();
        //path.moveTo((float)xy.getX(), (float)xy.getY());
        // cursor
        //viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
}
