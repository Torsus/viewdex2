/*
 * StudyDbUtility.java
 *
 * Copyright (c) 2017 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 * Created on 15 march 2017
 * Author Sune Svensson
 *
 */
package mft.vdex.ds;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import mft.vdex.app.AppMainAdmin;

public class StudyDbUtility {

    private AppMainAdmin appMainAdmin;

    public StudyDbUtility(AppMainAdmin appmainadmin) {
        this.appMainAdmin = appmainadmin;
    }

    /**
     * Get the rootstack.
     */
    public StudyDbStackNode getStudyDbStackNode(int cnt) {
        StudyDbStackNode stackNode = null;

        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (studyDbRootNodeList != null) {
            stackNode = studyDbRootNodeList.get(cnt);
        }
        return stackNode;
    }

    /**
     * Get the total number of stacks.
     * @returns the total number of stacks.
     */
    public int getTotalStackNodeCount() {
        int cnt = 0;

        if (appMainAdmin.viewDex.vgHistory != null) {
            ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
            cnt = studyDbRootNodeList.size();
        }
        return cnt;
    }

    /**
     * Get the selected <code>StudyDbStackNode<code/> stack node.
     *
     * @returns the selected node.
     */
    public StudyDbStackNode getSelectedStackNode() {
        StudyDbStackNode stackNode = null;

        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (studyDbRootNodeList != null) {
            int cnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
            stackNode = studyDbRootNodeList.get(cnt);
        }
        return stackNode;
    }

    /**
     * Get the selected <code>StudyDbImageNode<code/> image list.
     * @param the selected stack.
     * @returns the image list from the selected stack.
     */
    public ArrayList<StudyDbImageNode> getSelectedImageList() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        StudyDbStackNode stackNode = studyDbRootNodeList.get(selStackNodeCnt);
        if (stackNode != null) {
            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            return imageList;
        }
        return null;
    }

    /**
     * Get the selected <code>StudyDbImageNode<code/> imageNode.
     * The method get the selected stackNode and the selected imageNode
     * and return the imageNode object.
     * @returns the <code>StudyDbImageNode<code/> imageNode object.
     */
    public StudyDbImageNode getSelectedImageNode() {
        StudyDbImageNode imageNode = null;

        ArrayList<StudyDbStackNode> list = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (list != null) {
            int stackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
            StudyDbStackNode stackNode = list.get(stackNodeCnt);
            if (stackNode != null) {
                int imageNodeCount = stackNode.getSelImageNodeCount();
                ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
                if (imageNodeList != null) {
                    if (!imageNodeList.isEmpty()) {
                        imageNode = imageNodeList.get(imageNodeCount);
                    }
                }
            }
        }
        return imageNode;
    }

    /**
     * Get the selected <code>StudyDbImageNode<code/> count.
     * The method find the selected stack and return the selected image
     * node count.
     * @returns the image node count.
     *
     */
    public int getSelectedImageNodeCount() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        StudyDbStackNode stackNode = studyDbRootNodeList.get(selStackNodeCnt);
        int selImageNodeCount = stackNode.getSelImageNodeCount();
        return selImageNodeCount;
    }

    /**
     * Get the <code>StudyDbImageNode<code/>.
     * The method gets the selected stack and return the specified imageNode object.
     * @param nodeCnt - the number of the imageNode to return.
     * @returns the specified <code>StudyDbImageNode<code/> imageNode object.
     * NOT TESTED
     */
    public StudyDbImageNode getImageNode(int nodeCnt) {
        StudyDbImageNode imageNode = null;

        ArrayList<StudyDbStackNode> list = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (list != null) {
            int stackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
            StudyDbStackNode stackNode = list.get(stackNodeCnt);
            if (stackNode != null) {
                ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
                if (imageNodeList != null) {
                    if (!imageNodeList.isEmpty()) {
                        imageNode = imageNodeList.get(nodeCnt);
                    }
                }

            }
        }
        return imageNode;
    }

    /**
     * Set the selected imageNodeCount in the <code>StudyDbStackNode<code/>.
     * NOT IN USE
     */
    public void setSelectedImageNode(int cnt) {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        StudyDbStackNode stackNode = studyDbRootNodeList.get(selStackNodeCnt);
        stackNode.setSelImageNodeCount(cnt);
    }

    /**
     * Set the selected imageNodeCount to the first image in list.
     */
    public void setSelectedImageNodeCntToFirstImage() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        StudyDbStackNode stackNode = studyDbRootNodeList.get(selStackNodeCnt);
        if (stackNode != null) {
            stackNode.setSelImageNodeCount(0);
        }
    }

    /**
     * Set the selected imageNodeCount to the last image in list.
     */
    public void setSelectedImageNodeLast() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
        StudyDbStackNode stackNode = studyDbRootNodeList.get(selStackNodeCnt);
        if (stackNode != null) {
            ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
            int size = imageList.size();
            stackNode.setSelImageNodeCount(size - 1);
        }

    }

    /**
     * Check if there is a next node in the <code>StudyDbImageNode<code/> stack.
     * @return <code>true</code> if there is a next node in the stack
     * <code>false</code> if there is no next node in the stack.
     */
    public boolean nextImageNodeExist() {
        int cnt = 0;
        StudyDbStackNode stackNode = getSelectedStackNode();
        if (stackNode != null) {
            cnt = stackNode.getSelImageNodeCount();
            ArrayList<StudyDbImageNode> imageList = getSelectedImageList();
            if (cnt < imageList.size() - 1) {
                return true;
            } else {
                return false;
            }

        }
        return false;
    }

    /**
     * Check if next stack exist.
     * @return <code>true</code> if there is a next stack in the list.
     * <code>false</code> if there is no next stack in the list.
     */
    public boolean nextStackNodeExist() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();

        if (selStackNodeCnt < studyDbRootNodeList.size() - 1) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Check if stack exist.
     * @return <code>true</code> if the stack exist.
     * <code>false</code> otherwise.
     */
    public boolean stackExist(int cnt) {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (cnt >= 0 && cnt <= studyDbRootNodeList.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check for the last stack.
     * @return <code>true</code> if last stack.
     * <code>false</code> if not last stack.
     */
    public boolean stackLast(int cnt) {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (cnt == studyDbRootNodeList.size()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the next stack as the selected stack.
     *
     *@return <code>true<code/> if stack exist.
     *<code>false<code/> otherwise.
     */
    public boolean setNextSelectedStackNode() {
        if (!nextStackNodeExist()) {
            return false;
        } else {
            int cnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
            cnt++;
            appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(cnt);
            return true;
        }
    }

    /**
     * Set the selected image node count.
     *
     *@return <code>true<code/> if <code>StudyDbStackNode<code/> node exist
     *<code>false<code/> if <code>StudyDbStackNode<code/> node NOT exist.
     */
    public boolean setSelectedImageNodeCount(int cnt) {
        StudyDbStackNode stackNode = getSelectedStackNode();
        if (stackNode != null) {
            stackNode.setSelImageNodeCount(cnt);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Set the next image as the selected image.
     *
     *@return <code>true<code/> if <code>StudyDbStackNode<code/> node exist
     *<code>false<code/> if <code>StudyDbStackNode<code/> node NOT exist.
     */
    public boolean setNextSelectedImageNodeCount() {
        int cnt = 0;
        StudyDbStackNode stackNode = getSelectedStackNode();
        if (stackNode != null) {
            cnt = stackNode.getSelImageNodeCount();
        } else {
            return false;
        }
        stackNode.setSelImageNodeCount(++cnt);
        return true;
    }

    /**
     * Set the previous image as the selected image.
     *
     *@return <code>true<code/> if <code>StudyDbStackNode<code/> node exist
     *<code>false<code/> if <code>StudyDbStackNode<code/> node NOT exist.
     */
    public boolean setPrevSelectedImageNodeCount() {
        int cnt = 0;
        StudyDbStackNode stackNode = getSelectedStackNode();
        if (stackNode != null) {
            cnt = stackNode.getSelImageNodeCount();
        } else {
            return false;
        }

        stackNode.setSelImageNodeCount(--cnt);

        return true;
    }

    /**
     * Check if prev stack exist.
     * @return <code>true</code> if there is a prev stack in the list.
     * <code>false</code> otherwise.
     */
    public boolean prevStackNodeExist() {
        //ArrayList<StudyDbStackNode> studyDbRootNodeList = history.getStudyDbRootNodeList();
        int selStackNodeCnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();

        if (selStackNodeCnt - 1 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if a previous image exist in the stack.
     * @return <code>true</code> true if there is a previous image in the stack
     * <code>false</code> false if there is no previous image in the stack.
     */
    public boolean prevImageNodeExist() {
        StudyDbStackNode stackNode = getSelectedStackNode();
        int cnt = stackNode.getSelImageNodeCount();
        ArrayList<StudyDbImageNode> imageList = getSelectedImageList();

        if (cnt - 1 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the prev stack as the selected stack.
     *
     *@return <code>true<code/> if stack exist.
     *<code>false<code/> otherwise.
     */
    public boolean setPrevSelectedStackNode() {
        if (!prevStackNodeExist()) {
            return false;
        } else {
            int cnt = appMainAdmin.viewDex.vgHistory.getSelectedStackNodeCount();
            cnt--;
            appMainAdmin.viewDex.vgHistory.setSelectedStackNodeCount(cnt);
            return true;
        }
    }

    /**
     * Get the WindowLevel of the selected image
     * @return windowLevel the window/level of the seleceted image.
     */
    public String getWindowLevel() {
        String windowLevel = "";

        StudyDbImageNode imageNode = getSelectedImageNode();
        if (imageNode != null) {
            int[] width = imageNode.getWindowWidth();
            int[] center = imageNode.getWindowCenter();
            windowLevel =
                    Integer.toString(width[0]) + "/" + Integer.toString(center[0]);
        }

        return windowLevel;
    }

    /**
     * Get the adjusted WindowLevel value of the selected image
     * @return adjusted windowLevel value of the seleceted image.
     */
    public String getWindowLevelAdjusted() {
        String windowLevel = "";

        StudyDbImageNode imageNode = getSelectedImageNode();
        if (imageNode != null) {
            int width = imageNode.getWindowWidthAdjusted();
            int center = imageNode.getWindowCenterAdjusted();
            windowLevel =
                    Integer.toString(width) + "/" + Integer.toString(center);
        }

        return windowLevel;
    }

    /** IN-USE
     * Get the patientID of the selected image.
     * @return patientID the patientID of the seleceted image.
     */
    public String getPatientID() {
        String patientID = "";

        StudyDbImageNode imageNode = getSelectedImageNode();
        if (imageNode != null) {
            patientID = imageNode.getPatientID();
        }

        return patientID;
    }

    /** IN-USE
     * Get the studyInstanceUID of the selected image.
     * @return studyInstanceUID the studyInstanceUID of the seleceted image.
     */
    public String getStudyUID() {
        String studyUID = "";

        StudyDbImageNode imageNode = getSelectedImageNode();
        if (imageNode != null) {
            studyUID = imageNode.getStudyInstanceUID();
        }

        return studyUID;
    }

    /**
     * IN-USE Get the imagename of the selected image.
     * @return studyPathName the pathname.
     */
    public String getSelectedImageName() {
        File studyPath = null;
        String studyName = "";

        StudyDbImageNode imageNode = getSelectedImageNode();

        if (imageNode != null) {
            studyPath = imageNode.getStudyPath();
            studyName = studyPath.getName();
        }
        return studyName;
    }

    /**
     * Check if all the images in a stack has been loaded.
     */
    public boolean isImageStackLoaded() {
        int cnt = 0;
        StudyDbStackNode stackNode = getSelectedStackNode();

        ArrayList<StudyDbImageNode> imageList = stackNode.getImageNodeList();
        int size = imageList.size();

        for (int i = 0; i
                < imageList.size(); i++) {
            StudyDbImageNode imageNode = imageList.get(i);
            PlanarImage orgImage = imageNode.getOrgImage();
            if (orgImage != null) {
                cnt++;
            }

        }

        if (size == cnt) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Copy the imgOrg to the imgOrgBack.
     * If false a new WritableRaster will be created.
     * NOT IN USE
     */
    public void copyImgOrgToImgOrgBack() {
        StudyDbImageNode imageNode = getSelectedImageNode();
        PlanarImage imgOrg = imageNode.getOrgImage();
        TiledImage ti = new TiledImage(imgOrg, false);
        imageNode.setOrgBackImage(ti);
    }

    /**
     * Update the <code>StudyDbImageNode<code/> with user selections.
     * Set the ww and wc values that was set by the user, and the evaluation
     *  date, when the next image in the stack was selected. (Used for the log)
     */
    public void updateImageNode() {
        StudyDbImageNode imageNode = getSelectedImageNode();
        if (imageNode != null) {
            imageNode.setWindowWidthAdjusted(appMainAdmin.viewDex.canvas.getWindowWidth());
            imageNode.setWindowCenterAdjusted(appMainAdmin.viewDex.canvas.getWindowCenter());
            //imageNode.setImageEvaluationTimeStop();
            //studyLog.update();
        }
    }

    /**
     * Update the <code>StudyDbStackNode<code/> with user selections.
     * Set the ww and wc values set by the user, when the previous
     * image in the stack was selected.
     */
    public void updateStackNode() {
        StudyDbStackNode stackNode = getSelectedStackNode();
        if (stackNode != null) {
            int windowWidth = appMainAdmin.viewDex.canvas.getWindowWidthAdjusted();
            int windowCenter = appMainAdmin.viewDex.canvas.getWindowCenterAdjusted();
            AffineTransform atx = appMainAdmin.viewDex.canvas.getTransform();
            AffineTransform atx2 = (AffineTransform) atx.clone();
            stackNode.setWindowWidth(windowWidth);
            stackNode.setWindowCenter(windowCenter);
            stackNode.setAffineTransform(atx2);
        }
    }

    /**
     * Set the selected image.
     * Set the image count.
     */
    /*
    public void setImageSelected2() {
    StudyDbImageNode imageNode = getSelectedImageNode();
    if (imageNode != null) {
    setImage(imageNode);
    setImageCount();
    }
    }*/
    /**
     * Check if a next image exist in the stack.
     * @return <code>true</code> if there is another image to evaluate.
     * <code>false</code> if there is no more image to evaluate.
     */
    public boolean nextImageExist() {
        return nextImageNodeExist();
    }

    /**
     * Check if a next stack exist in the stack.
     * @return <code>true</code> if there is a next stack.
     * <code>false</code> if there is no stack.
     */
    public boolean nextStackExist() {
        return nextStackNodeExist();
    }

     /**
     * Check if stack exist.
     * @return <code>true</code> if the stack exist.
     * <code>false</code> otherwise.
     */
    public boolean stackExist() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = appMainAdmin.viewDex.vgHistory.getStudyDbRootNodeList();
        if (studyDbRootNodeList == null || (studyDbRootNodeList.isEmpty())) {
            return false;
        } else {
            return true;
        }
    }
}
