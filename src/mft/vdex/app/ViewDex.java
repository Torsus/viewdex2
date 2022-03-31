/* @(#) ViewDex.java 08/31/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.app;

import info.clearthought.layout.TableLayout;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.Properties;
import javax.media.jai.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import mft.vdex.modules.vg.VgFunctionPanel;
import mft.vdex.modules.vg.VgStudyNextCasePanel;
import mft.vdex.modules.vg.VgRunPanel;
import mft.vdex.dialog.AboutDialog;
import mft.vdex.modules.vg.VgHistory;
import mft.vdex.modules.vg.VgTaskPanel;
import mft.vdex.event.PlanarImageLoadedEvent;
import mft.vdex.event.ListSelectListener;
import mft.vdex.event.PlanarImageLoadedListener;
import mft.vdex.event.ListSelectEvent;
//import mft.vdex.imageio.FileBrowser;
//import mft.vdex.imageio.StudyLoader_old;
//import mft.vdex.imageio.ImageLoaderJAI;
import mft.vdex.modules.et.EyeTracking;
import mft.vdex.modules.vg.VgCineLoopPanel;
import mft.vdex.modules.vg.VgClarificationPanel;
import mft.vdex.modules.vg.VgHistoryCreateUtil;
import mft.vdex.modules.vg.VgHistoryDemoUtil;
import mft.vdex.modules.vg.VgHistoryEditUtil;
import mft.vdex.modules.vg.VgHistoryMainUtil;
import mft.vdex.modules.vg.VgHistoryUtil;
import mft.vdex.modules.vg.VgLocalizationPanel;
import mft.vdex.modules.vg.VgNotesPanel;
import mft.vdex.modules.vg.VgRunMode;
import mft.vdex.modules.vg.VgStudyNextCaseExtendedPanel;
import mft.vdex.modules.vg.VgTaskPanelUtility;
import mft.vdex.viewer.Localization;
import mft.vdex.viewer.PanInterface;
import mft.vdex.viewer.WindowLevelGUI;
import mft.vdex.viewer.PanGUI;
import mft.vdex.viewer.ZoomInterface;
import mft.vdex.viewer.ImageCanvasInterface;
import mft.vdex.viewer.ZoomGUI;
import mft.vdex.viewer.WindowLevel;
import mft.vdex.viewer.GeomManip;
import mft.vdex.viewer.Pan;
import mft.vdex.viewer.PixelValueMean;
import mft.vdex.viewer.PixelValueMeanGUI;
import mft.vdex.viewer.CanvasContextMenu;
import mft.vdex.viewer.Zoom;
import mft.vdex.viewer.ImageCanvas;
import mft.vdex.viewer.CanvasControl;
import mft.vdex.viewer.LocalizationGUI;
import mft.vdex.viewer.LocalizationInterface;
import mft.vdex.viewer.ScrollStack;
import mft.vdex.viewer.ScrollStackGUI;
import mft.vdex.viewer.ScrollStackInterface;
import mft.vdex.viewer.Distance;
import mft.vdex.viewer.DistanceGUI;
import mft.vdex.viewer.DistanceMeasurement;
import mft.vdex.viewer.PixelValueMeanMeasurement;
import mft.vdex.viewer.Area;
import mft.vdex.viewer.AreaGUI;
import mft.vdex.viewer.AreaMeasurement;


/**
 * <code>ViewDEX<code> is the main class for the ViewDEX application.
 * @author sune
 */
public class ViewDex extends JFrame implements ListSelectListener, KeyListener,
        WindowFocusListener {
    // Windows, Linux, MacOX
    //public static final String osType = "OS X";

    public static final String osType = "Windows";
    //public static final String osType = "Windows";
    public static final String productName = "ViewDEX 2";
    public static final String productVersion = "ViewDEX-2.57";    
    public AppMainAdmin appMainAdmin;
    public AppProperty appProperty;
    public AppPropertyCreate appPropertyCreate;
    public AppPropertyUtils appPropertyUtils;
    public EyeTracking eyeTracking;

    public VgHistory vgHistory;
    public VgHistoryUtil vgHistoryUtil;
    public VgHistoryMainUtil vgHistoryMainUtil;
    public VgHistoryCreateUtil vgHistoryCreateUtil;
    public VgHistoryEditUtil vgHistoryEditUtil;
    public VgHistoryDemoUtil vgHistoryDemoUtil;

    protected AppCommandPanelDialog commandPanelDialog;
    private Dimension commandPanelDialogSize;
    protected JPanel statusPanel;
    public ImageCanvas canvas;
    protected JTabbedPane controlTabbedPane;
    protected JPanel testSubPanel1, testSubPanel2, testSubPanel3;
    protected JPanel testSubPanel4, testSubPanel5, testSubPanel6;
    //protected FileBrowser fileBrowser;
    //protected ImageLoaderJAI loader;
    public CanvasControl canvasControl;
    public Pan pan;
    protected PanGUI panGUI;
    public ScrollStack scrollStack;
    protected ScrollStackGUI scrollStackGUI;
    public Zoom zoom;
    public ZoomGUI zoomGUI;
    public WindowLevel windowLevel;
    public WindowLevelGUI windowLevelGUI;
    public CanvasContextMenu canvasContextMenu;
    public GeomManip geomManip;
    // Main menu
    private JMenuBar appMenuBar;
    private JMenuItem fileMenu;
    private Font menuFont;
    private JMenuItem exitItem;
    // Tool menu
    private JMenu toolMenu;
    private JCheckBoxMenuItem wlCanvasDisplayCheckBoxmenuItem;
    private JCheckBoxMenuItem windowingFixedMinimumCheckBoxmenuItem;
    private JCheckBoxMenuItem mousePositionCheckBoxMenuItem;
    private JCheckBoxMenuItem pixelValueCheckBoxMenuItem;
    private JCheckBoxMenuItem displayAllCheckBoxMenuItem;
    // Misc menu
    private JMenu miscMenu;
    private JMenuItem createLogMenuItem;
    // Help menu
    private JMenu helpMenu;
    private JMenuItem helpAboutMenuItem;
    private JCheckBoxMenuItem itemCb;
    private boolean mousePositionDisplay;
    private static boolean studyInfoAppMainFrameStatus;
    // Vg study
    private JPanel vgStudyLayoutPanel;
    private JPanel vgStudyRunPanel;
    private JPanel vgStudyTaskPanel;
    private JPanel vgStudySelectPanel;
    private JPanel vgStudySelectExtendedPanel;
    private JPanel vgStudyCanvasFunctionPanel;
    private JPanel vgStudyClarificationPanel;
    public JPanel vgStudyNotesPanel;
    public VgNotesPanel vgNotesPanel;
    private JPanel vgStudyDumyPanel;
    public JPanel vgStudyLocalizationPanel;
    private JPanel vgStudyCineLoopPanel;
    private VgStudyNextCasePanel vgStudyDicomSelectControl;
    public VgStudyNextCaseExtendedPanel vgStudyNextCaseExtendedControl;
    public JPanel vgStudyMainPanel;
    public VgRunPanel vgRunPanel;
    public VgTaskPanel vgTaskMainPanel;
    public VgTaskPanelUtility vgTaskPanelUtility;
    public VgFunctionPanel vgFunctionPanel;
    public VgCineLoopPanel vgCineLoopPanel;
    public VgLocalizationPanel vgLocalizationPanel;
    // Localization
    public Localization localization;
    // Distance
    public Distance distance;
    public DistanceMeasurement distanceMeasurement;
    // Volume
    public Area area;
    public AreaMeasurement areaMeasurement;
    // PixelValue
    public PixelValueMean pixelValueMean;
    public PixelValueMeanMeasurement pixelValueMeanMeasurement;
    //test
    private javax.swing.JFrame mainFrame = null;

    /****************************************************
     *
     *   main application
     *
     ***************************************************/
    public ViewDex() {
        init();
    }

    private void init() {
        getSystemProperties();
        getGraphicsInfo();
        setFontUIResource();
        setJAIMediaLibDisableProperty();
        
        //====================================================
        //Runtime rt = Runtime.getRuntime();
        // The number of virtual processors is the number of hardware threads on the system.
        //int virtualProcessors = rt.availableProcessors();
        //====================================================
        
        createUI();
        
        addKeyListener(this);
        addWindowFocusListener(this);
        appMainAdmin = new AppMainAdmin(this);
        appProperty = new AppProperty(this);
        appPropertyCreate = new AppPropertyCreate(this);
        appPropertyUtils = new AppPropertyUtils();

        // History init
        vgHistory = new VgHistory();
        vgHistoryMainUtil = new VgHistoryMainUtil(this);
        vgHistoryCreateUtil = new VgHistoryCreateUtil(this);
        vgHistoryEditUtil = new VgHistoryEditUtil(this);
        vgHistoryDemoUtil = new VgHistoryDemoUtil(this);
        vgHistoryUtil = new VgHistoryUtil(this);

        eyeTracking = new EyeTracking(this);
        appMainAdmin.startRunLogin();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        mainFrame = this;
        //KeyHandler listener = new KeyHandler();
    }

    private void getSystemProperties() {
        String nl = "\n";
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String javaVendor = System.getProperty("java.vendor");
        String javaVersion = System.getProperty("java.version");
        String javaHome = System.getProperty("java.home");
        //String javaVirtuall = System.getProperty("java.virtual");
        String javaVmName = System.getProperty("java.vm.name");
        String userCountry = System.getProperty("user.country");
        String userLanguage = System.getProperty("user.language");
        String userHome = System.getProperty("user.home");
        System.out.println("userHome = " + userHome);
        String userDir = System.getProperty("user.dir");
        System.out.println("userDir = " + userDir);
        String osSystem = osName + " version " + osVersion + " running on " + osArch;
    }

    /*
     * Get and print some graphics info.
     */
    private void getGraphicsInfo() {
        String[] str = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        /*
        System.out.println("Available font families names");
        for(int i = 0; i < str.length; i++){
        System.out.println(str[i]);
        }
         */
    }

    /*
     * Sets the font for all OptionPane.
     */
    private void setFontUIResource() {
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font(
                "Arial", Font.BOLD, 14)));
    }

    /*
     * Disable/Enable the JAI mediaLib accelerator wrapper
     * To activate the accelerator add
     * -/jre1.6.0_03/bin
     * mlib_jai.dll
     * mlib_mmx.dll.
     * mlib_jai_util.dll
     * 
     * -jre1.6.0_03/lib/ext
     * jai-core.jar
     * jai_codec.jar
     * mlibwrapper_jai.jar
     */
    private void setJAIMediaLibDisableProperty() {
        java.util.Properties p = new java.util.Properties(System.getProperties());
        p.put("com.sun.media.jai.disableMediaLib", "true" + "");
        System.setProperties(p);
    }

    /**
     * Create the userinterface.
     */
    private void createUI() {
        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        int resolution = toolkit.getScreenResolution();
        //System.out.println("width = " + d.width);
        //System.out.println("height " + d.height);
        //System.out.println("resolution " + resolution + " dots/inch");

        Container contentPane = getContentPane();
        //double size[][] = {{TableLayout.FILL, 250}, {25, TableLayout.FILL}};
        //double size[][] = {{TableLayout.FILL}, {25, TableLayout.FILL}};
        double size[][] = {{TableLayout.FILL}, {TableLayout.FILL}};
        TableLayout layout = new TableLayout(size);
        contentPane.setLayout(layout);
        //contentPane.setBackground(new Color(0,150,0)); No effect

        //createAppMenuBar(contentPane);
        createCanvas();

        //createCommandPanel2();
        //contentPane.add(appMenuBar, "0,0");
        contentPane.add(canvas, "0,0");

        //contentPane.add(commandPanel, "1,1");

        allowClosing();
        //this.setBackground(Color.black);    no effect
        this.setTitle("ViewDEX");
        this.setLocation(0, 0);
        this.setSize(d);
        //this.show();  deprecated
        //this.requestFocusInWindow();
        this.setVisible(true);
    }

    /*
     * Remove AppPanels.
     */
    private void removeAppPanels() {
        Container contentPane = getContentPane();

        if (appMenuBar != null) {
            appMenuBar.removeAll();
            appMenuBar = null;
        }
        /*
        if(canvas != null){
        canvas.removeAll();
        canvas = null;
        }*/
    }

    /***************************************************
     *
     * end main application
     *
     **************************************************/
    /****************************************************
     *
     *                   VgStudy
     *
     ***************************************************/
    /**
     * Create the GUI for the Vg study.
     *
     *           **** IMPORTENT ****
     * A new tablelayout is created and set in the contentPane.
     * The menubar and canvas is add once again. I'm not sure
     * this is a corect way of doing things.
     *
     */
    public void createVgStudy(VgHistory history) {
        String key;

        // Screen size
        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        //Dimension d = toolkit.getScreenSize();
        //int resolution = toolkit.getScreenResolution();
        //System.out.println("width = " + d.width);
        //System.out.println("height " + d.height);

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();
        Container contentPane = getContentPane();

        // width
        /*
         * Modified 2015-03-19
        key = "controlpanel.width";
        int controlPanelWidth = propUtils.getPropertyIntegerValue(prop, key);
        if(controlPanelWidth == 0)
        controlPanelWidth = 350;  //default
         */

        // width
        key = "mainpanel.width";
        int mainPanelWidth = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (mainPanelWidth == 0) {
            mainPanelWidth = 350;  //default
        }
        // menu vertical size
        key = "app.menu.panel.vertical.size";
        int menuVerticalSize = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (menuVerticalSize == 0) {
            menuVerticalSize = 30; // default value
        }
        // localization
        //boolean localizationStatus = false;

        // deprecated
        /*
        key = "localization";
        String localizationStr = propUtils.getPropertyStringValue(prop, key);
        if(localizationStr.equalsIgnoreCase("Yes") ||
        localizationStr.equalsIgnoreCase("Y"))
        localizationStatus = true;
         */

        // moved 20170315
        //if(appMainAdmin.viewDex.vgTaskPanelUtility.getTaskPanelLocalizationStatusExist())
        //  localizationStatus = true;

        // cineLoopStatus
        boolean cineLoopStatus = false;
        key = "cineloop";
        String cineLoop = appPropertyUtils.getPropertyStringValue(prop, key);
        if (cineLoop.equalsIgnoreCase("Yes") || cineLoop.equalsIgnoreCase("Y")) {
            cineLoopStatus = true;
        }

        // studyselectextended
        boolean studyselectextendedStatus = false;
        key = "studyselectextended";
        String studyselectextended = appPropertyUtils.getPropertyStringValue(prop, key);
        if (appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST &&
                (studyselectextended.equalsIgnoreCase("Yes") || studyselectextended.equalsIgnoreCase("Y"))) {
            studyselectextendedStatus = true;
        }

        // notesStatus
        boolean notesStatus = false;
        key = "notespanel";
        String notes = appPropertyUtils.getPropertyStringValue(prop, key);
        if (notes.equalsIgnoreCase("Yes") || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // Removed 2018-06-11
        // If VgRunMode.EDIT_EXIST the vgStudySelectExtendedPanel is always created.
        //if (appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST
          //      || appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.SHOW_EXIST) {
            //studyselectextendedStatus = true;
        //}
        

        double size[][] = {{TableLayout.FILL, mainPanelWidth},
            {menuVerticalSize, TableLayout.FILL}};
        TableLayout layout = new TableLayout(size);
        contentPane.setLayout(layout);

        // remove
        removeVgPanels();
        removeAppPanels();
        //runGc();

        vgTaskPanelUtility = new VgTaskPanelUtility(this);
        boolean localizationStatus = false;
        if (appMainAdmin.viewDex.vgTaskPanelUtility.getTaskPanelLocalizationStatusExist()) {
            localizationStatus = true;
        }

        // create
        appMenuBar = createAppMenuBar(history);
        createVgImageCanvasContextMenu(history);
        vgStudyLayoutPanel = createVgStudyLayoutPanel(history);
        vgStudyMainPanel = createVgStudyMainPanel(history);
        vgStudyRunPanel = createVgStudyRunPanel(history);
        //vgStudyTaskPanel = createVgStudyTaskPanel(history);
        vgStudyTaskPanel = createVgStudyTaskPanel2(history);
        vgStudySelectPanel = createVgStudySelectPanel(history);
        if (studyselectextendedStatus) {
            vgStudySelectExtendedPanel = createVgStudySelectExtendedPanel(history);
        }
        vgStudyClarificationPanel = createVgStudyClarificationPanel(history);
        if (notesStatus) {
            vgStudyNotesPanel = createVgStudyNotesPanel(history);
        }
        if (cineLoopStatus) {
            vgStudyCineLoopPanel = createVgStudyCineLoopPanel(history);
        }
        vgStudyCanvasFunctionPanel = createVgStudyCanvasFunctionPanel(history);

        if (localizationStatus) {
            vgStudyLocalizationPanel = createVgStudyLocalizationPanel(history);
        }
        vgStudyDumyPanel = createVgStudyDumyPanel();// add
        contentPane.add(appMenuBar, "0,0,1,0");
        contentPane.add(canvas, "0,1");
        contentPane.add(vgStudyLayoutPanel, "1,1");

        vgStudyLayoutPanel.add(vgStudyMainPanel, "0,1");
        vgStudyMainPanel.add(vgStudyRunPanel, "0,0");

        // studyselectextendedStatus
        // notesStatus
        // cineLoopStatus
        // localizationStatus

        // 0000
        if (!studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,6");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,8");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,13");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,12");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0001
        if (!studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,6");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,8");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,13");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0010
        if (!studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,8");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,13");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0011
        if (!studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,8");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0100
        if (!studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0101
        if (!studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0110
        if (!studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 0111
        if (!studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && localizationStatus) {
            //vgStudyMainPanel.add(vgStudySelectExtendedPanel,"0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1000
        if (studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1001
        if (studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1010
        if (studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1011
        if (studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            //vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1100
        if (studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1101
        if (studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            //vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1110
        if (studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,0");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            //vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }
        // 1111
        if (studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && localizationStatus) {
            vgStudyMainPanel.add(vgStudySelectExtendedPanel, "0,2");
            vgStudyMainPanel.add(vgStudyTaskPanel, "0,4");
            vgStudyMainPanel.add(vgStudySelectPanel, "0,6");
            vgStudyMainPanel.add(vgStudyClarificationPanel, "0,8");
            vgStudyMainPanel.add(vgStudyNotesPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCineLoopPanel, "0,12");
            //vgStudyMainPanel.add(vgStudyDumyPanel, "0,10");
            vgStudyMainPanel.add(vgStudyCanvasFunctionPanel, "0,14");
            vgStudyMainPanel.add(vgStudyLocalizationPanel, "0,16");
            vgStudyMainPanel.add(vgStudyDumyPanel, "0,17");
        }

        allowClosing();
        setStudyInfoAppMainFrame(history);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        Point p = this.getLocationOnScreen();

        this.setSize(d);
        this.setLocation((int) p.getX(), (int) p.getY());
        this.setVisible(true);
    }

    /**
     * Create the vgStudyLayoutPanel.
     * This panel positions the vgStudyMainPanel in the
     * vertical direction.
     */
    public JPanel createVgStudyLayoutPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        String key;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // alignment top
        key = "runpanel.space.top";
        int alignTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignTop == 0) {
            alignTop = 0;
        }

        // color
        int[] color = new int[3];
        key = "controlpanel.color";
        color = appPropertyUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        double size[][] = {{f}, {alignTop, f, p}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        //panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(color[0], color[1], color[2]));
        return panel;
    }

    /**
     * Create the vgStudyMainPanel
     * This is the main container for the study controls.
     */
    public JPanel createVgStudyMainPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        String key;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();

        /*
        // studyselectextended
        boolean studyselectextendedStatus = false;
        key = "studyselectextended";
        String studyselectextended = appPropertyUtils.getPropertyStringValue(prop, key);
        if (studyselectextended.equalsIgnoreCase("Yes") || studyselectextended.equalsIgnoreCase("Y")) {
            studyselectextendedStatus = true;
        }*/
        
         // studyselectextended
        boolean studyselectextendedStatus = false;
        key = "studyselectextended";
        String studyselectextended = appPropertyUtils.getPropertyStringValue(prop, key);
        if (appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST &&
                (studyselectextended.equalsIgnoreCase("Yes") || studyselectextended.equalsIgnoreCase("Y"))) {
            studyselectextendedStatus = true;
        }

        // Added 20160512 test
        // Removed 20110611
        /*
        if (appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST
                || appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.SHOW_EXIST) {
            studyselectextendedStatus = true;
        }*/

        // notes
        boolean notesStatus = false;
        key = "notespanel";
        String notes = appPropertyUtils.getPropertyStringValue(prop, key);
        if (notes.equalsIgnoreCase("Yes")
                || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // cineLoopStatus
        boolean cineLoopStatus = false;
        key = "cineloop";
        String cineLoop = appPropertyUtils.getPropertyStringValue(prop, key);
        if (cineLoop.equalsIgnoreCase("Yes") || cineLoop.equalsIgnoreCase("Y")) {
            cineLoopStatus = true;
        }

        // localization
        /*
        boolean localizationStatus = false;
        key = "localization";
        String localization = propUtils.getPropertyStringValue(prop, key);
        if(localization.equalsIgnoreCase("Yes") ||
        localization.equalsIgnoreCase("Y"))
        localizationStatus = true;
         */

        boolean localizationStatus = false;
        if (appMainAdmin.viewDex.vgTaskPanelUtility.getTaskPanelLocalizationStatusExist()) {
            localizationStatus = true;
        }

        // panel color
        int[] mainPanelColor = new int[3];
        key = "mainpanel.color";
        mainPanelColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (mainPanelColor[0] == 0 && mainPanelColor[1] == 0 && mainPanelColor[2] == 0) {
            mainPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            mainPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            mainPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }


        // run
        // NOT IN USE
        key = "runpanel.space.top";
        int runPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (runPanelTop == 0) {
            runPanelTop = 0;
        }

        // selectextended
        key = "studyselectextendedpanel.space.top";
        int studySelectExtendedPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (studySelectExtendedPanelTop == 0) {
            studySelectExtendedPanelTop = 5;
        }

        // taskpanel vertical size
        // Default value for screen resolution 1600x1200 (200)
        // Default value for screen resolution 2048x1536 (400)
        // Default value for screen resolution 1280x1024 (400)
        // Default value for screen resolution 1920x1200 (230)
        // Default value for screen resolution 2048x1800 (xxx)
        // Default value for screen resolution 1440x900 (75)
        // Default value for screen resolution 2880x1800 (70)

        int defValue = 400;

        if (d.width == 1600 && d.height == 1200) {
            defValue = 200;
            if (studyselectextendedStatus == true) {
                defValue = 150;
            }
        } else if (d.width == 2048 && d.height == 1536) {
            defValue = 400;
        } else if (d.width == 1280 && d.height == 1024) {
            defValue = 400;
        } else if (d.width == 1920 && d.height == 1200) {
            defValue = 230;
            if (studyselectextendedStatus == true) {
                defValue = 150;
            }
        }
        if (d.width == 1440 && d.height == 900) {
            defValue = 75;
            if (studyselectextendedStatus == true) {
                defValue = 65;
            }
        }
        if (d.width == 2048 && d.height == 1800) {
            defValue = 230;
            if (studyselectextendedStatus == true) {
                defValue = 150;
            }
        }
        if (d.width == 2880 && d.height == 1800) {
            defValue = 70;
            if (studyselectextendedStatus == true) {
                defValue = 70;
            }
        }

        key = "taskpanel.vertical.size";
        int taskPanelVerticalSize = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (taskPanelVerticalSize == 0) {
            taskPanelVerticalSize = defValue;
        }

        // taskpanel space
        key = "taskpanel.space.top";
        int taskPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (taskPanelTop == 0) {
            taskPanelTop = 5;
        }

        // select
        key = "studyselectpanel.space.top";
        int studySelectPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (studySelectPanelTop == 0) {
            studySelectPanelTop = 3;
        }

        // clarification
        key = "taskpanel.clarificationpanel.space.top";
        int clarificationPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (clarificationPanelTop == 0) {
            clarificationPanelTop = 5;
        }

        // notes
        key = "notespanel.space.top";
        int notesPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (notesPanelTop == 0) {
            notesPanelTop = 5;
        }

        // cineloop
        key = "cineloop.space.top";
        int cineLoopPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (cineLoopPanelTop == 0) {
            cineLoopPanelTop = 5;
        }

        // localization
        key = "localization.space.top";
        int localizationPanelTop = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (localizationPanelTop == 0) {
            localizationPanelTop = 0;
        }

        // RunPanel Don't have any space panel
        // StudySelectExtendedPanel    Optional (property)
        // Taskpanel    vertical.size = 400 (default)
        // StudySelectPanel
        // ClarificationPanel    vertical.size = 190 (default)
        // NotesPanel  Optional (property)
        // Cine-LoopPanel/CanvasFunctionPanel  Optional (property)
        // LocalizationPanel    Optional (TaskPanel property)

        // Create the spaces for the panel, dependent of the
        // settings of the properties
        double size[][] = new double[2][18];

        /*
        size[0][0] = f;
        size[1][0] = p;  //RunPanel
        size[1][1] = 0;  //studySelectExtendedPanelTop
        size[1][2] = p;  //StudySelectExtendedPanel
        size[1][3] = 0;  //taskPanelTop
        size[1][4] = p;  //TaskPanel
        size[1][5] = 0;  //studySelectPanelTop
        size[1][6] = p;  //StudySelectPanel
        size[1][7] = 0;  //clarificationPanelTop;
        size[1][8] = p;  //ClarificationPanel
        size[1][9] = 0;  //notesPanelTop
        size[1][10] = p;  //NotesPanel
        size[1][11] = 0;  //cineLoopPanelTop
        size[1][12] = p;  //CineLoopPanel
        size[1][13] = 0;  //canvasFunctionPanelTop
        size[1][14] = p;  //CanvasFunctionPanel
        size[1][15] = 0;  //localizationPanelTop
        size[1][16] = p;  //LocalizationPanel
        size[1][17] = 0;  //dummy
         */

        // 0000
        if (!studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = f;  //DummyPanel
        }
        // 0001
        if (!studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel

        }
        // 0010
        if (!studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel

        }
        // 0011
        if (!studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 0100
        if (!studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 0101
        if (!studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 0110
        if (!studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 0111
        if (!studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = 0;  //studySelectExtendedPanelTop
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1000
        if (studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1001
        if (studyselectextendedStatus
                && !notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1010
        if (studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0; //localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1011
        if (studyselectextendedStatus
                && !notesStatus
                && cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = 0;  //notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1100
        if (studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel
            size[1][17] = p;  //DummyPanel
        }
        // 1101
        if (studyselectextendedStatus
                && notesStatus
                && !cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = 0;  //cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel;
            size[1][17] = p;  //DummyPanel
        }
        // 1110
        if (studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && !localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            //size[1][4] = p;  //TaskPanel
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = 0;  //localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel;
            size[1][17] = p;  //DummyPanel
        }
        // 1111
        if (studyselectextendedStatus
                && notesStatus
                && cineLoopStatus
                && localizationStatus) {
            size[0][0] = f;
            size[1][0] = p;  //RunPanel
            size[1][1] = studySelectExtendedPanelTop;
            size[1][2] = p;  //StudySelectExtendedPanel
            size[1][3] = taskPanelTop;
            size[1][4] = taskPanelVerticalSize;  //TaskPanel
            size[1][5] = studySelectPanelTop;
            size[1][6] = p;  //StudySelectPanel
            size[1][7] = clarificationPanelTop;
            size[1][8] = p;  //ClarificationPanel
            size[1][9] = notesPanelTop;
            size[1][10] = p;  //NotesPanel
            size[1][11] = cineLoopPanelTop;
            size[1][12] = p;  //CineLoopPanel
            size[1][13] = 0;  //canvasFunctionPanelTop
            size[1][14] = p;  //CanvasFunctionPanel
            size[1][15] = localizationPanelTop;
            size[1][16] = p;  //LocalizationPanel;
            size[1][17] = p;  //DummyPanel
        }

        /*
        if(studyselectextendedStatus){
        if(!cineLoopStatus){
        space[0] = studySelectExtendedPanelTop;
        space[1] = taskPanelTop;
        space[2] = studySelectPanelTop;
        space[3] = clarificationPanelTop;
        space[4] = 0;
        if(localizationStatus)
        space[4] = localizationPanelTop;
        }
        else{
        space[0] = studySelectExtendedPanelTop;
        space[1] = taskPanelTop;
        space[2] = studySelectPanelTop;
        space[3] = clarificationPanelTop;
        space[4] = cineLoopPanelTop;
        space[5] = 0;
        if(localizationStatus)
        space[6] = localizationPanelTop;
        }
        }
        else{
        if(!cineLoopStatus){
        space[0] = taskPanelTop;
        space[1] = studySelectPanelTop;
        space[2] = clarificationPanelTop;
        space[3] = 0;
        if(localizationStatus)
        space[4] = localizationPanelTop;
        }
        else{
        space[0] = taskPanelTop;
        space[1] = studySelectPanelTop;
        space[2] = clarificationPanelTop;
        space[3] = cineLoopPanelTop;
        space[4] = 0;
        if(localizationStatus)
        space[5] = localizationPanelTop;
        }
        }
         */

        //double size[][] = {{f},{p,space[0],p,space[1],p,space[2],p,space[3],p,space[4],p,space[5],p,space[6],p}};
        JPanel panel = new JPanel();
        panel.setBackground(new Color(mainPanelColor[0], mainPanelColor[1], mainPanelColor[2]));
        //panel.setBackground(new Color(255,0,0));
        panel.setLayout(new TableLayout(size));

        return panel;
    }

    /**
     * Create the vgFunctionPanelLocalization.
     */
    public JPanel createVgStudyRunPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        vgRunPanel = new VgRunPanel(this, history);
        mainPanel.add(vgRunPanel, "0,0");
        return mainPanel;
    }

    /**
     * Create the vgStudyRunPanel
     * NOT IN USE
     */
    public JPanel createVgStudyRunPanel2(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        String key;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // color
        key = "runpanel.color";
        int[] runPanelColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (runPanelColor[0] == 0
                && runPanelColor[1] == 0
                && runPanelColor[2] == 0) {
            runPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            runPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            runPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // border color
        key = "runpanel.title.color";
        int[] titleBorderColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (titleBorderColor[0] == 0 && titleBorderColor[1] == 0
                && titleBorderColor[2] == 0) {
            titleBorderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            titleBorderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            titleBorderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // border
        Border tborder = BorderFactory.createLineBorder(new Color(
                titleBorderColor[0], titleBorderColor[1], titleBorderColor[2]));

        // panel
        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        mainPanel.setBackground(new Color(
                runPanelColor[0],
                runPanelColor[1],
                runPanelColor[2]));

        // panel
        double sizeb[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(sizeb));
        panel.setBackground(new Color(
                runPanelColor[0],
                runPanelColor[1],
                runPanelColor[2]));
        panel.setBorder(tborder);

        vgRunPanel = new VgRunPanel(this, history);
        panel.add(vgRunPanel, "0,0");

        mainPanel.add(panel, "0,0");
        return mainPanel;
    }

    /**
     * Create the vgStudyTaskPanel.
     * 2015-03-30, Original
     * NOT USED
     */
    public JPanel createVgStudyTaskPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        String key;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        key = "taskpanel.color";
        int[] color = appPropertyUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // border color
        key = "taskpanel.border.color";
        int[] borderColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // alignment left
        key = "taskpanel.alignment.left";
        int alignLeft = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0) {
            alignLeft = 5;
        }

        // alignment right
        key = "taskpanel.alignment.right";
        int alignRight = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0) {
            alignRight = 5;
        }

        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0],
                borderColor[1], borderColor[2]));

        //double size[][] = {{alignLeft,f,alignRight},{5,f,5}};
        double size[][] = {{0, f, 0}, {0, f, 0}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        mainPanel.setBackground(new Color(color[0], color[1], color[2]));
        //mainPanel.setBackground(new Color(200,0,0));

        double sizeb[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(sizeb));
        panel.setBackground(new Color(color[0], color[1], color[2]));
        //panel.setBorder(lborder);

        vgTaskMainPanel = new VgTaskPanel(this, appProperty, history);
        vgTaskMainPanel.setBackground(new Color(color[0], color[1], color[2]));
        //vgTaskMainPanel.setBackground(new Color(200,0,0));

        // scrollPane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(new Color(color[0], color[1], color[2]));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        //scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));
        //Border empty = BorderFactory.createEmptyBorder();
        Border empty = BorderFactory.createEmptyBorder();
        scrollPane.setBorder(empty);
        //scrollPane.setAutoscrolls(false);
        panel.add(vgTaskMainPanel, "0,0");
        mainPanel.add(panel, "1,1");
        //return mainPanel;

        scrollPane.setViewportView(mainPanel);

        double size2[][] = {{alignLeft, f, alignRight}, {5, f, 5}};
        JPanel panelX = new JPanel();
        panelX.setLayout(new TableLayout(size2));
        panelX.setBackground(new Color(color[0], color[1], color[2]));
        panelX.add(scrollPane, "1,1");

        return panelX;
    }

    /**
     * Create the vgStudyTaskPanel.
     */
    public JPanel createVgStudyTaskPanel2(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        String key;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        key = "taskpanel.color";
        int[] color = appPropertyUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // border color
        key = "taskpanel.border.color";
        int[] borderColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // alignment left
        key = "taskpanel.alignment.left";
        int alignLeft = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0) {
            alignLeft = 5;
        }

        // alignment right
        key = "taskpanel.alignment.right";
        int alignRight = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0) {
            alignRight = 5;
        }

        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0],
                borderColor[1], borderColor[2]));

        //double size[][] = {{alignLeft,f,alignRight},{5,f,5}};
        double size[][] = {{0, f, 0}, {0, f, 0}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        mainPanel.setBackground(new Color(color[0], color[1], color[2]));
        //mainPanel.setBackground(new Color(200,0,0));

        double sizeb[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(sizeb));
        panel.setBackground(new Color(color[0], color[1], color[2]));
        //panel.setBorder(lborder);

        vgTaskMainPanel = new VgTaskPanel(this, appProperty, history);
        vgTaskMainPanel.setBackground(new Color(color[0], color[1], color[2]));
        //vgTaskMainPanel.setBackground(new Color(200,0,0));

        // scrollPane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(new Color(color[0], color[1], color[2]));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        //scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));
        //Border empty = BorderFactory.createEmptyBorder();
        Border empty = BorderFactory.createEmptyBorder();
        scrollPane.setBorder(empty);
        //scrollPane.setAutoscrolls(false);

        //panel.add(vgTaskMainPanel,"0,0");
        //mainPanel.add(panel, "1,1");
        //return mainPanel;

        //scrollPane.setViewportView(mainPanel);

        double size2[][] = {{alignLeft, f, alignRight}, {5, f, 5}};
        JPanel panelX = new JPanel();
        panelX.setLayout(new TableLayout(size2));
        panelX.setBackground(new Color(color[0], color[1], color[2]));
        panelX.add(scrollPane, "1,1");
        //return panelX;

        return vgTaskMainPanel;
    }

    /**
     * Create the vgStudySelectPanel.
     */
    public JPanel createVgStudySelectPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        vgStudyDicomSelectControl = new VgStudyNextCasePanel(this, history);
        mainPanel.add(vgStudyDicomSelectControl, "0,0");
        return mainPanel;
    }

    /**
     * Create the vgStudySelectExtendedPanel.
     */
    public JPanel createVgStudySelectExtendedPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        vgStudyNextCaseExtendedControl = new VgStudyNextCaseExtendedPanel(this, history);
        mainPanel.add(vgStudyNextCaseExtendedControl, "0,0");
        return mainPanel;
    }

    /**
     * Create the vgStudyClarificationPanel.
     */
    public JPanel createVgStudyClarificationPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{0, f, 0}, {0, f, 0}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        //mainPanel.setBackground(Color.RED);
        //mainPanel.setMaximumSize(new Dimension(50,50));

        double sizeb[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        //panel.setMaximumSize(new Dimension(50,50));
        panel.setLayout(new TableLayout(sizeb));

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "taskpanel.clarificationpanel.color";
        int[] taskPanelClarificationPanelColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (taskPanelClarificationPanelColor[0] == 0
                && taskPanelClarificationPanelColor[1] == 0
                && taskPanelClarificationPanelColor[2] == 0) {
            taskPanelClarificationPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            taskPanelClarificationPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            taskPanelClarificationPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        JPanel vgClarificationPanel = new VgClarificationPanel(this, appProperty, history);

        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBackground(Color.RED);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));

        //scrollPane.setViewportView(vgClarificationPanel);

        //panel.add(vgClarificationPanel, "0,0");
        mainPanel.add(vgClarificationPanel, "1,1");

        //*********************
        //Border border = BorderFactory.createLineBorder(new Color(150,150,150));
        //TitledBorder tborder = BorderFactory.createTitledBorder("");

        // This is the place to put a vertical max size eg.
        //double sizeX[][] = {{1,f,1}, {1,150,1}};

        // vertical offset
        key = "taskpanel.clarificationpanel.vertical.size";
        int verticalSize = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (verticalSize == 0) {
            verticalSize = 200;
        }

        // alignment
        key = "taskpanel.clarificationpanel.alignment.left";
        int alignLeft = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0) {
            alignLeft = 1;
        }

        // alignment
        key = "taskpanel.clarificationpanel.alignment.right";
        int alignRight = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0) {
            alignRight = 1;
        }

        double sizeX[][] = new double[2][3];
        if (verticalSize == 0) {
            sizeX[0][0] = 2;
            sizeX[0][1] = TableLayout.FILL;
            sizeX[0][2] = 2;

            sizeX[1][0] = alignLeft;
            sizeX[1][1] = TableLayout.FILL;
            sizeX[1][2] = alignRight;
        } else {
            sizeX[0][0] = alignLeft;
            sizeX[0][1] = TableLayout.FILL;
            sizeX[0][2] = alignRight;

            sizeX[1][0] = 1;
            sizeX[1][1] = verticalSize;
            sizeX[1][2] = 1;
        }

        JPanel panelX = new JPanel();
        panelX.setLayout(new TableLayout(sizeX));
        panelX.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));

        JPanel panelY = new JPanel();
        panelY.setLayout(new TableLayout(sizeX));
        panelY.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        //panelY.setBackground(Color.YELLOW);
        //panelY.setBorder(tborder);

        JPanel panelZ = new JPanel();
        panelZ.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        //panelZ.setBackground(Color.MAGENTA);
        panelZ.setLayout(new TableLayout(sizeX));

        panelX.add(panelY, "1,1");
        panelY.add(panelZ, "1,1");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));

        scrollPane.setViewportView(mainPanel);

        // test delete
        //Rectangle rec = mainPanel.getBounds();
        //Rectangle rec = new Rectangle(10,10);
        //.scrollRectToVisible(rec);

        panelZ.add(scrollPane, "1,1");
        //scrollPane.setViewportView(panelx);
        //*******************
        return panelZ;
    }

    /**
     * Create the vgStudyNotesPanel
     */
    public JPanel createVgStudyNotesPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{0, f, 0}, {0, f, 0}};
        JPanel vgNotesMainPanel = new JPanel();
        vgNotesMainPanel.setLayout(new TableLayout(size));
        vgNotesMainPanel.setBackground(Color.RED);
        //mainPanel.setMaximumSize(new Dimension(50,50));

        double sizeb[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        //panel.setMaximumSize(new Dimension(50,50));
        panel.setLayout(new TableLayout(sizeb));

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "notespanel.color";
        int[] taskPanelClarificationPanelColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (taskPanelClarificationPanelColor[0] == 0
                && taskPanelClarificationPanelColor[1] == 0
                && taskPanelClarificationPanelColor[2] == 0) {
            taskPanelClarificationPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            taskPanelClarificationPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            taskPanelClarificationPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        vgNotesPanel = new VgNotesPanel(this, appProperty, history);

        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBackground(Color.RED);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));

        //scrollPane.setViewportView(vgClarificationPanel);

        //panel.add(vgClarificationPanel, "0,0");
        vgNotesMainPanel.add(vgNotesPanel, "1,1");

        //*********************
        //Border border = BorderFactory.createLineBorder(new Color(150,150,150));
        //TitledBorder tborder = BorderFactory.createTitledBorder("");

        // This is the place to put a vertical max size eg.
        //double sizeX[][] = {{1,f,1}, {1,150,1}};

        // vertical offset
        key = "notespanel.vertical.size";
        int verticalSize = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (verticalSize == 0) {
            verticalSize = 80;
        }

        // alignment
        key = "notespanel.alignment.left";
        int alignLeft = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0) {
            alignLeft = 1;
        }

        // alignment
        key = "notespanel.alignment.right";
        int alignRight = appPropertyUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0) {
            alignRight = 1;
        }

        double sizeX[][] = new double[2][3];
        if (verticalSize == 0) {
            sizeX[0][0] = 2;
            sizeX[0][1] = TableLayout.FILL;
            sizeX[0][2] = 2;

            sizeX[1][0] = alignLeft;
            sizeX[1][1] = TableLayout.FILL;
            sizeX[1][2] = alignRight;
        } else {
            sizeX[0][0] = alignLeft;
            sizeX[0][1] = TableLayout.FILL;
            sizeX[0][2] = alignRight;

            sizeX[1][0] = 1;
            sizeX[1][1] = verticalSize;
            sizeX[1][2] = 1;
        }

        JPanel panelX = new JPanel();
        panelX.setLayout(new TableLayout(sizeX));
        panelX.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));

        JPanel panelY = new JPanel();
        panelY.setLayout(new TableLayout(sizeX));
        panelY.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        //panelY.setBackground(Color.YELLOW);
        //panelY.setBorder(tborder);

        JPanel panelZ = new JPanel();
        panelZ.setBackground(
                new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        //panelZ.setBackground(Color.MAGENTA);
        panelZ.setLayout(new TableLayout(sizeX));

        panelX.add(panelY, "1,1");
        panelY.add(panelZ, "1,1");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(new Color(taskPanelClarificationPanelColor[0],
                taskPanelClarificationPanelColor[1],
                taskPanelClarificationPanelColor[2]));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));

        scrollPane.setViewportView(vgNotesMainPanel);

        // test delete
        //Rectangle rec = mainPanel.getBounds();
        //Rectangle rec = new Rectangle(10,10);
        //.scrollRectToVisible(rec);

        panelZ.add(scrollPane, "1,1");
        //scrollPane.setViewportView(panelx);
        //*******************
        return panelZ;
    }

    /**
     * Create the vgStudyCanvasFunctionPanel.
     */
    public JPanel createVgStudyCanvasFunctionPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // mainPanel
        // panel color
        String key = "functionpanel.panel.color";
        int[] functionPanelPanelColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (functionPanelPanelColor[0] == 0
                && functionPanelPanelColor[1] == 0
                && functionPanelPanelColor[2] == 0) {
            functionPanelPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            functionPanelPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            functionPanelPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        double size[][] = {{3, f, 3}, {5, p, 5}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        mainPanel.setBackground(
                new Color(functionPanelPanelColor[0],
                functionPanelPanelColor[1],
                functionPanelPanelColor[2]));

        // panel
        double sizeb[][] = {{f}, {p}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(sizeb));

        //panel.setBackground(Color.GREEN);
        //panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //TitledBorder tborder = BorderFactory.createTitledBorder("Window/Level");
        //tborder.setTitleJustification(TitledBorder.LEFT);
        //tborder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        //panel.setBorder(tborder);

        vgFunctionPanel = new VgFunctionPanel(this, appProperty, history);

        panel.add(vgFunctionPanel, "0,0");
        mainPanel.add(panel, "1,1");
        return mainPanel;
    }

    /**
     * Create the vgFunctionPanelLocalization.
     */
    public JPanel createVgStudyLocalizationPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        vgLocalizationPanel = new VgLocalizationPanel(this, history);
        mainPanel.add(vgLocalizationPanel, "0,0");
        return mainPanel;
    }

    /**
     * Create the createVgStudyCineLoopPanel
     */
    public JPanel createVgStudyCineLoopPanel(VgHistory history) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new TableLayout(size));
        vgCineLoopPanel = new VgCineLoopPanel(this, history);
        mainPanel.add(vgCineLoopPanel, "0,0");
        return mainPanel;
    }

    /**
     * Create the vgStudyDumyPanel.
     */
    public JPanel createVgStudyDumyPanel() {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {f}};
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.setBackground(Color.GREEN);
        return panel;
    }

    /**
     * Create the imagecanvas context menus.
     */
    protected void createVgImageCanvasContextMenu(VgHistory history) {
        canvasContextMenu = new CanvasContextMenu(this, history, pan, zoom, scrollStack, localization, distance);
        canvas.addMouseListener(canvasContextMenu);
        //addKeyListener(pzwl);

        // add the mouse listener to the CatWindowLevelGUI class after the
        // mouse listener is added to the CatPanZoomWindowLevel class. This is
        // for correct behaveier of the canvas context menu.
        //canvas.addMouseListener(catWindowLevelGUI);
    }

    /*
     * Set some general- and image information text on the main
     * application window. This is only for testing purposes. 
     * These properties are not read from the history file. A
     * modification of this property affect the study each time
     * it is run.
     */
    private void setStudyInfoAppMainFrame(VgHistory history) {
        String key;

        // status
        boolean infoStatus = false;
        key = "mainpanel.studyinfo";
        String str = appPropertyUtils.getPropertyStringValue(appProperty.getStudyProperties(), key);
        if (str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y")) {
            infoStatus = true;
        }

        // menu font
        key = "mainpanel.studyinfo.font";
        String fontValue = appPropertyUtils.getPropertyFontValue(appProperty.getStudyProperties(), key);
        if (fontValue.equals("")) {
            fontValue = "SansSerif-plain-12";
        }

        // menu font color
        int[] fontColor = new int[3];
        key = "mainpanel.studyinfo.color";
        fontColor = appPropertyUtils.getPropertyColorValue(appProperty.getStudyProperties(), key);
        if (fontColor[0] == 0 && fontColor[1] == 0 && fontColor[2] == 0) {
            fontColor[0] = 0;
            fontColor[1] = 0;
            fontColor[2] = 0;
        }

        // Set the font ... how ???
        //Container contentPane = getContentPane();
        //contentPane.setFont(Font.decode(fontValue)); 

        setStudyInfoAppMainFrameStatus(infoStatus);
    }

    /*
     * Remove the vgPanels.
     */
    private void removeVgPanels() {
        if (vgStudyCanvasFunctionPanel != null) {
            vgStudyCanvasFunctionPanel.removeAll();
            vgStudyCanvasFunctionPanel = null;
        }

        if (vgStudyClarificationPanel != null) {
            vgStudyClarificationPanel.removeAll();
            vgStudyClarificationPanel = null;
        }

        if (vgStudyNotesPanel != null) {
            vgStudyNotesPanel.removeAll();
            vgStudyNotesPanel = null;
        }

        if (vgStudySelectPanel != null) {
            vgStudySelectPanel.removeAll();
            vgStudySelectPanel = null;
        }

        if (vgStudySelectExtendedPanel != null) {
            vgStudySelectExtendedPanel.removeAll();
            vgStudySelectExtendedPanel = null;
        }

        if (vgStudyTaskPanel != null) {
            vgStudyTaskPanel.removeAll();
            vgStudyTaskPanel = null;
        }

        if (vgStudyRunPanel != null) {
            vgStudyRunPanel.removeAll();
            vgStudyRunPanel = null;
        }

        if (vgStudyLocalizationPanel != null) {
            vgStudyLocalizationPanel.removeAll();
            vgStudyLocalizationPanel = null;
        }

        if (vgStudyCineLoopPanel != null) {
            vgStudyCineLoopPanel.removeAll();
            vgStudyCineLoopPanel = null;
        }

        if (vgStudyMainPanel != null) {
            vgStudyMainPanel.removeAll();
            vgStudyMainPanel = null;
        }

        if (vgStudyDumyPanel != null) {
            vgStudyDumyPanel.removeAll();
            vgStudyDumyPanel = null;
        }
    }

    /*******************************************************
     *
     *              end VgStudy
     *
     *******************************************************/
    /********************************************************
     * 
     * Canvas
     * 
     *******************************************************/
    /**
     * Create the image canvas and add some image manipulation
     * and other functions.
     */
    public void createCanvas() {
        canvas = new ImageCanvas(this);
        createPan();
        createScrollStack();
        createZoom();
        createGeomManip();
        createWindowLevel();
        createLocalization();
        createDistance();
        createPixelValueMean();
        createVolume();
        canvasControl = new CanvasControl(this);
        //canvas.addFocusListener(canvas);
        //canvas.setForeground(new Color(0,0,0));
    }

    /* Create the pan (pan). */
    protected void createPan() {
        pan = new Pan(this, (ImageCanvasInterface) canvas);
        panGUI = new PanGUI((PanInterface) pan);
        canvas.addMouseListener(panGUI);
        canvas.addMouseMotionListener(panGUI);
        addKeyListener(panGUI);
        //addFocusListener(panGUI);
    }

    /**
     * Create the scrollStack class. This class control
     * the scrolling of the image stack.
     */
    protected void createScrollStack() {
        scrollStack = new ScrollStack(this, (ImageCanvasInterface) canvas);
        scrollStackGUI = new ScrollStackGUI((ScrollStackInterface) scrollStack);
        canvas.addMouseListener(scrollStackGUI);
        canvas.addMouseWheelListener(scrollStackGUI);
        canvas.addMouseMotionListener(scrollStackGUI);
        addKeyListener(scrollStackGUI);
    }

    /* Create the Zoom. */
    protected void createZoom() {
        zoom = new Zoom(this, (ImageCanvasInterface) canvas);
        zoomGUI = new ZoomGUI((ZoomInterface) zoom, (ImageCanvasInterface) canvas);
        //catZoomGUI.setCanvasControlMode(2);  // default
        canvas.addMouseListener(zoomGUI);
        canvas.addMouseWheelListener(zoomGUI);
        addKeyListener(zoomGUI);
    }

    /* Create the GeomManip. */
    protected void createGeomManip() {
        geomManip = new GeomManip(this, (ImageCanvasInterface) canvas);
    }

    /* Create the windowLevel */
    protected void createWindowLevel() {
        windowLevel = new WindowLevel(this, canvas);
        windowLevelGUI = new WindowLevelGUI(canvas, windowLevel);
        canvas.addMouseListener(windowLevelGUI);
        canvas.addMouseMotionListener(windowLevelGUI);
    }

    /**
     * Create the localization.
     */
    protected void createLocalization() {
        localization = new Localization(this, (ImageCanvasInterface) canvas);
        LocalizationGUI localizationGUI = new LocalizationGUI((LocalizationInterface) localization);
        canvas.addMouseListener(localizationGUI);
        canvas.addMouseMotionListener(localizationGUI);
        addKeyListener(localizationGUI);
    }

    /**
     * Create the distance.
     */
    protected void createDistance() {
        distance = new Distance(this, (ImageCanvasInterface) canvas);
        distanceMeasurement = new DistanceMeasurement(this);
        DistanceGUI distanceGUI = new DistanceGUI(distance);
        canvas.addMouseListener(distanceGUI);
        canvas.addMouseMotionListener(distanceGUI);
        addKeyListener(distanceGUI);
    }

    /**
     * Create pixelvalue.
     */
    protected void createPixelValueMean() {
        pixelValueMean = new PixelValueMean(this, (ImageCanvasInterface) canvas);
        pixelValueMeanMeasurement = new PixelValueMeanMeasurement(this);
        PixelValueMeanGUI pixelvalueMeanGUI = new PixelValueMeanGUI(pixelValueMean);
        canvas.addMouseListener(pixelvalueMeanGUI);
        canvas.addMouseMotionListener(pixelvalueMeanGUI);
        addKeyListener(pixelvalueMeanGUI);
    }

    /**
     * Create the shapemaker.
     */
    protected void createVolume() {
        area = new Area(this, (ImageCanvasInterface) canvas);
        areaMeasurement = new AreaMeasurement(this);
        AreaGUI areaGUI = new AreaGUI(area);
        //vgShapeMakerControl = new VgShapeMakerControl(appMainAdmin.vgControl);
        canvas.addMouseListener(areaGUI);
        canvas.addMouseMotionListener(areaGUI);
        addKeyListener(areaGUI);
    }

    /**********************************************************
     *
     *         end
     *
     **********************************************************/
    /****************************************************
     *
     *    Create the studyControl panels
     *
     ***************************************************/
    /****************************************************
     *
     *                      end
     *
     ***************************************************/
    /**
     * Create the User Interface.
     * NOT IN USE!
     */
    private void createUI6() {
        Container contentPane = getContentPane();
        //double size[][] = {{TableLayout.FILL, 250}, {25, TableLayout.FILL}};
        double size[][] = {{TableLayout.FILL}, {25, TableLayout.FILL}};
        TableLayout layout = new TableLayout(size);
        contentPane.setLayout(layout);

        //createAppMenuBar(contentPane);
        //createCommandPanel(contentPane, layout);
        //createVGAPanel();

        //createBrandingPanel();
        //createStatusPanel();   // The lowest panel
        createCanvas5(contentPane, layout);
        //createCommandPanel(contentPane, layout);
        //createTestSubPanel();
        //createControlTabedPane();
        //createFileBrowser();
        //createImageLoaderAndSaver();
        //createImageLoaderListener();
        //createTestPanel();

        //addTestModules();
        // tmpfix
        //createAdminPanel(commandPanel);
        //createControlTabedPane();
        //createStudySubPanel();
        //createStudyPanel();

        allowClosing();
        //this.setBackground(Color.black);    no effect
        this.setLocation(0, 0);
        this.setSize(1280, 1015);
        this.show();
        //this.repaint();
    }

    /**
     * Create the Canvas
     */
    public void createCanvas5(Container pane, TableLayout layout) {
        canvas = new ImageCanvas(this);
        canvas.setBackground(Color.green);
        //canvas.setForeground(Color.white);
        pane.add(canvas, "0,1");
    }

    /**
     * Create the commandPanel.
     * NOT IN USE
     */
    public void createCommandPanel5(Container pane, TableLayout layout) {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        double size[][] = {{f}, {65, 5, f, 5, 50}};
        //vgStudyMainPanel = new JPanel();
        //vgStudyMainPanel.setBackground(Color.RED);
        //commandPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        //Border black = BorderFactory.createLineBorder(Color.black);
        //Border greyLine = BorderFactory.createLineBorder(new Color(204,204,204));
        //Border blackLine = BorderFactory.createLineBorder(Color.gray);
        //Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        //Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        /*
        blackLine = BorderFactory.createLineBorder(Color.gray);
        greyLine = BorderFactory.createLineBorder(new Color(204,204,204));
        empty = BorderFactory.createEmptyBorder();
        blackSelected = BorderFactory.createMatteBorder(2,2,2,2,Color.gray);
         */
        //commandPanel.setBorder(raisedbevel);
        //commandPanel.setBackground(new java.awt.Color(0,255,0));
        //commandPanel.setPreferredSize(new Dimension(256,900));
        //commandPanel.setMinimumSize(new Dimension(256,900));
        //vgStudyMainPanel.setLayout(new TableLayout(size));
        //pane.add(commandPanel, "1,1");
        //commandPanel.revalidate();
    }

    /** NOT IN USE */
    public void createCommandPanelDialog() {
        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        //int resolution = toolkit.getScreenResolution();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        //System.out.println("xScale: " + xScale);
        //System.out.println("yScale: " + yScale);


        //int commandXLocation = Math.round(258 * xScale);
        //int commandYLocation = Math.round(52 * yScale);
        int commandXLocation = Math.round(335 * xScale);
        int commandYLocation = Math.round(40 * yScale);

        commandPanelDialog = new AppCommandPanelDialog(this, false);

        commandPanelDialog.setResizable(true);
        commandPanelDialog.setUndecorated(true);
        double maxX = this.getBounds().getMaxX();
        double minY = this.getBounds().getMinY();
        //commandPanelDialog.setLocation(((int)maxX - commandXLocation), ((int)minY + commandYLocation));
        //commandPanelDialog.setLocation(600, 100);
        //commandPanelDialog.pack();
        commandPanelDialogSize = commandPanelDialog.getSize();
        //commandPanelDialog.setLocation(((int)maxX - commandPanelDialogSize.width), ((int)minY + commandYLocation));


        //System.out.println("CatMain:createCommandPanelDialog: width: " +  commandPanelDialogSize.width);
        //System.out.println("CatMain:createCommandPanelDialog: height: " +  commandPanelDialogSize.height);
        //commandPanelDialog.show();
    }

    /** createVGAPanel */
    protected void createVGAPanel() {
        //VGAPanel = new StudyVGAPanel(this);
        //VGAPanel.setBackground(Color.green);
        //double sizeVGA[][] ={{TableLayout.FILL},{0, TableLayout.FILL}};
        //VGAPanel.setLayout(new TableLayout(sizeVGA));
        //commandPanel.add(VGAPanel, "0,2");
    }

    /* updateVGAPAnel */
    public void updateVGAPanel() {
        //VGAPanel = new VGAStudy(this);
        //VGAPanel.setBackground(Color.CYAN);
        //double sizeVGA[][] ={{TableLayout.FILL},{0, TableLayout.FILL}};
        //VGAPanel.setLayout(new TableLayout(sizeVGA));
        //commandPanel.add(VGAPanel, "0,2");
        //VGAPanel.setVisible(false);
        //VGAPanel.removeAll();
        //VGAPanel.validate();
        //VGAPanel = new VGAStudy(this);
        //VGAPanel.setBackground(Color.CYAN);
        //commandPanel.add(VGAPanel, "0,2");
    }

    public void updateVGAPanel2() {
        //VGAPanel = new VGAStudy(this);
        //VGAPanel.setBackground(Color.CYAN);
        //double sizeVGA[][] ={{TableLayout.FILL},{0, TableLayout.FILL}};
        //VGAPanel.setLayout(new TableLayout(sizeVGA));
        //commandPanel.add(VGAPanel, "0,2");
        //VGAPanel.setVisible(true);
    }

    /**
     * Create BrandingPanel
     */
    public void createBrandingPanel() {
        AppFrameBranding brandingPanel = new AppFrameBranding();
        //brandingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        //commandPanel.add(brandingPanel, "0,0");
    }

    /**
     * Create StatusPanel
     */
    public void createStatusPanel() {
        statusPanel = new JPanel();
        //statusPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //commandPanel.add(statusPanel, "0,4");
    }

    /**
     * Create ControlTabedPanel
     *  NOT IN USE
     */
    public void createControlTabedPane() {
        controlTabbedPane = new JTabbedPane();
        controlTabbedPane.setFocusable(false);
        controlTabbedPane.setTabPlacement(JTabbedPane.TOP);
        //controlTabbedPane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        //controlTabbedPane.setBorder(BorderFactory.createEmptyBorder());
        //commandPanel.add(controlTabbedPane, "0,2");
    }

    /**
     * Create the TestSubPanel
     */
    public void createTestSubPanel() {
        // subPanel1
        testSubPanel1 = new JPanel();
        testSubPanel1.setBorder(BorderFactory.createEmptyBorder());
        //testSubPanel1.setBorder(BorderFactory.createLineBorder(Color.red));
        double sizesp1[][] = {{TableLayout.FILL}, {0, TableLayout.FILL}};
        testSubPanel1.setLayout(new TableLayout(sizesp1));

        // subPanel2
        testSubPanel2 = new JPanel();
        double sizep2[][] = {{TableLayout.FILL}, {0, TableLayout.FILL}};
        testSubPanel2.setLayout(new TableLayout(sizep2));

        // subPanel3
        testSubPanel3 = new JPanel();
        double sizep3[][] = {{TableLayout.FILL}, {0, TableLayout.FILL}};
        testSubPanel3.setLayout(new TableLayout(sizep3));

        // subPanel4
        testSubPanel4 = new JPanel();
        double sizep4[][] = {{TableLayout.FILL}, {0, TableLayout.FILL}};
        testSubPanel4.setLayout(new TableLayout(sizep4));
    }

    /**
     * Create the TestPanel.
     */
    protected void createTestPanel() {
        // Panel1
        //test1 = new Test1(this, canvas);
        //testSubPanel1.add(test1, "0,1");
        //controlTabedPane.add("1", testSubPanel1);
        //controlTabedPane.setEnabledAt(0,true);
        // ROC
        //catROC = new CatROC(this, canvas, catProp, catStudyAdmin);
        //catROCPanel.add(catROC, "0,1");
        //controlTabbedPane.add("ROC", catROCPanel);
        //controlTabedPane.setEnabledAt(1,false);
    }

    /*
    protected void createFileBrowser() {
        fileBrowser = new FileBrowser();
        fileBrowser.addListSelectListener(this);
    }*/

    /**
     * Create the ImageLoader & Saver
     */
    /*
    protected void createImageLoaderAndSaver(){
    fileBrowser = new FileBrowser();
    //fileBrowser.setBorder(BorderFactory.createLineBorder(Color.red));
    //fileBrowser.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    loader = new ImageLoaderJAI();//flBrowser.getLoader();

    fileBrowser.addListSelectListener(loader);
    //saver = new AppImageSaverPanel();
    //saver.setBorder(BorderFactory.createTitledBorder("Image Save"));
    }*/
    
    @Override
    public void load(ListSelectEvent e) {
        File currentDir = e.getPath();
        String[] flist = e.getFileList();
        String filePath =
                (currentDir != null) ? currentDir.toString() + File.separator + flist[0] : flist[0];
        //setImage(new File(filePath));
    }

    /*
    protected void setImage(File filePath) {
        setBusyCursor();
        StudyLoader_old studyLoader = new StudyLoader_old();
        studyLoader.loadImage(filePath);
        System.out.println("CatStudyAdmin:setImage File(disk) =" + filePath);

        PlanarImage orgImage = studyLoader.getLoadedPlanarImage();
        if (orgImage == null) {
            return;
        }

        // Get some tags
        String patientName = studyLoader.getPatientName();
        String patientID = studyLoader.getPatientID();
        String studyInstanceUID = studyLoader.getStudyInstanceUID();
        int[] windowWidth = studyLoader.getWindowWidth();
        int[] windowCenter = studyLoader.getWindowCenter();
        int bitsStored = studyLoader.getBitsStored();
        String photometricInterpretation = studyLoader.getPhotometricInterpretation();
        String modality = studyLoader.getModality();
        int noOfLesions = studyLoader.getNoOfLesions();
        int exposure = studyLoader.getExposure();

        canvas.setImage(orgImage);
        windowLevel.setPhotometricInterpretation(photometricInterpretation);
        windowLevel.setBitsStored(bitsStored);

        // TEST fix UPDATE UPDATE
        //windowLevel.setWindowLevelOldValues(2081, 1116);
        //windowLevel.setWindowLevel2(2081, 1116);
        windowLevel.setWindowLevel(windowWidth[0], windowCenter[0]);

        setDefaultCursor();
    }*/

    /*
    protected void createImageLoaderListener() {
        loader.addPlanarImageLoadedListener(new PlanarImageLoadedListener() {

            @Override
            public void imageLoaded(PlanarImageLoadedEvent e) {
                setBusyCursor();
                int type = e.getImageType();
                if (type == 0) {
                    BufferedImage imagebuf = e.getBufferedImage();
                    //panZoomWindowLevel.resetWL();
                    canvas.setBufferedImage(imagebuf);
                    //catWindowLevelPanel.setWindowLevelInit(177, 139);
                } else {
                    if (type == 1) {
                        PlanarImage image = e.getPlanarImage();
                        if (image == null) {
                            return;
                        }
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        SwingUtilities.invokeLater(new ImagePaint(image));

                        //Need to convert the image to a BufferedImage when using this browser
                        //BufferedImage imgb = image.getAsBufferedImage();
                        //panZoomWindowLevel.resetWL();
                        //canvas.setImage(img);

                        // Get some dicom tags.
                        //int bitsStored = loader.getBitsStored();
                        //String photometricInterpretation = loader.getPhotometricInterpretation();

                        //windowLevel.setPhotometricInterpretation(photometricInterpretation);
                        //windowLevel.setBitsStored(bitsStored);
                        //windowLevel.setWindowLevel(177, 139);
                    }
                }
                //SwingUtilities.invokeLater(new ImagePaint(image));
                setDefaultCursor();
            }
        });
    }*/

    /**
     * Add testModules
     */
    /*
    protected void addTestModules() {
        controlTabbedPane.add("B", fileBrowser);
        controlTabbedPane.add("X", testSubPanel1);
    }*/

    /********************************************************************
     *
     *  Menu
     *
     *******************************************************************/
    /**
     * Create the Menu.
     */
    public JMenuBar createAppMenuBar(VgHistory history) {
        JMenuBar menuBar = new JMenuBar();
        String menuText[] = {"File", "Tools", "Help"};
        String itemText[][] = {{"Exit"},
            {"Display window/level"},
            {"Display windowing fixed minimum"},
            {"Display mouse position (imagespace)"},
            {"Display mouse position pixel value"},
            {"Display all"},
            {"Help", "Info", "About"}};

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // color
        int[] color = new int[3];
        String key = "app.menu.panel.color";
        color = appPropertyUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // menu font
        key = "app.menu.font";
        String fontValue = appPropertyUtils.getPropertyFontValue(prop, key);
        if (fontValue.equals("")) {
            fontValue = "SansSerif-plain-16";  // default
        }
        // menu font color
        int[] menuFontColor = new int[3];
        key = "app.menu.font.color";
        menuFontColor = appPropertyUtils.getPropertyColorValue(prop, key);
        if (menuFontColor[0] == 0 && menuFontColor[1] == 0 && menuFontColor[2] == 0) {
            menuFontColor[0] = AppPropertyUtils.defTextColor[0];
            menuFontColor[1] = AppPropertyUtils.defTextColor[1];
            menuFontColor[2] = AppPropertyUtils.defTextColor[2];
        }

        menuBar.setBackground(new Color(color[0], color[1], color[2]));
        menuBar.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        menuFont = Font.decode(fontValue);
        //Font menuFont = getScaledFont("Sans Serif", Font.PLAIN, 14);


        /***************************************************************
         * 
         * File
         * 
         ***************************************************************/
        /***************
         * File
         ***************/
        fileMenu = new JMenu(menuText[0]);
        fileMenu.setFont(menuFont);
        fileMenu.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        menuBar.add(fileMenu);

        /****************
         * Exit
         ****************/
        exitItem = new JMenuItem(itemText[0][0]);
        exitItem.setFont(menuFont);
        exitItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        exitItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitItem);


        /***************************************************************
         * 
         * Tools
         * 
         **************************************************************/
        // Tools
        toolMenu = new JMenu(menuText[1]);
        toolMenu.setFont(menuFont);
        toolMenu.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        menuBar.add(toolMenu);

        /****************************
         * WindowLevel
         ****************************/
        wlCanvasDisplayCheckBoxmenuItem = new JCheckBoxMenuItem(itemText[1][0]);
        wlCanvasDisplayCheckBoxmenuItem.setFont(menuFont);
        wlCanvasDisplayCheckBoxmenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));

        // itemlistener event
        wlCanvasDisplayCheckBoxmenuItem.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if (state == ItemEvent.SELECTED) {
                    canvas.setCanvasOverlayWindowLevelStatus(true);
                    canvas.repaint();
                } else if (state == ItemEvent.DESELECTED) {
                    canvas.setCanvasOverlayWindowLevelStatus(false);
                    canvas.repaint();
                }
            }
        });

        // action event
        wlCanvasDisplayCheckBoxmenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAllCheckBoxMenuItem.setSelected(false);
            }
        });

        toolMenu.add(wlCanvasDisplayCheckBoxmenuItem);

        /***************************
         * Windowing fixed minimum
         ***************************/
        windowingFixedMinimumCheckBoxmenuItem = new JCheckBoxMenuItem(itemText[2][0]);
        windowingFixedMinimumCheckBoxmenuItem.setFont(menuFont);
        windowingFixedMinimumCheckBoxmenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));

        // itemlistener event
        windowingFixedMinimumCheckBoxmenuItem.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if (state == ItemEvent.SELECTED) {
                    canvasControl.setWindowingFixedMinOverlayStatus(true);
                    canvas.repaint();
                } else if (state == ItemEvent.DESELECTED) {
                    canvasControl.setWindowingFixedMinOverlayStatus(false);
                    canvas.repaint();
                }
            }
        });

        // action event
        windowingFixedMinimumCheckBoxmenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAllCheckBoxMenuItem.setSelected(false);
            }
        });

        toolMenu.add(windowingFixedMinimumCheckBoxmenuItem);
        windowingFixedMinimumCheckBoxmenuItem.setEnabled(false);


        /***********************
         * MousePositions
         ***********************/
        mousePositionCheckBoxMenuItem = new JCheckBoxMenuItem(itemText[3][0]);
        mousePositionCheckBoxMenuItem.setFont(menuFont);
        mousePositionCheckBoxMenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));

        // itemlistener event
        mousePositionCheckBoxMenuItem.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if (state == ItemEvent.SELECTED) {
                    canvas.setCanvasOverlayMousePositionStatus(true);
                    canvas.repaint();
                } else if (state == ItemEvent.DESELECTED) {
                    canvas.setCanvasOverlayMousePositionStatus(false);
                    canvas.repaint();
                }
            }
        });

        // actionevent
        mousePositionCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAllCheckBoxMenuItem.setSelected(false);
            }
        });

        toolMenu.add(mousePositionCheckBoxMenuItem);


        /********************
         * Pixelvalue
         *******************/
        pixelValueCheckBoxMenuItem = new JCheckBoxMenuItem(itemText[4][0]);
        pixelValueCheckBoxMenuItem.setFont(menuFont);
        pixelValueCheckBoxMenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));

        // itemlistener event
        pixelValueCheckBoxMenuItem.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if (state == ItemEvent.SELECTED) {
                    if (appMainAdmin.viewDex.canvasControl.getPixelValueStatus()) {
                        canvas.setCanvasOverlayMousePositionPixelValueStatus(true);
                    } else {
                        canvas.setCanvasOverlayMousePositionPixelValueStatus(false);
                    }

                    canvas.repaint();
                } else if (state == ItemEvent.DESELECTED) {
                    canvas.setCanvasOverlayMousePositionPixelValueStatus(false);
                    canvas.repaint();
                }
            }
        });

        // action event
        pixelValueCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAllCheckBoxMenuItem.setSelected(false);
            }
        });

        toolMenu.add(pixelValueCheckBoxMenuItem);


        /*******************
         * Display all
         *******************/
        displayAllCheckBoxMenuItem = new JCheckBoxMenuItem(itemText[5][0]);
        displayAllCheckBoxMenuItem.setFont(menuFont);
        displayAllCheckBoxMenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));

        // itemlistener event
        displayAllCheckBoxMenuItem.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if (state == ItemEvent.SELECTED) {
                    wlCanvasDisplayCheckBoxmenuItem.setSelected(true);
                    mousePositionCheckBoxMenuItem.setSelected(true);

                    if (appMainAdmin.viewDex.canvasControl.getWindowingFixedMinimumStatus()) {
                        windowingFixedMinimumCheckBoxmenuItem.setSelected(true);
                    }

                    if (appMainAdmin.viewDex.canvasControl.getPixelValueStatus()) {
                        pixelValueCheckBoxMenuItem.setSelected(true);
                    }
                } else if (state == ItemEvent.DESELECTED) {
                }
            }
        });

        displayAllCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {

                if (!displayAllCheckBoxMenuItem.isSelected()) {
                    wlCanvasDisplayCheckBoxmenuItem.setSelected(false);
                    mousePositionCheckBoxMenuItem.setSelected(false);
                    pixelValueCheckBoxMenuItem.setSelected(false);

                    if (appMainAdmin.viewDex.canvasControl.getWindowingFixedMinimumStatus()) {
                        windowingFixedMinimumCheckBoxmenuItem.setSelected(false);
                    }

                    // Do I need this one?
                    //if(appMainAdmin.viewDex.canvasControl.getPixelValueDislayStatus())
                    //  pixelValueCheckBoxMenuItem.setSelected(false);
                }
            }
        });

        toolMenu.add(displayAllCheckBoxMenuItem);


        /**************************************************************
         * 
         * Help
         * 
         *************************************************************/
        /****************
         * Help
         ***************/
        helpMenu = new JMenu(menuText[2]);
        helpMenu.setFont(menuFont);
        helpMenu.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        menuBar.add(helpMenu);

        // Help
        /*
        item = new JMenuItem(itemText[4][0]);
        item.setFont(menuFont);
        menu.add(item);
        item.addActionListener(
        new ActionListener() {
        public void actionPerformed(ActionEvent e){
        JOptionPane.showMessageDialog(ViewDex.this,
        " Under Construction",
        "Help",
        JOptionPane.INFORMATION_MESSAGE);
        }
        });
         **/

        // Info
        /*
        item = new JMenuItem(itemText[4][1]);
        item.setFont(menuFont);
        menu.add(item);
        item.addActionListener(
        new ActionListener() {
        public void actionPerformed(ActionEvent e){
        AppFrameInfoDialog infoDialog = new AppFrameInfoDialog(null, true);
        //infoDialog.displayURL();
        infoDialog.pack();

        // Set the location
        int canvasWidth = (int) canvas.getSize().getWidth();
        int canvasHeight = (int) canvas.getSize().getHeight();
        int dialogWidth = (int) infoDialog.getSize().getWidth();
        int dialogHeight = (int) infoDialog.getSize().getHeight();
        int xloc = canvasWidth/2 - dialogWidth/2;
        int yloc = canvasHeight/2 - dialogHeight/2 - 50;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        int xloc2 = Math.round(300 * xScale);
        int yloc2 = Math.round(250 * yScale);
        if(d.width == 1280)
        infoDialog.setLocation(xloc, yloc);
        else
        infoDialog.setLocation(xloc, yloc2);

        //infoDialog.setLocationRelativeTo(catMain.canvas);
        infoDialog.setVisible(true);
        }
        });
         */

        /**********************
         * About
         * *******************/
        helpAboutMenuItem = new JMenuItem(itemText[6][2]);
        helpAboutMenuItem.setFont(menuFont);
        helpAboutMenuItem.setForeground(new Color(menuFontColor[0], menuFontColor[1], menuFontColor[2]));
        helpMenu.add(helpAboutMenuItem);
        helpAboutMenuItem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AboutDialog dialog = new AboutDialog(appProperty, null, true, productVersion);
                        //dialog.displayURL();
                        dialog.setLocationRelativeTo(canvas);
                        //dialog.setLocation(330,200);

                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Dimension d = toolkit.getScreenSize();

                        if (d.width == 1600) {
                            dialog.setSize(420, 350);
                        } else if (d.width == 1280) {
                            dialog.setSize(420, 350);
                        } else if (d.width == 1024) {
                            dialog.setSize(420, 350);
                        } else {
                            dialog.setSize(420, 350);
                        }

                        dialog.setVisible(true);
                    }
                });

        return menuBar;
    }

    /*
     * set menuItem true/false
     */
    public void setWindowingFixedMinCheckBoxmenuItemEnable(boolean sta) {
        windowingFixedMinimumCheckBoxmenuItem.setEnabled(sta);
    }

    public void setPixelValueCheckBoxMenuItemEnable(boolean sta) {
        pixelValueCheckBoxMenuItem.setEnabled(sta);
    }

    public void setPixelValueCheckBoxMenuItemSelected(boolean sta) {
        pixelValueCheckBoxMenuItem.setSelected(sta);
    }

    public boolean getPixelValueCheckBoxMenuItemSelected() {
        return pixelValueCheckBoxMenuItem.isSelected();
    }

    public void setMousePositionCheckBoxMenuItemEnable(boolean sta) {
        mousePositionCheckBoxMenuItem.setEnabled(sta);
    }

    public boolean getWindowingFixedMinimumCheckBoxmenuItemStatus() {
        return windowingFixedMinimumCheckBoxmenuItem.isSelected();
    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    /*******************************************************
     *
     *   end menu
     *
     ******************************************************/
    /**
     * Create the allowClosing
     */
    public void allowClosing() {
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
    }

    /**
     * Gets font scaled for screen resolution
     * @param fontName              Logical font name i.e. SansSerif
     * @param fontStyle             Font class style defines
     * @param pointSizeFor1280Mode  How big in 1280 * 1024 resolution
     * @return                      The scaledFont value
     */
    public Font getScaledFont(String fontName, int fontStyle, int pointSizeFor1280Mode) {
        Font f = new Font(fontName, fontStyle, pointSizeFor1280Mode);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (d.height == 1024) {
            return f;
        } else {
            int numerator = pointSizeFor1280Mode * d.height;
            float sizeForCurrentResolution = (float) numerator / 1024;
            return f.deriveFont(sizeForCurrentResolution);
        }
    }

    private void createBorders() {
        //black = BorderFactory.createLineBorder(Color.black);
        //blackLine = BorderFactory.createLineBorder(Color.gray);
        //blackLineLoweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        //blackLineBold = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK);
        //greyLine = BorderFactory.createLineBorder(new Color(204,204,204));
        //empty = BorderFactory.createEmptyBorder();
    }

    public void setBusyCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setDefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
    /*
    if(args.length != 0){
    if(args[0].equals("-i"))
    }


    new ViewDex();
     *
    }*/
    public static void main(String[] args) {
        int runMode = 0;

        if (runMode == 0) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new ViewDex();
                }
            });
        } else if (runMode == 1) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new ViewDex();
                }
            });
        }
    }

    /** ImagePaint inner class
     */
    class ImagePaint implements Runnable {

        PlanarImage image;
        boolean firstTime = true;

        public ImagePaint(PlanarImage image) {
            this.image = image;
        }

        @Override
        public void run() {
            if (firstTime) {
                try {
                    firstTime = false;
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    canvas.setImage(image);
                    //saver.setDisplayImage(viewer.getDisplayImage());
                    canvas.repaint();
                    SwingUtilities.invokeLater(this);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(this);
                }
            } else {
                if (!canvas.isImageDrawn()) {
                    SwingUtilities.invokeLater(this);
                } else {
                    setCursor(Cursor.getDefaultCursor());
                    //updateRenderGrid();
                    //updateMemoryMessageBar(memoryMessageBar);
                }
            }
        }
    }

    /** createUserDefForTest
     */
    protected void createUserDefForTest() {
        //studyVGADefList = new ArrayList<VgTaskPanelQuestion>();
        //studyVGADefList.add(new CriterialDefClass("1) How xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 5));
        //studyVGADefList.add(new CriterialDefClass("1) How can you dlfkdfk dlsfkskdf sdlkfskdf", 5));
        //studyVGADefList.add(new CriterialDefClass("2) How can you visualize the basal ganglia?", 5));
        //studyVGADefList.add(new CriterialDefClass("3) How can you delineate the ventricular system?", 5));
        //studyVGADefList.add(new CriterialDefClass("4) How can you delineate the cerebrospinal fluid space around the mesencephaion?", 5));
        //studyVGADefList.add(new CriterialDefClass("5) How can you delineate the cerebrospinal fluid space around the brain?", 5));
    }

    public void createUserDefForTest2() {
        //studyVGADefList = new ArrayList<CriterialDefClass>();
        //studyVGADefList.add(new CriterialDefClass("1) How xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 1));
        //studyVGADefList.add(new CriterialDefClass("1) How can you", 5));
        //studyVGADefList.add(new CriterialDefClass("2) How can you visualize the basal ganglia?", 1));
        //studyVGADefList.add(new CriterialDefClass("3) How can you delineate the ventricular system?", 2));
        //studyVGADefList.add(new CriterialDefClass("4) How can you delineate the cerebrospinal fluid space around the mesencephaion?", 2));
        //studyVGADefList.add(new CriterialDefClass("5) How can you delineate the cerebrospinal fluid space around the brain?", 1));
    }

    /*
    public ArrayList<VgTaskPanelQuestion> getUserDefList(){
    return studyVGADefList;
    }*/
    /**
     * Set the title of the application on the main window frame.
     */
    public void setAppTitle(String str) {
        this.setTitle(str);
    }

    public static void runGc() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long mem = rt.freeMemory();
        System.out.println("Free Memory = " + mem);
    }

    /*
     * If set, information about the image vill be displayed on
     * the application main frame.
     * @param status the status
     */
    private static void setStudyInfoAppMainFrameStatus(boolean status) {
        studyInfoAppMainFrameStatus = status;
    }

    /*
     * Get the status whether or not to display image information on the
     * application main frame.
     * @return the status
     */
    public boolean getImageInfoAppMainFrameStatus() {
        return studyInfoAppMainFrameStatus;
    }

    /***********************************************************
     * KeyListener interface.
     **********************************************************/
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("ViewDex.keyPressed");

        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            if (appMainAdmin.vgControl != null) {
                //appMainAdmin.vgControl.setImageNextInStack();
                appMainAdmin.vgControl.setImageNextPrevInStack(0);
            }
        }

        if (keyCode == KeyEvent.VK_DOWN) {
            if (appMainAdmin.vgControl != null) {
                //appMainAdmin.vgControl.setImagePrevInStack();
                appMainAdmin.vgControl.setImageNextPrevInStack(1);
            }
        }
        /*
        if(keyCode == KeyEvent.VK_F){
        if(appMainAdmin.vgControl != null)
        appMainAdmin.vgControl.setCineMode(1);
        }*/
        /*
        if(keyCode == KeyEvent.VK_B){
        if(appMainAdmin.vgControl != null)
        appMainAdmin.vgControl.setCineMode(2);
        }*/
        /*
        if(keyCode == KeyEvent.VK_S){
        if(appMainAdmin.vgControl != null)
        appMainAdmin.vgControl.setCineMode(3);
        }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("ViewDex.keyTyped");
    }

    public String getProductVersion() {
        return productVersion;
    }

    public String getOsType() {
        return osType;
    }

    /*
     * test 2015-12-23
     */
    private int cnt = 0;

    public void toggleVisible() {
        System.out.println("ViewDex.toggleVisible" + "  " + cnt++);
        //super.setVisible(true);
        boolean sta = isVisible();
        //setVisible(!isVisible());
        if (isVisible()) {
            //mainFrame.setDisposed(false);
            super.setAlwaysOnTop(true);
            super.toFront();
            super.requestFocus();
            super.setAlwaysOnTop(false);

            //mainFrame.setExtendedState(Frame.ICONIFIED);
            //mainFrame.setExtendedState(Frame.NORMAL);

            //if(getState()!=Frame.NORMAL){
            //  setState(Frame.NORMAL);
            //}

            try {
                //remember the last locaation of mouse
                final Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();

                //simulate a mouse cliaack on title bar of window
                Robot robot = new Robot();
                //robot.keyPress(KeyEvent.VK_1);
                //robot.mouseMove(mainFrame.getX() + 100, mainFrame.getY() + 5);
                //robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                //robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                //move mouse to old location
                //robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_CONTROL);
            } catch (Exception ex) {
                //ignore exception
            } finally {
                setAlwaysOnTop(false);
            }
        }
        System.out.println("ViewDex.toggleVisible end" + "  " + cnt++);
    }
    // test
    int cnt2 = 0;

    public void setApplicationFocus() {
        System.out.println("ViewDex.setAplicationFocus" + "  " + cnt2++);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                mainFrame.toFront();
                mainFrame.repaint();
                mainFrame.requestFocus();
            }
        });
        System.out.println("ViewDex.setAplicationFocus end" + "  " + cnt2++);
    }
    /**********************************************************
     * Interface WindowFocusListener
     * *******************************************************/
    int cnt3 = 0;

    @Override
    public void addWindowFocusListener(WindowFocusListener l) {
        super.addWindowFocusListener(l);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        //System.out.println("ViewDex.windowGainedFocus" + "  " + cnt3++);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        //System.out.println("ViewDex.windowLostFocus" +"  " + cnt3++);
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
