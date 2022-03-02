package mft.vdex.imageio;

//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReadParam;
//import javax.imageio.ImageReader;
//import javax.imageio.stream.ImageInputStream;
//import org.dcm4che.image.ColorModelFactory;

//import org.apache.commons.cli.*;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.image.ICCProfile;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;
//import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.SafeClose;

import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;


/**
 * @author Sune Svensson
 */

/**
 * Read DICOM file image data.
 */
public class DicomFileImageDataReader {
    private static final ImageReader imageReader
            = ImageIO.getImageReadersByFormatName("DICOM").next();
    File file;
    BufferedImage bufferedImage;
    Raster raster = null;
    //static final ColorModelFactory cmFactory = ColorModelFactory.getInstance();
    int numberOfImages;
    
    public BufferedImage readImageFromDicomInputStream(File file) throws IOException {
        BufferedImage bi = null;

        try ( DicomInputStream dis = new DicomInputStream(file)) {
            imageReader.setInput(dis);

            //return imageReader.read(frame - 1, readParam()); // Origin
            DicomImageReadParam param
                    = (DicomImageReadParam) imageReader.getDefaultReadParam();
            //bi = imageReader.read(0, param);
            //ImageReadParam param = reader.getDefaultReadParam();  // Old code
            //ImageReader param_2 = imageReader.getRawImageType(numberOfImages);
            //bi = imageReader.read(0, param);
            //System.out.println("DicomFileImageDataReader.readImageFromDicomInputStream :" + bi.toString());
            //System.out.println("Object size(1): " + ObjectSizeCalculator.getObjectSize(bi));
            
            // test
            raster = imageReader.readRaster(0, param);
            System.out.println("DicomFileImageDataReader.readImageFromDicomInputStream raster :" + raster.toString());
            System.out.println("Object size(1): raster" + ObjectSizeCalculator.getObjectSize(raster));
            int aa = 10;

        } catch (IOException e) {
            System.err.println("dcm2jpg: " + e.getMessage());
            e.printStackTrace();
        }
        return bi;
    }
    
    private ImageReadParam readParam() {
        DicomImageReadParam param
                = (DicomImageReadParam) imageReader.getDefaultReadParam();
        //param.setWindowCenter(windowCenter);
        //param.setWindowCenter(windowCenter);
        //param.setWindowWidth(windowWidth);
        //param.setAutoWindowing(autoWindowing);
        //param.setIgnorePresentationLUTShape(ignorePresentationLUTShape);
        //param.setWindowIndex(windowIndex);
        //param.setVOILUTIndex(voiLUTIndex);
        //param.setPreferWindow(preferWindow);
        //param.setPresentationState(prState);
        //param.setOverlayActivationMask(overlayActivationMask);
        //param.setOverlayGrayscaleValue(overlayGrayscaleValue);
        //param.setOverlayRGBValue(overlayRGBValue);
        return param;
    }
    
    /**
     * Get bufferedImage.
     * @return bufferedImage
     */
    public BufferedImage getBufferedImage_ni(){
        return bufferedImage;
    }
    
    /**
     * Get Number of images
     * @return numberOfImages
     */
    public int getNumberOfImages_ni(){
        //return numberOfImages;
        return 1;
    }
}
    
    
    
    
