using System.Collections;
using System.Collections.Generic;
namespace ShineEditor
{
    public class ManifestNode
    {
        List<ManifestNode> children = new List<ManifestNode>();
        List<ManifestNode> values = new List<ManifestNode>();
        Dictionary<string, string> properties = new Dictionary<string, string>();

        ManifestNode curChild;
        int tabCount = -1;
        public string Name { get; set; }
        public string InnerText { get; set; }

        public List<ManifestNode> Children { get { return children; } }

        public Dictionary<string, string> Properties { get { return properties; } }

        public List<ManifestNode> Values { get { return values; } }

        bool propertyBegin;
        bool valueBegin;
        static int parseTabCount;
        string pKey;

        internal void Parse(ManifestParser parser, ManifestToken cur)
        {
            if (parseTabCount > tabCount)
            {
                bool isNew = false;
                if (curChild == null)
                {
                    isNew = true;
                    curChild = new ManifestNode();
                    curChild.tabCount = parseTabCount;
                    children.Add(curChild);
                }
                curChild.Parse(parser, cur);
                if (cur.Type == ManifestTokenTypes.EOL)
                {
                    var next = parser.PeekNextToken();
                    var next2 = parser.PeekNextToken(2);
                    ManifestTokenTypes nextType = ManifestTokenTypes.EOF;
                    int nextTab = 0;
                    if (next.Type == ManifestTokenTypes.Tabstop)
                    {
                        nextTab = ((ManifestTabStop)next).Count;
                        nextType = next2.Type;
                    }
                    else
                    {
                        nextType = next.Type;
                        parseTabCount = 0;
                    }

                    if ((nextTab <= curChild.tabCount && nextType == ManifestTokenTypes.String) || (nextTab < curChild.tabCount && nextType == ManifestTokenTypes.ValueBegin))
                    {
                        curChild = null;
                    }
                }
                if (isNew)
                    curChild.tabCount = parseTabCount;                                    
            }
            else if (parseTabCount < tabCount)
            {
                curChild = null;
                return;
            }
            else
            {
                if (curChild != null)
                {
                    if (curChild.tabCount > tabCount || (curChild.tabCount == tabCount && cur.Type == ManifestTokenTypes.ValueBegin))
                    {
                        if (curChild.tabCount == tabCount)
                            valueBegin = true;
                        curChild = null;
                    }
                    else
                    {
                        curChild.Parse(parser, cur);
                    }
                }
                else 
                {
                    switch (cur.Type)
                    {
                        case ManifestTokenTypes.Tabstop:
                            if (parser.LastToken == null || parser.LastToken.Type == ManifestTokenTypes.EOL)
                            {                                
                                parseTabCount = ((ManifestTabStop)cur).Count;
                            }
                            break;
                        case ManifestTokenTypes.ValueBegin:
                            if (parser.LastToken == null || parser.LastToken.Type == ManifestTokenTypes.EOL)
                                valueBegin = true;
                            else
                                throw new System.NotSupportedException("Unexpected token:" + cur);
                            break;
                        case ManifestTokenTypes.String:
                            {
                                //var lastType = parser.LastToken.Type;
                            }
                            break;
                        case ManifestTokenTypes.Node:
                            if (parser.LastToken.Type == ManifestTokenTypes.String)
                            {
                                if (valueBegin)
                                {
                                    if (curChild == null)
                                    {
                                        curChild = new ManifestNode();
                                        curChild.Name = parser.LastToken.Text;
                                        curChild.tabCount = tabCount;
                                        values.Add(curChild);
                                    }
                                    else
                                        throw new System.NotSupportedException("Unexpected token:" + cur);
                                }
                                else if (propertyBegin)
                                {
                                    if (pKey == null)
                                    {
                                        pKey = parser.LastToken.Text;
                                    }
                                    else
                                        throw new System.NotSupportedException("Unexpected token:" + cur);
                                }
                                else
                                {
                                    Name = parser.LastToken.Text;
                                }
                            }
                            else
                            {
                                throw new System.NotSupportedException("Unexpected token:" + cur);
                            }
                            break;
                        case ManifestTokenTypes.Comma:
                            {
                                if (propertyBegin)
                                {
                                    if (pKey != null)
                                    {
                                        if (parser.LastToken.Type == ManifestTokenTypes.String)
                                        {
                                            properties[pKey] = parser.LastToken.Text;
                                            pKey = null;
                                        }
                                        else
                                            throw new System.NotSupportedException("Unexpected token:" + cur);
                                    }
                                    else
                                        throw new System.NotSupportedException("Unexpected token:" + cur);
                                }
                                else
                                    throw new System.NotSupportedException("Unexpected token:" + cur);
                            }
                            break;
                        case ManifestTokenTypes.EOL:
                        case ManifestTokenTypes.EOF:
                            if (!propertyBegin)
                            {
                                if (valueBegin)
                                {
                                    if (curChild == null)
                                    {
                                        curChild = new ManifestNode();
                                        curChild.Name = parser.LastToken.Text;
                                        curChild.tabCount = tabCount;
                                        values.Add(curChild);
                                    }
                                    else
                                        throw new System.NotSupportedException("Unexpected token:" + cur);
                                    if (cur.Type == ManifestTokenTypes.EOL)
                                    {
                                        if (parser.PeekNextToken().Type == ManifestTokenTypes.Tabstop && parser.PeekNextToken(2).Type == ManifestTokenTypes.String)
                                        {
                                            parser.GetNextToken();
                                            var node = parser.GetNextToken();
                                            curChild.Name += node.Text;
                                        }
                                    }
                                }
                                else
                                {
                                    if (parser.LastToken.Type == ManifestTokenTypes.String)
                                    {
                                        InnerText = parser.LastToken.Text;
                                    }
                                }
                            }
                            else
                                throw new System.NotSupportedException("Unexpected token:" + cur);
                            propertyBegin = false;
                            valueBegin = false;
                            break;
                        case ManifestTokenTypes.PropertyBegin:
                            if (!propertyBegin)
                            {
                                propertyBegin = true;
                            }
                            else
                                throw new System.NotSupportedException("Unexpected token:" + cur);
                            break;
                        case ManifestTokenTypes.PropertyEnd:
                            if (propertyBegin)
                            {
                                if (pKey != null)
                                {
                                    if (parser.LastToken.Type == ManifestTokenTypes.String)
                                    {
                                        properties[pKey] = parser.LastToken.Text;
                                        pKey = null;
                                    }
                                    else
                                        throw new System.NotSupportedException("Unexpected token:" + cur);
                                }

                                propertyBegin = false;
                            }
                            break;
                        default:
                            throw new System.NotSupportedException("Unexpected token:" + cur);
                    }
                }
            }
        }

        public ManifestNode this[string name]
        {
            get
            {
                foreach (var i in children)
                {
                    if (i.Name == name)
                        return i;
                }
                return null;
            }
        }

        public override string ToString()
        {
            return Name != null ? Name : "(Node)";
        }
    }
}