LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := Utils
LOCAL_SRC_FILES := com_wos_screencapture_util_Utils.c
include $(BUILD_SHARED_LIBRARY)
