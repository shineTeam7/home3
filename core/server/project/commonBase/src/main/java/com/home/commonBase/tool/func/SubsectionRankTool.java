package com.home.commonBase.tool.func;

import com.home.commonBase.config.game.SubsectionRankConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.SubsectionRankToolData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntLongMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongIntMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

import java.util.Arrays;
import java.util.Comparator;

/** 分段排行插件基类 */
public abstract class SubsectionRankTool extends FuncTool implements ISubsectionRankTool
{
    /** 最大尺寸 */
    private int _maxNum;
    /** 最小允许排行值 */
    protected long _valueMin;
    /** 数据 */
    protected SubsectionRankToolData _data;
    /** 当前字典 */
    private LongObjectMap<RankData> _dic=new LongObjectMap<>(RankData[]::new);
    /** 玩家大组与小组index */
    private LongObjectMap<DIntData> _subsectionDic = new LongObjectMap();
    /** 排行值下限 */
    private IntObjectMap<IntLongMap> _valueLimitMap=new IntObjectMap<>();

    private Comparator<RankData> _comparator;

    private RankData _tempData;

    /** 是否需要反向compare */
    protected boolean _needReverseCompare=false;

    public SubsectionRankTool(int funcID,int maxNum,int valueMin)
    {
        super(FuncToolType.SubsectionRank,funcID);

        _maxNum=maxNum;
        _valueMin=valueMin;

        _comparator=this::compare;

        _tempData=toCreateRankData();
    }

    @Override
    protected void toSetData(FuncToolData data)
    {
        super.toSetData(data);

        _data=(SubsectionRankToolData)data;
    }

    @Override
    public void afterReadDataSecond()
    {
        super.afterReadDataSecond();

        readDicByList(_dic,_subsectionDic,_data.listListMap);
    }

    public SubsectionRankToolData getData()
    {
        return _data;
    }

    @Override
    protected FuncToolData createToolData()
    {
        return new SubsectionRankToolData();
    }

    @Override
    public void onNewCreate()
    {
        super.onNewCreate();

        _data.version=1;
    }

    /** 清空榜(包括修改数据) */
    public void clear()
    {
        _data.listListMap.clear();
        _dic.clear();
        _subsectionDic.clear();
        _valueLimitMap.clear();
    }

    /** 获取版本 */
    public int getVersion()
    {
        return _data.version;
    }

    /** 获取下限匹配值 */
    public long getValueLimit(int subsectionIndex,int subsectionSubIndex)
    {
        return _valueLimitMap.get(subsectionIndex).get(subsectionSubIndex);
    }

    /** 通过list读取数据到dic */
    private void readDicByList(LongObjectMap<RankData> dic,LongObjectMap<DIntData> subsectionDic,IntObjectMap<SList<SList<RankData>>> listListMap)
    {
        dic.clear();

        listListMap.forEach((kv,vv)->{

            _valueLimitMap.put(kv,new IntLongMap());

            for(int i=0;i<vv.length();++i)
            {
                SList<RankData> dataSList=vv.get(i);

                RankData[] values=dataSList.getValues();
                RankData v;

                for(int j=dataSList.length()-1;j>=0;--j)
                {
                    (v=values[j]).rank=j+1;
                    dic.put(v.key,v);
                    subsectionDic.put(v.key,DIntData.create(kv,i));
                }

                if(_maxNum>0 && vv.size()==_maxNum)
                {
                    _valueLimitMap.get(kv).put(i,dataSList.getLast().value);
                }
                else
                {

                    _valueLimitMap.get(kv).put(i,_valueMin);
                }
            }
        });
    }

    /** 获取排行数据 */
    public SList<RankData> getList(int subsectionIndex,int subsectionSubIndex)
    {
        return _data.listListMap.get(subsectionIndex).get(subsectionSubIndex);
    }

    /** 获取某key当前排行数据(没有返回null) */
    public RankData getRankData(long key)
    {
        return _dic.get(key);
    }

    /** 获取某key的排名 */
    public int getRank(long key)
    {
        RankData data;

        if((data=_dic.get(key))!=null)
        {
            return data.rank;
        }

        return -1;
    }

    /** 刷新原址数据 */
    protected void refreshSubsectionIndex(long key,int subsectionIndex,int subsectionSubIndex)
    {

    }

    /** 提交排行数据 */
    public void commitRank(int subsectionIndex,int subsectionSubIndex,int version,long key,long value,long[] args)
    {
        int re=toCommitRank(subsectionIndex,subsectionSubIndex,version,key,value,args,null);

        afterCommitRank(subsectionIndex,subsectionSubIndex,key,re,value);
    }

    /** 每秒 */
    @Override
    public void onSecond(int delay)
    {
        int i=100;
    }

    protected DIntData getSubsectionIndexData(long key)
    {
        return _subsectionDic.get(key);
    }

    /** 提交排行数据(返回排名)(vData有值时，key,value,args失效) */
    protected int toCommitRank(int subsectionIndex,int subsectionSubIndex,int version,long key,long value,long[] args,RankData vData)
    {
        //赛季已过
        if(version!=_data.version)
            return -1;

        if(value<_valueMin)
            return -1;

        //计算应该去的地方
        DIntData dIntData=_subsectionDic.get(key);

        if(dIntData==null)
        {
            SubsectionRankConfig subsectionRankConfig=SubsectionRankConfig.get(_funcID);

            if(subsectionIndex>subsectionRankConfig.subsectionConditions.length-1)
                subsectionIndex=subsectionRankConfig.subsectionConditions.length-1;

            if(subsectionIndex<0)
                subsectionIndex=0;

            SList<SList<RankData>> sList=_data.listListMap.get(subsectionIndex);

            if(!_data.listListMap.contains(subsectionIndex))
            {
                _data.listListMap.put(subsectionIndex,sList=new SList<>());
                _valueLimitMap.put(subsectionIndex,new IntLongMap());
            }

            if (sList.length() == 0)
            {
                sList.add(new SList<>(RankData[]::new));
            }
            else
            {
                if (subsectionRankConfig.groupNum != -1 && sList.get(sList.length() - 1).length() >= subsectionRankConfig.groupNum)
                {
                    sList.add(new SList<>(RankData[]::new));
                }
            }

            subsectionSubIndex=sList.length() - 1;

            _valueLimitMap.get(subsectionIndex).put(subsectionSubIndex,_valueMin);

            dIntData = new DIntData();
            dIntData.key=subsectionIndex;
            dIntData.value=subsectionSubIndex;
            _subsectionDic.put(key,dIntData);

            refreshSubsectionIndex(key,subsectionIndex,subsectionSubIndex);
        }
        else
        {
            if (dIntData.key != subsectionIndex || dIntData.value != subsectionSubIndex)
            {
                subsectionIndex=dIntData.key;
                subsectionSubIndex=dIntData.value;
                //同步
                refreshSubsectionIndex(key,subsectionIndex,subsectionSubIndex);
            }
        }

        RankData oldData;
        RankData data;

        RankData tempData;

        if(vData!=null)
        {
            (tempData=_tempData).copy(vData);
        }
        else
        {
            (tempData=_tempData).key=key;
            tempData.value=value;
            makeRankData(tempData,args);
        }

        if((oldData=_dic.get(key))==null)
        {
            if(value<_valueLimitMap.get(subsectionIndex).get(subsectionSubIndex))
                return -1;

            int insertIndex=getInsertIndex(tempData);

            //如相同往后排
            int index=insertIndex<0 ? -insertIndex-1 : insertIndex+1;

            SList<RankData> list=_data.listListMap.get(subsectionIndex).get(subsectionSubIndex);

            //有上限,出榜
            if(_maxNum>0 && index>=_maxNum)
            {
                return -1;
            }
            else
            {
                if(vData!=null)
                {
                    data=vData;
                }
                else
                {
                    data=toCreateRankData();
                    data.key=key;
                    data.value=value;
                    makeRankData(data,args);
                }

                list.insert(index,data);

                RankData[] values=list.getValues();
                int len=list.size();

                //修改名次
                for(int i=index;i<len;++i)
                {
                    values[i].rank=i+1;
                }

                //添加dic
                _dic.put(data.key,data);

                //有上限
                if(_maxNum>0)
                {
                    if(list.size()>_maxNum)
                    {
                        //移除
                        RankData pop=list.pop();
                        _dic.remove(pop.key);
                    }

                    //满了
                    if(list.size()==_maxNum)
                    {
                        _valueLimitMap.get(subsectionIndex).put(subsectionSubIndex, list.getLast().value);
                    }
                }

                return data.rank;
            }
        }
        else
        {
            int oldRank=oldData.rank;

            if(vData!=null)
            {
                //更新插入数据
                oldData=vData;
                _dic.put(key,vData);
            }

            int rank=toRefreshData(oldData,tempData,oldRank);
            //更新匹配值
            oldData.value=value;

            if(vData==null)
            {
                makeRankData(oldData,args);
            }

            return rank;
        }
    }

    /** 获取插入的位置 */
    private int getInsertIndex(RankData data)
    {
        DIntData dIntData=_subsectionDic.get(data.key);

        SList<RankData> list=_data.listListMap.get(dIntData.key).get(dIntData.value);

        //int re=Arrays.binarySearch(list.getValues(),0,list.size(),data,_comparator);
        //return re<0 ? -re-1 : re+1;

        return Arrays.binarySearch(list.getValues(),0,list.size(),data,_comparator);
    }

    /** 刷新方法(data,要插入数据,temp:比较数据,oldRank:旧排名) */
    private int toRefreshData(RankData data,RankData tempData,int oldRank)
    {
        int oldIndex=oldRank-1;

        DIntData dIntData=_subsectionDic.get(data.key);

        SList<RankData> list=_data.listListMap.get(dIntData.key).get(dIntData.value);

        if(ShineSetting.openCheck)
        {
            if(list.get(oldIndex).key!=data.key)
            {
                Ctrl.throwError("出错,数据错误");
            }
        }

        int insertIndex=getInsertIndex(tempData);

        int index;

        if(insertIndex<0)
        {
            insertIndex=-insertIndex-1;

            if(insertIndex<oldIndex)
            {
                index=insertIndex;
            }
            else if(insertIndex>oldIndex+1)
            {
                index=insertIndex-1;
            }
            else
            {
                index=oldIndex;
            }
        }
        else
        {
            index=insertIndex;
        }

        int rank=index+1;

        RankData[] values=list.getValues();

        if(index!=oldIndex)
        {
            //掉
            if(index>oldIndex)
            {
                for(int i=oldIndex;i<index;++i)
                {
                    (values[i]=values[i+1]).rank=i+1;
                }
            }
            else
            {
                for(int i=oldIndex;i>index;--i)
                {
                    (values[i]=values[i-1]).rank=i+1;
                }
            }

            (values[index]=data).rank=rank;

            //有上限
            if(_maxNum>0 && list.size()==_maxNum)
            {
                _valueLimitMap.get(dIntData.key).put(dIntData.value,list.getLast().value);
            }
        }

        return rank;
    }

    /** 删除排行数据(主线程) */
    public void removeRankData(int version,long key)
    {
        if(version!=_data.version)
            return;

        removeData(key);
    }

    /** 删除数据(删号用) */
    private void removeData(long key)
    {
        RankData oldData=_dic.get(key);

        //没有数据
        if(oldData==null)
            return;

        DIntData dIntData=_subsectionDic.get(key);

        //TODO
        SList<RankData> list=_data.listListMap.get(dIntData.key).get(dIntData.value);
        int oldIndex=oldData.rank-1;

        if(ShineSetting.openCheck)
        {
            if(list.get(oldIndex).key!=oldData.key)
            {
                Ctrl.throwError("出错,数据错误");
            }
        }

        //dic移除
        _dic.remove(key);

        list.remove(oldIndex);

        RankData[] values=list.getValues();
        int len=list.size();

        //修改名次
        for(int i=oldIndex;i<len;++i)
        {
            values[i].rank=i+1;
        }

        if(_maxNum>0)
        {
            //归零
            if (values.length == 0)
            {
                _valueLimitMap.get(dIntData.key).put(dIntData.value,_valueMin);
            }
            else
            {
                _valueLimitMap.get(dIntData.key).put(dIntData.value,_valueMin);
            }
        }

        //TODO
        //移除
        afterCommitRank(dIntData.key,dIntData.value,key,-1,0);
    }

    /** 重置 */
    public void reset()
    {
        _data.version++;

        beforeReset();

        _data.listListMap.clear();
        _dic.clear();
    }

    /** 比较方法(可override) */
    protected int compare(RankData arg0,RankData arg1)
    {
        int re = -Long.compare(arg0.value,arg1.value);

        if(re!=0)
        {
            return _needReverseCompare?-re:re;
        }

        return Long.compare(arg0.key,arg1.key);
    }

    /** 创建rankData(只创建类) */
    protected abstract RankData toCreateRankData();
    /** 构造rank数据组 */
    protected abstract void makeRankData(RankData data,long[] args);

    /** 提交排行后 */
    protected abstract void afterCommitRank(int subsectionIndex,int subsectionSubIndex,long key,int rank,long value);

    protected abstract void beforeReset();
}