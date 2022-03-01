#!/bin/bash
## ViewDEX start
JAVA_HOME=./lib/macos/jre1.8.0_45/Contents/Home
#JAVA_HOME=./lib/linux/jre1.6.0_45
VDEX_HOME=./ViewDEX.jar

echo $JAVA_HOME
echo $VDEX_HOME
#echo $PATH

$JAVA_HOME/bin/java -Xms128m -Xmx4096m -Dsun.java2d.noddraw=true -Dswing.aatext=true -cp $VDEX_HOME mft.vdex.app.ViewDex
