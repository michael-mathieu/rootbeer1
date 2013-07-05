/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.cpu;

import edu.syr.pcpratts.rootbeer.runtime.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CpuRuntime {

  private static CpuRuntime mInstance = null;
  private List<CpuCore> m_Cores;

  public static CpuRuntime v(){
    if(mInstance == null)
      mInstance = new CpuRuntime();
    return mInstance;
  }

  private CpuRuntime(){
    m_Cores = new ArrayList<CpuCore>();
    int num_cores = Runtime.getRuntime().availableProcessors();
    for(int i = 0; i < num_cores; ++i){
      m_Cores.add(new CpuCore());
    }
  }

  public void run(List<Kernel> jobs, Rootbeer rootbeer, ThreadConfig thread_config) {
    Iterator<Kernel> iter = jobs.iterator();
    for(int i = 0; i < m_Cores.size(); ++i){
      if(iter.hasNext()){
        Kernel job = iter.next();
        m_Cores.get(i).enqueue(job);
      }
    }
  }

  public boolean isGpuPresent() {
    return true;
  }
}
