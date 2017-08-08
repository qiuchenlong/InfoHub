#include <jni.h>
#include "art.h"
#include "common.h"
#include <String>

extern "C"

JNIEXPORT void JNICALL
Java_com_a10_infohub_fix_DexManager_replace
  (JNIEnv *env, jobject instance, jobject wrongMethod, jobject rightMethod){

    // 拿到错误的class 字节码 里面的方法表里面的ArtMethod
    art::mirror::ArtMethod* smeth = (art::mirror::ArtMethod*)env->FromReflectedMethod(wrongMethod);
    // 拿到正确的class 字节码 里面的方法表里面的ArtMethod
    art::mirror::ArtMethod* dmeth = (art::mirror::ArtMethod*)env->FromReflectedMethod(rightMethod);

    reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->class_loader_ = reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->class_loader_;
    reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->clinit_thread_id_ = reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->clinit_thread_id_;
    reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->status_ = reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->status_;
    reinterpret_cast<art::mirror::Class*>(dmeth->declaring_class_)->super_class_ = reinterpret_cast<art::mirror::Class*>(smeth->declaring_class_)->super_class_;

    smeth->declaring_class_ = dmeth->declaring_class_;
    smeth->dex_cache_resolved_types_ = dmeth->dex_cache_resolved_types_;
    smeth->access_flags_ = dmeth->access_flags_ | 0x0001;
    smeth->dex_cache_resolved_methods_ = dmeth->dex_cache_resolved_methods_;
    smeth->dex_code_item_offset_ = dmeth->dex_code_item_offset_;
    smeth->method_index_ = dmeth->method_index_;
    smeth->dex_method_index_ = dmeth->dex_method_index_;

    smeth->ptr_sized_fields_.entry_point_from_interpreter_ = dmeth->ptr_sized_fields_.entry_point_from_interpreter_;
    smeth->ptr_sized_fields_.entry_point_from_jni_ = dmeth->ptr_sized_fields_.entry_point_from_jni_;
    smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_ = dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_;

    LOGD("replace: %d , %d", smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_, dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);

}
