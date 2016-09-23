#include "jni.h"

JNIEXPORT void JNICALL Java_com_wos_screencapture_util_Utils_rgb2YCbCr420
  (JNIEnv *env, jobject obj, jbyteArray rgbbuff,jbyteArray yuvbuff, jint width, jint height)
{

    int len = width * height;
    int index = 0;
    //yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
    int y, u, v;
	int  r, g, b;
	int i,j;
	jbyte pYUV[len*3/2];
	jbyte* pRGB=  (jbyte *) env->GetPrimitiveArrayCritical(rgbbuff, NULL);
	if (pRGB == NULL)
	{
		ALOGE("setParameter: Error retrieving param pointer");
	}
    for ( i = 0; i < height; i++) {
        for ( j = 0; j < width; j++) {
			int b=(unsigned char)pRGB[index + 0] & 0xff;
            int g=(unsigned char)pRGB[index + 1] & 0xff;
            int r=(unsigned char)pRGB[index + 2] & 0xff;
            index += 4;
           //套用公式
           y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
           u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
           v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
           //调整
           y = y < 16 ? 16 : (y > 255 ? 255 : y);
           u = u < 0 ? 0 : (u > 255 ? 255 : u);
           v = v < 0 ? 0 : (v > 255 ? 255 : v);
           //赋值
           pYUV[i * width + j] =  (jbyte)y;
           pYUV[len + (i >> 1) * width + (j & ~1) + 0] = (jbyte) u;
           pYUV[len + +(i >> 1) * width + (j & ~1) + 1] = (jbyte) v;
		   index ++;
        }
    }
    env->SetByteArrayRegion(yuvbuff,0, len*3/2, pYUV);
}
