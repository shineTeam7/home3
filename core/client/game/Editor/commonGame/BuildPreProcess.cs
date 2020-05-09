using System.Collections;
using System.Collections.Generic;
using ShineEngine;
using UnityEditor;
using UnityEditor.Build;
using UnityEngine;

public class BuildPreProcess : IPreprocessBuild
{
    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {

    }

    public void OnPreprocessBuild(BuildTarget target, string path)
    {
        Ctrl.log("OnPreprocessBuild");
        
        ReporterEditor.CreateReporter();
    }

    public int callbackOrder
    {
        get
        {
            return 0;
        }
    }
}
