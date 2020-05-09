using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	[RequireComponent(typeof(Image))]
	public class ImageLoader:MonoBehaviour
	{
		private string _source;

		[SerializeField]
		private LoadingShowType _loadingType=LoadingShowType.Hide;

		[SerializeField]
		private bool _autoNativeSize=false;

		private LoadTool _loadTool;

		private Image _image;

		private bool _init=false;

		private Action _overFunc;

        [SerializeField]
        [HideInInspector]
        private bool _isAutoNativeSpriteSize = false;

        [SerializeField]
        [HideInInspector]
        private float _spriteWidth;
        [SerializeField]
        [HideInInspector]
        private float _spriteHeigh;
        public Image image
		{
			get {return _image;}
		}

		public string source
		{
			get {return _source;}
			set {_source=value;}
		}

        public bool isAutoNativeSpriteSize
        {
            get { return _isAutoNativeSpriteSize; }
            set { _isAutoNativeSpriteSize = value; }
        }

        public float spriteWidth
        {
            get { return _spriteWidth; }
            set { _spriteWidth = value; }
        }

        public float spriteHeigh
        {
            get { return _spriteHeigh; }
            set { _spriteHeigh = value; }
        }

        public Sprite sprite
		{
			get
			{
				if(!_init)
					return null;
				return _image.sprite;
			}
			set
			{
				if(_init)
				{
					stopLoading();
					_image.sprite=value;
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
			
			_image=gameObject.GetComponent<Image>();

			_loadTool=new LoadTool(onLoadOver);

			load(_source);
		}

		private void onLoadOver()
		{
			setLoading(false);

			if(_loadTool.getResourceID()==-1)
			{
				//TODO:将这里补充正确
				_image.sprite=null;
				return;
			}
			else
			{
				Sprite sprite=null;
				object asset=LoadControl.getResource(_loadTool.getResourceID());
				if(asset is Texture2D)
				{
					Texture2D tex=(Texture2D)asset;
					sprite=Sprite.Create(tex,new Rect(0f,0f,tex.width,tex.height),new Vector2((float)tex.width / 2,(float)tex.height / 2));
				}
				else if(asset is Sprite)
				{
					sprite=(Sprite)asset;
				}
				else
				{
					Ctrl.errorLog(new Exception("不支持的图片格式:" + asset));
				}

				_image.sprite=sprite;

				if(_autoNativeSize)
				{
					_image.SetNativeSize();
				}

                if (_isAutoNativeSpriteSize)
                {
                    float imageWidth = _image.sprite.rect.width;
                    float imageHeigh = _image.sprite.rect.height;

                    float scale = Mathf.Min(_spriteWidth / imageWidth, _spriteHeigh / imageHeigh);

                    RectTransform rectTransform = transform.GetComponent<RectTransform>();
                    rectTransform.sizeDelta = imageWidth * scale * Vector2.right + imageHeigh * scale * Vector2.up;
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
			sprite=null;
		}

		/** 设置是否启用autoNativeSize */
		public void setAutoNativeSize(bool isAuto)
		{
			_autoNativeSize=isAuto;
		}

        public void setAutoNativeSpriteSize(bool isAuto)
        {
            _isAutoNativeSpriteSize = true;
        }
	}
}