using System;
using UnityEngine.UI;
using UnityEngine;

public class SScrollViewElement3D : MonoBehaviour
{
    private Color[] m_colors;
    private MaskableGraphic[] m_maskables;
    private void Awake()
    {
        m_maskables = transform.GetComponentsInChildren<MaskableGraphic>();
        m_colors = new Color[m_maskables.Length];
        for (int i = 0; i < m_colors.Length; i++)
        {
            m_colors[i] = m_maskables[i].color;
        }
        
    }

    public void Tick(float factor)
    {
        if (Application.isPlaying)
        {
            for (int i = 0; i < m_maskables.Length; i++)
            {
                m_maskables[i].color = m_colors[i] * factor;
            }
        }
    }
}
