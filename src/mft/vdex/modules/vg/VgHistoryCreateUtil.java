/* @(#) VgHistoryCreateUtil.java 03/14/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.modules.vg;

import java.io.File;
import javax.swing.JOptionPane;
import mft.vdex.app.ViewDex;

/**
 *
 * @author sune
 */
public class VgHistoryCreateUtil {

    ViewDex viewDex;
    private boolean debug = false;

    public VgHistoryCreateUtil(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /*****************************************************************
     * imagedb main/demo path
     * logfile/history path
     * logfile path
     * ***************************************************************
    /*
     * Get imagedb main path
     */
    public String getImageDbMainPath() {
        boolean propExist = false;
        boolean propValue = false;

        String ostype = viewDex.getOsType();

        String key = "imagedb.directory.main";
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

        String imagedbPath = "";
        String imagedbMainPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            if (!propValue) {
                imagedbPath = ".\\" + "imagedb" + File.separator + viewDex.appProperty.getStudyName() + "-main";
            } else {
                imagedbPath = s1;
            }

            imagedbMainPath = imagedbPath;
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (!propValue) {
                    imagedbPath = "./" + "imagedb" + File.separator + viewDex.appProperty.getStudyName() + "-main";
                } else {
                    imagedbPath = s1;
                }
                ;

                imagedbMainPath = imagedbPath;
            }
        }

        if (debug) {
            System.out.println("vgControl.getImageDbDemoPath imagedbMainPath = " + imagedbMainPath);
        }

        if (!viewDex.vgHistoryUtil.fileExist(imagedbMainPath)) {
            String str = "   ImageDb not exist." + "   " + "System will exit.   ";
            System.out.print("Error: VgControl.getImageDbMainPath " + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return imagedbMainPath;
    }

    /*
     * Get imagedb demo path
     */
    public String getImageDbDemoPath() {
        boolean propExist = false;
        boolean propValue = false;

        String ostype = viewDex.getOsType();

        String key = "imagedb.directory.demo";
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

        String imagedbPath = "";
        String imagedbDemoPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            if (!propValue) {
                imagedbPath = ".\\" + "imagedb" + File.separator + viewDex.appProperty.getStudyName() + "-demo";
            } else {
                imagedbPath = s1;
            }

            imagedbDemoPath = imagedbPath;
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (!propValue) {
                    imagedbPath = "./" + "imagedb" + File.separator + viewDex.appProperty.getStudyName() + "-demo";
                } else {
                    imagedbPath = s1;
                }

                imagedbDemoPath = imagedbPath;
            }
        }

        if (debug) {
            System.out.println("vgControl.getImageDbDemoPath imagedbDemoPath = " + imagedbDemoPath);
        }

        /*
        if (!propExist) {
        String str = "Imagedb property  " + "imagedb.directory.demo" + " not defined.  System will exit.";
        System.out.print("Error: VgControl.getImageDbDemoPath:" + str);
        JOptionPane.showMessageDialog(appMainAdmin.viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
        }*/

        return imagedbDemoPath;
    }

    /*
     * Get the log/history path.
     */
    public String[] getLogfileHistoryPath(String k, String runmode) {
        boolean propExist = false;
        boolean propValue = false;
        String[] strPath = new String[2];
        String key = k;
        String runMode = runmode;

        String ostype = viewDex.getOsType();
        String user = viewDex.appProperty.getUserName() + runMode;

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

        if (!propExist) {
            String str = "History property  " + key + " not defined.  System will exit.";
            System.out.print("Error: VgControl.getLogPropertyPath" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String historyDirPath = "";
        String historyPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            if (key.equalsIgnoreCase("log.log1-directory")) {
                if (!propValue) {
                    historyDirPath = "." + File.separatorChar + File.separator
                            + "history" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    historyDirPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
                }
            }
            if (key.equalsIgnoreCase("log.log2-directory")) {
                if (!propValue) {
                    historyDirPath = "." + File.separatorChar + File.separator
                            + "log2" + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    historyDirPath = s1 + File.separator + "log2" + File.separator
                            + "history" + File.separator + viewDex.appProperty.getStudyName();
                }
            }
            historyPath = historyDirPath + File.separator + user + "-history_object.ser";
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (key.equalsIgnoreCase("log.log1-directory")) {
                    if (!propValue) {
                        historyDirPath = "." + File.separatorChar + "history" + File.separator
                                + viewDex.appProperty.getStudyName();
                    } else {
                        historyDirPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
                    }
                }
                if (key.equalsIgnoreCase("log.log2-directory")) {
                    if (!propValue) {
                        historyDirPath = "." + File.separatorChar + "log2" + File.separator
                                + "history" + File.separator + viewDex.appProperty.getStudyName();
                    } else {
                        historyDirPath = s1 + File.separator + "log2" + File.separator
                                + "history" + File.separator + viewDex.appProperty.getStudyName();
                    }
                }
            }
            historyPath = historyDirPath + File.separator + user + "-history_object.ser";
        }

        if (debug) {
            System.out.println("vgControl.getLogHistoryPath historyDirPath = " + historyDirPath);
            System.out.println("vgControl.getLogHistoryPath historyPath = " + historyPath);
        }

        strPath[0] = historyDirPath;
        strPath[1] = historyPath;

        return strPath;
    }

    /*
     * Get the logfile path.
     */
    public String[] getLogfilePath(String k, String runmode) {
        boolean propExist = false;
        boolean propValue = false;
        String[] strPath = new String[2];
        String key = k;
        String runMode = runmode;

        String ostype = viewDex.getOsType();
        String user = viewDex.appProperty.getUserName() + runMode;

        // The root directory for log & history
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

        if (!propExist) {
            String str = "History + property  " + key + " not defined.  System will exit.";
            System.out.print("Error: VgControl.getLogFilePath" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String logDirPath = "";
        String logPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            if (key.equalsIgnoreCase("log.log1-directory")) {
                if (!propValue) {
                    logDirPath = "." + File.separatorChar + File.separator + "log" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    logDirPath = s1 + File.separator + "log" + File.separator + viewDex.appProperty.getStudyName();
                }
            }
            if (key.equalsIgnoreCase("log.log2-directory")) {
                if (!propValue) {
                    logDirPath = "." + File.separatorChar + File.separator + "log2" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    logDirPath = s1 + File.separator + "log2" + File.separator + viewDex.appProperty.getStudyName();
                }
            }
            logPath = logDirPath + File.separator + user + "-" + viewDex.appProperty.getStudyName() + ".txt";
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (key.equalsIgnoreCase("log.log1-directory")) {
                    if (!propValue) {
                        logDirPath = "." + File.separatorChar + "log" + File.separator + viewDex.appProperty.getStudyName();
                    } else {
                        logDirPath = s1 + File.separator + "log" + File.separator + viewDex.appProperty.getStudyName();
                    }
                }
                if (key.equalsIgnoreCase("log.log2-directory")) {
                    if (!propValue) {
                        logDirPath = "." + File.separatorChar + "log2" + File.separator + viewDex.appProperty.getStudyName();
                    } else {
                        logDirPath = s1 + File.separator + "log2" + File.separator + viewDex.appProperty.getStudyName();
                    }
                }
                logPath = logDirPath + File.separator + user + "-" + viewDex.appProperty.getStudyName() + ".txt";
            }
        }

        if (debug) {
            System.out.println("vgControl.historyOriginalExist logDirPath = " + logDirPath);
            System.out.println("vgControl.historyOriginalExist logPath = " + logPath);
        }

        strPath[0] = logDirPath;
        strPath[1] = logPath;

        return strPath;
    }

     /*
     * Check if the history object exist for a specific user.
     * @return boolean true if history-object exist else false.
     * NOT IN USE
     */
    private boolean historyExist(String user) {
        boolean status = false;
        boolean propExist = false;
        boolean propValue = false;

        String ostype = viewDex.getOsType();

        // The root directory for log & history
        String key = "log.log1-directory";
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

        // Set default value
        if (!propValue) {
            s1 = "./";
            propValue = true;
        }

        if (!propValue) {
            String propName = "\"log.log1-directory\"";
            String str = " History property  " + propName + " not defined.  System will exit.";
            System.out.print("Error: VgControl.historyExist:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String historyPath = "";
        String historyObjectPath = "";

        if (ostype.equalsIgnoreCase("Windows")) {
            historyPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
            historyObjectPath = historyPath + File.separator + user + "-history_object.ser";
        } else {
            if (ostype.equalsIgnoreCase("Linux")) {
                if (s1.equalsIgnoreCase("./")) {
                    historyPath = s1 + "history" + File.separator + viewDex.appProperty.getStudyName();
                } else {
                    historyPath = s1 + File.separator + "history" + File.separator + viewDex.appProperty.getStudyName();
                }

                historyObjectPath = historyPath + File.separator + user + "-history_object.ser";
            }
        }

        if (debug) {
            System.out.println("vgControl.historyOriginalExist(): historyObjectPath = " + historyObjectPath);
        }

        if (viewDex.vgHistoryUtil.fileExist(historyObjectPath)) {
            status = true;
        }
        return status;
    }
}
