/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

public class GpuDevice {

  private int m_id;
  private int m_majorNum;
  private int m_minorNum;
  private String m_deviceName;
  private int m_freeMem;
  private int m_totalMem;
  private int m_threadsPerBlock;
  private int m_mpCount;
  private int m_blockDimX;
  private int m_gridDimX;
  
  public GpuDevice(int id, int major_num, int minor_num, String device_name, 
    int free_mem, int total_mem, int threads_per_block, int mp_count, 
    int block_dim_x, int grid_dim_x){
    
    m_id = id;
    m_majorNum = major_num;
    m_minorNum = minor_num;
    m_deviceName = device_name;
    m_freeMem = free_mem;
    m_totalMem = total_mem;
    m_threadsPerBlock = threads_per_block;
    m_mpCount = mp_count;
    m_blockDimX = block_dim_x;
    m_gridDimX = grid_dim_x;
  }
  
  public int getId(){
    return m_id;
  }
  
  public int getMajorNum(){
    return m_majorNum;
  }
  
  public int getMinorNum(){
    return m_minorNum;
  }
  
  public String getDeviceName(){
    return m_deviceName;
  }
  
  public int getFreeMem(){
    return m_freeMem;
  }
  
  public int getTotalMem(){
    return m_totalMem;
  }
  
  public int getThreadsPerBlock(){
    return m_threadsPerBlock;
  }
  
  public int getMpCount(){
    return m_mpCount;
  }
  
  public int getBlockDimX(){
    return m_blockDimX;
  }
  
  public int getGridDimX(){
    return m_gridDimX;
  }
}
