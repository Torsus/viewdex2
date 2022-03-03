package mft.vdex.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/**
 * @author Sune Svensson
 */

/**
 * Read DICOM DataSet.
 * Set DataSet in DicomAttribute.
 * Read and parse DICOM attributes.
 * Print DICOM attributes.
 */
public class DicomFileAttributeReader {
    File file;
    Attributes fmi;
    Attributes attributes;
    public DicomAttributes att = new DicomAttributes();
    BufferedImage bufferedImage;
    
    /**
     * Set DICOM image file.
     * @param file 
     */
    public void setFile(File file) {
        this.file = file;
    }
    
    /**
     * Read DataSet.
     * Read FileMetaInformation.
     * Set DataSet in DicomAttributes.
     * Read Attributes.
     * @param file
     * @throws IOException 
     */
    public void readAttributes(File file) throws IOException {
        try ( DicomInputStream dis = new DicomInputStream(file)) {
            dis.setIncludeBulkData(IncludeBulkData.URI);
            fmi = dis.readFileMetaInformation();
            attributes = dis.readDataset();
        }
        
        att.setDataSet(attributes);
        att.readAttributes();
    }
    
    /**
     * Get attributes
     * @return attributes
     */
    public Attributes getAttributes(){
        return attributes;
    }
}
