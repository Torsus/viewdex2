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
public class DicomFileImageDataReader2 {
   
    /**
     * 
     * @param f
     * @param imageIndex
     * @return
     * @throws IOException 
     */
    public BufferedImage readAsDicomImage(File f, int imageIndex) throws IOException {
        //System.out.println("ImageLoaderDICOM: readAsDicomImage");
        BufferedImage bi = null;
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(f);
        } catch (IOException e) {
            System.out.println("I/O exception obtaining a stream!");
            e.printStackTrace();
            System.exit(0);
        }

        if (iis == null) {
            System.out.println("ImageLoaderDICOM:readAsDicomImage:Unable to get a stream!");
            System.exit(0);
        }
        // imageReaders
        Iterator iter = ImageIO.getImageReaders(iis);
        //Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
        //ImageReader reader = (ImageReader)iter.next();
        ImageReader reader = null;
        while (iter.hasNext()) {
            // The method iter.next() get the the org.dcm4che3.imageio.plugins.dcm.DicomImageReader
            // this is the wrong plugin
             //test
            reader = (ImageReader) iter.next();
            System.out.println("Using " + reader.getClass().getName() + " to read.");
            break;
        }

        if (reader == null) {
            System.err.println("Unable to find a reader!");
            System.exit(0);
        }

        try {
            // Allow random access to multiple images
            reader.setInput(iis, false);
            int numImages = reader.getNumImages(true);
            //int imageIndex = 0;
            //int imageWidth = reader.getWidth(imageIndex);
            //int imageHeight = reader.getHeight(imageIndex);
            //int sourceWidth = imageWidth;
            //int sourceHeight = imageHeight;

            // test
            //org.dcm4che.imageio.plugins.DcmImageReadParam param = (org.dcm4che.imageio.plugins.DcmImageReadParam)reader.getDefaultReadParam();
            //DcmImageReadParam param2 = (DcmImageReadParam) reader.getDefaultReadParam();
            //param2.setAutoWindowing(true);

            //param2.setDestinationType(ImageTypeSpecifier.createGrayscale(16, DataBuffer.TYPE_USHORT,true));
            //byte[] lutR16 = new byte[65536];
            //byte[] lutG16 = new byte[65536];
            //byte[] lutB16 = new byte[65536];
            //byte[] lutAlfa = new byte[65536];
            //param2.setDestinationType(ImageTypeSpecifier.createIndexed(lutR16,lutG16,lutB16,lutAlfa,16,DataBuffer.TYPE_INT));
            //BufferedImage image = reader.read(imageIndex, param2);

            ImageReadParam param = reader.getDefaultReadParam();
            
            // read the image
            bi = reader.read(imageIndex, param);
            //imgRendered = reader.readAsRenderedImage(imageIndex, null);
            //Raster raster = reader.readRaster(imageIndex, null);  Not supported.

            /*
            dataset = ((DcmMetadata) reader.getStreamMetadata()).getDataset();
            patientName = dataset.getString(Tags.PatientName, null);
            patientID = dataset.getString(Tags.PatientID, null);
            studyInstanceUID = dataset.getString(Tags.StudyInstanceUID, null);
            highBit = dataset.getString(Tags.HighBit, null);
            photometricInterpretation = dataset.getString(Tags.PhotometricInterpretation, null);
            modality = dataset.getString(Tags.Modality, null);
            exposure = dataset.getString(Tags.Exposure, null);

            String pixelRepresentationValue = dataset.getString(Tags.PixelRepresentation, null);
            setPixelRepresentationValue(pixelRepresentationValue);

            String RescaleSlopeValue = dataset.getString(Tags.RescaleSlope, null);
            setRescaleSlopeValue(RescaleSlopeValue);

            String RescaleInterceptValue = dataset.getString(Tags.RescaleIntercept, null);
            setRescaleInterceptValue(RescaleInterceptValue);

            String PixelPaddingValue = dataset.getString(Tags.PixelPaddingValue, null);
            setPixelPaddingValue(PixelPaddingValue);

            String WindowCenterValue = dataset.getString(Tags.WindowCenter, null);
            setWindowCenterValue(WindowCenterValue);

            String WindowWidthValue = dataset.getString(Tags.WindowWidth, null);
            setWindowWidthValue(WindowWidthValue);

            String BitsStoredValue = dataset.getString(Tags.BitsStored, null);
            setBitsStoredValue(BitsStoredValue);

            String BitsAllocated = dataset.getString(Tags.BitsAllocated, null);
            setBitsAllocatedValue(BitsAllocated);

            // This one gives UnsupportedOperationException
            //int[] VOILUTSeq = dataset.getInts(Tags.VOILUTSeq);
            //setVOILUTSequenceValue(VOILUTSeq);

            //String LUTDescriptor = dataset.getString(Tags.LUTDescriptor, null);
            //setLUTDescriptorValue(LUTDescriptor);

            //String LUTExplanation = dataset.getString(Tags.LUTExplanation, null);
            //setLUTExplanationValue(LUTExplanation);

            //String LUTData = dataset.getString(Tags.LUTData, null);
            //setLUTDataValue(LUTData);
            */

            /*
            if ("MONOCHROME1".equals(photometricInterpretation) ||
                    "MONOCHROME2".equals(photometricInterpretation)) {
                cmParam = cmFactory.makeParam(dataset);
                int bits = dataset.getInt(Tags.BitsStored, 8);
                int size = 1 << bits;
                int signed = dataset.getInt(Tags.PixelRepresentation, 0);
                int min = dataset.getInt(Tags.SmallestImagePixelValue,
                        signed == 0 ? 0 : -(size >> 1));
                int max = dataset.getInt(Tags.LargestImagePixelValue,
                        signed == 0 ? size - 1 : (size >> 1) - 1);
                int c = (int) cmParam.toMeasureValue((min + max) >> 1);
                int cMin = (int) cmParam.toMeasureValue(min);
                int cMax = (int) cmParam.toMeasureValue(max - 1);
                int wMax = cMax - cMin;
                int w = wMax;

                int nWindow = cmParam.getNumberOfWindows();
                if (nWindow > 0) {
                    c = (int) cmParam.getWindowCenter(0);
                    w = (int) cmParam.getWindowWidth(0);
                }
            }*/
            int nframes = reader.getNumImages(true);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return bi;
    }
}




