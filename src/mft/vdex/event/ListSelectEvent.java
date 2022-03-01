/* @(#) ListSelectEvent.java 01/21/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */

package mft.vdex.event;

import java.io.*;
import java.awt.*;


/*
 * This class is an event-state class for ListSelect event.
 */
public class ListSelectEvent  extends java.awt.event.ActionEvent
       implements Serializable{
   File path;
   String[] fileList;

   public ListSelectEvent(Object obj, String command,File dir, String[] flist){
      super(obj,(int)AWTEvent.ACTION_EVENT_MASK, command);
      this.path = dir;
      this.fileList = flist;
   }

   public String[] getFileList(){
      return fileList;
   }
   public File getPath(){
      return path;
   }
}

