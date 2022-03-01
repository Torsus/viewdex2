/*
 * PropertiesUtils.java
 *
 * Created on den 20 april 2007, 10:10
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

package mft.vdex.app;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author Sune Svensson
 */

/****************************************************
 * Utilities for handling properties value.
 ****************************************************/

public class AppPropertyUtils {
    
    /** Creates a new instance of PropertiesUtils */
    public AppPropertyUtils() {
    }
    
    
     /*
      * getPropertyFontValue
      */
    public String getPropertyFontValue(Properties prop, String key){
        String s1 = "";
        
        if(prop.containsKey(key)){
            s1 = prop.getProperty(key).trim();
        }
        return s1;
    }
    
    /*
     * getPropertyColorValue
     */
    public int[] getPropertyColorValue(Properties prop, String key){
        String s1;
        int[] buf = {0,0,0};
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropColorValue(s2);
        }
        
        return buf;
    }
    
    /*
     * parsePropColorValue
     */
    public int[] parsePropColorValue(String str){
        int[] buf = {0,0,0};
        
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();
            
            if(cnt > 0 && cnt <= 3){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();
                    
                    try{
                        buf[j] = Integer.parseInt(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropColorValue: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }
    
    /*
     * Get a double value from the <code>Properties</code> class.
     * @param prop the <code>Properties</code> value.
     * @param key the property value.
     *
     * @return the buffer containing the property value.
     */
    public double[] getPropertyDoubleValueOLD(Properties prop, String key){
        String s1;
        double[] buf = {1.0,1.0,1.0};
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropDoubleValue(s2);
        }
        return buf;
    }
    
    /*
     * parsePropValue
     */
    public double[] parsePropDoubleValue(String str){
        double[] buf = {0.1,0.1,0.1};
        
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();
            
            if(cnt > 0 && cnt <= 3){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();
                    
                    try{
                        buf[j] = Double.parseDouble(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropDoubleValue: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }
    
    /*
      * Get an integer value from the <code>Properties</code> class.
      * @param prop the <code>Properties</code> value.
      * @param key the property value.
      *
      * @return the property value.
      */
    public int getPropertyIntegerValue(Properties prop, String key){
        String s1;
        int val = 0;
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            
            try{
                val = Integer.parseInt(s2);
            }catch(NumberFormatException e){
                if(!s2.contains(s2))
                    System.out.println("ViewDEX:getPropertyIntegerValue: NumberFormatException");
            }
        }
        return val;
    }
    
    /*
      * Get an double value from the <code>Properties</code> class.
      * @param prop the <code>Properties</code> value.
      * @param key the property value.
      *
      * @return the property value.
      */
    public double getPropertyDoubleValue(Properties prop, String key){
        double val = 0.0;
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            
            try{
                val = Double.parseDouble(s2);
            }catch(NumberFormatException e){
                if(!s2.contains(s2))
                    System.out.println("ViewDEX:getPropertyDoubleValue: NumberFormatException");
            }
        }
        return val;
    }
    
    /*
      * Get a String value from the <code>Properties</code> class.
      * @param prop the <code>Properties</code> value.
      * @param key the property value.
      *
      * @return the property value.
      */
    public String getPropertyStringValue(Properties prop, String key){
        String s1 = "";
        
        if(prop.containsKey(key))
            s1 = prop.getProperty(key).trim();
            
        return s1;
    }
    
    /*
     * getPropertySpaceValue
     */
    public int[] getPropertySpaceValue(Properties prop, String key){
        String s1;
        int[] buf = {5,5,5,5,5,5,5};
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropSpaceValue(s2);
        }
        
        return buf;
    }
    
    /*
     * parsePropSpaceValue
     */
    public int[] parsePropSpaceValue(String str){
        int[] buf = {5,5,5,5,5,5,5};
        
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();
            
            if(cnt > 0 && cnt <= 7){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();
                    
                    try{
                        buf[j] = Integer.parseInt(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropSpaceValue: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }
    
     /*
     * getPropertySizeValue
     */
    public int[] getPropertySizeValue(Properties prop, String key){
        String s1;
        int[] buf = {0,0,0,4};
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropSizeValue(s2);
        }
        return buf;
    }
    
    /*
     * parsePropBoxSizeValue
     */
    public int[] parsePropSizeValue(String str){
        int[] buf = {0,0,0,0};
        
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();
            
            if(cnt > 0 && cnt <= 4){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();
                    
                    try{
                        buf[j] = Integer.parseInt(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropValue: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }
    
    /*
     * getPropertyPositionValue
     */
    public int[] getPropertyPositionValue(Properties prop, String key){
        String s1;
        int[] buf = {0,0};
        
        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropPositionValue(s2);
        }
        return buf;
    }
    
    /*
     * parsePropBoxSizeValue
     */
    public int[] parsePropPositionValue(String str){
        int[] buf = {0,0};
        
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();
            
            if(cnt > 0 && cnt <= 2){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();
                    
                    try{
                        buf[j] = Integer.parseInt(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropPosition   Value: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }

    /*
     * getPropertyWLValue
     */
    public int[] getPropertyWLValue(Properties prop, String key){
        String s1;
        int[] buf = {0,0};

        String s2;
        if(prop.containsKey(key)){
            s2 = prop.getProperty(key).trim();
            buf = parsePropWLValue(s2);
        }
        return buf;
    }

    /*
     * parsePropWLValue
     */
    public int[] parsePropWLValue(String str){
        int[] buf = {0,0};

        if(str != null){
            StringTokenizer st = new StringTokenizer(str, ",");
            int cnt = st.countTokens();

            if(cnt > 0 && cnt <= 2){
                int j=0;
                while(st.hasMoreTokens()){
                    String str2 = st.nextToken().trim();

                    try{
                        buf[j] = Integer.parseInt(str2);
                        j++;
                    }catch(NumberFormatException e){
                        System.out.println("ViewDEX:parsePropPosition   Value: NumberFormatException");
                    }
                }
            }
        }
        return buf;
    }
    
     /**
     * Get the selStackNodeValue.
     */
    public int getPropertySelStackNodeValue(String str) {
        int value = Integer.MIN_VALUE;

        if (str != null) {
            try {
                value = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("PropertiesUtils.getSelStackNodeValue: NumberFormatException");
            }
        }
        return value;
    }
    
    public static int[] defCanvasColor = {0,0,0};
    public static int[] defCanvasGrayColor = {30,30,30};
    public static int[] defPanelColor = {240,240,240};
    public static int[] defTextColor = {0,0,0};
    public static int[] defButtonColor = {175, 175, 175};
    public static int[] defButtonTextColor = {10,10,10};
    public static int[] defTitleColor = {10,10,10};
    public static int[] defBorderColor = {199,221,242};
    //public static int[] defTitleLineBorderColor = {199,221,242};
    public static int[] defTitleLineBorderColor = {120,120,120};
    //public static int[] defCheckBoxBorderColor = {199,221,242};
    public static int[] defCheckBoxBorderColor = {20,20,20};
    public static int[] defCheckBoxSelectColor = {180,180,180};
    public static int[] defTaskPanelCheckBoxSize = {25, 25};
    public static int[] defFunctionPanelBorderTitleColor = {0,0,0};
    public static int[] defFunctionPanelBorderColor = {199,221,242};
    public static int[] defCanvasTextColor = {10,220,10};
    public static int[] defClarificationPanelColor = {236,233,216};
    public static int[] defNotificationPanelColor = {236,233,216};
    public static int[] defTextInputFieldColor = {230,230,230};
    
    public static int[] defCanvasDistanceMesurementLineColor = {255,255,10};
    public static int[] defCanvasVolumeMesurementLineColor = {255,255,10};
    public static int[] defCanvasPixelValueMeasurementLineColor = {255,255,10};
    public static int[] defCanvasLocalizationLineColor = {10,220,10};
    public static int[] defCanvasLocalizationPositionTextColor = {10,220,10};
}
