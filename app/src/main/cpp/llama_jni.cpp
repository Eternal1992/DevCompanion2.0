// File: llama_jni.cpp

#include <jni.h>
#include <string>
#include <mutex>
#include <vector>
#include <sstream>
#include <android/log.h>
#include "llama.h"

#define LOG_TAG "LlamaJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static struct llama_model* g_model = nullptr;
static struct llama_context* g_ctx = nullptr;
static std::mutex llama_mutex;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_devcompanion_app_LlamaNative_initializeModel(JNIEnv* env, jobject, jstring modelPath_) {
    const char* path = env->GetStringUTFChars(modelPath_, nullptr);

    LOGD("Initializing model from path: %s", path);

    std::lock_guard<std::mutex> lock(llama_mutex);

    struct llama_model_params model_params = llama_model_default_params();
    g_model = llama_load_model_from_file(path, &model_params);

    if (!g_model) {
        LOGD("Failed to load model!");
        env->ReleaseStringUTFChars(modelPath_, path);
        return JNI_FALSE;
    }

    struct llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 2048;  // context length
    ctx_params.n_threads = 4;

    g_ctx = llama_new_context_with_model(g_model, &ctx_params);

    env->ReleaseStringUTFChars(modelPath_, path);

    if (!g_ctx) {
        LOGD("Failed to create llama context!");
        return JNI_FALSE;
    }

    LOGD("Model initialized successfully.");
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_devcompanion_app_LlamaNative_run(JNIEnv* env, jobject, jstring input_) {
    const char* input = env->GetStringUTFChars(input_, nullptr);

    LOGD("Running inference for prompt: %s", input);

    std::lock_guard<std::mutex> lock(llama_mutex);

    if (!g_ctx) {
        LOGD("Context not initialized!");
        env->ReleaseStringUTFChars(input_, input);
        return env->NewStringUTF("Model not initialized.");
    }

    // Tokenize input
    std::vector<llama_token> tokens(512);
    int n_tokens = llama_tokenize(g_model, input, tokens.data(), tokens.size(), true);
    tokens.resize(n_tokens);

    // Evaluate prompt
    if (llama_eval(g_ctx, tokens.data(), tokens.size(), 0) != 0) {
        env->ReleaseStringUTFChars(input_, input);
        return env->NewStringUTF("Failed to evaluate prompt.");
    }

    // Generate response
    std::stringstream response;
    for (int i = 0; i < 64; i++) {
        llama_token token = llama_sample_token(g_ctx);
        if (token == 2) break; // EOS token

        const char* token_str = llama_token_get_text(g_model, token);
        response << token_str;

        std::vector<llama_token> input_token = { token };
        llama_eval(g_ctx, input_token.data(), input_token.size(), tokens.size());
    }

    env->ReleaseStringUTFChars(input_, input);
    return env->NewStringUTF(response.str().c_str());
}