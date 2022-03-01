package mft.vdex.imageio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.dcm4che3.data.Attributes;

/**
 * @author sune
 */
public class BufferedImageFactory {
    DicomFileAttributeReader attributeReader;
    private int[][] imageStat = new int[1][13];
    byte[] lutR8, lutG8, lutB8;
    byte[] lutR10, lutG10, lutB10;
    byte[] lutR12, lutG12, lutB12;
    byte[] lutR14, lutG14, lutB14;
    byte[] lutR15, lutG15, lutB15;
    byte[] lutR16, lutG16, lutB16;
    
    public BufferedImageFactory(){
        initLookupTables();
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
     * setAttributes
     * @param attributeReader
     */
    public void setAttributeReader(DicomFileAttributeReader attributeReader){
        this.attributeReader = attributeReader;
    }
    
    /**
     * setTransformStatus
     * @param imageStat 
     */
    public void setTransformStatus(int[][] imageStat){
        this.imageStat = imageStat;
    }
    
    /**
     * Create BufferedImage object from 16 bit Raster
     * @param raster
     * @return
     */
    public BufferedImage get16bitBuffImage(Raster raster) {
        short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();
        ColorModel colorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{16},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT);
        DataBufferUShort db = new DataBufferUShort(pixels, pixels.length);
        WritableRaster outRaster = Raster.createInterleavedRaster(
                db,
                raster.getWidth(),
                raster.getHeight(),
                raster.getWidth(),
                1,
                new int[1],
                null);
        return new BufferedImage(colorModel, outRaster, false, null);
    }

    /**
     * Create BufferedImage object from 8 bit Raster
     * @param raster
     * @return BufferedImage
     */
    public BufferedImage get8bitBuffImage(Raster raster) {
        //short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();
        int height = raster.getHeight();
        int width = raster.getWidth();
        int size = raster.getDataBuffer().getSize();
        int nDataElements = raster.getNumDataElements();
        SampleModel sm = raster.getSampleModel();
        DataBuffer db1 = raster.getDataBuffer();

        int sample[][] = new int[height * width][];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db1);
                sample[i * width + j] = sm.getPixel(j, i, pix, db1);
            }
        }

        short[] short_im_data = new short[width * height];
        for (int i = 0; i < sample.length; i++) {
            short_im_data[i] = (short) sample[i][0];
        }

        //short[] pixels = ((DataBufferUShort)raster.getDataBuffer()).getData();
        //System.exit(5);
        ColorModel colorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{16},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT);
        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        WritableRaster outRaster = Raster.createInterleavedRaster(
                short_db,
                raster.getWidth(),
                raster.getHeight(),
                raster.getWidth(),
                1,
                new int[1],
                null);
        return new BufferedImage(colorModel, outRaster, false, null);
    }

    /**
     * imageMod4
     * @param img
     * @return imgBuffered
     */
    private BufferedImage imageMod4(BufferedImage img) {
        int bitsStored = attributeReader.att.getBitsStoredValue();
        
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();
        short[] data = ((DataBufferUShort) db).getData();
        int paddingValue = Integer.MIN_VALUE;
        int paddingValue2 = Integer.MIN_VALUE;

        int width = img.getWidth();
        int height = img.getHeight();
        int totalPix = width * height;

        // bitStored = 12  -> mask1 = 0x800 (2048)
        // bitStored = 16  -> mask1 = 0x8000 (32768)
        int mask1 = 1 << bitsStored - 1;
        imageStat[0][7] = mask1;

        // bitStored = 12  -> mask25 = 0x7ff (2047)
        // bitStored = 16  -> mask25 = 0x7fff (32767)
        int mask2 = -1 >>> (32 - bitsStored + 1);

        // Pixel Padding Value (0028,0120).
        // The Value Representation of this attribute is determined
        // by the value of the Pixel Represenation. If the value of
        // Pixel Representation is signed the pixelvalues are
        // transported to unsigned before this method.
        // e.g. Pixel Padding value 63 536 (f830) as 2's 2000 (07d0).
        /*if(pixelPaddingValue != Integer.MIN_VALUE){
         int v1 = pixelPaddingValue;
         int v2 = v1 & 0x0000ffff;
         paddingValue = (int) (((~v2 & 0x0000ffff) + 1) * -1.0);
         }*/
        // Replaces the padding values with max negative value.
        // This gives black on the canvas even for big window width values.
        /*for(int i=0;i<data.length;i++){
         if(data[i] == paddingValue){
         //data[i] = (short) (0 - rescaleIntercept);
         data[i] = Short.MIN_VALUE;
         }
         }*/
        // Find the bits that have a 1 in the sign bit.
        int neg_max = Integer.MIN_VALUE;
        int neg_min = Integer.MAX_VALUE;
        short[] data2 = new short[totalPix];
        for (int i = 0; i < data.length; i++) {
            if (data[i] == paddingValue) {
                data[i] = Short.MIN_VALUE;
            }
            int v = data[i] & mask1;
            if (v == mask1) {
                data2[i] = (short) (data[i] & mask2);
                if (data2[i] > neg_max) {
                    neg_max = data2[i];
                }
                if (data2[i] < neg_min) {
                    neg_min = data2[i];
                }
            }
        }

        //printPixelValuesShort(data);
        //printPixelValuesShort(data2);
        //printPixelValuesShort(data, data2);
        //for(int i=0;i<data2.length;i++){
        //data2[i] = (short) (data2[i] - neg_min);
        //  data2[i] = (short) (data2[i]);
        //}
        //printPixelValuesShort(data2);
        // Find the bits that NOT have a 1 in the sign bit.
        // Put this values in a new short data array.
        // Add +max +1.
        int pos_max = Integer.MIN_VALUE;
        int pos_min = Integer.MAX_VALUE;
        short[] data3 = new short[totalPix];
        short[] data4 = new short[totalPix];
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & mask1;
            if (v != mask1) {
                int val = data[i] & mask2;
                if (val > pos_max) {
                    pos_max = val;
                }
                if (val < pos_min) {
                    pos_min = val;
                }
                //data2[i] = (short) (val + max + 1);
                //data2[i] = (short) (val + (neg_max - neg_min) + 1);
                //data2[i] = (short) (val + neg_max);   //ppp
                data2[i] = (short) ((val) + mask1);
            }
        }
        //printPixelValuesShort(data2);

        // new
        short[] data5 = new short[width * height];
        for (int i = 0; i < data2.length; i++) {
            //data5[i] = (short) ((data2[i] * rescaleSlope) + rescaleIntercept);
            data5[i] = (short) (data2[i]);
        }
        // Statistics
        // The window width have to be adjusted widt the (max2 - min2) value.
        //int[][] stat = new int[1][10];
        //stat[0][0] = min2;  // min pos value
        //stat[0][1] = (max - min) + (max2 - min2);  // total range
        //stat[0][2] = max - min;  // range for negative values.
        //stat[0][3] = max2 - min2;  // range for positive values.

        /*
         stat[0][0] = pos_min;
         stat[0][1] = pos_max;
         stat[0][2] = neg_min;
         stat[0][3] = neg_max;
         stat[0][5] = pos_max - pos_min;  //range_pos
         //stat[0][4] = neg_max - neg_min;  //range_neg
         stat[0][4] = mask1 - neg_min;  //range_neg
         stat[0][6] = (pos_max - pos_min) + (neg_max - neg_min);  //range_tot
         stat[0][7] = mask1;
         stat[0][8] = 0;
         stat[0][9] = 0;
         setImageStats(stat);
         */
        imageStat[0][7] = mask1;
        DataBufferUShort short_db = new DataBufferUShort(data5, data5.length);
        int[] bandOffsets = new int[1];
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
                img.getWidth(), 1, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuffered = new BufferedImage(icm2, wr2, false, null);

        return imgBuffered;
    }
    
    /**
     * imageMod4b
     * @param img
     * @return 
     */
    public BufferedImage imageMod4b(BufferedImage img) {
        int bitsStored = attributeReader.att.getBitsStoredValue();
        
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();
        short[] data = ((DataBufferUShort) db).getData();
        int paddingValue = Integer.MIN_VALUE;
        int paddingValue2 = Integer.MIN_VALUE;

        int width = img.getWidth();
        int height = img.getHeight();
        int totalPix = width * height;

        // bitStored = 12  -> mask1 = 0x800 (2048)
        // bitStored = 16  -> mask1 = 0x8000 (32768)
        int mask1 = 1 << bitsStored - 1;
        imageStat[0][7] = mask1;

        // bitStored = 12  -> mask25 = 0x7ff (2047)
        // bitStored = 16  -> mask25 = 0x7fff (32767)
        int mask2 = -1 >>> (32 - bitsStored + 1);

        // Pixel Padding Value (0028,0120).
        // The Value Representation of this attribute is determined
        // by the value of the Pixel Represenation. If the value of
        // Pixel Representation is signed the pixelvalues are
        // transported to unsigned before this method.
        // e.g. Pixel Padding value 63 536 (f830) as 2�s 2000 (07d0).
        /*if(pixelPaddingValue != Integer.MIN_VALUE){
         int v1 = pixelPaddingValue;
         int v2 = v1 & 0x0000ffff;
         paddingValue = (int) (((~v2 & 0x0000ffff) + 1) * -1.0);
         }*/
        // Replaces the padding values with max negative value.
        // This gives black on the canvas even for big window width values.
        /*for(int i=0;i<data.length;i++){
         if(data[i] == paddingValue){
         //data[i] = (short) (0 - rescaleIntercept);
         data[i] = Short.MIN_VALUE;
         }
         }*/
        // Find the bits that have a 1 in the sign bit.
        int neg_max = Integer.MIN_VALUE;
        int neg_min = Integer.MAX_VALUE;
        short[] data2 = new short[totalPix];
        for (int i = 0; i < data.length; i++) {
            if (data[i] == paddingValue) {
                data[i] = Short.MIN_VALUE;
            }
            int v = data[i] & mask1;
            if (v == mask1) {
                data2[i] = (short) (data[i] & mask2);
                if (data2[i] > neg_max) {
                    neg_max = data2[i];
                }
                if (data2[i] < neg_min) {
                    neg_min = data2[i];
                }
            }
        }

        //printPixelValuesShort(data);
        //printPixelValuesShort(data2);
        //printPixelValuesShort(data, data2);
        //for(int i=0;i<data2.length;i++){
        //data2[i] = (short) (data2[i] - neg_min);
        //  data2[i] = (short) (data2[i]);
        //}
        //printPixelValuesShort(data2);
        // Find the bits that NOT have a 1 in the sign bit.
        // Put this values in a new short data array.
        // Add +max +1.
        int pos_max = Integer.MIN_VALUE;
        int pos_min = Integer.MAX_VALUE;
        short[] data3 = new short[totalPix];
        short[] data4 = new short[totalPix];
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & mask1;
            if (v != mask1) {
                int val = data[i] & mask2;
                if (val > pos_max) {
                    pos_max = val;
                }
                if (val < pos_min) {
                    pos_min = val;
                }
                //data2[i] = (short) (val + max + 1);
                //data2[i] = (short) (val + (neg_max - neg_min) + 1);
                //data2[i] = (short) (val + neg_max);   //ppp
                data2[i] = (short) ((val) + mask1);
            }
        }
        //printPixelValuesShort(data2);

        // new
        short[] data5 = new short[width * height];
        for (int i = 0; i < data2.length; i++) {
            //data5[i] = (short) ((data2[i] * rescaleSlope) + rescaleIntercept);
            data5[i] = (short) (data2[i]);
        }

        // Create a new RGB databuffer
        short[] dataRGB = new short[width * height * 3];

        for (int i = 0; i < data5.length; i++) {
            dataRGB[i * 3] = (short) (data5[i]);
            dataRGB[(i * 3) + 1] = (short) (data5[i]);
            //dataRGB[(i*3+2)] = (short) (data5[i]);
        }

        // Statistics
        // The window width have to be adjusted widt the (max2 - min2) value.
        //int[][] stat = new int[1][10];
        //stat[0][0] = min2;  // min pos value
        //stat[0][1] = (max - min) + (max2 - min2);  // total range
        //stat[0][2] = max - min;  // range for negative values.
        //stat[0][3] = max2 - min2;  // range for positive values.
        /*
         stat[0][0] = pos_min;
         stat[0][1] = pos_max;
         stat[0][2] = neg_min;
         stat[0][3] = neg_max;
         stat[0][5] = pos_max - pos_min;  //range_pos
         //stat[0][4] = neg_max - neg_min;  //range_neg
         stat[0][4] = mask1 - neg_min;  //range_neg
         stat[0][6] = (pos_max - pos_min) + (neg_max - neg_min);  //range_tot
         stat[0][7] = mask1;
         stat[0][8] = 0;
         stat[0][9] = 0;
         setImageStats(stat);
         */
        imageStat[0][7] = mask1;
        DataBufferUShort short_db = new DataBufferUShort(dataRGB, dataRGB.length);
        int[] bandOffsets = new int[1];
        //WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
        //      img.getWidth(), 1, bandOffsets, new Point());
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
                (img.getWidth() * 3), 3, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuffered = new BufferedImage(icm2, wr2, false, null);

        return imgBuffered;
    }

    /**
     * Test code. RSNA99 Testplan Phase 1, test11. 12 in 16 NO production code.
     * Status: works
     */
    public BufferedImage imageMod3(BufferedImage img) {
        int bitsStored = attributeReader.att.getBitsStoredValue();
        
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();
        short[] data = ((DataBufferUShort) db).getData();

        int width = img.getWidth();
        int height = img.getHeight();
        int totalPix = width * height;
        short[] data2 = new short[totalPix];
        int sample[] = new int[totalPix];

        // bitStored = 12  -> mask1 = 0x800 (2048)
        int mask1 = 1 << bitsStored - 1;

        // bitStored = 12  -> mask5 = 0x7ff (2047)
        int mask2 = -1 >>> (32 - bitsStored + 1);

        // Find the bits that NOT have a 1 in the sign bit (12).
        // Put this values in a new short data array.
        // Add +2048.
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & mask1;
            if (v != 0x800) {
                data[i] &= mask2;  //0x7ff
                data2[i] = data[i];
                data2[i] = (short) (data2[i] + 2048);
                //data2[i] = (short) (data2[i] << 1);
                //data2[i] = (short) (data2[i] + 1);
                //data2[i] = (short) (data[i] * 2);
            }
        }

        // Find the bits that have a 1 in the sign bit (12).
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & mask1;
            if (v == 0x800) {
                data[i] &= mask2;  //0x7ff
                data2[i] = data[i];
            }
        }

        DataBufferUShort short_db = new DataBufferUShort(data2, data2.length);
        int[] bandOffsets = new int[1];
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
                img.getWidth(), 1, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuffered = new BufferedImage(icm2, wr2, false, null);

        return imgBuffered;
    }

    /**
     * Test 20180430zzz Read of RGB BufferedImage
     */
    private BufferedImage imageModRGB8Bit(BufferedImage img) {
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();
        int noOfBands = sm.getNumBands();
        Color myColor;

        //Color myColor = new Color(img.getRGB(x,y));
        int width = img.getWidth();
        int height = img.getHeight();
        int sample[][] = new int[height * width][];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db);
                sample[i * width + j] = sm.getPixel(j, i, pix, db);
            }
        }
        // test
        int[][] matrix = new int[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                //myColor = new Color(img.getRGB(j,i));
                int pixel = img.getRGB(j, i);
                matrix[i][j] = img.getRGB(i, j);
                printPixelARGB(i, j, pixel);
            }
        }
        //test
        BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[1].length; j++) {
                int pixel = matrix[i][j] << 24 | matrix[i][j] << 16 | matrix[i][j] << 8 | matrix[i][j];
                img2.setRGB(i, j, pixel);
            }
        }

        // org code
        short[] short_im_data = new short[width * height];
        for (int i = 0; i < sample.length; i++) {
            short_im_data[i] = (short) sample[i][0];
        }

        //printPixelValuesShort(short_im_data);
        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        int bandOffsets[] = {0};
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, width, height, width, 1, bandOffsets, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] bits = {16};
        ComponentColorModel cm2 = new ComponentColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        BufferedImage imgBuf = new BufferedImage(cm2, wr2, cm2.isAlphaPremultiplied(), null);

        return imgBuf;
    }

    /**
     * Image modifier
     */
    private BufferedImage imageMod2(BufferedImage im) {
        SampleModel sm = im.getSampleModel();
        WritableRaster wr = im.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = im.getColorModel();

        int width = im.getWidth();
        int height = im.getHeight();
        int sample[][] = new int[height * width][];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db);
                sample[i * width + j] = sm.getPixel(j, i, pix, db);
            }
        }

        int l = sample.length;
        short sample_s[][] = new short[sample[0].length][width * height];
        for (int i = 0; i < sample.length; i++) {
            for (int j = 0; j < width * height; j++) {
                sample_s[i][j] = (short) sample[j][i];
            }
        }

        short[] short_im_data = new short[width * height];
        for (int i = 0; i < sample[0].length; i++) {
            short_im_data[i] = (short) sample[0][i];
        }

        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        int bandOffsets[] = {0};
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, width, height, width, 1, bandOffsets, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] bits = {16};
        ComponentColorModel cm2 = new ComponentColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        BufferedImage imgBuf = new BufferedImage(cm2, wr2, cm2.isAlphaPremultiplied(), null);

        // Create an int data sample model.
        /*SampleModel sampleModel =
         RasterFactory.createBandedSampleModel(
         DataBuffer.TYPE_INT,
         width,
         height,
         1);
         */
        return imgBuf;
    }
    
    /**
     * testArray
     */
    public void testArray() {
        int len = 1 << 16;
        int[] pixel = new int[len];
        short[] pixel_s = new short[len];

        for (int i = 0; i < len; i++) {
            pixel[i] = i;
        }

        for (int i = 0; i < len; i++) {
            if (i == 32765) {
                int a = 2;
            }
            pixel_s[i] = (short) pixel[i];
        }
        int b = 10;
    }

    /**
     * getDataBuffer
     * @param img
     */
    public static DataBuffer getDataBuffer(BufferedImage img) {
        WritableRaster wr = img.getRaster();
        DataBuffer db_s = wr.getDataBuffer();

        return db_s;
    }

    /**
     * getImageStatistics
     * @param img
     * @return 
     */
    public int[][] getImageStatistics(BufferedImage img) {
        if (img == null) {
            return null;
        }
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();
        return getImageStatistics(sm, db, new Dimension(img.getWidth(), img.getHeight()));
    }

    /**
     * getImageStatistics
     * @param sm
     * @param db
     * @param imageSize
     * @return a two dimensional array. First dimension is the component and
     * second is the stats
     *
     */
    public static int[][] getImageStatistics(SampleModel sm, DataBuffer db, Dimension imageSize) {
        int imageWidth = imageSize.width;
        int imageHeight = imageSize.height;
        int pixel[][] = new int[imageHeight * imageWidth][];
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db);  // test
                pixel[i * imageWidth + j] = sm.getPixel(j, i, pix, db);
            }
        }

        int len = pixel[0].length;
        int sum[] = new int[len];
        int max[] = new int[len];
        int min[] = new int[len];
        int[][] imageStats = new int[len][10];
        for (int j = 0; j < len; j++) {
            sum[j] = 0;
            max[j] = Integer.MIN_VALUE;
            min[j] = Integer.MAX_VALUE;
            for (int i = 0; i < pixel.length; i++) {
                int pix = pixel[i][j];
                if (pix > max[j]) {
                    max[j] = pix;
                }
                if (pix < min[j]) {
                    min[j] = pix;
                }
                sum[j] += pix;
            }
            //System.out.println("min = "+min[j]+ " max = "+max[j]+ " average = "+ sum[j]/(imageHeight*imageWidth));
            imageStats[j][0] = min[j];  //min
            imageStats[j][1] = max[j];  //max
            imageStats[j][2] = 0;
            imageStats[j][3] = 0;
            imageStats[j][4] = 0;
            imageStats[j][5] = max[j] - min[j];
            imageStats[j][6] = max[j] - min[j];
            imageStats[j][7] = 0;  // mask1
            imageStats[j][8] = min[j];  // min
            imageStats[j][9] = max[j];  // max
        }
        return imageStats;
    }

    /**
     * printPixelValuesInt
     */
    private void printPixelValuesInt(int[] buf) {
        for (int i = 0; i < buf.length; i++) {
            int val = buf[i];
            System.out.println("i=" + i + " " + val);
        }
    }

    /**
     * printPixelValuesShort
     */
    private void printPixelValuesShort(short[] buf) {
        for (int i = 0; i < buf.length; i++) {
            short val = buf[i];
            System.out.println("i=" + i + " " + val);
        }
    }

    /**
     * printPixelValuesShort
     */
    private void printPixelValuesShort(short[] buf, short[] buf2) {
        for (int i = 0; i < buf.length; i++) {
            short val = buf[i];
            short val2 = buf2[i];
            System.out.println("i=" + i + " " + val + " " + val2);
        }
    }
    
    /**
     * imageMod8Bit
     * @param img
     * @return 
     */
    private BufferedImage imageMod8Bit(BufferedImage img) {
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();

        int width = img.getWidth();
        int height = img.getHeight();
        int sample[][] = new int[height * width][];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db);
                sample[i * width + j] = sm.getPixel(j, i, pix, db);
            }
        }

        short[] short_im_data = new short[width * height];
        for (int i = 0; i < sample.length; i++) {
            short_im_data[i] = (short) sample[i][0];
        }

        //printPixelValuesShort(short_im_data);
        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        int bandOffsets[] = {0};
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, width, height, width, 1, bandOffsets, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] bits = {16};
        ComponentColorModel cm2 = new ComponentColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        BufferedImage imgBuf = new BufferedImage(cm2, wr2, cm2.isAlphaPremultiplied(), null);

        return imgBuf;
    }

    /**
     * Convert BufferedImage Pixel Representation 0 Bits 16 Issue14 Origin
     * imageMod8Bit
     */
    /**
     * convertPr016Bits
     * Convert BufferedImage Pixel
     * PixelRepresentation: 0
     * AllocatedBits: 16
     * @param img
     * @return imgBuf
     */
    private BufferedImage convertPr016Bits(BufferedImage img) {
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        ColorModel cm = img.getColorModel();

        int width = img.getWidth();
        int height = img.getHeight();
        int sample[][] = new int[height * width][];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pix[] = null;
                int[] a = sm.getPixel(j, i, pix, db);
                sample[i * width + j] = sm.getPixel(j, i, pix, db);
            }
        }

        short[] short_im_data = new short[width * height];
        for (int i = 0; i < sample.length; i++) {
            short_im_data[i] = (short) sample[i][0];
        }

        //printPixelValuesShort(short_im_data);
        /*
        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        int bandOffsets[] = {0};
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, width, height, width, 1, bandOffsets, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] bits = {16};
        ComponentColorModel cm2 = new ComponentColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        BufferedImage imgBuf = new BufferedImage(icm2, wr2, false, null);
         */
        DataBufferUShort short_db = new DataBufferUShort(short_im_data, short_im_data.length);
        int[] bandOffsets = new int[1];
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, img.getWidth(), img.getHeight(),
                img.getWidth(), 1, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuf = new BufferedImage(icm2, wr2, false, null);

        //IndexColorModel cm2 = new IndexColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
        return imgBuf;
    }

    /**
     * printPixelARGB
     * @param i
     * @param j
     * @param pixel 
     */
    public void printPixelARGB(int i, int j, int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        System.out.println("i, j, argb: " + i + ", " + j + ", " + alpha + ", " + red + ", " + green + ", " + blue);
    }
}
