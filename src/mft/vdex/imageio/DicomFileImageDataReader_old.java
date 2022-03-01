package mft.vdex.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
//import org.dcm4che.image.ColorModelFactory;


/**
 * @author Sune Svensson
 */

/**
 * Read DICOM file image data.
 *.
 */
public class DicomFileImageDataReader_old {
    File file;
    BufferedImage bufferedImage;
    //static final ColorModelFactory cmFactory = ColorModelFactory.getInstance();
    int numberOfImages;

    // remove
    public void setFile(File file) {
        this.file = file;
    }
    
    /**
     * Read DICOM image file.
     * @param file
     * @param stackType
     * @param imageIndex
     * @throws IOException 
     */
    public void readBufferedImage(File file, int stackType, int imageIndex) throws IOException {
        //System.out.println("DicomFileReader: readBufferedImage");
        ImageInputStream iis = null;

        try {
            iis = ImageIO.createImageInputStream(file);
        } catch (IOException e) {
            System.out.println("I/O exception obtaining a stream!");
            e.printStackTrace();
            System.exit(0);
        }

        if (iis == null) {
            System.out.println("DicomReader.readBufferedImage: Unable to get a stream!");
            System.exit(0);
        }
        
        Iterator iter = ImageIO.getImageReaders(iis);
        //Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
        //ImageReader reader = (ImageReader)iter.next();
        ImageReader reader = null;
        while (iter.hasNext()) {
            reader = (ImageReader) iter.next();
            System.out.println("Using " + reader.getClass().getName() + " for reading.");
            break;
        }

        if (reader == null) {
            System.err.println("Unable to find a reader!");
            System.exit(0);
        }

        try {
            reader.setInput(iis, false);
            //int numImages = reader.getNumImages(true);
            ImageReadParam param = reader.getDefaultReadParam();
            bufferedImage = reader.read(imageIndex, param);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Read DICOM file number of images.
     * @param f file path
     */
    public void readNumberOfImages(File f){
        ImageInputStream iis = null;
        
        try {
            iis = ImageIO.createImageInputStream(f);
        } catch (IOException e) {
            System.out.println("I/O exception obtaining a stream!");
            e.printStackTrace();
            System.exit(0);
        }
        
        if (iis == null) {
            System.out.println("DicomFileImageDataReader.readNumberOfImages: Unable to get a stream!");
            System.exit(0);
        }
        Iterator iter = ImageIO.getImageReaders(iis);
        ImageReader reader = null;
        
        while (iter.hasNext()) {
            reader = (ImageReader) iter.next();
            System.out.println("Using " + reader.getClass().getName() +
                  " to read.");
            break;
        }
        
        if (reader == null) {
            System.err.println("Unable to find a reader!");
            System.exit(0);
        }
        
        int nbImages = 0;
        try {
            reader.setInput(iis, false);
            numberOfImages = reader.getNumImages(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Get bufferedImage.
     * @return bufferedImage
     */
    public BufferedImage getBufferedImage(){
        return bufferedImage;
    }
    
    /**
     * Get Number of images
     * @return numberOfImages
     */
    public int getNumberOfImages(){
        return numberOfImages;
    }
}
    
    
    
    
