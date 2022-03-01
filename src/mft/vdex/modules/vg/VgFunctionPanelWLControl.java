/* @(#) VgFunctionPanelWLControl.java 09/14/2005
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
 * The <code>VgFunctionPanelWL</code> class stores the
 * Function Panel window/level configuration read from the
 * vgstudy-xxxx.properties file.
 */
public class VgFunctionPanelWLControl implements Serializable{
    private String propName;
    private String propValue;
    private String propFont;
    
    
    // Creates a new instance
    public VgFunctionPanelWLControl(String propname, String value, String font){
        this.propValue = value;
        
        setFont(font);
        //this.propName = parseValue(propname);
        propName = parseProp(propname);
    }
    
    /**
     * Get the name of the property.
     * @return the property name.
     */
    public String getPropName(){
        return propName;
    }
    
    /**
     * Get the name of the property.
     * @return the name of the property value.
     */
    public String getPropValue(){
        return propValue;
    }
    
    /**
     * Get the name of the font.
     * @return the name of the font..
     */
    public String getPropFont(){
        return propFont;
    }
    
    private void setFont(String font){
        if(font == null || font.equalsIgnoreCase(""))
            propFont = "Dialog-plain-8";
        else propFont = font;
    }
    
    private String parseValue(String str){
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
        
        return text[len - 3] + "." + text[len -2] + "." + text[len - 1];
    }
    
    private String parseProp(String str){
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
        
        return text[len - 3] + "." + text[len - 2] + "." + text[len - 1];
    }
}
