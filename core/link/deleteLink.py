#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import shine
import os

pathDic = [
	#toolProject
	'/server/toolProject/shine8/src/main/java/com',
	'/server/testProject/commonBase8/src',
	'/server/testProject/commonGame8/src',
	'/server/testProject/commonCenter8/src'
]

if __name__ == '__main__':
	os.chdir(os.path.dirname(os.getcwd()))
	rootPath = os.getcwd()
	for path in pathDic:
		print('delete ', rootPath + path)
		shine.del_dir(rootPath + path)
	
	os.system("pause")
