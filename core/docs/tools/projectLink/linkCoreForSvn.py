#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import ctypes, sys
import shine
import os

pathDic = {
	'/server/toolProject/shine8': '/server/toolProject/shine8',
	'/server/toolProject/shineTool': '/server/toolProject/shineTool',
	'/server/toolProject/commonTool': '/server/toolProject/commonTool'
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
			shine.link_dir(pathDic, corePath, rootPath)
	
	os.system("pause")
else:
	if shine.is_python3():
		ctypes.windll.shell32.ShellExecuteW(None, "runas", sys.executable, __file__, None, 1)
	else:#in python2.x
		ctypes.windll.shell32.ShellExecuteW(None, u"runas", unicode(sys.executable), unicode(__file__), None, 1)