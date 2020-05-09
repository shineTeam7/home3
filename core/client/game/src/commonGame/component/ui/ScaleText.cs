using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ScaleText : Text
{
    public float minScale = 0.2f;

    readonly UIVertex[] m_TempVerts = new UIVertex[4];
    protected override void OnPopulateMesh(VertexHelper toFill)
    {
        if (font == null)
            return;

        m_DisableFontTextureRebuiltCallback = true;

        Vector2 extents = rectTransform.rect.size;

        var settings = GetGenerationSettings(extents);
        cachedTextGenerator.PopulateWithErrors(text, settings, gameObject);

        // Apply the offset to the vertices
        IList<UIVertex> verts = cachedTextGenerator.verts;
        if (verts == null || verts.Count == 0)
        {
            return;
        }
        float unitsPerPixel = 1 / pixelsPerUnit;
        //Last 4 verts are always a new line... (\n)
        int vertCount = verts.Count - 4;

        Vector2 roundingOffset = new Vector2(verts[0].position.x, verts[0].position.y) * unitsPerPixel;
        roundingOffset = PixelAdjustPoint(roundingOffset) - roundingOffset;
        toFill.Clear();
        float vertScale = 1f;
        float singleScale = 1 - (vertScale - minScale) / (vertCount / 4);
        if (roundingOffset != Vector2.zero)
        {
            
            for (int i = 0; i < vertCount; ++i)
            {
                int tempVertsIndex = i & 3;

                m_TempVerts[tempVertsIndex] = ScaleVerts(verts[i], vertScale);
                m_TempVerts[tempVertsIndex].position *= unitsPerPixel;
                m_TempVerts[tempVertsIndex].position.x += roundingOffset.x;
                m_TempVerts[tempVertsIndex].position.y += roundingOffset.y;
                if (tempVertsIndex == 3)
                {
                    toFill.AddUIVertexQuad(m_TempVerts);
                    vertScale *= singleScale;
                }
            }
        }
        else
        {
            for (int i = 0; i < vertCount; ++i)
            {
                int tempVertsIndex = i & 3;
                m_TempVerts[tempVertsIndex] = ScaleVerts(verts[i], vertScale);
                m_TempVerts[tempVertsIndex].position *= unitsPerPixel;
                if (tempVertsIndex == 3)
                {
                    toFill.AddUIVertexQuad(m_TempVerts);
                    vertScale *= singleScale;
                }
                    
            }
        }

        m_DisableFontTextureRebuiltCallback = false;
    }


    private UIVertex ScaleVerts(UIVertex vert,float scaleFactor)
    {
        vert.position = vert.position * scaleFactor;
        return vert;
    }
}
