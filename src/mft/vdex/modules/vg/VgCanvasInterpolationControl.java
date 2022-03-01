/* @(#) VgCanvasInterpolationControl.java 11/05/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.vg;

import java.io.Serializable;


/**
 * The <code>VgFunctionInterpolationControl</code> class stores the
 * Interpolation configuration read from the vgstudy-xxxx.properties file. 
 */
public class VgCanvasInterpolationControl implements Serializable{
    private String defName;
    private String name;
   
            
    // Creates a new instance
    public VgCanvasInterpolationControl(String defname, String ip){
        this.defName = defname;
        this.name = ip;
    }
    
    /**
     * Return the interpolation default property name.
     * @return the name of the default property.
     */
    public String getDefaultName(){
        return defName;
    }
    
    /**
     * Return the interpolation property name.
     * @return the name of the property.
     */
    public String getName(){
        return name;
    }
}
