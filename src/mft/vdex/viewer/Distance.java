/* @(#) Distance.java 04/07/2008
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
package mft.vdex.viewer;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbROID;
import javax.media.jai.PlanarImage;

/**
 *
 * @author Sune Svensson
 */
public class Distance {

    ViewDex viewDex;
    ImageCanvasInterface canvas;
    private AffineTransform atx;
    public final static int FREEHAND = 1;
    public final static int RECTANGLE = 2;
    public final static int ELLIPSE = 4;
    public final static int LINE = 5;
    protected int shapeType = LINE;
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
    // in use
    private Point2D xy = null;
    private Point userSpaceStartPointInt;
    private Point2D userSpaceStartPointDouble;
    //private Point2D userSpaceStartPointFloat;
    private Point userSpaceCurrentPointInt;
    private Point2D userSpaceCurrentPointDouble;
    private Point imageSpaceStartPointInt;
    private Point2D imageSpaceStartPointDouble;
    private Point imageSpaceCurrentPointInt;
    private Point2D imageSpaceCurrentPointDouble;
    private Point endPointUserSpace;
    private boolean keyAEnable = false;
    private boolean keyVEnable = false;
    private boolean keyQEnable = false;
    private int cntStartDraw = 0;
    private int cntDraw = 0;
    private int cntStopDraw = 0;
    public static final boolean dev_debug = false;

    public Distance(ViewDex viewDex, ImageCanvasInterface canvas) {
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
        if (dev_debug) {
            System.out.println("Distance.startDraw" + " " + x + " " + y + " " + cntStartDraw);
        }

        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
            //startPoint = new Point((int)(xy.getX()),(int)(xy.getY()));
        } catch (Exception e) {
            System.out.println(e);
        }

        // userSpace
        userSpaceStartPointInt = new Point(x, y);
        imageSpaceStartPointInt = new Point((int) (xy.getX()), (int) (xy.getY()));
        //userSpaceStartPointFloat = new Point2D.Float((float) x, (float) y);
        userSpaceStartPointDouble = new Point2D.Double((double) x, (double) y);

        PlanarImage img = canvas.getImage();
        if (imageSpaceStartPointInt.getX() < img.getMinX()
                || imageSpaceStartPointInt.getY() < img.getMinY()
                || imageSpaceStartPointInt.getX() > img.getMaxX()
                || imageSpaceStartPointInt.getY() > img.getMaxY()) {
            return;
        }

        // imageSpace
        imageSpaceStartPointDouble = new Point2D.Double(xy.getX(), xy.getY());
        //Point2D imageSpaceStartPointFloat = new Point2D.Float((float) imageSpaceStartPointDouble.getX(),
        //      (float) imageSpaceStartPointDouble.getY());

        if (dev_debug) {
            System.out.println("Distance.startDraw imageSpaceStartPointDouble "
                    + imageSpaceStartPointDouble.getX() + " "
                    + imageSpaceStartPointDouble.getY() + " "
                    + cntStartDraw++);
        }

        viewDex.distanceMeasurement.setROIDistanceItemStartPoint(
                userSpaceStartPointInt,
                imageSpaceStartPointInt,
                userSpaceStartPointDouble,
                imageSpaceStartPointDouble);
    }

    /** Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     * Called by mouseDragged.
     */
    public void draw(int x, int y) {
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;

        if (dev_debug) {
            System.out.println("Distance.draw" + " " + x + " " + y + " " + cntDraw);
        }

        switch (shapeType) {
            case LINE:
                try {
                    // convert the cordinats from userspace to imagespace
                    atx = canvas.getTransform();
                    xy = null;
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

                if (dev_debug) {
                    System.out.println("Distance.draw imageSpaceCurrentPointDouble "
                            + imageSpaceCurrentPointDouble.getX() + " "
                            + imageSpaceCurrentPointDouble.getY() + " "
                            + cntDraw++);
                }

                viewDex.distanceMeasurement.setROIDistanceItemCurrentPoint(userSpaceCurrentPointInt,
                        imageSpaceCurrentPointInt, userSpaceCurrentPointDouble, imageSpaceCurrentPointDouble);
                viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
                viewDex.windowLevel.setWindowLevel();
        }
    }

    /** Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     */
    public void stopDraw(int x, int y) {
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        if (dev_debug) {
            System.out.println("Distance.stopDraw" + " " + x + " " + y + " " + cntStopDraw);
        }

        if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN)) {
            return;
        }

        try {
            // convert the cordinats from userspace to imagespace
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
        } catch (Exception e) {
            System.out.println(e);
        }

        if (dev_debug) {
            System.out.println("Distance.stopDraw imageSpaceEndPointDouble "
                    + xy.getX() + " "
                    + xy.getY() + " "
                    + cntStopDraw++);
        }

        //endPointUserSpace = new Point(x,y);
        //endPointImageSpace = new Point((int)(xy.getX()),(int)(xy.getY()));

        //viewDex.appMainAdmin.vgControl.setROIItemStopPoint(endPointUserSpace,
        //endPointImageSpace);

        // create the Line2D object
        //viewDex.appMainAdmin.vgControl.createROIItemLineObject();

        setKeyAEnableStatus(false);
        viewDex.windowLevel.setWindowLevel();
    }

    /**
     * Draw the ROI object on the image.
     */
    public void drawROIDistanceOnCanvasAndRender(ArrayList<StudyDbROID> dList, boolean render) {
        //canvas.setCanvasROIDistanceDrawingStatus(false);
        canvas.setCanvasROIDistanceUpdateStatus(true);
        canvas.setCanvasROIDistanceUpdateValue(dList);
        //canvas.setCanvasOverlayDistanceMeasurementValue((int)p1.getX(), (int)p1.getY(),
        //      (int)p2.getX(), (int)p2.getY());

        if (render) {
            viewDex.windowLevel.setWindowLevel();
        }
    }
    // *** end

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
     * get the runMode status.
     * @return
     * NOT IN USE
     */
    public int getRunModeStatus() {
        return viewDex.appMainAdmin.vgControl.getRunModeStatus();
    }

    /**
     * get the userMode status.
     * @return
     * NOT IN USE
     */
    public int getUserModeStatus() {
        //return viewDex.appMainAdmin.vgControl.getUserModeStatus();
        return 999;
    }

    // key status
    public void setKeyAEnableStatus(boolean status) {
        keyAEnable = status;
    }

    public boolean getKeyAEnableStatus() {
        return keyAEnable;
    }

    public void setKeyVEnableStatus(boolean status) {
        keyVEnable = status;
    }

    public boolean getKeyVEnableStatus() {
        return keyVEnable;
    }

    public void setKeyQEnableStatus(boolean status) {
        keyQEnable = status;
    }

    // NOT IN USE
    public boolean getKeyQEnableStatus() {
        return keyQEnable;
    }
    // end

    /**
     * Set the ROIItemStatus
     */
    public void setROIItemActiveStatus() {
        viewDex.distanceMeasurement.setROIDistanceItemActiveStatus();
    }

    /**
     *
     */
    public void setROIGrabSymbols(boolean status) {
        viewDex.canvas.setCanvasROIDistanceGrabSymbols(status);
    }

    /*
     * 
     */
    public void resetROIDistanceOverlay() {
        viewDex.canvas.setCanvasROIDistanceDrawingValue(0, 0, 0, 0);
        viewDex.canvas.setCanvasROIDistanceUpdateStatus(false);
        viewDex.canvas.setCanvasROIDistanceUpdateValue(null);
    }

    //*****************************************************
    // NOT IN USE
    //*****************************************************/
    /** Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     * Called by mouseDragged.
     * NOT IN USE
     */
    public void draw2(int x, int y) {
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;

        switch (shapeType) {
            case RECTANGLE:
            case ELLIPSE:
                diff.x = x - shapeAnchor.x;
                diff.y = y - shapeAnchor.y;
                int wid = diff.x;
                int ht = diff.y;
                Point ulhc = new Point(shapeAnchor);
                if (diff.x < 0) {
                    wid = -diff.x;
                    ulhc.x = x;
                }
                if (diff.y < 0) {
                    ht = -diff.y;
                    ulhc.y = y;
                }
                if (shapeType == RECTANGLE) {
                    currentShape = new Rectangle(ulhc.x, ulhc.y, wid, ht);
                } else {
                    currentShape = new Ellipse2D.Double(ulhc.x, ulhc.y, wid, ht);
                }
                //g.setColor(Color.WHITE);

                //if(drawingColor == Color.white)
                //  g.setColor(Color.BLACK);

                //g.setXORMode(drawingColor);
                //g.setXORMode(Color.GREEN);
                //if(prevShape != null)
                //  g.draw(prevShape);
                //g.draw(currentShape);
                //prevShape = currentShape;
                break;
            case FREEHAND:
                //g.setPaintMode();
                //g.setColor(drawingColor);
                //g.drawLine(prevPoint.x, prevPoint.y, x,y);


                //canvas.draw(drawingColor, prevPoint.x, prevPoint.y, x,y);
                try {
                    // convert the cordinats from userspace to imagespace
                    atx = canvas.getTransform();
                    xy = null;
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
                try {
                    // convert the cordinats from userspace to imagespace
                    atx = canvas.getTransform();
                    xy = null;
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

                viewDex.distanceMeasurement.setROIDistanceItemCurrentPoint(userSpaceCurrentPointInt,
                        imageSpaceCurrentPointInt, userSpaceCurrentPointDouble, imageSpaceCurrentPointDouble);

                //viewDex.appMainAdmin.vgControl.setSelectedImageAndRender(true);
                //canvas.setCanvasROIDistanceDrawingStatus(true);

                // Draw a Line
                // orginal
                //canvas.setCanvasROIDistanceDrawingValue(startPointUserSpaceInt.x, startPointUserSpaceInt.y, x, y);


                // zzzzzzzzzzzzzzzzz 
                // Run this separate......
                viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();

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
}
