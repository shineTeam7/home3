#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function
import os,re,shutil,platform,sys

def is_windows():
	return platform.system() == "Windows"
	
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
	
def svn(cmd):
	print('svn ' + cmd)
	os.system('svn ' + cmd)

def add(cmd):
	print('add ' + cmd)
	svn('add ' + cmd)

def resp_root():
	svnInfo = os.popen('svn info ./').read()
	pattern = re.compile('Repository Root: (.*?)\n', re.M)
	return re.search(pattern, svnInfo).group(1)
	
def resp_current(dir):
	svnInfo = os.popen('svn info ./').read()
	pattern = re.compile('URL: (.*?)\n', re.M)
	return re.search(pattern, svnInfo).group(1) + '/' + dir

def branch_url(trunk_url, version):
	pattern = re.compile('trunk')
	return re.sub(pattern, 'branches', trunk_url) + '_' + version

def del_dir(dir):
	print('del_dir ' + dir)
	if os.path.exists(dir):
		if os.path.islink(dir):
			os.remove(dir)
		elif os.path.isdir(dir):
			# 以下代码会遇到文件锁定等权限问题，经常无法正确删除
			# shutil.rmtree(dir)
			# 分别调用各自平台的删除指令
			if is_windows():
				os.system('rd /s /q "' + dir + '"')
			else:
				os.system('rm -rf ' + dir)
		else:
			os.remove(dir)

def del_dir_externals(dir):
	print('del_dir_externals ' + dir)
	svn('propdel svn:externals "' + dir + '"')
	svn('propdel svn:ignore "' + dir + '"')
	
def del_dir_ignore(dir):
	print('del_dir_ignore ' + dir)
	svn('propdel svn:global-ignores "' + dir + '"')
	svn('propdel svn:ignore "' + dir + '"')

def del_externals(dir):
	svnPg = os.popen('svn pg -R svn:externals '+dir).read()
	list1 = svnPg.split("\n\n")
	for l1 in list1:
		list2 = l1.split(" - ")
		len2 = len(list2[0])
		if len2>0:
			del_dir_externals(list2[0])

def del_dot_svn(dir,matchStr):
	for file in os.listdir(dir):
		file_path = os.path.join(dir, file)
		if file_path.endswith(matchStr):
			del_dir(file_path)
		else:
			if os.path.isdir(file_path):
				del_dot_svn(file_path,matchStr)
										
if __name__ == '__main__':
	#固定develop目录
	branch_dir = 'develop'
	branch_path = os.getcwd() + '/' + branch_dir
	input('请确认' + branch_dir + '是主干目录，同时本脚本是放在' + branch_dir + '同级目录下, 按Enter键继续......')
	if not os.path.exists(branch_path):
		input('执行路径错误，即将退出......')
		sys.exit(1)
	
	version = input_path("输入分支版本号")
	#获取svn地址
	svn_url = resp_current(branch_dir)
	#设置分支地址
	branch_url = branch_url(svn_url, version)
	#创建分支
	svn('cp -m "branch_' + version + '" ' + svn_url + ' ' + branch_url)
	#先备份下目录,为了备份source等 svn上没有的资源
	shutil.copytree('./' + branch_dir,"./tmp")
	#切换到分支
	svn('sw ' + branch_url + ' ' + branch_dir)
	#clean一下
	svn('cleanup')
	#清除外链
	del_dir_externals(branch_dir)
	del_externals(branch_dir)
	#清除两个全局忽略参数
	#develop\client下边的temp source干掉
	del_dir_ignore(branch_dir+"\client")
	#develop\client\game\Assets\src 下的 meta忽略干掉，为了提交common层的meta
	del_dir_ignore(branch_dir+"\client\game\Assets\src")
	#提交清除操作
	svn('commit -m "清除外链" ' + branch_dir + ' --depth infinity ')
	#更新下
	svn('update ' + branch_dir)
	#删掉目录
	del_dir(branch_path)
	#删除掉.svn
	del_dot_svn('./tmp','.svn')
	#删掉meta
	del_dot_svn('./tmp','shine.meta')
	del_dot_svn('./tmp','shineLib.meta')
	del_dot_svn('./tmp','commonGame.meta')
	#还原回来
	shutil.move('./tmp', './' + branch_dir)
	#将外链的文件添加到版本库
	add(branch_dir + ' --force')
	#提交分支
	svn('commit -m "增加外链文件" ' + branch_dir + ' --depth infinity ')
	#更新下
	svn('update ' + branch_dir)
	#添加忽略参数
	svn('propset svn:global-ignores -F "' + branch_path + '/link/svnProps/game_ignore.txt" ' + branch_dir)
	svn('propset svn:global-ignores *.meta ' + branch_dir + '/client\game\Assets\src')
	svn('propset svn:global-ignores *.meta ' + branch_dir + '/client\game\Assets\source\commonGame\reporter')
	svn('propset svn:ignore hotfix ' + branch_dir + '/client\game\Assets\src')
	#提交清除操作
	svn('commit -m "设置全局忽略参数" ' + branch_dir + ' --depth infinity ')
	#由于外链，要先完全删除文件夹，再创建
	del_dir(branch_path)
	os.mkdir(branch_path)
	#切换回主干
	svn('sw ' + svn_url + ' ' + branch_dir)
	svn('revert -R ' + branch_dir)
	svn('update ' + branch_dir)
	os.system("pause")
	
