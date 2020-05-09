#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import ctypes, sys
import shine
import os

pathDic = {
	'/client/game/Assets/src/hotfix': '/client/hotfix/src/hotfix'
}

if shine.is_admin():
	os.chdir(os.path.dirname(os.getcwd()))
	rootPath = os.getcwd()
	shine.link_dir(pathDic, rootPath, rootPath)
	os.system("pause")
else:
	if shine.is_python3():
		ctypes.windll.shell32.ShellExecuteW(None, "runas", sys.executable, __file__, None, 1)
	else:#in python2.x
		ctypes.windll.shell32.ShellExecuteW(None, u"runas", unicode(sys.executable), unicode(__file__), None, 1)
