#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import ctypes, sys
import platform
import shutil
import os

# 是否是Windows系统
def is_windows():
	return platform.system() == "Windows"

# 是否是管理员权限执行，用于link等需要管理员权限时的判断
def is_admin():
	if is_windows():
		try:
			return ctypes.windll.shell32.IsUserAnAdmin()
		except:
			return False
	else:
		return True

def is_python3():
	return sys.version_info[0] == 3

# 输入字符串，为了兼容python2.x和python3.x
def input_str(prompt):
	if is_python3():
		return input(prompt)
	else:
		return raw_input(prompt)

# 输入路径，会去掉字符串两端的空格
def input_path(prompt):
	path = input_str(prompt)
	return path.strip()
	
# 执行Java
def java_run(args):
	_str_ = ' '
	cmd = 'java -jar '
	list = ['-Dfile.encoding=UTF-8', '--illegal-access=deny']
	cmd += _str_.join(list) + ' ' + args
	print('>>' + cmd)
	os.system(cmd)

# 删除目录
def del_dir(dir):
	# 以下代码会遇到文件锁定等权限问题，经常无法正确删除
	# if os.path.islink(dir):
	# 	os.remove(dir)
	# elif os.path.isdir(dir):
	# 	shutil.rmtree(dir)
	# else:
	# 	os.remove(dir)
	if os.path.exists(dir):
		# 分别调用各自平台的删除指令
		if is_windows():
			os.system('rd /s /q "' + dir + '"')
		else:
			os.system('rm -rf ' + dir)
		
# 强制软连接,如果之前存在会先进行删除
def symlink_force(src, dst):
	# 如果存在先删除
	del_dir(dst)
	print("link path", src, " >> ", dst)
	os.symlink(src, dst)

# 将组里的文件/夹一一软链接起来
def link_dir(pathDic, src, dst):
	list = pathDic.items()
	for key, value in list:
		srcPath = src + value
		dstPath = dst + key
		try:
			symlink_force(srcPath, dstPath)
		except:
			print("link error!  ", srcPath, " >> ", dstPath)

# 移动文件
def movefile(srcfile,dstfile):
	if not os.path.isfile(srcfile):
		print(srcfile+' not exist!')
	else:
		fpath,fname=os.path.split(dstfile)    #分离文件名和路径
		if not os.path.exists(fpath):
			os.makedirs(fpath)                #创建路径
		shutil.move(srcfile,dstfile)          #移动文件

# 复制文件
def copyfile(srcfile,dstfile):
	if not os.path.isfile(srcfile):
		print(srcfile+' not exist!')
	else:
		fpath,fname=os.path.split(dstfile)    #分离文件名和路径
		if not os.path.exists(fpath):
			os.makedirs(fpath)                #创建路径
		shutil.copyfile(srcfile,dstfile)      #复制文件
		
# 执行java jar
def runJava(args):
	_str_ = ' '
	cmd = 'java -jar '
	list = ['-Dfile.encoding=UTF-8', '--illegal-access=deny']
	cmd += _str_.join(list) + ' ' + args
	print('>>' + cmd)
	os.system(cmd)

