#include <jni.h>
#include <string>
#include "IPAddressFinder/IPAddressFinder.h"

extern "C" JNIEXPORT jstring

JNICALL
Java_io_cloudonix_androidiphttptest_MainActivity_ipAddressFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    auto ip = IPAddressFinder::getIPAddress();
    return env->NewStringUTF(ip);
}