#pragma once

#include "../SInclude.h"

#ifdef _isWindows
#include "../../jni/windows/jni.h"
#include "../../jni/windows/jni_md.h"
#endif

#ifdef _isMac
#include "../../jni/mac/jni.h"
#include "../../jni/mac/jni_md.h"
#endif

#ifdef _isLinux
#include "../../jni/linux/jni.h"
#include "../../jni/linux/jni_md.h"
#endif
