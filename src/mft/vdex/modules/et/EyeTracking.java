/* @(#) EyeTracking.java 03/14/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.et;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import mft.vdex.app.AppMainET;
import mft.vdex.app.AppPropertyUtils;
import mft.vdex.app.ViewDex;
import mft.vdex.util.UDPClient;

/**
 *
 * @author sune
 */
public class EyeTracking {
    ViewDex viewDex;

     // EyeTracking
    public UDPClient udpClient;
    boolean eyeTrackingStatus = false;
    private AppMainET appMainET;
    boolean runModeFlag = false;
    String eyeTrackingFilePath;
    boolean eyeTrackingRenderDuringLoopStatus;
    Color canvasETColor;
    // EyeTracking
    private ArrayList<VgEyeTrackingLog> eyeTrackingTmpList = null;

    public EyeTracking(ViewDex viewdex){
        this.viewDex = viewdex;
    }

    public void initEyeTracking() {

        //fix! Eyetracking not supported. The old code have been spared,
        // just in cased.
        if(viewDex.appMainAdmin.vgControl == null)
            return;

        // status
        String key = "eyetracking";
        String str = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key);
        if (str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y")) {
            eyeTrackingStatus = true;
        }

        // internetaddress
        String defValueHost = "localhost";
        String key2 = "eyetracking.hostname";
        String hostName = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key2);
        if (hostName.equals("")) {
            hostName = defValueHost;
        }

        // port
        String defValuePort = "4445";
        String key3 = "eyetracking.port";
        String port = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key3);
        if (port.equals("")) {
            port = defValuePort;
        }

        // message
        String defValueMsg = "rabbit";
        String key4 = "eyetracking.message";
        String msg = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key4);
        if (msg.equals("")) {
            msg = defValueMsg;
        }

        // filepath
        String defValueFilePath = "c:\\eyetracking";
        String key5 = "eyetracking.filepath";
        String filePath = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key5);
        if (filePath.equals("")) {
            filePath = defValueFilePath;
        }
        setEyeTrackingFilePath(filePath);

        // render of first image during loading of stack
        eyeTrackingRenderDuringLoopStatus = true;
        String key6 = "eyetracking.loadingstack.renderimage";
        String str6 = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key6);
        if (str6.equalsIgnoreCase("No") || str6.equalsIgnoreCase("N")) {
            eyeTrackingRenderDuringLoopStatus = false;
        }

        // canvas color during loading of stack
        int[] colorET = new int[3];
        String key7 = "eyetracking.loadingstack.canvas.color";
        colorET = viewDex.appPropertyUtils.getPropertyColorValue(viewDex.appProperty.getStudyProperties(), key7);
        if (colorET[0] == 0 && colorET[1] == 0 && colorET[2] == 0) {
            colorET[0] = AppPropertyUtils.defCanvasGrayColor[0];
            colorET[1] = AppPropertyUtils.defCanvasGrayColor[1];
            colorET[2] = AppPropertyUtils.defCanvasGrayColor[2];
        }

        canvasETColor = new Color(colorET[0], colorET[1], colorET[2]);

        if (eyeTrackingStatus) {
            udpClient = new UDPClient();
            udpClient.setHostName(hostName);
            udpClient.setPortName(port);
            //udpClient.setMessage(msg);
        }

        // Find if history exist
        boolean historyOrigExist = viewDex.vgHistoryMainUtil.exist();

        if (eyeTrackingStatus && !historyOrigExist) {
            appMainET = new AppMainET(viewDex);
            String id = appMainET.getID();
            String age = appMainET.getAge();
            String sex = appMainET.getSex();
            String dominantEye = appMainET.getDominantEye();

            String msg2 = "ET_REC";
            udpClient.setMessage(msg2);
            udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            /*
            String msg2 = "ET_REM" + " " + id + " " + age + " " + sex + " " + dominantEye;
            sendUDPMessage(msg2);
             */

            String msg3 = "ET_REM" + " " + id;
            udpClient.setMessage(msg3);
            udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg4 = "ET_REM" + " " + age;
            udpClient.setMessage(msg4);
            udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg5 = "ET_REM" + " " + sex;
            udpClient.setMessage(msg5);
            udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg6 = "ET_REM" + " " + dominantEye;
            udpClient.setMessage(msg6);
            udpClient.send();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            String msg7 = "ET_STP";
            udpClient.setMessage(msg7);
            udpClient.send();

            // Store in history object
            eyeTrackingTmpList = new ArrayList<VgEyeTrackingLog>();
            eyeTrackingTmpList.add(new VgEyeTrackingLog(id, age, sex, dominantEye));
            //history.setEyeTrackingList(list);
        }

        // to prevent flicker on the canvas
        if (eyeTrackingStatus && !eyeTrackingRenderDuringLoopStatus) {
            viewDex.canvas.initCanvasETColor(canvasETColor);
        }
    }

     // Create the exit message
    public void createEyeTrackingExitMsg() {
        ArrayList<VgEyeTrackingLog> list = new ArrayList<VgEyeTrackingLog>();
        String path = getEyeTrackingFilePath();

        if (eyeTrackingStatus && viewDex.appMainAdmin.vgControl.getVgHistory() != null) {
            list = viewDex.appMainAdmin.vgControl.getVgHistory().getEyeTrackingList();
            VgEyeTrackingLog item = list.get(0);
            String id = item.getId();
            if (id.equals("")) {
                id = "xxxxx";
            }

            String msg = "ET_SAV " + path + File.separator + id + ".idf";
            sendUDPMessage(msg);
        }
    }

    // set eyetracking filepath
    public void setEyeTrackingFilePath(String path) {
        eyeTrackingFilePath = path;
    }

    // get eyetracking filepath
    public String getEyeTrackingFilePath() {
        return eyeTrackingFilePath;
    }

    /*
     */
    public boolean getEyeTrackingStatus() {
        return eyeTrackingStatus;
    }

    /*
     */
    public boolean getEyeTrackingRenderDuringLoopStatus() {
        return eyeTrackingRenderDuringLoopStatus;
    }

    /*
     */
    public ArrayList<VgEyeTrackingLog> getEyeTrackingTmpList(){
        return eyeTrackingTmpList;
    }

     /**
     * Send an UDP message to the "Eye tracking system"
     * @param msg
     */
    public void sendUDPMessage(String msg) {
        if (eyeTrackingStatus && udpClient != null) {
            udpClient.setMessage(msg);
            udpClient.send();
        }
    }
}
