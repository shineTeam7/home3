#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import os
import shine

tempPath = os.getcwd() + '/temp.txt'
separator = '\n'

def svn(cmd):
	os.system('svn ' + cmd)

def add(cmd):
	svn('add ' + cmd)

def ignore(dic, dir):
	cmd = 'propset svn:ignore -F "'
	tf = open(tempPath, 'w')
	for key in dic:
		#print(key, file=tf)
		tf.write(key + separator)

	tf.close()
	cmd += tempPath + '" "' + dir + '"'
	svn(cmd)

def externals(dic, dir, path):
	cmd = 'propset svn:externals -F "'
	tf = open(tempPath, 'w')
	for key in dic:
		#print(path + key, file=tf)
		tf.write(path + key + separator)

	tf.close()
	cmd += tempPath + '" "' + dir + '"'
	svn(cmd)

if __name__ == '__main__':
	input("执行前请确保已执行deleteLink, 按Enter键继续......")
	corePath = shine.input_path("输入homeCore的svn地址，如(svn://xxx.com/home/core或者^/home/core)")
	if not corePath.startswith("svn") or corePath.startswith("^"):
		input('svn路径输入错误')
		sys.exit(1)
	
	localPath = os.getcwd()
	comment = 'first'
	
	os.chdir(os.path.dirname(os.getcwd()))
	os.chdir(os.path.dirname(os.getcwd()))

	if os.path.exists(tempPath):
		os.remove(tempPath)
	
	svn('propset svn:global-ignores -F "' + localPath + '/svnProps/game_ignore.txt" "."')
	add('art')
	add('design')
	add('develop --depth empty')
	ignore(['record'], 'develop')
	
	add('develop/link')
	pycache = localPath + '/__pycache__'
	if not os.path.exists(pycache):
		os.makedirs(pycache)
	ignore(['__pycache__'], 'develop/link')
	
	#common
	add('develop/common --depth empty')
	ignore(['commonConfig'], 'develop/common')
	add('develop/common/config')
	add('develop/common/data --depth empty')
	ignore(['commonData',
			'shineData'], 'develop/common/data')
	add('develop/common/data/gameData')

	#docs
	add('develop/docs --depth empty')
	ignore(['commonDocs'], 'develop/docs')
	add('develop/docs --force')
	
	#server
	add('develop/server --depth empty')
	ignore(['temp',
			'tools',
			'toolProject',
			'testProject'], 'develop/server')

	add('develop/server/project --depth empty')
	ignore(['shine',
			'commonBase',
			'commonGame',
			'commonCenter',
			'commonLogin',
			'commonManager'], 'develop/server/project')
	add('develop/server/project --force')

	add('develop/server/clientProject --depth empty')
	ignore(['commonClient'], 'develop/server/clientProject')
	add('develop/server/clientProject/client')

	add('develop/server/gmClientProject --depth empty')
	ignore(['commonGMClient'], 'develop/server/gmClientProject')
	add('develop/server/gmClientProject/gmClient')

	add('develop/server/bin --depth empty')
	ignore(['log'], 'develop/server/bin')
	add('develop/server/bin/serverConfig --depth empty')
	ignore(['serverLocal.xml'], 'develop/server/bin/serverConfig')
	add('develop/server/bin --force')

	add('develop/server/toolsMac')
	add('develop/server/save')

	#client
	add('develop/client --depth empty')
	ignore(['temp'], 'develop/client')
	add('develop/client/game --depth empty')

	svn('propset svn:ignore -F "' + localPath +'/svnProps/game_client_game_ignore.txt" "develop/client/game"')
	add('develop/client/game/Assets --depth empty')

	add('develop/client/hotfix --depth empty')
	svn('propset svn:ignore -F "' + localPath + '/svnProps/game_client_game_ignore.txt" "develop/client/hotfix"')
	
	ignore(['StreamingAssets',
			'StreamingAssets.meta'], 'develop/client/game/Assets')

	add('develop/client/game/Assets/Plugins --depth empty')
	add('develop/client/game/Assets/Plugins/Editor --depth empty')

	ignore(['JetBrains'], 'develop/client/game/Assets/Plugins/Editor')
	add('develop/client/game/Assets/src --depth empty')

	ignore(['shine',
			'shineLib',
			'commonGame',
			"hotfix"
			], 'develop/client/game/Assets/src')

	add('develop/client/game/Assets/Editor --depth empty')

	ignore(['shine',
			'shineLib',
			'commonGame'
			], 'develop/client/game/Assets/Editor')

	svn('propset svn:global-ignores "*.meta" "develop/client/game/Assets/src"')
	svn('propset svn:global-ignores "*.meta" "develop/client/game/Assets/Editor"')

	add('develop/client/game/Assets/Plugins --depth empty')
	add('develop/client/game/Assets/Plugins/Editor --depth empty')
	ignore(['JetBrains'], 'develop/client/game/Assets/Plugins/Editor')
	
	add('develop/client/game/Assets --depth files --force')
	

	add('develop/client/editor/Assets/source --depth empty')

	ignore(['commonGame',
			'commonGame.meta'], 'develop/client/editor/Assets/source')

	add('develop/client/editor/Assets/source/ui --depth empty')

	ignore(['generateModels',
			'generateModels.meta'], 'develop/client/editor/Assets/source/ui')

	add('develop/client --force')

	#externals
	externals(['/server/tools tools'], 'develop/server', corePath)

	externals(['/server/project/shine shine',
			   '/server/project/commonBase commonBase',
			   '/server/project/commonGame commonGame',
			   '/server/project/commonCenter commonCenter',
			   '/server/project/commonLogin commonLogin',
			   '/server/project/commonManager commonManager'], 'develop/server/project', corePath)

	externals(['/server/clientProject/commonClient commonClient'], 'develop/server/clientProject', corePath)
	externals(['/server/gmClientProject/commonGMClient commonGMClient'], 'develop/server/gmClientProject', corePath)
	
	externals(['/docs commonDocs'], 'develop/docs', corePath)
	externals(['/common/commonConfig commonConfig'], 'develop/common', corePath)

	externals(['/common/data/shineData shineData',
			   '/common/data/commonData commonData'], 'develop/common/data', corePath)

	externals(['/client/game/src/shine shine',
			   '/client/game/src/shineLib shineLib',
			   '/client/game/src/commonGame commonGame'], 'develop/client/game/Assets/src', corePath)

	externals(['/client/game/Editor/shine shine',
			   '/client/game/Editor/shineLib shineLib',
			   '/client/game/Editor/commonGame commonGame'], 'develop/client/game/Assets/Editor', corePath)

	externals(['/client/game/source/commonGame commonGame'], 'develop/client/game/Assets/source', corePath)

	svn('commit --depth infinity -m ' + comment)
	svn('update')
    
	os.remove(tempPath)
	os.system("pause")
