/* @(#) AppUser.java 08/30/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.app;


 /**
  * This class reads the user property and create a list of users.
  */ 
public class AppUser {
    private String user;
    private String study;
    
    /** Creates a new instance of AppUsers */
    public AppUser(String userName, String studyName) {
        user = userName;
        study = studyName;
    }
    
    public String getUser(){
        return user;
    }
    
    public String getStudy(){
        return study;
    }
}
