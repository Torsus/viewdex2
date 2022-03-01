/* @(#) VgTaskPanelQuestion.java 05/30/2005
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
 * The <code>VgTaskPanelQuestion</code> class stores the Task
 * Panel Question text and the Checkbox label text read from
 * the vgstudy-xxxx.properties file. 
 */
public class VgTaskPanelQuestion implements Serializable{
    private String taskText;
    private String taskTextFont;
    private String[] checkBoxText;
    private String checkBoxTextFont;
    private boolean localization;
    private boolean mandatory;
    
            
    /* Creates a new instance of CriterialDefClass */
    public VgTaskPanelQuestion(String task, String checkbox,
            String taskfont, String checkboxfont, String loc, String man) {
        this.taskText = task;
        
        this.taskTextFont = setFont(taskfont);
        this.checkBoxTextFont = setFont2(checkboxfont);
        
        /* Parse the checkbox text */
        this.checkBoxText = parseRatingLabels(checkbox);
        //taskTextFont = parseTaskTextFont(taskfont);
        //checkBoxTextFont = parseRatingTextFont(checkboxfont);
        this.localization = parseLocalizationStatus(loc);
        this.mandatory = parseMandatoryStatus(man);
    }
    
    public String getTaskText(){
        return taskText;
    }
    
    public String getTaskTextFont(){
        return taskTextFont;
    }
    
    public String[] getCheckBoxText(){
        return checkBoxText;
    }
    
    public String getCheckBoxTextFont(){
        return checkBoxTextFont;
    }
    
   /*
    * 
    */
    private String[] parseRatingLabels(String str){
        String charSeq = ",";
        
        String[] boxText= str.split(charSeq);
        int len = boxText.length;
        String[] text = new String[len]; 
        
        // Remove spaces etc ...
        for(int i=0; i<boxText.length; i++){
            text[i] = boxText[i].trim();
        }
        return text;
    }
    
    private String setFont(String str){
        String font;
        
        if(str == null || str.equalsIgnoreCase(""))
            font = "SansSerif-plain-20";
        else font = str;
        
        return font;
    }
    
    private String setFont2(String str){
        String font;
        
        if(str == null || str.equalsIgnoreCase(""))
            font = "SansSerif-plain-16";
        else font = str;
        
        return font;
    }
    
    private String[] parseTaskTextFont(String str){
        String charSeq = ",";
        
        String[] fontText= str.split(charSeq);
        int len = fontText.length;
        String[] text = new String[len]; 
        
        // Remove spaces etc ...
        for(int i=0; i<fontText.length; i++){
            text[i] = fontText[i].trim();
        }
        return text;
    }
    
    private String[] parseRatingTextFont(String str){
        String charSeq = ",";
        
        String[] fontText= str.split(charSeq);
        int len = fontText.length;
        String[] text = new String[len]; 
        
        // Remove spaces etc ...
        for(int i=0; i<fontText.length; i++){
            text[i] = fontText[i].trim();
        }
        return text;
    }
    
    /**
     * 
     * @param loc
     */
    private boolean parseLocalizationStatus(String loc){
        boolean status = false;
        
        if(loc.equalsIgnoreCase("y"))
            status = true;
        else
            if(loc.equalsIgnoreCase("n"))
            status = false;
        
        return status;
    }
    
    /**
     * 
     * @param man
     */
    private boolean parseMandatoryStatus(String man){
        boolean status = false;
        
        if(man.equalsIgnoreCase("y"))
            status = true;
        else
            if(man.equalsIgnoreCase("n"))
            status = false;
        
        return status;
    }
    
    /**
     * Get localizationTaskStatus, a taskPanel property.
     */
    public boolean getLocalizationTaskStatus(){
        return localization;
    }
    
    public boolean getMandatoryTaskStatus(){
        return mandatory;
    }
}
