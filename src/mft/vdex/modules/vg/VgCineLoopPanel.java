/* @(#) VgCineLoopPanel.java 06/09/2005
 *
 * Copyright (c) 2007 Sahlgrenska University Hospital.
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
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import mft.vdex.app.ViewDex;
import mft.vdex.controls.*;
import mft.vdex.app.AppPropertyUtils;
import mft.vdex.ds.StudyDbLocalizationStatus;

public class VgCineLoopPanel extends JPanel {

    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private VgHistory history;
    private JPanel mainPanel;
    public JButton eraseButton;
    public JButton showHideButton;
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    ArrayList<JButton> btnList;
    //ArrayList<JToggleButton> btnList;
    int[] btnColor;
    int[] btnSelColor;
    public boolean btnColorPropertyStatus;
    public boolean btnSelColorPropertyStatus;
    int selLoopValueBtn = -1;
    private JButton stopButton;

    public VgCineLoopPanel(ViewDex viewdex, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.history = vghistory;

        propUtils = new AppPropertyUtils();
        createUI();
    }

    protected void createUI() {
        createLayout();
        mainPanel = createMainPanel();
        JPanel upperPanel = createUpperPanel();
        JPanel lowerPanel = createLowerPanel();

        mainPanel.add(upperPanel, "0,0");
        mainPanel.add(lowerPanel, "0,1");
        this.add(mainPanel, "1,0");
    }

    /**
     * Create the main layout.
     */
    private void createLayout() {
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "cineloop.panel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // panel alignment left
        key = "cineloop.panel.alignment.left";
        int panelAlignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (panelAlignLeft == 0) {
            panelAlignLeft = 5;
        }

        // panel alignment right
        key = "cineloop.panel.alignment.right";
        int panelAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if (panelAlignRight == 0) {
            panelAlignRight = 5;
        }

        // panel alignment top
        /*
        key = "cineloop.panel.alignment.top";
        int panelAlignTop = propUtils.getPropertyIntegerValue(prop, key);
        if (panelAlignTop == 0) {
            panelAlignTop = 5;
        }

        // panel alignment bottom
        key = "cineloop.panel.alignment.bottom";
        int panelAlignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if (panelAlignBottom == 0) {
            panelAlignBottom = 5;
        }*/

        //zzzzzzzzzzzzzzzzzzzzzzzzzztest
        //double[][] size = {{panelAlignLeft, f, panelAlignRight}, {panelAlignBottom}};
        double[][] size = {{panelAlignLeft, f, panelAlignRight}, {p}};
        this.setLayout(new TableLayout(size));
        this.setBackground(new Color(color[0], color[1], color[2]));
    }

    /**
     * Create the mainPanel.
     * @return <code>JPanel</code> the panel.
     */
    private JPanel createMainPanel() {
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "cineloop.panel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // border color
        key = "cineloop.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0], borderColor[1], borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Cine-loop");


        // title font
        String defTitleFont = "SansSerif-plain-16";
        key = "cineloop.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if (titleFont.equals("")) {
            titleFont = defTitleFont;
        }

        // title color
        key = "cineloop.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if (titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0) {
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }

        // panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(color[0], color[1], color[2]));
        double[][] size = {{f}, {p, p}};
        panel.setLayout(new TableLayout(size));

        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0], titleColor[1], titleColor[2]));
        panel.setBorder(tborder);

        return panel;
    }

    /**
     * Create the cineLoopPanel.
     * @return <code>JPanel</code> the panel.
     */
    private JPanel createUpperPanel() {
        ArrayList<VgCineLoopPanelControl> list;
        btnList = new ArrayList<JButton>();

        // properties
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "cineloop.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if (panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0) {
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // button color
        btnColorPropertyStatus = true;
        key = "cineloop.button.color";
        btnColor = propUtils.getPropertyColorValue(prop, key);
        if (btnColor[0] == 0 && btnColor[1] == 0 && btnColor[2] == 0) {
            btnColorPropertyStatus = false;
        }

        // button select color
        btnSelColorPropertyStatus = true;
        key = "cineloop.button.select.color";
        btnSelColor = propUtils.getPropertyColorValue(prop, key);
        if (btnSelColor[0] == 0 && btnSelColor[1] == 0 && btnSelColor[2] == 0) {
            btnSelColorPropertyStatus = false;
        }


        // button text color
        key = "cineloop.button.text.color";
        int[] btnTextColor = propUtils.getPropertyColorValue(prop, key);
        if (btnTextColor[0] == 0 && btnTextColor[1] == 0 && btnTextColor[2] == 0) {
            btnTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            btnTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            btnTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }

        // button border color
        boolean btnBorderColorPropertyStatus = true;
        key = "cineloop.button.border.color";
        int[] btnBorderColor = propUtils.getPropertyColorValue(prop, key);
        if (btnBorderColor[0] == 0 && btnBorderColor[1] == 0 && btnBorderColor[2] == 0) {
            btnBorderColorPropertyStatus = false;
        }

        // border
        Border border = BorderFactory.createLineBorder(new Color(10, 10, 10));
        if (btnBorderColorPropertyStatus) {
            border = BorderFactory.createLineBorder(
                    new Color(btnBorderColor[0], btnBorderColor[1], btnBorderColor[2]));
        }

        // button font
        String defBtnFont = "Arial-plain-16";
        key = "cineloop.button.font";
        String btnFont = propUtils.getPropertyFontValue(prop, key);
        if (btnFont.equals("")) {
            btnFont = defBtnFont;
        }

        // displaySize button size
        int[] defBtnSize = {80, 25};
        key = "cineloop.frameinterval.button.size";
        int[] btnSize = propUtils.getPropertySizeValue(prop, key);
        if (btnSize[0] == 0 && btnSize[1] == 0) {
            btnSize[0] = defBtnSize[0];
            btnSize[1] = defBtnSize[1];
        }

        list = history.getCineLoopPanelControlList();
        for (int i = 0; i < list.size(); i++) {
            String btnName = list.get(i).getButtonName();
            //String btnPropName = list.get(i).getButtonPropName();
            String btnPropIntervalName = list.get(i).getButtonPropIntervalName();

            btnList.add(new JButton(btnName));
            //btnList.add(new JToggleButton(btnName));
            btnList.get(i).setFocusable(false);
            btnList.get(i).setSelected(false);
            btnList.get(i).setEnabled(false);
            btnList.get(i).setMargin(new Insets(0, 0, 0, 0));
            btnList.get(i).setFont(Font.decode(btnFont));
            btnList.get(i).setActionCommand(btnPropIntervalName);
            //btnList.get(i).setActionCommand(actionCommand);

            if (btnColorPropertyStatus) {
                btnList.get(i).setBackground(new Color(btnColor[0],
                        btnColor[1], btnColor[2]));
            }

            btnList.get(i).setForeground(new Color(
                    btnTextColor[0],
                    btnTextColor[1],
                    btnTextColor[2]));

            if (btnBorderColorPropertyStatus) {
                btnList.get(i).setBorder(border);
            }

            //btnList.get(i).setContentAreaFilled(false);

            //if(list.size() < 3)
            //  btnList.get(i).setPreferredSize(new Dimension(80, 23));
            //if(list.size() == 3)
            //  btnList.get(i).setPreferredSize(new Dimension(60, 23));
            //if(list.size() == 4)
            //  btnList.get(i).setPreferredSize(new Dimension(40, 23));
            //if(list.size() == 5)
            //  btnList.get(i).setPreferredSize(new Dimension(35, 23));

            btnList.get(i).setPreferredSize(new Dimension(
                    btnSize[0], btnSize[1]));

            btnList.get(i).setFocusPainted(false);
            btnList.get(i).addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if(viewDex.vgStudyNextCaseExtendedControl != null)
                        viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                    mainPanel.requestFocusInWindow();
                    cineloopBtnActionPerformed(evt);
                }
                });
        }

        // Group the toggglebuttons
        /*ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < list.size(); i++)
        group.add(btnList.get(i));
         */

        // button panel
        JPanel btnPanel = new JPanel();
        // space
        int defBtnSpaceHor = 18;
        int defBtnSpaceHor2 = 8;
        int btnHorSpace1 = 0;
        int btnHorSpace2 = 0;

        key = "cineloop.frameinterval.button.space.horizontal";
        int btnHorSpace = propUtils.getPropertyIntegerValue(prop, key);

        //test
        /*
        key = "cineloop.frameinterval.button.alignment.top";
        int btnAlignTop= propUtils.getPropertyIntegerValue(prop, key);
        key = "cineloop.frameinterval.button.alignment.bottom";
        int btnAlignBottom= propUtils.getPropertyIntegerValue(prop, key);
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, btnHGap, btnVGap));
         */
        // end test


        if (btnHorSpace == 0) {
            btnHorSpace1 = defBtnSpaceHor;
            btnHorSpace2 = defBtnSpaceHor2;

            if (list.size() < 3) {
                btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
                        btnHorSpace1, 0));
            }
            if (list.size() >= 3) {
                btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
                        btnHorSpace2, 0));
            }
        } else {
            btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
                    btnHorSpace, 0));
        }



        //panel.setBackground(new Color(138,176,134));
        btnPanel.setBackground(
                new Color(panelColor[0], panelColor[1], panelColor[2]));

        for (int i = 0; i < btnList.size(); i++) {
            btnPanel.add(btnList.get(i));
        }

        // panel
        JPanel panel = new JPanel();

        // button alignment top
        key = "cineloop.panel.alignment.top";
        int btnTopAlign = propUtils.getPropertyIntegerValue(prop, key);
        if (btnTopAlign == 0) {
            btnTopAlign = 1;
        }

        // NOT IN USE
        // button alignment bottom
        key = "cineloop.panel.alignment.bottom";
        int btnBottomAlign = propUtils.getPropertyIntegerValue(prop, key);
        if (btnBottomAlign == 0) {
            btnBottomAlign = 1;
        }


        // size
        //double size[][] = {{f,p,f},{8,p,8}};
        double[][] size = new double[2][3];
        size[0][0] = f;
        size[0][1] = p;
        size[0][2] = f;
        size[1][0] = (double) btnTopAlign;
        size[1][1] = p;
        //zzzzzzzz test
        //size[1][2] = (double) btnBottomAlign;
        size[1][2] = f;

        panel = new JPanel();
        panel.setLayout(new TableLayout(size));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));

        // add
        panel.add(btnPanel, "1,1");

        return panel;
    }

    /**
     * Create the cineLoopPanel.
     * @return <code>JPanel</code> the panel.
     */
    private JPanel createLowerPanel() {
        JPanel panel = null;

        // properties
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "cineloop.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if (panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0) {
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // button color
        btnColorPropertyStatus = true;
        key = "cineloop.button.color";
        btnColor = propUtils.getPropertyColorValue(prop, key);
        if (btnColor[0] == 0 && btnColor[1] == 0 && btnColor[2] == 0) {
            btnColorPropertyStatus = false;
        }

        // button select color
        btnSelColorPropertyStatus = true;
        key = "cineloop.button.select.color";
        btnSelColor = propUtils.getPropertyColorValue(prop, key);
        if (btnSelColor[0] == 0 && btnSelColor[1] == 0 && btnSelColor[2] == 0) {
            btnSelColorPropertyStatus = false;
        }


        // button text color
        key = "cineloop.button.text.color";
        int[] btnTextColor = propUtils.getPropertyColorValue(prop, key);
        if (btnTextColor[0] == 0 && btnTextColor[1] == 0 && btnTextColor[2] == 0) {
            btnTextColor[0] = AppPropertyUtils.defTextColor[0];
            btnTextColor[1] = AppPropertyUtils.defTextColor[1];
            btnTextColor[2] = AppPropertyUtils.defTextColor[2];
        }

        // button border color
        boolean btnBorderColorPropertyStatus = true;
        key = "cineloop.button.border.color";
        int[] btnBorderColor = propUtils.getPropertyColorValue(prop, key);
        if (btnBorderColor[0] == 0 && btnBorderColor[1] == 0 && btnBorderColor[2] == 0) {
            btnBorderColorPropertyStatus = false;
        }

        // border
        Border border = BorderFactory.createLineBorder(new Color(10, 10, 10));
        if (btnBorderColorPropertyStatus) {
            border = BorderFactory.createLineBorder(
                    new Color(btnBorderColor[0], btnBorderColor[1], btnBorderColor[2]));
        }

        // button font
        String defBtnFont = "Arial-plain-16";
        key = "cineloop.button.font";
        String btnFont = propUtils.getPropertyFontValue(prop, key);
        if (btnFont.equals("")) {
            btnFont = defBtnFont;
        }

        // button size
        int[] defBtnSize = {100, 28};
        key = "cineloop.stop.button.size";
        int[] btnSize = propUtils.getPropertySizeValue(prop, key);
        if (btnSize[0] == 0 && btnSize[1] == 0) {
            btnSize[0] = defBtnSize[0];
            btnSize[1] = defBtnSize[1];
        }

        // button alignment top
        key = "cineloop.button.vertical.space";
        int verticalSpace = propUtils.getPropertyIntegerValue(prop, key);
        if (verticalSpace == 0) {
            verticalSpace = 10;
        }

        // button alignment bottom
        key = "cineloop.panel.alignment.bottom";
        int btnBottomAlign = propUtils.getPropertyIntegerValue(prop, key);
        if (btnBottomAlign == 0) {
            btnBottomAlign = 5;
        }

        stopButton = new JButton("Stop");
        stopButton.setFocusable(false);
        stopButton.setSelected(false);
        stopButton.setEnabled(false);
        stopButton.setMargin(new Insets(0, 0, 0, 0));
        stopButton.setFont(Font.decode(btnFont));
        //btn.setActionCommand(btnStopActionCommand);

        if (btnColorPropertyStatus) {
            stopButton.setBackground(new Color(btnColor[0], btnColor[1], btnColor[2]));
        }
        stopButton.setForeground(new Color(btnTextColor[0], btnTextColor[1], btnTextColor[2]));
        if (btnBorderColorPropertyStatus) {
            stopButton.setBorder(border);
        }
        stopButton.setPreferredSize(new Dimension(btnSize[0], btnSize[1]));

        stopButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(viewDex.vgStudyNextCaseExtendedControl != null)
                    viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
                mainPanel.requestFocusInWindow();
                stopButtonActionPerformed(evt);
            }
        });

        // JPanel
        panel = new JPanel();

        // size
        //double size[][] = {{f,p,f},{8,p,8}};
        double[][] size = new double[2][3];
        size[0][0] = f;
        size[0][1] = p;
        size[0][2] = f;
        size[1][0] = (double) verticalSpace;
        size[1][1] = p;
        size[1][2] = (double) btnBottomAlign;

        panel.setLayout(new TableLayout(size));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));

        panel.add(stopButton, "1,1");
        return panel;
    }


    // **************************************************
    //
    //   Actions
    //
    // **************************************************
    /**
     * CineLoop buttons actions.
     * @param event
     */
    private void cineloopBtnActionPerformed(ActionEvent event) {
        final boolean runStatus = false;
        boolean action = true;
        final ActionEvent e = event;
        final JButton btn = (JButton) e.getSource();
        
        int runModeStatus = viewDex.appMainAdmin.vgControl.getRunModeStatus();
        
        if(runModeStatus == VgRunMode.CREATE_EXIST ||
                runModeStatus == VgRunMode.DEMO_EXIST ||
                runModeStatus == VgRunMode.SHOW_EXIST)
            action = viewDex.appMainAdmin.vgControl.studyDbUtility.isImageStackLoaded();
        else
            if(runModeStatus == VgRunMode.EDIT_EXIST)
                action = true;
        
        // Do not start the loop if a mark with status Active have been set.
        int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();
        Point2D activePoint = viewDex.localization.getLocalizationActivePoint();
        boolean taskPanelActivePointStatus = viewDex.localization.getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);
        if (activePoint != null && taskPanelActivePointStatus) {
            viewDex.localization.setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
        } else {
            if (activePoint != null && !taskPanelActivePointStatus) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }
        
        //if(action){
          if(true){
            new Thread(new Runnable() {
                public void run() {
                    String command = e.getActionCommand();
                    int val = getCineLoopValue(command);
                    selLoopValueBtn = getBtnItemCnt(command);

                    if (!btn.isSelected()) {
                        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
                        viewDex.appMainAdmin.vgControl.setCineLoopTimeValue(val);
                        viewDex.appMainAdmin.vgControl.runStudyAsCineLoop(); 
                    } else {
                        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
                    }

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (btnColorPropertyStatus && btnSelColorPropertyStatus) {
                                for (int i = 0; i < btnList.size(); i++) {
                                    btnList.get(i).setBackground(new Color(btnColor[0],
                                            btnColor[1], btnColor[2]));
                                //btnList.get(i).setContentAreaFilled(true);
                                //btnList.get(i).setSelected(false);
                                }
                                btn.setBackground(new Color(btnSelColor[0],
                                        btnSelColor[1], btnSelColor[2]));
                            }
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * Get the cineLoop value in milliseconds.
     * @param str the buttom property name
     * @return the value
     */
    private int getCineLoopValue(String str) {
        ArrayList<VgCineLoopPanelControl> list;
        int val = 20;

        list = history.getCineLoopPanelControlList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getButtonPropIntervalName().equalsIgnoreCase(str)) {
                val = list.get(i).getButtonIntervalValue();
            }
        }
        return val;
    }
    
    /**
     * Get the lowest cineLoop value in milliseconds.
     * @return the value
     */
    private int getCineLoopLowestFrameIntervalValue() {
        ArrayList<VgCineLoopPanelControl> list;
        int val_r = 0;

        list = history.getCineLoopPanelControlList();
        for (int i = 0; i < list.size(); i++) {
            int val = list.get(i).getButtonIntervalValue();
            if(val_r == 0)
                val_r = val;
            else
                if(val_r > val)
                    val_r = val;
        }
        return val_r;
    }

    /**
     * Get the button item cnt.
     * @param str the buttom property name
     * @return the item cnt.
     */
    private int getBtnItemCnt(String str) {
        ArrayList<VgCineLoopPanelControl> list;
        int val = -1;

        list = history.getCineLoopPanelControlList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getButtonPropIntervalName().equalsIgnoreCase(str)) {
                val = i;
                break;
            }
        }
        return val;
    }

    /**
     * 
     * @param event
     */
    private void stopButtonActionPerformed(ActionEvent e) {
        /*
        for (int i = 0; i < btnList.size(); i++) {
        btnList.get(i).setBackground(new Color(btnColor[0],
        btnColor[1], btnColor[2]));
        }
         * */
        //viewDex.appMainAdmin.vgControl.stopLoadStackInBackground();
        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        viewDex.appMainAdmin.viewDex.eyeTracking.sendUDPMessage("ET_STP");
    }

    /**
     * Set the default action.
     * NOT IN USE
     */
    public void setCineLoopDefaultAction2() {
        setButtonEnabled(true);

        if (btnList.size() != 0) {
            if (selLoopValueBtn == -1) {
                int s = btnList.size();

                if (s == 1) {
                    selLoopValueBtn = 0;
                } else {
                    selLoopValueBtn = 1;
                }
            }
            btnList.get(selLoopValueBtn).doClick(50);
        }
    }
    
    /**
     * Read the lowest configured frameinterval value and start the cineloop. 
     */
    public void setCineLoopStartAction(){
        int val = getCineLoopLowestFrameIntervalValue();
        
        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        viewDex.appMainAdmin.vgControl.setCineLoopTimeValue(val);
        viewDex.appMainAdmin.vgControl.runStudyAsCineLoop();
    }

    /**
     * Update the selected button selet status.
     */
    public void setLoopValueButtonSelected() {
        if (btnList.size() != 0) {
            if (selLoopValueBtn == -1) {
                int s = btnList.size();

                if (s == 1) {
                    selLoopValueBtn = 0;
                } else {
                    selLoopValueBtn = 1;
                }
            }
            if (btnColorPropertyStatus) {
                for (int i = 0; i < btnList.size(); i++) {
                    btnList.get(i).setBackground(new Color(btnColor[0],
                            btnColor[1], btnColor[2]));
                }
                btnList.get(selLoopValueBtn).setBackground(new Color(btnSelColor[0],
                    btnSelColor[1], btnSelColor[2]));
            }
        }
    }

    /**
     * Set the loop-value and stop buttons enabled.
     */
    public void setButtonEnabled(boolean sta) {
        ArrayList<VgCineLoopPanelControl> list = history.getCineLoopPanelControlList();

        for (int i = 0; i < list.size(); i++) {
            btnList.get(i).setEnabled(sta);
        }
        stopButton.setEnabled(sta);
    }
}

