#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_io_cloudonix_androidiphttptest_MainActivity_ipAddressFromJNI(
        JNIEnv *env,
        jobject mainActivity) {
    std::string hello = "100.127.255.255";
    return env->NewStringUTF(hello.c_str());
}