/* @(#) ImageInfoPlanar.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */


package mft.vdex.util;

import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PackedColorModel;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;



/*
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
public class ImageInfoPlanar {
    
    /** Creates a new instance of ImageInfoPlanar */
    public ImageInfoPlanar() {
    }
    
    /**
     * Display som parmeters.
     */
    public void show(PlanarImage pi){
        System.out.println("ImageInfoPlanar.show");
        showImageInfo(pi);
        computeMinMax(pi);
    }
    
    private void showImageInfo(PlanarImage pi){
        
        // dimensions and coordinates.
        System.out.print("Dimensions: ");
        System.out.print(pi.getWidth()+"x"+pi.getHeight()+" pixels");
        
        // Remember getMaxX and getMaxY return the coordinate of the next point!
        System.out.println(" (from "+pi.getMinX()+","+pi.getMinY()+" to " +
                (pi.getMaxX()-1)+","+(pi.getMaxY()-1)+")");
        
        // Tiles number, dimensions and coordinates. Will print this only
        // if there are more than one tile.
        if (pi.getNumXTiles()*pi.getNumYTiles() > 1) {
            System.out.print("Tiles: ");
            System.out.print(pi.getTileWidth()+"x"+pi.getTileHeight()+" pixels"+
                    " ("+pi.getNumXTiles()+"x"+pi.getNumYTiles()+" tiles)");
            System.out.print(" (from "+pi.getMinTileX()+","+pi.getMinTileY()+" to "+
                    pi.getMaxTileX()+","+pi.getMaxTileY()+")");
            System.out.println(" offset: "+pi.getTileGridXOffset()+","+
                    pi.getTileGridXOffset());
        }
        
        // Display info about the SampleModel of the image.
        SampleModel sm = pi.getSampleModel();
        System.out.println("Number of bands: "+sm.getNumBands());
        System.out.print("Data type: ");
        switch(sm.getDataType()) {
            case DataBuffer.TYPE_BYTE:      System.out.println("byte");
            break;
            case DataBuffer.TYPE_SHORT:     System.out.println("short");
            break;
            case DataBuffer.TYPE_USHORT:    System.out.println("ushort");
            break;
            case DataBuffer.TYPE_INT:       System.out.println("int");
            break;
            case DataBuffer.TYPE_FLOAT:     System.out.println("float");
            break;
            case DataBuffer.TYPE_DOUBLE:    System.out.println("double");
            break;
            case DataBuffer.TYPE_UNDEFINED: System.out.println("undefined");
            break;
        }
        
        // Display info about the ColorModel of the image.
        ColorModel cm = pi.getColorModel();
        if (cm != null) {
            System.out.print("Colormap type: ");
            if (cm instanceof DirectColorModel)
                System.out.println("DirectColorModel");
            else if (cm instanceof PackedColorModel)
                System.out.println("PackedColorModel");
            else if (cm instanceof IndexColorModel) {
                IndexColorModel icm = (IndexColorModel)cm;
                System.out.println("IndexColorModel with "+icm.getMapSize()+" entries");
            } else if (cm instanceof ComponentColorModel)
                System.out.println("ComponentColorModel");
            System.out.println("Number of color components: "+cm.getNumComponents());
            System.out.println("Bits per pixel: "+cm.getPixelSize());
            System.out.print("Transparency: ");
            switch(cm.getTransparency()) {
                case Transparency.OPAQUE:      System.out.println("opaque");
                break;
                case Transparency.BITMASK:     System.out.println("bitmask");
                break;
                case Transparency.TRANSLUCENT: System.out.println("translucent");
                break;
            } // end switch
        } else
            System.out.println("No color model.");
    }
    
    private void computeMinMax(PlanarImage pi){
        // Create a parameter block for extraction of the extrema.
        ParameterBlock pbMaxMin = new ParameterBlock();
        pbMaxMin.addSource(pi);
        RenderedOp extremaOp = JAI.create("extrema", pbMaxMin);

        // Get the extrema of all bands !
        double[] allMins = (double[])extremaOp.getProperty("minimum");
        double[] allMaxs = (double[])extremaOp.getProperty("maximum");
        for(int b=0;b<allMins.length;b++)
            System.out.println("Band "+b+" values are between "+allMins[b]+" and "+allMaxs[b]);
    }
}

