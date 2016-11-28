#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_sicao_smartwine_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "欢迎使用新朝智能酒柜";
    return env->NewStringUTF(hello.c_str());
}
