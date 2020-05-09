namespace Shine
{
    /** 配置控制 */
    export class ConfigControl
    {
        private _overCall:Func;

        private _readData:ConfigReadData;

        constructor()
        {
            
        }

        public onLoaded():void
        {
            // var re=Laya.loader.getRes(ShineGlobal.configPath);
            
            var bytes:ArrayBuffer=Laya.loader.getRes(ShineGlobal.configPath) as ArrayBuffer;

            var t:number=Ctrl.getTimer();

            this.afterReadConfig(this.toRead(new BytesReadStream(bytes)));

            if(ShineSetting.needDebugLog)
                Ctrl.debugLog("配置表加载耗时",Ctrl.getTimer()-t);
        }

        /** 读取热更配置 */
        public loadHotfix(bytes:number[]):void
        {
            if(bytes==null)
                return;

            var data:ConfigReadData=this.doRead(new BytesReadStream(bytes));

            if(data==null)
            {
                Ctrl.warnLog("configLoadHotfix失败");
                return;
            }

            this.afterReadConfigForHotfix(data);

            Ctrl.print("ok");
        }

        public makeConst():void
        {
            //常量构造
            this._readData=GameC.factory.createConfigReadData();
            this._readData.makeConstSize();
        }

        public getMsgDataVersion():number
        {
            return 1;
        }

        /** 存本地数据版本 */
        public getDBDataVersion():number
        {
            return 1;
        }

        /** 检查客户端game配置版本 */
        protected getGameConfigVersion():number
        {
            return 0;
        }

        /** 检查客户端热更代码版本 */
        protected getHotfixConfigVersion():number
        {
            return 0;
        }

        private toRead(stream:BytesReadStream):ConfigReadData
        {
            var data:ConfigReadData=this.doRead(stream);

            if(data==null)
            {
                ShineSetup.exit();
                return null;
            }

            this._readData=data;
            return data;
        }

        private doRead(stream:BytesReadStream):ConfigReadData
        {
            if(CommonSetting.configNeedCompress)
                stream.unCompress();

            if(!stream.checkVersion(ShineGlobal.configVersion))
            {
                Ctrl.errorLog("config结构版本不对");
                return null;
            }

            if(!this.checkStream(stream))
            {
                return null;
            }

            var data:ConfigReadData=GameC.factory.createConfigReadData();
            data.readBytes(stream);
            return data;
        }

        private checkStream(stream:BytesReadStream):boolean
        {
            //代码版本校验
            if(CodeCheckRecord.configVersion!=stream.readInt())
            {
                Ctrl.throwError("common代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
                return false;
            }

            if(this.getGameConfigVersion()!=stream.readInt())
            {
                Ctrl.throwError("game代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
                return false;
            }

            if(CommonSetting.clientNeedHotfix)
            {
                if(this.getHotfixConfigVersion()!=stream.readInt())
                {
                    Ctrl.throwError("hotfix代码版本与配置文件版本不一致,请刷新工程或重新导入配置(执行configExport)");
                    return false;
                }
            }

            return true;
        }

        private afterReadConfig(data:ConfigReadData):void
        {
            data.setToConfig();

            data.refreshData();

            //暂时不做
            // SensitiveWordConfig.init();
            this.refreshConfig();
            // StatusControl.init();
            // AttributeControl.init();

            data.afterReadConfigAll();
        }

        private afterReadConfigForHotfix(data:ConfigReadData):void
        {
            data.addToConfig();
            data.refreshData();
            data.afterReadConfigAll();
        }

        public refreshConfig():void
        {
            TextEnum.readConfig();

            // BaseGameUtils.initStrFilter(SensitiveWordConfig.getWordList(CommonSetting.languageType));
        }

        /** 刷新配置(在更改语言后) */
        public refreshConfigForLanguage():void
        {
            this._readData.refreshData();

            this.refreshConfig();
        }
    }
}