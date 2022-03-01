/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mft.vdex.ds;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.io.Serializable;

/**
 *
 * @author sune
 */
public class StudyDbShape implements Serializable {
    private Shape shape;
    private GeneralPath gPath;
    private boolean drawingActiveStatus = false;
    private boolean drawingSelectStatus = false;
    private int itemCnt = 0;
    private int uid;
    
    public StudyDbShape(Shape shape){
        this.shape = shape;
    }
    
    public StudyDbShape(GeneralPath gPath){
        this.gPath = gPath;
    }

    /**
     */
    public Shape getShape(){
        return shape;
    }
    
     /**
     */
    public GeneralPath getPath(){
        return gPath;
    }
    
     /**
     */
    public void setDrawingActiveStatus(boolean sta) {
        drawingActiveStatus = sta;
    }
    
     /**
     */
    public boolean getDrawingActiveStatus() {
        return drawingActiveStatus;
    }
    
     /**
      * 
     */
    public void setDrawingSelectStatus(boolean sta) {
        drawingSelectStatus = sta;
    }
    
    /**
      * 
     */
    public boolean getDrawingSelectStatus() {
        return drawingSelectStatus;
    }
    
     /**
     */
    public void setItemCnt(int cnt) {
        itemCnt = cnt;
    }
    
     /**
     */
    public int getItemCnt() {
        return itemCnt;
    }
    
    /**
     * 
     */
    public void setUID(int uId){
        uid = uId;
    }
    
     /**
     * 
     */
    public int getUID(){
        return uid;
    }
}
