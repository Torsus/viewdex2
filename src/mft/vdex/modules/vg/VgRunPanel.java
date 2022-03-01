/* @(#) VgRunPanel.java 06/09/2005
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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import mft.vdex.app.ViewDex;
import mft.vdex.app.AppPropertyUtils;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbStackNode;

/**
 *
 * @author Sune Svensson
 */
public class VgRunPanel extends JPanel{
    private ViewDex viewDex;
    private VgHistory history;
    private JPanel infoPanel, imageInfoPanel, stackInfoPanel;
    private JPanel imageInfoBackgroundReadingPanel;
    protected JLabel imageTotalCntLabel, imageSelCntLabel;
    protected JLabel stackTotalCntLabel, stackSelCntLabel;
    protected JLabel imageBackgroundReadingCntLabel;
    
    private JPanel buttonPanel;
    private AppPropertyUtils propUtils;
    private String studyName;
    private Properties prop;
    
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    
    public VgRunPanel(ViewDex viewdex, VgHistory history) {
        this.viewDex = viewdex;
        this.history = history;
        init(); 
    }
    
    /**
     */
    private void init(){
        propUtils = new AppPropertyUtils();
        studyName = history.getStudyName();
        prop = history.getVgProperties();
        
        createUI();
    }
    
    protected void createUI(){
        createLayout();
        JPanel mainPanel = createMainPanel();
        buttonPanel = createButtonPanel();
        infoPanel = createInfoPanel();
        stackInfoPanel= createStackInfoPanel();
        imageInfoPanel= createImageInfoPanel();
        imageInfoBackgroundReadingPanel = createImageInfoBackgroundReadingPanel();
        
        mainPanel.add(buttonPanel, "1,0");
        mainPanel.add(infoPanel, "0,0");
        infoPanel.add(stackInfoPanel, "1,0");
        infoPanel.add(imageInfoPanel, "3,0");
        infoPanel.add(imageInfoBackgroundReadingPanel, "5,0");
        
        this.add(mainPanel, "1,0");
    }
    
    /**
     */
    private void createLayout(){
        // panel color
        String key = "runpanel.panel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment left
        key = "runpanel.panel.alignment.left";
        int alignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0)
            alignLeft = 5;
        
        // alignment right
        key = "runpanel.panel.alignment.right";
        int alignRight = propUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0)
            alignRight = 5;
        
        double size[][] = {{alignLeft,f,alignRight},{f}};
        this.setLayout(new TableLayout(size));
        this.setBackground(new Color(color[0], color[1], color[2]));
        //this.setBackground(Color.GREEN);
    }
    
    /**
     */
    private JPanel createMainPanel() {
        // color
        String key = "runpanel.panel.color";
        int[] panelColor  = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 &&
                panelColor[1] == 0 &&
                panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
         // border color
        key = "runpanel.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if(borderColor[0] == 0 && borderColor[1] == 0 &&
                borderColor[2] == 0){
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }
        
        // border
        Border tborder = BorderFactory.createLineBorder(new Color(
                borderColor[0], borderColor[1], borderColor[2]));
        
        // panel
        double[][] size = {{f,p}, {f}};
        JPanel panel = new JPanel();
        this.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //this.setBackground(new Color(110,20,20));
        panel.setLayout(new TableLayout(size));
        panel.setBorder(tborder);

        return panel;
    }
    
    /**
     * 
     */
    private JPanel createInfoPanel(){
        // panel color
        String key = "runpanel.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment right
        key = "runpanel.stackinfo.alignment.right";
        int stackLblAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if(stackLblAlignRight == 0)
            stackLblAlignRight = 1;
        
        // alignment right
        key = "runpanel.imageinfo.alignment.right";
        int imageLblAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if(imageLblAlignRight == 0)
            imageLblAlignRight = 1;
        
        // alignment right
        key = "runpanel.imageinfo.backgroundreading.alignment.right";
        int backgroundReadingLblAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if(backgroundReadingLblAlignRight == 0)
            backgroundReadingLblAlignRight = 1;
        
        // size horizontal
        key = "runpanel.stackinfo.size.horizontal";
        int stackPanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (stackPanelHorizontalSize == 0)
            stackPanelHorizontalSize = 20;
        
         // size horizontal
        key = "runpanel.imageinfo.size.horizontal";
        int imagePanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imagePanelHorizontalSize == 0)
            imagePanelHorizontalSize = 10;
        
        // size horizontal
        key = "runpanel.imageinfo.backgroundreading.size.horizontal";
        int imageBackgroundReadingPanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imageBackgroundReadingPanelHorizontalSize == 0)
            imageBackgroundReadingPanelHorizontalSize = 10;
        
        double size[][] = {{f,stackPanelHorizontalSize,stackLblAlignRight,
                imagePanelHorizontalSize,imageLblAlignRight,
                imageBackgroundReadingPanelHorizontalSize,backgroundReadingLblAlignRight},{f}};
        JPanel panel = new JPanel();
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //panel.setBackground(new Color(0,200,0));
        panel.setLayout(new TableLayout(size));
        
        return panel;
    }
    
    /**
     * Create the button panel.
     * @return
     */
    private JPanel createButtonPanel(){
        // panel color
        String key = "runpanel.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // button color
        boolean buttonColorPropertyStatus = true;
        key = "runpanel.button.color";
        int[] buttonColor= propUtils.getPropertyColorValue(prop, key);
        if(buttonColor[0] == 0 &&
                buttonColor[1] == 0 &&
                buttonColor[2] == 0)
            buttonColorPropertyStatus = false;
      
        // button text color
        key = "runpanel.button.text.color";
        int[] buttonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonTextColor[0] == 0 && buttonTextColor[1] == 0 && buttonTextColor[2] == 0){
            buttonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            buttonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            buttonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }
        
        // button border color
        boolean buttonBorderColorPropertyStatus = true;
        key = "runpanel.button.border.color";
        int[] buttonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(buttonBorderColor[0] == 0 && buttonBorderColor[1] == 0 && buttonBorderColor[2] == 0)
            buttonBorderColorPropertyStatus = false;
        
        // default
        Border border = BorderFactory.createLineBorder(new Color(10,10,10));
        if(buttonBorderColorPropertyStatus){
            border = BorderFactory.createLineBorder(
                    new Color(buttonBorderColor[0], buttonBorderColor[1], buttonBorderColor[2]));
        }
        
        // button font
        String defRunPanelButtonFont = "Arial-plain-16";
        key = "runpanel.button.font";
        String buttonFont = propUtils.getPropertyFontValue(prop, key);
        if(buttonFont.equals(""))
            buttonFont = defRunPanelButtonFont;
        
        // button size
        int[] defRunPanelButtonSize = {125, 35};
        key = "runpanel.button.size";
        int[] buttonSize = propUtils.getPropertySizeValue(prop, key);
        if(buttonSize[0] == 0 && buttonSize[1] == 0){
            buttonSize[0] = defRunPanelButtonSize[0];
            buttonSize[1] = defRunPanelButtonSize[1];
        }
        
        // alignment
        key = "runpanel.button.alignment.right";
        int btnAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if(btnAlignRight == 0)
            btnAlignRight = 1;
        
        // alignment top
        key = "runpanel.button.alignment.top";
        int topAlign = propUtils.getPropertyIntegerValue(prop, key);
        if (topAlign == 0)
            topAlign = 10;
        
        // alignment bottom
        key = "runpanel.button.alignment.bottom";
        int bottomAlign = propUtils.getPropertyIntegerValue(prop, key);
        if (bottomAlign == 0)
            bottomAlign = 10;
        
        // panel
        double size[][] = {{f,buttonSize[0],btnAlignRight},{topAlign,buttonSize[1],bottomAlign}};
        JPanel panel = new JPanel();
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //panel.setBackground(new Color(200,200,0));
        panel.setLayout(new TableLayout(size));

        // stop button
        JButton stopButton = new JButton("Stop");
        if(buttonColorPropertyStatus)
            stopButton.setBackground(new Color(buttonColor[0], buttonColor[1], buttonColor[2]));
        stopButton.setForeground(new Color(buttonTextColor[0], buttonTextColor[1], buttonTextColor[0]));
        if(buttonBorderColorPropertyStatus)
            stopButton.setBorder(border);
        stopButton.setFont(Font.decode(buttonFont));
        //stopButton.setPreferredSize(new Dimension(buttonSize[0], buttonSize[1]));
        stopButton.setFocusable(false);

        // add
        panel.add(stopButton, "1,1");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed();
            }
        });
        
        return panel;
    }
    
    /**
     */
    private JPanel createStackInfoPanel(){
        // panel color
        String key = "runpanel.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // text color
        key = "runpanel.label.text.color";
        int[] textColor = propUtils.getPropertyColorValue(prop, key);
        if(textColor[0] == 0 && textColor[1] == 0 && textColor[2] == 0){
            textColor[0] = AppPropertyUtils.defTextColor[0];
            textColor[1] = AppPropertyUtils.defTextColor[1];
            textColor[2] = AppPropertyUtils.defTextColor[2];
        }
        
        // text font
        String defStackInfoTextFont = "Arial-plain-20";
        key = "runpanel.label.text.font";
        String textFont = propUtils.getPropertyFontValue(prop, key);
        if(textFont.equals(""))
            textFont = defStackInfoTextFont;
        
        // gap horizontal
        key = "runpanel.stackinfo.gap.horizontal";
        int stackGapHorizontal = propUtils.getPropertyIntegerValue(prop, key);
        if (stackGapHorizontal == 0)
            stackGapHorizontal = 1;
        
        // size horizontal
        /*
        key = "runpanel.stackinfo.size.horizontal";
        int stackPanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (stackPanelHorizontalSize == 0)
            stackPanelHorizontalSize = 20;
        */
        
        // size vertical
        /*
        key = "runpanel.stackinfo.size.vertical";
        int stackPanelVerticalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (stackPanelVerticalSize == 0)
            stackPanelVerticalSize = 5;
        */
        
        // panel
        JPanel panel = new JPanel();
        //panel.setBackground(new Color(200,0,0));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        panel.setLayout(new GridLayout(1,2,stackGapHorizontal,0));
        
        // label
        stackSelCntLabel = new JLabel();
        //stackSelCntLabel.setOpaque(true);
        //stackSelCntLabel.setBackground(new Color(0,200,0));
        stackSelCntLabel.setForeground(new Color(textColor[0], textColor[1], textColor[2]));
        stackSelCntLabel.setFont(Font.decode(textFont));
        stackSelCntLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        //stackSelCntLabel.setPreferredSize(new Dimension(stackPanelHorizontalSize,stackPanelVerticalSize));
        
        // label
        stackTotalCntLabel = new JLabel();
        stackTotalCntLabel.setForeground(new Color(textColor[0], textColor[1], textColor[2]));
        stackTotalCntLabel.setFont(Font.decode(textFont));
        stackTotalCntLabel.setHorizontalAlignment(JLabel.LEFT);
        
        panel.add(stackSelCntLabel);
        panel.add(stackTotalCntLabel);
        
        return panel;
    }
    
    /**
     */
    private JPanel createImageInfoPanel(){
        // panel color
        String key = "runpanel.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // text color
        key = "runpanel.label.text.color";
        int[] textColor = propUtils.getPropertyColorValue(prop, key);
        if(textColor[0] == 0 && textColor[1] == 0 && textColor[2] == 0){
            textColor[0] = AppPropertyUtils.defTextColor[0];
            textColor[1] = AppPropertyUtils.defTextColor[1];
            textColor[2] = AppPropertyUtils.defTextColor[2];
        }
        
        // text font
        String defImageInfoTextFont = "Arial-plain-20";
        key = "runpanel.label.text.font";
        String textFont = propUtils.getPropertyFontValue(prop, key);
        if(textFont.equals(""))
            textFont = defImageInfoTextFont;
        
        // gap horizontal
        key = "runpanel.imageinfo.gap.horizontal";
        int imageGapHorizontal = propUtils.getPropertyIntegerValue(prop, key);
        if (imageGapHorizontal == 0)
            imageGapHorizontal = 1;
        
        // size horizontal
        /*
        key = "runpanel.imageinfo.size.horizontal";
        int imagePanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imagePanelHorizontalSize == 0)
            imagePanelHorizontalSize = 10;
        */
        
        // size vertical
        /*
        key = "runpanel.imageinfo.size.vertical";
        int imagePanelVerticalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imagePanelVerticalSize == 0)
            imagePanelVerticalSize = 20;
        */
        
        // panel
        JPanel panel = new JPanel();
        //panel.setBackground(new Color(220,220,220));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        panel.setLayout(new GridLayout(1,2,imageGapHorizontal,0));
        
        // label1
        imageSelCntLabel = new JLabel();
        //imageSelCntLabel.setBackground(new Color(160,160,160));
        imageSelCntLabel.setForeground(new Color(textColor[0], textColor[1], textColor[2]));
        imageSelCntLabel.setFont(Font.decode(textFont));
        imageSelCntLabel.setHorizontalAlignment(JLabel.RIGHT);
        //imageSelCntLabel.setPreferredSize(new Dimension(imagePanelHorizontalSize, imagePanelVerticalSize));
        
        // label2
        imageTotalCntLabel = new JLabel();
        //imageTotalCntLabel.setBackground(new Color(210,210,210));
        imageTotalCntLabel.setForeground(new Color(textColor[0], textColor[1], textColor[2]));
        imageTotalCntLabel.setFont(Font.decode(textFont));
        imageTotalCntLabel.setHorizontalAlignment(JLabel.LEFT);
        
        panel.add(imageSelCntLabel);
        panel.add(imageTotalCntLabel);
        
        return panel;
    }
    
    /**
     */
    private JPanel createImageInfoBackgroundReadingPanel(){
        // panel color
        String key = "runpanel.panel.color";
        int[] panelColor = propUtils.getPropertyColorValue(prop, key);
        if(panelColor[0] == 0 && panelColor[1] == 0 && panelColor[2] == 0){
            panelColor[0] = AppPropertyUtils.defPanelColor[0];
            panelColor[1] = AppPropertyUtils.defPanelColor[1];
            panelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // text color
        key = "runpanel.label.text.color";
        int[] textColor = propUtils.getPropertyColorValue(prop, key);
        if(textColor[0] == 0 && textColor[1] == 0 && textColor[2] == 0){
            textColor[0] = AppPropertyUtils.defTextColor[0];
            textColor[1] = AppPropertyUtils.defTextColor[1];
            textColor[2] = AppPropertyUtils.defTextColor[2];
        }
        
        // text font
        String defImageInfoTextFont = "Arial-plain-20";
        key = "runpanel.label.text.font";
        String textFont = propUtils.getPropertyFontValue(prop, key);
        if(textFont.equals(""))
            textFont = defImageInfoTextFont;
        
        // size horizontal
        key = "runpanel.imageinfo.backgroungreading.size.horizontal";
        int imagePanelHorizontalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imagePanelHorizontalSize == 0)
            imagePanelHorizontalSize = 10;
        
        // size vertical
        key = "runpanel.imageinfo.backgroungreading.size.vertical";
        int imagePanelVerticalSize = propUtils.getPropertyIntegerValue(prop, key);
        if (imagePanelVerticalSize == 0)
            imagePanelVerticalSize = 20;
        
        // panel
        JPanel panel = new JPanel();
        double[][] size = {{f}, {f}};
        //panel.setBackground(new Color(0,220,0));
        panel.setBackground(new Color(panelColor[0], panelColor[1], panelColor[2]));
        //panel.setLayout(new GridLayout(1,1,0,0));
        panel.setLayout(new TableLayout(size));
        
        // label1
        imageBackgroundReadingCntLabel = new JLabel();
        //imageSelCntLabel.setBackground(new Color(160,160,160));
        imageBackgroundReadingCntLabel.setForeground(new Color(textColor[0], textColor[1], textColor[2]));
        imageBackgroundReadingCntLabel.setFont(Font.decode(textFont));
        imageBackgroundReadingCntLabel.setHorizontalAlignment(JLabel.CENTER);
        //imageBackgroundReadingCntLabel.setPreferredSize(new Dimension(imagePanelHorizontalSize, imagePanelVerticalSize));
        
        panel.add(imageBackgroundReadingCntLabel, "0,0");
        
        return panel;
    }
    
    /**
     * Run the login method.
     */
    private void stopButtonActionPerformed(){
        String str = viewDex.appProperty.getStudyName();
        
        // Cursor
        viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        viewDex.setAppTitle("      Please wait...  Saving history");
        
        int historyOption = viewDex.appMainAdmin.vgControl.getHistoryOptionStatus();
        
        /*
        if(historyOption == VgHistoryOptionType.HISTORY_OPTION_ORIGINAL){
            viewDex.appMainAdmin.vgControl.writeOriginalHistory();
            viewDex.appMainAdmin.vgControl.writeOriginalHistoryBackup();
        }else
            if(historyOption == VgHistoryOptionType.HISTORY_OPTION_EDITED){
                viewDex.appMainAdmin.vgControl.writeEditHistory();
                viewDex.appMainAdmin.vgControl.writeEditHistoryBackup();
        }*/

        // notes
        if(viewDex.vgNotesPanel != null)
            viewDex.vgNotesPanel.saveNotesPanel();

        // delete overlays
        viewDex.distanceMeasurement.deleteROIDistanceListAndNoRender();
        viewDex.areaMeasurement.deleteROIAreaListAndNoRender();
        viewDex.pixelValueMeanMeasurement.deleteROIPixelValueListAndNoRender();
        //viewDex.localization.deleteLocalizationList();
        
        int runModeStatus = viewDex.appMainAdmin.vgControl.getRunModeStatus();
        if (runModeStatus == VgRunMode.CREATE_EXIST){
            viewDex.vgHistoryMainUtil.writeHistory(viewDex.vgHistory);
            viewDex.vgHistoryMainUtil.writeHistoryBackup(viewDex.vgHistory);
        }else{
            if(runModeStatus == VgRunMode.EDIT_EXIST){
                viewDex.vgHistoryEditUtil.writeEditHistory(viewDex.vgHistory);
                viewDex.vgHistoryEditUtil.writeEditHistoryBackup(viewDex.vgHistory);
            }
            else{
                if (runModeStatus == VgRunMode.DEMO_EXIST){
                    viewDex.vgHistoryDemoUtil.writeDemoHistory(viewDex.vgHistory);
                    viewDex.vgHistoryDemoUtil.writeDemoHistoryBackup(viewDex.vgHistory);
                }
            }
        }

        viewDex.appMainAdmin.vgControl.stopLoadStackInBackground();
        viewDex.appMainAdmin.vgControl.stopStudyAsCineLoop();
        //System.out.println("VgControl.stopButtonActionPerformed:1");
        
        // Cursor
        viewDex.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
        boolean sta = viewDex.appMainAdmin.runLogin();
    }
    
    /*
     * Set the number of the total numbers of stacks.
     */
    public void setTotalStackCount(String str){
        //stackTotalCntLabel.setText("(1233)");
        stackTotalCntLabel.setText(str);
    }
    
     /*
     * Set the number of the selected stack.
     */
    public void setSelStackCount(String str){
        //stackSelCntLabel.setText("3444");
        stackSelCntLabel.setText(str);
    }
    
    /*
     * Set the selected image number count.
     */
    public void setSelImageCount(String str){
        //imageSelCntLabel.setText("2367");
        imageSelCntLabel.setText(str);
    }
    
    /*
     * Set the total number of images in the root/stack.
     */
    public void setTotalImageCount(String str){
        //imageTotalCntLabel.setText("(1003)");
        imageTotalCntLabel.setText(str);
    }
    
    /*
     * Set..
     */
    public void setTotalImageBackgroundReadingCount(String str){
        //imageBackgroundReadingCntLabel.setText("(1004)");
        imageBackgroundReadingCntLabel.setText(str);
    }
}
