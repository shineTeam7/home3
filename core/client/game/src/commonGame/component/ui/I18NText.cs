using System;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 国际化文本控件
	/// </summary>
	public class I18NText:Text
	{
		[Tooltip("字体id")]
		[SerializeField]
		private int _fontId=1;
//		[SerializeField]
//		private FontType _fontType=FontType.Bbbb;
		/// <summary>
		/// 设置字体
		/// </summary>
		/// <param name="fontId">字体id</param>
		public void setFont(int fontId)
		{
			if(_fontId==fontId)
				return;

			_fontId=fontId;

			refreshLanguage();
		}

		/// <summary>
		///   <para>See MonoBehaviour.Awake.</para>
		/// </summary>
		protected override void Awake()
		{
			base.Awake();
			FontControl.addText(this);
			// refreshLanguage();
		}
		
		/// <summary>
		///   <para>See MonoBehaviour.start.</para>
		/// </summary>
		protected override void Start()
		{
			base.Start();
			refreshLanguage();
		}

		/// <summary>
		///   <para>See MonoBehaviour.OnDestroy.</para>
		/// </summary>
		protected override void OnDestroy()
		{
			base.OnDestroy();
			FontControl.removeText(this);
		}
		
		/// <summary>
		///   <para>See MonoBehaviour.OnEnable.</para>
		/// </summary>
		protected override void OnEnable()
		{
			base.OnEnable();
#if UNITY_EDITOR
			FontControl.addText(this);
			refreshLanguage();
#endif
		}

		/// <summary>
		///   <para>See MonoBehaviour.OnDisable.</para>
		/// </summary>
		protected override void OnDisable()
		{
			base.OnDisable();
#if UNITY_EDITOR
			FontControl.removeText(this);
#endif
		}
		
		/// <summary>
		/// 刷新语言
		/// </summary>
		public void refreshLanguage()
		{
#if UNITY_EDITOR
			GameC.app.initConfigForEditor();
#endif
			//补丁，以后想办法
			if (FontConfig.getDic()==null)
			{
				Ctrl.print("**********************出现异常情况***************");
				return;
			}

			//暂时加个补丁
			if (_fontId == -1)
			{
				_fontId = 1;
			}
			
			//获取字体
			string fontSource=FontConfig.getFontSource(_fontId);

			if (String.IsNullOrEmpty(fontSource))
			{
				return;
			}

			if (font!=null)
			{
				if (fontSource.Contains(this.font.name))
					return;
			}

			Font loadFont = null;
			
			if(ShineSetting.isEditor)
			{
#if UNITY_EDITOR
				if (fontSource.Contains("Arial"))
				{
					this.font=Resources.GetBuiltinResource(typeof(Font),"Arial.ttf")as Font;
				}
				else
				{
					this.font=AssetDatabase.LoadAssetAtPath<Font>("Assets/source/" + fontSource);
				}
#endif
			}
			else
			{
				if (fontSource.Contains("Arial"))
				{
					loadFont = Resources.GetBuiltinResource(typeof(Font),"Arial.ttf")as Font;
					
					if (loadFont != null)
					{
						this.font = loadFont;
					}
				}
				else
				{
					LoadControl.loadOne(fontSource, () =>
					{
						if (this!=null)
						{
							loadFont = LoadControl.getResource(fontSource) as Font;

							if (loadFont != null)
							{
								this.font = loadFont;
							}
						}
					});
				}
			}
		}
	}
}
