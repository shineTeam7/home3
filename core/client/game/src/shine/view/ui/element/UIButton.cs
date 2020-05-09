using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
    /// <summary>
    /// 按钮
    /// </summary>
    public class UIButton : UIObject
    {
        private Button _button;

        private Action _longPress;

        private int _longPressIntervalIndex = -1;

        private int _longPressFirstDelayIndex = -1;

        //Button上套了一层又一层的Image 也不知道哪个是有用的，哪个是没用的。直接都禁，简单粗暴 有效。
        private Image[] _m_buttonGraphics = null;

        private Text _text;

        //按钮点击回调
        private Action _onButtonClick;
        public UIButton()
        {
            _type = UIElementType.Button;
        }

        public Button button
        {
            get { return _button; }
        }

        public override void init(GameObject obj)
        {
            base.init(obj);

            _button = obj.GetComponent<Button>();

            Transform tt=_button.transform.Find("Text");

            if(tt!=null)
            {
                _text=tt.GetComponent<Text>();
            }
        }

        protected override void dispose()
        {
            base.dispose();

            _button = null;
            _longPress = null;
            _onButtonClick = null;
            _text=null;
        }

        public void setTextString(string value)
        {
            _text.text = value;
        }

        public Text getText()
        {
            return _text;
        }


        /// <summary>
        /// 点击响应
        /// </summary>
        public Action click
        {
            set
            {
                _onButtonClick = value;
                getPointer().onClick = onButtonClick;

                bindGrid();
            }
        }

        /// <summary>
        /// 
        /// </summary>
        ///<remarks>Scroll View的Click是强制调用的getPointer().onClick所以不受interactive影响</remarks>
        private void onButtonClick()
        {
            if (button!=null&&button.interactable)
                _onButtonClick?.Invoke();
        }

        /// <summary>
        /// up响应
        /// </summary>
        public Action longPress
        {
            set
            {
                _longPress = value;

                if (_longPress != null)
                {
                    getPointer().onUp = onUp;
                    getPointer().onDown = onDown;
                }
                else
                {
                    getPointer().onUp = null;
                    getPointer().onDown = null;
                }
            }
        }

        private void onDown()
        {
            if (_longPress == null)
            {
                return;
            }

            _longPress();

            _longPressFirstDelayIndex =
                TimeDriver.instance.setTimeOut(longPressFirstDelay, ShineSetting.buttonLongPressFirstDelay);
        }

        private void onUp()
        {
            if (_longPressIntervalIndex > 0)
            {
                TimeDriver.instance.clearInterval(_longPressIntervalIndex);
            }

            if (_longPressFirstDelayIndex > 0)
            {
                TimeDriver.instance.clearInterval(_longPressFirstDelayIndex);
            }
        }

        /** 首次等待间隔回调 */
        private void longPressFirstDelay()
        {
            _longPressIntervalIndex =
                TimeDriver.instance.setInterval(longPressInterval, ShineSetting.buttonLongPressInterval);
        }

        /** 长按间隔回调 */
        private void longPressInterval(int delay)
        {
            if (_longPress != null)
            {
                _longPress();
            }
        }

        /** 获取所有图片 */
        public Image[] get_m_buttonGraphics()
        {
            if (_m_buttonGraphics == null)
                _m_buttonGraphics = gameObject.GetComponentsInChildren<Image>(true);

            return _m_buttonGraphics;
        }
    }
}