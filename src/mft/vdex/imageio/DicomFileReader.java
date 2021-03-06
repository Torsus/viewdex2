/* @(#) StudyLoader.java 02/14/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */
package mft.vdex.imageio;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import org.dcm4che3.data.Attributes;

/**
 * @author sune
 */
/**
 * The <code>DicomFileReader</code> read DICOM image file attributes
 * and pixel data. Convert if needed the <code>BufferedImage</code> object
 * for use by internal data structures. Set up parameters for image rendering.
 */
public class DicomFileReader {

    protected BufferedImage imgBuffered, imgBuffered2;
    protected PlanarImage planarImage_transformed;
    protected RenderedImage imgRendered, imgRendered2;
    protected RenderedOp imgRenderedOp;
    byte[] lutR8, lutG8, lutB8;
    byte[] lutR10, lutG10, lutB10;
    byte[] lutR12, lutG12, lutB12;
    byte[] lutR14, lutG14, lutB14;
    byte[] lutR15, lutG15, lutB15;
    byte[] lutR16, lutG16, lutB16;

    // dicom tags
    private org.dcm4che3.data.Attributes attributes;
    private int[] windowWidth_t;
    private int[] windowCenter_t;

    // transformation status
    private boolean modalityLUTSequenceStatus;
    private boolean voiLUTSequenceStatus;
    private boolean modalityLUTRescaleStatus;
    private boolean voiLUTWindowCenterWindowWidthExist;
    private boolean identityStatus;
    private boolean windowCenterOffsetStatus;

    private int[][] imageStat = new int[1][13];
    long msecs, startTime;
    private boolean wlMultipleValuesExist = false;
    public DicomFileAttributeReader attributeReader = null;
    DicomFileImageReader dicomFileImageBufferReader = null;
    BufferedImageFactory bufferedImageFactory = null;

    // Fix when windowCenter is defined as 0.5
    boolean windowCenterFloatValueExist = false;

    public DicomFileReader() {
        initLookupTables();
        attributeReader = new DicomFileAttributeReader();
        dicomFileImageBufferReader = new DicomFileImageReader();
        bufferedImageFactory = new BufferedImageFactory();
    }

    /**
     * initialisation lookup tables.
     */
    private void initLookupTables() {
        lutR8 = new byte[256];
        lutG8 = new byte[256];
        lutB8 = new byte[256];

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
    }

    /**
     * loadImage
     * @param fpath
     * @param stackType
     * @param imageCnt
     */
    public void loadImage(File fpath, int stackType, int imageCnt) {
        //String filterExtension[] = {".dcm"};
        String filterExtension[] = {".dcm", ".IMA"};
        Raster raster;

        //System.out.println("DicomFileReader.loadImage");
        if (fpath.getName().endsWith(filterExtension[0])
                || fpath.getName().endsWith(filterExtension[1])) {
            try {
                //msecs = System.currentTimeMillis();
                //startTime = System.nanoTime();
                
                attributeReader.readAttributes(fpath);
                attributes = attributeReader.getAttributes();
                raster = dicomFileImageBufferReader.readFileImageRaster(fpath, imageCnt);
                bufferedImageFactory.setAttributeReader(attributeReader);
                
                //System.out.println("Time to loadImage : " + ((System.nanoTime() - startTime)) / 1000000);
                setWindowCenterValueFloatStatus(attributeReader.att.getWindowCenter_str());
                setWWWCStatus();
                setTransformStatus();
                PlanarImage planarImage = createPlanarImage(raster);
                planarImage_transformed = transformPlanarImage(planarImage);
            } catch (IOException e) {
                System.err.println("DicomFileReader.loadImage : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a BufferedImage and wrap to PlanarImage. Convert from 2's
     * complement if needed. Store pixel data as DataBufferUShort. To handle
     * addition of rescale intercept an offset is added to the pixel data. pv' =
     * pv + 2^(bitsstored -1). Offset is not added if bitsStored = 16 bits and
     * PixelInterpretation = 0).
     *
     * @param raster
     * @return imgPlanar
     */
    PlanarImage createPlanarImage(Raster raster) {
        PlanarImage imgPlanar = null;
        
        if (attributeReader.att.getPixelRepresentation() == 1
                && attributeReader.att.getBitsAllocatedValue() == 16) {
            BufferedImage bi = bufferedImageFactory.get16bitBuffImage2(raster, getImageStats(),
                    attributeReader.att.getBitsAllocatedValue());
            imgPlanar = PlanarImage.wrapRenderedImage(bi);
        } else {
            if (attributeReader.att.getPixelRepresentation() == 0
                    && attributeReader.att.getBitsAllocatedValue() == 16) {
                BufferedImage bi = bufferedImageFactory.get16bitBuffImage(raster);
                imgPlanar = PlanarImage.wrapRenderedImage(bi);
            } else {
                if (attributeReader.att.getPixelRepresentation() == 0
                        && attributeReader.att.getBitsAllocatedValue() == 8
                        && !attributeReader.att.getPhotoMetricInterpretation().equalsIgnoreCase("RGB")) {
                    BufferedImage bi = bufferedImageFactory.get8bitBuffImage(raster);
                    imgPlanar = PlanarImage.wrapRenderedImage((RenderedImage) bi);
                } else {
                    if (attributeReader.att.getPixelRepresentation() == 1
                            && attributeReader.att.getBitsAllocatedValue() == 12) {
                        BufferedImage imgBuffered4 = bufferedImageFactory.imageMod3(imgBuffered);
                        imgPlanar = PlanarImage.wrapRenderedImage(imgBuffered4);
                    } else {
                        if (attributeReader.att.getPixelRepresentation() == 0
                                && attributeReader.att.getBitsAllocatedValue() == 8
                                && attributeReader.att.getPhotoMetricInterpretation().equalsIgnoreCase("RGB")) {
                            BufferedImage bi = bufferedImageFactory.get8bitBuffImage(raster);
                            imgPlanar = PlanarImage.wrapRenderedImage(bi);
                        }
                    }
                }
            }
        }
        return imgPlanar;
    }
    
    /**
     * Set WindowWidth and WindowCenter status.  
     */
    private void setWWWCStatus() {
        wlMultipleValuesExist = false;
        int[] windowWidth = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter = attributeReader.att.getWindowCenter_int_array();

        if (windowWidth != null && windowCenter != null
                && windowWidth.length > 1 && windowCenter.length > 1) {
            wlMultipleValuesExist = true;
        }
    }

    /**
     * setTransformStatus Set the Modality LUT, VOI LUT and Identity status.
     * Used for the grey scale transformation pipeline.
     *
     * Modality LUT Transformation 0028,3000 Modality LUT Sequence 0028,1052
     * Rescale Intercept 0028,1053 Rescale Slope 0028,1054 Rescale Type
     *
     * VOI LUT Transformation 0028,3010 VOI LUT Sequence 0028,1050 Window Center
     * 0028,1051 Window Width 0028,1055 Window Center & Width Explanation
     * 0028,1056 VOI LUT Function
     *
     * Identity Windows setting defined by Bits stored.
     */
    private void setTransformStatus() {
        int[] windowWidth = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter = attributeReader.att.getWindowCenter_int_array();
        String modalityLUTSequence = attributeReader.att.getModalityLUTSequenceString();
        short[] lutDescriptor = attributeReader.att.getLutDescriptorValue();
        int modalityLUTType = 0;
        short[] lutData = attributeReader.att.getLUTDataValue();
        double rescaleIntercept = attributeReader.att.getRescaleInterceptValue();
        double rescaleSlope = attributeReader.att.getRescaleSlopeValue();
        String voiLUTSequence = attributeReader.att.getVOILUTSequenceValue();

        modalityLUTSequenceStatus = false;
        modalityLUTRescaleStatus = false;
        voiLUTSequenceStatus = false;
        voiLUTWindowCenterWindowWidthExist = false;
        identityStatus = false;
        windowCenterOffsetStatus = false;

        // modalityLUTSequence   NOT TESTED
        if (modalityLUTSequence != null
                && lutDescriptor != null
                && modalityLUTType != Integer.MIN_VALUE
                && lutData != null) {
            modalityLUTSequenceStatus = true;
        }

        // rescaleIntercept
        if ((rescaleIntercept == 0.0) && (rescaleSlope == 1.0)) {
            rescaleIntercept = Double.MIN_VALUE;
            rescaleSlope = Double.MIN_VALUE;
        }

        if (rescaleIntercept != Double.MIN_VALUE
                && rescaleSlope != Double.MIN_VALUE) {
            modalityLUTRescaleStatus = true;
        }

        // VOILUTSequence
        if (voiLUTSequence != null
                && lutDescriptor != null
                && lutData != null) {
            voiLUTSequenceStatus = true;
        }

        // centerWidth
        //if(windowCenter[0] != Integer.MIN_VALUE &&
        //      windowWidth[0] != Integer.MIN_VALUE){
        //voiLUTCenterWidthStatus = true;
        //}
        if (windowCenter != null && windowCenter[0] != Integer.MIN_VALUE
                && windowWidth != null && windowWidth[0] != Integer.MIN_VALUE) {
            voiLUTWindowCenterWindowWidthExist = true;
        }

        // Identity
        //if(modalityLUTSequenceStatus == false)
        //  cwStatus = cw.identity;
        //if(voiLUTSequenceStatus == false)
        //    cwStatus = cw.identity;
    }

   
    /**
     * stat[0][0] pos_min
     * stat[0][1] pos_max
     * stat[0][2] neg_min
     * stat[0][3] neg_max
     * stat[0][4] range_neg
     * stat[0][5] range_pos
     * stat[0][6] range_tot
     * stat[0][7] mask1
     * stat[0][8] min
     * stat[0][9] max
     * stat[0][10] use mask1 status
     * stat[0][11] signed
     * stat[0][12] no windowWidth offset
     *
     * Create windowWidth and windowCenter values for the initial display of
     * image. If window center and window width is not defined in DICOM
     * attributes, calculate and set the values. Apply Rescale Intercept &
     * Rescale Slope.
     * @param imgPlanar
     * @return 
     */
    private PlanarImage transformPlanarImage(PlanarImage imgPlanar) {
        PlanarImage pi = imgPlanar;
        //boolean identity = false;
        double[][] stat;
        double min, max;
        int[] windowWidth = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter = attributeReader.att.getWindowCenter_int_array();
        double[] windowWidthFloat = attributeReader.att.getWindowWidth_double_array();
        double[] windowCenterFloat = attributeReader.att.getWindowCenter_double_array();
        String photometricInterpretation = attributeReader.att.getPhotoMetricInterpretation();
        int bitsStored = attributeReader.att.getBitsStoredValue();
        double rescaleIntercept = attributeReader.att.getRescaleInterceptValue();
        short[] lutDescriptor = attributeReader.att.getLutDescriptorValue();

        stat = computeExtrema(pi, null);
        min = stat[0][0];
        max = stat[1][0];
        //System.out.println("StudyLoader.transform: min = " + min);
        //System.out.println("StudyLoader.transform: max = " + max);

        imageStat[0][8] = (int) min;
        imageStat[0][9] = (int) max;
        imageStat[0][6] = (int) (max - min);
        //int mask1 = imageStats[0][7];

        if (modalityLUTSequenceStatus && !modalityLUTRescaleStatus) {
            // PlanarImage pi = imageMod7(img);
            windowWidth_t[0] = (int) lutDescriptor[0];
            windowCenter_t[0] = (int) lutDescriptor[1] + (lutDescriptor[0] / 2) + imageStat[0][7];
            windowCenterOffsetStatus = true;
        }

        if (modalityLUTSequenceStatus && modalityLUTRescaleStatus) {
            BufferedImage img = pi.getAsBufferedImage();
            BufferedImage img2 = applyRescaleSlopeIntercept2(img);
            //new ImageInfoBuffered().show(img2);
            //new ImageInfoBuffered().getImageStats(img2);
            pi = PlanarImage.wrapRenderedImage(img2);
            windowCenterOffsetStatus = true;
        }

        if (modalityLUTRescaleStatus) {
            BufferedImage img = pi.getAsBufferedImage();
            //BufferedImage img2 = applyRescaleSlopeIntercept(img);
            BufferedImage img2 = applyRescaleSlopeIntercept2(img);
            // test 2014-04-28
            //BufferedImage img2 = applyRescaleSlopeIntercept3(img);
            //new ImageInfoBuffered().show(img2);
            //new ImageInfoBuffered().getImageStats(img2);
            pi = PlanarImage.wrapRenderedImage(img2);
            windowCenterOffsetStatus = true;
        }

        if (voiLUTWindowCenterWindowWidthExist) {
            // init
            if (windowWidth != null) {
                int len = windowWidth.length;
                windowWidth_t = new int[len];
            }

            if (windowCenter != null) {
                int len = windowCenter.length;
                windowCenter_t = new int[len];
            }

            for (int i = 0; i < windowWidth.length; i++) {
                windowWidth_t[i] = windowWidth[i];
            }

            for (int i = 0; i < windowCenter.length; i++) {
                windowCenter_t[i] = windowCenter[i] + imageStat[0][7];
            }

            //windowCenter_m = (int) ((windowCenter - rescaleIntercept) / rescaleSlope) + imageStats[0][7];
            windowCenterOffsetStatus = true;
        }

        // Fix for MR image with Window Center defined as 0.5
        // Test image: IM_0128.dcm
        // Rescale Intercept: 0
        // Rescale Slope:  1,0/4095 -> 0.000244...
        // Window Width: 1
        // Window Center: 0.5
        // ViewDEX dcm4che library does't read Rescale Intercept & Rescale Slope ?
        //
        // New test... 20180907 zzzzz
        // This is a fix for MR images like the following or alike
        // 0028,1050 Window Center: 0.5
        // 0028,1051 Window Width: 1
        // 0028,1052 Rescale Intercept: 0
        // 0028,1053 Rescale Slope: 0,00024... 1/4096
        //
        // The dcm4che library does't read Rescale Slope! Obsolete.
        //
        if (voiLUTWindowCenterWindowWidthExist) {
            if (attributeReader.att.getModality().equalsIgnoreCase("MR")
                    && windowWidthFloat[0] == 1
                    && windowCenterFloat[0] == 0.5
                    && rescaleIntercept == Double.MIN_VALUE) //&& rescaleSlope != 0)
            {
                //windowWidth_t[0] = imageStats[0][6];
                //windowCenter_t[0] = imageStats[0][6] / 2;

                if (bitsStored == 16) {
                    windowWidth_t[0] = 65535;
                    windowCenter_t[0] = 65535 / 2;
                }

                if (bitsStored == 12) {
                    windowWidth_t[0] = 4095;
                    windowCenter_t[0] = 4095 / 2;
                }

                if (bitsStored == 8) {
                    windowWidth_t[0] = 255;
                    windowCenter_t[0] = 255 / 2;
                }
            }
            //System.out.println("StudyLoader.transform: winddowWidth_t = " + imageStats[0][6]);
            //System.out.println("StudyLoader.transform: windowCenter_t = " + imageStats[0][6] / 2);
        }

        // Test for Modality MR and PhotometricInterpretation RGB
        // Set of w/l to full window
        // Fix for images "angelica/MR-bilder/Snittbilder/IM_0068.dcm - IM_00_0102.dcm",
        // where WC/WW is 5008/8706 
        if (voiLUTWindowCenterWindowWidthExist) {
            if ((attributeReader.att.getModality().equalsIgnoreCase("MR")
                    || attributeReader.att.getModality().equalsIgnoreCase("XA"))
                    && photometricInterpretation.equalsIgnoreCase("RGB")) {
                //windowWidth_t[0] = imageStats[0][6];
                //windowCenter_t[0] = imageStats[0][6] / 2;

                if (bitsStored == 16) {
                    windowWidth_t[0] = 65535;
                    windowCenter_t[0] = 65535 / 2;
                }

                if (bitsStored == 12) {
                    windowWidth_t[0] = 4095;
                    windowCenter_t[0] = 4095 / 2;
                }

                if (bitsStored == 8) {
                    windowWidth_t[0] = 255;
                    windowCenter_t[0] = 255 / 2;
                }
            }
            //System.out.println("StudyLoader.transform: winddowWidth_t = " + imageStats[0][6]);
            //System.out.println("StudyLoader.transform: windowCenter_t = " + imageStats[0][6] / 2);
        }

        if (voiLUTSequenceStatus && voiLUTWindowCenterWindowWidthExist) {
            //PlanarImage pi2 = applyVOILUTSequence(pi);

            // init
            if (windowWidth != null) {
                int len = windowWidth.length;
                windowWidth_t = new int[len];
            }

            if (windowCenter != null) {
                int len = windowCenter.length;
                windowCenter_t = new int[len];
            }

            for (int i = 0; i < windowWidth.length; i++) {
                windowWidth_t[i] = windowWidth[i];
            }

            for (int i = 0; i < windowCenter.length; i++) {
                windowCenter_t[i] = windowCenter[i] + imageStat[0][7];
            }
        }

        if (voiLUTSequenceStatus && !voiLUTWindowCenterWindowWidthExist) {
            //PlanarImage pi2 = applyVOILUTSequence(pi);
            // lutDescriptor[0] -> number of entries in LUT
            // lutDescriptor[1] -> first input value mapped

            // init
            windowWidth_t = new int[1];
            windowCenter_t = new int[1];

            windowWidth_t[0] = (int) lutDescriptor[0];
            windowCenter_t[0] = (int) lutDescriptor[1] + (lutDescriptor[0] / 2) + imageStat[0][7];
            windowCenterOffsetStatus = true;
        }

        // Identity transformation
        if (!voiLUTSequenceStatus && !voiLUTWindowCenterWindowWidthExist) {
            // init
            windowWidth_t = new int[1];
            windowCenter_t = new int[1];

            //stat = computeExtrema(pi, null);
            //min = stat[0][0];
            //max = stat[1][0];
            imageStat[0][6] = (int) Math.abs(max - min);
            windowWidth_t[0] = (int) Math.abs(max - min);
            windowCenter_t[0] = (int) (min + ((double) ((Math.abs(max - min)) / 2)));
            windowCenterOffsetStatus = true;
            //windowWidth_m = (int) ((max - min) * rescaleSlope);
            //windowCenter_m = (int) ((((max + min) / 2) * rescaleSlope));
        }
        return pi;
    }

    /**
     * computeExtrema
     * @param img
     * @param roi
     * @return minmax
     */
    private double[][] computeExtrema(PlanarImage img, ROIShape roi) {
        if (img == null) {
            return null;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(roi);
        pb.add(1);
        pb.add(1);
        RenderedOp op = JAI.create("extrema", pb);
        double[][] minmax = (double[][]) op.getProperty("extrema");
        return minmax;
    }
    
    /**
     * applyRescaleSlopeIntercept2
     * @param img
     * @return imgBuf
     */
    private BufferedImage applyRescaleSlopeIntercept2(BufferedImage img) {
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        int offset = 0;
        int width = img.getWidth();
        int height = img.getHeight();
        int bitsStored_l = attributeReader.att.getBitsStoredValue();
        int pixelRepresentation_l = attributeReader.att.getPixelRepresentation();
        double rescaleSlope_l = attributeReader.att.getRescaleSlopeValue();
        double rescaleIntercept_l = attributeReader.att.getRescaleInterceptValue();

        int samples[][] = getPixelSamples(img);

        // mask
        // bitStored = 12  -> mask1 = 0x800 (2048)
        // bitStored = 16  -> mask1 = 0x8000 (32768)
        int mask = 1 << bitsStored_l - 1;

        if (imageStat[0][7] == 0) {
            offset = mask;
            imageStat[0][7] = mask;
        } else if (pixelRepresentation_l == 1) {
            offset = 0;
        } else if (pixelRepresentation_l == 0 && bitsStored_l == 16) {
            offset = 0;
        }

        short[] data2 = new short[width * height];
        for (int i = 0; i < samples[0].length; i++) {
            data2[i] = (short) ((samples[0][i] * rescaleSlope_l) + rescaleIntercept_l + offset);
        }

        DataBufferUShort short_db = new DataBufferUShort(data2, data2.length);
        int[] bandOffsets = new int[1];
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
                img.getWidth(), 1, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuf = new BufferedImage(icm2, wr2, false, null);

        return imgBuf;
    }

    /**
     * getPixelSamples
     * @param img
     * @return
     */
    public static int[][] getPixelSamples(BufferedImage img) {
        WritableRaster wr = img.getRaster();
        Dimension size = new Dimension(img.getWidth(), img.getHeight());
        return getPixelSamples(wr, size);
    }

    /**
     * getPixelSamples
     * @param raster
     * @param imageSize
     * @return pixel
     */
    public static int[][] getPixelSamples(Raster raster, Dimension imageSize) {
        if ((raster == null) || (imageSize == null)) {
            return null;
        }
        SampleModel sm = raster.getSampleModel();
        DataBuffer db = raster.getDataBuffer();
        int imageWidth = (int) imageSize.getWidth();
        int imageHeight = (int) imageSize.getHeight();
        int totalPix = imageWidth * imageHeight;
        int sample[][] = new int[totalPix][];
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                int pix[] = null;
                sample[i * imageWidth + j] = sm.getPixel(j, i, pix, db);
            }
        }
        int l = sample.length;
        int l2 = sample[0].length;
        int pixel[][] = new int[sample[0].length][totalPix];
        for (int i = 0; i < pixel.length; i++) {
            for (int j = 0; j < totalPix; j++) {
                pixel[i][j] = sample[j][i];
            }
        }
        return pixel;
    }

    /**
     * getAttributes
     * @return attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * getWindowWidth
     * @return windowWidth_t
     */
    public int[] getWindowWidth() {
        return windowWidth_t;
    }

    /**
     * getWindowCenter
     * @return windowCenter_t
     */
    public int[] getWindowCenter() {
        return windowCenter_t;
    }

    /**
     * getWLMultipleValuesExist
     * @return wlMultipleValuesExist
     */
    public boolean getWLMultipleValuesExist() {
        return wlMultipleValuesExist;
    }

    /**
     * getLoadedPlanarImage
     * @return planarImage_transformed
     */
    public PlanarImage getLoadedPlanarImage() {
        return planarImage_transformed;
    }

    /**
     * getModalityLUTSequenceStatus
     * @return modalityLUTSequenceStatus
     */
    public boolean getModalityLUTSequenceStatus() {
        return modalityLUTSequenceStatus;
    }

    /**
     * getVoiLUTSequenceStatus
     * @return voiLUTSequenceStatus
     */
    public boolean getVoiLUTSequenceStatus() {
        return voiLUTSequenceStatus;
    }

    /**
     * getRescaleSlopeInterceptStatus
     * @return modalityLUTRescaleStatus
     */
    public boolean getRescaleSlopeInterceptStatus() {
        return modalityLUTRescaleStatus;
    }

    /**
     * getCenterWidthStatus
     * @return voiLUTWindowCenterWindowWidthExist
     */
    public boolean getCenterWidthStatus() {
        return voiLUTWindowCenterWindowWidthExist;
    }

    /**
     * getIdentityStatus
     * @return identityStatus
     */
    public boolean getIdentityStatus() {
        return identityStatus;
    }

    /**
     * getWindowCenterOffsetStatus
     * @return windowCenterOffsetStatus
     */
    public boolean getWindowCenterOffsetStatus() {
        return windowCenterOffsetStatus;
    }

    /**
     * setImageStats
     * @param stats
     */
    private void setImageStats(int[][] stats) {
        imageStat = stats;
    }

    /**
     * getImageStats
     * @return imageStats
     */
    public int[][] getImageStats() {
        return imageStat;
    }

    /**
     * Fix for MR images like the following or alike 0028,1050 Window Center:
     * 0.5 0028,1051 Window Width: 1 0028,1052 Rescale Intercept: 0 0028,1053
     * Rescale Slope: 0,00024... 1/4096 Obsolete remove?
     * windowCenterFloatValueExist =
     * imageLoader.getWindowCenterFloatValueExist(); new implementation Find out
     * if windowCenter value is typed as a float value.
     *
     * @param str
     */
    public void setWindowCenterValueFloatStatus(String str) {
        float[] buf = null;

        if (str == null) {
            return;
        }

        //str = "1\\2\\0.7\\135.2";
        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new float[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = Float.parseFloat(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM:setWindowCenterMultipleDataValue: NumberFormatException");
                }
            }
        }

        for (int i = 0; i < buf.length; i++) {
            if (buf[i] - (int) buf[i] != 0) {
                windowCenterFloatValueExist = true;
            }
        }
    }

    /**
     * printWWWCValues
     */
    private void printWWWCValues() {
        int[] windowWidth_l = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter_l = attributeReader.att.getWindowCenter_int_array();

        if (windowWidth_l != null) {
            String str = null;
            for (int i = 0; i < windowWidth_l.length; i++) {
                if (i == 0) {
                    str = "" + windowWidth_l[i];
                } else {
                    str = str + ", " + windowWidth_l[i];
                }
            }
            System.out.println("WindowWidth_l =  " + str);
        } else {
            System.out.println("WindowWidthMultiple =  ");
        }

        if (windowCenter_l != null) {
            String str2 = null;
            for (int i = 0; i < windowCenter_l.length; i++) {
                if (i == 0) {
                    str2 = "" + windowCenter_l[i];
                } else {
                    str2 = str2 + ", " + windowCenter_l[i];
                }
            }
            System.out.println("WindowCenter_l =  " + str2);
        } else {
            System.out.println("WindowCenterMultiple =  ");
        }
    }
}
