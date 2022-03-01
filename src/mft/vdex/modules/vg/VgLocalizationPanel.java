/* @(#) VgFunctionLocalizationPanel.java 06/09/2005
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
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Properties;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import mft.vdex.app.ViewDex;
import mft.vdex.controls.*;
import mft.vdex.app.AppPropertyUtils;

public class VgLocalizationPanel extends JPanel implements KeyListener {

    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private VgHistory history;
    private JPanel mainPanel;
    private JButton eraseButton;
    public JButton showHideButton;
    private Vector imgLst = new Vector();
    private Thread eraseLoaderThread;
    private Thread showHideLoaderThread;
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    private boolean showHideLocalizationButtonStatus = true;

    public VgLocalizationPanel(ViewDex viewdex, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.history = vghistory;
        propUtils = new AppPropertyUtils();
        createUI();
    }

    protected void createUI() {
        createLayout();
        mainPanel = createMainPanel();

        this.add(mainPanel, "1,1");
    }

    private void createLayout() {
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "localization.panel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment left
        key = "localization.panel.alignment.left";
        int alignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0)
            alignLeft = 5;
        
        // alignment right
        key = "localization.panel.alignment.right";
        int alignRight = propUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0)
            alignRight = 5;

        double[][] size = {{alignLeft, f, alignRight}, {3, f, 8}};
        this.setLayout(new TableLayout(size));
        this.setBackground(new Color(color[0], color[1], color[2]));
    }

    /**
     * 
     * @return
     */
    private JPanel createMainPanel() {
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "localization.panel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // border color
        key = "localization.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0], borderColor[1], borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Localization");


        // title font
        String defTitleFont = "SansSerif-plain-16";
        key = "localization.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if (titleFont.equals("")) {
            titleFont = defTitleFont;
        }
        // title color
        key = "localization.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if (titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0) {
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }

        // button color
        boolean buttonColorPropertyStatus = true;
        key = "localization.button.color";
        int[] buttonColor = propUtils.getPropertyColorValue(prop, key);
        if (buttonColor[0] == 0 && buttonColor[1] == 0 && buttonColor[2] == 0) {
            buttonColorPropertyStatus = false;
        }

        // button text color
        key = "localization.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if (buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0) {
            buttonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }

        // button font
        String defButtonFont = "Arial-plain-16";
        key = "localization.button.font";
        String buttonFont = propUtils.getPropertyFontValue(prop, key);
        if (buttonFont.equals("")) {
            buttonFont = defButtonFont;
        }
        
        // button size
        int[] defButtonSize = {123, 32};
        key = "localization.button.size";
        int[] buttonSize = propUtils.getPropertySizeValue(prop, key);
        if (buttonSize[0] == 0 && buttonSize[1] == 0) {
            buttonSize[0] = defButtonSize[0];
            buttonSize[1] = defButtonSize[1];
        }

        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "localization.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if (buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0) {
            buttonBorderColorPropertyStatus = false;
        }
        Border buttonBorder = BorderFactory.createLineBorder(new Color(10, 10, 10));
        if (buttonBorderColorPropertyStatus) {
            buttonBorder = BorderFactory.createLineBorder(new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // alignment top
        key = "localization.panel.alignment.top";
        int alignTop = propUtils.getPropertyIntegerValue(prop, key);
        if (alignTop == 0)
            alignTop = 0;
        
        // alignment bottom
        key = "localization.panel.alignment.bottom";
        int alignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if (alignBottom == 0)
            alignBottom = 10;

        // button space
        key = "localization.button.space.horizontal";
        int btnSpaceHorizaontal = propUtils.getPropertyIntegerValue(prop, key);
        if (btnSpaceHorizaontal == 0)
            btnSpaceHorizaontal = 20;
         
        // panel
        JPanel studyLocalizationPanel = new JPanel();
        studyLocalizationPanel.setBackground(new Color(color[0], color[1], color[2]));
        //double size[][] = {{f,p,f},{f,p,8}};
        double[][] size = {{f, p, btnSpaceHorizaontal, p, f}, {alignTop, p, alignBottom}};
        studyLocalizationPanel.setLayout(new TableLayout(size));

        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0], titleColor[1], titleColor[2]));
        studyLocalizationPanel.setBorder(tborder);

        // Erase button
        eraseButton = new JButton("Erase all");
        eraseButton.setFont(Font.decode(buttonFont));
        /*
        eraseButton.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(java.awt.event.ActionEvent evt){
        eraseButtonActionPerformed(evt);
        }
        });
         */
        eraseButton.addActionListener(new EraseButtonAction());

        eraseButton.setFocusable(false);
        eraseButton.setEnabled(false);
        //eraseButton.addFocusListener(this);
        //eraseButton.addKeyListener(this);
        if (buttonColorPropertyStatus) {
            eraseButton.setBackground(new Color(buttonColor[0], buttonColor[1], buttonColor[2]));
        }
        eraseButton.setForeground(new Color(buttonTextColor[0], buttonTextColor[1], buttonTextColor[2]));
        eraseButton.setPreferredSize(new Dimension(buttonSize[0], buttonSize[1]));

        if (buttonBorderColorPropertyStatus) {
            eraseButton.setBorder(buttonBorder);
        }
        
        // Show/Hide toggle button
        showHideButton = new JButton("Show/Hide");
        showHideButton.setFont(Font.decode(buttonFont));
        showHideButton.addActionListener(new ShowHideButtonAction());

        showHideButton.setFocusable(false);
        showHideButton.setEnabled(false);
        //showHideButton.addFocusListener(this);
        //Button.addKeyListener(this);
        if (buttonColorPropertyStatus) {
            showHideButton.setBackground(new Color(buttonColor[0], buttonColor[1], buttonColor[2]));
        }

        showHideButton.setForeground(new Color(buttonTextColor[0], buttonTextColor[1], buttonTextColor[2]));

        showHideButton.setPreferredSize(new Dimension(buttonSize[0], buttonSize[1]));

        if (buttonBorderColorPropertyStatus) {
            showHideButton.setBorder(buttonBorder);
        }
        // add
        studyLocalizationPanel.add(showHideButton, "1,1");
        studyLocalizationPanel.add(eraseButton, "3,1");

        return studyLocalizationPanel;
    }
    // **************************************************
    //
    //   Actions
    //
    // **************************************************

/**
     * ActionListener for the "Erase all" button.
     */
    class EraseButtonAction extends AbstractAction implements Runnable {

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if(viewDex.vgStudyNextCaseExtendedControl != null)
                viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
            viewDex.requestFocusInWindow();
            
            if (eraseLoaderThread != null) {
                return;
            }
            eraseLoaderThread = new Thread((Runnable) this);
            eraseLoaderThread.start();
        }

        public void run() {
            eraseButtonActionPerformed();
            //viewDex.localization.deleteSelecetedLocalizationAndRender();
            eraseLoaderThread = null;
        }
    }

    /**
     */
    protected void eraseButtonActionPerformed() {
        // Reset overlay
        //viewDex.canvas.setCanvasOverlayLocalizationPositionValue(0,0,0);
        //viewDex.canvas.setCanvasOverlayLocalizationStatus(false);
        
        //viewDex.canvas.setCanvasROIDistanceDrawingStatus(false);
        //viewDex.canvas.setCanvasROIDistanceDrawingValue(0,0,0,0);
        //viewDex.canvas.setCanvasROIDistanceUpdateStatus(false);
        //viewDex.canvas.setCanvasROIDistanceUpdateValue(null);
        
        //viewDex.appMainAdmin.vgControl.eraseAllMarkInStack();
        //setShowHideText();
        //showHideLocalizationButtonStatus = true;
        //viewDex.appMainAdmin.vgControl.setCanvasOverlayStackInfo();

        viewDex.localization.eraseLocalizationButtonAction();
    }

    /**
     * ActionListener for the "Show/Hide" button
     */
    class ShowHideButtonAction extends AbstractAction implements Runnable {

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if(viewDex.vgStudyNextCaseExtendedControl != null)
                viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
            viewDex.requestFocusInWindow();
            
            if (showHideLoaderThread != null) {
                return;
            }
            showHideLoaderThread = new Thread((Runnable) this);
            showHideLoaderThread.start();
        }

        public void run() {
            showHideButtonActionPerformed();
            showHideLoaderThread = null;
        }
    }

    /**
     * If showHideLocalisationButtonStatus == true -> symbols are displayed
     *     -> button display the text Hide.
     * If showHideLocalisationButtonStatus == false -> symbols are hidden
     *     -> button display the text Show
     * 
     */
    protected void showHideButtonActionPerformed() {
        String str = showHideButton.getText();
        
        if(str.equals("Show")){
            showHideLocalizationButtonStatus = true;
            showHideButton.setText("Hide");
            //viewDex.appMainAdmin.vgControl.showLocalizationButtonAction();
            viewDex.localization.showLocalizationButtonAction();
        }
            
        if(str.equals("Hide")){
            showHideLocalizationButtonStatus = false;
            showHideButton.setText("Show");
            //viewDex.appMainAdmin.vgControl.hideLocalizationButtonAction();
            viewDex.localization.hideLocalizationButtonAction();
        }
    }
    
    /*
     * Set the "Show" text on the button.
     * NOT IN USE
     */
    public void setShowText(){
        showHideButton.setText("Show");
    }
    
    /*
     * Set the "Hide" text on the button.
     */
    public void setHideText(){
        showHideButton.setText("Hide");
        showHideLocalizationButtonStatus = true;
    }
    
    /*
     * Set the "Show/Hide" text on the button.
     */
    public void setShowHideText(){
        showHideButton.setText("Show/Hide");
        showHideLocalizationButtonStatus = true;
    }
    
    /*
     */
    public boolean getLocalizationPanelStatus(){
        return showHideLocalizationButtonStatus;
    }
    
    /**
     * 
     * @param status
     */
    public void setLocalizationEraseButtonEnableStatus(boolean status){
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
            eraseButton.setEnabled(status);
    }
    
    /**
     * 
     * @param status
     */
    public void setLocalizationShowHideButtonEnableStatus(boolean status){
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST ||
                viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST)
            showHideButton.setEnabled(status);
    }
    
    /**
     * 
     * @param status
     * NOT IN USE
     */
    public void setLocalizationButtonInit(){
        setShowHideText();
        showHideButton.setEnabled(false);
        eraseButton.setEnabled(false);
        showHideLocalizationButtonStatus = true;
    }
    
    /*******************************************************
     * KeyListener interface
     ******************************************************/
    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("VgFunctionLocalizationPanel.keyPressed");
    }
}