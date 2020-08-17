file(GLOB INCLUDES src/*.h src/**/*.h src/**/**/*.h src/**/**/**/*.h)
file(GLOB SOURCES src/*.cpp src/**/*.cpp src/**/**/*.cpp src/**/**/**/*.cpp)

add_library(shineTest STATIC ${SOURCES})

set(ShineTest_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src")

target_include_directories(shineTest PUBLIC
        "$<BUILD_INTERFACE:${ShineTest_INCLUDE_DIR}>"
        )

set_target_properties(shineTest PROPERTIES
        SOVERSION ${SOVERSION}
        VERSION ${VERSION}
        )

target_link_libraries(shineTest Recast Detour DetourCrowd DetourTileCache DebugUtils benchmark shine)

install(TARGETS shineTest
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
        COMPONENT library
        )

install(FILES ${INCLUDES} DESTINATION include)