# CPU Usage Dashboard

This document describes the CPU Usage Dashboard and how it was generated using a Java-based observability-as-code approach.

## Overview

The **CPU Usage Dashboard** is a Grafana dashboard that monitors CPU usage metrics from Prometheus. It visualizes the `cpu_usage` metric across all instances and provides visual indicators when CPU usage approaches or exceeds the alert threshold of 80%.

## Dashboard Features

- **Time-series visualization**: Displays CPU usage percentage over time for all monitored instances
- **Alert threshold indicators**: Color-coded thresholds:
  - Green: 0-60% (Normal)
  - Yellow: 60-80% (Warning)
  - Red: 80-100% (Critical - Alert firing)
- **Auto-refresh**: Updates every 5 seconds
- **Instance grouping**: Each server instance is displayed as a separate series in the legend
- **Time range**: Default view shows last 15 minutes

## Files

- **Dashboard JSON**: `grafana/dashboards/definitions/cpu-usage-dashboard.json`
- **Java Generator**: `dashboard-generator/src/main/java/com/grafana/dashboard/CpuUsageDashboardGenerator.java`
- **Maven Configuration**: `dashboard-generator/pom.xml`

## Prerequisites

- **Docker** and **Docker Compose** installed
- **k6 v1.2.0** or later (for generating test data)
- **Java 11+** and **Maven** (for regenerating the dashboard)

## Running the Environment

### Start Grafana and Prometheus

To run the demo environment and view the dashboard in your browser:

```bash
docker compose up
```

This command starts:
- **Grafana** - Dashboard platform at [http://localhost:3000](http://localhost:3000/)
- **Prometheus** - Metrics database at [http://localhost:9090](http://localhost:9090/)

Wait a few seconds for the services to start up completely. The dashboard is automatically provisioned and will be available immediately.

### Stop the Environment

To stop the services:

```bash
docker compose down
```

## How to Use the Dashboard

### 1. Access the Dashboard

Once the Docker containers are running:

1. Navigate to http://localhost:3000
2. Go to **Dashboards** in the left menu
3. Look in the **TestFolder** folder
4. Click on **CPU Usage Dashboard**

### 2. Generate Test Data

To see data in the dashboard, run the k6 test script:

```bash
k6 run testdata/1.cpu-usage.js
```

This script generates CPU usage metrics between 80-100% for a server instance called `server1`, which will trigger the alert threshold visualization.

### 3. View Alert Behavior

The dashboard is configured to work with the Prometheus alert rule defined in `prometheus/rules/1.basic.yml`:

```yaml
- alert: HighCPUUsage
  expr: avg_over_time(cpu_usage[5m])>80
```

When the 5-minute average CPU usage exceeds 80%, you'll see:
- The time-series line turn **red**
- The metric values displayed in red in the legend
- The alert firing in Prometheus (http://localhost:9090/alerts)
