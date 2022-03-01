/* @(#) VgTaskPanelResult.java 05/30/2005
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
import java.io.Serializable;
import java.util.Date;

/**
 * The <code>VgTaskPanelResult</code> class stores
 * the answering results from the Task panel Checkbox. 
 */
public class VgTaskPanelResult implements Serializable, Comparable{
    private int stackNodeCnt = -1;
    private int imageNodeCnt = -1;
    private int taskNumber = -1;
    private int selItem = -1;
    private Point2D point;
    private Date date;
    boolean logDone = false;
    private int windowWidth;
    private int windowCenter;
    private boolean localizationStatus = false;
    private int itemCnt = 0;
    private long timeStampLocalization = 0;
    
    public VgTaskPanelResult(){    
    }
    
    public VgTaskPanelResult(int stacknodecnt, int imagenodecnt, int tasknumber,
            int selitem, Point2D p, long timestamplocalization, int ww, int wc, boolean localizationStatus){
        this.stackNodeCnt = stacknodecnt;
        this.imageNodeCnt = imagenodecnt;
        this.taskNumber = tasknumber;
        this.selItem = selitem;
        this.point = p;
        this.date = new Date();
        this.windowWidth = ww;
        this.windowCenter = wc;
        this.localizationStatus = localizationStatus;
        this.timeStampLocalization = timestamplocalization;
    }
    
     /**
     * Set the object cnt.
     */
    public void setItemCnt(int cnt){
        itemCnt = cnt;
    }
    
     /**
     * Get the object cnt.
     */
    public int getItemCnt(){
        return itemCnt;
    }
    
    /**
     * Returns the stackNodeCnt of the selected <code>StudyDbStackNode<code/>
     *
     * @return the selected cnt.
     */
    public int getStackNodeCnt(){
        return stackNodeCnt;
    }
    
     /**
     * Returns the imageNodeCnt of the selected <code>StudyDbImageNode<code/>
     *
     * @return the selected cnt.
     */
    public int getImageNodeCnt(){
        return imageNodeCnt;
    }
    
    /**
     * Returns the task number.
     *
     * @return the task number.
     */
    public int getTaskNb(){
        return taskNumber;
    }
    
    /**
     * Returns the selected item.
     *
     * @return the selected item.
     */
    public int getSelItem(){
        return selItem;
    }
    
     /**
     * Set the selected item.
     * @return
     */
    public void setSelItem(int item){
        selItem = item;
    }
    
    /**
     * Set the current adjusted w/l value.
     */
    public void setWindowLevel(int ww, int wc){
        windowWidth = ww;
        windowCenter = wc;
    }
    
    /**
     * Get the windowWidth value;
     */
    public int getWindowWidth(){
        return windowWidth;
    }
    
    /**
     * Get the windowCenter value;
     */
    public int getWindowCenter(){
        return windowCenter;
    }
    
    /**
     * Get the point.
     */
    public Point2D getPoint(){
        return point;
    }
    
    /**
     */
    public void setLogDone(boolean sta){
        logDone = sta;
    }
    
    /**
     */
    public boolean getLogDone(){
        return logDone;
    }
    
    /**
     */
    public void setLocalizationStatus(boolean status){ 
        localizationStatus = status;
    }
    
    /**
     */
    public boolean getLocalizationStatus(){ 
        return localizationStatus;
    }
    
    /**
     * Set the date.
     */
    public void setDate(Date d){
        date = d;
    }

    public long getTimeStampLocalization(){
        return timeStampLocalization;
    }
    
    
    /* compareTo */
    public int compareTo(Object obj) {
        int val = 0;
        VgTaskPanelResult item = (VgTaskPanelResult)obj;
        
        return date.compareTo(item.date);
        
        /*
        if(taskNumber > item.taskNumber)
            val = -1;
        else
            if(taskNumber < item.taskNumber)
                val =  1;
            else
                 if(taskNumber == item.taskNumber)
                    val =  0;
        return val;
        */
            
        /*
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
         */
    }
}
