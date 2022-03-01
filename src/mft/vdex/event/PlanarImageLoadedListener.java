/* @(#) PlanarImageLoadedListener.java 01/21/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */

package mft.vdex.event;

import mft.vdex.event.PlanarImageLoadedEvent;


  /*
   * The listener interface for the planarImageLoaded event.
   **/

public interface PlanarImageLoadedListener extends java.util.EventListener{
   /** Called when a planarImageLoaded event is fired.
     * @param e the PlanarImageLoadedEvent object which carries the PlanarImage object
     * loaded by the source.
     */
   public void imageLoaded(PlanarImageLoadedEvent e);
}
