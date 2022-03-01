/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mft.vdex.dialog;

import java.util.Random;
import javax.swing.SwingWorker;
import mft.vdex.ds.StudyDb;

/**
 *
 * @author sune
 */
public class StackSortingProgressMonitorWorker extends SwingWorker<Void, Void> {
    StudyDb studyDb;
    
    public  StackSortingProgressMonitorWorker(StudyDb studyDb){
        this.studyDb = studyDb;    
    }

    @Override
    public Void doInBackground() {
        Random random = new Random();
        int progress = 0;
        //Initialize progress property.
        setProgress(0);
        //Sleep for at least one second to simulate "startup".
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        
        int totalCnt = studyDb.getSortStackTotalCnt();
        int currentCnt = studyDb.getSortStackCurrentCnt();
        float val = ((currentCnt * 100) / totalCnt);
        progress = Math.round(val);
       
        while (progress < 100) {
            //Sleep for up to one second.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignore) {
            }
            setProgress(progress);
        }
        return null;
    }

    @Override
    public void done() {
        setProgress(0);
    }
}
