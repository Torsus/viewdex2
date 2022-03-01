/* @(#) VgTaskPanel.java 05/27/2005
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
import java.awt.Toolkit;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import mft.vdex.app.AppProperty;
import mft.vdex.app.ViewDex;
import mft.vdex.ds.StudyDbImageNode;
import mft.vdex.ds.StudyDbStackNode;
import mft.vdex.app.AppPropertyUtils;

/**
 * The <code>VgTaskPanel</code> class creates the GUI
 * for the VG-study.
 */
public class VgTaskPanel extends JPanel implements KeyListener, MouseListener, FocusListener {

    private AppPropertyUtils propUtils;
    private String studyName;
    private Properties prop;
    private Border black, blackLine, blackLineBold, greyLine, empty;
    private Border blackLineLoweredEtched;
    private JPanel mainPanel, taskMainPanel;
    private ArrayList<JPanel> taskPanel = new ArrayList<JPanel>();
    private ArrayList<JPanel> taskTextPanel = new ArrayList<JPanel>();
    private ArrayList<JPanel> taskCheckBoxPanel = new ArrayList<JPanel>();
    private JPanel[][] taskCheckBoxSubPanel;
    private JPanel[][] taskCheckBoxLabelPanel;
    private JLabel[][] taskCheckBoxLabel;
    private ViewDex viewDex;
    private AppProperty appProperty;
    private VgHistory history;
    double f = TableLayout.FILL;
    double p = TableLayout.PREFERRED;
    private int[] checkBoxColor;
    private int[] checkBoxSelectColor;
    private JScrollPane scrollPane;

    /**
     * Creates a new instance of a VG Study.
     */
    public VgTaskPanel(ViewDex viewdex, AppProperty appproperty, VgHistory vghistory) {
        this.viewDex = viewdex;
        this.appProperty = appproperty;
        this.history = vghistory;
        propUtils = new AppPropertyUtils();

        studyName = history.getStudyName();
        prop = history.getVgProperties();

        init();
        createUI();
    }

    private void createUI() {

        /** Get the user definition list */
        //ArrayList<CriterialDefClass> defList = xMedicalViewer.getUserDefList();
        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        createLayout();
        createBorders();
        createMainPanel();
        createTaskMainPanel(list);
        createTaskPanel(list);
        createTaskTextPanel2(list);
        createTaskCheckBoxPanel(list);
        createScrollPane();

        scrollPane.setViewportView(taskMainPanel);


        //JLabel criterialText = createCriterialTextComponent(defList, 0);
        //createCriterialScoreComponent();


        //createCriterialDefinitionPanel();
        //createCriterialDefinitionPanel();
        //createCriterialDefScoreComponent();

        //createStudySelectionCountPanel();
        //createStudySelectionPanel();
        //createDisplayModePanel();
        //createWindowLevelPanel();
        addPanels(list);
    }

    /*
     * init
     */
    private void init() {

        // checkbox color
        String key = "taskpanel.checkbox.color";
        checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
            checkBoxColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // checkbox select color
        key = "taskpanel.checkbox.select.color";
        checkBoxSelectColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxSelectColor[0] == 0 && checkBoxSelectColor[1] == 0 && checkBoxSelectColor[2] == 0) {
            checkBoxSelectColor[0] = AppPropertyUtils.defCheckBoxSelectColor[0];
            checkBoxSelectColor[1] = AppPropertyUtils.defCheckBoxSelectColor[1];
            checkBoxSelectColor[2] = AppPropertyUtils.defCheckBoxSelectColor[2];
        }
    }

    /**
     * Create the layout.
     */
    private void createLayout() {
        //double size[][] = {{f}, {8,f,2,40,25}};
        double size[][] = {{f}, {0, f, 0}};
        this.setLayout(new TableLayout(size));
        //this.setBackground(Color.GREEN);
    }

    /**
     * Create the main panel.
     */
    private void createMainPanel() {
        // panel color
        String key = "taskpanel.color";
        int[] taskPanelColor = propUtils.getPropertyColorValue(prop, key);
        if (taskPanelColor[0] == 0 && taskPanelColor[1] == 0 && taskPanelColor[2] == 0) {
            taskPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            taskPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            taskPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(taskPanelColor[0], taskPanelColor[1], taskPanelColor[2]));
        //mainPanel.setBackground(new Color(255,0,0));
        double size[][] = {{f}, {0, p, 0}};
        mainPanel.setLayout(new TableLayout(size));
    }

    /**
     * Create the taskMainPanel.
     */
    private void createTaskMainPanel(ArrayList<VgTaskPanelQuestion> list) {
        // panel color
        String key = "taskpanel.mainpanel.color";
        int[] mainPanelColor = propUtils.getPropertyColorValue(prop, key);
        if (mainPanelColor[0] == 0 && mainPanelColor[1] == 0 && mainPanelColor[2] == 0) {
            mainPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            mainPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            mainPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // Create the criterial panel
        taskMainPanel = new JPanel();
        taskMainPanel.setBackground(new Color(mainPanelColor[0], mainPanelColor[1], mainPanelColor[2]));
        //taskMainPanel.setBackground(new Color(0,255,0));

        double[][] size = createMainPanelTableLayoutArray(list);
        taskMainPanel.setLayout(new TableLayout(size));
    }

    /**
     * Create a double array according to the following
     *
     *  if(list.size() == 1) {
     *    size[0][0] = (double) 0;
     *    size[0][1] = f;
     *    size[0][2] = (double) 0;
     *    size[1][0] = (double) 0;
     *    size[1][1] = p;
     *    size[1][2] = (double) 0;
     *  }
     *
     *   if (list.size() == 2) {
     *       size[0][0] = (double) 0;
     *      size[0][1] = f;
     *      size[0][2] = (double) 0;
     *      size[1][0] = (double) 0;
     *      size[1][1] = p;
     *      size[1][2] = (double) 7;
     *      size[1][3] = p;
     *      size[1][4] = (double) 0;
     *  }
     * 
     *  if (list.size() == 3) {
     *      size[0][0] = (double) 0;
     *      size[0][1] = f;
     *      size[0][2] = (double) 0;
     *      size[1][0] = (double) 0;
     *      size[1][1] = p;
     *      size[1][2] = (double) 7;
     *      size[1][3] = p;
     *      size[1][4] = (double) 7;
     *      size[1][5] = p;
     *      size[1][6] = (double) 0;
     *  }
     *
     *  if (list.size() == 4) {
     *      size[0][0] = (double) 0;
     *       size[0][1] = f;
     *      size[0][2] = (double) 0;
     *      size[1][0] = (double) 0;
     *      size[1][1] = p;
     *      size[1][2] = (double) 7;
     *      size[1][3] = p;
     *      size[1][4] = (double) 7;
     *      size[1][5] = p;
     *      size[1][6] = (double) 7;
     *      size[1][7] = p;
     *      size[1][8] = (double) 0;
     *  }
     * 
     *  and so on ...
     */
    double[][] createMainPanelTableLayoutArray(ArrayList<VgTaskPanelQuestion> list) {
        int cnt = list.size();
        int cnt2 = (cnt * 2) + 1;
        int cnt3 = cnt2 + 3;
        double[][] array = new double[2][cnt2];

        // 2015-03-31
        // The right margin value have change from 0->15 to give space for the scrollbar.
        // The left margin was also adjusted from 0->5, for better appearance.
        if (list.size() > 0) {
            array[0][0] = (double) 5;
            array[0][1] = f;
            array[0][2] = (double) 15;
            array[1][0] = 0;
        }

        if (list.size() == 1) {
            for (int i = 0; i < list.size(); i++) {
                array[1][i * 2 + 1] = p;
            }
        } else {
            int a = 0;
            for (int i = 0; i < list.size(); i++) {
                array[1][i * 2 + 1] = p;
                array[1][i * 2 + 2] = (double) 7;
                a = (i * 2 + 2);
            }
            array[1][a] = 0;
        }

        return array;
    }

    /**
     * Create the taskpanel.
     * @param list
     */
    private void createTaskPanel(ArrayList<VgTaskPanelQuestion> list) {
        // Initialize the array.
        //int cnt = list.size();
        //criterialPanel = new JPanel[cnt];

        /** Create the Tablelayout */
        double size[][] = {{f}, {f, p}};

        for (int i = 0; i < list.size(); i++) {
            taskPanel.add(new JPanel());
            taskPanel.get(i).setLayout(new TableLayout(size));
            //criterialPanel[i].setBorder(blackLine);
            //taskPanel.get(i).setBackground(Color.GREEN);
            //criterialPanel[i].setLayout(new TableLayout(size));
        }
    }

    /**
     * Create the taskTextPanel.
     */
    private void createTaskTextPanel(ArrayList<VgTaskPanelQuestion> list) {
        String key;

        /** Create the Tablelayout */
        double size[][] = {{f}, {f}};

        // panel color
        key = "taskpanel.textpanel.color";
        int[] textPanelColor = propUtils.getPropertyColorValue(prop, key);
        if (textPanelColor[0] == 0 && textPanelColor[1] == 0 && textPanelColor[2] == 0) {
            textPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            textPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            textPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // text color
        key = "taskpanel.task.text.color";
        int[] taskTextColor = propUtils.getPropertyColorValue(prop, key);
        if (taskTextColor[0] == 0 && taskTextColor[1] == 0 && taskTextColor[2] == 0) {
            taskTextColor[0] = AppPropertyUtils.defTextColor[0];
            taskTextColor[1] = AppPropertyUtils.defTextColor[1];
            taskTextColor[2] = AppPropertyUtils.defTextColor[2];
        }

        for (int i = 0; i < list.size(); i++) {
            taskTextPanel.add(new JPanel());
            //taskTextPanel.get(i).setLayout(new TableLayout(size));
            //taskTextPanel.get(i).setLayout(new FlowLayout(FlowLayout.LEFT));
            taskTextPanel.get(i).setLayout(new GridLayout(1, 1));
            taskTextPanel.get(i).setBackground(
                    new Color(textPanelColor[0], textPanelColor[1], textPanelColor[2]));


            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String taskText = def.getTaskText();
            String taskTextFont = def.getTaskTextFont();
            JLabel label = new JLabel();
            label.setFont(Font.decode(taskTextFont));
            //label.setForeground(Color.red);
            label.setForeground(new Color(taskTextColor[0], taskTextColor[1], taskTextColor[2]));
            //label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setText("<html>" + taskText);
            //taskTextPanel.get(i).add(label, "0,0");
            taskTextPanel.get(i).add(label, "1");
        }
    }

    /**
     * Create the taskTextPanel.
     */
    // 2015-03-31
    // This method is replaced because the JLabel component can only have one
    // line of text. When using a JEditorPane text component and when the scrollPane
    // is visible (in the complex layout of panels that is used), the word wrap
    // functin is no longer working. This problem is well known and documented.
    // The JTextArea works fine. I have added a new value for the left and right
    // margins, in the TaskMainPanel layout. The new right margin value give
    // enough space for the scrollbar.
    private void createTaskTextPanel2(ArrayList<VgTaskPanelQuestion> list) {
        String key;

        /** Create the Tablelayout */
        double size[][] = {{f}, {f}};

        // panel color
        key = "taskpanel.textpanel.color";
        int[] textPanelColor = propUtils.getPropertyColorValue(prop, key);
        if (textPanelColor[0] == 0 && textPanelColor[1] == 0 && textPanelColor[2] == 0) {
            textPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            textPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            textPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // text color
        key = "taskpanel.task.text.color";
        int[] taskTextColor = propUtils.getPropertyColorValue(prop, key);
        if (taskTextColor[0] == 0 && taskTextColor[1] == 0 && taskTextColor[2] == 0) {
            taskTextColor[0] = AppPropertyUtils.defTextColor[0];
            taskTextColor[1] = AppPropertyUtils.defTextColor[1];
            taskTextColor[2] = AppPropertyUtils.defTextColor[2];
        }

        for (int i = 0; i < list.size(); i++) {
            taskTextPanel.add(new JPanel());
            taskTextPanel.get(i).setLayout(new GridLayout(1, 1));
            taskTextPanel.get(i).setBackground(
                    new Color(textPanelColor[0], textPanelColor[1], textPanelColor[2]));
            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String taskText = def.getTaskText();
            String taskTextFont = def.getTaskTextFont();
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(new Color(textPanelColor[0], textPanelColor[1], textPanelColor[2]));
            textArea.setFont(Font.decode(taskTextFont));
            textArea.setForeground(new Color(taskTextColor[0], taskTextColor[1], taskTextColor[2]));
            textArea.setText(taskText);
            taskTextPanel.get(i).add(textArea, "1");
        }
    }

    /**
     * Create the taskCheckBoxPanel.
     */
    private void createTaskCheckBoxPanel(ArrayList<VgTaskPanelQuestion> list) {
        double[][] size = new double[2][11];

        // Initialize the arrays.
        taskCheckBoxSubPanel = new JPanel[list.size()][100];
        taskCheckBoxLabelPanel = new JPanel[list.size()][100];
        taskCheckBoxLabel = new JLabel[list.size()][100];

        // checkbox color
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
            checkBoxColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // checkbox panel color
        key = "taskpanel.checkbox.panel.color";
        int[] checkBoxPanelColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxPanelColor[0] == 0 && checkBoxPanelColor[1] == 0 && checkBoxPanelColor[2] == 0) {
            checkBoxPanelColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxPanelColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxPanelColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        for (int i = 0; i < list.size(); i++) {
            taskCheckBoxPanel.add(new JPanel());
            taskCheckBoxPanel.get(i).setBackground(new Color(
                    checkBoxPanelColor[0], checkBoxPanelColor[1], checkBoxPanelColor[2]));
            //taskCheckBoxPanel.get(i).setBackground(Color.MAGENTA);

            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String[] taskCheckBoxText = def.getCheckBoxText();
            String taskCheckBoxTextFont = def.getCheckBoxTextFont();

            // Create the Tablelayout.
            // *** NOT IN USE ***
            if (taskCheckBoxText.length == 1) {
                size[0][0] = f;
                size[0][1] = (double) 37;
                size[0][2] = f;
                size[1][0] = (double) 0;
                size[1][1] = p;
                size[1][2] = (double) 0;
            }

            if (taskCheckBoxText.length == 2) {
                size[0][0] = f;
                size[0][1] = (double) 37;
                size[0][2] = f;
                size[0][3] = (double) 37;
                size[0][4] = f;
                size[1][0] = (double) 0;
                size[1][1] = p;
                size[1][2] = (double) 0;
            }

            if (taskCheckBoxText.length == 3) {
                size[0][0] = f;
                size[0][1] = (double) 37;
                size[0][2] = f;
                size[0][3] = (double) 37;
                size[0][4] = f;
                size[0][5] = (double) 37;
                size[0][6] = f;
                size[1][0] = (double) 0;
                size[1][1] = p;
                size[1][2] = (double) 0;
            }

            if (taskCheckBoxText.length == 4) {
                size[0][0] = f;
                size[0][1] = (double) 37;
                size[0][2] = f;
                size[0][3] = (double) 37;
                size[0][4] = f;
                size[0][5] = (double) 37;
                size[0][6] = f;
                size[0][7] = (double) 37;
                size[0][8] = f;
                size[1][0] = (double) 0;
                size[1][1] = p;
                size[1][2] = (double) 0;
            }

            if (taskCheckBoxText.length == 5) {
                size[0][0] = f;
                size[0][1] = p;
                size[0][2] = f;
                size[0][3] = p;
                size[0][4] = f;
                size[0][5] = p;
                size[0][6] = f;
                size[0][7] = p;
                size[0][8] = f;
                size[0][9] = p;
                size[0][10] = f;
                size[1][0] = 0;
                size[1][1] = p;
                size[1][2] = 0;
            }

            //criterialRatingPanel[i].setLayout(new TableLayout(size));
            //taskCheckBoxPanel.get(i).setLayout(new TableLayout(size));

            // Fill whith zeros.
            for (int k = 0; k < 2; k++) {
                for (int l = 0; l < 11; l++) {
                    size[k][l] = (double) 0;
                }
            }

            // taskCheckBoxSubPanel
            key = "taskpanel.color";
            int[] taskPanelColor = propUtils.getPropertyColorValue(prop, key);
            if (taskPanelColor[0] == 0 && taskPanelColor[1] == 0 && taskPanelColor[2] == 0) {
                taskPanelColor[0] = AppPropertyUtils.defPanelColor[0];
                taskPanelColor[1] = AppPropertyUtils.defPanelColor[1];
                taskPanelColor[2] = AppPropertyUtils.defPanelColor[2];
            }

            // taskCheckBoxLabelPanel color
            key = "taskpanel.checkbox.text.color";
            int[] checkBoxTextColor = propUtils.getPropertyColorValue(prop, key);
            if (checkBoxTextColor[0] == 0 && checkBoxTextColor[1] == 0 && checkBoxTextColor[2] == 0) {
                checkBoxTextColor[0] = AppPropertyUtils.defTextColor[0];
                checkBoxTextColor[1] = AppPropertyUtils.defTextColor[1];
                checkBoxTextColor[2] = AppPropertyUtils.defTextColor[2];
            }

            // border color
            key = "taskpanel.checkbox.border.color";
            int[] borderColor = propUtils.getPropertyColorValue(prop, key);
            if (borderColor[0] == 0 && borderColor[1] == 0 && borderColor[2] == 0) {
                borderColor[0] = AppPropertyUtils.defCheckBoxBorderColor[0];
                borderColor[1] = AppPropertyUtils.defCheckBoxBorderColor[1];
                borderColor[2] = AppPropertyUtils.defCheckBoxBorderColor[2];
            }
            Border border = BorderFactory.createLineBorder(new Color(borderColor[0], borderColor[1], borderColor[2]));

            // checkBoxSize
            key = "taskpanel.checkbox.size";
            int[] checkBoxSize = propUtils.getPropertySizeValue(prop, key);
            if (checkBoxSize[0] == 0 && checkBoxSize[1] == 0) {
                checkBoxSize[0] = AppPropertyUtils.defTaskPanelCheckBoxSize[0];
                checkBoxSize[1] = AppPropertyUtils.defTaskPanelCheckBoxSize[1];
            }

            // taskPanelCheckBoxHorizontalAlignemt
            int checkBoxHorizontalAlignment = 0;
            key = "taskpanel.checkbox.horizontal.alignment";
            checkBoxHorizontalAlignment = propUtils.getPropertyIntegerValue(prop, key);
            if (checkBoxHorizontalAlignment == 0) {
                checkBoxHorizontalAlignment = 6;
            }


            // checkBoxVerticalAlignmentTop
            int checkBoxVerticalAlignmentTop = 0;
            key = "taskpanel.checkbox.vertical.alignment.top";
            checkBoxVerticalAlignmentTop = propUtils.getPropertyIntegerValue(prop, key);
            if (checkBoxVerticalAlignmentTop == 0) {
                checkBoxVerticalAlignmentTop = 6;
            }


            // checkBoxVerticalAlignmentBottom
            int checkBoxVerticalAlignmentBottom = 0;
            key = "taskpanel.checkbox.vertical.alignment.bottom";
            checkBoxVerticalAlignmentBottom = propUtils.getPropertyIntegerValue(prop, key);


            /* Create the checkBox subpanels */
            for (int j = 0; j < taskCheckBoxText.length; j++) {
                /* sub panel */
                taskCheckBoxSubPanel[i][j] = new JPanel();
                taskCheckBoxSubPanel[i][j].addMouseListener(this);
                taskCheckBoxSubPanel[i][j].setBackground(
                        new Color(checkBoxPanelColor[0], checkBoxPanelColor[1], checkBoxPanelColor[2]));

                // Create the Check Box subPanel.
                // Find how many characters there are to display and
                // set the width of the tabelLayout size.
                int[] layoutSize = getLayoutSize(taskCheckBoxText.length,
                        taskCheckBoxText[j].length());

                // This is the place where you can adjust the spaces
                // between the different tasks.
                double[][] size2 = {{layoutSize[0], layoutSize[1], layoutSize[0]}, {1, layoutSize[2], 1}};
                taskCheckBoxSubPanel[i][j].setLayout(new TableLayout(size2));

                //sune
                //double[][] size2 = {{layoutSize[0], layoutSize[1], layoutSize[0]},{1, layoutSize[2], 1}};
                double[][] size4 = {{checkBoxHorizontalAlignment,
                        checkBoxSize[0],
                        checkBoxHorizontalAlignment
                    },
                    {checkBoxVerticalAlignmentTop,
                        checkBoxSize[1],
                        checkBoxVerticalAlignmentBottom
                    }
                };

                taskCheckBoxSubPanel[i][j].setLayout(new TableLayout(size4));

                // taskCheckBoxLabelPanel
                double[][] size3 = {{f}, {f}};
                taskCheckBoxLabelPanel[i][j] = new JPanel();
                taskCheckBoxLabelPanel[i][j].setBackground(
                        new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));

                taskCheckBoxLabelPanel[i][j].setLayout(new TableLayout(size3));
                taskCheckBoxLabelPanel[i][j].setBorder(border);

                // taskCheckBoxLabel
                taskCheckBoxLabel[i][j] = new JLabel();
                taskCheckBoxLabel[i][j].setText(taskCheckBoxText[j]);
                taskCheckBoxLabel[i][j].setFont(Font.decode(taskCheckBoxTextFont));
                taskCheckBoxLabel[i][j].setForeground(
                        new Color(checkBoxTextColor[0], checkBoxTextColor[1], checkBoxTextColor[2]));
                taskCheckBoxLabel[i][j].setVerticalAlignment(JLabel.CENTER);
                taskCheckBoxLabel[i][j].setHorizontalAlignment(JLabel.CENTER);

                /* add */
                /*
                criterialRatingLabelPanel[i][j].add(criterialRatingLabel[i][j], "0,0");
                criterialRatingSubPanel[i][j].add(criterialRatingLabelPanel[i][j], "1,1");
                 */
            }

            /** Add the subpanels */
            /*
            int cntA = 1;
            for(int k = 0; k < criterialRatingSubPanel.length; k++){
            String str = Integer.toString(cntA);
            String str2 = str + ",1";
            criterialRatingPanel[i].add(criterialRatingSubPanel[j][k], str2);
            cntA = cntA + 2;
            }*/
        }
    }

    /**
     *
     */
    private void createScrollPane() {
        // scrollPane
        scrollPane = new JScrollPane();
        //scrollPane.setBackground(new Color(color[0], color[1], color[2]));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        //scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        //scrollPane.setPreferredSize(new Dimension(200, 150));
        //scrollPane.setMinimumSize(new Dimension(300, 100));
        //scrollPane.setMaximumSize(new Dimension(300,50));
        //scrollPane.setForeground(new Color(223,230,185));
        //Border empty = BorderFactory.createEmptyBorder();
        Border empty = BorderFactory.createEmptyBorder();
        scrollPane.setBorder(empty);
        //scrollPane.setAutoscrolls(false);
        //scrollPane.setViewportView(mainPanel);
    }

    /**
     * Add the panels.
     */
    private void addPanels(ArrayList<VgTaskPanelQuestion> list) {

        /* Add the taskTextPanel
         *         taskRatingPanel
         *         taskRatingLabel
         *         taskRatingLabelPanel
         */
        for (int i = 0; i < taskPanel.size(); i++) {
            taskPanel.get(i).add(taskTextPanel.get(i), "0,0");
            taskPanel.get(i).add(taskCheckBoxPanel.get(i), "0,1");

            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String[] ratingsLabelsText = def.getCheckBoxText();

            int cnt1 = 1;
            for (int j = 0; j < ratingsLabelsText.length; j++) {
                taskCheckBoxLabelPanel[i][j].add(taskCheckBoxLabel[i][j], "0,0");
                taskCheckBoxSubPanel[i][j].add(taskCheckBoxLabelPanel[i][j], "1,1");

                String str = Integer.toString(cnt1);
                String str2 = str + ",1";
                taskCheckBoxPanel.get(i).add(taskCheckBoxSubPanel[i][j], str2);
                cnt1 = cnt1 + 2;
            }
        }

        /* Add the criterialPanel */
        int cnt = 1;
        for (int i = 0; i < taskPanel.size(); i++) {
            String str = Integer.toString(cnt);
            String str2 = "1," + str;
            taskMainPanel.add(taskPanel.get(i), str2);
            cnt = cnt + 2;
        }

        /* Add the criterialMainPanel
         *         mainPanel
         */
        mainPanel.add(taskMainPanel, "0,1");
        //mainPanel.add(clarificationPanel, "0,3");
        scrollPane.setViewportView(mainPanel);
        this.add(scrollPane, "0,1");
        //this.add(mainPanel, "0,1");
    }

    /**
     * Set the checkbox init color for all task's.
     */
    public void setRatingInitStateAll() {
        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        // checkbox color
        /*
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
        checkBoxColor[0] = PropertiesUtils.defPanelColor[0];
        checkBoxColor[1] = PropertiesUtils.defPanelColor[1];
        checkBoxColor[2] = PropertiesUtils.defPanelColor[2];
        }*/

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String[] ratingsLabelsText = def.getCheckBoxText();

            for (int j = 0; j < ratingsLabelsText.length; j++) {
                taskCheckBoxLabelPanel[i][j].setBackground(
                        new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
            }
        }
    }

    /**
     * Set the checkbox init color for task's with the property
     * 'taskpaneX.taskX.localization = y'.
     */
    public void setRatingInitStateLocalization() {
        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        // checkbox color
        /*
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
        checkBoxColor[0] = PropertiesUtils.defPanelColor[0];
        checkBoxColor[1] = PropertiesUtils.defPanelColor[1];
        checkBoxColor[2] = PropertiesUtils.defPanelColor[2];
        }*/

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelQuestion item = (VgTaskPanelQuestion) list.get(i);
            String[] ratingsLabelsText = item.getCheckBoxText();

            if (item.getLocalizationTaskStatus()) {
                for (int j = 0; j < ratingsLabelsText.length; j++) {
                    taskCheckBoxLabelPanel[i][j].setBackground(
                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                }
            }
        }
    }

    /**
     * Set the checkbox init color for task's with the property
     * 'taskpaneX.taskX.localization = n'.
     */
    public void setRatingInitStateNoLocalization() {
        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        // checkbox color
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
            checkBoxColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelQuestion item = (VgTaskPanelQuestion) list.get(i);
            String[] ratingsLabelsText = item.getCheckBoxText();

            if (!item.getLocalizationTaskStatus()) {
                for (int j = 0; j < ratingsLabelsText.length; j++) {
                    taskCheckBoxLabelPanel[i][j].setBackground(
                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                }
            }
        }
    }

    /**
     * Set the checkbox init color for Task 1 & 2,
     * "Type of lesion" & "Visibility settings".
     * NOT IN USE
     */
    public void setRatingInitStateTask1And2() {
        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        // checkbox color
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
            checkBoxColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelQuestion def = (VgTaskPanelQuestion) list.get(i);
            String[] ratingsLabelsText = def.getCheckBoxText();

            if ((i == 0) || (i == 1)) {
                for (int j = 0; j < ratingsLabelsText.length; j++) {
                    taskCheckBoxLabelPanel[i][j].setBackground(
                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                }
            }
        }
    }

    /**
     * Test
     */
    public void updateRatingValues_TEST() {
        ArrayList<VgTaskPanelResult> resultList = viewDex.vgTaskPanelUtility.getTaskPanelResultList();
        printTaskPanelResultList(resultList, "100");

        // Create a str for items with NO localization.
        // Sort localization == false
        ArrayList<VgTaskPanelResult> list20 = sortResultListLocalization(resultList, false);
        printTaskPanelResultList(list20, "201");

    }

    /**
     * Sort the <code>VgTaskPanelResult</code> list according to the localizationStatus.
     * @param ArrayList<VgTaskPanelResult> resultList
     * @param localization if <code>true</code> return a list with items localizationStatus == true, if <code>false</code> return a list with items localizationStatus == false.
     * @return a sorted <code>ArrayList<VgTaskPanelResult></code> list.
     */
    private ArrayList<VgTaskPanelResult> sortResultListLocalization(ArrayList<VgTaskPanelResult> resultList, boolean localization) {
        ArrayList<VgTaskPanelResult> list = new ArrayList<VgTaskPanelResult>();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult node = resultList.get(i);
            if ((node.getLocalizationStatus() && localization)
                    || (!node.getLocalizationStatus() && !localization)) {
                list.add(node);
            }
        }
        return list;
    }

    /**
     * Print
     */
    public void printTaskPanelResultList(ArrayList<VgTaskPanelResult> list, String str) {
        VgTaskPanelResult node;
        String locStr, str2;

        System.out.println("Print: " + str);
        for (int i = 0; i < list.size(); i++) {
            node = list.get(i);

            if (node.getLocalizationStatus()) {
                locStr = "T";
                str2 =
                        "itemCnt = " + node.getItemCnt() + ", "
                        + "imageNodeCnt = " + node.getImageNodeCnt() + ", "
                        + "locStatus = " + locStr + ", "
                        + "point.x = " + Math.round(node.getPoint().getX()) + ", "
                        + "point.y = " + Math.round(node.getPoint().getY()) + ", "
                        + "taskNb = " + node.getTaskNb() + ", "
                        + "selItem = " + node.getSelItem() + ", ";
            } else {
                locStr = "F";
                str2 =
                        "itemCnt = " + node.getItemCnt() + ", "
                        + "imageNodeCnt = " + node.getImageNodeCnt() + ", "
                        + "locStatus = " + locStr + ", "
                        + //"point.x = " + ", " +
                        //"point.y = " + ", " +
                        "taskNb = " + node.getTaskNb() + ", "
                        + "selItem = " + node.getSelItem() + ", ";
            }
            System.out.println(str2);
        }
        System.out.println("");
    }

    /**
     * Set the rating values (answers) for the task that are defined
     * as NOT localized.
     */
    public void setRatingValuesNotLocalized() {
        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<VgTaskPanelResult> resultList = stackNode.getTaskPanelResultList();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult result = resultList.get(i);
            int taskNb = result.getTaskNb();
            int selItem = result.getSelItem();
            boolean locStatus = result.getLocalizationStatus();

            if (!locStatus && selItem != -1) {
                taskCheckBoxLabelPanel[taskNb][selItem].setBackground(
                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1],
                        checkBoxSelectColor[2]));
            }
        }
    }

    /**
     * Set the rating values (answers) for the task that are defined
     * as NOT localized.
     */
    public void setRatingValuesNotLocalizedOLD() {
        // color
        /*
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
        checkBoxColor[0] = PropertiesUtils.defPanelColor[0];
        checkBoxColor[1] = PropertiesUtils.defPanelColor[1];
        checkBoxColor[2] = PropertiesUtils.defPanelColor[2];
        }

        // color
        key = "taskpanel.checkbox.select.color";
        int[] checkBoxSelectColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxSelectColor[0] == 0 && checkBoxSelectColor[1] == 0 && checkBoxSelectColor[2] == 0) {
        checkBoxSelectColor[0] = PropertiesUtils.defCheckBoxSelectColor[0];
        checkBoxSelectColor[1] = PropertiesUtils.defCheckBoxSelectColor[1];
        checkBoxSelectColor[2] = PropertiesUtils.defCheckBoxSelectColor[2];
        }*/

        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();

        StudyDbStackNode stackNode = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedStackNode();
        ArrayList<StudyDbImageNode> imageNodeList = stackNode.getImageNodeList();
        Iterator<StudyDbImageNode> iter = imageNodeList.iterator();

        while (iter.hasNext()) {
            StudyDbImageNode imageNode = iter.next();
            ArrayList<VgTaskPanelResult> resultList = imageNode.getTaskPanelResultList();

            for (int i = 0; i < resultList.size(); i++) {
                VgTaskPanelResult result = resultList.get(i);
                int stackCnt = result.getStackNodeCnt();
                int imageCnt = result.getImageNodeCnt();
                int taskNb = result.getTaskNb();
                int selItem = result.getSelItem();
                boolean locStatus = result.getLocalizationStatus();

                if (!locStatus && selItem != -1) {
                    taskCheckBoxLabelPanel[taskNb][selItem].setBackground(
                            new Color(checkBoxSelectColor[0], checkBoxSelectColor[1],
                            checkBoxSelectColor[2]));
                }
            }
        }
    }

    /**
     * Update the ratings (answers) if they are any previous set.
     * NOT IN USE
     */
    public void updateRatingValues_OLD() {
        int stackNodeCnt = history.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

        // color
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
            checkBoxColor[0] = AppPropertyUtils.defPanelColor[0];
            checkBoxColor[1] = AppPropertyUtils.defPanelColor[1];
            checkBoxColor[2] = AppPropertyUtils.defPanelColor[2];
        }

        // color
        key = "taskpanel.checkbox.select.color";
        int[] checkBoxSelectColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxSelectColor[0] == 0 && checkBoxSelectColor[1] == 0 && checkBoxSelectColor[2] == 0) {
            checkBoxSelectColor[0] = AppPropertyUtils.defCheckBoxSelectColor[0];
            checkBoxSelectColor[1] = AppPropertyUtils.defCheckBoxSelectColor[1];
            checkBoxSelectColor[2] = AppPropertyUtils.defCheckBoxSelectColor[2];
        }

        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();
        ArrayList<VgTaskPanelResult> resultList = viewDex.vgTaskPanelUtility.getTaskPanelResultList();

        /*
        for(int i=0; i < list.size(); i++){
        VgTaskPanelQuestion def = (VgTaskPanelQuestion)list.get(i);
        String[] taskLabelsText = def.getCheckBoxText();
        for(int j=0; j < taskLabelsText.length; j++){
        // reset background color
        for(int k = 0; k < taskLabelsText.length; k++)
        taskCheckBoxLabelPanel[i][k].setBackground(
        new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
        // set prev result
        if((resultList.size() > 0)
        VgTaskPanelResult result = resultList.get(i);
        int stackCnt = result.getStackNodeCnt();
        int imageCnt = result.getImageNodeCnt();
        int taskNb = result.getTaskNb();
        int selItem = result.getSelItem();
        if(stackNodeCnt == stackCnt &&
        imageNodeCnt == imageCnt &&
        taskNb == i)
        taskCheckBoxLabelPanel[i][selItem].setBackground(
        new Color(checkBoxSelectColor[0],checkBoxSelectColor[1],
        checkBoxSelectColor[2]));
        }
        }
         **/

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult result = resultList.get(i);
            int stackCnt = result.getStackNodeCnt();
            int imageCnt = result.getImageNodeCnt();
            int taskNb = result.getTaskNb();
            int selItem = result.getSelItem();

            if (stackNodeCnt == stackCnt
                    && imageNodeCnt == imageCnt
                    && taskNb == i) {
                taskCheckBoxLabelPanel[i][selItem].setBackground(
                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1],
                        checkBoxSelectColor[2]));
            }
        }
    }

    /**
     * Update the rating(answer) for a specific point value.
     */
    public void setRatingValue(Point2D p) {
        int stackNodeCnt = history.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

        // color
        /*
        String key = "taskpanel.checkbox.color";
        int[] checkBoxColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxColor[0] == 0 && checkBoxColor[1] == 0 && checkBoxColor[2] == 0) {
        checkBoxColor[0] = PropertiesUtils.defPanelColor[0];
        checkBoxColor[1] = PropertiesUtils.defPanelColor[1];
        checkBoxColor[2] = PropertiesUtils.defPanelColor[2];
        }

        // color
        key = "taskpanel.checkbox.select.color";
        int[] checkBoxSelectColor = propUtils.getPropertyColorValue(prop, key);
        if (checkBoxSelectColor[0] == 0 && checkBoxSelectColor[1] == 0 && checkBoxSelectColor[2] == 0) {
        checkBoxSelectColor[0] = PropertiesUtils.defCheckBoxSelectColor[0];
        checkBoxSelectColor[1] = PropertiesUtils.defCheckBoxSelectColor[1];
        checkBoxSelectColor[2] = PropertiesUtils.defCheckBoxSelectColor[2];
        }*/

        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();
        ArrayList<VgTaskPanelResult> resultList = viewDex.vgTaskPanelUtility.getTaskPanelResultList();

        for (int i = 0; i < resultList.size(); i++) {
            VgTaskPanelResult result = resultList.get(i);
            int stackCnt = result.getStackNodeCnt();
            int imageCnt = result.getImageNodeCnt();
            int taskNb = result.getTaskNb();
            int selItem = result.getSelItem();
            Point2D p2 = result.getPoint();

            if (stackNodeCnt == stackCnt
                    && imageNodeCnt == imageCnt
                    && p.equals(p2)) {
                taskCheckBoxLabelPanel[taskNb][selItem].setBackground(
                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1],
                        checkBoxSelectColor[2]));
            }
        }
    }

    /**
     * Create some borders.
     */
    private void createBorders() {
        black = BorderFactory.createLineBorder(Color.black);
        blackLine = BorderFactory.createLineBorder(Color.gray);
        blackLineLoweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        blackLineBold = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK);
        greyLine = BorderFactory.createLineBorder(new Color(204, 204, 204));
        empty = BorderFactory.createEmptyBorder();
    }

    /*****************************************************************
     * 
     *  KeyListener interface.
     *
     ****************************************************************/
    public void keyPressed(java.awt.event.KeyEvent keyEvent) {
        int a = 10;
    }

    public void keyReleased(java.awt.event.KeyEvent keyEvent) {
        int a = 10;
    }

    public void keyTyped(java.awt.event.KeyEvent keyEvent) {
        int a = 10;
    }

    /****************************************************************
     * 
     *  MouseListener interface.
     *
     ****************************************************************/
    @Override
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
        int a = 10;
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
        int a = 10;
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
        int a = 10;
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        mousePressedAction(e);
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
    }

    /*****************************************************************
     * 
     *  FocusListener interface
     *
     *****************************************************************/
    @Override
    public void focusGained(java.awt.event.FocusEvent focusEvent) {
        System.out.println("VgTaskPanel.focusGained");
    }

    @Override
    public void focusLost(java.awt.event.FocusEvent focusEvent) {
        System.out.println("VgTaskPanel.focusLost");
    }

    /*****************************************************************
     *  end FocusListener interface
     *****************************************************************/
    /**
     * The actions when the mouse is pressed.
     * @param e
     */
    private void mousePressedAction(java.awt.event.MouseEvent e) {
        boolean taskSelected = false;

        //System.out.println("VgTaskPanel.mousePressedAction()");

        // Prohibite mouseActions if the cineLoop is running.
        if (viewDex.appMainAdmin.vgControl.getCineLoopRunningStatus()
                || viewDex.appMainAdmin.vgControl.getImageLoadingWorkerStatus()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        ArrayList<VgTaskPanelQuestion> list = history.getTaskPanelQuestionList();
        int stackNodeCnt = history.getSelectedStackNodeCount();
        int imageNodeCnt = viewDex.appMainAdmin.vgControl.studyDbUtility.getSelectedImageNodeCount();

        if (viewDex.vgStudyNextCaseExtendedControl != null) {
            viewDex.vgStudyNextCaseExtendedControl.setGotoInputField("");
        }
        viewDex.requestFocusInWindow();

        // Check for runMode
        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.SHOW_EXIST) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            VgTaskPanelQuestion questionList = (VgTaskPanelQuestion) list.get(i);
            String[] taskLabelsText = questionList.getCheckBoxText();
            for (int j = 0; j < taskLabelsText.length; j++) {
                if (e.getComponent().equals(taskCheckBoxSubPanel[i][j])
                        || e.getComponent().equals(taskCheckBoxLabelPanel[i][j])) {
                    if (javax.swing.SwingUtilities.isLeftMouseButton(e)
                            || javax.swing.SwingUtilities.isRightMouseButton(e)) {

                        // Eye tracking
                        if (i == 0) {
                            viewDex.appMainAdmin.viewDex.eyeTracking.sendUDPMessage("ET_REC");
                            viewDex.appMainAdmin.viewDex.eyeTracking.sendUDPMessage("ET_INC");
                        }

                        // get status from '.taskpanel.taskx.localication' property
                        boolean localizationTask = questionList.getLocalizationTaskStatus();
                        boolean localizationActive = viewDex.localization.localizationActiveStatusExist();

                        // Note! The following code is dependent of the order.

                        // createMode, none localization
                        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST
                                || viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST) {
                            if (!localizationTask) {
                                // reset background color
                                for (int k = 0; k < taskLabelsText.length; k++) {
                                    taskCheckBoxLabelPanel[i][k].setBackground(
                                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                                }
                                // Set the select color
                                taskCheckBoxLabelPanel[i][j].setBackground(
                                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1], checkBoxSelectColor[2]));

                                // set task value (localization)
                                viewDex.vgTaskPanelUtility.setTaskPanelResult(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);

                                // set task value (no localization)
                                viewDex.vgTaskPanelUtility.setTaskPanelResultNoLocalization(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);

                                // test
                                //ArrayList<VgTaskPanelResult> list_test =  viewDex.vgTaskPanelUtility.getTaskPanelResultList();

                                //
                                //viewDex.appMainAdmin.vgControl.updateTaskPanelResult(stackNodeCnt, imageNodeCnt, i, j);

                                taskSelected = true;
                                return;
                            }
                        }

                        // createMode localization & localizationActive
                        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.CREATE_EXIST
                                || viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.DEMO_EXIST) {
                            if (localizationTask && localizationActive) {
                                // reset background color
                                for (int k = 0; k < taskLabelsText.length; k++) {
                                    taskCheckBoxLabelPanel[i][k].setBackground(
                                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                                }
                                // Set the select color
                                taskCheckBoxLabelPanel[i][j].setBackground(
                                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1], checkBoxSelectColor[2]));

                                // set task value
                                viewDex.vgTaskPanelUtility.setTaskPanelResult(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);
                                taskSelected = true;
                                //viewDex.localization.setLocalizationStatus();
                                return;
                            }
                        }

                        // editMode non localization
                        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST) {
                            //boolean localizationSelect = viewDex.appMainAdmin.vgControl.getLocalizationSelectStatus();

                            if (!localizationTask) {
                                // reset background color
                                for (int k = 0; k < taskLabelsText.length; k++) {
                                    taskCheckBoxLabelPanel[i][k].setBackground(
                                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                                }
                                // Set the select color
                                taskCheckBoxLabelPanel[i][j].setBackground(
                                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1], checkBoxSelectColor[2]));

                                // set task value (localization)
                                viewDex.vgTaskPanelUtility.setTaskPanelResult(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);

                                // set task value (no localization
                                viewDex.vgTaskPanelUtility.setTaskPanelResultNoLocalization(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);

                                //propagate the results to all the images in the stack
                                //viewDex.appMainAdmin.vgControl.updateTaskPanelResult(stackNodeCnt, imageNodeCnt, i, j);

                                taskSelected = true;
                                return;
                            }
                        }

                        // editMode localization & localizationSelect
                        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST) {
                            //zzzz check this function!
                            //boolean localizationSelect = viewDex.appMainAdmin.vgControl.getLocalizationSelectStatus();
                            boolean localizationSelectStatusExist = viewDex.localization.localizationSelectStatusExist();

                            if (localizationTask && localizationSelectStatusExist) {
                                // reset background color
                                for (int k = 0; k < taskLabelsText.length; k++) {
                                    taskCheckBoxLabelPanel[i][k].setBackground(
                                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                                }
                                // Set the select color
                                taskCheckBoxLabelPanel[i][j].setBackground(
                                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1], checkBoxSelectColor[2]));

                                // set task value
                                viewDex.vgTaskPanelUtility.setTaskPanelResultEditSelect(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);
                                taskSelected = true;
                                return;
                            }
                        }

                        // editMode localization & localizationActive
                        if (viewDex.appMainAdmin.vgControl.getRunModeStatus() == VgRunMode.EDIT_EXIST) {
                            //boolean localizationSelect = viewDex.appMainAdmin.vgControl.getLocalizationSelectStatus();

                            if (localizationTask && localizationActive) {
                                // reset background color
                                for (int k = 0; k < taskLabelsText.length; k++) {
                                    taskCheckBoxLabelPanel[i][k].setBackground(
                                            new Color(checkBoxColor[0], checkBoxColor[1], checkBoxColor[2]));
                                }
                                // Set the select color
                                taskCheckBoxLabelPanel[i][j].setBackground(
                                        new Color(checkBoxSelectColor[0], checkBoxSelectColor[1], checkBoxSelectColor[2]));

                                // set task value
                                viewDex.vgTaskPanelUtility.setTaskPanelResult(
                                        stackNodeCnt, imageNodeCnt, i, j, localizationTask);

                                taskSelected = true;
                                return;
                            }
                        }

                        // beep
                        if (!taskSelected) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        return;
                    }
                    return;
                }
            }
        }
    }

    /**
     * Get the layoutsize.
     */
    private int[] getLayoutSize(int nbBoxes, int len) {
        int[] layoutSize = new int[3];

        if (nbBoxes == 1) {
            layoutSize[0] = 5;
            if (len == 1 || len == 0) {
                layoutSize[1] = 20;
                layoutSize[2] = 21;
            } else if (len > 1) {
                layoutSize[1] = 20 + (len * 8);
                layoutSize[2] = 22;
            }
        }

        if (nbBoxes == 2) {
            layoutSize[0] = 5;
            if (len == 1 || len == 0) {
                layoutSize[1] = 20;
                layoutSize[2] = 21;
            } else if (len > 1) {
                layoutSize[1] = 20 + (len * 8);
                layoutSize[2] = 22;
            }
        }

        if (nbBoxes == 3) {
            layoutSize[0] = 5;
            if (len == 1 || len == 0) {
                layoutSize[1] = 20;
                layoutSize[2] = 21;
            } else if (len > 1) {
                layoutSize[1] = 20 + (len * 8);
                layoutSize[2] = 22;
            }
        }

        if (nbBoxes == 4) {
            layoutSize[0] = 5;
            if (len == 1 || len == 0) {
                layoutSize[1] = 20;
                layoutSize[2] = 21;
            } else if (len > 1) {
                layoutSize[1] = 20 + (len * 8);
                layoutSize[2] = 22;
            }
        }

        if (nbBoxes == 5) {
            layoutSize[0] = 5;
            layoutSize[1] = 20;
            layoutSize[2] = 21;
        }

        if (nbBoxes == 6) {
            layoutSize[0] = 5;
            layoutSize[1] = 20;
            layoutSize[2] = 21;
        }

        if (nbBoxes == 7) {
            layoutSize[0] = 4;
            layoutSize[1] = 20;
            layoutSize[2] = 21;
        }

        if (nbBoxes == 8) {
            layoutSize[0] = 2;
            layoutSize[1] = 20;
            layoutSize[2] = 21;
        }

        if (nbBoxes == 9) {
            layoutSize[0] = 1;
            layoutSize[1] = 19;
            layoutSize[2] = 20;
        }

        if (nbBoxes == 10) {
            layoutSize[0] = 1;
            layoutSize[1] = 16;
            layoutSize[2] = 17;
        }

        if (nbBoxes == 11) {
            layoutSize[0] = 0;
            layoutSize[1] = 15;
            layoutSize[2] = 16;
        }

        if (nbBoxes == 12) {
            layoutSize[0] = 0;
            layoutSize[1] = 14;
            layoutSize[2] = 15;
        }

        if (nbBoxes == 13) {
            layoutSize[0] = 0;
            layoutSize[1] = 13;
            layoutSize[2] = 14;
        }

        if (nbBoxes > 14) {
            layoutSize[0] = 0;
            layoutSize[1] = 8;
            layoutSize[2] = 9;
        }

        return layoutSize;
    }
}
