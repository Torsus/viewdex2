/* @(#) VgLogOptionalTag.java 05/30/2005
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
 * The <code>VgLogOptionalTag</code> class stores the log
 * option properties from the vgstudy-xxxx.properties file. 
 */
public class VgLogOptionalTag implements Serializable{
    private String tagStr;
    private String tagName;
    private String tagText;
            
    /* Creates a new instance */
    public VgLogOptionalTag(String tagstr, String tagname, String tagtext) {
        tagName = tagname;
        tagText = tagtext;
        
        tagStr = parseTag(tagstr);
    }
    
    public String getTagStr(){
        return tagStr;
    }
    
    public String getTagName(){
        return tagName;
    }
    
    public String getTagText(){
        return tagText;
    }
    
    
    private String parseTag(String str){
        String charSeq = ",";
        String tag3 = "";
        
        String[] tag = str.split(charSeq);
        int len = tag.length;
        String[] tag2  = new String[len];
        
        // Remove spaces etc ...
        for(int i=0; i<tag2.length; i++){
            tag2[i] = tag[i].trim();
            tag3 = tag3.concat(tag2[i]);
        }
        
        return tag3;
    }
}
