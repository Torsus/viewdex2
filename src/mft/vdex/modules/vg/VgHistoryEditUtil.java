/* @(#) VgHistoryEditUtil.java 03/14/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.modules.vg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbStackNode;

/**
 *
 * @author sune
 */
public class VgHistoryEditUtil {

    ViewDex viewDex;
    private VgHistory history;

    public VgHistoryEditUtil(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

    /**
     * Create the edit history.
     * Copy and rename the original object.
     */
    public void create() {
        history = viewDex.vgHistoryMainUtil.readOriginalHistoryObject();
        writeEditHistory(history);
        writeEditHistoryBackup(history);
    }

    public void create(VgHistoryMainUtil vgHistoryMain) {
        history = vgHistoryMain.getHistory();
        writeEditHistory(history);
        writeEditHistoryBackup(history);

        // Do I need this
        String time = getHistoryLoginTime();
        history.setStudyLogInElapsedTime(time);
        history.setStudyLoginDate(new Date());
    }

    /**
     * Check if the edit history object exist.
     * @return boolean true if history-object exist else false.
     */
    public boolean exist() {
        boolean status = false;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "edit");

        if (viewDex.vgHistoryUtil.fileExist(path[1])) {
            status = true;
        }
        return status;
    }

    /**
     * Write the edit history object.
     */
    public void writeEditHistory(VgHistory hist) {
        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "edit");

        File fileHistoryPath = new File(path[1]);

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
            out.writeObject(hist);
            out.close();
        } catch (Exception e) {
            String str = "Unable to create the user history_object:  " + fileHistoryPath + "." + " System will exit.";
            System.out.print("Error: VgControl.writeEditHistory() " + str);
            JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Write the VgHistory backup object
     */
    public void writeEditHistoryBackup(VgHistory hist) {
        boolean dirExist = false;

        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log2-directory", "edit");

        dirExist = viewDex.vgHistoryUtil.fileExist(path[0]);
        if (!dirExist) {
            dirExist = viewDex.vgHistoryUtil.createDirectory(path[0]);
        }

        if (dirExist) {
            File fileHistoryPath = new File(path[1]);

            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileHistoryPath));
                out.writeObject(hist);
                out.close();
            } catch (Exception e) {
                String str = "Unable to create the user backup history_object  " + "\"" + fileHistoryPath + "\"" + ".";
                System.out.print("Error: VgControl.writeEditHistoryBackup() " + str);
                JOptionPane.showMessageDialog(viewDex.canvas, str, "Error", JOptionPane.ERROR_MESSAGE);
                //System.exit(1);
            }
        } else {
            String propName = "\"log.log2-directory\"";
            String str = "Log2 directory " + path[1] + " can not be created.";
            System.out.println("Warning: VgControl.writeEditlHistoryBackup() " + str);
            //JOptionPane.showMessageDialog(appMainAdmin.viewDex.canvas,
            //      str, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Read the edited history object. The object file is
     * only read from the 'log.log1-dirctory' property.
     */
    public VgHistory readEditHistoryObj() {
        String[] path = viewDex.vgHistoryCreateUtil.getLogfileHistoryPath("log.log1-directory", "edit");

        File path2 = new File(path[1]);
        VgHistory histObj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path2));
            histObj = (VgHistory) in.readObject();
            in.close();
        } catch (Exception e) {
            System.out.print("Error: VgControl:readEditHistoryObject(). Unable to read the app user history_object file");
            System.exit(1);
        }
        return histObj;
    }

    public void read() {
        history = readEditHistoryObj();
        String time = getHistoryLoginTime();
        history.setStudyLogInElapsedTime(time);
        history.setStudyLoginDate(new Date());
    }

    public VgHistory getHistory(){
        return history;
    }

    /**
     * Get the time elapsed since the last login.
     */
    protected String getHistoryLoginTime() {
        String str = "";
        Date date1 = new Date();
        Date date2 = new Date();

        date1 = history.getStudyLoginDate();
        date2 = new Date();

        if (date1 != null && date2 != null) {
            long time1 = date1.getTime();
            long time2 = date2.getTime();

            //long diff = 92123456;
            double diff = time2 - time1;
            float f = (float) diff / (1000 * 60 * 60 * 24);
            double d = diff / (1000 * 60 * 60 * 24); // 1.0662437
            long d2 = Math.round(diff / (1000 * 60 * 60 * 24)); // 1
            double h = diff % (1000 * 60 * 60 * 24); // 5 723 456
            double hf = (float) h / (1000 * 60 * 60); // 1.5898489
            long h2 = Math.round(h / (1000 * 60 * 60)); // 1
            double m = h % (1000 * 60 * 60); // 2 123 456
            double m2 = (float) m / (1000 * 60); // 35.390933
            long m3 = Math.round(m / (1000 * 60)); // 35
            double s = m % (1000 * 60); // 23 456
            double sf = (float) s / (1000); // 23.456
            long s2 = Math.round(s / 1000); // 23
            str = d2 + ":" + h2 + ":" + m3 + ":" + s2;
        }
        return str;
    }

    /*
     */
    public boolean studyDbRootNodeListExist() {
        ArrayList<StudyDbStackNode> studyDbRootNodeList = history.getStudyDbRootNodeList();
        if (studyDbRootNodeList == null || (studyDbRootNodeList.isEmpty())) {
            return false;
        } else {
            return true;
        }
    }
}
