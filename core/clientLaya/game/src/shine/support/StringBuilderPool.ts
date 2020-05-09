namespace Shine
{
    /**  */
    export class StringBuilderPool
    {
        constructor()
        {
            
        }

        public static create():StringBuilder
        {
            return new StringBuilder();
        }

        public static releaseStr(sb:StringBuilder):string
        {
            return sb.toString();
        }
    }
}