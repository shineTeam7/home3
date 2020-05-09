import os
import shine

def mvn(cmd):
	os.system('mvn ' + cmd)

if __name__ == '__main__':
	os.chdir(os.getcwd() + '/../project')
	mvn('clean')
	mvn('package')
	shine.copyfile(os.getcwd() + '/all/target/all-0.0.1-SNAPSHOT-jar-with-dependencies.jar',os.getcwd() + '/../bin/jar/game.jar')
	os.system("pause")