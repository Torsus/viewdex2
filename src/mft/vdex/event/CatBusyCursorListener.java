/* @(#) CatBusyCursorListener.java 08/31/2004
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.event;

import mft.vdex.event.CatBusyCursorEvent;


public interface CatBusyCursorListener extends java.util.EventListener{
   /** Called when a busycursorevent is fired.
     * @param e the BusyCursorEvent object which carries the status too
     * set the cursor.
     */
   public void setCursor(CatBusyCursorEvent e);
}
