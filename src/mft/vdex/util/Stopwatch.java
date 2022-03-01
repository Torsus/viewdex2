/* @(#) Stopwatch.java 05/12/2003
 *
 * Copyright (c) 2006 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */

package mft.vdex.util;

public class Stopwatch{
  private long startTime = -1;
  private long stopTime = -1;
  private boolean running = false;
    
  public Stopwatch start(){
    startTime = System.currentTimeMillis();
    running = true;
    return this;
  }
  
  public Stopwatch stop(){
    stopTime = System.currentTimeMillis();
    running = false;
    return this;
  }
  /** returns elapsed time in milliseconds
   * if the watch never been started then
   * return zero
   */
  public long getElapsedTime(){
    if(startTime == -1){
      return 0;
    }
    if(running){
      return System.currentTimeMillis() - startTime;
    }else{
      return stopTime-startTime;
    }
  }
  
  public Stopwatch reset(){
    startTime = -1;
    stopTime = -1;
    running = false;
    return this;
  }
}
