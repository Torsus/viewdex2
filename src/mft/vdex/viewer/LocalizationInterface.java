/*
 * LocalizationInterface.java
 *
 * Created on den 16 juli 2007, 13:46
 * Author Sune Svensson
 *
 */

package mft.vdex.viewer;

import java.awt.geom.Point2D;


public interface LocalizationInterface {
    
    public boolean mousePressedCreateAction(int x, int y);
    public boolean mousePressedShowSelectAction(int x, int y);
    public boolean mousePressedRightSelectAction(int x, int y);
    public boolean mousePressedEditSelectAction(Point2D p);
    public boolean mousePressedRightCreateAction(Point2D p);
    public int getRunModeStatus();
    public int getCanvasControlMode();
    public void setFocus();
    public void setGotoInputField();
    public void resetLocalizationOverlay();
    public void setKeyCtrlEnable(boolean status);
    public boolean getKeyCtrlEnable();

    //public boolean getShowStudyExist();
    //public boolean getEditStudyExist();
    //public void draw(int ancoX, int ancorY);
    //public void mouseMovedAction(Point2D p);
}
