/* @(#) VgClarificationPanel2.java 08/29/2005
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
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mft.vdex.app.AppProperty;
import mft.vdex.app.ViewDex;


// NOT IN USE
public class VgClarificationPanel2 extends JPanel{
    private ViewDex viewDex;
    private AppProperty appProperty;
    private VgHistory vgHistory;
    
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JPanel clarificationMainPanel;
    private ArrayList<JPanel> clarificationSubPanel = new ArrayList<JPanel>();
    private ArrayList<JPanel>clarificationLabelPanel = new ArrayList<JPanel>();
    private ArrayList<JLabel> clarificationHeadLabel = new ArrayList<JLabel>();
    private ArrayList<JLabel> clarificationLabel = new ArrayList<JLabel>();
    
    
    public VgClarificationPanel2(ViewDex viewdex, AppProperty appproperty, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.appProperty = appproperty;
        this.vgHistory = vghistory;
        
        createUI();
    }
    
    private void createUI(){
         /** Get the user definition list */
        ArrayList<VgTaskPanelClarification> list = vgHistory.getTaskPanelClarificationList();
        
        createLayout();
        createMainPanel();
        createClarificationMainPanel(list);
        createClarificationSubPanel(list);
        createClarificationLabelPanel(list);
        createClarificationHeadLabel(list);
        createClarificationLabel(list);
        addPanels(list);
    }
    
    /**
     * Create the layout.
     */
    private void createLayout(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        double size[][] = {{f}, {0,f,0}};
        this.setLayout(new TableLayout(size));
        //this.setBackground(Color.GREEN);
    }
    
    /**
     * Create the mainPanel and the scrollPane.
     */
    private void createMainPanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(235,230,185));
        //double size[][] = {{f},{2,f,5,f,1,30,2,41,f}};
        double size[][] = {{f},{0,p,0}};
        mainPanel.setLayout(new TableLayout(size));
        //TitledBorder tborder = BorderFactory.createTitledBorder("  VGA ");
        //tborder.setTitleJustification(TitledBorder.LEFT);
        //tborder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        //mainPanel.setBorder(tborder);
        
        scrollPane = new JScrollPane();
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //scrollPane.setLayout();
        //scrollPane.setViewportView(mainPanel);
    }
    
    /**
     * Create the ClarificationMainPanel.
     */
    private void createClarificationMainPanel(ArrayList<VgTaskPanelClarification> list){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        //double[][] size = {{3,200,2},{10,35,10,35,10}};
        double[][] size = new double[2][15];
        //for(int i=0; i<=5; i++)
        //size[][i]= new double[i+1];
        
        /** Create the criterial panel */
        clarificationMainPanel = new JPanel();
        clarificationMainPanel.setBackground(new Color(223,230,185));
        
        /** Create the Tablelayout */
        if(list.size() == 1){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
        }
        
        if(list.size() == 2){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
        }
        
        if(list.size() == 3){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
            size[1][4] = (double) 3;
            size[1][5] = p;
        }
        
        if(list.size() == 4){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
            size[1][4] = (double) 3;
            size[1][5] = p;
            size[1][6] = (double) 3;
            size[1][7] = p;
        }
        
        if(list.size() == 5){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
            size[1][4] = (double) 3;
            size[1][5] = p;
            size[1][6] = (double) 3;
            size[1][7] = p;
            size[1][8] = (double) 3;
            size[1][9] = p;
        }
        
        if(list.size() == 6){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
            size[1][4] = (double) 3;
            size[1][5] = p;
            size[1][6] = (double) 3;
            size[1][7] = p;
            size[1][8] = (double) 3;
            size[1][9] = p;
            size[1][10] = (double) 3;
            size[1][11] = p;
        }
        
        if(list.size() == 7){
            size[0][0] = (double) 3;
            size[0][1] = f;
            size[0][2] = (double) 3;
            size[1][0] = (double) 3;
            size[1][1] = p;
            size[1][2] = (double) 3;
            size[1][3] = p;
            size[1][4] = (double) 3;
            size[1][5] = p;
            size[1][6] = (double) 3;
            size[1][7] = p;
            size[1][8] = (double) 3;
            size[1][9] = p;
            size[1][10] = (double) 3;
            size[1][11] = p;
            size[1][12] = (double) 3;
            size[1][13] = p;
        }
        
        double[][] size2 = {{3,200,3},{10,35,10,35,10}};
        clarificationMainPanel.setLayout(new TableLayout(size));
    }
    
     /**
      * Create the clarification sub panels.
     */
    private void createClarificationSubPanel(ArrayList<VgTaskPanelClarification> list){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        /** Create the Tablelayout */
        double size[][] = {{30, f},{f}};
        
        for(int i=0; i < list.size(); i++){
            clarificationSubPanel.add(new JPanel());
            clarificationSubPanel.get(i).setLayout(new TableLayout(size));
            //clarificationSubPanel.get(i).setLayout(new GridLayout(1, 2));
            clarificationSubPanel.get(i).setBackground(new Color(235,230,185));
            //criterialPanel[i].setBorder(blackLine);
            //criterialPanel[i].setBackground(Color.GREEN);
            //criterialPanel[i].setLayout(new TableLayout(size));
            
            
        }
    }
    
     /**
      * Create the clarification label panels.
     */
    private void createClarificationLabelPanel(ArrayList<VgTaskPanelClarification> list){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        /** Create the Tablelayout */
        double size[][] = {{30, f},{f}};
        
        for(int i=0; i < list.size(); i++){
            clarificationLabelPanel.add(new JPanel());
            //clarificationSubPanel.get(i).setLayout(new TableLayout(size));
            clarificationLabelPanel.get(i).setLayout(new GridLayout(1, 1));
            clarificationLabelPanel.get(i).setBackground(new Color(235,230,185));
            //criterialPanel[i].setBorder(blackLine);
            //criterialPanel[i].setBackground(Color.GREEN);
            //criterialPanel[i].setLayout(new TableLayout(size));
            
            
        }
    }
    
     /**
      * Create the clarification head labels.
      */
    private void createClarificationHeadLabel(ArrayList<VgTaskPanelClarification> list){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        /* Create the Tablelayout */
        double size[][] = {{f},{f}};
        
        for(int i=0; i < list.size(); i++){
            clarificationHeadLabel.add(new JLabel());
            clarificationHeadLabel.get(i).setVerticalTextPosition(JLabel.BOTTOM);
            //clarificationHeadLabel.get(i).setLayout(new TableLayout(size));
            clarificationHeadLabel.get(i).setFont(new Font("SansSerif", Font.PLAIN, 16));
            String str = list.get(i).getClarificationHeadText() + ":";
            clarificationHeadLabel.get(i).setText("<html>" + str);
        }
    }
    
     /**
      * Create the clarification labels.
      */
    private void createClarificationLabel(ArrayList<VgTaskPanelClarification> list){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        /* Create the Tablelayout */
        double size[][] = {{f},{f}};
        
        for(int i=0; i < list.size(); i++){
            clarificationLabel.add(new JLabel());
            //clarificationLabel.get(i).setLayout(new TableLayout(size));
            //clarificationLabel.get(i).setLayout(new GridLayout(1, 1));
            //clarificationLabel.get(i).setForeground(Color.MAGENTA);
            clarificationLabel.get(i).setFont(new Font("SansSerif", Font.PLAIN, 14));
            String str = list.get(i).getClarificationText();
            clarificationLabel.get(i).setText("<html>" + str);
        }
    }
    
    /**
     * Add the panels.
     */
    private void addPanels(ArrayList<VgTaskPanelClarification> list){
        
         /* Add the clarificationLabelPanel to the clarificationSubPanel.
         */
        for(int i = 0; i < clarificationSubPanel.size(); i++){
            clarificationSubPanel.get(i).add(clarificationLabelPanel.get(i), "1,0");
        }
        
        /* Add the clarificationLabel to the clarificationLabelPanel.
         */
        for(int i = 0; i < clarificationLabelPanel.size(); i++){
            clarificationLabelPanel.get(i).add(clarificationLabel.get(i), "1");
        }
        
        /* Add the clarificationHeadPanel and the clarificationLabel
         * to the clarificationSubPanel.
         */
        
        for(int i = 0; i < clarificationSubPanel.size(); i++){
            clarificationSubPanel.get(i).add(clarificationHeadLabel.get(i), "0,0");
        }
        
        /* Add the clarificationHeadPanel and the clarificationLabel
         * to the clarificationSubPanel.
         */
        /*
        for(int i = 0; i < clarificationSubPanel.size(); i++){
            clarificationSubPanel.get(i).add(clarificationHeadLabel.get(i), "0,0");
            clarificationSubPanel.get(i).add(clarificationLabel.get(i), "1,0");
        }*/
        
        /* Add clarificationSubPanel to the clarificationMainPanel.
         */
        int cnt = 1;
        for(int i = 0; i < clarificationSubPanel.size(); i++){
            String str = Integer.toString(cnt);
            String str2 = "1," + str;
            clarificationMainPanel.add(clarificationSubPanel.get(i), str2);
            cnt = cnt + 2;
        }
        
        /* Add the clarificationMainPanel to the mainPanel.
         */
        mainPanel.add(clarificationMainPanel, "0,1");
        //this.add(scrollPane, "0,1");
        this.add(mainPanel, "0,1");
    }
}
