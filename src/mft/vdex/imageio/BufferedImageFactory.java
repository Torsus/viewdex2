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
    public void setImageStat(int[][] imageStat){
        this.imageStat = imageStat;
    }
    
    /**
     * Called for images having the following attributes
     * BitsAllocatd = 16
     * PixelRepresantation = 0
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
     * Called for images having the following attributes
     * Photometric Interpretation = !RGB
     * BitsAllocatd = 8
     * PixelRepresantation = 0
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
     * Called for images having the following attributes
     * Photometric Interpretation = 
     * BitsAllocatd = 16
     * PixelRepresantation = 1
     * Create BufferedImage object from 16 bit Raster
     * @param raster
     * @return BufferedImage
     */
    public BufferedImage get16bitBuffImage2(Raster raster, int[][] is, int bitstored) {
        int bitsStored = bitstored;
        int[][] imageStats = is;
        //short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();
        short[] data = ((DataBufferUShort) raster.getDataBuffer()).getData();
        int height = raster.getHeight();
        int width = raster.getWidth();
        int size = raster.getDataBuffer().getSize();
        int nDataElements = raster.getNumDataElements();
        SampleModel sm = raster.getSampleModel();
        DataBuffer db1 = raster.getDataBuffer();
        //short[] data = ((DataBufferUShort) db).getData();
        int totalPix = width * height;
         int paddingValue = Integer.MIN_VALUE;
        int paddingValue2 = Integer.MIN_VALUE;

        // bitStored = 12  -> mask1 = 0x800 (2048)
        // bitStored = 16  -> mask1 = 0x8000 (32768)
        int mask1 = 1 << bitsStored - 1;
        imageStats[0][7] = mask1;

        // bitStored = 12  -> mask25 = 0x7ff (2047)
        // bitStored = 16  -> mask25 = 0x7fff (32767)
        int mask2 = -1 >>> (32 - bitsStored + 1);
        
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
         
        imageStats[0][7] = mask1;
        DataBufferUShort short_db = new DataBufferUShort(dataRGB, dataRGB.length);
        int[] bandOffsets = new int[1];
        WritableRaster wr2 = Raster.createInterleavedRaster(short_db, width, height,
                (width* 3), 3, bandOffsets, new Point());
        IndexColorModel icm2 = new IndexColorModel(16, 65535, lutR16, lutG16, lutB16);
        BufferedImage imgBuffered = new BufferedImage(icm2, wr2, false, null);

        return imgBuffered;
    }
    
    /**
     * Test code. RSNA99 Test plan Phase 1, test11. 12 in 16 NO production code.
     * Status: works
     * Called by 12 images.
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
}
