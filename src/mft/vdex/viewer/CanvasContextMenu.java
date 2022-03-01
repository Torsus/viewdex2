/* @(#) CanvasContextMenu.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;
import javax.media.jai.Interpolation;
import javax.swing.*;
import mft.vdex.app.ViewDex;
import mft.vdex.modules.vg.VgHistory;
import mft.vdex.modules.vg.VgRunMode;
import mft.vdex.app.AppPropertyUtils;

/**
 * Implements the pan, zoom and window/level features together.
 *
 * @see com.vistech.imageviewer.Zoom
 * @see com.vistech.imageviewer.Pan
 * @see com.vistech.imageviewer.ZoomController
 * @see com.vistech.imageviewer.ScrollController
 */
public class CanvasContextMenu extends MouseAdapter implements KeyListener, PropertyChangeListener {

    public ViewDex viewDex;
    private VgHistory history;
    protected Pan pan;
    protected Zoom zoom;
    protected ScrollStack scrollStack;
    protected Localization localization;
    protected Distance distance;
    private String studyName;
    private Properties prop;
    //protected CatImageCanvasManipulator catImageCanvasManipulator;
    //protected WindowLevelGUI windowLevelGUI;
    //protected CatMain catMain;
    private String runMode;
    private boolean shiftKeyActivated = false;
    // meny enable
    private int wlMenuEnable;
    private int scrollStackMenuEnable;
    private int panMenuEnable;
    private int zoomInMenuEnable;
    private int zoomOutMenuEnable;
    private int displayModeMenuEnable;
    private int flipModeMenuEnable;
    private int nearestMenuEnable;
    private int bilinearMenuEnable;
    private int bicubicMenuEnable;
    private int bicubic2MenuEnable;
    private int localizationMenuEnable = 0;
    private int cineLoopMenuCreate;
    private int cineLoopStartMenuItemCreate;
    private int cineLoopStopMenuItemCreate;
    
    /** The single instance of this singleton class. */
    private static CanvasContextMenu instance;
    private AppPropertyUtils propUtils;
    // Scroll
    private boolean scrollMenuItemEnableStatus = false;
    // Cine-loop
    private boolean cineMenuEnableStatus = false;
    private boolean cineMenuStartItemEnableStatus = false;
    private boolean cineMenuStopItemEnableStatus = false;

    public CanvasContextMenu(ViewDex viewdex, VgHistory vghistory, Pan sCroll,
            Zoom zOom, ScrollStack sStack, Localization loc, Distance dist) {
        this.viewDex = viewdex;
        this.history = vghistory;
        pan = sCroll;
        zoom = zOom;
        scrollStack = sStack;
        localization = loc;
        distance = dist;

        init();
    }

    /**
     * The default private constructor to guarantee the singleton property
     *  of this class.
     */
    private CanvasContextMenu() {
    //changeSupport = new MedicalPropertyChangeSupport(this);
    }

    private void init() {
        propUtils = new AppPropertyUtils();
        viewDex.pan.setCanvasControlMode(CanvasControlMode.NONE);
        viewDex.windowLevel.setCanvasControlMode(CanvasControlMode.NONE);

        //studyName = history.getStudyName();
        prop = history.getVgProperties();

        // Set the scrollMenuItemEnableStatus && cineMenuBtnEnableStatus
        // TEST
        /*
        boolean cineLoopStatus = false;
        String key = "cineloop";
        String cineLoop = propUtils.getPropertyStringValue(prop, key);
        if (cineLoop.equalsIgnoreCase("Yes") || cineLoop.equalsIgnoreCase("Y")) {
            cineLoopStatus = true;
        }

        if (cineLoopStatus) {
            cineMenuEnableStatus = true;
            cineMenuBtnEnableStatus = true;
            scrollMenuItemEnableStatus = true;
        } else {
            cineMenuEnableStatus = false;
            cineMenuBtnEnableStatus = false;
            scrollMenuItemEnableStatus = false;
        }
        */


    // Not in use
    //zoom.setCanvasControlMode(CanvasDisplayMode.MANIP_PAN);
    }

    /**
     *  NOT IN USE
     * Return the single instance of this class. This method guarantees
     *  the singleton property of this class.
     */
    public static synchronized CanvasContextMenu getInstance() {
        if (instance == null) {
            instance = new CanvasContextMenu();
        }
        return instance;
    }

    // NOT IN USE
    public void propertyChange(PropertyChangeEvent ev) {
        if (ev.getPropertyName().equalsIgnoreCase("pan")) {
            int a = 10;
        }
    }

    /***********************************************************************
     * 
     * 
     * Menu enable mode
     * 
     * 
     * ********************************************************************/
    /**
     * Set the WL mode. If this mode is true a menuitem vill be created.
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setWLMode(int mode) {
        wlMenuEnable = mode;
    }

    /**
     * Set the pan stack mode. If this mode is true a menuitem vill be created.
     *
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setScrollStackMode(int mode) {
        scrollStackMenuEnable = mode;
    }
    
     /**
     * Set the cineLoopMode. If this mode is true a menuitem vill be created.
     *
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setCineLoopMenuCreate(int mode) {
        cineLoopMenuCreate = mode;
        
        // set these as well
        cineLoopStartMenuItemCreate = mode;
        cineLoopStopMenuItemCreate = mode;
    }
    
    /**
     * Set the cineLoopStartMode. If this mode is true a menuitem vill be created.
     *
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setCineLoopStartItemCreate(int mode) {
        cineLoopStartMenuItemCreate = mode;
    }
    
     /**
     * Set the cineLoopStopMode. If this mode is true a menuitem vill be created.
     *
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setCineLoopStopItemCreate(int mode) {
        cineLoopStopMenuItemCreate = mode;
    }

    /**
     * Set the pan mode. If this mode is true a menuitem vill be created.
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setPanMode(int mode) {
        panMenuEnable = mode;
    }

    /**
     * Set the zoomIn mode. If this mode is true a menuitem vill be created.
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setZoomInMode(int mode) {
        zoomInMenuEnable = mode;
    }

    /**
     * Set the zoomOut mode. If this mode is true a menuitem vill be created.
     * @param 0 no menuitem created.
     * @param 1 menuitem created.
     */
    public void setZoomOutMode(int mode) {
        zoomOutMenuEnable = mode;
    }

    /**
     * Set the interpolation menu status. A menuitem vill be created
     * for each interpolation type;
     *
     * @param ipDefinition the defined interpolation types.
     */
    public void setInterpolationMenuStatus(String[] ipDefinition) {
        for (int i = 0; i < ipDefinition.length; i++) {
            String str = ipDefinition[i];
            if (str.equalsIgnoreCase("nearest neighbor") || str.contains("nearest") || str.contains("Nearest")) {
                nearestMenuEnable = 1;
            } else if (str.equalsIgnoreCase("bilinear")) {
                bilinearMenuEnable = 1;
            } else if (str.equalsIgnoreCase("bicubic")) {
                bicubicMenuEnable = 1;
            } else if (str.equalsIgnoreCase("bicubic2")) {
                bicubic2MenuEnable = 1;
            }
        }
    }

    /**
     * Set the localisation mode.
     */
    public void setLocalizationMode(int mode) {
        localizationMenuEnable = mode;
    }

    /***********************************************************************
     * 
     * 
     * Menu
     * 
     * 
     * ********************************************************************/
    /**
     * Popup of the canvas context menu.
     */
    protected void popupMenu(JComponent comp, int x, int y) {
        JPopupMenu menu = new JPopupMenu("");
        menu.setLightWeightPopupEnabled(true);
        comp.add(menu);

        // panel color
        String key = "canvas.contextmenu.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if (panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0) {
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // menu font
        key = "canvas.contextmenu.font";
        String fontValue = propUtils.getPropertyFontValue(prop, key);
        if (fontValue.equals("")) {
            fontValue = "SansSerif-plain-14";
        }
        // font color
        int[] fontColor = new int[3];
        key = "canvas.contextmenu.font.color";
        fontColor = propUtils.getPropertyColorValue(prop, key);
        if (fontColor[0] == 0 && fontColor[1] == 0 && fontColor[2] == 0) {
            fontColor[0] = AppPropertyUtils.defButtonTextColor[0];
            fontColor[1] = AppPropertyUtils.defButtonTextColor[1];
            fontColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }


        /*************************************************************
         * Scroll (stack)
         ************************************************************/
        if (scrollStackMenuEnable == 1) {
            JMenuItem item = new JMenuItem("Scroll");
            item.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            item.setFont(Font.decode(fontValue));
            item.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            //item.setEnabled(scrollMenuItemEnableStatus);
            item.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvasControl.setZoomControlAction("scroll.stack");
                //scrollStack.setCanvasControlMode(CanvasControlMode.MANIP_SCROLL_STACK);
                //pan.setCanvasControlMode(CanvasControlMode.NONE);
                //zoom.setCanvasControlMode(CanvasControlMode.NONE);
                }
            });
            menu.add(item);
        }

        /*************************************************************
         * Pan
         ************************************************************/
        if (panMenuEnable == 1) {
            JMenuItem item = new JMenuItem("Pan");
            item.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            item.setFont(Font.decode(fontValue));
            item.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            item.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    // test 2012-05-08
                    viewDex.canvasControl.setPanControlAction("pan");
                    //viewDex.canvasControl.setZoomControlAction("pan");
                    //scrollStack.setCanvasControlMode(CanvasControlMode.NONE);
                    //pan.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
                    //localization.setCanvasControlMode(CanvasControlMode.MANIP_PAN);
                }
            });
            menu.add(item);
        }

        /*************************************************************
         * Zoom In
         ************************************************************/
        if (zoomInMenuEnable == 1) {
            JMenuItem zoomIn = new JMenuItem("Zoom in");
            zoomIn.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            zoomIn.setFont(Font.decode(fontValue));
            zoomIn.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            zoomIn.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvasControl.setZoomControlAction("zoom.in");
                    //pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
                    //zoom.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN);
                    //localization.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_IN)
                }
            });
            menu.add(zoomIn);
        }

        /*************************************************************
         * Zoom Out
         ************************************************************/
        if (zoomOutMenuEnable == 1) {
            JMenuItem zoomOut = new JMenuItem("Zoom out");
            zoomOut.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            zoomOut.setFont(Font.decode(fontValue));
            zoomOut.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));

            zoomOut.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvasControl.setZoomControlAction("zoom.out");
                    //pan.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
                    //zoom.setCanvasControlMode(CanvasControlMode.MANIP_ZOOM_OUT);
                }
            });
            menu.add(zoomOut);
        }

        /*************************************************************
         * DisplayMode
         ************************************************************/
        if (displayModeMenuEnable == 1) {
            JMenu dispMode = new JMenu("Display Mode");
            dispMode.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            dispMode.setFont(Font.decode(fontValue));
            dispMode.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));

            dispMode.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                }
            });

            JMenuItem originalMenu = new JMenuItem("Original");
            originalMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            originalMenu.setFont(Font.decode(fontValue));
            originalMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            originalMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvas.setDisplayMode(CanvasControlMode.DISPLAY_ORIG);
                }
            });

            JMenuItem toFitMenu = new JMenuItem("ToFit");
            toFitMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            toFitMenu.setFont(Font.decode(fontValue));
            toFitMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            toFitMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvas.setDisplayMode(CanvasControlMode.DISPLAY_TO_FIT);
                }
            });

            JMenuItem scaledMenu = new JMenuItem("Scaled");
            scaledMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            scaledMenu.setFont(Font.decode(fontValue));
            scaledMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            scaledMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvas.setDisplayMode(CanvasControlMode.DISPLAY_SCALED);
                }
            });

            JMenuItem halfSizeMenu = new JMenuItem("1:2");
            halfSizeMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            halfSizeMenu.setFont(Font.decode(fontValue));
            halfSizeMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            halfSizeMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.canvas.setDisplayMode(CanvasControlMode.DISPLAY_HALF_SIZE);
                }
            });

            dispMode.add(originalMenu);
            dispMode.add(toFitMenu);
            dispMode.add(scaledMenu);
            dispMode.add(halfSizeMenu);

            menu.add(dispMode);
        }

        /*************************************************************
         * flip
         ************************************************************/
        // NOT IN USE
        if (flipModeMenuEnable == 1) {
            JMenu flipMode = new JMenu("Flip Mode");
            flipMode.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            flipMode.setFont(Font.decode(fontValue));
            flipMode.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));

            flipMode.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                }
            });

            JMenuItem leftRightMenu = new JMenuItem("LeftToRight");
            leftRightMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            leftRightMenu.setFont(Font.decode(fontValue));
            leftRightMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            leftRightMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.geomManip.flip(CanvasControlMode.FLIP_LEFT_RIGHT);
                }
            });

            JMenuItem topBottomMenu = new JMenuItem("TopToBottom");
            topBottomMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            topBottomMenu.setFont(Font.decode(fontValue));
            topBottomMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            topBottomMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.geomManip.flip(CanvasControlMode.FLIP_TOP_BOTTOM);
                }
            });

            JMenuItem topBottomLeftRightMenu = new JMenuItem("TB And LR");
            topBottomLeftRightMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            topBottomLeftRightMenu.setFont(Font.decode(fontValue));
            topBottomLeftRightMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            topBottomLeftRightMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    viewDex.geomManip.flip(CanvasControlMode.FLIP_TOP_BOTTOM_LEFT_RIGHT);
                }
            });

            flipMode.add(leftRightMenu);
            flipMode.add(topBottomMenu);
            flipMode.add(topBottomLeftRightMenu);

            menu.add(flipMode);
        }


        /**********************************************************
         *  Interpolation
         *********************************************************/
        JMenu interPolation = new JMenu("Interpolation");
        interPolation.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        interPolation.setFont(Font.decode(fontValue));
        interPolation.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));

        // Interpolation
        if ((nearestMenuEnable == 1) || (bilinearMenuEnable == 1) || (bicubicMenuEnable == 1) || (bicubic2MenuEnable == 1)) {
            interPolation.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                }
            });
        }

        // Nearest
        if (nearestMenuEnable == 1) {
            JMenuItem nearestMenu = new JMenuItem("Nearest neighbor");
            nearestMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            nearestMenu.setFont(Font.decode(fontValue));
            nearestMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            nearestMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //pan.setCanvasControlMode(CanvasDisplayMode.INTERPOLATION_NEAREST);
                    //viewer.canvas.setInterpolationMode(Interpolation.INTERP_NEAREST);
                    viewDex.canvas.setInterpolation(Interpolation.INTERP_NEAREST);
                }
            });
            interPolation.add(nearestMenu);
        }

        // Bilinear
        if (bilinearMenuEnable == 1) {
            JMenuItem bilinearMenu = new JMenuItem("Bilinear");
            bilinearMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            bilinearMenu.setFont(Font.decode(fontValue));
            bilinearMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            bilinearMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //pan.setCanvasControlMode(CanvasDisplayMode.INTERPOLATION_BILINEAR);
                    //viewer.canvas.setInterpolationMode(Interpolation.INTERP_BILINEAR);
                    viewDex.canvas.setInterpolation(Interpolation.INTERP_BILINEAR);
                }
            });
            interPolation.add(bilinearMenu);
        }

        // Bicubic
        if (bicubicMenuEnable == 1) {
            JMenuItem bicubicMenu = new JMenuItem("Bi-cubic");
            bicubicMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            bicubicMenu.setFont(Font.decode(fontValue));
            bicubicMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            bicubicMenu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //pan.setCanvasControlMode(CanvasDisplayMode.INTERPOLATION_BICUBIC);
                    //viewer.canvas.setInterpolationMode(Interpolation.INTERP_BICUBIC);
                    viewDex.canvas.setInterpolation(Interpolation.INTERP_BICUBIC);
                }
            });
            interPolation.add(bicubicMenu);
        }

        // Bicubic2
        if (bicubic2MenuEnable == 1) {
            JMenuItem bicubic2Menu = new JMenuItem("Bi-cubic2");
            bicubic2Menu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            bicubic2Menu.setFont(Font.decode(fontValue));
            bicubic2Menu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            bicubic2Menu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //pan.setCanvasControlMode(CanvasDisplayMode.INTERPOLATION_BICUBIC2);
                    //viewer.canvas.setInterpolationMode(Interpolation.INTERP_BICUBIC_2);
                    viewDex.canvas.setInterpolation(Interpolation.INTERP_BICUBIC_2);
                }
            });
            interPolation.add(bicubic2Menu);
        }

        if ((nearestMenuEnable == 1) || (bilinearMenuEnable == 1) || (bicubicMenuEnable == 1) || (bicubic2MenuEnable == 1)) {
            menu.add(interPolation);
        }

        /**********************************************************
         *  Cine-loop
         *********************************************************/
        if (cineLoopMenuCreate == 1) {
            JMenu cineLoop = new JMenu("Cine-Loop");
            cineLoop.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            cineLoop.setFont(Font.decode(fontValue));
            cineLoop.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
            cineLoop.setEnabled(cineMenuEnableStatus);
            
            /*
            if(commandModeu){
            }*/
            
            
            // start
            if (cineLoopStartMenuItemCreate == 1) {
                JMenuItem startCineMenuBtn = new JMenuItem("Start");
                startCineMenuBtn.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
                startCineMenuBtn.setFont(Font.decode(fontValue));
                startCineMenuBtn.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
                startCineMenuBtn.setEnabled(cineMenuStartItemEnableStatus);
                startCineMenuBtn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        viewDex.vgCineLoopPanel.setLoopValueButtonSelected();
                        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
                        viewDex.appMainAdmin.vgControl.runStudyAsCineLoop();
                    }
                });
                cineLoop.add(startCineMenuBtn);
            }

            // Stop
            if (cineLoopStopMenuItemCreate == 1) {
                JMenuItem stopCineMenuBtn = new JMenuItem("Stop");
                stopCineMenuBtn.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
                stopCineMenuBtn.setFont(Font.decode(fontValue));
                stopCineMenuBtn.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
                stopCineMenuBtn.setEnabled(cineMenuStopItemEnableStatus);
                stopCineMenuBtn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
                    }
                });
                cineLoop.add(stopCineMenuBtn);
            }
            
            menu.add(cineLoop);
        }

        /*******************************************************************
         * Localization
         *****************************************************************/
        // Check for the existans of a selected localization mark.
        localization.updateLocalizationMode();

        if (localizationMenuEnable == 1) {
            JMenu localizationMenu = new JMenu("Localization");
            localizationMenu.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
            localizationMenu.setFont(Font.decode(fontValue));
            localizationMenu.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));

            if(localization.localizationSelectStatusExist()){
                JMenuItem deleteMenuItem = new JMenuItem("Delete");
                deleteMenuItem.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
                deleteMenuItem.setFont(Font.decode(fontValue));
                deleteMenuItem.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
                localizationMenu.add(deleteMenuItem);
                
                deleteMenuItem.addActionListener(new ActionListener() {
                    @Override
                public void actionPerformed(ActionEvent e) {
                    localization.deleteSelectedLocalizationMark();
                }
                });
            }
            
            // If there exist more marks then the selected on on the seleceted image
            if(localization.localizationActiveStatusExist() || localization.localizationSetStatusExist()){
                JMenuItem deleteAllMenuItem = new JMenuItem("Delete all");
                deleteAllMenuItem.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
                deleteAllMenuItem.setFont(Font.decode(fontValue));
                deleteAllMenuItem.setForeground(new Color(fontColor[0], fontColor[1], fontColor[2]));
                localizationMenu.add(deleteAllMenuItem);
                
                deleteAllMenuItem.addActionListener(new ActionListener() {
                    @Override
                public void actionPerformed(ActionEvent e) {
                    localization.deleteAllLocalizationMark();
                }
                });
            }
            menu.add(localizationMenu);
        }
        localization.setKeyCtrlEnable(false);
        menu.show(comp, x, y);
    }

    /**
     * 
     * @param sta
     */
    public void setScrollMenuItemEnabled(boolean sta) {
        scrollMenuItemEnableStatus = sta;
    }

    /**
     * 
     * @param sta
     */
    public void setCineMenuButtonEnabled(boolean sta) {
        cineMenuEnableStatus = sta;
        
        // set these as well
        cineMenuStartItemEnableStatus = sta;
        cineMenuStopItemEnableStatus = sta;
    }

    // ---------------------------------------------------------------
    // mouse events
    // ---------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent e) {
    //int a = 10;
        /*if(!SwingUtilities.isLeftMouseButton(e)){
    popupMenu((JComponent)e.getSource(), e.getX(), e.getY());
    }*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        AffineTransform atx;
        Point2D currPoint = null;
        Point2D setPoint = null;
        Point2D p2 = null;
        boolean selStatus = false;
        boolean wlStatus = false;

        Point2D p = e.getPoint();

        if (SwingUtilities.isRightMouseButton(e) &&
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST) {
            /*
            try {
            // Transform the ancor point from user space to device space.
            atx = viewDex.canvas.getTransform();
            currPoint = atx.inverseTransform((new Point((int) p.getX(), (int) p.getY())), p2);
            } catch (Exception exp) {
            System.out.println(exp);
            }
            // See if there is a localization mark within a circle, created with the
            // mouse hot-point as the center of the circle.
            int r1 = 20;
            Shape ellipse = new Ellipse2D.Double((currPoint.getX() - r1), (currPoint.getY() - r1), (r1 * 2), (r1 * 2));
            // Get a localization mark that exist within the the ellipse.
            setPoint = viewDex.appMainAdmin.vgControl.getLocalizationInsideShape(ellipse);
            selStatus = viewDex.localization.getLocalizationSelectStatus(setPoint);
             */

            // if true a windowLevel operation is activated
            wlStatus = viewDex.windowLevelGUI.getWLStatus();

            if ((!wlStatus)) {
                popupMenu((JComponent) e.getSource(), e.getX(), e.getY());
            }
        }

        if (SwingUtilities.isRightMouseButton(e) &&
                (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.SHOW_EXIST)) {

            // if true a windowLevel operation is activated
            wlStatus = viewDex.windowLevelGUI.getWLStatus();

            if (!wlStatus) {
                popupMenu((JComponent) e.getSource(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    // Font
    // -------------------------------------------------------------------

    // -------------------------------------------------------------------
    // getScaledFont
    // -------------------------------------------------------------------
    /**
     *  Gets font scaled for screen resolution
     * @param fontName              Logical font name i.e. SansSerif
     * @param fontStyle             Font class style defines
     * @param pointSizeFor1280Mode  How big in 1280 * 1024 resolution
     * @return                      The scaledFont value
     */
    private Font getScaledFont(String fontName, int fontStyle, int pointSizeFor1280Mode) {
        Font f = new Font(fontName, fontStyle, pointSizeFor1280Mode);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (d.height == 1024) {
            return f;
        } else {
            int numerator = pointSizeFor1280Mode * d.height;
            float sizeForCurrentResolution = (float) numerator / 1024;
            return f.deriveFont(sizeForCurrentResolution);
        }
    }
}
