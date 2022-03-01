/* @(#) ImageInfoBuffered.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.util;

import java.awt.*;
import java.awt.image.*;
//import java.io.*;
import java.awt.color.*;
//import com.sun.image.codec.jpeg.JPEGCodec;

public class ImageInfoBuffered{
    public final static int RED = 0;
    public final static int GREEN = 1;
    public final static int BLUE = 2;
    public final static int ALPHA = 3;
    
    public final static int DIRECT = 0;
    public final static int INDEX = 1;
    public final static int COMPONENT = 2;
    
    public final static int COMPONENT_SAMPLE = 0;
    public final static int BANDED = 1;
    public final static int PIXEL_INTERLEAVED = 2;
    public final static int SINGLE_PIXEL_PACKED =3;
    public final static int MULTI_PIXEL_PACKED = 4;
    
    
    /** Creates a new instance of ImageInfo */
    public ImageInfoBuffered(){
    }
    
    public void show(BufferedImage img){
        if(true)
            System.out.println("ImageInfoBuffered.show");
        showImageInfo(img);
        //int [][] stat = getImageStats(img);
    }
    
    public void showImageInfo(BufferedImage img){
        SampleModel sm = img.getSampleModel();
        WritableRaster wr = img.getRaster();
        DataBuffer db = wr.getDataBuffer();
        
        // colormodel
        ColorModel cm = img.getColorModel();
        String str = getColorModelAsText(getColorModelType(cm));
        System.out.println("Color Model: " + str);
        
        // colorspace
        ColorSpace cs = cm.getColorSpace();
        str = getColorSpaceAsText(cs.getType());
        System.out.println("Color Space: " + str);
        
        // samplemodel
        str = getSampleModelAsText(getSampleModelType(sm));
        System.out.println("Sample model: " + str);
        
        // datatype
        str = getDataTypeAsText(db.getDataType());
        System.out.println("Data type: " + str);
        
        // number of bands
        int numbands = sm.getNumBands();
        str =  Integer.toString(numbands);
        System.out.println("Number of Bands: " + str);
        
        // number of banks
        int numbanks = db.getNumBanks();
        str =  Integer.toString(numbanks);
        System.out.println("Number of Banks: " + str);
        
        // image width
        str = Integer.toString(img.getWidth());
        System.out.println("Image Width: " + str);
        
        // image height
        str = Integer.toString(img.getHeight());
        System.out.println("Image Height: " + str);
        
        // ComponentSampleModel
        int scanlineStride=0;
        if(sm instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sm;
            scanlineStride = csm.getScanlineStride();
            str = Integer.toString(scanlineStride);
            System.out.println("Scanline stride: " + str);
            
            // pixel stride
            int pixelStride =   csm.getPixelStride();
            str = Integer.toString(pixelStride);
            System.out.println("Pixel stride: " + str);
            
            //int[] bankIndices = csm.getBankIndices();
            //System.out.println("bank indices = " );
            //for(int j=0; j<bankIndices.length;j++)
            //    System.out.println(bankIndices[j]);
            //int[] bandOffsets = csm.getBandOffsets();
            //System.out.println("bank offsets = " );
            //for(int j=0; j<bandOffsets.length;j++)
            //System.out.println(bandOffsets[j]);
        } else {
            if( sm  instanceof SinglePixelPackedSampleModel){
                SinglePixelPackedSampleModel  ssm = (SinglePixelPackedSampleModel)sm;
                scanlineStride = ssm.getScanlineStride();
                str = Integer.toString(scanlineStride);
                System.out.println("Scanline stride: " + str);
                
                System.out.println("Pixel stride: " + "N/A");
            } else if ( sm  instanceof MultiPixelPackedSampleModel){
                MultiPixelPackedSampleModel  msm = (MultiPixelPackedSampleModel)sm;
                
                // scanline stride
                scanlineStride = msm.getScanlineStride();
                str = Integer.toString(scanlineStride);
                System.out.println("Scanline stride: " + str);
                
                // pixel bit stride
                int pixelBitStride = msm.getPixelBitStride();
                str = Integer.toString(pixelBitStride);
                System.out.println("Pixel bit stride: " + str);
            }
        }
    }
    
    // ---------------------------------------------------------
    public static int getColorModelType(ColorModel cm) {
        int type = DIRECT;
        if(cm instanceof ComponentColorModel){
            type = COMPONENT;
            //System.out.println("Component color model");
        }
        if(cm instanceof DirectColorModel){
            type =  DIRECT;
            //System.out.println("Direct color model");
        }
        if(cm instanceof IndexColorModel) {
            type = INDEX;
            //System.out.println("Indexed color model");
        }
        return type;
    }
    
    // ---------------------------------------------------------
    public static String getColorModelAsText(int cmtype){
        switch(cmtype) {
            case DIRECT:
                return " Direct";
            case COMPONENT:
                return " Component";
            case INDEX:
                return " Indexed";
            default:
                return " Unknown";
        }
    }
    
    // --------------------------------------------------------
    public static String getColorSpaceAsText(int cs){
        switch(cs) {
            case ColorSpace.CS_GRAY:
                return " Gray";
            case ColorSpace.CS_PYCC:
                return " Photo YCC";
            case ColorSpace.CS_sRGB:
                return " sRGB ";
            case ColorSpace.CS_LINEAR_RGB:
                return " Linea RGB";
            case ColorSpace.CS_CIEXYZ:
                return " CIEXYZ ";
            case ColorSpace.TYPE_GRAY:
                return " Gray";
            case ColorSpace.TYPE_CMYK:
                return " CMYK";
            case ColorSpace.TYPE_RGB:
                return " RGB ";
            case ColorSpace.TYPE_HSV:
                return " HSV";
            case ColorSpace.TYPE_XYZ:
                return "XYZ ";
            default:
                return " Unknown";
        }
    }
    
    public static String getSampleModelAsText(int smtype){
        switch(smtype) {
            case PIXEL_INTERLEAVED:
                return " Pixel interleaved";
            case BANDED:
                return " Banded";
            case SINGLE_PIXEL_PACKED:
                return "Single pixel packed";
            case MULTI_PIXEL_PACKED:
                return "Muti pixel packed";
            case COMPONENT_SAMPLE:
                return " Component ";
            default:
                return " Unknown";
        }
    }
    
    public static int getSampleModelType(SampleModel cm) {
        int type = COMPONENT_SAMPLE;
        if(cm instanceof ComponentSampleModel){
            type = COMPONENT_SAMPLE;
            
            //System.out.println("Component sample model");
        }
        if(cm instanceof  SinglePixelPackedSampleModel ){
            type = SINGLE_PIXEL_PACKED;
            //System.out.println("single pixel model");
        }
        if(cm instanceof MultiPixelPackedSampleModel) {
            type = MULTI_PIXEL_PACKED;
            //System.out.println("Multi sample model");
        }
        
        if(cm instanceof BandedSampleModel) {
            type = BANDED;
            //System.out.println("Banded sample model");
        }
        
        if(cm instanceof PixelInterleavedSampleModel) {
            type = PIXEL_INTERLEAVED;
            //System.out.println("Pixel interleaved sample model");
        }
        
        return type;
    }
    
    public static String getDataTypeAsText(int dttype){
        switch(dttype) {
            case DataBuffer.TYPE_BYTE:
                return " Byte";
            case DataBuffer.TYPE_SHORT:
                return " Short";
            case DataBuffer.TYPE_USHORT:
                return " Unsigned short";
            case DataBuffer.TYPE_INT:
                return " int";
            case DataBuffer.TYPE_FLOAT:
                return " Float";
            case DataBuffer.TYPE_DOUBLE:
                return " Double";
            case DataBuffer.TYPE_UNDEFINED:
                return " Undefined";
            default:
                return " Unknown";
        }
    }
    
    public int[][] getImageStats(BufferedImage img){
       if(img == null) return null;
       SampleModel sm = img.getSampleModel();
       WritableRaster wr = img.getRaster();
       DataBuffer db = wr.getDataBuffer();
       ColorModel cm = img.getColorModel();
       return getImageStats(sm, db, new Dimension(img.getWidth(), img.getHeight()));
    }


    /**
      * @return a two dimensional array. First dimension is the component and second is the stats
      **/
    public static int[][] getImageStats(SampleModel sm, DataBuffer db, Dimension imageSize){
       int imageWidth = imageSize.width;
       int imageHeight = imageSize.height;
       int pixel[][] = new int[imageHeight*imageWidth][];
       for(int i=0;i<imageHeight;i++){
           for(int j=0; j<imageWidth;j++) {
               int pix[] = null;
               int [] a = sm.getPixel(j, i, pix, db);  // test
               pixel[i*imageWidth+j] =  sm.getPixel(j,i, pix, db);
           }
       }

       int len = pixel[0].length;
       int sum[] = new int[len];
       int max[] = new int[len];
       int min[] = new int[len];
       int[][] imageStats = new int[len][3];
       for(int j=0;j<len;j++) {
           sum[j] =0;
           max[j] = Integer.MIN_VALUE;
           min[j] = Integer.MAX_VALUE;
           for(int i=0;i<pixel.length;i++) {
               int pix = pixel[i][j];
               if(pix > max[j])
                   max[j] = pix;
               if(pix < min[j] )
                   min[j] = pix;
               sum[j] += pix;
           }
           System.out.println("min = "+min[j]+ " max = "+max[j]+ " average = "+ sum[j]/(imageHeight*imageWidth));
           imageStats[j][0] = min[j];
           imageStats[j][1] = max[j];
           imageStats[j][2] = sum[j]/(imageHeight*imageWidth);
       }
       return imageStats;
    }
    
     /**
      * @return a two dimensional array. First dimension is the component and second is the stats
      **/
    public void printPixelValues(BufferedImage img){
        if(img == null)
            return;
       SampleModel sm = img.getSampleModel();
       WritableRaster wr = img.getRaster();
       DataBuffer db = wr.getDataBuffer();
       ColorModel cm = img.getColorModel();
        
       int imageWidth = img.getWidth();
       int imageHeight = img.getHeight();
       int pixel[][] = new int[imageHeight*imageWidth][];
       for(int i=0;i<imageHeight;i++){
           for(int j=0; j<imageWidth;j++) {
               int pix[] = null;
               int [] a = sm.getPixel(j, i, pix, db); //test
               pixel[i*imageWidth+j] =  sm.getPixel(j,i, pix, db);
           }
       }
       
       for(int i=0;i<pixel.length;i++){
           int val = pixel[i][0];
           System.out.println("i=" + i + " " + val);
       }
    }
}
