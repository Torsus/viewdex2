/* @(#) ConfigDialog.java 06/02/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.util;

import info.clearthought.layout.TableLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import mft.vdex.app.ViewDex;


public class ConfigDialog extends javax.swing.JDialog implements ActionListener{
    private ViewDex xMedicalViewer;
    private JButton okButton, cancelButton;
    private ButtonGroup group;
    private JLabel studyLabel1, studyLabel2, studyLabel3, studyLabel4;
    private JRadioButton studyButton1, studyButton2;
    private JRadioButton studyButton3, studyButton4, studyButton5;
    
    private String ICSstr = "Criterial 1";
    private String ROCstr = "Criterial 2";
    private String NETstr = "3";
    private String MGTstr = "4";
    private String DEMstr = "5";
    private String CTSstr = "6";
    
    private JLabel studyNameStatus1, studyNameStatus2;
    private JLabel studyNameStatus3, studyNameStatus4;
    private JLabel studyNameStatus5;
    
    
    /** Creates a new instance of ConfigDialog */
    public ConfigDialog(ViewDex xmedicalviewer, boolean modal) {
        super((java.awt.Frame) xmedicalviewer, modal);
        xMedicalViewer = xmedicalviewer;
        initComponents();
        allowClosing();
    }
    
    private void initComponents(){
        Container pane = this.getContentPane();
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double size[][] = {{f},{p,p,f,p}};
        TableLayout layout = new TableLayout(size);
        pane.setLayout(layout);
        
        addHeaderPanel(pane, layout);
        //addUserPanel(pane, layout);
        addSelectPanel(pane, layout);
        addButtonPanel(pane, layout);
        
        this.setResizable(true);
        this.pack();
    }
    
    /* header */
    public void addHeaderPanel(Container pane, TableLayout layout){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        JPanel headerPanel = new JPanel();
        //headerPanel.setBackground(new Color(200,0,0));
        double size[][] = {{f,p,f},{p}};
        headerPanel.setLayout(new TableLayout(size));
        pane.add(headerPanel, "0,0");
        
        JLabel title = new JLabel("Test Dialog");
        title.setFont(new Font("Sans Serif", Font.PLAIN, 24));
        title.setHorizontalAlignment(JLabel.CENTER);
        
        headerPanel.add(title, "1,0");
        
    }
    
    // -------------------------------------------------------------------
    // buttonPanel
    // -------------------------------------------------------------------
    public void addButtonPanel(Container pane, TableLayout layout){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        int resolution = toolkit.getScreenResolution();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        //System.out.println("CatStudySelectDialog:addButtonPanel: xScale: " + xScale);
        //System.out.println("CatStudySelectDialog:addButtonPanel: yScale: " + yScale);
        
        JPanel buttonPanel = new JPanel();
        //buttonPanel.setBackground(new Color(0,0,200));
        int k = Math.round(30 * xScale);
        int l = Math.round(15 * yScale);
        int m = Math.round(10 * yScale);
        double size[][] = {{f,p,k,p,f},{l,p,m}};
        buttonPanel.setLayout(new TableLayout(size));
        pane.add(buttonPanel,"0,3");
        
        // okButton
        // Create the okAction
        Action okAction= new okButtonAction("enter", null, "This is the OK button",
                new Integer(KeyEvent.VK_P));
        okButton = new JButton();
        //okButton.setAction(myAction);
        okButton.setText("OK");
        okButton.setFocusable(true);
        // okButton.requestFocus();  not work
        okButton.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        int a = Math.round(100 * xScale);
        int b = Math.round(30 * yScale);
        okButton.setPreferredSize(new Dimension(a, b));
        okButton.setNextFocusableComponent(cancelButton);
        
        // Key Bindings
        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "enter");
        okButton.getActionMap().put(okAction.getValue(Action.NAME), okAction);
        //okButton.getActionMap().put("enter", actionPerformed());
        /*Action myAction = new AbstractAction("enter"){
            public void actionPerformed(java.awt.event.ActionListener evt){
                doSomething();
            }
        };*/
        
        //okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),new Object());
        //this.getRootPane().setDefaultButton(okButton);
        
        okButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                okButtonActionPerformed(evt);
            }
        });
        // end okButton
        
        // cancelButton
        // Create the cancelButtonAction
        Action cancelAction= new cancelButtonAction("enter", null, "This is the Cancel button",
                new Integer(KeyEvent.VK_P));
        cancelButton = new JButton("Cancel");
        cancelButton.setFocusable(true);
        cancelButton.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(a, b));
        
        // Key Bindings
        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "enter");
        cancelButton.getActionMap().put(cancelAction.getValue(Action.NAME), cancelAction);
        
        cancelButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                cancelButtonActionPerformed(evt);
            }
        });
        // end cancelButton
        
        buttonPanel.add(okButton, "1,1");
        buttonPanel.add(cancelButton, "3,1");
    }
    
    // -------------------------------------------------------------------
    // selectPanel
    // -------------------------------------------------------------------
    public void addSelectPanel(Container pane, TableLayout layout){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        // Screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        int resolution = toolkit.getScreenResolution();
        float xScale = (float) d.width / 1280;
        float yScale = (float) d.height / 1024;
        
        JPanel buttonPanel = new JPanel();
        //buttonPanel.setBackground(new Color(0,0,200));
        int k = Math.round(10 * xScale);
        
        // selectMainPanel
        JPanel selectMainPanel = new JPanel();
        //selectMainPanel.setBackground(new Color(200,0,0));
        double sizesmp[][] = {{k,f,k},{k,f,k}};
        selectMainPanel.setLayout(new TableLayout(sizesmp));
        pane.add(selectMainPanel,"0,2");
        
        // selectPanel
        JPanel selectPanel = new JPanel();
        //selectPanel.setBackground(new Color(0,100,0));
        Border blackBorder = BorderFactory.createEtchedBorder();
        selectPanel.setBorder(blackBorder);
        double sizesp[][] = {{f},{f}};
        selectPanel.setLayout(new TableLayout(sizesp));
        
        // selectStudyPanel
        JPanel selectStudyPanel = new JPanel();
        //selectStudyPanel.setBackground(new Color(0,200,100));
        
        if(true){
            int x1 = Math.round(10 * xScale);
            int x2 = Math.round(30 * xScale);
            int x3 = Math.round(375 * xScale); // mod 2004-04-27
            int x4 = Math.round(50 * xScale);
            
            int y1 = Math.round(5 * xScale);
            int y2 = Math.round(3 * xScale);
            double sizessp[][] = {{x1,p,x2,x3,x4},{f,y1,p,y2,p,y2,p,y2,p,y2,p,y1,f}};
            selectStudyPanel.setLayout(new TableLayout(sizessp));
        }
        
        if(1 == 2){
            int a1 = Math.round(10 * xScale);
            int a2 = Math.round(30 * xScale);
            int a3 = Math.round(610 * xScale); // Overall size
            int a4 = Math.round(50 * xScale);
            
            int b5 = Math.round(5 * xScale);
            int b6 = Math.round(3 * xScale);
            
            double sizessp[][] = {{a1,p,a2,a3,a4},{f,b5,p,b6,p,b6,p,b6,p,b6,p,b5,f}};
            selectStudyPanel.setLayout(new TableLayout(sizessp));
        }
        
        // button1
        studyButton1 = new JRadioButton(" " + ICSstr);
        studyButton1.setEnabled(true);
        studyButton1.setFont(getScaledFont("SansSerif", Font.BOLD, 14));
        studyButton1.addActionListener(this);
        studyButton1.setActionCommand("studyButton1");
        studyButton1.setSelected(false);
        //studyButton1.setSize(20,20);
        
        // button2
        studyButton2 = new JRadioButton(" " + ROCstr);
        studyButton2.setEnabled(true);
        studyButton2.setFont(getScaledFont("SansSerif", Font.BOLD, 14));
        studyButton2.addActionListener(this);
        studyButton2.setActionCommand("studyButton2");
        studyButton2.setSelected(false);
        
        // button3
        studyButton3 = new JRadioButton(" " + NETstr);
        studyButton3.setEnabled(true);
        studyButton3.setFont(getScaledFont("SansSerif", Font.BOLD, 14));
        studyButton3.addActionListener(this);
        studyButton3.setActionCommand("studyButton3");
        studyButton3.setSelected(false);
        
        // button4
        studyButton4 = new JRadioButton(" " + MGTstr);
        studyButton4.setEnabled(true);
        studyButton4.setFont(getScaledFont("SansSerif", Font.BOLD, 14));
        studyButton4.addActionListener(this);
        studyButton4.setActionCommand("studyButton4");
        studyButton4.setSelected(false);
        
        // button5
        studyButton5 = new JRadioButton(" " + CTSstr);
        studyButton5.setEnabled(true);
        studyButton5.setFont(getScaledFont("SansSerif", Font.BOLD, 14));
        studyButton5.addActionListener(this);
        studyButton5.setActionCommand("studyButton5");
        studyButton5.setSelected(false);
        
        // add
        selectStudyPanel.add(studyButton1, "1,2");
        selectStudyPanel.add(studyButton2, "1,4");
        selectStudyPanel.add(studyButton3, "1,6");
        selectStudyPanel.add(studyButton4, "1,8");
        selectStudyPanel.add(studyButton5, "1,10");
        
        // studyNameStatus
        studyNameStatus1 = new JLabel();
        studyNameStatus1.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        studyNameStatus2 = new JLabel();
        studyNameStatus2.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        studyNameStatus3 = new JLabel();
        studyNameStatus3.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        studyNameStatus4 = new JLabel();
        studyNameStatus4.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        studyNameStatus5 = new JLabel();
        studyNameStatus5.setFont(getScaledFont("SansSerif", Font.PLAIN, 14));
        
        // add
        selectStudyPanel.add(studyNameStatus1, "3,2");
        selectStudyPanel.add(studyNameStatus2, "3,4");
        selectStudyPanel.add(studyNameStatus3, "3,6");
        selectStudyPanel.add(studyNameStatus4, "3,8");
        selectStudyPanel.add(studyNameStatus5, "3,10");
        
        selectPanel.add(selectStudyPanel,"0,0");
        selectMainPanel.add(selectPanel, "1,1");
    }
    
    /* closing the dialog */
    public void allowClosing(){
        addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent evt){
                closeDialog(evt);
            }
        });
    }
    
    /* closeDialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt){
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //doClose(RET_OK);
        setVisible(false);
        dispose();
        setCursor(Cursor.getDefaultCursor());
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt){
        //doClose(RET_CANCEL);
        setVisible(false);
        dispose();
    }
    
    /* doClose */
    private void doClose(int retStatus){
    }
    
    // *******************************************************************
    // *******************************************************************
    // actions
    // *******************************************************************
    // *******************************************************************
    public class okButtonAction extends AbstractAction{
        public okButtonAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
        
        public void actionPerformed(ActionEvent e) {
            okButton.doClick();
            //okButtonActionPerformed(e);
        }
    }
    
    public class cancelButtonAction extends AbstractAction{
        public cancelButtonAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
        public void actionPerformed(ActionEvent e) {
            cancelButton.doClick();
            //okButtonActionPerformed(e);
        }
    }
    
    /** actionPerformed
     * Action when the studyType radiobuttons are selected.
     */
    public void actionPerformed(ActionEvent e){
        if("studyButton1".equals(e.getActionCommand())){
            okButton.requestFocus();
            xMedicalViewer.updateVGAPanel();
            return;
        }
        
        if("studyButton2".equals(e.getActionCommand())){
            //xMedicalViewer.updateVGAPanel2();
            xMedicalViewer.createUserDefForTest2();
            okButton.requestFocus();
            return;
        }
        
        if("studyButton3".equals(e.getActionCommand())){
            okButton.requestFocus();
            return;
        }
        
        if("studyButton4".equals(e.getActionCommand())){
            okButton.requestFocus();
            return;
        }
        
        if("studyButton5".equals(e.getActionCommand())){
            okButton.requestFocus();
            return;
        }
    }
    
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
    public Font getScaledFont(String fontName, int fontStyle, int pointSizeFor1280Mode){
        Font f = new Font(fontName, fontStyle, pointSizeFor1280Mode);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if(d.height == 1024)
            return f;
        else{
            int numerator = pointSizeFor1280Mode * d.height;
            float sizeForCurrentResolution = (float)numerator/1024;
            return f.deriveFont(sizeForCurrentResolution);
        }
    }
    
}
