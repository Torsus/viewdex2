/*
 * StudyDbInterface.java
 * Created on 20 juni 2007
 * Author Sune Svensson
 *
 */

package mft.vdex.ds;

public interface StudyDbStackType {
    
    /**
     * The StudyDbStackTypeInterface specifies the stack_type constants.
     */
     
    public static final int STACK_TYPE_SINGLE_IMAGE = 0;
    public static final int STACK_TYPE_STACK_IMAGE = 1;
    public static final int STACK_TYPE_MULTI_FRAME_STACK_IMAGE = 2;
}
