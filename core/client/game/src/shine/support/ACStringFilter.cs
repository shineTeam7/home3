using System;

namespace ShineEngine
{
	public class ACStringFilter
	{
		/** DFA入口 */
		private DFANode dfaEntrance;
		/** 要忽略的字符 */
		private CharSet ignoreChars;
		/** 过滤时要被替换的字符 */
		private char subChar;
		/**
		 * 定义不进行小写转化的字符,在此表定义所有字符,其小写使用原值,以避免转小写后发生冲突:
		 * <ul>
		 * <li>char code=304,İ -> i,即İ的小写是i,以下的说明类似</li>
		 * <li>char code=8490,K -> k,</li>
		 * </ul>
		 */
		private static char[] ignoreLowerCaseChars=new char[]{(char)304,(char)8490};

		/**
		 * @param ignore     要忽略的字符,即在检测时将被忽略的字符
		 * @param substitute 过滤时要被替换的字符
		 */
		public ACStringFilter(char[] ignore,char substitute)
		{
			dfaEntrance=new DFANode();
			this.subChar=substitute;

			ignoreChars=new CharSet(ignore.Length);

			foreach(char c in ignore)
			{
				ignoreChars.add(c);
			}
		}

		public bool init(SList<string> keyWords)
		{
			clear();

			if(keyWords!=null && !keyWords.isEmpty())
			{
				string[] values=keyWords.getValues();
				string v;

				for(int i1=0,len=keyWords.size();i1<len;++i1)
				{
					v=values[i1];

					if(v==null || (v=StringUtils.trim(v)).Length==0)
					{
						continue;
					}

					DFANode currentDFANode=dfaEntrance;
					for(int i=0;i<v.Length;i++)
					{
						char _c=v[i];
						// 逐点加入DFA
						char _lc=toLowerCaseWithoutConfict(_c);
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
					}
				}
			}

			buildFailNode();
			return true;
		}

		/**
		 * 构造失效节点： 一个节点的失效节点所代表的字符串是该节点所表示它的字符串的最大 部分前缀
		 */
		private void buildFailNode()
		{
			// 以下构造失效节点
			SQueue<DFANode> queues=new SQueue<DFANode>();
			dfaEntrance.failNode=dfaEntrance;//

			foreach(DFANode v in dfaEntrance.dfaTransition)
			{
				v.level=1;
				queues.offer(v);
				v.failNode=dfaEntrance;// 失效节点指向状态机初始状态
			};

			DFANode curNode=null;
			DFANode failNode=null;
			while(!queues.isEmpty())
			{
				curNode=queues.poll();// 该节点的失效节点已计算
				failNode=curNode.failNode;


				foreach(var kv in curNode.dfaTransition.entrySet())
				{
					char k=kv.key;
					DFANode v=kv.value;

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
		public string replace(string s)
		{
			// char[] input=s.ToCharArray();
			char[] result=s.ToCharArray();
			bool _filted=false;

			DFANode currentDFANode=dfaEntrance;
			DFANode _next=null;
			int replaceFrom=0;
			int ignoreLength=0;
			bool endIgnore=false;
			for(int i=0;i<s.Length;i++)
			{
				char _lc=this.toLowerCaseWithoutConfict(s[i]);
				_next=currentDFANode.dfaTransition.get(_lc);
				while(_next==null && !isIgnore(_lc) && currentDFANode!=dfaEntrance)
				{
					currentDFANode=currentDFANode.failNode;
					_next=currentDFANode.dfaTransition.get(_lc);
				}
				if(_next!=null)
				{
					// 找到状态转移，可继续
					currentDFANode=_next;
				}
				if(!endIgnore && currentDFANode!=dfaEntrance && isIgnore(_lc))
				{
					ignoreLength++;
				}
				// 看看当前状态可退出否
				if(currentDFANode.isTerminal)
				{
					endIgnore=true;
					// 可退出，记录，可以替换到这里
					int j=i - (currentDFANode.level - 1) - ignoreLength;
					if(j<replaceFrom)
					{
						j=replaceFrom;
					}
					replaceFrom=i + 1;
					for(;j<=i;j++)
					{
						result[j]=this.subChar;
						_filted=true;
					}
					currentDFANode=dfaEntrance;
					ignoreLength=0;
					endIgnore=false;
				}
			}
			if(_filted)
			{
				return new string(result);
			}
			else
			{
				return s;
			}
		}

		// 2017-11-9 添加忽略字符过滤
		public bool contain(string inputMsg)
		{
			DFANode currentDFANode=dfaEntrance;
			DFANode _next=null;
			for(int i=0;i<inputMsg.Length;i++)
			{
				char _lc=this.toLowerCaseWithoutConfict(inputMsg[i]);
				if(!isIgnore(_lc))
				{
					_next=currentDFANode.dfaTransition.get(_lc);
					while(_next==null && currentDFANode!=dfaEntrance)
					{
						currentDFANode=currentDFANode.failNode;
						_next=currentDFANode.dfaTransition.get(_lc);
					}
				}
				if(_next!=null)
				{
					// 找到状态转移，可继续
					currentDFANode=_next;
				}
				// 看看当前状态可退出否
				if(currentDFANode.isTerminal)
				{
					// 可退出，记录，可以替换到这里
					return true;
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
			return (c==ignoreLowerCaseChars[0] || c==ignoreLowerCaseChars[1]) ? c : Char.ToLower(c);
		}

		/**
		 * 是否属于被忽略的字符
		 *
		 * @param c
		 * @return
		 */
		private bool isIgnore(char c)
		{
			return ignoreChars.contains(c);
		}

		private class DFANode
		{
			// 是否终结状态的节点
			public bool isTerminal=false;
			/** 保存小写字母，判断时用小写 */
			public CharObjectMap<DFANode> dfaTransition=new CharObjectMap<DFANode>();
			// 不匹配时走的节点
			public DFANode failNode;
			// 节点层数
			public int level=0;
		}
	}
}