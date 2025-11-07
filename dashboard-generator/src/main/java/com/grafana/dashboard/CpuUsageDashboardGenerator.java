package com.grafana.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CpuUsageDashboardGenerator {

    private static final String OUTPUT_PATH = "../grafana/dashboards/definitions/cpu-usage-dashboard.json";
    private static final String PROMETHEUS_DATASOURCE_UID = "DS_PROMETHEUS_UID";

    public static void main(String[] args) {
        try {
            Map<String, Object> dashboard = createDashboard();
            saveDashboard(dashboard);
            System.out.println("Dashboard JSON generated successfully at: " + OUTPUT_PATH);
        } catch (IOException e) {
            System.err.println("Error generating dashboard: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Map<String, Object> createDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("title", "CPU Usage Dashboard");
        dashboard.put("uid", "cpu-usage-dashboard");
        dashboard.put("tags", Arrays.asList("demo", "prometheus", "monitoring", "cpu"));
        dashboard.put("timezone", "browser");
        dashboard.put("schemaVersion", 38);
        dashboard.put("version", 1);
        dashboard.put("refresh", "5s");

        Map<String, Object> time = new LinkedHashMap<>();
        time.put("from", "now-15m");
        time.put("to", "now");
        dashboard.put("time", time);


        Map<String, Object> timepicker = new LinkedHashMap<>();
        timepicker.put("refresh_intervals", Arrays.asList("5s", "10s", "30s", "1m", "5m"));
        dashboard.put("timepicker", timepicker);


        dashboard.put("panels", Arrays.asList(createCpuUsagePanel()));


        Map<String, Object> templating = new LinkedHashMap<>();
        templating.put("list", new ArrayList<>());
        dashboard.put("templating", templating);


        Map<String, Object> annotations = new LinkedHashMap<>();
        annotations.put("list", new ArrayList<>());
        dashboard.put("annotations", annotations);


        dashboard.put("editable", true);
        dashboard.put("fiscalYearStartMonth", 0);
        dashboard.put("graphTooltip", 1);
        dashboard.put("links", new ArrayList<>());
        dashboard.put("liveNow", false);
        dashboard.put("style", "dark");

        return dashboard;
    }


    private static Map<String, Object> createCpuUsagePanel() {
        Map<String, Object> panel = new LinkedHashMap<>();


        panel.put("id", 1);
        panel.put("type", "timeseries");
        panel.put("title", "CPU Usage");
        panel.put("description", "CPU usage percentage across all instances. Alert threshold is set at 80%.");


        Map<String, Object> gridPos = new LinkedHashMap<>();
        gridPos.put("h", 9);
        gridPos.put("w", 24);
        gridPos.put("x", 0);
        gridPos.put("y", 0);
        panel.put("gridPos", gridPos);


        Map<String, Object> datasource = new LinkedHashMap<>();
        datasource.put("type", "prometheus");
        datasource.put("uid", PROMETHEUS_DATASOURCE_UID);
        panel.put("datasource", datasource);


        panel.put("targets", Arrays.asList(createPrometheusTarget()));


        panel.put("fieldConfig", createFieldConfig());


        panel.put("options", createPanelOptions());

        return panel;
    }


    private static Map<String, Object> createPrometheusTarget() {
        Map<String, Object> target = new LinkedHashMap<>();

        Map<String, Object> datasource = new LinkedHashMap<>();
        datasource.put("type", "prometheus");
        datasource.put("uid", PROMETHEUS_DATASOURCE_UID);
        target.put("datasource", datasource);

        target.put("expr", "cpu_usage");
        target.put("refId", "A");
        target.put("legendFormat", "{{instance}}");
        target.put("instant", false);
        target.put("range", true);

        return target;
    }


    private static Map<String, Object> createFieldConfig() {
        Map<String, Object> fieldConfig = new LinkedHashMap<>();


        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("unit", "percent");
        defaults.put("min", 0);
        defaults.put("max", 100);


        Map<String, Object> color = new LinkedHashMap<>();
        color.put("mode", "thresholds");
        defaults.put("color", color);


        Map<String, Object> thresholds = new LinkedHashMap<>();
        thresholds.put("mode", "absolute");

        List<Map<String, Object>> steps = new ArrayList<>();

        Map<String, Object> step1 = new LinkedHashMap<>();
        step1.put("color", "green");
        step1.put("value", null);
        steps.add(step1);

        Map<String, Object> step2 = new LinkedHashMap<>();
        step2.put("color", "yellow");
        step2.put("value", 60);
        steps.add(step2);

        Map<String, Object> step3 = new LinkedHashMap<>();
        step3.put("color", "red");
        step3.put("value", 80); // Alert threshold
        steps.add(step3);

        thresholds.put("steps", steps);
        defaults.put("thresholds", thresholds);


        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put("drawStyle", "line");
        custom.put("lineInterpolation", "linear");
        custom.put("barAlignment", 0);
        custom.put("lineWidth", 1);
        custom.put("fillOpacity", 10);
        custom.put("gradientMode", "none");
        custom.put("spanNulls", false);
        custom.put("showPoints", "never");
        custom.put("pointSize", 5);

        Map<String, Object> stacking = new LinkedHashMap<>();
        stacking.put("mode", "none");
        stacking.put("group", "A");
        custom.put("stacking", stacking);

        custom.put("axisPlacement", "auto");
        custom.put("axisLabel", "");
        custom.put("scaleDistribution", Map.of("type", "linear"));
        custom.put("hideFrom", Map.of("tooltip", false, "viz", false, "legend", false));

        Map<String, Object> thresholdsStyle = new LinkedHashMap<>();
        thresholdsStyle.put("mode", "line");
        custom.put("thresholdsStyle", thresholdsStyle);

        defaults.put("custom", custom);

        fieldConfig.put("defaults", defaults);
        fieldConfig.put("overrides", new ArrayList<>());

        return fieldConfig;
    }


    private static Map<String, Object> createPanelOptions() {
        Map<String, Object> options = new LinkedHashMap<>();

        Map<String, Object> tooltip = new LinkedHashMap<>();
        tooltip.put("mode", "multi");
        tooltip.put("sort", "none");
        options.put("tooltip", tooltip);

        Map<String, Object> legend = new LinkedHashMap<>();
        legend.put("showLegend", true);
        legend.put("displayMode", "list");
        legend.put("placement", "bottom");
        legend.put("calcs", new ArrayList<>());
        options.put("legend", legend);

        return options;
    }


    private static void saveDashboard(Map<String, Object> dashboard) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        File outputFile = new File(OUTPUT_PATH);
        outputFile.getParentFile().mkdirs();

        mapper.writeValue(outputFile, dashboard);
    }
}
