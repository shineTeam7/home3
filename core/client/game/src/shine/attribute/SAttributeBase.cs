using System;
using UnityEngine;

namespace ShineEngine
{
	[AttributeUsage(AttributeTargets.Field, Inherited = true, AllowMultiple = false)]
	public abstract class SAttributeBase:PropertyAttribute
	{
		public string dataField = "";
	}
}