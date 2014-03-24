package com.joey.svm;

import org.apache.solr.handler.component.ShardDoc;
import org.apache.solr.common.params.*;

public interface Model {
  public double predict(ShardDoc sdoc, SolrParams params);
}
