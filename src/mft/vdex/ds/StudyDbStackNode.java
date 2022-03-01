




/*
 * StudyDbStackNode.java
 *
 * Created on den 19 juni 2007, 16:10
 *
 */

package mft.vdex.ds;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import mft.vdex.modules.vg.VgTaskPanelResult;
//import org.dcm4che.data.Dataset;
import org.dcm4che3.data.Attributes;


/**
 *
 * @author Sune Svensson
 */
public class StudyDbStackNode implements Serializable, Comparable{
    private int itemCnt = 0;
    private int selImageNodeCount = 0;
    private File nodePath;
    private String[] fileExtension;
    private int nodeType;
    private int stackType;
    private String nodeName;
    public ArrayList<StudyDbImageNode> imageNodeList = new ArrayList<StudyDbImageNode>();
    private ArrayList <VgTaskPanelResult> taskPanelResultList;
    private int biradsValue = -1;
    private int windowWidth;
    private int windowCenter;
    private AffineTransform atx;
    
     // evaluation time
    private long startTime = -1;
    private long stopTime = -1;
    private long totalTime = -1;
    
    private Attributes dataset;
    
    private String notes;
    
    /**
     * Creates a new instance of StudyDbStackNode
     */
    public StudyDbStackNode(int cnt, File nodePath, String[] fileExtension, int nodeType, int stackType){
        this.itemCnt = cnt;
        this.nodePath = nodePath;
        this.fileExtension = fileExtension;
        this.nodeType = nodeType;
        this.stackType = stackType;
        
        init();
    }
    
    /**
     * Creates a new instance of StudyDbStackNode
     */
    public StudyDbStackNode(int cnt, File nodePath, String nodeName, String[] fileExtension, int nodeType, int stackType){
        this.itemCnt = cnt;
        this.nodePath = nodePath;
        this.nodeName = nodeName;
        this.fileExtension = fileExtension;
        this.nodeType = nodeType;
        this.stackType = stackType;
        
        init();
    }
    
    private void init(){
        taskPanelResultList = createTaskPanelResultList();
    }
    
     /**
     * Returns the item cnt.
     *
     * @param return the item cnt.
     */
    public int getItemCnt(){
        return itemCnt;
    }
    
    public File getNodePath(){
        return nodePath;
    }
    
    public int getNodeType(){
        return nodeType;
    }
    
    public int getStackType(){
        return stackType;
    }
    
    /*
     * Get the nodeName.
     */
    public String getNodeName(){
        return nodeName;
    }
    
    /*
     * Set the nodeName.
     */
    public void setNodeName(String nodeName){
        this.nodeName = nodeName;
    }
    
    public String[] getFileExtension(){
        return fileExtension;
    }
    
    public void setImageNodeList(ArrayList<StudyDbImageNode> imagenodelist){
        imageNodeList = imagenodelist;
    }
    
    public ArrayList<StudyDbImageNode>getImageNodeList(){
        return imageNodeList;
    }
    
    public StudyDbImageNode getImageNode(int item){
        return imageNodeList.get(item);
    }
    
     /*
      * Set the <code>AffineTransform<code/> object.
      */
    public void setAffineTransform(AffineTransform at){
        atx = at;
    }
    
    /*
     * Get the <code>AffineTransform<code/> object.
     */
    public AffineTransform getAffineTransform(){
        return atx;
    }
    
    /**************************
     **************************/
    
    /**
     * Sets the item cnt.
     */
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }
    
    /**
     * Set the SelImageNodeCount.
     */
    public void setSelImageNodeCount(int item){
        selImageNodeCount = item;
    }
    
    public int getSelImageNodeCount(){
        return selImageNodeCount;
    }
    
    /**
     * Set the BIRADS [0,1] value
     */
    public void setBIRADSValue(int val){
        biradsValue = val;
    }
    
    /**
     */
     public int getBIRADSValue(){
        return biradsValue;
    }
     
      /**
      */
     public void setWindowWidth(int ww){
         windowWidth = ww;
     }
     
     /**
      */
     public int getWindowWidth(){
         return windowWidth;
     }
     
     /**
      */
     public void setWindowCenter(int wc){
         windowCenter = wc;
     }
     
     /**
      */
     public int getWindowCenter(){
         return windowCenter;
     }
     
      /**
     * Set the time when the evaluation start.
     */
    public void setStackEvaluationTimeStart(){
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Set the time when the evaluation stop.
     */
    public void setStackEvaluationTimeStop(){
        stopTime = System.currentTimeMillis();
        totalTime = (stopTime - startTime);
    }
    
    /**
     * Get the image evaluation time.
     * @return total number of millisecunds. 
     */
    public long getStackEvaluationTime(){
        return totalTime;
    }
    
     /**
      * Set dataset.
     */
    public void setDataset(Attributes attributes){
        dataset = attributes;
    }
    
    /**
     * Get dataset.
     */
    public Attributes getDataset(){
        return dataset;
    }
    
    /**
     * compareTo
     * @param obj
     * @return
     */
    public int compareTo_OLD(Object obj) {
        StudyDbStackNode item = (StudyDbStackNode)obj;
        
        if(itemCnt > item.itemCnt)
            return 1;
        else{
            if(itemCnt < item.itemCnt)
                return -1;
            else{
                if(itemCnt == item.itemCnt)
                    return 0;
            }
        }
        return 0;
    }
    
     /**
     * compareTo
     * @param obj
     * @return
     */
    public int compareTo(Object obj) {
        StudyDbStackNode item = (StudyDbStackNode)obj;
        int val = Integer.parseInt(item.nodeName);
        
        if(Integer.parseInt(nodeName) > Integer.parseInt(item.nodeName))
            return 1;
        else{
            if(Integer.parseInt(nodeName) < Integer.parseInt(item.nodeName))
                return -1;
            else{
                if(Integer.parseInt(nodeName) == Integer.parseInt(item.nodeName))
                    return 0;
            }
        }
        return 0;
    }
    
    /**********************************************************
     * taskpanel result list (none localization) 
     *********************************************************/
    /**
     * Create a list for the non localization answering results
     * from the task panel Checkboxes.
     */
    private ArrayList<VgTaskPanelResult> createTaskPanelResultList(){
        return new ArrayList<VgTaskPanelResult>();
    }
    
    /**
     * Set the none localization taskPanelResult list.
     */
    public void setTaskPanelResultList(ArrayList<VgTaskPanelResult> list){
        taskPanelResultList = list;
    }
    
    /*
     * Get the none localization taskPanelResult list.
     */ 
    public ArrayList<VgTaskPanelResult> getTaskPanelResultList(){
        return taskPanelResultList;
    }
    
    /*
     * Set the notePanel text.
     */
    public void setNotes(String s){
        notes = s;
    }
    
    /*
     * Get the notePanel text.
     */
    public String getNotes(){
        return notes;
    }
}
