/* @(#) ImageManipulatorInterface.java 01/28/2005
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 * This code is based on code written by Lawrence Rodriges.
 */

package mft.vdex.viewer;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import java.util.ArrayList;
import javax.media.jai.PlanarImage;
import mft.vdex.ds.StudyDbROID;
import mft.vdex.ds.StudyDbROIV;
import mft.vdex.ds.StudyDbROIPixelValueD;
import mft.vdex.ds.StudyDbLocalizationM;

public interface ImageCanvasInterface{

    public PlanarImage getImage();
    
    /** Sets the panOffset property.
     * @param panOffset the offset by which the currently displayed image is moved
     * from the previous position.
     */
    public void setPanOffset(Point panOffset);
    
    
    /** Returns the panOffset property.
     * @return the panOffset.
     */
    public Point getPanOffset();
    
    
    /** Sets the magFactor property.
     * @param magFactor the magnification factor.
     */
    public void setMagFactor(double magFactor);
    
    
    /** Gets the magFactor property.
     * @return the magFactor.
     */
    public double getMagFactor();
    
    
    /** Sets the rotationAngle property.
     * @param rotationAngle the rotation angle
     */
    public void setRotationAngle(double theta);
    
    
    /** Gets the rotation angle property.
     * @return the rotation angle.
     */
    public double getRotationAngle();
    
    
    /** Sets the shearFactor property.
     * @param shearFactor the shearFactor property.
     */
    public void setShearFactor(double shear);
    
    
    /** @return the shearFactor. */
    public double getShearFactor();
    
    
    /** Gets the transform property.
     * @return the current transform object.
     */
    public AffineTransform getTransform();
    
    
    /** Sets the transform property.
     * @param the transform.
     */
    public void setTransform(AffineTransform at, boolean renderStatus);
    
    
    /** Applies the transform
     * @param the transform.
     */
    public void applyTransform(AffineTransform atx);
    
    
    /** Sets the interpolation property.
     * @param the interpolation type
     */
    public void setInterpolation(int interType);
    
    
    /* @return the interpolation type */
    public int getInterpolation();
    
    
    /**  Resets manipulation.*/
    public void resetManipulation();
    
    
    /** Type AWT Image */
    public static final int TYPE_AWT_IMAGE = 1;
    
    
    /** Type BufferedImage */
    public static final int TYPE_BUFFERED_IMAGE = 2;
    
    
    /** Sets the image property. If the input parameter is not null, the
     * it becomes the current image. This is a bound property.
     * @param image the image object
     */
    public void setAWTImage(Image image);
    
    
    /** Returns the image property.
     * @return the image object
     */
    public Image getAWTImage();
    
    
    /** Returns the image size.
     * @return the image size.
     */
    public Dimension getImageSize();
    
    
    /** Sets the BufferedImage property. If the input parameter is not null, the
     * it becomes the current image. This is a bound property.
     * @param BufferedImage the image object
     */
    public void setBufferedImage(BufferedImage image);
    
    
    /** Returns the BufferedImage property. If the image loaded is of Image type,
     * this method creates a BufferedImage from the original image object.
     * @return the BufferedImage object
     */
    public BufferedImage getBufferedImage();
    
    
    /** Sets the image type property.
     * @param imageType the image type.
     */
    public void setImageType(int imageType);
    
    
    /** Returns the image type property.
     * @return the image type.
     */
    public int getImageType();
    
    
    /** Sets the display mode. This is a bound property .  The input parameter can be
     * one of NORMAL, ORIG_SIZE, TO_FIT, SCALED. These constants are
     * defined in the DisplayMode class.
     * Call to this method would reset the viewport and the image is redarwn with new display
     * mode.
     * @param dispMode the display mode
     */
    public void setDisplayMode(int dispMode);
    
    
    /** Returns the display mode.
     * @return the current display mode.
     */
    public int getDisplayMode();
    
    
    /** Sets the flip mode. This is a bound property . The input parameter can be
     * one of NORMAL, LEFT_RIGHT, TOP_BOTTOM, LEFT_RIGHT_TOP_BOTTON. These constants are
     * defined in the FlipMode class.
     * Call to this method would reset the viewport and the image is redraw with new flip
     * mode.
     * @param flipMode the flip mode
     */
    public void setFlipMode(int flipMode);
    
    
    /** Returns the flip mode.
     * @return the current flip mode.
     */
    public int getFlipMode();
    
    
    /** Sets the invert mode property. It fires the propertyChange events because it
     * is a bound property. If set true, it inverts the image.
     * @param onOrOff the invert mode.
     */
    public void setInvert(boolean onOrOff);
    
    
    /** Returns the invert mode.
     * @return the invert mode
     */
    public boolean getInvert();
    
    
    /** Sets the off screen image.
     * @param image the off screen image.
     */
    public void setOffScreenImage(BufferedImage image);
    
    
    /** Returns the off screen image.
     * @return the off screen imge.
     */
    public BufferedImage getOffScreenImage();
    
    
    /** Sets the image to be displayed on the canvas. When an image is
     * set this way, it is not saved. This method needs to be
     * used for temporory display only.The correct way to set the image
     * is to call the setAWTImage() or setBufferedImage() methods.
     * @param image the image to be displayed.
     */
    public void setDisplayImage(PlanarImage image);
    
    
    /** Returns the image displayed on the canvas.
     * @return the displayed imge.
     */
    public BufferedImage getDisplayedImage();
    
    
    /** This method paints the current image i.e.; the image set by the
     * setImage() method. While painting it applies the current display and flip mode
     * policies. Call to this method will reset the panOffset and magFactor properties to
     * default values. This method is called whenever the display
     * and flip modes are changed.
     */
    public boolean paintImage();
    
    /**
     * Test
     * Update the image canvas.
     */
    public void paintImage2();
    
    /**
     * Set the mouse x,y position.
     */
    public void setCanvasOverlayMousePositionPixelValue(int mousePosX, int mousePosY, boolean renderStatus);
    
     /**
     */
    public void draw(int line1, int line2, int line3, int line4, BasicStroke bs);
    
    /**
     * Use this method for setting the localization symbol.
     */
    public void draw(Line2D.Double line1, Line2D.Double line2, BasicStroke bs);
    
    /**
     * 
     */
    public void draw(Line2D.Double line1, Line2D.Double line2, Line2D.Double line3,
            Line2D.Double line4, BasicStroke bs);
    
    
    /**
     * Use this method for setting the localization and select symbol.
     */
    public void draw(Shape s, Line2D.Double line1, Line2D.Double line2, BasicStroke bs);
    
   /**
     * Set the localization select symbol.
     */
    public void draw(Shape s);
    
     /**
     * Set the localization select symbol.
     */
    public void draw(Shape s, BasicStroke bs);
    
    // NOT IN USE
    public void draw2(int x, int y);
  
    
    /**
     * test
     */
    public void setImage2();
    
    /**
     * test
     */
    public void resetImageCanvas();
    
    /**
     * setFocus
     */
    public void setFocus();
    
    // Localization
    public void setCanvasOverlayLocalizationSymbolProperties(Double x1, Double x2,
            Double x3, Double x4, BasicStroke x5, Color lineColor, Color positionTextColor);
    public void drawTest(Line2D.Double line1, Line2D.Double line2, BasicStroke bs);
    public void setCanvasOverlayLocalizationRenderStatus(boolean status);
    public void setCanvasOverlayLocalizationRenderPositionStatus(boolean sta);
    public void setCanvasOverlayLocalizationList(ArrayList<StudyDbLocalizationM> list);

    // roi distance measurement
    public void setCanvasROIDistanceDrawingStatus(boolean status);
    public void setCanvasROIDistanceUpdateStatus(boolean status);
    public void setCanvasROIDistanceGrabSymbols(boolean status);
    public void setCanvasROIDistanceDrawingValue(int x1, int y1, int x2, int y2);
    public void setCanvasROIDistanceDrawingValue(Line2D l);
    public void setCanvasROIDistanceUpdateValue(ArrayList<StudyDbROID> list);

     // roi area measurement
    public void setCanvasROIAreaUpdateStatus(boolean status);
    public void setCanvasROIAreaUpdateTextStatus(boolean status);
    public void setCanvasROIAreaGrabSymbols(boolean status);
    public void setCanvasROIAreaDrawingValue(Line2D l);
    public void setCanvasROIAreaUpdateValue(ArrayList<StudyDbROIV> list);
    
    // roi pixel value measurement
    public void setCanvasROIPixelValueMeanDrawingStatus(boolean status);
    public void setCanvasROIPixelValueMeanUpdateStatus(boolean status);
    public void setCanvasROIPixelValueMeanUpdateValue(ArrayList<StudyDbROIPixelValueD> list);
    
    /*
     */
    public void setCanvasETColor();
    
            
    /** Adds a mouse listener object.
     * @param ml the MouseListener.
     */
    //public void addMouseListener(MouseListener ml);
    
    /** Adds a mouse motion listener object.
     * @param ml the MouseMotionListener.
     */
    //public void addMouseMotionListener(MouseMotionListener e);
    
    /** Removes a mouse listener object.
     * @param ml the MouseLister.
     */
    //public void removeMouseListener(MouseListener ml);
    
    /** Removes a mouse motion listener object.
     * @param ml the MouseMotionLister.
     */
    //public void removeMouseMotionListener(MouseMotionListener ml);
    
    /** Adds a PropertyChangeListener object.
     * @param pc the PropertyChangeListener object.
     */
    //public void addPropertyChangeListener(PropertyChangeListener pc);
    
    /** Adds a VetoableChangeListener object.
     * @param vl the VetoableChangeListener.
     */
    //public void addVetoableChangeListener(VetoableChangeListener vl);
    
    /** Removes a PropertyChangeListener object.
     * @param pc the PropertyChangeListener object.
     */
    //public void removePropertyChangeListener(PropertyChangeListener pc);
    
    /** Removes a VetoableChangeListener object.
     * @param ml the VetoableChangeListener object.
     */
    //public void removeVetoableChangeListener(VetoableChangeListener vl);
    
    /** Sets the cursor.
     * @param cursor the Cursor object.
     */
    //public void setCursor(Cursor cursor);
    
    /** Sets the clip shape on the destination image
     * @param clipshape the shape of the clipping area
     */
    //public void setClip(Shape clipshape);
    
    /** Draws the specified shape on the destination image
     * @param shape the shape to be drawn
     */
    //public void draw(Shape shape);
}
