package com.joey.svm;

import java.util.List;

import org.apache.solr.handler.component.ShardDoc;
import org.apache.solr.util.plugin.NamedListInitializedPlugin;

public interface RankOperation extends NamedListInitializedPlugin{
  public double operate(List<ShardDoc> docs);
}
