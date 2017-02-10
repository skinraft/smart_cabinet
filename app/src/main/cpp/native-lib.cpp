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
Java_com_sicao_smartwine_SmartCabinetApplication_getAppID(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "57368a09e0b847a39e40469f88c06782";
    return env->NewStringUTF(hello.c_str());
}
jstring
Java_com_sicao_smartwine_SmartCabinetActivity_getProductKey(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "cd688469b42446e99a6130c9550c9ee7";
    return env->NewStringUTF(hello.c_str());
}

jstring
Java_com_sicao_smartwine_SmartCabinetActivity_getProductSecret(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "61f2c1daaad64484a58187b49da4fc0e";
    return env->NewStringUTF(hello.c_str());
}
}

