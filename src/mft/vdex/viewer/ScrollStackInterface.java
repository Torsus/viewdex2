/*
 * ScrollStackInterface.java
 *
 * Created on den 24 augusti 2007, 10:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mft.vdex.viewer;

import java.awt.geom.Point2D;

/**
 *
 * @author sune
 */
public interface ScrollStackInterface {
    
    /**
     */
    public void scrollStackDown(int units);
    
    /**
     */
    public void scrollStackUp(int units);
    
     /**
      * Set the context menu constants.
     */
    public void setCanvasControlMode(int mode);
    
     /**
      * Get the context menu constants.
      * @return the context menu constants.
      */
    public int getCanvasControlMode();
    
     /**
     * Check if there is a <code>Localization.SELECT<code/> mark insde a
     * predefine circle with the mouse hot-point as the center of the circle.
     */
    public boolean localizationInsideShapeExist(Point2D p);
    
    /**
     * Check if there is a <code>Localization.SELECT<code/> on the image.
     */
    public boolean getLocalizationSelectStatus();
    
    /*
     * test
     */
    public void setCineLoopStatus(boolean status);
    
    /**
     * test
     */ 
    public void runStudyAsCineLoop();
    
    /*
     */
    public void stopStackLoadInBackground();
    
    /*
     */
    public void stopStudyCineLoop();
    
    /*
     */
    public boolean getImageLoadingWorkerStatus();
    
    /*
     */
    public boolean getCineLoopRunningStatus();
}
