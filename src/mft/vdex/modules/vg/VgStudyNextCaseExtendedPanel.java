/* @(#) VgImageSelectControlPanel.java 06/09/2005
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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import mft.vdex.app.ViewDex;
import mft.vdex.controls.*;
import mft.vdex.ds.StudyDbLocalizationStatus;
import mft.vdex.util.NumericTextField;
import mft.vdex.app.AppPropertyUtils;

public class VgStudyNextCaseExtendedPanel extends JPanel implements FocusListener, KeyListener, ActionListener{
    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private VgHistory history;
    private JPanel mainPanel;
    private JButton prevButton;
    private Vector imgLst = new Vector();
    private Thread loaderThread;
    
    //private JFormattedTextField textInput;
    //private JTextField textInput;
    public NumericTextField textInput;
    private JLabel gotoLabel;
    
    public VgStudyNextCaseExtendedPanel(ViewDex viewdex, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.history = vghistory;
        propUtils = new AppPropertyUtils();
        createUI();
    }
    
    protected void createUI(){
        createLayout();
        mainPanel = createMainPanel();
        
        this.add(mainPanel, "1,1");
    }
    
    private void createLayout(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();
        
        // panel color
        String key = "studyselectextendedpanel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if(color[0] == 0 && color[1] == 0 && color[2] == 0){
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // alignment left
        key = "studyselectextendedpanel.alignment.left";
        int alignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (alignLeft == 0)
            alignLeft = 5;
        
        // alignment right
        key = "studyselectextendedpanel.alignment.right";
        int alignRight = propUtils.getPropertyIntegerValue(prop, key);
        if (alignRight == 0)
            alignRight = 5;
        
        double size[][] = {{alignLeft, f, alignRight}, {3,f,8}};
        this.setLayout(new TableLayout(size));
        this.setBackground(new Color(color[0], color[1], color[2]));
    }
    
    /**
     * createMainPanel
     * @return
     */
    private JPanel createMainPanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();
        
        /*******************************************************
         * panel
         ******************************************************/
         // panel color
        String key = "studyselectextendedpanel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if(color[0] == 0 && color[1] == 0 && color[2] == 0){
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }
        
        // border color
        key = "studyselectextendedpanel.border.color";
        int[] borderColor = propUtils.getPropertyColorValue(prop, key);
        if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
            borderColor[0] = AppPropertyUtils.defTitleLineBorderColor[0];
            borderColor[1] = AppPropertyUtils.defTitleLineBorderColor[1];
            borderColor[2] = AppPropertyUtils.defTitleLineBorderColor[2];
        }

        // border
        Border lborder = BorderFactory.createLineBorder(new Color(borderColor[0],
                borderColor[1], borderColor[2]));
        TitledBorder tborder = BorderFactory.createTitledBorder(lborder, "Study select");
        
        // title font
        String defTitleFont = "SansSerif-plain-16";
        key = "studyselectextendedpanel.title.font";
        String titleFont = propUtils.getPropertyFontValue(prop, key);
        if (titleFont.equals("")) {
            titleFont = defTitleFont;
        }
        
        // title color
        key = "studyselectextendedpanel.title.color";
        int[] titleColor = propUtils.getPropertyColorValue(prop, key);
        if (titleColor[0] == 0 && titleColor[1] == 0 && titleColor[2] == 0) {
            titleColor[0] = AppPropertyUtils.defTitleColor[0];
            titleColor[1] = AppPropertyUtils.defTitleColor[1];
            titleColor[2] = AppPropertyUtils.defTitleColor[2];
        }
        
        // alignment top
        key = "studyselectextendedpanel.alignment.top";
        int alignTop = propUtils.getPropertyIntegerValue(prop, key);
        if (alignTop == 0)
            alignTop = 0;
        
        // alignment bottom
        key = "studyselectextendedpanel.alignment.bottom";
        int alignBottom = propUtils.getPropertyIntegerValue(prop, key);
        if (alignBottom == 0)
            alignBottom = 10;
        
        
        /*************************************************
         * Goto label
         ************************************************/
        // alignment left
        key = "studyselectextendedpanel.goto.label.alignment.left";
        int labelAlignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if (labelAlignLeft == 0)
            labelAlignLeft = 5;
        
        // label font
        String defLabelFont = "SansSerif-plain-16";
        key = "studyselectextendedpanel.goto.label.font";
        String labelFont = propUtils.getPropertyFontValue(prop, key);
        if (labelFont.equals(""))
            labelFont = defLabelFont;
        
        // Label
        gotoLabel = new JLabel("Go to:");
        gotoLabel.setBackground(new Color(255,0,0));
        gotoLabel.setFont(Font.decode(labelFont));
        
        // Set label enable status
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            gotoLabel.setEnabled(false);
        
        
        /****************************************************
         * textInput
         ***************************************************/
        // size
        key = "studyselectextendedpanel.goto.input.size";
        int textInputSize = propUtils.getPropertyIntegerValue(prop, key);
        if(textInputSize == 0)
            textInputSize = 45;
        
        // alignment right
        key = "studyselectextendedpanel.goto.input.alignment.left";
        int textInputAlignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if(textInputAlignLeft == 0)
            textInputAlignLeft = 5;
        
        // font
        String defTextFont = "SansSerif-plain-16";
        key = "studyselectextendedpanel.goto.input.font";
        String textFont = propUtils.getPropertyFontValue(prop, key);
        if (textFont.equals(""))
            textFont = defLabelFont;
        
        // color
        key = "studyselectextendedpanel.goto.input.color";
        int[] col = propUtils.getPropertyColorValue(prop, key);
        if(col[0] == 0 && col[1] == 0 && col[2] == 0){
            col[0] = AppPropertyUtils.defTextInputFieldColor[0];
            col[1] = AppPropertyUtils.defTextInputFieldColor[1];
            col[2] = AppPropertyUtils.defTextInputFieldColor[2];
        }
        
        // color
        key = "studyselectextendedpanel.goto.border.color";
        int[] bCol = propUtils.getPropertyColorValue(prop, key);
        if(bCol[0] == 0 && bCol[1] == 0 && bCol[2] == 0){
            //bCol[0] = PropertiesUtils.defTextInputFieldColor[0];
            //bCol[1] = PropertiesUtils.defTextInputFieldColor[1];
            //bCol[2] = PropertiesUtils.defTextInputFieldColor[2];
            bCol[0] = 120;
            bCol[1] = 120;
            bCol[2] = 120;
        }
        
        // textinput
        //textInput = new JFormattedTextField(createFormatter(""));
        //textInput = new JFormattedTextField();
        //textInput = new JTextField();
        textInput = new NumericTextField();
        textInput.setHorizontalAlignment(JTextField.CENTER);
        textInput.setFont(Font.decode(textFont));
        textInput.setBackground(new Color(col[0], col[1], col[2]));
        
        
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            textInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180,180,180)));
        else
            textInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(bCol[0], bCol[1], bCol[2])));
            
        textInput.addActionListener(this);
        //textInput.setBackground(new Color(230,230,230));
        //textInput.setSize(20, 20);
        //textInput.setBackground(new Color(0,255,18));
        
        // Set textInput enable status
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST)
            textInput.setEnabled(false);
        
        
        /****************************************************
         * prev button
         ***************************************************/
        // button font
        String defImageSelectPanelPrevtButtonFont = "Arial-plain-16";
        key = "studyselectextendedpanel.prev.button.font";
        String prevButtonFont = propUtils.getPropertyFontValue(prop, key);
        if(prevButtonFont.equals(""))
            prevButtonFont = defImageSelectPanelPrevtButtonFont;
        
        // button size
        int[] defImageSelectPanelNextButtonSize = {160,38};
        key = "studyselectextendedpanel.prev.button.size";
        int[] prevButtonSize = propUtils.getPropertySizeValue(prop, key);
        if(prevButtonSize[0] == 0 && prevButtonSize[1] == 0){
            prevButtonSize[0] = defImageSelectPanelNextButtonSize[0];
            prevButtonSize[1] = defImageSelectPanelNextButtonSize[1];
        }
        
        // button space
        key = "studyselectextendedpanel.prev.button.alignment.left";
        int btnAlignLeft = propUtils.getPropertyIntegerValue(prop, key);
        if(btnAlignLeft == 0)
            btnAlignLeft = 20;
        
        // button space
        key = "studyselectextendedpanel.prev.button.alignment.right";
        int btnAlignRight = propUtils.getPropertyIntegerValue(prop, key);
        if(btnAlignRight == 0)
            btnAlignRight = 5;
        
        // button border color
        boolean imageSelectPanelNextButtonBorderColorPropertyStatus = true;
        key = "studyselectextendedpanel.prev.button.border.color";
        int[] prevButtonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if(prevButtonBorderColor[0] == 0 && prevButtonBorderColor[1] == 0 &&
                prevButtonBorderColor[2] == 0){
            imageSelectPanelNextButtonBorderColorPropertyStatus = false;
        }
        
        // button color
        boolean imageSelectPanelNextButtonColorPropertyStatus = true;
        key = "studyselectextendedpanel.prev.button.color";
        int[] prevButtonColor = propUtils.getPropertyColorValue(prop, key);
        if(prevButtonColor[0] == 0 && prevButtonColor[1] == 0 && prevButtonColor[2] == 0){
            imageSelectPanelNextButtonColorPropertyStatus = false;
        }
        
        // button text color
        key = "studyselectextendedpanel.prev.button.text.color";
        int[] prevButtonTextColor = propUtils.getPropertyColorValue(prop, key);
        if(prevButtonTextColor[0] == 0 && prevButtonTextColor[1] == 0 &&
                prevButtonTextColor[2] == 0){
            prevButtonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            prevButtonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            prevButtonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }
        
        // border
        Border imageSelectPanelNextButtonBorder =
                BorderFactory.createLineBorder(new Color(10,10,10));
        if(imageSelectPanelNextButtonBorderColorPropertyStatus){
            imageSelectPanelNextButtonBorder =
                    BorderFactory.createLineBorder(new Color(
                    prevButtonBorderColor[0],
                    prevButtonBorderColor[1],
                    prevButtonBorderColor[2]));
        }
        
        // button
        prevButton = new JButton("Prev");
        prevButton.setFont(Font.decode(prevButtonFont));
        prevButton.addActionListener(new PrevButtonAction());
        prevButton.setFocusable(false);
        //nextButton.addFocusListener(this);
        //nextButton.addKeyListener(this);
        
        if(imageSelectPanelNextButtonColorPropertyStatus){
            prevButton.setBackground(new Color(
                    prevButtonColor[0],
                    prevButtonColor[1],
                    prevButtonColor[2]));
        }
        prevButton.setForeground(new Color(
                prevButtonTextColor[0],
                prevButtonTextColor[1],
                prevButtonTextColor[2]));
        prevButton.setPreferredSize(new Dimension(
                prevButtonSize[0],
                prevButtonSize[1]));
        
        if(imageSelectPanelNextButtonBorderColorPropertyStatus)
            prevButton.setBorder(imageSelectPanelNextButtonBorder);
        
        /*******************************************************
         * create panel, label, textInput & button
         ******************************************************/
        // panel
        JPanel studySelectionPanel = new JPanel();
        studySelectionPanel.setBackground(new Color(color[0], color[1], color[2]));
        //double size[][] = {{f,p,f},{f,p,f}};
        double[][] size = {{labelAlignLeft, p, textInputAlignLeft, textInputSize,
                f, p, btnAlignRight}, {alignTop, p, alignBottom}};
        studySelectionPanel.setLayout(new TableLayout(size));
        tborder.setTitleFont(Font.decode(titleFont));
        tborder.setTitleColor(new Color(titleColor[0], titleColor[1], titleColor[2]));
        studySelectionPanel.setBorder(tborder);
        
        
        /***************************************************
         * add
         **************************************************/
        
        studySelectionPanel.add(gotoLabel, "1,1");
        studySelectionPanel.add(textInput, "3,1");
        studySelectionPanel.add(prevButton, "5,1");
        
        // color
        this.setBackground(new Color(color[0], color[1], color[2]));
        
        //createNextAction();
        //createPrevAction();
        //createFastKey1Action();
        //createFastKey2Action();
        //createFastKey3Action();
        //createFastKey4Action();
        //createFastKey5Action();
        //createFastKeyNext1Action();
        //createFastKeyNext2Action();
        //createFastKeyNext3Action();
        
        /*
        nextButton = new JButton(createImageIcon("c:/development/TCat/images/optionpane/ibu.gif", npDescription));
        nextButton.setPressedIcon(createImageIcon("c:/development/TCat/images/optionpane/ibu.gif", npDescription));
        nextButton.setRolloverIcon(createImageIcon("c:/development/TCat/images/optionpane/ibu.gif", npDescription));
        nextButton.setDisabledIcon(createImageIcon("c:/development/TCat/images/optionpane/ibu.gif", npDescription));
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.setMargin(new Insets(0,0,0,0));
         */
        
        // Key Bindings
        //nextButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0), "next");
        //nextButton.getActionMap().put(nextStackAction.getValue(Action.NAME), nextStackAction);
        
        
        /**
         * ChangeListener for the Next button.
         * This method is invoked before the actionPerformed method and
         * sets the canvas to black before the new image is displayed.
         * This is to avoid the so called "filmeffect" when using fantom
         * images.
         * NOT IN USE
         */
        
        //nextButton.addChangeListener(new ChangeListener(){
          //  public void stateChanged(ChangeEvent e){
              //System.out.println("ImageSelectControl:nextButton.ChangeListner");
              //  ButtonModel model = nextButton.getModel();
                
              //  boolean status1 = viewDex.appMainAdmin.vgControl.getLocalizationActiveStatus();
              //  boolean status2 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatusNoLocalization();
                
              //  StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.getSelectedStackNode();
              //if (stackNode.getNodeType() == StudyDbNodeType.NODE_TYPE_STACK) {
                //    if(model.isPressed() && !status1 && status2){
                  //      String str;
                    //    VgHistory vgHistory = viewDex.appMainAdmin.vgControl.getVgHistory();
                      //  String studyName = vgHistory.getStudyName();
                        //Properties prop = vgHistory.getVgProperties();
                        
                        // properties
                        //String key = "vgstudy." + studyName + ".stack.image-display.black-to-next";
                        //String strStack = propUtils.getPropertyStringValue(prop, key);
                        
                         // properties
                        //key = "vgstudy." + studyName + ".image-display.black-to-next";
                        //String strImage = propUtils.getPropertyStringValue(prop, key);
                        
                        /*
                        if(stackNode.getNodeType() == StudyDbStackType.STACK_TYPE_STACK_IMAGE)
                            str = strStack;
                        else
                            if(stackNode.getNodeType() == StudyDbStackType.STACK_TYPE_SINGLE_IMAGE)
                                str = strImage;
                        */  
                        
                        //if(strStack.equalsIgnoreCase("Yes") || strStack.equalsIgnoreCase("Y"))
                          //  viewDex.canvasControl.setCanvasToBlack();
                    //}
                //}
           //}
       //});
       
        
        /*
         * This one would't solve the problem with the button staying armed.
         */
        prevButton.addMouseListener(new MouseListener(){
            public void mouseReleased(MouseEvent e){
                /*try{
                    Thread.sleep(0);
                } catch (InterruptedException ea){
                }*/
                
            }
            public void mouseExited(MouseEvent e){
                
            }
            
            public void mouseEntered(MouseEvent e){
                
            }
            
            public void mousePressed(MouseEvent e){
                
            }
            
            public void mouseClicked(MouseEvent e){
                
            }
        });
        
        
        // prevAction
        //Action prevAction= new prevButtonAction("prev", null, "This is the prevbutton",
        //                                new Integer(KeyEvent.VK_NUMPAD1));
        //description = getString("ButtonDemo.phone");
        //prevButton = new JButton("Prev");
        //prevButton.setFocusable(false);
        
        /*
        prevButton = new JButton(createImageIcon("c:/development/TCat/images/buttons/arrow_right.gif", npDescription));
        prevButton.setPressedIcon(createImageIcon("c:/development/TCat/images/buttons/b1p.gif", npDescription));
        prevButton.setRolloverIcon(createImageIcon("c:/development/TCat/images/buttons/b1r.gif", npDescription));
        prevButton.setDisabledIcon(createImageIcon("c:/development/TCat/images/buttons/b1d.gif", npDescription));
        prevButton.setFocusPainted(false);
        prevButton.setBorderPainted(false);
        prevButton.setContentAreaFilled(false);
        prevButton.setMargin(new Insets(0,0,0,0));
         */
        
        // Key Bindings
        //prevButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), "prev");
        //prevButton.getActionMap().put(prevAction.getValue(Action.NAME), prevAction);
        
        /*
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed();
            }
        });
         **/
        // end prevButton
        
        
        //studySelectionPanel.add(prevButton, "1,1");
        return studySelectionPanel;
    }
    
    
    // **************************************************
    //
    //   Actions
    //
    // **************************************************
    
    
    /**
     * ActionListener for the Next button.
     * This class handles only the stack mode.
     */
    class PrevButtonAction extends AbstractAction implements Runnable{
        /*public void actionPerformed(java.awt.event.ActionEvent evt){
            Point2D activePoint = viewDex.appMainAdmin.vgControl.getLocalizationActivePoint();
            viewDex.appMainAdmin.vgControl.updateLocalizationActiveStatus(activePoint);
            
            boolean status1 = viewDex.appMainAdmin.vgControl.getLocalizationActiveStatus();
            boolean status2 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatusNoLocalization(); 
            //boolean status3 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatus();
            
            //long msecs;
            //msecs = System.currentTimeMillis();
            if(!status1 && status2)
                viewDex.appMainAdmin.vgControl.nextStackAction();
            else
                Toolkit.getDefaultToolkit().beep();
            //System.out.println("Time for NextButtonAction: " + (System.currentTimeMillis()-msecs));
        }*/
        
        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt){
            mainPanel.requestFocusInWindow();
            int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
            int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

            Point2D activePoint = viewDex.localization.getLocalizationActivePoint();
            boolean taskPanelActivePointStatus = viewDex.localization.getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);
            if (taskPanelActivePointStatus) {
                viewDex.localization.setLocalizationStatus(activePoint, StudyDbLocalizationStatus.SET);
            }

            boolean activeStatusExist = viewDex.localization.localizationActiveStatusExist();
            //boolean status2 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatusNoLocalization(); 
            //boolean status3 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatus();
            
            //long msecs;
            //msecs = System.currentTimeMillis();
            //if(!(!status1 && status2))
            if(activeStatusExist)
                Toolkit.getDefaultToolkit().beep();
            else{
                // Start no thread if first stack/image
                if(viewDex.appMainAdmin.vgControl.studyDbUtility.prevStackNodeExist()) {
                    if(loaderThread != null)
                        return;
                    loaderThread = new Thread((Runnable) this);
                    loaderThread.start();
                }
                else
                    Toolkit.getDefaultToolkit().beep();
            }
        }
        
        public void run(){
            viewDex.setBusyCursor();
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            VgHistory vgHistory = viewDex.appMainAdmin.vgControl.getVgHistory();
            //String studyName = vgHistory.getStudyName();
            Properties prop = vgHistory.getVgProperties();
            long val = 0;
            
            String key = "image-display.delay-to-next";
            String s1 = "0";
            if(prop.containsKey(key))
                s1 = prop.getProperty(key).trim();
            if(s1.equals(""))
                s1 = "0";
            try{
                val = Long.valueOf(s1);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
            
            // Do not use this value
            val = 0;
            
            // Test for a negative value
            if(Long.signum(val) == -1)
                val = 0L;
            
            // properties
            key = "image-display.black-to-next";
            String strStack = propUtils.getPropertyStringValue(prop, key);
           
             // Set canvas black
            if(strStack.equalsIgnoreCase("Yes") || strStack.equalsIgnoreCase("Y")){
                viewDex.canvasControl.setCanvasToBlack();
            }
            else{
                if(viewDex.appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus() &&
                        !viewDex.appMainAdmin.viewDex.eyeTracking.getEyeTrackingRenderDuringLoopStatus())
                    viewDex.canvasControl.setCanvasETColor();
            }
            
            /*
            if(strStack.equalsIgnoreCase("Yes") || strStack.equalsIgnoreCase("Y")){
                // eye tracking
                if(!viewDex.appMainAdmin.vgControl.getEyeTrackingRenderDuringLoopStatus())
                    viewDex.canvasControl.setCanvasETColor();
                viewDex.canvasControl.setCanvasToBlack();
            }*/
            
            try{
                Thread.sleep(val);
            } catch (Exception e){}
            
            viewDex.appMainAdmin.vgControl.prevStackAction();
            if(viewDex.vgStudyNextCaseExtendedControl != null)
                viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
            loaderThread = null;
            
            //setCursor(Cursor.getDefaultCursor());
            viewDex.setDefaultCursor();
        }
    }
            
      /*
            if(viewDex.appMainAdmin.vgControl.getStackDoneStatus())
                nextButtonActionPerformed();
            else
                Toolkit.getDefaultToolkit().beep();
        }
        
        public void run(){
        }
        */
    
    /**
     * ActionListener for the Next button.
     */
    /*
    class NextButtonAction extends AbstractAction implements Runnable{
        public void actionPerformed(java.awt.event.ActionEvent evt){
           
            // Check if all questions have been answered.
            //if(!viewDex.appMainAdmin.vgControl.getTaskPanelResultStatus()){
            
            if(false){
                Toolkit.getDefaultToolkit().beep();
            } else{
                // Start no thread if last image
                if(viewDex.appMainAdmin.vgControl.nextImageExist()) {
                    if(loaderThread != null)
                        return;
                    loaderThread = new Thread((Runnable) this);
                    loaderThread.start();
                } else
                    nextButtonActionPerformed();
            }
        }
         
        public void run(){
            viewDex.setBusyCursor();
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            VgHistory vgHistory = viewDex.appMainAdmin.vgControl.getVgHistory();
            //String studyName = vgHistory.getStudyName();
            Properties prop = vgHistory.getVgProperties();
            long val = 0;
            
            String key = "image-display.delay-to-next";
            String s1 = "0";
            if(prop.containsKey(key))
                s1 = prop.getProperty(key).trim();
            if(s1.equals(""))
                s1 = "0";
            try{
                val = Long.valueOf(s1);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
            
            // Test for a negative value
            if(Long.signum(val) == -1)
                val = 0L;
            
            try{
                Thread.sleep(val);
            } catch (Exception e){}
            
            nextButtonActionPerformed();
            loaderThread = null;
            
            //setCursor(Cursor.getDefaultCursor());
            viewDex.setDefaultCursor();
        }
    }
     */
    
    /**
     * Check if next image exist.
     * Set the next image.
     * Update rating.
     * NOT IN USE
     */
    protected void nextButtonActionPerformed(){
        //System.out.println("ImageSelectControl: nextButtonActionPerformed");
        
        viewDex.appMainAdmin.vgControl.nextStackAction();
    }

    // *****************************************************
    //
    //    ImageSelectEvent
    //
    // *****************************************************
    
    public void addImageSelectListener(ImageSelectListener il){
        imgLst.addElement(il);
    }
    
    public void removeImageSelectListener(ImageSelectListener il){
        if(!(imgLst.isEmpty()))
            imgLst.removeElement(il);
    }
    
    protected void fireImageSelectEvent() {
        for(Enumeration e= imgLst.elements(); e.hasMoreElements();){
            ImageSelectEvent ie = new ImageSelectEvent();
            ImageSelectListener il = (ImageSelectListener)(e.nextElement());
            il.nextImage(ie);
        }
    }
    
    /*******************************************************
     * focusListener interface
     ******************************************************/
    public void focusGained(java.awt.event.FocusEvent e){
    }
    
    public void focusLost(java.awt.event.FocusEvent e){
        
    }
    
    /*******************************************************
     * KeyListener interface
     ******************************************************/
    public void keyReleased(KeyEvent e) {
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
        //System.out.println("VgImageSelectControlPanel.keyPressed");
    }
    
    //A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("VgStudyDicomSelectExtendedPAnel:createFormatter formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    public void actionPerformed(ActionEvent e) {
        int value = 0;
        String text = textInput.getText();
        
        try{
            value = Integer.parseInt(text);
        }catch(NumberFormatException exp){
            System.out.println("VgStudyDicomSelectExtendedPanel.actionPerformed Value: NumberFormatException");
        }
        textInput.setText("");
        //textInput.setCaretPosition(0);
        //textInput.getCaret().setBlinkRate(0);
        
        // Set the focus on the label to get rid of the textInput caret
        //mainPanel.requestFocus();
        
        viewDex.appMainAdmin.vgControl.stackAction(value -1);
    }
    
    /**
     * Clear the inputField.
     */
    public void setGotoInputField(String str){
        textInput.setText(str);
    }
}
