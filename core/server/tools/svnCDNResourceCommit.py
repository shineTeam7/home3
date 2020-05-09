#!/usr/bin/env python 
# -*- coding: utf-8 -*-

import os
import shine

if __name__ == '__main__':
    filename=os.path.dirname(os.path.dirname(os.getcwd()))+"/client/cdnSource/ios"
    print(filename)
    
    msg = "Test"
    svnname = "fenghaiyu"
    svnpw = "r4Qfgvb8"

    os.chdir(filename)
    r=os.popen('svn st')
    info = r.readlines()
    for line in info:
        line = line.strip('\n').split('       ')
        one=line[0]
        tow=line[1]
        if one == '?':
            print(one)
            print(tow)
     
            os.system('svn add %s' % tow )
            os.system('svn commit -m "%s" --username %s --password %s' % (msg,svnname,svnpw))
        elif one == '!':
            print(one)
            print(tow)
          
            os.system('svn delete %s' % tow)
            os.system('svn commit -m "%s" --username %s --password %s' % (msg,svnname,svnpw))
    os.system("pause")