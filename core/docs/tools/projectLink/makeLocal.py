#!/usr/bin/env python 
# -*- coding: utf-8 -*-

from __future__ import print_function

import shine
import os


os.chdir(os.path.dirname(os.getcwd()))
rootPath = os.getcwd()

shine.copyfile(rootPath+'/docs/commonDocs/local/serverLocal.xml',rootPath+'/server/bin/serverConfig/serverLocal.xml')


