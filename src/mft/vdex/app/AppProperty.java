/* @(#) AppProperty.java 07/06/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author sune
 */
public class AppProperty {

    ViewDex viewDex;
    private Properties studyProperties;
    private Properties userProperties;
    private String userName;
    private String studyName;
    private String loginName;
    private Properties systemProperties = null;

    /** Creates a new instance of AppPropertyClass */
    public AppProperty(ViewDex viewdex) {
        this.viewDex = viewdex;
        init();
    }

    private void init() {
        //systemProperties = readSystemProperties();  deprecated
        userProperties = readUserProperties();
        //printUserProperties();
    }

    /**
     * Check if a "study property" is defined.
     */
    public boolean studyPropertyExist(String key) {
        boolean propExist = false;
        boolean propValue = false;
        String s1 = "";

        if (viewDex.appProperty.getStudyProperties().containsKey(key)) {
            s1 = viewDex.appProperty.getStudyProperties().getProperty(key).trim();
            propExist = true;
        } else {
            propExist = false;
        }
        if (s1.equalsIgnoreCase("")) {
            propValue = false;
        } else {
            propValue = true;
        }
        return propExist;
    }

    /**
     * Read the vgstudy-vgxx.properties file. The properties are
     * read from the user.dir/resource directory.
     */
    public void readStudyProperties() {
        Properties prop = new Properties();

        String userDir = System.getProperty("user.dir");
        String appResourcesPath = userDir + File.separator + "resources" + File.separator + studyName + ".properties";
        System.out.println("VgControl: readVgProperties: " + appResourcesPath);

        try {
            FileInputStream in = new FileInputStream(appResourcesPath);
            prop.load(in);
        } catch (IOException e) {
            //String propName = "\"vgstudy-" + studyName + ".properties\"";
            String propName = "\"" + studyName + ".properties\"";
            String str = "Unable to find the  " + propName + "  file.";
            System.out.print("Error: VgControl.readVgProperties:" + str);
            JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        studyProperties = prop;
    }

    /** Read the user.properties file.
     */
    private Properties readUserProperties() {
        Properties prop = new Properties();
        String userDir = System.getProperty("user.dir");
        //String appRoot = "c:" + File.separator + "ViewDEX";
        String userResourcesPath = userDir + File.separator + "resources" + File.separator + "user.properties";
        System.out.println("AppProperty:readUserProperties: " + userResourcesPath);

        try {
            FileInputStream in = new FileInputStream(userResourcesPath);
            prop.load(in);
        } catch (IOException e) {
            System.out.print("Error: AppProperty:readUserProperties: Can�t find the user.properties file");
            System.exit(1);
        }
        return prop;
    }

    public Properties getStudyProperties() {
        return studyProperties;
    }

    public void setUserName(String username) {
        userName = username;
    }

    public void setStudyName(String studyname) {
        studyName = studyname;
    }

    public void setLoginName(String loginname) {
        loginName = loginname;
    }

    public String getUserName() {
        return userName;
    }

    public String getStudyName() {
        return studyName;
    }

    public String getLoginName() {
        return loginName;
    }

    private void printSystemProperties() {
        Enumeration keys = systemProperties.keys();

        while (keys.hasMoreElements()) {
            String keyStr = (String) keys.nextElement();
            String keyValue = systemProperties.getProperty(keyStr);
            System.out.println(keyStr + "  =  " + keyValue);
        }
    }

    /** Print the user properties in the console window.
     */
    private void printUserProperties() {
        Enumeration keys = userProperties.keys();

        while (keys.hasMoreElements()) {
            String keyStr = (String) keys.nextElement();
            String keyValue = userProperties.getProperty(keyStr);
            System.out.println(keyStr + "  =  " + keyValue);
        }
    }

    public void setSystemProperty(Properties prop) {
        systemProperties = prop;
    }

    public Properties getSystemProperty() {
        return systemProperties;
    }

    public Properties getUserProperty() {
        return userProperties;
    }

    /* Get the users defined in the user.properties file.
     * @return the users.
     */
    public String[] getUsers() {
        String[] goodUser = new String[100];
        String key;
        int keyCnt = 0;

        int noOfKeys = userProperties.size();
        for (int i = 0; i
                < noOfKeys; i++) {
            int cnt = i + 1;
            key = "user" + cnt + "." + "name";
            if (userProperties.containsKey(key)) {
                goodUser[keyCnt] = userProperties.getProperty(key);
                keyCnt++;
            }
        }
        return goodUser;
    }

     /*
     * Error dialog.
     */
    public void demoStudyPropErrorMessage() {
        String str = "imagedb.directory.demo";
        //String str2 = "Property  " + '\"' + str + '\"' + "  not defined.";
        String str2 = "Property  " + '\"' + str + '\"' + "  not defined. " + "Default value used!   ";
        System.out.print("Error: VgControl.getDemoRunMode:" + str2);
        JOptionPane.showMessageDialog(viewDex.canvas, str2, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
