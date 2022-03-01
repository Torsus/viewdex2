/* @(#) CanvasControlMode.java 08/31/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.viewer;


/**
 * The ContextMenuMode interface specifies the display mode constants.
 */
public interface CanvasControlMode {
    public static final int NONE = 0;
    public static final int WINDOW_LEVEL = 1;
    public static final int MANIP_SCROLL_STACK = 2;
    public static final int MANIP_PAN = 3;
    public static final int MANIP_ZOOM_IN = 4;
    public static final int MANIP_ZOOM_OUT = 5;
    public static final int DISPLAY_TO_FIT = 6;
    public static final int DISPLAY_SCALED = 7;
    public static final int DISPLAY_ORIG = 8;
    public static final int DISPLAY_HALF_SIZE = 9;
    public static final int FLIP_NORMAL = 10;
    public static final int FLIP_LEFT_RIGHT = 11;
    public static final int FLIP_TOP_BOTTOM = 12;
    public static final int FLIP_TOP_BOTTOM_LEFT_RIGHT = 13;
    public static final int INTERPOLATION_NEAREST = 14;
    public static final int INTERPOLATION_BILINEAR = 15;
    public static final int INTERPOLATION_BICUBIC = 16;
    public static final int INTERPOLATION_BICUBIC2 = 17;
}

