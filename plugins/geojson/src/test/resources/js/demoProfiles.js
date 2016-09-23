/**
 * Created by gnain on 12/09/16.
 */

var Demo = function () {

    var mymap;
    var userPositionMarker;
    var filterCircle;
    var markers;
    var graph;
    var currentContract;
    var flatpickr;
    var currentNodeId;

    var init = function () {

        graph = new org.mwg.GraphBuilder().withStorage(new org.mwg.plugin.WSClient("ws://" + window.location.hostname + ":8050")).withPlugin(new org.mwg.structure.StructurePlugin()).withPlugin(new org.mwg.ml.MLPlugin()).build();
        graph.connect(function () {

            initOptionList();
            updateContract("Luxembourg");
            //updateContract("Paris")

        });
        initFlatpickr();
        initMap();
    };

    var initFlatpickr = function () {
        flatpickr = document.querySelector(".flatpickr").flatpickr(
            {
                defaultDate: new Date(),
                onChange: function (dateObject, dateString) {
                    showAvailabilities(currentNodeId, dateObject.getTime());
                }
            });
    };

    var fillOptionListTask = org.mwg.task.Actions
    //.hook(TimerHookFactory())
        .setTime("{{processTime}}")
        .fromIndexAll("cities")
        .get("name")
        .then(function (context) {
            var result = context.result();
            var s = result.size();

            var selector = document.querySelector("select");
            for (var i = 0; i < s; i++) {
                var val = result.get(i);
                var opt = document.createElement("option");
                opt.textContent = val;
                if (val == "Paris") {
                    opt.setAttribute("selected", "");
                }
                selector.appendChild(opt);
            }
            context.continueTask();
        });

    var initOptionList = function () {
        //console.log("Filling Option List");
        var context = fillOptionListTask.prepareWith(graph, null, function (result) {
            document.querySelector("#cities_init").textContent = document.querySelector("#cities_init").textContent + "... Done !";
        });
        context.setGlobalVariable("processTime", (new Date()).getTime());
        fillOptionListTask.executeUsing(context);
    };


    var initMap = function () {
        //PARIS: 48.8523947,2.3462913
        //Luxembourg: 49.632386, 6.168544
        mymap = L.map('mapid').setView([49.632386, 6.168544], 12);
        L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/streets-v9/tiles/256/{z}/{x}/{y}?access_token={accessToken}', {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
            maxZoom: 18,
            id: 'test',
            accessToken: 'pk.eyJ1IjoiZ25haW4iLCJhIjoiY2lzbG96eWZwMDA3NzJucGtwMTd5bXh2MiJ9.tJUI9PFDrl7eENeVW9kaWw'
        }).on('load', function (e) {
            document.querySelector("#map_init").textContent = document.querySelector("#map_init").textContent + "... Done !";
        }).addTo(mymap);

        mymap.on('click', function (event) {
            //updateNearest(e);
            userPositionMarker.setLatLng([event.latlng.lat, event.latlng.lng]);
            filterCircle.setLatLng([event.latlng.lat, event.latlng.lng]);
            updateFilter();
        });

        markers = new L.layerGroup();
        markers.addTo(mymap);

        initiateUserPosition();

    };

    var initiateUserPosition = function () {
        /*
         if (navigator.geolocation) {
         navigator.geolocation.getCurrentPosition(function (position) {
         addUserPositionMarker(position.coords.latitude, position.coords.longitude);
         addFilterCircle(position.coords.latitude, position.coords.longitude)
         });
         } else {
         */
        //console.warn("Geolocation is not supported by this browser.");
        //Luxembourg: 49.632386, 6.168544
        //PARIS: 48.8344884,2.3716972
        addUserPositionMarker(49.632386, 6.168544);
        addFilterCircle(49.632386, 6.168544)
        //}
    };

    var addUserPositionMarker = function (lat, lng) {
        userPositionMarker = L.marker([lat, lng], {
            icon: L.icon({
                iconUrl: 'img/red_pin.png',
                iconAnchor: [15, 44],
                iconSize: [30, 44],
            }),
            draggable: true,
            zIndexOffset: 1000
        }).bindPopup("Your position").addTo(mymap);

        userPositionMarker["lastDrag"] = (new Date()).getTime();

        userPositionMarker.on("drag", function (event) {
            filterCircle.setLatLng([event.latlng.lat, event.latlng.lng]);
            if (((new Date()).getTime() - userPositionMarker.lastDrag) > 100) { // threshold
                userPositionMarker["lastDrag"] = (new Date()).getTime();
                updateFilter();
            }
        });

        userPositionMarker.on("dragend", function (event) {
            updateFilter();
        });

    };
    var addFilterCircle = function (lat, lng) {
        filterCircle = L.circle([lat, lng], 2000).addTo(mymap);
    };


    var displayStationsTask = org.mwg.task.Actions
    //.hook(TimerHookFactory())
        .setTime("{{processTime}}")
        .fromIndex("cities", "name={{contract_name}}")
        .traverseIndexAll("stations")
        .asVar("stations")
        .traverse("position")
        .asVar("positions")
        .foreach(
            org.mwg.task.Actions.then(function (context) {
                var index = context.variable("i").get(0);
                var station = context.variable("stations").get(index);
                var position = context.variable("positions").get(index);
                addMarker(station, position, context);
            })
        ).then(function (context) {
            context.continueTask();
        });

    function updateContract(contract_name) {
        //console.log("Updating contract:" + contract_name);
        currentContract = contract_name;
        markers.clearLayers();
        var context = displayStationsTask.prepareWith(graph, null, function (result) {
            result.free();
            document.querySelector("#markers_loading").textContent = document.querySelector("#markers_loading").textContent + "... Done !";
            document.querySelector(".modalDialog").style.setProperty("opacity", 0);
        });
        context.setVariable("contract_name", contract_name);
        context.setVariable("processTime", (new Date()).getTime());

        displayStationsTask.executeUsing(context);
    }

    var askKDTree = org.mwg.task.Actions
    // .hook(hookFactory)
        .setTime("{{processTime}}")
        .fromIndex("cities", "name={{contract_name}}")
        .traverse("positions")
        //.print("{{result}}")
        .action(org.mwg.structure.action.NTreeNearestNWithinRadius.NAME, "{{actionParam}}")
        .ifThen(function (context) {
                markers.clearLayers();
                return context.result().size() > 0;
            }, org.mwg.task.Actions.asVar("stations")
                .traverse("position")
                .asVar("positions")
                .foreach(org.mwg.task.Actions.then(function (context) {
                    var index = context.variable("i").get(0);
                    var station = context.variable("stations").get(index);
                    var position = context.variable("positions").get(index);
                    addMarker(station, position, context);
                })).then(function (context) {
                    context.continueTask();
                })
        );

    function updateFilter() {
        var form = document.querySelector("#filter_form");
        var nbStations = form.querySelector("[name=find_stations_nb]").value;
        var radiusStations = form.querySelector("[name=find_stations_radius]").value;
        filterCircle.setRadius(radiusStations);


        var context = askKDTree.prepareWith(graph, null, function (result) {
            result.free();
        });
        context.setVariable("actionParam", "" + userPositionMarker.getLatLng().lat + "," + userPositionMarker.getLatLng().lng + "," + nbStations + "," + radiusStations);
        context.setVariable("processTime", (new Date()).getTime());
        context.setVariable("contract_name", currentContract);
        askKDTree.executeUsing(context);
    };


    var addMarker = function (node, position, context) {
        var marker = L.marker([position.get("lat"), position.get("lng")])
        //.addTo(mymap)
            .bindPopup(node.get("name"));
        marker["node"] = node.id();
        marker.on('click', function (e) {
            currentNodeId = e.target.node;
            showAvailabilities(e.target.node, flatpickr.selectedDateObj.getTime());
        });
        markers.addLayer(marker);

        context.continueTask();
    };


    var showAvailabilitiesTask = org.mwg.task.Actions
    // .hook(hookFactory)
        .setTime("{{processTime}}")
        .lookup("{{nodeId}}")
        .asVar("node")
        //.println("{{result}}")
        .traverse("available_bikes")
        .asVar("available_bikes")
        .fromVar("node")
        .traverse("available_bike_stands")
        .asVar("available_bike_stands")
        .fromVar("node")
        .traverse("station_profile")
        .asVar("station_profile")
        .then(function (context) {
            document.querySelector("#all_stands").textContent = context.variable("node").get(0).get("bike_stands");
            document.querySelector("#free_stands").textContent = context.variable("available_bike_stands").get(0).get("value");
            document.querySelector("#available_bikes").textContent = context.variable("available_bikes").get(0).get("value");

            updateTrendTables(context.variable("station_profile").get(0), context.variable("node").get(0).get("bike_stands"));

            context.continueTask();
        });

    var updateTrendTables = function (gaussianSlotNode, totalStands) {
        var bikesTable = document.querySelector("#bikesAvailabilityTrend table");
        if (bikesTable == undefined) {
            document.querySelector("#bikesAvailabilityTrend").appendChild(createTable("bikesAvailabilityTrend"));
        }

        var standsTable = document.querySelector("#bikeStandsAvailabilityTrend table");
        if (standsTable == undefined) {
            document.querySelector("#bikeStandsAvailabilityTrend").appendChild(createTable("bikeStandsAvailabilityTrend"));
        }

        var currentTimestamp = gaussianSlotNode.time();
        var day = new Date(currentTimestamp);
        day.setHours(0, 0, 0);

        for (var i = 0; i < 24; i++) {
            (function (i) {
                gaussianSlotNode.jump((day.getTime() + (i * 3600 * 1000)), function (node) {
                    node.predict(function (prediction) {
                        var bikeLevel = document.querySelector("#bikesAvailabilityTrend_" + i + " div");
                        bikeLevel.className = 'level ';
                        if (prediction[0] / totalStands >= 0.75) {
                            bikeLevel.className += 'full';
                        } else if (prediction[0] / totalStands >= 0.5) {
                            bikeLevel.className += 'high';
                        } else if (prediction[0] / totalStands >= 0.30) {
                            bikeLevel.className += 'medium';
                        } else if (prediction[0] / totalStands >= 0.15) {
                            bikeLevel.className += 'low';
                        } else {
                            bikeLevel.className += 'empty';
                        }

                        var bikeStandLevel = document.querySelector("#bikeStandsAvailabilityTrend_" + i + " div");
                        bikeStandLevel.className = 'level ';
                        if (prediction[1] / totalStands >= 0.75) {
                            bikeStandLevel.className += 'full';
                        } else if (prediction[1] / totalStands >= 0.5) {
                            bikeStandLevel.className += 'high';
                        } else if (prediction[1] / totalStands >= 0.30) {
                            bikeStandLevel.className += 'medium';
                        } else if (prediction[1] / totalStands >= 0.15) {
                            bikeStandLevel.className += 'low';
                        } else {
                            bikeStandLevel.className += 'empty';
                        }
                    });
                });
            })(i);
        }


    };

    var createTable = function (id) {
        var standsTable = document.createElement("table");
        standsTable.style.setProperty("font-size", "8px");
        standsTable.style.setProperty("width", "100%");
        standsTable.style.setProperty("border-spacing", "0px");
        var slotLine = document.createElement("tr");
        var timeLine = document.createElement("tr");
        for (var i = 0; i < 24; i++) {
            var slot = document.createElement("td");
            slot.setAttribute("id", id + "_" + i);
            slot.style.setProperty("padding", "0px");
            slot.style.setProperty("vertical-align", "bottom");
            slot.style.setProperty("height", "20px");
            slot.style.setProperty("border-left", "0.5px solid grey");
            slotLine.appendChild(slot);
            var level = document.createElement("div");
            level.className = "level empty";
            slot.appendChild(level);
            var time = document.createElement("td");
            time.style.setProperty("width", "12px");
            time.style.setProperty("border-left", "0.5px solid grey");
            time.style.setProperty("border-top", "0.5px solid grey");
            time.style.setProperty("vertical-align", "bottom");
            time.textContent = "" + i;
            timeLine.appendChild(time);
        }
        standsTable.appendChild(slotLine);
        standsTable.appendChild(timeLine);
        return standsTable;
    };

    var showAvailabilities = function (nodeId, time) {
        var context = showAvailabilitiesTask.prepareWith(graph, null, function (result) {
            result.free();
        });
        context.setVariable("nodeId", nodeId);
        context.setVariable("processTime", ( time == undefined ? (new Date()).getTime() : time));
        showAvailabilitiesTask.executeUsing(context);

    };


    var getGraph = function () {
        return graph;
    };

    return {
        init: init,
        updateContract: updateContract,
        updateFilter: updateFilter,
        graph: getGraph
    };
};
