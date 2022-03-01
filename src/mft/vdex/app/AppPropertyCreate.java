/* @(#) AppPropertyCreate.java 03/14/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.app;

import java.util.ArrayList;
import mft.vdex.modules.vg.VgCanvasInterpolationControl;
import mft.vdex.modules.vg.VgCineLoopPanelControl;
import mft.vdex.modules.vg.VgFunctionPanelUserDefinedWLControl;
import mft.vdex.modules.vg.VgFunctionPanelWLControl;
import mft.vdex.modules.vg.VgFunctionPanelZoomControl;
import mft.vdex.modules.vg.VgFunctionPanelZoomModeControl;
import mft.vdex.modules.vg.VgLogOptionalSpecial;
import mft.vdex.modules.vg.VgLogOptionalTag;
import mft.vdex.modules.vg.VgTaskPanelClarification;
import mft.vdex.modules.vg.VgTaskPanelQuestion;

public class AppPropertyCreate {

    ViewDex viewDex;

    public AppPropertyCreate(ViewDex viewdex) {
        viewDex = viewdex;
    }

    /**
     * Create a list containing the Task panel questions and the
     * checkbox label text from the vgstudy-xxxx.properties file.
     * This list is used to dynamical create the taskPanel GUI.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgTaskPanelQuestion> createTaskPanelQuestionList() {
        ArrayList<VgTaskPanelQuestion> list = new ArrayList<VgTaskPanelQuestion>();
        String str1 = "taskpanel.task";
        String str2 = "taskpanel.task";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1, key2, key3, key4, key5, key6;

            key1 = str1 + (i + 1) + ".text";
            key2 = str2 + (i + 1) + ".checkbox.text";
            key3 = str1 + (i + 1) + ".text.font";
            key4 = str2 + (i + 1) + ".checkbox.text.font";
            key5 = str2 + (i + 1) + ".localization";
            key6 = str1 + (i + 1) + ".mandatory";

            if (viewDex.appProperty.getStudyProperties().containsKey(key1)
                    && viewDex.appProperty.getStudyProperties().containsKey(key2)) {
                String s1 = null;
                String s2 = null;
                String s3 = null;
                String s4 = null;
                String s5 = null;
                String s6 = null;

                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();
                s2 = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();

                if (viewDex.appProperty.getStudyProperties().containsKey(key3)) {
                    s3 = viewDex.appProperty.getStudyProperties().getProperty(key3).trim();
                }

                if (viewDex.appProperty.getStudyProperties().containsKey(key4)) {
                    s4 = viewDex.appProperty.getStudyProperties().getProperty(key4).trim();
                }

                String def5 = "n";
                if ((viewDex.appProperty.getStudyProperties().containsKey(key5))) {
                    s5 = viewDex.appProperty.getStudyProperties().getProperty(key5).trim();
                }

                if (s5 == null || s5.equalsIgnoreCase("")) {
                    s5 = def5;
                }

                String def6 = "y";
                if ((viewDex.appProperty.getStudyProperties().containsKey(key6))) {
                    s6 = viewDex.appProperty.getStudyProperties().getProperty(key6).trim();
                }

                if (s6 == null || s6.equalsIgnoreCase("")) {
                    s6 = def6;
                }

                list.add(new VgTaskPanelQuestion(s1, s2, s3, s4, s5, s6));
            }
        }
        return list;
    }

    /**
     * Create a list containing the Task panel clarification text
     * read from the vgstudy-xxxx.properties file.
     * This list is used to dynamical create the taskPanel GUI.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgTaskPanelClarification> createTaskPanelClarificationList() {
        ArrayList<VgTaskPanelClarification> list = new ArrayList<VgTaskPanelClarification>();
        String str1 = "taskpanel.clarification";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1;
            String key2;

            key1 = str1 + (i + 1) + ".text";
            key2 = str1 + (i + 1) + ".text.font";

            if (viewDex.appProperty.getStudyProperties().containsKey(key1)) {
                String s1 = null;
                String s2 = null;
                String s3 = null;
                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();

                if (viewDex.appProperty.getStudyProperties().containsKey(key2)) {
                    s2 = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();
                }

                list.add(new VgTaskPanelClarification(s1, s2));
            }

        }
        return list;
    }

    /**
     * Create a list containing the functionPanelZoomMode definitions
     * read from the vgstudy-xxxx.properties file.
     * The list is used to dynamical create the function panel GUI.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgFunctionPanelZoomModeControl> createFunctionPanelZoomModeList() {
        ArrayList<VgFunctionPanelZoomModeControl> list = new ArrayList<VgFunctionPanelZoomModeControl>();
        String str = "functionpanel.displaysize.button";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1;
            String key2;

            String key3;

            key1 = str + (i + 1) + ".name";
            key2 = str + (i + 1) + ".zoom";
            key3 = str + (i + 1) + ".font";

            if (viewDex.appProperty.getStudyProperties().containsKey(key1)
                    && viewDex.appProperty.getStudyProperties().containsKey(key2)) {
                String s1 = null;
                String s2 = null;
                String s3 = null;

                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();
                s2 = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();

                if (viewDex.appProperty.getStudyProperties().containsKey(key3)) {
                    s3 = viewDex.appProperty.getStudyProperties().getProperty(key3).trim();
                }

                if (!(s1.equalsIgnoreCase("") || s2.equalsIgnoreCase(""))) {
                    list.add(new VgFunctionPanelZoomModeControl(key1, s1, key2, s2, s3));
                }
            }
        }
        return list;
    }

    /**
     * UPDATE!
     * Create a list containing the cineLoopPanel definitions read from
     * the vgstudy-xxxx.properties file.
     * The list is used to dynamical create the Cine-loop panel GUI.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgCineLoopPanelControl> createCineLoopPanelControlList() {
        ArrayList<VgCineLoopPanelControl> list = new ArrayList<VgCineLoopPanelControl>();
        String str = "cineloop.button";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1, key2;

            key1 = str + (i + 1) + ".name";
            key2 = str + (i + 1) + ".frame.interval.value";

            String s1 = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key1);
            int val = viewDex.appPropertyUtils.getPropertyIntegerValue(viewDex.appProperty.getStudyProperties(), key2);
            if (val == 0) {
                val = 80;
            }  //Default

            if (!(s1.equalsIgnoreCase(""))) {
                list.add(new VgCineLoopPanelControl(key1, s1, key2, val));
            }
        }
        return list;
    }

    /**
     * Create a list containing the functionPanelZoomControl definitions
     * read from the vgstudy-xxxx.properties file.
     * The list is used to dynamical create the function panel GUI.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgFunctionPanelZoomControl> createFunctionPanelZoomControlList() {
        ArrayList<VgFunctionPanelZoomControl> list = new ArrayList<VgFunctionPanelZoomControl>();
        String[] key = new String[7];

        key[0] = "functionpanel.zoom.reset";
        key[1] = "functionpanel.zoom.reset.button.font";

        key[2] = "functionpanel.zoom.in";
        key[3] = "functionpanel.zoom.in.button.font";

        key[4] = "functionpanel.zoom.out";
        key[5] = "functionpanel.zoom.out.button.font";

        //key[6] = "functionpanel.zoom.adjust";

        // The reset, in and out properties are read only if reset is defined.
        // zoom.reset
        String defZoomReset = "yes";
        if (viewDex.appProperty.getStudyProperties().containsKey(key[0])) {
            String zoomReset = viewDex.appProperty.getStudyProperties().getProperty(key[0]).trim();
            if (zoomReset.equals("")) {
                zoomReset = defZoomReset;
            }

            if (zoomReset.equalsIgnoreCase("Yes") || zoomReset.equalsIgnoreCase("Y")) {
                String s1 = null;
                String s2 = null;
                String s3 = null;
                String s4 = null;
                String s5 = null;
                String s6 = null;

                // zoom.reset
                defZoomReset = "yes";
                if (viewDex.appProperty.getStudyProperties().containsKey(key[0])) {
                    s1 = viewDex.appProperty.getStudyProperties().getProperty(key[0]).trim();
                }

                if (s1.equals("")) {
                    s1 = defZoomReset;
                }

                // zoom.reset.button.font
                if (viewDex.appProperty.getStudyProperties().containsKey(key[1])) {
                    s2 = viewDex.appProperty.getStudyProperties().getProperty(key[1]).trim();
                }

                list.add(new VgFunctionPanelZoomControl(key[0], s1, s2));

                // functionpanel.zoom.in
                String defZoomIn = "y";
                if (viewDex.appProperty.getStudyProperties().containsKey(key[2])) {
                    s3 = viewDex.appProperty.getStudyProperties().getProperty(key[2]).trim();
                }

                if (s3.equals("")) {
                    s3 = defZoomIn;
                }

                // functionpanel.zoom.in.button.font
                if (viewDex.appProperty.getStudyProperties().containsKey(key[3])) {
                    s4 = viewDex.appProperty.getStudyProperties().getProperty(key[3]).trim();
                }

                list.add(new VgFunctionPanelZoomControl(key[2], s3, s4));

                // functionpanel.zoom.out
                String defZoomOut = "y";
                if (viewDex.appProperty.getStudyProperties().containsKey(key[4])) {
                    s5 = viewDex.appProperty.getStudyProperties().getProperty(key[4]).trim();
                }

                if (s5.equals("")) {
                    s5 = defZoomOut;
                }

                // functionpanel.zoom.out.button.font
                if (viewDex.appProperty.getStudyProperties().containsKey(key[5])) {
                    s6 = viewDex.appProperty.getStudyProperties().getProperty(key[5]).trim();
                }

                list.add(new VgFunctionPanelZoomControl(key[4], s5, s6));
            }

        }
        return list;
    }

    /**
     * Create a list containing the functionPanel WL definitions
     * read from the vgstudy-xxxx.properties file.
     * The list is used to dynamical create the function panel GUI.
     * The list is stored in the history object. The history object
     * is used for persistent storage.
     */
    public ArrayList<VgFunctionPanelWLControl> createFunctionPanelWLList() {
        ArrayList<VgFunctionPanelWLControl> list = new ArrayList<VgFunctionPanelWLControl>();

        //String str1 = "functionpanel.wl.preset.button";
        String str1 = "functionpanel.wl.additional-ww-wc.button";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1;
            String key2;

            key1 = str1 + (i + 1) + ".name";
            key2 = str1 + (i + 1) + ".font";
            if (viewDex.appProperty.getStudyProperties().containsKey(key1)) {
                String s1;
                String s2;

                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();
                s2 = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();
                if (!s1.equalsIgnoreCase("")) {
                    list.add(new VgFunctionPanelWLControl(key1, s1, s2));
                }
            }
        }
        return list;
    }

    /**
     * Create a list containing the functionPaneUserDefined wl definitions
     * read from the vgstudy-xxxx.properties file.
     * The list is used to dynamical create the function panel GUI.
     * The list is stored in the history object. The history object
     * is used for persistent storage.
     */
    public ArrayList<VgFunctionPanelUserDefinedWLControl> createFunctionPanelUserDefinedWLList() {
        ArrayList<VgFunctionPanelUserDefinedWLControl> list = new ArrayList<VgFunctionPanelUserDefinedWLControl>();

        //String str1 = "functionpanel.wl.preset.button";
        String str1 = "functionpanel.wl.userdefined-ww-wc.button";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1, key2, key3;

            key1 = str1 + (i + 1) + ".name";
            key2 = str1 + (i + 1) + ".value";
            key3 = str1 + (i + 1) + ".font";
            if (viewDex.appProperty.getStudyProperties().containsKey(key1)) {
                String s1, s2, s3;

                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();
                s2 = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();
                s3 = viewDex.appProperty.getStudyProperties().getProperty(key3).trim();
                if (!s1.equalsIgnoreCase("")) {
                    list.add(new VgFunctionPanelUserDefinedWLControl(key1, s1, s2, s3));
                }
            }
        }
        return list;
    }

    /**
     * Create a list containing the interpolation properties read from
     * the vgstudy-xxxx.properties file.
     * This list is used to dynamical create the canvas context menu items,
     * and to set the default interpolation.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     */
    public ArrayList<VgCanvasInterpolationControl> createCanvasInterpolationList() {
        ArrayList<VgCanvasInterpolationControl> list = new ArrayList<VgCanvasInterpolationControl>();
        String key2 = null;
        String defValue = "bilinear";
        String ip = null;
        String key = "canvas.interpolation.default";
        String str2 = "canvas.contextmenu.interpolation";

        // default value
        String interpolation = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key);
        if (interpolation.equals("")) {
            interpolation = defValue;
        }

        // defined interpolations
        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            key2 = str2 + (i + 1);
            if (viewDex.appProperty.getStudyProperties().containsKey(key2)) {
                ip = viewDex.appProperty.getStudyProperties().getProperty(key2).trim();
                list.add(new VgCanvasInterpolationControl(interpolation, ip));
            }
        }
        return list;
    }

    /**
     * Create a list containing the optional log data defined in
     * the vgstudy-xxxx.properties file.
     * The list is stored in the history object. The history object
     * is used for persistens storage.
     *
     *@return the <code>VgLogOptionTag</code> list.
     */
    public ArrayList<VgLogOptionalTag> createLogOptionalTagList() {
        ArrayList<VgLogOptionalTag> list = new ArrayList<VgLogOptionalTag>();

        // The properties
        // vgstudy.vg01.log.data.option-01.tag
        // vgstudy.vg01.log.data.option-01.text
        String str1 = "log.data.option-";

        for (int i = 0; i < viewDex.appProperty.getStudyProperties().size(); i++) {
            String key1;
            String key2;

            String key3;

            String key4;

            key1 = str1 + (i + 1) + ".tag";
            //key2 = str1 + (i + 1) + ".name";
            key3 = str1 + (i + 1) + ".text";

            if (viewDex.appProperty.getStudyProperties().containsKey(key1)) {
                String s1 = null;
                String s2 = null;
                String s3 = null;

                s1 = viewDex.appProperty.getStudyProperties().getProperty(key1).trim();
                s3 = viewDex.appProperty.getStudyProperties().getProperty(key3).trim();

                list.add(new VgLogOptionalTag(s1, s2, s3));
            }
        }
        return list;
    }

    /**
     * Create a list containing the special optional log data
     * defined in the vgstudy-xxxx.properties file.
     * The list is stored in the history object. The history
     * object is used for persistent storage.
     *
     * @return the <code>VgLogOptionalSpecial</code> list.
     */
    public ArrayList<VgLogOptionalSpecial> createLogOptionalSpecialList() {
        ArrayList<VgLogOptionalSpecial> list = new ArrayList<VgLogOptionalSpecial>();
        String[] key = new String[7];

        key[0] = "log.data.option-wl";
        key[1] = "log.data.option-timespent";

        String s1 = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key[0]);
        if (s1.equals("")) {
            s1 = "y";
        }

        list.add(new VgLogOptionalSpecial(key[0], s1));

        String s2 = viewDex.appPropertyUtils.getPropertyStringValue(viewDex.appProperty.getStudyProperties(), key[1]);
        if (s2.equals("")) {
            s2 = "y";
        }

        list.add(new VgLogOptionalSpecial(key[1], s2));

        return list;
    }
}
