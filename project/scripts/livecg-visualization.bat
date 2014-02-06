@ECHO OFF

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;../lib/*
set CLASSPATH=%CLASSPATH%;../lib/batik/*
set CLASSPATH=%CLASSPATH%;../bin/main/
set CLASSPATH=%CLASSPATH%;../bin/test/
set CLASSPATH=%CLASSPATH%;../

%JAVA_HOME%\bin\java de.topobyte.livecg.ShowVisualization %1 %2 %3 %4 %5 %6 %7 %8 %9
