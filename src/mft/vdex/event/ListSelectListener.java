/* @(#) ListSelectListener.java 01/21/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */

package mft.vdex.event;

/*
 * ListSelectListener interface.
 */  
public interface ListSelectListener extends java.util.EventListener{
   /** This method is typically called whenever a list is selected.
     * The ListSelectEvent carries the list select event data. This
     * includes the list and the path.
     * @param e the ListSelectEvent.
     */
   public void load(ListSelectEvent e);
}
