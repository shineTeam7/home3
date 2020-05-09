using System.Collections;
using System.Collections.Generic;
namespace ShineEditor
{
    public class AssetBundleManifestDocument
    {
        ManifestParser parser;
        ManifestNode root = new ManifestNode();

        public ManifestNode Root { get { return root; } }

        public AssetBundleManifestDocument(string content)
        {
            parser = new ManifestParser(content);
            parser.Reset();

            Parse();
        }

        void Parse()
        {
            ManifestToken token;            
            do
            {
                token = parser.GetNextToken();
                root.Parse(parser, token);
            }
            while (token.Type != ManifestTokenTypes.EOF);
        }
    }
}