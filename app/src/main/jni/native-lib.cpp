#include <jni.h>
#include <string>
#include <android/log.h>
#include<cstdlib>
#include<cstring>
#define TAG "dydy"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
using namespace std;

char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

unsigned char *__xhhenc(const char *body) {
    int jm = 0;
    unsigned int b = 378551;
    unsigned int a = 63689;
    int jm2 = 0;
    int len;
    int str_len;
    unsigned char *res;
    int i, j;
    auto *fl = reinterpret_cast<unsigned char *>(strdup(body));


    auto *ta = (unsigned char *) "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    str_len = strlen(reinterpret_cast<const char *>(fl));
    if (str_len % 3 == 0)
        len = str_len / 3 * 4;
    else
        len = (str_len / 3 + 1) * 4;
    res = static_cast<unsigned char *>(malloc(sizeof(unsigned char) * len + 1));
    res[len] = '\0';
    for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
        res[i] = ta[fl[j] >> 2];
        res[i + 1] = ta[(fl[j] & 0x3) << 4 | (fl[j + 1] >> 4)];
        res[i + 2] = ta[(fl[j + 1] & 0xf) << 2 | (fl[j + 2] >> 6)];
        res[i + 3] = ta[fl[j + 2] & 0x3f];
    }
    switch (str_len % 3) {
        case 1:
            res[i - 2] = '=';
            res[i - 1] = '=';
            break;
        case 2:
            res[i - 1] = '=';
            break;
    }

    fl = res;

//    LOGI("%s\n", reinterpret_cast<const char *>(fl));

    while (*fl) {
        jm = (*fl) + (jm << 2) + (jm << 12) - jm;
        jm2 = jm2 * a + (*fl++);
        a *= b;
    }
    int jm3 = jm + jm2;
    int jm4 = jm & 0x7FFFFFFF;
    int jm5 = jm2 & 0x7FFFFFFF;
    auto *str = reinterpret_cast<unsigned char *>((char *) malloc(sizeof(char) * 100));
    int ln = 0;

//    LOGI("%d\n", jm);
//    LOGI("%d\n", jm2);
//    LOGI("%d\n", jm3);
//    LOGI("%d\n", jm4);
//    LOGI("%d\n", jm5);


    const char *tt = "0Ab1Cd2Ef3Gh4Ij5Kl6Mn7Op8Qr9St.UvWxYzAbCdEfGhIjKlMnOpQrStUvWxYz/";
    while (jm != 1) {
        str[ln++] = tt[(jm % 64 + 64) % 64];
        jm /= 5;
        jm++;
    }
    LOGI("%s\n", str);
    while (jm2 != 1) {
        str[ln++] = tt[(jm2 % 64 + 64) % 64];
        jm2 /= 5;
        jm2++;
    }
    LOGI("%d\n", jm2);
    while (jm3 != 1) {
        str[ln++] = tt[(jm3 % 64 + 64) % 64];
        jm3 /= 5;
        jm3++;
    }
    LOGI("%d\n", jm3);
    while (jm4 != 1) {
        str[ln++] = tt[(jm4 % 64 + 64) % 64];
        jm4 /= 5;
        jm4++;
    }
//    LOGI("%s\n", str);
    while (jm5 != 1) {
        str[ln++] = tt[(jm5 % 64 + 64) % 64];
        jm5 /= 5;
        jm5++;
    }
    str[ln] = '\0';
//    LOGI("%s\n", str);

    str_len = strlen(reinterpret_cast<const char *>(str));
    if (str_len % 3 == 0)
        len = str_len / 3 * 4;
    else
        len = (str_len / 3 + 1) * 4;
    res = static_cast<unsigned char *>(malloc(sizeof(unsigned char) * len + 1));
    res[len] = '\0';
    for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
        res[i] = ta[str[j] >> 2];
        res[i + 1] = ta[(str[j] & 0x3) << 4 | (str[j + 1] >> 4)];
        res[i + 2] = ta[(str[j + 1] & 0xf) << 2 | (str[j + 2] >> 6)];
        res[i + 3] = ta[str[j + 2] & 0x3f];
    }
    switch (str_len % 3) {
        case 1:
            res[i - 2] = '\0';
            res[i - 1] = '\0';
            break;
        case 2:
            res[i - 1] = '\0';
            break;
    }

    LOGI("%s\n", reinterpret_cast<const char *>(res));
    ta = (unsigned char *) "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    str_len = strlen(reinterpret_cast<const char *>(res));
    if (str_len % 3 == 0)
        len = str_len / 3 * 4;
    else
        len = (str_len / 3 + 1) * 4;
    str = static_cast<unsigned char *>(malloc(sizeof(unsigned char) * len + 1));
    str[len] = '\0';
    for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
        str[i] = ta[res[j] >> 2];
        str[i + 1] = ta[(res[j] & 0x3) << 4 | (res[j + 1] >> 4)];
        str[i + 2] = ta[(res[j + 1] & 0xf) << 2 | (res[j + 2] >> 6)];
        str[i + 3] = ta[res[j + 2] & 0x3f];
    }
    switch (str_len % 3) {
        case 1:
            str[i - 2] = '=';
            str[i - 1] = '=';
            break;
        case 2:
            str[i - 1] = '=';
            break;
    }
//    LOGI("%s\n", reinterpret_cast<const char *>(str));
    return str;
}



extern "C" JNIEXPORT jstring JNICALL
Java_link_dayang_nativepg_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject instance, /* this */
        jstring input) {

    std::string in = jstringToChar(env, input);
    unsigned char *out = __xhhenc(in.c_str());
    return env->NewStringUTF(reinterpret_cast<const char *>(out));

}