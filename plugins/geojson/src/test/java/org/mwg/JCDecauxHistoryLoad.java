package org.mwg;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.importer.ImporterPlugin;
import org.mwg.importer.action.ReadFiles;
import org.mwg.memory.offheap.OffHeapMemoryPlugin;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;

import static org.mwg.importer.ImporterActions.READFILES;
import static org.mwg.plugin.geojson.GeoJsonActions.LOADJSON;
import static org.mwg.task.Actions.*;

public class JCDecauxHistoryLoad {

    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/dataset";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/subset";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedSubset/Luxembourg_BRICHERHAFF.json";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedDataset";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedSubset";
    public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedDatasetSubset";


    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withStorage(new LevelDBStorage("fullTempStorage2")).withMemorySize(2000000).withPlugin(new ImporterPlugin()).withPlugin(new GeoJsonPlugin()).withPlugin(new StructurePlugin()).withPlugin(new MLPlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            Task processFiles = newTask()
                    .inject(0)
                    .asGlobalVar("count")
                    .inject(System.currentTimeMillis())
                    .asVar("processStartTime")
                    .inject(dataSetFolder)
                    .subTask(updateIndexes)
                    .fromVar("stationsPaths")
                    .foreach(
                            isolate(processFile)
                            //.ifThen(context -> ((int)context.variable("i").get(0)) %250 == 0, save())
                    ).then(context -> {
                        long start = (long) context.variable("processStartTime").get(0);
                        long duration = System.currentTimeMillis() - start;
                        System.out.println("All process finished. Duration: " + duration / (60 * 1000) + "m" + (int) ((duration % (60 * 1000)) / 1000) + "s   " + context.variable("count").get(0));
                        context.continueTask();
                    });

            TaskContext context = processFiles.prepareWith(g, null, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {

                }
            });
            context.setVariable("processTime", System.currentTimeMillis());
            processFiles.executeUsing(context);

        });
    }

    private Task updateIndexes = newTask()
            .action(READFILES, "{{result}}")
            .foreach(
                    isolate(
                            asVar("folderPath")
                                    .map(obj -> {
                                        String fileName = (String) obj;
                                        return JCDecauxHistoryOrganizer.cleanString(fileName.substring(fileName.lastIndexOf("/") + 1));
                                    })
                                    .asVar("cityName")
                                    .fromIndex("cities", "name={{cityName}}")
                                    .ifThen((context -> context.result().size() == 0),
                                            setTime("" + Constants.BEGINNING_OF_TIME)
                                                    .newNode()
                                                    .setProperty("name", Type.STRING, "{{cityName}}")
                                                    .asVar(CITY_NODE)
                                                    .indexNodeAt("0", "" + Constants.BEGINNING_OF_TIME, "cities", "name")
                                                    //.setTime("" + Constants.BEGINNING_OF_TIME)
                                                    .newTypedNode(KDTree.NAME)
                                                    .setProperty(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE + "")
                                                    .setProperty(KDTree.FROM, Type.STRING, "position.lat,position.lng")
                                                    .asVar(POSITIONS_TREE)
                                                    .fromVar(CITY_NODE)
                                                    //.setTime("{{processTime}}")
                                                    .add("positions", POSITIONS_TREE)
                                                    .println("City Created: {{cityName}}")
                                    )
                                    .asVar(CITY_NODE)
                                    .action(READFILES, "{{folderPath}}")
                                    .foreach(
                                            isolate(
                                                    asVar("stationFilePath")
                                                            .addToGlobalVar("stationsPaths")
                                                            .map(obj -> {
                                                                String fileName = (String) obj;
                                                                return JCDecauxHistoryOrganizer.cleanString(fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")));
                                                            }).asVar("stationName")
                                                            .fromVar(CITY_NODE)
                                                            .traverseIndex("stations", "name", "{{stationName}}")
                                                            .ifThen((context -> context.result().size() == 0),
                                                                    then(context -> createStationNode(context))
                                                                            .println("Station Created: {{stationName}}")
                                                                            .asVar(STATION_NODE)
                                                                            .fromVar(CITY_NODE)
                                                                            .localIndex("stations", "name", STATION_NODE)
                                                                            .fromVar(STATION_NODE)
                                                            )
                                            )
                                    )
                    )
            );
    //.save();


    final Task processFile =
            print("Processing File: {{result}}\n")
                    .asVar("stationFilePath")
                    .then(context -> {
                        String[] pathElements = ((String) context.result().get(0)).split(File.separator);
                        final String cityName = pathElements[pathElements.length - 2];
                        final String fileName = pathElements[pathElements.length - 1];
                        final String stationName = fileName.substring(0, fileName.lastIndexOf("."));
                        context.setVariable("cityName", JCDecauxHistoryOrganizer.cleanString(cityName));
                        context.setVariable("stationName", JCDecauxHistoryOrganizer.cleanString(stationName));
                        context.continueTask();
                    })
                    .println("Looking for city: {{cityName}}")
                    .fromIndex("cities", "name={{cityName}}")
                    .asVar(CITY_NODE)
                    .println("Looking for station: {{stationName}}")
                    .traverseIndex("stations", "name", "{{stationName}}")
                    .asVar(STATION_NODE)
                    .fromVar("stationFilePath")
                    .action(LOADJSON, "{{result}}")
                    .asVar("jsonFileContent")
                    .setTime("{{processTime}}")
                    .foreach(
                            asVar("jsonObject")
                                    .math("count+1")
                                    .asGlobalVar("count")
                                    .fromVar("jsonObject")
                                    .map(jsonValue -> ((JsonObject) jsonValue).get("last_update").asLong())
                                    .asVar("lastUpdateTime")
                                    .fromVar(STATION_NODE)
                                    //.println("{{result}}")
                                    .jump("{{lastUpdateTime}}")
                                    .asVar(STATION_NODE)
                                    .fromVar(CITY_NODE)
                                    .traverse("positions")
                                    .asVar(POSITIONS_TREE)
                                    //.println("{{result}}")
                                    .fromVar(STATION_NODE)
                                    .traverse(AVAILABLE_BIKES)
                                    .asVar(AVAILABLE_BIKES)
                                    //.println("{{result}}")
                                    .fromVar(STATION_NODE)
                                    .traverse(AVAILABLE_STANDS)
                                    .asVar(AVAILABLE_STANDS)
                                    //.println("{{result}}")
                                    .fromVar(STATION_NODE)
                                    .traverse(STATION_PROFILE)
                                    .asVar(STATION_PROFILE_NODE)
                                    //.println("{{result}}")
                                    .fromVar(STATION_NODE)
                                    .traverse(POSITION)
                                    .asVar(POSITION_NODE)
                                    //.println("{{result}}")
                                    .fromVar(STATION_NODE)
                                    .then(context -> updateStationValues(context))
                                    /*
                                    .action(NTreeInsertTo.NAME, POSITIONS_TREE)
                                        */
                    ).save();


    public void createStationNode(TaskContext context) {

        Graph g = context.graph();

        Node n = g.newNode(context.world(), Constants.BEGINNING_OF_TIME);
        String stationName = (String) context.variable("stationName").get(0);
        n.set("name", stationName);

        Node positionNode = g.newNode(context.world(), Constants.BEGINNING_OF_TIME);
        n.add(POSITION, positionNode);

        Node stationProfile = g.newTypedNode(context.world(), Constants.BEGINNING_OF_TIME, GaussianSlotNode.NAME);
        stationProfile.setProperty(GaussianSlotNode.SLOTS_NUMBER, Type.INT, 7 * 24);
        stationProfile.setProperty(GaussianSlotNode.PERIOD_SIZE, Type.LONG, 7 * 24 * 3600 * 1000);
        n.add(STATION_PROFILE, stationProfile);

        Node bikes = g.newNode(context.world(), Constants.BEGINNING_OF_TIME);
        n.add(AVAILABLE_BIKES, bikes);

        Node stands = g.newNode(context.world(), Constants.BEGINNING_OF_TIME);
        n.add(AVAILABLE_STANDS, stands);

        TaskResult<Node> res = context.newResult();
        res.add(n);
        context.continueWith(res);
    }

    private void updateStationValues(TaskContext context) {

        Node stationNode = (Node) context.variable(STATION_NODE).get(0);

        Node positionNode = (Node) context.variable(POSITION_NODE).get(0);

        GaussianSlotNode stationProfileNode = (GaussianSlotNode) context.variable(STATION_PROFILE_NODE).get(0);

        KDTree positionsTree = (KDTree) context.variable(POSITIONS_TREE).get(0);

        if(stationProfileNode == null) {
            System.out.println("");
        }


        JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);
        double[] profileValues = new double[2];
        profileValues[0] = obj.get(AVAILABLE_BIKES).asDouble();
        profileValues[1] = obj.get(AVAILABLE_STANDS).asDouble();
        stationProfileNode.learnArray(profileValues);

        boolean index = false;
        Iterator<JsonObject.Member> mIt = obj.iterator();
        while (mIt.hasNext()) {
            JsonObject.Member m = mIt.next();
            JsonValue value = m.getValue();
            String name = m.getName();
            if (name.equals(POSITION)) {

                JsonObject posJson = obj.get(POSITION).asObject();
                if(positionNode.get("lat") == null) {
                    index = true;
                } else if (posJson.get("lat").asDouble() != (double) positionNode.get("lat")
                        || posJson.get("lng").asDouble() != (double) positionNode.get("lng")) {
                    index = true;
                }
                Iterator<JsonObject.Member> mIt2 = posJson.iterator();
                while (mIt2.hasNext()) {
                    JsonObject.Member p = mIt2.next();
                    positionNode.set(p.getName(), p.getValue().asDouble());
                }
                //System.out.println(positionNode.toString());
            } else if (name.equals(AVAILABLE_BIKES)) {
                ((Node) context.variable(AVAILABLE_BIKES).get(0)).setProperty("value", Type.INT, value.asInt());
            } else if (name.equals(AVAILABLE_STANDS)) {
                ((Node) context.variable(AVAILABLE_STANDS).get(0)).setProperty("value", Type.INT, value.asInt());
            } else {
                if (value.isString()) {
                    stationNode.set(m.getName(), value.asString());
                } else if (value.isBoolean()) {
                    stationNode.set(m.getName(), value.asBoolean());
                } else if (value.isNumber()) {
                    try {
                        stationNode.set(m.getName(), value.asInt());
                    } catch (Exception e) {
                        try {
                            stationNode.set(m.getName(), value.asLong());
                        } catch (Exception e2) {
                            try {
                                stationNode.set(m.getName(), value.asFloat());
                            } catch (Exception e3) {
                                try {
                                    stationNode.set(m.getName(), value.asDouble());
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                    context.continueTask();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (index) {
            positionsTree.insert(stationNode, result -> context.continueTask());
        } else {
            context.continueTask();
        }
    }

    public static final String POSITION = "position";
    public static final String AVAILABLE_BIKES = "available_bikes";
    public static final String AVAILABLE_STANDS = "available_bike_stands";
    public static final String STATION_PROFILE = "station_profile";

    public static final String CITY_NODE = "cityNode";
    public static final String STATION_NODE = "stationNode";
    public static final String POSITIONS_TREE = "positionsTree";
    public static final String POSITION_NODE = "positionNode";
    public static final String STATION_PROFILE_NODE = "station_profileNode";
    public static final String AVAILABLE_BIKES_VALUE = "available_bike_stands_val";
    public static final String AVAILABLE_STANDS_VALUE = "available_bikes_val";

    public static void main(String[] args) {
        try {
            Path rootPath = Paths.get("fullTempStorage2");
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JCDecauxHistoryLoad test = new JCDecauxHistoryLoad();
        test.baseTest();
    }


}
