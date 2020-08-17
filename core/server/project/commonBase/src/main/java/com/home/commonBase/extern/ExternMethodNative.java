package com.home.commonBase.extern;

public class ExternMethodNative
{
	public static native void init(String binPath);
	
	public static native void registMap(int mapID,byte[] bytes);
	
	public static native long createScene(int mapID);
	
	public static native void removeScene(long scenePtr);
	
	public static native boolean samplePosition(long scenePtr,float[] fArr,int[] iArr,float maxDistance,int areaMask);
	
	public static native boolean raycast(long scenePtr,float[] fArr,int[] iArr,int areaMask);
	
	public static native boolean calculatePath(long scenePtr,float[] fArr,int[] iArr,int areaMask);
	
	public static native long addCylinderObstacle(long scenePtr,float[] fArr);
	
	public static native long addBoxObstacle(long scenePtr,float[] fArr);
	
	public static native long removeObstacle(long scenePtr,long ptr);
}
