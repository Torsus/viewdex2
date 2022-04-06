/* @(#) VgHistory.java 03/14/2003
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
//import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

/**
 *
 * @author sune
 */
public class VgHistoryMainUtil {
    public ViewDex viewDex;
    private VgHistory history;

    public VgHistoryMainUtil(ViewDex viewdex){
        viewDex = viewdex;
    }

    /**
     * Create the original history.
     * Read imagedb
     */
    public void create() {
        createOriginalHistoryObject();
        createOriginalHistoryObjectBackup();

        history = readOriginalHistoryObject();
        history.setVgProperties(viewDex.appProperty.getStudyProperties());
        history.setStudyLoginDate(new Date());

        if (viewDex.eyeTracking.getEyeTrackingStatus() && viewDex.eyeTracking.getEyeTrackingTmpList() != null) {
            history.setEyeTrackingList(viewDex.eyeTracking.getEyeTrackingTmpList());
        }

        createHistoryPropertyList();
        //viewDex.appMainAdmin.vgControl.readImageDb();
        writeHistory(history);
        writeHistoryBackup(history);
        //System.exit(1);
    }

     /**
     * Read the original history.
     */
    public void read() {
        //System.out.println("VgControl.readOriginalHistory");

        history = readOriginalHistoryObject();
        String time = getHistoryLoginTime();
        history.setStudyLogInElapsedTime(time);
        history.setStudyLoginDate(new Date());
    }
    
    public void setHistory(VgHistory hist){
        history = hist;
    }

    public VgHistory getHistory(){
        return history;
    }

    /**
     * Check if the "history/log1" property exist.
     * Check if the original history object exist.
     * @return boolean true if history-object exist else false.
     */
    public boolean exist() {
        boolean status = false;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "");

        if (viewDex.vgHistoryUtil.fileExist(path[1])) {
            status = true;
        }
        return status;
    }

    /**
     * Create the original history object and write to disk.
     */
    private void createOriginalHistoryObject() {
        boolean dirExist = false;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "");

        // history object
        if (!(dirExist = viewDex.vgHistoryUtil.fileExist(path[0]))) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(path[0]);
        }

        if (dirExist) {
            File fileHistoryPath = new File(path[1]);

            if (!fileHistoryPath.exists()) {
                try {
                    VgHistory userHistory = new VgHistory(viewDex.appProperty.getUserName(),
                            viewDex.appProperty.getStudyName());
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                    out.writeObject(userHistory);
                    out.close();
                } catch (Exception e) {
                    String str = "Unable to create the user history_object:  " + path[1] + "." + "  System will exit.";
                    System.out.print("Error: VgControl.createHistory:" + str);
                    JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }
        // Add Error dialog?
    }

    /**
     * Read the original history object. The object file is
     * only read from the 'log.log1-directory' property.
     */
    public VgHistory readOriginalHistoryObject() {
        VgHistory histObj = null;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "");
        File path2 = new File(path[1]);

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path2));
            histObj = (VgHistory) in.readObject();
            in.close();
        } catch (Exception e) {
            System.out.print("Error: VgControl.readOriginalHistoryObject(). Unable to read the app user history_object file");
            System.exit(1);
        }
        return histObj;
    }

    /**
     * Create a history backup object and write the object to disk.
     */
    private void createOriginalHistoryObjectBackup() {
        boolean dirExist = false;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "");

        if (!(dirExist = viewDex.vgHistoryUtil.fileExist(path[0]))) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(path[0]);
        }

        if (!dirExist) {
            String propName = "\".log.log2-directory\"";
            String str = "Log2 directory " + path[0] + " can not be created.";
            System.out.println("Warning: VgControl.createOriginalHistoryObjectBackup:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        if (dirExist) {
            File fileHistoryPath = new File(path[1]);
            if (!fileHistoryPath.exists()) {
                try {
                    VgHistory userHistory = new VgHistory(viewDex.appProperty.getUserName(), viewDex.appProperty.getStudyName());
                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                    out.writeObject(userHistory);
                    out.close();
                } catch (Exception e) {
                    String str = "Unable to create the user history_object:  " + path[1] + "." + "  System will exit.";
                    System.out.println("Error: VgControl.createOriginalHistoryObjectBackup:" + str);
                    JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        } else {
            String propName = "\"log.log2-directory\"";
            String str = "Log2 property  " + propName + "  not defined.";
            System.out.println("Warning: VgControl.createOriginalHistoryObjectBackup:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Write the userHistory object
     */
    public void writeHistory(VgHistory hist) {
        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "");

        File fileHistoryPath = new File(path[1]);
        
        //====================================================================
        // Test history object
        
        //private ArrayList<StudyDbStackNode> studyDbStackNodeListOrig;
        //private ArrayList<StudyDbStackNode> studyDbZeroNodeListOrig;
        //private ArrayList<StudyDbStackNode> studyDbRootNodeList;
        //private ArrayList<StudyDbStackNode> studyDbStackNodeList;
        //private ArrayList<StudyDbStackNode> studyDbZeroNodeList;
        //private ArrayList<StudyDbStackNode> studyDbZeroNodeAsStackList;
        
        //ArrayList <StudyDbStackNode> rootNodeList  = hist.getStudyDbRootNodeList();
        //ArrayList <StudyDbStackNode> zeroNodeListOrig  = hist.getStudyDbZeroNodeListOrig();
        //ArrayList <StudyDbStackNode> zeroNodeList  = hist.getStudyDbZeroNodeList();
        //ArrayList <StudyDbStackNode> stackNodeListOrig  = hist.getStudyDbStackNodeListOrig();
        //ArrayList <StudyDbStackNode> stackNodeList  = hist.getStudyDbStackNodeList();
        //ArrayList <StudyDbStackNode> zeroNodeAsStackList  = hist.getStudyDbZeroNodeAsStackList();
        /*
        System.out.println("VgHistoryMainUtil.writeHistory rootNodeList object size: " + ObjectSizeCalculator.getObjectSize(rootNodeList));
        System.out.println("VgHistoryMainUtil.writeHistory zeroNodeListOrig object size: " + ObjectSizeCalculator.getObjectSize(zeroNodeListOrig));
        System.out.println("VgHistoryMainUtil.writeHistory zeroNodeList object size: " + ObjectSizeCalculator.getObjectSize(zeroNodeList));
        System.out.println("VgHistoryMainUtil.writeHistory stackNodeListOrig object size: " + ObjectSizeCalculator.getObjectSize(stackNodeListOrig));
        System.out.println("VgHistoryMainUtil.writeHistory stackNodeList object size: " + ObjectSizeCalculator.getObjectSize(stackNodeList));
        System.out.println("VgHistoryMainUtil.writeHistory zeroNodeAsStack object size: " + ObjectSizeCalculator.getObjectSize(zeroNodeAsStackList));
        */
        
        // test
        //System.out.println("Object size(1): " + ObjectSizeCalculator.getObjectSize(1));
        //System.out.println("Object size(hist): " + ObjectSizeCalculator.getObjectSize(hist)); 
        
        // end test History object 
        //====================================================================
        
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
            out.writeObject(hist);
            out.close();
        } catch (Exception e) {
            String str = "Unable to create the user history_object:  " + fileHistoryPath + "." + " System will exit.";
            System.out.print("Error: VgControl.writeOriginalHistory:" + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            System.exit(1);
        }
    }

    /** Write the VgHistory backup object
     */
    public void writeHistoryBackup(VgHistory hist) {
        boolean dirExist = false;
        
        // test
        //System.out.println("Object size(1): " + ObjectSizeCalculator.getObjectSize(1));
        //System.out.println("Object size(hist): " + ObjectSizeCalculator.getObjectSize(hist));

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "");
        dirExist = viewDex.vgHistoryUtil.fileExist(path[0]);

        if (!dirExist) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(path[0]);
        }

        if (dirExist) {
            File fileHistoryPath = new File(path[1]);

            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                out.writeObject(hist);
                out.close();
            } catch (Exception e) {
                String str = "Unable to create the user backup history_object  " + "\"" + fileHistoryPath + "\"" + ".";
                System.out.print("Error: VgControl.writeOriginalHistoryBackup:" + str);
                JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                //System.exit(1);
            }
        } else {
            String propName = "\"log.log2-directory\"";
            String str = "Log2 directory " + path[0] + " can not be created.";
            System.out.println("Warning: VgControl.writeOriginalHistoryBackup:" + str);
            //JOptionPane.showMessageDialog(appMainAdmin.viewDex.canvas,
            //      str, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Create the history property list.
     * Set the list in the History object.
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

     /*
     */
    public boolean studyDbRootNodeListExist(){
        ArrayList<StudyDbStackNode> studyDbRootNodeList = history.getStudyDbRootNodeList();
        if (studyDbRootNodeList == null || (studyDbRootNodeList.isEmpty())) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Print History Object size.
     */
    public void printHistoryObjectSize(){
  //      System.out.println("VgHistory.printHistoryObjectSize: " + " " + ObjectSizeCalculator.getObjectSize(history));
    }
    
    /**
     * Print History Object size.
     * @param s 
     */
    public void printHistoryObjectSize(String s){
  //      System.out.println("VgHistory.printHistoryObjectSize: " + s + " " + ObjectSizeCalculator.getObjectSize(history));
    }
    // end Test History object
    //=====================================================================
}
