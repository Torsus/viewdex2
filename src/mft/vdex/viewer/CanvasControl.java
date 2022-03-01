/* @(#) CanvasControl.java 11/05/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

 /*
 * @author Sune Svensson.
 */
package mft.vdex.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import javax.media.jai.Interpolation;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.modules.vg.VgHistory;
import mft.vdex.modules.vg.VgRunMode;
import mft.vdex.app.AppPropertyUtils;

/**
 * This class hides some implementation details of the mft.vdex.viewer package
 * from the mft.vdex.modules.vg package. Is this a good design? The canvas and
 * windowLevel is accessed direct from the mft.vdex.modules.vg.VgControl in the
 * startStudy --> setImage method. anyway.
 *
 */
public class CanvasControl implements CanvasControlMode, VgRunMode {

    private AppPropertyUtils propUtils;
    public ViewDex viewDex;
    // windowingMode
    private boolean windowingFixedMinimumStatus = false;
    private int windowingMode = WindowingMode.NONE;
    /**
     * The single instance of this singleton class.
     */
    private static CanvasContextMenu instance;
    // Window level
    private boolean WLActivatedInStack = false;

    public CanvasControl(ViewDex viewdex) {
        this.viewDex = viewdex;
        init();
    }

    /**
     * init
     */
    private void init() {
        //viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK); //mod 2008-06-26
        viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.NONE); //add 2008-06-26
        viewDex.windowLevel.setCanvasControlMode(CanvasControlMode.NONE);
        viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
        viewDex.zoom.setCanvasControlMode(CanvasControlMode.NONE);

        propUtils = new AppPropertyUtils();
    }

    /**
     *
     */
    public void setInit() {

        // ScrollstackMode
        // cineLoopStatus
        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        boolean cineLoopStatus = false;
        String key = "cineloop";
        String str = propUtils.getPropertyStringValue(prop, key);
        if (str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y")) {
            cineLoopStatus = true;
        }

        if (cineLoopStatus) {
            //viewDex.canvasContextMenu.setScrollStackMode(1);
            viewDex.canvasContextMenu.setCineLoopMenuCreate(1);
            viewDex.canvasContextMenu.setCineLoopStartItemCreate(1);
            viewDex.canvasContextMenu.setCineLoopStopItemCreate(1);
        } else {
            //viewDex.canvasContextMenu.setScrollStackMode(0);
            viewDex.canvasContextMenu.setCineLoopMenuCreate(0);
            viewDex.canvasContextMenu.setCineLoopStartItemCreate(0);
            viewDex.canvasContextMenu.setCineLoopStopItemCreate(0);
        }

        //viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK); //Mod 2008-06-26
        viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.NONE);  //add 2008-06-26
        //viewDex.windowLevel.setCanvasControlMode(CanvasControlMode.NONE);
        viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
        viewDex.zoom.setCanvasControlMode(CanvasControlMode.NONE);
    }

    /**
     * ************************************************
     * Window/Level
     ************************************************
     */
    /**
     * Set the w/l to the values find in the DICOM element.
     */
    public void wlReset() {
        int[] val = viewDex.appMainAdmin.vgControl.getSelImageWLDefault();
        viewDex.windowLevel.setWindowLevel(val[0], val[1]);

        setWLActivatedInStack(false);
    }

    /**
     * Set the w/l values to be used by the buttons defined by the properties
     * "vgstudy.vg01.functionpanel.wl.preset.name.1" ... NOT IN USE
     */
    public void wlPreset(int[] val) {
        String str = viewDex.appProperty.getStudyName();
        viewDex.windowLevel.setWindowLevel(val[0], val[1]);
    }

    /**
     * Set the window/level <code>CanvasControlMode<code/>.
     *
     * @param 0 <code>CanvasControlMode.NONE<code/>
     * @param 1 <code>CanvasControlMode.WINDOW_LEVEL<code/>
     */
    public void setWLMode(int mode) {
        if (mode == 0) {
            viewDex.windowLevel.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.canvasContextMenu.setWLMode(0);
        }
        if (mode == 1) {
            viewDex.windowLevel.setCanvasControlMode(CanvasControlMode.WINDOW_LEVEL);
            viewDex.canvasContextMenu.setWLMode(1);
        }
    }

    /**
     * Set the window/level mouse motion map constant.
     */
    public void setWindowLevelMapConstant(double val) {
        viewDex.windowLevel.setMapConstant(val);
    }

    /**
     * Set WLActivatedInStack.
     */
    public void setWLActivatedInStack(boolean val) {
        WLActivatedInStack = val;
    }

    public boolean getWLActivateInStack() {
        return WLActivatedInStack;
    }

    /**
     * *********************************************
     * Scroll stack
     *********************************************
     */
    /**
     * Set the scroll stack <code>CanvasControlMode<code/>.
     *
     * @param 0 <code>CanvasControlMode.NONE<code/>
     * @param 1 <code>CanvasControlMode.MANIP_SCROLL_STACK<code/>
     * NOT IN USE DELETE
     */
    public void setScrollStackInitMode(int mode) {
        if (mode == 0) {
            //viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.canvasContextMenu.setScrollStackMode(0);
        }
        if (mode == 1) {
            viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.canvasContextMenu.setScrollStackMode(1);
        }
    }

    /**
     * ********************************************
     * Pan
     ********************************************
     */
    /**
     * Set the pan <code>CanvasControlMode<code/>.
     *
     * @param 0 <code>CanvasControlMode.NONE<code/>
     * @param 1 <code>CanvasControlMode.MANIP_PAN<code/>
     */
    public void setPanMode(int mode) {
        if (mode == 0) {
            viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.canvasContextMenu.setPanMode(0);
        }
        if (mode == 1) {
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.canvasContextMenu.setPanMode(1);
        }
    }

    public void setPanControlAction(String mode) {
        if (mode.equalsIgnoreCase("pan")) {
            viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.distance.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.area.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.pixelValueMean.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
        } else if (mode.equalsIgnoreCase("reset")) {
            viewDex.canvas.resetTranformation();
            viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
            viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            viewDex.localization.setLocalizationOverlayListInCanvas();
            viewDex.windowLevel.setWindowLevel();
        }
    }

    /**
     * ****************************************************
     * Zoom
     ****************************************************
     */
    /**
     * Set the zoomIn <code>CanvasControlMode<code/> value. Call the
     * <code>canvasContextMenu.setZoomInMode</code> for creating the zoomIn
     * menuItem.
     *
     * @param 0 <code>CanvasControlMode.NONE<code/>
     * @param 1 <code>CanvasControlMode.MANIP_ZOOM_IN<code/>
     */
    public void setZoomInMode(int mode) {
        if (mode == 0) {
            //viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.canvasContextMenu.setZoomInMode(0);
        }

        if (mode == 1) {
            //viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.canvasContextMenu.setZoomInMode(1);
        }
    }

    /**
     * Maybe this method need a better name...
     *
     * @param mode
     */
    public void setZoomControlAction(String mode) {
        double val = 0;

        if (mode.equalsIgnoreCase("zoom.reset")) {
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.localization.setCanvasControlMode(CanvasControlMode.NONE);

            val = viewDex.appMainAdmin.vgControl.getDefaultDisplaySize();
            viewDex.canvas.setZoomDefault(val);
            viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
            viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
            viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
            viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
            viewDex.localization.setLocalizationOverlayListInCanvas();
            //viewDex.canvas.setZoom(1.0);
            viewDex.windowLevel.setWindowLevel();
        } else if (mode.equalsIgnoreCase("zoom.in")) {
            viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.localization.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.distance.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.area.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
            viewDex.pixelValueMean.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
        } else if (mode.equalsIgnoreCase("zoom.out")) {
            viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.localization.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.distance.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.area.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.pixelValueMean.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
        } else if (mode.equalsIgnoreCase("pan")) {
            viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
            viewDex.localization.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
        } else if (mode.equalsIgnoreCase("scroll.stack")) {
            viewDex.scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK);
            viewDex.distance.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK);
            viewDex.area.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK);
            viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.zoom.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.localization.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK);
        }
    }

    /**
     * Set the zoomOut <code>CanvasControlMode<code/>.
     *
     * @param 0 <code>CanvasControlMode.NONE<code/>
     * @param 1 <code>CanvasControlMode.MANIP_ZOOM_OUT<code/>
     */
    public void setZoomOutMode(int mode) {
        if (mode == 0) {
            //viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
            viewDex.canvasContextMenu.setZoomOutMode(0);
        }

        if (mode == 1) {
            //viewDex.pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
            viewDex.canvasContextMenu.setZoomOutMode(1);
        }
    }

    /**
     * Set the zoom mode function.
     *
     */
    public void setZoomModeAction(double val) {
        viewDex.canvas.setZoom(val);
        viewDex.distanceMeasurement.setROIDistanceInCanvasAndNoRender();
        viewDex.areaMeasurement.setROIAreaInCanvasAndNoRender();
        viewDex.pixelValueMeanMeasurement.setROIPixelValueInCanvasAndNoRender();
        viewDex.canvas.setCanvasOverlayLocalizationRenderStatus(true);
        viewDex.localization.setLocalizationOverlayListInCanvas();
    }

    /**
     * Set the zoom mode function. Do not call for window/level update on the
     * canvas.
     *
     */
    public void setZoomModeDefault(double val) {
        viewDex.canvas.setZoomDefault(val);
    }

    /**
     * Set the zoomIncrement default value.
     *
     * @param val
     */
    public void setZoomIncrementDefault(double val) {
        viewDex.zoom.setZoomIncrementDefault(val);
    }

    /**
     * Set the canvas interpolation. The choises are. Nearestneighbor, Bilinear,
     * Bicubic, Bicubic2.
     *
     * @param ipDefault the default value.
     * @param ipDefinition the values available for the application.
     */
    public void setInterpolation(String ipDefault, String[] ipDefinition) {
        if (ipDefault.equalsIgnoreCase("nearest neighbor")
                || ipDefault.contains("nearest") || ipDefault.contains("Nearest")) {
            viewDex.canvas.setDefaultInterpolation(Interpolation.INTERP_NEAREST);
        } else if (ipDefault.equalsIgnoreCase("bilinear")) {
            viewDex.canvas.setDefaultInterpolation(Interpolation.INTERP_BILINEAR);
        } else if (ipDefault.equalsIgnoreCase("bicubic")) {
            viewDex.canvas.setDefaultInterpolation(Interpolation.INTERP_BICUBIC);
        } else if (ipDefault.equalsIgnoreCase("bicubic2")) {
            viewDex.canvas.setDefaultInterpolation(Interpolation.INTERP_BICUBIC_2);
        }

        viewDex.canvasContextMenu.setInterpolationMenuStatus(ipDefinition);
    }

    /**
     * If set to 1 the localization popupmenu will display.
     */
    public void setLocalizationMode(int mode) {
        viewDex.canvasContextMenu.setLocalizationMode(mode);
    }

    /**
     * Set the canvas to black ...
     */
    public void setCanvasToBlack() {
        //VgHistory vgHistory = viewDex.appMainAdmin.vgControl.getVgHistory();
        //ArrayList<StudyDbImageNode> imageList = vgHistory.getStudyImageList();
        //int selImage = vgHistory.getSelectedImage();
        //int size = imageList.size();
        //StudyDbImageNode item = (StudyDbImageNode) imageList.get(selImage);
        //String photometricInterpretation = item.getPhotometricInterpretation();

        //viewDex.windowLevel.setWindowLevel(1, -1000);
        // Set the w/l according to if the image is inverted or not
        //if(photometricInterpretation.equals("MONOCHROME1"))
        //  viewDex.windowLevel.setWindowLevel(1,  -1000);
        //if(photometricInterpretation.equals("MONOCHROME2"))
        //  viewDex.windowLevel.setWindowLevel(1, 4000);
        viewDex.canvas.resetImageCanvas();
    }

    /**
     * Set the canvas color NOT IN USE
     */
    public void setCanvasColor(Color c) {
        //viewDex.canvas.setCanvasColor(c);
    }

    /**
     * Set the canvas default color
     */
    public void setCanvasETColor() {
        viewDex.canvas.setCanvasETColor();
    }

    /**
     * Create the canvas display font and fontcolor. Read study properties
     */
    public void createCanvasTextProperties() {

        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        //font
        String defCanvasFont = "SansSerif-plain-20";
        String key = "canvas.overlay.font";
        String font = propUtils.getPropertyFontValue(prop, key);
        if (font.equals("")) {
            font = defCanvasFont;
        }
        Font canvasFont = Font.decode(font);

        // font color
        key = "canvas.overlay.font.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasTextColor[0];
            color[1] = AppPropertyUtils.defCanvasTextColor[1];
            color[2] = AppPropertyUtils.defCanvasTextColor[2];
        }
        Color canvasColor = new Color(color[0], color[1], color[2]);

        viewDex.canvas.setCanvasOverlayFontAndColor(canvasFont, canvasColor);
    }

    /**
     * ************************************************
     * Distance measurement
     *************************************************
     */
    /**
     * Create the canvas ROI distance properties Read study properties
     */
    public void createCanvasROIDistanceProperties() {

        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        int defWidthValue = 1;
        String key = "distance.measurement.drawing.line.width";
        int width = propUtils.getPropertyIntegerValue(prop, key);
        if (width == 0) {
            width = defWidthValue;
        }
        BasicStroke lineWidth = new BasicStroke(width);

        // line color
        key = "distance.measurement.drawing.line.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasDistanceMesurementLineColor[0];
            color[1] = AppPropertyUtils.defCanvasDistanceMesurementLineColor[1];
            color[2] = AppPropertyUtils.defCanvasDistanceMesurementLineColor[2];
        }
        Color lineColor = new Color(color[0], color[1], color[2]);

        //font
        String defCanvasFont = "SansSerif-plain-20";
        key = "distance.measurement.font";
        String font = propUtils.getPropertyFontValue(prop, key);
        if (font.equals("")) {
            font = defCanvasFont;
        }
        Font distanceFont = Font.decode(font);

        // font color
        key = "distance.measurement.font.color";
        color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasTextColor[0];
            color[1] = AppPropertyUtils.defCanvasTextColor[1];
            color[2] = AppPropertyUtils.defCanvasTextColor[2];
        }
        Color distanceColor = new Color(color[0], color[1], color[2]);

        viewDex.canvas.setCanvasROIDistanceFontValue(distanceFont, distanceColor);
        viewDex.canvas.setCanvasROIDistanceDrawingLineValue(lineWidth, lineColor);
    }

    /**
     * ************************************************
     * Volume measurement This module measures the area of a freehand ROI Volume
     * measurement might be implemented later on Properties named "area" is used
     *************************************************
     */
    /**
     * Create the canvas ROI area properties Read study properties
     */
    public void createCanvasROIVolumeProperties() {

        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        int defWidthValue = 1;
        String key = "area.measurement.drawing.line.width";
        int width = propUtils.getPropertyIntegerValue(prop, key);
        if (width == 0) {
            width = defWidthValue;
        }
        BasicStroke lineWidth = new BasicStroke(width);

        // line color
        key = "area.measurement.drawing.line.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasVolumeMesurementLineColor[0];
            color[1] = AppPropertyUtils.defCanvasVolumeMesurementLineColor[1];
            color[2] = AppPropertyUtils.defCanvasVolumeMesurementLineColor[2];
        }
        Color lineColor = new Color(color[0], color[1], color[2]);

        //font
        String defCanvasFont = "SansSerif-plain-20";
        key = "area.measurement.font";
        String font = propUtils.getPropertyFontValue(prop, key);
        if (font.equals("")) {
            font = defCanvasFont;
        }
        Font distanceFont = Font.decode(font);

        // font color
        key = "area.measurement.font.color";
        color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasTextColor[0];
            color[1] = AppPropertyUtils.defCanvasTextColor[1];
            color[2] = AppPropertyUtils.defCanvasTextColor[2];
        }
        Color distanceColor = new Color(color[0], color[1], color[2]);

        viewDex.canvas.setCanvasROIAreaFontValue(distanceFont, distanceColor);
        viewDex.canvas.setCanvasROIAreaDrawingLineValue(lineWidth, lineColor);
    }

    /**
     * ***************************************************
     * Pixel mean value measurement
     ****************************************************
     */
    /**
     * Create PixelValueMean line, linecolor, width, font and fontcolor Read
     * study properties
     */
    public void createCanvasROIPixelValueMeanProperties() {
        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        Properties prop = history.getVgProperties();

        int defWidthValue = 1;
        String key = "pixelvalue.measurement.drawing.line.width";
        int width = propUtils.getPropertyIntegerValue(prop, key);
        if (width == 0) {
            width = defWidthValue;
        }
        BasicStroke lineWidth = new BasicStroke(width);

        // line color
        key = "pixelvalue.measurement.drawing.line.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasPixelValueMeasurementLineColor[0];
            color[1] = AppPropertyUtils.defCanvasPixelValueMeasurementLineColor[1];
            color[2] = AppPropertyUtils.defCanvasPixelValueMeasurementLineColor[2];
        }
        Color lineColor = new Color(color[0], color[1], color[2]);

        //font
        String defCanvasFont = "SansSerif-plain-20";
        key = "pixelvalue.measurement.font";
        String font = propUtils.getPropertyFontValue(prop, key);
        if (font.equals("")) {
            font = defCanvasFont;
        }
        Font pixelvalueFont = Font.decode(font);

        // font color
        key = "pixelvalue.measurement.font.color";
        color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasTextColor[0];
            color[1] = AppPropertyUtils.defCanvasTextColor[1];
            color[2] = AppPropertyUtils.defCanvasTextColor[2];
        }
        Color fontColor = new Color(color[0], color[1], color[2]);

        viewDex.canvas.setCanvasROIPixelValueMeanLineValue(lineWidth, lineColor);
        viewDex.canvas.setCanvasROIPixelValueMeanTextValue(pixelvalueFont, fontColor);
    }

    /**
     * **************************************************************
     * Windowing Mode, NM & MR "whitepoint" values, Windowing status 
     **************************************************************
     */
    public void setWindowingMode(int wm) {
        windowingMode = wm;
        //viewDex.canvas.setWindowingMode(wm);
    }

    public void setWindowingFixedMinimumValue(int val) {
        viewDex.canvas.setWindowingFixedMinimumValue(val);
    }

    public void setWindowingFixedMinimumPercentValue() {
        viewDex.canvas.setWindowingFixedMinimumPercentValue();
    }

    // Not in use
    public void setWindowingFixedMinOverlayStatus(StudyDbImageNode imageNode) {
        boolean status = false;
        String modality = imageNode.getModality();

        if ((windowingMode == WindowingMode.FIXED_MINIMUM)
                && ((modality.equalsIgnoreCase("NM")
                || (modality.equalsIgnoreCase("MR"))))) {
            status = true;
        }

        viewDex.canvas.setWindowingFixedMinOverlayStatus(status);
    }

    /*
     * Call by setImage()->VgControl.setImageInitialPresentation()
     */
    public void setToolMenuItemStatus(StudyDbImageNode imageNode) {
        boolean status = false;
        String modality = imageNode.getModality();

        // Fixed minimum
        if ((windowingMode == WindowingMode.FIXED_MINIMUM)
                && ((modality.equalsIgnoreCase("NM")
                || (modality.equalsIgnoreCase("MR"))))) {
            status = true;
        }

        viewDex.setWindowingFixedMinCheckBoxmenuItemEnable(status);

        if (viewDex.getWindowingFixedMinimumCheckBoxmenuItemStatus()) {
            viewDex.canvas.setWindowingFixedMinOverlayStatus(status);
        }

        // If RGB image, disable pixelValue checkbox
        /*
        boolean status2 = true;
        String photoMetricInterpretation = imageNode.getPhotometricInterpretation();
        if (photoMetricInterpretation != null && photoMetricInterpretation.equalsIgnoreCase("RGB")) {
            status2 = false;
            viewDex.setPixelValueCheckBoxMenuItemEnable(status2);
            viewDex.canvas.setCanvasOverlayMousePositionPixelValueStatus(status2);
        } else {
            viewDex.setPixelValueCheckBoxMenuItemEnable(true);
            if (viewDex.getPixelValueCheckBoxMenuItemSelected()) {
                viewDex.canvas.setCanvasOverlayMousePositionPixelValueStatus(true);
            }
        }
         */
    }

    public void setWindowingFixedMinOverlayStatus(boolean sta) {
        boolean status = false;

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        String modality = imageNode.getModality();

        if (sta && ((windowingMode == WindowingMode.FIXED_MINIMUM)
                && ((modality.equalsIgnoreCase("NM")
                || (modality.equalsIgnoreCase("MR")))))) {
            status = true;
        }

        viewDex.canvas.setWindowingFixedMinOverlayStatus(status);
        viewDex.windowLevel.setWindowingFixedMinOverlayStatus(status);
    }

    public boolean getWindowingFixedMinimumStatus() {
        boolean status = false;

        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();
        String modality = imageNode.getModality();

        if ((windowingMode == WindowingMode.FIXED_MINIMUM)
                && ((modality.equalsIgnoreCase("NM")
                || (modality.equalsIgnoreCase("MR"))))) {
            status = true;
        }

        return status;
    }

    /**
     *
     * @return No need for this one anymore...
     */
    public boolean getPixelValueStatus() {
        boolean status = true;
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNode();

        String photoMetricInterpretation = imageNode.getPhotometricInterpretation();
        if (photoMetricInterpretation != null && photoMetricInterpretation.equalsIgnoreCase("RGB")) {
            status = true;
        }

        return status;
    }

    /**
     * ***********************************************
     * Canvas
     ***********************************************
     */
    /**
     * Create and set the canvas color background properties. Read study
     * properties
     */
    public void createCanvasColorProperties() {

        VgHistory history = viewDex.appMainAdmin.vgControl.getVgHistory();
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // color
        int[] color = new int[3];
        String key = "canvas.color";
        color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defCanvasColor[0];
            color[1] = AppPropertyUtils.defCanvasColor[1];
            color[2] = AppPropertyUtils.defCanvasColor[2];
        }

        Color canvasColor = new Color(color[0], color[1], color[2]);

        viewDex.canvas.initCanvasDefaultColor(canvasColor);
        viewDex.canvas.setCanvasDefaultColor();
    }
}
