from PIL import Image

def imageScaleByPath(str):
	
	str = str.replace("\n","")
	str = str.strip()
	array = str.split(',')
	imageScale(array[0],array[1],array[2])
	return

def imageScale(path,width,heigh):
	print (path)
	img = Image.open(path)
	out = img.resize((int(width),int(heigh)),Image.ANTIALIAS)
	out.save(path,'PNG',quality = 100)
	return

f = open("buildingTexture.txt","r",encoding='utf-8-sig')
line = f.readline()
while line:
	print (line,end = '')
	imageScaleByPath(line)
	line = f.readline()
f.close()


