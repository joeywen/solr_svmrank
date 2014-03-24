package com.joey.svm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ShardDoc;

public abstract class SVMModel implements Model, ModelHelper {

  public svm_model model;
  public TreeMap<Integer, ScaleMinMax> sMinMax = new TreeMap<Integer, ScaleMinMax>();
  protected String mfile = "";
  protected String sfile = "";
  protected double coeffAtt = 0.007;
  protected double lower = 0;
  protected double upper = 1;

  public void init(NamedList args) throws IOException {
    this.mfile = (String) args.get("mfile");
    this.sfile = (String) args.get("sfile");
    Object attCoeffArg = args.get("coeffAtt");
    if (attCoeffArg != null)
      coeffAtt = Double.parseDouble((String) attCoeffArg);

    this.model = svm.svm_load_model(this.mfile);
    this.loadScale(this.sfile);
  }

  public void loadScale(String fname) {
    BufferedReader fp = null;
    if (fname != null) {
      int idx;
      double vmin, vmax;
      try {
        fp = new BufferedReader(new FileReader(fname));

        String restore_line = null;
        while ((restore_line = fp.readLine()) != null) {
          StringTokenizer st2 = new StringTokenizer(restore_line);
          if (st2.countTokens() < 3) {
            continue;
          }
          idx = Integer.parseInt(st2.nextToken());
          vmin = Double.parseDouble(st2.nextToken());
          vmax = Double.parseDouble(st2.nextToken());
          sMinMax.put(idx, new ScaleMinMax(vmin, vmax));
        }

        fp.close();
      } catch (Exception e) {
        System.err.println("Catch Exception. " + fname);
        e.printStackTrace();
      }
    }
  }

  @Override
  public double predict(ShardDoc sdoc, SolrParams params) {
    double label = 0.0;
    svm_node[] x = doc2vec(sdoc, params);
    label = svm.svm_predict(getRealModel(), x);
    return label;
  }

  protected svm_node[] doc2vec(ShardDoc sdoc, SolrParams params) {
    HashMap<Integer, Object> signals = getSignals(sdoc, params);
    return vectorization(signals);
  }

  protected svm_node[] vectorization(HashMap<Integer, Object> signals) {
    int size = signals.size();
    svm_node[] nodes = new svm_node[size];
    TreeMap<Integer, ScaleMinMax> sMinMax = getMinMax();
    int i = 0;
    Set<Integer> indexs = signals.keySet();
    ScaleMinMax smm = new ScaleMinMax();
    for (Integer idx : indexs) {
      smm = sMinMax.get(idx);
      if (smm == null) {
        nodes[i] = new svm_node();
        nodes[i].index = idx;
        nodes[i].value = 0.0;
        i++;
        continue;
      }
      Object obj = signals.get(idx);
      if (obj == null) {
        nodes[i] = new svm_node();
        nodes[i].index = idx;
        nodes[i].value = 0.0;
        i++;
        continue;
      }
      double value = Double.parseDouble(obj.toString());
      if (idx.intValue() == 6) {
        value = (new Date().getTime() / 1000.0) - value;
        value = Math.exp(-1 * coeffAtt * value / 3600.0);
      }

      if (value >= smm.vmax_) {
        value = upper;
      } else if (value <= smm.vmin_) {
        value = lower;
      } else {
        value = lower + (upper - lower) * (value - smm.vmin_)
            / (smm.vmax_ - smm.vmin_);
      }

      nodes[i] = new svm_node();
      nodes[i].index = idx;
      nodes[i].value = value;
      i++;
    }

    return nodes;
  }

  protected String vec2str(svm_node[] x) {
    String res = "";
    int i = 0;

    for (i = 0; i < x.length; i++) {
      res += Integer.toString(x[i].index) + ":" + Double.toString(x[i].value)
          + " ";
    }

    return res;
  }

  public svm_model getRealModel() {
    return model;
  }

  public TreeMap<Integer, ScaleMinMax> getMinMax() {
    return sMinMax;
  }

  public double getCoeffAtt() {
    return coeffAtt;
  }

  static public class ScaleMinMax {
    public double vmin_;
    public double vmax_;

    public ScaleMinMax() {
      vmin_ = 0.0;
      vmax_ = 0.0;
    }

    public ScaleMinMax(double vmin, double vmax) {
      vmin_ = vmin;
      vmax_ = vmax;
    }

    public void set(double vmin, double vmax) {
      vmin_ = vmin;
      vmax_ = vmax;
    }
  }

}
