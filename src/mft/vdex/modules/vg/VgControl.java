/* @(#) VgControl.java 06/08/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

 /*
 * @author Sune Svensson.
 */
package mft.vdex.modules.vg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import mft.vdex.app.AppMainAdmin;
import mft.vdex.dialog.ProgressMonitorDialog;
import mft.vdex.dialog.StackSortingProgressMonitorWorker;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDb;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.imageio.ImageLoadingWorker;
//import mft.vdex.imageio.StudyLoader_old;
import mft.vdex.app.AppPropertyUtils;
//import org.dcm4che.data.Dataset;
import mft.vdex.ds.StudyDbNodeType;
import mft.vdex.ds.StudyDbStackType;
import mft.vdex.viewer.WindowingMode;
import mft.vdex.ds.StudyDbLocalizationStatus;
import mft.vdex.ds.StudyDbUtility;
import mft.vdex.imageio.DicomFileReader;
import mft.vdex.util.Stopwatch;
import org.dcm4che3.data.Attributes;

/**
 * The <code>VgControl</code> class ...
 *
 * The taskPanelQuestionList <code>VgTaskPanelQuestionList</code> stores the
 * Task panel questions and the Checkbox label text read from the
 * vgstudy-xxxx.properties file. This list is used to dynamical create the
 * taskPanel GUI.
 *
 * The studyImageList <code>StudyDbImageNode</code> stores information about a
 * single image (name, path, dicom meta information ...)
 */
public class VgControl implements KeyListener, ActionListener, PropertyChangeListener, VgRunMode {

    public AppMainAdmin appMainAdmin;
    public StudyDbUtility studyDbUtility;
    private VgLog studyLog;
    private AppPropertyUtils propUtils;
    private boolean loadStackInBackgroundStatus;
    private static boolean cineLoopStartAutoStatus;
    //private StudyLoader_old studyLoader;

    // Cine-loop
    private boolean cineLoopStatus;
    private Timer timerCineLoop;
    public int cineLoopDirection = 0;
    private int cineLoopTimerValue = -1;
    private int selImageListSize;
    public boolean showOrgImage = true;
    private long prevTime;
    private int runMode = VgRunMode.NONE;
    public boolean cineLoopRunningStatus = false;
    public ImageLoadingWorker imageLoadingWorker;  // Status to show when the loading thread is in use.
    boolean imageLoadingWorkerStatus = false;
    ProgressMonitor progressMonitor;
    StackSortingProgressMonitorWorker pmd;
    ProgressMonitorDialog progressMonitorDialog;
    private int historyOption;
    boolean runModeFlag = false;
    boolean newStackStatus = true;
    private boolean debug = false;

    /**
     * Creates a new instance of StudyVGAControl
     */
    public VgControl() {
    }

    /* Constructor */
    public VgControl(AppMainAdmin appmainadmin, boolean runModeFlag) {
        this.appMainAdmin = appmainadmin;
        this.runModeFlag = runModeFlag;

        init();

        if (runModeFlag) {
            appMainAdmin.viewDex.eyeTracking.initEyeTracking();
        }
    }

    private void init() {
        propUtils = new AppPropertyUtils();
        studyDbUtility = new StudyDbUtility(appMainAdmin);
        //studyLoader = new StudyLoader();
        //KeyHandler listener = new KeyHandler();
        //addKeyListener();
    }

    /**
     * *************************************************************
     *
     * Run mode
     *
     ***************************************************************
     */
    public int getRunMode() {
        int runMode = VgRunMode.NONE;

        runMode = getCreateRunMode();
        if (runMode == VgRunMode.CREATE_NEW || runMode == VgRunMode.CREATE_EXIST) {
            if (runMode == VgRunMode.CREATE_NEW) {
                runMode = VgRunMode.CREATE_EXIST;
            }
            return runMode;
        }

        runMode = getDemoRunMode();
        if (runMode == VgRunMode.DEMO_NEW || runMode == VgRunMode.DEMO_EXIST) {
            runMode = VgRunMode.DEMO_EXIST;
            return runMode;
        }

        runMode = getShowRunMode();
        if (runMode == VgRunMode.SHOW_EXIST) {
            return runMode;
        }

        runMode = getEditRunMode();
        if (runMode == VgRunMode.EDIT_EXIST || runMode == VgRunMode.EDIT_ERROR) {
            return runMode;
        }

        return runMode;
    }
    
    //---------------------------------------------------
    // Create runMode
    //---------------------------------------------------
    /**
     * Get create runMode.
     */
    private int getCreateRunMode() {
        int runMode = VgRunMode.NONE;

        boolean nameExist = createStudyNameExist();
        boolean historyExist = appMainAdmin.viewDex.vgHistoryMainUtil.exist();

        if (nameExist) {
            if (historyExist) {
                runMode = VgRunMode.CREATE_EXIST;
            } else {
                runMode = VgRunMode.CREATE_NEW;
            }
        }
        return runMode;
    }

    /**
     * Check if the "create" study name exist.
     */
    private boolean createStudyNameExist() {
        if (appMainAdmin.viewDex.appProperty.getLoginName().contains("demo")
                || appMainAdmin.viewDex.appProperty.getLoginName().contains("show")
                || appMainAdmin.viewDex.appProperty.getLoginName().contains("edit")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get the demo runMode. DEPRECATED
     */
    /*
    private int getDemoRunMode() {
    int runMode = VgRunMode.NONE;

    boolean demoNameExist = demoStudyNameExist();
    boolean demoHistoryExist = historyDemoExist();
    boolean demoImageDbPropertyExist = demoStudyImageDbPropertyExist();

    if(demoNameExist){
    if(!demoImageDbPropertyExist){
    demoStudyPropErrorMessage();
    runMode = VgRunMode.DEMO_ERROR;
    }
    if(demoHistoryExist){
    if(demoStudyInit()){
    runMode = VgRunMode.DEMO_EXIST;
    return runMode;
    }
    }
    runMode = VgRunMode.DEMO_NEW;
    }
    return runMode;
    }*/
    /**
     * Get the demo runMode.
     */
    private int getDemoRunMode() {
        int runMode = VgRunMode.NONE;

        boolean demoPropertyExist = appMainAdmin.viewDex.appProperty.studyPropertyExist("imagedb.directory.demo");
        boolean demoNameExist = loginNameContains("demo");
        boolean demoHistoryExist = appMainAdmin.viewDex.vgHistoryDemoUtil.exist();

        if (demoNameExist) {
            if (!demoPropertyExist) {
                appMainAdmin.viewDex.appProperty.demoStudyPropErrorMessage();
                runMode = VgRunMode.DEMO_ERROR;
            }
            if (demoHistoryExist) {
                runMode = VgRunMode.DEMO_EXIST;
                //demoStudyInit();
                return runMode;
            }
            runMode = VgRunMode.DEMO_NEW;
        }
        return runMode;
    }

    /**
     * Check if login name contains any runmode string.
     */
    private boolean loginNameContains(String str) {
        if (appMainAdmin.viewDex.appProperty.getLoginName().contains(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check for a demo study. If history exist check if the study is done.
     * Check for "delete.demo-log" property. If the study is done delete the
     * logfiles and the history file. Return the initStatus status.
     */
    public void demoStudyInit() {
        boolean initStatus = false;
        boolean status1 = false;
        boolean status2 = false;
        boolean status3 = false;
        boolean status4 = false;

        //boolean demoPropertyExist = studyPropertyExist("imagedb.directory.demo");
        boolean demoNameExist = loginNameContains("demo");
        boolean demoHistoryExist = appMainAdmin.viewDex.vgHistoryDemoUtil.exist();

        if (demoNameExist && demoHistoryExist) {
            VgHistory demoHistory = appMainAdmin.viewDex.vgHistoryDemoUtil.readDemoHistoryObject();
            if (demoHistory != null) {
                if (demoHistory.getStudyDone()) {
                    String defDeleteDemoLog = "y";
                    String key = "delete.demo-log";
                    String s1 = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
                    if (s1.equals("")) {
                        s1 = defDeleteDemoLog;
                    }

                    String[] log1Path = appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfilePath("log.log1-directory", "demo");
                    String[] log2Path = appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfilePath("log.log2-directory", "demo");
                    String[] history1Path = appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");
                    String[] history2Path = appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "demo");

                    if (s1.equalsIgnoreCase("yes") || s1.equalsIgnoreCase("Y")) {
                        status1 = appMainAdmin.viewDex.vgHistoryUtil.delete(log1Path[1]);
                        status2 = appMainAdmin.viewDex.vgHistoryUtil.delete(log2Path[1]);
                        status3 = appMainAdmin.viewDex.vgHistoryUtil.delete(history1Path[1]);
                        status4 = appMainAdmin.viewDex.vgHistoryUtil.delete(history2Path[1]);
                    }
                }
            }
        }
    }

    //-------------------------------------------------------
    // Show runMode
    //-------------------------------------------------------
    /**
     * Get the show runMode.
     */
    private int getShowRunMode() {
        int runMode = VgRunMode.NONE;

        boolean showNameExist = showStudyNameExist();

        if (showNameExist) {
            runMode = VgRunMode.SHOW_EXIST;
        }

        return runMode;
    }

    /**
     * Check if the "show" study name exist.
     */
    private boolean showStudyNameExist() {
        if (appMainAdmin.viewDex.appProperty.getLoginName().contains("show")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the global userName to (userName (minus) "show").
     */
    private void initShowMode() {
        if (appMainAdmin.viewDex.appProperty.getUserName().contains("show")) {
            // Find the username (userName - "show").
            String user = appMainAdmin.viewDex.appProperty.getUserName().substring(0,
                    appMainAdmin.viewDex.appProperty.getUserName().length() - 4);

            // Set the global userName
            appMainAdmin.viewDex.appProperty.setUserName(user);
        }
    }

    // ----------------------------------------------------------
    // Edit runMode
    // ----------------------------------------------------------
     
    /**
     * Get the edit runMode.
     */
    private int getEditRunMode() {
        int runMode = VgRunMode.NONE;

        boolean editNameExist = editStudyNameExist();

        if (editNameExist) {
            runMode = VgRunMode.EDIT_EXIST;
        }
        return runMode;
    }

    /**
     * Check if the "edit" study name exist.
     */
    private boolean editStudyNameExist() {
        if (appMainAdmin.viewDex.appProperty.getLoginName().contains("edit")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ***********************************************************
     *
     * Init state
     *
     ************************************************************
     */
    /**
     *
     */
    public void setInitState() {
        String key;

        // Exceptions
        if (appMainAdmin.viewDex.appProperty.getStudyProperties() == null) {
            String str = "   No study properties find in history object." + "   " + "System will exit.   ";
            System.out.print("Error: VgControl.setInitState:" + str);
            JOptionPane.showMessageDialog(appMainAdmin.viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        /**
         * **********************************************************
         *
         * canvas.overlay.windowwidth.pos
         *
         ***********************************************************
         */
        // Default value for screen resolution 1600x1200 (90,910)
        // Default value for screen resolution 2048x1536 (30x1200)
        // Default value for screen resolution 1280x1024 (100,900)
        // Default value for screen resolution 1920x1200 (100,910)
        // Default value for screen resolution 1440x900 (43,690)
        // Default value for screen resolution 2880x1800 (43,690)
        int[] defValue = {100, 900};

        if (d.width == 1600 && d.height == 1200) {
            defValue[0] = 90;
            defValue[1] = 910;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue[0] = 30;
            defValue[1] = 1200;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue[0] = 100;
            defValue[1] = 900;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue[0] = 100;
            defValue[1] = 910;
        } else if (d.width == 1440 && d.height == 900) {
            defValue[0] = 43;
            defValue[1] = 690;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue[0] = 43;
            defValue[1] = 690;
        }

        key = "canvas.overlay.windowwidth.pos";
        int[] value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue[0];
            value[1] = defValue[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayWindowWidthPos(value[0], value[1]);

        /**
         * *****************************************************
         *
         * canvas.overlay.windowcenter.pos
         *
         ******************************************************
         */
        // Default value for screen resolution 1600x1200 (90,885)
        // Default value for screen resolution 2048x1536 (30,1230)
        // Default value for screen resolution 1280x1024 (100,880)
        // Default value for screen resolution 1920x1200 (100,885)
        // Default value for screen resolution 1440x900 (43,670)
        // Default value for screen resolution 2880x1800 (43,670)
        int[] defValue2 = {100, 880};

        if (d.width == 1600 && d.height == 1200) {
            defValue2[0] = 90;
            defValue2[1] = 885;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue2[0] = 30;
            defValue2[1] = 1230;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue2[0] = 100;
            defValue2[1] = 880;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue2[0] = 100;
            defValue2[1] = 885;
        } else if (d.width == 1440 && d.height == 900) {
            defValue2[0] = 43;
            defValue2[1] = 670;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue2[0] = 43;
            defValue2[1] = 670;
        }

        key = "canvas.overlay.windowcenter.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue2[0];
            value[1] = defValue2[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayWindowCenterPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayWindowLevelStatus(false);

        /**
         * ****************************************************************
         *
         * canvas.overlay.mouseposition.pos.x
         *
         ****************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,860)
        // Default value for screen resolution 2048x1536 (1480x1200)
        // Default value for screen resolution 1280x1024 (900,880)
        // Default value for screen resolution 1920x1200 (1060,860)
        // Default value for screen resolution 1440x900 (850,650)
        // Default value for screen resolution 2880x1800 (850, 650)
        int[] defValue3 = {900, 880};

        if (d.width == 1600 && d.height == 1200) {
            defValue3[0] = 900;
            defValue3[1] = 860;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue3[0] = 1480;
            defValue3[1] = 1200;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue3[0] = 900;
            defValue3[1] = 880;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue3[0] = 1060;
            defValue3[1] = 860;
        } else if (d.width == 1440 && d.height == 900) {
            defValue3[0] = 850;
            defValue3[1] = 650;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue3[0] = 850;
            defValue3[1] = 650;
        }

        key = "canvas.overlay.mouseposition.pos.x";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue3[0];
            value[1] = defValue3[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayMousPositionPosX(value[0], value[1]);

        /**
         * ****************************************************************
         *
         * canvas.overlay.mouseposition.pos.y
         *
         ****************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,885)
        // Default value for screen resolution 2048x1536 (1480x1230)
        // Default value for screen resolution 1280x1024 (900,900)
        // Default value for screen resolution 1920x1200 (1060,885)
        // Default value for screen resolution 1440x900 (920,650)
        // Default value for screen resolution 2880x1800 (920,650)
        int[] defValue4 = {900, 900};

        if (d.width == 1600 && d.height == 1200) {
            defValue4[0] = 900;
            defValue4[1] = 885;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue4[0] = 1480;
            defValue4[1] = 1230;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue4[0] = 900;
            defValue4[1] = 900;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue4[0] = 1060;
            defValue4[1] = 885;
        } else if (d.width == 1440 && d.height == 900) {
            defValue4[0] = 850;
            defValue4[1] = 670;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue4[0] = 850;
            defValue4[1] = 670;
        }

        key = "canvas.overlay.mouseposition.pos.y";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue4[0];
            value[1] = defValue4[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayMousPositionPosY(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayMousePositionStatus(false);

        /**
         * ****************************************************************
         *
         * canvas.overlay.mouseposition.pixelvalue.pos
         *
         ****************************************************************
         */
        // Default value for screen resolution 1600x1200 (90,860)
        // Default value for screen resolution 2048x1536 (1480,1260)
        // Default value for screen resolution 1280x1024 (100,860)
        // Default value for screen resolution 1920x1200 (100,860)
        // Default value for screen resolution 1440x900 (43,650)
        // Default value for screen resolution 2880x1800 (43,650)
        int[] defValue5 = {100, 860};

        if (d.width == 1600 && d.height == 1200) {
            defValue5[0] = 90;
            defValue5[1] = 860;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue5[0] = 1480;
            defValue5[1] = 1260;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue5[0] = 100;
            defValue5[1] = 860;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue5[0] = 100;
            defValue5[1] = 860;
        } else if (d.width == 1440 && d.height == 900) {
            defValue5[0] = 43;
            defValue5[1] = 650;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue5[0] = 43;
            defValue5[1] = 650;
        }

        key = "canvas.overlay.mouseposition.pixelvalue.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue5[0];
            value[1] = defValue5[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayMousePositionPixelValuePos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayMousePositionPixelValueStatus(false);
    }

    /**
     * ***********************************************************
     *
     * Create
     *
     ************************************************************
     */
    /**
     * Set the create init state.
     */
    public void setCreateInitState() {
        String key;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.x
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,70)
        // Default value for screen resolution 2048x1536 (1480x80)
        // Default value for screen resolution 1280x1024 (800,60)
        // Default value for screen resolution 1920x1200 (1060,60)
        // Default value for screen resolution 1440x900 (850,80)
        // Default value for screen resolution 2880x1800 (850,80)
        int[] defValue4 = {800, 60};

        if (d.width == 1600 && d.height == 1200) {
            defValue4[0] = 900;
            defValue4[1] = 70;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue4[0] = 1480;
            defValue4[1] = 80;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue4[0] = 800;
            defValue4[1] = 60;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue4[0] = 1060;
            defValue4[1] = 60;
        } else if (d.width == 1440 && d.height == 900) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        }

        key = "canvas.overlay.localization.pos.x";
        int[] value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue4[0];
            value[1] = defValue4[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosX(value[0], value[1]);

        /**
         * **************************************************
         * canvas.overlay.localization.pos.y
         * *************************************************
         */
        // Default value for screen resolution 1600x1200 (900,95)
        // Default value for screen resolution 2048x1536 (1480,110)
        // Default value for screen resolution 1280x1024 (800,80)
        // Default value for screen resolution 1920x1200 (1060,85)
        // Default value for screen resolution 1440x900 (850,100)
        // Default value for screen resolution 2880x1800 (850,100)
        int[] defValue5 = {800, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue5[0] = 900;
            defValue5[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue5[0] = 1480;
            defValue5[1] = 110;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue5[0] = 800;
            defValue5[1] = 80;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue5[0] = 1060;
            defValue5[1] = 85;
        } else if (d.width == 1440 && d.height == 900) {
            defValue5[0] = 850;
            defValue5[1] = 100;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue5[0] = 850;
            defValue5[1] = 100;
        }

        key = "canvas.overlay.localization.pos.y";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue5[0];
            value[1] = defValue5[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayPosLocalizationY(value[0], value[1]);

        /**
         * **************************************************
         * canvas.overlay.localization.pos.z
         * *************************************************
         */
        // Default value for screen resolution 1600x1200 (900,120)
        // Default value for screen resolution 2048x1536 (1480x140)
        // Default value for screen resolution 1280x1024 (800,100)
        // Default value for screen resolution 1920x1200 (1060,110)
        // Default value for screen resolution 1440x900 (850,120)
        // Default value for screen resolution 2880x1800 (850,120)
        int[] defValue6 = {800, 100};

        if (d.width == 1600 && d.height == 1200) {
            defValue6[0] = 900;
            defValue6[1] = 120;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue6[0] = 1480;
            defValue6[1] = 140;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue6[0] = 800;
            defValue6[1] = 100;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue6[0] = 1060;
            defValue6[1] = 110;
        } else if (d.width == 1440 && d.height == 900) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        }

        key = "canvas.overlay.localization.pos.z";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue6[0];
            value[1] = defValue6[1];
        }

        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosZ(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(true);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);

        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoStatus(false);

        /**
         * ***********************************************************
         * Color lookup tables
         * **********************************************************
         */
        key = "color-lookup-tables";
        String str = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        boolean lutDefined = true;

        if (str.equalsIgnoreCase("")) {
            lutDefined = false;
        }

        appMainAdmin.viewDex.windowLevel.setLookupTableDefined(lutDefined);
        appMainAdmin.viewDex.windowLevel.setLookupTableDefinedStr(str);
        //appMainAdmin.viewDex.windowLevel.initColorLUT();

        if (lutDefined) {
            appMainAdmin.viewDex.windowLevel.readColorLUT(str);
        }

        /**
         * *************************************************************
         * WindowingMode, NM & MR "whitepoint" values
         * ************************************************************
         */
        key = "fixed-minimum.value";
        int wm = 0;

        String s1 = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (s1.equals("")) {
            wm = WindowingMode.CLASSIC;
        } else {
            wm = WindowingMode.FIXED_MINIMUM;
        }

        int fixedMin = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        // This value is set in method CanvasControl.setCanvasOverlayWindowingStatus().
        appMainAdmin.viewDex.canvasControl.setWindowingMode(wm);
        appMainAdmin.viewDex.canvasControl.setWindowingFixedMinimumValue(fixedMin);

        appMainAdmin.viewDex.windowLevel.setWindowingMode(wm);
        appMainAdmin.viewDex.windowLevel.setWindowingFixedMinimumValue(fixedMin);

        //Default value for screen resolution 1600x1200 (30x980)
        //Default value for screen resolution 2048x1536 (30x1280)
        // position "fixed-minimum value"
        int[] defValue7 = {30, 980};

        if (d.width == 1600 && d.height == 1200) {
            defValue7[0] = 30;
            defValue7[1] = 980;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue7[0] = 30;
            defValue7[1] = 1280;
        }

        key = "canvas.overlay.fixed-minimum.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue7[0];
            value[1] = defValue7[1];
        }

        appMainAdmin.viewDex.canvas.setCanvasOverlayFixedMinimumPos(value[0], value[1]);
    }

    /**
     * ***********************************************************
     *
     * Show
     *
     ************************************************************
     */
    /**
     * Set the init state.
     */
    public void setShowInitState() {
        String key;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        /**
         * ****************************************************************
         * canvas.overlay.stack.no.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,95)
        // Default value for screen resolution 2048x1536 (30,1300)
        // Default value for screen resolution 1280x1024 (100,80)
        // Default value for screen resolution 1920x1200 (100,85)
        // Default value for screen resolution 1440x900 (43,80)
        // Default value for screen resolution 2880x1800 (43,80)
        int[] defValue = {100, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue[0] = 80;
            defValue[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue[0] = 30;
            defValue[1] = 1300;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue[0] = 100;
            defValue[1] = 80;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue[0] = 100;
            defValue[1] = 85;
        } else if (d.width == 1440 && d.height == 900) {
            defValue[0] = 43;
            defValue[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue[0] = 43;
            defValue[1] = 80;
        }

        key = "canvas.overlay.stack.no.pos";
        int[] value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue[0];
            value[1] = defValue[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoStatus(true);

        /**
         * ****************************************************************
         * canvas.overlay.stack.localization.no.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,120)
        // Default value for screen resolution 2048x1536 (30,1370)
        // Default value for screen resolution 1280x1024 (100,100)
        // Default value for screen resolution 1920x1200 (100,110)
        // Default value for screen resolution 1440x900 (43,100)
        // Default value for screen resolution 2880x1800 (43,100)
        int[] defValue2 = {100, 100};

        if (d.width == 1600 && d.height == 1200) {
            defValue2[0] = 80;
            defValue2[1] = 120;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue2[0] = 30;
            defValue2[1] = 1370;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue2[0] = 100;
            defValue2[1] = 100;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue2[0] = 100;
            defValue2[1] = 110;
        } else if (d.width == 1440 && d.height == 900) {
            defValue2[0] = 43;
            defValue2[1] = 100;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue2[0] = 43;
            defValue2[1] = 100;
        }

        key = "canvas.overlay.stack.localization.no.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue2[0];
            value[1] = defValue2[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoStatus(true);

        /**
         * ****************************************************************
         * canvas.overlay.patientid.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,70)
        // Default value for screen resolution 2048x1536 (30,1370)
        // Default value for screen resolution 1280x1024 (100,60)
        // Default value for screen resolution 1920x1200 (100,60)
        // Default value for screen resolution 1440x900 (43,60)
        // Default value for screen resolution 2880x1800 (43,60)
        int[] defValue3 = {100, 60};

        if (d.width == 1600 && d.height == 1200) {
            defValue3[0] = 80;
            defValue3[1] = 70;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue3[0] = 30;
            defValue3[1] = 1370;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue3[0] = 100;
            defValue3[1] = 60;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue3[0] = 100;
            defValue3[1] = 60;
        } else if (d.width == 1440 && d.height == 900) {
            defValue3[0] = 43;
            defValue3[1] = 60;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue3[0] = 43;
            defValue3[1] = 60;
        }

        boolean patentInfoStatus = false;
        String defPatientInfo = "y";
        key = "canvas.overlay.patientinfo";
        String patientInfo = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (patientInfo.equals("")) {
            patientInfo = defPatientInfo;
        }
        if (patientInfo.equalsIgnoreCase("yes") || patientInfo.equalsIgnoreCase("y")) {
            patentInfoStatus = true;
        }

        // patientid position
        key = "canvas.overlay.patientid.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue3[0];
            value[1] = defValue3[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdStatus(patentInfoStatus);

        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);
        appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(0);
        studyDbUtility.setSelectedImageNodeCntToFirstImage();
        appMainAdmin.viewDex.vgHistory.setStudyDone(false);

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.x
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,95)
        // Default value for screen resolution 2048x1536 (1480,110)
        // Default value for screen resolution 1280x1024 (800,60)
        // Default value for screen resolution 1920x1200 (1060,60)
        // Default value for screen resolution 1440x900 (850,80)
        // Default value for screen resolution 2880x1800 (850,80)
        int[] defValue4 = {980, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue4[0] = 900;
            defValue4[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue4[0] = 1480;
            defValue4[1] = 110;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue4[0] = 800;
            defValue4[1] = 60;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue4[0] = 1060;
            defValue4[1] = 60;
        } else if (d.width == 1440 && d.height == 900) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        }

        key = "canvas.overlay.localization.pos.x";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue4[0];
            value[1] = defValue4[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosX(value[0], value[1]);

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.y
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,95)
        // Default value for screen resolution 2048x1536 (1480,110)
        // Default value for screen resolution 1280x1024 (800,80)
        // Default value for screen resolution 1920x1200 (1060,85)
        // Default value for screen resolution 1440x900 (850,80)
        // Default value for screen resolution 2880x1800 (850,100)
        int[] defValue5 = {800, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue5[0] = 900;
            defValue5[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue5[0] = 1480;
            defValue5[1] = 110;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue5[0] = 800;
            defValue5[1] = 80;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue5[0] = 1060;
            defValue5[1] = 85;
        } else if (d.width == 1440 && d.height == 900) {
            defValue5[0] = 850;
            defValue5[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue5[0] = 850;
            defValue5[1] = 100;
        }

        key = "canvas.overlay.localization.pos.y";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue5[0];
            value[1] = defValue5[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayPosLocalizationY(value[0], value[1]);

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.z
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,120)
        // Default value for screen resolution 2048x1536 (1480x140)
        // Default value for screen resolution 1280x1024 (800,100)
        // Default value for screen resolution 1920x1200 (1060,110)
        // Default value for screen resolution 1440x900 (850,120)
        // Default value for screen resolution 2880x1800 (850,120)
        int[] defValue6 = {800, 100};

        if (d.width == 1600 && d.height == 1200) {
            defValue6[0] = 900;
            defValue6[1] = 120;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue6[0] = 1480;
            defValue6[1] = 140;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue6[0] = 800;
            defValue6[1] = 100;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue6[0] = 1060;
            defValue6[1] = 110;
        } else if (d.width == 1440 && d.height == 900) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        }

        key = "canvas.overlay.localization.pos.z";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue6[0];
            value[1] = defValue6[1];
        }

        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosZ(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(true);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);

        /**
         * ***********************************************************
         * Color lookup tables
         * **********************************************************
         */
        key = "color-lookup-tables";
        String str = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        boolean lutDefined = true;

        if (str.equalsIgnoreCase("")) {
            lutDefined = false;
        }

        appMainAdmin.viewDex.windowLevel.setLookupTableDefined(lutDefined);
        appMainAdmin.viewDex.windowLevel.setLookupTableDefinedStr(str);
        //appMainAdmin.viewDex.windowLevel.initColorLUT();
        if (lutDefined) {
            appMainAdmin.viewDex.windowLevel.readColorLUT(str);
        }

        /**
         * *************************************************************
         * WindowingMode, NM & MR "whitepoint" values
         * ************************************************************
         */
        key = "fixed-minimum.value";
        int wm = 0;

        String s1 = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (s1.equals("")) {
            wm = WindowingMode.CLASSIC;
        } else {
            wm = WindowingMode.FIXED_MINIMUM;
        }

        int fixedMin = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        // This value is set in method CanvasControl.setCanvasOverlayWindowingStatus().
        appMainAdmin.viewDex.canvasControl.setWindowingMode(wm);
        appMainAdmin.viewDex.canvasControl.setWindowingFixedMinimumValue(fixedMin);

        appMainAdmin.viewDex.windowLevel.setWindowingMode(wm);
        appMainAdmin.viewDex.windowLevel.setWindowingFixedMinimumValue(fixedMin);

        //Default value for screen resolution 1600x1200 (30x980)
        //Default value for screen resolution 2048x1536 (30x1280)
        // position "fixed-minimum value"
        int[] defValue7 = {30, 980};

        if (d.width == 1600 && d.height == 1200) {
            defValue7[0] = 30;
            defValue7[1] = 980;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue7[0] = 30;
            defValue7[1] = 1280;
        }

        key = "canvas.overlay.fixed-minimum.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue7[0];
            value[1] = defValue7[1];
        }

        appMainAdmin.viewDex.canvas.setCanvasOverlayFixedMinimumPos(value[0], value[1]);
    }

    /**
     * Set the given answers and marks. BIRADS NOT IN USE REMOVE!
     */
    public void runShowVgStudyUpdateAnswerAndMarks() {
        //appMainAdmin.viewDex.vgTaskMainPanel.updateRatingValues();
        //appMainAdmin.viewDex.vgTaskMainPanel.updateBIRADSRatingValues();
        //appMainAdmin.viewDex.localization.setLocalizationSetSymbolAndRender();
    }

    /**
     * Set the showStudyExist status.
     *
     * @param status
     */
    public void setRunModeStatus(int status) {
        runMode = status;
    }

    /**
     * Get the runModeStatusstatus.
     *
     * @param status
     */
    public int getRunModeStatus() {
        return runMode;
    }

    /**
     * Set the <code/>VgHistoryOptionType</code> value.
     *
     * @param status
     */
    public void setHistoryOptionStatus(int status) {
        historyOption = status;
    }

    /**
     * Get the <code/>VgHistoryOptionType</code> value.
     *
     * @param status
     */
    public int getHistoryOptionStatus() {
        return historyOption;
    }

    /**
     * *********************************************************
     *
     * Edit mode
     *
     * *********************************************************
     */
    /**
     * Set the init state.
     */
    public void setEditInitState() {
        String key;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        /**
         * ****************************************************************
         * canvas.overlay.stack.no.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,95)
        // Default value for screen resolution 2048x1536 (30,1300)
        // Default value for screen resolution 1280x1024 (100,80)
        // Default value for screen resolution 1920x1200 (100,85)
        // Default value for screen resolution 1440x900 (43,80)
        // Default value for screen resolution 2880x1800 (43,80)
        int[] defValue = {100, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue[0] = 80;
            defValue[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue[0] = 30;
            defValue[1] = 1300;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue[0] = 100;
            defValue[1] = 80;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue[0] = 100;
            defValue[1] = 85;
        } else if (d.width == 1440 && d.height == 900) {
            defValue[0] = 43;
            defValue[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue[0] = 43;
            defValue[1] = 80;
        }

        key = "canvas.overlay.stack.no.pos";
        int[] value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue[0];
            value[1] = defValue[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoStatus(true);

        /**
         * ****************************************************************
         * canvas.overlay.stack.localization.no.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,120)
        // Default value for screen resolution 2048x1536 (30,1370)
        // Default value for screen resolution 1280x1024 (100,100)
        // Default value for screen resolution 1920x1200 (100,110)
        // Default value for screen resolution 1440x900 (43,100)
        // Default value for screen resolution 2880x1800 (43,100)
        int[] defValue2 = {100, 100};

        if (d.width == 1600 && d.height == 1200) {
            defValue2[0] = 80;
            defValue2[1] = 120;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue2[0] = 30;
            defValue2[1] = 1370;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue2[0] = 100;
            defValue2[1] = 100;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue2[0] = 100;
            defValue2[1] = 110;
        } else if (d.width == 1440 && d.height == 900) {
            defValue2[0] = 43;
            defValue2[1] = 100;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue2[0] = 43;
            defValue2[1] = 100;
        }

        key = "canvas.overlay.stack.localization.no.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue2[0];
            value[1] = defValue2[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoStatus(true);

        /**
         * ****************************************************************
         * canvas.overlay.patientid.pos
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (80,70)
        // Default value for screen resolution 2048x1536 (30,1370)
        // Default value for screen resolution 1280x1024 (100,60)
        // Default value for screen resolution 1920x1200 (100,60)
        // Default value for screen resolution 1440x900 (43,60)
        // Default value for screen resolution 2880x1800 (43,60)
        int[] defValue3 = {100, 60};

        if (d.width == 1600 && d.height == 1200) {
            defValue3[0] = 80;
            defValue3[1] = 70;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue3[0] = 30;
            defValue3[1] = 1370;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue3[0] = 100;
            defValue3[1] = 60;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue3[0] = 100;
            defValue3[1] = 60;
        } else if (d.width == 1440 && d.height == 900) {
            defValue3[0] = 43;
            defValue3[1] = 60;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue3[0] = 43;
            defValue3[1] = 60;
        }

        // patientinfo
        boolean patentInfoStatus = false;
        String defPatientInfo = "y";
        key = "canvas.overlay.patientinfo";
        String patientInfo = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (patientInfo.equals("")) {
            patientInfo = defPatientInfo;
        }
        if (patientInfo.equalsIgnoreCase("yes") || patientInfo.equalsIgnoreCase("y")) {
            patentInfoStatus = true;
        }

        // patientid position
        key = "canvas.overlay.patientid.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue3[0];
            value[1] = defValue3[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdPos(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdStatus(patentInfoStatus);

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.x
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,70)
        // Default value for screen resolution 2048x1536 (1480,80)
        // Default value for screen resolution 1280x1024 (800,60)
        // Default value for screen resolution 1920x1200 (1060,60)
        // Default value for screen resolution 1440x900 (850,80)
        // Default value for screen resolution 2880x1800 (850,80)
        int[] defValue4 = {980, 80};

        if (d.width == 1600 && d.height == 1200) {
            defValue4[0] = 900;
            defValue4[1] = 70;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue4[0] = 1480;
            defValue4[1] = 80;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue4[0] = 800;
            defValue4[1] = 60;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue4[0] = 1060;
            defValue4[1] = 60;
        } else if (d.width == 1440 && d.height == 900) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue4[0] = 850;
            defValue4[1] = 80;
        }

        key = "canvas.overlay.localization.pos.x";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue4[0];
            value[1] = defValue4[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosX(value[0], value[1]);

        /**
         * ***************************************************************
         * canvas.overlay.localization.pos.y
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,95)
        // Default value for screen resolution 2048x1536 (1480x110)
        // Default value for screen resolution 1280x1024 (800,80)
        // Default value for screen resolution 1920x1200 (1060,85)
        // Default value for screen resolution 1440x900 (850,100)
        // Default value for screen resolution 2880x1800 (850,100)
        int[] defValue5 = {980, 110};

        if (d.width == 1600 && d.height == 1200) {
            defValue5[0] = 900;
            defValue5[1] = 95;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue5[0] = 1480;
            defValue5[1] = 110;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue5[0] = 800;
            defValue5[1] = 80;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue5[0] = 1060;
            defValue5[1] = 85;
        } else if (d.width == 1440 && d.height == 900) {
            defValue5[0] = 850;
            defValue5[1] = 100;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue5[0] = 850;
            defValue5[1] = 100;
        }

        key = "canvas.overlay.localization.pos.y";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue5[0];
            value[1] = defValue5[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayPosLocalizationY(value[0], value[1]);

        /**
         * ****************************************************************
         * canvas.overlay.localization.pos.z
         * ***************************************************************
         */
        // Default value for screen resolution 1600x1200 (900,120)
        // Default value for screen resolution 2048x1536 (1480,140)
        // Default value for screen resolution 1280x1024 (800,100)
        // Default value for screen resolution 1920x1200 (1060,110)
        // Default value for screen resolution 1440x900 (850,120)
        // Default value for screen resolution 2880x1800 (850,120)
        int[] defValue6 = {980, 140};

        if (d.width == 1600 && d.height == 1200) {
            defValue6[0] = 900;
            defValue6[1] = 120;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue6[0] = 1480;
            defValue6[1] = 140;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue6[0] = 800;
            defValue6[1] = 100;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue6[0] = 1060;
            defValue6[1] = 110;
        } else if (d.width == 1440 && d.height == 900) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        } else if (d.width == 2880 && d.height == 1800) {
            defValue6[0] = 850;
            defValue6[1] = 120;
        }

        key = "canvas.overlay.localization.pos.z";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue6[0];
            value[1] = defValue6[1];
        }
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPosZ(value[0], value[1]);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(true);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);

        /**
         * *************************************************************
         * Set the stackNodeCnt, imageNodeCnt and setStudyDone
         * ************************************************************
         */
        appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(0);
        appMainAdmin.viewDex.vgHistory.setStudyDone(false);

        // Maintenance Property to set the userName.
        key = "history.userName";
        String userName = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (!userName.isEmpty()) {
            appMainAdmin.viewDex.vgHistory.setUserName(userName);
        }

        // Maintenance Property to set the studyDone.
        String key1 = "history.studyDone";
        String studyDone = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key1);

        if (studyDone.equalsIgnoreCase("yes")) {
            appMainAdmin.viewDex.vgHistory.setStudyDone(true);
        } else if (studyDone.equalsIgnoreCase("no")) {
            appMainAdmin.viewDex.vgHistory.setStudyDone(false);
        }

        // Maintenance Property to set the selStackNode.
        key = "history.selStackNode";
        String str = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        int stackNodeCnt = propUtils.getPropertySelStackNodeValue(str);
        if (stackNodeCnt != Integer.MIN_VALUE) {
            appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(stackNodeCnt);
        }

        studyDbUtility.setSelectedImageNodeCntToFirstImage();

        /**
         * ***********************************************************
         * Color lookup tables
         * **********************************************************
         */
        key = "color-lookup-tables";
        String strLUT = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        boolean lutDefined = true;

        if (strLUT.equalsIgnoreCase("")) {
            lutDefined = false;
        }

        appMainAdmin.viewDex.windowLevel.setLookupTableDefined(lutDefined);
        appMainAdmin.viewDex.windowLevel.setLookupTableDefinedStr(strLUT);
        //appMainAdmin.viewDex.windowLevel.initColorLUT();
        if (lutDefined) {
            appMainAdmin.viewDex.windowLevel.readColorLUT(strLUT);
        }

        /**
         * *************************************************************
         * WindowingMode, NM & MR "whitepoint" values
         * ************************************************************
         */
        key = "fixed-minimum.value";
        int wm = 0;

        String s1 = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (s1.equals("")) {
            wm = WindowingMode.CLASSIC;
        } else {
            wm = WindowingMode.FIXED_MINIMUM;
        }

        int fixedMin = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);

        // This value is set in method CanvasControl.setCanvasOverlayWindowingStatus().
        appMainAdmin.viewDex.canvasControl.setWindowingMode(wm);
        appMainAdmin.viewDex.canvasControl.setWindowingFixedMinimumValue(fixedMin);

        appMainAdmin.viewDex.windowLevel.setWindowingMode(wm);
        appMainAdmin.viewDex.windowLevel.setWindowingFixedMinimumValue(fixedMin);

        //Default value for screen resolution 1600x1200 (30x980)
        //Default value for screen resolution 2048x1536 (30x1280)
        // position "fixed-minimum value"
        int[] defValue7 = {30, 980};

        if (d.width == 1600 && d.height == 1200) {
            defValue7[0] = 30;
            defValue7[1] = 980;
        } else if (d.width == 2048 && d.height == 1536) {
            defValue7[0] = 30;
            defValue7[1] = 1280;
        }

        key = "canvas.overlay.fixed-minimum.pos";
        value = propUtils.getPropertyPositionValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (value[0] == 0 && value[1] == 0) {
            value[0] = defValue7[0];
            value[1] = defValue7[1];
        }

        appMainAdmin.viewDex.canvas.setCanvasOverlayFixedMinimumPos(value[0], value[1]);
    }

    /**
     * Set the given answers and marks. BIRADS
     */
    public void runEditVgStudyUpdateAnswerAndMarks() {
        //appMainAdmin.viewDex.vgTaskMainPanel.updateRatingValues();
        //appMainAdmin.viewDex.vgTaskMainPanel.updateBIRADSRatingValues();
        //appMainAdmin.viewDex.localization.setLocalizationSetSymbolAndRender();
    }

    /**
     * ************************************************************
     *
     * Create VgStudy and do some init.
     *
     * ***********************************************************
     */
    /**
     * Read and sort the imageDb.
     */
    public void readImageDb(String imageDbPath) {
        String stackAndImagesViewingOrder = null;
        String stackImagesSortOrder = null;

        String ostype = appMainAdmin.viewDex.getOsType();
        int runMode = getRunModeStatus();

        //String imageDbMainPath = appMainAdmin.viewDex.vgHistoryCreate.getImageDbMainPath();
        //String imageDbDemoPath = appMainAdmin.viewDex.vgHistoryCreate.getImageDbDemoPath();

        /*if (runMode == VgRunMode.DEMO_EXIST) {
            if (!appMainAdmin.viewDex.vgHistoryUtil.fileExist(imageDbDemoPath)) {
                appMainAdmin.viewDex.vgHistoryDemo.demoStudyFilePathErrorMessage();
            }
        }*/
        //imageDbExist();
        // Used for the root node list
        String defStackAndImagesViewingOrder = "random";
        String key = "stack-images.viewing-order";
        if (appMainAdmin.viewDex.appProperty.getStudyProperties().containsKey(key)) {
            stackAndImagesViewingOrder = appMainAdmin.viewDex.appProperty.getStudyProperties().getProperty(key);
        }
        if (stackAndImagesViewingOrder.equalsIgnoreCase("random")) {
            stackAndImagesViewingOrder = "random";
        }
        if (stackAndImagesViewingOrder.equalsIgnoreCase("sequence")) {
            stackAndImagesViewingOrder = "sequence";
        }

        if (!(stackAndImagesViewingOrder.equals("random")
                || stackAndImagesViewingOrder.equals("sequence"))) {
            stackAndImagesViewingOrder = defStackAndImagesViewingOrder;
        }

        // Stack image custom sort order
        String defStackImagesSortOrder = "natural";
        key = "stack-images.sort";
        if (appMainAdmin.viewDex.appProperty.getStudyProperties().containsKey(key)) {
            stackImagesSortOrder = appMainAdmin.viewDex.appProperty.getStudyProperties().getProperty(key);
        }
        if (stackImagesSortOrder != null && stackImagesSortOrder.equalsIgnoreCase("customer")) {
            stackImagesSortOrder = "customer";
        } else {
            stackImagesSortOrder = defStackImagesSortOrder;
        }

        // Used for the root node list.
        //key = "image.viewing-order";
        //if (vgProp.containsKey(key))
        //  imageViewingOrder = vgProp.getProperty(key);
        // Set path to imageDb
        /*
        String imageDbPath = null;
        if (this.runMode == VgRunMode.CREATE_EXIST
                || this.runMode == VgRunMode.EDIT_EXIST
                || this.runMode == VgRunMode.SHOW_EXIST) {
            imageDbPath = imageDbMainPath;
        } else if (this.runMode == VgRunMode.DEMO_EXIST) {
            imageDbPath = imageDbDemoPath;
        }*/
        /**
         * ***************************************************************
         *
         * StudyDb, sort & merge of rootNodeList and stackNodeList.
         *
         ***************************************************************
         */
        // studyDb
        StudyDb studyDb = new StudyDb(appMainAdmin, appMainAdmin.viewDex.appProperty.getStudyName());
        //studyDbUtility = new StudyDbUtility(appMainAdmin);

        // time
        //long msecs;
        //msecs = System.currentTimeMillis();
        // zeroNodeList, stackNodeList
        studyDb.createStudyNodeList(imageDbPath);
        ArrayList<StudyDbStackNode> znListP = studyDb.getZeroNodeList();
        ArrayList<StudyDbStackNode> snListP = studyDb.getStackNodeList();
        //studyDb.printStudyDbStackNodeDirectory(snListP, "stackNodeListDirectory");
        //studyDb.printStudyDbStackNode(znListP, "zeroNodeList");
        //studyDb.printStudyDbStackNode(snListP, "stackNodeList");
        //System.exit(1);

        // copy
        ArrayList<StudyDbStackNode> znList = studyDb.getZeroNodeList();
        ArrayList<StudyDbStackNode> snList = studyDb.getStackNodeList();
        ArrayList<StudyDbStackNode> znList2 = studyDb.copyStackNodeList(znList);
        ArrayList<StudyDbStackNode> snList2 = studyDb.copyStackNodeList(snList);
        //studyDb.printStudyDbStackNode(znList2, "zeroNodeList2");
        //studyDb.printStudyDbStackNode(snList2, "zeroNodeList2");
        //System.exit(1);

        // set orig list
        // I don't like this..
        /*
        if (this.runMode == VgRunMode.CREATE_EXIST
                || this.runMode == VgRunMode.EDIT_EXIST
                || this.runMode == VgRunMode.SHOW_EXIST) {
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbZeroNodeListOrig(znList2);
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbStackNodeListOrig(snList2);
        } else if (this.runMode == VgRunMode.DEMO_EXIST) {
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbZeroNodeListOrig(znList2);
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbStackNodeListOrig(snList2);
        }
         */
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeListOrig(znList2);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbStackNodeListOrig(snList2);

        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbZeroNodeListOrig(znList2);
        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbStackNodeListOrig(snList2);
        //ArrayList<StudyDbStackNode> znListOrig = history.getStudyDbZeroNodeListOrig();
        //ArrayList<StudyDbStackNode> snListOrig = history.getStudyDbStackNodeListOrig();
        //studyDb.printStudyDbStackNode(znListOrig, "zeroNodeListOrig");
        //studyDb.printStudyDbStackNode(snListOrig, "stackNodeListOrig");
        //System.exit(1);
        // Sort stackNode, {sequens, random}.
        //studyDb.sortStackNode(stackViewingOrder);
        //ArrayList<StudyDbStackNode> snListSort = studyDb.getStackNodeList();
        //studyDb.printStudyDbStackNode(snListSort, "stackNodeList sorted {sequence, random}");
        //System.exit(1);
        // Sort imageNode for each stack.
        //long msecs = System.currentTimeMillis();
        //msecs = System.currentTimeMillis();
        // Sort on "(0020,0013)InstanceNumber"
        if (stackImagesSortOrder.equalsIgnoreCase("natural")) {
            studyDb.sortStackNodeImageNodeList();
        } else if (stackImagesSortOrder.equalsIgnoreCase("customer")) {
            studyDb.sortStackNodeImageNodeListCustom();
        }

        //System.out.println("Time to sortStack 1: " + (System.currentTimeMillis()- msecs));
        //ArrayList<StudyDbStackNode> snListSort2 = studyDb.getStackNodeList();
        //studyDb.printStudyDbStackNode(snListSort2, "stackNodeList sorted by Instans Number (default)}");
        //System.exit(1);
        // Sort zeroNode, the images in the zeroNodeList (images contained in the imagedb directory)
        //studyDb.sortZeroNodeList(imageViewingOrder);
        //ArrayList<StudyDbStackNode> znList3 = studyDb.getZeroNodeList();
        //studyDb.printStudyDbStackNode(znList3, "images in the zeroNodeList sorted {sequence, random}");
        //System.exit(1);
        // zeroNodeAsStackList
        studyDb.createZeroNodeAsStackList();
        //ArrayList<StudyDbStackNode> znList4 = studyDb.getZeroNodeAsStackList();
        //studyDb.printStudyDbStackNode(znList4, "images in the zeroNodeAsStackList as a stackList");
        //System.exit(1);

        // Copy and set the rootNodeListMaster
        //ArrayList<StudyDbStackNode> rList = studyDb.getRootNodeList();
        //ArrayList<StudyDbStackNode> rList2 = studyDb.copyStackNodeList(rList);
        //studyDb.setRootNodeListMaster(rList2);
        //ArrayList<StudyDbStackNode> rListMasterP = studyDb.getRootNodeListMaster();
        //studyDb.printStudyDbStackNode(rListMasterP, 105);
        //System.exit(1);
        // Sort the images in the stackNodeList. Sortorder according to study by Tony Svahn.
        /*
        if(appMainAdmin.viewDex.getMultiScreenStatus()){
        int instance = 0;
        ArrayList<StudyDbStackNode> snList3 = studyDb.getStackNodeList();
        studyDb.printStudyDbStackNode(snList3, "stackNodeList");
        ArrayList<StudyDbStackNode> snList4 =
        studyDb.sortImageNodeMLOCC(instance, snList3);
        studyDb.setStackNodeList(snList4);
        }
        ArrayList<StudyDbStackNode> snList5 = studyDb.getStackNodeList();
        studyDb.printStudyDbStackNode(snList5, "stackNodeList sorted MLO,CC");
         */
        // copy & set history
        ArrayList<StudyDbStackNode> znList20 = studyDb.getZeroNodeList();
        ArrayList<StudyDbStackNode> znList21 = studyDb.getZeroNodeAsStackList();
        ArrayList<StudyDbStackNode> snList20 = studyDb.getStackNodeList();

        //studyDb.printStudyDbStackNode(znList20, "zeroNodeList");
        //studyDb.printStudyDbStackNode(znList21, "zeroNodeListAsStackList");
        //studyDb.printStudyDbStackNode(snList20, "stackNodeList");
        //System.exit(1);
        ArrayList<StudyDbStackNode> znList30 = studyDb.copyStackNodeList(znList20);
        ArrayList<StudyDbStackNode> znList31 = studyDb.copyStackNodeList(znList21);
        ArrayList<StudyDbStackNode> snList30 = studyDb.copyStackNodeList(snList20);

        /*
        if (this.runMode == VgRunMode.CREATE_EXIST
                || this.runMode == VgRunMode.EDIT_EXIST
                || this.runMode == VgRunMode.SHOW_EXIST) {
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbZeroNodeList(znList30);
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbZeroNodeAsStackList(znList31);
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbStackNodeList(snList30);
        } else if (this.runMode == VgRunMode.DEMO_EXIST) {
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbZeroNodeList(znList30);
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbZeroNodeAsStackList(znList31);
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbStackNodeList(snList30);
        }
         */
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeList(znList30);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeAsStackList(znList31);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbStackNodeList(snList30);

        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbZeroNodeList(znList30);
        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbZeroNodeAsStackList(znList31);
        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbStackNodeList(snList30);
        // rootNodeList
        studyDb.createRootNodeList(znList31, snList30);

        // Sort rootNodeList (stacks and images), {sequens, random}.
        studyDb.sortRootNodeList(stackAndImagesViewingOrder);
        //ArrayList<StudyDbStackNode> rnList20 = studyDb.getRootNodeList();
        //studyDb.printStudyDbStackNode(rnList20, "rootNodeList sorted {sequence, random}");
        //System.exit(1);

        // history
        ArrayList<StudyDbStackNode> rnList = studyDb.getRootNodeList();

        /*
        if (this.runMode == VgRunMode.CREATE_EXIST
                || this.runMode == VgRunMode.EDIT_EXIST
                || this.runMode == VgRunMode.SHOW_EXIST) {
            appMainAdmin.viewDex.vgHistoryMain.history.setStudyDbRootNodeList(rnList);
            ArrayList<StudyDbStackNode> rnList2 = appMainAdmin.viewDex.vgHistoryMain.history.getStudyDbRootNodeList();
        } else if (this.runMode == VgRunMode.DEMO_EXIST) {
            appMainAdmin.viewDex.vgHistoryDemo.history.setStudyDbRootNodeList(rnList);
            ArrayList<StudyDbStackNode> rnList2 = appMainAdmin.viewDex.vgHistoryDemo.history.getStudyDbRootNodeList();
        }*/
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbRootNodeList(rnList);
        
        // Lists removed from the History object. Only used for creation of studyDbRootNodeList.
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeList(null);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbStackNodeList(null);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbStackNodeListOrig(null);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeListOrig(null);
        appMainAdmin.viewDex.vgHistoryMainUtil.getHistory().setStudyDbZeroNodeAsStackList(null);
        
        //ArrayList<StudyDbStackNode> rnList2 = appMainAdmin.viewDex.vgHistoryMain.history.getStudyDbRootNodeList();

        //appMainAdmin.viewDex.vgHistoryOriginal.history.setStudyDbRootNodeList(rnList);
        //ArrayList<StudyDbStackNode> rnList2 = appMainAdmin.viewDex.vgHistoryOriginal.history.getStudyDbRootNodeList();
        //studyDb.printStudyDbStackNode(rnList2, "rootNodeList final");
        //System.exit(1);
        
        // free memory
        //studyDb = null;
        //Runtime r = Runtime.getRuntime();
        //r.gc();

        //System.out.println("Time to read studyDb 1: " + (System.currentTimeMillis()-msecs));
        // Cursor
        //appMainAdmin.viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Invoked when task's progress property changes. IN USE?
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /*if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressMonitorDialog.progressBar.setIndeterminate(false);
            progressMonitorDialog.progressBar.setValue(progress);
        }*/
    }

    /**
     * Create the GUI for the Vg study. Create interpolation. Create
     * mapconstant. Create localization symbol size,width... values Create
     * shapeMaker control
     */
    public void createVgStudy() {
        appMainAdmin.viewDex.createVgStudy(appMainAdmin.viewDex.vgHistory);

        // canvascontrol init
        appMainAdmin.viewDex.canvasControl.setInit();

        // zoom increment default value
        double zoomIncr = createVgZoomIncrementDefault();
        appMainAdmin.viewDex.canvasControl.setZoomIncrementDefault(zoomIncr);

        // interpolation
        String[] ipDefinition = createVgInterpolation();
        String ipDefault = createVgInterpolationDefault();
        appMainAdmin.viewDex.canvasControl.setInterpolation(ipDefault, ipDefinition);

        // map constant
        double mapConst = createVgWindowLevelMapconstant();
        appMainAdmin.viewDex.canvasControl.setWindowLevelMapConstant(mapConst);

        createCineLoopStatus();
        createStackLoadInBackgroundStatus();
        createCineLoopStartAutoStatus();
        createLocalizationSymbolOutlineValue();

        // Canvas text display properties.
        appMainAdmin.viewDex.canvasControl.createCanvasTextProperties();

        // Canvas color
        appMainAdmin.viewDex.canvasControl.createCanvasColorProperties();

        // Canvas distance text properties
        appMainAdmin.viewDex.canvasControl.createCanvasROIDistanceProperties();
        appMainAdmin.viewDex.canvasControl.createCanvasROIVolumeProperties();
        appMainAdmin.viewDex.canvasControl.createCanvasROIPixelValueMeanProperties();
    }

    /**
     * Init the <code>VgLog</code> object.
     */
    public void createVgLog() {
        if (studyLog != null) {
            studyLog = null;
        }
        studyLog = new VgLog(this, appMainAdmin.viewDex.vgHistory);
    }

    /**
     * createVgZoomIncrement
     *
     * @return
     */
    private double createVgZoomIncrementDefault() {
        double val = 0.0;

        double defIncr = 0.1;
        String key = "functionpanel.zoom.magnification.increment";
        val = propUtils.getPropertyDoubleValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (val == 0.0) {
            val = defIncr;
        }
        return val;
    }

    /**
     * createVgInterpolation
     *
     * @return
     */
    private String[] createVgInterpolation() {
        ArrayList<VgCanvasInterpolationControl> list;

        list = appMainAdmin.viewDex.vgHistory.getCanvasInterpolationList();
        String[] str = new String[list.size()];
        for (int i = 0; i
                < list.size(); i++) {
            str[i] = list.get(i).getName();
        }
        return str;
    }

    private String createVgInterpolationDefault() {
        ArrayList<VgCanvasInterpolationControl> list;

        list = appMainAdmin.viewDex.vgHistory.getCanvasInterpolationList();
        if (list.size() != 0) {
            String str = list.get(0).getDefaultName();
            return str;
        } else {
            return "nearest neighbor";
        }
    }

    private double createVgWindowLevelMapconstant() {
        String mapConst = null;
        double value = 0.0;

        String defMapConst = "3.0";
        String key = "canvas.wl.mapconstant";
        if (appMainAdmin.viewDex.appProperty.getStudyProperties().containsKey(key)) {
            mapConst = appMainAdmin.viewDex.appProperty.getStudyProperties().getProperty(key).trim();
        }

        if (mapConst.equals("")) {
            mapConst = defMapConst;
        }

        if (mapConst != null) {
            try {
                value = Double.parseDouble(mapConst);
            } catch (NumberFormatException e) {
                System.out.println("VgControl:createVgWindowLevelMapconstant: NumberFormatException");
            }
        }
        return value;
    }

    /*
     * StackLoadInBackgroundStatus
     */
    private void createStackLoadInBackgroundStatus() {

        // Obsolite
        // Removed 2010-03-30
        /*
        StudyDbStackNode stackNode = getSelectedStackNode();
        int nt = stackNode.getNodeType();

        boolean sta = false;
        String key = "stack.load.background";
        String str = propUtils.getPropertyStringValue(prop, key);
        if (str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y")) {
        sta = true;
        }

        // a quick fix
        if(nt == 1)
        setStackLoadInBackgroundStatus(sta);
        else
        setStackLoadInBackgroundStatus(false);
         */
        setStackLoadInBackgroundStatus(true);
    }

    /*
     */
    public void setStackLoadInBackgroundStatus(boolean sta) {
        loadStackInBackgroundStatus = sta;
    }

    /*
     */
    public boolean getStackLoadInBackgroundStatus() {
        return loadStackInBackgroundStatus;
    }

    /**
     * Read study properties
     */
    private void createCineLoopStatus() {
        cineLoopStatus = false;
        String key = "cineloop";
        String cineLoop = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (cineLoop.equalsIgnoreCase("Yes") || cineLoop.equalsIgnoreCase("Y")) {
            cineLoopStatus = true;
        }
    }

    /*
     * Read study properties
     */
    private void createCineLoopStartAutoStatus() {
        boolean sta = false;
        String key = "cineloop.start.auto";
        String str = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y")) {
            sta = true;
        }
        setCineLoopStartAutoStatus(sta);
    }

    /*
     * cineLoopStatus
     */
    public void setCineLoopStatus(boolean sta) {
        cineLoopStatus = sta;
    }

    /*
     * cineLoopStatus
     */
    public boolean getCineLoopStatus() {
        return cineLoopStatus;
    }

    /*
     * getLoadStackInBackgroundStatus
     */
    public boolean getLoadStackInBackgroundStatus() {
        return loadStackInBackgroundStatus;
    }

    /*
     */
    private static void setCineLoopStartAutoStatus(boolean sta) {
        cineLoopStartAutoStatus = sta;
    }

    /*
     */
    public boolean getCineLoopStartAutoStatus() {
        return cineLoopStartAutoStatus;
    }

    /**
     * Create localization symbol size and width Read study properties
     */
    public void createLocalizationSymbolOutlineValue() {
        int defSizeValue = 30;
        String key1 = "localization.symbol.size";
        int val = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key1);
        if (val == 0) {
            val = defSizeValue;
        }

        int defWidthValue = 5;
        String key2 = "localization.symbol.line.width";
        int val2 = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key2);
        if (val2 == 0) {
            val2 = defWidthValue;
        }

        // line color
        String key3 = "localization.symbol.line.color";
        int[] color = propUtils.getPropertyColorValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key3);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasLocalizationLineColor[0];
            color[1] = AppPropertyUtils.defCanvasLocalizationLineColor[1];
            color[2] = AppPropertyUtils.defCanvasLocalizationLineColor[2];
        }
        Color lineColor = new Color(color[0], color[1], color[2]);

        // position text color
        String key4 = "localization.position.text.color";
        int[] color2 = propUtils.getPropertyColorValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key4);
        if (color2[0] == 0 && color2[1] == 0 && color2[2] == 0) {
            color2[0] = AppPropertyUtils.defCanvasLocalizationLineColor[0];
            color2[1] = AppPropertyUtils.defCanvasLocalizationLineColor[1];
            color2[2] = AppPropertyUtils.defCanvasLocalizationLineColor[2];
        }
        Color positionTextColor = new Color(color2[0], color2[1], color2[2]);

        appMainAdmin.viewDex.localization.setLocalizationSymbolSizeWidthValue(val, val2, lineColor, positionTextColor);


        /* ShapeMaker */
        // Move to function of its own..
        int defShapeMakerLineWidthValue = 5;
        String key5 = "shapemaker.measurement.drawing.line.width";
        int val5 = propUtils.getPropertyIntegerValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key5);
        if (val5 == 0) {
            val5 = defShapeMakerLineWidthValue;
        }

        // fix 20160215
        //appMainAdmin.viewDex.shapeMaker.setDrawingLineWidthValue(val3);
    }

    public VgHistory getVgHistory() {
        return appMainAdmin.viewDex.vgHistory;
    }

    /**
     * *****************************************************************
     *
     * Run
     *
     ******************************************************************
     */
    /**
     * Check if the study is done.
     *
     * @return
     */
    public boolean getStudyDone() {
        boolean status = false;

        if (appMainAdmin.viewDex.vgHistory.getStudyDone()) {
            setStudyDone();
            status = true;
        }
        return status;
    }

    /**
     * Start the study as a stack.
     */
    public boolean startStudyAsStack() {
        StudyDbStackNode stackNode = null;
        StudyDbImageNode imageNode = null;
        int nodeType;
        boolean status = false;

        // notesStatus
        boolean notesStatus = false;
        String key = "notespanel";
        String notes = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (notes.equalsIgnoreCase("Yes") || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // stackNode exist
        stackNode = studyDbUtility.getSelectedStackNode();
        if (stackNode == null) {
            setStudyDone();  //dialog
        } else {
            stackNode = studyDbUtility.getSelectedStackNode();
            nodeType = stackNode.getNodeType();
            if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
                // In the selected stackNode, set as the selected image
                // the first one in the imagelist.
                studyDbUtility.setSelectedImageNodeCntToFirstImage();
                imageNode = studyDbUtility.getSelectedImageNode();

                if (imageNode == null) {
                    setStudyDone();   //dialog
                } else {
                    startStudy(imageNode);
                    stackNode.setStackEvaluationTimeStart();
                    appMainAdmin.viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();

                    // update notes value
                    if (notesStatus && appMainAdmin.viewDex.vgNotesPanel != null) {
                        appMainAdmin.viewDex.vgNotesPanel.setNotesPanel();
                    }
                    status = true;
                }
            }
        }
        return status;
    }

    /**
     * Start the study.
     */
    public void startStudy(StudyDbImageNode imageNode) {

        // Start message in the log
        if (runMode == VgRunMode.CREATE_EXIST
                || runMode == VgRunMode.EDIT_EXIST
                || runMode == VgRunMode.DEMO_EXIST) {
            studyLog.start();
        }

        if (getStackLoadInBackgroundStatus()) {
            loadStackInBackground();
            setSelImageCount();
            setTotalImageCount();
            setSelStackCount();
            setTotalStackCount();
        } else {
            runStudy(imageNode);
            setSelImageCount();
            setTotalImageCount();
            setSelStackCount();
            setTotalStackCount();
        }
    }

    /**
     * Set the <code>StudyDone</code> dialog.
     */
    private void setStudyDone() {
        // Stack count
        appMainAdmin.viewDex.vgRunPanel.setTotalStackCount("");
        appMainAdmin.viewDex.vgRunPanel.setSelStackCount("");

        // Image count
        appMainAdmin.viewDex.vgRunPanel.setTotalImageCount("");
        appMainAdmin.viewDex.vgRunPanel.setSelImageCount("");

        VgStudyDone studyDone = new VgStudyDone(appMainAdmin.viewDex);
        appMainAdmin.runLogin();
    }

    /**
     * Set the image (if the image does not already exist in the imageNode, read
     * from the disk). Render the image. Set image count.
     */
    public void runStudy(StudyDbImageNode imageNode) {
        setImage(imageNode);
        setCanvasOverlayStackInfo();

        // render the image
        int[] windowWidthImage = imageNode.getWindowWidth();
        int[] windowCenterImage = imageNode.getWindowCenter();

        // Reset status 20150910
        appMainAdmin.viewDex.canvasControl.setWLActivatedInStack(false);

        // wl status
        // NOT IN USE
        //boolean stackImagesWLStatus = false;
        //String key = "stack-images.wl";
        //String wl = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        //if (wl.equalsIgnoreCase("Yes") || wl.equalsIgnoreCase("Y")) {
        //  stackImagesWLStatus = true;
        //}
        /*
        if(!stackImagesWLStatus)
        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidth[], windowCenter[]);
        else
        appMainAdmin.viewDex.windowLevel.
        setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
         */
        //render
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
        appMainAdmin.viewDex.localization.setLocalizationOverlayListInCanvas();
        if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
            appMainAdmin.viewDex.vgLocalizationPanel.setHideText();
        }

        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthImage[0], windowCenterImage[0]);

        // EyeTracking
        //if(!eyeTrackingStatus)
        //  appMainAdmin.viewDex.canvasControl.setCanvasToBlack();
        if (appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus()
                && !appMainAdmin.viewDex.eyeTracking.getEyeTrackingRenderDuringLoopStatus()) {
            appMainAdmin.viewDex.canvasControl.setCanvasToBlack();
            appMainAdmin.viewDex.canvasControl.setCanvasETColor();
        }

        // Eyetracking
        // position & scale
        if (appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus() && newStackStatus) {
            AffineTransform at = appMainAdmin.viewDex.canvas.getTransform();
            double x = at.getTranslateX();
            double y = at.getTranslateY();
            double scalex = at.getScaleX();
            double scaley = at.getScaleY();

            /*
            ArrayList<VgEyeTrackingLog> list = new ArrayList<VgEyeTrackingLog>();
            list = history.getEyeTrackingList();
            VgEyeTrackingLog item = list.get(0);
            
            String id = item.getId();
            String age = item.getAge();
            String sex = item.getSex();
            String dominantEye = item.getDominantEye();
            
            try {
            Thread.sleep(200);
            } catch (InterruptedException ignore){}
            
            // id 
            String mg2 = "ET_REC";
            udpClient.setMessage(mg2);
            udpClient.send();
            
            try {
            Thread.sleep(200);
            } catch (InterruptedException ignore){}
             */

 /*
            String msg2 = "ET_REM" + " " + id + " " + age + " " + sex + " " + dominantEye;
            sendUDPMessage(msg2);
             */
 /*
            String mg3 = "ET_REM" + " " + id;
            udpClient.setMessage(mg3);
            udpClient.send();

            try {
            Thread.sleep(200);
            } catch (InterruptedException ignore){}
            
            String mg4 = "ET_REM" + " " + age;
            udpClient.setMessage(mg4);
            udpClient.send();

            try {
            Thread.sleep(200);
            } catch (InterruptedException ignore){}

            String mg5 = "ET_REM" + " " + sex;
            udpClient.setMessage(mg5);
            udpClient.send();

            try {
            Thread.sleep(200);
            } catch (InterruptedException ignore){}

            String mg6 = "ET_REM" + " " + dominantEye;
            udpClient.setMessage(mg6);
            udpClient.send();
            
            String mg7 = "ET_STP";
            udpClient.setMessage(mg7);
            udpClient.send();
             */
            // position & scale
            String msg3 = "ET_REC";
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg3);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            /*
            String msg4 = "ET_REM " + x + " " + y + " " + scalex + " " + scaley;
            udpClient.setMessage(msg4);
            udpClient.send();
             */
            String msg5 = "ET_REM" + " " + x;
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg5);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg6 = "ET_REM" + " " + y;
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg6);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg7 = "ET_REM" + " " + scalex;
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg7);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg8 = "ET_REM" + " " + scaley;
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg8);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg9 = "ET_STP";
            appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg9);
            appMainAdmin.viewDex.eyeTracking.udpClient.send();

            newStackStatus = false;
        }
    }

    /**
     * ****************************************************************
     *
     * CineLoop
     *
     ****************************************************************
     */
    /*
     * Run the study as a cine-loop.
     */
    public void runStudyAsCineLoop() {
        //System.out.println("VgControl.runStudyAsCineLoop");

        boolean running = false;

        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        if (stackNode != null) {
            //stackNode.setSelImageNodeCount(0);
            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            selImageListSize = imageList.size();

            if (timerCineLoop == null) {
                timerCineLoop = new Timer(cineLoopTimerValue, this);
            }

            if (timerCineLoop != null) {
                running = timerCineLoop.isRunning();
            }

            if (!running) {
                timerCineLoop.restart();
            }

        }
        cineLoopRunningStatus = true;
        appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(true);
    }

    /*
     * Stop the cine-loop.
     */
    public void stopStudyAsCineLoop() {
        if (timerCineLoop != null) {
            timerCineLoop.stop();
            timerCineLoop = null;
        }
        cineLoopRunningStatus = false;
    }

    /**
     * Callback from the Swing Timer. Calculate the fraction elapsed of our
     * desired animation duration and interpolate between our start and end
     * colors accordingly.
     */
    public void actionPerformed(ActionEvent e) {
        //System.out.println("VgControl:actionPerfomed");
        //setImageNextPrevInStack(0);
        //    StudyDbImageNode imageNode = imageList.get(i);
        //runStudy(imageNode);
        //}

        //System.out.println("Time: " + (System.currentTimeMillis()));
        //System.out.println("Time: " + (System.nanoTime()));
        /**
         * ****************************************
         * Time measurement * Time between display of each image *
         * ****************************************
         */
        /*
        long nowTime = System.nanoTime();
        long elapsedTime = (nowTime - prevTime);
        long t = elapsedTime / 1000000;
        System.out.println("Elapsed time = " + elapsedTime / 1000000);
        prevTime = nowTime;
         */
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int selImageNodeCount = stackNode.getSelImageNodeCount();

        if (selImageNodeCount == selImageListSize - 1) {
            cineLoopDirection = 1;
        }

        if (selImageNodeCount == 0) {
            cineLoopDirection = 0;
        }

        // set the image
        setImageNextPrevInStack(cineLoopDirection);

        if (runMode == VgRunMode.EDIT_EXIST) {
            if (appMainAdmin.viewDex.localization.getLocalizationMarkExistStatusForSelectedImage()) {
                stopStudyAsCineLoop();
            }
        }
    }

    /**
     * Set the cineLoop timing value.
     */
    public void setCineLoopTimeValue(int val) {
        cineLoopTimerValue = val;
        //stopStudyAsCineLoop();
        //runStudyAsCineLoop();
    }

    /**
     * Get the cineLoopTimerValue.
     */
    public int getCineLoopTimerValue() {
        return cineLoopTimerValue;
    }

    /**
     * Set the showOrgImage status. True means the original image is displayed.
     * False means the mark image is displayed.
     */
    public void setshowOrgImageStatus(boolean sta) {
        showOrgImage = sta;
    }

    /**
     * Get the running status of the cineLoop.
     *
     * @return <code>true<code/> if the loop is running.
     */
    public boolean getCineLoopRunningStatus() {
        return cineLoopRunningStatus;
    }

    /**
     * Set the cineloop direction.
     */
    public void setCineLoopDirection(int direction) {
        cineLoopDirection = direction;
    }

    /**
     * ***************************************************************
     *
     * *************************************************************
     */
    /**
     * Read the image data and DICOM tags from the imagedb. Set canvas module.
     * Set functionPanel module. Set windowLevel module. Set image initial
     * presentation. Set image info on application main frame.
     *
     * @param <code>StudyDbImageNode</code> the node contanining the image and
     * the information about the image.
     */
    private void setImage(StudyDbImageNode imageNode) {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        File filePath = imageNode.getStudyPath();
        //int cnt = imageNode.getItemCnt();
        PlanarImage orgImage = imageNode.getOrgImage();

        if (orgImage == null) {
            DicomFileReader studyLoader2 = new DicomFileReader();

            if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE
                    || stackNode.getStackType() == StudyDbStackType.STACK_TYPE_STACK_IMAGE) {
                studyLoader2.loadImage(filePath, stackNode.getStackType(), 0);
            } else if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_MULTI_FRAME_STACK_IMAGE) {
                studyLoader2.loadImage(filePath, stackNode.getStackType(), imageNode.getImageNo());
            }

            orgImage = studyLoader2.getLoadedPlanarImage();
            setImageNode(studyLoader2, stackNode, imageNode, orgImage);
            studyLoader2 = null;

            setCanvas(imageNode, orgImage);
            setFunctionPanel(imageNode);
            setWindowLevelInitValue(imageNode);
            setImageInitialPresentation(imageNode);
            imageNode.setImageEvaluationTimeStart();

            // set main frame info
            if (appMainAdmin.viewDex.getImageInfoAppMainFrameStatus()) {
                setAppMainFrameImageInfo();
            }

        } else {
            setCanvas(imageNode, orgImage);
            setFunctionPanel(imageNode);
            setWindowLevelInitValue(imageNode);
            //zzzzzzzzzzzzzzzzzzzzzzzzzzzz
            //appMainAdmin.viewDex.canvasControl.setCanvasOverlayWindowingStatus(false);
            setImageInitialPresentation(imageNode);
            imageNode.setImageEvaluationTimeStart();

            // info
            if (appMainAdmin.viewDex.getImageInfoAppMainFrameStatus()) {
                setAppMainFrameImageInfo();
            }
        }
        // symbol size
        //appMainAdmin.viewDex.localization.setLocalizationSymbolSize(imageNode);
    }

    /**
     * Set the selected image and do the rendering.
     *
     * @param <code>StudyDbImageNode</code> the node contanining the image and
     * the information about the image. NOT IN USE
     */
    public void setSelectedImageAndRender(boolean render) {
        studyDbUtility.updateStackNode();
        deleteImageInSelectedNode();

        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
        setImage(imageNode);

        int windowWidthStack = stackNode.getWindowWidth();
        int windowCenterStack = stackNode.getWindowCenter();
        int[] windowWidthImage = imageNode.getWindowWidth();
        int[] windowCenterImage = imageNode.getWindowCenter();
        AffineTransform atx = stackNode.getAffineTransform();
        appMainAdmin.viewDex.canvas.setTransform(atx, false);

        //Implemented 20140107
        // wl status
        boolean stackImagesWLStatus = false;
        String key = "stack-images.wl";
        String wl = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (wl.equalsIgnoreCase("Yes") || wl.equalsIgnoreCase("Y")) {
            stackImagesWLStatus = true;
        }

        // 20150911
        boolean WLActivateInStack = appMainAdmin.viewDex.canvasControl.getWLActivateInStack();
        if (render && stackImagesWLStatus && !WLActivateInStack) {
            appMainAdmin.viewDex.windowLevel.setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
        } else {
            if (render && !stackImagesWLStatus) {
                appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthStack, windowCenterStack);
            }
        }

        /*
        if (render & !stackImagesWLStatus)
        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthStack, windowCenterStack);
        else
        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthImage[0], windowCenterImage[0]);
         */
    }

    /**
     * Set the image and render. Used by <ImageLoadingWorker>.
     */
    public void setImageAndRender(StudyDbImageNode imageNode) {
        studyDbUtility.updateStackNode();
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        setImage(imageNode);

        // render
        int[] windowWidthImage = imageNode.getWindowWidth();
        int[] windowCenterImage = imageNode.getWindowCenter();
        int windowWidthStack = stackNode.getWindowWidth();
        int windowCenterStack = stackNode.getWindowCenter();
        AffineTransform atx = stackNode.getAffineTransform();
        appMainAdmin.viewDex.canvas.setTransform(atx, false);

        // EyeTracking
        if (appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus()
                && !appMainAdmin.viewDex.eyeTracking.getEyeTrackingRenderDuringLoopStatus()) {
            appMainAdmin.viewDex.canvasControl.setCanvasToBlack();
            appMainAdmin.viewDex.canvasControl.setCanvasETColor();
        } else {
            appMainAdmin.viewDex.canvas.setCanvasDefaultColor();
        }

        //Implemented 20140107
        // wl status
        boolean stackImagesWLStatus = false;
        String key = "stack-images.wl";
        String wl = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (wl.equalsIgnoreCase("Yes") || wl.equalsIgnoreCase("Y")) {
            stackImagesWLStatus = true;
        }

        //render
        //appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidth, windowCenter);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
        appMainAdmin.viewDex.localization.setLocalizationOverlayListInCanvas();
        // 20150911
        boolean WLActivateInStack = appMainAdmin.viewDex.canvasControl.getWLActivateInStack();
        if (stackImagesWLStatus && !WLActivateInStack) {
            appMainAdmin.viewDex.windowLevel.setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
        } else {
            appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthStack, windowCenterStack);
        }

        /*
        if(!stackImagesWLStatus)
        appMainAdmin.viewDex.windowLevel.
        setWindowLevel(windowWidthStack, windowCenterStack);
        else
        appMainAdmin.viewDex.windowLevel.
        setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
         */
    }

    /**
     * Read the image data and DICOM tags from the imagedb. Set canvas module.
     * Set functionPanel module. Set windowLevel module. Set image initial
     * presentation. Set image info on application main frame.
     *
     * @param <code>StudyDbImageNode</code> the node contanining the image and
     * the information about the image.
     *
     * NOT IN USE
     */
    /*
    private void setImage_test(StudyDbImageNode imageNode) {
        int loadStatus = 0;

        //boolean showMark = appMainAdmin.viewDex.vgFunctionPanelLocalization.getLocShowMarkStatus();
        File filePath = imageNode.getStudyPath();
        PlanarImage orgImage = imageNode.getOrgImage();

        if (loadStatus == 0 && orgImage == null) {
            studyLoader = new StudyLoader_old();
            studyLoader.loadImage(filePath);
            orgImage = studyLoader.getLoadedPlanarImage();
            //setImageNode(studyLoader, imageNode, orgImage);
            studyLoader = null;

            //appMainAdmin.viewDex.localization.setMarkForImageNode(imageNode);
        }

        if (loadStatus == 0 && orgImage != null) {
            setCanvas(imageNode, orgImage);
            setFunctionPanel(imageNode);
            setWindowLevelInitValue(imageNode);
            setImageInitialPresentation(imageNode);
            imageNode.setImageEvaluationTimeStart();

            if (appMainAdmin.viewDex.getImageInfoAppMainFrameStatus()) {
                setAppMainFrameImageInfo();
            }

        }

        if (loadStatus == 1) {
            PlanarImage orgBackImage = imageNode.getOrgBackImage();
            setCanvas(imageNode, orgBackImage);
            setFunctionPanel(imageNode);
            setWindowLevelInitValue(imageNode);
            setImageInitialPresentation(imageNode);
            imageNode.setImageEvaluationTimeStart();

            if (appMainAdmin.viewDex.getImageInfoAppMainFrameStatus()) {
                setAppMainFrameImageInfo();
            }

        }
    }*/

    /**
     * Preload the images. Read the image data and DICOM tags from the imagedb.
     * NOT IN USE
     */
    /*
    private void stackPreLoad44() {
        //StudyLoader studyLoader = null;
        //StudyDbImageNode imageItem;
        boolean loadStatus = false;

        ArrayList<StudyDbImageNode> imageList = studyDbUtility.getSelectedImageList();

        appMainAdmin.viewDex.setBusyCursor();
        for (int i = 0; i < imageList.size(); i++) {
            StudyDbImageNode imageNode = imageList.get(i);
            File filePath = imageNode.getStudyPath();
            PlanarImage orgImage = imageNode.getOrgImage();

            if (orgImage == null) {
                loadStatus = true;
            } else {
                loadStatus = false;
            }

            if (orgImage == null) {
                studyLoader = new StudyLoader_old();
                studyLoader.loadImage(filePath);
                orgImage = studyLoader.getLoadedPlanarImage();
                //setImageNode(studyLoader, imageNode, orgImage);
                studyLoader = null;
            }

            if (loadStatus) {
                System.out.println("VgControl:preLoadImages: Load selected image");
            } else {
                System.out.println("VgControl:preLoadImages: NOT load selected image");
            }
// test
            String countStr = Integer.toString(i) + "(" + Integer.toString(imageList.size()) + ")";

            updateImageCount3(countStr);

            // Update the imageCount
            //appMainAdmin.viewDex.vgRunPanel.setCount(Integer.toString(i) +
            //      "(" + Integer.toString(imageList.size()) + ")");
        }

        appMainAdmin.viewDex.setDefaultCursor();
    }*/

    /**
     * Read the image data and DICOM tags from the imagedb as a background task.
     */
    public void loadStackInBackground() {
        if (appMainAdmin.viewDex.vgCineLoopPanel != null) {
            appMainAdmin.viewDex.vgCineLoopPanel.setButtonEnabled(false);
        }
        stopStudyAsCineLoop();
        stopLoadStackInBackground();

        if (imageLoadingWorker == null) {
            StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
            ArrayList<StudyDbImageNode> imageList = studyDbUtility.getSelectedImageList();
            //appMainAdmin.viewDex.setBusyCursor();
            imageLoadingWorker = new ImageLoadingWorker(stackNode, imageList,
                            appMainAdmin.viewDex.vgRunPanel.imageTotalCntLabel, appMainAdmin, cineLoopStatus, cineLoopStartAutoStatus, runMode);
            imageLoadingWorker.execute();
        }
    }

    /**
     * Stop the imaging loading thread. Be sure to stop in a safe and clean way.
     */
    public void stopLoadStackInBackground() {
        //System.out.println("VgControl.stopLoadStackInBackground");

        if (imageLoadingWorker != null) {
            imageLoadingWorker.cancel(true);
            imageLoadingWorker = null;
            imageLoadingWorkerStatus = false;
        }
    }

    /**
     * Shows when the imageLoader thread is running.
     *
     * @return
     */
    public boolean getImageLoadingWorkerStatus() {
        return imageLoadingWorkerStatus;
    }

    /**
     *
     * @return
     */
    public void setImageLoadingWorkerStatus(boolean status) {
        imageLoadingWorkerStatus = status;
    }

    /**
     * *****************************************************************
     *
     *****************************************************************
     */
    /**
     * @param str
     */

    /*
     * NOT IN USE
     */
    private void updateImageCount3(final String str) {
        new Thread(new Runnable() {

            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        appMainAdmin.viewDex.vgRunPanel.setTotalImageCount(str);
                    }
                });
            }
        }).start();
    }

    /**
     * Set the image and some image header data.
     */
    private void setImageNode(DicomFileReader studyLoader, StudyDbStackNode stackNode,
            StudyDbImageNode imageNode, PlanarImage orgImage) {
        int[][] imgStat = null;

        // get
        String patientId = studyLoader.attributeReader.att.getPatientID();
        String modality = studyLoader.attributeReader.att.getModality();
        Attributes dataset = studyLoader.attributeReader.getAttributes();
        //int[] windowWidth = studyLoader.attributeReader.att.getWindowWidth_int_array();
        //int[] windowCenter = studyLoader.attributeReader.att.getWindowCenter_int_array();
        int[] windowWidth = studyLoader.getWindowWidth();
        int[] windowCenter = studyLoader.getWindowCenter();
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
            imageNode.setDataset(dataset);
        }

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

        imageNode.setOrgImage(orgImage);
    }

    /**
     * Set DICOM header info. Create wl panel dynamic. The FunctionPanel ->
     * windowLevel buttons for multiple wc/ww values has to be recreated for
     * every new image.
     */
    private void setFunctionPanel(StudyDbImageNode imageNode) {
        int[] windowCenter = imageNode.getWindowCenter();
        int[] windowWidth = imageNode.getWindowWidth();

        appMainAdmin.viewDex.vgFunctionPanel.setWindowCenter(windowCenter);
        appMainAdmin.viewDex.vgFunctionPanel.setWindowWidth(windowWidth);
        appMainAdmin.viewDex.vgFunctionPanel.createWindowLevelLowerPanelDynamic();
    }

    /**
     * Set DICOM tags in the canvas object.
     */
    private void setCanvas(StudyDbImageNode imageNode, PlanarImage img) {
        // get
        double rescaleIntercept = imageNode.getRescaleIntercept();
        double rescaleSlope = imageNode.getRescaleSlope();
        String patientId = imageNode.getPatientID();
        String photometricInterpretation = imageNode.getPhotometricInterpretation();

        boolean modalityLUTSequenceStatus = imageNode.getModalityLUTSequenceStatus();
        boolean voiLUTSequenceStatus = imageNode.getVoiLUTSequenceStatus();
        boolean rescaleSlopeInterceptStatus = imageNode.getRescaleSlopeInterceptStatus();
        boolean centerWidthStatus = imageNode.getCenterWidthStatus();
        boolean identityStatus = imageNode.getIdentityStatus();
        boolean windowCenterOffsetStatus = imageNode.getWindowCenterOffsetStatus();
        int[][] imgStat = imageNode.getImgStat();
        //PlanarImage orgImage = imageNode.getOrgImage();

        // set
        appMainAdmin.viewDex.area.setImageStat(imgStat);
        appMainAdmin.viewDex.area.setRescaleIntercept(rescaleIntercept);

        appMainAdmin.viewDex.canvas.setCanvasOverlayPatientIdValue(patientId);
        appMainAdmin.viewDex.canvas.setRescaleIntercept(rescaleIntercept);
        appMainAdmin.viewDex.canvas.setRescaleSlope(rescaleSlope);
        appMainAdmin.viewDex.canvas.setModalityLUTSequenceStatus(modalityLUTSequenceStatus);
        appMainAdmin.viewDex.canvas.setVoiLUTSequenceStatus(voiLUTSequenceStatus);
        appMainAdmin.viewDex.canvas.setRescaleSlopeInterceptStatus(rescaleSlopeInterceptStatus);
        appMainAdmin.viewDex.canvas.setCenterWidthStatus(centerWidthStatus);
        appMainAdmin.viewDex.canvas.setIdentityStatus(identityStatus);
        appMainAdmin.viewDex.canvas.setWindowCenterOffsetStatus(windowCenterOffsetStatus);
        appMainAdmin.viewDex.canvas.setPhotometricInterpretation(photometricInterpretation);

        appMainAdmin.viewDex.canvas.setImageStat(imgStat);
        appMainAdmin.viewDex.canvas.setImage(img);
    }

    /**
     * Set init values for the WindowLevel object.
     */
    private void setWindowLevelInitValue(StudyDbImageNode imageNode) {
        int[] windowWidth = imageNode.getWindowWidth();
        int[] windowCenter = imageNode.getWindowCenter();
        int bitsStored = imageNode.getBitsStored();
        int bitsAllocated = imageNode.getBitsAllocated();
        double rescaleIntercept = imageNode.getRescaleIntercept();
        double rescaleSlope = imageNode.getRescaleSlope();
        int pixelRepresentation = imageNode.getPixelRepresentation();
        String photometricInterpretation = imageNode.getPhotometricInterpretation();
        //testzzzzColorModelParam cmParam = imageNode.getColorModelParam();
        boolean modalityLUTSequenceStatus = imageNode.getModalityLUTSequenceStatus();
        boolean voiLUTSequenceStatus = imageNode.getVoiLUTSequenceStatus();
        boolean rescaleSlopeInterceptStatus = imageNode.getRescaleSlopeInterceptStatus();
        boolean centerWidthStatus = imageNode.getCenterWidthStatus();
        boolean identityStatus = imageNode.getIdentityStatus();
        boolean windowCenterOffsetStatus = imageNode.getWindowCenterOffsetStatus();
        int[][] imgStat = imageNode.getImgStat();
        String modality = imageNode.getModality();

        // set
        appMainAdmin.viewDex.windowLevel.setWindowWidth(windowWidth);
        appMainAdmin.viewDex.windowLevel.setWindowCenter(windowCenter);
        appMainAdmin.viewDex.windowLevel.setBitsStored(bitsStored);
        appMainAdmin.viewDex.windowLevel.setBitsAllocated(bitsAllocated);
        appMainAdmin.viewDex.windowLevel.setRescaleIntercept(rescaleIntercept);
        appMainAdmin.viewDex.windowLevel.setRescaleSlope(rescaleSlope);
        appMainAdmin.viewDex.windowLevel.setPixelRepresentation(pixelRepresentation);
        appMainAdmin.viewDex.windowLevel.setPhotometricInterpretation(photometricInterpretation);
        //testzzzzzappMainAdmin.viewDex.windowLevel.setColorModelParam(cmParam);
        appMainAdmin.viewDex.windowLevel.setModalityLUTSequenceStatus(modalityLUTSequenceStatus);
        appMainAdmin.viewDex.windowLevel.setVoiLUTSequenceStatus(voiLUTSequenceStatus);
        appMainAdmin.viewDex.windowLevel.setRescaleSlopeInterceptStatus(rescaleSlopeInterceptStatus);
        appMainAdmin.viewDex.windowLevel.setCenterWidthStatus(centerWidthStatus);
        appMainAdmin.viewDex.windowLevel.setIdentityStatus(identityStatus);
        appMainAdmin.viewDex.windowLevel.setWindowCenterOffsetStatus(windowCenterOffsetStatus);
        appMainAdmin.viewDex.windowLevel.setImageStat(imgStat);
        appMainAdmin.viewDex.windowLevel.setMapConstant();
        appMainAdmin.viewDex.windowLevel.setModality(modality);
    }

    /**
     * Put some image info on the application main frame.
     */
    private void setAppMainFrameImageInfo() {
        String str;
        String str20 = null;
        String str21 = null;
        int center_d;

        StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
        if (imageNode == null) {
            return;
        }

        str = "ViewDEX  " + appMainAdmin.viewDex.appProperty.getLoginName() + "," + imageNode.getStudyName()
                + ", " + "s=" + appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount()
                + "," + "i=" + imageNode.getItemCnt() + "," + imageNode.getStudyPath().toString() + ",";

        int[][] imgStat = imageNode.getImgStat();
        String str2 = "m=" + imageNode.getModality() + "," + "ba=" + imageNode.getBitsAllocated()
                + "," + "bs=" + imageNode.getBitsStored() + "," + "range=" + imgStat[0][6] + ",";

        String str3 = "r=" + imageNode.getRows() + "," + "c=" + imageNode.getColumns() + ",";

        int[] ww = imageNode.getWindowWidth();
        if (ww != null) {
            for (int i = 0; i
                    < ww.length; i++) {
                if (i == 0) {
                    str20 = "" + "ww=" + ww[i] + ",";
                } else {
                    str20 = str20 + ww[i] + ",";
                }
            }
        }

        int[] windowCenter = imageNode.getWindowCenter();
        if (windowCenter != null) {
            for (int i = 0; i
                    < windowCenter.length; i++) {
                if (i == 0) {
                    if (imageNode.getWindowCenterOffsetStatus()) {
                        center_d = (int) (windowCenter[i] - imgStat[0][7]);
                    } else {
                        center_d = (int) windowCenter[i];
                    }
                    str21 = "" + "wc=" + center_d + ",";
                } else {
                    if (imageNode.getWindowCenterOffsetStatus()) {
                        center_d = (int) (windowCenter[i] - imgStat[0][7]);
                    } else {
                        center_d = (int) windowCenter[i];
                    }
                    str21 = str21 + center_d + ",";
                }
            }
        }
        String str5 = imageNode.getPhotometricInterpretation() + ",";
        //appMainAdmin.viewDex.setFont(new Font("SansSerif", Font.PLAIN, 8)); NOT WORKING
        appMainAdmin.viewDex.setAppTitle(str + " " + str2 + "" + str3 + "" + str20 + "" + str21 + "" + str5);
    }

    /*
     * Called by Panel Window/Level Reset button.
     */
    public int[] getSelImageWLDefault() {
        int[] width;
        int[] center;
        int[] val = new int[2];

        StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
        if (imageNode != null) {
            width = imageNode.getWindowWidth();
            center = imageNode.getWindowCenter();
            val[0] = width[0];
            val[1] = center[0];
        }
        return val;
    }

    /* There are properties that define how the image is presented
     * when it is first displayed. These are read and then stored
     * in the studyitem object.
     */
    private void setImageInitialPresentation(StudyDbImageNode imageNode) {
        double val = getDefaultDisplaySize();
        appMainAdmin.viewDex.canvasControl.setZoomModeDefault(val);
        //appMainAdmin.viewDex.canvasControl.setWindowingFixedMinOverlayStatus(imageNode);
        appMainAdmin.viewDex.canvasControl.setWindowingFixedMinimumPercentValue();
        appMainAdmin.viewDex.canvasControl.setToolMenuItemStatus(imageNode);


        /* If the WindowWidth, WindowCenter or PhotometricInterpretation
         * is defined in the image database, these values should be used.
         */
        //int windowWidthProp = getWindowWidthPropertyValue();
        //if(windowWidthProp != -1)
        //  studyItem.setWindowWidth(windowWidthProp);
        //int windowCenterProp = getWindowCenterPropertyValue();
        //if(windowCenterProp != -1)
        //  studyItem.setWindowCenter(windowCenterProp);
        //String photometricInterpretationProp = getPhotoMetricInterpretationProperty();
        //if(photometricInterpretationProp != null)
        //  photometricInterpretation = photometricInterpretationProp;
        //windowLevelMouseMotionMapping = getWindowLevelMouseMotionMappingProperty();
        /* Use this displacement property value when displaying the image. */
        //int ImageDisplacementPosition = (int)getImageDisplacementPositionProperty();
        //catMain.setImageDisplacementPosition(ImageDisplacementPosition);
        /* Read the ROISize from the image property file
        and set the property in the catStudyItem object.
         */
        //setFROCPatologyROISize();
        /* Read the ModalityOrigin from the property file.
         * and set the property in the catStudyItem object.
         */
        //setFROCModalityOrigin();
        /* Init of WindowLevel lutArraySize and IndexColorModel LUT size
         */
        //int bitsStored = studyItem.getBitsStored();
        //catMain.initLUT(bitsStored);
        //catMain.setBitsStored(bitsStored);
        //catMain.setPhotometricInterpretation(photometricInterpretation);
        //catMain.catWindowLevel.setWindowLevelMappingConstant(windowLevelMouseMotionMapping);
        //catMain.setImage(orgImage);
        /* Read the InitialDisplayMode value from the property file
         * and set the value in the canvas.
         */
        //String displayModeStr = getInitialDisplayModeProperty();
        //if(displayModeStr.equals("Original"))
        //  displayMode = DisplayMode.ORIG_SIZE;
        //if(displayModeStr.equals("Scaled"))
        //  displayMode = DisplayMode.SCALED;
        //if(displayModeStr.equals("HalfSize"))
        //  displayMode = DisplayMode.HALF_SIZE;
        //catMain.catFROC.setDisplayModeImageUpdate(displayMode);
        //catMain.canvas.setImagePosition(ImageDisplacementPosition);
    }

    /**
     * Check if any display size buttons are defined.
     *
     * @return true if buttons are defined.
     */
    public boolean displaySizeDefined() {
        ArrayList<VgFunctionPanelZoomModeControl> list = appMainAdmin.viewDex.vgHistory.getFunctionPanelZoomModeList();

        return list.isEmpty();
    }

    /**
     * Get the default displaysize defined in the property file.
     */
    public double getDefaultDisplaySize() {
        String s1 = null, s2 = null;
        double defValue = -0.0; // transformation FIT
        double orgValue = 1.0;  // transformation original size
        double numValue = 0.0;
        double val = defValue;

        // get the property value
        String key = "functionpanel.displaysize.default";
        if (appMainAdmin.viewDex.appProperty.getStudyProperties().containsKey(key)) {
            s1 = appMainAdmin.viewDex.appProperty.getStudyProperties().getProperty(key).trim();
            s2 = s1.replace('"', ' ').trim();
        }

        // numeric value
        try {
            numValue = Double.valueOf(s2);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            numValue = Double.MIN_VALUE;
        }

        // if "FIT" or "fit" is defined use transformation "-0.0".
        if (s1.equalsIgnoreCase("FIT") || s1.equalsIgnoreCase("fit")){
            val = defValue;
        } else if(numValue != Double.MIN_VALUE){
            val = numValue;
        } else{
            val = orgValue;
        }
            
        return val;
    }

    /**
     * Get the default displaysize defined in the property file. NOT IN USE
     * 2009-08-15
     */
    public double getDefaultDisplaySizeOld() {
        String s1 = null, s2 = null;
        double val = 1.0;

        String key = "functionpanel.displaysize.default";
        if (appMainAdmin.viewDex.appProperty.getStudyProperties().containsKey(key)) {
            s1 = appMainAdmin.viewDex.appProperty.getStudyProperties().getProperty(key).trim();
            s2 = s1.replace('"', ' ').trim();
        }

        // Read defined displaysize button.
        ArrayList<VgFunctionPanelZoomModeControl> list = appMainAdmin.viewDex.vgHistory.getFunctionPanelZoomModeList();
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).getZoomModeName();
            if (str.equalsIgnoreCase(s2)) {
                val = list.get(i).getZoomValue();
                break;
            }
        }
        return val;
    }

    /**
     * ******************************************************************
     *
     * Action
     *
     ******************************************************************
     */
    /**
     * The "Next" button action. If the nodeType is root, show the next image in
     * the stack. If the nodetype is stack, show the next stack.
     */
    public void nextStackAction() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        boolean localizationStatusActive = appMainAdmin.viewDex.localization.localizationActiveStatusExist();

        if (localizationStatusActive) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // notesStatus
        boolean notesStatus = false;
        String key = "notespanel";
        String notes = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (notes.equalsIgnoreCase("Yes") || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // Eye tracking
        //appMainAdmin.viewDex.eyeTracking.sendUDPMessage("ET_STP");
        // reset
        boolean localizationSelectStatusExist = appMainAdmin.viewDex.localization.localizationSelectStatusExist();
        if (localizationSelectStatusExist) {
            appMainAdmin.viewDex.localization.updateLocalizationStatus(StudyDbLocalizationStatus.SELECTED, StudyDbLocalizationStatus.SET);
        }

        appMainAdmin.viewDex.localization.resetLocalizationOverlay();
        appMainAdmin.viewDex.distance.resetROIDistanceOverlay();
        appMainAdmin.viewDex.area.resetROIVolumeOverlay();
        appMainAdmin.viewDex.pixelValueMean.resetROIPixelValueMeanOverlay();

        // status
        newStackStatus = true;

        if (stackNode.getNodeType() == StudyDbNodeType.NODE_TYPE_STACK) {
            appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateAll();
            stackNode.setStackEvaluationTimeStop();

            // save the notes text
            if (notesStatus && appMainAdmin.viewDex.vgNotesPanel != null) {
                stackNode.setNotes(appMainAdmin.viewDex.vgNotesPanel.getNoteText());
                appMainAdmin.viewDex.vgNotesPanel.setNotesText("");
            }
            
            //===================================================
            // test History object 
            // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("VgControl.deleteImagesInSelectedStack");
            // end test history
            //===================================================

            deleteImagesInSelectedStack();
            appMainAdmin.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
            appMainAdmin.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
            appMainAdmin.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();

            //===================================================
            // test History object
            // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("StudyDbUtility.updateImageNode");
            // end test History object
            //===================================================
            
            studyDbUtility.updateImageNode();
            
            //===================================================
            // test History object
            // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("StudyDbUtilityNode.updateImageNode");
            // end test history
            //===================================================

            //runModeStatus
            if (runMode == VgRunMode.CREATE_EXIST
                    || runMode == VgRunMode.EDIT_EXIST
                    || runMode == VgRunMode.DEMO_EXIST) {
                studyLog.update();
            }

            if (studyDbUtility.nextStackNodeExist()) {
                
                //================================================
                // test History object
                // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("StudyDbUtility.setNextSelectedStackNode");
                // end History object
                //================================================
                
                studyDbUtility.setNextSelectedStackNode();
                
                //================================================
                // test History object
                // appMainAdmin.viewDex.vgHistoryMainUtil.printHistoryObjectSize("StudyDbUtility.setNextSelectedStackNode");
                // end test
                //================================================
                
                studyDbUtility.setSelectedImageNodeCntToFirstImage();

                //EvaluationTime
                StudyDbStackNode stackNode2 = studyDbUtility.getSelectedStackNode();
                stackNode2.setStackEvaluationTimeStart();

                if (getStackLoadInBackgroundStatus()) {
                    setSelStackCount();
                    setTotalStackCount();
                    loadStackInBackground();
                } else {
                    // render the image
                    StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
                    if (imageNode != null) {
                        setImage(imageNode);
                        int[] windowWidth = imageNode.getWindowWidth();
                        int[] windowCenter = imageNode.getWindowCenter();
                        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidth[0], windowCenter[0]);
                        setSelImageCount();
                        setTotalImageCount();
                        setSelStackCount();
                        setTotalStackCount();
                        setCanvasOverlayStackInfo();
                    }
                }

                // update rating values
                appMainAdmin.viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();

                // update the notes value
                if (notesStatus && appMainAdmin.viewDex.vgNotesPanel != null) {
                    appMainAdmin.viewDex.vgNotesPanel.setNotesPanel();
                }

                // runModeStatus
                if (runMode == VgRunMode.CREATE_EXIST) {
                    
                    
                    //=============================================
                    // test History object
                    // long t10 = System.currentTimeMillis();
                    // end test History object
                    //=============================================
                    
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                    
                    //==============================================
                    // test History object
                    // long t11 = System.currentTimeMillis() - t10;
                    // long t12 = (t11/1000) / 60;
                    // System.out.println("VgControl.NextStackAction writeHistory: " + 
                    //         "time to write: " + t11 + " milliseconds " + "(" + t12 + " minutes)");
                    // end test History object
                    //===============================================
                    
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistory();
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistoryBackup();
                } else {
                    if (runMode == VgRunMode.EDIT_EXIST) {
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistory(appMainAdmin.viewDex.vgHistory);
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistoryBackup(appMainAdmin.viewDex.vgHistory);
                    } else {
                        if (runMode == VgRunMode.DEMO_EXIST) {
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistory(appMainAdmin.viewDex.vgHistory);
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistoryBackup(appMainAdmin.viewDex.vgHistory);
                        }
                    }
                }

                // button init
                // SHOW_EXIST missing ??
                if (runMode == VgRunMode.CREATE_EXIST
                        || runMode == VgRunMode.DEMO_EXIST) {
                    if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
                        appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(false);
                        appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
                        appMainAdmin.viewDex.vgLocalizationPanel.setShowHideText();
                    }
                }
                return;
            } else {
                appMainAdmin.viewDex.vgHistory.setStudyDone(true);
                appMainAdmin.viewDex.canvasControl.setCanvasToBlack();

                // runModeStatus
                if (runMode == VgRunMode.CREATE_EXIST) {
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistory();
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistoryBackup();
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);
                } else {
                    if (runMode == VgRunMode.EDIT_EXIST) {
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistory(appMainAdmin.viewDex.vgHistory);
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistoryBackup(appMainAdmin.viewDex.vgHistory);
                    } else {
                        if (runMode == VgRunMode.DEMO_EXIST) {
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistory(appMainAdmin.viewDex.vgHistory);
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistoryBackup(appMainAdmin.viewDex.vgHistory);
                        }
                    }
                }
                setStudyDone();
            }
        }
        return;
    }

    /**
     * The "Go to & Prev" panel, "Prev" button action. If the nodeType is root,
     * show the prev image in the stack "directory". If the nodetype is stack,
     * show the prev stack.
     *
     */
    public void prevStackAction() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        boolean localizationStatusActive = appMainAdmin.viewDex.localization.localizationActiveStatusExist();

        if (localizationStatusActive) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // notesStatus
        boolean notesStatus = false;
        String key = "notespanel";
        String notes = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
        if (notes.equalsIgnoreCase("Yes") || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // Eye tracking
        //appMainAdmin.viewDex.eyeTracking.sendUDPMessage("ET_STP");
        // reset
        appMainAdmin.viewDex.localization.resetLocalizationOverlay();
        appMainAdmin.viewDex.distance.resetROIDistanceOverlay();
        appMainAdmin.viewDex.area.resetROIVolumeOverlay();
        appMainAdmin.viewDex.pixelValueMean.resetROIPixelValueMeanOverlay();

        // status
        newStackStatus = true;

        if (stackNode.getNodeType() == StudyDbNodeType.NODE_TYPE_STACK) {
            appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateAll();
            stackNode.setStackEvaluationTimeStop();

            // save the note text
            if (notesStatus && appMainAdmin.viewDex.vgNotesPanel != null) {
                stackNode.setNotes(appMainAdmin.viewDex.vgNotesPanel.getNoteText());
            }

            deleteImagesInSelectedStack();
            appMainAdmin.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
            appMainAdmin.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
            appMainAdmin.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();

            studyDbUtility.updateImageNode();

            //runModeStatus
            if (runMode == VgRunMode.CREATE_EXIST
                    || runMode == VgRunMode.EDIT_EXIST
                    || runMode == VgRunMode.DEMO_EXIST) {
                studyLog.update();
            }

            if (studyDbUtility.prevStackNodeExist()) {
                studyDbUtility.setPrevSelectedStackNode();
                studyDbUtility.setSelectedImageNodeCntToFirstImage();

                //EvaluationTime
                StudyDbStackNode stackNode2 = studyDbUtility.getSelectedStackNode();
                stackNode2.setStackEvaluationTimeStart();

                if (getStackLoadInBackgroundStatus()) {
                    setSelStackCount();
                    setTotalStackCount();
                    loadStackInBackground();
                } else {
                    // render the image
                    StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
                    if (imageNode != null) {
                        setImage(imageNode);
                        int[] windowWidth = imageNode.getWindowWidth();
                        int[] windowCenter = imageNode.getWindowCenter();
                        appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidth[0], windowCenter[0]);
                        setSelImageCount();
                        setTotalImageCount();
                        setSelStackCount();
                        setTotalStackCount();
                        setCanvasOverlayStackInfo();
                    }
                }

                // update rating values
                appMainAdmin.viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();

                // update the notes value
                if (notesStatus && appMainAdmin.viewDex.vgNotesPanel != null) {
                    appMainAdmin.viewDex.vgNotesPanel.setNotesPanel();
                }

                // runModeStatus
                if (runMode == VgRunMode.CREATE_EXIST) {
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistory();
                    //appMainAdmin.viewDex.vgHistoryMain.writeOriginalHistoryBackup();
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);

                } else {
                    if (runMode == VgRunMode.EDIT_EXIST) {
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistory(appMainAdmin.viewDex.vgHistory);
                        appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistoryBackup(appMainAdmin.viewDex.vgHistory);
                    } else {
                        if (runMode == VgRunMode.DEMO_EXIST) {
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistory(appMainAdmin.viewDex.vgHistory);
                            appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistoryBackup(appMainAdmin.viewDex.vgHistory);
                        }
                    }
                }

                // button init
                // SHOW_EXIST missing ??
                if (runMode == VgRunMode.CREATE_EXIST
                        || runMode == VgRunMode.DEMO_EXIST) {
                    if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
                        appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(false);
                        appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
                        appMainAdmin.viewDex.vgLocalizationPanel.setShowHideText();
                    }
                }
                return;
            } else {
                appMainAdmin.viewDex.vgHistory.setStudyDone(true);
                appMainAdmin.viewDex.canvasControl.setCanvasToBlack();

                // runModeStatus
                if (runMode != VgRunMode.SHOW_EXIST) {
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                    appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);
                }
                setStudyDone();
            }
        }
        return;
    }

    /**
     * The "GO to" textInput action. If the nodeType is root, show the next
     * image in the stack. If the nodetype is stack, show the next stack.
     */
    public void stackAction(int cnt) {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int stackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = studyDbUtility.getSelectedImageNodeCount();
        Point2D activePoint = appMainAdmin.viewDex.localization.getLocalizationActivePoint();

        //appMainAdmin.viewDex.localization.updateLocalizationActiveStatus_old(pSelect);
        boolean taskPanelActivePointStatus = appMainAdmin.viewDex.localization.getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);
        if (taskPanelActivePointStatus) {
            appMainAdmin.viewDex.localization.setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
        }

        boolean localizationActive = appMainAdmin.viewDex.localization.localizationActiveStatusExist();

        // Overlay reset
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);

        //appMainAdmin.viewDex.canvas.setCanvasROIDistanceDrawingStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasROIDistanceDrawingValue(0, 0, 0, 0);
        appMainAdmin.viewDex.canvas.setCanvasROIDistanceUpdateStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasROIDistanceUpdateValue(null);

        appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateTextStatus(false);
        appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateValue(null);

        if (!(stackNode.getNodeType() == StudyDbNodeType.NODE_TYPE_STACK)) {
            return;
        }

        if (localizationActive) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        if (!studyDbUtility.stackExist(cnt)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // prepare for stack change
        //appMainAdmin.viewDex.localization.drawSetSymbolOnImageAndRender(activePoint, true);
        appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateAll();
        stackNode.setStackEvaluationTimeStop();
        deleteImagesInSelectedStack();
        appMainAdmin.viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
        appMainAdmin.viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
        appMainAdmin.viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();

        studyDbUtility.updateImageNode();

        //runModeStatus
        if (runMode == VgRunMode.CREATE_EXIST
                || runMode == VgRunMode.EDIT_EXIST
                || runMode == VgRunMode.DEMO_EXIST) {
            studyLog.update();
        }

        if (studyDbUtility.stackLast(cnt)) {
            appMainAdmin.viewDex.vgHistory.setStudyDone(true);
            appMainAdmin.viewDex.canvasControl.setCanvasToBlack();

            // runModeStatus
            if (runMode != VgRunMode.SHOW_EXIST) {
                appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);
            }
            setStudyDone();
        } else {
            // set stack & image
            appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(cnt);
            studyDbUtility.setSelectedImageNodeCntToFirstImage();

            //EvaluationTime
            StudyDbStackNode stackNode2 = studyDbUtility.getSelectedStackNode();
            stackNode2.setStackEvaluationTimeStart();

            // setImage
            if (getStackLoadInBackgroundStatus()) {
                setSelStackCount();
                setTotalStackCount();
                loadStackInBackground();
            } else {
                // render the image
                StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
                if (imageNode != null) {
                    setImage(imageNode);
                    int[] windowWidth = imageNode.getWindowWidth();
                    int[] windowCenter = imageNode.getWindowCenter();
                    appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidth[0], windowCenter[0]);
                    setSelImageCount();
                    setTotalImageCount();
                    setSelStackCount();
                    setTotalStackCount();
                    setCanvasOverlayStackInfo();
                }
            }

            // update rating values
            appMainAdmin.viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();

            // runModeStatus
            if (runMode == VgRunMode.CREATE_EXIST) {
                appMainAdmin.viewDex.vgHistoryMainUtil.writeHistory(appMainAdmin.viewDex.vgHistory);
                appMainAdmin.viewDex.vgHistoryMainUtil.writeHistoryBackup(appMainAdmin.viewDex.vgHistory);
            } else {
                if (runMode == VgRunMode.EDIT_EXIST) {
                    appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistory(appMainAdmin.viewDex.vgHistory);
                    appMainAdmin.viewDex.vgHistoryEditUtil.writeEditHistoryBackup(appMainAdmin.viewDex.vgHistory);
                } else {
                    if (runMode == VgRunMode.DEMO_EXIST) {
                        appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistory(appMainAdmin.viewDex.vgHistory);
                        appMainAdmin.viewDex.vgHistoryDemoUtil.writeDemoHistoryBackup(appMainAdmin.viewDex.vgHistory);
                    }
                }
            }

            // button init
            // SHOW_EXIST missing ??
            if (runMode == VgRunMode.CREATE_EXIST
                    || runMode == VgRunMode.DEMO_EXIST) {
                if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
                    appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationShowHideButtonEnableStatus(false);
                    appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
                    appMainAdmin.viewDex.vgLocalizationPanel.setShowHideText();
                }
            }
        }
    }

    /**
     * Called from the Localization panel "Hide" button. If localization mark
     * exist delete the image. Set the original image in the canvas. The image
     * is reloaded but the previous wl and transfomation settings is maintained.
     * These are read from the <code>StudyDbStackNode<code/> object. Set button
     * enable to false. OLD
     */
    public void hideLocalizationButtonAction() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int nodeType = studyDbUtility.getSelectedStackNode().getNodeType();

        if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
            studyDbUtility.updateStackNode();

            // reset overlay
            appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
            appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationStatus(false);

            //appMainAdmin.viewDex.canvas.setCanvasOverlayDistanceMeasurementStatus(false);
            //appMainAdmin.viewDex.canvas.setCanvasOverlayDistanceMeasurementValue(0,0,0,0);
            // rating
            //appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateAll();
            appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateLocalization();

            boolean imageMarkExist = appMainAdmin.viewDex.localization.getLocalizationMarkExistStatusForSelectedImage();

            if (imageMarkExist) {
                appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(false);
                deleteImageInSelectedNode();

                // render
                StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
                setImage(imageNode);

                int windowWidthStack = stackNode.getWindowWidth();
                int windowCenterStack = stackNode.getWindowCenter();
                int[] windowWidthImage = imageNode.getWindowWidth();
                int[] windowCenterImage = imageNode.getWindowCenter();
                AffineTransform atx = stackNode.getAffineTransform();
                appMainAdmin.viewDex.canvas.setTransform(atx, false);

                //Implemented 20140107
                // wl status
                boolean stackImagesWLStatus = false;
                String key = "stack-images.wl";
                String wl = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
                if (wl.equalsIgnoreCase("Yes") || wl.equalsIgnoreCase("Y")) {
                    stackImagesWLStatus = true;
                }

                //20150911
                boolean WLActivateInStack = appMainAdmin.viewDex.canvasControl.getWLActivateInStack();
                if (stackImagesWLStatus && !WLActivateInStack) {
                    appMainAdmin.viewDex.windowLevel.setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
                } else {
                    appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthStack, windowCenterStack);
                }

                /*
                if(!stackImagesWLStatus)
                appMainAdmin.viewDex.windowLevel.
                setWindowLevel(windowWidthStack, windowCenterStack);
                else
                appMainAdmin.viewDex.windowLevel.
                setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
                 */
            }
        }
    }

    /**
     * If nodeType is <code>NODE_TYPE_STACK<code/> update the image node and set
     * the next image in the stack.
     *
     * @param <code>direction</code> 0 means next, 1 means prev.
     */
    public boolean setImageNextPrevInStack(int direction) {
        //System.out.println("VgControl:setImageNextPrevInStack");
        int stackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = studyDbUtility.getSelectedImageNodeCount();

        // Update the status
        Point2D activePoint = appMainAdmin.viewDex.localization.getLocalizationActivePoint();

        // It's not possible to show the next/prev image it there's an active mark,
        // and the taskpanel questions have not been answered.
        boolean taskPanelActivePointStatus = appMainAdmin.viewDex.localization.getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);

        if (activePoint != null && taskPanelActivePointStatus) {
            appMainAdmin.viewDex.localization.setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
        } else {
            if (activePoint != null && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }

        appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateAll();
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int nodeType = studyDbUtility.getSelectedStackNode().getNodeType();
        if (nodeType == StudyDbNodeType.NODE_TYPE_STACK) {
            studyDbUtility.updateStackNode();
            if (direction == 0) {
                if (studyDbUtility.nextImageNodeExist()) {
                    studyDbUtility.setNextSelectedImageNodeCount();
                }
            }
            if (direction == 1) {
                if (studyDbUtility.prevImageNodeExist()) {
                    studyDbUtility.setPrevSelectedImageNodeCount();
                }
            }

            // update rating values
            appMainAdmin.viewDex.vgTaskMainPanel.setRatingValuesNotLocalized();

            // overlay reset
            appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0, 0, 0);
            appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
            appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(false);

            // Distance
            //appMainAdmin.viewDex.canvas.setCanvasROIDistanceDrawingStatus(false);
            appMainAdmin.viewDex.canvas.setCanvasROIDistanceDrawingValue(0, 0, 0, 0);
            appMainAdmin.viewDex.canvas.setCanvasROIDistanceUpdateStatus(false);
            appMainAdmin.viewDex.canvas.setCanvasROIDistanceUpdateValue(null);

            // Volume
            appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateStatus(false);
            appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateTextStatus(false);
            appMainAdmin.viewDex.canvas.setCanvasROIAreaUpdateValue(null);

            // Pixel Measure
            // To add
            // localization button status
            boolean showHideLocalizationButtonStatus = false;
            if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
                showHideLocalizationButtonStatus = appMainAdmin.viewDex.vgLocalizationPanel.getLocalizationPanelStatus();
            }

            boolean imageMarkExist = appMainAdmin.viewDex.localization.getLocalizationMarkExistStatusForSelectedImage();

            // set the image
            StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
            setImage(imageNode);

            // set localization Localization.Set marks
            if (showHideLocalizationButtonStatus && imageMarkExist) {
                //setLocalizationSetSymbolAndNoRender();
                if (appMainAdmin.viewDex.vgLocalizationPanel != null) {
                    appMainAdmin.viewDex.vgLocalizationPanel.setHideText();
                    appMainAdmin.viewDex.vgLocalizationPanel.setLocalizationEraseButtonEnableStatus(true);
                    appMainAdmin.viewDex.vgLocalizationPanel.showHideButton.setEnabled(true);
                }
            }

            if (showHideLocalizationButtonStatus) {
                // set select symbol
                if (appMainAdmin.viewDex.localization.localizationSelectStatusExist()) {
                    Point2D selPoint = appMainAdmin.viewDex.localization.getSelectedLocalizationPoint();
                    appMainAdmin.viewDex.vgTaskMainPanel.setRatingInitStateLocalization();
                    appMainAdmin.viewDex.vgTaskMainPanel.setRatingValue(selPoint);
                    int cnt = appMainAdmin.viewDex.appMainAdmin.vgControl.getSelectedStackImageCnt();
                    appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationPositionValue((int) selPoint.getX(), (int) selPoint.getY(), cnt);
                    appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderPositionStatus(true);
                }
            }
            
            // render
            int windowWidthStack = stackNode.getWindowWidth();
            int windowCenterStack = stackNode.getWindowCenter();
            int[] windowWidthImage = imageNode.getWindowWidth();
            int[] windowCenterImage = imageNode.getWindowCenter();
            AffineTransform atx = stackNode.getAffineTransform();
            appMainAdmin.viewDex.canvas.setTransform(atx, false);

            // set drawing overlay
            appMainAdmin.viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            appMainAdmin.viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
            appMainAdmin.viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();

            if(showHideLocalizationButtonStatus){
                appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            } else{
                appMainAdmin.viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(false);
            }
                
            appMainAdmin.viewDex.localization.setLocalizationOverlayListInCanvas();

            appMainAdmin.viewDex.canvas.setCanvasDefaultColor();

            // wl status. Implemented 20140107
            // NOT IN USE
            boolean stackImagesWLStatus = false;
            String key = "stack-images.wl";
            String wl = propUtils.getPropertyStringValue(appMainAdmin.viewDex.appProperty.getStudyProperties(), key);
            if (wl.equalsIgnoreCase("Yes") || wl.equalsIgnoreCase("Y")) {
                stackImagesWLStatus = true;
            }

            //20150911
            boolean WLActivateInStack = appMainAdmin.viewDex.canvasControl.getWLActivateInStack();

            //There is no longer any property to set stack wide w/l
            if (stackImagesWLStatus && !WLActivateInStack) {
                appMainAdmin.viewDex.windowLevel.setWindowLevel((int) windowWidthImage[0], (int) windowCenterImage[0]);
            } else {
                appMainAdmin.viewDex.windowLevel.setWindowLevel(windowWidthStack, windowCenterStack);
            }

            // Rendering timeStamp
            imageNode.setTimeStampImageRendering();
            //System.out.println("TimeStampImageRendering (VgControl.setImageNextPrevInStack) = "
            //      + imageNode.getTimeStampImageRendering());

            // Eye tracking
            // send an UDP message to the "Eye tracking" system
            if (appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus() && appMainAdmin.viewDex.eyeTracking.udpClient != null) {
                appMainAdmin.viewDex.eyeTracking.udpClient.setMessage("ET_REC");
                appMainAdmin.viewDex.eyeTracking.udpClient.send();

                // filePath
                File studyPath = imageNode.getStudyPath();
                String msg = "ET_REM " + studyPath;
                appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg);
                appMainAdmin.viewDex.eyeTracking.udpClient.send();

                // frameNumber
                int imageNo = imageNode.getImageNo();
                stackNode = studyDbUtility.getSelectedStackNode();
                int nodeCnt = stackNode.getSelImageNodeCount();
                String msg2 = "ET_REM " + nodeCnt;
                appMainAdmin.viewDex.eyeTracking.udpClient.setMessage(msg2);
                appMainAdmin.viewDex.eyeTracking.udpClient.send();

                // position & scale
                /*
                if(newStackStatus){
                AffineTransform at = appMainAdmin.viewDex.canvas.getTransform();
                double x = at.getTranslateX();
                double y = at.getTranslateY();
                double scalex = at.getScaleX();
                double scaley = at.getScaleY();

                String msg3 = "ET_REC " + x + " " + y + " " + scalex + " " + scaley;
                udpClient.setMessage(msg3);
                udpClient.send();
                newStackStatus = false;
                }*/
            }

            setSelImageCount();
            setTotalImageCount();
            setSelStackCount();
            setTotalStackCount();
        }
        return true;
    }
    
    /**
     * Set the selected stack count label on the <code>VgRunPanel<code/>.
     */
    public void setSelStackCount() {
        int cnt = 0;

        if (appMainAdmin.viewDex.vgHistory != null) {
            cnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        }

        appMainAdmin.viewDex.vgRunPanel.setSelStackCount(Integer.toString(cnt + 1));
    }

    /**
     * Set the total stack count label on the <code>VgRunPanel<code/>.
     */
    public void setTotalStackCount() {
        int cnt = studyDbUtility.getTotalStackNodeCount();

        // Cnt is subtracted with -1. The first stackNode is the default node
        // containing separate images under the imagedb directory.
        appMainAdmin.viewDex.vgRunPanel.setTotalStackCount("(" + Integer.toString(cnt) + ")");
    }

    /**
     * Set the image count label on the <code>VgRunPanel<code/>.
     */
    public void setSelImageCount() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int cnt = stackNode.getSelImageNodeCount();
        //ArrayList<StudyDbImageNode> imageList = getSelectedImageList();
        //int size = imageList.size();
        appMainAdmin.viewDex.vgRunPanel.setSelImageCount(Integer.toString(cnt + 1));
        //System.out.println("VgControl.setSelImageCnt =" + Integer.toString(cnt + 1));
    }

    /**
     * Set the total number of images contained in the selected stack on the
     * count label on the <code>VgRunPanel<code/>.
     */
    public void setTotalImageCount() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int cnt = stackNode.getSelImageNodeCount();
        ArrayList<StudyDbImageNode> imageList = studyDbUtility.getSelectedImageList();
        int size = imageList.size();

        appMainAdmin.viewDex.vgRunPanel.setTotalImageCount("(" + Integer.toString(size) + ")");
    }

    /**
     * Get the selected stack image count.
     *
     * @return <code>int<code/> the image count.
     */
    public int getSelectedStackImageCnt() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int cnt = stackNode.getSelImageNodeCount();

        return cnt + 1;
    }

    /**
     * Set the canvas overlay <code>StudyDbStackNode<code/> info.
     */
    public void setCanvasOverlayStackInfo() {
        StudyDbStackNode stackNode = studyDbUtility.getSelectedStackNode();
        int stackNo = stackNode.getItemCnt();
        appMainAdmin.viewDex.canvas.setCanvasOverlayStackNoValue(Integer.toString(stackNo + 1));

        int markTot = appMainAdmin.viewDex.localization.getLocalizationSelectStackNoOfMarks();
        appMainAdmin.viewDex.canvas.setCanvasOverlayMarkNoValue(Integer.toString(markTot));
    }

    /**
     * Delete the images in the selected stack.
     */
    public void deleteImagesInSelectedStack() {
        ArrayList<StudyDbImageNode> imageList = studyDbUtility.getSelectedImageList();

        for (int i = 0; i < imageList.size(); i++) {
            imageList.get(i).deleteImageOrg();
            imageList.get(i).deleteDataSet();
        }
    }

    /**
     * Delete the image in the selected imageNode.
     */
    public void deleteImageInSelectedNode() {
        StudyDbImageNode imageNode = studyDbUtility.getSelectedImageNode();
        imageNode.deleteImageOrg();
    }

    /**
     * *******************************************************
     * KeyListener inteface
     * ******************************************************
     */
    @Override
    public void keyPressed(KeyEvent e) {
        /*int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_UP)
        setImageNextInStack();*/
    }

    @Override
    public void keyReleased(KeyEvent e) {
        /*int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_DOWN)
        setImagePrevInStack();*/
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}
