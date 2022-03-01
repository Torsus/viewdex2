/* @(#) CatBusyCursorEvent.java 08/31/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.event;
 
import java.awt.*;
import java.io.*;


public class CatBusyCursorEvent extends java.util.EventObject implements Serializable{
  protected boolean cursorstatus;
  
  /**  Constructor for BusyCursorStatus
     * @param status of the cursor to display.
     */
  public CatBusyCursorEvent(Object obj, boolean status){
    super(obj);
    cursorstatus = status;
  }

  /** Returns the status too set the cursor
    * @return the status.
    */
  public boolean getStatus(){
    return cursorstatus;
  }
}

  

