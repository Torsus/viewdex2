/* @(#) ImageCanvas.java 05/12/2003
 *
 * Copyright (c) 2003 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.viewer;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.image.renderable.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

import javax.media.jai.*;
import javax.media.jai.operator.LookupDescriptor;
import mft.vdex.app.ViewDex;

//import com.vistech.imageviewer.*;
import mft.vdex.ds.StudyDbROID;
import mft.vdex.ds.StudyDbROIV;
import mft.vdex.ds.StudyDbROIPixelValueD;
import mft.vdex.ds.StudyDbLocalizationM;
import mft.vdex.ds.StudyDbLocalizationStatus;
//import com.vistech.util.*;


/* public class ImageManipulatorCanvas extends JComponent implements ImageManipulatorInterface, MouseMotionListener */
public class ImageCanvas extends JComponent implements FocusListener, ImageCanvasInterface {

    transient protected Image awtImage;
    protected int imageType = 0;
    protected Point panOffset = new Point(0, 0);
    protected boolean magOn = true;
    protected double magFactor = 1.0;
    protected int magCenterX = 0;
    protected int magCenterY = 0;
    protected double rotationAngle = 0.0;
    protected boolean rotateOn = true;
    protected int rotationCenterX = 0;
    protected int rotationCenterY = 0;
    protected boolean shearOn = true;
    protected double shearFactor = 0.0;
    protected double shearX = 0.0, shearY = 0.0;
    protected int displayMode = CanvasControlMode.DISPLAY_ORIG;
    protected int flipMode = CanvasFlipMode.FLIP_NORMAL;
    protected int interpMode = Interpolation.INTERP_NEAREST;
    protected double diffFactor = 0.0;
    // new
    protected AffineTransform atx = new AffineTransform();
    protected AffineTransform atx2 = new AffineTransform();
    //transient protected PlanarImage orgImage;
    // Original image
    protected PlanarImage orgImage, orgImage2;
    SampleModel orgImage_sm;
    Raster orgImage_raster;
    DataBuffer orgImage_db;
    Rectangle orgImage_rec;
    //int[] pixel = new int[1];
    //double[] pixel_d = new double[1];
    protected PlanarImage displayImage;
    protected PlanarImage testImage;
    protected BufferedImage displayBufImage;
    protected int tileWidth = 256, tileHeight = 256;
    transient protected SampleModel sampleModel;
    protected ColorModel colorModel;
    protected int maxTileIndexX, maxTileIndexY;
    protected int maxTileCordX, maxTileCordY;
    protected int minTileIndexX, minTileIndexY;
    protected int minTileCordX, minTileCordY;
    protected int tileGridXOffset, tileGridYOffset;
    protected int imageWidth = 0, imageHeight = 0;
    protected TileCache tc;
    protected int viewerWidth = 480, viewerHeight = 400;
    protected boolean imageDrawn = false;
    protected int panX = 0, panY = 0;
    protected Point scrollAnchor = new Point(0, 0);
    protected int width, height;
    protected ViewDex viewdex;
    
    // canvas "overlay" info display
    String window, level;
    private Font overlayTextFont;
    private Color overlayTextColor;
    private int selWindowWidth = 0, selWindowCenter = 0;
    private int yScaleWL1, yScaleWL2;
    private int xScaleMPos1, yScaleMPos1, yScaleMPos2;
    private int xScaleMPosPixelValue, yScaleMPosPixelValue, yScaleMPosPixelValue2;
    boolean infoDisplayMousePositionStatus = false;
    boolean pixelValueSigned = false;
    int mousePosX = 0, mousePosY = 0;
    int[] pixelValue = new int[3];
    int[] pixelValue2 = new int[1];
    double rescaleIntercept, rescaleSlope;
    int[][] imgStat;
    private Point2D p1 = null;
    private String photometricInterpretation;
    
    // stackNo
    boolean canvasOverlayStackNoStatus = false;
    private String canvasOverlayStackNoValue;
    private int xPosStackNo, yPosStackNo;
    // markNo
    boolean canvasOverlayMarkNoStatus = false;
    private String canvasOverlayMarkNoValue;
    private int xPosMarkNo, yPosMarkNo;
    // patientId
    boolean canvasOverlayPatientIdStatus = false;
    private String canvasOverlayPatientIdValue;
    private int xPosPatientId, yPosPatientId;
    // windowLevel
    private boolean canvasOverlayWindowLevelStatus = false;
    private int xPosWindowWidth, yPosWindowWidth;
    private int xPosWindowCenter, yPosWindowCenter;
    // WindowingMode, NM & MR "whitepoint" value 
    private int windowingMode = WindowingMode.NONE;
    private boolean canvasOverlayWindowingFixedMinimumStatus = false;
    private int windowingFixedMinimumValue;
    private long windowingFixedMinimumValuePercent;
    private long windowingPercent;
    private int xPosFixedMinimum;
    private int yPosFixedMinimum;
    private int windowWidthFixedMin;
    //private int windowWidthFixedMin2;
    // mousePosition
    private boolean canvasOverlayMousePositionStatus = false;
    private int xPosMousePositionX, yPosMousePositionX;
    private int xPosMousePositionY, yPosMousePositionY;
    private int posMousePositionXWidth, posMousePositionXHeight;
    private int posMousePositionYWidth, posMousePositionYHeight;
    private int mousePositionValueWidth, mousePositionValueHeight;
    // mousePositionPixelValue
    private boolean canvasOverlayMousePositionPixelValueStatus = false;
    private int xPosMousePositionValue;
    private int yPosMousePositionValue;
    // localization
    private Shape canvasOverlaySelectShape;
    private Line2D canvasOverlaySelectLine1;
    private Line2D canvasOverlaySelectLine2;
    private BasicStroke canvasOverlayBasicStroke;
    private boolean canvasOverlayLocalizationRenderStatus = false;
    private boolean canvasOverlayLocalizationPositionStatus = false;
    private int xPosLocalizationMark, yPosLocalizationMark, zPosLocalizationMark;
    private int xPosLocalizationX, yPosLocalizationX;
    private int xPosLocalizationY, yPosLocalizationY;
    private int xPosLocalizationZ, yPosLocalizationZ;
    private ArrayList<StudyDbLocalizationM> lList;
    // ROI distance measuarement
    private boolean roiDistanceDrawingStatus = false;
    private boolean roiDistanceUpdateStatus = false;
    private boolean roiDistanceGrabStatus = false;
    private BasicStroke roiDistanceLineStroke;
    private Color roiDistanceLineColor;
    private int startPointXInt, startPointYInt;
    private int curPointXInt, curPointYInt;
    private String roiDistanceText;
    private Font roiDistanceFont;
    private Color roiDistanceFontColor;
    private ArrayList<StudyDbROID> dList;
    private Line2D dLine;
    // ROI area measurement
    private boolean roiVolumeDrawingStatus = false;
    private boolean roiVolumeUpdateStatus = false;
    private boolean roiVolumeUpdateTextStatus = false;
    private boolean roiVolumeGrabStatus = false;
    private BasicStroke roiVolumeLineStroke;
    private Color roiVolumeLineColor;
    private int roiVolumeStartPointXInt, roiVolumeStartPointYInt;
    private int roiVolumeCurPointXInt, roiVolumeCurPointYInt;
    private String roiVolumeText;
    private Font roiVolumeFont;
    private Color roiVolumeFontColor;
    private ArrayList<StudyDbROIV> vList;
    private Line2D vLine;
    // ROI pixel value measurement
    private boolean roiPixelValueMeanDrawingStatus = false;
    private boolean roiPixelValueMeanUpdateStatus = false;
    private boolean roiPixelValueMeanGrabStatus = false;
    private BasicStroke pixelValueMeanDrawingLineStroke;
    private Color pixelValueMeanDrawingLineColor;
    private int pixelValueMeanStartPointXInt, pixelValueMeanStartPointYInt;
    private int pixelValueMeanCurPointXInt, pixelValueMeanCurPointYInt;
    private String pixelValueText;
    private Font pixelValueMeanFont;
    private Color pixelValueMeanFontColor;
    private ArrayList<StudyDbROIPixelValueD> pvList;
    // transformation status
    private boolean modalityLUTSequenceStatus;
    private boolean voiLUTSequenceStatus;
    private boolean rescaleSlopeInterceptStatus;
    private boolean centerWidthStatus;
    private boolean identityStatus;
    // localization properties
    private double localizationActiveSymbolLineSize;
    private double localizationSetSymbolElipseSize;
    private double localizationSelectSymbolLineXSize;
    private double localizationSelectSymbolLineYSize;
    private BasicStroke localizationSymbolStroke;
    private Color localizationLineColor;
    private Color localizationPositionTextColor;
    // test
    Line2D.Double markLine1 = null;
    Line2D.Double markLine2 = null;
    Shape shape = null;
    // Window/Level status
    private boolean windowCenterOffsetStatus;
    Line2D.Double testLine1 = null;
    Line2D.Double testLine2 = null;
    Line2D pixelValueDLine;
    // Canvas
    Color canvasDefaultColor;
    Color canvasETColor;
    Color canvasColor;
    //test
    int renderCnt = 0;
    int renderCntDistance = 0;
    int renderCntVolume = 0;
    int renderCntMeanValue = 0;
    int renderCntLocalization = 0;
    int pixelValueCnt = 0;

    public ImageCanvas(ViewDex viewdex) {
        this.viewdex = viewdex;
        init();
    }

    /***********************************************************
     * IN USE
     **********************************************************/
    /**
     * Sets the TileCache memory capacity.
     */
    private void init() {
        /*TileCache tileCache = JAI.getDefaultInstance().getTileCache();
        tileCache.setMemoryCapacity(10000L * 10000L);
        long memCapacity = tileCache.getMemoryCapacity();
        System.out.println("TileCache memory capacity = " + memCapacity);
         */
        //this.setDoubleBuffered(true);
        //this.setOpaque(true);
        //this.setBackground(Color.CYAN);  // no effect
        //this.setFocusable(true);
        //this.addMouseListener(this);
        //this.addMouseMotionListener(this);
        setFocusable(true);
        addFocusListener(this);

        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        yScaleWL1 = Math.round(100 * yScale);
        yScaleWL2 = Math.round(75 * yScale);

        xScaleMPos1 = Math.round(130 * xScale);
        yScaleMPos1 = Math.round(150 * yScale);
        yScaleMPos2 = Math.round(125 * yScale);

        xScaleMPosPixelValue = Math.round(130 * xScale);
        yScaleMPosPixelValue = Math.round(100 * yScale);
        yScaleMPosPixelValue2 = Math.round(75 * yScale);

        int xCanvasOverlayLocalizationValue = Math.round(175 * xScale);
        int yCanvasOverlayLocalizationValue = Math.round(100 * yScale);
        int yCanvasOverlayLocalizationValue2 = Math.round(120 * yScale);
        int yCanvasOverlayLocalizationValue3 = Math.round(140 * yScale);

        // canvas size
        Dimension dim = this.getSize();
        double width_local = dim.getWidth();
        double height_local = dim.getHeight();
    }

    /**
     * Get the window width value.
     *@return the window width value
     */
    public int getWindowWidth() {
        return selWindowWidth;
    }

    /**
     * get the window center value.
     *@ return the window center value.
     */
    public int getWindowCenter() {
        return selWindowCenter;
    }

    /**
     * Set the lookup table.
     * Create a new <code>RenderedOp<code/>.
     * Call repaint
     */
    public void setLookupTable(LookupTableJAI lookup) {
        if (orgImage == null) {
            return;
        }
        //System.out.println("ImageManipulatorCanvas:Set3");

        //LookupTableJAI lookup_save = lookup;

        //test
        //new ImageInfoPlanar().show(orgImage);
        //BufferedImage img10 = orgImage.getAsBufferedImage();
        //new ImageInfoBuffered().show(img10);
        //System.exit(33);
        //new ImageInfoBuffered().printPixelValues(img10);

        // RenderedOp represent a node in the rendered chain.
        RenderedOp op = LookupDescriptor.create(orgImage, lookup, null);
        //new ImageInfoPlanar().show(op);
        //System.exit(44);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(op);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        op = JAI.create("affine", pb);

        //test
        //new ImageInfoPlanar().show(op);
        //BufferedImage img11 = op.getAsBufferedImage();
        //new ImageInfoBuffered().show(img11);
        //new ImageInfoBuffered().printPixelValues(img11);

        //PlanarImage imgPlanar = op.createInstance();
        //ParameterBlock pb2 = new ParameterBlock();
        //double[] constant = {-300.0};
        //double[] offset = {1000.0};

        //pb2.add(constant);
        //pb2.add(offset);
        //pb2.addSource(orgImage);
        //op = JAI.create("addconst", pb2);


        //ParameterBlock pb2 = new ParameterBlock();
        //pb2.addSource(op);
        //pb2.add(val);
        //op = JAI.create("addconst", pb2);


        // Untile this node so that when compute the next node,
        // no extra memory and time are used in PlanarImage.getExtendedData().
        //ImageLayout il = new ImageLayout();
        //il.setTileWidth(256);
        //il.setTileHeight(256);
        //il.setTileGridXOffset(op.getMinX());
        //il.setTileGridYOffset(op.getMinY());
        //RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);

        //op = JAI.create("affine", pb, hints);

        /*ImageLayout tileLayout = new ImageLayout(op);
        tileLayout.setTileWidth(orgImage.getWidth());
        tileLayout.setTileHeight(orgImage.getHeight());
        RenderingHints tileHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, tileLayout);
        ParameterBlock pb2 = new ParameterBlock();
        pb2.addSource(op);
        op = JAI.create("format", pb2, tileHints);
         */

        displayImage = op;

        //displayImage = orgImage;
        //displayImage = op.createInstance();
        repaint();
    }

    /**
     * Set the zoom default value magnification.
     * This method is called when a new image is set
     * for the first time.
     */
    public void setZoomDefault(double mag) {
        setScaledImage(mag);
        //viewdex.windowLevel.setWindowLevel();
    }

    /**
     * Scale the image and center in the canvas.
     * There are 3 mag values with a special meaning.
     *
     * @param the magnification value.
     */
    private void setScaledImage(double mag) {
        String monitorOrientation;
        int width_local = (orgImage.getWidth());
        int height_local = (orgImage.getHeight());
        Dimension i = new Dimension(width_local, height_local);
        Dimension c = new Dimension(this.getWidth(), this.getHeight());

        /*
        if((this.getWidth() / this.getHeight()) > 0)
        monitorOrientation= "landscape";
        else
        monitorOrientation = "portrait";
         */

        //System.out.println("ImageCanvas.setScaledImage()");

        int widthMag = (int) (i.width * mag);
        int heightMag = (int) (i.height * mag);

        // Test for the special case of "FIT" for the Display Size.
        // "fit" in canvas" where mag == -0.0.
        // "fit2" in canvas" where mag == -2.0.
        // "fit3" in canvas" where mag == -3.0.

        // "FIT" or "fit"
        if (0 == Double.compare(mag, -0.0)) {
            double magX = (double) c.width / (double) i.width;
            double magY = (double) c.height / (double) i.height;

            //double magA = (magY > magX)? magX:magY;
            double magA = (magX > magY) ? magY : magX;

            double widthMag2 = (double) (i.width * magA);
            double heightMag2 = (double) (i.height * magA);

            double posX = (double) (c.width / 2) - (widthMag2 / 2);
            double posY = (double) (c.height / 2) - (heightMag2 / 2);

            atx.setToTranslation(posX, posY);
            atx.scale(magA, magA);
            //atx.rotate(Math.PI/2.0, c.width / 2, c.height / 2);
        } else {
            // "FIT2" or "fit2"
            if (0 == Double.compare(mag, -2.0)) {
                double magX = (double) c.width / (double) i.width;
                double magY = (double) c.height / (double) i.height;

                double magA = (magY > magX) ? magY : magX;

                double widthMag2 = (double) (i.width * magA);
                double heightMag2 = (double) (i.height * magA);

                double posX = (double) (c.width / 2) - (widthMag2 / 2);
                double posY = (double) (c.height / 2) - (heightMag2 / 2);

                atx.setToTranslation(posX, posY);
                atx.scale(magA, magA);
            } else {
                // "FIT3" or "fit3"
                if (0 == Double.compare(mag, -3.0)) {
                    double magWidth = (double) c.width / (double) i.width;
                    double magHeight = (double) c.height / (double) i.height;

                    double widthMag2 = (double) (i.width * magWidth);
                    double heightMag2 = (double) (i.height * magHeight);

                    double xpos = (double) (c.width / 2) - (widthMag2 / 2);
                    double ypos = (double) (c.height / 2) - (heightMag2 / 2);

                    atx.setToTranslation(xpos, ypos);
                    atx.scale(magWidth, magHeight);
                } else {
                    int xpos = (c.width / 2) - (widthMag / 2);
                    int ypos = (c.height / 2) - (heightMag / 2);
                    atx.setToTranslation(xpos, ypos);
                    atx.scale(mag, mag);
                }
            }
        }
    }

    /**
     */
    @Override
    public AffineTransform getTransform() {
        return atx;
    }

    /************************************************************
     * 
     ***********************************************************/
    /*
     * The image canvas goes black.
     */
    @Override
    public void resetImageCanvas() {
        displayImage = null;
        displayBufImage = null;
        orgImage = null;
        repaint();
    }

    /**
     * @return
     */
    @Override
    public PlanarImage getImage() {
        return orgImage;
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getImageSize() {
        return new Dimension(orgImage.getWidth(), orgImage.getHeight());
    }

    /**
     * Set the interpolation and update windowLevel.
     * @param mode
     */
    @Override
    public synchronized void setInterpolation(int mode) {
        interpMode = mode;
        viewdex.windowLevel.setWindowLevel();
    }

    /** Set the default interpolation.
     */
    public synchronized void setDefaultInterpolation(int mode) {
        interpMode = mode;
    }

    @Override
    public int getInterpolation() {
        return interpMode;
    }

    /**
     * NOT IN USE
     * Set the displayMode.
     */
    @Override
    public synchronized void setDisplayMode(int mode) {
        if (mode == displayMode) {
            return;
        }
        int oldmode = displayMode;
        //firePropertyChange("displayMode",oldmode,mode);
        this.displayMode = mode;

        int canvasWidth = this.getBounds().width;
        int canvasHeight = this.getBounds().height;
        int imageWidth_local = orgImage.getWidth();
        int imageHeight_local = orgImage.getHeight();

        double magX = (double) canvasWidth / (double) imageWidth_local;
        double magY = (double) canvasHeight / (double) imageHeight_local;

        switch (displayMode) {
            case CanvasControlMode.DISPLAY_ORIG:
                atx = AffineTransform.getTranslateInstance(0.0, 0.0);
                break;
            case CanvasControlMode.DISPLAY_SCALED:
                double mag = (magY > magX) ? magX : magY;
                atx.setToScale(mag, mag);
                break;
            case CanvasControlMode.DISPLAY_TO_FIT:
                atx.setToScale(magX, magY);
                break;
            case CanvasControlMode.DISPLAY_HALF_SIZE:
                atx.setToScale(0.5, 0.5);
                break;
        }
        viewdex.windowLevel.setWindowLevel();
    }

    /** Create the zoom result image.
     * Example from "medicalViewer"
     * NOT IN USE
     */
    private void zoomOperator(RenderedImage source,
            double magX, double magY) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add((float) magX);
        pb.add((float) magY);
        pb.add(0.0f);
        pb.add(0.0f);
        pb.add(Interpolation.getInstance(interpMode));

        // Tiling on this scale node: (1) Reduces the tiling memory
        // overhead; (2) Pull only the displayed tiles when the zoom
        // factor is large.
        //ImageLayout il = new ImageLayout();
        //il.setTileWidth(tileSize);
        //il.setTileHeight(tileSize);
        //RenderingHints hints =
        //        new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);

        RenderedOp op = JAI.create("scale", pb);
        createTiledImage(op.createInstance());
    }

    /*
     * NOT IN USE
     */
    @Override
    public int getDisplayMode() {
        return displayMode;
    }

    /*
     * NOT IN USE
     */
    @Override
    public synchronized void setFlipMode(int mode) {
        if (mode == flipMode) {
            return;
        }
        int oldmode = flipMode;
        //firePropertyChange("flipMode",oldmode, mode);
        this.flipMode = mode;
        //repaint();
    }

    @Override
    public int getFlipMode() {
        return flipMode;
    }

    @Override
    public void setMagFactor(double magFactor) {
        firePropertyChange("MagFactor",
                new Double(this.magFactor),
                new Double(magFactor));
        diffFactor = this.magFactor - magFactor;
        this.magFactor = magFactor;
    }

    @Override
    public double getMagFactor() {
        return magFactor;
    }

    @Override
    public void setShearFactor(double shearFactor) {
        firePropertyChange("ShearFactor",
                new Double(this.shearFactor),
                new Double(shearFactor));
        this.shearFactor = shearFactor;
    }

    @Override
    public double getShearFactor() {
        return shearFactor;
    }

    public double getShearFactorX() {
        return shearX;
    }

    public double getShearFactorY() {
        return shearY;
    }

    @Override
    public void setRotationAngle(double rotationAngle) {
        firePropertyChange("RotationAngle",
                new Double(this.rotationAngle),
                new Double(rotationAngle));
        this.rotationAngle = rotationAngle;
    }

    @Override
    public double getRotationAngle() {
        return rotationAngle;
    }

    // NOT IN USE
    public void paintImage(int magCenterX, int magCenterY, double mag) {
        setMagFactor(this.magFactor * mag);
        int dx = this.magCenterX - magCenterX;
        int dy = this.magCenterY - magCenterY;
        this.magCenterX = magCenterX;
        this.magCenterY = magCenterY;
        try {
            Point2D mgp = null;
            mgp = atx.inverseTransform((Point2D) (new Point(magCenterX - panX, magCenterY - panY)), (Point2D) mgp);
            double x = (mgp.getX() * mag) - mgp.getX();
            double y = (mgp.getY() * mag) - mgp.getY();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // NOT IN USE
    protected void applyTransform(RenderedImage ri, AffineTransform atx) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(ri);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);
        //createTiledImage(op.createInstance());
        //displayImage = op;
        repaint();
    }

    @Override
    public void applyTransform(AffineTransform atx) {
        applyTransform(orgImage, atx);
    }

    // NOT IN USE
    @Override
    public void resetManipulation() {
        panOffset = new Point(0, 0);
        magCenterX = 0;
        magCenterY = 0;
        panX = 0;
        panY = 0;
        magFactor = 1.0;
        shearX = 0.0;
        shearY = 0.0;
        rotationAngle = 0.0;
        scrollAnchor = new Point(0, 0);
        //createTiledImage(origImage);
        atx = AffineTransform.getTranslateInstance(0.0, 0.0);
        applyTransform(atx);
        repaint();
    }

    // test   NOT IN USE
    public void resetTranformation2() {
        panX = 0;
        panY = 0;
        Dimension i;

        // Find the canvas and image dimension.
        // Calculate the new translate position.
        Dimension c = new Dimension(this.getWidth(), this.getHeight());

        if (orgImage == null) {
            i = new Dimension(-1, -1);
        } else {
            int width_local = (int) (orgImage.getWidth());
            int height_local = (int) (orgImage.getHeight());
            i = new Dimension(width_local, height_local);
        }
        int magCenterX_local = c.width / 2;
        int magCenterY_local = c.height / 2;
        int xpos = (c.width / 2) - (i.width / 2);
        int ypos = (c.height / 2) - (i.height / 2);
        //double scaleX = atx.getScaleX();
        //double scaleY = atx.getScaleY();
        //AffineTransform at = new AffineTransform();
        //at.setToTranslation(xpos,ypos);
        //atx.translate(xpos, ypos);
        //atx = AffineTransform.getTranslateInstance((double)xpos, (double)ypos);
        atx.setToTranslation(xpos, ypos);
        //atx.scale(scaleX, scaleY);
        //atx.translate(magCenterX, magCenterY);
    }

    // test  NOT IN USE
    public void resetTranformation3() {
        Dimension c = new Dimension(this.getWidth(), this.getHeight());
        int canvasCenterX = c.width / 2;
        int canvasCenterY = c.height / 2;

        int width_local = (int) (orgImage.getWidth());
        int height_local = (int) (orgImage.getHeight());

        AffineTransformOp atop = new AffineTransformOp(atx, (this.getInterpolation() + 1));
        //Rectangle2D rec = atop.getBounds2D(displayImage.getAsBufferedImage());
        //double centerX = rec.getCenterX();
        //double centerY = rec.getCenterY();
        //double x = rec.getX();
        //double y = rec.getY();

        try {
            Point2D mgp2 = null;
            mgp2 = atx.inverseTransform((Point2D) (new Point((int) canvasCenterX, (int) canvasCenterY)), (Point2D) mgp2);

            Point2D mgp = null;
            //mgp = atx.inverseTransform((Point2D)(new Point((int)x, (int)y)),(Point2D)mgp);
            //double scalex = atx.getScaleX();
            //double scaley = atx.getScaleY();
            //double magNewX = mag / scalex;
            //double magNewY = mag / scalex;
            //double x = (mgp.getX()*magNewX)-mgp.getX();
            //double y = (mgp.getY()*magNewY)-mgp.getY();
            //double x2 = mgp2.getX() - x;
            //double y2 = mgp2.getY() - y;

            //atx.translate(x2, y2);
            //atx.setToTranslation(canvasCenterX - width/2, canvasCenterY - height/2);
            //atx.setToScale(scalex,  scaley);
            //atx.translate(canvasCenterX, canvasCenterY);
            //atx.translate(-mgp.getX(), -mgp.getY());
            //atx.scale(magNewX, magNewY);
            //imageCanvas.applyWindowLevel();
            //applyTransform(atx);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Reset the transformation.
     * Center the image in the canvas.
     * No magnification of the image.
     * Called by pan->reset.
     */
    public void resetTranformation() {
        int width_local = (int) (orgImage.getWidth());
        int height_local = (int) (orgImage.getHeight());
        Dimension i = new Dimension(width_local, height_local);
        Dimension c = new Dimension(this.getWidth(), this.getHeight());

        double scaleX = atx.getScaleX();
        double scaleY = atx.getScaleY();

        int widthMag = (int) (i.width * scaleX);
        int heightMag = (int) (i.height * scaleY);

        int xpos = (c.width / 2) - (widthMag / 2);
        int ypos = (c.height / 2) - (heightMag / 2);
        atx.setToTranslation(xpos, ypos);
        atx.scale(scaleX, scaleY);
    }

    /**
     * Set the zoom value magnification.
     * This method is called when a zoom button with a fixed
     * value is activated. The setWindowLevel is called for
     * an update of the canvas.
     */
    public void setZoom(double mag) {
        setScaledImage(mag);
        viewdex.windowLevel.setWindowLevel();
    }

    /**
     * Test Test NOT IN USE
     */
    private void setScaledImage3(double mag) {
        //int dx = this.magCenterX -magCenterX;
        //int dy = this.magCenterY-magCenterY;
        Dimension c = new Dimension(this.getWidth(), this.getHeight());
        int magCenterX_local = c.width / 2;
        int magCenterY_local = c.height / 2;
        //System.out.println("magCenterX = " + magCenterX);
        try {
            Point2D mgp = null;
            //atx = this.getTransform();
            AffineTransform at = new AffineTransform();
            mgp = at.inverseTransform((Point2D) (new Point(magCenterX_local, magCenterY_local)), (Point2D) mgp);
            double x = (mgp.getX() * mag) - mgp.getX();
            double y = (mgp.getY() * mag) - mgp.getY();
            //scale(-x,-y, mag);

            at.translate(-x, -y);
            //double x = atx.getTranslateX();
            //double y = atx.getTranslateY();
            at.scale(mag, mag);
            atx.concatenate(at);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Test Test NOT IN USE
     */
    private void setScaledImage2(double mag) {
        AffineTransformOp atop = new AffineTransformOp(atx, 1);
        Rectangle2D rec = atop.getBounds2D(orgImage.getAsBufferedImage());
        double centerX = rec.getCenterX();
        double centerY = rec.getCenterY();
        //double width = rec.getWidth();
        //double height = rec.getHeight();

        // magnification  center
        Dimension c = new Dimension(this.getWidth(), this.getHeight());
        centerX = c.width / 2;
        centerY = c.height / 2;
        int width_local = (int) (orgImage.getWidth());
        int height_local = (int) (orgImage.getHeight());
        Dimension i = new Dimension((int) width_local, (int) height_local);

        int xpos = (c.width / 2) - (i.width / 2);
        int ypos = (c.height / 2) - (i.height / 2);

        //atx.scale(mag, mag);
        atx.translate(xpos, ypos);

        //AffineTransform atx2 = new AffineTransform();
        //atx2.setToTranslation(xpos,ypos);
        //atx.concatenate(atx2);

        //AffineTransform at = new AffineTransform();
        //at.scale(mag, mag);
        //at.setToTranslation(xpos, ypos);
        //atx.concatenate(at);

        /*
        try{
        Point2D mgp = null;
        AffineTransform atx = this.getTransform();
        mgp = atx.inverseTransform((Point2D)(new Point((int)centerX, (int)centerY)), (Point2D)mgp);
        double scalex = atx.getScaleX();
        double scaley = atx.getScaleY();
        double magNewX = mag / scalex;
        double magNewY = mag / scalex;
        double x = (mgp.getX() * magNewX) - mgp.getX();
        double y = (mgp.getY() * magNewY) - mgp.getY();
        //atx.translate(-x,-y);
        //atx.scale(magNewX, magNewY);
        //atx.setToTranslation(xpos,ypos);
        //imageCanvas.applyWindowLevel();
        //applyTransform(atx);
        }catch (Exception e) {System.out.println(e);}
         */
    }

    /**
     * Scale the image and retain the prevoius center point.
     * This one works fine. NOT IN USE
     */
    private void setScaledImage4(double mag) {
        AffineTransformOp atop = new AffineTransformOp(atx, 1);
        Rectangle2D rec = atop.getBounds2D(orgImage.getAsBufferedImage());
        double centerX = rec.getCenterX();
        double centerY = rec.getCenterY();

        try {
            Point2D mgp = null;
            AffineTransform atx_local = this.getTransform();
            mgp = atx_local.inverseTransform((Point2D) (new Point((int) centerX, (int) centerY)), (Point2D) mgp);
            double scalex = atx_local.getScaleX();
            double scaley = atx_local.getScaleY();
            double magNewX = mag / scalex;
            double magNewY = mag / scaley;
            double x = (mgp.getX() * magNewX) - mgp.getX();
            double y = (mgp.getY() * magNewY) - mgp.getY();
            atx_local.translate(-x, -y);
            atx_local.scale(magNewX, magNewY);
            //imageCanvas.applyWindowLevel();
            //applyTransform(atx);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * In Swing components, the paintComponent() method actually draws the image.
     * This method is called by the painting threads when repaint() is called.
     * An application should override the paintComponent for painting.
     * paint() is called by both system-triggered and application-triggered
     * painting.
     * paint() call paintComponent(), paintBorder(), paintChildren().
     */
    @Override
    public void paintComponent(Graphics gc) {
        Graphics2D g = (Graphics2D) gc;
        Rectangle rect = this.getBounds();
        int height = this.getBounds().height;
        int width = this.getBounds().width;
        TiledImage ti = null;

        /*if((width != rect.width) || (height != rect.height)){
        double magx = rect.width/(double)width ;
        double magy = rect.height/(double)height ;
        }*/

        //System.out.println("ImageCanvas.paintComponent: renderCnt = " + ++renderCnt);

        g.setColor(canvasColor);
        //g.setColor(new Color(200,40,40));
        // Clear the components background to the background color.
        g.fillRect(0, 0, rect.width, rect.height);

        // Enabling antialiasing
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (displayImage != null) {
            //System.out.println("ImageCanvas.paintComponent: 1.0");
            //g.setClip(300,300, 300,300);
            g.setFont(overlayTextFont);
            g.setColor(overlayTextColor);
            g.drawRenderedImage(displayImage, atx2);


            /*******************************************
             * WindowLevel
             ******************************************/
            if (canvasOverlayWindowLevelStatus && true) {
                int windowValue, windowCenterValue;
                if (canvasOverlayWindowingFixedMinimumStatus) {
                    if ((selWindowWidth - windowingFixedMinimumValue) <= 1) {
                        windowValue = 1;
                        windowCenterValue = (windowingFixedMinimumValue);
                    } else {
                        windowValue = selWindowWidth - windowingFixedMinimumValue;
                        windowCenterValue = ((selWindowWidth + windowingFixedMinimumValue) / 2);
                    }
                    window = "Window: " + windowValue;
                    level = "Level: " + windowCenterValue;
                } else {
                    window = "Window: " + selWindowWidth;
                    level = "Level: " + selWindowCenter;
                }
                
                //  rescaleSlope
                // test
                // Present the valuse when window settings is between 0 and 1 
                //if(rescaleSlope != 0 || rescaleSlope != 1){
                  //  float windowf = (float) (selWindowWidth * rescaleSlope);
                   // window = "Window: " + windowf;
                //}
                
                g.drawString(window, xPosWindowWidth, yPosWindowWidth);
                g.drawString(level, xPosWindowCenter, yPosWindowCenter);
            }

            if (canvasOverlayWindowingFixedMinimumStatus && true) {
                int windowValue;
                //System.out.println("ImageCanvas.paintComponent: 2.0");

                //windowWidth = windowingFixedMinimumValue + selWindowWidth;
                if ((selWindowWidth - windowingFixedMinimumValue) <= 1) {
                    windowValue = windowingFixedMinimumValue;

                    if (imgStat != null) {
                        if (imgStat[0][6] == 0) {
                            windowingPercent = 0;
                        } else {
                            double wp = (double) windowingFixedMinimumValue / imgStat[0][6];
                            windowingPercent = Math.round(wp * 100);
                        }
                    }
                } else {
                    windowValue = selWindowWidth;

                    if (imgStat != null) {
                        if (imgStat[0][6] == 0) {
                            windowingPercent = 0;
                        } else {
                            double wp = (double) selWindowWidth / imgStat[0][6];
                            windowingPercent = Math.round(wp * 100);
                        }
                    }
                }

                String minToMax = "From: " + "" + windowingFixedMinimumValuePercent + " % "
                        + "(" + windowingFixedMinimumValue + ")"
                        + " to: " + windowingPercent + " % " + "(" + windowValue + ")";
                g.drawString(minToMax, xPosFixedMinimum, yPosFixedMinimum);

                // Calculate new level to display
                //int newLevel = selWindowWidth/2;
                //String level = "Level: " + newLevel;
                //g.drawString(level, xPosWindowCenter, yPosWindowCenter);
            }

            /******************************************
             * MousePosition
             *****************************************/
            if (canvasOverlayMousePositionStatus && true) {
                //System.out.println("ImageCanvas.paintComponent: 1");

                String posX = "X: " + Integer.toString(mousePosX);
                String posY = "Y: " + Integer.toString(mousePosY);
                g.drawString(posX, xPosMousePositionX, yPosMousePositionX);
                g.drawString(posY, xPosMousePositionY, yPosMousePositionY);
            }

            /********************************************
             * MousePositionPixelValue
             *******************************************/
            if (canvasOverlayMousePositionPixelValueStatus && true) {
                //System.out.println("ImageCanvas.paintComponent: 2");

                String pixVal;
                if(photometricInterpretation.equalsIgnoreCase("RGB")){
                    pixVal = "Value: " +
                        "R:" + Integer.toString(pixelValue[0]) + " " +
                        "G:" + Integer.toString(pixelValue[1]) + " " +
                        "B:" + Integer.toString(pixelValue[2]);
                }else{
                    pixVal = "Value: " + Integer.toString(pixelValue[0]);
                    // zzzzzzzzzz test
                    //float pixVal22 = (float) pixelValue[0] * 1/4096;
                    //pixVal = "Value: " + pixVal22;
                    //if(pixelValue[0] > 200){
                        //float pixVal2 = (float) pixelValue[0] * 1/4096;
                        //System.out.println(pixVal2);
                    //}
                    //System.out.println("pixVal = " + pixelValue[0] + " " + pixVal2);
                }

                //test
                //FontMetrics fm = g.getFontMetrics();
                //Rectangle2D area = fm.getStringBounds(pixVal, g);
                //double w = area.getWidth();
                //double h = area.getHeight();
                //System.out.println("ImageCanvas.PaintComponent: Value= " + pixVal + " w= " + w + " h= " + h);
                g.drawString(pixVal, xPosMousePositionValue, yPosMousePositionValue);

                if (pixelValueSigned && false) {
                    String pixVal2 = "Value: " + Integer.toString(pixelValue2[0]);
                    g.drawString(pixVal2, xPosMousePositionValue, yPosMousePositionValue);
                }
            }

            /***************************************************
             * Localization
             **************************************************/
            if (canvasOverlayLocalizationRenderStatus && true) {
                //System.out.println("ImageCanvas.paintComponent Localization " + ++renderCntLocalization);
                g.setStroke(localizationSymbolStroke);
                g.setColor(localizationLineColor);

                for (int i = 0; i < lList.size(); i++) {
                    StudyDbLocalizationM item = lList.get(i);
                    Point2D p = item.getUserSpacePointDouble();
                    if (item.getLocalizationStatus() == StudyDbLocalizationStatus.ACTIVE
                            || item.getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED) {
                        Line2D.Double line1 = new Line2D.Double(p.getX() - localizationActiveSymbolLineSize,
                                p.getY(), p.getX() + localizationActiveSymbolLineSize,
                                p.getY());
                        Line2D.Double line2 = new Line2D.Double(p.getX(),
                                p.getY() - localizationActiveSymbolLineSize,
                                p.getX(), p.getY() + localizationActiveSymbolLineSize);
                        g.draw(line1);
                        g.draw(line2);
                    }
                    if (item.getLocalizationStatus() == StudyDbLocalizationStatus.SET
                            || item.getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED) {
                        Line2D.Double line3 = new Line2D.Double(p.getX() - localizationActiveSymbolLineSize,
                                p.getY(), p.getX() + localizationActiveSymbolLineSize, p.getY());
                        Line2D.Double line4 = new Line2D.Double(p.getX(), p.getY() - localizationActiveSymbolLineSize,
                                p.getX(), p.getY() + localizationActiveSymbolLineSize);
                        Shape ellipse = new Ellipse2D.Double((p.getX() - localizationSetSymbolElipseSize),
                                (p.getY() - localizationSetSymbolElipseSize), (localizationSetSymbolElipseSize * 2),
                                (localizationSetSymbolElipseSize * 2));
                        g.draw(ellipse);
                        g.draw(line3);
                        g.draw(line4);
                    }
                    if ((item.getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED)) {
                        Line2D.Double line1 = new Line2D.Double(p.getX() - localizationSelectSymbolLineXSize,
                                p.getY() - localizationSelectSymbolLineYSize,
                                p.getX() + localizationSelectSymbolLineXSize,
                                p.getY() - localizationSelectSymbolLineYSize);
                        Line2D.Double line2 = new Line2D.Double(p.getX() - localizationSelectSymbolLineXSize,
                                p.getY() + localizationSelectSymbolLineYSize,
                                p.getX() + localizationSelectSymbolLineXSize,
                                p.getY() + localizationSelectSymbolLineYSize);
                        Line2D.Double line3 = new Line2D.Double(p.getX() - localizationSelectSymbolLineXSize,
                                p.getY() - localizationSelectSymbolLineYSize,
                                p.getX() - localizationSelectSymbolLineXSize,
                                p.getY() + localizationSelectSymbolLineYSize);
                        Line2D.Double line4 = new Line2D.Double(p.getX() + localizationSelectSymbolLineXSize,
                                p.getY() - localizationSelectSymbolLineYSize,
                                p.getX() + localizationSelectSymbolLineXSize,
                                p.getY() + localizationSelectSymbolLineYSize);
                        Shape ellipse = new Ellipse2D.Double((p.getX() - localizationSetSymbolElipseSize),
                                (p.getY() - localizationSetSymbolElipseSize), (localizationSetSymbolElipseSize * 2),
                                (localizationSetSymbolElipseSize * 2));
                        g.draw(ellipse);
                        g.draw(line1);
                        g.draw(line2);
                        g.draw(line3);
                        g.draw(line4);
                    }
                }
            }

            if (canvasOverlayLocalizationPositionStatus && true) {
                g.setColor(localizationPositionTextColor);
                //System.out.println("ImageCanvas.paintComponent: 5");
                String posX = "Localization x: " + Integer.toString(xPosLocalizationMark);
                String posY = "Localization y: " + Integer.toString(yPosLocalizationMark);
                String posZ = "Localization z: " + Integer.toString(zPosLocalizationMark);
                g.drawString(posX, xPosLocalizationX, yPosLocalizationX);
                g.drawString(posY, xPosLocalizationY, yPosLocalizationY);
                g.drawString(posZ, xPosLocalizationZ, yPosLocalizationZ);
            }

            /*************************************************
             * Div
             ************************************************/
            if (canvasOverlayPatientIdStatus) {
                String str = "PatientId: " + canvasOverlayPatientIdValue;
                g.drawString(str, xPosPatientId, yPosPatientId);
            }

            if (canvasOverlayStackNoStatus) {
                String str = "Case: " + canvasOverlayStackNoValue;
                g.drawString(str, xPosStackNo, yPosStackNo);
            }

            if (canvasOverlayMarkNoStatus) {
                String str = "Marks: " + canvasOverlayMarkNoValue;
                g.drawString(str, xPosMarkNo, yPosMarkNo);
            }

            /*********************************************
             * Distance
             ********************************************/
            //test
            /*if(dList == null)
            System.out.println("dList=null");
            else if(dList != null)
            System.out.println("dList=NOT null");
             */
            if (roiDistanceUpdateStatus && dList != null) {
                //System.out.println("ImageCanvas.painComponent: roiDistanceUpdateStatus");
                //System.out.println("ImageCanvas.painComponent: distance " + ++renderCntDistance);

                //Color roiDistanceDrawingLineColor = new Color(255,255,10);
                //Color colorLine = new Color(0,0,100);
                //String font = "SansSerif-plain-14";
                //Font roiDistanceFont = Font.decode(font);
                //String roiDistanceText = "Distance: 2.56 mm";

                g.setStroke(roiDistanceLineStroke);
                g.setFont(roiDistanceFont);
                //g.setColor(roiDistanceFontColor);
                //int len = dList.size();
                //System.out.println("ImageCanvas.painComponent: size= " + len);

                for (int i = 0; i < dList.size(); i++) {
                    //System.out.println("ImageCanvas.painComponent: roiDistanceUpdateStatus i=" + i);

                    StudyDbROID roid = dList.get(i);
                    Point2D startPointUserSpace = roid.getStartPointUserSpace();
                    Point2D endPointUserSpace = roid.getEndPointUserSpace();
                    Point2D startPointImageSpace = roid.getStartPointImageSpace();
                    Point2D endPointImageSpace = roid.getEndPointImageSpace();

                    //20160405
                    if (startPointUserSpace == null || endPointUserSpace == null
                            || startPointImageSpace == null || endPointImageSpace == null) {
                        break;
                    }

                    // distance
                    //double dist = startPointImageSpace.distance(endPointImageSpace);
                    double dist = roid.getDistance();

                    //double d2 = Math.round(dist*100.0/100.0);
                    //DecimalFormat df2 = new DecimalFormat("#,###,###,##0.00");
                    //Double dd2dec = new Double(df2.format(dist)).doubleValue();

                    DecimalFormat df = new DecimalFormat("0.00");
                    String dstStr = df.format(dist);

                    // This one works as well
                    /*
                    int decimalPlaces = 3;
                    BigDecimal bd = new BigDecimal(dist);
                    bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
                    double dist2 = bd.doubleValue();
                    String dstStr = Double.toString(dist2);
                     */
                    String dStr = "Length: " + dstStr + " mm";

                    g.setColor(roiDistanceLineColor);
                    g.drawLine((int) startPointUserSpace.getX(), (int) startPointUserSpace.getY(),
                            (int) endPointUserSpace.getX(), (int) endPointUserSpace.getY());

                    //g.drawLine((int) dLine.getX1(), (int) dLine.getY1(), (int) dLine.getX2(), (int) dLine.getY2());
                    //g.drawLine(startPointX, startPointY, curPointX, curPointY);//g.drawString(roiDistanceText, curPointX + 10, curPointY - 10);
                    //Math.r
                    //g.setColor(new Color(20, 255, 20));
                    //g.drawString(dStr, (int) endPointUserSpace.getX() + 10,
                    //      (int) endPointUserSpace.getY() - 10);

                    // text
                    g.setColor(roiDistanceFontColor);
                    g.drawString(dStr, (int) endPointUserSpace.getX() + 10,
                            (int) endPointUserSpace.getY() - 10);

                    if (roiDistanceGrabStatus) {
                        //test
                        //BasicStroke bs = new BasicStroke(1);
                        //g.setStroke(bs);
                        g.setColor(new Color(0, 0, 0));
                        g.drawRect((int) startPointUserSpace.getX() - 2, (int) startPointUserSpace.getY() - 2, 4, 4);
                        g.drawRect((int) endPointUserSpace.getX() - 2, (int) endPointUserSpace.getY() - 2, 4, 4);

                        g.setColor(new Color(255, 255, 255));
                        g.fillRect((int) startPointUserSpace.getX() - 1, (int) startPointUserSpace.getY() - 1, 3, 3);
                        g.fillRect((int) endPointUserSpace.getX() - 1, (int) endPointUserSpace.getY() - 1, 3, 3);


                        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        //g.drawOval((int) startPointUserSpace.getX() - 1, (int) startPointUserSpace.getY() - 1, 2, 2);
                        //g.drawOval((int) endPointUserSpace.getX() - 1, (int) endPointUserSpace.getY() - 1, 2, 2);
                        //g.fillOval((int) startPointUserSpace.getX() - 1, (int) startPointUserSpace.getY() - 1, 2, 2);
                        //g.fillOval((int) endPointUserSpace.getX() - 1, (int) endPointUserSpace.getY() - 1, 2, 2);
                    }

                    /*
                    if(setCanvasROIDistanceGrabStatus){
                    //g.drawRect((int) startPointUserSpace.getX() - 2, (int) startPointUserSpace.getY() - 2, 5, 5);
                    //g.drawRect((int) endPointUserSpace.getX() -2, (int) endPointUserSpace.getY() -2, 5, 5);

                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.drawOval((int) startPointUserSpace.getX() - 2, (int) startPointUserSpace.getY() - 2, 4, 4);
                    g.drawOval((int) endPointUserSpace.getX() - 2, (int) endPointUserSpace.getY() - 2, 4, 4);
                    g.fillOval((int) startPointUserSpace.getX() - 2, (int) startPointUserSpace.getY() - 2, 4, 4);
                    g.fillOval((int) endPointUserSpace.getX() - 2, (int) endPointUserSpace.getY() - 2, 4, 4);
                    }*/
                }
                roiDistanceUpdateStatus = true;
            }


            /**********************************************
             * Area (Volume)
             * Mean Value
             *********************************************/
            //test
            /*if(dList == null)
            System.out.println("dList=null");
            else if(dList != null)
            System.out.println("dList=NOT null");
             */
            if (roiVolumeUpdateStatus && vList != null) {
                //System.out.println("ImageCanvas.painComponent: roiVolumeUpdateStatus");
                //System.out.println("ImageCanvas.painComponent: area " + ++renderCntVolume);

                g.setStroke(roiVolumeLineStroke);
                g.setFont(roiVolumeFont);
                //g.setColor(roiDistanceFontColor);
                //int len = dList.size();
                //System.out.println("ImageCanvas.painComponent: size= " + len);

                //int size = vList.size();
                //System.out.println("size = " + size);
                for (int i = 0; i < vList.size(); i++) {
                    //System.out.println("ImageCanvas.painComponent: roiDistanceUpdateStatus i=" + i);

                    StudyDbROIV roiv = vList.get(i);
                    Shape shapeUser = roiv.getShapeUser();
                    //Shape shapeImage = roiv.getShapeImage();
                    g.setColor(roiVolumeLineColor);
                    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    g.draw(shapeUser);
                    //g.draw(shapeImage);

                    //PathIterator pi = shapeUser.getPathItevvvvvvvvvvvvrator(null);
                    //double[] coords = new double[100];
                    //boolean status = pi.isDone();
                    //int pst = pi.currentSegment(coords); // PathSegmentType

                    if (roiVolumeUpdateTextStatus) {
                        //System.out.println("ImageCanvas.paintComponenet. roiVolumeUpdateTextStatus" + "cnt= " + ++renderCntVolume);
                        //int nPix = roiv.getPixelCount();
                        double area = roiv.getArea();
                        int m = roiv.getMean();
                        //System.out.println("ImageCanvas.PaintComponenet() Volume,Mean:" + area + " " + m);
                        int m2 = (int) ((m - imgStat[0][7]) + rescaleIntercept - rescaleIntercept);
                        if (pixelValueSigned) {
                            m2 = (int) (m - imgStat[0][7]);
                        }

                        DecimalFormat df = new DecimalFormat("0.00");
                        String af = df.format(area);
                        //String nStr = "Number of pixels: " + nPix;v
                        if (m2 == -32768) {
                            m2 = 0;
                        }
                        String aStr = "Area: " + af + " mm\u00B2";
                        String mStr = "Mean value: " + m2;

                        // text
                        g.setColor(roiDistanceFontColor);
                        Point2D userSpaceStartPointDouble = roiv.getUserSpaceStartPointDouble();
                        //Point2D userSpaceStartPointDouble = roiv.getUserSpaceStartPointDouble();
                        //g.drawString(nStr, (int) userSpaceStartPointDouble.getX() + 10,
                        //      (int) userSpaceStartPointDouble.getY()- 30);
                        g.drawString(aStr, (int) userSpaceStartPointDouble.getX() + 15,
                                (int) userSpaceStartPointDouble.getY() - 25);
                        g.drawString(mStr, (int) userSpaceStartPointDouble.getX() + 15,
                                (int) userSpaceStartPointDouble.getY() - 3);
                        //g.drawString(nStr, (int) coords[0] + 10,
                        //      (int) coords[1] - 10);
                    }
                }
                roiVolumeUpdateStatus = true;
            }

            /*************************************************
             * Mean Value
             * Circle
             *************************************************/
            //test
            /*
            if(pvList == null)
            System.out.println("pvList=null");
            else if(pvList != null)
            System.out.println("pvList=NOT null");
             */
            if (roiPixelValueMeanUpdateStatus & pvList != null) {
                //System.out.println("ImageCanvas.painComponent: roiPixelValueUpdateStatus");
                //if(true & pvList != null){
                //System.out.println("ImageCanvas.painComponent pixel " + ++renderCntMeanValue);

                for (int i = 0; i < pvList.size(); i++) {
                    //System.out.println("ImageCanvas.painComponent: roiPixelValueUpdateStatus i=" + i);

                    StudyDbROIPixelValueD roip = pvList.get(i);

                    //Point2D userSpaceStartPoint = roip.getUserSpaceStartPoint();
                    Point2D userSpaceStartPointAdj = roip.getUserSpaceStartPointAdj();
                    //Point2D userSpaceEndPoint = roip.getUserSpaceEndPoint();
                    //Point2D imageSpaceStartPoint = roip.getImageSpaceStartPoint();
                    double w = roip.getWidth();
                    double h = roip.getHeight();
                    int m = roip.getMean();
                    int m2 = (int) ((m - imgStat[0][7]) + rescaleIntercept - rescaleIntercept);
                    if (pixelValueSigned) {
                        m2 = (int) (m - imgStat[0][7]);
                    }
                    //System.out.println("ImageCanvas.PaintComponent() MeanValue Ellipse :" + m2);

                    // userspace
                    Shape s = new Ellipse2D.Double(userSpaceStartPointAdj.getX(), userSpaceStartPointAdj.getY(), w * 2, h * 2);
                    //Shape s = new Ellipse2D.Double(imageSpaceStartPoint.getX(), imageSpaceStartPoint.getY(), w*2, h*2);

                    //Shape s2 = roip.getShape();

                    g.setStroke(pixelValueMeanDrawingLineStroke);
                    g.setFont(pixelValueMeanFont);
                    g.setColor(pixelValueMeanDrawingLineColor);
                    g.draw(s);

                    //DecimalFormat df2 = new DecimalFormat("0.00");
                    //String dstStr = df2.format(m);
                    String meanText = "Mean value:  " + m2;

                    int x2 = 0, y2 = 0;
                    int x = (int) roip.getUserSpaceStartPoint().getX();
                    int y = (int) roip.getUserSpaceStartPoint().getY();
                    int xcur = (int) roip.getUserSpaceCurrentPoint().getX();
                    int ycur = (int) roip.getUserSpaceCurrentPoint().getY();
                    if (xcur > x && ycur < y) {
                        x2 = xcur + 15;
                        y2 = ycur - 10;
                    } else {
                        if (xcur < x && ycur < y) {
                            x2 = xcur - 160;
                            y2 = ycur - 10;
                        } else {
                            if (xcur < x && ycur > y) {
                                x2 = xcur - 165;
                                y2 = ycur + 15;
                            } else {
                                if (xcur > x && ycur > y) {
                                    x2 = xcur + 20;
                                    y2 = ycur + 20;
                                } else {
                                    x2 = xcur + 15;
                                    y2 = ycur - 10;
                                }
                            }
                        }
                    }

                    //System.out.println(x + ", " + y);
                    //System.out.println(xcur + "," + ycur);
                    //System.out.println(x2 + "," + y2);
                    //System.out.println();

                    g.setColor(pixelValueMeanFontColor);
                    g.drawString(meanText, x2, y2);

                    //g.drawString(meanText, (int) userSpaceStartPointAdj.getX() + width2/2 + 10,
                    //      (int) userSpaceStartPointAdj.getY() - 10);
                }
                roiPixelValueMeanUpdateStatus = true;
            }
        }
    }

    /* Test NOT IN USE
     * Called by zoom for update of the canvas.
     */
    @Override
    public void paintImage2() {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(orgImage);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);
        //displayImage = op;
        repaint();
    }

//ImageDisplay Methods
    @Override
    public void setAWTImage(Image image) {
        awtImage = image;
    }

    @Override
    public Image getAWTImage() {
        return awtImage;
    }

    @Override
    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    @Override
    public int getImageType() {
        return imageType;
    }

    public void setClip(Shape clip) {
    }

    @Override
    public void setBufferedImage(BufferedImage image) {
    }

    @Override
    public BufferedImage getBufferedImage() {
        return null;
    }

    @Override
    public void setOffScreenImage(BufferedImage image) {
    }

    @Override
    public BufferedImage getOffScreenImage() {
        return null;
    }

    /** Set the display image
     */
    @Override
    public void setDisplayImage(PlanarImage image) {
        //this.displayImage = image; //mod
    }

    @Override
    public BufferedImage getDisplayedImage() {
        return null;
    }

    public void invert(boolean on) {
    }

    @Override
    public void setInvert(boolean on) {
    }

    @Override
    public boolean getInvert() {
        return true;
    }

    @Override
    public boolean paintImage() {
        return true;
    }

    /**
     * Test test test
     */
    @Override
    public void setTransform(AffineTransform at, boolean renderStatus) {
        atx = at;
        //ParameterBlock pb = new ParameterBlock();
        //pb.addSource(getImage());
        //pb.add(at);
        //pb.add(Interpolation.getInstance(interpMode));
        //RenderedOp op = JAI.create("affine", pb);
        //displayImage = op;
        if (renderStatus) {
            repaint();
        }
    }

    /** Sets the image.
     * @param img the planar image.
     * NOT IN USE
     */
    public void setImage222(PlanarImage img) {
        orgImage = img;
        panX = 0;
        panY = 0;
        atx = AffineTransform.getTranslateInstance(0.0, 0.0);
        RenderedOp op = makeTiledImage(img);
        //displayImage = op.createInstance();
        sampleModel = displayImage.getSampleModel();
        colorModel = displayImage.getColorModel();
        //getTileInfo(displayImage);
        //fireTilePropertyChange();
        imageDrawn = false;
        repaint();
    }

    /**
     * Set the image for display.
     * Calculate the position.
     * Do NOT the rendering.
     *
     * @param img the planar image.
     */
    public void setImage(PlanarImage img) {
        orgImage = img;

        // Slow down the scroll of images
        //orgImage = new TiledImage(img, true);
        //orgImage2 = img;
        panX = 0;
        panY = 0;
        Dimension i;

        //System.out.println("ImageCanvas.setImage()");
        //return;

        // These values are used when calculating the mouse
        // pointing pixel value for display in the"canvas
        // information overlay".

        //qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
        /*
        orgImage_sm = orgImage.getSampleModel();
        orgImage_raster = orgImage.getData();
        orgImage_db = orgImage_raster.getDataBuffer();
        orgImage_rec = orgImage_raster.getBounds();

        // Find the canvas and image dimension.
        // Calculate the new translate position.
        Dimension c = new Dimension(this.getWidth(), this.getHeight());

        if (img == null) {
        i = new Dimension(-1, -1);
        } else {
        int width = (img.getWidth());
        int height = (img.getHeight());
        i = new Dimension(width, height);
        }
        int magCenterX = c.width / 2;
        int magCenterY = c.height / 2;
        int xpos = (c.width / 2) - (i.width / 2);
        int ypos = (c.height / 2) - (i.height / 2);
        //AffineTransform at = new AffineTransform();
        //at.setToTranslation(xpos,ypos);
        //atx.translate(xpos, ypos);
        atx = AffineTransform.getTranslateInstance((double) xpos, (double) ypos);
         */
        //qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
    }

    /**
     * Set the image for display.
     * Calculate the position.
     *
     * @param img the planar image.
     * NOT IN USE
     */
    public void setImage(PlanarImage img, AffineTransform atx) {
        orgImage = img;

        // These values are used when calculating the mouse
        // pointing pixel value for display in the"canvas
        // information overlay".
        orgImage_sm = orgImage.getSampleModel();
        orgImage_raster = orgImage.getData();
        orgImage_db = orgImage_raster.getDataBuffer();
        orgImage_rec = orgImage_raster.getBounds();
    }

    /**
     * test no good
     * NOT IN USE
     */
    public void setImage2() {
        //orgImage = null;
        //displayImage = null;
        //orgImage = orgImage2;
        //repaint();
        //setImage(orgImage2);
    }

    /**
     * Replace the displayed image.
     * @param im the new image for display.
     * NOT IN USE
     */
    public void set2(PlanarImage img) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);
        createTiledImage(op.createInstance());
    }

    /**
     * Called by windowLevel.
     * Replace the displayed image.
     * @param im the new image for display.
     * NOT IN USE
     */
    public void set(PlanarImage img) {
        //displayImage = img;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);
        //displayImage = op;
        //createTiledImage(op.createInstance());
        repaint();
    }

    /**
     * test
     */
    public void createTiledImage(PlanarImage img) {
        /*displayImage =  makeTiledImage(img);
        sampleModel = displayImage.getSampleModel();
        colorModel = displayImage.getColorModel();
        //getTileInfo(displayImage);
        //fireTilePropertyChange();
        imageDrawn = false;
        repaint();
         **/
    }

    /**
     * makeTiledImage
     */
    protected RenderedOp makeTiledImage(PlanarImage img) {
        ImageLayout tileLayout = new ImageLayout(img);
        tileLayout.setTileWidth(256);
        tileLayout.setTileHeight(256);
        RenderingHints tileHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, tileLayout);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        return JAI.create("format", pb, tileHints);
    }

    /** Computes tile information of the specified image.
     * @param img the planar image.
     */
    protected void getTileInfo(PlanarImage img) {
        imageWidth = img.getWidth();
        imageHeight = img.getHeight();
        tileWidth = img.getTileWidth();
        tileHeight = img.getTileHeight();
        maxTileIndexX = img.getMinTileX() + img.getNumXTiles() - 1;
        maxTileIndexY = img.getMinTileY() + img.getNumYTiles() - 1;
        maxTileCordX = img.getMaxX();
        maxTileCordY = img.getMaxY();
        minTileIndexX = img.getMinTileX();
        minTileIndexY = img.getMinTileY();
        minTileCordX = img.getMinX();
        minTileCordY = img.getMinY();
        tileGridXOffset = img.getTileGridXOffset();
        tileGridYOffset = img.getTileGridYOffset();
    }

// test
    // NOT IN USE
    public void setxx(ColorModel cm) {
        ParameterBlock pb = null;
        //RenderedOp dst = null;

        // affineTransform
        pb = new ParameterBlock();
        pb.addSource(orgImage);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);

        // test
        /*
        BufferedImage bufImage = op.getAsBufferedImage();
        WritableRaster raster = bufImage.getRaster();
        Raster raster2 = bufImage.getData();
        BufferedImage finalBufImage = new BufferedImage(cm, bufImage.getRaster(), false, null);
         */

        // WIndow/Level
        ImageLayout il = new ImageLayout(op.createInstance());
        ColorModel cm2 = il.getColorModel(null);
        SampleModel sm = il.getSampleModel(null);
        boolean val = cm2.isCompatibleSampleModel(sm);
        il.setColorModel(cm);
        ColorModel cm4 = il.getColorModel(null);
        SampleModel sm4 = il.getSampleModel(null);

        il.setTileWidth(orgImage.getWidth());
        il.setTileHeight(orgImage.getHeight());
        HashMap map = new HashMap();
        map.put(JAI.KEY_IMAGE_LAYOUT, il);
        //map.put(JAI.KEY_COLOR_MODEL_FACTORY, il);
        //map.put(JAI.KEY_INTERPOLATION, Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
        RenderingHints hints = new RenderingHints(map);
        pb = new ParameterBlock();
        pb.addSource(op.createInstance());
        pb.add(op.getSampleModel().getDataType());
        RenderedOp op2 = JAI.create("format", pb, hints);

        // info
        ImageLayout il3 = new ImageLayout(op2.createInstance());
        ColorModel cm3 = il3.getColorModel(null);
        SampleModel sm3 = il3.getSampleModel(null);

        // The interpolation works only if creating a new ImageLayout.
        // Untile this rotate node so that when compute the next node,
        // no extra memory and time are used in PlanarImage.getExtendedData().
        /*ImageLayout il2 = new ImageLayout();
        il2.setTileWidth(orgImage.getWidth());
        il2.setTileHeight(orgImage.getHeight());
        //il2.setTileGridXOffset(orgImage.getMinX());
        //il2.setTileGridYOffset(orgImage.getMinY());
        RenderingHints hints2 = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il2);
        pb = new ParameterBlock();
        pb.addSource(op2);
         */
        //RenderedOp op3 = JAI.create("format", pb, hints2);

        //displayImage = bufImage;
        //displayImage = op.createInstance();
        createTiledImage(op2.createInstance());
        //repaint();
    }

// test
    // NOT IN USE
    public void set12(RenderedImage img) {
        // affineTransform
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpMode));
        RenderedOp op = JAI.create("affine", pb);

        //RenderedImage renderedImage = zoomOperator(img, 0.1d);
        //RenderedOp op2 = zoomOperator(img, 2.0d);

        // test
        SampleModel sampleModel = op.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();
        ImageLayout il = new ImageLayout(op.createInstance());
        ColorModel cm2 = il.getColorModel(null);
        SampleModel sm = il.getSampleModel(null);
        boolean val = cm2.isCompatibleSampleModel(sm);

        //displayImage = op;
        repaint();
    }

    /** Create the zoom result image. */
    private RenderedOp zoomOperator(RenderedImage source,
            double zoomFactor) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add((float) zoomFactor);
        pb.add((float) zoomFactor);
        pb.add(0.0f);
        pb.add(0.0f);
        pb.add(new InterpolationBilinear());

        // Tiling on this scale node: (1) Reduces the tiling memory
        // overhead; (2) Pull only the displayed tiles when the zoom
        // factor is large.
        ImageLayout il = new ImageLayout();
        il.setTileWidth(tileWidth);
        il.setTileHeight(tileHeight);
        RenderingHints hints =
                new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);

        RenderedOp img = JAI.create("scale", pb, hints);

        return img;
    }

// NOT IN USE
    public void createTiledImage2(PlanarImage img) {
        //displayImage =  makeTiledImage(img);
        //sampleModel = displayImage.getSampleModel();
        //colorModel = displayImage.getColorModel();
        //getTileInfo(displayImage);
        //fireTilePropertyChange();
        imageDrawn = false;
        repaint();
    }

// NOT IN USE
    protected RenderedOp makeTiledImage2(PlanarImage img) {
        ImageLayout tileLayout = new ImageLayout(img);
        tileLayout.setTileWidth(tileWidth);
        tileLayout.setTileHeight(tileHeight);
        RenderingHints tileHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, tileLayout);

        //format
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        RenderedOp op = JAI.create("format", pb, tileHints);

        // info
        ImageLayout il3 = new ImageLayout(op.createInstance());
        ColorModel cm3 = il3.getColorModel(null);
        SampleModel sm3 = il3.getSampleModel(null);

        return op;
    }

    /**
     * NOT IN USE
     * Computes tile information of the specified image.
     * @param img the planar image.
     */
    protected void getTileInfo2(PlanarImage img) {
        imageWidth = img.getWidth();
        imageHeight = img.getHeight();
        tileWidth = img.getTileWidth();
        tileHeight = img.getTileHeight();
        maxTileIndexX = img.getMinTileX() + img.getNumXTiles() - 1;
        maxTileIndexY = img.getMinTileY() + img.getNumYTiles() - 1;
        maxTileCordX = img.getMaxX();
        maxTileCordY = img.getMaxY();
        minTileIndexX = img.getMinTileX();
        minTileIndexY = img.getMinTileY();
        minTileCordX = img.getMinX();
        minTileCordY = img.getMinY();
        tileGridXOffset = img.getTileGridXOffset();
        tileGridYOffset = img.getTileGridYOffset();
    }

    /** Sets the tile width of the formatted image.
     * @param int the tile width.
     */
    public void setTileWidth(int tw) {
        tileWidth = tw;
        //setImage(displayImage);
    }

    /** Sets the tile height of the formatted image.
     * @param int the tile height.
     */
    public void setTileHeight(int th) {
        tileHeight = th;
        //setImage(displayImage);
    }

    public void setPanOffset(Point panOffset) {
        //firePropertyChange("PanOffset",this.panOffset,panOffset);
        this.panOffset = panOffset;
        panX = panOffset.x;
        panY = panOffset.y;
    }

    public Point getPanOffset() {
        return panOffset;
    }

    public boolean isImageDrawn() {
        return imageDrawn;
    }

    /** Returns the tile width of the formatted image.
     * @return the tile width of the formatted image.
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /** Returns the tile height of the formatted image.
     * @return the tile height of the formatted image.
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /** Returns the maxTileIndexX property of the current image.
     * @return the maxTileIndexX property of the current image.
     */
    public int getMaxTileIndexX() {
        return maxTileIndexX;
    }

    /** Returns the maxTileIndexY property of the current image.
     * @return the maxTileIndexY property of the current image.
     */
    public int getMaxTileIndexY() {
        return maxTileIndexY;
    }

    /** Returns the width of the current image.
     * @return the width of the current image.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /** Returns the height of the current image.
     * @return the height of the current image.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Set setWindowCenterOffsetStatus(
     */
    public void setWindowCenterOffsetStatus(Boolean sta) {
        windowCenterOffsetStatus = sta;
    }
    
    /**
     * setPhotometricInterpretation
     * @param str 
     */
    public void setPhotometricInterpretation(String str){
        photometricInterpretation = str;    
    }

    /*****************************************************************
     * 
     *  Overlays
     * 
     ****************************************************************/
    /**
     * Define the font and color used when displaying text
     * on the canvas.
     */
    public void setCanvasOverlayFontAndColor(Font font, Color color) {
        overlayTextFont = font;
        overlayTextColor = color;

        String pixVal = "Value: " + 12345;
        String pX = "X: " + 1234;
        String pY = "Y: " + 1234;

        Graphics g = this.getGraphics();
        g.setFont(overlayTextFont);
        g.setColor(overlayTextColor);
        FontMetrics fm = g.getFontMetrics();

        // Calculate pixelValue
        Rectangle2D area = fm.getStringBounds(pixVal, g);
        mousePositionValueWidth = (int) area.getWidth();
        mousePositionValueHeight = (int) area.getHeight();
        //System.out.println("ImageCanvas.setCanvasOverlayFontAndColor: Value= " +
        //      pixVal + " w= " + mousePositionValueWidth + " h= " + mousePositionValueHeight);

        // Calculate pixel x position
        Rectangle2D pXArea = fm.getStringBounds(pX, g);
        posMousePositionXWidth = (int) pXArea.getWidth();
        posMousePositionXHeight = (int) pXArea.getHeight();
        //System.out.println("ImageCanvas.setCanvasOverlayFontAndColor: Value= " +
        //      pixVal + " w= " + posMousePositionXWidth  + " h= " + posMousePositionXHeight);

        // Calculate pixel y position
        Rectangle2D pYArea = fm.getStringBounds(pY, g);
        posMousePositionYWidth = (int) pYArea.getWidth();
        posMousePositionYHeight = (int) pYArea.getHeight();
        //System.out.println("ImageCanvas.setCanvasOverlayFontAndColor: Value= " +
        //      pixVal + " w= " + posMousePositionYWidth + " h= " + posMousePositionYHeight);
    }

    /******************************************************************
     * StackNo
     ******************************************************************/
    public void setCanvasOverlayStackNoStatus(boolean status) {
        canvasOverlayStackNoStatus = status;
    }

    public void setCanvasOverlayStackNoPos(int x, int y) {
        xPosStackNo = x;
        yPosStackNo = y;
    }

    public void setCanvasOverlayStackNoValue(String str) {
        canvasOverlayStackNoValue = str;
    }

    /******************************************************************
     * MarkNo
     ******************************************************************/
    public void setCanvasOverlayMarkNoStatus(boolean status) {
        canvasOverlayMarkNoStatus = status;
    }

    public void setCanvasOverlayMarkNoPos(int x, int y) {
        xPosMarkNo = x;
        yPosMarkNo = y;
    }

    public void setCanvasOverlayMarkNoValue(String str) {
        canvasOverlayMarkNoValue = str;
    }

    /******************************************************************
     * PatientId
     ******************************************************************/
    public void setCanvasOverlayPatientIdStatus(boolean status) {
        canvasOverlayPatientIdStatus = status;
    }

    public void setCanvasOverlayPatientIdPos(int x, int y) {
        xPosPatientId = x;
        yPosPatientId = y;
    }

    public void setCanvasOverlayPatientIdValue(String str) {
        canvasOverlayPatientIdValue = str;
    }

    /******************************************************************
     * WindowLevel
     ******************************************************************/
    // WindowLevel
    public void setCanvasOverlayWindowLevelStatus(boolean status) {
        canvasOverlayWindowLevelStatus = status;
    }

    public void setCanvasOverlayWindowWidthPos(int x, int y) {
        xPosWindowWidth = x;
        yPosWindowWidth = y;
    }

    public void setCanvasOverlayWindowCenterPos(int x, int y) {
        xPosWindowCenter = x;
        yPosWindowCenter = y;
    }

    /**
     * Set the adjusted WindowCenter/WindowWidth values.
     */
    public void setWindowLevelValues(int width, int center) {
        selWindowWidth = width;
        selWindowCenter = center;

        if (canvasOverlayWindowingFixedMinimumStatus) {
            selWindowCenter = windowingFixedMinimumValue + selWindowWidth / 2;
        }
    }

    /**
     * Get the window width corrected value.
     *@return the window width corrected value
     */
    public int getWindowWidthAdjusted() {
        return selWindowWidth;
    }

    /**
     * get the window center correted value.
     *@ return the window center corrected value.
     */
    public int getWindowCenterAdjusted() {
        int wc = 0;
        if (windowCenterOffsetStatus) {
            wc = (int) (selWindowCenter + imgStat[0][7]);
        }
        return wc;
    }

    /****************************************************************
     * WindowingMode, NM & MR "whitepoint" values
     ***************************************************************/
    public void setWindowingMode(int wm) {
        windowingMode = wm;
    }

    public void setWindowingFixedMinimumValue(int val) {
        windowingFixedMinimumValue = val;
    }

    public void setWindowingFixedMinimumPercentValue() {
        long wp2 = 0;
        if (imgStat != null) {
            if (imgStat[0][6] != 0) {
                double wp = (double) windowingFixedMinimumValue / imgStat[0][6];
                wp2 = Math.round(wp * 100);
            }
        }
        windowingFixedMinimumValuePercent = wp2;
    }

    public void setWindowingFixedMinOverlayStatus(boolean sta) {
        canvasOverlayWindowingFixedMinimumStatus = sta;
    }

    // Not in use
    public void setCanvasOverlayWindowingFixedMinStatus(boolean status) {
        canvasOverlayWindowingFixedMinimumStatus = status;
    }

    public void setCanvasOverlayFixedMinimumPos(int x, int y) {
        xPosFixedMinimum = x;
        yPosFixedMinimum = y;
    }

    public int getCanvasWindowingFixedMinimumValue() {
        return windowingFixedMinimumValue;

    }

    /****************************************************************
     * Localization
     ***************************************************************/
    @Override
    public void setCanvasOverlayLocalizationList(ArrayList<StudyDbLocalizationM> list) {
        lList = list;
    }

    @Override
    public void setCanvasOverlayLocalizationSymbolProperties(Double x1, Double x2,
            Double x3, Double x4, BasicStroke x5, Color lineColor, Color textColor) {
        localizationActiveSymbolLineSize = x1;
        localizationSetSymbolElipseSize = x2;
        localizationSelectSymbolLineXSize = x3;
        localizationSelectSymbolLineYSize = x4;
        localizationSymbolStroke = x5;
        localizationLineColor = lineColor;
        localizationPositionTextColor = textColor;
    }

    @Override
    public void setCanvasOverlayLocalizationRenderStatus(boolean sta) {
        canvasOverlayLocalizationRenderStatus = sta;
    }

    @Override
    public void setCanvasOverlayLocalizationRenderPositionStatus(boolean sta) {
        canvasOverlayLocalizationPositionStatus = sta;
    }

    /*
     * OLD remove
     */
    public void setCanvasOverlayLocalizationStatus(boolean status) {
        canvasOverlayLocalizationPositionStatus = status;
    }

    public void setCanvasOverlayLocalizationPosX(int x, int y) {
        xPosLocalizationX = x;
        yPosLocalizationX = y;
    }

    public void setCanvasOverlayPosLocalizationY(int x, int y) {
        xPosLocalizationY = x;
        yPosLocalizationY = y;
    }

    public void setCanvasOverlayLocalizationPosZ(int x, int y) {
        xPosLocalizationZ = x;
        yPosLocalizationZ = y;
    }

    public void setCanvasOverlayLocalizationPositionValue(int x, int y, int z) {
        xPosLocalizationMark = x;
        yPosLocalizationMark = y;
        zPosLocalizationMark = z;
    }

    /******************************************************************
     * MousePosition
     ******************************************************************/
    public void setCanvasOverlayMousePositionStatus(boolean status) {
        canvasOverlayMousePositionStatus = status;
    }

    public void setCanvasOverlayMousPositionPosX(int x, int y) {
        xPosMousePositionX = x;
        yPosMousePositionX = y;
    }

    public void setCanvasOverlayMousPositionPosY(int x, int y) {
        xPosMousePositionY = x;
        yPosMousePositionY = y;
    }

    /******************************************************************
     * MousePositionPixelValue
     ******************************************************************/
    public void setCanvasOverlayMousePositionPixelValueStatus(boolean status) {
        canvasOverlayMousePositionPixelValueStatus = status;
    }

    public void setCanvasOverlayMousePositionPixelValuePos(int x, int y) {
        xPosMousePositionValue = x;
        yPosMousePositionValue = y;
    }

    /**
     * Set the mouse x,y position (used for overlay info).
     * Set the mouse x,y value (used for overlay info).
     */
    @Override
    //20160627
    public void setCanvasOverlayMousePositionPixelValue(int x, int y, boolean renderStatus) {
        mousePosX = x;
        mousePosY = y;
        SampleModel sm = null;
        Raster raster = null;
        DataBuffer db = null;
        Rectangle rec = null;

        //System.out.println("ImageCanvas.setCanvasOverlayMousePositionPixelValue: " + pixelValueCnt++);

        if (canvasOverlayMousePositionPixelValueStatus && orgImage != null) {
            sm = orgImage.getSampleModel();
            raster = orgImage.getData();
            db = raster.getDataBuffer();
            rec = raster.getBounds();
        }

        //20160627
        if ((canvasOverlayMousePositionStatus
                || canvasOverlayMousePositionPixelValueStatus) && orgImage != null) {

            int w = orgImage.getWidth();
            int h = orgImage.getHeight();

            try {
                p1 = atx.inverseTransform((Point2D) (new Point(x, y)), p1);
                mousePosX = (int) p1.getX();
                mousePosY = (int) p1.getY();
            } catch (Exception exp) {
                System.out.println(exp);
            }
            if (mousePosX > w) {
                mousePosX = 0;
                mousePosY = 0;
            }
            if (mousePosY > h) {
                mousePosY = 0;
                mousePosX = 0;
            }
            if (mousePosX < 0) {
                mousePosX = 0;
                mousePosY = 0;
            }
            if (mousePosY < 0) {
                mousePosY = 0;
                mousePosX = 0;
            }
        }

        // find the pixelvalue
        if (canvasOverlayMousePositionPixelValueStatus) {
            //System.out.println("mousePosX= " + mousePosX + " mousePosY= " + mousePosY);
            pixelValue[0] = 0; pixelValue[1] = 0; pixelValue[2] = 0;
            pixelValue2[0] = 0;
            if (orgImage != null && rec != null
                    && sm != null && db != null) {
                if (rec.contains(mousePosX, mousePosY)) {
                    pixelValue = sm.getPixel(
                            mousePosX, mousePosY, pixelValue, db);
                    //pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7]) * rescaleSlope) + rescaleIntercept);
                    pixelValue[0] = (int) (((pixelValue[0] - imgStat[0][7])) + rescaleIntercept - rescaleIntercept);
                    if (pixelValueSigned) {
                        pixelValue2[0] = (int) (pixelValue[0] - imgStat[0][7]);
                    }
                }
            }
        }

        // NOT IN USE
        if (renderStatus && canvasOverlayMousePositionPixelValueStatus && false) {
            //System.out.println("renderStatus == true");
            //System.out.println("canvasOverlayMousePositionPixelValueStatus == true");
            //System.out.println("ImageCanvas.setCanvasOverlayMousePositionPixelValue: 2");

            repaint(xPosMousePositionValue, yPosMousePositionValue - mousePositionValueHeight,
                    mousePositionValueWidth, mousePositionValueHeight);
            repaint(xPosMousePositionX, yPosMousePositionX - posMousePositionXHeight,
                    posMousePositionXWidth, posMousePositionXHeight);
            repaint(xPosMousePositionY, yPosMousePositionY - posMousePositionYHeight,
                    posMousePositionYWidth, posMousePositionYHeight);
        }

        // fix
        if (renderStatus && (canvasOverlayMousePositionStatus
                || canvasOverlayMousePositionPixelValueStatus)) {
            repaint();
        }
    }

    /**********************************************************
     * distance measurements
     *********************************************************/
    //NOT IN USE
    @Override
    public void setCanvasROIDistanceDrawingStatus(boolean status) {
        roiDistanceDrawingStatus = status;
    }

    /**
     *
     * @param status
     */
    @Override
    public void setCanvasROIDistanceUpdateStatus(boolean status) {
        roiDistanceUpdateStatus = status;
    }

    /**
     *
     * @param status
     */
    @Override
    public void setCanvasROIDistanceGrabSymbols(boolean status) {
        roiDistanceGrabStatus = status;
    }

    /**
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    @Override
    public void setCanvasROIDistanceDrawingValue(int x1, int y1, int x2, int y2) {
        startPointXInt = x1;
        startPointYInt = y1;
        curPointXInt = x2;
        curPointYInt = y2;
    }

    /**
     *
     * @param l
     */
    @Override
    public void setCanvasROIDistanceDrawingValue(Line2D l) {
        dLine = l;
    }

    /**
     *
     * @param list
     */
    @Override
    public void setCanvasROIDistanceUpdateValue(ArrayList<StudyDbROID> list) {
        dList = list;
    }

    /**
     *
     * @param str
     */
    public void setCanvasROIDistanceValue(String str) {
        roiDistanceText = str;
    }

    /**
     *
     * @param font
     * @param color
     */
    public void setCanvasROIDistanceFontValue(Font font, Color color) {
        roiDistanceFont = font;
        roiDistanceFontColor = color;
    }

    /**
     *
     * @param bs
     * @param color
     */
    public void setCanvasROIDistanceDrawingLineValue(BasicStroke bs, Color color) {
        roiDistanceLineStroke = bs;
        roiDistanceLineColor = color;
    }
    // end canvas overlay

    /********************************************************
     * Area measurements
     *******************************************************/
    /**
     * 
     * @param status
     */
    @Override
    public void setCanvasROIAreaGrabSymbols(boolean status) {
        roiVolumeGrabStatus = status;
    }

    /**
     *
     * @param status
     */
    @Override
    public void setCanvasROIAreaUpdateStatus(boolean status) {
        roiVolumeUpdateStatus = status;
    }

    /**
     * 
     * @param status
     */
    @Override
    public void setCanvasROIAreaUpdateTextStatus(boolean status) {
        roiVolumeUpdateTextStatus = status;
    }

    /**
     *
     * @param l
     */
    @Override
    public void setCanvasROIAreaDrawingValue(Line2D l) {
        vLine = l;
    }

    /**
     *
     * @param list
     */
    @Override
    public void setCanvasROIAreaUpdateValue(ArrayList<StudyDbROIV> list) {
        vList = list;
    }

    public void setCanvasROIAreaValue(String str) {
        roiVolumeText = str;
    }

    public void setCanvasROIAreaFontValue(Font font, Color color) {
        roiVolumeFont = font;
        roiVolumeFontColor = color;
    }

    public void setCanvasROIAreaDrawingLineValue(BasicStroke bs, Color color) {
        roiVolumeLineStroke = bs;
        roiVolumeLineColor = color;
    }
    // end canvas overlay

    /******************************************************************
     * pixelvalue mean measurements
     *****************************************************************/
    // NOT IN USE
    @Override
    public void setCanvasROIPixelValueMeanDrawingStatus(boolean status) {
        roiPixelValueMeanDrawingStatus = status;
    }

    @Override
    public void setCanvasROIPixelValueMeanUpdateStatus(boolean status) {
        roiPixelValueMeanUpdateStatus = status;
    }

    // NOT IN USE
    public void setCanvasROIPixelValueMeanGrabSymbols(boolean status) {
        roiPixelValueMeanGrabStatus = status;
    }

    public void setCanvasROIPixelValueMeanDrawingValue(int x1, int y1, int x2, int y2) {
        pixelValueMeanStartPointXInt = x1;
        pixelValueMeanStartPointYInt = y1;
        pixelValueMeanCurPointXInt = x2;
        pixelValueMeanCurPointYInt = y2;
    }

    public void setCanvasROIPixelValueMeanDrawingValue(Line2D l) {
        pixelValueDLine = l;
    }

    @Override
    public void setCanvasROIPixelValueMeanUpdateValue(ArrayList<StudyDbROIPixelValueD> list) {
        pvList = list;
    }

    public void setCanvasROIPixelValueMeanValue(String str) {
        pixelValueText = str;
    }

    public void setCanvasROIPixelValueMeanTextValue(Font font, Color color) {
        pixelValueMeanFont = font;
        pixelValueMeanFontColor = color;
    }

    public void setCanvasROIPixelValueMeanLineValue(BasicStroke bs, Color color) {
        pixelValueMeanDrawingLineStroke = bs;
        pixelValueMeanDrawingLineColor = color;
    }
    // end canvas overlay

    /******************************************************************
     * canvas color
     *****************************************************************/
    public void initCanvasDefaultColor(Color color) {
        canvasDefaultColor = color;
    }

    public void setCanvasDefaultColor() {
        canvasColor = canvasDefaultColor;
    }

    public void initCanvasETColor(Color color) {
        canvasETColor = color;
    }

    @Override
    public void setCanvasETColor() {
        canvasColor = canvasETColor;
    }

    /************************************************
     * Mouse Listener section
     ***********************************************/
    public void mouseReleased(MouseEvent e) {
        //System.out.println("mouseReleased CatImageCanvas");
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * MouseMotionListener interface
     */
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved_NOT(MouseEvent e) {
    }

    // This one is to heavy to use on slow machines.
    // NOT IN USE
    public void mouseMoved_NOT2(MouseEvent e) {
        Point2D p1 = null;
        Point pos = e.getPoint();
        int x = (int) pos.getX();
        int y = (int) pos.getY();
        //System.out.println("CatImageCanvas:mouseMoved" + "x: " + x);
        //System.out.println("CatImageCanvas:mouseMoved" + "y: " + y);

        // convert the cordinats from userspace to imagespace
        try {
            p1 = atx.inverseTransform((Point2D) (new Point(x, y)), p1);
            mousePosX = (int) p1.getX();
            mousePosY = (int) p1.getY();
        } catch (Exception exp) {
            System.out.println(exp);
        }

        //pixelValue = getPixelValue();
        //pixelValue2 = getPixelValue2();

        //repaint();
    }

    /** Wraps the provided image into a NullOpImage with a tile cache
     * (the default tile cache of JAI) as a RenderingHints.  So that
     * the computed tiles of the provided image can be cached.  This
     * method is designed because of DICOMImages loaded from a
     * customized image reader.
     */
    private RenderedImage cacheIt(RenderedImage src) {
        RenderingHints hints = new RenderingHints(JAI.KEY_TILE_CACHE, JAI.getDefaultInstance().getTileCache());
        return new NullOpImage(src, null, hints, OpImage.OP_IO_BOUND);
    }

    /**
     * Get the pixel value from the transformed image i.e after
     * window/level adjustment.
     * This method is NOT WORKING.
     * NOT IN USE
     */
    private int[] getPixelValue2() {
        int[] pixel = new int[1];

        if (displayImage != null) {
            SampleModel sm = displayImage.getSampleModel();
            Raster raster = displayImage.getData();
            DataBuffer db = raster.getDataBuffer();
            Rectangle rec = raster.getBounds();

            if (rec.contains(mousePosX, mousePosY)) {
                System.out.println("inside");
                pixel = sm.getPixel(mousePosX, mousePosY, pixel, db);
            }
            //Object ob = sm.getDataElements(mousePosX, mousePosY, null, db);
        }
        return pixel;
    }

    /**
     * Set RescaleIntercept.
     */
    public void setRescaleIntercept(double val) {
        rescaleIntercept = val;
    }

    /**
     * Set RescaleSlope.
     */
    public void setRescaleSlope(double val) {
        rescaleSlope = val;
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
     * Set modalityLUTSequenceStatus
     */
    public void setModalityLUTSequenceStatus(Boolean sta) {
        modalityLUTSequenceStatus = sta;
    }

    /**
     * Set voiLUTSequenceStatus
     */
    public void setVoiLUTSequenceStatus(Boolean sta) {
        voiLUTSequenceStatus = sta;
    }

    /**
     * Set rescaleSlopeInterceptStatus
     */
    public void setRescaleSlopeInterceptStatus(Boolean sta) {
        rescaleSlopeInterceptStatus = sta;
    }

    /**
     * Set centerWidthStatus
     */
    public void setCenterWidthStatus(Boolean sta) {
        centerWidthStatus = sta;
    }

    /**
     * Set identityStatus
     */
    public void setIdentityStatus(Boolean sta) {
        identityStatus = sta;
    }

    /**
     * Set the <code>Stroke<code/>.
     * NOT IN USE
     */
    public void setStroke(Stroke stroke) {
        Graphics2D g = (Graphics2D) displayImage.getAsBufferedImage().getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setStroke(stroke);
    }

    /**
     * Set the <code>Color<code/>.
     * NOT IN USE
     */
    public void setColor(Color color) {
        Graphics2D g = (Graphics2D) displayImage.getAsBufferedImage().getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setColor(color);
    }

    /********************************************************
     * Localization
     *******************************************************/
    public void setCanvasOverlaySelectLineUpdateStatus(boolean status) {
        roiVolumeUpdateStatus = status;
    }

    /**
     * Use this method for setting the localization symbol.
     */
    public void draw_old(Line2D.Double line1, Line2D.Double line2, BasicStroke bs) {
        //System.out.println("ImageManipulatorCanvas:draw");

        TiledImage ti = new TiledImage(orgImage, true);
        Graphics2D g = ti.createGraphics();
        //g.setColor(Color.RED);
        g.setStroke(bs);

        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.draw(line1);
        g.draw(line2);
        orgImage = ti;

        //test
        //markLine1 = line1;
        //markLine2 = line2;


        //Graphics2D g = (Graphics2D) displayImage.getAsBufferedImage().getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.draw(line);
        //repaint();
    }

    /**
     * Use this method for setting the localization symbol.
     * NEW
     */
    @Override
    public void drawTest(Line2D.Double line1, Line2D.Double line2, BasicStroke bs) {
        //System.out.println("ImageManipulatorCanvas:draw");
        //canvasOverlaySelectShape = s;
        canvasOverlaySelectLine1 = line1;
        canvasOverlaySelectLine2 = line2;
        canvasOverlayBasicStroke = bs;
    }

    /**
     * Use this method for setting the localization symbol.
     * NOT IN USE
     */
    @Override
    public void draw(Line2D.Double line1, Line2D.Double line2, BasicStroke bs) {
        System.out.println("ImageManipulatorCanvas:draw");
        testLine1 = line1;
        testLine2 = line2;

        Graphics g = displayImage.getGraphics();
        g.drawLine(300, 300, 400, 400);

        //TiledImage ti = new TiledImage(displayImage, true);
        //Graphics2D g = ti.createGraphics();
        //g.setColor(Color.BLACK);
        //g.setStroke(bs);

        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.draw(line1);
        //g.draw(line2);
        //orgImage = ti;

        //test
        //displayImage = ti;
        //Graphics2D g2 = (Graphics2D) displayImage.getGraphics();
        //g2.draw(line1);
        //g2.draw(line2);
        repaint();
        //repaint((int)line1.x1, (int)line2.y1, (int)(line1.x2 - line1.x1), (int)(line2.y2 - line2.y1));

        //test
        //markLine1 = line1;
        //markLine2 = line2;


        //Graphics2D g = (Graphics2D) displayImage.getAsBufferedImage().getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.draw(line);
        //repaint();
    }

    // NOT IN USE
    // NOT WORKING
    public void draw77(Line2D.Double line1, Line2D.Double line2) {
        //System.out.println("ImageManipulatorCanvas:draw");
        Graphics2D g = (Graphics2D) orgImage.getGraphics();
        //TiledImage ti = (TiledImage) orgImage;  // nw
        //Graphics2D g = ti.createGraphics();

        g.setStroke(new BasicStroke(5));
        g.draw(line1);
        g.draw(line2);
    }

    /**
     * Use this method for setting the localization symbol.
     */
    @Override
    public void draw(Line2D.Double line1, Line2D.Double line2, Line2D.Double line3,
            Line2D.Double line4, BasicStroke bs) {
        //System.out.println("ImageManipulatorCanvas:draw");

        TiledImage ti = new TiledImage(orgImage, true);
        Graphics2D g = ti.createGraphics();
        //g.setColor(Color.RED);
        g.setStroke(bs);
        g.draw(line1);
        g.draw(line2);
        g.draw(line3);
        g.draw(line4);
        orgImage = ti;
    }

    /**
     * Use this method for setting the localization and select symbol.
     */
    @Override
    public void draw(Shape s, Line2D.Double line1, Line2D.Double line2, BasicStroke bs) {
        //System.out.println("ImageManipulatorCanvas:draw");
        if (orgImage != null) {
            TiledImage ti = new TiledImage(orgImage, true);
            Graphics2D g = ti.createGraphics();
            //g.setColor(Color.RED);  // No effect...
            g.setStroke(bs);
            g.draw(line1);
            g.draw(line2);
            g.draw(s);
        }
        //line1 = line1;
        //line2 = line2;
        //shape = s;
        //Graphics2D g = (Graphics2D) displayImage.getAsBufferedImage().getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.draw(line);
        //repaint();
    }

    /**
     * NOT IN USE
     */
    @Override
    public void draw(Shape s) {
        TiledImage ti = new TiledImage(orgImage, true);
        Graphics2D g = ti.createGraphics();
        g.setStroke(new BasicStroke(5));
        g.draw(s);
    }

    /**
     * Draw a Shape object on the canvas.
     * NOT IN USE
     */
    @Override
    public void draw(Shape s, BasicStroke bs) {
        if (orgImage == null) {
            return;
        }

        TiledImage ti = new TiledImage(orgImage, true);
        Graphics2D g = ti.createGraphics();
        g.setStroke(bs);
        g.draw(s);
        orgImage = ti;
    }

    /**
     *  Draw a line, used by <code>ShapeMaker</code> class.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * NOT IN USE
     */
    @Override
    public void draw(int x1, int y1, int x2, int y2, BasicStroke bs) {
        //System.out.println("ImageManipulatorCanvas:draw");
        if (orgImage == null) {
            return;
        }

        TiledImage ti = new TiledImage(orgImage, true);
        Graphics2D g = ti.createGraphics();
        g.setStroke(bs);

        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        //      RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setColor(new Color(100,100,100));
        //g.setXORMode(Color.BLACK);
        g.drawLine(x1, y1, x2, y2);
        orgImage = ti;
    }

    /**
     * setFocus
     * NOT IN USE
     */
    @Override
    public void setFocus() {
        this.requestFocusInWindow();
    }

    /***********************************************************
     * FocusListener interface
     **********************************************************/
    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("ImageCanvas.focusGained");
        //viewdex.requestFocusInWindow();
    }

    @Override
    public void focusLost(FocusEvent e) {
        //System.out.println("ImageCanvas.focusLost");
    }

    public void setCanvasOverlayDistanceMeasurementValue(int x1, int y1, int x2, int y2, BasicStroke bs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // NOT IN USE
    @Override
    public void draw2(int x, int y) {
    }
}
