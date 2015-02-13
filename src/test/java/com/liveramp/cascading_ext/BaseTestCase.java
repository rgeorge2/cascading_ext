/**
 *  Copyright 2012 LiveRamp
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.liveramp.cascading_ext;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;

import cascading.tap.Tap;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntryIterator;

import com.liveramp.cascading_ext.bloom.BloomProps;
import com.liveramp.cascading_ext.fs.TrashHelper;

import static org.junit.Assert.assertTrue;

public abstract class BaseTestCase {
  private String TEST_ROOT;
  protected static final FileSystem fs = FileSystemHelper.getFS();

  static {
    Logger.getRootLogger().setLevel(Level.ALL);
  }

  protected BaseTestCase() {
    TEST_ROOT = "/tmp/cascading_ext_" + this.getClass().getSimpleName() + "_AUTOGEN";
  }

  @Before
  public void baseSetUp() throws Exception {

    // set the default job polling interval to 10ms. this makes the tests run *much* faster.
    CascadingUtil.get().setDefaultProperty("cascading.flow.job.pollinginterval", 10);
    CascadingUtil.get().setDefaultProperty("io.sort.record.percent", 0.10);

    CascadingUtil.get().setDefaultProperty(BloomProps.TEST_MODE, true);

    TrashHelper.deleteUsingTrashIfEnabled(fs, new Path(TEST_ROOT));

    System.err.println("------ test start ------");
    System.out.println("------ test start ------");
  }

  protected String getTestRoot() {
    return TEST_ROOT;
  }

  protected List<Tuple> getAllTuples(Tap sink) throws IOException {
    List<Tuple> ret = Lists.newArrayList();
    TupleEntryIterator tupleEntryIterator = sink.openForRead(CascadingUtil.get().getFlowProcess());
    while (tupleEntryIterator.hasNext()) {
      ret.add(new Tuple(tupleEntryIterator.next().getTuple()));
    }
    return ret;
  }

  protected void printCollection(Collection coll) {
    for (Object item : coll) {
      System.out.println(item);
    }
  }

  protected <T> void assertCollectionEquivalent(Collection<T> expected, Collection<T> actual) {
    assertTrue(CollectionUtils.isEqualCollection(expected, actual));
  }
}
