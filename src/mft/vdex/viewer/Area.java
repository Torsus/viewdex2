/* Volume.java 20160216
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
/**
 * @author Sune Svensson
 */
package mft.vdex.viewer;

import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbROIV;

public class Area {

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
    protected int nodeCnt = -1;
    protected Shape currentShape, prevShape;
    private int canvasControlMode = CanvasControlMode.NONE;
    private BasicStroke drawingLineStroke;
    private Point userSpaceCurrentPointInt;
    private Point imageSpaceCurrentPointInt;
    private Point userSpaceStartPointInt;
    private Point imageSpaceStartPointInt;
    private Point2D userSpaceStartPointDouble;
    private Point2D imageSpaceStartPointDouble;
    private boolean keyVEnable = false;
    private boolean keyAEnable = false;
    private boolean keyQEnable = false;
    private int cnt = 0;
    private Point2D xy;
    private GeneralPath pathUser;
    private GeneralPath pathImage;
    private int cntStartDraw = 0;
    private int cntDraw = 0;
    private int cntStopDraw = 0;
    private int[][] imgStat;
    private double rescaleIntercept, rescaleSlope;
    private boolean pixelValueSigned = false;

    public Area(ViewDex viewDex, ImageCanvasInterface canvas) {
        this.viewDex = viewDex;
        this.canvas = canvas;
    }

    /**
     * Initiates shape drawing at the specified position.
     * @param x the coordinate of the starting position of the shape.
     * @param y the coordinate of the starting position of the shape.
     */
    public void startDraw(int x, int y) {
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;
        //System.out.println("Volume.startDraw" + " " + x + " " + y + " " + cntStartDraw);

        try {
            // convert the cordinats from userspace to imagespace
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
        //imageSpaceStartPointDouble = new Point2D.Double((double) xy.getX(), (double) xy.getY());

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

        /*viewDex.appMainAdmin.vgControl.setROIVolumeItemStartPoint(
        userSpaceStartPointInt,
        imageSpaceStartPointInt,
        userSpaceStartPointDouble,
        imageSpaceStartPointDouble);*/

        //System.out.println("Volume.startDraw imageSpaceStartPointDouble " +
        //      imageSpaceStartPointDouble.getX() + " " +
        //    imageSpaceStartPointDouble.getY() + " " +
        //  cntStartDraw++);

        // new
        pathUser = new GeneralPath();
        pathImage = new GeneralPath();
        // imagespace
        //path.moveTo((float)xy.getX(), (float)xy.getY());
        // userspace
        pathUser.moveTo((double) userSpaceStartPointInt.getX(), (double) userSpaceStartPointInt.getY());
        pathImage.moveTo((double) imageSpaceStartPointInt.getX(), (double) imageSpaceStartPointInt.getY());
        //shape = path;
        //ROIShape roiShape = new ROIShape(shape);
        viewDex.areaMeasurement.setROIAreaValueStart(pathUser, pathImage,
                userSpaceStartPointDouble, imageSpaceStartPointDouble);
    }

    /**
     * Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     * Called by mouseDragged.
     */
    public void draw(int x, int y) {
        Point2D userSpaceCurrentPointDouble;
        Point2D imageSpaceCurrentPointDouble;
        //System.out.println("Volume.draw" + " " + x + " " + y + " " + cntDraw);

        //System.out.println("VolumeGUI.mouseDragged" + "  " + cnt);
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN))
        //  return;

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

                //viewDex.appMainAdmin.vgControl.setROIVolumeItemCurrentPoint(userSpaceCurrentPointInt,
                //      imageSpaceCurrentPointInt, userSpaceCurrentPointDouble, imageSpaceCurrentPointDouble);

                // imagespace
                // path.lineTo(imageSpaceCurrentPointDouble.getX(), imageSpaceCurrentPointDouble.getY());
                // userspace

                //System.out.println("Volume.draw imageSpaceCurrentPointDouble " +
                //      imageSpaceCurrentPointDouble.getX() + " " +
                //    imageSpaceCurrentPointDouble.getY() + " " +
                //  cntDraw++);

                pathUser.lineTo(userSpaceCurrentPointDouble.getX(), userSpaceCurrentPointDouble.getY());
                pathImage.lineTo(imageSpaceCurrentPointDouble.getX(), imageSpaceCurrentPointDouble.getY());

                // test 20160622
                //viewDex.appMainAdmin.vgControl.updateROIVolumeValue(pathUser, pathImage, 0, 0, 0);
                viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
                viewDex.windowLevel.setWindowLevel();
        }
        //System.out.println("VolumeGUI.mouseDragged end" + "  " + cnt);
    }

    /**
     * Draws the current shape at the specified end position.
     * @param x the coordinate of the current end position of the shape.
     * @param y the coordinate of the current end position of the shape.
     */
    public void stopDraw(int x, int y) {
        //Graphics2D g = imageCanvas.getDisplayedImageGC();
        //System.out.println("Volume.stopDraw" + " " + x + " " + y + " " + cntStopDraw);

        // ZZZzz mod 20160219
        //if ((getCanvasControlMode() == CanvasControlMode.MANIP_PAN)) {
        //  return;
        //}

        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
        } catch (Exception e) {
            System.out.println(e);
        }

        //endPointUserSpace = new Point(x,y);
        //endPointImageSpace = new Point((int)(xy.getX()),(int)(xy.getY()));

        //viewDex.appMainAdmin.vgControl.setROIItemStopPoint(endPointUserSpace,
        //endPointImageSpace);

        // create the Line2D object
        //viewDex.appMainAdmin.vgControl.createROIItemLineObject();

        //System.out.println("Volume.stopDraw imageSpaceEndPointDouble " +
          //           xy.getX() + " " +
            //       xy.getY() + " " +
              //   cntStopDraw++);

        setKeyVEnableStatus(false);
        pathUser.closePath();
        pathImage.closePath();
        Shape s = getShape(pathUser);
        Shape s2 = getShape(pathImage);
        Shape s3 = pathImage;
        ROIShape roiShapeUser = new ROIShape(s);
        ROIShape roiShapeImage = new ROIShape(s2);
        ROIShape roiShapeImage2 = new ROIShape(s3);
        PlanarImage img = canvas.getImage();

        int[] meanAndPixelCnt = new int[2];
        double[] meanValue = getMeanValue(img, roiShapeImage2);
        meanAndPixelCnt[0] = 0;
        meanAndPixelCnt[1] = (int) meanValue[0];

        /*
        double[][] minMax = computeMinMax(img);
        double[][] minMaxShape = computeMinMax(img, roiShapeImage);
        System.out.println("Volume.stopDraw meanValue = " + meanValue[0]);
        System.out.println("Volume.stopDraw minValueImage = " + minMax[0][0]);
        System.out.println("Volume.stopDraw maxValueImage = " + minMax[1][0]);
        System.out.println("Volume.stopDraw minValueShape = " + minMaxShape[0][0]);
        System.out.println("Volume.stopDraw maxValueShape = " + minMaxShape[1][0]);
         */
        /*
        try {
        Thread.sleep(5000);
        } catch (InterruptedException ignore){}
         */
        //int numBins = 255;
        //int[][] hist = computeHistogram(img, roiShapeImage, numBins);
        //double[] meanROI = getMeanValue(img, roiShapeImage);
        //int meanValue = 0;

        // Area and Mean value
        meanAndPixelCnt[0] = getPixelCnt(roiShapeImage2);
        //int[] meanAndPixelCnt = getMeanValueAndPixelCnt(img, roiShapeImage);
        double area = computeArea(meanAndPixelCnt[0]);
        //System.out.println("Volume.stopDraw() pixelCnt: " + meanAndPixelCnt[0]);
        //System.out.println("Volume.stopDraw() area " + area);
        //System.out.println();

        // Mean Value
        //int meanValue = getMeanValue2(img, roiShapeImage);
        //System.out.println("Volume.stopDraw() meanValue: " + meanValue);

        //int nPixels = computeNumberOfPixelsInsideROI(img, roiShapeImage);

        // test
        // Prints the coordinates
        /*
        PathIterator pi = pathImage.getPathIterator(null);
        int segNumber = 0;
        while(pi.isDone() == false){
        segNumber++;
        System.out.println("Segment: " + segNumber);
        float[] coords = new float[6];
        int currentSegmentType = pi.currentSegment(coords);
        // get segment types and sequential pairs of (x,y) coords
        System.out.println("Current Segment Type: " + currentSegmentType);
        // print coords pairs
        for(int j=0;j<2;j++){
        //System.out.println("j:" + j + " coords[j] "+coords[j]);
        System.out.println("coords[" + j + "] " + coords[j]);
        }
        pi.next();
        }
         */

        viewDex.areaMeasurement.updateROIAreaValue(pathUser, pathImage, meanAndPixelCnt[0], area, meanAndPixelCnt[1]);
        viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
        canvas.setCanvasROIAreaUpdateTextStatus(true);
        viewDex.windowLevel.setWindowLevel();
    }

    /**
     *
     * @param vList
     * @param render
     */
    public void drawROIAreaOnCanvasAndRender(ArrayList<StudyDbROIV> vList, boolean render) {
        //canvas.setCanvasROIDistanceDrawingStatus(false);
        canvas.setCanvasROIAreaUpdateStatus(true);
        canvas.setCanvasROIAreaUpdateValue(vList);
        canvas.setCanvasROIAreaUpdateTextStatus(true);
        //canvas.setCanvasOverlayDistanceMeasurementValue((int)p1.getX(), (int)p1.getY(),
        //      (int)p2.getX(), (int)p2.getY());

        if (render) {
            viewDex.windowLevel.setWindowLevel();
        }
    }

    /**
     *
     * @param mode
     */
    public void setCanvasControlMode(int mode) {
        canvasControlMode = mode;
    }

    /**
     *
     * @return
     */
    public int getCanvasControlMode() {
        return canvasControlMode;
    }

    /**
     *
     * @return
     * NOT IN USE
     */
    public int getRunModeStatus() {
        return viewDex.appMainAdmin.vgControl.getRunModeStatus();
    }

    /**
     *
     * @return
     * NOT IN USE
     */
    public int getUserModeStatus() {
        //return viewDex.appMainAdmin.vgControl.getUserModeStatus();
        return 999;
    }

    /**
     *
     * @param status
     */
    public void setKeyVEnableStatus(boolean status) {
        keyVEnable = status;
    }

    /**
     *
     * @return
     */
    public boolean getKeyVEnableStatus() {
        return keyVEnable;
    }

    /**
     *
     * @param status
     */
    public void setKeyAEnableStatus(boolean status) {
        keyAEnable = status;
    }

    /**
     *
     * @return
     */
    public boolean getKeyAEnableStatus() {
        return keyAEnable;
    }

    /**
     *
     * @param status
     */
    public void setKeyQEnableStatus(boolean status) {
        keyQEnable = status;
    }

    /**
     *
     * @return
     * NOT IN USE
     */
    public boolean getKeyQEnableStatus() {
        return keyQEnable;
    }

    /**
     *
     */
    public void setROIItemActiveStatus() {
        viewDex.areaMeasurement.setROIAreaItemActiveStatus();
    }

    /**
     * 
     * @param path
     * @return
     */
    private Shape getShape(GeneralPath path) {
        Shape shape = null;

        // convert cordinats from imagespace to userspace
        try {
            AffineTransform atx = canvas.getTransform();
            shape = atx.createTransformedShape(path);
        } catch (Exception e) {
            System.out.println("Volume.getShape: Exception error");
        }
        return shape;
    }

    /**
     *
     * @param img
     * @param roi
     * @return
     */
    private double[] getMeanValue(PlanarImage img, ROIShape roi) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(roi);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("mean", pb);
        double[] mean = (double[]) op.getProperty("mean");
        return mean;
    }

    /**
     *
     * @param image
     * @return
     */
    public double[][] computeMinMax(PlanarImage img) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("extrema", pb);
        double[][] minmax = (double[][]) op.getProperty("extrema");
        return minmax;
    }

    /**
     *
     * @param image
     * @param roi
     * @return
     * Not worknig...
     */
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

    /**
     *
     * @param image
     * @param roi
     * @param nbins
     * @return
     * Test
     */
    public int[][] computeHistogram(PlanarImage image, ROIShape roi, int nbins) {
        double[][] minmax = computeMinMax(image);
        int numbands = image.getSampleModel().getNumBands();
        int[] bins = new int[numbands];
        for (int i = 0; i < numbands; i++) {
            bins[i] = nbins;
        }

        //javax.media.jai.Histogram his =
        //      new javax.media.jai.Histogram(bins, minmax[0], minmax[1]);

        ParameterBlock pbl = new ParameterBlock();
        pbl.addSource(image);
        //pbl.add(his);
        pbl.add(roi); // ROI
        pbl.add(1);  // Sampling, each and every pixel.
        pbl.add(1);  // Sampling, each and every pixel.
        pbl.add((new int[]{nbins}));  // Bins
        RenderedOp opl = JAI.create("histogram", pbl);

        // Get the histogram from the RenderedOp
        javax.media.jai.Histogram hist =
                (javax.media.jai.Histogram) opl.getProperty("histogram");

        System.out.println("Image width: " + image.getWidth());
        System.out.println("Image height " + image.getHeight());
        System.out.println("Number of pixels: " + image.getWidth() * image.getHeight());

        System.out.println("Histogram with " + nbins + " bins:");
        // For each band
        for (int b = 0; b < image.getNumBands(); b++) {
            System.out.println("Mean: " + hist.getMean()[b]);
            System.out.println("StdDev: " + hist.getStandardDeviation()[b]);
            System.out.println("Entropy: " + hist.getEntropy()[b]);
            System.out.println("Totals: " + hist.getTotals()[b]);
        }
        return hist.getBins();
    }

     /**
     *
     * @param image
     * @param roi
     */
    private int getPixelCnt(ROIShape roiImage) {
        int width; int height;
        Rectangle2D r = roiImage.getBounds2D();
        double x = r.getX(); double y = r.getY();
        double w = r.getWidth(); double h = r.getHeight();
        int numpixel = 0;
        width = (int) (x + w);
        height = (int) (y + h);

        for (int i = (int)y; i <= height; i++) {
            for (int j = (int)x; j <= width; j++) {
                if (roiImage.contains(j, i))
                    numpixel++;
            }
        }
        return numpixel;
    }

    /**
     * 
     * @param image
     * @param roi
     * Test NOT IN USE
     */
    private int getPixelCnt2(PlanarImage img, ROIShape roiUser, ROIShape roiImage) {
        //SampleModel sm = img.getSampleModel();
        //ColorModel cm = img.getColorModel();
        Raster raster = img.getData();
        DataBuffer db = raster.getDataBuffer();
        //int dt = db.getDataType();
        //int size = db.getSize();
        //short[] data = ((DataBufferUShort) db).getData();
        //int paddingValue = Integer.MIN_VALUE;
        //int paddingValue2 = Integer.MIN_VALUE;
        Point2D p = null;
        Point2D p2 = null;
        int px, py = 0;

        int width = img.getWidth();
        int height = img.getHeight();
        Rectangle2D r = roiImage.getBounds2D();
        double x = r.getX();
        double y = r.getY();
        double w = r.getWidth();
        double h = r.getHeight();
        //int totalPix = width * height;
        //ArrayList npix = new ArrayList<String>();
        //ArrayList npix2 = new ArrayList<String>();
        int numpixel = 0;
        //int[][] npix3 = new int[width][height];
        //short[] data4 = new short[totalPix];
        //height = 2000; width = 2000;
        width = (int) (x + w);
        height = (int) (y + h);
        //for (int i = 0; i <= height; i++) {
          //  for (int j = 0; j <= width; j++) {
          for (int i = (int)y; i <= height; i++) {
              for (int j = (int)x; j <= width; j++) {
               try {
                    // convert cordinats from imagespace to userspace
                    atx = canvas.getTransform();
                    //userSpaceStartPoint = atx.transform(imageSpaceStartPoint, null);
                    //userSpaceStartPointAdj = atx.transform(imageSpaceStartPointAdj, null);
                    //userSpaceCurrentPoint = atx.transform(imageSpaceCurrentPointInt, null);
                    //width2 = atx.transform(new Point2D.Double(width, height), null);
                    //userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, userSpaceStartPointDouble);
                    //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointInt, userSpaceCurrentPointDouble);
                    p2 = atx.transform(new Point(j, i), null);
                } catch (Exception e) {
                    System.out.println("VgControl.setROIInCanvasAndRender: Error");
                }
                px = (int) p2.getX();
                py = (int) p2.getY();
                //String str1 = "i:" + i + "," + "j:" + j + "  " + Integer.toString(px) + "," + Integer.toString(py);
                //System.out.println("i: " + i + " x: " + (int) p2.getX());
                //System.out.println("j: " + j + " y: " + (int) p2.getY());
                px = j; py = i;
                if (roiImage.contains(px, py)) {
                    numpixel++;
                }
            }
        }
        return numpixel;
    }

    private double computeArea(int nPixels) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        String modality = imageNode.getModality();
        double[] pixelSpacing = imageNode.getPixelSpacing();
        double[] imagerPixelSpacing = imageNode.getImagerPixelSpacing();
        double Rs = 0.0;
        double Cs = 0.0;
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

        double area = nPixels * (Rs * Cs);
        return area;
    }

    private int[] getMeanValueAndPixelCnt(PlanarImage img, ROIShape roi) {
        int[] pixelValue = new int[1];
        int[] pixelValue2 = new int[1];
        int sum = 0; int meanValue = 0;
        pixelValue[0] = 0; pixelValue2[0] = 0;
        Point2D p = null; Point2D p2 = null;
        int px, py = 0;
        int width = img.getWidth();
        int height = img.getHeight();
        int numpixel = 0;
        SampleModel img_sm = img.getSampleModel();
        Raster img_raster = img.getData();
        DataBuffer img_db = img_raster.getDataBuffer();
        Rectangle img_rec = img_raster.getBounds();
        int[] result = new int[2];

        //int[][] npix3 = new int[width][height];
        //short[] data4 = new short[totalPix];
        if (img_sm != null && img_db != null) {
            for (int i = 0; i <= height; i++) {
                for (int j = 0; j <= width; j++) {
                    // convert cordinats from userspace to imagespace
                    try {
                        atx = canvas.getTransform();
                        p2 = atx.transform(new Point(j, i), null);
                    } catch (Exception e) {
                        System.out.println("VgControl.getMeanValue: Error");
                    }
                    px = (int) p2.getX();
                    py = (int) p2.getY();
                    //String str1 = "i:" + i + "," + "j:" + j + "  " + Integer.toString(px) + "," + Integer.toString(py);
                    //System.out.println("i: " + i + " x: " + (int) p2.getX());
                    //System.out.println("j: " + j + " y: " + (int) p2.getY());
                    //px = j; py = i;
                    if (roi.contains(px, py)) {
                        numpixel++;
                        if (img_rec.contains(px, py)) {
                            pixelValue = img_sm.getPixel(j, i, pixelValue, img_db);
                            //pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7]) * rescaleSlope) + rescaleIntercept);
                            pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7])) + rescaleIntercept - rescaleIntercept);
                            if (pixelValueSigned) {
                                pixelValue2[0] = (int) (pixelValue[0] - imgStat[0][7]);
                            }
                        }
                        sum += pixelValue[0];
                    }
                }
            }
        }
        if (numpixel != 0) {
            meanValue = sum / numpixel;
        }
        result[0] = numpixel;
        result[1] = meanValue;
        return result;
    }

    /*
     * NOT IN USE
     */
    private void getMeanValue_bluprint(PlanarImage img, int x, int y) {
        SampleModel img_sm;
        Raster img_raster;
        DataBuffer img_db;
        Rectangle img_rec;
        int[] pixelValue = new int[1];
        int[] pixelValue2 = new int[1];
        pixelValue[0] = 0;
        pixelValue2[0] = 0;

        if (img != null) {
            img_sm = img.getSampleModel();
            img_raster = img.getData();
            img_db = img_raster.getDataBuffer();
            img_rec = img_raster.getBounds();

            if (img_rec != null && img_sm != null && img_db != null) {
                if (img_rec.contains(x, y)) {
                    pixelValue = img_sm.getPixel(x, y, pixelValue, img_db);
                    //pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7]) * rescaleSlope) + rescaleIntercept);
                    pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7])) + rescaleIntercept - rescaleIntercept);
                    if (pixelValueSigned) {
                        pixelValue2[0] = (int) (pixelValue[0] - imgStat[0][7]);
                    }
                }
            }
        }
    }

    /**
     * Set image statistics.
     */
    public void setImageStat(int[][] sta) {
        imgStat = sta;

        if (imgStat[0][7] != 0) {
            pixelValueSigned = true;
        } else {
            pixelValueSigned = false;
        }
    }

    /**
     * Set RescaleIntercept.
     */
    public void setRescaleIntercept(double val) {
        rescaleIntercept = val;
    }

    /**
     *
     * @param status
     * NOT IN USE
     */
    public void setROIGrabSymbols(boolean status) {
        viewDex.canvas.setCanvasROIDistanceGrabSymbols(status);
    }

     /*
     *
     */
    public void resetROIVolumeOverlay(){
        viewDex.canvas.setCanvasROIAreaUpdateStatus(false);
        viewDex.canvas.setCanvasROIAreaUpdateTextStatus(false);
        viewDex.canvas.setCanvasROIAreaUpdateValue(null);
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
        final Point userSpaceCurrentPointInt1 = userSpaceCurrentPointInt;
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
                pathUser.lineTo((float) xy.getX(), (float) xy.getY());
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
                //userSpaceCurrentPointDouble = new Point2D.Double((double) x, (double) y);

                // imageSpace
                imageSpaceCurrentPointInt = new Point((int) (xy.getX()), (int) (xy.getY()));
                //imageSpaceCurrentPointDouble = new Point2D.Double(xy.getX(), xy.getY());

                // create a line
                //Line2D l = new Line2D.Double(startPointUserSpaceDouble, currentPointUserSpaceDouble);

                //viewDex.appMainAdmin.vgControl.setROIDistanceItemCurrentPoint(userSpaceCurrentPointInt,
                //      imageSpaceCurrentPointInt, userSpaceCurrentPointDouble, imageSpaceCurrentPointDouble);

                //viewDex.appMainAdmin.vgControl.setSelectedImageAndRender(true);
                //canvas.setCanvasROIDistanceDrawingStatus(true);

                // Draw a Line
                // orginal
                //canvas.setCanvasROIDistanceDrawingValue(startPointUserSpaceInt.x, startPointUserSpaceInt.y, x, y);


                // zzzzzzzzzzzzzzzzz 
                // Run this separate......
                viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();

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
