#!/usr/bin/env python 
# -*- coding: utf-8 -*-

import os

if __name__ == '__main__':
	os.system('curl http://127.0.0.1:10003/reloadConfig')
	os.system('curl http://127.0.0.1:30003/reloadConfig')
	os.system("pause")
