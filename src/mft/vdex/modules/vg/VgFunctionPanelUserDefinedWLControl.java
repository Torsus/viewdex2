/* @(#) VgFunctionPanelUserDefinedWLControl.java 04/02/2015
 *
 * Copyright (c) 2015 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.vg;

import java.io.Serializable;
import mft.vdex.app.AppPropertyUtils;

/**
 * The <code>VgFunctionPanelUserDefinedWL</code> class stores the
 * Function Panel window/level configuration read from the
 * vgstudy-xxxx.properties file.
 */
public class VgFunctionPanelUserDefinedWLControl implements Serializable{
    private String propertyName;
    private String name;
    private String value;
    private String font;
    private int windowWidth = 0;
    private int windowCenter = 0;
    
    
    // Creates a new instance
    public VgFunctionPanelUserDefinedWLControl(String propname, String name,
            String value, String font){
        this.name = name;
        this.value = value;

        setFont(font);
        parseWLValue(value);
        propertyName = parsePropertyName(propname);
    }
    
    /**
     * Get the name of the property.
     * @return the property name.
     */
    public String getPropertyName(){
        return propertyName;
    }
    
    /**
     */
    public String getName(){
        return name;
    }
    
    /**
     * Get the name of the font.
     * @return the name of the font..
     */
    public String getFont(){
        return font;
    }
    
    private void setFont(String f){
        if(f == null || f.equalsIgnoreCase(""))
            f = "Dialog-plain-8";
        else font = f;
    }
    
    public int getWindowWidth(){
        return windowWidth;
    }
    
    public int getWindowCenter(){
        return windowCenter;
    }

    private void parseWLValue(String str){
        AppPropertyUtils propUtils = new AppPropertyUtils();
        int[] val = propUtils.parsePropWLValue(str);
        windowWidth = val[0];
        windowCenter = val[1];
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
    
    private String parsePropertyName(String str){
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
