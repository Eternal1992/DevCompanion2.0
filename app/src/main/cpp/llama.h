// File: llama.h
#ifndef LLAMA_H
#define LLAMA_H

#include <stddef.h>
#include <stdbool.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

#define LLAMA_API __attribute__ ((visibility("default")))

// Forward declarations
struct llama_model;
struct llama_context;
struct llama_token_data;
struct llama_batch;
typedef int32_t llama_token;

// Model parameters
struct llama_model_params {
    int32_t n_gpu_layers;
    int32_t main_gpu;
    bool use_mlock;
    bool use_mmap;
    bool vocab_only;
    bool cont_batching;
    bool embedding;
};

// Context parameters
struct llama_context_params {
    int32_t seed;
    int32_t n_ctx;
    int32_t n_batch;
    int32_t n_threads;
    int32_t n_threads_batch;
    int32_t rope_freq_base;
    int32_t rope_freq_scale;
    bool mul_mat_q;
    bool logits_all;
    bool embedding;
};

// Model loading
LLAMA_API struct llama_model * llama_load_model_from_file(
    const char * path_model,
    const struct llama_model_params * params);

LLAMA_API void llama_free_model(struct llama_model * model);

// Context creation
LLAMA_API struct llama_context * llama_new_context_with_model(
    struct llama_model * model,
    const struct llama_context_params * params);

LLAMA_API void llama_free(struct llama_context * ctx);

// Tokenization
LLAMA_API int llama_tokenize(
    const struct llama_model * model,
    const char * text,
    llama_token * tokens,
    int n_max_tokens,
    bool add_bos);

LLAMA_API int llama_n_vocab(const struct llama_model * model);
LLAMA_API const char * llama_token_get_text(const struct llama_model * model, llama_token token);

// Evaluation
LLAMA_API int llama_eval(
    struct llama_context * ctx,
    const llama_token * tokens,
    int n_tokens,
    int n_past);

LLAMA_API float * llama_get_logits(struct llama_context * ctx);

// Sampling
LLAMA_API llama_token llama_sample_token(struct llama_context * ctx);

// Generate text
LLAMA_API int llama_sample_top_p_top_k(
    struct llama_context * ctx,
    const float * logits,
    llama_token * output_token,
    float temperature,
    int top_k,
    float top_p);

// Context
LLAMA_API int llama_n_ctx(const struct llama_context * ctx);

// Cleanup
LLAMA_API void llama_print_timings(struct llama_context * ctx);

#ifdef __cplusplus
}
#endif

#endif // LLAMA_H