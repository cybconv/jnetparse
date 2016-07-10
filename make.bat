@echo off
REM JAVA package: net.sf.jsecnet.util.net
set DEST=. 
set CLASSES=Address.java IPv4.java IPv6.java MAC.java ISO.java
set CLASSES=%CLASSES% NetUtils.java QoS.java MetaData.java TopicInfo.java
set CLASSES=%CLASSES% PasswordGenerator.java
set CLASSES=%CLASSES% Version.java GUI.java AboutGUI.java
set INCLUDE=net LICENSE.txt

@echo on
javac -Xlint:unchecked -Xlint:deprecation -d %DEST% %CLASSES%
jar cfm jnetparse.jar manifest.mf %INCLUDE% 

@echo off
set DEST=
set CLASSES=
set INCLUDE=
@echo on