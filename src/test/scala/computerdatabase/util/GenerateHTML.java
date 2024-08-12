package computerdatabase.util;

import org.json.JSONObject;
import org.json.JSONArray;

public class GenerateHTML {

    public static String generateHTMLContent(JSONObject statsJson) {
        JSONObject allRequests = statsJson.getJSONObject("stats");
        int totalRequests = allRequests.getJSONObject("numberOfRequests").getInt("total");
        int okRequests = allRequests.getJSONObject("numberOfRequests").getInt("ok");
        int koRequests = allRequests.getJSONObject("numberOfRequests").getInt("ko");

        int minResponseTime = allRequests.getJSONObject("minResponseTime").getInt("total");
        int maxResponseTime = allRequests.getJSONObject("maxResponseTime").getInt("total");
        int meanResponseTime = allRequests.getJSONObject("meanResponseTime").getInt("total");
        int standardDeviation = allRequests.getJSONObject("standardDeviation").getInt("total");

        JSONArray paths = new JSONArray();
        JSONObject contents = statsJson.getJSONObject("contents");
        for (String key : contents.keySet()) {
            JSONObject requestStats = contents.getJSONObject(key).getJSONObject("stats");
            JSONObject pathStats = new JSONObject();
            pathStats.put("name", requestStats.getString("name"));
            pathStats.put("numberOfRequests", requestStats.getJSONObject("numberOfRequests"));
            pathStats.put("standardDeviation", requestStats.getJSONObject("standardDeviation"));
            pathStats.put("meanResponseTime", requestStats.getJSONObject("meanResponseTime"));
            pathStats.put("ko", requestStats.getJSONObject("numberOfRequests").getInt("ko"));
            paths.put(pathStats);
        }

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Gatling Test Analysis</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background-color: #121212;\n" +
                "            color: #ffffff;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "        }\n" +
                "        .dashboard {\n" +
                "            display: flex;\n" +
                "            flex-wrap: wrap;\n" +
                "            gap: 20px;\n" +
                "        }\n" +
                "        .card {\n" +
                "            border: 1px solid #333;\n" +
                "            border-radius: 8px;\n" +
                "            padding: 16px;\n" +
                "            width: 600px;\n" +
                "            background-color: #1e1e1e;\n" +
                "        }\n" +
                "        .card h3 {\n" +
                "            margin-top: 0;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .chart {\n" +
                "            width: 600px;\n" +
                "            height: 400px;\n" +
                "        }\n" +
                "        .explanation {\n" +
                "            margin-top: 0;\n" +
                "            color: #aaaaaa;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Gatling Test Analysis Dashboard</h1>\n" +
                "<div class=\"dashboard\">\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Failure Rate Analysis</h3>\n" +
                "        <p class=\"explanation\">This chart shows the failure rate as a percentage for each request type. It helps identify which request types are failing more often, providing insight into potential areas for improvement.</p>\n" +
                "        <canvas id=\"failureRateChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Response Time Variability</h3>\n" +
                "        <p class=\"explanation\">This chart displays the standard deviation of response times for each request type, indicating how much the response times vary. High variability can indicate inconsistent performance, which might need further investigation.</p>\n" +
                "        <canvas id=\"responseTimeVariabilityChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Request Rate vs Error Rate Correlation</h3>\n" +
                "        <p class=\"explanation\">This chart shows the correlation between the request rate and the error rate over time.</p>\n" +
                "        <canvas id=\"requestErrorRateCorrelationChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Cumulative Error Count</h3>\n" +
                "        <p class=\"explanation\">This chart shows the cumulative error count over the duration of the test.</p>\n" +
                "        <canvas id=\"cumulativeErrorCountChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Top N Slowest Endpoints</h3>\n" +
                "        <p class=\"explanation\">This chart shows the top N slowest endpoints based on mean response time.</p>\n" +
                "        <canvas id=\"slowestEndpointsChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Response Time Distributions</h3>\n" +
                "        <p class=\"explanation\">This chart compares the response time distributions of different endpoints.</p>\n" +
                "        <canvas id=\"responseTimeDistributionChart\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"card\">\n" +
                "        <h3>Heatmap of Response Times</h3>\n" +
                "        <p class=\"explanation\">This chart shows a heatmap of response times for different endpoints.</p>\n" +
                "        <canvas id=\"responseTimeHeatmap\" class=\"chart\"></canvas>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "<script src=\"https://cdn.jsdelivr.net/npm/chartjs-chart-matrix@1.2.0/dist/chartjs-chart-matrix.min.js\"></script>\n" +
                "<script>\n" +
                "    var stats = " + paths.toString() + ";\n" +
                "    var totalRequests = " + totalRequests + ";\n" +
                "    var okRequests = " + okRequests + ";\n" +
                "    var koRequests = " + koRequests + ";\n" +
                "    // Extract failure rate data\n" +
                "    var failureRateData = {\n" +
                "        labels: stats.map(path => path.name),\n" +
                "        datasets: [{\n" +
                "            label: 'Failure Rate (%)',\n" +
                "            data: stats.map(path => (path.numberOfRequests.ko / path.numberOfRequests.total) * 100),\n" +
                "            backgroundColor: 'rgba(255, 99, 132, 0.2)',\n" +
                "            borderColor: 'rgba(255, 99, 132, 1)',\n" +
                "            borderWidth: 2\n" +
                "        }]\n" +
                "    };\n" +
                "    // Extract response time variability data\n" +
                "    var responseTimeVariabilityData = {\n" +
                "        labels: stats.map(path => path.name),\n" +
                "        datasets: [{\n" +
                "            label: 'Standard Deviation (ms)',\n" +
                "            data: stats.map(path => path.standardDeviation.total),\n" +
                "            backgroundColor: 'rgba(54, 162, 235, 0.2)',\n" +
                "            borderColor: 'rgba(54, 162, 235, 1)',\n" +
                "            borderWidth: 2\n" +
                "        }]\n" +
                "    };\n" +
                "    // Extract request rate vs error rate correlation data\n" +
                "    var requestErrorRateCorrelationData = {\n" +
                "        labels: Array.from({length: totalRequests}, (_, i) => i + 1),\n" +
                "        datasets: [{\n" +
                "            label: 'Request Rate',\n" +
                "            data: Array(totalRequests).fill(okRequests / totalRequests),\n" +
                "            backgroundColor: 'rgba(75, 192, 192, 0.2)',\n" +
                "            borderColor: 'rgba(75, 192, 192, 1)',\n" +
                "            borderWidth: 2,\n" +
                "            yAxisID: 'y',\n" +
                "        }, {\n" +
                "            label: 'Error Rate',\n" +
                "            data: Array(totalRequests).fill(koRequests / totalRequests),\n" +
                "            backgroundColor: 'rgba(255, 99, 132, 0.2)',\n" +
                "            borderColor: 'rgba(255, 99, 132, 1)',\n" +
                "            borderWidth: 2,\n" +
                "            yAxisID: 'y1',\n" +
                "        }]\n" +
                "    };\n" +
                "    // Extract cumulative error count data\n" +
                "    var cumulativeErrorCountData = {\n" +
                "        labels: Array.from({length: totalRequests}, (_, i) => i + 1),\n" +
                "        datasets: [{\n" +
                "            label: 'Cumulative Errors',\n" +
                "            data: Array(totalRequests).fill(0).map((_, i) => (i + 1) * (koRequests / totalRequests)),\n" +
                "            backgroundColor: 'rgba(255, 99, 132, 0.2)',\n" +
                "            borderColor: 'rgba(255, 99, 132, 1)',\n" +
                "            borderWidth: 2,\n" +
                "            pointStyle: 'rectRot',\n" +
                "            pointRadius: 5,\n" +
                "            pointHoverRadius: 7\n" +
                "        }]\n" +
                "    };\n" +
                "    // Extract top N slowest endpoints data\n" +
                "    var slowestEndpoints = stats.sort((a, b) => b.meanResponseTime.total - a.meanResponseTime.total).slice(0, 8);\n" +
                "    var slowestEndpointsData = {\n" +
                "        labels: slowestEndpoints.map(path => path.name),\n" +
                "        datasets: [{\n" +
                "            label: 'Mean Response Time (ms)',\n" +
                "            data: slowestEndpoints.map(path => path.meanResponseTime.total),\n" +
                "            backgroundColor: 'rgba(255, 206, 86, 0.2)',\n" +
                "            borderColor: 'rgba(255, 206, 86, 1)',\n" +
                "            borderWidth: 2\n" +
                "        }]\n" +
                "    };\n" +
                "    // Extract response time distribution data\n" +
                "    var responseTimeDistributionData = {\n" +
                "        labels: stats.map(path => path.name),\n" +
                "        datasets: stats.map((path, index) => ({\n" +
                "            label: path.name,\n" +
                "            data: [\n" +
                "                {x: path.meanResponseTime.total, y: path.numberOfRequests.total}\n" +
                "            ],\n" +
                "            backgroundColor: 'rgba(' + (index * 30) % 255 + ', 99, 132, 0.2)',\n" +
                "            borderColor: 'rgba(' + (index * 30) % 255 + ', 99, 132, 1)',\n" +
                "            borderWidth: 2\n" +
                "        }))\n" +
                "    };\n" +
                "    // Extract heatmap data\n" +
                "    var heatmapData = {\n" +
                "        labels: stats.map(path => path.name),\n" +
                "        datasets: [{\n" +
                "            label: 'Heatmap',\n" +
                "            data: stats.map((path, rowIndex) => ({\n" +
                "                x: rowIndex,\n" +
                "                y: path.meanResponseTime.total,\n" +
                "                v: path.numberOfRequests.total\n" +
                "            })),\n" +
                "            backgroundColor: (context) => {\n" +
                "                var value = context.dataset.data[context.dataIndex].v;\n" +
                "                var alpha = value / totalRequests;\n" +
                "                return 'rgba(255, 99, 132, ' + alpha + ')';\n" +
                "            },\n" +
                "            borderColor: 'rgba(255, 99, 132, 1)',\n" +
                "            borderWidth: 2,\n" +
                "            width: (context) => {\n" +
                "                var a = context.chart.chartArea;\n" +
                "                var b = context.dataset.data.length;\n" +
                "                return (a.right - a.left) / b;\n" +
                "            },\n" +
                "            height: (context) => {\n" +
                "                var a = context.chart.chartArea;\n" +
                "                var b = context.chart.data.labels.length;\n" +
                "                return (a.bottom - a.top) / b;\n" +
                "            }\n" +
                "        }]\n" +
                "    };\n" +
                "    // Create the failure rate chart\n" +
                "    var ctx1 = document.getElementById('failureRateChart').getContext('2d');\n" +
                "    var failureRateChart = new Chart(ctx1, {\n" +
                "        type: 'bar',\n" +
                "        data: failureRateData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Failure Rate (%)',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff',\n" +
                "                        callback: function(value) { return value + '%' }\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Endpoints',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                tooltip: {\n" +
                "                    callbacks: {\n" +
                "                        label: function(context) {\n" +
                "                            return context.dataset.label + ': ' + context.raw.toFixed(2) + '%';\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                legend: {\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the response time variability chart\n" +
                "    var ctx2 = document.getElementById('responseTimeVariabilityChart').getContext('2d');\n" +
                "    var responseTimeVariabilityChart = new Chart(ctx2, {\n" +
                "        type: 'bar',\n" +
                "        data: responseTimeVariabilityData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Standard Deviation (ms)',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Endpoints',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the request rate vs error rate correlation chart\n" +
                "    var ctx3 = document.getElementById('requestErrorRateCorrelationChart').getContext('2d');\n" +
                "    var requestErrorRateCorrelationChart = new Chart(ctx3, {\n" +
                "        type: 'line',\n" +
                "        data: requestErrorRateCorrelationData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Request Rate',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                y1: {\n" +
                "                    beginAtZero: true,\n" +
                "                    position: 'right',\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Error Rate',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        drawOnChartArea: false,\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Request Number',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the cumulative error count chart\n" +
                "    var ctx4 = document.getElementById('cumulativeErrorCountChart').getContext('2d');\n" +
                "    var cumulativeErrorCountChart = new Chart(ctx4, {\n" +
                "        type: 'line',\n" +
                "        data: cumulativeErrorCountData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Cumulative Errors',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Request Number',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            elements: {\n" +
                "                point: {\n" +
                "                    radius: 5,\n" +
                "                    hoverRadius: 7,\n" +
                "                    backgroundColor: 'rgba(255, 99, 132, 1)',\n" +
                "                    borderColor: '#ffffff'\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the slowest endpoints chart\n" +
                "    var ctx5 = document.getElementById('slowestEndpointsChart').getContext('2d');\n" +
                "    var slowestEndpointsChart = new Chart(ctx5, {\n" +
                "        type: 'bar',\n" +
                "        data: slowestEndpointsData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Mean Response Time (ms)',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Endpoints',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    display: false,\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the response time distribution chart\n" +
                "    var ctx6 = document.getElementById('responseTimeDistributionChart').getContext('2d');\n" +
                "    var responseTimeDistributionChart = new Chart(ctx6, {\n" +
                "        type: 'scatter',\n" +
                "        data: responseTimeDistributionData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Number of Requests',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Response Time (ms)',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    // Create the response time heatmap chart\n" +
                "    var ctx7 = document.getElementById('responseTimeHeatmap').getContext('2d');\n" +
                "    var responseTimeHeatmapChart = new Chart(ctx7, {\n" +
                "        type: 'matrix',\n" +
                "        data: heatmapData,\n" +
                "        options: {\n" +
                "            scales: {\n" +
                "                y: {\n" +
                "                    beginAtZero: true,\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Response Time (ms)',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                x: {\n" +
                "                    title: {\n" +
                "                        display: true,\n" +
                "                        text: 'Endpoints',\n" +
                "                        color: '#ffffff'\n" +
                "                    },\n" +
                "                    grid: {\n" +
                "                        color: '#444444'\n" +
                "                    },\n" +
                "                    ticks: {\n" +
                "                        color: '#ffffff',\n" +
                "                        callback: function(value) {\n" +
                "                            return stats[value].name;\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            plugins: {\n" +
                "                legend: {\n" +
                "                    display: false,\n" +
                "                    labels: {\n" +
                "                        color: '#ffffff'\n" +
                "                    }\n" +
                "                },\n" +
                "                tooltip: {\n" +
                "                    callbacks: {\n" +
                "                        label: function(context) {\n" +
                "                            var index = context.dataIndex;\n" +
                "                            return stats[index].name + ': ' + context.raw.y + ' ms (' + context.raw.v + ' requests)';\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }
}
