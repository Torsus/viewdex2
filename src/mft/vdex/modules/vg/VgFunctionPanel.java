/* @(#) VgFunctionPanel.java 06/09/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.vg;

import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import mft.vdex.app.AppProperty;
import mft.vdex.app.ViewDex;
import mft.vdex.app.AppPropertyUtils;


/**
 * The VgFunctionPanel class creates the
 * Window/Level, Pan, Zoom, UI.
 *
 */
public class VgFunctionPanel extends JPanel{
    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private AppProperty appProperty;
    private VgHistory history;
    private String studyName;
    private Properties prop;
    
    /** The wrapped instance of SwingPropertyChangeSupport.
     */
    PropertyChangeSupport changeSupport;
    
    // status fix
    private int panPanelStatus = 0;
    private int zoomPanelStatus = 0;

    JPanel wlMainPanel = null;
    JPanel wlResetPanel = null;
    JPanel wlUserDefinedPanel = null;
    JPanel wlAdditionalPanel = null;
    JPanel panMainPanel;
    JPanel zoomMainPanel;
    
    private int[] windowWidth;
    private int[] windowCenter;
    
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    
    public VgFunctionPanel(ViewDex viewdex, AppProperty appproperty, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.appProperty = appproperty;
        this.history = vghistory;
        
        propUtils = new AppPropertyUtils();
        studyName = history.getStudyName();
        prop = history.getVgProperties();
        
        // Constructs a <code>SwingPropertyChangeSupport</code> object.
        // "this" The bean to be given as the source for any events.
        changeSupport = new PropertyChangeSupport(this);
        
        createUI();
    }
    
    /*
     * Create the Window/Level, Zoom and Pan panels.
     */
    protected void createUI(){
        createLayout();
        boolean mouseEnableStatus = wlMouseStatus();
        boolean additionalStatus = wlAdditionalStatus();
        boolean userDefinedStatus = wlUserDefinedStatus();

        wlMainPanel = createWindowLevelMainPanel(3);
        if(mouseEnableStatus)
            wlResetPanel = createWindowLevelResetPanel();
        if(userDefinedStatus)
            wlUserDefinedPanel = createWindowLevelUserDefinedPanel();

        if(wlResetPanel != null)
             wlMainPanel.add(wlResetPanel, "0,1");
        if(wlUserDefinedPanel != null)
             wlMainPanel.add(wlUserDefinedPanel, "0,3");


       /*
        if(mouseEnableStatus && !userDefinedStatus){
            wlMainPanel.add(wlResetPanel, "0,2");
        } else{
            if(mouseEnableStatus && userDefinedStatus){
                wlMainPanel.add(wlResetPanel, "0,2");
                wlMainPanel.add(wlUserDefinedPanel, "0,4");
            }
        }
        */
        
        // Zoom
        JPanel zoomUpperPanel = createZoomUpperPanel();
        JPanel zoomLowerPanel = createZoomLowerPanel();
        zoomMainPanel = createZoomMainPanel(zoomPanelStatus);
        if(zoomPanelStatus == 0 || zoomPanelStatus == 1){
            zoomMainPanel.add(zoomUpperPanel, "0,0");
        } else{
            if(zoomPanelStatus == 2){
                zoomMainPanel.add(zoomUpperPanel, "0,1");
                zoomMainPanel.add(zoomLowerPanel, "0,3");
            }
        }
        
        // Pan
        JPanel panUpperPanel = createPanUpperPanel();
        panMainPanel = createPanMainPanel(panPanelStatus);
        if(panPanelStatus == 0 || panPanelStatus == 1)
            panMainPanel.add(panUpperPanel, "0,1");
        /*
        } else{
            if(zoomPanelStatus == 2){
                zoomMainPanel.add(zoomUpperPanel, "0,0");
                //zoomMainPanel.add(zoomLowerPanel, "0,2");
            }
        }*/
        
        this.add(wlMainPanel, "1,1");
        this.add(panMainPanel, "1,3");
        this.add(zoomMainPanel, "1,5");
    }
    
    private void createLayout(){
        // panel color
        String key = "functionpanel.panel.color";
        int[] panelColor  = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment top
        key = "functionpanel.wl.space.top";
        int wlPanelTop = propUtils.getPropertyIntegerValue(prop, key);
        if (wlPanelTop == 0)
            wlPanelTop = 5;
        
        // alignment top
        key = "functionpanel.pan.space.top";
        int panPanelTop = propUtils.getPropertyIntegerValue(prop, key);
        if (panPanelTop == 0)
            panPanelTop = 5;
        
        // alignment top
        key = "functionpanel.zoom.space.top";
        int zoomPanelTop = propUtils.getPropertyIntegerValue(prop, key);
        if (zoomPanelTop == 0)
            zoomPanelTop = 5;
        
         // alignment left
        key = "functionpanel.alignment.left";
        int alignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0)
            alignLeft = 3;
        
        // alignment right
        key = "functionpanel.alignment.right";
        int alignRight = propUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0)
            alignRight = 3;
        
        double size[][] = {{alignLeft,f,alignRight}, {wlPanelTop,p,panPanelTop,p,zoomPanelTop,p}};
        this.setLayout(new TableLayout(size));
        
        // color
        this.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //this.setBackground(Color.RED);
    }
    
    // ***********************************************************
    //
    //         Window/Level
    //
    // ************************************************************
    private JPanel createWindowLevelMainPanel(int mode){
        // panel color
        String key = "functionpanel.wl.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment
        key = "functionpanel.wl.panel.alignment.top";
        int alignTop = propUtils.getPropertyIntegerValue(prop, key);
        if (alignTop == 0)
            alignTop = 0;
        
        // alignment
        key = "functionpanel.wl.panel.alignment.botton";
        int alignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if (alignBottom == 0)
            alignBottom = 5;
        
        // alignment
        key = "functionpanel.wl.additional-ww-wc.button.vertical.space";
        int additionalButtonVerticalSpace = propUtils.getPropertyIntegerValue(prop, key);
        if (additionalButtonVerticalSpace == 0)
            additionalButtonVerticalSpace = 5;

         // alignment
        key = "functionpanel.wl.userdefined-ww-wc.button.vertical.space";
        int userDefinedVerticalSpace = propUtils.getPropertyIntegerValue(prop, key);
        if (userDefinedVerticalSpace == 0)
            userDefinedVerticalSpace = 5;
        
        // border color
        key = "functionpanel.wl.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if(borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0){
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }
        
        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0],
                borderColor[1], borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Window/Level");
        
        // title font
        String defTitleFont = "SansSerif-plain-16";
        key = "functionpanel.wl.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if(titleFont.equals(""))
            titleFont = defTitleFont;
        
        // title color
        key = "functionpanel.wl.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if(titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0){
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }
        
        // size
        //double size[][] = {{f},{f}};
        double[][] size = new double[2][7];
        if(mode == 0){
            size[0][0] = f;
            size[1][0] = f;
        }
        
        //double size[][] = {{f},{8,p,8}};
        if(mode == 1){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][1] = p;
            size[1][2] = (double) alignBottom;
        }
        
        //double size[][] = {{f},{8,p,10,p,8}};
        if(mode == 2){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][0] = p;
            size[1][1] = (double) additionalButtonVerticalSpace;
            size[1][2] = p;
            size[1][3] = (double) alignBottom;
        }

        //double size[][] = {{f},{8,p,10,p,10,p,8}};
        if(mode == 3){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][1] = p;  // Reset button
            size[1][2] = (double) additionalButtonVerticalSpace;
            size[1][3] = p;
            size[1][4] = (double) userDefinedVerticalSpace;
            size[1][5] = p;
            size[1][6] = (double) alignBottom;
        }
        
        // panel
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //tborder.setTitleJustification(TitledBorder.LEFT);
        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0], titleColor[1],
                titleColor[2]));
        panel.setBorder(tborder);
        
        return panel;
    }

    /****************************************************************
     *
     ***************************************************************/
    private JPanel createWindowLevelResetPanel(){
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        String key;

        // button font
        String defResetButtonFont = "Arial-plain-16";
        key = "functionpanel.wl.button.font";
        String resetButtonFont  = propUtils.getPropertyFontValue(prop, key);
        if(resetButtonFont.equals(""))
            resetButtonFont = defResetButtonFont;
        
        // button color
        boolean buttonColorPropertyStatus = true;
        key = "functionpanel.wl.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
        
        // button text color
        key = "functionpanel.wl.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 &&
                buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }
        
        // border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.wl.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        // border
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // button size
        int[] defResetButtonSize = {90,25};
        key = "functionpanel.wl.button.size";
        int[] resetButtonSize = propUtils.getPropertySizeValue(prop, key);
        if(resetButtonSize[0] == 0 && resetButtonSize[1] == 0){
            resetButtonSize[0] = defResetButtonSize[0];
            resetButtonSize[1] = defResetButtonSize[1];
        }
        
        // button
        JButton btn = new JButton("Reset");
        btn.setMargin(new Insets(0,0,0,0));
        btn.setFont(Font.decode(resetButtonFont));
        btn.setPreferredSize(new Dimension(resetButtonSize[0], resetButtonSize[1]));
        //btn.setFocusPainted(false);
        btn.setFocusable(false);
        //btn.addKeyListener(this);
        //btn.addFocusListener(this);
        
        if(buttonColorPropertyStatus)
            btn.setBackground(new Color(buttonColor[0], buttonColor[1], buttonColor[2]));
            
        btn.setForeground(new Color(buttonTextColor[0], buttonTextColor[1], buttonTextColor[2]));
        if(buttonBorderColorPropertyStatus)
            btn.setBorder(border);
        btn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(viewDex.vgStudyNextCaseExtendedControl != null)
                    viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                viewDex.requestFocusInWindow();
                //for(double x=0; x<999999999; x++);
                wlResetButtonActionPerformed();
            }
        });
        btnList.add(btn);

        // enables wl whith the mouse
        viewDex.canvasControl.setWLMode(1);

        // panel color
        key = "functionpanel.wl.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // panel
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        
        for(int i = 0; i < btnList.size(); i++)
            panel.add(btnList.get(i));

        return panel;
    }

    /*
     *
     */
    private JPanel createWindowLevelUserDefinedPanel(){
        ArrayList <VgFunctionPanelUserDefinedWLControl> list;
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        JPanel panel = null;

        // panel color
        String key = "functionpanel.wl.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // button color
        boolean buttonColorPropertyStatus = true;
        key = "functionpanel.wl.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;

        // button text color
        key = "functionpanel.wl.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defTextColor[2];
        }

        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.wl.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;

        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }

        // button size
        int[] defButtonSize = {90,25};
        key = "functionpanel.wl.userdefined-ww-wc.button.size";
        int[] buttonSize = propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defButtonSize[0];
            buttonSize[1] = defButtonSize[1];
        }

        // There are two sources for multiple w/l values.
        // 1. Defined in the properties file.
        // 2. Defined in the image Dicom header data.

       /*
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.getSelectedImageNode();
        if(imageNode != null){
            windowCenter = imageNode.getWindowCenter();
            windowWidth = imageNode.getWindowWidth();
        }*/

         list = history.getFunctionPanelUserDefinedWLList();

         if(!list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                String propertyName = list.get(i).getPropertyName();
                String name = list.get(i).getName();
                int windowWidth = list.get(i).getWindowWidth();
                int windowCenter = list.get(i).getWindowCenter();
                String font = list.get(i).getFont();
                btnList.add(new JButton(name));
                btnList.get(i).setFocusable(false);
                btnList.get(i).setMargin(new Insets(0,0,0,0));
                btnList.get(i).setActionCommand(name);
                btnList.get(i).setFont(Font.decode(font));

                if(buttonColorPropertyStatus)
                    btnList.get(i).setBackground(new Color(buttonColor[0],
                            buttonColor[1],
                            buttonColor[2]));

                btnList.get(i).setForeground(new Color(
                        buttonTextColor[0],
                        buttonTextColor[1],
                        buttonTextColor[2]));

                if(buttonBorderColorPropertyStatus)
                    btnList.get(i).setBorder(border);

                btnList.get(i).setPreferredSize(new Dimension(
                        buttonSize[0],
                        buttonSize[1]));
                        /*
                         if(windowWidth.length < 3)
                         btnList.get(i).setPreferredSize(new Dimension(80, 23));
                         if(windowWidth.length == 3)
                         btnList.get(i).setPreferredSize(new Dimension(67, 23));
                         */
                
                btnList.get(i).setFocusPainted(false);
                btnList.get(i).addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if(viewDex.vgStudyNextCaseExtendedControl != null)
                                    //viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                                viewDex.requestFocusInWindow();
                                wlSet(evt);
                            }
                        });
                    }
                }

        // panel
        if(!btnList.isEmpty()){
            panel = new JPanel();
            if(btnList.size() < 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
            if(btnList.size() == 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            if(btnList.size() > 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            //panel.setBackground(new Color(125,89,134));
            panel.setBackground(new Color(panelColor[0],panelColor[1],panelColor[2]));

            for(int i = 0; i < btnList.size(); i++)
                panel.add(btnList.get(i));
        }
        return panel;
    }
    
    /*
     * NOT IN USE
     * Deprecated by createWindowLevelLowerPanel2
     * Create a button by checking the "Additional window settings property"
     * and the windowCenterMultiple and windowWidthMultiple values in the
     * StudyLoader class. NOT WORKING A lot of testing ...
     */
    private JPanel createWindowLevelLowerPanel(){
        ArrayList <VgFunctionPanelWLControl> list;
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        JPanel panel = null;
        
        list = history.getFunctionPanelWLList();
        // test if ...wl.mouse = "yes"
        if(wlMouseStatus()&& wlAdditionalStatus()){
            for(int i = 0; i < list.size(); i++){
                String propValue = list.get(i).getPropValue();
                String propFont = list.get(i).getPropFont();
                
                if(windowWidth != null){
                    btnList.add(new JButton(propValue));
                    String propName = list.get(i).getPropName();
                    btnList.get(i).setFocusable(false);
                    btnList.get(i).setActionCommand(propName);
                    btnList.get(i).setMargin(new Insets(0,0,0,0));
                    btnList.get(i).setFont(Font.decode(propFont));
                    
                    if(windowWidth.length < 3)
                        btnList.get(i).setPreferredSize(new Dimension(80, 23));
                    if(windowWidth.length == 3)
                        btnList.get(i).setPreferredSize(new Dimension(67, 23));
                    
                    btnList.get(i).setFocusPainted(false);
                    btnList.get(i).addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            wlPresetButtonActionPerformed(evt);
                        }
                    });
                }
            }
        }
        
        // panel
        if(!btnList.isEmpty()){
            panel = new JPanel();
            if(btnList.size() < 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
            if(btnList.size() == 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            //panel.setBackground(new Color(125,89,134));
            
            for(int i = 0; i < btnList.size(); i++)
                panel.add(btnList.get(i));
        }
        return panel;
    }
    
    /*
     * createWindowLevelLowerPanel2
     * Create a button by checking the "Additional window settings property"
     * and the windowCenterMultiple and windowWidthMultiple values in the
     * StudyLoader class.
     */
    private JPanel createWindowLevelAdditionalPanel(){
        ArrayList <VgFunctionPanelWLControl> list;
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        JPanel panel = null;
        
        // panel color
        String key = "functionpanel.wl.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // button color
        boolean buttonColorPropertyStatus = true;
        key = "functionpanel.wl.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
        
        // button text color
        key = "functionpanel.wl.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defTextColor[2];
        }
        
        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.wl.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // button size
        int[] defButtonSize = {90,25};
        key = "functionpanel.wl.additional-ww-wc.button.size";
        int[] buttonSize = propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defButtonSize[0];
            buttonSize[1] = defButtonSize[1];
        }

        // There are two sources for multiple w/l values.
        // 1. Defined in the properties file.
        // 2. Defined in the image Dicom header data.
        list = history.getFunctionPanelWLList();
        
        /*
        StudyDbImageNode imageNode = viewDex.appMainAdmin.vgControl.getSelectedImageNode();
        if(imageNode != null){
            windowCenter = imageNode.getWindowCenter();
            windowWidth = imageNode.getWindowWidth();
        }*/

        //boolean mouseEnableStatus = wlMouseStatus();
        //boolean additionalStatus = wlAdditionalStatus();
        //boolean userDefinedStatus = wlUserDefinedStatus();

        if(!list.isEmpty()){
            //if(wlMouseStatus() && wlAdditionalStatus() && windowWidth != null){
            if(wlAdditionalStatus() && windowWidth != null){
                for(int i = 0; i < windowCenter.length; i++){
                    String propValue = list.get(i).getPropValue();
                    String propFont = list.get(i).getPropFont();
                    
                    if(windowWidth.length >= 2){
                        //btnList.add(new JButton(propValue));
                        String propName = list.get(i).getPropName();
                        String propName2 = Integer.toString(windowWidth[i]);
                        String propName3 = Integer.toString(windowCenter[i]);
                        String propName4 = propName2 + "/" + propName3;
                        
                        btnList.add(new JButton(propValue));
                        btnList.get(i).setFocusable(false);
                        btnList.get(i).setMargin(new Insets(0,0,0,0));
                        btnList.get(i).setActionCommand(propName);
                        btnList.get(i).setFont(Font.decode(propFont));
                        
                        if(buttonColorPropertyStatus)
                            btnList.get(i).setBackground(new Color(buttonColor[0],
                                    buttonColor[1],
                                    buttonColor[2]));
                        
                        btnList.get(i).setForeground(new Color(
                                buttonTextColor[0],
                                buttonTextColor[1],
                                buttonTextColor[2]));
                        
                        if(buttonBorderColorPropertyStatus)
                            btnList.get(i).setBorder(border);
                        
                        btnList.get(i).setPreferredSize(new Dimension(
                                buttonSize[0],
                                buttonSize[1]));
                        /*
                         if(windowWidth.length < 3)
                         btnList.get(i).setPreferredSize(new Dimension(80, 23));
                         if(windowWidth.length == 3)
                         btnList.get(i).setPreferredSize(new Dimension(67, 23));
                         */
                        
                        btnList.get(i).setFocusPainted(false);
                        btnList.get(i).addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                if(viewDex.vgStudyNextCaseExtendedControl != null)
                                    viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                                viewDex.requestFocusInWindow();
                                wlPresetButtonActionPerformed(evt);
                            }
                        });
                    }
                }
            }
        }
        
        // panel
        if(!btnList.isEmpty()){
            panel = new JPanel();
            if(btnList.size() < 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
            if(btnList.size() == 3)
                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            //panel.setBackground(new Color(125,89,134));
            panel.setBackground(new Color(panelColor[0],panelColor[1],panelColor[2]));
            
            for(int i = 0; i < btnList.size(); i++)
                panel.add(btnList.get(i));
        }
        return panel;
    }
    
    /*
     *
     */
    public void createWindowLevelLowerPanelDynamic(){
        if(wlAdditionalPanel != null)
            wlMainPanel.remove(wlAdditionalPanel);
        
        wlAdditionalPanel = createWindowLevelAdditionalPanel();
        
        if(wlResetPanel != null && wlUserDefinedPanel != null && wlAdditionalPanel != null){
                wlMainPanel.add(wlResetPanel, "0,1");
                wlMainPanel.add(wlUserDefinedPanel, "0,3");
                wlMainPanel.add(wlAdditionalPanel, "0,5");
            }
            else{
                if(wlResetPanel != null && wlUserDefinedPanel == null && wlAdditionalPanel != null){
                    wlMainPanel.add(wlResetPanel, "0,1");
                    wlMainPanel.add(wlAdditionalPanel, "0,3");
                }
                else{
                    if(wlResetPanel == null && wlUserDefinedPanel != null && wlAdditionalPanel != null){
                        wlMainPanel.add(wlUserDefinedPanel, "0,1");
                        wlMainPanel.add(wlAdditionalPanel, "0,3");
                    }
                    else{
                        if(wlResetPanel == null && wlUserDefinedPanel == null && wlAdditionalPanel != null)
                            wlMainPanel.add(wlAdditionalPanel, "0,1");
                    }
                }
            }
        }
    
    // *****************************************************************
    //
    //       Pan
    //
    // ****************************************************************
    private JPanel createPanMainPanel(int mode){
        // panel color
        String key = "functionpanel.pan.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment
        key = "functionpanel.pan.panel.alignment.top";
        int alignTop = propUtils.getPropertyIntegerValue(prop, key);
        if(alignTop == 0)
            alignTop = 0;
        
        // alignment
        key = "functionpanel.pan.panel.alignment.bottom";
        int alignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if(alignBottom == 0)
            alignBottom = 7;
        
        // title border color
        key = "functionpanel.pan.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if(borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0){
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }
        
        // border
        Border lborder = BorderFactory.createLineBorder(new Color(
                borderColor[0], borderColor[1],
                borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Pan");
        
        // title color
        key = "functionpanel.pan.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if(titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0){
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }
        // titlefont
        String defTitleFont = "SansSerif-plain-16";
        key = "functionpanel.pan.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if(titleFont.equals(""))
            titleFont = defTitleFont;
        
        // size
        //double size[][] = {{f},{8,p,8}};
        double[][] size = new double[2][4];
        if(mode == 1){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][1] = p;
            size[1][2] = (double) alignBottom;
        }
        
        // panel
        JPanel panel = new JPanel();
        
        panel.setLayout(new TableLayout(size));
        //tborder.setTitleJustification(TitledBorder.LEFT);
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0], titleColor[1], titleColor[2]));
        panel.setBorder(tborder);
        return panel;
    }
    
    /**
     * 
     * @return
     */
    private JPanel createPanUpperPanel(){
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        String defFont;
        
        // button color
        boolean buttonColorPropertyStatus = true;
        String key = "functionpanel.pan.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
        
        // button text color
        key = "functionpanel.pan.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }
        
        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.pan.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        // border
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // button size
        int[] defButtonSize = {90,25};
        key = "functionpanel.pan.button.size";
        int[] buttonSize  = propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defButtonSize[0];
            buttonSize[1] = defButtonSize[1];
        }
        
        // pan enable
        String defPanEnable = "y";
        key = "functionpanel.pan";
        String panEnable = propUtils.getPropertyStringValue(prop, key);
        if(panEnable.equals(""))
            panEnable = defPanEnable;
        
        // button font
        String defButtonFont = "Arial-plain-16";
        key = "functionpanel.pan.button.font";
        String buttonFont = propUtils.getPropertyFontValue(prop, key);
        if(buttonFont.equals(""))
            buttonFont = defButtonFont;
        
        // button
        if(panEnable != null && (panEnable.equalsIgnoreCase("yes") ||
                panEnable.equalsIgnoreCase("y"))){
            JButton btn = new JButton("Reset");
            btn.setMargin(new Insets(0,0,0,0));
            btn.setFont(Font.decode(buttonFont));
            //btn.setFocusPainted(false);
            btn.setFocusable(false);
            if(buttonColorPropertyStatus)
                btn.setBackground(new Color(buttonColor[0], buttonColor[1], buttonColor[2]));
            btn.setForeground(new Color(buttonTextColor[0], buttonTextColor[1],
                    buttonTextColor[2]));
            btn.setPreferredSize(new Dimension(buttonSize[0], buttonSize[1]));
            if(buttonBorderColorPropertyStatus)
                btn.setBorder(border);
            
            btn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if(viewDex.vgStudyNextCaseExtendedControl != null)
                        viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                    viewDex.requestFocusInWindow();
                    panResetButtonActionPerformed();
                }
            });
            btnList.add(btn);
            viewDex.canvasControl.setPanMode(1);
            
            //CanvasContextMenu menu = CanvasContextMenu.getInstance();
            //addPropertyChangeListener("pan", vdex.canvasContextMenu);
            // Object newValue = null;
            // String menuItem = "pan";
            // Update the contextmenu with a pan menu item.
            //firePropertyChange("pan", 20, 10);
        }
        
        // panel color
        key = "functionpanel.pan.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // panel
        JPanel panel = new JPanel();
        double size[][] = {{f,p,f},{f,p,f}};
        panel.setLayout(new TableLayout(size));
        //panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //panel.add(btnList.get(0), "1,1");
        
        for(int i = 0; i < btnList.size(); i++)
            panel.add(btnList.get(i), "1,1");
        
        if(btnList.size() != 0)
            panPanelStatus = 1;
        
        return panel;
    }
    
    // ********************************************************
    //
    //        Zoom
    //
    // *********************************************************
    private JPanel createZoomMainPanel(int mode){
        double[][] size = new double[2][5];
        
        // panel color
        String key = "functionpanel.zoom.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment
        key = "functionpanel.zoom.panel.alignment.top";
        int alignTop = propUtils.getPropertyIntegerValue(prop, key);
        if (alignTop == 0)
            alignTop = 0;
        
        // alignment
        key = "functionpanel.zoom.panel.alignment.bottom";
        int alignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if (alignBottom == 0)
            alignBottom = 5;
        
        // alignment
        key = "functionpanel.zoom.button.vertical.space";
        int verticalSpace = propUtils.getPropertyIntegerValue(prop, key);
        if (verticalSpace == 0)
            verticalSpace = 10;
        
        // border color
        key = "functionpanel.zoom.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if(borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0){
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }
        
        // border
        Border lborder = BorderFactory.createLineBorder(new Color(
                borderColor[0], borderColor[1], borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Zoom");
        
        // title color
        key = "functionpanel.zoom.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if(titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0){
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }
        
        // title font
        String defTitleFont = "SansSerif-plain-16";
        key = "functionpanel.zoom.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if(titleFont.equals(""))
            titleFont = defTitleFont;
        
        //double size[][] = {{f},{8,f,8}};
        if(mode == 0){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][1] = f;
            size[1][2] = (double) alignBottom;
        }
        
        //double size[][] = {{f},{8,p,8}};
        if(mode == 1){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][0] = p;
            size[1][1] = (double) alignBottom;
        }
        
        //double size[][] = {{f},{8,p,10,p,8}};
        if(mode == 2){
            size[0][0] = f;
            size[1][0] = (double) alignTop;
            size[1][1] = p;
            size[1][2] = (double) verticalSpace;
            size[1][3] = p;
            size[1][4] = (double) alignBottom;
        }
        
        // panel
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.setBackground(new Color(panelColor[0],
                panelColor[1],
                panelColor[2]));
        //tborder.setTitleJustification(TitledBorder.LEFT);
        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0],
                titleColor[1],
                titleColor[2]));
        panel.setBorder(tborder);
        
        return panel;
    }
    
    /**
     * Create the zooUpperPanel.
     * @return
     */
    private JPanel createZoomUpperPanel(){
        ArrayList <VgFunctionPanelZoomControl> list;
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        JPanel panel = null;
        
        // panel color
        String key = "functionpanel.zoom.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // button color
        boolean buttonColorPropertyStatus = true;
        key = "functionpanel.zoom.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
        
        // button text color
        key = "functionpanel.zoom.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defTextColor[2];
        }
        
        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.zoom.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        // border
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // button font
        String defButtonFont = "Arial-plain-16";
        key = "functionpanel.zoom.button.font";
        String buttonFont = propUtils.getPropertyFontValue(prop, key);
        if(buttonFont.equals(""))
            buttonFont = defButtonFont;
        
        // button size
        int[] defButtonSize = {70, 25};
        key = "functionpanel.zoom.button.size";
        int[] buttonSize  = propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defButtonSize[0];
            buttonSize[1] = defButtonSize[1];
        }
        
        // zoom.reset
        String defZoomReset = "yes";
        key = "functionpanel.zoom.reset";
        String resetEnable = propUtils.getPropertyStringValue(prop, key);
        if(resetEnable.equals(""))
                resetEnable = defZoomReset;
        
        // zoom.in
        String defZoomIn = "y";
        key = "functionpanel.zoom.in";
        String zoomInEnable = propUtils.getPropertyStringValue(prop, key);
        if(zoomInEnable.equals(""))
            zoomInEnable = defZoomIn;
        
        // zoom.out
        String defZoomOut = "y";
        key = "functionpanel.zoom.out";
        String zoomOutEnable = propUtils.getPropertyStringValue(prop, key);
        if(zoomOutEnable.equals(""))
                    zoomOutEnable= defZoomOut;
        
        // reset
        if(resetEnable != null && (resetEnable.equalsIgnoreCase("yes") ||
                resetEnable.equalsIgnoreCase("y"))){
            
            btnList.add(new JButton("Reset"));
            int cnt1 = btnList.size();
            btnList.get(cnt1 - 1).setFocusable(false);
            btnList.get(cnt1 - 1).setMargin(new Insets(0,0,0,0));
            btnList.get(cnt1 - 1).setFont(Font.decode(buttonFont));
            btnList.get(cnt1 - 1).setActionCommand("zoom.reset");
            if(buttonColorPropertyStatus)
                btnList.get(cnt1 - 1).setBackground(new Color(
                        buttonColor[0], buttonColor[1], buttonColor[2]));
            
            btnList.get(cnt1 - 1).setForeground(new Color(
                    buttonTextColor[0], buttonTextColor[1], buttonTextColor[2]));
            
            if(buttonBorderColorPropertyStatus)
                btnList.get(cnt1 - 1).setBorder(border);
            
            btnList.get(cnt1 - 1).setPreferredSize(new Dimension(
                    buttonSize[0], buttonSize[1]));
            
            // zoom in
            if(zoomInEnable != null && (zoomInEnable.equalsIgnoreCase("yes") ||
                zoomInEnable.equalsIgnoreCase("y"))){
                
                btnList.add(new JButton("In"));
                int cnt = btnList.size();
                btnList.get(cnt - 1).setFocusable(false);
                btnList.get(cnt - 1).setMargin(new Insets(0,0,0,0));
                btnList.get(cnt - 1).setFont(Font.decode(buttonFont));
                btnList.get(cnt - 1).setActionCommand("zoom.in");
                if(buttonColorPropertyStatus){
                    btnList.get(cnt - 1).setBackground(new Color(
                            buttonColor[0], buttonColor[1], buttonColor[2]));
                }
                btnList.get(cnt - 1).setForeground(new Color(buttonTextColor[0],
                        buttonTextColor[1], buttonTextColor[2]));
                
                if(buttonBorderColorPropertyStatus)
                    btnList.get(cnt - 1).setBorder(border);
                
                btnList.get(cnt - 1).setPreferredSize(new Dimension(
                        buttonSize[0], buttonSize[1]));
                
                viewDex.canvasControl.setZoomInMode(1);
            }
            
             // zoom out
            if(zoomOutEnable != null && (zoomOutEnable.equalsIgnoreCase("yes") ||
                zoomOutEnable.equalsIgnoreCase("y"))){
                
                btnList.add(new JButton("Out"));
                int cnt = btnList.size();
                btnList.get(cnt - 1).setFocusable(false);
                btnList.get(cnt - 1).setMargin(new Insets(0,0,0,0));
                btnList.get(cnt - 1).setFont(Font.decode(buttonFont));
                btnList.get(cnt - 1).setActionCommand("zoom.out");
                
                if(buttonColorPropertyStatus)
                    btnList.get(cnt - 1).setBackground(new Color(
                            buttonColor[0], buttonColor[1], buttonColor[2]));
                
                btnList.get(cnt - 1).setForeground(new Color(buttonTextColor[0],
                        buttonTextColor[1], buttonTextColor[2]));
                
                if(buttonBorderColorPropertyStatus)
                    btnList.get(cnt - 1).setBorder(border);
                
                btnList.get(cnt - 1).setPreferredSize(new Dimension(
                        buttonSize[0], buttonSize[1]));
                
                viewDex.canvasControl.setZoomOutMode(1);
            }
        }
        
        // set button properties and actions
        for(int i = 0; i < btnList.size(); i++){
            zoomPanelStatus = 1;
            /*if(list.size() < 3)
                btnList.get(i).setPreferredSize(new Dimension(80, 23));
            if(list.size() == 3)
                btnList.get(i).setPreferredSize(new Dimension(67, 23));
            */
            btnList.get(i).setFocusPainted(false);
            btnList.get(i).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if(viewDex.vgStudyNextCaseExtendedControl != null)
                        viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                    viewDex.requestFocusInWindow();
                    zoomControlButtonActionPerformed(evt);
                }
            });
        }

        // panel
        //list = history.getFunctionPanelZoomControlList();
        //String name = list.get(i).getZoomModeName();
        //String propName = list.get(i).getPropName();
        //String propFont = list.get(i).getPropFontName();
        
        // space
        int defButtonSpace1 = 18;
        int defButtonSpace2 = 12;
        int buttonSpace = 0;
        key = "functionpanel.zoom.button.horizontal.space";
        buttonSpace  = propUtils.getPropertyIntegerValue(prop, key);
        if(buttonSpace == 0){
            if(btnList.size() < 3)
                buttonSpace = defButtonSpace1;
            if(btnList.size() == 3)
                buttonSpace = defButtonSpace2;
        }
        
        panel = new JPanel();
        if(btnList.size() < 3)
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonSpace, 0));
        if(btnList.size() == 3)
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonSpace, 0));
        //panel.setBackground(new Color(125,89,134));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        
        for(int i = 0; i < btnList.size(); i++)
            panel.add(btnList.get(i));
        
        //if(btnList.size() != 0)
        //  zoomPanelStatus = 2;
        
        return panel;
    }
    
    /**
     * Create the zoomLowerPanel.
     * @return
     */
    private JPanel createZoomLowerPanel(){
        ArrayList <VgFunctionPanelZoomModeControl> list;
        ArrayList <JButton> btnList = new ArrayList<JButton>();
        JPanel panel = null;
        
        // panel color
        String key = "functionpanel.zoom.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // button color
        boolean buttonColorPropertyStatus = true;
        key = "functionpanel.displaysize.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
        
        // button text color
        key = "functionpanel.displaysize.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }
        
        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "functionpanel.displaysize.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        // border
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus)
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        
        // button font
        String defButtonFont = "Arial-plain-16";
        key = "functionpanel.displaysize.button.font";
        String buttonFont = propUtils.getPropertyFontValue(prop, key);
        if(buttonFont.equals(""))
            buttonFont = defButtonFont;
        
        // displaySize button size
        int[] defDisplaySizeButtonSize = {75, 25};
        key = "functionpanel.displaysize.button.size";
        int[] buttonSize= propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defDisplaySizeButtonSize[0];
            buttonSize[1] = defDisplaySizeButtonSize[1];
        }
        
        list = history.getFunctionPanelZoomModeList();
        if(zoomAdjustStatus() == 1){
            for(int i = 0; i < list.size(); i++){
                
                String name = list.get(i).getZoomModeName();
                String propName = list.get(i).getPropName();
                String propFont = list.get(i).getPropFontName();
                
                //float zoom = functionPanelZoomList.get(i).getZoom();
                btnList.add(new JButton(name));
                btnList.get(i).setFocusable(false);
                btnList.get(i).setMargin(new Insets(0,0,0,0));
                btnList.get(i).setFont(Font.decode(buttonFont));
                btnList.get(i).setActionCommand(propName);
                //btnList.get(i).setActionCommand(actionCommand);
                
                if(buttonColorPropertyStatus)
                    btnList.get(i).setBackground(new Color(buttonColor[0],
                            buttonColor[1], buttonColor[2]));
                
                btnList.get(i).setForeground(new Color(
                        buttonTextColor[0],
                        buttonTextColor[1],
                        buttonTextColor[2]));
                
                if(buttonBorderColorPropertyStatus)
                    btnList.get(i).setBorder(border);
                
                //if(list.size() < 3)
                //  btnList.get(i).setPreferredSize(new Dimension(80, 23));
                //if(list.size() == 3)
                //  btnList.get(i).setPreferredSize(new Dimension(60, 23));
                //if(list.size() == 4)
                //  btnList.get(i).setPreferredSize(new Dimension(40, 23));
                //if(list.size() == 5)
                //  btnList.get(i).setPreferredSize(new Dimension(35, 23));
                
                btnList.get(i).setPreferredSize(new Dimension(
                        buttonSize[0],
                        buttonSize[1]));
                
                btnList.get(i).setFocusPainted(false);
                btnList.get(i).addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if(viewDex.vgStudyNextCaseExtendedControl != null)
                            viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                        viewDex.requestFocusInWindow();
                        zoomModeButtonActionPerformed(evt);
                    }
                });
            }
        }
        
        // panel
        panel = new JPanel();
        
        // space
        int defButtonSpace1 = 18;
        int defButtonSpace2 = 12;
        int buttonSpace = 0;
        key = "functionpanel.displaysize.button.horizontal.space";
        buttonSpace  = propUtils.getPropertyIntegerValue(prop, key);
        if(buttonSpace == 0){
            if(list.size() < 3)
                buttonSpace = defButtonSpace1;
            if(list.size() == 3)
                buttonSpace = defButtonSpace2;
        }
        
        if(list.size() < 3)
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonSpace, 0));
        if(list.size() == 3)
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonSpace, 0));
        //panel.setBackground(new Color(138,176,134));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        
        for(int i = 0; i < btnList.size(); i++)
            panel.add(btnList.get(i));
        
        if(btnList.size() != 0)
            zoomPanelStatus = 2;
        
        return panel;
    }
    
    
    /**
     * Return true if ...zoom.reset (zoom.adjust) is defined with "yes".
     * @return int 1 if true, 0 if false.
     */
    private int zoomAdjustStatus(){
        ArrayList <VgFunctionPanelZoomControl> list;
        int status = 0;
        
        list = history.getFunctionPanelZoomControlList();
        for(int i = 0; i < list.size(); i++){
            String propNamePart = list.get(i).getPropNamePart();
            String propVal = list.get(i).getPropValue();
            if(propNamePart.equalsIgnoreCase("zoom.reset") &&
                    (propVal.equalsIgnoreCase("yes") || propVal.equalsIgnoreCase("y")))
                status = 1;
        }
        return status;
    }
    
    /**
     * Return true if property functionpanel.wl.mouse is defined with "yes".
     * If not return false.
     * @return true or false.
     */
    private boolean wlMouseStatus(){
        boolean status = false;

        String defMouseEnable = "y";
        String key = "functionpanel.wl.mouse";
        String mouseEnable = propUtils.getPropertyStringValue(prop, key);
        if(mouseEnable.equals(""))
            mouseEnable = defMouseEnable;
        
        if(mouseEnable.equalsIgnoreCase("yes") || mouseEnable.equalsIgnoreCase("y")){
            status = true;
        }

        return status;
    }

    /**
     * Return true if the property functionpanel.wl.additional-ww-wc is defined
     * with "yes". If not return false.
     * @return true or false.
     */
    private boolean wlAdditionalStatus(){
        boolean status = false;

        String defadditionalWwWc = "n";
        String key = "functionpanel.wl.additional-ww-wc";
        String additionalWwWc = propUtils.getPropertyStringValue(prop, key);
        
        if(additionalWwWc.equals(""))
            additionalWwWc = defadditionalWwWc;
        
        if(additionalWwWc.equalsIgnoreCase("yes") || additionalWwWc.equalsIgnoreCase("y"))
            status = true;

      return status;
    }

    /**
     * Return true if property functionpanel.wl.userdefined-ww-wc is defined
     * with "yes". Else return false.
     * @return true or false.
     */
    private boolean wlUserDefinedStatus(){
        boolean status = false;

        String defValue = "n";
        String key = "functionpanel.wl.userdefined-ww-wc";
        String userdefined = propUtils.getPropertyStringValue(prop, key);

        if(userdefined.equals(""))
            userdefined = defValue;

        if(userdefined.equalsIgnoreCase("yes") || userdefined.equalsIgnoreCase("y"))
            status = true;

        return status;
    }
    
    
    // **************************************************
    // Setter functions
    // **************************************************
    
    /*
     */
    public void setWindowWidth(int[] windowwidth){
        windowWidth = windowwidth;
    }
    
    /*
     */
    public void setWindowCenter(int[] windowcenter){
        windowCenter = windowcenter;
    }
    
    
// ************************************************************************
//
//  Actions
//
// ************************************************************************
    
    private void wlResetButtonActionPerformed(){
        viewDex.canvasControl.wlReset();
    }

    private void wlSet(ActionEvent event){
        ArrayList <VgFunctionPanelUserDefinedWLControl> list;
        int [][] imageStats = viewDex.windowLevel.getImageStat();

        String cmd = event.getActionCommand();
        list = history.getFunctionPanelUserDefinedWLList();
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().equalsIgnoreCase(cmd)){
                int w = list.get(i).getWindowWidth();
                int c = list.get(i).getWindowCenter();
                viewDex.windowLevel.setWindowLevel(w, c + imageStats[0][7]);
            }
        }
    }

    private void wlPresetButtonActionPerformed(ActionEvent event){
        String command = event.getActionCommand();
        int[] val = getVindowLevelPresetValue(command);
        viewDex.canvasControl.wlPreset(val);
    }
    
    private void panResetButtonActionPerformed(){
        viewDex.canvasControl.setPanControlAction("reset");
    }
    
    private void zoomControlButtonActionPerformed(ActionEvent event){
        String command = event.getActionCommand();
        viewDex.canvasControl.setZoomControlAction(command);
    }
    
    private void zoomModeButtonActionPerformed(ActionEvent event){
        String command = event.getActionCommand();
        double val = getZoomModeValue(command);
        viewDex.canvasControl.setZoomModeAction(val);
    }
    
    private double getZoomModeValue(String str){
        ArrayList <VgFunctionPanelZoomModeControl> list;
        double zoomVal = 0.0;
        
        list = history.getFunctionPanelZoomModeList();
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getPropName().equalsIgnoreCase(str)){
                zoomVal = list.get(i).getZoomValue();
            }
        }
        return zoomVal;
    }
    
    private int[] getVindowLevelPresetValue(String str){
        ArrayList <VgFunctionPanelWLControl> list;
        int val[] = new int[2];
        
        list = history.getFunctionPanelWLList();
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getPropName().equalsIgnoreCase(str)){
                //val = list.get(i).getZoomValue();
                if(i <= windowWidth.length)
                    val[0] = windowWidth[i];
                if(i <= windowCenter.length)
                    val[1] = windowCenter[i];
            }
        }
        return val;
    }
    
    // ************************************************
    //
    //      Listeners
    //
    // ************************************************
    /** Add a property change listener. */
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    
    /** Remove a property change listener. */
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }
    
    /*******************************************************
     * KeyListener interface
     ******************************************************/
    /*
    public void keyReleased(KeyEvent e) {
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
        System.out.println("VgFunctionPanel: keyPressed");
    }
     */
    
    /*******************************************************
     * focusListener interface
     ******************************************************/
    /*
    public void focusGained(java.awt.event.FocusEvent e){
    }
    
    public void focusLost(java.awt.event.FocusEvent e){ 
    }
    */
}
