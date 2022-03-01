/* @(#) VgHistoryDemoUtil.java 03/14/2003
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbStackNode;

/**
 *
 * @author sune
 */
public class VgHistoryDemoUtil {

    ViewDex viewDex;
    private boolean debug = false;
    private VgHistory history;

    public VgHistoryDemoUtil(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     * Create the demo history.
     */
    public void create() {
        createDemoHistoryObject();
        createDemoHistoryObjectBackup();

        history = readDemoHistoryObject();
        history.setVgProperties(viewDex.appProperty.getStudyProperties());
        history.setStudyLoginDate(new Date());
        createHistoryPropertyList();
        //viewDex.appMainAdmin.vgControl.readImageDb();
        writeDemoHistory(history);
        writeDemoHistoryBackup(history);
    }

    /**
     * Read the demo history.
     */
    public void read() {
        //System.out.println("VgControl.readDemoHistory");

        history = readDemoHistoryObject();
        String time = getHistoryLoginTime();
        history.setStudyLogInElapsedTime(time);
        history.setStudyLoginDate(new Date());
    }

    public VgHistory getHistory(){
        return history;
    }

    /**
     * Check if the "history and log" property "log.log1-directory" exist.
     * Check if the original history demo object exist.
     * @return boolean true if history-object exist else false.
     */
    public boolean exist() {
        boolean status = false;

        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");

        if (debug) {
            System.out.println("vgControl.historyOriginalExist historyPath = " + historyPath);
        }

        // history object
        if (viewDex.vgHistoryUtil.fileExist(historyPath[1])) {
            status = true;
        }
        return status;
    }

    /**
     * Create the original history object and write to disk.
     */
    private void createDemoHistoryObject() {
        boolean dirExist = false;

        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");

        if (debug) {
            System.out.println("vgControl.createOriginalHistoryObject(): historyPath = " + historyPath[1]);
        }

        // history object
        if (!(dirExist = viewDex.vgHistoryUtil.fileExist(historyPath[0]))) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(historyPath[0]);
        }

        if (dirExist) {
            File fileHistoryPath = new File(historyPath[1]);
            if (!fileHistoryPath.exists()) {
                try {
                    VgHistory hist = new VgHistory(viewDex.appProperty.getUserName(), viewDex.appProperty.getStudyName());
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                    out.writeObject(hist);
                    out.close();
                } catch (Exception e) {
                    String str = "Unable to create the demo user history_object:  " + fileHistoryPath + "." + "  System will exit.";
                    System.out.print("Error: VgControl.createDemoHistoryObject:" + str);
                    JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Read the demo history object. The file is only
     * read from the 'log.log1-directory'.
     */
    public VgHistory readDemoHistoryObject() {
        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");

        VgHistory histObj = null;
        if (viewDex.vgHistoryUtil.fileExist(historyPath[1])) {
            File path = new File(historyPath[1]);
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
                histObj = (VgHistory) in.readObject();
                in.close();
            } catch (Exception e) {
                System.out.print("Error: VgControl.readDemoHistoryObject: Unable to read the demo user history_object file");
                System.exit(1);
            }
        }
        return histObj;
    }

    /**
     * Create a demo history backup object and write the object to disk.
     */
    private void createDemoHistoryObjectBackup() {
        boolean dirExist = false;

        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "demo");

        if (debug) {
            System.out.println("vgControl.createDemoHistoryObjectBackup(): historyPath = " + historyPath[1]);
        }

        dirExist = viewDex.vgHistoryUtil.fileExist(historyPath[0]);
        if (!dirExist) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(historyPath[0]);
        }

        if (!dirExist) {
            String propName = "\".log.log2-directory\"";
            String str = "Log2 directory " + historyPath + " can not be created.";
            System.out.println("Warning: VgControl.createDemoHistoryObjectBackup:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        if (dirExist) {
            File fileHistoryPath = new File(historyPath[1]);
            if (!fileHistoryPath.exists()) {
                try {
                    VgHistory hist = new VgHistory(viewDex.appProperty.getUserName(), viewDex.appProperty.getStudyName());
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                    out.writeObject(hist);
                    out.close();
                } catch (Exception e) {
                    String str = "Unable to create the demo user history_object:  " + historyPath[1] + "." + "  System will exit.";
                    System.out.println("Error: VgControl.createDemoHistoryObjectBackup:" + str);
                    JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        } else {
            String propName = "\"log.log2-directory\"";
            String str = "log2 property  " + propName + "  not defined.";
            System.out.println("Warning: VgControl.createDemoHistoryObjectBackup:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Write the demo user history object
     */
    public void writeDemoHistory(VgHistory hist) {
        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");

        if (debug) {
            System.out.println("vgControl.writeDemolHistory: historyPath = " + historyPath[1]);
        }

        File fileHistoryPath = new File(historyPath[1]);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
            out.writeObject(hist);
            out.close();
        } catch (Exception e) {
            String str = "Unable to create the demo user history_object:  " + fileHistoryPath + "." + " System will exit.";
            System.out.print("Error: VgControl.writeDemolHistory:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            System.exit(1);
        }
    }

    /** Write the VgHistory backup object
     */
    public void writeDemoHistoryBackup(VgHistory hist) {
        String[] historyPath = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "demo");

        if (debug) {
            System.out.println("vgControl.writeDemoHistoryBackup: historyObjectPath = " + historyPath[1]);
        }

        boolean dirExist = false;
        dirExist = viewDex.vgHistoryUtil.fileExist(historyPath[0]);
        if (!dirExist) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(historyPath[0]);
        }

        if (dirExist) {
            File fileHistoryPath = new File(historyPath[1]);

            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                out.writeObject(hist);
                out.close();
            } catch (Exception e) {
                String str = "Unable to create the user demo history_object backup  " + "\"" + fileHistoryPath + "\"" + ".";
                System.out.print("Error: VgControl.writeOriginalHistoryBackup:" + str);
                JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                //System.exit(1);
            }
        } else {
            String propName = "\"log.log2-directory\"";
            String str = "Log2 directory " + historyPath[1] + " can not be created.";
            System.out.println("Warning: VgControl.writeDemoHistoryBackup:" + str);
            //JOptionPane.showMessageDialog(appMainAdmin.viewDex.canvas,
            //      str, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /*
     * Error dialog.
     */
    public void demoStudyHistoryErrorMessage() {
        String str = "history read error";
        System.out.print("Error: VgControl.demoStudyInit: + str1");
        JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /*
     * Error dialog.
     */
    public void demoStudyFilePathErrorMessage() {
        String str = "imagedb.directory.demo";
        String str2 = "Path to  " + '\"' + str + '\"' + "  not find.  System will exit! ";
        System.out.print("Error: VgControl.demoStudyFilepathErrorMessage:  " + str2);
        JOptionPane.showMessageDialog(viewDex.canvas, str2, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /*
     */
    public boolean stackExist() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = history.getStudyDbRootNodeList();
        if (studyDbRootNodeList == null || (studyDbRootNodeList.isEmpty())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Create createHistoryPropertyList.
     * Put this in the History object?
     */
    public void createHistoryPropertyList() {
        // taskPanelQuestionList
        ArrayList<VgTaskPanelQuestion> taskPanelQuestionList = viewDex.appPropertyCreate.createTaskPanelQuestionList();
        history.setTaskPanelQuestionList(taskPanelQuestionList);

        // taskPanelClarificationList
        ArrayList<VgTaskPanelClarification> taskPanelClarificationList = viewDex.appPropertyCreate.createTaskPanelClarificationList();
        history.setTaskPanelClarificationList(taskPanelClarificationList);

        // cineLoopPanelControlList

        ArrayList<VgCineLoopPanelControl> cineLoopPanelControlList = viewDex.appPropertyCreate.createCineLoopPanelControlList();
        history.setCineLoopPanelControlList(cineLoopPanelControlList);

        // functionPanelZoomControlList
        ArrayList<VgFunctionPanelZoomControl> functionPanelZoomControlList = viewDex.appPropertyCreate.createFunctionPanelZoomControlList();
        history.setFunctionPanelZoomControlList(functionPanelZoomControlList);

        // functionPanelZoomModeList
        ArrayList<VgFunctionPanelZoomModeControl> functionPanelZoomModeList = viewDex.appPropertyCreate.createFunctionPanelZoomModeList();
        history.setFunctionPanelZoomModeList(functionPanelZoomModeList);

        // functionPanelWLList
        ArrayList<VgFunctionPanelWLControl> functionPanelWLList = viewDex.appPropertyCreate.createFunctionPanelWLList();
        history.setFunctionPanelWLList(functionPanelWLList);

        // functionPanelUserDefinedWLList
        ArrayList<VgFunctionPanelUserDefinedWLControl> functionPanelUserDefinedWLList = viewDex.appPropertyCreate.createFunctionPanelUserDefinedWLList();
        history.setFunctionPanelUserDefinedWLList(functionPanelUserDefinedWLList);

        // functionInterpolationControlList
        ArrayList<VgCanvasInterpolationControl> functionInterpolationList = viewDex.appPropertyCreate.createCanvasInterpolationList();
        history.setCanvasInterpolationList(functionInterpolationList);

        // log optional list
        ArrayList<VgLogOptionalTag> logOptionalTagList = viewDex.appPropertyCreate.createLogOptionalTagList();
        history.setLogOptionalTagList(logOptionalTagList);

        // log special optional list
        ArrayList<VgLogOptionalSpecial> logOptionalSpecialList = viewDex.appPropertyCreate.createLogOptionalSpecialList();
        history.setLogOptionalSpecialList(logOptionalSpecialList);
    }

    /**
     * Get the time elapsed since the last login.
     */
    protected String getHistoryLoginTime() {
        String str = "";
        Date date1 = new Date();
        Date date2 = new Date();

        date1 = history.getStudyLoginDate();
        date2 = new Date();

        if (date1 != null && date2 != null) {
            long time1 = date1.getTime();
            long time2 = date2.getTime();

            //long diff = 92123456;
            double diff = time2 - time1;
            float f = (float) diff / (1000 * 60 * 60 * 24);
            double d = diff / (1000 * 60 * 60 * 24); // 1.0662437
            long d2 = Math.round(diff / (1000 * 60 * 60 * 24)); // 1
            double h = diff % (1000 * 60 * 60 * 24); // 5 723 456
            double hf = (float) h / (1000 * 60 * 60); // 1.5898489
            long h2 = Math.round(h / (1000 * 60 * 60)); // 1
            double m = h % (1000 * 60 * 60); // 2 123 456
            double m2 = (float) m / (1000 * 60); // 35.390933
            long m3 = Math.round(m / (1000 * 60)); // 35
            double s = m % (1000 * 60); // 23 456
            double sf = (float) s / (1000); // 23.456
            long s2 = Math.round(s / 1000); // 23
            str = d2 + ":" + h2 + ":" + m3 + ":" + s2;
        }
        return str;
    }
}
