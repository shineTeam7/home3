#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import ctypes, sys
import shine
import os

createDic = ['/server/toolProject/shine8/src/main/java']

pathDic = {
	'/server/toolProject/shine8/src/main/java/com': '/server/project/shine/src/main/java/com',
	'/server/testProject/commonBase8/src': '/server/project/commonBase/src',
	'/server/testProject/commonSceneBase8/src': '/server/project/commonSceneBase/src',
	'/server/testProject/commonGame8/src': '/server/project/commonGame/src',
	'/server/testProject/commonCenter8/src': '/server/project/commonCenter/src'
}

if shine.is_admin():
	os.chdir(os.path.dirname(os.getcwd()))
	rootPath = os.getcwd()
	
	#先创建文件夹
	for key in createDic:
		if not os.path.exists:
			os.makedirs(rootPath + key)
		else:
			print('The dir', rootPath + key, 'is exist')
	
	shine.link_dir(pathDic, rootPath, rootPath)
	os.system("pause")
else:
	if shine.is_python3():
		ctypes.windll.shell32.ShellExecuteW(None, "runas", sys.executable, __file__, None, 1)
	else:#in python2.x
		ctypes.windll.shell32.ShellExecuteW(None, u"runas", unicode(sys.executable), unicode(__file__), None, 1)
