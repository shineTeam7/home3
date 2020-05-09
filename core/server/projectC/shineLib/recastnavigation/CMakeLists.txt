cmake_minimum_required(VERSION 3.0)

project(RecastNavigation)

# lib versions
SET(SOVERSION 1)
SET(VERSION 1.0.0)

#add_definitions(-std=c++0x)

message(WIN32,${WIN32})
message(APPLE,${APPLE})


option(RECASTNAVIGATION_DEMO "Build demo" ON)
option(RECASTNAVIGATION_TESTS "Build tests" ON)
option(RECASTNAVIGATION_EXAMPLES "Build examples" ON)
option(RECASTNAVIGATION_STATIC "Build static libraries" ON)

if(WIN32)
    
elseif(APPLE)
    set(RECASTNAVIGATION_DEMO OFF)
    set(RECASTNAVIGATION_TESTS OFF)
    set(RECASTNAVIGATION_EXAMPLES OFF)

    add_definitions(-fdeclspec)
    add_definitions(-fPIC)
    SET(CMAKE_BUILD_TYPE "Release")
else()
    set(RECASTNAVIGATION_DEMO OFF)
    set(RECASTNAVIGATION_TESTS OFF)
    set(RECASTNAVIGATION_EXAMPLES OFF)

    SET(CMAKE_BUILD_TYPE "Release")
    add_definitions(-fPIC)
endif()


message(${RECASTNAVIGATION_DEMO})
message(${RECASTNAVIGATION_TESTS})
message(${RECASTNAVIGATION_EXAMPLES})

add_subdirectory(Recast)
add_subdirectory(Detour)
add_subdirectory(DetourCrowd)
add_subdirectory(DetourTileCache)
add_subdirectory(DebugUtils)

if (RECASTNAVIGATION_DEMO)
    add_subdirectory(RecastDemo)
endif ()

if (RECASTNAVIGATION_TESTS)
    enable_testing()
    add_subdirectory(Tests)
endif ()
