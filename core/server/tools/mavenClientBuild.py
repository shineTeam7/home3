import os
import shine

def mvn(cmd):
	os.system('mvn ' + cmd)

if __name__ == '__main__':
	os.chdir(os.getcwd() + '/../clientProject')
	mvn('clean')
	mvn('package')
	shine.copyfile(os.getcwd() + '/client/target/all-0.0.1-SNAPSHOT-jar-with-dependencies.jar',os.getcwd() + '/../bin/jar/client.jar')
	os.system("pause")