/* @(#) VgLogOptionalSpecial.java 05/12/2003
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
 * The <code>VgLogOptionalSpecial</code> class stores the log
 * special option properties from the vgstudy-xxxx.properties file. 
 */
public class VgLogOptionalSpecial implements Serializable{
    private String name;
    private String value;
            
    /* Creates a new instance */
    public VgLogOptionalSpecial(String nAme, String val) {
        name = nAme;
        value = val;
    }
    
    public String getName(){
        return name;
    }
    
    public String getValue(){
        return value;
    }
}
