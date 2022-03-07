package mft.vdex.imageio;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.PlanarImage;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

/**
 * @author sune
 */
public class DicomFileImageReader {
    DicomFileAttributeReader dicomFileAttributeReader;
    PlanarImage imgPlanar;
    int numberOfImages;

    /**
     * readFileImageRaster
     * @param file
     * @param imgcnt
     * @return 
     */
    public Raster readFileImageRaster(File file, int imgcnt) {
        Raster raster = null;
        //System.out.println("DicomFileImageBufferReader.readFileImageRaster " + file.getName());

        try {
            Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
            ImageReader reader = (ImageReader) iter.next();
            DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            reader.setInput(iis, false);
            raster = reader.readRaster(imgcnt, param);
            numberOfImages = reader.getNumImages(true);
            if (raster == null) {
                System.out.println("Error: couldn't read Dicom image!");
            }
            iis.close();
        } catch (IOException e) {
            System.out.println("Error: couldn't read dicom image! " + e.getMessage());
            e.printStackTrace();
        }
        
        return raster;
    }
    
    /**
     * get16bitBuffImage
     * @param raster
     * @return BufferedImage
     */
    public static BufferedImage get16bitBuffImage(Raster raster) {
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
     * Get Number of images
     * @return numberOfImages
     */
    public int getNumberOfImages(){
        return numberOfImages;
    }
}
