file(GLOB INCLUDES src/*.h src/**/*.h src/**/**/*.h src/**/**/**/*.h)
file(GLOB SOURCES src/*.cpp src/**/*.cpp src/**/**/*.cpp src/**/**/**/*.cpp)

list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)

link_directories(commonTest ${ShineTestLibDir})

option(Need_Run "Need run" ON)
set(Need_Run OFF)

if (Need_Run)
    message(Up)
    add_executable(commonTest ${SOURCES})

    install(TARGETS commonTest
            RUNTIME DESTINATION bin
            BUNDLE DESTINATION bin
            )
else()
    message(Down)
    add_library(commonTest SHARED ${SOURCES})

    install(TARGETS commonTest
            ARCHIVE DESTINATION lib
            LIBRARY DESTINATION lib
            COMPONENT library
            )
endif ()

add_dependencies(commonTest shine commonGame shineTest)
target_link_libraries(commonTest Recast Detour DetourCrowd DetourTileCache DebugUtils benchmark shine commonGame shineTest)

install(FILES ${INCLUDES} DESTINATION include)
