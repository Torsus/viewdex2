/* @(#) VgFunctionPanelZoomModeControl.java 09/14/2005
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
public class VgFunctionPanelZoomModeControl implements Serializable{
    private String propName;
    private String propZoom;
    private String zoomModeName;
    private String propFontName;
    private double zoomValue;
    
    // Creates a new instance
    public VgFunctionPanelZoomModeControl(String propname, String propnamevalue,
            String propzoom, String propzoomvalue, String font){
        //this.zoomModeName = propnamevalue;
        this.propZoom = propzoom;
        
        zoomModeName = parseNameValue(propnamevalue);
        propName = parseProp(propname);
        propZoom = parseProp(propzoom);
        zoomValue = parseZoomValue(propzoomvalue);
        propFontName = setFont(font);
        
        /* Parse the checkbox text */
        //clarificationText = parseClarificationText(cText);
    }
    
    /**
     * Return the property name (part of the property name).
     * @return the property name.
     */
    public String getPropName(){
        return propName;
    }
    
    /**
     * Return the property name (part of the property name).
     * @return the property name.
     */
    public String getPropZoom(){
        return propZoom;
    }
    
    /**
     * Return the name of the zoom mode function.
     * @return the name of the zoom mode function.
     */
    public String getZoomModeName(){
        return zoomModeName;
    }
    
    /**
     * Return the name of the button font.
     * @return the name of the font.
     */
    public String getPropFontName(){
        return propFontName;
    }
    
    /**
     * Return the value of the zoom function.
     * @return the zoom value.
     */
    /**
     * Return the zoom magnification factor.
     * @return double the value of the magnification.
     */
    public double getZoomValue(){
        return zoomValue;
    }
    
    private String setFont(String font){
        String str;
        
        if(font == null || font.equalsIgnoreCase(""))
            str = "Dialog-plain-14";
        else str = font;
        
        return str;
    }
    
     private String parseNameValue(String str){
        //String charSeq = ",";
        //String strN;
        
        //String str3 = str.replace('.', ',');
        String str3 = str.replace('"', ' ');
        //str3 = str.replace('"', ' ');
        //String[] str4 = str3.split(charSeq);
        //int len = str4.length;
        //String[] text = new String[len];
        
        // Remove spaces etc ...
        //for(int i=0; i<str4.length; i++){
          //  text[i] = str4[i].trim();
        //}
        
        //return text[len - 3] + "." + text[len - 2] + "." + text[len - 1];
        return str3;
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
    
    /*
     * Parse the zoom value string.
     *
     * Return the zoom magnification value.
     * There are 3 names that have a special meaning.
     * FIT
     * Return value of -0.0.
     * FIT2
     * Return value of -2.0.
     * FIT3
     * Return value of -3.0.
     */
    private double parseZoomValue(String str){
        double val = 1.0;
        
        if(str.equalsIgnoreCase(""))
            return 1.0;
        
        if(str.equalsIgnoreCase("fit")){
            return -0.0;
        }else{
            if(str.equalsIgnoreCase("fit2")){
                return -2.0;
            }else{
                if(str.equalsIgnoreCase("fit3")){
                    return -3.0;
                }else{
                    try{
                        val = Double.valueOf(str);
                    } catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                
                return val;
            }
        }
    }
}
