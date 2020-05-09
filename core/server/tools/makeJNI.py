#!/usr/bin/env python 
# -*- coding: utf-8 -*-

import os
import shine
import shutil

if __name__ == '__main__':
	cwd=os.getcwd()
	print(cwd)

	#commonTool
	os.chdir(cwd + '/../toolProject/commonTool/src/main/java/com/home/commonTool/extern')
	os.system('javac -h ./ ToolExternMethodNative.java')
	os.remove('ToolExternMethodNative.class')
	targetPath=cwd+'/../projectC/commonGame/ToolExternMethodNative.h'

	if os.path.exists(targetPath):
		os.remove(targetPath)
	shutil.move('com_home_commonTool_extern_ToolExternMethodNative.h',targetPath)

	#commonBase
	os.chdir(cwd + '/../project/commonBase/src/main/java/com/home/commonBase/extern')
	os.system('javac -h ./ ExternMethodNative.java')
	os.remove('ExternMethodNative.class')
	targetPath=cwd+'/../projectC/commonGame/ExternMethodNative.h'

	if os.path.exists(targetPath):
		os.remove(targetPath)
	shutil.move('com_home_commonBase_extern_ExternMethodNative.h',targetPath)

	
	#base
	os.chdir(cwd + '/../project/base/src/main/java/com/home/base/extern')
	if os.path.exists('GExternMethodNative.java'):
		os.system('javac -h ./ GExternMethodNative.java')
		os.remove('GExternMethodNative.class')
		targetPath=cwd+'/../projectC/game/GExternMethodNative.h'
		if os.path.exists(targetPath):
			os.remove(targetPath)
		shutil.move('com_home_base_extern_GExternMethodNative.h',targetPath)

	print("OK")



