%~d0

cd %~d0%~p0
java -jar proguard.jar -libraryjars "%JAVA_HOME8%\jre\lib\rt.jar" @encrypt.cfg
pause