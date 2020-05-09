%~d0

cd %~d0%~p0
chcp 65001
java -Dfile.encoding=UTF-8 -jar proguard.jar -libraryjars "%JAVA_HOME8%\jre\lib\rt.jar" @encrypt.cfg