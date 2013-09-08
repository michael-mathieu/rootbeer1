/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test;

import org.trifort.rootbeer.remap.java.util.ArrayList;
import org.trifort.rootbeer.remap.java.util.List;


public class ApplicationMain implements TestApplicationFactory {

  public List<TestApplication> getProviders() {
    List<TestApplication> ret = new ArrayList<TestApplication>();
    return ret;
  }

}
