/* @(#) ImageCanvasNOTINUSE.java 05/12/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.viewer;

import java.awt.geom.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.*;
import javax.media.jai.Interpolation;
import javax.swing.*;
import mft.vdex.app.ViewDex;

public class ImageCanvasNOTINUSE extends JComponent implements ImageCanvasManipulator{
    public final static int MAX_WIDTH = 2048;
    public final static int MAX_HEIGHT = 2048;
    protected ViewDex appFrame;
    transient protected PlanarImage originalImage;
    protected PlanarImage offscreenImage;
    protected PlanarImage displayImage;
    transient protected BufferedImage originalBImage;
    protected AffineTransform atx = new AffineTransform();
    protected boolean imageDrawn = false;
    //protected int interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
    protected int interpolationMode = Interpolation.INTERP_NEAREST;
    protected int displayMode = CanvasControlMode.DISPLAY_ORIG;
    protected int flipMode = CanvasFlipMode.FLIP_NORMAL;
    
    protected int panX =0, panY =0;
    protected Point scrollAnchor  = new Point(0,0);
    protected boolean scrollOn = true;
    protected Point vpPos = new Point(0,0);
    protected Point panOffset = new Point(0,0);
   
    protected int width, height;
    private int orgImageWidth;
    private int orgImageHeight;
    private int window, level;
    
    public ImageCanvasNOTINUSE(){
    }
    
    /** Creates a new instance of ImageCanvas */
    public ImageCanvasNOTINUSE(ViewDex appFrame) {
        this.appFrame = appFrame;
        
        init();
    }
    
    private void init(){
    }
    
    public synchronized void setImage(PlanarImage img){
        //reset();
        originalImage = img;
        //offscreenImage = img;
        //displayImage = img;
        orgImageWidth = originalImage.getWidth();
        orgImageHeight = originalImage.getHeight();
        //repaint();
        paintImage();
    }
    
     /** Paints the currently set image. Applies the current display
      * and filp modes before painting.
      */
    public void paintImage() {
        //System.out.println("CatImageCanvas: paintImage");
        if(originalImage != null)
            doDisplayModeAndFlip(orgImageWidth, orgImageHeight);
        //displayImage = offscreenImage;
        //repaint();
    }
    
    public void doDisplayModeAndFlip(int imageWidth, int imageHeight){
        //System.out.println("CatImageCanvas: doDisplayModeAndFlip");
        
        width = this.getBounds().width;
        height = this.getBounds().height;
        double magX= (double)width/(double)imageWidth;
        double magY = (double)height/(double)imageHeight;
        double mag = 1.0;
        int bufferWid = width, bufferHt=height;
        
        AffineTransform dispModeAtx = AffineTransform.getTranslateInstance(0.0,0.0);
        
        switch(displayMode){
            case CanvasControlMode.DISPLAY_ORIG:
                bufferWid = imageWidth;
                bufferHt = imageHeight;
                break;
            case CanvasControlMode.DISPLAY_SCALED:
                mag = (magY > magX)? magX:magY;
                dispModeAtx.setToScale(mag, mag);
                bufferWid = (int)(imageWidth*mag);
                bufferHt = (int)(imageHeight*mag);
                break;
            case CanvasControlMode.DISPLAY_TO_FIT:
                dispModeAtx.setToScale(magX, magY);
                bufferWid = width;
                bufferHt = height;
                break;
            case CanvasControlMode.DISPLAY_HALF_SIZE:
                magX = 0.5;
                magY = 0.5;
                dispModeAtx.setToScale(magX, magY);
                bufferWid = (int)(imageWidth * 0.5);
                bufferHt = (int)(imageHeight * 0.5);
                break;
            default:
                break;
        }
        
        //AffineTransform at = createFlipTransform(width, height);
        //dispModeAtx.concatenate(at);
        //applyTransform(dispModeAtx);
        
        setTransform(dispModeAtx);
        
        // Create the offscreenImage
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(originalImage);
        pb.add(dispModeAtx);
        pb.add(Interpolation.getInstance(interpolationMode));
        RenderedOp op = JAI.create("affine", pb);
        offscreenImage = op.createInstance();
        
        // test
        update();
    }
    
    
    public AffineTransform createFlipTransform(int imageWid, int imageHt){
        //System.out.println("CatImageCanvas: createFlipTransform");
        AffineTransform at = new AffineTransform();
        switch(flipMode){
            case CanvasFlipMode.FLIP_NORMAL:
                break;
            case CanvasFlipMode.FLIP_TOP_BOTTOM:
                at =  new AffineTransform(new double[] {1.0,0.0,0.0,-1.0});
                at.translate(0.0, -imageHt);
                break;
            case CanvasFlipMode.FLIP_LEFT_RIGHT :
                at = new AffineTransform(new double[] {-1.0,0.0,0.0,1.0});
                at.translate(-imageWid, 0.0);
                break;
            case CanvasFlipMode.FLIP_TOP_BOTTOM_LEFT_RIGHT:
                at = new AffineTransform(new double[] {-1.0,0.0,0.0,-1.0});
                at.translate(-imageWid, -imageHt);
                break;
            default:
        }
        return at;
    }
    
     /** 
     * Applies the current display and flip mode settings to the displayed image.
     * The display mode and flip mode settings are applied by
     * using the affine transformation.
     * First computes an affine transformation for current display mode setting.
     * It then creates a BufferedImage object size of which is compted using this transformation,
     * and draws the current image on this BufferedImage.
     *
     * This method then calls the createFlipTransform() method to create an affine transformtion
     * for the current flip mode setting. It then applies the flips transforamtion to the BufferedImage
     * object, and then displays it.
     *
     * @param imageWidth the image width.
     * @param imageHeight the image height.
     * @param if successful returns true.
     */
    public void doDisplayModeAndFlip3(int imageWidth, int imageHeight){
        //System.out.println("CatImageCanvas: doDisplayModeAndFlip");
        
        RenderedOp op = null;
        
        width = this.getBounds().width;
        height = this.getBounds().height;
        float magX= (float)width/(float)imageWidth;
        float magY = (float)height/(float)imageHeight;
        int bufferWid = width;
        int bufferHt = height;
        
       
        float transx = 0.0f;
        float transy = 0.0f;
        Interpolation interpolation = getInterpolationInstance();
        
        
        if(displayMode == CanvasControlMode.DISPLAY_ORIG){
            float mag = 1.0f;
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(originalImage);
            pb.add(mag);
            pb.add(mag);
            pb.add(transx);
            pb.add(transy);
            pb.add(interpolation);
            op = JAI.create("scale", pb);
        }
        else{
            if(displayMode == CanvasControlMode.DISPLAY_TO_FIT){
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(originalImage);
                pb.add(magX);
                pb.add(magX);
                pb.add(transx);
                pb.add(transy);
                pb.add(interpolation);
                op = JAI.create("scale", pb);
            }
            else{
                if(displayMode == CanvasControlMode.DISPLAY_SCALED){
                    float mag = (magY > magX)? magX:magY;
                    ParameterBlock pb = new ParameterBlock();
                    pb.addSource(originalImage);
                    pb.add(mag);
                    pb.add(mag);
                    pb.add(transx);
                    pb.add(transy);
                    pb.add(interpolation);
                    op = JAI.create("scale", pb);
                }
                else{
                    if(displayMode == CanvasControlMode.DISPLAY_HALF_SIZE){
                        magX = 0.5f; magY = 0.5f;
                        ParameterBlock pb = new ParameterBlock();
                        pb.addSource(originalImage);
                        pb.add(magX);
                        pb.add(magX);
                        pb.add(transx);
                        pb.add(transy);
                        pb.add(interpolation);
                        op = JAI.create("scale", pb);
                    }
                }
            }
        }
        offscreenImage = op.createInstance();
    }
    
    /** 
     * Applies the current display and flip mode settings to the displayed image.
     * The display mode and flip mode settings are applied by
     * using the affine transformation.
     * First computes an affine transformation for current display mode setting.
     * It then creates a BufferedImage object size of which is compted using this transformation,
     * and draws the current image on this BufferedImage.
     *
     * This method then calls the createFlipTransform() method to create an affine transformtion
     * for the current flip mode setting. It then applies the flips transforamtion to the BufferedImage
     * object, and then displays it.
     *
     * @param imageWidth the image width.
     * @param imageHeight the image height.
     * @param if successful returns true.
     */
    public void doDisplayModeAndFlip2(int imageWidth, int imageHeight){
        //System.out.println("CatImageCanvas: doDisplayModeAndFlip");
        
        RenderedOp op = null;
        
        width = this.getBounds().width;
        height = this.getBounds().height;
        float magX= (float)width/(float)imageWidth;
        float magY = (float)height/(float)imageHeight;
        int bufferWid = width;
        int bufferHt = height;
        
       
        float transx = 0.0f;
        float transy = 0.0f;
        Interpolation interpolation = getInterpolationInstance();
        
        
        if(displayMode == CanvasControlMode.DISPLAY_ORIG){
            float mag = 1.0f;
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(originalImage);
            pb.add(mag);
            pb.add(mag);
            pb.add(transx);
            pb.add(transy);
            pb.add(interpolation);
            op = JAI.create("scale", pb);
        }
        else{
            if(displayMode == CanvasControlMode.DISPLAY_TO_FIT){
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(originalImage);
                pb.add(magX);
                pb.add(magX);
                pb.add(transx);
                pb.add(transy);
                pb.add(interpolation);
                op = JAI.create("scale", pb);
            }
            else{
                if(displayMode == CanvasControlMode.DISPLAY_SCALED){
                    float mag = (magY > magX)? magX:magY;
                    ParameterBlock pb = new ParameterBlock();
                    pb.addSource(originalImage);
                    pb.add(mag);
                    pb.add(mag);
                    pb.add(transx);
                    pb.add(transy);
                    pb.add(interpolation);
                    op = JAI.create("scale", pb);
                }
                else{
                    if(displayMode == CanvasControlMode.DISPLAY_HALF_SIZE){
                        magX = 0.5f; magY = 0.5f;
                        ParameterBlock pb = new ParameterBlock();
                        pb.addSource(originalImage);
                        pb.add(magX);
                        pb.add(magX);
                        pb.add(transx);
                        pb.add(transy);
                        pb.add(interpolation);
                        op = JAI.create("scale", pb);
                    }
                }
            }
        }
        offscreenImage = op.createInstance();
    }
            
    public void setBufferedImage(BufferedImage img){
        originalBImage = img;
    }
    
    public void setDisplayImage(RenderedOp op){
        displayImage = op.createInstance();
        repaint();
    }
    
    public void setTransform(AffineTransform at){
        atx = at;
    }
    
    public AffineTransform getTransform(){
        return atx;
    };
    
    public void reset(){
        atx = new AffineTransform();
        panX =0; panY =0;
    }
    
    
    public void paintComponent(Graphics gc){
        Graphics2D g = (Graphics2D)gc;
        Rectangle rect = this.getBounds();
        //if((width != rect.width) || (height != rect.height)){
        //    double magx = rect.width/(double)width ;
        //    double magy = rect.height/(double)height ;
        //}
        g.setColor(Color.black);
        g.fillRect(0,0,rect.width, rect.height);
        //atx =  AffineTransform.getTranslateInstance(panX,panY);
        if(displayImage != null)
            g.drawRenderedImage(displayImage, new AffineTransform());
        imageDrawn = true;
    }
    
    /*
    public void applyTransform(AffineTransform at) {
        //System.out.println("CatImageCanvas: applyTransform(x)");
     
        this.atx = at;
        //applyTransform(offScrImage, at);
        //paintImage();
        //catMain.setWindowLevel(); dissabled 2004-02-17
        //displayImageGc.setColor(Color.black); // zzz
        repaint();  // trigger paintComponent
    }*/
    
    /**
     * Applies a specified tranformation and interpolation to the
     * displayImage and then call repaint.
     *
     * @param ri the image to render.
     * @param atx the transformation to be applied.
     */
    protected void applyTransform(RenderedImage ri, AffineTransform atx){
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(ri);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpolationMode));
        RenderedOp op = JAI.create("affine", pb);
        displayImage = op.createInstance();
        repaint();
    }
    
    /* Call the applyTransform method.
     * Called by CatZoom.scale() after atx.translate and atx.scale.
     *
     * @param atx the transformation.
     */
    public void applyTransform(AffineTransform atx){
        setTransform(atx);
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(offscreenImage);
        pb.add(atx);
        pb.add(Interpolation.getInstance(interpolationMode));
        RenderedOp op = JAI.create("affine", pb);
        displayImage = op.createInstance();
        repaint();
    }
    
    /* Applies the saved transformation and interpolationMode to
     * the offscreenimage and then call repaint.
     * Used to update the screen when a new interpolation is selected.
     *
     */
    public void setInterpolation(){
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(offscreenImage);
        pb.add(atx);
        pb.add(javax.media.jai.Interpolation.getInstance(interpolationMode));
        RenderedOp op = JAI.create("affine", pb);
        displayImage = op.createInstance();
        repaint();
    }
    
    
     /**
     * Create the displayImage and then call repaint.
     * 
     */
    protected void update(){
       setTransform(new AffineTransform());
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(offscreenImage);
        pb.add(new AffineTransform());
        pb.add(Interpolation.getInstance(interpolationMode));
        RenderedOp op = JAI.create("affine", pb);
        displayImage = op.createInstance();
        repaint();
    }
    
    /** Returns the OffscreenImage property.
     * @return the PlanarImage
     */
    public PlanarImage getOffScreenImage(){
        return offscreenImage;
    }
    
    /** Returns the canvas size.
     * @return the canvas size.
     */
    public Dimension getImageCanvasSize(){
        return new Dimension(this.getWidth(), this.getHeight());
    }
    
    /** Sets the interpolationMode property.
     * @param the interpolation type
     */
    public void setInterpolationMode(int interpolationmode){
        interpolationMode = interpolationmode;
    }
    
    /** Return the interpolation type.
     * @return the interpolation type
     */
    public int getInterpolationMode(){
        return interpolationMode;
    }
    
    /** Return an instance of the current selected interpolation mode.
     * @return an instance of the current selected interpolation mode.
     */
    public Interpolation getInterpolationInstance(){
        if(interpolationMode == Interpolation.INTERP_NEAREST)
            return Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        
        if(interpolationMode == Interpolation.INTERP_BILINEAR)
            return Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
        
        if(interpolationMode == Interpolation.INTERP_BICUBIC)
            return Interpolation.getInstance(Interpolation.INTERP_BICUBIC);
        
        if(interpolationMode == Interpolation.INTERP_BICUBIC_2)
            return Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2);
        
        return null;
    }
    
    /** Sets the panOffset property.
     * @param panOffset the offset by which the currently displayed image is moved
     * from the previous position.
     */
    public void setPanOffset(Point panoffset) {
        panOffset = panoffset;
    }
    
    /** Get the pan offset.
     * @return the panOffset value.
     */
    public Point getPanOffset(){
        return panOffset;
    }
    
    public void setDisplayMode(int displaymode){
        displayMode = displaymode;
    }
    
    public void setFlipMode(int displaymode){
        flipMode = displaymode;
    }
    
    public static RenderedOp scale(RenderedImage image,
        float magx, float magy,
        float transx, float transy){
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(magx);
        pb.add(magy);
        pb.add(transx);
        pb.add(transy);
        pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
        return JAI.create("scale", pb);
    }
    
    /* Set the window and level properties.
     */
    public void setWindowLevelValues(int win, int lev){
        window = win;
        level = lev;
    }
}
