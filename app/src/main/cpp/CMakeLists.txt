# File: CMakeLists.txt

cmake_minimum_required(VERSION 3.10.2)
project("devcompanion")

add_library(
    llama_jni
    SHARED
    llama_jni.cpp
)

# Include directory for llama.h
include_directories(
    ${CMAKE_SOURCE_DIR}
    ${CMAKE_SOURCE_DIR}/../../../../../../AI_Models/
)

# Link precompiled llama.so
add_library(
    llama SHARED
    IMPORTED
)

set_target_properties(
    llama PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/../jniLibs/arm64-v8a/libllama.so
)

# Add the native log lib
find_library(
    log-lib
    log
)

# Link libraries
target_link_libraries(
    llama_jni
    llama
    ${log-lib}
)