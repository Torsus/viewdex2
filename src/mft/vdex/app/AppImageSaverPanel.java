/* @(#) AppImageSaverPanel.java 09/03/2000
 * @version 1.0
 * @author Lawrence Rodrigues
 *
 */

package mft.vdex.app;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.media.jai.*;
//import com.sun.media.jai.codec.*;

/** Creates a panel for entering image saving related info, including
  * the file name and the image format.
  * @version 1.0  3 Sept 2000
  * @author Lawrence Rodrigues
  */
public class AppImageSaverPanel extends JPanel{
   public String[] tags = {"JPEG", "BMP","TIFF","PNG_GRAY","PNG_RGB","PNG_PALLETTE", "PNM"};
   protected String fileName = "default";
   protected RenderedImage plImage;
   protected RenderedImage dispImage;
   private JTextField fnField = null;
   private JList saveTypeList;

   public AppImageSaverPanel(){createUI();}

   public void setImage(PlanarImage pImage){ this.plImage = pImage;}

   public void setDisplayImage(PlanarImage pImage){
      this.dispImage = pImage.getAsBufferedImage();
   }

   public void saveDisplay(int ind){
      if(fnField.getText() != null)fileName = fnField.getText();
      save(dispImage, ind, fileName);
   }

   public void saveOrig(int ind){
      if(fnField.getText() != null)fileName = fnField.getText();
      save(plImage, ind, fileName);
   }

   private void createUI() {
      ListModel  saveTypeModel = new AbstractListModel(){
            @Override
         public int getSize(){
            if(tags == null) return 0;
            else return tags.length;
         }
            @Override
         public Object getElementAt(int index){
            if(tags == null) return null;
            if((index <0) || (index>tags.length)) return null;
            return tags[index];
         }
      };
      saveTypeList = new JList(saveTypeModel);
      saveTypeList.setForeground(Color.blue);
      saveTypeList.setSelectedIndex(0);
      MouseListener mouseListener = new MouseAdapter() {
            @Override
         public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
               int ind = saveTypeList.getSelectedIndex();
               String str = tags[ind];
               if(plImage == null) return;
               if(fnField.getText() != null)fileName = fnField.getText();
            }
         }
      };

      saveTypeList.addMouseListener(mouseListener);
      saveTypeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      fnField = new JTextField(12);
      fnField.setText(fileName);
      fnField.addActionListener(
           new ActionListener() {
            @Override
              public void actionPerformed(ActionEvent e){
                 int ind = saveTypeList.getSelectedIndex();
                 if(fnField.getText() != null)fileName = fnField.getText();
              }
           }
      );
      JButton saveb = new JButton("Save Orig Image");
      saveb.addActionListener(
           new ActionListener() {
            @Override
              public void actionPerformed(ActionEvent e){
                  int ind = saveTypeList.getSelectedIndex();
                  if(fnField.getText() != null)fileName = fnField.getText();
                  saveOrig(ind);
              }
           }
      );
      JButton saved = new JButton("Save Displayed Image");
      saved.addActionListener(
           new ActionListener() {
            @Override
              public void actionPerformed(ActionEvent e){
                  int ind = saveTypeList.getSelectedIndex();
                  if(fnField.getText() != null)fileName = fnField.getText();
                  saveDisplay(ind);
              }
           }
      );

      JLabel flab = new JLabel("Enter file", SwingConstants.LEFT);
      JLabel typelab = new JLabel("Format ", SwingConstants.LEFT);
      GridBagLayout   gb = new GridBagLayout() ;
      GridBagConstraints c = new GridBagConstraints();
      setLayout(gb);
      c.insets = new Insets(10,1,0,0);
      c.anchor = GridBagConstraints.NORTHEAST;
      c.weighty = 1.0; c.weightx = 0.1;
      c.gridwidth = 1;
      gb.setConstraints(flab,c);
      add(flab,c);
      c.weighty = 1.0; c.weightx = 1.0;
      c.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(fnField,c);
      add(fnField);
      c.gridwidth = 1;
      c.weighty = 1.0; c.weightx = 0.1;
      add(typelab);
      gb.setConstraints(typelab,c);
      c.fill = GridBagConstraints.BOTH;
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.weighty = 1.0; c.weightx = 1.0;
      add(saveTypeList);
      gb.setConstraints(saveTypeList,c);
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weighty = 0.1; c.weightx = 0.1;
      add(saveb);
      gb.setConstraints(saveb,c);
      add(saved);
      gb.setConstraints(saved,c);
   }
   protected void save(RenderedImage image, int ind, String filename) {
      try {
      switch(ind) {
        case 0:
          AppImageSaverJAI.saveAsJPEG(image, fileName);
          break;
       case 1:
          AppImageSaverJAI.saveAsBMP(image, fileName);
          break;
        case 2:
          AppImageSaverJAI.saveAsTIFF(image, fileName);
          break;
        case 3:
          AppImageSaverJAI.saveAsPNGGray(image, fileName);
          break;
        case 4:
          AppImageSaverJAI.saveAsPNGRGB(image, fileName);
          break;
        case 5:
          AppImageSaverJAI.saveAsPNGPalette(image, fileName);
          break;
        case 6:
          AppImageSaverJAI.saveAsPNM(image, fileName, true);
          break;
      }
      } catch(Exception e) {}
   }
}