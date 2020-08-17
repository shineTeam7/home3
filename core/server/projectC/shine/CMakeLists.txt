file(GLOB INCLUDES ${JNI_DIR} src/*.h src/**/*.h src/**/**/*.h src/**/**/**/*.h)
file(GLOB SOURCES src/**/**/*.c src/*.cpp src/**/*.cpp src/**/**/*.cpp src/**/**/**/*.cpp)

add_library(shine STATIC ${SOURCES})
#add_library(projectC::shine ALIAS shine)

set(Shine_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src")

target_include_directories(shine PUBLIC
        "$<BUILD_INTERFACE:${Shine_INCLUDE_DIR}>"
        )

set_target_properties(shine PROPERTIES
        SOVERSION ${SOVERSION}
        VERSION ${VERSION}
        )

target_link_libraries(shine Recast Detour DetourCrowd DetourTileCache DebugUtils)

install(TARGETS shine
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
        COMPONENT library
        )

install(FILES ${INCLUDES} DESTINATION include)