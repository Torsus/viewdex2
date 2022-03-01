/* @(#) VgCineLoopPanelControl.java 09/14/2005
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
 * The <code>VgFunctionPanelZoomModeControl</code> class stores the
 * FunctionPanelZoomMode configuration read from the
 * vgstudy-xxxx.properties file.
 */
public class VgCineLoopPanelControl implements Serializable{
    private String btnPropName;
    private String btnName;
    private String btnPropIntervalName;
    private int btnIntervalValue;
    
    public VgCineLoopPanelControl(String btnpropname, String btnname ,
            String btnpropintervalname, int btnintervalvalue){
        this.btnPropName = btnpropname;
        this.btnName = btnname;
        this.btnPropIntervalName = btnpropintervalname;
        this.btnIntervalValue = btnintervalvalue;
    }
     
    /**
     * Return the button property name.
     * @return the button property name.
     */
    public String getButtonPropName(){
        return btnPropName;
    }
    
    /**
     * Return the button name.
     * @return the button name.
     */
    public String getButtonName(){
        return btnName;
    }
    
    /**
     * Return the button interval property name.
     * @return <code>String</code> property name.
     */
    public String getButtonPropIntervalName(){
        return btnPropIntervalName;
    }
    
    /**
     * Return the button interval value.
     * @return <code>int</code> interval value.
     */
    public int getButtonIntervalValue(){
        return btnIntervalValue;
    }
}
    
   