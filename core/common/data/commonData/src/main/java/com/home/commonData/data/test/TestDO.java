package com.home.commonData.data.test;

import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** 测试数据 */
public class TestDO
{
	/** 00 */
	boolean a1;
	/** 11 */
	byte a2;
	
	int a6;
	
	float a7;
	
	double a8;
	
	long a9;
	
	String a10;
	
	Test2DO b1;
	/** b2 */
	Test2DO b2;
	
	@MaybeNull
	Test2DO b3;
	/** b4 */
	@MaybeNull
	Test2DO b4;
	
	byte[] c1;
	
	int[] c2;
	
	long[] c3;
	
	String[] c4;
	
	int[][] c5;
	
	int[][][] c6;
	
	List<Integer> d1;
	
	List<Long> d2;
	
	List<String> d3;
	
	List<Test2DO> d4;
	List<Test2DO> d5;
	@MaybeNull
	List<Test2DO> d6;
	
	Set<Integer> d7;
	
	Set<Long> d8;
	
	Set<String> d9;
	
	Set<Test2DO> d10;
	Set<Test2DO> d11;
	
	Map<Integer,Integer> e1;
	
	Map<Long,Long> e12;
	
	Map<Integer,Test2DO> e2;
	
	Map<Long,Test2DO> e3;
	
	Map<String,Test2DO> e4;
	
	Map<Integer,Test2DO> e5;
	@MapKeyInValue("a")
	Map<Integer,Test2DO> e6;
	@MapKeyInValue("a")
	Map<Integer,Test2DO> e7;
	@MapKeyInValue("a")
	Map<Integer,Test2DO> e8;
	
	List<Integer>[] f1;
	
	List<int[]>[] f2;
	
	List<Test2DO>[] f3;
	
	List<Test2DO[]>[] f4;
	
	@MaybeNull
	List<Test2DO>[] f5;
	
	@MaybeNull
	List<Test2DO[]>[] f6;
	
	Map<Integer,Map<Integer,Test2DO>> g1;
	
	Queue<Integer> h1;
	
	Queue<Test2DO> h2;
	
	Map<Integer,Queue<Test2DO>> h3;
}
