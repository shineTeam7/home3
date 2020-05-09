using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	[RequireComponent(typeof(RawImage))]
	public class RawImageLoader:MonoBehaviour
	{
		private string _source;

		[SerializeField]
		private LoadingShowType _loadingType=LoadingShowType.Hide;

		[SerializeField]
		private bool _autoNativeSize=false;

		private LoadTool _loadTool;

		private RawImage _image;

		private bool _init=false;

		private Action _overFunc;

		public RawImage image
		{
			get {return _image;}
		}

		public string source
		{
			get {return _source;}
			set {_source=value;}
		}

		public Texture texture
		{
			get
			{
				if(!_init)
					return null;
				return _image.texture;
			}
			set
			{
				if(_init)
				{
					stopLoading();
					_image.texture=value;
					if(value==null)
						_image.enabled=false;
					else
						_image.enabled=true;
				}
			}
		}

		public void load(int id)
		{
			init();
			
			setLoading(true);
			_loadTool.loadOne(id);
		}

		public void load(string path)
		{
			load(LoadControl.getResourceIDByName(path));
		}

		public void setOverFunc(Action overFunc)
		{
			_overFunc=overFunc;
		}

		public void setLoading(bool isLoading)
		{
			switch(_loadingType)
			{
				case LoadingShowType.Hide:
				{
					if(isLoading)
						_image.enabled=false;
					else
						_image.enabled=true;
				}
					break;
				case LoadingShowType.None:
				{
				}
					break;
			}
		}

		private void OnEnable()
		{
			init();
		}

		private void OnDestroy()
		{
			if(!_init)
				return;

			_loadTool.clear();
			_init=false;
		}

		public void init()
		{
			if(_init)
				return;

			_init=true;
			
			_image=gameObject.GetComponent<RawImage>();

			_loadTool=new LoadTool(onLoadOver);

			load(_source);
		}

		private void onLoadOver()
		{
			setLoading(false);

			if(_loadTool.getResourceID()==-1)
			{
				//TODO:将这里补充正确
//				_image.=null;
			}
			else
			{
				object asset=LoadControl.getResource(_loadTool.getResourceID());
				if(asset is Texture2D)
				{
                    Texture2D tex=(Texture2D)asset;
                    _image.texture = tex;
				}
				else
				{
					Ctrl.errorLog(new Exception("不支持的图片格式:" + asset));
				}

				if(_autoNativeSize)
				{
					_image.SetNativeSize();
				}
			}

			if(_overFunc!=null)
				_overFunc();
		}

		/** 停止加载 */
		public void stopLoading()
		{
			_loadTool.clear();

			setLoading(false);
		}

		/** 置空 */
		public void setEmpty()
		{
			texture=null;
		}

		/** 设置是否启用autoNativeSize */
		public void setAutoNativeSize(bool isAuto)
		{
			_autoNativeSize=isAuto;
		}
	}
}