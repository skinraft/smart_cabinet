#include <jni.h>
#include <string>

extern "C" {
jstring
Java_com_sicao_smartwine_SmartCabinetActivity_getAppSecret(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "2653519b9fad41d883fb379bbc5bc2e9";
    return env->NewStringUTF(hello.c_str());
}

jstring
Java_com_sicao_smartwine_SmartCabinetActivity_getProductKey(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "cd688469b42446e99a6130c9550c9ee7";
    return env->NewStringUTF(hello.c_str());
}
}

