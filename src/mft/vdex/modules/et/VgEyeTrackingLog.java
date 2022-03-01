/* @(#)  VgEyeTrackingLog.java 11/09/2010
 *
 * Copyright (c) 2010Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/**
 * @author sunsv1
 */

package mft.vdex.modules.et;
import java.io.Serializable;

/**
 * The <code>VgEyeTrackingLog</code> class stores the
 * eye tracking persistent data.
 */
public class VgEyeTrackingLog implements Serializable{
    private String id;
    private String age;
    private String sex;
    private String dominantEye;
    
    public VgEyeTrackingLog(String Id, String Age, String Sex, String Dominanteye){
        id = Id;
        age = Age;
        sex = Sex;
        dominantEye = Dominanteye;
    }
    
    public String getId(){
        return id;
    }
    
    public String getAge(){
        return age;
    }
    
    public String getSex(){
        return sex;
    }
    
    public String getDominantEye(){
        return dominantEye;
    }
}
