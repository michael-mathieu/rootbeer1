/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Rootbeer implements IRootbeer {

  private IRootbeerInternal m_Rootbeer;
  private List<StatsRow> m_stats;
  private boolean m_ranGpu;
  private ThreadConfig m_threadConfig;
  
  public Rootbeer(){
    RootbeerFactory factory = new RootbeerFactory();
    m_Rootbeer = factory.create(this);
  }
  
  public static void init(){
    try {
      Class c = Class.forName("edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaRuntime2");
      Method v_method = c.getMethod("v");
      v_method.invoke(null);
    } catch(Exception ex){
      //ignore
    }
  }
  
  public void setThreadConfig(int block_shape_x, int grid_shape_x, int numThreads){
    m_threadConfig = new ThreadConfig(block_shape_x, grid_shape_x, numThreads);
  }
  
  public void runAll(Kernel job_template){
    if(job_template instanceof CompiledKernel == false){
      m_ranGpu = false;
    }
    //this must happen above Rootbeer.runAll in case exceptions are thrown
    m_ranGpu = true;
      
    m_stats = new ArrayList<StatsRow>();
    if(m_threadConfig != null){
      m_Rootbeer.setThreadConfig(m_threadConfig);
      m_threadConfig = null;
    } else {
      m_Rootbeer.clearThreadConfig();
    }
    m_Rootbeer.runAll(job_template);
  }
  
  public void runAll(List<Kernel> jobs) {
    if(jobs.isEmpty()){
      m_ranGpu = false;
      return;
    }
    if(jobs.get(0) instanceof CompiledKernel == false){
      for(Kernel job : jobs){
        job.gpuMethod();
      }
      m_ranGpu = false;
    } else {
      //this must happen above Rootbeer.runAll in case exceptions are thrown
      m_ranGpu = true;
      
      m_stats = new ArrayList<StatsRow>();
      if(m_threadConfig != null){
        m_Rootbeer.setThreadConfig(m_threadConfig);
        m_threadConfig = null;
      } else {
        m_Rootbeer.clearThreadConfig();
      }
      m_Rootbeer.runAll(jobs);
    }
  }
  
  public void printMem(int start, int len){
    m_Rootbeer.printMem(start, len);
  }

  public boolean getRanGpu(){
    return m_ranGpu;  
  }
  
  public List<GpuDevice> getDevices(){
    List<GpuDevice> ret = new ArrayList<GpuDevice>();
    
    return ret;
  }
  
  public void addStatsRow(StatsRow row) {
    m_stats.add(row);
  }
  
  public List<StatsRow> getStats(){
    return m_stats;
  }
  
  public List<GpuDevice> getGpuDevices(){
    return GpuDeviceSetup.v().getDevices();
  }
  
  public static void main(String[] args){
    Rootbeer rootbeer = new Rootbeer();
    List<GpuDevice> devices = rootbeer.getGpuDevices();
    for(GpuDevice device : devices){
      System.out.println("device: "+device.getId());
    }
  }
}
