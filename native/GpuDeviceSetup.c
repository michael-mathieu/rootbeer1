#include <jni.h>
#include <cuda.h>

#define CHECK_STATUS(env,msg,status) \
if (CUDA_SUCCESS != status) {\
  throw_cuda_errror_exception(env, msg, status);\
  return;\
}

JNIEXPORT jobject JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_GpuDeviceSetup_doGetDevices
  (JNIEnv *env, jobject this_obj)
{
  int i;
  int major_num;
  int minor_num;
  int max_registers_per_block;
  int status;
  int num_devices;
  size_t free_mem;
  size_t total_mem;
  char str[1024];
  int threads_per_block;
  int clock_rate;
  int threads_per_mp;
  int mp_count;
  int block_dim_x;
  int grid_dim_x;
  jclass list_cls;
  jmethodID list_cons;
  jmethodID list_add;
  jobject list_obj;
  jclass gpu_card_cls;
  jmethodID gpu_card_cons;
  jobject gpu_card_obj;
  CUcontext cuContext;
  jstring device_name;

  status = cuInit(0);
  CHECK_STATUS(env,"error in cuInit",status)

  cuDeviceGetCount(&num_devices);
      
  list_cls = (*env)->FindClass(env, "java/util/ArrayList");
  list_cons = (*env)->GetMethodID(env, list_cls, "<init>", "()V");
  list_add = (*env)->GetMethodID(env, list_cls, "add", "(Ljava/lang/Object;)Z");
  list_obj = (*env)->NewObject(env, list_cls, list_cons); 

  for(i = 0; i < num_devices; ++i){
    CUdevice dev;
    status = cuDeviceGet(&dev, i);
    CHECK_STATUS(env,"error in cuDeviceGet",status)

    status = cuCtxCreate(&cuContext, CU_CTX_MAP_HOST, dev);
    CHECK_STATUS(env,"error in cuCtxCreate",status)

    status = cuDeviceComputeCapability(&major_num, &minor_num, dev);
    CHECK_STATUS(env, "error in cuDeviceComputeCapability", status)

    status = cuDeviceGetName(str, 1024, dev);
    CHECK_STATUS(env, "error in cuDeviceGetName", status)
        
    status = cuMemGetInfo(&free_mem, &total_mem);
    CHECK_STATUS(env, "error in cuDeviceGetName", status)

    status = cuDeviceGetAttribute(&threads_per_block, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    status = cuDeviceGetAttribute(&clock_rate, CU_DEVICE_ATTRIBUTE_CLOCK_RATE, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    status = cuDeviceGetAttribute(&threads_per_mp, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    status = cuDeviceGetAttribute(&mp_count, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    status = cuDeviceGetAttribute(&block_dim_x, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    status = cuDeviceGetAttribute(&grid_dim_x, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X, dev);
    CHECK_STATUS(env, "error in cuDeviceGetAttribute", status)

    device_name = (*env)->NewStringUTF(env, str);
    
    gpu_card_cls = (*env)->FindClass(env, "edu/syr/pcpratts/rootbeer/runtime/GpuDevice");
    gpu_card_cons = (*env)->GetMethodID(env, gpu_card_cls, "<init>", "(IIIIIIIIII)V");

    gpu_card_obj = (*env)->NewObject(env, gpu_card_cls, gpu_card_cons, (int) dev, major_num,
      minor_num, device_name, free_mem, total_mem, threads_per_block, mp_count, block_dim_x,
      grid_dim_x);

    (*env)->CallBooleanMethod(env, list_obj, list_add, gpu_card_obj);

    cuCtxDestroy(cuContext);
  }  

  return list_obj;
}
