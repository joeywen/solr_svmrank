package com.joey.svm;

import java.util.HashMap;
import java.util.List;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ShardDoc;

public class TestModel extends SVMModel implements RankOperation{

  @Override
  public void init(NamedList args) {
    
  }
  
  @Override
  public double operate(List<ShardDoc> docs) {
    return 0;
  }

  @Override
  public HashMap<Integer, Object> getSignals(ShardDoc sdoc, SolrParams params) {
    return null;
  }

}
