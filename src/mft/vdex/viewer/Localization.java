/* Localization.java 07/16/2007
 * 
 * Copyright (c) 2007 Sahlgrenska University Hospital.
 * All Rights Reserved.
 */

/*
 * @Author Sune Svensson
 */
package mft.vdex.viewer;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbLocalization;
import mft.vdex.ds.StudyDbLocalizationM;
import mft.vdex.ds.StudyDbLocalizationStatus;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.modules.vg.VgRunMode;
import mft.vdex.modules.vg.VgTaskPanelQuestion;
import mft.vdex.modules.vg.VgTaskPanelResult;

public class Localization implements LocalizationInterface {

    protected ViewDex viewDex;
    protected ImageCanvasInterface canvas;
    boolean shapeLock = true;
    boolean lesionMarkActive = false;
    boolean lesionMarkExist = false;
    private int shapeType;
    private Point2D p1 = null, p2 = null;
    public final static int CROSSMARK = 2;
    public final static int ELLIPSE = 1;
    // test
    private int actionCnt = 0;
    private int actionSelectCnt = 0;
    Shape eRestore = null;
    private int runModeStatus = VgRunMode.NONE;
    private int canvasControlMode = CanvasControlMode.NONE;
    private AffineTransform atx;
    private Point2D xy = null;
    // NOT IN USE
    private Ellipse2D shapeEllipse;
    // localization properties
    boolean localizationSelectedExist = false;
    private double localizationActiveSymbolLineSize;
    private double localizationSetSymbolElipseSize;
    private double localizationSelectSymbolLineXSize;
    private double localizationSelectSymbolLineYSize;
    private BasicStroke localizationSymbolStroke;
    public static final boolean dev_debug = false;
    private int cnt = 0;
    private boolean keyCtrlEnable = false;

    /**
     * *******************************************************************
     *
     * The following definitions for localization exist.
     *
     * Localization.ACTIVE
     * Localization.SET
     * Localization.SELECTED
     * Localization.SELECTEDNOSETSYMBOL
     *
     *           |
     * Active  --|--
     *           |
     *
     *           |
     * Set     --O--
     *           |
     *
     *            ______ 
     *           |  |  |
     * Selected  |--O--|
     *           |__|__|
     *
     *                       ______
     *                      |     | 
     * SelectedNoSETSymbol  |     |
     *                      |_____|
     * 
     *
     * *****************************************************************
     */ 
    /**
     * Creates a new instance of ShapaMarker
     */
    public Localization(ViewDex viewdex, ImageCanvasInterface imagemanipulator) {
        this.viewDex = viewdex;
        this.canvas = imagemanipulator;
    }

    /**
     * Create an Localization.ACTIVE mark in the image.
     */
    @Override
    public boolean mousePressedCreateAction(int x, int y) {
        //System.out.println("Localization: mousePressedCreateAction");
        if (dev_debug) {
            System.out.println("Localization.mousePressedCreateAction" + " " + x + " " + y + " " + cnt);
        }

        int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();

        // Prohibite localization if the cineLoop is running.
        if (viewDex.appMainAdmin.vgControl.getCineLoopRunningStatus()
                || viewDex.appMainAdmin.vgControl.getImageLoadingWorkerStatus()) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        // Prohibite localization if not defined in the "TaskPanel property" section.
        if (!viewDex.vgTaskPanelUtility.getTaskPanelLocalizationStatusExist()) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
            //startPoint = new Point((int)(xy.getX()),(int)(xy.getY()));
        } catch (Exception e) {
            System.out.println(e);
        }

        // Check if the mark is set outside of the image.
        Dimension d = canvas.getImageSize();
        double x1 = d.getWidth();
        double y1 = d.getHeight();
        double x2 = xy.getX();
        double y2 = xy.getY();

        if (x2 < 0.0 || y2 < 0.0 || x2 > x1 || y2 > y1) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        Point userSpacePointInt = new Point(x, y);
        Point2D userSpacePointDouble = new Point2D.Double((double) x, (double) y);
        Point imageSpacePointInt = new Point((int) (xy.getX()), (int) (xy.getY()));
        Point2D imageSpacePointDouble = new Point2D.Double(xy.getX(), xy.getY());
        boolean activeStatus = localizationActiveStatusExist();
        Point2D activePoint = getLocalizationActivePoint();
        boolean taskPanelStatusActive = getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);

        // Reset the taskpanel
        // Set the active point and render
        if (activePoint == null) {
            viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
            // update to set if selected exist
            updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
            createLocalizationItem(userSpacePointInt, imageSpacePointInt,
                    userSpacePointDouble, imageSpacePointDouble);

            // render
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            setLocalizationOverlayListInCanvas();
            //viewDex.windowLevel.setWindowLevel();

            if (viewDex.vgLocalizationPanel != null) {
                //viewDex.vgLocalizationPanel.setHideText();
                viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
                viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(false);
                //viewDex.vgLocalizationPanel.showHideButton.setEnabled(false);
            }
        }

        if (activePoint != null && !taskPanelStatusActive) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        if (activePoint != null) {
            viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
            updateLocalizationStatus(StudyDbLocalizationStatus.ACTIVE, StudyDbLocalizationStatus.SET);
            updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
            createLocalizationItem(userSpacePointInt, imageSpacePointInt, userSpacePointDouble, imageSpacePointDouble);

            // render
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            setLocalizationOverlayListInCanvas();
            //viewDex.windowLevel.setWindowLevel();
        }
        viewDex.windowLevel.setWindowLevel();
        return true;
    }

    /*
     * mousePressedShowSelectAction
     */
    @Override
    public boolean mousePressedShowSelectAction(int x, int y) {
        int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

        // Prohibite localization if not defined in the "TaskPanel property" section.
        if (!viewDex.vgTaskPanelUtility.getTaskPanelLocalizationStatusExist()) {
            return false;
        }

        // Prohibite localization if the cineLoop is running.
        if (viewDex.appMainAdmin.vgControl.getCineLoopRunningStatus()
                || viewDex.appMainAdmin.vgControl.getImageLoadingWorkerStatus()) {
            //Toolkit.getDefaultToolkit().beep();
            return false;
        }

        // zoom
        if ((getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_IN)
                || (getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_OUT)) {
            viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
            viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();

            //test For ZoomIn/ZoomOut
            //viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            setLocalizationOverlayListInCanvas();
            //viewDex.windowLevel.setWindowLevel();
            //return true;
        }

        //double x1 = 0;
        //double y1 = 0;
        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);

            // Check if the mark is set in- or outside of the image.
            Dimension d = canvas.getImageSize();
            if (xy.getX() < 0.0 || xy.getY() < 0.0 || xy.getX() > d.getWidth() || xy.getY() > d.getHeight()) {
                Toolkit.getDefaultToolkit().beep();
                return true;
            }
        } catch (NoninvertibleTransformException e) {
            System.out.println(e);
        }

        // Find out if a lmb activation will make a previous localization selected.
        // Create an ellipse with the mouse hot-point in the center of the circle.
        // The size of the ellipse have to be relative to the size of the image.
        
        //Shape ellipse = new Ellipse2D.Double((xy.getX() - r1), (xy.getY() - r1), (r1 * 2), (r1 * 2));
        //Shape ellipse = new Ellipse2D.Double((p.getX() - localizationSetSymbolElipseSize),
          //                      (p.getY() - localizationSetSymbolElipseSize), (localizationSetSymbolElipseSize * 2),
            //                 (localizationSetSymbolElipseSize * 2));
        
        // Create new userspace coordinates
        int x5 = x + (int)localizationSetSymbolElipseSize;
        int y5 = y + (int)localizationSetSymbolElipseSize;
        
        // transform
        Point2D xy5 = null;
         try {
            atx = canvas.getTransform();
            xy5 = atx.inverseTransform((Point2D) (new Point(x5, y5)), xy5);
        } catch (NoninvertibleTransformException e) {
            System.out.println(e);
        }
        
         // Create the ellipse with imagespace coordinates
        Shape ellipse = new Ellipse2D.Double((xy.getX()-(xy5.getX() - xy.getX())),
            (xy.getY() - (xy5.getY() - xy.getY())),
            ((xy5.getX() - xy.getX()) * 2),
            ((xy5.getX() - xy.getX()) * 2));
        
        Point2D localizationInsideShape = getLocalizationInsideShape(ellipse);
        int localizationInsideShapeStatus = getLocalizationInsideShapeStatus(ellipse);
        boolean taskPanelLocalizationInsideShapeStatus = getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, localizationInsideShape);

        Point2D activePoint = getLocalizationActivePoint();
        int localizationStatus = getLocalizationStatus(activePoint);
        boolean taskPanelActivePointStatus = getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);
        boolean localizationSelectStatusExist = localizationSelectStatusExist();

        if (localizationInsideShape == null) {
            if ((localizationStatus == StudyDbLocalizationStatus.ACTIVE) && taskPanelActivePointStatus) {
                setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                viewDex.vgLocalizationPanel.setHideText();
                //viewDex.windowLevel.setWindowLevel();

                if (viewDex.vgLocalizationPanel != null) {
                    viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(true);
                    viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(true);
                }
            } else {
                if ((localizationStatus != StudyDbLocalizationStatus.SET) && taskPanelActivePointStatus) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }

        if (localizationInsideShape == null) {
            if (localizationStatus == StudyDbLocalizationStatus.ACTIVE && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        // If showHideLocalisationButtonStatus == true -> symbols are displayed
        // -> button display the text "Hide".
        // If showHideLocalisationButtonStatus == false -> symbols are hidden
        // -> button display the text "Show".
        
        boolean showHideLocalizationButtonStatus = viewDex.vgLocalizationPanel.getLocalizationPanelStatus();
        
        if (localizationInsideShape == null) {
            if (localizationSelectStatusExist && showHideLocalizationButtonStatus) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
            }
        }
        
        // localizationSelectStatusExist && !showHideLocalizationButtonStatus
        if (localizationInsideShape == null) {
            if (localizationSelectStatusExist && !showHideLocalizationButtonStatus) {
                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.ACTIVE && taskPanelActivePointStatus) {
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(), (int) localizationInsideShape.getY(), imageNodeCnt + 1);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.vgLocalizationPanel.setHideText();
                //viewDex.windowLevel.setWindowLevel();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.ACTIVE && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && localizationActiveStatusExist() && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
                return true;
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && localizationActiveStatusExist() && taskPanelActivePointStatus) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(),
                        (int) localizationInsideShape.getY(), imageNodeCnt);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
                viewDex.vgTaskMainPanel.setRatingValue(localizationInsideShape);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
                //Toolkit.getDefaultToolkit().beep();
                return true;
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && taskPanelLocalizationInsideShapeStatus) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(),
                        (int) localizationInsideShape.getY(), imageNodeCnt + 1);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
                viewDex.vgTaskMainPanel.setRatingValue(localizationInsideShape);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
            }
        }
        viewDex.windowLevel.setWindowLevel();
        return true;
    }

    /*
     * mousePressedRightSelectAction
     * NOT IN USE
     */
    @Override
    public boolean mousePressedRightSelectAction(int x, int y) {
        int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();

        // Prohibite localization if the cineLoop is running.
        if (viewDex.appMainAdmin.vgControl.getCineLoopRunningStatus()
                || viewDex.appMainAdmin.vgControl.getImageLoadingWorkerStatus()) {
            //Toolkit.getDefaultToolkit().beep();
            return false;
        }

        // zoom
        if ((getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_IN)
                || (getCanvasControlMode() == CanvasControlMode.MANIP_ZOOM_OUT)) {
            viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
            viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();

            //test For ZoomIn/ZoomOut
            //viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            setLocalizationOverlayListInCanvas();
            //viewDex.windowLevel.setWindowLevel();
            //return true;
        }

        // convert the cordinats from userspace to imagespace
        try {
            atx = canvas.getTransform();
            xy = null;
            xy = atx.inverseTransform((Point2D) (new Point(x, y)), xy);
            //startPoint = new Point((int)(xy.getX()),(int)(xy.getY()));

            // Check if the mark is set in- or outside of the image.
            Dimension d = canvas.getImageSize();
            double x1 = d.getWidth();
            double y1 = d.getHeight();
            double x2 = xy.getX();
            double y2 = xy.getY();

            if (x2 < 0.0 || y2 < 0.0 || x2 > x1 || y2 > y1) {
                //Toolkit.getDefaultToolkit().beep();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        // mouse hot-point in the center of the circle.
        int r1 = 20;
        Shape ellipse = new Ellipse2D.Double((xy.getX() - r1), (xy.getY() - r1), (r1 * 2), (r1 * 2));
        Point2D localizationInsideShape = getLocalizationInsideShape(ellipse);
        int localizationInsideShapeStatus = getLocalizationInsideShapeStatus(ellipse);
        boolean taskPanelLocalizationInsideShapeStatus = getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, localizationInsideShape);

        Point2D activePoint = getLocalizationActivePoint();
        int localizationStatus = getLocalizationStatus(activePoint);
        boolean taskPanelActivePointStatus = getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);
        boolean localizationSelectStatusExist = localizationSelectStatusExist();

        if (localizationInsideShape == null) {
            if ((localizationStatus == StudyDbLocalizationStatus.ACTIVE) && taskPanelActivePointStatus) {
                setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                viewDex.vgLocalizationPanel.setHideText();
                //viewDex.windowLevel.setWindowLevel();

                if (viewDex.vgLocalizationPanel != null) {
                    viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(true);
                    viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(true);
                }
            } else {
                if ((localizationStatus != StudyDbLocalizationStatus.SET) && taskPanelActivePointStatus) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }

        if (localizationInsideShape == null) {
            if (localizationStatus == StudyDbLocalizationStatus.ACTIVE && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        if (localizationInsideShape == null) {
            if (localizationSelectStatusExist) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.ACTIVE && taskPanelActivePointStatus) {
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(), (int) localizationInsideShape.getY(), imageNodeCnt);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.vgLocalizationPanel.setHideText();
                //viewDex.windowLevel.setWindowLevel();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.ACTIVE && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && localizationActiveStatusExist() && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
                return true;
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && localizationActiveStatusExist() && taskPanelActivePointStatus) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(),
                        (int) localizationInsideShape.getY(), imageNodeCnt);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
                viewDex.vgTaskMainPanel.setRatingValue(localizationInsideShape);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
                //Toolkit.getDefaultToolkit().beep();
                return true;
            }
        }

        if (localizationInsideShape != null) {
            if (localizationInsideShapeStatus == StudyDbLocalizationStatus.SET && taskPanelLocalizationInsideShapeStatus) {
                updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
                setLocalizationStatus(localizationInsideShape, StudyDbLocalizationStatus.SELECTED);
                viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) localizationInsideShape.getX(),
                        (int) localizationInsideShape.getY(), imageNodeCnt);
                viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
                viewDex.vgTaskMainPanel.setRatingValue(localizationInsideShape);

                // render
                viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
                setLocalizationOverlayListInCanvas();
                //viewDex.windowLevel.setWindowLevel();
            }
        }
        viewDex.windowLevel.setWindowLevel();
        return true;
    }

    /**
     * Get Localization status.
     */
    public int getLocalizationStatus(Point2D p) {
        int status = 0;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        for (int i = 0; i < localizationList.size(); i++) {
            if (localizationList.get(i).getImageSpacePointDouble() == p) {
                status = localizationList.get(i).getLocalizationStatus();
            }
        }
        return status;
    }

    /**
     * SetLocalizationStatus
     */
    public void setLocalizationStatus(Point2D p, int status) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        for (int i = 0; i < localizationList.size(); i++) {
            if (localizationList.get(i).getImageSpacePointDouble() == p) {
                localizationList.get(i).setLocalizationStatus(status);
            }
        }
    }

    private void updateLocalizationStatus(Point2D p, int sta) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        for (int i = 0; i < localizationList.size(); i++) {
            if (localizationList.get(i).getUserSpacePointDouble() == p) {
                localizationList.get(i).setLocalizationStatus(sta);
            }
        }
    }

    /**
     * If all gradings of the taskPanel are giving, set the localization status
     * to <code>StudyDbLocalizationStatus.SET<code/>.
     * This means that it is possible to set a new mark on the screen.
     */
    public void updateLocalizationStatus(Point2D p) {
        int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        imageNodeCnt = imageNode.getItemCnt();  //test

        if (getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, p)) {
            setLocalizationSetStatus(p);
        }
    }

    /**
     * Get the <code>Point2D<code/> of the active localization mark.
     *
     * @return the <code>Point2D<code/> of the active localization mark.
     */
    public Point2D getLocalizationActivePoint() {
        Point2D p = null;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();

        if (localizationList != null) {
            for (int i = 0; i < localizationList.size(); i++) {
                if (localizationList.get(i).getLocalizationStatus() == StudyDbLocalizationStatus.ACTIVE) {
                    p = localizationList.get(i).getImageSpacePointDouble();
                    return p;
                }
            }
        }
        return p;
    }

    /**
     * Create a new localization object and set Localization status to
     * Localization.ACTIVE.
     */
    private void createLocalizationItem(Point userSpacePointInt, Point imageSpacePointInt,
            Point2D userSpacePointDouble, Point2D imageSpacePointDouble) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        StudyDbLocalization localization = new StudyDbLocalization(userSpacePointInt,
                imageSpacePointInt, userSpacePointDouble, imageSpacePointDouble,
                StudyDbLocalizationStatus.ACTIVE);
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (localizationList != null) {
            localizationList.add(localization);
        }

        for (int i = 0; i < localizationList.size(); i++) {
            StudyDbLocalization item = localizationList.get(i);
            item.setItemCnt(i);
        }
    }

    /**
     * Set Localization.SET status.
     */
    private void setLocalizationSetStatus(Point2D p) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        for (int i = 0; i < localizationList.size(); i++) {
            if (localizationList.get(i).getUserSpacePointDouble() == p) {
                localizationList.get(i).setLocalizationStatus(StudyDbLocalizationStatus.SET);
            }
        }
    }

    /**
     */
    public void updateLocalizationStatus(int oldStatus, int newStatus) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        for (int i = 0; i < localizationList.size(); i++) {
            if (localizationList.get(i).getLocalizationStatus() == oldStatus) {
                localizationList.get(i).setLocalizationStatus(newStatus);
            }
        }
    }

    /**
     * Check if all the answers that is required for the Tasks, with
     * localization property to yes, has been given.
     *
     * @param
     * @return
     */

    public boolean getTaskPanelTaskStatusLocalization(int stackNodeCnt, int imageNodeCnt, Point2D selPoint) {
        boolean resultStatus = true;
        ArrayList<VgTaskPanelQuestion> questionList = viewDex.vgHistory.getTaskPanelQuestionList();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

        // find the number of tasks with localization status == true
        int locTrueNb = 0;
        for (int m = 0; m < questionList.size(); m++) {
            VgTaskPanelQuestion q = questionList.get(m);
            if (q.getLocalizationTaskStatus()) {
                locTrueNb++;
            }
        }
        int[] result = new int[locTrueNb];

        int n = 0;
        for (int i = 0; i < questionList.size(); i++) {
            VgTaskPanelQuestion qItem = questionList.get(i);
            if (qItem.getLocalizationTaskStatus()) {
                n++;
                for (int j = 0; j < resultList.size(); j++) {
                    VgTaskPanelResult resultItem = resultList.get(j);
                    if (taskPanelResultPointExist(resultItem, stackNodeCnt, imageNodeCnt, i, selPoint)) {
                        result[n - 1] = 1;
                        break;
                    } else {
                        result[n - 1] = 0;
                    }
                }
            }
        }

        for (int k = 0; k < result.length; k++) {
            if (result[k] == 0) {
                resultStatus = false;
                break;
            }
        }
        return resultStatus;
    }

    /**
     *
     * @param resultItem
     * @param stackNodeCnt
     * @param imageNodeCnt
     * @param taskNumber
     * @param selPoint
     * @return
     */
    private boolean taskPanelResultPointExist(VgTaskPanelResult resultItem,
            int stackNodeCnt, int imageNodeCnt, int taskNumber, Point2D selPoint) {
        if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                && (resultItem.getImageNodeCnt() == imageNodeCnt)
                && (resultItem.getTaskNb() == taskNumber)
                && (resultItem.getPoint() == selPoint)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param p
     * @param s
     * @return COPY
     */
    public Point2D getLocalizationInsideShape(Shape s) {
        Point2D pValue = null;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (!localizationList.isEmpty()) {
            for (int i = 0; i < localizationList.size(); i++) {
                StudyDbLocalization localization = localizationList.get(i);
                //Shape s2 = localization.getShape();
                //status = s2.contains(p);
                Point2D p2 = localization.getImageSpacePointDouble();
                if (s.contains(p2)) {
                    pValue = p2;
                    break;
                }
            }
        }
        return pValue;
    }

    /**
     * @param p
     * @param s
     * @return COPY
     */
    public int getLocalizationInsideShapeStatus(Shape s) {
        int status = 0;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (!localizationList.isEmpty()) {
            for (int i = 0; i < localizationList.size(); i++) {
                StudyDbLocalization localization = localizationList.get(i);
                //Shape s2 = localization.getShape();
                //status = s2.contains(p);
                Point2D p2 = localization.getImageSpacePointDouble();
                if (s.contains(p2)) {
                    status = localization.getLocalizationStatus();
                    break;
                }
            }
        }
        return status;
    }

    /**
     * Set the Localization marks overlay item in canvas. Transform cordinates
     * from image- to userspace.
     */
    public void setLocalizationOverlayListInCanvas() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> locList = imageNode.getLocalizationList();
        ArrayList<StudyDbLocalizationM> lList = new ArrayList<StudyDbLocalizationM>();

        if (locList != null) {
            for (int i = 0; i < locList.size(); i++) {
                // init
                //Point2D userSpaceStartPointInt = null;
                Point2D userSpacePointDouble = null;
                //Point2D userSpaceCurrentPointInt = null;
                Point2D userSpaceCurrentPointDouble = null;
                //Point2D imageSpaceStartPointInt = null;
                Point2D imageSpacePointDouble = null;
                //Point2D imageSpaceCurrentPointInt = null;
                Point2D imageSpaceCurrentPointDouble = null;

                //boolean status = roiList.get(i).getROIActiveStatus();
                StudyDbLocalization loc = locList.get(i);
                //imageSpaceStartPointInt = roi.getImageSpaceStartPointInt();
                //imageSpaceCurrentPointInt = roi.getImageSpaceCurrentPointInt();
                imageSpacePointDouble = loc.getImageSpacePointInt();
                //imageSpaceCurrentPointDouble = roi.getImageSpaceCurrentPointDouble();
                int localizationStatus = loc.getLocalizationStatus();

                // convert coordinats from imagespace to userspace
                try {
                    AffineTransform atx2 = viewDex.canvas.getTransform();
                    //userSpaceStartPointInt = atx.transform(imageSpaceStartPointInt, userSpaceStartPointInt);
                    //userSpaceStartPointDouble = atx.transform(imageSpaceStartPointDouble, userSpaceStartPointDouble);
                    userSpacePointDouble = atx2.transform(imageSpacePointDouble, null);
                    //userSpaceCurrentPointInt = atx.transform(imageSpaceCurrentPointInt, userSpaceCurrentPointInt);
                    //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, userSpaceCurrentPointDouble);
                    //userSpaceCurrentPointDouble = atx.transform(imageSpaceCurrentPointDouble, null);

                } catch (Exception e) {
                    System.out.println("Localization.setLocalizationInCanvasAndRender: Error");
                }
                //dList.add(new StudyDbROID(userSpaceStartPointInt, userSpaceCurrentPointInt,
                //imageSpaceStartPointInt, imageSpaceCurrentPointInt));

                // calculate the distance
                //20160405
                /*double distance = 0;
                 if((imageNode != null) && imageSpaceStartPointDouble != null && imageSpaceCurrentPointDouble != null)
                 distance = calculateROIDistance(imageNode, imageSpaceStartPointDouble, imageSpaceCurrentPointDouble);
                 */
                //double distance = calculateROIDistance(imageNode, imageSpaceStartPointDouble, imageSpaceCurrentPointDouble);
                lList.add(new StudyDbLocalizationM(userSpacePointDouble, localizationStatus));
            }
            //setLocalizationOnCanvasAndRender(lList, false);
            canvas.setCanvasOverlayLocalizationList(lList);
        }
    }

    /**
     * Called from the Localization panel "Hide" button. Hide the localization
     * overlayes
     */
        public void hideLocalizationButtonAction() {
        viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
        viewDex.windowLevel.setWindowLevel();
    }

    /**
     * Called from the Localization panel "Show" button. Shows the localization
     * overlayes
     */
    public void showLocalizationButtonAction() {
        // Don't know if this one is needed
        // zzzz erase taskPanel
        viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
        if (localizationSelectStatusExist()) {
            Point2D point = getSelectedLocalizationPoint();
            viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
            viewDex.vgTaskMainPanel.setRatingValue(point);
            viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
        }
        viewDex.windowLevel.setWindowLevel();
    }

    public void eraseLocalizationButtonAction() {
        if (runModeStatus == VgRunMode.SHOW_EXIST) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        //viewDex.canvas.setCanvasROIDistanceDrawingValue(0, 0, 0, 0);
        //viewDex.canvas.setCanvasROIDistanceUpdateStatus(false);
        //viewDex.canvas.setCanvasROIDistanceUpdateValue(null);

        //viewDex.appMainAdmin.vgControl.eraseAllMarkInStack();
        deleteTaskLocalizationResult();
        viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
        setCanvasOverlayStackInfo();

        // buttons
        if (viewDex.vgLocalizationPanel != null) {
            viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
            viewDex.vgLocalizationPanel.showHideButton.setEnabled(false);
        }
        viewDex.windowLevel.setWindowLevel();
    }

    /*
     * deleteSelectedLocalizationMark
     */
    public void deleteSelectedLocalizationMark() {
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);

        Point2D selPoint = getSelectedLocalizationPoint();
        viewDex.vgTaskPanelUtility.deleteTaskPanelResultItem(selPoint);
        viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
        deleteSelectedLocalizationItem();
        setLocalizationOverlayListInCanvas();

        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
        viewDex.windowLevel.setWindowLevel();
    }

    /*
     * deleteAllLocalizationMark
     */
    public void deleteAllLocalizationMark() {
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);

        deleteTaskLocalizationResult();
        viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
        setCanvasOverlayStackInfo();

        // buttons
        if (viewDex.vgLocalizationPanel != null) {
            viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
            viewDex.vgLocalizationPanel.showHideButton.setEnabled(false);
        }
        viewDex.windowLevel.setWindowLevel();
    }

    /**
     * Get the Localization.SELECT point from the localization list. return
     * <Point2D/> the selected localization point.
     */
    public Point2D getSelectedLocalizationPoint() {
        Point2D p = null;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        Iterator<StudyDbLocalization> iter = localizationList.iterator();

        while (iter.hasNext()) {
            StudyDbLocalization item = iter.next();
            if (item.getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED) {
                p = item.getImageSpacePointDouble();
                break;
            }
        }
        return p;
    }

    /**
     * Delete the <code><StudyDbLocalization><code/> list.
     * Delete the <code><VgTaskPanelResult><code/ list.
     */
    private void deleteTaskLocalizationResult() {
        //Why get the stackNode???
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
        for (int i = 0; i < imageList.size(); i++) {
            StudyDbImageNode imageNode = imageList.get(i);
            imageNode.deleteTaskLocalizationList();
            imageNode.deleteTaskLocalizationResultList();
        }
    }

    /*
     * deleteLocalizationList
     */
    public void deleteLocalizationList() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        imageNode.deleteLocalizationList();
    }

    public boolean localizationListExist() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (!localizationList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the canvas overlay <code>StudyDbStackNode<code/> info.
     */
    public void setCanvasOverlayStackInfo() {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        int stackNo = stackNode.getItemCnt();
        viewDex.canvas.setCanvasOverlayStackNoValue(Integer.toString(stackNo + 1));

        int markTot = getLocalizationSelectStackNoOfMarks();
        viewDex.canvas.setCanvasOverlayMarkNoValue(Integer.toString(markTot));
    }

    /**
     * Get the <code>timeStampLocalization<code/> of the selected localization mark.
     *
     * @return the <code>timeStampLocalization<code/> of the selected localization mark.
     */
    public long getLocalizationTimeStamp() {
        long ts = 0;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();

        if (localizationList != null) {
            for (int i = 0; i < localizationList.size(); i++) {
                int localizationStatus = localizationList.get(i).getLocalizationStatus();
                if (localizationStatus == StudyDbLocalizationStatus.ACTIVE) {
                    ts = localizationList.get(i).getTimeStampLocalization();
                    return ts;
                }
            }
        }
        return ts;
    }

    /**
     * Check if the selected image has any localization marks.
     *
     * @return <code>true<code/> if any location mark exist
     * @return<code>false<code/> if no mark exist.
     */
    public boolean getLocalizationMarkExistStatusForSelectedImage() {
        Boolean status = false;

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (!localizationList.isEmpty()) {
            status = true;
        }
        return status;
    }

    /**
     * Delete the selected localization for the selected imagenode.
     */
    public void deleteSelectedLocalizationItem() {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        Iterator<StudyDbLocalization> iter = localizationList.iterator();

        while (iter.hasNext()) {
            StudyDbLocalization item = iter.next();
            if (item.getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED) {
                iter.remove();
            }
        }
    }

    /**
     */
    public void updateLocalizationMode() {
        Point2D selectedPoint = getSelectedLocalizationPoint();
        boolean localizationActiveExist = localizationActiveStatusExist();
        boolean localizationSetStatusExist = localizationSetStatusExist();

        if ((selectedPoint != null || localizationActiveExist || localizationSetStatusExist)
                && ((viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
                || (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
                || (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST))) {
            viewDex.canvasControl.setLocalizationMode(1);
        } else {
            viewDex.canvasControl.setLocalizationMode(0);
        }
    }

    /**
     * Find out if there is a Localization.ACTIVE status in the
     * localization list.
     *
     * @return <code>true<code/> if a Localization.ACTIVE  status exist.
     * <code>false<code/> if no Localization.ACTIVE status exist.
     */
    public boolean localizationActiveStatusExist() {
        boolean status = false;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (localizationList != null) {
            for (int i = 0; i < localizationList.size(); i++) {
                if (localizationList.get(i).getLocalizationStatus() == StudyDbLocalizationStatus.ACTIVE) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    /**
     * Find out if there is a Localization.SET status in the localization list.
     *
     * @return <code>true<code/> if a Localization.SET  status exist.
     * <code>false<code/> if no Localization.SET status exist.
     */
    public boolean localizationSetStatusExist() {
        boolean status = false;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (localizationList != null) {
            for (int i = 0; i < localizationList.size(); i++) {
                if (localizationList.get(i).getLocalizationStatus() == StudyDbLocalizationStatus.SET) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    /**
     * Find out if there is a Localization.SELECT on the image.
     */
    public boolean localizationSelectStatusExist() {
        boolean status = false;

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        if (localizationList != null) {
            for (int i = 0; i < localizationList.size(); i++) {
                if (localizationList.get(i).getLocalizationStatus() == StudyDbLocalizationStatus.SELECTED) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    @Override
    public void resetLocalizationOverlay() {
        viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
    }

    /**
     * Get the total number of localization marks in the selected <code>StudyDbStackNode<code/> stack.
     *
     * @return the total number of localization marks in the selected stack.
     * Checked!
     */
    public int getLocalizationSelectStackNoOfMarks() {
        int markTotal = 0;

        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageList.iterator();

        while (iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            ArrayList<StudyDbLocalization> locList = imageNode.getLocalizationList();
            markTotal = markTotal + locList.size();
        }
        return markTotal;
    }

    @Override
    public void setKeyCtrlEnable(boolean status) {
        keyCtrlEnable = status;
    }

    @Override
    public boolean getKeyCtrlEnable() {
        return keyCtrlEnable;
    }

    /**
     * get the runMode status.
     *
     * @return
     */
    @Override
    public int getRunModeStatus() {
        return viewDex.appMainAdmin.vgControl.getRunModeStatus();
    }

    /**
     * Set the size for the Localization symbols.
     */
    public void setLocalizationSymbolSizeWidthValue(int size, int width,
            Color lineColor, Color positionTextColor) {

        //alZ     activeSymbolLineSize
        //seZ     setSymbolElipseSize
        //sellXZ  selectSymbolLineXSize
        //sellYZ  selectSymbolLineYSize
        localizationActiveSymbolLineSize = size;
        localizationSetSymbolElipseSize = size / 2.2;
        localizationSelectSymbolLineXSize = size * 1.2;
        localizationSelectSymbolLineYSize = size * 1.2;

        //new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        localizationSymbolStroke = new BasicStroke(width);
        canvas.setCanvasOverlayLocalizationSymbolProperties(localizationActiveSymbolLineSize,
                localizationSetSymbolElipseSize, localizationSelectSymbolLineXSize,
                localizationSelectSymbolLineYSize, localizationSymbolStroke,
                lineColor, positionTextColor);
    }

    /**
     * Set the canvasControlMode.
     */
    public void setCanvasControlMode(int mode) {
        canvasControlMode = mode;
    }

    /**
     * Get the cavasControlMode.
     *
     * @return the context menu constants.
     */
    @Override
    public int getCanvasControlMode() {
        return canvasControlMode;
    }

    /**
     * Call the requestFocusInWindow()
     */
    @Override
    public void setFocus() {
        //System.out.println("Localization.setFocus");
        viewDex.requestFocusInWindow();
    }

    /**
     * Clear the Goto input field.
     */
    @Override
    public void setGotoInputField() {
        if (viewDex.vgStudyNextCaseExtendedControl != null) {
            viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
        }
    }

    /*
     * NOT IN USE
     */
    @Override
    public boolean mousePressedEditSelectAction(Point2D p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean mousePressedRightCreateAction(Point2D p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
