cd `dirname $0`
java -jar proguard.jar -libraryjars "$JAVA_HOME8/jre/lib/rt.jar" @encrypt.cfg