/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaLoader;
import java.util.List;

public class GpuDeviceSetup {

  public static GpuDeviceSetup m_instance;
  
  public static GpuDeviceSetup v(){
    if(m_instance == null){
      m_instance = new GpuDeviceSetup();
    }
    return new GpuDeviceSetup();
  }
  
  private List<GpuDevice> m_devices;
  
  private GpuDeviceSetup(){
    CudaLoader loader = new CudaLoader();
    loader.load();
    
    m_devices = doGetDevices();
  }
  
  public List<GpuDevice> getDevices(){
    return m_devices;  
  }
  
  private native List<GpuDevice> doGetDevices(); 
}
