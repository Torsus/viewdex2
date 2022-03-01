REM ViewDEX start
set JAVA_HOME=.\lib\64\jre1.6.0_03
set VDEX_HOME=.\ViewDEX.jar
echo %PATH%

%JAVA_HOME%\bin\java -Dsun.java2d.noddraw=true -Dswing.aatext=true -cp %VDEX_HOME% mft.vdex.app.ViewDex