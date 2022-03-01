/* @(#) VgLog.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.modules.vg;

import java.awt.geom.Point2D;
import java.io.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.JOptionPane;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbNodeType;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.ds.StudyDbStackType;
import mft.vdex.app.AppPropertyUtils;
//import org.dcm4che.data.Dataset;
import org.dcm4che3.data.Attributes;


/**
 * The <code>VgLog</code> creates the study log files.
 */
public class VgLog implements StudyDbNodeType {
    VgControl vgControl;
    VgHistory history;
    String productVersion = "not available";
    String log1Root = null, log2Root = null;
    String studyStatus;
    String studyName;
    AppPropertyUtils propUtils;

    public VgLog(VgControl vgcontrol, VgHistory hist) {
        //System.out.println("VgLog:VgLog");
        productVersion = vgcontrol.appMainAdmin.viewDex.getProductVersion();
        this.vgControl = vgcontrol;
        this.history = hist;
        //this.studyName = studyname;

        init();
    }
    
    /**
     * Init
     */
    private void init() {
        createLogRoot();
        createLog2Root();
    }
    
    private void createLogRoot(){
        String[] path = vgControl.appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "");
        
        if (!fileExist(path[0]))
            createDirectory(path[0]);

        /*
         if (!propValue) {
            String propName = "\"log.log1-directory\"";
            String str = "History property  " + propName + " not defined. System will Exit.";
            System.out.println("Error: VgLog.init:" + str);
            JOptionPane.showMessageDialog(vgControl.appMainAdmin.viewDex.canvas,
                    str, "Eror", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }*/
    }

    private void createLog2Root(){
        String[] path = vgControl.appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "");
        
        if (!fileExist(path[0]))
            createDirectory(path[0]);
    }

    /**
     * Start
     */
    public void start() {
        String str;
        String logUserName = "";

        //Get the time elapsed between a user login and logout.
        //long elapsedTime = catStudyAdmin.getHistoryLoginTime();
        //Returned value not correct
        
        //String elapsedTime = history.getStudyLogInElapsedTime();

        //if(elapsedTime.equalsIgnoreCase(""))
        //  elapsedTime = "0";
        
        if(vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            logUserName = history.getUserName();
        else
            if(vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST)
                logUserName = history.getUserName() + "demo";
            else
                if(vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
                    logUserName = history.getUserName() + "edit";
        
        str = "**Logfile started on: " + new Date() + "; " +
                "User:" + logUserName + "; " +
                "Version:" + productVersion + "; " +
                //"Time since previous login:" + elapsedTime + ";" +
                System.getProperty("line.separator");
        
        // write
        writeLogFile(str);
        writeLogFileBackup(str);
    }

    /*
     * Update.
     */
    public void update() {
        StudyDbStackNode stackNode = vgControl.studyDbUtility.getSelectedStackNode();
        
        if(stackNode.getStackType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE)
            createLogEntrySingleImage(stackNode);
        else
            if(stackNode.getNodeType() == NODE_TYPE_STACK)
                createLogEntryImageStack2(stackNode);
    }
    
    /**
     * Create the log entries for a single image.
     * @param <code>StudyDbStackNode<code/> the stackNode.
     */
    private void createLogEntrySingleImage(StudyDbStackNode stackNode){
        String caseStr = null;
        String notesStr = null;
        String locStr = null;
        String itemStr = null;

        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        StudyDbImageNode imageNode = stackNode.getImageNode(0);
        int selCnt = stackNode.getSelImageNodeCount();  // test
        
        if(imageNodeList.size() != 1){
            System.out.println("Error: VgLog.createLogEntrySingleImage. ImageNodeList size error!");
            System.exit(1);
        }

        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();
        //printTaskPanelResultList(resultList, "100");
        
        caseStr = createCaseSingleImageStackNodeString(stackNode);
        notesStr = createCaseStackNotesString(stackNode);
        
        // Localization exist
        if(resultListLocaliztionExist(resultList)){
            locStr = createResultStringLocalization(imageNode, resultList);
            itemStr = createImageItemString(imageNode, resultList, locStr);
        }
        else
            itemStr = createImageItemNoLocalizationString(imageNode, resultList, locStr);
        
        // write
        writeLogFile(itemStr);
        writeLogFile(caseStr);
        
        if(notesStr != null)
            writeLogFile(notesStr);
        
        writeLogFileBackup(itemStr);
        writeLogFileBackup(caseStr);
        
        if(notesStr != null)
            writeLogFileBackup(notesStr);
    }
    
    /**
     * Create the log entries for an image stack.
      * @param <code>StudyDbStackNode<code/> the stackNode.
     */
    private void createLogEntryImageStack2(StudyDbStackNode stackNode){
        String caseStr = null;
        String notesStr = null;
        String locStr = null;
        String itemStr = null;
        
        caseStr = createCaseStackNodeString(stackNode);
        notesStr = createCaseStackNotesString(stackNode);
        
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        
        boolean localizationExist = false;
        for(int i=0; i < imageNodeList.size(); i++){
            StudyDbImageNode imageNode = imageNodeList.get(i);
            ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();
            //printTaskPanelResultList(resultList, "100");
            
            // Localization exist
            if(resultListLocaliztionExist(resultList)){
                localizationExist = true;
                
                locStr = createResultStringLocalizationForStack(imageNodeList, resultList);
                itemStr = createImageItemString(imageNode, resultList, locStr);
            }
            //else
              //  itemStr = createImageItemNoLocalizationString(imageNode, resultList, locStr);
            
            // write
            if(itemStr != null){
                writeLogFile(itemStr);
                writeLogFileBackup(itemStr);
            }
            itemStr = null;
        }
        
        if(!localizationExist && imageNodeList.size() >=1){
            StudyDbImageNode imageNode = imageNodeList.get(0);
            String itemStrNoLocalization = createImageItemNoLocalizationString(imageNode, stackNode);
            
            // write
            if(itemStrNoLocalization != null){
                writeLogFile(itemStrNoLocalization);
                writeLogFileBackup(itemStrNoLocalization);
            }
        }
        
        // write
        if(caseStr != null){
            writeLogFile(caseStr);
            writeLogFileBackup(caseStr);
        }
        if(notesStr != null){
            writeLogFile(notesStr);
            writeLogFileBackup(notesStr);
        }
    }
    
    /**
     * resultListLocaliztionExist
     */
    private boolean resultListLocaliztionExist(ArrayList<VgTaskPanelResult> list){
        boolean status = false;
        
        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelResult node = list.get(i);
            if(node.getLocalizationStatus() == true){
                status = true;
                break;
            }
        }
        return status;
    }
    
    /**
     * Create the result string for the localization tasks.
     */
    private String createResultStringLocalization(StudyDbImageNode imageNode,
            ArrayList<VgTaskPanelResult> list){
        ArrayList<VgTaskPanelResult> list2 = null;
        String cordinateStr = "";
        String resultLocStr = "";
        String locStr = "";

        double tsir = imageNode.getTimeStampImageRendering();
        // Get properties
        propUtils = new AppPropertyUtils();
        Properties prop = history.getVgProperties();

        int imageTimeStampLogNo = 0;
        int defValue = 0;
        String key1 = "image.timestamp.logged";
        imageTimeStampLogNo = propUtils.getPropertyIntegerValue(prop, key1);
        if (imageTimeStampLogNo == 0)
            imageTimeStampLogNo = defValue;
        
        //Sort the list according to the timestamp in the <code>VgTaskPanelResult<code/> object.
        Collections.sort(list);
        //printTaskPanelResultList(resultList, "101");
        
        // true means: sort localization items
        list2 = sortResultListLocalization(list, true);
        //printTaskPanelResultList(list, "102");
        //System.exit(99);

        boolean imageTimeStampLog = false;
        for (int i = 0; i < list2.size(); i++) {
            if(imageTimeStampLogNo == 0)
                imageTimeStampLog = false;
            else{
                if(i < imageTimeStampLogNo)
                    imageTimeStampLog = true;
                else
                    imageTimeStampLog = false;
            }
            
            VgTaskPanelResult result = list2.get(i);
            Point2D p = result.getPoint();
            TreeSet<VgTaskPanelResult> list3 = findResultItemForLocPointSortedList(list2, p);
            //printTaskPanelResultList(list2, "104");

            cordinateStr = createCoordinateLocString(list3);
            resultLocStr = createTaskResultLocString(list3, tsir, imageTimeStampLog);

            locStr = locStr + cordinateStr + resultLocStr;
            
            //System.out.println("cordinateStr = " + cordinateStr);
            //System.out.println("resultLocStr = " + resultLocStr);
            //System.out.println("locStr = " + locStr);
        }
        
         
        // Set the logDone status to false. The resultList will be used again.
        setLogDoneStatus(list, false);
        
        return locStr;
    }

     /**
     * Create the result string for the localization tasks.
     */
    private String createResultStringLocalizationForStack(ArrayList<StudyDbImageNode> imageNodeList,
            ArrayList<VgTaskPanelResult> list){
        ArrayList<VgTaskPanelResult> list2 = null;
        String cordinateStr = "";
        String resultLocStr = "";
        String locStr = "";

        //long tsir = imageNode.getTimeStampImageRendering();

        //Sort the list according to the timestamp in the <code>VgTaskPanelResult<code/> object.
        Collections.sort(list);
        //printTaskPanelResultList(resultList, "101");

        // true means: sort localization items
        list2 = sortResultListLocalization(list, true);
        //printTaskPanelResultList(list, "102");
        //System.exit(99);

        for (int i = 0; i < list2.size(); i++) {
            VgTaskPanelResult result = list2.get(i);
            Point2D p = result.getPoint();
            TreeSet<VgTaskPanelResult> list3 = findResultItemForLocPointSortedList(list2, p);
            //printTaskPanelResultList(list2, "104");

            cordinateStr = createCoordinateLocString(list3);
            resultLocStr = createTaskResultLocStringForStack(list3);
            locStr = locStr + cordinateStr + resultLocStr;

            //System.out.println("cordinateStr = " + cordinateStr);
            //System.out.println("resultLocStr = " + resultLocStr);
            //System.out.println("locStr = " + locStr);
        }

        // Set the logDone status to false. The resultList will be used again.
        setLogDoneStatus(list, false);

        return locStr;
    }
    
    /**
     * Create the image item string.
     * @param <code>ArrayList<VgTaskPanelResult><code/> the taskPanel result list.
     * @param <code>String<code/> the localization result string.
     * @return <code>String<code/> the imageItem string.
     */
    private String createImageItemString(StudyDbImageNode imageNode, ArrayList<VgTaskPanelResult> list,
            String resultLocalized){
        String str = null;
        String optionalSpecial = "";
        String optionalTagsStr;
        
        //String imageName = imageNode.getStudyPath().getName();
        //int itemCnt = imageNode.getItemCnt();
        
        // not in use
        optionalSpecial = createOptionalSpecial(imageNode);
        
        String optionalTags = createOptionalTag(imageNode);
        if (optionalTags.equalsIgnoreCase("")) {
            optionalTagsStr = "";
        } else {
            optionalTagsStr = optionalTags + ";";
        }
        
        str = "ImageName:" + 
                imageNode.getStudyPath().getName() +
                "; " +
                "ImageNo:" + (imageNode.getItemCnt() + 1) + "; " +
                "PatID:" + imageNode.getPatientID() + "; " +
                "Result(localized):" + resultLocalized +
                " " +
                optionalSpecial + optionalTagsStr + System.getProperty("line.separator");
        return str;
    }
    
    /**
     * Create the image item string.
     * @param <code>ArrayList<VgTaskPanelResult><code/> the taskPanel result list.
     * @param <code>String<code/> the localization result string.
     * @return <code>String<code/> the imageItem string.
     */
    private String createImageItemNoLocalizationString(StudyDbImageNode imageNode, StudyDbStackNode stackNode){
        String str = null;
        String optionalSpecial = "";
        String optionalTagsStr;
        
        //String imageName = imageNode.getStudyPath().getName();
        //int itemCnt = imageNode.getItemCnt();
        
        // not in use
        //optionalSpecial = createOptionalSpecial(imageNode);
        
        String optionalTags = createOptionalTag(imageNode);
        if (optionalTags.equalsIgnoreCase("")) {
            optionalTagsStr = "";
        } else {
            optionalTagsStr = optionalTags + ";";
        }
        
        str = "ImageName:" + 
                imageNode.getStudyPath().getName() +
                "; " +
                "ImageNo:" + (imageNode.getItemCnt() + 1) + "; " +
                "PatID:" + imageNode.getPatientID() + "; " +
                "OptionalLogData:" + 
                " " +
                optionalSpecial + optionalTagsStr +
                " " +
                "Variant:" + "n/a" + System.getProperty("line.separator");
        
        // test for variants
        //optionalTagVariantExist(stackNode);
        
        return str;
    }
    
    /**
     * Create the image item no localization string.
     * @param <code>ArrayList<VgTaskPanelResult><code/> the taskPanel result list.
     * @param <code>String<code/> the localization result string.
     * @return <code>String<code/> the imageItem string.
     */
    private String createImageItemNoLocalizationString(StudyDbImageNode imageNode,
            ArrayList<VgTaskPanelResult> list, String resultLocalized){
        String str = null;
        String optionalSpecial = "";
        String optionalTagsStr;
        
        //String imageName = imageNode.getStudyPath().getName();
        optionalSpecial = createOptionalSpecial(imageNode);
        
        String optionalTags = createOptionalTag(imageNode);
        if (optionalTags.equalsIgnoreCase("")) {
            optionalTagsStr = "";
        } else {
            optionalTagsStr = optionalTags + ";";
        }
        
        str = "ImageName:" + 
                imageNode.getStudyPath().getName() + "; " +
                "PatID:" + imageNode.getPatientID() + "; " +
                optionalSpecial + optionalTagsStr + System.getProperty("line.separator");
        return str;
    }
    
    /**
     * Create the case string.
     * @param<code>StudyDbStackNode<code/> the selected stackNode.
     * @return <code>String<code/> the case stackNode string.
     */
    private String createCaseSingleImageStackNodeString(StudyDbStackNode stackNode){
        String str = null;
        String str2 = null;
        String str3 = null;
        String strResult = null;
        
        int cnt = stackNode.getItemCnt();
        File nodePath = stackNode.getNodePath();
        String patientId = vgControl.studyDbUtility.getPatientID();
        
        String resultStr = createTaskPanelResultNotLocalized(stackNode);
        
        // StackNo
        str = "CaseNo:" + (cnt + 1) + "; " + "ImageDbPath:" + nodePath + "; " + "PatID:" + patientId + "; ";
        strResult = "Result(not localized):" + resultStr + " ";
        
        // Mandatory dicom tags
        // w/l
        String str1 = vgControl.studyDbUtility.getWindowLevelAdjusted();
        str2 = str + strResult + "w/l:" + str1 + "; ";
        
        // TimeSpent
        long time = stackNode.getStackEvaluationTime();
        long t2 = (long) Math.round(time / 1000);
        String timeSpent = Long.toString(t2);
        str3 = str2 + "TimeSpent:" + timeSpent + ";" + System.getProperty("line.separator");
        
        return str3;
    }
    
    /**
     * Create the case string.
     * @param<code>StudyDbStackNode<code/> the selected stackNode.
     * @return <code>String<code/> the case stackNode string.
     */
    private String createCaseStackNodeString(StudyDbStackNode stackNode){
        String str = null;
        String str2 = null;
        String str3 = null;
        String strResult = null;
        
        int cnt = stackNode.getItemCnt();
        File nodePath = stackNode.getNodePath();
        String patientId = vgControl.studyDbUtility.getPatientID();
        
        String resultStr = createTaskPanelResultNotLocalized(stackNode);
        
        // StackNo
        str = "CaseNo:" + (cnt + 1) + "; " + "StackPath:" + nodePath + "; " + "patID:" + patientId + "; ";
        strResult = "Result(not localized):" + resultStr + " ";
        
        // Mandatory tags
        // w/l
        String str1 = vgControl.studyDbUtility.getWindowLevelAdjusted();
        str2 = str + strResult + "w/l:" + str1 + "; ";
        
        // TimeSpent
        long time = stackNode.getStackEvaluationTime();
        long t2 = (long) Math.round(time / 1000);
        String timeSpent = Long.toString(t2);
        str3 = str2 + "TimeSpent:" + timeSpent + ";" + System.getProperty("line.separator");
        
        return str3;
    }
    
    /**
     * Create the case notes string.
     * @param<code>StudyDbStackNode<code/> the selected stackNode.
     * @return <code>String<code/> the case stackNode notes string.
     */
    private String createCaseStackNotesString(StudyDbStackNode stackNode){
        String str = null;
        
        int cnt = stackNode.getItemCnt();
        String notesStr = stackNode.getNotes();
        
        if(notesStr != null)
            if(!notesStr.equalsIgnoreCase(""))
                str = "CaseNo:" + (cnt + 1) + "; " + "StackNotes:" +
                        //System.getProperty("line.separator") +
                        notesStr +
                        System.getProperty("line.separator");
        
        return str;
    }
    
    /**
     * Create and write the logfiles.
     * NOT IN USE
     */
    private void createAndWriteLogFiles(StudyDbStackNode stackNode, StudyDbImageNode imageNode) {
        String str = null;
        String studyType, studyTypeStr;
        String optionalSpecial = "";
        String optionalTagsStr;
        
        // create the log text
        String userName = history.getUserName();
        int stackCnt = stackNode.getItemCnt();
        int imageCnt = imageNode.getItemCnt();
        //String timeSpent = createEvaluationTime();
        String studyName = history.getStudyName();
        String imageName = imageNode.getStudyPath().getName();
        String studyUID = vgControl.studyDbUtility.getStudyUID();
        String patientID = vgControl.studyDbUtility.getPatientID();
        //String windowLevel = history.getWindowLevel();
        String windowLevel = "";
        
        // Localization
        String taskPanelResultLocalized = createTaskPanelResultLocalized(imageNode);

        if (stackNode.getStackType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE)
            optionalSpecial = createOptionalSpecial(imageNode);

        String optionalTags = createOptionalTag(imageNode);

        if (optionalTags.equalsIgnoreCase("")) {
            optionalTagsStr = "";
        } else {
            optionalTagsStr = optionalTags + ";";
        }

        // stack image
        if(stackNode.getNodeType() == NODE_TYPE_STACK &&
                stackNode.getStackType()== StudyDbStackType.STACK_TYPE_SINGLE_IMAGE){
            str = "img:" + 
                imageName +
                "; " +
                //"cntNo:" +
                //Integer.toString(stackCnt) +
                //"; " +
                //Integer.toString(imageCnt) +
                //"; "+ "SUID:" + studyUID + "; "
                //+ "patID:" + patientID + "; "
                //+ "w/l:" + windowLevel + "; "
                "Result(localized)" + taskPanelResultLocalized +
                " " +
                //+ "TimeSpent:" + timeSpent + "; "
                optionalSpecial + optionalTagsStr + System.getProperty("line.separator");
        }
        else
            if(stackNode.getNodeType() == NODE_TYPE_STACK &&
                stackNode.getStackType() == StudyDbStackType.STACK_TYPE_STACK_IMAGE){
            str = "img:" + 
                imageName +
                "; " +
                //"stackCnt:" +
                //Integer.toString(stackCnt) +
                //"; " +
                "imgNo:" +
                Integer.toString(imageCnt + 1) +
                "; " +
                //+ "SUID:" + studyUID + "; "
                //+ "patID:" + patientID + "; "
                //+ "w/l:" + windowLevel + "; "
                "Result(localized)" + taskPanelResultLocalized +
                " " +
                //+ "TimeSpent:" + timeSpent + "; "
                optionalSpecial + optionalTagsStr + System.getProperty("line.separator");
        }
            
        // write
        writeLogFile(str);
        writeLogFileBackup(str);
    }

    /**
     * Update the logfiles with the stackNode information.
     * NOT IN USE
     */
    private void updateStackNodeInfo(StudyDbStackNode stackNode){
        String str = null;
        String str2 = null;
        String str3 = null;
        String strResult = null;
        String taskResultNoLocalized = "1,4|2,5; ";
        
        int cnt = stackNode.getItemCnt();
        File nodePath = stackNode.getNodePath();
        String patientId = vgControl.studyDbUtility.getPatientID();
        
        String taskPanelResultNotLocalized = createTaskPanelResultNotLocalized(stackNode);
        
        // StackNo
        str = "StackNo:" + (cnt + 1) + "; " + "StackPath:" + nodePath + "; " + "patID:" + patientId + "; ";
        strResult = "Result(not localized):" + taskResultNoLocalized + ";";
        
        // w/l
        String str1 = vgControl.studyDbUtility.getWindowLevelAdjusted();
        str2 = str + strResult + "w/l:" + str1 + "; ";
        
        // TimeSpent
        long time = stackNode.getStackEvaluationTime();
        long t2 = (long) Math.round(time / 1000);
        String timeSpent = Long.toString(t2);
        str3 = str2 + "TimeSpent:" + timeSpent + ";" + System.getProperty("line.separator");
        
        // write
        writeLogFile(str3);
        writeLogFileBackup(str3);
    }

    /**
     * Write the log file.
     */
    private void writeLogFile(String str){
        if(str == null)
            return;

        String runMode = "";
        if(vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            runMode = "";
        else
            if(vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST)
                runMode = "demo";
            else
                if(vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
                    runMode = "edit";
        
        String[] path = vgControl.appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfilePath("log.log1-directory", runMode);
        
        if (!fileExist(path[0]))
            createDirectory(path[0]);

        try {
            FileWriter fileWriter = new FileWriter(path[1], true);
            fileWriter.write(str);
            fileWriter.close();
        } catch (Throwable e) {
            String str2 = "Log1: " + "Unable to create log1   " + path[1] + " System will exit";
            System.out.println("Error: VgLog.writeLogFile:" + str2);
            JOptionPane.showMessageDialog(vgControl.appMainAdmin.viewDex.canvas,
                    str2, "Error", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            System.exit(1);
        }
    }

    /*
     * Write the backup log file
     */
    private void writeLogFileBackup(String str){
        boolean dirExist = false;

        if(str == null)
            return;

        String runMode = "";
        if(vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            runMode = "";
        else
            if(vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST)
                runMode = "demo";
            else
                if(vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
                    runMode = "edit";

        String[] path = vgControl.appMainAdmin.viewDex.vgHistoryCreateUtil.getLogfilePath("log.log2-directory", runMode);
        
        if(!fileExist(path[0]))
            dirExist = createDirectory(path[0]);

        try{
            FileWriter fileWriter = new FileWriter(path[1], true);
            fileWriter.write(str);
            fileWriter.close();
        }catch(Throwable e){
            String str2 = "Unable to create log2:   " + path[1];
            System.out.println("Warning: VgLog.writeLogFileBackup:" + str2);
            JOptionPane.showMessageDialog(vgControl.appMainAdmin.viewDex.canvas,
                    str2, "Warning", JOptionPane.WARNING_MESSAGE);
            //e.printStackTrace();
            //System.exit(1);
        }
    }

    /**
     * Delete the logfiles.
     * NOT IN USE
     */
    public void deleteLog() {
        String log1Path = log1Root + File.separator + history.getUserName() + "-" + history.getStudyName() + ".txt";
        String log2Path = log2Root + File.separator + history.getUserName() + "-" + history.getStudyName() + ".txt";

        delete(log1Path);
    //delete(log2Path);
    }
    
    /**
     * Create the
     * taskPanelResult string for the localized tasks.
     * NOT IN USE
     */
    private String createTaskPanelResultLocalized(StudyDbImageNode imageNode) {
        String cordinateStr = "";
        String resultLocStr = "";
        String locStr = "";

        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();
        //printTaskPanelResultList(resultList, "100");
        double tsir = imageNode.getTimeStampImageRendering();

        //Sort the list according to the timestamp in the <code>VgTaskPanelResult<code/> object.
        Collections.sort(resultList);
        //printTaskPanelResultList(resultList, "101");
        
        // Create a str for the items with localization. 
        // Sort localization == true
        ArrayList<VgTaskPanelResult> list = sortResultListLocalization(resultList, true);
        //printTaskPanelResultList(list, "102");
        //System.exit(99);
        
        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelResult result = list.get(i);
            Point2D p = result.getPoint();
            TreeSet<VgTaskPanelResult> list2 = findResultItemForLocPointSortedList(list, p);
            //printTaskPanelResultList(list2, "104");
            
            cordinateStr = createCoordinateLocString(list2);
            //resultLocStr = createTaskResultLocString(list2, tsir);
            locStr = locStr + cordinateStr + resultLocStr;
            
            //System.out.println("cordinateStr = " + cordinateStr);
            //System.out.println("resultLocStr = " + resultLocStr);
            //System.out.println("locStr = " + locStr);
        }
        
        // Set the logDone status to false. The resultList will be used again.
        setLogDoneStatus(list, false);
        
        // Create a str for items with NO localization.
        // Sort localization == false
        /*
        ArrayList<VgTaskPanelResult> list20 = sortResultListLocalization(resultList, false);
        printTaskPanelResultList(list20, "201");
        
        TreeSet<VgTaskPanelResult> list21 = sortResultTaskListAccordingToTaskNumber(list20);
        String resultStr = createTaskResultString(list21);
        System.out.println("resultStr = " + resultStr);
        */
        
        //String finalStr = locStr + resultStr;
        String finalStr = locStr;
        System.out.println("finalStr = " + finalStr);
      
        return finalStr;
    }
    
    /**
     * Create the taskPanelResult string for not localized tasks.
     * @param <code>StudyDbStackNode<code/> the selected stackNode
     * @return <code>String<code/> the taskPanel result string for the no localization tasks.
     */
    private String createTaskPanelResultNotLocalized(StudyDbStackNode stackNode) {
        String resultStr = null;
        TreeSet<VgTaskPanelResult> list21 = null;
        
        ArrayList<VgTaskPanelResult> resultList = stackNode.getTaskPanelResultList();
        if (resultList.size() != 0){
            
            // Sort with argument 'localization = false'
            ArrayList<VgTaskPanelResult> list20 = sortResultListLocalization(resultList, false);
            //int size3 = list20.size();
            //printTaskPanelResultList(list20, "201");
            
            list21 = sortResultTaskListAccordingToTaskNumber(list20);
            
        }
        
        if(list21 == null)
            resultStr = ";";
        else
            resultStr = createTaskResultString(list21);
        
        return resultStr;
        
        //Sort the list according to the timestamp in the <code>VgTaskPanelResult<code/> object.
        //Collections.sort(resultList);
        //printTaskPanelResultList(resultList, "101");
    }
    
    /**
     * Create the taskPanelResult string for not localized tasks.
     * @param <code>StudyDbStackNode<code/> the selected stackNode
     * @return <code>String<code/> the taskPanel result string for the no localization tasks.
     * NOT IN USE
     */
    private String createTaskPanelResultNotLocalized_OLD(StudyDbStackNode stackNode) {
        String locStr = "";
        TreeSet<VgTaskPanelResult> list21 = null;
        
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        int size = imageNodeList.size();
        
        while(iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();
            int size2 = resultList.size();
            if (resultList.size() != 0){
                // Create a str for items with no localization.
                // Sort with argument 'localization = false'
                ArrayList<VgTaskPanelResult> list20 = sortResultListLocalization(resultList, false);
                int size3 = list20.size();
                //printTaskPanelResultList(list20, "201");
                
                list21 = sortResultTaskListAccordingToTaskNumber(list20);
            }
        }
        
        //ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();
        //printTaskPanelResultList(resultList, "100");

        //Sort the list according to the timestamp in the <code>VgTaskPanelResult<code/> object.
        //Collections.sort(resultList);
        //printTaskPanelResultList(resultList, "101");
        
        // Create a str for items with no localization.
        // Sort with argument 'localization = false'
        //ArrayList<VgTaskPanelResult> list20 = sortResultListLocalization(resultList, false);
        //printTaskPanelResultList(list20, "201");
        
        //TreeSet<VgTaskPanelResult> list21 = sortResultTaskListAccordingToTaskNumber(list20);
        
        String resultStr = createTaskResultString(list21);
        //System.out.println("resultStr = " + resultStr);
            
        //String finalStr = locStr + resultStr;
        //String finalStr = locStr;
        //System.out.println("finalStr = " + finalStr);
      
        return resultStr;
    }
    
    /**
     * Sort the <code>VgTaskPanelResult</code> list according to the localizationStatus.
     * @param ArrayList<VgTaskPanelResult> resultList
     * @param localization if <code>true</code> return a list with items localizationStatus == true, if <code>false</code> return a list with items localizationStatus == false.
     * @return a sorted <code>ArrayList<VgTaskPanelResult></code> list.
     */
    private ArrayList<VgTaskPanelResult> sortResultListLocalization(ArrayList<VgTaskPanelResult> resultList, boolean localization){
        ArrayList<VgTaskPanelResult> list = new ArrayList<VgTaskPanelResult>();
        
        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult node = resultList.get(i);
            if((node.getLocalizationStatus() && localization) ||
                    (!node.getLocalizationStatus() && !localization))
                list.add(node);
        }
        return list;
    }
    
     /**
     * Print
     */
    public void printTaskPanelResultList(ArrayList<VgTaskPanelResult> list, String str){
        VgTaskPanelResult node;
        String locStr, str2;
        
        System.out.println("Print: " + str);
        for(int i=0; i<list.size(); i++){
            node  = list.get(i);
            
            if(node.getLocalizationStatus()){
                locStr = "T";
                 str2 =
                    "itemCnt = " + node.getItemCnt() + ", " +
                    "imageNodeCnt = " + node.getImageNodeCnt() + ", " +
                    "locStatus = " + locStr + ", " +
                    "point.x = " + Math.round(node.getPoint().getX()) + ", " +
                    "point.y = " + Math.round(node.getPoint().getY()) + ", " +
                    "taskNo = " + node.getTaskNb() + ", " +
                    "selItem = " + node.getSelItem() + ", ";
            }
            else{
                locStr = "F";
                str2 =
                    "itemCnt = " + node.getItemCnt() + ", " +
                    "imageNodeCnt = " + node.getImageNodeCnt() + ", " +
                    "locStatus = " + locStr + ", " +
                    //"point.x = " + ", " +
                    //"point.y = " + ", " +
                    "taskNo = " + node.getTaskNb() + ", " +
                    "selItem = " + node.getSelItem() + ", ";
            }
            System.out.println(str2);
        }
        System.out.println("");
    }
    
    /**
     * Print
     */
    public void printTaskPanelResultList(TreeSet<VgTaskPanelResult> list, String str){
        VgTaskPanelResult node;
        String locStr, str2;
        
        //System.out.println("Print: " + str);
        Iterator iter = list.iterator();
        while(iter.hasNext()){
            node = (VgTaskPanelResult) iter.next();
            if(node.getLocalizationStatus()){
                locStr = "T";
                 str2 =
                    "itemCnt = " + node.getItemCnt() + ", " +
                    "imageNodeCnt = " + node.getImageNodeCnt() + ", " +
                    "locStatus = " + locStr + ", " +
                    "point.x = " + Math.round(node.getPoint().getX()) + ", " +
                    "point.y = " + Math.round(node.getPoint().getY()) + ", " +
                    "taskNo = " + node.getTaskNb() + ", " +
                    "selItem = " + node.getSelItem() + ", ";
            }
            else{
                locStr = "F";
                str2 =
                    "itemCnt = " + node.getItemCnt() + ", " +
                    "imageNodeCnt = " + node.getImageNodeCnt() + ", " +
                    "locStatus = " + locStr + ", " +
                    //"point.x = " + ", " +
                    //"point.y = " + ", " +
                    "taskNo = " + node.getTaskNb() + ", " +
                    "selItem = " + node.getSelItem() + ", ";
            }
            System.out.println(str2);
        }
        System.out.println("");
    }

    
    /**
     * NOT IN USE
     */
    private String createCordinateString(ArrayList<VgTaskPanelResult> list) {
        String str = "";

        if (!list.isEmpty()) {
            VgTaskPanelResult res = list.get(0);
            double x = res.getPoint().getX();
            double y = res.getPoint().getY();
            int x2 = (int) Math.round(x);
            int y2 = (int) Math.round(y);
            str = str + "(" + x2 + "," + y2 + ")";
        }
        return str;
    }
    
     /**
      * Create the coordinate localization string.
     */
    private String createCoordinateLocString(TreeSet<VgTaskPanelResult> list) {
        String str = "";

        if (!list.isEmpty()) {
            VgTaskPanelResult res = list.first();
            //VgTaskPanelResult res = list.get(0);
            /*
            double x = res.getPoint().getX();
            double y = res.getPoint().getY();
            int x2 = (int) Math.round(x);
            int y2 = (int) Math.round(y);
            */
            
            // this is compatible with the overlay code
            int x2 = (int) res.getPoint().getX();
            int y2 = (int) res.getPoint().getY();
            str = str + "(" + x2 + "," + y2 + ")";
        }
        return str;
    }

    /**
     */
    private String createTaskResultString(ArrayList<VgTaskPanelResult> list) {
        String str = "";

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelResult res = list.get(i);
            int task = res.getTaskNb();
            int item = res.getSelItem();

            // 1,1|
            str = str + Integer.toString(task + 1) + "," + Integer.toString(item + 1);
            if (i < list.size() - 1) {
                str = str + "|";
            }
            if (i == list.size() - 1) {
                str = str + ";" + "";
            }
        }
        str = "()" + str;
        return str;
    }
    
    /**
      * Create the result string for a task with 'localization == true'.
     * @param <code>TreeSet<VgTaskPanelResult><code/>
     * @return <codeString<code/> the task result string.
     */
    private String createTaskResultString(TreeSet<VgTaskPanelResult> list) {
        String str = "";
        
            Iterator iter = list.iterator();
            while(iter.hasNext()){
                VgTaskPanelResult res = (VgTaskPanelResult) iter.next();
                int task = res.getTaskNb();
                int item = res.getSelItem();
                // 1,1|
                str = str + Integer.toString(task + 1) + "," + Integer.toString(item + 1);
                if(iter.hasNext())
                    str = str + "|";
                else
                    str = str + ";" + "";
            }
            //str = "()" + str;
            return str;
    }
    
     /**
      * Create the result string for a task with localization == true.
     */
    private String createTaskResultLocString(TreeSet<VgTaskPanelResult> list,
            double tsir, boolean imageTimeStampLog) {
        String str = "";
        
        // Get properties
        propUtils = new AppPropertyUtils();
        Properties prop = history.getVgProperties();

        int imageTimeStampTemporalResolution = 0;
        int defValue2 = 0;
        String key2 = "image.timestamp.temporal.resolution";
        imageTimeStampTemporalResolution = propUtils.getPropertyIntegerValue(prop, key2);
        if (imageTimeStampTemporalResolution == 0)
            imageTimeStampTemporalResolution = defValue2;
        
        Iterator iter = list.iterator();
        while(iter.hasNext()){
            VgTaskPanelResult res = (VgTaskPanelResult) iter.next();
            int task = res.getTaskNb();
            int item = res.getSelItem();
            double tsl = res.getTimeStampLocalization();
            double t_msec = tsl - tsir;
            double t_sec = (t_msec / 1000);

            String t;
            DecimalFormat df;
            switch(imageTimeStampTemporalResolution){
                case 0:
                    df = new DecimalFormat("0");
                    t = df.format(t_sec);
                    break;
                case 1:
                    df = new DecimalFormat("0.0");
                    t = df.format(t_sec);
                    break;
                case 2:
                    df = new DecimalFormat("0.00");
                    t = df.format(t_sec);
                    break;
                case 3:
                    df = new DecimalFormat("0.000");
                    t = df.format(t_sec);
                    break;
                default:
                    df = new DecimalFormat("0");
                    t = df.format(t_sec);
            }

            if(imageTimeStampLog){
                str = str + Integer.toString(task + 1) + "," + Integer.toString(item + 1)
                        + "(" + t + " sec" + ")";
                if(iter.hasNext())
                    str = str + "|";
                else
                    str = str + ";" + "";
            }
            else{
                str = str + Integer.toString(task + 1) + "," + Integer.toString(item + 1);
                if(iter.hasNext())
                    str = str + "|";
                else
                    str = str + ";" + "";
            }
        }
        return str;
    }

    /**
      * Create the result string for a task with localization == true.
     */
    private String createTaskResultLocStringForStack(TreeSet<VgTaskPanelResult> list) {
        String str = "";

        Iterator iter = list.iterator();
        while(iter.hasNext()){
            VgTaskPanelResult res = (VgTaskPanelResult) iter.next();
            int task = res.getTaskNb();
            int item = res.getSelItem();
            // 1,1|
            str = str + Integer.toString(task + 1) + "," + Integer.toString(item + 1);
            if(iter.hasNext())
                str = str + "|";
            else
                str = str + ";" + "";
        }
        return str;
    }

    /**
     * Find the resulting objects for a specific localization mark.
     * Sort the list according to the taskNumber. 
     * Set the logDone status when an object is added to the list.
     */
    private TreeSet<VgTaskPanelResult> findResultItemForLocPointSortedList(ArrayList<VgTaskPanelResult> resultList, Point2D p) {
        VgTaskPanelResultComparator comp = new VgTaskPanelResultComparator();
        TreeSet<VgTaskPanelResult> list = new TreeSet<VgTaskPanelResult>(comp);
        for (int i = 0; i < resultList.size(); i++) {
            if (p == resultList.get(i).getPoint() && !resultList.get(i).getLogDone()) {
                VgTaskPanelResult node = resultList.get(i);
                //int selItem = node.getSelItem();
                //double x = node.getPoint().getX();
                //double y = node.getPoint().getY();
                node.setLogDone(true);
                list.add(node);
            }
        }
        return list;
    }
    
    /*
     * Set the <code>ArrayList<VgTaskPanelResult><code/> resultList logDone status.
     */
    private void setLogDoneStatus(ArrayList<VgTaskPanelResult> resultList, boolean status) {
        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult node = resultList.get(i);
            node.setLogDone(status);
        }
    }
    
    /**
     * Sort the list according to the taskNumber. 
     */
    private TreeSet<VgTaskPanelResult> sortResultTaskListAccordingToTaskNumber(ArrayList<VgTaskPanelResult> resultList) {
        VgTaskPanelResultComparator comp = new VgTaskPanelResultComparator();
        TreeSet<VgTaskPanelResult> list = new TreeSet<VgTaskPanelResult>(comp);
        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult node = resultList.get(i);
            list.add(node);
        }
        return list;
    }

    /**
     * 
     */
    private void setResultListStudyDoneStatus(StudyDbImageNode imageNode) {
        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult node = resultList.get(i);
            node.setLogDone(false);
        }
    }

    /**
     * Sort the objects acording to the taskNb.
     * zzzzzzzzzzzzzzzzzzzzzzzzzzzzzz Bad not working! Try toAttar and bubblesort.
     * NOT IN USE
     */
    private ArrayList<VgTaskPanelResult> sortResultList(ArrayList<VgTaskPanelResult> resultList) {
        ArrayList<VgTaskPanelResult> list = new ArrayList<VgTaskPanelResult>();
        for (int i = 0; i < resultList.size(); i++) {
            for (int j = 0; j < resultList.size(); j++) {
                VgTaskPanelResult node = resultList.get(j);
                if (node.getTaskNb() == i) {
                    list.add(node);
                }
            }
        }
        return list;
    }

    /**
     * Create the optional tag string.
     * @param <code>StudyDbImageNode<code/>
     * @return <code>String<code/> the optional tag string.
     */
    private String createOptionalTag(StudyDbImageNode imageNode) {
        String str = "";

        ArrayList<VgLogOptionalTag> logOptionalTagList = history.getLogOptionalTagList();

        Attributes dataset = imageNode.getDataset();
        for (int j = 0; j < logOptionalTagList.size(); j++) {
            VgLogOptionalTag item = logOptionalTagList.get(j);
            String tagStr = item.getTagStr();
            String tagText = item.getTagText();
            String tagValue = getDataElementName(dataset, tagStr);
            str = str + tagText + ":" + tagValue;

            if (j < logOptionalTagList.size() - 1) {
                str = str + ";" + " ";
            }
        }
        return str;
    }
     /**
     * Find out if all the Optional dicom values are the same in all the images.
     * This is quit hard to do because of all the different datatypes that exist.
      * TEST TEST
      * NOT COMPLEATED
     */
    private String optionalTagVariantExist(StudyDbStackNode stackNode) {
        String str = "";

        ArrayList<VgLogOptionalTag> logOptionalTagList = history.getLogOptionalTagList();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        int size = imageNodeList.size();
        
        while(iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            Attributes dataset = imageNode.getDataset();
            
        
        for (int j = 0; j < logOptionalTagList.size(); j++) {
            VgLogOptionalTag item = logOptionalTagList.get(j);
            String tagStr = item.getTagStr();
            String tagText = item.getTagText();
            String tagValue = getDataElementName(dataset, tagStr);
            
            Iterator<StudyDbImageNode> iter2 = imageNodeList.iterator();
            while(iter2.hasNext()) {
                StudyDbImageNode imageNode2 = iter.next();
                Attributes dataset2 = imageNode2.getDataset();
                String tagValue2 = getDataElementName(dataset, tagStr);
                
                // just compare the values !!
                // a dicom value can be a lot......
                // mayby there is a method out somewhere...
               
            }
        }
        }
            return str;
    }

    /**
     * Create the optional special string.
     * Current optional special properties are:
     * WW and WL when answers given.
     * Time spent viewing each image. (Time between
     * two activations of the Next image? button).
     */
    private String createOptionalSpecial(StudyDbImageNode imageNode) {
        String str = "";
        String windowLevel = null, timeSpent = null;
        ArrayList<VgLogOptionalSpecial> logOptionalSpecialList = history.getLogOptionalSpecialList();
        String[] key = new String[2];

        key[0] = "log.data.option-wl";
        key[1] = "log.data.option-timespent";

        // WindowLevel
        for (int i = 0; i < logOptionalSpecialList.size(); i++) {
            VgLogOptionalSpecial item = logOptionalSpecialList.get(i);
            String name = item.getName();
            String value = item.getValue();

            if (name.contains(key[0])) {
                if (value != null) {
                    if (value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("Y")) {
                        if (imageNode != null) {
                            int width = imageNode.getWindowWidthAdjusted();
                            int center = imageNode.getWindowCenterAdjusted();
                            windowLevel = Integer.toString(width) + "/" + Integer.toString(center);
                        }
                        break;
                    }
                }
            }
        }

        // TimeSpent
        for (int i = 0; i < logOptionalSpecialList.size(); i++) {
            VgLogOptionalSpecial item = logOptionalSpecialList.get(i);
            String name = item.getName();
            String value = item.getValue();

            if (name.contains(key[1])) {
                if (value != null) {
                    if (value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("Y")) {
                        if (imageNode != null) {
                            timeSpent = createEvaluationTime(imageNode);
                        }
                        break;
                    }
                }
            }
        }

        if (windowLevel != null)
            str = "w/l:" + windowLevel + "; ";

        if (timeSpent != null)
            str = str + "TimeSpent:" + timeSpent + "; ";

        //str = "w/l:" + windowLevel + "; " + "TimeSpent:" + timeSpent + "; ";
        return str;
    }

    /**
     * Create the time spent for evaluation of an image.
     */
    private String createEvaluationTime(StudyDbImageNode imageNode) {
        String totalTime = "0";

        long time = imageNode.getImageEvaluationTime();
        double s = time % (1000);
        double sf = (float) s / (1000);
        long s2 = (long) Math.round(time / 1000);
        totalTime = Long.toString(s2);

        return totalTime;
    }

    protected String getHistoryLoginTime() {
        String str = "";
        Date  date1 = new Date();
        Date date2 = new Date();

        date1 = history.getStudyLoginDate();
        date2 = new Date();

        if (date1 != null && date2 != null) {
            long time1 = date1.getTime();
            long time2 = date2.getTime();

            //long diff = 92123456;
            double diff = time2 - time1;
            float f = (float) diff / (1000 * 60 * 60 * 24);
            double d = diff / (1000 * 60 * 60 * 24);  // 1.0662437
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

    /**
     * Get the Data Element Name.
     *
     *@param dataset the class that hold all the Dicom overhead data.
     *@param tagstr the Data Element Tag (gggg,eeee), where gggg equates
     * to the Group Number and eeee equates to the Element Number
     * within that Group.
     *@return the Data element Name.
     */
    private String getDataElementName(Attributes dataset, String tagstr) {
        String str = "0x" + tagstr;
        int val = 0;
        String tagName = null;

        try {
            val = Integer.decode(str);
        } catch (NumberFormatException e) {
            System.out.println("StudyLoader:getTagValue: NumberFormatException");
        }

        if (dataset != null) {
            tagName = dataset.getString(val, null);
        }

        return tagName;
    }

//*****************************************************
//
//  file
//
//*****************************************************
    /**
     * Check if directory exist.
     *@param directory path
     *@return true if exist, false if not exist
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
        //System.out.println("Error: VgLog:fileExist");
        //System.exit(1);
        }
        return status;
    }

    /* create a directory */
    private boolean createDirectory(String path) {
        boolean status = false;

        try {
            File f = new File(path);
            status = f.mkdirs();
        } catch (Exception e) {
            System.out.println("Error: VgLog:createDirectory");
            System.exit(1);
        }
        return status;
    }

    /**
     * delete
     */
    private boolean delete(String path) {
        boolean status = false;
        try {
            File f = new File(path);
            if (f.exists()) {
                status = f.delete();
            }
        } catch (Exception e) {
            System.out.println("Error: VgLog:delete");
            System.exit(1);
        }
        return status;
    }
}


