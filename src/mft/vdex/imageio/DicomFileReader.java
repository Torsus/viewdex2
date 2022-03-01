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
 * The <code>DicomFileReader</code> class read DICOM image files. Read
 * Attributes and pixel data. Convert if needed the <code>BufferedImage</code>
 * image object for the internal data structure. Set up image rendering
 * parameters.
 */
public class DicomFileReader {
    protected BufferedImage imgBuffered, imgBuffered2;
    protected PlanarImage imgPlanar20;
    protected RenderedImage imgRendered, imgRendered2;
    protected RenderedOp imgRenderedOp;
    byte[] lutR8, lutG8, lutB8;
    byte[] lutR10, lutG10, lutB10;
    byte[] lutR12, lutG12, lutB12;
    byte[] lutR14, lutG14, lutB14;
    byte[] lutR15, lutG15, lutB15;
    byte[] lutR16, lutG16, lutB16;

    // dicom tags
  
    private String photometricInterpretation;
    private String modality;
    private org.dcm4che3.data.Attributes attributes;

    // Dicom Data Element Tags
    private int bitsStored;
    private short[] lutDescriptor;              // LUT Descriptor        (0028,3002)
    private double rescaleIntercept;            // Rescale Intercept     (0028,1052)
    private int[] windowWidth;                  // Window Width          (0028.1051)
    private int[] windowCenter;                 // Window Center         (0028.1050)
    private double[] windowWidthFloat;          // Window Width          (0028.1051)
    private double[] windowCenterFloat;         // Window Center         (0028.1050)
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
    DicomFileImageDataReader dicomFileImageDataReader = null;
    DicomFileImageDataReader2 dicomFileImageDataReader2 = null;
    DicomFileImageReader dicomFileImageBufferReader = null;
    BufferedImageFactory bufferedImageFactory = null;

    // Fix when windowCenter is defined as 0.5
    boolean windowCenterFloatValueExist = false;
    
    public DicomFileReader() {
        initLookupTables();
        attributeReader = new DicomFileAttributeReader();
        dicomFileImageDataReader = new DicomFileImageDataReader();
        dicomFileImageDataReader2 = new DicomFileImageDataReader2();
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
     * Load image file
     * @param fpath
     * @param stackType
     * @param imageCnt
     */
    public void loadImage(File fpath, int stackType, int imageCnt) {
        //String filterExtension[] = {".dcm"};
        String filterExtension[] = {".dcm", ".IMA"};
        PlanarImage imgPlanar = null;
        PlanarImage imgPlanar2 = null;
        Raster raster;

        //System.out.println("DicomFileReader.loadImage");
        if (fpath.getName().endsWith(filterExtension[0])
                || fpath.getName().endsWith(filterExtension[1])) {
            try {
                //msecs = System.currentTimeMillis();
                //startTime = System.nanoTime();
                //ImageLoaderDICOM imageLoader = new ImageLoaderDICOM();
                //imageLoader = new ImageLoaderDICOM();

                //DicomFileReader dicomFileReader;
                //dicomFileReader = new DicomFileReader();
                attributeReader.readAttributes(fpath);
                attributes = attributeReader.getAttributes();
                raster = dicomFileImageBufferReader.readFileImageRaster(fpath, imageCnt);
                bufferedImageFactory.setAttributeReader(attributeReader);
                
                //imgBuffered = bufferedImageFactory.get16bitBuffImage(raster);
                //imgBuffered = dicomFileImageDataReader.readImageFromDicomInputStream(fpath);
                //imgBuffered = dicomFileImageDataReader2.readAsDicomImage(fpath, 1);
                //System.out.println("DicomFileReader.loadImage readImageFromDicomInputStream 1 :" + imgBuffered.toString());
                //System.out.println("DicomFileReader.loadImage Object size(1) 1: " + ObjectSizeCalculator.getObjectSize(imgBuffered));
                /*
                SampleModel sm = imgBuffered.getSampleModel();
                WritableRaster wr = imgBuffered.getRaster();
                DataBuffer db = wr.getDataBuffer();
                ColorModel cm = imgBuffered.getColorModel();
                //short[] data = ((DataBufferUShort) db).getData();
                short[] data_b = ((DataBufferUShort) db).getData();
                int paddingValue = Integer.MIN_VALUE;
                int paddingValue2 = Integer.MIN_VALUE;
                int width = imgBuffered.getWidth();
                int height = imgBuffered.getHeight();
                int totalPix = width * height;
                int aa = 10;
                 */
                //System.out.println("Time loadImage 11: " + ((System.nanoTime() - startTime)) / 1000000);
               

                // This is a fix for MR images like the following or alike
                // 0028,1050 Window Center: 0.5
                // 0028,1051 Window Width: 1
                // 0028,1052 Rescale Intercept: 0
                // 0028,1053 Rescale Slope: 0,00024... 1/4096
                // Obsolite remove (2022, sune)
                // windowCenterFloatValueExist = imageLoader.getWindowCenterFloatValueExist();
                // new implementation
                setWindowCenterValueFloatStatus(attributeReader.att.getWindowCenter_str());
                
                // Used by setTransformStatus() ----> need to be fixed
                windowWidthFloat = attributeReader.att.getWindowWidth_double_array();
                windowCenterFloat = attributeReader.att.getWindowCenter_double_array();
                photometricInterpretation = attributeReader.att.getPhotoMetricInterpretation();
                modality = attributeReader.att.getModality();
                bitsStored = attributeReader.att.getBitsStoredValue();
                rescaleIntercept = attributeReader.att.getRescaleInterceptValue();
                lutDescriptor = attributeReader.att.getLutDescriptorValue();
                windowWidth = attributeReader.att.getWindowWidth_int_array();
                windowCenter = attributeReader.att.getWindowCenter_int_array();
                
                setWWWCStatus();
                //printWWWCValues();

                // Modality LUT, VOI LUT and Identity transform status.
                setTransformStatus();
                bufferedImageFactory.setTransformStatus(imageStat);

                /*
                * Convert the image. Wrap the image to PlanarImage class.
                * Convert from 2's complement if needed. Store pixeldata
                * as DataBufferUShort. To handle addition of rescale intercept
                * an offset is added to the pixeldata. pv' = pv + 2^(bitsstored -1).
                * (The offset is not added if bitsStored = 16 bits and
                * PixelInterpretation = 0).
                */
                
                /*
                * 0028,0103 Pixel Represetation
                * Enumerated Values: 0 - unsigned integer, 1 - 2's complement
                */
                if (attributeReader.att.getPixelRepresentation() == 0 &&
                        attributeReader.att.getBitsAllocatedValue() == 16) {
                    //BufferedImage imgBuffered3 = imageMod4b(imgBuffered);
                    BufferedImage bi = bufferedImageFactory.get16bitBuffImage(raster);
                    imgPlanar = PlanarImage.wrapRenderedImage(bi);
                }
                
                if (attributeReader.att.getPixelRepresentation()== 0 &&
                        attributeReader.att.getBitsAllocatedValue() == 8
                        && !photometricInterpretation.equalsIgnoreCase("RGB")) {
                    //BufferedImage imgBuf8 = imageMod8Bit(imgBuffered);
                    BufferedImage bi = bufferedImageFactory.get8bitBuffImage(raster);
                    //BufferedImage imgBuf8 = imageModRGB8Bit(imgBuffered);
                    imgPlanar = PlanarImage.wrapRenderedImage((RenderedImage) bi);
                }

                // NOT TESTED
                if (attributeReader.att.getPixelRepresentation() == 1 &&
                        attributeReader.att.getBitsAllocatedValue() == 16) {
                    BufferedImage imgBuffered3 = bufferedImageFactory.imageMod4b(imgBuffered);
                    imgPlanar = PlanarImage.wrapRenderedImage(imgBuffered3);
                }

                if (attributeReader.att.getPixelRepresentation() == 1
                        && attributeReader.att.getBitsAllocatedValue() == 12) {
                    BufferedImage imgBuffered4 = bufferedImageFactory.imageMod3(imgBuffered);
                    imgPlanar = PlanarImage.wrapRenderedImage(imgBuffered4);
                }

                if (attributeReader.att.getPixelRepresentation() == 0 &&
                        attributeReader.att.getBitsAllocatedValue() == 8
                        && photometricInterpretation.equalsIgnoreCase("RGB")) {
                    //BufferedImage imgBuf8 = imageModRGB8Bit(imgBuffered);
                    imgPlanar = PlanarImage.wrapRenderedImage(imgBuffered);
                    //imageTestplanarImage(imgPlanar); // testzzz 201804330
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            transform(imgPlanar);
        }
    }

    /*
     * Set the windowWidth & windowCenter values.
     * The ww and wc are read, by using the dcm4che library.
     * Two different methods are used. The
     * "((DcmMetadata)reader.getStreamMetadata()).getDataset().getString()",
     * and by reading the Dicom image meta data and create a DOM object,
     * modeling that data and using the XPath routines to find the Dicom tags.
     *
     * These methods set the wlMultipleValueExist status and merge the different readings
     * into one datastructure.
     */
    private void setWWWCStatus() {
        wlMultipleValuesExist = false;
        int[] windowWidth_l = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter_l = attributeReader.att.getWindowCenter_int_array();

        if (windowWidth_l != null && windowCenter_l != null
                && windowWidth_l.length > 1 && windowCenter_l.length > 1) {
            wlMultipleValuesExist = true;
        }
    }

    /*
     * Print the ww and wc values.
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

    /**
     * setTransformStatus Set the Modality LUT, VOI LUT and Identity status.
     * Used for the grayscale transformation pipeline.
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
        int[] windowWidth_l = attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter_l = attributeReader.att.getWindowCenter_int_array();
        String modalityLUTSequence_l = attributeReader.att.getModalityLUTSequenceString();
        short[] lutDescriptor_l = attributeReader.att.getLutDescriptorValue();
        int modalityLUTType_l = 0;
        short[] lutData_l = attributeReader.att.getLUTDataValue();
        double rescaleIntercept_l = attributeReader.att.getRescaleInterceptValue();
        double rescaleSlope_l = attributeReader.att.getRescaleSlopeValue();
        String voiLUTSequence_l = attributeReader.att.getVOILUTSequenceValue();
        
        modalityLUTSequenceStatus = false;
        modalityLUTRescaleStatus = false;
        voiLUTSequenceStatus = false;
        voiLUTWindowCenterWindowWidthExist = false;
        identityStatus = false;
        windowCenterOffsetStatus = false;

        // modalityLUTSequence   NOT TESTED
        if (modalityLUTSequence_l != null
                && lutDescriptor_l != null
                && modalityLUTType_l != Integer.MIN_VALUE
                && lutData_l != null) {
            modalityLUTSequenceStatus = true;
        }

        // rescaleIntercept
        // fix 2014-04-28
        if ((rescaleIntercept_l == 0.0) && (rescaleSlope_l == 1.0)) {
            rescaleIntercept_l = Double.MIN_VALUE;
            rescaleSlope_l = Double.MIN_VALUE;
        }

        if (rescaleIntercept_l != Double.MIN_VALUE
                && rescaleSlope_l != Double.MIN_VALUE) {
            modalityLUTRescaleStatus = true;
        }

        // VOILUTSequence
        if (voiLUTSequence_l != null
                && lutDescriptor_l != null
                && lutData_l != null) {
            voiLUTSequenceStatus = true;
        }

        // centerWidth
        //if(windowCenter[0] != Integer.MIN_VALUE &&
        //      windowWidth[0] != Integer.MIN_VALUE){
        //voiLUTCenterWidthStatus = true;
        //}
        if (windowCenter_l != null && windowCenter_l[0] != Integer.MIN_VALUE
                && windowWidth_l != null && windowWidth_l[0] != Integer.MIN_VALUE) {
            voiLUTWindowCenterWindowWidthExist = true;
        }

        // Identity
        //if(modalityLUTSequenceStatus == false)
        //  cwStatus = cw.identity;
        //if(voiLUTSequenceStatus == false)
        //    cwStatus = cw.identity;
    }

    //stat[0][0] pos_min
    //stat[0][1] pos_max
    //stat[0][2] neg_min
    //stat[0][3] neg_max
    //stat[0][5] range_pos
    //stat[0][4] range_neg
    //stat[0][6] range_tot
    //stat[0][7] mask1
    //stat[0][8] min
    //stat[0][9] max
    //stat[0][10] use mask1 status
    //stat[0][11] signed
    //stat[0][12] no windowWidth offset
    /**
     * Create the windowWidth and windowCenter values for the initial display of
     * the image. If the center and width is not defined in the DICOM file
     * calculate and set the values. Apply Rescale Intercept & Rescale Slope.
     */
    private void transform(PlanarImage imgPlanar) {
        PlanarImage pi = imgPlanar;
        boolean identity = false;
        double[][] stat;
        double min, max;

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
        // The dcm4che library does't read Rescale Slope!
        //
        if (voiLUTWindowCenterWindowWidthExist) {
            //if (modality.equalsIgnoreCase("MR") && windowCenterFloatValueExist) {
            if (modality.equalsIgnoreCase("MR")
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
            if ((modality.equalsIgnoreCase("MR")
                    || modality.equalsIgnoreCase("XA"))
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
        
        imgPlanar20 = pi;
    }
    
    /**
     * Computing minimum and maximum pixel values.
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
    
    /*
     * applyRescaleSlopeIntercept2
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
     * @return 
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
     * Get Attributes
     * @return attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }
    
    /**
     * Get the window/level multiple values exist status.
     */
    public boolean getWLMultipleValuesExist() {
        return wlMultipleValuesExist;
    }
    
    /**
     * getLoadedPlanarImage
     * @return imgPlanar20
     */
    public PlanarImage getLoadedPlanarImage() {
        return imgPlanar20;
    }
    
    /**
     * Get ModalityLUTSequenceStatus
     *
     * @return modalityLUTSequenceStatus
     */
    public boolean getModalityLUTSequenceStatus() {
        return modalityLUTSequenceStatus;
    }
    
    /**
     * Get the voiLUTSequenceStatus. The metadata is read by using DOM, XPath
     * ...
     */
    public boolean getVoiLUTSequenceStatus() {
        return voiLUTSequenceStatus;
    }
    
    /**
     * Get the rescaleSlopeInterceptStatus
     */
    public boolean getRescaleSlopeInterceptStatus() {
        return modalityLUTRescaleStatus;
    }
    
    /**
     * Get the centerWidthStatus
     */
    public boolean getCenterWidthStatus() {
        return voiLUTWindowCenterWindowWidthExist;
    }
    
    /**
     * Get the identityStatus
     */
    public boolean getIdentityStatus() {
        return identityStatus;
    }
    
    /**
     * Get the windowCenterOffsetStatus
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
     *
     * @return imageStats
     */
    public int[][] getImageStats() {
        return imageStat;
    }

 
    /**
     * Find if windowCenter value is typed as a float value.
     * @param str the DICOM tag (0028,1050)
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

        /*
        for(int i = 0; i < buf.length; i++){
            if(buf[i] % 1 != 0){
                windowCenterValueFloatStatus = true;
         */
    }
}
