#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_sicao_smartwine_SmartCabinetActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "欢迎使用新朝智能酒柜";
    return env->NewStringUTF(hello.c_str());
}
jstring
Java_com_sicao_smartwine_SmartCabinetApplication_appIDFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "57368a09e0b847a39e40469f88c06782";
    return env->NewStringUTF(hello.c_str());
}
jstring
Java_com_sicao_smartwine_SmartCabinetActivity_appSecretFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "2653519b9fad41d883fb379bbc5bc2e9";
    return env->NewStringUTF(hello.c_str());
}
