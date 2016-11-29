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
    std::string hello = "cd688469b42446e99a6130c9550c9ee7";
    return env->NewStringUTF(hello.c_str());
}
jstring
Java_com_sicao_smartwine_SmartCabinetActivity_appSecretFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "61f2c1daaad64484a58187b49da4fc0e";
    return env->NewStringUTF(hello.c_str());
}
