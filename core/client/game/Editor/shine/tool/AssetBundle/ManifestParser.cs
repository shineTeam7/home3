using System.Collections;
using System.Collections.Generic;
namespace ShineEditor
{
    class ManifestParser
    {
        char[] content;
        int idx, column, line;
        ManifestToken lastToken;
        ManifestToken lastToken2;

        public ManifestToken LastToken { get { return lastToken2; } }

        public bool EOF { get { return idx >= content.Length; } }
        public ManifestParser(string str)
        {
            content = str.ToCharArray();
            idx = 0;
        }

        public void Reset()
        {
            idx = 0;
            line = 1;
            column = 1;
        }

        public ManifestToken PeekNextToken(int it = 1)
        {
            int oldIdx = idx;
            int oldLine = line;
            int oldColumn = column;
            ManifestToken oldToken = lastToken;
            ManifestToken oldToken2 = lastToken2;
            ManifestToken res = null;
            for (int i = 0; i < it; i++)
                res = GetNextToken();
            idx = oldIdx;
            line = oldLine;
            column = oldColumn;
            lastToken = oldToken;
            lastToken2 = oldToken2;

            return res;
        }

        public ManifestToken GetNextToken()
        {
            System.Text.StringBuilder sb = new System.Text.StringBuilder();
            bool isEscape = false, isFinished = false, stringBlock=false;
            string escapecode = null;
            System.Text.StringBuilder sbEscape = null;
            ManifestToken curToken = null;
        
            while (idx < content.Length)
            {
                if (curToken != null && isFinished)
                {
                    curToken.Text = sb.ToString();
                    lastToken2 = lastToken;
                    lastToken = curToken;
                    return curToken;
                }
                char c = content[idx];

                if (curToken == null)
                {
                    if (c == ' ')
                    {
                        curToken = new ManifestTabStop();
                    }
                    else if (c == '\n')
                    {
                        curToken = new ManifestEOL();
                        curToken.LineNumber = line;
                        curToken.ColumnNumber = column;
                        curToken.Text = "";
                        lastToken2 = lastToken;
                        lastToken = curToken;
                        line++;
                        idx++;
                        column = 1;
                        return curToken;
                    }
                    else if (c == '\r')
                    {
                        idx++;
                        column++;
                        continue;
                    }
                    else if (c == '{')
                    {
                        curToken = new ManifestPropertyBegin();
                    }
                    else if (c == '}')
                    {
                        curToken = new ManifestPropertyEnd();
                    }
                    else if (c == ',')
                    {
                        curToken = new ManifestComma();
                    }
                    else if (c == ':')
                    {
                        curToken = new ManifestNodeToken();
                    }
                    else if (c == '-')
                    {
                        if (lastToken == null || lastToken.Type == ManifestTokenTypes.EOL)
                            curToken = new ManifestValueBegin();
                        else
                            curToken = new ManifestStringToken();
                    }
                    else
                    {
                        curToken = new ManifestStringToken();
                    }

                    curToken.LineNumber = line;
                    curToken.ColumnNumber = column;
                }

                switch (curToken.Type)
                {
                    case ManifestTokenTypes.EOL:
                    case ManifestTokenTypes.PropertyBegin:
                    case ManifestTokenTypes.PropertyEnd:
                    case ManifestTokenTypes.ValueBegin:
                    case ManifestTokenTypes.Comma:                    
                    case ManifestTokenTypes.EOF:
                    case ManifestTokenTypes.Node:
                        isFinished = true;
                        break;
                    case ManifestTokenTypes.Tabstop:
                        {
                            if (c != ' ')
                            {
                                isFinished = true;
                                ((ManifestTabStop)curToken).Count = sb.Length;
                                continue;
                            }
                        }
                        break;
                    case ManifestTokenTypes.String:
                        {
                            if (IsTerminalChar(c) && !stringBlock)
                            {
                                isFinished = true;
                                if (isEscape)
                                {
                                    sb.Append(FinishEscape(escapecode, sbEscape.ToString()));
                                    isEscape = false;
                                }
                                continue;
                            }
                            if (isEscape)
                            {
                                if (escapecode == null)
                                    escapecode = c.ToString();
                                else
                                {
                                    sbEscape.Append(c);
                                    if (sbEscape.Length >= GetEscapeLength(escapecode))
                                    {
                                        sb.Append(FinishEscape(escapecode, sbEscape.ToString()));
                                        isEscape = false;
                                    }
                                }
                                idx++;
                                column++;
                                continue;
                            }
                            else
                            {
                                if (c == '\\')
                                {
                                    isEscape = true;
                                    sbEscape = new System.Text.StringBuilder();
                                    escapecode = null;
                                    idx++;
                                    column++;
                                    continue;
                                }
                                if (c == '\"')
                                {
                                    stringBlock = !stringBlock;
                                    idx++;
                                    column++;
                                    continue;
                                }
                            }
                        }
                        break;
                }
                idx++;
                column++;                
                
                sb.Append(c);
                
            }
            if (idx >= content.Length)
            {
                lastToken2 = lastToken;
                lastToken = new ManifestEOF();
                lastToken.LineNumber = line;
                lastToken.ColumnNumber = column;
                isFinished = true;
                return LastToken;
            }

            return null;
        }

        bool IsTerminalChar(char c)
        {
            return c == '\r' || c == '\n' || c == ',' || c == '{' || c == '}' || c == ' ' || c == ':';
        }

        int GetEscapeLength(string code)
        {
            switch (code)
            {
                case "u":
                    return 4;
            }
            return 0;
        }
        string FinishEscape(string code, string content)
        {
            switch (code)
            {
                case "u":
                    return ((char)System.Convert.ToInt32(content, 16)).ToString();
            }
            return null;
        }
    }

    enum ManifestTokenTypes
    {
        String,
        Node,
        PropertyBegin,
        PropertyEnd,
        EOL,
        Tabstop,
        Comma,
        EOF,
        ValueBegin,
    }

    abstract class ManifestToken
    {
        public abstract ManifestTokenTypes Type { get; }
        public string Text { get; set; }
        public int LineNumber { get; set; }

        public int ColumnNumber { get; set; }

        public override string ToString()
        {
            return string.Format("[{0}]{1}(Line:{2} Column:{3})", Type, Text, LineNumber, ColumnNumber);
        }
    }

    class ManifestStringToken : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.String; }
        }
    }

    class ManifestNodeToken : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.Node; }
        }
    }

    class ManifestPropertyBegin : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.PropertyBegin; }
        }
    }
    class ManifestPropertyEnd : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.PropertyEnd; }
        }
    }

    class ManifestEOL : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.EOL; }
        }
    }
    class ManifestEOF : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.EOF; }
        }
    }

    class ManifestTabStop : ManifestToken
    {
        public int Count { get; set; }
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.Tabstop; }
        }
    }
    class ManifestComma : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.Comma; }
        }
    }
    class ManifestValueBegin : ManifestToken
    {
        public override ManifestTokenTypes Type
        {
            get { return ManifestTokenTypes.ValueBegin; }
        }
    }
}