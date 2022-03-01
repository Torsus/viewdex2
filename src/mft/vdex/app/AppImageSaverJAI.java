/* @(#) AppImageSaverJAI.java 09/03/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */
package mft.vdex.app;

import java.io.*;
import java.awt.image.*;
import javax.media.jai.*;
import com.sun.media.jai.codec.*;
import java.awt.image.renderable.*;

/** A utility class that provides several static methods to
 * save an image to a local directory in different formats supported by the JAI Image I/O package.
 *
 * @version 1.0  3 Sept 2000
 * @author Lawrence Rodrigues
 */
public class AppImageSaverJAI {

    public static void saveAsTIFF(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".tiff")) {
            filename = file + ".tiff";
        }
        OutputStream out = new FileOutputStream(filename);
        TIFFEncodeParam param = new TIFFEncodeParam();
        ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", out, param);
        encoder.encode(image);
        out.close();
    }

    public static void saveAsBMP(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".bmp")) {
            filename = file + ".bmp";
        }
        OutputStream out = new FileOutputStream(filename);
        BMPEncodeParam param = new BMPEncodeParam();
        ImageEncoder encoder = ImageCodec.createImageEncoder("BMP", out, param);
        encoder.encode(image);
        out.close();
    }

    public static void saveAsPNGRGB(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".png")) {
            filename = file + ".png";
        }
        OutputStream out = new FileOutputStream(filename);
        PNGEncodeParam.RGB param = new PNGEncodeParam.RGB();
        ImageEncoder encoder = ImageCodec.createImageEncoder("PNG", out, param);
        encoder.encode(image);
        out.close();
    }

    public static void saveAsPNGGray(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".png")) {
            filename = file + ".png";
        }
        OutputStream out = new FileOutputStream(filename);
        PNGEncodeParam.Gray param = new PNGEncodeParam.Gray();
        ImageEncoder encoder = ImageCodec.createImageEncoder("PNG", out, param);
        encoder.encode(image);
        out.close();
    }

    public static void saveAsPNGPalette(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".png")) {
            filename = file + ".png";
        }
        OutputStream out = new FileOutputStream(filename);
        PNGEncodeParam.Palette param = new PNGEncodeParam.Palette();
        ImageEncoder encoder = ImageCodec.createImageEncoder("PNG", out, param);
        encoder.encode(image);
        out.close();
    }

    public static void saveAsJPEG(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".jpg")) {
            filename = file + ".jpg";
        }
        OutputStream out = new FileOutputStream(filename);
        //JPEGEncodeParam param = new JPEGEncodeParam();
        //ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", out, param);
        //encoder.encode(image);
        out.close();
    }

    public static void saveAsPNM(RenderedImage image, String file)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".pnm")) {
            filename = file + ".pnm";
        }
        OutputStream out = new FileOutputStream(filename);
        PNMEncodeParam param = new PNMEncodeParam();
        ImageEncoder encoder = ImageCodec.createImageEncoder("PNM", out, param);
        encoder.encode(image);
    }

    public static void saveAsPNM(RenderedImage image, String file, boolean rawOrAscii)
            throws java.io.IOException {
        String filename = file;
        if (!filename.endsWith(".pnm")) {
            filename = file + ".pnm";
        }
        PNMEncodeParam param = new PNMEncodeParam();
        param.setRaw(rawOrAscii);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(filename);
        pb.add("pnm");
        pb.add(param);
        JAI.create("filestore", pb);
    }
}
