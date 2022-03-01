/* @(#) SystemInfo.java 09/22/2006
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.util;

import java.io.IOException;
import java.util.Properties;

public class SystemInfo {
    
    /** Creates a new instance of SystemInfo */
    public SystemInfo() {
    }
    
    public void printSystemInfo(){
        try{
            Properties sysprops = System.getProperties();
            sysprops.store(System.out, "System Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
