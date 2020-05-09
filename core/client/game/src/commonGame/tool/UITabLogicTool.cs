using ShineEngine;

/// <summary>
/// 界面多帧逻辑工具
/// </summary>
public class UITabLogicTool
{
    /** tab列表 */
    private SList<UILogicBase> _logicList = new SList<UILogicBase>();

    /** 当前显示tab */
    private int _tab = -1;

    public UITabLogicTool()
    {

    }

    /** 添加逻辑 */
    public void addLogic(UILogicBase logic)
    {
        _logicList.add(logic);
    }

    /** 设置tab */
    public virtual void setTab(int index,params object[] args)
    {
        if (_tab == index)
            return;

        int max;
        if (index > (max = (_logicList.size() - 1)))
            index = max;

        if (_tab != -1)
        {
            _logicList.get(_tab).hide();
        }

        _tab = index;

        if (index != -1)
        {
            _logicList.get(_tab).show(args);
        }
    }

    /** 获取tab */
    public int getTab()
    {
        return _tab;
    }
    
    /** 隐藏 */
    public virtual void hide()
    {
        setTab(-1);
    }
}