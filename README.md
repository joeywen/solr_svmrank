solr_svmrank
============

    能够直接应用于Solr的SVMRank java代码。
    采用libSVM 的jar包。

    使用时可以在SolrConfig.xml文件中进行简单配置，例如
    
    <searchComponent name="SVMRank" class="org.apache.solr.handler.component.QueryComponent">
      <str name="RankClass">com.joey.svmrank.TestModel</str>
      <lst name="models">
            <str name="mfile">rank/svmdata/model</str>
            <str name="sfile">rank/svmdata/scale</str>
      </lst>
      <lst name="RankArgs"> 
          <str name="similityPercent">0.5</str>
          <str name="coeffGP">3</str> 
          <str name="coeffTextScore">1</str> 
          <str name="coeffTPScore">3</str> 
          <str name="coeffBPScore">1</str> 
          <str name="coeffStockScore">1</str> 
      </lst>
   </searchComponent>
   

Contact
============
    1.joey.wen@outlook.com
    2.zhgwen@outlook.com

