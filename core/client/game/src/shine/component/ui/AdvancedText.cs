using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Events;
using UnityEngine.EventSystems;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;

namespace ShineEngine
{
	public class AdvancedText:Text,IPointerClickHandler,IPointerDownHandler,IPointerUpHandler
	{
		#region oldpart

		[SerializeField]
		[TextArea(5,10)]
		private string s_OriginText;

		[SerializeField]
		private ImageFont s_ImageFont;

		[SerializeField]
		private Color s_UnderlineColor;

		[SerializeField]
		private Color s_LinkDefautColor;

		private Color m_UnderlineColor;

		public Color UnderlineColor
		{
			get {return m_UnderlineColor;}
			set
			{
				if(m_UnderlineColor!=value)
				{
					m_UnderlineColor=value;
					SetVerticesDirty();
				}
			}
		}

		private Color m_LinkDefautColor;

		public Color LinkDefautColor
		{
			get {return m_LinkDefautColor;}
			set
			{
				if(m_LinkDefautColor!=value)
				{
					m_LinkDefautColor=value;

					//虽然这个方法没有改变文本内容，但实际上改了。强制重新计算。
					string temp=m_OriginText;
					m_OriginText="";
					text=temp;
				}
			}
		}

		ImageFont m_ImageFont;

		public ImageFont ImageFont
		{
			get {return m_ImageFont;}
			set
			{
				if(value==null || m_ImageFont==value)
					return;

				m_ImageFont=value;
				s_ImageFont=m_ImageFont;
				if(im!=null)
					im.SetTexture(m_ImageFont.Texture);
			}
		}

		private string m_OriginText="";

		public string OriginalText
		{
			get {return m_OriginText;}
			set
			{
				bool e1=string.IsNullOrEmpty(m_OriginText);
				bool e2=string.IsNullOrEmpty(value);
				if(e1 && e2)
					return;
				if(m_OriginText.Equals(value))
					return;

				sti.clear();
				if(e2)
				{
					m_OriginText="";
					base.text="";
				}
				else
				{
					m_OriginText=value;
					base.text=ParseText();
				}

				//当标记改变了内容 可能传给base.text的内容仍然不变。
				SetVerticesDirty();
				s_OriginText=m_OriginText;
			}
		}

		public override string text
		{
			set {OriginalText=value;}
		}

		/** 刷新显示 */
		public void refreshView()
		{
			ImageFont=s_ImageFont;
			UnderlineColor=s_UnderlineColor;
			LinkDefautColor=s_LinkDefautColor;
			OriginalText=s_OriginText;
		}

		/** 准备打包时清理索引 */
		public void generateClear()
		{
			s_ImageFont=null;
		}

#if UNITY_EDITOR
		protected override void OnValidate()
		{
			//print("AdvancedText.OnValidate");
			//序列化数据发生，并不会自动更新效果。
			refreshView();
		}
#endif
		protected override void Start()
		{
			// print("AdvancedText.Start");
			base.Start();
			if(Application.isPlaying)
			{
				RectTransform caretRectTrans;
				GameObject go=new GameObject("Images");
				go.hideFlags=HideFlags.DontSave;
				go.transform.SetParent(transform);
				go.transform.SetAsFirstSibling();
				go.layer=gameObject.layer;

				//一定要在awake或之后调用。
				caretRectTrans=go.AddComponent<RectTransform>();
				caretRectTrans.anchorMin=Vector2.zero;
				caretRectTrans.anchorMax=Vector2.one;
				caretRectTrans.offsetMax=Vector2.zero;
				caretRectTrans.offsetMin=Vector2.zero;
				caretRectTrans.localScale=Vector3.one;
				caretRectTrans.anchoredPosition3D=Vector3.zero;

				//caretRectTrans.anchoredPosition3D = new Vector3(0, 0, 100.0f);
				go.AddComponent<CanvasRenderer>();
				im=go.AddComponent<Image2>();

				//这个发生在OnValidate后，实为ImageFont.set的延迟。
				//但是也要注意是否已经绑定了ImageFont.
				if(im!=null && m_ImageFont!=null)
					im.SetTexture(m_ImageFont.Texture);
			}
		}

		void DrawSprite(VertexHelper toFill)
		{
			//public override void SetVerticesDirty()
			//在这个时间点上还不可以操纵它所在的GameObject。
			//这个方法的执行时机是顶点变化了，但并不一定是内容发生了变化.
			//这个方法[先于]Awake，且执行了[很多]遍，但是OnPopulateMesh UpdateGeometry保证在Awake enable start 之后。
			//m_spriteTagRegex =
			//new Regex(@"<quad name=(.+?) size=(\d*\.?\d+%?) width=(\d*\.?\d+%?) />", RegexOptions.Singleline);
			if(m_ImageFont==null || sti==null)
				return;

			Vector3[] vertex_buffer=new Vector3[sti.Count * 4];
			int[] index_buffer=new int[sti.Count * 6];
			Vector2[] uv_buffer=new Vector2[sti.Count * 4];

			int[] indexorder={0,1,2,2,3,0};

			//float[] uvorder = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };
			//现在填充UI网格数据并间接交给image2的canvasrender!!!

			for(int image_index=0;image_index<sti.Count;image_index++)
			{
				float deta_down=fontSize * 0.1f;
				SpriteTagInfo curtag=sti[image_index];
				UIVertex curVertex=new UIVertex();
				int basepos=curtag.start_index * 4;
				if(basepos + 3>=toFill.currentVertCount)
				{
					print("spritetag.endIndex >= toFill.currentVertCount");
					break;
				}

				int vertex_start=image_index * 4;
				int index_start=image_index * 6;
				int uv_start=image_index * 4;
				for(int i=0;i<6;i++)
					index_buffer[index_start + i]=indexorder[i] + vertex_start;

				int number=1;
				number=int.Parse(curtag.sprite_name);
				if(number<0 || number>m_ImageFont.data.Length)
					number=1;

				//默认0号资源。
				var cur_sprite=m_ImageFont.data[number - 1].Sprite;

				//cur_sprite中的uv没规律。
				if(cur_sprite!=null)
					CalcUV(cur_sprite.uv,uv_buffer,uv_start);

				for(int i=0;i<4;i++)
				{
					int pos=basepos + i;
					toFill.PopulateUIVertex(ref curVertex,pos);

					//if (pos >= toFill.currentVertCount)
					//print(string.Format("序号：{0}  i={1} 总顶点数{2}  当前偏移{3}",image_index,i,toFill.currentVertCount,pos));
					vertex_buffer[vertex_start + i]=curVertex.position;

					//这里将默认占位网格整体下移 原因在于预留确实为fontsize大小，但是偏高。
					vertex_buffer[vertex_start + i].y-=deta_down;

					//完成构造好网格后要设定原顶点为0，因为这些乱码部分要被覆盖。
					//一段Quad标记占据等数量的四边形，但是除了首个四边形，之后的都是已经失能的，正如下面的这条语句。
					curVertex.position=Vector3.zero;
					toFill.SetUIVertex(curVertex,pos);
				}
			}

			if(im==null)
				return;

			//注意一定要准备好数据再传给mesh.
			Mesh all_image_mesh=new Mesh();
			all_image_mesh.vertices=vertex_buffer;
			all_image_mesh.triangles=index_buffer;
			all_image_mesh.uv=uv_buffer;

			//这里要将顶点数据传递过去。
			im.SetContent(all_image_mesh);
		}

		//用于显示这些图像的类。
		Image2 im;

		private class Image2:MaskableGraphic
		{
			private Texture ImageSource;

			//如果要使用的ImageSource变了，就要调用 base.UpdateMaterial()
			//继而调用约定的mainTexture。
			public override Texture mainTexture
			{
				//s_WhiteTexture是白色且不透明的。
				get
				{
					if(ImageSource==null)
						return s_WhiteTexture;
					else
						return ImageSource;
				}
			}

			protected override void Awake()
			{
				mesh=new Mesh();
			}

			Mesh mesh;

			public void SetContent(Mesh mesh)
			{
				this.mesh=mesh;

				//这个GameObject上不会自已意识到需要重新绘制。
				//纹理一般不变。只要初始时设定。
				UpdateGeometry();
			}

			public void SetTexture(Texture src)
			{
				ImageSource=src;
				base.UpdateMaterial();
			}

			protected override void UpdateGeometry()
			{
				//不让基类默认的几何更新方法调用。
				//几何与材质是两个分离的更新过程，系统只在一定的时刻调用。
				//要知道UpdateGeometry也可能为系统所调用，因此不可能简单的合并功能。
				canvasRenderer.SetMesh(mesh);
			}
		}

		void CalcUV(Vector2[] uvorigin,Vector2[] target,int basepos)
		{
			float minX=float.MaxValue,minY=float.MaxValue,maxX=float.MinValue,maxY=float.MinValue;
			foreach(var i in uvorigin)
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

			target[basepos + 0]=new Vector2(minX,maxY);
			target[basepos + 1]=new Vector2(maxX,maxY);
			target[basepos + 2]=new Vector2(maxX,minY);
			target[basepos + 3]=new Vector2(minX,minY);
		}

		#endregion

		#region newpart

		private static readonly Regex m_originTagRegex=new Regex(@"\[/\d\d\d\]",RegexOptions.Singleline);

		private readonly StringBuilder sb=new StringBuilder();

		private readonly SList<SpriteTagInfo> sti=new SList<SpriteTagInfo>();

		private readonly SList<HrefInfo> m_HrefInfos=new SList<HrefInfo>();

		private static readonly Regex s_HrefRegex=new Regex(@"\[/http(noline)?_(.*?)_(.*?)\]",RegexOptions.Singleline);

		private class SpriteTagInfo
		{
			public string sprite_name;

			public int start_index;

			public Vector2 display_size;
		}

		private class HrefInfo
		{
			public int startpos_linkedtext;

			//下面是顶点上的索引。
			public int startIndex;

			public int endIndex;

			public bool bUnderline;

			//下划线所包含的内容。
			public string content;

			public string parameter;

			public readonly SList<Rect> boxes=new SList<Rect>();
		}

		private string ParseText()
		{
			string linkedtext=GetLinkedText(OriginalText);

			//先对文本中的超链接进行解析。
			//然后再解析表情标记。
			int lastpos=0;
			sb.Length=0;
			foreach(Match match in m_originTagRegex.Matches(linkedtext))
			{
				SpriteTagInfo tempSpriteTag=new SpriteTagInfo();
				string str=match.Groups[0].Value;
				sb.Append(linkedtext.Substring(lastpos,match.Index - lastpos));
				tempSpriteTag.start_index=sb.Length;
				lastpos=match.Index + match.Length;
				tempSpriteTag.sprite_name=str.Substring(2,3);
				tempSpriteTag.display_size=new Vector2(fontSize,fontSize);
				sti.add(tempSpriteTag);
				string imgstr=string.Format("<quad name={0} size={1} width=1 />",tempSpriteTag.sprite_name,fontSize);
				sb.Append(imgstr);

				//在这里要注意到因为[/ddd]被替换成更长的形式。所以要去更改链接索引。
				//即lastpos以前从[/ddd]到<quad>。
				int deta=(imgstr.Length - 6) * 4;
				foreach(var hrefInfo in m_HrefInfos)
				{
					if(hrefInfo.startpos_linkedtext<lastpos)
						continue;

					hrefInfo.startIndex+=deta;
					hrefInfo.endIndex+=deta;
				}
			}

			sb.Append(linkedtext.Substring(lastpos));
			return sb.ToString();
		}

		private string GetLinkedText(string text)
		{
			sb.Length=0;
			m_HrefInfos.clear();
			var indexText=0;
			Color32 defautcolor=m_LinkDefautColor;
			defautcolor.a=0xff;
			string colorprefix=string.Format("<color=#{0:x2}{1:x2}{2:x2}>",defautcolor.r,defautcolor.g,defautcolor.b);
			string colorappendix="</color>";
			foreach(Match match in s_HrefRegex.Matches(text))
			{
				sb.Append(text.Substring(indexText,match.Index - indexText));
				bool bUnderLine=true;
				if(match.Groups[1].Value.Equals("noline"))
					bUnderLine=false;
				string content=match.Groups[2].Value;
				if(content.Length>0)
				{
					sb.Append(colorprefix); // 超链接颜色
					var hrefInfo=new HrefInfo
					{
						startpos_linkedtext=sb.Length, //一次处理后的文本起始索引
						startIndex=sb.Length * 4, // 相应的顶点索引
						endIndex=(sb.Length + content.Length - 1) * 4 + 3,
						bUnderline=bUnderLine,
						content=content,
						parameter=match.Groups[3].Value
					};
					m_HrefInfos.add(hrefInfo);
					sb.Append(content);
					sb.Append(colorappendix);
				}
				indexText=match.Index + match.Length;
			}

			sb.Append(text.Substring(indexText,text.Length - indexText));

			//print(string.Format("found {0} links",m_HrefInfos.Count));
			return sb.ToString();
		}

		#endregion

		#region finalpart

		readonly UIVertex[] m_TempVerts=new UIVertex[4];

		protected override void OnPopulateMesh(VertexHelper toFill)
		{
			if(ImageFont==null && s_ImageFont!=null)
			{
				ImageFont=s_ImageFont;

				//print("add ImageFont");
			}

			//print("AdvancedText.OnPopulateMesh");
			base.OnPopulateMesh(toFill);
			DrawSprite(toFill);
			if(m_HrefInfos.Count==0)
				return;


			// 计算出每个超链接的所有包围盒。
			UIVertex vert=new UIVertex();
			foreach(var hrefInfo in m_HrefInfos)
			{
				hrefInfo.boxes.clear();
				if(hrefInfo.endIndex>=toFill.currentVertCount)
				{
					print("hrefInfo.endIndex >= toFill.currentVertCount");
					break;
				}

				//以该链接的首个字符初始化包围盒。
				toFill.PopulateUIVertex(ref vert,hrefInfo.startIndex + 3);
				var lastminpos=vert.position;
				var bounds=new Bounds(lastminpos,Vector3.zero);
				toFill.PopulateUIVertex(ref vert,hrefInfo.startIndex + 1);
				var lastmaxpos=vert.position;
				bounds.Encapsulate(lastmaxpos);

				float max_deta_down=fontSize * 0.3f;
				for(int i=hrefInfo.startIndex + 4;i<=hrefInfo.endIndex;i+=4)
				{
					//注意以一个字符的矩形区域为单位加入到包围盒中。
					//TODO 需要合理方式以判断是否发生换行，目前是经验性的。
					toFill.PopulateUIVertex(ref vert,i + 3);
					bool b_newline=(vert.position.x<lastmaxpos.x) && ((lastminpos.y - vert.position.y)>max_deta_down);
					lastminpos=vert.position;
					toFill.PopulateUIVertex(ref vert,i + 1);
					lastmaxpos=vert.position;
					if(b_newline)
					{
						// 换行重新添加包围框.注意换行的检测方法
						//即起点在末点之前，注意顶点自身的排列顺序。
						hrefInfo.boxes.add(new Rect(bounds.min,bounds.size));
						bounds=new Bounds(lastminpos,Vector3.zero);
					}
					else
						bounds.Encapsulate(lastminpos);
					bounds.Encapsulate(lastmaxpos);
				}

				hrefInfo.boxes.add(new Rect(bounds.min,bounds.size));
			}

			//更改UV到一个恒定点(UV对应的纹理值为1)。这源于text上所用shader的着色机制。
			Vector2 extents=rectTransform.rect.size;
			var settings=GetGenerationSettings(extents);
			TextGenerator _UnderlineText=new TextGenerator();
			_UnderlineText.Populate("_",settings);
			IList<UIVertex> _TUT=_UnderlineText.verts;
			if(_TUT.Count<4)
				return;

			Vector2 centerUV=new Vector2(0.0f,0.0f);
			for(int i=0;i<4;i++)
				centerUV+=_TUT[i].uv0;

			centerUV/=4.0f;
			Color32 defautcolor=m_UnderlineColor;
			defautcolor.a=0xff;

			//alpha值一定要置1.
			for(int i=0;i<4;i++)
			{
				m_TempVerts[i].uv0=centerUV;

				//定义下划线的颜色可调。
				m_TempVerts[i].color=defautcolor;
			}

			//只是更动顶点位置,应用deta_down，然后加入,注意效率,计算不冗余。
			//最末一行的下划线应当进行clamp,以保证它可以显示出来。
			//TODO 最后一行的下划线在靠进下边界时太细。
			float linewidth=fontSize * 0.07f;
			float deta_down=2;
			foreach(var hrefInfo in m_HrefInfos)
			{
				if(hrefInfo.endIndex>=toFill.currentVertCount)
				{
					print("hrefInfo.endIndex >= toFill.currentVertCount");
					break;
				}

				if(!hrefInfo.bUnderline)
					continue;

				var nbox=hrefInfo.boxes.Count;
				for(int i=0;i<nbox;i++)
				{
					var curbox=hrefInfo.boxes[i];
					float startx=curbox.x,starty=curbox.y,width=curbox.width;
					float miny=starty - deta_down;

					//rectTransform.rect确实给出了中心坐标系下的范围。
					if(miny<rectTransform.rect.yMin)
						miny=rectTransform.rect.yMin;
					float maxy=miny + linewidth;
					m_TempVerts[0].position=new Vector3(startx,miny,0.0f);
					m_TempVerts[1].position=new Vector3(startx + width,miny,0.0f);
					m_TempVerts[2].position=new Vector3(startx + width,maxy,0.0f);
					m_TempVerts[3].position=new Vector3(startx,maxy,0.0f);
					toFill.AddUIVertexQuad(m_TempVerts);
				}
			}
		}

		#endregion

		#region LinkClickEvent

		//按照UI类中提供事件处理接口的一般方法。
		//onClick.AddListener.回调的参数是href=后的string.
		public class ClickEvent:UnityEvent<string>
		{
		}

		[SerializeField]
		private ClickEvent m_OnClick=new ClickEvent();

		public ClickEvent OnClick
		{
			get {return m_OnClick;}
			set {m_OnClick=value;}
		}

		public void OnPointerClick(PointerEventData eventData)
		{
			if(eventData.button!=PointerEventData.InputButton.Left)
				return;

			Vector2 lp;

			//eventData.position是屏幕坐标下的位置。
			RectTransformUtility.ScreenPointToLocalPointInRectangle(rectTransform,eventData.position,eventData.pressEventCamera,out lp);

			foreach(var hrefInfo in m_HrefInfos)
			{
				var boxes=hrefInfo.boxes;
				for(var i=0;i<boxes.Count;++i)
				{
					if(boxes[i].Contains(lp))
					{
						m_OnClick.Invoke(hrefInfo.parameter);

						//print(hrefInfo.content);
						//print(hrefInfo.parameter);
						return;
					}
				}
			}
		}

		/*
		public bool IsClickHyperLinkContent()
		{
		    Vector2 lp;
		    //eventData.position是屏幕坐标下的位置。
		    RectTransformUtility.ScreenPointToLocalPointInRectangle(
		        rectTransform, Input.mousePosition, GameUIComponent.UICamera, out lp);
		    foreach (var hrefInfo in m_HrefInfos)
		    {
		        var boxes = hrefInfo.boxes;
		        for (var i = 0; i < boxes.Count; ++i)
		        {
		            if (boxes[i].Contains(lp))
		            {
		                return true;
		            }
		        }
		    }
		    return false;
		}
		*/
		public void OnPointerDown(PointerEventData eventData)
		{
			print(string.Format("{0} from {1}","OnPointerDown",gameObject.name));
		}

		public void OnPointerUp(PointerEventData eventData)
		{
			print(string.Format("{0} from {1}","OnPointerUp",gameObject.name));
		}

		#endregion
	}
}