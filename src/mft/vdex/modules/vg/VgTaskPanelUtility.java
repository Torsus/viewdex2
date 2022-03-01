/*
 * TaskPanelUtility.java
 *
 * Copyright (c) 2017 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 15 march 2017
 * Author Sune Svensson
 *
 */
package mft.vdex.modules.vg;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbStackNode;

/**
 *
 * @author sune
 */
public class VgTaskPanelUtility {

    ViewDex viewDex;

    public VgTaskPanelUtility(ViewDex viewdex) {
        viewDex = viewdex;
    }

    /**
     * Find out if any task is defined as localized.
     * @return <code>boolean<code/> true if localized is defined.
     * @return <code>boolean<code/> false if localized is not defined.
     */
    public boolean getTaskPanelLocalizationStatusExist() {
        boolean status = false;
        ArrayList<VgTaskPanelQuestion> questionList = viewDex.vgHistory.getTaskPanelQuestionList();

        for (int i = 0; i < questionList.size(); i++) {
            VgTaskPanelQuestion q = questionList.get(i);
            if (q.getLocalizationTaskStatus()) {
                status = true;
                break;
            }
        }
        return status;
    }

    /**
     * Set the task panel result. This value represents the
     * checkbox that is selected for a task. The results are
     * stored in the <code>VgTaskPanelResult</code> class.
     *
     *@param stackNodeCnt the selected stacknode
     *@param imageNodeCnt the selected image
     *@param taskNumber the number of the selected task.
     *@param selItem the selected checkbox.
     *@param localizationStatus
     *
     *
     * Do I need stackNode & imageNode in this class ???
     *
     */
    public void setTaskPanelResult(int stackNodeCnt, int imageNodeCnt, int taskNumber,
            int selItem, boolean localizationStatus) {
        Point2D selPoint = null;
        long timeStampLocalization = 0;
        
        int windowWidth = viewDex.canvas.getWindowWidthAdjusted();
        int windowCenter = viewDex.canvas.getWindowCenterAdjusted();

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        //ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();

        if (localizationStatus) {
            selPoint = viewDex.localization.getLocalizationActivePoint();
            timeStampLocalization = viewDex.localization.getLocalizationTimeStamp();
        }

        // Check if result item exist
        if (taskPanelResultItemExist(stackNodeCnt, imageNodeCnt, taskNumber, selPoint)) {
            for (int i = 0; i < taskPanelResultList.size(); i++) {
                VgTaskPanelResult resultItem = taskPanelResultList.get(i);
                if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                        && (resultItem.getImageNodeCnt() == imageNodeCnt)
                        && (resultItem.getTaskNb() == taskNumber)
                        && (resultItem.getPoint() == selPoint)
                        && (resultItem.getLocalizationStatus() == localizationStatus)) {
                    resultItem.setSelItem(selItem);
                    resultItem.setDate(new Date());
                    resultItem.setWindowLevel(windowWidth, windowCenter);
                    break;
                }
            }
        } else {
            taskPanelResultList.add(new VgTaskPanelResult(stackNodeCnt, imageNodeCnt,
                    taskNumber, selItem, selPoint, timeStampLocalization, windowWidth,
                    windowCenter, localizationStatus));
        }

        // set cnt
        for (int j = 0; j < taskPanelResultList.size(); j++) {
            VgTaskPanelResult item = taskPanelResultList.get(j);
            item.setItemCnt(j);
        }
    }

    /**
     * Check if the result item already has been created (that means
     * an answer has been given for that task).
     */
    public boolean taskPanelResultItemExist(int stackNodeCnt, int imageNodeCnt, int taskNumber, Point2D selPoint) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();
        for (int i = 0; i < taskPanelResultList.size(); i++) {
            VgTaskPanelResult resultItem = taskPanelResultList.get(i);
            if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                    && (resultItem.getImageNodeCnt() == imageNodeCnt)
                    && (resultItem.getTaskNb() == taskNumber)
                    && (resultItem.getPoint() == selPoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the task panel result for the no localization tasks.
     * This value represents the checkbox that is selected for a task.
     * The results are stored in the <code>VgTaskPanelResult</code> class,
     * in the <code>StudyDbStackNode<code/> class.
     *
     *@param stackNodeCnt the selected stacknode
     *@param imageNodeCnt the selected image
     *@param taskNumber the number of the selected task.
     *@param selItem the selected checkbox.
     *@param localizationStatus
     *
     *
     * Do I need stackNode & imageNode in this class ???
     *
     */
    public void setTaskPanelResultNoLocalization(int stackNodeCnt, int imageNodeCnt, int taskNumber,
            int selItem, boolean localizationStatus) {
        Point2D selPoint = null;
        long timeStampLocalization = 0;

        int windowWidth = viewDex.canvas.getWindowWidthAdjusted();
        int windowCenter = viewDex.canvas.getWindowCenterAdjusted();

        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<VgTaskPanelResult> taskPanelResultList = stackNode.getTaskPanelResultList();

        // Check if result item exist
        if (taskPanelResultItemExistNoLocalization(stackNodeCnt, imageNodeCnt, taskNumber, selPoint)) {
            for (int i = 0; i < taskPanelResultList.size(); i++) {
                VgTaskPanelResult resultItem = taskPanelResultList.get(i);
                if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                        && (resultItem.getTaskNb() == taskNumber)
                        && (resultItem.getPoint() == selPoint)
                        && (resultItem.getLocalizationStatus() == localizationStatus)) {
                    resultItem.setSelItem(selItem);
                    resultItem.setDate(new Date());
                    resultItem.setWindowLevel(windowWidth, windowCenter);
                    break;
                }
            }
        } else {
            taskPanelResultList.add(new VgTaskPanelResult(stackNodeCnt, imageNodeCnt,
                    taskNumber, selItem, selPoint, timeStampLocalization, windowWidth,
                    windowCenter, localizationStatus));
        }

        // set cnt
        for (int j = 0; j < taskPanelResultList.size(); j++) {
            VgTaskPanelResult item = taskPanelResultList.get(j);
            item.setItemCnt(j);
        }
    }

    /*
     * setTaskPanelResultEditSelect
     */
    public void setTaskPanelResultEditSelect(int stackNodeCnt, int imageNodeCnt,
            int taskNumber, int selItem, boolean localizationTaskStatus) {
        Point2D selPoint = null;
        long timeStampLocalization = 0;

        int windowWidth = viewDex.canvas.getWindowWidthAdjusted();
        int windowCenter = viewDex.canvas.getWindowCenterAdjusted();

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        //ArrayList<StudyDbLocalization> localizationList = imageNode.getLocalizationList();
        ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();

        //zzz old function! Replace or update!
        //selPoint = getLocalizationSelectPoint();
        Point2D selectedLocalizationPoint = viewDex.localization.getSelectedLocalizationPoint();

        // Check if result item exist
        if (taskPanelResultItemExist(stackNodeCnt, imageNodeCnt, taskNumber, selectedLocalizationPoint)) {
            for (int i = 0; i < taskPanelResultList.size(); i++) {
                VgTaskPanelResult resultItem = taskPanelResultList.get(i);
                if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                        && (resultItem.getImageNodeCnt() == imageNodeCnt)
                        && (resultItem.getTaskNb() == taskNumber)
                        && (resultItem.getPoint() == selectedLocalizationPoint)
                        && (resultItem.getLocalizationStatus() == localizationTaskStatus)) {
                    resultItem.setSelItem(selItem);
                    resultItem.setDate(new Date());
                    resultItem.setWindowLevel(windowWidth, windowCenter);
                    break;
                }
            }
        } else {
            taskPanelResultList.add(new VgTaskPanelResult(stackNodeCnt, imageNodeCnt,
                    taskNumber, selItem, selectedLocalizationPoint, timeStampLocalization,
                    windowWidth, windowCenter, localizationTaskStatus));
        }

        // set cnt
        for (int j = 0; j < taskPanelResultList.size(); j++) {
            VgTaskPanelResult item = taskPanelResultList.get(j);
            item.setItemCnt(j);
        }
    }

    /**
     * Check if the result item (no localization) already has been created
     * (that means an answer has been given for that task).
     */
    public boolean taskPanelResultItemExistNoLocalization(int stackNodeCnt, int imageNodeCnt, int taskNumber, Point2D selPoint) {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<VgTaskPanelResult> taskPanelResultList = stackNode.getTaskPanelResultList();
        for (int i = 0; i < taskPanelResultList.size(); i++) {
            VgTaskPanelResult resultItem = taskPanelResultList.get(i);
            if ((resultItem.getStackNodeCnt() == stackNodeCnt)
                    && (resultItem.getTaskNb() == taskNumber)
                    && (resultItem.getPoint() == selPoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update the task panel result (propagate the results to all the
     * images in a stack). The <code>int<code/> selItem value represents
     * the checkbox that is selected for a specific task. Update all
     * results that exists for the <code>taskPanelResultList</code> task
     * with localizationTaskStatus == false. The results are
     * stored in the <code>VgTaskPanelResult</code> class.
     *
     *@param stackNodeCnt the selected stacknode
     *@param imageNodeCnt the selected image
     *@param taskNumber the number of the selected task.
     *@param selItem the selected checkbox.
     *@return void
     *
     */
    public void updateTaskPanelResult(int stackNodeCnt, int imageNodeCnt, int taskNumber, int selItem) {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();

        while (iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

            for (int i = 0; i < resultList.size(); i++) {
                VgTaskPanelResult result = resultList.get(i);
                int taskNb = result.getTaskNb();

                if (taskNb == taskNumber) {
                    result.setSelItem(selItem);
                    break;
                }
            }
        }
    }

    /**
     * Check that all the answers, that is required for the tasks that have the
     * no localization property, is given.
     *
     * Requirements for going to the next case.
     * 1) No localizationActive status.
     * 2) All <code>VgTaskPanelResult</code> item must have the following fields
     * stackNodeCnt, imageNodeCnt, taskNumber and selItem, with values different
     * from [int -1].
     * 3) Task with property 'taskpanel.taskX.localization = n' must have an answer.
     * 4) Task with property 'taskpanel.taskX.localization = y', no test is possible.
     *
     * @return <code>bolean<code/> true if all answers are given.
     * Special case 1: if only localization tasks have been defined true is returned.
     * Special case 2: if no tasks have been defined true is returned.
     * @return <code>bolean<code/> false if NOT all answers are given.
     *
     */
    public boolean getTaskPanelTaskStatusNoLocalization() {
        boolean status = true;
        boolean answerExist;
        ArrayList<VgTaskPanelQuestion> questionList = viewDex.vgHistory.getTaskPanelQuestionList();

        for (int i = 0; i < questionList.size(); i++) {
            VgTaskPanelQuestion q = questionList.get(i);
            if (!q.getLocalizationTaskStatus()) {
                answerExist = taskPanelAnswerExist(i);
                if (answerExist == false) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }

    /**
     * Check if the answer specified by the taskNb, exist in the stack.
     * All <code>VgTaskPanelResult</code> lists must be checked. There
     * is one list for each <code>StudyDbImageNode<code/>, if any answers
     * are giving.
     */
    private boolean taskPanelAnswerExist(int taskNb) {
        boolean status = false;

        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        while (iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            status = TaskAnswerExist(imageNode, taskNb);
            if (status == true) {
                break;
            }
        }
        return status;
    }

    /**
     * Check if the answer specified by the taskNb, exist in the
     * <code>StudyDbImageNode<code/> imageNode, <code><VgTaskPanelResult></code>
     * resultList.
     * @param imageNode
     * @param taskNb
     * @return
     */
    private boolean TaskAnswerExist(StudyDbImageNode imageNode, int taskNb) {
        boolean status = false;
        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult result = resultList.get(i);
            if (!result.getLocalizationStatus()) {
                if (result.getStackNodeCnt() != -1
                        && result.getImageNodeCnt() != -1
                        && result.getTaskNb() == taskNb
                        && result.getSelItem() != -1) {
                    status = true;
                    break;
                }

            }
        }
        return status;
    }

    /**
     * Check if all the answers that is required for the Tasks,
     * with localization property to yes, has been given.
     * @param
     * @return
     */
    public boolean getTaskPanelActiveLocalizationResultStatus(int stackNodeCnt, int imageNodeCnt, Point2D selPoint) {
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
     * Get the taskPanelResultList of the selected stack image.
     *
     * @return <code>VgTaskPanelResult><code/>.
     */
    public ArrayList<VgTaskPanelResult> getTaskPanelResultList() {
        ArrayList<VgTaskPanelResult> resultList = new ArrayList<VgTaskPanelResult>();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();

        if (imageNode != null) {
            resultList = imageNode.getTaskPanelResultList();
        }

        return resultList;
    }

    /**
     * Delete taskPanelResult item.
     * @param p
     */
    public void deleteTaskPanelResultItem(Point2D selPoint) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();

        Iterator<VgTaskPanelResult> iter = taskPanelResultList.iterator();

        while (iter.hasNext()) {
            VgTaskPanelResult resultItem = iter.next();
            if (resultItem.getPoint() == selPoint) {
                iter.remove();
            }
        }
    }

    //****************************************************************
    // Created 20150225
    // Fix for Kerstin Lagerstrand study
    public boolean getTaskPanelLocalizationExist() {
        boolean status = false;

        ArrayList<VgTaskPanelQuestion> questionList = viewDex.vgHistory.getTaskPanelQuestionList();

        for (int i = 0; i < questionList.size(); i++) {
            VgTaskPanelQuestion q = questionList.get(i);
            if (q.getLocalizationTaskStatus()) {
                status = true;
                break;
            }
        }
        return status;
    }

    // Created 20150225
    // Fix for Kerstin Lagerstrand
    /**
     * ****** Need to rewrite *****
     * Check that all the answers, that is required for the tasks that have the
     * no localization property, is given.
     *
     * Requirements for going to the next case.
     * 1) No localizationActive status.
     * 2) All <code>VgTaskPanelResult</code> item must have the following fields
     * stackNodeCnt, imageNodeCnt, taskNumber and selItem, with values different
     * from [int -1].
     * 3) Task with property 'taskpanel.taskX.localization = n' must have an answer.
     * 4) Task with property 'taskpanel.taskX.localization = y', no test is possible.
     *
     * @return <code>bolean<code/> true if all answers are given.
     * Special case 1: if only localization tasks have been defined true is returned.
     * Special case 2: if no tasks have been defined true is returned.
     * @return <code>bolean<code/> false if NOT all answers are given.
     *
     */
    // Created 20150225
    // Fix for Kerstin Lagerstrand study
    public boolean getTaskPanelResultStatusForLocalization() {
        boolean status = false;
        boolean answerExist;
        ArrayList<VgTaskPanelQuestion> questionList = viewDex.vgHistory.getTaskPanelQuestionList();

        for (int i = 0; i < questionList.size(); i++) {
            VgTaskPanelQuestion q = questionList.get(i);
            if (q.getLocalizationTaskStatus()) {
                answerExist = taskPanelAnswerExistForLocalization(i);
                if (answerExist == true) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    // Created 20150225
    // Fix for Kerstin Lagerstrand study
    /**
     * Check if the answer specified by the taskNb, exist in the stack.
     * All <code>VgTaskPanelResult</code> lists must be checked. There
     * is one list for each <code>StudyDbImageNode<code/>, if any answers
     * are giving.
     */
    private boolean taskPanelAnswerExistForLocalization(int taskNb) {
        boolean status = false;

        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();
        while (iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            status = TaskAnswerExistForLocalization(imageNode, taskNb);
            if (status == true) {
                break;
            }
        }
        return status;
    }

    // Created 20150225 Kerstin Lagerstrand study
    /**
     * Check if the answer specified by the taskNb, exist in the
     * <code>StudyDbImageNode<code/> imageNode, <code><VgTaskPanelResult></code>
     * resultList.
     * @param imageNode
     * @param taskNb
     * @return
     */
    private boolean TaskAnswerExistForLocalization(StudyDbImageNode imageNode, int taskNb) {
        boolean status = false;
        ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult result = resultList.get(i);
            if (result.getLocalizationStatus()) {
                if (result.getStackNodeCnt() != -1
                        && result.getImageNodeCnt() != -1
                        && result.getTaskNb() == taskNb
                        && result.getSelItem() != -1) {
                    status = true;
                    break;
                }

            }
        }
        return status;
    }

     /**
     * Utility
     */
    public void printTaskPanelResultList(String str) {
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();
        printTaskPanelResultList(taskPanelResultList, str);
    }

     /**
     * Utility Print
     */
    public void printTaskPanelResultList(ArrayList<VgTaskPanelResult> list, String str) {
        VgTaskPanelResult resultNode;
        String locStr,
                str2;

        System.out.println("Print: " + str);
        for (int i = 0; i
                < list.size(); i++) {
            resultNode = list.get(i);

            if (resultNode.getLocalizationStatus()) {
                locStr = "T";
                str2 =
                        "itemCnt = " + resultNode.getItemCnt() + ", "
                        + "imageNodeCnt = " + resultNode.getImageNodeCnt() + ", "
                        + "locStatus = " + locStr + ", "
                        + "point.x = " + Math.round(resultNode.getPoint().getX()) + ", "
                        + "point.y = " + Math.round(resultNode.getPoint().getY()) + ", "
                        + "taskNb = " + resultNode.getTaskNb() + ", "
                        + "selItem = " + resultNode.getSelItem() + ", ";
            } else {
                locStr = "F";
                str2 =
                        "itemCnt = " + resultNode.getItemCnt() + ", "
                        + "imageNodeCnt = " + resultNode.getImageNodeCnt() + ", "
                        + "locStatus = " + locStr + ", "
                        + //"point.x = " + ", " +
                        //"point.y = " + ", " +
                        "taskNb = " + resultNode.getTaskNb() + ", "
                        + "selItem = " + resultNode.getSelItem() + ", ";
            }

            System.out.println(str2);
        }

        System.out.println("");
    }

    /**
     * Utility
     */
    public void printTaskPanelResultList(String mode, String str) {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();

        if (mode.equalsIgnoreCase("imageNode")) {
            printTaskPanelResultList2(imageNodeList, str);
        }
    }

     /**
     * Utility Print
     */
    public void printTaskPanelResultList2(ArrayList<StudyDbImageNode> imageNodeList, String str) {
        String locStr;

        System.out.println("Print: " + str);

        for (int i = 0; i
                < imageNodeList.size(); i++) {
            StudyDbImageNode imageNode = imageNodeList.get(i);
            String str2 = "imageNode: itemCnt = " + imageNode.getItemCnt() + " "
                    + "studyPath = " + imageNode.getStudyPath();
            System.out.println(str2);

            ArrayList<VgTaskPanelResult> taskPanelResultList = imageNode.getTaskPanelResultList();
            if (taskPanelResultList != null) {
                for (int j = 0; j
                        < taskPanelResultList.size(); j++) {
                    VgTaskPanelResult resultNode = taskPanelResultList.get(j);

                    if (resultNode.getLocalizationStatus()) {
                        locStr = "T";
                        str2 =
                                "ResultNode: itemCnt = " + resultNode.getItemCnt() + ", "
                                + "imageNodeCnt = " + resultNode.getImageNodeCnt() + ", "
                                + "locStatus = " + locStr + ", "
                                + "point.x = " + Math.round(resultNode.getPoint().getX()) + ", "
                                + "point.y = " + Math.round(resultNode.getPoint().getY()) + ", "
                                + "taskNb = " + resultNode.getTaskNb() + ", "
                                + "selItem = " + resultNode.getSelItem() + ", ";
                    } else {
                        locStr = "F";
                    }

                    str2 =
                            "ResultNode: itemCnt = " + resultNode.getItemCnt() + ", "
                            + "imageNodeCnt = " + resultNode.getImageNodeCnt() + ", "
                            + "locStatus = " + locStr + ", "
                            + //"point.x = " + ", " +
                            //"point.y = " + ", " +
                            "taskNb = " + resultNode.getTaskNb() + ", "
                            + "selItem = " + resultNode.getSelItem() + ", ";

                    System.out.println(str2);
                }

            }
        }
        System.out.println("");
    }
}
