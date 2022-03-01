/* @(#) CatImageDbControl.java 01/28/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.imageio;

import java.awt.image.*;
import java.io.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.*;


/**
 * This class find all images in the image database.
 * Gets the imagedb.root properties and the name
 * of the directory where to find the Study images.
 * Reads the image properties file (FROC studies).
 * @version 1.0   2004-03-31
 * @author Sune Svensson
 **/
public class CatImageDbControl{
    //private CatUserHistory userHistory;
    private File studyImageDbRoot;
    private String studyImageDbRoot_mod;
    private File studySelectedImageDbRoot;
    private List studyList;
    private String[] userProp;
    private Properties props;
    private int[] studySelectedStatus = new int[4];
    
    protected String fString[] = {".dcm"};
    protected String[][] fileInfoList = new String[1][3];
    
    
    public CatImageDbControl(){
        init();
    }
    
    public void init(){
        studyList = new ArrayList();
        //studySelectedStatus = userHistory.getStudySelectedStatus();  old
        //studyRootPath = getStudyRootPath2();
        //studySelectedImageDbRoot = getSelectedStudyImageDbRoot();  old
    }
    
    /** Find all files that ends with a ".dcm".
     */
    public List findAllStudy(String studyImageDbPath){
        String curDir = ".";
        File currentDir;
        String filelist[];
        
        File studyImageDbPathFile = new File(studyImageDbPath);
        try{
            //String study_home = "c:/imagedb/tcat_studies";
            //currentDir = new File(study_home);
            //currentDir = new File(".");
            //curDir = currentDir.getAbsolutePath();
            curDir = studyImageDbPathFile.getAbsolutePath();
        }catch (Exception e){
            
        }
        
        filelist = listFilteredFiles(curDir, fString);
        if(filelist != null)
            setStudy(studyImageDbPath, filelist);
        return studyList;
    }
    
    private String[] listFilteredFiles(String dir, String[] filt){
        try {
            File direct = new File(dir);
            File[] flist = direct.listFiles();
            if(flist == null)
                return null;
            String[] ftlist = filterFileNames(flist, filt);
            return ftlist;
        } catch (Exception e) {return null;}
    }
    
    private String[] filterFileNames(File filelist[], String fstr[]){
        Vector fl = new Vector();
        Vector finfo = new Vector();
        int j =0;
        int numfiles = filelist.length;
        
        for(int i=0; i< filelist.length;i++){
            try {
                if(filelist[i].isDirectory()) {
                    fl.add(filelist[i].getName()); j++;
                    continue;
                }
            } catch (Exception e) {continue;}
            
            String fi[] = new String[2];
            for(int k=0; k < fstr.length;k++){
                if(filelist[i].getName().endsWith(fstr[k])){
                    fl.add(filelist[i].getName());
                    fi[0] = filelist[i].getName();
                    long len = filelist[i].length();
                    int size = (int)(len/1000);
                    fi[1] = Integer.toString(size)+"k";
                    finfo.add(fi);
                    j++;
                    break;
                };
            }
        }
        int size = fl.size();
        String[] newfl = new String[size];
        size = finfo.size();
        fl.copyInto(newfl);
        fileInfoList = new String[size][3];
        finfo.copyInto(fileInfoList);
        return newfl;
    }
    
    /* NOT IN USE
    private File getStudyRootPath2(){
        Properties props = new Properties();
        props = new Properties();
     
        try{
            FileInputStream in = new FileInputStream("c:\\development\\TCat\\resources\\tcat-properties.properties");
            //FileInputStream in = new FileInputStream("CatResources.properties");
            props.load(in);
        }catch(IOException e){
            e.printStackTrace();
        }
        String str = props.getProperty("studydb.root");
        return new File(str);
    }*/
    
    private File getSelectedStudyImageDbRoot(){
        String str = null;
        String imageDbRoot = props.getProperty("imagedb.root");
        
        if(studySelectedStatus[0] == 1)
            str = "ICS";
        if(studySelectedStatus[1] == 1)
            str = "ROC";
        if(studySelectedStatus[2] == 1)
            str = "NET";
        if(studySelectedStatus[3] == 1)
            str = "MGT";
        if(studySelectedStatus[4] == 1)
            str = "DEM";
        
        File path = new File(imageDbRoot + File.separator + str);
        return path;
    }
    
    private void setStudy(String studyImageDbPath, String[] filelist){
        int numFiles = filelist.length;
        for(int i=0; i< filelist.length;i++){
            File path = new File(studyImageDbPath.toString() + File.separator + filelist[i]);
            studyList.add(path);
        }
    }
    
    public File getStudy(int index){
        File path = (File) studyList.get(index);
        return path;
    }
    
    public void printStudyList(){
        Iterator iter = studyList.iterator();
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
    }
    
    public File getStudyRootPath(){
        return studyImageDbRoot;
    }
    
    /*****************************************************************
     * Image properties
     ****************************************************************/
    public Properties readImageProperties(File path){
        Properties prop = new Properties();
        
        try{
            FileInputStream in = new FileInputStream(path);
            prop.load(in);
        }catch(IOException e){
            System.out.print("Error: CatStudyAdmin:readImageProperties: Can't find the image properties file");
            //System.exit(1);
        }
        return prop;
    }
}

