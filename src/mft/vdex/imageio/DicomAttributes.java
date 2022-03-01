package mft.vdex.imageio;

import java.util.Arrays;
import java.util.StringTokenizer;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/**
 * @author sune
 */
public class DicomAttributes {
    private static int cnt = 0;
    Attributes dataset;
    private String transferSyntaxUID;                                       // 0002,0010 UI Transfer Syntax UID
    private String[] imageType;                                             // 0008,0008 CS Image Type
    private String modality;                                                // 0008,0060 CS Modality
    private String manufacturer;                                            // 0008,0070 LO Manufacturer
    private String institutionName;                                         // 0008,0080 LO Institution Name
    private String patientName;                                             // 0010,0010 PN Patient Name
    private String patientID;                                               // 0010,0020 LO Patient ID
    private double estimatedRadiographicMagnificationFactor;                // 0018,1114 DS Estimated Radiographic Magnification Factor
    private String[] exposureTime;                                          // 0018,1150 IS Exposure Time
    private String exposure_str;                                            // 0018,1152 IS Exposure
    private int exposure;                                                   // 0018,1152 IS Exposure
    private double[] imagerPixelSpacing;                                    // 0018,1164 DS Imager Pixel Spacing
    private double[] nominalScannedPixelSpacing;                            // 0018,2010 DS Nominal Scanned Pixel Spacing
    private String studyInstanceUID;                                        // 0020,000D UI Study Instance UID
    private String seriesInstanceUID;                                       // 0020,000E UI Series Instance UID
    private String seriesNumber;                                            // 0020,0011 IS Series Number
    private int samplesPerPixel;                                            // 0028,0002 US Samples per Pixel
    private String photoMetricInterpretation;                               // 0028,0004 CS Photometric Interpretation
    private String numberOfFrames;                                          // 0028,0008 iS Number Of Frames
    private int numberOfFrames_int;                                         // 0028,0008 iS Number Of Frames
    private int instanceNumber;                                             // xxxx,xxxx    Instance Number (Depricated?)
    private int rows;                                                       // 0028,0010 CS Rows
    private int rows_int;                                                   // 0028,0010 CS Rows
    private int columns;                                                    // 0028,0011 US Columns
    private int columns_int;                                                // 0028,0011 US Columns
    private double[] pixelSpacing;                                          // 0028,0030 DS PixelSpacing
    private int[] pixelAspectRatio;                                         // 0028,0034 IS Pixel Aspect Ratio
    private int bitsAllocated;                                              // 0028,0100 US BitsAllocated
    private int bitsStored;                                                 // 0028,0101 US BitsStored
    private int highBit;                                                    // 0028,0102 US HighBit
    private int pixelRepresentation;                                        // 0028,0103 US Pixel Representation
    private int smallestImagePixelValue;                                    // 0028,0106 US,SS Smallest Image Pixel Value
    private int largestImagePixelValue;                                     // 0028,0107 US,SS Largest Image Pixel Value
    private int pixelPaddingValue;                                          // 0028,0120 US,SS Pixel Padding Value
    private String qualityControlImage;                                     // 0028,0300 CS Quality Control Image
    private String burnedInAnnotation;                                      // 0028,0301 CS Burned In Annotation
    private String pixelSpacingCalibrationType;                             // 0028,0A02 CS Pixel Spacing Calibration Type
    private String pixelSpacingCalibrationDescription;                      // 0028,0A04 CS Pixel Spacing Calibration Description
    private String pixelIntensityRelationship;                              // 0028,1040 CS Pixel Intensity Relationship
    private int pixelIntensityRelationshipSign;                             // 0028,1041 SS Pixel Intensity Relationship Sign
    private double[] windowCenter;                                          // 0028,1050 DS Window Center
    private String windowCenter_str;                                        // 0028,1050 DS Window Center
    private int[] windowCenter_int_array;                                   // 0028,1050 DS Window Center
    private double[] windowCenter_double_array;                             // 0028,1051 DS Window Width
    private double[] windowWidth;                                           // 0028,1051 DS Window Width
    private int[] windowWidth_int_array;                                    // 0028,1051 DS Window Width
    private double[] windowWidth_double_array;                              // 0028,1051 DS Window Width
    private double rescaleIntercept;                                        // 0028,1052 DS Rescale Intercept
    private double rescaleSlope;                                            // 0028,1053 DS Rescale Slope
    private String rescaleType;                                             // 0028,1054 LO Rescale Type
    private String[] windowCenterWidthExplanation;                          // 0028,1055 LO Window Center & Width Explanation
    private String[] modalityLUTSequence;                                   // 0028,3000 SQ Modality LUT Sequence
    private String modalityLUTSequence_str;                                 // 0028,3000 SQ Modality LUT Sequence
    private short[] lutDescriptor;                                          // 0028,3002 US LUT Descriptor
    private String lutExplanation;                                          // 0028,3003 LO LUT Explanation
    private int lutData;                                                    // 0028,3006 US LUT Data
    private short[] lutData_short_array;                                    // 0028,3006 US LUT Data
    private String voiLUTSequence;                                          // 0028,3010 SQ VOI LUT Sequence
    
    private boolean windowCenterFloatValueExist;
    
    public DicomAttributes(){ 
    }
    
    public DicomAttributes(Attributes dataset){
        this.dataset = dataset; 
    }
    
    public void setDataSet(Attributes dataset){
        this.dataset = dataset;
    }
    
    public Attributes getDataSet(){
        return dataset;
    }
    
    /**
     * Read DICOM attributes
     */
    public void readAttributes(){
        // 0002,0010 Transfer Syntax UID
        String TransferSyntaxUID = dataset.getString(Tag.TransferSyntaxUID);
        setTransferSyntaxUID(TransferSyntaxUID);
        
        // 0008,0008 Image Type
        String[] ImageType = dataset.getStrings(Tag.ImageType);
        setImageType(ImageType);
        
        // 0008,0060 Modality
        String Modality = dataset.getString(Tag.Modality, null);
        setModality(Modality);

        // 0008,0070 Manufacturer
        String Manufacturer = dataset.getString(Tag.Manufacturer);
        setManufacturer(Manufacturer);
        
        // 0008,0080 Institution Name
        String InstitutionName = dataset.getString(Tag.InstitutionName);
        setInstitutionName(InstitutionName);
        
         // 0010,0010 Patient Name
        String PatientName = dataset.getString(Tag.PatientName, null);
        setPatientName(PatientName);

        // 0010,0020 Patient ID
        String PatientID = dataset.getString(Tag.PatientID);
        setPatientID(PatientID);

        // 0018,1114 Estimated Radiographic Magnification Factor
        String EstimatedRadiographicMagnificationFactor = dataset.
                getString(Tag.EstimatedRadiographicMagnificationFactor, null);
        setEstimatedRadiographicMagnificationFactorDataValue(EstimatedRadiographicMagnificationFactor);

        // 0018,1150 Exposure Time
        String[] ExposureTime = dataset.getStrings(Tag.ExposureTime);
        setExposureTime(ExposureTime);

        // 0018,1152 Exposure
        String Exposure = dataset.getString(Tag.Exposure);
        setExposureValue(Exposure);

        // 0018,1164 Imager Pixel Spacing
        String ImagerPixelSpacing[] = dataset.getStrings(Tag.ImagerPixelSpacing);
        setImagerPixelSpacing(ImagerPixelSpacing);
        
        //setImagerPixelSpacingDataValue(ImagerPixelSpacing[0]);
        
        // 0018,2010 Nominal Scanned Pixel Spacing
        String NominalScannedPixelSpacing[] = dataset.getStrings(Tag.NominalScannedPixelSpacing);
        setNominalScannedPixelSpacing(NominalScannedPixelSpacing);
        
        // Old version
        //String NominalScannedPixelSpacing_old = dataset.getString(Tag.NominalScannedPixelSpacing, null);
        //setNominalScannedPixelSpacingDataValue(NominalScannedPixelSpacing_old);

        // 0020,000D Study Instance UID 
        String StudyInstanceUID = dataset.getString(Tag.StudyInstanceUID);
        setStudyInstanceUID(StudyInstanceUID);

        // 0020,000E Series Instance UID
        String SeriesInstanceUID =dataset.getString(Tag.SeriesInstanceUID);
        setSeriesInstanceUID(SeriesInstanceUID);

        // 0020,0011 Series Number
        String SeriesNumber = dataset.getString(Tag.SeriesNumber);
        setSeriesNumber(SeriesNumber);

        // 0028,0002 Samples per Pixel
        int SamplesPerPixel = dataset.getInt(Tag.SamplesPerPixel, 8);
        setSamplesPerPixel(SamplesPerPixel);

        // 0028,0004 Photometric Interpretation
        String PhotometricInterpretation = dataset.getString(Tag.PhotometricInterpretation, null);
        setPhotometricInterpretation(PhotometricInterpretation);
        
        // 0028,0008
        String NumberOfFrames = dataset.getString(Tag.NumberOfFrames);
        setNumberOfFrames(NumberOfFrames);
        
        // xxxx,xxxx
        int instanceNumber = dataset.getInt(Tag.InstanceNumber, 0);
        setInstanceNumber(instanceNumber);
        
        // 0028,0008
        int NumberOfFrames_int = dataset.getInt(Tag.NumberOfFrames, 0);
        setNumberOfFramesInt(NumberOfFrames_int);

        // 0028,0010 Rows
        int Rows = dataset.getInt(Tag.Rows, 0);
        setRows(Rows);
        
        // 0028,0010 Rows
        String Rows_str = dataset.getString(Tag.Rows, null);
        setRowsDataValue(Rows_str);
        
        // 0028,0011 Columns
        int Columns = dataset.getInt(Tag.Columns, 0);
        setColumns(Columns);
        
        // 0028,0011 Columns
        String Columns_str = dataset.getString(Tag.Columns, null);
        setColumnsDataValue(Columns_str);
       
        // 0028,0030 PixelSpacing
        String PixelSpacing[] = dataset.getStrings(Tag.PixelSpacing);
        setPixelSpacing(PixelSpacing);
        
        // 0028,0034 Pixel Aspect Ratio
        String PixelAspectRatio[] = dataset.getStrings(Tag.PixelAspectRatio);
        setPixelAspectRatio(PixelAspectRatio);

        // 0028,0100 BitsAllocated
        int BitsAllocated = dataset.getInt(Tag.BitsAllocated, 0);
        setBitsAllocated(BitsAllocated);
        
        // ViewDEX
        String BitsAllocatedValue = dataset.getString(Tag.BitsAllocated, null);
        setBitsAllocatedValue(BitsAllocatedValue);

        // 0028,0101 BitsStored
        int BitsStored = dataset.getInt(Tag.BitsStored, 0);
        setBitsStored(BitsStored);
        
        // ViewDEX
        String BitsStoredValue = dataset.getString(Tag.BitsStored, null);
        setBitsStoredValue(BitsStoredValue);

        // 0028,0102 HighBit
        int HighBit = dataset.getInt(Tag.HighBit, 0);
        setHighBit(HighBit);

        // 0028,0103 Pixel Representation
        int PixelRepresentationValue = dataset.getInt(Tag.PixelRepresentation, 0);
        setPixelRepresentation(PixelRepresentationValue);
        
        // ViewDEX
        //String pixelRepresentationValue = dataset.getString(Tags.PixelRepresentation, null);
        //setPixelRepresentationValue(pixelRepresentationValue);
        /*private void setPixelRepresentationValue(String str) {
        int value = 0;

        if (str != null) {
            try {
                value = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setPixelRepresentationValue: NumberFormatException");
            }
        }
        pixelRepresentation = value;
        }*/
        /*public int getPixelRepresentationValue() {
        return pixelRepresentation;
        }*/

        // 0028,0106 Smallest Image Pixel Value
        int SmallestImagePixelValue = dataset.getInt(Tag.SmallestImagePixelValue, 0);
        setSmallestImagePixelValue(SmallestImagePixelValue);
        
        // 0028,0107 Largest Image Pixel Value 
        int LargestImagePixelValue = dataset.getInt(Tag.LargestImagePixelValue, 0);
        setLargestImagePixelValue(LargestImagePixelValue);

        // 0028,0120 Pixel Padding Value
        int PixelPaddingValue = dataset.getInt(Tag.PixelPaddingValue, 0);
        setPixelPaddingValue(PixelPaddingValue);
        
        // ViewDEX
        String PixelPaddingValueValue = dataset.getString(Tag.PixelPaddingValue, null);
        setPixelPaddingValueValue(PixelPaddingValueValue);

        // 0028,0300 Quality Control Image
        String QualityControlImage = dataset.getString(Tag.QualityControlImage);
        setQualityControlImage(QualityControlImage);

        // 0028,0301 Burned In Annotation
        String BurnedInAnnotation = dataset.getString(Tag.BurnedInAnnotation);
        setBurnedInAnnotation(BurnedInAnnotation);

        // 0028,0A02 Pixel Spacing Calibration Type
        String PixelSpacingCalibrationType = dataset.getString(Tag.PixelSpacingCalibrationType, null);
        setPixelSpacingCalibrationType(PixelSpacingCalibrationType);
        
        // 0028,0A04 Pixel Spacing Calibration Description
        String PixelSpacingCalibrationDescription = dataset.getString(Tag.PixelSpacingCalibrationDescription, null);
        setPixelSpacingCalibrationDescription(PixelSpacingCalibrationDescription);

        // 0028,1040 Pixel Intensity Relationship
        String PixelIntensityRelationship = dataset.getString(Tag.PixelIntensityRelationship);
        setPixelIntensityRelationship(PixelIntensityRelationship);

        // 0028,1041 Pixel Intensity Relationship Sign
        int PixelIntensityRelationshipSign = dataset.getInt(Tag.PixelIntensityRelationshipSign, 0);
        setPixelIntensityRelationshipSign(PixelIntensityRelationshipSign);

        // 0028,1050 Window Center
        String[] WindowCenter = dataset.getStrings(Tag.WindowCenter);
        setWindowCenter(WindowCenter);

        // 0028,1050 Window Center
        String WindowCenter_str = dataset.getString(Tag.WindowCenter, null);
        setWindowCenter_str(WindowCenter_str);
        setWindowCenter_int_array(WindowCenter_str);
        setWindowCenter_double_array(WindowCenter_str);
        
        // 0028,1051 Window Width
        String[] WindowWidth = dataset.getStrings(Tag.WindowWidth);
        setWindowWidth(WindowWidth);

        // 0028,1051 Window Width
        String WindowWidth_str = dataset.getString(Tag.WindowWidth, null);
        setWindowWidth_int_array(WindowWidth_str);
        setWindowWidth_double_array(WindowWidth_str);

        // 0028,1052 Rescale Intercept
        String RescaleIntercept = dataset.getString(Tag.RescaleIntercept, null);
        setRescaleInterceptValue(RescaleIntercept);
        
         // 0028,1053 Rescale Slope
        String RescaleSlope = dataset.getString(Tag.RescaleSlope, null);
        setRescaleSlopeValue(RescaleSlope);

        // 0028,1054 Rescale Type
        String RescaleType = dataset.getString(Tag.RescaleType);
        setRescaleType(RescaleType);
        
        // 0028,1055 LO Window Center & Width Explanation
        String[] WindowCenterWidthExplanation = dataset.getStrings(Tag.WindowCenterWidthExplanation);
        setWindowCenterWidthExplanation(WindowCenterWidthExplanation);

        // 0028,3000 Modality LUT Sequence
        String[] ModalityLUTSequence = dataset.getStrings(Tag.ModalityLUTSequence);
        setModalityLUTSequence(ModalityLUTSequence);
        
         // 0028,3000 Modality LUT Sequence
        String ModalityLUTSequence_str = dataset.getString(Tag.ModalityLUTSequence, null);
        setModalityLUTSequenceString(ModalityLUTSequence_str);
        
        //0028,3002 US LUT Descriptor
        String LUTDescriptor = dataset.getString(Tag.LUTDescriptor, null);
        setLUTDescriptorValue(LUTDescriptor);
        
        //0028,3003 LO LUT Explanation
        String LUTExplanation = dataset.getString(Tag.LUTDescriptor, null);
        setLUTExplanationValue(LUTExplanation);

        // 0028,3006 US LUT Data
        int LutData = dataset.getInt(Tag.LUTData, 0);
        setLutData(LutData);
        
        // 0028,3006 US LUT Data
        String LutData_str = dataset.getString(Tag.LUTData, null);
        setLUTDataValue(LutData_str);

        // 0028,3010 VOI LUT Sequence
        String VOILUTSequence = dataset.getString(Tag.VOILUTSequence, null);
        setVOILUTSequenceValue(VOILUTSequence);
        
        // 0028,3006 LUT Data
        //lutData = dataset.getInt(Tag.LUTData, 0);
        // 0028,1052 Rescale Intercept
        //String RescaleInterceptValue = dataset.getString(Tag.RescaleIntercept, null);
        //setRescaleInterceptValue(RescaleInterceptValue);
        
        // Test Calculate the correct value.
        //String PixelPaddingValue = dataset.getString(Tag.PixelPaddingValue, null);
        //setPixelPaddingValue(PixelPaddingValue);
        
       
        // 0028,1051 Window Width
        //String[] WindowWidth = dataset.getStrings(Tag.WindowWidth);
        //windowWidth = stringArrayToDubleArray(WindowWidth);

        // 0028,0101 BitsStored
        //String BitsStoredValue = dataset.getString(Tag.BitsStored, null);
        //setBitsStoredValue(BitsStoredValue);

        // 0028,0106 Smallest Image Pixel Value
        //String SmallestImagePixelValue = dataset.getString(Tag.SmallestImagePixelValue, null);
        //setSmallestImagePixelValue(SmallestImagePixelValue);
        
        //String LargestImagePixelValue = dataset.getString(Tag.LargestImagePixelValue, null);
        //setLargestImagePixelValue(LargestImagePixelValue);
        
        //String VOILUTSequence = dataset.getString(Tag.VOILUTSequence, null);
        //setVOILUTSequenceValue(VOILUTSequence);
        
        //String LUTDescriptor = dataset.getString(Tag.LUTDescriptor, null);
        //setLUTDescriptorValue(LUTDescriptor);
        
        //String LUTExplanation = dataset.getString(Tag.LUTDescriptor, null);
        //setLUTExplanationValue(LUTExplanation);
        
       
        
        // old
        //setPixelAspectRatioDataValue(PixelAspectRatio); 
        
        // old
        //String PixelSpacing[] = dataset.getStrings(Tag.PixelSpacing);
        //pixelSpacing = parseStringArrayToDubleArray(PixelSpacing);
        //setPixelSpacingDataValue(PixelSpacing);
        
        //String ImagerPixelSpacing = dataset.getString(Tag.ImagerPixelSpacing, null);
        //setImagerPixelSpacingDataValue(ImagerPixelSpacing);
        // 0028,1055 LO Window Center & Width Explanation
       
    }
    
    /**
     * Set 0008,0008 Image Type
     * @param ImageType
     */
    public void setImageType(String[] ImageType) {
        this.imageType = ImageType;
    }
    
     /**
     * Get 0008,0008 Image Type
     * @return imageType
     */
    public String[] getImageType() {
        return imageType;
    }
    
    /**
     * Set 0002,0010 Transfer Syntax UID
     * @param transferSyntaxUID
     */
    public void setTransferSyntaxUID(String transferSyntaxUID) {
        this.transferSyntaxUID = transferSyntaxUID;
    }
    
     /**
     * Get 0002,0010 Transfer Syntax UID
     * @return transferSyntaxUID
     */
    public String getTransferSyntaxUID() {
        return transferSyntaxUID;
    }
    
    /**
     * Set 0008,0060 Modality
     * @param Modality
     */
    public void setModality(String Modality) {
        this.modality = Modality ;
    }
    
     /**
     * Get 0008,0060 Modality
     * @return modality
     */
    public String getModality() {
        return modality;
    }
    
    /**
     * Set 0008,0070 Manufacturer
     * @param Manufacturer
     */
    public void setManufacturer(String Manufacturer) {
        this.manufacturer = Manufacturer;
    }
    
     /**
     * Get 0008,0070 Manufacturer
     * @return manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }
    
     /**
     * Set 0008,0080 Institution Name
     * @param InstitutionName
     */
    public void setInstitutionName(String InstitutionName) {
        this.institutionName = InstitutionName;
    }
    
     /**
     * Get 0008,0080 Institution Name
     * @return institutionName
     */
    public String getInstitutionName() {
        return institutionName;
    }
    
    /**
     * Set 0010,0010 Patient Name
     * @param PatientName
     */
    public void setPatientName(String PatientName) {
        this.patientName = PatientName;
    }
    
     /**
     * Get 0010,0010 Patient Name
     * @return patientName
     */
    public String getPatientName() {
        return patientName;
    }
    
    /**
     * Set 0010,0020 Patient ID
     * @param PatientID
     */
    public void setPatientID(String PatientID) {
        this.patientID = PatientID;
    }
    
     /**
     * Get 0010,0020 Patient ID
     * @return patientID
     */
    public String getPatientID() {
        return patientID;
    }
    
    /**
     * Set DICOM attribute 0018,1114 Estimated Radiographic Magnification Factor
     * @param 
     */
    private void setEstimatedRadiographicMagnificationFactorDataValue(String str) {
        double value = 0;

        if (str != null) {
            try {
                value = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM."
                        + "setEstimatedRadiongraphicMagnificationFactorDataValue:"
                        + "NumberFormatException");
            }
        }
        this.estimatedRadiographicMagnificationFactor = value;
    }
    
    /**
     * Get DICOM attribute 0018,1114 Estimated Radiographic Magnification Factor
     * @return estimatedRadiographicMagnificationFactor
     */
    public double getEstimatedRadiographicMagnificationFactor() {
        return estimatedRadiographicMagnificationFactor;
    }
    
    /**
     * Set 0018,1150 Exposure Time 
     * @param ExposureTime
     */
    public void setExposureTime(String[] ExposureTime) {
        this.exposureTime = ExposureTime;
    }
    
     /**
     * Get 0018,1150 Exposure Time 
     * @return exposureTime
     */
    public String[] getExposureTime() {
        return exposureTime;
    }
    
    /**
     * Set 0018,1152 Exposure 
     * @param Exposure
     */
    public void setExposure(String Exposure) {
        this.exposure_str = Exposure;
    }
    
     /**
     * Get 0018,1152 Exposure 
     * @return exposure
     */
    public String getExposure() {
        return exposure_str;
    }
    
     /**
     * Set 0018,1152 Exposure 
     * @param Exposure
     */
    public void setExposureValue(String Exposure) {
        int exp = 0;

        if (Exposure != null) {
            try {
                // there is a + sign to be removed
                //if(exposure.startsWith("+"))
                exp = Integer.parseInt(Exposure);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:getExposure: NumberFormatException");
            }
        }
        this.exposure = exp;
    }
    
    /**
     * Get 0018,1152 Exposure 
     * @return exposure
     */
    public int getExposureValue() {
        return exposure;
    }
    
    /**
     * Set 0018,1164 ImagerPixelSpacing
     * @param ImagerPixelSpacing
     */
    public void setImagerPixelSpacing(String[] ImagerPixelSpacing) {
        if(ImagerPixelSpacing != null){
            double[] ips = stringArrayToDubleArray(ImagerPixelSpacing);
            this.imagerPixelSpacing = ips;
        }
    }
    
     /**
     * Get 0018,1164 ImagerPixelSpacing
     * @return imagerPixelSpacing
     */
    public double[] getImagerPixelSpacing() {
        return imagerPixelSpacing;
    }
    
    /**
     * Set 0018,2010 NominalScannedPixelSpacing
     * @param NominalScannedPixelSpacing
     */
    public void setNominalScannedPixelSpacing(String[] NominalScannedPixelSpacing) {
        if(NominalScannedPixelSpacing != null){
            double[] nsps = stringArrayToDubleArray(NominalScannedPixelSpacing);
            this.nominalScannedPixelSpacing = nsps;
        }
    }
    
    /**
     * Get 0018,2010 NominalScannedPixelSpacing
     * @return nominalScannedPixelSpacing
     */
    public double[] getNominalScannedPixelSpacing() {
        return nominalScannedPixelSpacing;
    }
    
    /**
     * Set DICOM attribute 0028,0103 Pixel Representation
     * @param str 
     */
    private void setPixelRepresentationValue(String str) {
        int value = 0;

        if (str != null) {
            try {
                value = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setPixelRepresentationValue: NumberFormatException");
            }
        }
        this.pixelRepresentation = value;
    }
    
    /**
     * Get DICOM attribute 0028,0103 Pixel Representation
     * @return pixelRepresentation
     */
    public int getPixelRepresentationValue() {
        return pixelRepresentation;
    }
    
    /**
     * Set 0020,000D Study Instance UID 
     * @param StudyInstanceUID
     */
    public void setStudyInstanceUID(String StudyInstanceUID) {
        this.studyInstanceUID = StudyInstanceUID;
    }
    
     /**
     * Get 0020,000D Study Instance UID 
     * @return studyInstanceUID
     */
    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }
    
    /**
     * Set 0020,000E Series Instance UID
     * @param SeriesInstanceUID
     */
    public void setSeriesInstanceUID(String SeriesInstanceUID) {
        this.seriesInstanceUID = SeriesInstanceUID;
    }
    
     /**
     * Get 0020,000E Series Instance UID
     * @return seriesInstanceUID
     */
    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }
    
    /**
     * Set 0020,0011 Series Number
     * @param SeriesNumber
     */
    public void setSeriesNumber(String SeriesNumber) {
        this.seriesNumber = SeriesNumber;
    }
    
    /**
     * Get 0020,0011 Series Number
     * @return seriesNumber
     */
    public String getSeriesNumber() {
        return seriesNumber;
    }
    
    /**
     * Set 0028,0002 Samples per Pixel
     * @param SamplesPerPixel
     */
    public void setSamplesPerPixel(int SamplesPerPixel) {
        this.samplesPerPixel = SamplesPerPixel;
    }
    
    /**
     * Get 0028,0002 Samples per Pixel
     * @return SamplesPerPixel
     */
    public int getSamplesPerPixel() {
        return samplesPerPixel;
    }
    
    /**
     * Set 0028,0004 PhotoMetric Interpretation
     * @param PhotoMetricInterpretation
     */
    public void setPhotometricInterpretation(String PhotoMetricInterpretation) {
        this.photoMetricInterpretation = PhotoMetricInterpretation;
    }
    
    /**
     * Get 0028,0004 PhotoMetric Interpretation
     * @return photoMetricInterpretation
     */
    public String getPhotoMetricInterpretation() {
        return photoMetricInterpretation;
    }
    
    /**
     * Set 0028,0008
     * @param NumberOfFrames
     */
    public void setNumberOfFrames(String NumberOfFrames) {
        this.numberOfFrames = NumberOfFrames;
    }
    
    /**
     * Get 0028,0008 NumberOfFrames
     * @return numberOfFrames
     */
    public String getNumberOfFrames() {
        return numberOfFrames;
    }
    
     /**
     * Set 0028,0008
     * @param NumberOfFrames_int
     */
    public void setNumberOfFramesInt(int NumberOfFrames_int) {
        this.numberOfFrames_int = NumberOfFrames_int;
    }
    
    /**
     * Get 0028,0008 NumberOfFrames_int
     * @return numberOfFrames_int
     */
    public int getNumberOfFramesInt() {
        return numberOfFrames_int;
    }
    
    /**
     * Set xxxx,xxxx Instance Number (Deprecated?)
     * @param InstanceNumber
     */
    public void setInstanceNumber(int InstanceNumber) {
        this.instanceNumber = InstanceNumber;
    }
    
    /**
     * Get xxxx,xxxx Instance Number (Deprecated?)
     * @return instanceNumber
     */
    public int getInstanceNumber() {
        return instanceNumber;
    }
    
    /**
     * Set 0028,0010 Rows
     * @param Rows
     */
    public void setRows(int Rows) {
        this.rows = Rows;
    }
    
    /**
     * Get 0028,0010 Rows
     * @return rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Set 0028,0011 Columns
     * @param Columns
     */
    public void setColumns(int Columns) {
        this.columns = Columns;
    }
    
    /**
     * Get 0028,0011 Columns
     * @return columns
     */
    public int getColumns() {
        return columns;
    }
    
    /**
     * Set 0028,0030 PixelSpacing
     * @param PixelSpacing
     */
    public void setPixelSpacing(String[] PixelSpacing) {
        if(PixelSpacing != null){
            double[] ps = stringArrayToDubleArray(PixelSpacing);
            this.pixelSpacing = ps;
        }
    }
    
    /**
     * Get 0028,0030 PixelSpacing
     * @return pixelSpacing
     */
    public double[] getPixelSpacing() {
        return pixelSpacing ;
    }
    
    /**
     * Set 0028,0034 Pixel Aspect Ratio
     * @param PixelAspectRatio
     */
    public void setPixelAspectRatio(String[] PixelAspectRatio) {
        if(PixelAspectRatio != null){
            int[] par = stringArrayToIntArray(PixelAspectRatio);
            this.pixelAspectRatio = par;
        }
    }
    
    /**
     * Get 0028,0034 Pixel Aspect Ratio
     * @return PixelAspectRatio
     */
    public int[] getPixelAspectRatio() {
        return pixelAspectRatio ;
    }
    
    /**
     * Set 0028,0100 BitsAllocated
     * @param BitsAllocated
     */
    public void setBitsAllocated(int BitsAllocated) {
        this.bitsAllocated = BitsAllocated;
    }
    
    /**
     * Get 0028,0100 BitsAllocated
     * @return bitsAllocated
     */
    public int getBitsAllocated() {
        return bitsAllocated;
    }
    
    /**
     * Set 0028,0101 BitsStored
     * @param BitsStored
     */
    public void setBitsStored(int BitsStored) {
        this.bitsStored = BitsStored;
    }
    
    /**
     * Get 0028,0101 BitsStored
     * @return bitsStored
     */
    public int getBitsStored() {
        return bitsStored;
    }
    
    /**
     * Set 0028,0102 HighBit
     * @param HighBit
     */
    public void setHighBit(int HighBit) {
        this.highBit = HighBit;
    }
    
    /**
     * Get 0028,0102 HighBit
     * @return highBit
     */
    public int getHighBit() {
        return highBit;
    }
    
    /**
     * Set 0028,0103 Pixel Representation
     * @param PixelRepresentation
     */
    public void setPixelRepresentation(int PixelRepresentation) {
        this.pixelRepresentation = PixelRepresentation;
    }
    
    /**
     * Get 0028,0103 Pixel Representation
     * @return pixelRepresentation
     */
    public int getPixelRepresentation() {
        return pixelRepresentation;
    }
    
    /**
     * Set 0028,0106 Smallest Image Pixel Value
     * @param SmallestImagePixelValue
     */
    public void setSmallestImagePixelValue(int SmallestImagePixelValue) {
        this.smallestImagePixelValue = SmallestImagePixelValue;
    }
    
    /**
     * Get 0028,0106 Smallest Image Pixel Value
     * @return smallestImagePixelValue
     */
    public int getSmallestImagePixelValue() {
        return smallestImagePixelValue;
    }
    
    /**
     * Set 0028,0107 Largest Image Pixel Value 
     * @param LargestImagePixelValue
     */
    public void setLargestImagePixelValue(int LargestImagePixelValue) {
        this.largestImagePixelValue = LargestImagePixelValue;
    }
    
    /**
     * Get 0028,0107 Largest Image Pixel Value 
     * @return largestImagePixelValue
     */
    public int getLargestImagePixelValue() {
        return largestImagePixelValue;
    }
    
    /**
     * Set 0028,0120 Pixel Padding Value
     * @param PixelPaddingValue
     */
    public void setPixelPaddingValue(int PixelPaddingValue) {
        this.pixelPaddingValue = PixelPaddingValue;
    }
    
    /**
     * Get 0028,0120 Pixel Padding Value
     * @return pixelPaddingValue
     */
    public int getPixelPaddingValue() {
        return pixelPaddingValue;
    }
    
    /**
     * Set 0028,0120 Pixel Padding Value
     * @param PixelPaddingValueValue
     */
    public void setPixelPaddingValueValue(String PixelPaddingValueValue){
        int value = Integer.MIN_VALUE;
        if (PixelPaddingValueValue != null) {
            try {
                value = Integer.parseInt(PixelPaddingValueValue);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setPixelPaddingValue: NumberFormatException");
            }
            this.pixelPaddingValue = value;
        }
    }
    
    /**
     * Get 0028,0120 Pixel Padding Value
     * @return pixelPaddingValue
     */
    public int getPixelPaddingValueValue() {
        return pixelPaddingValue;
    }
    
    /**
     * Set 0028,0300 Quality Control Image
     * @param QualityControlImage
     */
    public void setQualityControlImage(String QualityControlImage) {
        this.qualityControlImage = QualityControlImage;
    }
    
    /**
     * Get 0028,0300 Quality Control Image
     * @return qualityControlImage
     */
    public String getQualityControlImage() {
        return qualityControlImage;
    }
    
    /**
     * Set 0028,0301 Burned In Annotation
     * @param BurnedInAnnotation
     */
    public void setBurnedInAnnotation(String BurnedInAnnotation) {
        this.burnedInAnnotation = BurnedInAnnotation;
    }
    
    /**
     * Get 0028,0301 Burned In Annotation
     * @return burnedInAnnotation
     */
    public String getBurnedInAnnotation() {
        return burnedInAnnotation;
    }
    
    /**
     * Set 0028,0A02 Pixel Spacing Calibration Type
     * @param PixelSpacingCalibrationType
     */
    public void setPixelSpacingCalibrationType(String PixelSpacingCalibrationType) {
        this.pixelSpacingCalibrationType = PixelSpacingCalibrationType;
    }
    
    /**
     * Get 0028,0A02 Pixel Spacing Calibration Type
     * @return pixelSpacingCalibrationType
     */
    public String getPixelSpacingCalibrationType() {
        return pixelSpacingCalibrationType;
    }
    
    /**
     * Set 0028,0A04 Pixel Spacing Calibration Description
     * @param PixelSpacingCalibrationDescription
     */
    public void setPixelSpacingCalibrationDescription(String PixelSpacingCalibrationDescription) {
        this.pixelSpacingCalibrationDescription = PixelSpacingCalibrationDescription;
    }
    
    /**
     * Get 0028,0A04 Pixel Spacing Calibration Description
     * @return pixelSpacingCalibrationDescription
     */
    public String getPixelSpacingCalibrationDescription() {
        return pixelSpacingCalibrationDescription;
    }
    
    /**
     * Set 0028,1040 Pixel Intensity Relationship
     * @param PixelIntensityRelationship
     */
    public void setPixelIntensityRelationship(String PixelIntensityRelationship) {
        this.pixelIntensityRelationship = PixelIntensityRelationship;
    }
    
    /**
     * Get 0028,1040 Pixel Intensity Relationship
     * @return pixelIntensityRelationship
     */
    public String getPixelIntensityRelationship() {
        return pixelIntensityRelationship;
    }
    
    /**
     * Set 0028,1041 Pixel Intensity Relationship Sign
     * @param PixelIntensityRelationshipSign
     */
    public void setPixelIntensityRelationshipSign(int PixelIntensityRelationshipSign) {
        this.pixelIntensityRelationshipSign = PixelIntensityRelationshipSign;
    }
    
    /**
     * Get 0028,1041 Pixel Intensity Relationship Sign
     * @return pixelIntensityRelationshipSign
     */
    public int getPixelIntensityRelationshipSign() {
        return pixelIntensityRelationshipSign;
    }
    
    /**
     * Set 0028,1050 Window Center
     * @param WindowCenter
     */
    public void setWindowCenter(String[] WindowCenter) {
        if(WindowCenter != null){
            double[] wc = stringArrayToDubleArray(WindowCenter);
            this.windowCenter = wc;
        }
        else
            this.windowCenter = null;
    }
    
     /**
     * Get 0028,1050 Window Center
     * @return windowCenter
     */
    public double[] getWindowCenter() {
        return windowCenter;
    }
    
    /**
     * Set 0028,1050 Window Center
     * @param WindowCenter_str
     */
    public void setWindowCenter_str(String WindowCenter_str) {
        this.windowCenter_str = WindowCenter_str;
    }
    
     /**
     * Get 0028,1050 Window Center
     * @return windowCenter_str
     */
    public String getWindowCenter_str() {
        return windowCenter_str;
    }
    
    /**
     * Set 0028,1050 Window Width
     * @param WindowWidth
     */
    public void setWindowWidth(String[] WindowWidth) {
        if(WindowWidth != null){
            double[] ww = stringArrayToDubleArray(WindowWidth);
            this.windowWidth = ww;
        }
        else
            this.windowWidth = null;     
    }
    
     /**
     * Get 0028,1050 Window Width
     * @return windowWidth
     */
    public double[] getWindowWidth() {
        return windowWidth;
    }
    
    /**
     * Set Window Width (0028,1051)
     * @param str
     */
    public void setWindowWidth_int_array(String str) {
        int[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new int[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = (int) Float.parseFloat(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM:setWindowWidthMultipleDataValue: NumberFormatException");
                }
            }
        }
        this.windowWidth_int_array = buf;
    }
    
    /**
     * Get Window Width (0028,1051)
     */
    public int[] getWindowWidth_int_array() {
        return windowWidth_int_array;
    }
    
    /**
     * Set Window Width float value (0028,1051)
     * This is a fix for MR images like the following or alike
     * 0028,1050 Window Center: 0.5
     * 0028,1051 Window Width: 1
     * 0028,1052 Rescale Intercept: 0
     * 0028,1053 Rescale Slope: 0,00024... 1/4096
     * @param str
     */
    public void setWindowWidth_double_array(String str) {
        double[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new double[cnt];
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
                    System.out.println("ImageLoaderDICOM:setWindowWidthMultipleDataValue: NumberFormatException");
                }
            }
        }
        this.windowWidth_double_array = buf;
    }
    
    /**
     * Get Window Width (0028,1051)
     * @return windowWidth_double_array
     */
    public double[] getWindowWidth_double_array() {
        return windowWidth_double_array;
    }
   
    /**
     * Set Window Center float value(0028,1050)
     * This is a fix for MR images like the following or alike
     * 0028,1050 Window Center: 0.5
     * 0028,1051 Window Width: 1
     * 0028,1052 Rescale Intercept: 0
     * 0028,1053 Rescale Slope: 0,00024... 1/4096
     */
    public void setWindowCenter_double_array(String str) {
        double[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new double[cnt];
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
        this.windowCenter_double_array = buf;
    }

    /**
     * Get Window Center (0028,1050)
     * @return windowCenter_float_array
     */
    public double[] getWindowCenter_double_array() {
        return windowCenter_double_array;
    }
    
    /**
     * Set Rows (0028,0010)
     * @param str
     */
    public void setRowsDataValue(String str) {
        String a = null;
        //int b = 0x8FFFFFFF;
        int b = Integer.MIN_VALUE;

        if (str != null) {
            try {
                a = str.trim();
                b = Integer.parseInt(a);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setRowsValue: NumberFormatException");
            }
        }
        this.rows_int = b;
    }
    
    /**
     * Get DICOM attribute 0028,0010 Rows
     * @return rows
     */
    public int getRowsDataValue() {
        return rows_int;
    }
    
    /**
     * Set Columns (0028,0011)
     * @param str
     */
    public void setColumnsDataValue(String str) {
        String a = null;
        int b = Integer.MIN_VALUE;

        if (str != null) {
            try {
                a = str.trim();
                b = Integer.parseInt(a);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setColumnsValue: NumberFormatException");
            }
        }
        this.columns_int = b;
    }
    
     /**
     * Get Columns (0028,0011)
     */
    public int getColumnsDataValue() {
        return columns_int;
    }
    //kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
    
     /**
     * Set Window Center (0028,1050)
     * @param str
     */
    public void setWindowCenter_int_array(String str) {
        int[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new int[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = (int)Float.parseFloat(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM:setWindowCenterMultipleDataValue: NumberFormatException");
                }
            }
        }
     
        this.windowCenter_int_array = buf;
    }
    
    /**
     * Get Window Center (0028,1050)
     * @return windowCenter_int_array
     */
    public int[] getWindowCenter_int_array() {
        return  windowCenter_int_array;
    }
    
    /**
     * Set 0028,1054 Rescale Type
     * @param RescaleType
     */
    public void setRescaleType(String RescaleType) {
        this.rescaleType = RescaleType;
    }
    
    /**
     * Get 0028,1054 Rescale Type
     * @return rescaleType
     */
    public String getRescaleType() {
        return rescaleType;
    }
    
    /**
     * Set 0028,1055 Window Center & Width Explanation
     * @param WindowCenterWidthExplanation
     */
    /*
    public void setWindowCenterWidthExplanation_test(String[] WindowCenterWidthExplanation) {
        double[] wcwe = stringArrayToDubleArray(WindowCenterWidthExplanation);
        this.windowCenterWidthExplanation= wcwe;
    }
    */
    
    /**
     * Set 0028,1055 Window Center & Width Explanation
     * @param WindowCenterWidthExplanation
     */
    public void setWindowCenterWidthExplanation(String[] WindowCenterWidthExplanation) {
        this.windowCenterWidthExplanation = WindowCenterWidthExplanation;
    }
    
    /**
     * Get 0028,1055 Window Center & Width Explanation
     * @return windowCenterWidthExplanation
     */
    public String[] getWindowCenterWidthExplanation() {
        return windowCenterWidthExplanation;
    }
    
    /**
     * Set 0028,3000 Modality LUT Sequence 
     * @param ModalityLUTSequence
     */
    public void setModalityLUTSequence(String[] ModalityLUTSequence) {
        this.modalityLUTSequence = ModalityLUTSequence;
    }
    
    /**
     * Get 0028,3000 Modality LUT Sequence 
     * @return modalityLUTsequence
     */
    public String[] getModalityLUTSequence() {
        return modalityLUTSequence;
    }
    
    /**
     * Set 0028,3000 Modality LUT Sequence 
     * @param ModalityLUTSequence_str
     */
    public void setModalityLUTSequenceString(String ModalityLUTSequence_str) {
        this.modalityLUTSequence_str = ModalityLUTSequence_str;
    }
    
    /**
     * Get 0028,3000 Modality LUT Sequence 
     * @return modalityLUTsequence_str
     */
    public String getModalityLUTSequenceString() {
        return modalityLUTSequence_str;
    }
    
    /**
     * Set 0028,3006 LUT Data 
     * @param LutData
     */
    public void setLutData(int LutData) {
        this.lutData = LutData;
    }
    
    /**
     * Get 0028,3006 LUT Data 
     * @return lutData
     */
    public int getLutData() {
        return lutData;
    }
    
    /**
     * Set LUT Data value (0028,3006).
     */
    public void setLUTDataValue(String str) {
        short[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new short[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                String strX = "0x" + str2;
                try {
                    buf[j] = Short.decode(strX);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("StudyLoader:getTagValue: NumberFormatException");
                }
            }
        }
        this.lutData_short_array = buf;
    }

    /**
     * Get the LUT Data value (0028,3006)
     */
    public short[] getLUTDataValue() {
        return lutData_short_array;
    }
    
    /**
     * Set DICOM attribute 0028,0120 Pixel Padding
     * @param str 
     */
    private void setPixelPaddingValue(String str) {
        int value = Integer.MIN_VALUE;

        if (str != null) {
            try {
                value = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setPixelPaddingValue: NumberFormatException");
            }
        }
        pixelPaddingValue = value;
    }

    /**
     * 
     * Get DICOM attribute 0028,0120 Pixel Padding
     * @return pixelPaddingValue
     */
    /*
    public int getPixelPaddingValue() {
        return pixelPaddingValue;
    }
    */
    
    /**
     * Set 0028,1052 Rescale Intercept
     * @param str 
     */
    private void setRescaleInterceptValue(String str) {
        double value = Double.MIN_VALUE;

        //Examples
        //Siemens "-01.024000E+03"
        //Siemens "-1024"
        if (str != null) {
            try {
                value = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setRescaleInterceptValue: NumberFormatException");
            }
        }
        this.rescaleIntercept = value;
    }

    /**
     * Get DICOM attribute 0028,1052 Rescale Intercept
     * @return rescaleIntercept
     */
    public double getRescaleInterceptValue() {
        return rescaleIntercept;
    }
    
    /**
     * Set 0028,1053 Rescale Slope
     * @param str 
     */
    private void setRescaleSlopeValue(String str) {
        double value = Double.MIN_VALUE;

        //Siemens "001.000000E+00"
        if (str != null) {
            try {
                value = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setRescaleSlopeValue: NumberFormatException");
            }
        }
        this.rescaleSlope = value;
    }

    /**
     * Get 0028,1053 Rescale Slope
     * @return rescaleSlope
     */
    public double getRescaleSlopeValue() {
        return rescaleSlope;
    }
    
    /**
     * Set windowCenterFloatValueExist status
     * True if 0028,1050 Window Center attribute represents a float value
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
        
        /*
        for(int i = 0; i < buf.length; i++){
            if(buf[i] % 1 != 0){
                windowCenterValueFloatStatus = true;
            }
        }
         */
    }
    
    /**
     * Set DICOM attribute 0028,0030 DS PixelSpacing
     * @param str
     */
    /*
    public void setPixelSpacingDataValue(String str) {
        double[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        //   StringTokenizer st = new StringTokenizer(str, System.getProperty("file.separator"));
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new double[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = Double.parseDouble(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM.setPixelSpacingDataValue: NumberFormatException");
                }
            }
        }
        pixelSpacing = buf;
    }
    */

    /**
     * Get DICOM attribute 0028,0030 DS PixelSpacing
     * @return
     */
    public double[] getPixelSpacingValue() {
        return pixelSpacing;
    }
    
    /**
     * Set DICOM attribute 0018,1164 ImagerPixelSpacing
     * @param str
     */
    public void setImagerPixelSpacingDataValue(String str) {
        double[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        //  StringTokenizer st = new StringTokenizer(str, System.getProperty("file.separator"));
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new double[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = Double.parseDouble(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM.setImagerPixelSpacingDataValue: NumberFormatException");
                }
            }
        }
        imagerPixelSpacing = buf;
    }
    
    /**
     * Convert String array to double array.
     * @param str array
     * @return double array
     */
    public double[] stringArrayToDubleArray(String[] str) {
        double[] buf = new double[str.length];
        
        for (int i=0; i<str.length; i++){
            buf[i] = Double.valueOf(str[i]);
        }
        return buf;
    }
    
    /**
     * Convert String array to int array.
     * @param str array
     * @return int array
     */
    public int[] stringArrayToIntArray(String[] str) {
        int[] buf = new int[str.length];
        
        for (int i=0; i<str.length; i++){
            buf[i] = Integer.parseInt(str[i]);
        }
        return buf;
    }

    /**
     * Get DICOM attribute 0018,1164 ImagerPixelSpacing
     * @return
     */
    public double[] getImagerPixelSpacingValue() {
        return imagerPixelSpacing;
    }
    
    /**
     * Set DICOM attribute 0028,0A02 Pixel Spacing Calibration Type
     * @param str
     */
    /*
    public void setPixelSpacingCalibrationTypeDataValue(String str) {
        String b = null;

        if (str != null) {
            b = str.trim();
        }
        pixelSpacingCalibrationType = b;
    }
    */
    
    /**
     * Get DICOM attribute 0028,0A02 Pixel Spacing Calibration Type
     * @return
     */
    /*
    public String getPixelSpacingCalibrationTypeValue() {
        return pixelSpacingCalibrationType;
    }
    */
    
    /**
     * Set DICOM attribute 0028,0A04 PixelSpacingCalibrationDescription
     * @param str
     */
    public void setPixelSpacingCalibrationDescriptionDataValue(String str) {
        String b = null;

        if (str != null) {
            b = str.trim();
        }
        pixelSpacingCalibrationDescription = b;
    }

    /**
     * Get DICOM attribute 0028,0A04 PixelSpacingCalibrationDescription
     * @return
     */
    public String getPixelSpacingCalibrationDescriptionValue() {
        return pixelSpacingCalibrationDescription;
    }
    
    /**
     * Set DICOM attribute 0018,2010 NominalScannedPixelSpacing
     * @param str
     */
    public void setNominalScannedPixelSpacingDataValue(String str) {
        double[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        //  StringTokenizer st = new StringTokenizer(str, System.getProperty("file.separator"));
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new double[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = Double.parseDouble(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM.setNominalScannedPixelSpacingDataValue: NumberFormatException");
                }
            }
        }
        nominalScannedPixelSpacing = buf;
    }

    /**
     * Get DICOM attribute 0018,2010 NominalScannedPixelSpacing
     * @return
     */
    public double[] getNominalScannedPixelSpacingValue() {
        return nominalScannedPixelSpacing;
    }
    
    
    /**
     * Set DICOM attribute 0028,0034 Pixel Aspect Ratio
     * @param str
     */
    /*
    public void setPixelAspectRatioDataValue(String str) {
        int[] buf = null;

        if (str == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(str, "\\");
        //    StringTokenizer st = new StringTokenizer(str, System.getProperty("file.separator"));
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new int[cnt];
            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                //String strX = "0x" + str2;
                try {
                    //buf[j] = Short.decode(str2);
                    //buf[j] = Short.valueOf(str2);
                    buf[j] = Integer.parseInt(str2);
                    j++;
                } catch (NumberFormatException e) {
                    System.out.println("ImageLoaderDICOM.setPixelAspectRatioDataValue: NumberFormatException");
                }
            }
        }
        pixelAspectRatio = buf;
    }
    */
    
    
    /**
     * Get DICOM attribute 0028,0034 Pixel Aspect Ratio
     * @return
     */
    /*
    public int[] getPixelAspectRatioValue() {
        return pixelAspectRatio;
    }
    */
    
    /**
     * Set DICOM attribute 0028,0101 BitsStored
     * @param str 
     */
    private void setBitsStoredValue(String str) {
        int b = 0x8FFFFFFF;

        if (str != null) {
            try {
                b = (int) Float.parseFloat(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setBitStoredValue: NumberFormatException");
            }
        }
        this.bitsStored = b;
    }
    
    /**
     * Get DICOM attribute 0028,0101 BitsStored
     * @return bitsStored
     */
    public int getBitsStoredValue() {
        return bitsStored;
    }
    
    /**
     * Set DICOM attribute 0028,0100 BitsAllocated
     * @param str 
     */
    private void setBitsAllocatedValue(String str) {
        int b = 0x8FFFFFFF;

        if (str != null) {
            try {
                b = (int) Float.parseFloat(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setBitsAllocatedValue: NumberFormatException");
            }
        }
        this.bitsAllocated = b;
    }

    /**
     * Get DICOM attribute 0028,0100 BitsAllocated
     * @return bitsAllocated
     */
    public int getBitsAllocatedValue() {
        return bitsAllocated;
    }
    
    /**
     * Set DICOM attribute 0028,0106 Smallest Image Pixel Value
     * @param str
     */
    private void setSmallestImagePixelValue(String str) {
        int b = 0x8FFFFFFF;

        if (str != null) {
            try {
                b = (int) Float.parseFloat(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setSmallestImagePixelValue: NumberFormatException");
            }
        }
        smallestImagePixelValue = b;
    }

    /**
     * Get DICOM attribute 0028,0106 Smallest Image Pixel Value
     * @return smallestImagePixelValue
     */
    //public int getSmallestImagePixelValue() {
      //  return smallestImagePixelValue;
    //}
    
    /**
     * Set DICOM attribute 0028,0107 Largest Image Pixel Value
     * @param 
     */
    private void setLargestImagePixelValue(String str) {
        int b = 0x8FFFFFFF;

        if (str != null) {
            try {
                b = (int) Float.parseFloat(str);
            } catch (NumberFormatException e) {
                System.out.println("ImageLoaderDICOM:setLargesstImagePixelValue: NumberFormatException");
            }
        }
        largestImagePixelValue = b;
    }

    /**
     * Get DICOM attribute 0028,0107 Largest Image Pixel Value
     * @return largestImagePixelValue
     */
    /*
    public int getLargestImagePixelValue() {
        return largestImagePixelValue;
    }
    */
    /**
     * Set DICOM attribute 0028,3010 VOI LUT Sequence
     * @param str
     */
    public void setVOILUTSequenceValue(String str) {
        String b = null;

        if (str != null) {
            b = str.trim();
        }
        this.voiLUTSequence = b;
    }
    
    /**
     * Get DICOM attribute 0028,3010 VOI LUT Sequence
     * @return 
     */
    public String getVOILUTSequenceValue() {
        return voiLUTSequence;
    }
    
     /**
     * Set DICOM attribute 0028,3002 LUT Descriptor
     * See DICOM PS 3.3-2004, page 697, C.11-2
     * @param str
     */
    public void setLUTDescriptorValue(String str) {
        short[] buf = null;

        if (str == null) {
            return;
        }

        //  StringTokenizer st = new StringTokenizer(str, "\\");
        //  StringTokenizer st = new StringTokenizer(str, System.getProperty("file.separator"));
        StringTokenizer st = new StringTokenizer(str, "\\");
        int cnt = st.countTokens();

        if (cnt > 0) {
            buf = new short[cnt];

            int j = 0;
            while (st.hasMoreTokens()) {
                String str2 = st.nextToken().trim();
                buf[j] = Short.parseShort(str2);
                j++;
            }
        }
        this.lutDescriptor = buf;
    }
    
    /**
     * Get 0028,3002 LUT Descriptor
     * @return lutDescriptor
     */
    public short[] getLutDescriptorValue() {
        return lutDescriptor;
    }
    
    /**
     * Set DICOM attributes 0028,3003 LUT Explanation
     * @param str
     */
    public void setLUTExplanationValue(String str) {
        String b = null;

        if (str != null) {
            b = str.trim();
        }
        this.lutExplanation = b;
    }
    
     /**
     * Get DICOM attributes 0028,3003 LUT Explanation
     * @return 
     */
    public String getLUTExplanationValue() {
        return lutExplanation;
    }
    
    /**
     * Print out selected DICOM attributes
     */
    public void printDataset(){
        System.out.println("DicomFileReaderMethod3.printDataSet()");
        System.out.println("");
        System.out.println("cnt = " + cnt++);
        
        System.out.println("0008,0008 CS Image Type: " + Arrays.toString(getImageType()));
        System.out.println("0002,0010 UI Transfer Syntax UID: " + getTransferSyntaxUID());
        System.out.println("0008,0060 CS Modality: " + getModality());
        System.out.println("0008,0070 LO Manufacturer: " + getManufacturer());
        System.out.println("0008,0080 LO Institution Name: " + getInstitutionName());
        System.out.println("0010,0010 PN Patient Name: " + getPatientName());
        System.out.println("0010,0020 LO Patient ID: " + getPatientID());
        System.out.println("0018,1114 DS Estimated Radiographic Manification: " + getEstimatedRadiographicMagnificationFactor());
        System.out.println("0018,1150 IS Exposure Time: " + Arrays.toString(getExposureTime()));
        System.out.println("0018,1164 DS ImagerPixelSpacing: " + Arrays.toString(getImagerPixelSpacing()));
        System.out.println("0018,2010 DS NominalScannedPixelSpacing: " + Arrays.toString(getNominalScannedPixelSpacing()));
        System.out.println("0020,000D UI Study Instance UID: " + getStudyInstanceUID());
        System.out.println("0020,000E UI Series Instance UID: " + getSeriesInstanceUID());
        System.out.println("0020,0011 IS Series Number: " + getSeriesNumber());
        System.out.println("0028,0002 US Samples per Pixel: " + getSamplesPerPixel());
        System.out.println("0028,0004 CS PhotoMetric Interpretation: " + getPhotoMetricInterpretation());
        System.out.println("0028,0010 CS Rows: " + getRows());
        System.out.println("0028,0011 US Columns: " + getColumns());
        System.out.println("0028,0030 DS PixelSpacing: " + Arrays.toString(pixelSpacing));
        System.out.println("0028,0034 IS Pixel Aspect Ratio: " + Arrays.toString(getPixelAspectRatio()));
        System.out.println("0028,0100 US Bits Allocated: " + getBitsAllocated());
        System.out.println("0028,0101 US BitsStored: " + getBitsStored());
        System.out.println("0028,0102 US High Bit: " + getHighBit());
        System.out.println("0028,0103 US Pixel Representation: " + getPixelRepresentation());
        System.out.println("0028,0106 US,SS Smallest Image Pixel Value: " + getSmallestImagePixelValue());
        System.out.println("0028,0111 US,SS Largest Image Pixel Value: " + getLargestImagePixelValue());
        System.out.println("0028,0120 US,SS Pixel Padding Value: " + getPixelPaddingValue());
        System.out.println("0028,0300 CS Quality Control Image: " + getQualityControlImage());
        System.out.println("0028,0301 CS Burned In Annotation: " + getBurnedInAnnotation());
        System.out.println("0028,0A02 CS Pixel Spacing Calibration Type: " + getPixelSpacingCalibrationType());
        System.out.println("0028,0A04 Pixel Spacing Calibration Description: " + getPixelSpacingCalibrationDescription());
        System.out.println("0028,1040 CS Pixel Intensity Relationship: " + getPixelIntensityRelationship());
        System.out.println("0028,1041 SS Pixel Intensity Relationship Sign: " + getPixelIntensityRelationshipSign());
        System.out.println("0028,1050 DS Window Center: " + Arrays.toString(getWindowCenter()));
        System.out.println("0028,1051 DS Window Width: " + Arrays.toString(getWindowWidth()));
        System.out.println("0028,1052 DS Rescale Intercept: " + getRescaleInterceptValue());
        System.out.println("0028,1053 DS Rescale Slope: " + getRescaleSlopeValue());
        System.out.println("0028,1054 LO Rescale Type: " + getRescaleType());
        System.out.println("0028,1055 LO Window Center & Width Explanation: " + Arrays.toString(getWindowCenterWidthExplanation()));
        System.out.println("0028,3000 SQ Modality LUT Sequence: " + Arrays.toString(getModalityLUTSequence()));
        System.out.println("0028,3002 US LUT Descriptor: " + Arrays.toString(getLutDescriptorValue()));
        System.out.println("0028,3003 LO LUT Explanation: " + getLUTExplanationValue());
        System.out.println("0028,3006 US LUT Data: " + getLutData());
        System.out.println("0028,3006 US LUT Data: " + Arrays.toString(getLUTDataValue()));
        System.out.println("0028,3010 SQ VOI LUT Sequence: " + getVOILUTSequenceValue());
        System.out.println();
    }
}
    
    /**
        transferSyntaxUID = dataset.getString(Tag.TransferSyntaxUID);
        manufacturer = dataset.getString(Tag.Manufacturer);
        institutionName = dataset.getString(Tag.InstitutionName);
        imagerPixelSpacing = dataset.getStrings(Tag.ImagerPixelSpacing);
        nominalScannedPixelSpacing = dataset.getStrings(Tag.NominalScannedPixelSpacing);
        estimatedRadiographicMagnificationFactor = dataset.getDouble(Tag.EstimatedRadiographicMagnificationFactor, 0.0);
        // NullPointerException!! exposureTime = dataset.getStrings(Tag.ExposureTime);
        seriesNumber = dataset.getString(Tag.InstanceNumber);
       
        rows = dataset.getInt(Tag.Rows, 8);
        columns = dataset.getInt(Tag.Columns, 8);
        pixelSpacing = dataset.getStrings(Tag.PixelSpacing);
        pixelAspectRatio = dataset.getStrings(Tag.PixelAspectRatio);
        pixelPaddingValue = dataset.getInt(Tag.PixelPaddingValue, 0);
        bitsStored = dataset.getInt(Tag.BitsStored, 8);
        smallestImagePixelValue = dataset.getInt(Tag.SmallestImagePixelValue, 0);
        largestImagePixelValue = dataset.getInt(Tag.LargestImagePixelValue, 0);
        
        burnedInAnnotation = dataset.getString(Tag.BurnedInAnnotation);
        pixelSpacingCalibrationType = dataset.getString(Tag.PixelSpacingCalibrationType);
        pixelSpacingCalibrationDescription = dataset.getString(Tag.PixelSpacingCalibrationDescription);
       
        pixelIntensityRelationshipSign = dataset.getInt(Tag.PixelIntensityRelationshipSign, 0);
        windowCenter = dataset.getString(Tag.WindowCenter);
        windowWith = dataset.getString(Tag.WindowWidth);
        rescaleIntercept = dataset.getInt(Tag.RescaleIntercept, 0);
        modalityLUTsequence = dataset.getStrings(Tag.ModalityLUTSequence);
        VOILUTSequence = dataset.getStrings(Tag.VOILUTSequence);
        LUTDescriptor = dataset.getInt(Tag.LUTDescriptor, 0);
        LUTExplanation = dataset.getString(Tag.LUTExplanation);
        LUTData = dataset.getInt(Tag.LUTData, 0);
        **/
