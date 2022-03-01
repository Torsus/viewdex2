/*
 * StudyDbROIV.java
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

 /* Created 20160216 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.ds;

import java.awt.Shape;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;

/**
 *
 * @author sune
 */
public class StudyDbROIVS {
    private Shape shape;
    
    public StudyDbROIVS(Shape s){
        this.shape = s;
    }
   
    /*
     */
    public Shape getShape(){
        return shape;
    }
    
    /*
     */
    public void updateShape(Shape s){
        shape = s;
    }
}
