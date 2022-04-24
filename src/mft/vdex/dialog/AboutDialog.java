/* @(#) AboutDialog.java 01/24/2006
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.dialog;

import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import mft.vdex.app.AppProperty;

/**
 *
 * @author sune
 */
public class AboutDialog extends JDialog{
    AppProperty appProp;
    JTextPane detailTextPane;
    JTextPane detailTextPane2;
    JTextPane contactTextPane;
    JTextPane licenseTextPane;
    String productVersion;
    
    /** Creates a new instance of AboutDialog */
    public AboutDialog(AppProperty prop, java.awt.Frame parent, boolean modal, String ver){
        super(parent, modal);
        this.appProp = prop;
        this.productVersion = ver;
        createUI();
    }
    
    private void createUI(){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(230,230,230));
        Font menuFont = getScaledFont("Sans Serif", Font.PLAIN, 12);
        tabbedPane.setFont(menuFont);
        
        JPanel detail = createDetailPanel();
        JPanel contact = createContactPanel();
        JPanel license = createLicensePanel();
        
        JScrollPane licenseScrollPane = new JScrollPane(license);
        licenseScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        licenseScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //licenseScrollPane.setViewportView(license);
        
        tabbedPane.addTab("Detail", detail);
        tabbedPane.addTab("Contact", contact);
        tabbedPane.addTab("License", licenseScrollPane);
        
        getContentPane().add(tabbedPane);
        pack();
        
        // closeDialog
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
    }
    
    /**
     * Create the Detail panel.
     */
    private JPanel createDetailPanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230,230,230));
        double size[][] = {{20,125,f},{0,40,10,200,f}};
        panel.setLayout(new TableLayout(size));
        
        JLabel text = new JLabel("ViewDEX Product Information");
        Font font = getScaledFont("Sans Serif", Font.BOLD, 14);
        text.setFont(font);
        
        detailTextPane = new JTextPane();
        detailTextPane.setBackground(new Color(230,230,230));
        detailTextPane.setEditable(false);
        createVersionHeadInfo();
        
        detailTextPane2 = new JTextPane();
        detailTextPane2.setBackground(new Color(230,230,230));
        detailTextPane2.setEditable(false);
        createVersionInfo();
        
        panel.add(text, "1,1,2,1");
        panel.add(detailTextPane, "1,3");
        panel.add(detailTextPane2, "2,3");
        
        return panel;
    }
    
    /**
     * Create the Contact panelpanel.
     */
    private JPanel createContactPanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230,230,230));
        double size[][] = {{20,f},{5,f}};
        panel.setLayout(new TableLayout(size));
        
        contactTextPane = new JTextPane();
        contactTextPane.setBackground(new Color(230,230,230));
        contactTextPane.setEditable(false);
        createContactInfo();
        
        panel.add(contactTextPane, "1,1");
        return panel;
    }
    
    /**
     * Create the License panel.
     */
    private JPanel createLicensePanel(){
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230,230,230));
        double size[][] = {{20,360},{5,f}};
        panel.setLayout(new TableLayout(size));
        
        licenseTextPane = new JTextPane();
        licenseTextPane.setBackground(new Color(230,230,230));
        licenseTextPane.setEditable(false);
        createLicenseInfo();
        
        panel.add(licenseTextPane, "1,1");
        return panel;
    }
    
    
    /**
     * Create the "Detail" header information.
     */
    private void createVersionHeadInfo(){
        String nl = "\n";
        
        /*
        String[] initString =
                { "This is an editable JTextPane, ",		//regular
                  "another ",					//italic
                  "styled ",					//bold
                  "text ",					//small
                  "component, ",				//large
                  "which supports embedded components..." + newline,//regular
                  " " + newline,				//button
                  "...and embedded icons..." + newline,		//regular
                  " ", 						//icon
                  newline + "JTextPane is a subclass of JEditorPane that " +
                    "uses a StyledEditorKit and StyledDocument, and provides " +
                    "cover methods for interacting with those objects."
                 };
         */
        String[] initString =
        {"Product Version:" + nl,
                 "Operating System:" + nl,
                 "Java:" + nl,
                 "VM:" + nl,
                 "Vendor:" + nl,
                 "Java Home:" + nl,
                 "User Country:" + nl,
                 "User Language:" + nl,
                 "Home Dir:" + nl,
                 "User Dir:" + nl,
                 //"ViewDEX Install:"
        };
        
        String[] initStyles = {"bold12","bold12","bold12","bold12","bold12",
                "bold12","bold12","bold12","bold12","bold12"};
                
                initStylesForDetailInfo();
                Document doc = detailTextPane.getDocument();
                
                try {
                    for (int i=0; i < initString.length; i++) {
                        doc.insertString(doc.getLength(), initString[i],
                                detailTextPane.getStyle(initStyles[i]));
                    }
                } catch (BadLocationException ble) {
                    System.err.println("Couldn't insert initial text.");
                }
    }
    
    
    //*************************************************************
    //    Version info
    //*************************************************************
    
    private void createVersionInfo(){
        String nl = "\n";
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String javaVendor = System.getProperty("java.vendor");
        String javaVersion = System.getProperty("java.version");
        String javaHome = System.getProperty("java.home");
        //String javaVirtuall = System.getProperty("java.virtual");
        String javaVmName = System.getProperty("java.vm.name");
        String userCountry = System.getProperty("user.country");
        String userLanguage = System.getProperty("user.language");
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");
        String osSystem= osName + " version " + osVersion + " running on " + osArch;
        
        // Prints out the system properties
        //SystemInfo si = new SystemInfo();
        //si.printSystemInfo();
        
        Properties sysProp = appProp.getSystemProperty();
        //String appRoot = sysProp.getProperty("app.root");
        //String historyBackupRoot = sysProp.getProperty("app.history.backup.root");
        //String imagedbRoot = sysProp.getProperty("imagedb.root");
        
        String[] initString =
        {productVersion + nl,
                 osSystem + nl,
                 javaVersion + nl,
                 javaVmName + nl,
                 javaVendor + nl,
                 javaHome + nl,
                 userCountry + nl,
                 userLanguage + nl,
                 userHome + nl,
                 userDir + nl,
                 //appRoot + nl,
                 //historyBackupRoot + nl,
                 //imagedbRoot + nl,
        };
        
        String[] initStyles = {"mono12","mono12","mono12","mono12","mono12",
                "mono12","mono12","mono12","mono12","mono12","mono12","mono12"};
                
                //initStylesForDetailInfo();
                Document doc = detailTextPane2.getDocument();
                
                try {
                    for (int i=0; i < initString.length; i++) {
                        doc.insertString(doc.getLength(), initString[i],
                                detailTextPane2.getStyle(initStyles[i]));
                    }
                } catch (BadLocationException ble) {
                    System.err.println("Couldn't insert initial text.");
                }
    }
    
    /**
     * Init styles.
     */
    private void initStylesForDetailInfo(){
        Style variable = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // monoText
        Style monoText = detailTextPane.addStyle("mono12", variable);
        StyleConstants.setFontFamily(variable, "Times New Roman");
        StyleConstants.setFontSize(monoText, 16);
        
        // bold16
        Style boldText = detailTextPane.addStyle("bold12", variable);
        StyleConstants.setFontFamily(boldText, "Dialog");
        StyleConstants.setFontSize(boldText, 12);
        StyleConstants.setBold(boldText, true);
        
        // bold14
        Style boldText14 = detailTextPane.addStyle("bold14", variable);
        StyleConstants.setFontFamily(boldText14, "Dialog");
        StyleConstants.setFontSize(boldText14, 14);
        StyleConstants.setBold(boldText14, true);
    }
    
    
    //*************************************************************
    //    Contact info
    //*************************************************************
    private void createContactInfo(){
        String nl = "\n";
        String header = "For more information about the ViewDEX project please contact.";
        String company = "Sahlgrenska University Hospital";
        String dept = "Dept. of Medical Physics and Biomedical Engineering";
        String adress = "SE-413 45 Goteborg, Sweden";
        
        String support1 = "Magnus Båth";
        String support11 = "Phone  +46 31 342 7276";
        String support12 = "Email  magnus.bath@vgregion.se";
        
        String support2 = "Markus Håkansson";
        String support21 = "Phone  +46 33 616 2365";
        String support22 = "Email  markus.hakansson@vgregion.se";
        
        String support3 = "Sune Svensson";
        String support31 = "Phone  +46 31 342 7271";
        String support32 = "Email  sune.l.svensson@vgregion.se";
        
        String[] initString =
        {header + nl,                                    // mono12
                 nl,                                             // mono12
                 company + nl,                                   // mono12
                 dept + nl,                                      // mono12
                 adress + nl + nl,                               // mono12
                 " " + support1 + nl,                            // mono12
                 "     " + support11 + nl,                       // mono12
                 "     " + support12 + nl,                       // mono12
                 "" + nl,                                        // mono12
                 " " + support2 + nl,                            // mono12
                 "     " + support21 + nl,                       // mono12
                 "     " + support22 + nl,                       // mono12
                 "" + nl,                                        // mono12
                 " " + support3 + nl,                            // mono12
                 "     " + support31 + nl,                       // mono12
                 "     " + support32 + nl,                       // mono12
        };
        
        String[] initStyles = {"mono12","mono12","mono12","mono12","mono12",
                "mono12","mono12","mono12",
                "mono12","mono12","mono12","mono12",
                "mono12","mono12","mono12","mono12"};
                
                initStylesForContactInfo();
                Document doc = contactTextPane.getDocument();
                
                try {
                    for (int i=0; i < initString.length; i++) {
                        doc.insertString(doc.getLength(), initString[i],
                                contactTextPane.getStyle(initStyles[i]));
                    }
                } catch (BadLocationException ble) {
                    System.err.println("Couldn't insert initial text.");
                }
    }
    
    private void initStylesForContactInfo(){
        Style variable = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // monoText
        Style monoText = contactTextPane.addStyle("mono12", variable);
        StyleConstants.setFontFamily(variable, "Dialog");
        StyleConstants.setFontSize(monoText, 12);
        
        // bold16
        Style boldText = contactTextPane.addStyle("bold16", variable);
        StyleConstants.setFontFamily(boldText, "Dialog");
        StyleConstants.setFontSize(boldText, 16);
        StyleConstants.setBold(boldText, true);
        
        // bold14
        Style boldText14 = contactTextPane.addStyle("bold14", variable);
        StyleConstants.setFontFamily(boldText14, "Dialog");
        StyleConstants.setFontSize(boldText14, 14);
        StyleConstants.setBold(boldText14, true);
    }
    // end createContactInfo
    
    
    //*************************************************************
    //    License info
    //*************************************************************
    private void createLicenseInfo(){
        String comment = "(Ett exemple p� hur det kan se ut) / Sune Svensson.";
        String nl = "\n";
        String header = "LICENCE AGREEMENT for ViewDEX";
        String release = "Release 2.57";
        
        // Important
        String licence10 = "IMPORTANT:This licence agreement is a binding contract between you and ";
        String licence11 = "Sahlgrenska University Hospital, Goteborg Sweden, regarding the above mentioned Software. ";
        String licence12 = "Read the terms of this agreement carefully. ";
        String licence13 = "By installing the Software you accept the terms and conditions of this agreement. ";
        String licence14 = "If you do not agree to the terms of this license agreement, remove the above mentioned Software from your computer.";
        String licence100 = licence10 + licence11 + licence12 + licence13 + licence14;
        
        // Licence Grant
        String licence20 = "1. Licence Grant";
        String licence21 = "a) Sahlgrenska University Hospital grants to you, subject to the following terms and conditions, ";
        String licence22 = "the right to use the Software free of any charge.";
        String licence23 = "b) You may reproduce and use the Software internally in binary form.";
        String licence24 = "c) You must not reverse engineer, decompile, dissambly, modify or ";
        String licence25 = "translate the Software or make any attempt to discover the source code ";
        String licence26 = "of the Software or create derivative works from the Software. ";
        String licence27 = "The source code must not be distributed to third parties. ";
        String licence28 = "d) All intellectual property rights in the Software and user documentation ";
        String licence29 = "are owned by Sahlgrenska University Hospital, Gotenborg Sweden.";
        
        // Warrenty
        String licence30 = "2. Warranty, Liability, Indemnity";
        String licence31 = "a) As the licence is granted for free, the Software is provided \"as is\" ";
        String licence32 = "and there is no warranty, representation, promise or guarantee of ";
        String licence33 = "Sahlgrenska University Hospital, either express or implied, statutory ";
        String licence34 = "or otherwise, with respect to the Sofware, user documentation or related ";
        String licence35 = "technical support, including their quality, performance or fitness for a particular purpose.";
        
        String licence36 = "b) In no event will Sahlgrenska University Hospital be liable for indirect, special incidental ";
        String licence37 = "or economic damages arising out of the use of or inability to use the Software. ";
        String licence38 = "Sahlgrenska University Hospital will not be liable for any loss of profits, ";
        String licence39 = "business, goodwill, data or computer programs.";
        
        String licence40 = "c) This Software is not intended for clinical use. This software has neither ";
        String licence41 = "been tested nor approved  for clinical use. It is the user's responsibility to ";
        String licence42 = "comply with any applicable local, state, national or international regulations.";
        
        String licence300 = licence31 + licence32 + licence33 + licence34 + licence35;
        String licence301 = licence36 + licence37 + licence38 + licence39;
        String licence302 = licence40 + licence41 + licence42;
        
        
        
        // Term and Termination
        String licence50 = "3. Term and Termination  ";
        String licence51 = "a) This agreement is in effective from the date you install the Software ";
        String licence52 = "and will remain in force for indefinite time.";
        String licence53 = "b) You may terminate this agreement at any time by destroying the documentation ";
        String licence54 = "and the Software together with all copies.";
        String licence500 = licence51 + licence52;
        String licence501 = licence53 + licence54;
        
        // string
        String[] initString =
        {header + nl,                                   // mono12
                 release + nl + nl,                     // mono12
                 //comment + nl + nl,                     // mono12
                 licence100 + nl + nl,                  // mono10
                 licence20 + nl,                        // mono10
                 licence21 + licence22 + nl + nl,       // mono10
                 licence23 + nl + nl,                   // mono10
                 licence24 + licence25 + licence26,     // mono10
                 licence27 + nl + nl,                   // mono10
                 licence28 + licence29,                 // mono10
                 nl + nl,                               // mono10
                 licence30 + nl,                        // mono10
                 licence300 + nl + nl,                  // mono10
                 licence301 + nl + nl,                  // mono10
                 licence302 + nl + nl,                  // mono10
                 licence50 + nl,                        // mono10
                 licence500 + nl + nl,                  // mono10
                 licence501 + nl + nl                   // mono11
        };
        
        String[] initStyles = {"mono12","mono12",
                "mono12",
                "mono10","mono10","mono10","mono10","mono10",
                "mono10","mono10","mono10","mono10","mono10",
                "mono10","mono10","mono10","mono10","mono10"};
                
                initStylesForLicenseInfo();
                Document doc = licenseTextPane.getDocument();
                
                try {
                    for (int i=0; i < initString.length; i++) {
                        doc.insertString(doc.getLength(), initString[i],
                                licenseTextPane.getStyle(initStyles[i]));
                    }
                } catch (BadLocationException ble) {
                    System.err.println("Couldn't insert initial text.");
                }
                
                //jScrollPane1.getVerticalScrollBar().setValue(1);
                licenseTextPane.setCaretPosition(0);
    }
    
    private void initStylesForLicenseInfo(){
        Style variable = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // monoText
        Style monoText = licenseTextPane.addStyle("mono12", variable);
        StyleConstants.setFontFamily(variable, "Dialog");
        StyleConstants.setFontSize(monoText, 12);
        
        // bold16
        Style mono10 = licenseTextPane.addStyle("mono10", variable);
        StyleConstants.setFontFamily(mono10, "Dialog");
        StyleConstants.setFontSize(mono10, 10);
    }
    // end createLicenseInfo
    
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }
    
    /**
     * Gets font scaled for screen resolution
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