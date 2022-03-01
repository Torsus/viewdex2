/* @(#) AppMainAdmin.java 06/07/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.app;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JOptionPane;
import mft.vdex.modules.vg.VgControl;
import mft.vdex.modules.vg.VgHistoryOptionType;
import mft.vdex.modules.vg.VgRunMode;

/**
 *
 * @author sune
 */
/**
 * The <code>AppMainAdmin</code> class start the login process,
 * find the user and the type of study to run. Instantiates the
 * study class and start the study.
 */
public class AppMainAdmin implements VgRunMode, VgHistoryOptionType {

    public ViewDex viewDex;
    public VgControl vgControl;
    public VgControl vgControl_tmp;
    private AppMainLogin appLogin;
    private AppMainOptionLogin appOptionLogin;
    private ArrayList<AppUser> userList;
    private int historyOption = 0;

    /** Constructor.
     * @param viewdex the main class of the application.
     */
    public AppMainAdmin(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     */
    @SuppressWarnings("empty-statement")
    public void startRunLogin() {
        while (!runLogin());
    }

    /**
     * Run the login process.
     * Check for a valid user and start the appropriate study.
     */
    public boolean runLogin() {
        boolean status = false;
        int runMode = VgRunMode.NONE;

        viewDex.setAppTitle("");
        userList = createUserList();
        historyOption = 0;  // reset

        // login
        appLogin = new AppMainLogin(viewDex, userList);
        String loginName = appLogin.getLoginName();
        String userName = getUserName(loginName);
        String studyName = getStudy(userName);

        viewDex.appProperty.setUserName(userName);
        viewDex.appProperty.setStudyName(studyName);
        viewDex.appProperty.setLoginName(loginName);
        viewDex.appProperty.readStudyProperties();

        // option dialog
        if (showStudyNameExist(loginName) || editStudyNameExist(loginName)) {
            appOptionLogin = new AppMainOptionLogin(viewDex, loginName);
            historyOption = appOptionLogin.getHistoryOption();
        }

        // find the run mode
        vgControl_tmp = new VgControl(this, false);
        int runMode_tmp = vgControl_tmp.getRunMode();
        vgControl_tmp.demoStudyInit();

        if (runMode_tmp == VgRunMode.NONE
                || runMode == VgRunMode.SHOW_ERROR
                || runMode == VgRunMode.EDIT_ERROR) {
            return false;
        }

        vgControl_tmp = null;

        if (vgControl != null) {
            vgControl = null;
        }

        // Create the 'real' vgControl object    
        vgControl = new VgControl(this, true);
        runMode = vgControl.getRunMode();
        vgControl.setRunModeStatus(runMode);
        vgControl.setHistoryOptionStatus(historyOption);

        // Cursor
        viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        viewDex.setAppTitle("      Please wait...  Initializing history");

        // Create history...
        if (runMode == VgRunMode.CREATE_EXIST) {
            if (historyOption == VgHistoryOptionType.HISTORY_OPTION_ORIGINAL) {
                if (!viewDex.vgHistoryMainUtil.exist()) {
                    viewDex.vgHistoryMainUtil.create();
                } else {
                    viewDex.vgHistoryMainUtil.read();
                }
            }
        } else if (runMode == VgRunMode.EDIT_EXIST) {
            if (historyOption == VgHistoryOptionType.HISTORY_OPTION_ORIGINAL) {
                if (!viewDex.vgHistoryMainUtil.exist()) {
                    viewDex.vgHistoryMainUtil.create();
                } else {
                    viewDex.vgHistoryMainUtil.read();
                    /*if (!viewDex.vgHistoryEdit.exist()) {
                        viewDex.vgHistoryEdit.create(viewDex.vgHistoryMain);
                        viewDex.vgHistoryMain.history = viewDex.vgHistoryEdit.history;
                    }*/
                }
            } else if (historyOption == VgHistoryOptionType.HISTORY_OPTION_EDITED) {
                if (viewDex.vgHistoryEditUtil.exist()) {
                    viewDex.vgHistoryEditUtil.read();
                    viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryEditUtil.getHistory());
                } else {
                    viewDex.vgHistoryEditUtil.create();
                    viewDex.vgHistoryEditUtil.read();
                    viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryEditUtil.getHistory());
                }
            }
        } else if (runMode == VgRunMode.SHOW_EXIST) {
            if (historyOption == VgHistoryOptionType.HISTORY_OPTION_ORIGINAL) {
                if (!viewDex.vgHistoryMainUtil.exist()) {
                    viewDex.vgHistoryMainUtil.create();
                } else {
                    viewDex.vgHistoryMainUtil.read();
                }
            } else if (historyOption == VgHistoryOptionType.HISTORY_OPTION_EDITED) {
                if (viewDex.vgHistoryEditUtil.exist()) {
                    viewDex.vgHistoryEditUtil.read();
                    viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryEditUtil.getHistory());

                } else {
                    viewDex.vgHistoryEditUtil.create();
                    viewDex.vgHistoryEditUtil.read();
                    viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryEditUtil.getHistory());
                }
            }
        } else if (runMode == VgRunMode.DEMO_EXIST) {
            if (viewDex.vgHistoryDemoUtil.exist()) {
                viewDex.vgHistoryDemoUtil.read();
                viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryDemoUtil.getHistory());
            } else {
                viewDex.vgHistoryDemoUtil.create();
                viewDex.vgHistoryDemoUtil.read();
                viewDex.vgHistoryMainUtil.setHistory(viewDex.vgHistoryDemoUtil.getHistory());
            }
        }

        // Assign to the main history object
         // Set path to imageDb
        String imageDbPath = null;
        if (runMode == VgRunMode.CREATE_EXIST || runMode == VgRunMode.EDIT_EXIST
                || runMode == VgRunMode.SHOW_EXIST) {
            imageDbPath = viewDex.vgHistoryCreateUtil.getImageDbMainPath();
        } else if (runMode == VgRunMode.DEMO_EXIST) {
            imageDbPath = viewDex.vgHistoryCreateUtil.getImageDbDemoPath();
        }
        
        // DemoDb not defined
        if (runMode == VgRunMode.DEMO_EXIST) {
            if (!viewDex.vgHistoryUtil.fileExist(imageDbPath)) {
                viewDex.vgHistoryDemoUtil.demoStudyFilePathErrorMessage();
            }
        }

        if(!viewDex.vgHistoryMainUtil.studyDbRootNodeListExist()){
            //========================================================
            // Test History object
            //long t1 = System.currentTimeMillis();
            // end test History object
            //========================================================
            
            viewDex.appMainAdmin.vgControl.readImageDb(imageDbPath);
            
            //========================================================
            // Test History object
            //long t2 = System.currentTimeMillis() - t1;
            //long t3 = (t2/1000) / 60;
            //System.out.println("AppMainAdmin.runLogin() readImageDB: " +
            //        "time to load: " + t2 + " milliseconds " +
            //        "(" + t3 + " minutes)");
            // end test History object
            //=========================================================
        }
        viewDex.vgHistory = viewDex.vgHistoryMainUtil.getHistory();

        // Cursor
        viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        viewDex.setAppTitle("");

        // imagedb exist
        if (!viewDex.vgHistoryMainUtil.studyDbRootNodeListExist()) {
            Toolkit.getDefaultToolkit().beep();
            String str = "  No studies in the imagedb  ";
            System.out.println("Error: AppMainAdmin.runLogin:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // init
        vgControl.setInitState();
        
        if (runMode == VgRunMode.CREATE_EXIST) {
            vgControl.setCreateInitState();
        } else if (runMode == VgRunMode.SHOW_EXIST) {
            vgControl.setShowInitState();
        } else if (runMode == VgRunMode.EDIT_EXIST) {
            vgControl.setEditInitState();
        } else if (runMode == VgRunMode.DEMO_EXIST) {
            vgControl.setCreateInitState();
        }

        // Create log & log2, if not already exist.
        if (runMode == VgRunMode.CREATE_EXIST || runMode == VgRunMode.EDIT_EXIST
                || runMode == VgRunMode.DEMO_EXIST) {
            vgControl.createVgLog();
        }

        // Create the GUI & start the study
        vgControl.createVgStudy();
        if (!vgControl.getStudyDone()) {
            vgControl.startStudyAsStack();
        }

        return true;
    }

    /**
     */
    private String getUserName(String loginName) {
        String name = null;

        if (loginName.contains("edit")) {
            // Find the username (loginName 'minus' "edit").
            name = loginName.substring(0, loginName.length() - 4);
        } else if (loginName.contains("show")) {
            // Find the username (loginName 'minus' "show").
            name = loginName.substring(0, loginName.length() - 4);
        } else if (loginName.contains("demo")) {
            // Find the username (loginName 'minus' "demo").
            name = loginName.substring(0, loginName.length() - 4);
        } else {
            name = loginName;
        }
        return name;
    }

    /**
     * Check if a "show" study name exist.
     */
    private boolean showStudyNameExist(String userName) {
        if (userName.contains("show")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the "edit" study name exist.
     */
    private boolean editStudyNameExist(String userName) {
        if (userName.contains("edit")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find out if there is a valid 'edit' userName.
     * @return
     */
    public boolean userNameHistoryEditExist(String userName) {
        boolean status = false;

        if (userName.contains("edit")) {
            // Find the username (userName 'minus' "edit").
            String user = userName.substring(0, userName.length() - 4);
            status = historyEditExist(user);
        } else if (userName.contains("show")) {
            // Find the username (userName 'minus' "show").
            String user = userName.substring(0, userName.length() - 4);
            status = historyEditExist(user);
        }
        return status;
    }

    /**
     * Find out if there is a valid 'show' userName.
     * @return
     */
    public boolean userNameShowValid(String userName) {
        if (userName.contains("show")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find out if there is a valid 'edit' userName.
     * @return
     */
    public boolean userNameEditValid(String userName) {
        if (userName.contains("edit")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the edit history object exist.
     *@return boolean true if history-object exist else false.
     * NOT IN USE
     */
    /*
    public boolean historyEditExist(String name) {
    boolean status = false;
    boolean propexist = false;

    Properties prop = readProperties();

    String key = "log.log1-directory";
    String s1 = "";
    if(prop.containsKey(key)) {
    s1 = prop.getProperty(key).trim();
    propexist = true;
    } else
    propexist = false;

    if (s1.equalsIgnoreCase(""))
    propexist = false;

    if (!propexist) {
    String propName = "\"log.log1-directory\"";
    String str = "History property  " + propName + " not defined.  System will exit.";
    System.out.print("Error: AppMainAdmin.historyExist:" + str);
    JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
    System.exit(1);
    }

    String historyPath = s1 + File.separator + "history" + File.separator + studyName;
    String historyObjectPath = historyPath + File.separator + name + "edit" + "-history_object.ser";

    // history object
    if (fileExist(historyObjectPath))
    status = true;

    return status;
    }*/
    /**
     * Check if the edit history object exist.
     *@return boolean true if history-object exist else false.
     */
    public boolean historyEditExist(String name) {
        boolean status = false;
        boolean propExist = false;
        boolean propValue = false;

        Properties prop = readProperties();
        String ostype = viewDex.getOsType();

        // The root directory for log & history
        String key = "log.log1-directory";
        String s1 = "";
        if (prop.containsKey(key)) {
            s1 = prop.getProperty(key).trim();
            propExist = true;
        } else {
            propExist = false;
        }

        if (s1.equalsIgnoreCase("")) {
            propValue = false;
        } else {
            propValue = true;
        }

        // Set default value
        if (!propValue) {
            s1 = "./";
            propValue = true;
        }

        if (!propValue) {
            String propName = "\"log.log1-directory\"";
            String str = "History property  " + propName + " not defined.  System will exit.";
            System.out.print("Error: VgControl.historyOriginalExist:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String historyPath = "";
        String historyObjectPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            historyPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
            historyObjectPath = historyPath + File.separator + name + "edit" + "-history_object.ser";
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (s1.equalsIgnoreCase("./")) {
                    historyPath = s1 + "history" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    historyPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
                }

                historyObjectPath = historyPath + File.separator + name + "edit" + "-history_object.ser";
            }
        }

        System.out.println("vgControl.historyOriginalExist(): historyObjectPath = " + historyObjectPath);

        // history object
        if (fileExist(historyObjectPath)) {
            status = true;
        }
        return status;
    }

    /** Read the vgstudy-vgxx.properties file. The properties are
     * read from the user.dir/resource directory.
     */
    private Properties readProperties() {
        Properties prop = new Properties();

        String userDir = System.getProperty("user.dir");
        String appResourcesPath = userDir + File.separator + "resources" + File.separator + viewDex.appProperty.getStudyName() + ".properties";
        System.out.println("AppMainAdmin: readProperties: " + appResourcesPath);

        try {
            FileInputStream in = new FileInputStream(appResourcesPath);
            prop.load(in);
        } catch (IOException e) {
            //String propName = "\"vgstudy-" + studyName + ".properties\"";
            String propName = "\"" + viewDex.appProperty.getStudyName() + ".properties\"";
            String str = "Unable to find the  " + propName + "  file.";
            System.out.print("Error: AppMainAdmin.readProperties:" + str);
            JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return prop;
    }

    /**
     * Reads the user properties and create a list of all users
     * and the assign type of study.
     * Create a "demo" user.
     * Create a "show" user.
     * Create a "edit" user.
     */
    private ArrayList<AppUser> createUserList() {
        ArrayList<AppUser> list;

        Properties prop = viewDex.appProperty.getUserProperty();
        int size = prop.size();

        list = new ArrayList<AppUser>();
        String key1, key2, s1 = null, s2 = null, s3 = null;
        for (int i = 0; i < size; i++) {
            int cnt = i + 1;
            key1 = "user" + cnt + ".name";
            key2 = "user" + cnt + ".study";
            if (prop.containsKey(key1) && prop.containsKey(key2)) {
                s1 = prop.getProperty(key1);
                s2 = prop.getProperty(key2);
                list.add(new AppUser(s1, s2));

                // Create a "demo" user
                s3 = s1 + "demo";
                list.add(new AppUser(s3, s2));

                // Create a "show" user
                s3 = s1 + "show";
                list.add(new AppUser(s3, s2));

                // Create a "edit" user
                s3 = s1 + "edit";
                list.add(new AppUser(s3, s2));
            }
        }
        return list;
    }

    /**
     * Get the study for a selected user.
     * @param user The selected user.
     * @return The study for the selected user.
     */
    private String getStudy(String user) {
        for (int i = 0; i < userList.size(); i++) {
            if (user.equals(userList.get(i).getUser())) {
                return userList.get(i).getStudy();
            }
        }
        return null;
    }

    /**
     * Check if the edit history object exist.
     *@return boolean true if history-object exist else false.
     */
    public boolean historyEditExist() {
        boolean status = false;
        boolean propexist = false;
        String key = "log.log1-directory";
        String s1 = "";

        if (viewDex.appProperty.getStudyProperties().containsKey(key)) {
            s1 = viewDex.appProperty.getStudyProperties().getProperty(key).trim();
            propexist = true;
        } else {
            propexist = false;
        }

        if (s1.equalsIgnoreCase("")) {
            propexist = false;
        }

        if (!propexist) {
            String propName = "\".log.log1-directory\"";
            String str = "History property  " + propName + " not defined.  System will exit.";
            System.out.print("Error: AppMainAdmin.historyExist:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String historyPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
        String historyObjectPath = historyPath + File.separator + viewDex.appProperty.getUserName() + "edit" + "-history_object.ser";

        // history object
        if (fileExist(historyObjectPath)) {
            status = true;
        }

        return status;
    }

    /* Check if directory exist
     */
    private boolean fileExist(String path) {
        boolean status = false;

        try {
            File f = new File(path);
            if (f.exists()) {
                status = true;
            } else {
                status = false;
            }

        } catch (Exception e) {
            System.out.print("Error: AppMainAdmin.fileExist");
            System.exit(1);
        }
        return status;
    }
    //studiesForSelUser = getStudiesForSelUser(userName);
    //createHistory();
    //userHistory = readHistory();
    // Get the time elapsed between previous login.
    //long elapsedTime = getHistoryLoginTime();
    //userHistory.setStudyLogInElapsedTime(elapsedTime);
    // Set the new login timestamp
    //userHistory.setStudyLoginDate(new Date());
    // Select study dialog
    //catStudySelectDialog = new CatStudySelectDialog(this, catMain, true, userHistory);
    //catStudySelectDialog.pack();
    // Set the location
        /*int canvasWidth = (int) catMain.canvas.getSize().getWidth();
    int canvasHeight = (int) catMain.canvas.getSize().getHeight();
    int dialogWidth = (int) catStudySelectDialog.getSize().getWidth();
    int dialogHeight = (int) catStudySelectDialog.getSize().getHeight();

    int xloc = canvasWidth/2 - dialogWidth/2;
    int yloc = canvasHeight/2 - dialogHeight/2 - 50;

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension d = toolkit.getScreenSize();
    float xScale = (float) d.width / 1280;
    float yScale = (float) d.height / 1024;

    int xloc2 = Math.round(300 * xScale);
    int yloc2 = Math.round(250 * yScale);

    if(d.width == 1280)
    catStudySelectDialog.setLocation(xloc, yloc);
    else
    catStudySelectDialog.setLocation(xloc, yloc2);
     */
    //catStudySelectDialog.show();
    //new CatLog(userHistory);
    //catLog.start(userHistory); old moved
}
