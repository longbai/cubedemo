import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.pandora.common.QiniuException;
import cube.CubeClient;
import in.zapr.druid.druidry.Interval;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.aggregator.HyperUniqueAggregator;
import in.zapr.druid.druidry.aggregator.LongSumAggregator;
import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.DruidJerseyClient;
import in.zapr.druid.druidry.dimension.DruidDimension;
import in.zapr.druid.druidry.dimension.SimpleDimension;
import in.zapr.druid.druidry.filter.AndFilter;
import in.zapr.druid.druidry.filter.SelectorFilter;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.postAggregator.ArithmeticFunction;
import in.zapr.druid.druidry.postAggregator.ArithmeticPostAggregator;
import in.zapr.druid.druidry.postAggregator.DruidPostAggregator;
import in.zapr.druid.druidry.postAggregator.FieldAccessPostAggregator;
import in.zapr.druid.druidry.query.aggregation.DruidGroupByQuery;
import in.zapr.druid.druidry.query.aggregation.DruidTopNQuery;
import in.zapr.druid.druidry.topNMetric.SimpleMetric;
import in.zapr.druid.druidry.topNMetric.TopNMetric;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) throws IOException {
        String ACCESS_KEY = "";
        String SECRET_KEY = "";
        String repo = "prod_session_analytic";
        SelectorFilter selectorFilter1 = new SelectorFilter("Country", "中国");
        SelectorFilter selectorFilter2 = new SelectorFilter("Province", "上海");

        AndFilter filter = new AndFilter(Arrays.asList(selectorFilter1, selectorFilter2));

        DruidAggregator aggregator1 = new LongSumAggregator("pv", "pv");
        DruidAggregator aggregator2 = new HyperUniqueAggregator("uv", "uv");


        DateTime startTime = new DateTime(2019, 3, 11, 0, 0, 0, DateTimeZone.getDefault());
        DateTime endTime = new DateTime(2019, 3, 12, 0, 0, 0, DateTimeZone.getDefault());
        Interval interval = new Interval(startTime, endTime);

        Granularity granularity = new SimpleGranularity(PredefinedGranularity.DAY);
        DruidDimension dimension = new SimpleDimension("Isp");
        TopNMetric metric = new SimpleMetric("pv");

        DruidTopNQuery query = DruidTopNQuery.builder()
                .dataSource(repo)
                .granularity(granularity)
                .filter(filter)
                .dimension(dimension)
                .aggregators(Arrays.asList(aggregator1, aggregator2))
                .intervals(Collections.singletonList(interval))
                .threshold(50)
                .topNMetric(metric)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String requiredJson = mapper.writeValueAsString(query);
        CubeClient cc = CubeClient.newCubeClient(ACCESS_KEY, SECRET_KEY);

        System.out.println(requiredJson);
        byte[] r = cc.query(requiredJson);

        System.out.println(new String(r));
    }

}
