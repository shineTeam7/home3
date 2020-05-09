using System.Collections.Generic;
using UnityEngine;
using System.Text;

namespace ShineEngine
{

	[System.Serializable]
	public class SymbolData
	{
		[SerializeField]
		Sprite sprite;

		[SerializeField]
		string value;

		public Sprite Sprite
		{
			get {return sprite;}
			set {sprite=value;}
		}

		public string Value
		{
			get {return value;}
			set {this.value=value;}
		}
	}

	public struct SymbolSpriteInfo
	{
		public Vector2[] UV
		{
			get;
			set;
		}

		public int Index
		{
			get;
			set;
		}
	}

	public struct ReplacementInfo
	{
		public SymbolSpriteInfo[] Symbols
		{
			get;
			set;
		}

		public string ReplacedText
		{
			get;
			set;
		}

		public int ReplacedSpriteLength
		{
			get;
			set;
		}

		public string OriginalText
		{
			get;
			set;
		}
	}

	/// <summary>
	/// 图文混排
	/// </summary>
	public class ImageFont:MonoBehaviour
	{
		[SerializeField]
		public SymbolData[] data=new SymbolData[0];

		[System.NonSerialized]
		bool needRebuild;

		[System.NonSerialized]
		DirtWordNode rootNode;

		[System.NonSerialized]
		SMap<string,Sprite> mapping;

		[System.NonSerialized]
		Texture tex;

		public SymbolData[] SymbolData
		{
			get {return data;}
			set
			{
				data=value;
				needRebuild=true;
			}
		}

		public Texture Texture
		{
			get
			{
				if(tex==null)
				{
					if(data!=null && data.Length>0)
						tex=data[0].Sprite.texture;
				}
				return tex;
			}
		}

		void OnEnable()
		{
			needRebuild=true;
		}

		public ReplacementInfo ReplaceText(string text,float imgSize=0f)
		{
			if(needRebuild || rootNode==null)
			{
				RebuildMapping();
			}

			ReplacementInfo res=new ReplacementInfo();
			res.OriginalText=text;
			SList<DirtWordNode> replacement;
			text.ReplaceDirtWord(rootNode,out replacement);
			System.Text.StringBuilder sb=new System.Text.StringBuilder();
			res.Symbols=new SymbolSpriteInfo[replacement.Count];

			int curIdx=0;
			int newIdx=0;
			int appendLength=0;
			int idx=0;
			foreach(var i in replacement)
			{
				SymbolSpriteInfo info=new SymbolSpriteInfo();

				sb.Append(text.Substring(curIdx,i.Index - curIdx));
				int prefixLength=0;
				if(imgSize!=0)
				{
					int b=sb.Length;
					sb.Append("<size=");
					sb.Append(imgSize);
					sb.Append('>');
					prefixLength=sb.Length - b;
				}
				appendLength+=prefixLength;
				newIdx+=i.Index - curIdx + prefixLength;
				info.Index=newIdx;
				newIdx++;

				Sprite sp=mapping[i.Word];
				info.UV=CalcUV(sp);

				Rect r=sp.rect;
				float ratio=r.width / r.height;
				string rep=string.Format("<quad width={0:0.00}>",ratio);
				res.ReplacedSpriteLength=rep.Length;
				sb.Append(rep);
				curIdx=i.Index + i.Word.Length;
				res.Symbols[idx++]=info;
				if(imgSize!=0)
				{
					sb.Append("</size>");
					appendLength+=7;
					newIdx+=7;
				}
			}

			sb.Append(text.Substring(curIdx));
			res.ReplacedText=sb.ToString();

			return res;
		}

		void RebuildMapping()
		{
			needRebuild=false;
			mapping=new SMap<string,Sprite>();
			rootNode=new DirtWordNode();
			if(data!=null)
			{
				foreach(var i in data)
				{
					mapping[i.Value]=i.Sprite;
					DirtWordTool.AddDirtWord(i.Value,rootNode);
				}
			}
		}

		Vector2[] CalcUV(Sprite sprite)
		{
			float minX=float.MaxValue,minY=float.MaxValue,maxX=float.MinValue,maxY=float.MinValue;
			foreach(var i in sprite.uv)
			{
				if(i.x<minX)
					minX=i.x;
				if(i.x>maxX)
					maxX=i.x;
				if(i.y<minY)
					minY=i.y;
				if(i.y>maxY)
					maxY=i.y;
			}

			return new Vector2[4] {new Vector2(minX,maxY),new Vector2(maxX,maxY),new Vector2(maxX,minY),new Vector2(minX,minY)};
		}
	}

	class DirtWordNode
	{
		SMap<char,DirtWordNode> children=new SMap<char,DirtWordNode>();

		string word="";

		public char Character
		{
			get;
			set;
		}

		public bool CanTerminate
		{
			get;
			set;
		}

		public int Index
		{
			get;
			set;
		}

		public string Word
		{
			get {return word;}
			set {word=value;}
		}

		public bool Terminated
		{
			get {return children.Count==0;}
		}

		public DirtWordNode this[char c]
		{
			get {return children.get(c);}
		}

		public DirtWordNode AppendChild(char c,bool canTerminate)
		{
			if(!children.contains(c))
			{
				DirtWordNode node=new DirtWordNode();
				node.Character=c;
				node.Word=word + c;
				node.CanTerminate=canTerminate;
				children.put(c,node);
				return node;
			}
			else
				return children[c];
		}
	}

	static class DirtWordTool
	{
		static DirtWordNode root=new DirtWordNode();

		public static void Initialize(string[] words)
		{
			foreach(var i in words)
			{
				AddDirtWord(i.Trim('\r'),root);
			}
		}

		public static void AddDirtWord(string word,DirtWordNode root)
		{
			DirtWordNode curNode=root;
			char[] arr=word.ToCharArray();
			for(int i=0;i<arr.Length;i++)
			{
				char c=arr[i];
				curNode=curNode.AppendChild(c,i==arr.Length - 1);
			}
		}

		public static bool HasDirtWord(this string str)
		{
			SList<DirtWordNode> m;
			ReplaceDirtWord(str,out m);
			return m.Count>0;
		}

		public static string ReplaceDirtWord(this string str)
		{
			SList<DirtWordNode> m;
			return ReplaceDirtWord(str,out m);
		}

		public static string ReplaceDirtWord(this string str,out SList<DirtWordNode> match)
		{
			return ReplaceDirtWord(str,root,out match);
		}

		public static string ReplaceDirtWord(this string str,DirtWordNode root,out SList<DirtWordNode> match)
		{
			match=new SList<DirtWordNode>();
			if(str==null)
				return null;

			char[] arr=str.ToCharArray();
			int curIdx=0;
			int wordIdx=0;
			StringBuilder sb=new StringBuilder();
			DirtWordNode curNode=root;
			DirtWordNode old=curNode;
			while(curIdx<str.Length)
			{
				char c=arr[curIdx + wordIdx];
				old=curNode;
				curNode=curNode[c];
				if(curNode!=null)
				{
					wordIdx++;
					if(curNode.Terminated)
					{
						DirtWordNode newNode=new DirtWordNode();
						newNode.Word=curNode.Word;
						newNode.Index=curIdx;
						match.add(newNode);
						for(int i=0;i<curNode.Word.Length;i++)
						{
							sb.Append('*');
						}

						curIdx+=wordIdx;
						wordIdx=0;
						curNode=root;
					}
					else if(curIdx + wordIdx>=str.Length)
					{
						for(int i=0;i<wordIdx;i++)
							sb.Append(arr[curIdx + i]);

						curIdx+=wordIdx;
					}
				}
				else
				{
					if(old!=null && old!=root)
					{
						if(old.CanTerminate)
						{
							DirtWordNode newNode=new DirtWordNode();
							newNode.Word=curNode.Word;
							newNode.Index=curIdx;
							match.add(newNode);
							for(int i=0;i<old.Word.Length;i++)
							{
								sb.Append('*');
							}
						}
						else
							sb.Append(old.Word);

						curNode=root;
						curIdx+=wordIdx;
						wordIdx=0;
						old=null;
					}
					else
					{
						sb.Append(c);
						curNode=root;
						curIdx++;
						wordIdx=0;
					}
				}
			}

			if(old!=null && old!=root)
			{
				if(old.CanTerminate)
				{
					DirtWordNode newNode=new DirtWordNode();
					newNode.Word=curNode.Word;
					newNode.Index=curIdx;
					match.add(newNode);
					for(int i=0;i<old.Word.Length;i++)
					{
						sb.Append('*');
					}
				}
				else
					sb.Append(old.Word);

				curNode=root;
				curIdx+=wordIdx;
				wordIdx=0;
				old=null;
			}

			return sb.ToString();
		}
	}
}