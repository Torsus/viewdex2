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
import javax.swing.JPanel;
import javax.swing.border.Border;
import mft.vdex.app.ViewDex;
import mft.vdex.controls.*;
import mft.vdex.ds.StudyDbLocalizationStatus;
import mft.vdex.app.AppPropertyUtils;
import mft.vdex.util.Stopwatch;

public class VgStudyNextCasePanel extends JPanel implements FocusListener, KeyListener {

    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private VgHistory history;
    private JPanel mainPanel;
    private JButton nextButton;
    private Vector imgLst = new Vector();
    private Thread loaderThread;

    public VgStudyNextCasePanel(ViewDex viewdex, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.history = vghistory;
        propUtils = new AppPropertyUtils();
        createUI();
    }

    protected void createUI() {
        createLayout();
        mainPanel = createMainPanel();

        // border
        //Border border = BorderFactory.createLineBorder(new Color(0,0,0));
        //mainPanel.setBorder(border);

        this.add(mainPanel, "0,1");
    }

    private void createLayout() {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "studyselectpanel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        double size[][] = {{f}, {3, f, 8}};
        this.setLayout(new TableLayout(size));
        this.setBackground(new Color(color[0], color[1], color[2]));
    }

    private JPanel createMainPanel() {
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;

        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();

        // panel color
        String key = "studyselectpanel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if (color[0] == 0 && color[1] == 0 && color[2] == 0) {
            color[0] = AppPropertyUtils.defPanelColor[0];
            color[1] = AppPropertyUtils.defPanelColor[1];
            color[2] = AppPropertyUtils.defPanelColor[2];
        }

        // panel
        JPanel studySelectionPanel = new JPanel();
        studySelectionPanel.setBackground(new Color(color[0], color[1], color[2]));
        double size[][] = {{f, p, f}, {f, p, f}};
        studySelectionPanel.setLayout(new TableLayout(size));

        // color
        this.setBackground(new Color(color[0], color[1], color[2]));

        // button color
        boolean imageSelectPanelNextButtonColorPropertyStatus = true;
        key = "studyselectpanel.next.button.color";
        int[] nextButtonColor = propUtils.getPropertyColorValue(prop, key);
        if (nextButtonColor[0] == 0 && nextButtonColor[1] == 0 && nextButtonColor[2] == 0) {
            imageSelectPanelNextButtonColorPropertyStatus = false;
        }

        // button text color
        key = "studyselectpanel.next.button.text.color";
        int[] nextButtonTextColor = propUtils.getPropertyColorValue(prop, key);
        if (nextButtonTextColor[0] == 0 && nextButtonTextColor[1] == 0
                && nextButtonTextColor[2] == 0) {
            nextButtonTextColor[0] = AppPropertyUtils.defButtonTextColor[0];
            nextButtonTextColor[1] = AppPropertyUtils.defButtonTextColor[1];
            nextButtonTextColor[2] = AppPropertyUtils.defButtonTextColor[2];
        }

        // button font
        String defImageSelectPanelNextButtonFont = "Arial-plain-26";
        key = "studyselectpanel.next.button.font";
        String nextButtonFont = propUtils.getPropertyFontValue(prop, key);
        if (nextButtonFont.equals("")) {
            nextButtonFont = defImageSelectPanelNextButtonFont;
        }

        // button size
        int[] defImageSelectPanelNextButtonSize = {160, 38};
        key = "studyselectpanel.next.button.size";
        int[] nextButtonSize = propUtils.getPropertySizeValue(prop, key);
        if (nextButtonSize[0] == 0 && nextButtonSize[1] == 0) {
            nextButtonSize[0] = defImageSelectPanelNextButtonSize[0];
            nextButtonSize[1] = defImageSelectPanelNextButtonSize[1];
        }

        // button border color
        boolean imageSelectPanelNextButtonBorderColorPropertyStatus = true;
        key = "studyselectpanel.next.button.border.color";
        int[] nextButtonBorderColor = propUtils.getPropertyColorValue(prop, key);
        if (nextButtonBorderColor[0] == 0 && nextButtonBorderColor[1] == 0
                && nextButtonBorderColor[2] == 0) {
            imageSelectPanelNextButtonBorderColorPropertyStatus = false;
        }
        Border imageSelectPanelNextButtonBorder =
                BorderFactory.createLineBorder(new Color(10, 10, 10));
        if (imageSelectPanelNextButtonBorderColorPropertyStatus) {
            imageSelectPanelNextButtonBorder =
                    BorderFactory.createLineBorder(new Color(
                    nextButtonBorderColor[0],
                    nextButtonBorderColor[1],
                    nextButtonBorderColor[2]));
        }

        // nextbutton
        nextButton = new JButton("Next");
        nextButton.setFont(Font.decode(nextButtonFont));
        nextButton.addActionListener(new NextButtonAction());
        nextButton.setFocusable(false);
        //nextButton.addFocusListener(this);
        //nextButton.addKeyListener(this);

        if (imageSelectPanelNextButtonColorPropertyStatus) {
            nextButton.setBackground(new Color(
                    nextButtonColor[0],
                    nextButtonColor[1],
                    nextButtonColor[2]));
        }
        nextButton.setForeground(new Color(
                nextButtonTextColor[0],
                nextButtonTextColor[1],
                nextButtonTextColor[2]));
        nextButton.setPreferredSize(new Dimension(
                nextButtonSize[0],
                nextButtonSize[1]));

        if (imageSelectPanelNextButtonBorderColorPropertyStatus) {
            nextButton.setBorder(imageSelectPanelNextButtonBorder);
        }

        studySelectionPanel.add(nextButton, "1,1");

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
        nextButton.addMouseListener(new MouseListener() {

            public void mouseReleased(MouseEvent e) {
                /*try{
                Thread.sleep(0);
                } catch (InterruptedException ea){
                }*/
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
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
    class NextButtonAction extends AbstractAction implements Runnable {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            
            //===========================================================
            // Test History object
            
            //Stopwatch timer = new Stopwatch().start();
            //timer.stop();
            //long etime = timer.getElapsedTime();
            //System.out.println(timer.getElapsedTime());
            //System.out.print(etime);
            
            // Test history 20191023
            //Runtime r = Runtime.getRuntime();
            //r.gc();
            //System.gc();
            //viewDex.vgHistoryMainUtil.printHistoryObjectSize("VgStudyNextCasePanel");
            
            // end test history
            //============================================================
            
            viewDex.requestFocusInWindow();
            int stackNodeCnt = viewDex.vgHistory.getSelectedStackNodeCount();
            int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

            if (viewDex.vgStudyNextCaseExtendedControl != null) {
                viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
            }

            // if activePoint exist and all localization answers are given set localizationStatus to Set status.
            boolean localizationActive = viewDex.localization.localizationActiveStatusExist();
            Point2D activePoint = viewDex.localization.getLocalizationActivePoint();
            boolean taskPanelActivePointStatus = viewDex.localization.getTaskPanelTaskStatusLocalization(stackNodeCnt, imageNodeCnt, activePoint);

            if (localizationActive && taskPanelActivePointStatus) {
                viewDex.localization.updateLocalizationStatus(StudyDbLocalizationStatus.ACTIVE, StudyDbLocalizationStatus.SET);
            }

            // taskPanel status
            int localizationStatus = viewDex.localization.getLocalizationStatus(activePoint);
            boolean taskPanelResultNoLocalization = viewDex.vgTaskPanelUtility.getTaskPanelTaskStatusNoLocalization();
            //boolean status3 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatus();

            //Mod 2015-02-24 Kerstin Lagerstrand study
            // Force to give answers for all localization tasks
            /*
            boolean status3 = true;
            boolean localizationExist = viewDex.appMainAdmin.vgControl.getTaskPanelLocalizationExist();
            if(localizationExist)
            status3 = viewDex.appMainAdmin.vgControl.getTaskPanelResultStatusForLocalization();
            if(!(!status1 && status2 && status3))
             */

            //long msecs;
            //msecs = System.currentTimeMillis();
            if ((localizationStatus == StudyDbLocalizationStatus.NONE ||
                    localizationStatus == StudyDbLocalizationStatus.SET ||
                    localizationStatus == StudyDbLocalizationStatus.SELECTED) &&
                    (taskPanelResultNoLocalization)){

                // Start no thread if last stack/image
                if (viewDex.appMainAdmin.vgControl.studyDbUtility.nextStackNodeExist()) {
                    if (loaderThread != null) {
                        return;
                    }
                    loaderThread = new Thread((Runnable) this);
                    loaderThread.start();
                } else {
                    viewDex.appMainAdmin.vgControl.nextStackAction();
                    //viewDex.vgNotesPanel.setNoteText("");
                }
            }
            else
                Toolkit.getDefaultToolkit().beep();

            //if(!(!localizationActive && taskPanelResultNoLocalization))
            //  Toolkit.getDefaultToolkit().beep();
            //else{
            // Start no thread if last stack/image
            //  if(viewDex.appMainAdmin.vgControl.nextStackNodeExist()) {
            //    if(loaderThread != null)
            //      return;
            // loaderThread = new Thread((Runnable) this);
            //loaderThread.start();
            //} else{
            //  viewDex.appMainAdmin.vgControl.nextStackAction();
            //viewDex.vgNotesPanel.setNoteText("");
            //}
            //}
            
            //==========================================================
            // Test History object
            
            // System.out.println(timer.getElapsedTime());
            
            // end test History object
            //==========================================================
        }

        public void run() {
            viewDex.setBusyCursor();
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            VgHistory vgHistory = viewDex.appMainAdmin.vgControl.getVgHistory();
            //String studyName = vgHistory.getStudyName();
            Properties prop = vgHistory.getVgProperties();
            long val = 0;

            String key = "image-display.delay-to-next";
            String s1 = "0";
            if (prop.containsKey(key)) {
                s1 = prop.getProperty(key).trim();
            }
            if (s1.equals("")) {
                s1 = "0";
            }
            try {
                val = Long.valueOf(s1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // Test for a negative value
            if (Long.signum(val) == -1) {
                val = 0L;
            }

            // properties
            key = "image-display.black-to-next";
            String strStack = propUtils.getPropertyStringValue(prop, key);

            // Set canvas black
            if (strStack.equalsIgnoreCase("Yes") || strStack.equalsIgnoreCase("Y")) {
                viewDex.canvasControl.setCanvasToBlack();
            } else {
                if (viewDex.appMainAdmin.viewDex.eyeTracking.getEyeTrackingStatus()
                        && !viewDex.appMainAdmin.viewDex.eyeTracking.getEyeTrackingRenderDuringLoopStatus()) {
                    viewDex.canvasControl.setCanvasETColor();
                }
            }

            try {
                Thread.sleep(val);
            } catch (Exception e) {
            }

            viewDex.appMainAdmin.vgControl.nextStackAction();
            loaderThread = null;

            //setCursor(Cursor.getDefaultCursor());
            viewDex.setDefaultCursor();
            //viewDex.vgNotesPanel.setNoteText("");
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
    protected void nextButtonActionPerformed() {
        //System.out.println("ImageSelectControl: nextButtonActionPerformed");
        //viewDex.appMainAdmin.vgControl.nextStackAction();
    }

    // *****************************************************
    //
    //    ImageSelectEvent
    //
    // *****************************************************
    public void addImageSelectListener(ImageSelectListener il) {
        imgLst.addElement(il);
    }

    public void removeImageSelectListener(ImageSelectListener il) {
        if (!(imgLst.isEmpty())) {
            imgLst.removeElement(il);
        }
    }

    protected void fireImageSelectEvent() {
        for (Enumeration e = imgLst.elements(); e.hasMoreElements();) {
            ImageSelectEvent ie = new ImageSelectEvent();
            ImageSelectListener il = (ImageSelectListener) (e.nextElement());
            il.nextImage(ie);
        }
    }

    /*******************************************************
     * focusListener interface
     ******************************************************/
    public void focusGained(java.awt.event.FocusEvent e) {
    }

    public void focusLost(java.awt.event.FocusEvent e) {
        viewDex.requestFocusInWindow();
    }

    /*******************************************************
     * KeyListener interface
     ******************************************************/
    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("VgImageSelectControlPanel: keyPressed");
    }
}
