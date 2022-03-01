/* @(#) StudyDbImageNode.java 06/09/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import javax.media.jai.PlanarImage;
import mft.vdex.modules.vg.*;
//import org.dcm4che.data.Dataset;
import org.dcm4che3.data.Attributes;
//import org.dcm4che.image.ColorModelParam;


/**
 * The <code>StudyDbImageNode</code> class stores information
 * about a single image (name, path, dicom meta information ...)
 *
 * The <code>VgTaskPanelResult</code> list, stores the answering
 * results from the Task panel Checkbox.
 * 
 * The <code>StudyDbLocalization</code> list stores the positions
 * of the localization mark.
 * 
 * The <code>StudyDbROI</code> list stores the positions and shapes.
 */
public class StudyDbImageNode implements Serializable, Comparable{
    private int itemCnt = 0;
    private int imageNo = 0;
    private File studyPath;
    private File studyImageDbRoot;
    private String studyName;
    
    private ArrayList <VgTaskPanelResult> taskPanelResultList;
    private ArrayList <StudyDbLocalization> localizationList = null;
    private ArrayList <StudyDbROI> roiDistanceList = null;
    private ArrayList <StudyDbROI> roiVolumeList = null;
    private ArrayList <StudyDbROIPixelValue> roiPixelValueList = null;
 
    private Attributes dataset;
    
    //private transient BufferedImage image;
    private String patientName;
    private String patientID;
    private String studyInstanceUID;
    private String photometricInterpretation;
    private int[] windowWidth, windowCenter;
    private int windowWidthAdjusted, windowCenterAdjusted;
    private int windowCenterCorrected;
    private int bitsStored;
    private int bitsAllocated;
    private int rows;
    private int columns;
    private int instanceNumber = Integer.MIN_VALUE;
    private String userName;
    private String modality;
    private boolean windowCenterOffsetStatus;
    private boolean imageDone;
    private int[][] imgStat;
    private transient PlanarImage orgImage = null;
    private transient PlanarImage orgBackImage = null;
    private Point2D localization = null;
    private double rescaleIntercept;
    private double rescaleSlope;
    private int pixelRepresentation;
    //private ColorModelParam cmParam;
    private boolean modalityLUTSequenceStatus;
    private boolean voiLUTSequenceStatus;
    private boolean rescaleSlopeInterceptStatus;
    private boolean centerWidthStatus;
    private boolean identityStatus;
    
    // mesurement
    private double[] pixelSpacing;                     // Pixel Spacing  (0028,0030)
    private double[] imagerPixelSpacing;               // Imager Pixel Spacing  (0018,1164)
    private String pixelSpacingCalibrationType;        // Pixel Spacing Calibration Type (0028,0A02)
    private String pixelSpacingCalibrationDescription; // Pixel Spacing Calibration Description (0028,0A04)
    private double[] nominalScannedPixelSpacing;      // Norminal Scanned Pixel Spacing  (0018,2010)
    private int[] pixelAspectRatio;                    // Pixel Aspect Ratio (0028,0034)
    private double estimatedRadiographicMagnificationFactor; //EstimatedRadiographicMagnificationFactor (0018.1114)
    
    // case evaluation time
    private long startTime = -1;
    private long stopTime = -1;
    private long totalTime = -1;

    // Image rendering to localization timestamp
    private long timeStampImageRendering = 0;
    
    
    /** Constructor
     */
    public StudyDbImageNode(){
    }
    
    public StudyDbImageNode(File file, int cnt, File dbpath, String studyname){
        this.itemCnt = cnt;
        this.studyPath = file;


        this.studyImageDbRoot = dbpath;
        this.studyName = studyname;
        
        init();
    }
    
     public StudyDbImageNode(File file, int cnt, int imgNo, File dbpath, String studyname){
        this.itemCnt = cnt;
        this.imageNo = imgNo;
        this.studyPath = file;
        this.studyImageDbRoot = dbpath;
        this.studyName = studyname;
        
        init();
    }
    
    private void init(){
        taskPanelResultList = createTaskPanelResultList();
        localizationList = createLocalizationtList();
        roiDistanceList = createROIList();
        roiVolumeList = createROIList();
        roiPixelValueList = createROIPixelValueList();
        imageDone = false;
    }
    
    /**
     * Returns the item cnt.
     *
     * @param return the item cnt.
     */
    public int getItemCnt(){
        return itemCnt;
    }
    
     /**
      * Set the item cnt.
      */
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }
    
     /**
      * Set the imageNo.
      * 
      * @param the imageNo
      */
    public void setImageNo(int nb){
        imageNo = nb;
    }
    
     /**
     * Returns the imageNo.
     *
     * @return the imageNo.
     */
    public int getImageNo(){
        return imageNo;
    }
    
    /**
     */
    public File getStudyPath(){
        return studyPath;
    }
    
    /**
     */
    public String getStudyName(){
        return studyName;
    }
    
    
    /*****************************************
     * ROI
     *****************************************/

    /**
     * Create a list containing the ROI object.
     */
    private ArrayList<StudyDbROI> createROIList(){
        return new ArrayList<StudyDbROI>();
    }
    
    // test
    public void createROIListOnTest(){
        roiDistanceList = createROIList();
    }
    
   

    /*****************************************
     * Distance
     *****************************************/
    /**
     * Get the <code>StudyDbLocalization<code/> list.
     */
    public ArrayList<StudyDbROI> getROIDistanceList(){
        return roiDistanceList;
    }
    
     /**
      * Delete the ROI list.
     */
    public void deleteROIDistanceList(){
        roiDistanceList = null;
        roiDistanceList = createROIList();
    }
    
    
    /******************************************
    * Volume
    ******************************************/
     /**
     * Get the <code>ROIVolumeList<code/> list.
     */
    public ArrayList<StudyDbROI> getROIAreaList(){
        return roiVolumeList;
    }
    
     /**
      * Delete the ROI list.
     */
    public void deleteROIAreaList(){
        roiVolumeList = null;
        roiVolumeList = createROIList();
    }
    
    /*****************************************
     * PixelValue
     *****************************************/

    /**
     * Create the ROIPixelValueList.
     */
    private ArrayList<StudyDbROIPixelValue> createROIPixelValueList(){
        return new ArrayList<StudyDbROIPixelValue>();
    }
    
     /**
     * Get the <code>ROIPixelValueList<code/> list.
     */
    public ArrayList<StudyDbROIPixelValue> getROIPixelValueList(){
        return roiPixelValueList;
    }
    
    
    /*****************************************
     * Localization
     *****************************************/

    /**
     * Create a list containing the answering results from the
     * Task panel Checkbox.
     */
    private ArrayList<StudyDbLocalization> createLocalizationtList(){
        return new ArrayList<StudyDbLocalization>();
    }

    /**
     * Get the <code>StudyDbLocalization<code/> list.
     */
    public ArrayList<StudyDbLocalization> getLocalizationList(){
        return localizationList;
    }

     /**
      * Delete the localization list.
     */
    public void deleteLocalizationList(){
        localizationList = null;
        localizationList = createLocalizationtList();
    }


    /*****************************************
     * TaskPanel
     *****************************************/

    /**
     * Set the taskPanelResult list.
     */
    public void setTaskPanelResultList(ArrayList<VgTaskPanelResult> list){
        taskPanelResultList = list;
    }
    
    public ArrayList<VgTaskPanelResult> getTaskPanelResultList(){
        return taskPanelResultList;
    }

    /**
     * Create a list containing the answering results from the
     * Task panel Checkbox.
     */
    private ArrayList<VgTaskPanelResult> createTaskPanelResultList(){
        return new ArrayList<VgTaskPanelResult>();
    }


    /******************************************
     * Setter & getter methods
     *****************************************/
    
    /**
     * Set dataset
     */
    public void setDataset(Attributes attributes){
        dataset = attributes;
    }
    
    /**
     * Get dataset
     */
    public Attributes getDataset(){
        return dataset;
    }
    
    /**
     */
    public void setPatientName(String str){
        patientName = str;
    }
    
    /**
     */
    public String getPatientName(){
        return patientName;
    }
    
    /**
     */
    public void setPatientID(String str){
        patientID = str;
    }
    
    /**
     */
    public String getPatientID(){
        return patientID;
    }
    
    /**
     */
    public void setStudyInstanceUID(String str){
        studyInstanceUID = str;
    }
    
    /**
     */
    public String getStudyInstanceUID(){
        return studyInstanceUID;
    }
    
    /**
     */
    public void setBitsStored(int bits){
        bitsStored = bits;
    }
    
    /**
     */
    public int getBitsStored(){
        return bitsStored;
    }
    
    /**
     */
    public void setBitsAllocated(int bits){
        bitsAllocated = bits;
    }
    
    /**
     */
    public int getBitsAllocated(){
        return bitsAllocated;
    }
    
    /**
     */
    public void setRows(int rOws){
        rows = rOws;
    }
    
    /**
     */
    public int getRows(){
        return rows;
    }
    
    /**
     */
    public void setColumns(int cOlumns){
        columns = cOlumns;
    }
    
    /**
     */
    public int getColumns(){
        return columns;
    }
    
    /*
     */
    public void setWindowWidth(int[] width){
        windowWidth = width;
    }
    
    /**
     */
    public int[] getWindowWidth(){
        return windowWidth;
    }
    
    /*
     */
    public void setWindowCenter(int[] center){
        windowCenter = center;
    }
    
    /**
     */
    public int[] getWindowCenter(){
        return windowCenter;
    }
    
     /*
      * Set the adjusted value when a new image is selected
      * from the stack.
      */
    public void setWindowWidthAdjusted(int width){
        windowWidthAdjusted = width;
    }
    
    /**
     * Get the adjusted window width.
     */
    public int getWindowWidthAdjusted(){
        return windowWidthAdjusted;
    }
    
    /*
     * Set the adjusted value when a new image is selected
      * from the stack.
     */
    public void setWindowCenterAdjusted(int center){
        windowCenterAdjusted = center;
    }
    
    /**
     * get the adjusted window center.
     */
    public int getWindowCenterAdjusted(){
        return windowCenterAdjusted;
    }
    
    /**
     */
    public void setPhotometricInterpretation(String pmi){
        photometricInterpretation = pmi;
    }
    
    /**
     */
    public String getPhotometricInterpretation(){
        return photometricInterpretation;
    }
    
    /**
     */
    public void setUserName(String str){
        userName = str;
    }
    
    /**
     */
    public String getUserName(){
        return userName;
    }
    
    /**
     * Set the time when the evaluation start.
     */
    public void setImageEvaluationTimeStart(){
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Set the time when the evaluation stop.
     */
    public void setImageEvaluationTimeStop(){
        stopTime = System.currentTimeMillis();
        totalTime += (stopTime - startTime);
    }
    
    
    /**
     * Get the image evaluation time.
     * @return total number of millisecunds. 
     */
    public long getImageEvaluationTime(){
        return totalTime;
    }
    
    /**
     * Set the imageDone status. When set to true
     * the image is evaluated.
     * NOT IN USE
     */
    public void setImageDone(boolean sta){
        imageDone = sta;
    }
    
    /**
     * Get the imageDone status. When set to true
     * the image is evaluated.
     * NOT IN USE
     */
    public boolean getImageDone(){
        return imageDone;
    }
    
    /**
     */
    public void setModality(String str){
        modality = str;
    }
    
     /**
     */
    public String getModality(){
        return modality;
    }
    
    /**
     */
    public void setImgStat(int[][] stat){
        imgStat = stat;
    }
    
    /**
     */
    public int[][] getImgStat(){
        return imgStat;
    }
    
    /**
     */
    public void setWindowCenterOffsetStatus(boolean sta){
        windowCenterOffsetStatus = sta;
    }
    
    /**
     */
    public boolean getWindowCenterOffsetStatus(){
        return windowCenterOffsetStatus;
    }
    
    /**
     */
    public void setOrgImage(PlanarImage img){
        orgImage = img;
    }
    
    /**
     */
    public PlanarImage getOrgImage(){
        return orgImage;
    }
    
    /**
     */
    public void setOrgBackImage(PlanarImage img){
        orgBackImage = img;
    }
    
    /**
     */
    public PlanarImage getOrgBackImage(){
        return orgBackImage;
    }
    
    /**
     */
     public boolean orgBackImageExist(){
         if(orgBackImage != null)
             return true;
         else return false;
     }
    
    /**
     */
    public void deleteImageOrg(){
        orgImage = null;
    }
    
    /**
     */
    public void deleteDataSet(){
        dataset = null;
    }
    
    /**
     */
    public void setRescaleIntercept(double val){
        rescaleIntercept = val;
    }
    
    /**
     */
    public double getRescaleIntercept(){
        return rescaleIntercept;
    }
    
    /**
     */
    public void setRescaleSlope(double val){
        rescaleSlope = val;
    }
    
    /**
     */
    public double getRescaleSlope(){
        return rescaleSlope;
    }
    
    /**
     */
    public void setPixelRepresentation(int val){
        pixelRepresentation = val;
    }
    
    /**
     */
    public int getPixelRepresentation(){
        return pixelRepresentation;
    }
    
    /**
     */
    /*public void setColorModelParam(ColorModelParam val){
        cmParam = val;
    }*/
    
    /**
     */
    /*public ColorModelParam getColorModelParam(){
        return cmParam;
    }*/
    
    /**
     */
    public void setModalityLUTSequenceStatus(boolean val){
        modalityLUTSequenceStatus = val;
    }
    
    /**
     */
    public boolean getModalityLUTSequenceStatus(){
        return modalityLUTSequenceStatus;
    }
    
    /**
     */
    public void setVoiLUTSequenceStatus(boolean val){
        voiLUTSequenceStatus = val;
    }
    
    /**
     */
    public boolean getVoiLUTSequenceStatus(){
        return voiLUTSequenceStatus;
    }
    
    /**
     */
    public void setRescaleSlopeInterceptStatus(boolean val){
        rescaleSlopeInterceptStatus = val;
    }
    
    /**
     */
    public boolean getRescaleSlopeInterceptStatus(){
        return rescaleSlopeInterceptStatus;
    }
    
    /**
     */
    public void setCenterWidthStatus(boolean val){
        centerWidthStatus = val;
    }
    
    /**
     */
    public boolean getCenterWidthStatus(){
        return centerWidthStatus;
    }
    
    /**
     */
    public void setIdentityStatus(boolean val){
        identityStatus = val;
    }
    
    /**
     */
    public boolean getIdentityStatus(){
        return identityStatus;
    }
    
       /**
     */
    public File getStudyImageDbRoot(){
        return studyImageDbRoot;
    }
    
    /**
     * Set InstanceNumber.
     */
    public void setInstanceNumber(int val){
        instanceNumber = val;
    }
    
     /**
     * Get InstanceNumber.
     */
    public int getInstanceNumber(){
        return instanceNumber;
    }
    
    /**
     * Set pixelSpacing.
     */
    public void setPixelSpacing(double[] val){
        pixelSpacing = val;
    }
    
     /**
     * Get pixelSpacing.
     */
    public double[] getPixelSpacing(){
        return pixelSpacing;
    }
    
    /**
     * Set imagerPixelSpacing
     */
    public void setImagerPixelSpacing(double[] val){
        imagerPixelSpacing = val;
    }
    
     /**
     * Get imagerPixelSpacing.
     */
    public double[] getImagerPixelSpacing(){
        return imagerPixelSpacing;
    }
    
    /**
     * Set pixelSpacingCalibrationType.
     */
    public void setPixelSpacingCalibrationType(String val){
        pixelSpacingCalibrationType = val;
    }
    
     /**
     * Get pixelSpacingCalibrationType.
     */
    public String getPixelSpacingCalibrationType(){
        return pixelSpacingCalibrationType;
    }
    
    /**
     * Set pixelSpacingCalibrationDescription.
     */
    public void setPixelSpacingCalibrationDescription(String val){
        pixelSpacingCalibrationDescription = val;
    }
    
     /**
     * Get pixelSpacingCalibrationDescription.
     * @return 
     */
    public String getPixelSpacingCalibrationDescription(){
        return pixelSpacingCalibrationDescription;
    }
    
    /**
     * Set nominalScannedPixelSpacing,
     * @param val
     */
    public void setNominalScannedPixelSpacing(double[] val){
        nominalScannedPixelSpacing = val;
    }
    
     /**
     * Get nominalScannedPixelSpacing.
     * @return 
     */
    public double[] getNominalScannedPixelSpacing(){
        return nominalScannedPixelSpacing;
    }
    
    /**
     * Set pixelAspectRatio.
     * @param val
     */
    public void setPixelAspectRatio(int[] val){
        pixelAspectRatio = val;
    }
    
     /**
     * Get pixelAspectRatio.
     */
    public int[] getPixelAspectRatio(){
        return pixelAspectRatio;
    }
    
    /**
     * Set EstimatedRadiographicMagnificationFactor
     * @param val 
     */
    public void setEstimatedRadiographicMagnificationFactor(double val){
        estimatedRadiographicMagnificationFactor = val;
    }
    
    /**
     * Get estimatedRadiographicMagnificationFactor
     * @return 
     */
    public double getEstimatedRadiographicMagnificationFactor(){
        return estimatedRadiographicMagnificationFactor;
    }
    
    /**************************************************
     * end
     *************************************************/
    
    
    /**
     * compareTo
     * @param obj
     * @return
     * NOT IN USE
     */
    public int compareToOLD(Object obj) {
        StudyDbImageNode item = (StudyDbImageNode)obj;
        if(itemCnt > item.itemCnt)
            return 1;
        else{
            if(itemCnt < item.itemCnt)
                return -1;
            else{
                if(itemCnt == item.itemCnt)
                    return 0;
            }
        }
        return 0;
    }
    
    /**
     * compareTo
     * @param obj
     * @return
     */
    public int compareTo(Object obj) {
        StudyDbImageNode item = (StudyDbImageNode)obj;
        
        if(instanceNumber > item.instanceNumber)
            return 1;
        else{
            if(instanceNumber < item.instanceNumber)
                return -1;
            else{
                if(instanceNumber == item.instanceNumber)
                    return 0;
            }
        }
        return 0;
    }
    
    /**
     * NOT IN USE
     */
    public boolean setTaskLocalizationResult(int stackNodeCnt, int imageNodeCnt, Point2D p){
        if(imageNodeCnt == itemCnt && p != null){
            localization = p;
            return true;
        }
        return false;
    }
    
    /**
     * Return status about if a localization object exist.
     * NOT IN USE
     */
    public boolean getTaskLocalizationResultStatus(){
        if(localization != null)
            return true;
        else
            return false;
    }
    
    /**
     * Delete the Localization list.
     */
    public void deleteTaskLocalizationList(){
        localizationList = null;
        localization = null;
        localizationList = createLocalizationtList();
    }
    
    /**
      * Delete the ROIPixelValue list.
     */
    public void deleteROIPixelValueList(){
        roiPixelValueList = null;
        roiPixelValueList = createROIPixelValueList();
    }
    
    /**
     */
    public void deleteTaskLocalizationResultList(){
        taskPanelResultList = null;
        taskPanelResultList = createTaskPanelResultList();
    }

    public void setTimeStampImageRendering(){
        timeStampImageRendering = System.currentTimeMillis();
        //System.out.println("TimeStampImageRendering = " + timeStampImageRendering);
    }

    public long getTimeStampImageRendering(){
        return timeStampImageRendering;
    }
}