#!/usr/bin/env python 
# -*- coding: utf-8 -*-

import os
import shine
import sys

if __name__ == '__main__':
	os.chdir(os.getcwd() + '/jar')
	shine.runJava('tools.jar exportNav '+sys.argv[1])
