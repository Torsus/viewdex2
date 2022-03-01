REM StudyTool

set JAVA_HOME=.\lib\32\jre1.6.0_03-32
REM set JAVA_HOME=.\lib\32\jre-8u31
REM set JAVA_HOME=.\lib\64\jre1.6.0_03-64
REM set JAVA_HOME=.\lib\64\jre1.7.0_25-64
set VDEX_HOME=.\ViewDEX.jar
REM set TABLE_LAYOUT_HOME=.\lib\tablelayout.jar
REM set DCM_HOME=.\lib\dcm4che.jar
REM set LOG4J_HOME=.\lib\log4j.jar
echo %PATH%
REM -Xms128m Initial heapsize
REM -Xmx1200m Maximum heapsize

REM Java 2D's use of DirectX
REM -Dsun.java2d.noddraw=true

REM -Xverify:none
REM -XX:PermSize=20m


%JAVA_HOME%\bin\java -Xms128m -Xmx1280m -Dsun.java2d.noddraw=true -Dswing.aatext=true -cp %VDEX_HOME% mft.vdex.app.ViewDex