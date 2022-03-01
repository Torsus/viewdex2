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

import mft.vdex.modules.et.VgEyeTrackingLog;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbStackNode;


/**
 * The <code>VgHistory</code> class stores the results
 * from the study and the status of an ongoing study.
 * This object is used as a persistent storage for the
 * application.
 * 
 * The taskPanelQuestionList <code>VgTaskPanelQuestion</code>
 * stores the Task panel questions and the checkbox label text
 * read from the vgstudy-xxxx.properties file. This list is used
 * to dynamical create the taskPanel GUI.
 * 
 * The taskPanelResultList <code>VgTaskPanelResult</code> stores
 * the answering results from the Task panel Checkbox.
 * 
 * The studyImageList <code>StudyDbImageNode</code> stores the
 * information about a single image (name, path, dicom meta
 * information ...) The list also stores the answering results
 * from the Task panel Checkbox.
 * 
 * The <code>StudyDbStackNode</code> studyDbRootNodeList stores the
 * <code>StudyDbImageNode</code> studyDbImageNodeList.
 */
public class VgHistory implements Serializable{
    private String userName;
    private String studyName;
    
    private Date studyLoginDate;
    private Date studyLogoutDate;
    private String studyLogInElapsedTime = "";
    private ArrayList<Integer> taskResult = new ArrayList<Integer>();
    //private int[] taskResult2 = new int[5];
    
    private int studyHistoryStatus = 0;
    private List studyList;
    private int selStackNode = 0;
    private ArrayList <VgTaskPanelQuestion> taskPanelQuestionList;
    private ArrayList <VgTaskPanelClarification> taskPanelClarificationList;
    private ArrayList <VgCineLoopPanelControl> cineLoopPanelControlList;
    private ArrayList <VgFunctionPanelZoomModeControl> functionPanelZoomModeList;
    private ArrayList <VgFunctionPanelZoomControl> functionPanelZoomControlList;
    private ArrayList <VgFunctionPanelWLControl> functionPanelWLList;
    private ArrayList <VgFunctionPanelUserDefinedWLControl> functionPanelUserDefinedWLList;
    private ArrayList <VgCanvasInterpolationControl> canvasInterpolationList;
    private ArrayList <VgLogOptionalTag> logOptionalTagList;
    private ArrayList <VgLogOptionalSpecial> logOptionalSpecialList;
    private ArrayList<StudyDbImageNode> studyImageList;
    private Properties vgProperties;
    
    private boolean initDialogStatus = true;
    private boolean studyDone;
    
    //private ArrayList<StudyDbStackNode> studyDbRootNodeListMaster;
    private ArrayList<StudyDbStackNode> studyDbStackNodeListOrig;
    private ArrayList<StudyDbStackNode> studyDbZeroNodeListOrig;
    private ArrayList<StudyDbStackNode> studyDbRootNodeList;
    private ArrayList<StudyDbStackNode> studyDbStackNodeList;
    private ArrayList<StudyDbStackNode> studyDbZeroNodeList;
    private ArrayList<StudyDbStackNode> studyDbZeroNodeAsStackList;
    
    // Multi-frame
    private ArrayList<StudyDbStackNode> studyDbZeroMfNodeListOrig;
    private ArrayList<StudyDbStackNode> studyDbZeroMfNodeAsStackListOrig;
    
    // EyeTracking
    private ArrayList<VgEyeTrackingLog> eyeTrackingList;
    
    
    /** Constructor
     */
    public VgHistory() {
    }
    
    public VgHistory(String userName, String studyname){
        this.userName = userName;
        this.studyName = studyname;
    }
    
     
     
    /** Set the user name */
    public void setUserName(String name){
        userName = name;
    }
    
    /** Get the user name */
    public String getUserName(){
        return userName;
    }
    
    /************************************************
     * New methods
     ***********************************************/
    
    public void setVgProperties(Properties prop){
        this.vgProperties = prop;
    }
    
    /**
     * Get the properties read from the
     * vgstudy-vgxx.properties file.
     *
     * @return the vgstudy properties.
     */
    public Properties getVgProperties(){
        return vgProperties;
    }
    
    // taskPanelQuestionList
    public void setTaskPanelQuestionList(ArrayList<VgTaskPanelQuestion> list){
        this.taskPanelQuestionList = list;
    }
    
    public ArrayList<VgTaskPanelQuestion> getTaskPanelQuestionList(){
        return taskPanelQuestionList;
    }
    
    // taskPanelClarificationList
    public void setTaskPanelClarificationList(ArrayList<VgTaskPanelClarification> list){
        this.taskPanelClarificationList = list;
    }
    
    public ArrayList<VgTaskPanelClarification> getTaskPanelClarificationList(){
        return taskPanelClarificationList;
    }
    
    // cineLoopPanelControlList
    public ArrayList<VgCineLoopPanelControl> getCineLoopPanelControlList(){
        return cineLoopPanelControlList;
    }
    
    public void setCineLoopPanelControlList(ArrayList<VgCineLoopPanelControl> list){
        this.cineLoopPanelControlList = list;
    }
    
    // functionPanelZoomControlList
    public void setFunctionPanelZoomControlList(ArrayList<VgFunctionPanelZoomControl> list){
        this.functionPanelZoomControlList = list;
    }
    
    public ArrayList<VgFunctionPanelZoomControl> getFunctionPanelZoomControlList(){
        return functionPanelZoomControlList;
    }
    
    // functionPanelZoomModeList
    public void setFunctionPanelZoomModeList(ArrayList<VgFunctionPanelZoomModeControl> list){
        this.functionPanelZoomModeList = list;
    }
    
    public ArrayList<VgFunctionPanelZoomModeControl> getFunctionPanelZoomModeList(){
        return functionPanelZoomModeList;
    }
    
    // functionPanelWLList
    public void setFunctionPanelWLList(ArrayList<VgFunctionPanelWLControl> list){
        this.functionPanelWLList = list;
    }
    
    public ArrayList<VgFunctionPanelWLControl> getFunctionPanelWLList(){
        return functionPanelWLList;
    }

    // functionPanelUserDefinedWLList
    public void setFunctionPanelUserDefinedWLList(ArrayList<VgFunctionPanelUserDefinedWLControl> list){
        this.functionPanelUserDefinedWLList = list;
    }
    
    public ArrayList<VgFunctionPanelUserDefinedWLControl> getFunctionPanelUserDefinedWLList(){
        return functionPanelUserDefinedWLList;
    }
    
    /**
     * Set the CanvasInterpolationList. This list contains the
     * interpolation types defined in the vgstudy-xxxx.properties file.
     */
    public void setCanvasInterpolationList(ArrayList<VgCanvasInterpolationControl> list){
        this.canvasInterpolationList = list;
    }
    
    /**
     * Get the CanvasInterpolationList. This list contains the
     * interpolation types defined in the vgstudy-xxxx.properties file.
     *
     * @return the list of the defined interpolation types.
     */
    public ArrayList<VgCanvasInterpolationControl> getCanvasInterpolationList(){
        return canvasInterpolationList;
    }
    
    /**
     * Set the <code>LogOptionalTag</code> list. This list contains the
     * log optional tags defined in the vgstudy-xxxx.properties file.
     */
    public void setLogOptionalTagList(ArrayList<VgLogOptionalTag> list){
        this.logOptionalTagList = list;
    }
    
    /**
     * Get the <code>VgLogOptionalTag</code> list. This list contains the
     * log optional tags defined in the vgstudy-xxxx.properties file.
     *
     * @return the <code>VgLogOptionalTag</code> list.
     */
    public ArrayList<VgLogOptionalTag> getLogOptionalTagList(){
        return logOptionalTagList;
    }
    
    /**
     * Set the <code>LogOptionalSpecial</code> list. This list contains the
     * log optional special properties defined in the vgstudy-xxxx.properties
     * file.
     */
    public void setLogOptionalSpecialList(ArrayList<VgLogOptionalSpecial> list){
        this.logOptionalSpecialList = list;
    }
    
    /**
     * Get the <code>VgLogOptionalSpecial</code> list. This list contains the
     * log optional special properties defined in the vgstudy-xxxx.properties
     * file.
     *
     * @return the <code>VgLogOptionalSpecial</code> list.
     */
    public ArrayList<VgLogOptionalSpecial> getLogOptionalSpecialList(){
        return logOptionalSpecialList;
    }
    
    // studyImageList
    // NOT IN USE
    public void setStudyImageList(ArrayList<StudyDbImageNode> list){
        this.studyImageList = list;
    }
    
    /**
     * Returns the <code>StudyDbImageNode</code> list.
     * @param return the <code>StudyDbImageNode</code> list.
     * NOT IN USE
     */
    public ArrayList<StudyDbImageNode> getStudyImageList(){
        return studyImageList;
    }
    
    // eyeTrackingList
    public void setEyeTrackingList(ArrayList<VgEyeTrackingLog> list){
        this.eyeTrackingList = list;
    }
    
    /**
     * Returns the <code>StudyDbImageNode</code> list.
     * @param return the <code>StudyDbImageNode</code> list.
     */
    public ArrayList<VgEyeTrackingLog> getEyeTrackingList(){
        return eyeTrackingList;
    }
    
    
    // ** Old stuff ---->
    public void setStudyHistoryStatus(int status){
        studyHistoryStatus = status;
    }
    
    /* Set the time elapsed between the current login and the previos one. */
    public void setStudyLogInElapsedTime(String str){
        studyLogInElapsedTime = str;
    }
    
    /**
     * Set the date when the user login to the study.
     *
     * @param the date <code>Date</code>
     */
    public void setStudyLoginDate(Date date){
        studyLoginDate = date;
    }
    
    /**
     * Set the date when the user logout from the study.
     *
     * @param the date <code>Date</code>
     */
    public void setStudyLogoutDate(Date date){
        studyLogoutDate = date;
    }
    
    
    public void setInitDialogStatus(boolean status){
        initDialogStatus = status;
    }
    
    /**
     * Set the studyDone status.
     *
     * <code>true</code> the study is done.
     * <code>false</code> the study is not done.
     */
    public void setStudyDone(boolean sta){
        studyDone = sta;
    }
    
    
    /******************************************
     * Getter methods
     ******************************************/
    
    /** Get the study name */
    public String getStudyName(){
        return studyName;
    }
    
    /* Get the study list */
    public List getStudyList(){
        return studyList;
    }
    
    /*
     * Set the selected stack count.
     */
    public void setSelectedStackNodeCount(int sel){
        selStackNode = sel;
    }
    /**
     * Return the selected stack count.
     *
     * @param return the selected stack count.
     */
    public int getSelectedStackNodeCount(){
        return selStackNode;
    }
    
    /* Get the date when the user logout occure. */
    public Date getStudyLogoutDate(){
        return studyLogoutDate;
    }
    
    /** Get the time elapsed between the current login
     * and the previos one.
     */
    public String getStudyLogInElapsedTime(){
        return studyLogInElapsedTime;
    }
    
    /* Get the date when the user login occure. */
    public Date getStudyLoginDate(){
        return studyLoginDate;
    }
    
    /**
     * Get the studyDone status.
     *
     * @return the studyDone status
     * <code>true</code> the study is done.
     * <code>false</code> the study is not done.
     */
    public boolean getStudyDone(){
        return studyDone;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> stack list.
     * List used to access the various nodes eg. <code>StudyDBStackNode<code/>
     * and <code>StudyDBImageNode<code/>.
     */
    public void setStudyDbRootNodeList(ArrayList<StudyDbStackNode> list){
        studyDbRootNodeList = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> stack list.
     *
     * @returns the stack list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbRootNodeList(){
        return studyDbRootNodeList;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> stack list.
     */
    public void setStudyDbStackNodeList(ArrayList<StudyDbStackNode> list){
        studyDbStackNodeList = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> stack list.
     *
     * @returns the stack list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbStackNodeList(){
        return studyDbStackNodeList;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> stackNodeListMaster
     */
    public void setStudyDbStackNodeListOrig(ArrayList<StudyDbStackNode> list){
        studyDbStackNodeListOrig = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> stackNodeListMaster.
     *
     * @returns the stack list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbStackNodeListOrig(){
        return studyDbStackNodeListOrig;
    }
    
     /**
     * Set the <code>StudyDbStackNode<code/> stack list.
     */
    public void setStudyDbZeroNodeList(ArrayList<StudyDbStackNode> list){
        studyDbZeroNodeList = list;
    }
    
     /**
     * Get the <code>StudyDbStackNode<code/> stack list.
     *
     * @returns the list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbZeroNodeList(){
        return studyDbZeroNodeList;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> stack list.
     */
    public void setStudyDbZeroNodeAsStackList(ArrayList<StudyDbStackNode> list){
        studyDbZeroNodeAsStackList = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> stack list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbZeroNodeAsStackList(){
        return studyDbZeroNodeAsStackList;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> zeroNodeListOrig.
     */
    public void setStudyDbZeroNodeListOrig(ArrayList<StudyDbStackNode> list){
        studyDbZeroNodeListOrig = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> zeroNodeListOrig.
     *
     * @returns the list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbZeroNodeListOrig(){
        return studyDbZeroNodeListOrig;
    }
    
    /**************************************************************
     * Multi-frame  NOT IN USE
     *************************************************************/
    
    /**
     * Set the <code>StudyDbStackNode<code/> zeroMfNodeListOrig.
     */
    public void setStudyDbZeroMfNodeListOrig(ArrayList<StudyDbStackNode> list){
        studyDbZeroMfNodeListOrig = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> zeroMfNodeListOrig.
     * @returns the list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbZeroMfNodeListOrig(){
        return studyDbZeroMfNodeListOrig;
    }
    
    /**
     * Set the <code>StudyDbStackNode<code/> zeroMfNodeAsStackListOrig.
     */
    public void setStudyDbZeroMfNodeAsStackListOrig(ArrayList<StudyDbStackNode> list){
        studyDbZeroMfNodeAsStackListOrig = list;
    }
    
    /**
     * Get the <code>StudyDbStackNode<code/> zeroMfNodeAsStackListOrig.
     * @returns the list.
     */
    public ArrayList<StudyDbStackNode> getStudyDbZeroMfNodeAsStackListOrig(){
        return studyDbZeroMfNodeAsStackListOrig;
    }
}
