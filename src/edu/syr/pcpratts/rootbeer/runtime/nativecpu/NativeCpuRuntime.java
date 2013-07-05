/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.nativecpu;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.ThreadConfig;
import java.util.List;

public class NativeCpuRuntime {

  private static NativeCpuRuntime m_Instance = null;
  
  public static NativeCpuRuntime v(){
    if(m_Instance == null)
      m_Instance = new NativeCpuRuntime();
    return m_Instance;
  }
  
  NativeCpuDevice m_Device;
  
  private NativeCpuRuntime(){
    m_Device = new NativeCpuDevice();
  }
  
  public void run(List<Kernel> blocks, Rootbeer rootbeer, ThreadConfig thread_config) {
    m_Device.run(blocks);
  }
  
  public void run(Kernel kernel_template, Rootbeer rootbeer, ThreadConfig thread_config) {
    m_Device.run(kernel_template, thread_config);
  }

  public boolean isGpuPresent() {
    return true;
  }
  
}
