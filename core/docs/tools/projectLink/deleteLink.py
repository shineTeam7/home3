#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import shine
import os

pathDic = [
	#client editor
	'/client/game/Assets/Editor/shine',
	'/client/game/Assets/Editor/shineLib',
	'/client/game/Assets/Editor/commonGame',
	
	#client game
	'/client/game/Assets/src/shine',
	'/client/game/Assets/src/shineLib',
	'/client/game/Assets/src/commonGame',
	'/client/game/Assets/src/hotfix',

	#client source
	'/client/game/Assets/source/commonGame',

	#common
	'/common/commonConfig',
	'/common/data/commonData',
	'/common/data/shineData',
	
	#docs
	'/docs/commonDocs',
	
	#server clientProject
	'/server/clientProject/commonClient',
	#server gmClientProject
	'/server/gmClientProject/commonGMClient',
	
	#server project
	'/server/project/commonBase',
	'/server/project/commonCenter',
	'/server/project/commonGame',
	'/server/project/commonLogin',
	'/server/project/commonManager',
	'/server/project/shine',

	#server toolProject
	'/server/toolProject/shine8',
	'/server/toolProject/shineTool',
	'/server/toolProject/commonTool',

	#server tools
	'/server/tools'
]

if __name__ == '__main__':
	os.chdir(os.path.dirname(os.getcwd()))
	rootPath = os.getcwd()
	for path in pathDic:
		print('delete ', rootPath + path)
		shine.del_dir(rootPath + path)
	
	os.system("pause")
