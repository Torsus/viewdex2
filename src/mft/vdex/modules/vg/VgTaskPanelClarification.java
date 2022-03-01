/* @(#) VgTaskPanelClarification.java 06/09/2005
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
import java.util.ArrayList;


/**
 * The <code>VgTaskPanelClarification</code> class stores the Task
 * Panel Clarification text read from the vgstudy-xxxx.properties file. 
 */
public class VgTaskPanelClarification implements Serializable{
    private String cText;
    private String[] clarificationText;
    private String[] clarificationTextFont = new String[3];
            
    /* Creates a new instance of CriterialDefClass */
    public VgTaskPanelClarification(String text, String textfont){
        this.cText = text;
        this.clarificationTextFont[0] = textfont;
        
        /* Parse the checkbox text */
        clarificationText = parseClarificationText(cText);
        clarificationTextFont = parseClarificationTextFont(textfont);
    }
    
    public String getClarificationHeadText(){
        return clarificationText[0];
    }
    
    public String getClarificationText(){
        return clarificationText[1];
    }
    
    public String[] getClarificationTextFont(){
        return clarificationTextFont;
    }
    
    private String[] parseClarificationText(String str){
        String charSeq = ",";
        
        String[] str2 = str.split(charSeq);
        int len = str2.length;
        String[] text = new String[len];
        
        // Remove spaces etc ...
        for(int i=0; i<str2.length; i++){
            text[i] = str2[i].trim();
            //text[i] = str2[i];
        }
        return text;
    }
    
    private String[] parseClarificationTextFont(String str){
        String charSeq = "-";
        String[] strDef = {"Dialog", "plain", "34"};
        
        //String str3 = str.replace('.', ',');
        //String str3 = str.replace(".", ", ");
        if(str == null || str.equals(""))
            return strDef;
        
        String[] str4 = str.split(charSeq);
        int len = str4.length;
        String[] text = new String[len];
        
        // Remove spaces etc ...
        for(int i=0; i<str4.length; i++){
            text[i] = str4[i].trim();
        }
        
        return text;
    }
}
