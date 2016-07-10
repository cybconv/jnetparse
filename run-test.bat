@echo off
set LIB=jnetparse.jar
set CP=classes;%LIB%
java -classpath %CP% Test

set LIB=
set CP=
@echo on