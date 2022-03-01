/* @(#) VgClarificationPanel.java 08/29/2005
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
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import mft.vdex.app.AppProperty;
import mft.vdex.app.ViewDex;
import mft.vdex.app.AppPropertyUtils;

public class VgClarificationPanel extends JPanel{
    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private AppProperty appProperty;
    private VgHistory history;
    private JScrollPane scrollPane;
    private JTextPane textPane, textPane2;
    private String nl = "\n";
    
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    
    
        public VgClarificationPanel(ViewDex viewdex, AppProperty appproperty, VgHistory history) {
        this.viewDex = viewdex;
        this.appProperty = appproperty;
        this.history = history;
        propUtils = new AppPropertyUtils();
        createUI();
    }
    
    private void createUI(){
        /** Get the user definition list */
        ArrayList<VgTaskPanelClarification> list = history.getTaskPanelClarificationList();
        
        createLayout();
        //JPanel mainPanel = createMainPanel();
        JPanel mainPanel2 = createMainPanel2();
        
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();
        
        // top, left, bottom
        Insets inSets = new Insets(7,4,6,0);
        
        // color
        //int[] color = new int[3];
        //String studyName = history.getStudyName();
        //Properties prop = history.getVgProperties();
        //String key = "vgstudy" + "." + studyName + ".taskpanel.clarificationpanel.textpane.color";
        //color = getPropertyColorValue(prop, key);
        
        // panel color
        String key = "taskpanel.clarificationpanel.textpanel.color";
        int[] taskpanelClarificationPanelTextpaneColor = propUtils.getPropertyColorValue(prop, key);
        if(taskpanelClarificationPanelTextpaneColor[0] == 0 &&
                taskpanelClarificationPanelTextpaneColor[1] == 0 &&
                taskpanelClarificationPanelTextpaneColor[2] == 0){
            taskpanelClarificationPanelTextpaneColor[0] = AppPropertyUtils.defClarificationPanelColor[0];
            taskpanelClarificationPanelTextpaneColor[1] = AppPropertyUtils.defClarificationPanelColor[1];
            taskpanelClarificationPanelTextpaneColor[2] = AppPropertyUtils.defClarificationPanelColor[2];
        }
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setEnabled(true);
        textPane.setMargin(inSets);
        textPane.setDoubleBuffered(true);
        textPane.setFocusable(false);
        textPane.setBackground(
                new Color(taskpanelClarificationPanelTextpaneColor[0],
                taskpanelClarificationPanelTextpaneColor[1],
                taskpanelClarificationPanelTextpaneColor[2]));
        
        textPane2 = new JTextPane();
        textPane2.setEditable(false);
        textPane2.setEnabled(true);
        //float fl = (float)20.0;
        //textPane2.setAlignmentY(fl);
        textPane2.setMargin(inSets);
        textPane2.setDoubleBuffered(true);
        textPane2.setFocusable(false);
        textPane2.setBackground(
                new Color(taskpanelClarificationPanelTextpaneColor[0],
                taskpanelClarificationPanelTextpaneColor[1],
                taskpanelClarificationPanelTextpaneColor[2]));
        //textPane.scrollRectToVisible(null); //test delete
        
        createClarificationInfo2(list);
        createClarificationInfo3(list);
        
        mainPanel2.add(textPane, "0,1");
        mainPanel2.add(textPane2, "1,1");
        this.add(mainPanel2, "0,0");
        
        //test delete
        //Rectangle rec = textPane.getBounds();
        //mainPanel2.scrollRectToVisible(rec);
    }
    
    
    // 2007-05-20
    // NOT IN USE
    private void createUIold(){
        /** Get the user definition list */
        ArrayList<VgTaskPanelClarification> list = history.getTaskPanelClarificationList();
        
        createLayout();
        //JPanel mainPanel = createMainPanel();
        JPanel mainPanel2 = createMainPanel2();
        
        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        scrollPane.setForeground(new Color(223,230,185));
        
        Insets inSets = new Insets(7,5,0,0);
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setEnabled(true);
        textPane.setMargin(inSets);
        textPane.setDoubleBuffered(true);
        textPane.setBackground(new Color(223,230,185));
        
        textPane2 = new JTextPane();
        textPane2.setEditable(false);
        textPane2.setEnabled(true);
        float fl = (float)20.0;
        textPane2.setAlignmentY(fl);
        textPane2.setMargin(inSets);
        textPane2.setDoubleBuffered(true);
        textPane2.setBackground(new Color(223,230,185));
        
        createClarificationInfo2(list);
        createClarificationInfo3(list);
        
        mainPanel2.add(textPane, "0,1");
        mainPanel2.add(textPane2, "1,1");
        
        //mainPanel.add(mainPanel2, "0,1");
        //scrollPane.setViewportView(mainPanel);
        
        
        //mainPanel.add(scrollPane, "0,1");
        this.add(mainPanel2, "0,0");
        //this.add(scrollPane, "0,0");
    }
    
    /**
     * Create the layout.
     */
    private void createLayout(){
        double size[][] = {{f},{f}};
        this.setLayout(new TableLayout(size));
        //this.setBackground(Color.YELLOW);
    }
    
    /**
     * Create the mainPanel.
     */
    private JPanel createMainPanel(){
        double size[][] = {{f},{0,f,0}};
        JPanel panel = new JPanel();
        //panel.setBackground(Color.GREEN);
        panel.setLayout(new TableLayout(size));
        
        return panel;
    }
    
     /**
     * Create the mainPanel2
     */
    private JPanel createMainPanel2(){
        double size[][] = {{p,f,},{0,f,0}};
        JPanel panel = new JPanel();
        //panel.setBackground(Color.ORANGE);
        panel.setLayout(new TableLayout(size));
        //Border border = BorderFactory.createLineBorder(new Color(150,150,150));
        //TitledBorder tborder = BorderFactory.createTitledBorder("");
        //panel.setBorder(border);
        
        return panel;
    }
    
    /**
     * Create clarification info.
     */
    private void createClarificationInfo2(ArrayList<VgTaskPanelClarification> list){
        String[] strClar = new String[100];
        String[] styles = new String[100];
        String[] styles2 = new String[100];
        
         for(int j=0;j<strClar.length;j++)
            strClar[j] = null;
        
        styles2 = initStyles2(list);
        
        for(int i=0; i < list.size(); i++){
            if(list.get(i).getClarificationHeadText().equalsIgnoreCase("delimiter")){
                strClar[i] = "\n";
                //styles[i] = "delim12";
            } else{
                String str = list.get(i).getClarificationHeadText() + ":";
                 if(!(i == list.size() - 1))
                    strClar[i] = str + "\n";
                else
                    strClar[i] = str;
            }
        }
        Document doc = textPane.getDocument();
        
        try {
            for (int i=0; i < list.size(); i++) {
                doc.insertString(doc.getLength(), strClar[i],
                        textPane.getStyle(styles2[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }
    
    private String[] initStyles2(ArrayList<VgTaskPanelClarification> list){
        String[] styles = new String[100];
        
        // test
        /*java.util.List tabList = new ArrayList();
        // Create a left-aligned tab stop at 100 pixels from the left margin
        float pos = 20;
        int align = TabStop.ALIGN_LEFT;
        int leader = TabStop.LEAD_NONE;
        TabStop tstop = new TabStop(pos, align, leader);
        tabList.add(tstop);

        // Create a tab set from the tab stops
        TabStop[] tstops = (TabStop[])tabList.toArray(new TabStop[0]);
        TabSet tabs = new TabSet(tstops);

        // Add the tab set to the logical style;
        // the logical style is inherited by all paragraphs
        Style logicalStyle = textPane.getLogicalStyle();
        StyleConstants.setTabSet(logicalStyle, tabs);
        textPane.setLogicalStyle(logicalStyle);
         */
        
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        
        for(int i = 0; i < list.size(); i++){
            String[] fnt = list.get(i).getClarificationTextFont();
            String styleName = "style" + Integer.toString(i);
            Style style = textPane.addStyle(styleName, null);
            //Style style = textPane.addStyle(styleName, logicalStyle);
            //textPane.setLogicalStyle(logicalStyle);
            //StyleConstants.setTabSet(style, tabs);
            StyleConstants.setFontFamily(style, fnt[0]);
            StyleConstants.setFontSize(style, Integer.decode(fnt[2]));
            
            if(fnt[1].equalsIgnoreCase("bold"))
                StyleConstants.setBold(style, true);
            
            if(fnt[1].equalsIgnoreCase("italic"))
                StyleConstants.setItalic(style, true);
            
            styles[i] = styleName;
        }
        return styles;
    }
    
    /**
     * Create clarification info.
     */
    private void createClarificationInfo3(ArrayList<VgTaskPanelClarification> list){
        String strDelim= "                ----------------------" + "\n";
        String[] strClar = new String[100];
        String[] styles = new String[100];
        String[] styles3 = new String[100];
        
        for(int j=0;j<strClar.length;j++)
            strClar[j] = null;
        
        styles3 = initStyles3(list);
        
        for(int i=0; i < list.size(); i++){
            if(list.get(i).getClarificationHeadText().equalsIgnoreCase("delimiter")){
                strClar[i] = strDelim;
                styles[i] = "delim12";
            } else{
                //String str = list.get(i).getClarificationHeadText() + ":   ";
                String str2 = list.get(i).getClarificationText();
                if(!(i == list.size() - 1))
                    strClar[i] = str2 + "\n";
                else
                    strClar[i] = str2 ;
            }
        }
        Document doc = textPane2.getDocument();
        
        try {
            for (int i=0; i < list.size(); i++) {
                doc.insertString(doc.getLength(), strClar[i],
                        textPane2.getStyle(styles3[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }
    
    private String[] initStyles3(ArrayList<VgTaskPanelClarification> list){
        String[] styles = new String[100];
        
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        
        for(int i = 0; i < list.size(); i++){
            String[] fnt = list.get(i).getClarificationTextFont();
            String styleName = "style" + Integer.toString(i);
            Style style = textPane2.addStyle(styleName, null);
            StyleConstants.setFontFamily(style, fnt[0]);
            StyleConstants.setFontSize(style, Integer.decode(fnt[2]));
            
            if(fnt[1].equalsIgnoreCase("bold"))
                StyleConstants.setBold(style, true);
            
            if(fnt[1].equalsIgnoreCase("italic"))
                StyleConstants.setItalic(style, true);
            
            styles[i] = styleName;
        }
        return styles;
    }
    
    // Original
    // NOT IN USE
    private void createClarificationInfo(ArrayList<VgTaskPanelClarification> list){
        String strDelim= "            ----------------------" + "\n";
        String[] strClar = new String[100];
        String[] styles = new String[100];
        
        for(int i=0; i < list.size(); i++){
            if(list.get(i).getClarificationHeadText().equalsIgnoreCase("delimiter")){
                strClar[i] = strDelim;
                styles[i] = "delim12";
            } else{
                String str = list.get(i).getClarificationHeadText() + ":   ";
                String str2 = list.get(i).getClarificationText();
                if(!(i == list.size() - 1))
                    strClar[i] = str + str2 + "\n";
                else
                    strClar[i] = str + str2 ;
            }
            
            styles[i] = "mono12";
        }
        
        //initStyles(textPane);
        Document doc = textPane.getDocument();
        
        try {
            for (int i=0; i < list.size(); i++) {
                doc.insertString(doc.getLength(), strClar[i],
                        textPane.getStyle(styles[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }
    
    private void initStyles(JTextPane pane){
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        
        // vaiableText
        Style variable= StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style monoText = textPane.addStyle("mono12", variable);
        StyleConstants.setFontFamily(variable, "Dialog");
        StyleConstants.setFontSize(monoText, 14);
        
        // Delimiter
        Style delimText = textPane.addStyle("delim12", variable);
        StyleConstants.setFontFamily(variable, "Dialog");
        StyleConstants.setFontSize(delimText, 12);
    }
}
