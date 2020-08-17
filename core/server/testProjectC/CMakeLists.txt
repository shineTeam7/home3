cmake_minimum_required(VERSION 3.0)
project(testProjectC)

#add_definitions(-std=c++0x)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# lib versions
SET(SOVERSION 1)
SET(VERSION 1.0.0)

message(WIN32,${WIN32})
message(APPLE,${APPLE})

if(WIN32)
    SET(CMAKE_BUILD_TYPE "Debug")
    set(ShineTestLibDir "${CMAKE_CURRENT_SOURCE_DIR}/shineLib/recastnavigation/lib/x64/Debug;${CMAKE_CURRENT_SOURCE_DIR}/shineLib/benchmark/lib/x64/Debug")
elseif(APPLE)
    SET(CMAKE_BUILD_TYPE "Release")
    add_definitions(-fdeclspec)
    add_definitions(-fPIC)
    set(ShineTestLibDir "${CMAKE_CURRENT_SOURCE_DIR}/shineLib/recastnavigation/lib/mac/Debug;${CMAKE_CURRENT_SOURCE_DIR}/shineLib/benchmark/lib/mac/Debug")
else()
    SET(CMAKE_BUILD_TYPE "Release")
    add_definitions(-fPIC)
    set(ShineTestLibDir "${CMAKE_CURRENT_SOURCE_DIR}/shineLib/recastnavigation/lib/linux/Release;${CMAKE_CURRENT_SOURCE_DIR}/shineLib/benchmark/lib/linux/Release")

    message(${CMAKE_CURRENT_SOURCE_DIR})
endif()

set(ShineLibPath "${CMAKE_CURRENT_SOURCE_DIR}/shineLib")

include_directories(${ShineLibPath}/recastnavigation/Recast/Include)
include_directories(${ShineLibPath}/recastnavigation/Detour/Include)
include_directories(${ShineLibPath}/recastnavigation/DetourCrowd/Include)
include_directories(${ShineLibPath}/recastnavigation/DetourTileCache/Include)
include_directories(${ShineLibPath}/recastnavigation/DebugUtils/Include)
include_directories(${ShineLibPath}/benchmark/include/benchmark)

add_subdirectory(../projectC/shine)
add_subdirectory(../projectC/commonGame)
add_subdirectory(shineTest)
add_subdirectory(commonTest)
