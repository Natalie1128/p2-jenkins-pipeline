/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "GET User By Id Request"], "isController": false}, {"data": [1.0, 500, 1500, "DELETE Approval Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET All Approvals Request"], "isController": false}, {"data": [1.0, 500, 1500, "PUT Expense Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET One Expense Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET All User Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET All Expenses Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET Expense By User Non Pending Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET Approval by Expense Id Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET Approval By Id Request"], "isController": false}, {"data": [1.0, 500, 1500, "POST Create Expense Request"], "isController": false}, {"data": [1.0, 500, 1500, "GET Expense By User Request"], "isController": false}, {"data": [1.0, 500, 1500, "Login Request"], "isController": false}, {"data": [1.0, 500, 1500, "POST Approval Request"], "isController": false}, {"data": [1.0, 500, 1500, "DELETE Expense Request"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 3000, 0, 0.0, 10.040333333333326, 1, 290, 5.0, 21.0, 35.0, 88.98999999999978, 80.90178523272746, 1562.3953701846583, 15.511442546248853], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["GET User By Id Request", 200, 0, 0.0, 5.265, 1, 78, 3.0, 8.900000000000006, 21.94999999999999, 49.98000000000002, 5.881488016468166, 1.3554991912953975, 0.89026429936774], "isController": false}, {"data": ["DELETE Approval Request", 200, 0, 0.0, 17.494999999999987, 6, 158, 9.0, 40.70000000000002, 57.94999999999999, 137.86000000000013, 5.881142117799277, 1.2290668097744584, 1.0567677242920577], "isController": false}, {"data": ["GET All Approvals Request", 200, 0, 0.0, 11.850000000000001, 6, 86, 8.0, 20.80000000000001, 39.0, 74.97000000000003, 5.883218120311811, 1080.728324018238, 0.9020168407118695], "isController": false}, {"data": ["PUT Expense Request", 200, 0, 0.0, 16.67, 6, 244, 8.0, 32.70000000000002, 65.84999999999997, 122.91000000000008, 5.86080586080586, 1.5281593406593406, 1.688415750915751], "isController": false}, {"data": ["GET One Expense Request", 200, 0, 0.0, 5.774999999999996, 1, 63, 3.0, 16.0, 24.899999999999977, 37.97000000000003, 5.881660981061051, 1.4876466739207153, 0.9247533378426068], "isController": false}, {"data": ["GET All User Request", 200, 0, 0.0, 5.674999999999999, 1, 65, 3.0, 11.0, 24.0, 53.87000000000012, 5.900401227283456, 65.36538234599952, 0.8816029177484068], "isController": false}, {"data": ["GET All Expenses Request", 200, 0, 0.0, 9.140000000000004, 2, 107, 4.0, 22.0, 34.94999999999999, 88.62000000000035, 5.875958515732878, 272.76055974013576, 0.8951655551311808], "isController": false}, {"data": ["GET Expense By User Non Pending Request", 200, 0, 0.0, 4.615000000000002, 2, 82, 3.0, 5.0, 16.94999999999999, 34.98000000000002, 5.863039399624765, 1.9409866762429642, 0.9790817747420262], "isController": false}, {"data": ["GET Approval by Expense Id Request", 200, 0, 0.0, 7.434999999999999, 1, 142, 3.0, 15.900000000000006, 29.94999999999999, 103.86000000000013, 5.900053100477904, 1.6363428520856687, 0.979501003009027], "isController": false}, {"data": ["GET Approval By Id Request", 200, 0, 0.0, 5.335000000000001, 1, 138, 3.0, 5.0, 22.899999999999977, 78.69000000000028, 5.886334873590958, 1.6325381875974925, 0.9312365717985697], "isController": false}, {"data": ["POST Create Expense Request", 200, 0, 0.0, 15.29, 6, 119, 8.0, 36.70000000000002, 52.0, 106.98000000000002, 5.885122410546139, 1.5172581214689265, 1.6379491084039548], "isController": false}, {"data": ["GET Expense By User Request", 200, 0, 0.0, 7.689999999999997, 2, 76, 4.0, 17.80000000000001, 28.94999999999999, 69.93000000000006, 5.886161633998469, 268.8308025137589, 0.9369573694743657], "isController": false}, {"data": ["Login Request", 200, 0, 0.0, 5.839999999999997, 1, 103, 3.0, 14.900000000000006, 19.94999999999999, 61.850000000000136, 5.912087262407993, 1.3625513612580922, 1.3683248839752875], "isController": false}, {"data": ["POST Approval Request", 200, 0, 0.0, 15.340000000000005, 6, 290, 8.0, 28.80000000000001, 43.89999999999998, 150.5500000000004, 5.878203620973431, 1.658985201622384, 1.796755598988949], "isController": false}, {"data": ["DELETE Expense Request", 200, 0, 0.0, 17.189999999999994, 6, 226, 9.0, 28.900000000000006, 66.29999999999984, 179.59000000000037, 5.846927439630474, 1.2162065865637606, 1.0449098842308366], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 3000, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
