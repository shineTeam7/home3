#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import ctypes, sys
import shine
import os

pathDic = {
	#corePath : linkPath
	#client editor
	'/client/game/Assets/Editor/shine': '/client/game/Editor/shine',
	'/client/game/Assets/Editor/shineLib': '/client/game/Editor/shineLib',
	'/client/game/Assets/Editor/commonGame': '/client/game/Editor/commonGame',
	
	#client game
	'/client/game/Assets/src/shine': '/client/game/src/shine',
	'/client/game/Assets/src/shineLib': '/client/game/src/shineLib',
	'/client/game/Assets/src/commonGame': '/client/game/src/commonGame',

	#client source
	'/client/game/Assets/source/commonGame': '/client/game/source/commonGame',

	#common
	'/common/commonConfig': '/common/commonConfig',
	'/common/data/commonData': '/common/data/commonData',
	'/common/data/shineData': '/common/data/shineData',
	
	#docs
	'/docs/commonDocs': '/docs',
	
	#server clientProject
	'/server/clientProject/commonClient': '/server/clientProject/commonClient',
	#server gmClientProject
	'/server/gmClientProject/commonGMClient': '/server/gmClientProject/commonGMClient',
	
	#server project
	'/server/project/commonBase': '/server/project/commonBase',
	'/server/project/commonCenter': '/server/project/commonCenter',
	'/server/project/commonGame': '/server/project/commonGame',
	'/server/project/commonLogin': '/server/project/commonLogin',
	'/server/project/commonManager': '/server/project/commonManager',
	'/server/project/shine': '/server/project/shine',

	#server toolProject
	'/server/toolProject/shine8': '/server/toolProject/shine8',
	'/server/toolProject/shineTool': '/server/toolProject/shineTool',
	'/server/toolProject/commonTool': '/server/toolProject/commonTool',

	#server tools
	'/server/tools': '/server/tools',
	#toolsMac
	'/server/toolsMac/jar/tools.jar': '/server/tools/jar/tools.jar',
}

if shine.is_admin():
	os.chdir(os.path.dirname(os.getcwd()))
	corePath = shine.input_path("请输入core目录的路径")
	
	n=len(corePath);
	if corePath[n-4:n]!="core":
		print("must input corePath!!")
	else:
		if not os.path.isdir(corePath):
			print("corePath is not exist")
		else:
			rootPath = os.getcwd()
			
			if not os.path.exists(rootPath+'/server/toolsMac/jar'):
				os.mkdir(rootPath+'/server/toolsMac/jar')

			shine.link_dir(pathDic, corePath, rootPath)
	
	os.system("pause")
else:
	if shine.is_python3():
		ctypes.windll.shell32.ShellExecuteW(None, "runas", sys.executable, __file__, None, 1)
	else:#in python2.x
		ctypes.windll.shell32.ShellExecuteW(None, u"runas", unicode(sys.executable), unicode(__file__), None, 1)
