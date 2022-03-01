/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mft.vdex.modules.vg;

/**
 *
 * @author Sune Svensson
 */
public interface VgRunMode {
    public static final int NONE = 0;
    public static final int CREATE_NEW = 1;
    public static final int CREATE_EXIST = 2;
    public static final int CREATE_ERROR = 3;
    public static final int DEMO_NEW = 4;
    public static final int DEMO_EXIST = 5;
    public static final int DEMO_ERROR = 6;
    public static final int SHOW_NEW = 7;
    public static final int SHOW_EXIST = 8;
    public static final int SHOW_ERROR = 9;
    public static final int EDIT_NEW = 10;
    public static final int EDIT_EXIST = 11;
    public static final int EDIT_ERROR = 12;
}
