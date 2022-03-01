@version 2.56
################################################################################
#                                 ViewDEX 2                                    #
#                                                                              #
# ViewDEX is a Java-based software for presentation and evaluation of medical  #
# images in observer performance studies. It has been developed at Sahlgrenska #
# University Hospital, University of Gothenburg and Södra Älvsborg Hospital.   #
#                                                                              #
# ViewDEX is free for all non-commercial use. However, we ask you to include   #
# the following references to ViewDEX in publications describing studies that  #
# have been performed using ViewDEX.                                           #
#                                                                              #
# A Svalkvist, S Svensson, M Håkansson, M Båth and L G Månsson, "ViewDEX:a     #
# status report", Radiat. Prot. Dosimetry 169(1-4):38-45,2016.                 #
#                                                                              #
# M Håkansson, S Svensson, S Zachrisson, A Svalkvist, M Båth and L G Månsson,  #
# "ViewDEX: an efficient and easy-to-use software for observer performance     #
# studies", Radiat. Prot. Dosimetry 139(1-3):42-51, 2010.                      #
#                                                                              #
# S Börjesson, M Håkansson, M Båth, S Kheddache, S Svensson, A Tingberg,       #
# A Grahn, M Ruschin, B Hemdal, S Mattsson and L G Månsson, "A software        #
# tool for increased efficiency in observer performance studies in radiology", #
# Radiat. Prot. Dosimetry 114(1-3):45-52, 2005.                                #
#                                                                      	       #
################################################################################ 




##### Running the program
No installation is needed for ViewDEX, and the program can run from any location
(harddrive, USB, CD,...) as long as the hierarchy of all files in the directory
is kept.

To run the program, on Windows system execute the file ViewDEX.exe
To run the program, on Linux system execute the file ViewDEX.sh.
To run the program, on macOS execute the file ViewDEX.sh.

The software comes with two test studies. For each test study there are two
versions of the study property file, the only difference being the settings
of the graphical components, altered to fit two different monitor resolutions
(1280x1024, 2048x1536). To run the studies, enter LOGIN name.

 "sam"  (example of visual grading study 1280x1024) or
 "sally"  (example of roc study 1280x1024) or
 "ted"  (example of free response ROC study 1280x1024)

 "sam2"  (example of visual grading study 2048x1536) or
 "sally2"  (example of roc study 2048x1536) or
 "ted2"  (example of free response ROC study 2048x1536)

macOS distribution have additional files for the following resolutions,
1400x900 and 2880x1800.

In order to change to a study with graphical properties more appropriate for
a 3 MP monitor, the user property file has to be changed (see below).

##### Basic functionality
# To change image, click "NEXT" (may require that task check boxes are marked).

# To make a localisation mark in an image: "ctrl and left mouse button".

# To erase a marking, choose the marking by clicking on it with the left mouse
  button, then use the right mouse button to display the context menu. Choose
  "localisation - delete".

# To erase all markings in a case, click the "erase all" button.
  (This feature may be disabled within the property file).

# To alter window settings, hold down the right mouse button and move the cursor
  in the image canvas. To reset the window settings, the reset button in the
  function panel can be used. If multiple values for window settings are available
  in the DICOM file, buttons for those values can be created by enabling this
  function in the property file.

# To use the zoom function, either click on the fast choice buttons in the
  function panel or use right mouse button to display the context menu and choose
  "zoom in" or "zoom out". Left mouse button is then used to zoom. Use the reset
  button in the function panel to reset the zoom settings.
 
# To use the pan function, either click on the fast choice button in the function
  panel or click the right mouse button in the the image canvas and choose "Pan"
  in the context menu. Hold down the left mouse button and move the
  cursor to pan the image. Use the reset button in the task panel to reset
  the settings.

# To start or stop the cine-loop function, either click on the fast choice buttons
  in the function panel or do a right mouse button click to display the context
  menu and choose "cine-loop start or stop". In the function panel the cine-loop
  is started by choosing one of the "speed" buttons. 80 is the fastest cine-loop
  (80 ms/image) and 1000 the slowest (1000 ms/image). The specific times are
   definded in the property file.

# To make a distance measurement, press "A" and left mouse button at the same time
  as cursor is moved. Annotation will disappear with next mouse click event.

# To draw a circular ROI for mean pixel value measurement, press "Q" and left mouse
  button at the same time as the cursor is moved. Annotation will disappear with
  next mouse click event.

# To draw a ROI of variable shape for mean pixel value and area measurements, press
  "V" and left mouse button at the same time as the cursor is moved. Annotation
  will disappear with next mouse click event.

##### Configuring a new study
A new study is configured by creating a study property file named "nn.properties"
in the sub directory "resources", where "nn" is an arbitrary name of the study.
In such a study property file, all the properties of the study are defined. The
six existing example files ("VGC 1280x1024.properties", "ROC 1280x1024.properties",
"FROC 1280x1024.properties", "VGC 2048x1536.properties", "ROC 2048x1536.properties"
and "FROC 2048x1536.properties") contain information about how to define the
properties. The macOS distribution have additional files for the following
resolutions, 1440x900 and 2880x1800. The file "user.properties", located in the
same sub directory, is used to give readers access to specific studies. The file
contains information about how this is done. All property files are ascii files,
readable by any text editor.

##### Description of the distribution
The contents of the root directory of ViewDEX.

imagedb            			-  Directory for image databases.
lib             			-  Library files directory.
lib\64\jre1.8.0_191                     -  Java run time libraries (Windows)
lut					-  Examples of color-LUT files	
resources          			-  Study and user properties.
ViewDEX.exe        			-  Start file for Windows.
README.txt         			-  Readme file.
license.txt        			-  License information.
icons                                   -  Directory for icons.
lib/{linux,macos}/jre1.6.0              -  Java run time libraries (Linux and macOS)
ViewDEX.jar                             -  Class file (Linux and macOS).
ViewDEX.sh                              -  Start file (Linux and macOS).
ViewDEX.l4j.ini (Windows)               -  Program launcher configuration file.



## imagedb
Suggested directory for the image databases that the studies are using. Each
study has a main database containing all the images for the study, but can also
have a demo database usually containing a smaller amount of images for training.

## lib
Library files for the application.

## lib\64\jre1.8.0_191 (Windows)
The java runtime libraries for the application.

## resources
Directory containing the following property files.

1. user.properties
Definition of the users and the studies to perform.

user1.name   - name of user number 1.
user1.study  - name of the study to perform for user number 1.

Examples
user1.name = sally
user1.study = VGC 1280x1024
user2.name = sam
user2.study = ROC 1280x1024

2. [study].properties
Definition of properties for configuration of a study.
Each study has its own [study].properties file. eg.
"ROC 1280x1024.properties".

## Start file .\ViewDEX.exe (Windows)
Executable file that start the program.

## lib/{linux, macos}/jre1.6.0 (Linux and macOS)
The java runtime libraries for the application.

## Start file ./ViewDEX.sh (Linux and macOS)
Script file that start the program.

## Class file ./ViewDEX.jar
Class file containing the applications compiled code.

## ViewDEX.l4j.ini (Windows)
Configuration file to pass jvm (Java Virtual Machine) options at runtime to
the ViewDEX application. The amount of memory that the program uses can be
configured (Initial and maximum heapsize). For example, if the maximum heapsize 
is set larger than the amount of memory available on the computer,
the software will not launch.


######

Magnus Båth
Sahlgrenska University Hospital
Department of Medical Physics and Biomedical Engineering
magnus.bath@vgregion.se

Angelica Svalkvist
Sahlgrenska University Hospital
Department of Medical Physics and Biomedical Engineering
angelica.svalkvist@vgregion.se

Sune Svensson
Sahlgrenska University Hospital
Department of Medical Physics and Biomedical Engineering
sune.l.svensson@vgregion.se

Markus Håkansson
Södra Älvsborg Hospital
Department of Diagnostic Radiology
markus.hakansson@vgregion.se

Sara Asplund
NU Hospital Group
Section of Diagnistic Imaging and Functional Medicine
sara.asplund@vgregion.se

Lars Gunnar Månsson
Sahlgrenska University Hospital
Department of Medical Physics and Biomedical Engineering
lars.gunnar.mansson@vgregion.se

#####
Ingvar Andersson, Sara Börjesson, Anna Carlander, Johanna Dalmo, Valeria Fisichella,
Anna Grahn, Jonny Hansson, Mikael Hellström, Bengt Hemdal, Åse A. Johnsson,
Susanne Kheddache, Roger Lampa, Kerstin Ledenius, Jens Lönnblad, Sören Mattsson,
Michael Moores, Anna Olsson, Mark Rushin, Patrik Sund, Tony Svahn, Christina Söderman,
Anne Thilander Klang, Pontus Timberg, Anders Tingberg and Jenny Vikgren 
are acknowledged for valuable input and feed back. 

The development of ViewDEX has been supported by grants from the Swedish Radiation 
Safety Authority, the King Gustav V Jubilee Clinic Cancer Research Foundation and
the Commission of the European Communities.


-- end