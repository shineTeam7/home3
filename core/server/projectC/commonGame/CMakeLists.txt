file(GLOB INCLUDES src/*.h src/**/*.h src/**/**/*.h src/**/**/**/*.h)
file(GLOB SOURCES src/*.cpp src/**/*.cpp src/**/**/*.cpp src/**/**/**/*.cpp)

add_library(commonGame STATIC ${SOURCES})

set(CommonGame_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src")

target_include_directories(commonGame PUBLIC
    "$<BUILD_INTERFACE:${CommonGame_INCLUDE_DIR}>"
)

set_target_properties(commonGame PROPERTIES
        SOVERSION ${SOVERSION}
        VERSION ${VERSION}
        )

target_link_libraries(commonGame Recast Detour DetourCrowd DetourTileCache DebugUtils shine)

install(TARGETS commonGame
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
        COMPONENT library
        )

install(FILES ${INCLUDES} DESTINATION include)
