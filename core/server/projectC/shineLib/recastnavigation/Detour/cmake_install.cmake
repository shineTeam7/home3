# Install script for directory: /Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE STATIC_LIBRARY FILES "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/libDetour.a")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libDetour.a" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libDetour.a")
    execute_process(COMMAND "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/ranlib" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libDetour.a")
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include" TYPE FILE FILES
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourAlloc.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourAssert.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourCommon.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourMath.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourNavMesh.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourNavMeshBuilder.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourNavMeshQuery.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourNode.h"
    "/Users/sunming/E/home/homeGit/core/server/projectC/shineLib/recastnavigation/Detour/Include/DetourStatus.h"
    )
endif()

