/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */
package edu.syr.pcpratts.rootbeer.runtime;

import edu.syr.pcpratts.rootbeer.configuration.Configuration;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CudaTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.NativeCpuTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.runtime.cpu.CpuRuntime;
import edu.syr.pcpratts.rootbeer.runtime.nativecpu.NativeCpuRuntime;
import edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaRuntime2;
import java.util.List;

public class ConcreteRootbeer implements IRootbeerInternal {

  private Rootbeer m_rootbeer;
  private ThreadConfig m_threadConfig;

  public ConcreteRootbeer(Rootbeer rootbeer) {
    m_rootbeer = rootbeer;
  }

  public void runAll(List<Kernel> list) {
    if(list.isEmpty()){
      return;
    }
    Kernel first = list.get(0);
    if (Configuration.runtimeInstance().getMode() == Configuration.MODE_NEMU) {
      runOnNativeCpu(list);
    } else if (Configuration.runtimeInstance().getMode() == Configuration.MODE_JEMU
            || first instanceof CompiledKernel == false) {

      runOnCpu(list);
    } else {
      Tweaks.setInstance(new CudaTweaks());
      runOnCudaGpu(list);
    }
  }


  public void runAll(Kernel kernel) {
    if (kernel instanceof CompiledKernel == false) {
      runKernelTemplateJava(kernel);
      return;
    }

    if (Configuration.runtimeInstance().getMode() == Configuration.MODE_NEMU) {
      NativeCpuRuntime.v().run(kernel, m_rootbeer, m_threadConfig);
    } else if (Configuration.runtimeInstance().getMode() == Configuration.MODE_JEMU) {
      runKernelTemplateJava(kernel);
    } else {
      Tweaks.setInstance(new CudaTweaks());
      CudaRuntime2.v().run(kernel, m_rootbeer, m_threadConfig);
    }
  }

  private void runOnCpu(List<Kernel> jobs) {
    CpuRuntime.v().run(jobs, m_rootbeer, m_threadConfig);
  }

  private void runOnCudaGpu(List<Kernel> jobs) {
    Tweaks.setInstance(new CudaTweaks());
    CudaRuntime2.v().run(jobs, m_rootbeer, m_threadConfig);
  }

  private void runOnNativeCpu(List<Kernel> jobs) {
    Tweaks.setInstance(new NativeCpuTweaks());
    NativeCpuRuntime.v().run(jobs, m_rootbeer, m_threadConfig);
  }

  public void setThreadConfig(ThreadConfig thread_config) {
    m_threadConfig = thread_config;
  }

  public void clearThreadConfig() {
    m_threadConfig = null;
  }

  private void runKernelTemplateJava(Kernel kernel) {
    int warp = 32;
    TemplateThreadListsProvider templateThreadListsProvider = new TemplateThreadListsProvider();

    for (int i = 0; i < m_threadConfig.getNumThreads() - warp; i += warp) {
      while (templateThreadListsProvider.getSleeping().isEmpty());
      TemplateThread t = templateThreadListsProvider.getSleeping().remove(0);
      t.kernel = kernel;
      t.m_blockIdxx = 0;
      t.startid = i;
      t.endid = i + warp;
      t.compute = true;
      t.interrupt();
    }
    while (!templateThreadListsProvider.getComputing().isEmpty()) {
    }
  }

  public void printMem(int start, int len) {
    CudaRuntime2.v().printMem(start, len);
  }
}
