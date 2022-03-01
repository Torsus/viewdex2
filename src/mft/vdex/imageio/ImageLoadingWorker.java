/*
 * ImageLoadingWorker.java
 *
 * Created on 2007-okt-19, 14:02:04
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mft.vdex.imageio;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javax.media.jai.PlanarImage;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import mft.vdex.app.AppMainAdmin;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.ds.StudyDbStackType;
//import org.dcm4che.data.Dataset;
import org.dcm4che3.data.Attributes;

/**
 *
 * @author Sune Svensson
 */
public class ImageLoadingWorker extends SwingWorker<ArrayList<StudyDbImageNode>, String> implements StudyDbStackType {

    private StudyDbStackNode stackNode;
    private ArrayList<StudyDbImageNode> imageList;
    private AppMainAdmin appMainAdmin;
    private JLabel cntLabel;
    private boolean cineLoopStartAutoStatus;
    private boolean cineLoopStatus;
    private int runModeStatus;
    private boolean doRender = true;

    public ImageLoadingWorker(StudyDbStackNode stackNode, ArrayList<StudyDbImageNode> list, JLabel cntlabel,
            AppMainAdmin app, boolean sta, boolean sta2, int rms) {
        this.stackNode = stackNode;
        this.imageList = list;
        this.appMainAdmin = app;
        this.cntLabel = cntlabel;
        this.cineLoopStatus = sta;
        this.cineLoopStartAutoStatus = sta2;
        this.runModeStatus = rms;
    }

    // In the EDT
    // Called by ......
    @Override
    protected void done() {
        //System.out.println("ImageLoadingWorker.done");

        if (cineLoopStatus && cineLoopStartAutoStatus && imageList.size() >= 2) {
            appMainAdmin.viewDex.vgCineLoopPanel.setCineLoopStartAction();
            appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(true);
            appMainAdmin.viewDex.canvasContextMenu.setCineMenuButtonEnabled(true);
        } else {
            if (cineLoopStatus && !cineLoopStartAutoStatus && imageList.size() >= 2) {
                appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(true);
                appMainAdmin.viewDex.canvasContextMenu.setCineMenuButtonEnabled(true);
            } else {
                if (!cineLoopStatus && imageList.size() >= 2) {
                    // Bug fix 20160420
                    if (appMainAdmin.viewDex.vgCineLoopPanel != null) {
                        appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(false);
                        appMainAdmin.viewDex.canvasContextMenu.setCineMenuButtonEnabled(false);
                    }
                } else {
                    if (!cineLoopStatus && imageList.size() <= 1) {
                        if (appMainAdmin.viewDex.vgCineLoopPanel != null) {
                            appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(false);
                        }
                        appMainAdmin.viewDex.canvasContextMenu.setCineMenuButtonEnabled(false);
                    }
                }
            }
        }

        // cnt label
        appMainAdmin.viewDex.vgRunPanel.setTotalImageBackgroundReadingCount("");
        appMainAdmin.vgControl.setImageLoadingWorkerStatus(false);
    }

    // In the EDT
    //@Override
    // Not working not called from the thread! 2007-11-13
    public void process(String msg) {
        //appMainAdmin.viewDex.vgRunPanel.setTotCount(msg[0]);
        //cntLabel.setText(msg);
        //System.out.println(msg);
    }

    // In a thread
    @Override
    public ArrayList<StudyDbImageNode> doInBackground() {
        boolean loadStatus = false;

        appMainAdmin.vgControl.setImageLoadingWorkerStatus(true);

        // Set the status on GUI components.
        if (imageList.size() >= 2) {
            appMainAdmin.viewDex.canvasContextMenu.setScrollStackMode(1);
            appMainAdmin.viewDex.canvasControl.setZoomControlAction("scroll.stack");
            //appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(false);
        } else if (imageList.size() <= 1) {
            appMainAdmin.viewDex.canvasContextMenu.setScrollStackMode(0);
            appMainAdmin.viewDex.canvasControl.setZoomControlAction("pan");
            appMainAdmin.viewDex.canvasContextMenu.setCineMenuButtonEnabled(false);
            if (appMainAdmin.viewDex.vgCineLoopPanel != null) {
                appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(false);
            }
        }

        // Get and set the image
        for (int i = 0; i < imageList.size(); i++) {
            StudyDbImageNode imageNode = imageList.get(i);
            File filePath = imageNode.getStudyPath();
            PlanarImage orgImage = imageNode.getOrgImage();
            int imageNo = imageNode.getImageNo();

            if (orgImage == null) {
                loadStatus = true;
            } else {
                loadStatus = false;
            }
            if (orgImage == null) {
                DicomFileReader studyLoader = new DicomFileReader();

                if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE
                        || stackNode.getStackType() == StudyDbStackType.STACK_TYPE_STACK_IMAGE) //studyLoader.loadImage(filePath);
                {
                    studyLoader.loadImage(filePath, stackNode.getStackType(), 0);
                } else if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE) {
                    studyLoader.loadImage(filePath, stackNode.getStackType(), imageNode.getImageNo());
                }

                orgImage = studyLoader.getLoadedPlanarImage();
                setImageNode(studyLoader, imageNode, orgImage);
                setStackNode(studyLoader);
                //studyLoader = null;
            }

            //test
            //appMainAdmin.vgControl.writeOriginalHistory();
            //if(i == 100)
            //  System.exit(3);

            /*
            if (loadStatus)
            System.out.println("VgControl:preLoadImages: Load selected image");
            else
            System.out.println("VgControl:preLoadImages: NOT load selected image");
             */

            // Cineloop
            if (cineLoopStatus && !cineLoopStartAutoStatus) {
                int selCnt = appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

                if (i == selCnt) {
                    appMainAdmin.vgControl.runStudy(imageNode);
                    appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                    appMainAdmin.viewDex.localization.setLocalizationOverlayListInCanvas();

                    if (appMainAdmin.viewDex.localization.localizationListExist()) {
                        appMainAdmin.viewDex.vgLocalizationPanel.showHideButton.setEnabled(true);
                    }
                    appMainAdmin.vgControl.setSelImageCount();
                    appMainAdmin.vgControl.setTotalImageCount();

                    // Rendering timestamp
                    if (imageNode != null) {
                        imageNode.setTimeStampImageRendering();
                    }
                }
            }
            // Do the rendering until there is a location mark in the image.
            if (doRender && cineLoopStatus && cineLoopStartAutoStatus) {
                //test
                //if(doRender && cineLoopStatus2 && cineLoopStartAutoStatus){
                appMainAdmin.vgControl.studyDbUtility.setSelectedImageNodeCount(i);

                if (i == 0) {
                    appMainAdmin.vgControl.runStudy(imageNode);

                    // Rendering timestamp
                    if (imageNode != null) {
                        imageNode.setTimeStampImageRendering();
                    }
                } else {
                    appMainAdmin.vgControl.setImageAndRender(imageNode);

                    // Rendering timestamp
                    if (imageNode != null) {
                        imageNode.setTimeStampImageRendering();
                    }
                }

                //Date date = new Date();
                //System.out.println("Time: " + (System.currentTimeMillis()));
                //System.out.println("Time: " + (System.nanoTime()));

                appMainAdmin.vgControl.setSelImageCount();

                // Stop the loop if a mark exist
                if (runModeStatus == 11) { // EDIT mode
                    if (appMainAdmin.viewDex.localization.getLocalizationMarkExistStatusForSelectedImage()) {
                        doRender = false;
                        appMainAdmin.vgControl.setImageLoadingWorkerStatus(false);
                        appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(true);
                    }
                }
            }

            if (!cineLoopStatus) {
                if (i == 0 && imageNode != null) {
                    appMainAdmin.vgControl.runStudy(imageNode);
                    appMainAdmin.vgControl.setSelImageCount();
                }
            }

            // cnt label
            appMainAdmin.vgControl.setTotalImageCount();
            appMainAdmin.viewDex.vgRunPanel.setTotalImageBackgroundReadingCount(Integer.toString(i + 1));

            // NOT WORKING, process not activated.
            //publish(countStr);

            // This method returns true if cancel has been invoked for this SwingWorker.
            // The "Stop button invokes the SwingWorker.cansel method.
            if (isCancelled()) {
                break;
            }
        }
        //appMainAdmin.viewDex.setDefaultCursor();

        return imageList;
    }

    /**
     * Set  the header data.
     */
    private void setStackNode(DicomFileReader studyLoader) {
        Attributes dataset = studyLoader.attributeReader.getAttributes();

        if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE) {
            stackNode.setDataset(dataset);
        }
    }

    /**
     * Set the image and image header data.
     */
    private void setImageNode(DicomFileReader studyLoader, StudyDbImageNode imageNode, PlanarImage orgImage) {
        int[][] imgStat = null;

        // get
        String patientId = studyLoader.attributeReader.att.getPatientID();
        String modality = studyLoader.attributeReader.att.getModality();
        Attributes dataset = studyLoader.attributeReader.getAttributes();
        int[] windowWidth = studyLoader.attributeReader.att.getWindowWidth_int_array();
        int[] windowCenter = studyLoader.attributeReader.att.getWindowCenter_int_array();
        int bitsStored = studyLoader.attributeReader.att.getBitsStored();
        int bitsAllocated = studyLoader.attributeReader.att.getBitsAllocated();
        int rows = studyLoader.attributeReader.att.getRows();
        int columns = studyLoader.attributeReader.att.getColumns();
        double rescaleIntercept = studyLoader.attributeReader.att.getRescaleInterceptValue();
        double rescaleSlope = studyLoader.attributeReader.att.getRescaleSlopeValue();
        int pixelRepresentation = studyLoader.attributeReader.att.getPixelRepresentation();
        String photometricInterpretation = studyLoader.attributeReader.att.getPhotoMetricInterpretation();

        // It is NOT possible or desirable to save the ColorModeParam object in the
        // history object due to the serialization interface.
        //ColorModelParam cmParam = studyLoader.getColorModelParam();
        boolean modalityLUTSequenceStatus = studyLoader.getModalityLUTSequenceStatus();
        boolean voiLUTSequenceStatus = studyLoader.getVoiLUTSequenceStatus();
        boolean rescaleSlopeInterceptStatus = studyLoader.getRescaleSlopeInterceptStatus();
        boolean centerWidthStatus = studyLoader.getCenterWidthStatus();
        boolean identityStatus = studyLoader.getIdentityStatus();
        boolean windowCenterOffsetStatus = studyLoader.getWindowCenterOffsetStatus();
        imgStat = studyLoader.getImageStats();

        // mesurement
        double[] pixelSpacing = studyLoader.attributeReader.att.getPixelSpacing();
        double[] imagerPixelSpacing = studyLoader.attributeReader.att.getImagerPixelSpacing();
        int[] pixelAspectRatio = studyLoader.attributeReader.att.getPixelAspectRatio();
        String pixelSpacingCalibrationType = studyLoader.attributeReader.att.getPixelSpacingCalibrationType();
        String pixelSpacingCalibrationDescription = studyLoader.attributeReader.att.getPixelSpacingCalibrationDescription();
        double[] nominalScannedPixelSpacing = studyLoader.attributeReader.att.getNominalScannedPixelSpacing();
        double estimatedRadiographicMagnificationFactor = studyLoader.attributeReader.att.getEstimatedRadiographicMagnificationFactor();

        // set
        imageNode.setPatientID(patientId);
        imageNode.setModality(modality);

        if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE
                || stackNode.getStackType() == StudyDbStackType.STACK_TYPE_STACK_IMAGE) {
            
            //=====================================================
            // test History object
            // System.out.println("ImageLoadintWorker.setImageNode Object size(dataset): " +
            // ObjectSizeCalculator.getObjectSize(dataset));
            // end test History object
            //=====================================================
            imageNode.setDataset(dataset);
        }
        
        //=========================================================
        // test History object
        // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("ImageLoadintWorker.setImageNode Object size(hist)");
        // end test
        //=========================================================

        imageNode.setWindowWidth(windowWidth);
        imageNode.setWindowCenter(windowCenter);
        imageNode.setBitsStored(bitsStored);
        imageNode.setBitsAllocated(bitsAllocated);
        imageNode.setRows(rows);

        imageNode.setColumns(columns);
        imageNode.setRescaleIntercept(rescaleIntercept);
        imageNode.setRescaleSlope(rescaleSlope);
        imageNode.setPixelRepresentation(pixelRepresentation);
        imageNode.setPhotometricInterpretation(photometricInterpretation);
        // testzzzzimageNode.setColorModelParam(cmParam);
        imageNode.setModalityLUTSequenceStatus(modalityLUTSequenceStatus);
        imageNode.setVoiLUTSequenceStatus(voiLUTSequenceStatus);
        imageNode.setRescaleSlopeInterceptStatus(rescaleSlopeInterceptStatus);
        imageNode.setCenterWidthStatus(centerWidthStatus);
        imageNode.setIdentityStatus(identityStatus);
        imageNode.setWindowCenterOffsetStatus(windowCenterOffsetStatus);
        imageNode.setImgStat(imgStat);
        //imageNode.setImageEvaluationTime();

        // mesurement
        imageNode.setPixelSpacing(pixelSpacing);
        imageNode.setImagerPixelSpacing(imagerPixelSpacing);
        imageNode.setPixelAspectRatio(pixelAspectRatio);
        imageNode.setPixelSpacingCalibrationType(pixelSpacingCalibrationType);
        imageNode.setPixelSpacingCalibrationDescription(pixelSpacingCalibrationDescription);
        imageNode.setNominalScannedPixelSpacing(nominalScannedPixelSpacing);
        imageNode.setEstimatedRadiographicMagnificationFactor(estimatedRadiographicMagnificationFactor);
        
        //=====================================================
        // test history
        // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("ImageLoadintWorker.setImageNode Object size(hist)");
        // System.out.println("ImageLoadingWorker.setImageNode Object size (orgImage) :" + ObjectSizeCalculator.getObjectSize(orgImage));
        // end test history
        //=====================================================

        imageNode.setOrgImage(orgImage);
    }
}
