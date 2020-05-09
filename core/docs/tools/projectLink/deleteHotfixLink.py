#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import shine
import os

pathDic = [
	'/client/game/Assets/src/hotfix',
]

if __name__ == '__main__':
	os.chdir(os.path.dirname(os.getcwd()))
	rootPath = os.getcwd()
	for path in pathDic:
		print('delete ', rootPath + path)
		shine.del_dir(rootPath + path)
	
	os.system("pause")
