/* @(#) PlanarImageLoadedEvent.java 01/21/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */

package mft.vdex.event;

import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.media.jai.*;



/*
 * The event-state class for the planarImageLoaded event. Depending on the source bean,
 * it can carry a single or multiple images.
 **/
public class PlanarImageLoadedEvent  extends java.util.EventObject
       implements Serializable{
   protected PlanarImage image;
   protected BufferedImage imagebuf;
   protected PlanarImage[] imageset;
   //cat
   protected int imagetype=0;

   /** Constructor for a single image.
     * @param obj the source object.
     * @param img the image to be sent to the target.
     */
   public PlanarImageLoadedEvent(Object obj, PlanarImage img, BufferedImage imgb){
      super(obj);
      image = img;
      imagebuf = imgb;
      imageset = new PlanarImage[] {img};
   }

   //cat
   public PlanarImageLoadedEvent(Object obj, PlanarImage img, 
                                       BufferedImage imgb, int type){
      super(obj);
      image = img;
      imagebuf = imgb;
      imageset = new PlanarImage[] {img};
      imagetype = type;
   }
   
  /**  Constructor for multiple images.
     * @param obj the source object.
     * @param img an array of images to be sent to the target.
     */
   public PlanarImageLoadedEvent(Object obj, PlanarImage[] img){
      super(obj);
      imageset = img;
      if((img != null) && (img.length >0))image = img[0];
   }

   /** Returns the image type, buffered image or planar image.
     * @return the type image.
     * @0 = buffered image.
     * @1 = planar image.
     */
   public int getImageType(){return imagetype;}
   
   /** Returns the planar image loaded by the source bean.
     * @return the planar image.*/
   public PlanarImage getPlanarImage(){return image;}

   /** Returns the buffered image loaded by the source bean.
     * @return the buffered image.*/
   public BufferedImage getBufferedImage(){return imagebuf;}
   
   /** Returns an array of planar images loaded by the source bean.
     * @return an array of planar images.
     */
   public PlanarImage[] getImages(){return imageset;}
}

