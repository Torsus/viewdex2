/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mft.vdex.modules.vg;

import java.util.Comparator;

/**
 *
 * @author Sune Svensson
 */
public class VgTaskPanelResultComparator implements Comparator<VgTaskPanelResult>{
    public int compare(VgTaskPanelResult a, VgTaskPanelResult b){
         int val = 0;
         
         if(a.getTaskNb() < b.getTaskNb())
            val = -1;
        else
            if(a.getTaskNb() > b.getTaskNb())
                val =  1;
            else
                 if(a.getTaskNb() == b.getTaskNb())
                    val =  0;
        return val;
    }
}
