cd `dirname $0`
cd ../../toolProject/shine8
mvn install
cd ../../toolProject/shineTool
mvn dependency:copy-dependencies