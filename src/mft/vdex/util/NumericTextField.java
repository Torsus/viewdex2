/* @(#) NumericTextField.java 02/10/2008
 *
 * Copyright (c) 2008 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author downloaded from www.javaswing
 */
package mft.vdex.util;

import java.awt.Toolkit;
 
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class NumericTextField extends JTextField {	 
  //Add other constructors as required. If you do,
  //be sure to call the "addFilter" method
  public NumericTextField(String text, int columns){
    super(text, columns);
    addFilter();
  }
  
  public NumericTextField(){
    super();
    addFilter();
  }
 
  //Add an instance of NumericDocumentFilter as a 
  //document filter to the current text field
  private void addFilter(){
    ((AbstractDocument)this.getDocument()).
       setDocumentFilter(new NumericDocumentFilter());
  }
 
 
  class NumericDocumentFilter extends DocumentFilter{
    public void insertString(FilterBypass fb, 
      int offset, String string, AttributeSet attr) 
        throws BadLocationException {
 
      if(string == null) return;
      if(isStringNumeric(string)){
        super.insertString(fb, offset, string, attr);
      }
      else{
        Toolkit.getDefaultToolkit().beep();
      }
    }
 
    public void replace(FilterBypass fb, int offset, 
      int length, String text, AttributeSet attrs) 
        throws BadLocationException {
      if(text == null) return;
      if(isStringNumeric(text)){
        super.replace(fb, offset, length, text, attrs);
      }
      else{
        Toolkit.getDefaultToolkit().beep();
      }
    }
 
    private boolean isStringNumeric(String string){
     char[] characters = string.toCharArray();
     for(char c: characters){
       if(! Character.isDigit(c)){					 
         return false;
       }
     }
     return true;
    }
  }
}
