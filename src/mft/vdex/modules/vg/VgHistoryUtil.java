/* @(#) VgHistoryUtil.java 03/14/2003
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
import mft.vdex.app.ViewDex;

/**
 *
 * @author sune
 */
public class VgHistoryUtil {
     ViewDex viewDex;
    private boolean debug = false;

    public VgHistoryUtil(ViewDex viewdex) {
        this.viewDex = viewdex;
    }

     /* Check if directory exist
     */
    public boolean fileExist(String path) {
        boolean status = false;

        try {
            File f = new File(path);
            if (f.exists()) {
                status = true;
            } else {
                status = false;
            }

        } catch (Exception e) {
            System.out.print("Error: VgHistoryUtil.fileExist");
            System.exit(1);
        }
        return status;
    }

    /* Create a directory.
     */
    public boolean createDirectory(String path) {
        boolean status = false;

        try {
            File f = new File(path);
            status = f.mkdirs();
        } catch (Exception e) {
            System.out.print("Error: VgHistoryUtil.createDirectory");
            System.exit(1);
        }
        return status;
    }

    /* Delete a file.
     */
    public boolean fileDelete(String path) {
        boolean status = false;

        try {
            File f = new File(path);
            if (f.exists()) {
                status = f.delete();
            }

        } catch (Exception e) {
            System.out.print("Error: VgHistoryUtil.fileDelete");
            System.exit(1);
        }
        return status;
    }

    /**
     * delete
     */
    public boolean delete(String path) {
        boolean status = false;
        try {
            File f = new File(path);
            if (f.exists()) {
                status = f.delete();
            }

        } catch (Exception e) {
            System.out.print("Error: VgHistoryUtil.delete");
            System.exit(1);
        }
        return status;
    }
}
