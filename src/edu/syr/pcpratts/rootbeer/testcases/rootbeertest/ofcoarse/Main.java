/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerFactory;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;

import org.trifort.rootbeer.remap.java.util.ArrayList;
import org.trifort.rootbeer.remap.java.util.Iterator;
import org.trifort.rootbeer.remap.java.util.List;

public class Main implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new OfCoarse());
    return ret;
  }

  public void makeHarder() {
    //ignore
  }
}
