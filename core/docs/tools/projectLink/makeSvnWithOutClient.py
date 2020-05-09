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


	svn('commit --depth infinity -m ' + comment)
	svn('update')
    
	os.remove(tempPath)
	os.system("pause")
