using System;
using UnityEditor;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 伪3D循环滑动控件
	/// </summary>
	public class SScrollViewFake3D:SScrollView
	{

        public Vector3 minScale = Vector3.one * 0.8f;

        public float minBrightness = 0.5f;

        private SScrollViewElement3D[] element3Ds;

        protected override void doInitGrid(GameObject modelGrid, bool isVirticle)
        {
            base.doInitGrid(modelGrid, isVirticle);
            element3Ds = new SScrollViewElement3D[_gridList.Length];
            for (int i = 0; i < element3Ds.Length; i++)
            {
                element3Ds[i] = _gridList[i].GetComponent<SScrollViewElement3D>();
            }
        }

        protected override void setData(int index, int dataIndex)
        {
            base.setData(index, dataIndex);
            if (_scrollType == SScrollType.Horizontal)
            {
                float centerPosX = _width / 2f;
                float factor = 0f;
                float x = _gridList[index].anchoredPosition.x + _gridList[index].sizeDelta.x / 2f;
                if (x < centerPosX)
                    factor = centerPosX - x;
                else
                    factor = x - centerPosX;
                factor = Mathf.Clamp(factor / centerPosX,0,1);
                Vector3 currentScale = Vector3.Lerp(Vector3.one,minScale, factor);
                //Color currentColor = Color.Lerp(Color.white,minColor, factor);

                _gridList[index].localScale = currentScale;

                //偏移
                _gridList[index].anchoredPosition += _gridCoverWidth * (1 - currentScale.x) * Vector2.right / 2f;
                element3Ds[index].Tick(Mathf.Lerp(1f, minBrightness, factor));
            }
            else
            {

            }
        }


        public void SetScrollBack(Action completeAction = null)
        {
            for (int i = 0; i < _gridDataCache.Length; i++)
            {
                if (_gridDataCache[i] >= 0)
                {
                    float spaceX = _width / 2f - (_gridList[i].anchoredPosition.x + _gridCoverWidth / 2f);
                    if (Mathf.Abs(spaceX) <= _gridCoverWidth / 2f)
                    {
                        SetScrollProgress(spaceX, completeAction);
                        return;
                    }
                }
            }

        }

        private void SetScrollProgress(float spaceX,Action completeAction)
        {
            float targetPos = _scrollPos - spaceX;
            float distance = Mathf.Abs(spaceX);
            int tweenTime = (int)(distance * 400 / (_gridCoverWidth / 2f));
            if (tweenTime > 400)
            {
                tweenTime = 400;
            }
            if (tweenTime > 0 && !ShineSetting.isEditor)
            {
                _tweenIndex = Tween.normal.create(_scrollPos, targetPos, tweenTime, onBackTween, completeAction);
            }
            else
            {
                setScrollPosion(targetPos);
            }
        }

        public int GetCurrentGridIndex()
        {
            for (int i = 0; i < _gridDataCache.Length; i++)
            {
                if (_gridDataCache[i] >= 0)
                {
                    float spaceX = _width / 2f - (_gridList[i].anchoredPosition.x + _gridCoverWidth / 2f);
                    if (Mathf.Abs(spaceX) <= _gridCoverWidth / 2f)
                    {
                        return i;
                    }
                }
            }
            return -1;
        }

        private void onBackTween(float v)
        {
            setScrollPosion(v);
        }
    }
}