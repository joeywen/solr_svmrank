package com.joey.svm;

import java.util.HashMap;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ShardDoc;

public interface ModelHelper {
  public HashMap<Integer,Object> getSignals(ShardDoc sdoc, SolrParams params);
} 
