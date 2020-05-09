package com.home.shine.support;

import com.home.shine.support.collection.CharObjectMap;
import com.home.shine.support.collection.CharSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SQueue;

/** ac匹配 */
public class ACStringFilter
{
	/** DFA入口 */
	private final DFANode dfaEntrance;
	/** 要忽略的字符 */
	private final CharSet ignoreChars;
	/** 过滤时要被替换的字符 */
	private final char subChar;
	
	private boolean _keepWholeWord;
	/**
	 * 定义不进行小写转化的字符,在此表定义所有字符,其小写使用原值,以避免转小写后发生冲突:
	 * <ul>
	 * <li>char code=304,İ -> i,即İ的小写是i,以下的说明类似</li>
	 * <li>char code=8490,K -> k,</li>
	 * </ul>
	 */
	private final static char[] ignoreLowerCaseChars=new char[]{304,8490};
	
	/**
	 * @param ignore     要忽略的字符,即在检测时将被忽略的字符
	 * @param substitute 过滤时要被替换的字符
	 */
	public ACStringFilter(char[] ignore,char substitute,boolean keepWholeWord)
	{
		dfaEntrance=new DFANode();
		this.subChar=substitute;
		_keepWholeWord=keepWholeWord;
		
		ignoreChars=new CharSet(ignore.length);
		for(char c:ignore)
		{
			ignoreChars.add(c);
		}
	}
	
	public boolean init(SList<String> keyWords)
	{
		clear();
		
		if(keyWords!=null && !keyWords.isEmpty())
		{
			String[] values=keyWords.getValues();
			String v;
			
			for(int i1=0,len=keyWords.size();i1<len;++i1)
			{
				v=values[i1];
				
				if(v==null || (v=v.trim()).length()==0)
				{
					continue;
				}
				
				char[] patternTextArray=v.toCharArray();
				DFANode currentDFANode=dfaEntrance;
				
				boolean isE=true;
				
				for(int i=0;i<patternTextArray.length;i++)
				{
					char _c=patternTextArray[i];
					// 逐点加入DFA
					char _lc=toLowerCaseWithoutConfict(_c);
					
					if(_keepWholeWord && !isEnglishChar(_lc))
						isE=false;
					
					DFANode _next=currentDFANode.dfaTransition.get(_lc);
					if(_next==null)
					{
						_next=new DFANode();
						currentDFANode.dfaTransition.put(_lc,_next);
					}
					currentDFANode=_next;
				}
				
				if(currentDFANode!=dfaEntrance)
				{
					currentDFANode.isTerminal=true;
					
					//全英文字
					if(_keepWholeWord && isE)
					{
						currentDFANode.isAllEnglish=true;
					}
				}
			}
		}
		
		buildFailNode();
		return true;
	}
	
	/**
	 * 构造失效节点： 一个节点的失效节点所代表的字符串是该节点所表示它的字符串的最大 部分前缀
	 */
	private final void buildFailNode()
	{
		// 以下构造失效节点
		SQueue<DFANode> queues=new SQueue<>();
		dfaEntrance.failNode=dfaEntrance;//
		
		dfaEntrance.dfaTransition.forEach((k,v)->
		{
			v.level=1;
			queues.offer(v);
			v.failNode=dfaEntrance;// 失效节点指向状态机初始状态
		});
		
		DFANode curNode=null;
		DFANode failNode=null;
		while(!queues.isEmpty())
		{
			curNode=queues.poll();// 该节点的失效节点已计算
			failNode=curNode.failNode;
			
			char k;
			DFANode v;
			
			for(CharObjectMap<DFANode>.Entry<DFANode> kv : curNode.dfaTransition.entrySet())
			{
				k=kv.key;
				v=kv.value;
				
				// 如果父节点的失效节点中有条相同的出边，那么失效节点就是父节点的失效节点
				while(failNode!=dfaEntrance && !failNode.dfaTransition.contains(k))
				{
					failNode=failNode.failNode;
				}
				
				v.failNode=failNode.dfaTransition.get(k);
				
				if(v.failNode==null)
					v.failNode=dfaEntrance;
				
				v.level=curNode.level + 1;
				queues.offer(v);// 计算下一层
			}
		}
	}
	
	// 基于AC状态机查找匹配，并根据节点层数覆写应过滤掉的字符
	// 2017-11-9 屏蔽字替换包含忽略字符
	public String replace(String s)
	{
		char[] input=s.toCharArray();
		char[] result=s.toCharArray();
		boolean _filted=false;
		
		DFANode currentDFANode=dfaEntrance;
		DFANode _next=null;
		int replaceFrom=0;
		int ignoreLength=0;
		boolean isIgnoreC;
		
		for(int i=0;i<input.length;i++)
		{
			char _lc=this.toLowerCaseWithoutConfict(input[i]);
			isIgnoreC=isIgnore(_lc);
			_next=currentDFANode.dfaTransition.get(_lc);
			
			while(_next==null && !isIgnoreC && currentDFANode!=dfaEntrance)
			{
				currentDFANode=currentDFANode.failNode;
				_next=currentDFANode.dfaTransition.get(_lc);
			}
			
			if(_next!=null)
			{
				// 找到状态转移，可继续
				currentDFANode=_next;
			}
			
			if(currentDFANode!=dfaEntrance && isIgnoreC)
			{
				ignoreLength++;
			}
			
			// 看看当前状态可退出否
			if(currentDFANode.isTerminal)
			{
				// 可退出，记录，可以替换到这里
				int j=i - (currentDFANode.level - 1) - ignoreLength;
				
				boolean canReplace=true;
				
				if(_keepWholeWord && currentDFANode.isAllEnglish)
				{
					//前置
					int start=j-1;
					int end=i+1;
					
					//两边有一个也为英文字符
					if((start>=0 && isEnglishChar(Character.toLowerCase(input[start]))) || (end<input.length && isEnglishChar(Character.toLowerCase(input[end]))))
					{
						canReplace=false;
					}
				}
				
				if(j<replaceFrom)
				{
					j=replaceFrom;
				}
				replaceFrom=i + 1;
				
				if(canReplace)
				{
					for(;j<=i;j++)
					{
						result[j]=this.subChar;
						_filted=true;
					}
				}
				
				currentDFANode=dfaEntrance;
				ignoreLength=0;
			}
		}
		if(_filted)
		{
			return String.valueOf(result);
		}
		else
		{
			return s;
		}
	}
	
	// 2017-11-9 添加忽略字符过滤
	public boolean contain(final String inputMsg)
	{
		char[] input=inputMsg.toCharArray();
		DFANode currentDFANode=dfaEntrance;
		DFANode _next=null;
		
		int ignoreLength=0;
		boolean isIgnoreC;
		
		for(int i=0;i<input.length;i++)
		{
			char _lc=this.toLowerCaseWithoutConfict(input[i]);
			
			isIgnoreC=isIgnore(_lc);
			_next=currentDFANode.dfaTransition.get(_lc);
			
			while(_next==null && !isIgnoreC && currentDFANode!=dfaEntrance)
			{
				currentDFANode=currentDFANode.failNode;
				_next=currentDFANode.dfaTransition.get(_lc);
			}
			
			if(_next!=null)
			{
				// 找到状态转移，可继续
				currentDFANode=_next;
			}
			
			if(currentDFANode!=dfaEntrance && isIgnoreC)
			{
				ignoreLength++;
			}
			
			// 看看当前状态可退出否
			if(currentDFANode.isTerminal)
			{
				if(_keepWholeWord && currentDFANode.isAllEnglish)
				{
					//前置
					int j=i - (currentDFANode.level - 1) - ignoreLength;
					
					int start=j-1;
					int end=i+1;
					
					//两边有一个也为英文字符
					if((start>=0 && isEnglishChar(Character.toLowerCase(input[start]))) || (end<input.length && isEnglishChar(Character.toLowerCase(input[end]))))
					{
						//继续
					}
					else
					{
						return true;
					}
				}
				else
				{
					// 可退出，记录，可以替换到这里
					return true;
				}
				
				currentDFANode=dfaEntrance;
				ignoreLength=0;
			}
		}
		
		return false;
	}
	
	/**
	 * 初始化时先调用此函数清理
	 */
	private void clear()
	{
		// 清理入口
		dfaEntrance.dfaTransition.clear();
	}
	
	/**
	 * 将指定的字符转成小写,如果与{@link #ignoreLowerCaseChars}所定义的字符相冲突,则取原值
	 *
	 * @param c
	 * @return
	 */
	private char toLowerCaseWithoutConfict(char c)
	{
		return (c==ignoreLowerCaseChars[0] || c==ignoreLowerCaseChars[1]) ? c : Character.toLowerCase(c);
	}
	
	/**
	 * 是否属于被忽略的字符
	 *
	 * @param c
	 * @return
	 */
	private boolean isIgnore(final char c)
	{
		return ignoreChars.contains(c);
	}
	
	/** 是否英文字符(lower过的) */
	private boolean isEnglishChar(char c)
	{
		return c>='a' && c<='z';
	}
	
	private static class DFANode
	{
		// 是否终结状态的节点
		public boolean isTerminal=false;
		/** 保存小写字母，判断时用小写 */
		private final CharObjectMap<DFANode> dfaTransition=new CharObjectMap<>(DFANode[]::new);
		// 不匹配时走的节点
		private DFANode failNode;
		// 节点层数
		private int level=0;
		
		public boolean isAllEnglish;
	}
}
