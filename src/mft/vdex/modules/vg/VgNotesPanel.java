/* @(#) VgClarificationPanel.java 08/29/2005
 *
 * Copyright (c) 2010 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.modules.vg;

import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mft.vdex.app.AppProperty;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.app.AppPropertyUtils;

public class VgNotesPanel extends JPanel{
    private AppPropertyUtils propUtils;
    private ViewDex viewDex;
    private AppProperty appProperty;
    private VgHistory history;
    private String nl = "\n";
    
    private double f = TableLayout.FILL;
    private double p = TableLayout.PREFERRED;
    
    public JTextArea textArea;
    public JPanel mainPanel;
    
    
        public VgNotesPanel(ViewDex viewdex, AppProperty appproperty, VgHistory history) {
        this.viewDex = viewdex;
        this.appProperty = appproperty;
        this.history = history;
        propUtils = new AppPropertyUtils();
        createUI();
    }
    
    private void createUI(){
        /** Get the user definition list */
        //ArrayList<VgTaskPanelClarification> list = history.getTaskPanelClarificationList();
        
        createLayout();
        //JPanel mainPanel = createMainPanel();
        mainPanel = createMainPanel();
        
        //String studyName = history.getStudyName();
        Properties prop = history.getVgProperties();
        
        // top,left,bottom,right
        Insets inSets = new Insets(7,4,6,7);
        
        // panel color
        String key = "notespanel.textpanel.color";
        int[] color = propUtils.getPropertyColorValue(prop, key);
        if(color[0] == 0 && color[1] == 0 && color[2] == 0){
            color[0] = AppPropertyUtils.defNotificationPanelColor[0];
            color[1] = AppPropertyUtils.defNotificationPanelColor[1];
            color[2] = AppPropertyUtils.defNotificationPanelColor[2];
        }
        Color notiColor = new Color(color[0], color[1], color[2]);
        
        //font
        String defCanvasFont = "SansSerif-plain-20";
        key = "notespanel.font";
        String font = propUtils.getPropertyFontValue(prop, key);
        if (font.equals(""))
            font = defCanvasFont;
        Font notiFont = Font.decode(font);
        
        textArea = new JTextArea();
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //textArea.setColumns(10);
        textArea.setMargin(inSets);
        textArea.setLineWrap(true);
        //textArea.setRows(10);
        textArea.setWrapStyleWord(true);
        textArea.setFont(notiFont);
        textArea.setBackground(notiColor);
        
        //jScrollPane = new JScrollPane(textArea);
        //mainPanel.add(jScrollPane, "1,1");
        mainPanel.add(textArea, "1,1");
        this.add(mainPanel, "0,0");
        
        // Check for runMode
        if(viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.SHOW_EXIST){
            //Toolkit.getDefaultToolkit().beep();
            //textArea.setEnabled(false);
            textArea.setEditable(false);
        }
        
        //test delete
        //Rectangle rec = textPane.getBounds();
        //mainPanel2.scrollRectToVisible(rec);
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
     * Create the mainPanel2
     */
    private JPanel createMainPanel(){
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
     * Get the text
     */
    public String getNoteText(){
        String str = textArea.getText();
        return str;
    }
    
     /**
     * Clear the text area
     */
    public void setNotesText(String str){
        textArea.setText(str);
    }

     public void saveNotesPanel() {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        Properties vgProp = history.getVgProperties();

        // notesStatus
        boolean notesStatus = false;
        String key = "notespanel";
        String notes = propUtils.getPropertyStringValue(vgProp, key);
        if (notes.equalsIgnoreCase("Yes") || notes.equalsIgnoreCase("Y")) {
            notesStatus = true;
        }

        // Save notes text
        if (notesStatus && viewDex.vgNotesPanel != null) {
            stackNode.setNotes(viewDex.appMainAdmin.viewDex.vgNotesPanel.getNoteText());
        }
    }

    /**
     * Set the notesPanel.
     */
    public void setNotesPanel() {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        String str = stackNode.getNotes();

        if (viewDex.vgNotesPanel != null) {
            viewDex.vgNotesPanel.setNotesText(str);
        }
    }
}
