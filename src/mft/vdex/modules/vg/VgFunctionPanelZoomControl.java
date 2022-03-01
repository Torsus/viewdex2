/* @(#) VgFunctionPanelZoomControl.java 09/14/2005
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
 * The <code>VgFunctionPanelZoomControl</code> class stores the
 * FunctionPanelZoomControl configuration read from the
 * vgstudy-xxxx.properties file.
 */
public class VgFunctionPanelZoomControl implements Serializable{
    private String propName;
    private String propNamePart;
    private String propNameFont;
    private String propValue;
    private String contr;
    
    // Creates a new instance
    public VgFunctionPanelZoomControl(String propname, String propvalue, String font){
        this.propName = propname;
        this.propValue = propvalue;
        
        this.propNamePart = parsePropName(propname);
        propNameFont = setFont(font);
    }
    
    /**
     * Returns the name of the property.
     * @return the property name.
     */
    public String getPropName(){
        return propName;
    }
    
    /**
     * Returns the last part of the value of the property.
     * @return String the last part of the property value
     */
    public String getPropNamePart(){
        return propNamePart;
    }
    
    public String getPropNameFont(){
        return propNameFont;
    }
    
    /**
     * Returns the value of the property.
     * @return String the value of the property
     */
    public String getPropValue(){
        return propValue;
    }
    
    private String setFont(String font){
        String str;
        
        if(font == null || font.equalsIgnoreCase(""))
            str = "Dialog-plain-14";
        else str = font;
        
        return str;
    }
    
    private String parsePropName(String str){
        String charSeq = ",";
        String strN;
        
        //String str3 = str.replace('.', ',');
        String str3 = str.replace(".", ", ");
        String[] str4 = str3.split(charSeq);
        int len = str4.length;
        String[] text = new String[len];
        
        // Remove spaces etc ...
        for(int i=0; i<str4.length; i++){
            text[i] = str4[i].trim();
        }
        
        return text[len -2] + "." + text[len - 1];
    }
}
