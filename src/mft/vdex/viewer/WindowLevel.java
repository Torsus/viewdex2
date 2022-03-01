/* @(#) WindowLevel.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.viewer;

//import creatergbimagelookuptestmag.LUTFunctions;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import javax.media.jai.LookupTableJAI;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ImageLayout;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import mft.vdex.app.ViewDex;
import mft.vdex.util.LUTUtil;
//import org.dcm4che.image.ColorModelParam;

public class WindowLevel {

    public ViewDex viewDex;
    protected ImageCanvas canvas;
    protected int minValue = 0;
    protected int maxValue = 255;
    protected int MIN_SCREEN_VALUE = 0;
    protected int MAX_SCREEN_VALUE = 255;
    //protected int LUT_SIZE = 256;
    //protected int lutSize = LUT_SIZE;
    protected BufferedImage wlImage, img;
    protected int window, level;
    protected int anchorX = 0, anchorY = 0;
    protected int width_old = 0, winNew = 0;
    protected int center_old = 0, levNew = 0;
    protected boolean debug = false;
    protected boolean firstTime = true;
    private int canvasControlMode;
    
    // test
    RenderedImage windowLevelResult;
    
    // dicom tags
    private int pixelRepresentation;
    private int pixelPaddingValue;
    private int[] windowWidth;
    private int[] windowCenter;
    private int bitsStored;
    private int bitsAllocated;
    private double rescaleIntercept;
    private double rescaleSlope;
    private String photometricInterpretation;
    private String modality;
    private boolean lutDefined = false;
    private String lutDefinedStr = "";
    
    private double mapConst, mapConstVal;
    private boolean mapConstStatus = false;
    private int cnt = 0;
    
    private int[][] imageStats;
    //private int min, max, width, range_neg, range_pos;
    private int pos_min, pos_max, neg_min, neg_max;
    private int range_neg, range_pos, range_tot, mask1, min, max, mask_status;
    
    // transformation status
    private boolean modalityLUTSequenceStatus;
    private boolean voiLUTSequenceStatus;
    private boolean rescaleSlopeInterceptStatus;
    private boolean centerWidthStatus;
    private boolean identityStatus;
    private boolean windowCenterOffsetStatus;
    
    // static final ColorModelFactory cmFactory = ColorModelFactory.getInstance();
    static final Color myGray = new Color(204, 204, 204);
    //private ColorModelParam cmParam = null;
    
    // 256,1024,4096,16384,32768,65536
    //int lutArraySize = 65536;
    //byte lutR[] = new byte[lutArraySize];
    //byte lutG[] = new byte[lutArraySize];
    //byte lutB[] = new byte[lutArraySize];
    int lutSize = 65536;
    byte[] lutR, lutG, lutB;
    byte[] lutR8, lutG8, lutB8;
    byte[] lutR10, lutG10, lutB10;
    byte[] lutR12, lutG12, lutB12;
    byte[] lutR14, lutG14, lutB14;
    byte[] lutR15, lutG15, lutB15;
    byte[] lutR16, lutG16, lutB16;
    
    byte[][] lutRGB8;
    byte[][] lutRGB8Bit;
    byte[][] lutRGB8HotIron;
    byte[][] lutRGB8GeCol;
    byte[][] lutRGB8Tmp;
    byte[][] lutRed;
    byte[][] lutRGB16;
    
    // For the createByteLookupTable functions
    int ymin = 0;
    int ymax = 255;
    byte bymin = (byte) ymin;
    byte bymax = (byte) ymax;
    double yrange = ymax - ymin;
    
    // Used by setWindowLevel method.
    double[] val = new double[2];
    LookupTableJAI lookup = null;
    int width_d, center_d;
    
    int windowLevelcnt = 0;
    
    // WindowingMode
    private int windowingMode = WindowingMode.NONE;
    private int windowingFixedMinimumValue;
    
    private boolean canvasOverlayWindowingFixedMinimumStatus = false;
    
    /** The WindowLevel constructor.
     * @param imageCanvas the canvas in which the image is drawn.
     */
    public WindowLevel(ViewDex viewdex, ImageCanvas imageCanvas){
        this.viewDex = viewdex;
        this.canvas = imageCanvas;
        init();
        //initColorLUT();
    }
    
    // -------------------------------------------------------------------
    // init
    // -------------------------------------------------------------------
    protected void init(){
        lutR8 = new byte[256];
        lutG8 = new byte[256];
        lutB8 = new byte[256];
        lutRGB8 = new byte[3][256];
        lutRGB8HotIron = new byte[3][256];
        lutRGB8Bit = new byte[3][256];
        lutRGB8Tmp = new byte[3][256];
        lutRed = new byte[3][256];
        
        lutR10 = new byte[1024];
        lutG10 = new byte[1024];
        lutB10 = new byte[1024];
        
        lutR12 = new byte[4096];
        lutG12 = new byte[4096];
        lutB12 = new byte[4096];
        
        lutR14 = new byte[16384];
        lutG14 = new byte[16384];
        lutB14 = new byte[16384];
        
        lutR15 = new byte[32768];
        lutG15 = new byte[32768];
        lutB15 = new byte[32768];
        
        lutR16 = new byte[65536];
        lutG16 = new byte[65536];
        lutB16 = new byte[65536];
        lutRGB16 = new byte[3][65536];
    }
    
    /**
     * 
     */
    /*
    public void initColorLUT() {
        //lutRGB8HotIron = LUTFunctions.hotIron8();
        //lutRed = LUTFunctions.red();
        if(lutDefinedStr.equalsIgnoreCase("Hotiron"))
            lutRGB8 = LUTFunctions.hotIron8();
        else
            if(lutDefinedStr.equalsIgnoreCase("GECol"))
                lutRGB8 = LUTGeCol.init();
    }*/
    
    /**
     * 
     * @param str
     */
    public void readColorLUT(String str){
        LUTUtil lutUtil = new LUTUtil();
        lutRGB8 = lutUtil.fileRead(str);
    }
    
    // -------------------------------------------------------------------
    // initLUT  NOT IN USE
    // -------------------------------------------------------------------
    protected void initLUT(int bits){
        if(bits == 16){
            mapConst = 20.0;
            lutSize = 65536;
            lutR = new byte[lutSize];
            lutG = new byte[lutSize];
            lutB = new byte[lutSize];
        } else
            if(bits == 12){
            mapConst = 1.0;
            lutSize = 4096;
            lutR = new byte[lutSize];
            lutG = new byte[lutSize];
            lutB = new byte[lutSize];
            } else
                if(bits == 10){
            mapConst = 1.0;
            lutSize = 4096;
            lutR = new byte[lutSize];
            lutG = new byte[lutSize];
            lutB = new byte[lutSize];
                } else{
            lutSize = 65536;
            lutR = new byte[lutSize];
            lutG = new byte[lutSize];
            lutB = new byte[lutSize];
                }
        bitsStored = bits;
    }
    
    /*******************************************************
     *    IN USE
     ******************************************************/
    
    /**
     * Set Window Width.
     */
    public void setWindowWidth(int[] val){
        windowWidth = val;
    }
    
    /**
     * Set Window Center.
     */
    public void setWindowCenter(int[] val){
        windowCenter = val;
    }
    
    /**
     * Set BitsStored.
     */
    public void setBitsStored(int val){
        bitsStored = val;
    }
    
    /**
     * Set bitsAllocated
     */
    public void setBitsAllocated(int val){
        bitsAllocated = val;
    }
    
    /**
     * Set rescaleIntercept
     */
    public void setRescaleIntercept(double val){
        rescaleIntercept = val;
    }
    
    /**
     * Set rescaleSlope
     */
    public void setRescaleSlope(double val){
        rescaleSlope = val;
    }
    
    /**
     * Set PhotometricInterpretation.
     */
    public void setPhotometricInterpretation(String pmi){
        photometricInterpretation = pmi;
        
    }
    
    /**
     * Set PixelReprensentation.
     */
    public void setPixelRepresentation(int val){
        pixelRepresentation = val;
    }
    
    /*
    public void setColorModelParam(ColorModelParam cmparam){
        cmParam = cmparam;
    }*/
    
    /**
     * Set modalityLUTSequenceStatus
     */
    public void setModalityLUTSequenceStatus(Boolean sta){
        modalityLUTSequenceStatus = sta;
    }
    
    /**
     * Set voiLUTSequenceStatus
     */
    public void setVoiLUTSequenceStatus(Boolean sta){
        voiLUTSequenceStatus = sta;
    }
    
    /**
     * Set rescaleSlopeInterceptStatus
     */
    public void setRescaleSlopeInterceptStatus(Boolean sta){
        rescaleSlopeInterceptStatus = sta;
    }
    
    /**
     * Set centerWidthStatus
     */
    public void setCenterWidthStatus(Boolean sta){
        centerWidthStatus = sta;
    }
    
    /**
     * Set identityStatus
     */
    public void setIdentityStatus(Boolean sta){
        identityStatus = sta;
    }
    
    /**
     * Set windowCenterOffsetStatus(
     */
    public void setWindowCenterOffsetStatus(Boolean sta){
        windowCenterOffsetStatus = sta;
    }
    
    /**
     * Set modality
     */
    public void setModality(String mod){
        modality = mod;
    }
    
    /**
     * Set lookup table defined
     */
    public void setLookupTableDefined(boolean sta){
        lutDefined = sta;
    }
    
    /**
     * Set lookup table defined string
     */
    public void setLookupTableDefinedStr(String str){
        lutDefinedStr = str;
    }
    
    /**
     * Set image statistics.
     */
    public void setImageStat(int[][] sta){
        imageStats = sta;
        
        pos_min = imageStats[0][0];
        pos_max = imageStats[0][1];
        neg_min = imageStats[0][2];
        neg_max = imageStats[0][3];
        range_pos = imageStats[0][5];
        range_neg = imageStats[0][4];
        range_tot = imageStats[0][6];
        mask1 = imageStats[0][7];
        min = imageStats[0][8];
        max = imageStats[0][9];
        mask_status = imageStats[0][10];
    }

    public int[][] getImageStat(){
        return imageStats;
    }
    
     /**
     * Set the window/level mouse motion map constant.
     */
    public void setMapConstant(){
        mapConst = ((double)windowWidth[0] / 4096) * mapConstVal;
        
        //mapConst = Math.pow(2, (bitsStored - 12));
        
        /*if(bitsStored == 10){
            if(range_tot >= 0 && range_tot <= 255)
                mapConst = 0.1;
            else
                if(range_tot >= 256 && range_tot <= 1024)
                    mapConst = 0.1;
            
        }*/
        
        /*if(bitsStored == 12){
            if(range_tot >= 0 && range_tot <= 255)
                mapConst = 0.1;
            else
                if(range_tot >= 256 && range_tot <= 1024)
                    mapConst = 0.1;
             else
                    if(range_tot >= 1025 && range_tot <= 4095)
                        mapConst = 1.0;
            
        }*/
        
        /*if(bitsStored == 16){
            if(range_tot >= 0 && range_tot <= 255)
                mapConst = 0.1;
            else
                if(range_tot >= 256 && range_tot <= 1024)
                    mapConst = 0.1;
                else
                    if(range_tot >= 1025 && range_tot <= 4095)
                        mapConst = 0.1;
                    else
                        if(range_tot >= 4096 && range_tot <= 16385)
                            mapConst = 0.1;
                        else
                            if(range_tot >= 16386 && range_tot <= 32767)
                                mapConst = 8;
                            else
                                if(range_tot >= 32768 && range_tot <= 65535)
                                    mapConst = 15;
        }*/
    }
    
    /*
     * setWindowingMode
     */
    public void setWindowingMode(int wm){
        windowingMode = wm;
    }
    
    /**
     */
    public void setWindowingFixedMinimumValue(int val){
        windowingFixedMinimumValue = val;
    }
    
     /**
     */
    public void setWindowingFixedMinOverlayStatus(boolean sta){
        canvasOverlayWindowingFixedMinimumStatus = sta;
    }
    
    /**
     * Set the Window/Level.
     */
    public void setWindowLevel(int width, int center) {
        int center_d, width_d;
        width_old = width;
        center_old = center;
        width_d = width;
        center_d = center;
        
        //System.out.println("WindowLevel:setWindowLevel" + windowLevelcnt++);
        // Update of "canvas layout information".
        //width_d = (int) (width * rescaleSlope);
        //if(mask_status == 0)
        //  center_d = (int) ((center + rescaleIntercept) / rescaleSlope);
        //else
        //  center_d = (int) (((center + rescaleIntercept) - mask1) / rescaleSlope);
        //canvas.setWindowLevelValues(width_d, center_d);
        /*
        if(rescaleSlopeInterceptStatus == true){
            width_d = width;
            center_d = (int) (((center - imageStats[0][7]) * rescaleSlope) + rescaleIntercept);
        }*/
        width_d = width;
        if (windowCenterOffsetStatus) {
            center_d = (int) (center - imageStats[0][7]);
        } else {
            center_d = (int) center;
        }
        
        canvas.setWindowLevelValues(width_d, center_d);
        
        if (bitsAllocated == 16) {
            if (photometricInterpretation.equals("MONOCHROME1")) {
                createByteLookupTable16Mono1(width, center);
                lookup = new LookupTableJAI(lutR16);
            } else {
                if (photometricInterpretation.equals("MONOCHROME2")) {
                    if ((modality.equalsIgnoreCase("NM") //zzzzz
                            || modality.equalsIgnoreCase("MR")
                            || modality.equalsIgnoreCase("OT"))
                            && lutDefined) {
                        if (windowingMode == WindowingMode.CLASSIC) {
                            if (width >= 1) {
                                createByteLookupTableRGB16Mono2_2(width, center);
                                lookup = new LookupTableJAI(lutRGB16);
                            } else {
                                createByteLookupTableRGB16Mono2_2(1, center);
                                lookup = new LookupTableJAI(lutRGB16);
                            }
                        } else if (windowingMode == WindowingMode.FIXED_MINIMUM) {
                            if (width - windowingFixedMinimumValue >= 1) {
                                    createByteLookupTableRGB16Mono2FixedMinimum_2(width - windowingFixedMinimumValue, center);
                                    lookup = new LookupTableJAI(lutRGB16);
                            } // Added 2013-09-17
                            else {
                                    createByteLookupTableRGB16Mono2FixedMinimum_2(1, center);
                                    lookup = new LookupTableJAI(lutRGB16);
                                }
                            }
                    } else {
                        createByteLookupTable16Mono2(width, center);
                        lookup = new LookupTableJAI(lutR16);
                    }
                }
            }
        } else {
            if (bitsAllocated == 8) {
                if (photometricInterpretation.equals("MONOCHROME1")) {
                    // THis is a temporary fix.
                    // A 8 bit (mono1) images are not displayed correct if using
                    // a inverted lookup table (Works ok if using a mono2 table).
                    createByteLookupTable8Mono1(width, center);
                } else {
                    //width = 255;
                    //center = 128;
                    createByteLookupTable8Mono2(width, center);
                }
                lookup = new LookupTableJAI(lutR8);
            } else {
                if (bitsAllocated == 10) {
                    if (photometricInterpretation.equals("MONOCHROME1")) {
                        createByteLookupTable12Mono1(width, center);  // This must be wrong...
                    } else {
                        createByteLookupTable12Mono2(width, center);  // This must be wrong..
                    }
                    lookup = new LookupTableJAI(lutR12, 0);
                } else {
                    if (bitsAllocated == 12) {
                        if (photometricInterpretation.equals("MONOCHROME1")) {
                            createByteLookupTable12Mono1(width, center);
                        } else {
                            createByteLookupTable12Mono2(width, center);
                        }
                        lookup = new LookupTableJAI(lutR12, 0);
                    } else {
                        if (bitsAllocated == 15) {
                            if (photometricInterpretation.equals("MONOCHROME1")) {
                                createByteLookupTable15Mono1(width, center);
                            } else {
                                createByteLookupTable15Mono2(width, center);
                        }
                            lookup = new LookupTableJAI(lutR15, 0);
                    }
                }
            }
        }
        }
        canvas.setLookupTable(lookup);
    }
    
    //=======================================================
    // end in use
    //=======================================================
    
    /**
     * Set the Window/Level
     * NOT IN USE
     */
    public void setWindowLevel1_old(int width, int center){
        double[] val = new double[2];
        LookupTableJAI lookup = null;
        
        if(bitsAllocated == 8){
            if(width == 0x8FFFFFFF || center == 0xFFFFFFF)
                val = createWindowLevelDefaultValues(width, center);
            else
                val = createWindowLevelValues(width, center);
            
            canvas.setWindowLevelValues((int)val[0], (int)val[1]);
            
            if(photometricInterpretation.equals("MONOCHROME1"))
                createByteLookupTable8Mono1((int)val[0], (int)val[1]);
            else
                createByteLookupTable8Mono2((int)val[0], (int)val[1]);
            lookup = new LookupTableJAI(lutR8,0);
        } else{
            if(bitsAllocated == 10){
                if(width == 0x8FFFFFFF || center == 0xFFFFFFF)
                    val = createWindowLevelDefaultValues(width, center);
                else
                    val = createWindowLevelValues(width, center);
                
                canvas.setWindowLevelValues((int)val[0], (int)val[1]);
                
                if(photometricInterpretation.equals("MONOCHROME1"))
                    createByteLookupTable12Mono1(width, center);
                else
                    createByteLookupTable12Mono2(width, center);
                lookup = new LookupTableJAI(lutR12,0);
            }else{
                if(bitsAllocated == 12){
                    if(width == 0x8FFFFFFF || center == 0xFFFFFFF)
                        val = createWindowLevelDefaultValues(width, center);
                    else
                        val = createWindowLevelValues(width, center);
                    
                    canvas.setWindowLevelValues((int)val[0], (int)val[1]);
                    
                    if(photometricInterpretation.equals("MONOCHROME1"))
                        createByteLookupTable12Mono1(width, center);
                    else
                        createByteLookupTable12Mono2(width, center);
                    lookup = new LookupTableJAI(lutR12,0);
                }else{
                    if(bitsAllocated == 15){
                        if(width == 0x8FFFFFFF || center == 0xFFFFFFF)
                            val = createWindowLevelDefaultValues(width, center);
                        else
                            val = createWindowLevelValues(width, center);
                        
                        canvas.setWindowLevelValues((int)val[0], (int)val[1]);
                        
                        if(photometricInterpretation.equals("MONOCHROME1"))
                            createByteLookupTable15Mono1(width, center);
                        else
                            createByteLookupTable15Mono2(width, center);
                        lookup = new LookupTableJAI(lutR15,0);
                    }else{
                        if(bitsAllocated == 16){
                            if(width == 0x8FFFFFFF || center == 0xFFFFFFF){
                                val = createWindowLevelDefaultValues(width, center);
                                canvas.setWindowLevelValues((int)val[0], (int)val[1]);
                            } else{
                                val = createWindowLevelValues(width, center);
                                canvas.setWindowLevelValues(width, center);
                            }
                            
                            if(photometricInterpretation.equals("MONOCHROME1"))
                                createByteLookupTable16Mono1((int)val[0], (int)val[1]);
                            else
                                createByteLookupTable16Mono2((int)val[0], (int)val[1]);
                            lookup = new LookupTableJAI(lutR16);
                        }
                    }
                }
            }
        }
        //System.out.println("Time WL20 " + (System.currentTimeMillis()-msecs));
        //BufferedImage offScrImage = canvas.getOffScreenImage();
        //for(int i=4000;i<=6000;i++)
        //    System.out.println("i=" + i + " lutR=" + lutR[i]);
        
        //long msecs = System.currentTimeMillis();
        //BufferedImage finalImage = new BufferedImage(icm, offScrImage.getRaster(), false, null);
        //System.out.println("Time WL30 " + (System.currentTimeMillis()-msecs));
        
        //new CatImageInfo().show(finalImage);
        //canvas.setDisplayImage(finalImage);
        
        //RenderedImage source = canvas.getImage();
        //ParameterBlock pb = new ParameterBlock();
        
        //pb.addSource(source);
        //pb.add(lookup);
        //RenderedImage dst = JAI.create("lookup", pb, null);
        //canvas.set((PlanarImage)dst);
        //PlanarImage img = canvas.getOffScreenImage();
        //RenderedOp op = JAI.create("lookup", img, blut);
        //canvas.setDisplayImage(op);
        
        //RenderedOp op = LookupDescriptor.create(source, lookup, null);
        //canvas.set(op);
        
        canvas.setLookupTable(lookup);
    }
    
    /**
     * Create the canvas default window/level values.
     * This values are for updating of screen window width
     * and screen window center information.
     */
    private double[] createWindowLevelDefaultValues(int width, int center){
        double[] val = new double[2];
        double center_r, width_r;
        
        val[0] = width_r = ((max - min) * rescaleSlope);
        //val[0] = width_r = ((range_tot) * rescaleSlope);
        
        val[1] = center_r = (((max + min) / 2) * rescaleSlope) - rescaleIntercept;
        //val[1] = center_r = 0;
        //val[1] = center_r = ((((range_tot + pos_min) + pos_min) / 2) * rescaleSlope) - rescaleIntercept;
        
        //width_old = (int)width_r; center_old = (int)center_r;
        width_old = (int)width_r;
        
        if(mask1 == 0)
            center_old = (int)center_r;
        else
            center_old = 0;
        
        return val;
    }
    
    /**
     * Create the canvas window/level values.
     * This values are for updating of screen window width
     * and screen window center information.
     */
    private double[] createWindowLevelValues(int width, int center){
        double[] val = new double[2];
        double center_r, width_r;
        
        // test dcm4che
        //final float w = (max - min) * cmParam.getRescaleSlope();
        //final float c = ((max + min) / 2) * cmParam.getRescaleSlope()
        //+ cmParam.getRescaleIntercept();
        
        //int min = imgStat[0][0];
        //int max = imgStat[0][1];
        //int range_neg = imgStat[0][2];
        //int range_pos = imgStat[0][3];
        
        // Values for lookuptable
        //val[0] = width_r = width * rescaleSlope;
        //val[1] = center_r = ((center + range_neg) * rescaleSlope) - rescaleIntercept;
        //width_old = (int)width; center_old = (int)center;
        
        //val[0] = width;
        val[0] = width_r = width * rescaleSlope;
        
        //val[1] = center;
        val[1] = center_r = ((center * rescaleSlope) - rescaleIntercept) + mask1;
        //val[1] = center_r = ((min + center + range_neg) * rescaleSlope) - rescaleIntercept;
        //val[1] = center_r = ((((max + min) / 2) + center) * rescaleSlope) - rescaleIntercept; //dont work
        
        width_old = (int)width; center_old = (int)center;
        
        return val;
    }
    
     /**
     * Set the window/level mouse motion map constant.
     */
    public void setMapConstant(double val){
        if(val == 0.0)
            mapConstVal = 3.0;
        else
            mapConstVal = val;
    }
    
   
    
    /**
     * Set the MouseMotionMapConstant.
     * NOT IN USE
     */
    public void setDefaultMouseMotionMapConstant(){
        if(!mapConstStatus){
            if(range_tot >= 0 && range_tot <= 255)
                mapConst = 0.1;
            else
                if(range_tot >= 256 && range_tot <= 4095)
                    mapConst = 0.1;
                else
                    if(range_tot >= 4096 && range_tot <= 16385)
                        mapConst = 0.1;
                    else
                        if(range_tot >= 16386 && range_tot <= 32767)
                            mapConst = 8;
                        else
                            if(range_tot >= 32768 && range_tot <= 65535)
                                mapConst = 15;
        }
    }
    
    
    
    /**
     * Do the window/level.
     * @param x the x coordinate of the current mouse position.
     * @param y the y coordinate of the current mouse position.
     **/
    public void doWL(int x, int y) {
        if((x < 0 )|| (y < 0))
            return;

        canvas.setCanvasOverlayMousePositionPixelValue(x, y, false);
        setWindowLevelMapping(x, y);
    }
    
    /**
     * Set the windowLevelMapping.
     * Check for window values <= 0
     */
    protected void setWindowLevelMapping(int x, int y){
        int dX = x - anchorX;
        double dXmod = (double) (dX * mapConst);
        int winNew = width_old + (int)Math.round(dXmod);
        if((int)Math.abs((x - anchorX)) > 8)
            anchorX = x;
        
        int dY = y - anchorY;
        int dYmod = (int) (dY * mapConst);
        int levNew = center_old + (int)Math.round(dYmod);
        if((int)Math.abs((y - anchorY)) > 8)
            anchorY = y;
        
        // The value of 0 for window is forbidden in the
        // DICOM standard.
        if(winNew <= 0)
            winNew = 1;
        
        width_old = winNew;
        center_old = levNew;
        setWindowLevel(winNew, levNew);
        
        // Set WLActivatedInStack whenever a wl action occur
        viewDex.canvasControl.setWLActivatedInStack(true);
    }
    
    /**
     * createByteLookupTable8Mono1
     */
    public void createByteLookupTable8Mono1(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 256;
        
        for(int x=startx; x<endx; ++x){
            byte y;
            if(x <= bottom)
                y = bymax;
            else if (x > top)
                y = bymin;
            else
                y = (byte) (256.0 - (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin)));
            
            //lutR8[x&0xffff]= y;
            lutR8[x] = y;
            lutG8[x&0xffff]= y;
            lutB8[x&0xffff]= y;
        }
        
        //for(int t=0; t<=255; t++){
          //  lutR8[t] = (byte) (255 - t);
        //}
            
        //for(int z=0; z<=255; z++)
          //  System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lutR8[z]);
    }
    
    /**
     * createByteLookupTable8Mono2
     */
    public void createByteLookupTable8Mono2(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 256;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y = bymin;
            else if (x > top)
                y = bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            lutR8[x&0xffff]= y;
            lutG8[x&0xffff]= y;
            lutB8[x&0xffff]= y;
            
            //lutRGB8[0][x&0xffff] = y;
            //lutRGB8[1][x&0xffff] = y;
            //lutRGB8[2][x&0xffff] = y;
        }
        
        //for(int z=0; z<=255; z++)
          //  System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lutR8[z]);
    }
    
    /**
     * createByteLookupTableRGB8Mono2
     * NOT IN USE
     */
    public void createByteLookupTableRGB8Mono2(int w, int c){
        for(int i=0;i<256;i++){
            lutRGB8Tmp[0][i] = lutRGB8[0][i];
            lutRGB8Tmp[1][i] = lutRGB8[1][i];
            lutRGB8Tmp[2][i] = lutRGB8[2][i];
        }
        
        /*for(int i=0;i<256;i++){
            if(i > 2*(c/2)){
                lutRGB8Tmp[0][i] = (byte)255;
                lutRGB8Tmp[1][i] = (byte)255;
                lutRGB8Tmp[2][i] = (byte)255;
            }
        }*/
        
        /*
        double window = 2*c;
        double q = window/255;
        
        for(int i=0;i<256;i++){
            if((byte)(lutRGB8HotIron[0][i] * q) <= 255)
                lutRGB8Tmp[0][i] = (byte) (lutRGB8HotIron[0][i] * q);
            else
                lutRGB8Tmp[0][i] = (byte)255;
            
            if((byte)(lutRGB8HotIron[1][i] * q) <= 255)
                lutRGB8Tmp[1][i] = (byte) (lutRGB8HotIron[1][i] * q);
            else
                lutRGB8Tmp[1][i] = (byte)255;
            
            if((byte)(lutRGB8HotIron[2][i] * q) <= 255)
                lutRGB8Tmp[2][i] = (byte) (lutRGB8HotIron[2][i] * q);
            else
                lutRGB8Tmp[2][i] = (byte)255;
        }*/
    }
    
    /**
     * createByteLookupTable12Mono1
     */
    public void createByteLookupTable12Mono1(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 4096;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y = bymax;
            else
                if (x > top)
                    y = bymin;
                else
                    y = (byte) (256.0 - (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin)));
            
            lutR12[x&0xffff]= y;
            lutG12[x&0xffff]= y;
            lutB12[x&0xffff]= y;
        }
    }
    
    /**
     * createByteLookupTable12Mono2
     */
    public void createByteLookupTable12Mono2(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 4096;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else
                if (x > top)
                    y=bymax;
                else
                    y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            lutR12[x&0xffff]= y;
            lutG12[x&0xffff]= y;
            lutB12[x&0xffff]= y;
        }
    }
    
    /**
     * createByteLookupTable15Mono1
     */
    public void createByteLookupTable15Mono1(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 32768;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymax;
            else if (x > top)
                y=bymin;
            else
                y = (byte) (256.0 - (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin)));
            
            lutR15[x&0xffff]= y;
            lutG15[x&0xffff]= y;
            lutB15[x&0xffff]= y;
        }
    }
    
    /**
     * createByteLookupTable15Mono2
     */
    public void createByteLookupTable15Mono2(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 32768;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            lutR15[x&0xffff]= y;
            lutG15[x&0xffff]= y;
            lutB15[x&0xffff]= y;
        }
        //for(int z=80;z<=90;z++)
        //  System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lutR16[z]);
    }
    
    /**
     * createByteLookupTable16Mono1
     */
    public void createByteLookupTable16Mono1(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 65536;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymax;
            else if (x > top)
                y=bymin;
            else
                y = (byte) (256.0 - (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin)));
            
            lutR16[x&0xffff]= y;
            lutG16[x&0xffff]= y;
            lutB16[x&0xffff]= y;
        }
    }
    
    /**
     * createByteLookupTable16Mono2
     */
    public void createByteLookupTable16Mono2(int w, int c){
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 65536;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            //if(y == 0){
            //  y = -1;
            // }
            
            lutR16[x&0xffff]= y;
            lutG16[x&0xffff]= y;
            lutB16[x&0xffff]= y;
            
            //lutRGB16[0][x&0xffff] = y;
            //lutRGB16[1][x&0xffff] = y;
            //lutRGB16[2][x&0xffff] = y;
            
            /*for(int i=0; i<65535; i++)
                lutR16[i] = (byte) 200;
            for(int i=0; i<65535; i++)
                lutG16[i] = (byte) 10;
            for(int i=0; i<65535; i++)
                lutB16[i] = (byte) 10;
            */
            //lutG16[x&0xffff]= y;
            //lutB16[x&0xffff]= y;
            
            
        }
        //int b = 10;
        //for(int z=0;z<=endx;z++)
        //  System.out.println("WindowLevel:createByteLookupTable16Mono2 :" + z + " " + lutR16[z]);
    }
    
    /**
     * createByteLookupTableRGB16Mono2
     */
    public void createByteLookupTableRGB16Mono2(int w, int c){
        // startindex for the red component 32768.
        //System.out.println("w=" + w + " c=" + c);
        
        
        for(int i=0; i <= 65535 ; i++){
            lutRGB16[0][i&0xffff] = (byte) 255;
            lutRGB16[1][i&0xffff] = (byte) 255;
            lutRGB16[2][i&0xffff] = (byte) 255;
        }
        
        int mValue = w/256;
        float mValue2 = (float) w/256;
        int mValue3 = Math.round(mValue2);
        mValue = mValue3;
        
        //System.out.println("mValue:" + mValue);
        if(mValue <= 0)
            mValue = 1;
        
        //int a = c - 32798 - w/2;
        int a = c - imageStats[0][7] - w/2;
        
        //int k = 32768;
        int k = imageStats[0][7];
        int lutCnt = 0;
         
        if(mValue2 >= 1){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                for(int j=i; j < i + (mValue); j++){
                    if(lutCnt <= 255){
                        lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                        lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                        lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                    }
                }
                if(lutCnt >= 255){
                    int g = 10;
                }
                lutCnt++;
                i = i + (mValue - 1);
            }
            /*
            if(mValue2 >= 1 && mValue2 <= 1.5){
                for(int i = k + a + 256; i <= k + a + w; i++){
                    lutRGB16[0][i&0xffff] = (byte) 255;
                    lutRGB16[1][i&0xffff] = (byte) 0;
                    lutRGB16[2][i&0xffff] = (byte) 255;
                }
            }*/
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.5 && mValue2 < 1){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 2;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.25 && mValue2 < 0.5){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 4;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.125 && mValue2 < 0.25){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 8;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.06 && mValue2 < 0.125){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 16;
            }
            
             for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        //int b = 10;
        //for(int z=32768;z<=32768 + w;z++)
          //System.out.println("WL:" + z + " " + lutRGB16[0][z]);
        //int d = 10;
    }
    
    /**
     * createByteLookupTableRGB16Mono2
     */
    public void createByteLookupTableRGB16Mono2_2(int w, int c){
        byte buf0[] = new byte[w];
        byte buf1[] = new byte[w];
        byte buf2[] = new byte[w];
        // startindex for the red component 32768.
        //System.out.println("w=" + w + " c=" + c);
        
        for(int i=0; i <= 65535 ; i++){
            lutRGB16[0][i&0xffff] = (byte) 255;
            lutRGB16[1][i&0xffff] = (byte) 255;
            lutRGB16[2][i&0xffff] = (byte) 255;
        }
        
        int mValue = w/256;
        float mValue2 = (float) w/256;
        int mValue3 = Math.round(mValue2);
        mValue = mValue3;
        
        //int a = c - 32798 - w/2;
        int a = c - imageStats[0][7] - w/2;
        
        //int k = 32768;
        int k = imageStats[0][7];
        
        buf0 = getBufferScaled(lutRGB8[0], 256, mValue2);
        buf1 = getBufferScaled(lutRGB8[1], 256, mValue2);
        buf2 = getBufferScaled(lutRGB8[2], 256, mValue2);
        
        for(int i=0; i < k + a; i++){
            lutRGB16[0][i&0xffff] = (byte) 0;
            lutRGB16[1][i&0xffff] = (byte) 0;
            lutRGB16[2][i&0xffff] = (byte) 0;
        }
        
        for(int i=0; i < buf0.length; i++){
            lutRGB16[0][(i+k+a)&0xffff] = (byte) buf0[i];
            lutRGB16[1][(i+k+a)&0xffff] = (byte) buf1[i];
            lutRGB16[2][(i+k+a)&0xffff] = (byte) buf2[i];
        }
        
        for(int i = k+a+w+1; i<=65535; i++){
            lutRGB16[0][i&0xffff] = (byte) 255;
            lutRGB16[1][i&0xffff] = (byte) 255;
            lutRGB16[2][i&0xffff] = (byte) 255;
        }
    }
        
    /*
     */
    public byte[] getBufferScaled(byte[] lutBuf, int width, float mValue2){
        byte [] buf;
        DataBufferByte dbuffer = new DataBufferByte(lutBuf,256);
        SampleModel sampleModel = RasterFactory.
                createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, 1, 1);
        ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
        Raster raster = RasterFactory.createWritableRaster(sampleModel,dbuffer,new Point(0,0));
        TiledImage tiledImage = new TiledImage(0,0,width,1,0,0,sampleModel,colorModel);
        tiledImage.setData(raster);
        //JAI.create("filestore",tiledImage,"lutImage.tif","TIFF");
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(tiledImage);
        pb.add(mValue2);
        pb.add(1.0f);
        pb.add(0.0F);
        pb.add(0.0F);
        pb.add(new InterpolationNearest());
        
        PlanarImage scaledImage = JAI.create("scale", pb);
        //JAI.create("filestore",scaledImage,"lutImageScaled.tif","TIFF");
        
        //SampleModel sm = scaledImage.getSampleModel();
        //Raster raster2 = scaledImage.getData();
        //DataBuffer db = raster2.getDataBuffer();
        //int size = db.getSize();
        //byte[] data = ((DataBufferByte) db).getData();
        DataBuffer db = scaledImage.getData().getDataBuffer();
        buf = ((DataBufferByte) db).getData();
        return buf;
    }
    
    /**
     * createByteLookupTableRGB16Mono2
     */
    public void createByteLookupTableRGB16Mono2FixedMinimum(int w, int c){
        // startindex for the red component 32768.
        //System.out.println("w=" + w + " c=" + c);
        
        int mValue = w/256;
        float mValue2 = (float) w/256;
        int mValue3 = Math.round(mValue2);
        mValue = mValue3;
        
        //System.out.println("mValue:" + mValue);
        if(mValue <= 0)
            mValue = 1;
        
        //int a = c - 32798 - w/2;
        int a = c - imageStats[0][7] - w/2;
        
        // new
        a = windowingFixedMinimumValue;
        c = a + w/2;
        
        //int k = 32768;
        int k = imageStats[0][7];
        int lutCnt = 0;
        
        if(mValue2 >= 1){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                for(int j=i; j<i + (mValue); j++){
                    if(lutCnt <= 255){
                        lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                        lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                        lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                    }
                }
                lutCnt++;
                i = i + (mValue - 1);
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.5 && mValue2 < 1){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 2;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
            
        }
        
        else if(mValue2 >= 0.25 && mValue2 < 0.5){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 4;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.125 && mValue2 < 0.25){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 8;
            }
            
            for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        else if(mValue2 >= 0.06 && mValue2 < 0.125){
            for(int i=0; i < k + a; i++){
                lutRGB16[0][i&0xffff] = (byte) 0;
                lutRGB16[1][i&0xffff] = (byte) 0;
                lutRGB16[2][i&0xffff] = (byte) 0;
            }
            
            for(int i = k + a; i <= k + a + w; i++){
                int j=i;
                if(lutCnt <= 255){
                    lutRGB16[0][j&0xffff] = (byte) (lutRGB8[0][lutCnt]);
                    lutRGB16[1][j&0xffff] = (byte) (lutRGB8[1][lutCnt]);
                    lutRGB16[2][j&0xffff] = (byte) (lutRGB8[2][lutCnt]);
                }
                lutCnt = lutCnt + 16;
            }
            
             for(int i = k+a+w+1; i<=65535; i++){
                lutRGB16[0][i&0xffff] = (byte) 255;
                lutRGB16[1][i&0xffff] = (byte) 255;
                lutRGB16[2][i&0xffff] = (byte) 255;
            }
        }
        
        //int b = 10;
        //for(int z=32768;z<=32768 + w;z++)
        //System.out.println("WL:" + z + " " + lutRGB16[0][z]);
        //int d = 10;
    }
    
    /**
     * createByteLookupTableRGB16Mono2FixedMinimum_2
     */
    public void createByteLookupTableRGB16Mono2FixedMinimum_2(int w, int c){
        byte buf0[] = new byte[w];
        byte buf1[] = new byte[w];
        byte buf2[] = new byte[w];
        // startindex for the red component 32768.
        //System.out.println("w=" + w + " c=" + c);
        
        for(int i=0; i <= 65535 ; i++){
            lutRGB16[0][i&0xffff] = (byte) 255;
            lutRGB16[1][i&0xffff] = (byte) 255;
            lutRGB16[2][i&0xffff] = (byte) 255;
        }
        
        int mValue = w/256;
        float mValue2 = (float) w/256;
        int mValue3 = Math.round(mValue2);
        mValue = mValue3;
        
        //int a = c - 32798 - w/2;
        //int a = c - imageStats[0][7] - w/2;
        int a = windowingFixedMinimumValue;
        c = a + w/2;
        
        //int k = 32768;
        int k = imageStats[0][7];
        
        buf0 = getBufferScaled(lutRGB8[0], 256, mValue2);
        buf1 = getBufferScaled(lutRGB8[1], 256, mValue2);
        buf2 = getBufferScaled(lutRGB8[2], 256, mValue2);
        
        for(int i=0; i < k + a; i++){
            lutRGB16[0][i&0xffff] = (byte) 0;
            lutRGB16[1][i&0xffff] = (byte) 0;
            lutRGB16[2][i&0xffff] = (byte) 0;
        }
        
        for(int i=0; i < buf0.length; i++){
            lutRGB16[0][(i+k+a)&0xffff] = (byte) buf0[i];
            lutRGB16[1][(i+k+a)&0xffff] = (byte) buf1[i];
            lutRGB16[2][(i+k+a)&0xffff] = (byte) buf2[i];
        }
        
        for(int i = k+a+w+1; i<=65535; i++){
            lutRGB16[0][i&0xffff] = (byte) 255;
            lutRGB16[1][i&0xffff] = (byte) 255;
            lutRGB16[2][i&0xffff] = (byte) 255;
        }
    }
    
    /**
     * createByteLookupTableRGB16Mono2
     */
    public void createByteLookupTableR16Mono2_test(int w, int c){
        int levelOffsetRGB = w/3;
        int wR = 200; int wG = 95; int wB = 60;
        //c = levelOffsetRGB;
        //w = wR;
        //int cNew = 34395;
        //int cNew = 36022;
        //int cNew = 37649;
        //int cNew = 38000;
        int cNew = 32815;
        
        System.out.println("wR = " + w);
        System.out.println("cR = " + c);
        System.out.println("cRNew = " + cNew);
        
        double cmp5 = cNew - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 65536;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            //if(y == 0){
            //  y = -1;
            // }
            
            //lutR16[x&0xffff]= y;
            //lutG16[x&0xffff]= y;
            //lutB16[x&0xffff]= y;
            
            lutRGB16[0][x&0xffff] = y;
            //lutRGB16[1][x&0xffff] = y;
            //lutRGB16[2][x&0xffff] = y;
            
            /*for(int i=0; i<65535; i++)
                lutR16[i] = (byte) 200;
            for(int i=0; i<65535; i++)
                lutG16[i] = (byte) 10;
            for(int i=0; i<65535; i++)
                lutB16[i] = (byte) 10;
            */
            //lutG16[x&0xffff]= y;
            //lutB16[x&0xffff]= y;
            
            
        }
        //int b = 10;
        //for(int z=0;z<=endx;z++)
        //  System.out.println("WindowLevel:createByteLookupTable16Mono2 :" + z + " " + lutR16[z]);
    }
    
    /**
     * createByteLookupTableG16Mono2
     */
    public void createByteLookupTableG16Mono2_test(int w, int c){
        int levelOffsetRGB = w/3;
        int wR = 200; int wG = 95; int wB = 60;
        //c = levelOffsetRGB;
        //w = wR;
        //int cNew = 34395;
        //int cNew = 36022;
        //int cNew = 37649;
        //int cNew = 38000;
        int cNew = 32911;
        
        System.out.println("wG = " + w);
        System.out.println("cG = " + c);
        System.out.println("cGNew = " + cNew);
        
        double cmp5 = cNew - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 65536;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            //if(y == 0){
            //  y = -1;
            // }
            
            //lutR16[x&0xffff]= y;
            //lutG16[x&0xffff]= y;
            //lutB16[x&0xffff]= y;
            
            //lutRGB16[0][x&0xffff] = y;
            lutRGB16[1][x&0xffff] = y;
            //lutRGB16[2][x&0xffff] = y;
        }
        //int b = 10;
        //for(int z=0;z<=endx;z++)
        //  System.out.println("WindowLevel:createByteLookupTable16Mono2 :" + z + " " + lutR16[z]);
    }
    
    /**
     * createByteLookupTableG16Mono2
     */
    public void createByteLookupTableB16Mono2_test(int w, int c){
        int levelOffsetRGB = w/3;
        int wR = 200; int wG = 95; int wB = 60;
        //c = levelOffsetRGB;
        //w = wR;
        //int cNew = 34395;
        //int cNew = 36022;
        //int cNew = 37649;
        //int cNew = 32000;
        int cNew = 32960;
        
        System.out.println("wB = " + w);
        System.out.println("cB = " + c);
        System.out.println("cBNew = " + cNew);
        
        double cmp5 = cNew - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 65536;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else
                y = (byte) ((((x-cmp5)/wm1) + 0.5) * (yrange + ymin));
            
            //if(y == 0){
            //  y = -1;
            // }
            
            //lutR16[x&0xffff]= y;
            //lutG16[x&0xffff]= y;
            //lutB16[x&0xffff]= y;
            
            //lutRGB16[0][x&0xffff] = y;
            //lutRGB16[1][x&0xffff] = y;
            lutRGB16[2][x&0xffff] = y;
        }
        //int b = 10;
        //for(int z=0;z<=endx;z++)
        //  System.out.println("WindowLevel:createByteLookupTable16Mono2 :" + z + " " + lutR16[z]);
    }
    
    
//********************************************************
// DICOM tags for the current image
//********************************************************
    
//**************************************************************
// DICOM tags end
//**************************************************************
    
    /**Starts the window/level.
     * @param x the x coordinate of the current mouse position.
     * @param y the y coordinate of the current mouse position.
     **/
    public void startWL(int x, int y) {
        //canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        anchorX = x;
        anchorY = y;
    }
    
    /**
     * Set the default cursor.
     * NOT IN USE.
     * 
     */
    public void stopWL() {
        canvas.setCursor(Cursor.getDefaultCursor());
    }
    
    /**
     * Set the windowLevel.
     */
    public void setWindowLevel(){
        setWindowLevel(width_old, center_old);
    }
    
    /**
     * Set the windowLevel.
     * NOT IN USE
     */
    public void setWindowLevel_old(int win, int lev){
        LookupTableJAI lookup = null;
        IndexColorModel icm=null;
        width_old = win;
        center_old = lev;
        
        //cnt++;
        //System.out.println("CatWindowLevel: setWindowLevel" + "  " + cnt);
        //System.out.println("W/L =" + win + " " + lev);
        
        // Set the values for later update of the canvas.
        //canvas.setWindowLevelValues(win, lev);
        
        //long msecs = System.currentTimeMillis();
        if(bitsStored == 8){
            if(photometricInterpretation.equals("MONOCHROME1"))
                createByteLookupTable8Mono1(win, lev);
            else
                createByteLookupTable8Mono2(win, lev);
            lookup = new LookupTableJAI(lutR8,0);
        } else{
            if(bitsStored == 10){
                if(photometricInterpretation.equals("MONOCHROME1"))
                    createByteLookupTable12Mono1(win, lev);
                else
                    createByteLookupTable12Mono2(win, lev);
                lookup = new LookupTableJAI(lutR12,0);
            } else{
                if(bitsStored == 12){
                    if(photometricInterpretation.equals("MONOCHROME1"))
                        createByteLookupTable12Mono1(win, lev);
                    else
                        createByteLookupTable12Mono2(win, lev);
                    lookup = new LookupTableJAI(lutR12,0);
                } else{
                    if(bitsStored == 16){
                        if(photometricInterpretation.equals("MONOCHROME1"))
                            createByteLookupTable16Mono1(win, lev);
                        else
                            createByteLookupTable16Mono2(win, lev);
                        lookup = new LookupTableJAI(lutR16,0);
                    }
                }
            }
        }
        //System.out.println("Time WL20 " + (System.currentTimeMillis()-msecs));
        //BufferedImage offScrImage = canvas.getOffScreenImage();
        //for(int i=4000;i<=6000;i++)
        //    System.out.println("i=" + i + " lutR=" + lutR[i]);
        
        //long msecs = System.currentTimeMillis();
        //BufferedImage finalImage = new BufferedImage(icm, offScrImage.getRaster(), false, null);
        //System.out.println("Time WL30 " + (System.currentTimeMillis()-msecs));
        
        //new CatImageInfo().show(finalImage);
        //canvas.setDisplayImage(finalImage);
        
        RenderedImage source = canvas.getImage();
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(lookup);
        RenderedImage dst = JAI.create("lookup", pb, null);
        //canvas.set((PlanarImage)dst);
        //PlanarImage img = canvas.getOffScreenImage();
        //RenderedOp op = JAI.create("lookup", img, blut);
        //canvas.setDisplayImage(op);
    }
    
    
    
    
    
// -------------------------------------------------------------------
// createByteLookupTable12
// -------------------------------------------------------------------
    public void createByteLookupTable12(int w, int c){
        int ymin = 0; int ymax = 255;
        byte bymin = (byte)ymin;
        byte bymax = (byte)ymax;
        double yrange = ymax - ymin;
        
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 4096;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            //bytey3=0, y4;
            //int z=0;
            //double y1=0, y2=0;
            if(x <= bottom)
                y=bymax;
            else if (x > top)
                y=bymin;
            else{
                //y1 = ((x-cmp5)/wm1) + 0.5;
                //y2 = (y1 * 255.0) + ymin;
                //z = (int)y2;
                //y3 = (byte)z;
                //y4 = (byte)z;
                y = (byte) (256.0 - (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin)));
            }
            lutR12[x&0xffff]= y;
            lutG12[x&0xffff]= y;
            lutB12[x&0xffff]= y;
            
            //lut[x]=y;
            //lut[x&0xffff]= y
            /*
            if(photometricInterpretation.equals("MONOCHROME1")){
                lutR12[x&0xffff]= (byte) (256-y);
                lutG12[x&0xffff]= (byte) (256-y);
                lutB12[x&0xffff]= (byte) (256-y);
            }
            else{
                lutR12[x&0xffff]= y;
                lutG12[x&0xffff]= y;
                lutB12[x&0xffff]= y;
            } */
            //System.out.println("CatWindowLevel:createByteLookupTable :" + x + " " + y);
            //System.out.println("CatWindowLevel:createByteLookupTable :" + x + " " + y3);
        }
        int a =10;
        //for(int z=300;z<=700;z++)
        //   System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        //return new ByteLookupTable(0, lut);
    }
    
// -------------------------------------------------------------------
// createByteLookupTable14
// -------------------------------------------------------------------
    public void createByteLookupTable14(int w, int c){
        int ymin = 0; int ymax = 255;
        byte bymin = (byte)ymin;
        byte bymax = (byte)ymax;
        double yrange = ymax - ymin;
        
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 16384;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            //bytey3=0, y4;
            //int z=0;
            //double y1=0, y2=0;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else{
                //y1 = ((x-cmp5)/wm1) + 0.5;
                //y2 = (y1 * 255.0) + ymin;
                //z = (int)y2;
                //y3 = (byte)z;
                //y4 = (byte)z;
                y = (byte) (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin));
            }
            //lut[x]=y;
            //lut[x&0xffff]= y;
            if(photometricInterpretation.equals("MONOCHROME1")){
                lutR14[x&0xffff]= (byte) (256-y);
                lutG14[x&0xffff]= (byte) (256-y);
                lutB14[x&0xffff]= (byte) (256-y);
            } else{
                lutR14[x&0xffff]= y;
                lutG14[x&0xffff]= y;
                lutB14[x&0xffff]= y;
            }
            
            //System.out.println("CatWindowLevel:createByteLookupTable :" + x + " " + y);
            //System.out.println("CatWindowLevel:createByteLookupTable :" + x + " " + y3);
        }
        //for(int z=300;z<=700;z++)
        //   System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        //return new ByteLookupTable(0, lut);
    }
    
// -------------------------------------------------------------------
// createByteLookupTable15
// -------------------------------------------------------------------
    public void createByteLookupTable15(int w, int c){
        int ymin = 0; int ymax = 255;
        byte bymin = (byte)ymin;
        byte bymax = (byte)ymax;
        double yrange = ymax - ymin;
        
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 32768;
        
        for(int x=startx; x < endx; ++x){
            byte y;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else{
                y = (byte) (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin));
            }
            if(photometricInterpretation.equals("MONOCHROME1")){
                lutR15[x&0xffff]= (byte) (256-y);
                lutG15[x&0xffff]= (byte) (256-y);
                lutB15[x&0xffff]= (byte) (256-y);
            } else{
                lutR15[x&0xffff]= y;
                lutG15[x&0xffff]= y;
                lutB15[x&0xffff]= y;
            }
        }
    }
    
    
    
    
// -------------------------------------------------------------------
// -------------------------------------------------------------------
//
// Some old code not in use
//
// -------------------------------------------------------------------
// -------------------------------------------------------------------
    
    private RenderedImage rescale(RenderedImage img){
        RenderedImage dst = null;
        
        // rescale
        double[] constant = {1.0};
        double[] offset = {-1024.0};
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(constant);
        pb.add(offset);
        //pb.add(DataBuffer.TYPE_USHORT);
        dst = JAI.create("rescale", pb, null);
        SampleModel sampleModel = dst.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();
        
        return dst;
    }
    
    /** Create the window/level result image. */
    private final RenderedImage windowLevelOperator(RenderedImage source,
            double low,
            double high) {
        
        /**
         * The look-up-table for window/leveling. It is also used in the
         *  display of histogram/statistics.
         */
        byte[][] lut;
        
        if ( source == null ) {
            return null;
        }
        
        ParameterBlock pb = null;
        RenderedImage dst = null;
        RenderedImage dst2 = null;
        SampleModel sampleModel = source.getSampleModel();
        int bands = sampleModel.getNumBands();
        int datatype = sampleModel.getDataType();
        double rmin;
        double rmax;
        double slope;
        double y_int;
        int tableLength = 256;
        
        if (datatype == DataBuffer.TYPE_SHORT ||
                datatype == DataBuffer.TYPE_USHORT) {
            tableLength = 65536;
        }
        
        // use a lookup table for rescaling
        if (high != low) {
            slope = 256.0 / (high - low);
            y_int = 256.0 - slope*high;
        } else {
            slope = 0.0;
            y_int = 0.0;
        }
/*
        ImageLayout il = new ImageLayout();
        il.setTileWidth(tileSize);
        il.setTileHeight(tileSize);
        il.setTileGridXOffset(0);
        il.setTileGridYOffset(0);
        RenderingHints hints =
                new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
 */
        
        
        if (datatype >= DataBuffer.TYPE_BYTE &&
                datatype < DataBuffer.TYPE_INT) {
            lut = new byte[bands][tableLength];
            
            for (int i = 0; i < tableLength; i++) {
                for (int j = 0; j < bands; j++) {
                    int value = (int)(slope*i + y_int);
                    
                    if ( datatype == DataBuffer.TYPE_USHORT ) {
                        value &= 0xFFFF;
                    }
                    
                    if (value > 255)
                        value = 255;
                    
                    if (i < (int)low) {
                        value = 0;
                    } else if (i > (int)high) {
                        value = 255;
                    } else {
                        value &= 0xFF;
                    }
                    
                    lut[j][i] = (byte) value;
                }
            }
            
            LookupTableJAI lookup = new LookupTableJAI(lut);
            
            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(lookup);
            dst = JAI.create("lookup", pb, null); //hints);
        } else if ( datatype == DataBuffer.TYPE_INT   ||
                datatype == DataBuffer.TYPE_FLOAT ||
                datatype == DataBuffer.TYPE_DOUBLE ) {
            pb = new ParameterBlock();
            pb.addSource(source);
            pb.add(slope);
            pb.add(y_int);
            dst = JAI.create("rescale", pb, null);
            
            // produce a byte image
            pb = new ParameterBlock();
            pb.addSource(dst);
            pb.add(DataBuffer.TYPE_BYTE);
            dst = JAI.create("format", pb, null);
        }
        
        return dst;
    }
    
    public void setWindowLevelOldValues(int win, int lev){
        width_old = win;
        center_old = lev;
    }
    
// sets wlImage in canvas to null
    public void reset(){
        //System.out.println("CatWindowLevel: reset");
        firstTime = true;
        //canvas.resetWLImage();
    }
    
    
    public ShortLookupTable createShortLookupTable(int w, int c){
        int ymin = 0;
        int ymax = 255;
        int test;
        
        short bymin = (short)ymin;
        short bymax = (short)ymax;
        double yrange = ymax - ymin;
        
        double cmp5 = c - 0.5;
        double wm1 = w - 1.0;
        double halfwm1 = wm1/2.0;
        double bottom = cmp5 - halfwm1;
        double top = cmp5 + halfwm1;
        
        int startx = 0;
        int endx = 1024;   // 256; 1024
        short lut[] = new short[1024];   // 256; 1024
        
        for(int x = startx; x < endx; ++x){
            short y, y3=0, y4;
            int z=0;
            double y1=0, y2=0;
            if(x <= bottom)
                y=bymin;
            else if (x > top)
                y=bymax;
            else{
                y1 = ((x-cmp5)/wm1) + 0.5;
                y2 = (y1 * 255.0) + ymin;
                z = (int)y2;
                y3 = (byte)z;
                y4 = (byte)z;
                y = (short) (((((x-cmp5)/wm1) + 0.5)) * (yrange + ymin));
            }
            if(x == 620)
                test = 10;
            //lut[x]=y;
            //y = (short)(ymax-y);
            lut[x&0xffff]= y;
            
            //System.out.println("CatWindowLevel:createByteLookupTable :" + x + " " + y3);
        }
        for(int z=300;z<=700;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        /*for(int z=101;z<=200;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        for(int z=201;z<=400;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        for(int z=401;z<=600;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        for(int z=601;z<=800;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
        for(int z=801;z<=1023;z++)
            System.out.println("CatWindowLevel:createByteLookupTable :" + z + " " + lut[z]);
         */
        return new ShortLookupTable(0, lut);
    }
    
    /** Creates a lookup table for the specified window width and level.
     * @param win width of the window.
     * @param lev the level.
     */
    
    public ByteLookupTable createByteLookupTable_2(int win, int lev) {
        int windowStart= lev-win/2;
        int windowEnd  = lev+win/2;
        int lutSize = 1024;
        
        if(windowStart <= 0)
            windowStart =0;
        if(windowEnd > lutSize)
            windowEnd = lutSize;
        
        byte lut[] = new byte[lutSize];
        double windowMappingRatio = (MAX_SCREEN_VALUE-MIN_SCREEN_VALUE)/(double)(win);
        
        for(int i=0;i<windowStart;i++) {
            lut[i] = (byte)MIN_SCREEN_VALUE;
        }
        
        for(int i = windowStart; i< windowEnd; i++) {
            lut[i] = (byte)((i-windowStart)* windowMappingRatio);
        }
        
        for(int i = windowEnd; i < lutSize; i++) {
            lut[i] = (byte)MAX_SCREEN_VALUE;
        }
        
        for(int x=windowStart; x<lutSize; x++){
            int b = lut[x];
            //System.out.println("CatWindowLevel:createByteLookupTable_2 :" + x + " " + b);
        }
        return  new ByteLookupTable(0, lut);
    }
    
    
    
// -------------------------------------------------------------------
// Testing
// -------------------------------------------------------------------
   
    
    public void setWindowLevel_test(){
        //setWindowLevel_test2(winOld, levOld); // Modified 2004-02-18
    }
    
    /*
     * Setting the window/level by use of the dcm4che library
     */
    /*
    public void setWindowLevel10(int win, int lev){
        width_old = win;
        center_old = lev;
        LookupTableJAI lookup = null;
        
        //System.out.println("WindowLevel:setWindowLevel");
        //System.out.println("W/L =" + win + " " + lev);
        
        // canvas.setWindowLevelValues(win, lev);
        //BufferedImage offScrImage = canvas.getOffScreenImage();
        //new CatImageInfo().show(offScrImage);
        //int windowWidth = dataset.getString(Tags.WindowWidth, null);
        //int windowCenter = dataset.getString(Tags.WindowCenter, null);
        cmParam = cmParam.update((float)lev, (float)win, cmParam.isInverse());
        //ColorModel cm = cmFactory.getColorModel(cmParam);
        
        //PlanarImage img = canvas.getImage();
        //ImageLayout il = new ImageLayout();
        //il.setColorModel(cm);
        //RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
        //BufferedImage bufImage = img.getAsBufferedImage();
        //BufferedImage finalImage = new BufferedImage(cm, bufImage.getRaster(), false, null);
        //WritableRaster wraster2 = img.copyData(null);
        //PlanarImage img2 = canvas.getImage();
        //Raster raster = img.getData();
        //WritableRaster wraster = raster.createCompatibleWritableRaster();
        //wraster.setRect(raster);
        //BufferedImage bufImage = new BufferedImage(cm, wraster2, false, null);
        //PlanarImage planarImage = PlanarImage.wrapRenderedImage(finalImage);
        //new CatImageInfo().show(finalImage);
        
        //createByteLookupTable16Mono2(win, lev);
        //lookup = new LookupTableJAI(lutR16,0);
        
        //ParameterBlock pb = new ParameterBlock();
        //pb.addSource(img);
        //pb.add(img.getSampleModel().getDataType());
        //pb.add(lookup);
        //float f1 = 1.0f;
        //float f2 = 1.0f;
        //pb.add(f1);
        //pb.add(f2);
        //PlanarImage dst = JAI.create("format", pb, hints);
        
        //canvas.set(cm);
    }*/

    // NOT IN USE
    /*
    private void setWindowLevelTest1(int window, int level){
        System.out.println("WindowLevel: setWindow2");
        System.out.println("W/L =" + window + " " + level);
        
        RenderedImage source = canvas.getImage();
        //RenderedImage rimg = rescale(source);
        windowLevelResult = windowLevelOperator(source, level - (window / 2.0),
                level + (window / 2.0));
        
        //RenderedImage windowResult = windowLevelOperator(source, level - window / 2.0,
        //        level + window / 2.0);
        //rotationResult = rotationOperator(windowLevelResult, rotationAngle);
        //zoomResult = zoomOperator(rotationResult, zoomFactor);
        
        //canvas.set((PlanarImage)windowLevelResult);
        canvas.set12(windowLevelResult);
    }*/
    
    /*
    private void setLevelTest2(int window, int level){
        RenderedImage source = canvas.getImage();
        windowLevelResult = windowLevelOperator(source, level - (window / 2.0),
                level + (window / 2.0));
        //RenderedImage levelResult = windowLevelOperator(source, level - window / 2.0,
        //        level + window / 2.0);
        //rotationResult = rotationOperator(windowLevelResult, rotationAngle);
        //zoomResult = zoomOperator(rotationResult, zoomFactor);
        
        //canvas.set((PlanarImage)windowLevelResult);
    }*/
    
    /** Set the new window value.
     * @param window The new window value.
     * @param level The new level value.
     * DELETE DELETE
     */
    /*
    public void setWindowLevel_oldold(int window, int level) {
        //System.out.println("Window: " + window);
        //System.out.println("Level: " + level);
        
        //setWindowLevel1(window, level);
        //setWindowLevel2(window, level);
        //setWindow2(window, level);
        //setWindowLevelTest1(window, level);
        //setLevel2(window, level);
        //setWindow2(1000, 800);
        //setLevel2(1000, 800);
    }*/
    
    
    
// -------------------------------------------------------------------
// setWindowLevel
// NOT IN USE
// -------------------------------------------------------------------
    /*
    public void setWindowLevel2(int win, int lev){
        IndexColorModel icm=null;
        width_old = win;
        center_old = lev;
        
        System.out.println("CatWindowLevel1: window = " + win);
        System.out.println("CatWindowLevel1: level = " + lev);
        
        //cnt++;
        //System.out.println("CatWindowLevel: setWindowLevel" + "  " + cnt);
        
        // Set the values for later update of the canvas.
        //canvas.setWindowLevelValues(win, lev);
        
        //long msecs = System.currentTimeMillis();
        if(bitsStored == 8){
            if(photometricInterpretation.equals("MONOCHROME1"))
                createByteLookupTable8Mono1(win, lev);
            else
                createByteLookupTable8Mono2(win, lev);
            icm = new IndexColorModel(8, 256, lutR8,lutG8,lutB8);
        } else{
            if(bitsStored == 10){
                if(photometricInterpretation.equals("MONOCHROME1"))
                    createByteLookupTable12Mono1(win, lev);
                else
                    createByteLookupTable12Mono2(win, lev);
                icm = new IndexColorModel(12, 4096, lutR12,lutG12,lutB12);
            } else{
                if(bitsStored == 12){
                    if(photometricInterpretation.equals("MONOCHROME1"))
                        createByteLookupTable12Mono1(win, lev);
                    else
                        createByteLookupTable12Mono2(win, lev);
                    icm = new IndexColorModel(12, 4096, lutR12,lutG12,lutB12);
                } else{
                    if(bitsStored == 16){
                        if(photometricInterpretation.equals("MONOCHROME1"))
                            createByteLookupTable16Mono1(win, lev);
                        else
                            createByteLookupTable16Mono2(win, lev);
                        //icm = new IndexColorModel(16, 65536, lutR16,lutG16,lutB16);
                    }
                }
            }
        }
        //System.out.println("Time WL20 " + (System.currentTimeMillis()-msecs));
        //BufferedImage offScrImage = canvas.getOffScreenImage();
        //for(int i=4000;i<=6000;i++)
        //    System.out.println("i=" + i + " lutR=" + lutR[i]);
        
        //long msecs = System.currentTimeMillis();
        //BufferedImage finalImage = new BufferedImage(icm, offScrImage.getRaster(), false, null);
        //System.out.println("Time WL30 " + (System.currentTimeMillis()-msecs));
        
        //new CatImageInfo().show(finalImage);
        //canvas.setDisplayImage(finalImage);
        
        cmParam = cmParam.update((float)lev, (float)win, cmParam.isInverse());
        //ColorModel cm5 = cmFactory.getColorModel(cmParam);
        
        PlanarImage img = canvas.getImage();
        ColorModel cm = img.getColorModel();
        SampleModel sm = img.getSampleModel();
        int dt = sm.getDataType();
        System.out.println(img.getData().getSample(100, 100, 0));
        
        int imageDepth = 3;
        ImageLayout il = new ImageLayout(img);
        il.setColorModel(new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                false, // hasAlpha
                false, // Alpha premultiplied
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT));
        RenderingHints hints =
                new RenderingHints(JAI.KEY_COLOR_MODEL_FACTORY, il);
        ParameterBlock pb2 = new ParameterBlock();
        pb2.addSource(img);
        pb2.add(DataBuffer.TYPE_USHORT);
        img = JAI.create("format", pb2, il);
        
        ColorModel cm3 = img.getColorModel();
        SampleModel sm3 = img.getSampleModel();
        int dt3 = sm3.getDataType();
        System.out.println(img.getData().getSample(100, 100, 0));
        
        //produce a byte image
        //ParameterBlock pb = new ParameterBlock();
        //pb.addSource(img);
        //pb.add(DataBuffer.TYPE_USHORT);
        //RenderedImage dst = JAI.create("format", pb, null);
        
        //Raster raster = img.getData();
        //DataBuffer buf = raster.getDataBuffer();
        
        ParameterBlock pb = new ParameterBlock();
        double[] constant = {1.0};
        pb.add(constant);
        double[] offset = {-1024.0};
        pb.add(offset);
        pb.addSource(img);
        //img = JAI.create("Rescale", pb);
        
        ColorModel cm2 = img.getColorModel();
        SampleModel sm2 = img.getSampleModel();
        int dt2 = sm2.getDataType();
        System.out.println(img.getData().getSample(100, 100, 0));
        
        //produce a byte image
        //pb = new ParameterBlock();
        //pb.addSource(img);
        //pb.add(DataBuffer.TYPE_USHORT);
        //img = JAI.create("format", pb, null);
        //System.out.println(img.getData().getSample(100, 100, 0));
        
        LookupTableJAI lookup = new LookupTableJAI(lutR16,0);
        pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(lookup);
        img = JAI.create("lookup", pb, null); //hints);
        
        // test
        //SampleModel sampleModel = dst2.getSampleModel();
        //int bands = sampleModel.getNumBands();
        //int datatype = sampleModel.getDataType();
        //ImageLayout il = new ImageLayout(dst2.createInstance());
        //ColorModel cm2 = il.getColorModel(null);
        //SampleModel sm = il.getSampleModel(null);
        //boolean val = cm2.isCompatibleSampleModel(sm);
        
        canvas.set12(img);
        
        //RenderedOp op = JAI.create("lookup", img, blut);
        //canvas.setDisplayImage(op);
        //LookupTableJAI lookup = new LookupTableJAI(lut);
    }*/
    
    /*
     * NOT IN USE
     */
    /*
    protected void setWindowLevelMappingOld(int x, int y){
        //System.out.println("CatWindowLevel: setWindowLevelMapFactor");
        
        int dX = x - anchorX;
        double dXmod = (double) (dX * mapConst);
        int winNew = width_old + (int)Math.round(dXmod);
        if((int)Math.abs((x - anchorX)) > 8)
            anchorX = x;
        
        int dY = y - anchorY;
        int dYmod = (int) (dY * mapConst);
        int levNew = center_old + (int)Math.round(dYmod);
        if((int)Math.abs((y - anchorY)) > 8)
            anchorY = y;
        
        System.out.println();
            System.out.println();
            System.out.println("winOld = " + winOld);
            System.out.println("levOld = " + levOld);
            System.out.println("win = " + x);
            System.out.println("lev = " + x);
            System.out.println("anchorX = " + anchorX);
            System.out.println("anchorY = " + anchorY);
            System.out.println("dX = " + dX);
            System.out.println("dY = " + dY);
            System.out.println("dXmod = " + dXmod);
            System.out.println("dYmod = " + dYmod);
            System.out.println("winNew = " + winNew);
            System.out.println("levNew = " + levNew);
         
        width_old = winNew;
        center_old = levNew;
        
        setWindowLevel(winNew, levNew);
    }*/
    
    /**
     * Set the canvas control mode.
     * @param mode the <code>CanvasControlMode<code/> constant.
     */
    public void setCanvasControlMode(int mode){
        canvasControlMode = mode;
    }
    
    /**
     * Get the canvas control mode.
     * @return canvasControlMode <code>CanvasControlMode<code/> constant.
     */
    public int getCanvasControlMode(){
        return canvasControlMode;
    }
}

/** Sets the window width and level.
 * This methods calls the createByteLookupTable()to create a lookup
 * table for the window and level values specified in the input. Using this lookup
 * table, creates a LookupOp object which is used for applying the
 * lookup table to the image displayed on the canvas.This method then displays
 * the resulting image on the canvas.
 * @see createByteLookupTable()
 * @param win width of the window.
 * @param lev the level.
 */
    /*
    public void setWindowLevel_NOTINUSE(int win, int lev) {
        System.out.println("CatWindowLevel.setWindowLevel");
        ByteLookupTable blut = createByteLookupTable(win, lev);
        LookupOp lkop = new LookupOp(blut, null);
        wlImage = lkop.filter(canvas.getOffScreenImage() , null);
        canvas.setDisplayImage(wlImage);
    }*/
    /*
    public void setWindowLevel(int win, int lev){
        winOld = win;
        levOld = lev;
     
        //System.out.println("CatWindowLevel: setWindowLevel");
     
        // Set the values for later update of the canvas.
        canvas.setWindowLevelValues(win, lev);
     
        //ShortLookupTable blut = createShortLookupTable(win, lev);
        //ByteLookupTable blut = createByteLookupTable(win, lev);
     
        createByteLookupTable(win, lev, lutArraySize);
        //LookupOp lookupOp = new LookupOp(blut, null);
        //System.out.println("Window " + win);
        //System.out.println("Level " + lev);
     
        BufferedImage offScrImage = canvas.getOffScreenImage();
        //new CatImageInfo().show(offScrImage);
     
        //BufferedImage wlImage = lookupOp.filter(offScrImage, null);
        //new CatImageInfo().show(wlImage);
        //canvas.setDisplayImage(wlImage);
     
        // test
        int LUTSIZE = 65536;  // 1024, 4096, 65536
        //for(int i=4000;i<=6000;i++)
        //    System.out.println("i=" + i + " lutR=" + lutR[i]);
        IndexColorModel icm = new IndexColorModel(16, LUTSIZE, lutR,lutG,lutB);
        //IndexColorModel icm = new IndexColorModel(16, LUTSIZE, lutR,lutG,lutB);
        BufferedImage finalImage = new BufferedImage(icm, offScrImage.getRaster(), false, null);
        //new CatImageInfo().show(finalImage);
     
        canvas.setDisplayImage(finalImage);
        //canvas.setOffScreenImage(finalImage);
        // end test
     
        //BufferedImage img = canvas.getDisplayImage();
        //new CatImageInfo().show(img);
        //int b=12;
        //canvas.setOffScreenImage(wlImage);
        //canvas.applyWindowLevel();
        //img = canvas.getOffScreenImage();
        /*
        img = canvas.getDisplayImage();
        wlImage = lookupOp.filter(img, null);
        canvas.setDisplayImage(wlImage);
        //canvas.setWLImage(wlImage);
        //canvas.setOffScreenImage(wlImage);
     */

        /*
        if(firstTime){
            firstTime=false;
            img = canvas.getOffScreenImage();
            wlImage = lookupOp.filter(img, null);
            canvas.setWLImage(img);
        }
        else{
            wlImage = lookupOp.filter(canvas.getWLImage(), null);
        }
        canvas.setOffScreenImage(wlImage);
        canvas.applyWindowLevel();
         */
    /*
    }*/