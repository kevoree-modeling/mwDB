package org.mwg;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.importer.ImporterPlugin;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.*;

import java.util.Iterator;

import static org.mwg.importer.ImporterActions.READFILES;
import static org.mwg.plugin.geojson.GeoJsonActions.loadJson;
import static org.mwg.task.Actions.*;

public class JCDecauxHistoryLoad {

    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/dataset";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/subset";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedSubset/Luxembourg_BRICHERHAFF.json";
    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedDataset";
    public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedSubset";

    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withStorage(new LevelDBStorage("tempStorage")).withMemorySize(200000).withPlugin(new ImporterPlugin()).withPlugin(new GeoJsonPlugin()).withPlugin(new StructurePlugin()).withPlugin(new MLPlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            Task processFiles = newTask()
                    .inject(System.currentTimeMillis())
                    .asVar("processStartTime")
                    .action(READFILES, dataSetFolder)
                    .asVar("citiesFolders")
                    .foreachPar(
                            isolate(
                                    asVar("folderPath")
                                            .inject(System.currentTimeMillis())
                                            .asVar("cityProcessStartTime")
                                            .fromVar("folderPath")
                                            //.println("FolderPath::{{folderPath}}")
                                            .map(obj -> {
                                                String fileName = (String) obj;
                                                return fileName.substring(fileName.lastIndexOf("/") + 1);
                                            })
                                            .asVar("cityName")
                                            //.println("{{cityName}}")
                                            .fromIndex("cities", "name={{cityName}}")
                                            .ifThen((context -> context.result().size() == 0),
                                                    newNode()
                                                            .setProperty("name", Type.STRING, "{{cityName}}")
                                                            .asVar(CITY_NODE)
                                                            .indexNodeAt("0", "0", "cities", "name")
                                                            .setTime("" + Constants.BEGINNING_OF_TIME)
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
                                            //.print("City Node: {{result}}")
                                            .traverse("positions")
                                            .asVar(POSITIONS_TREE)
                                            //.println("FolderPath2::{{folderPath}}")
                                            .action(READFILES, "{{folderPath}}")
                                            //.println("{{result}}")
                                            .foreachPar(
                                                    isolate(
                                                            asVar("stationFilePath")
                                                                    .inject(System.currentTimeMillis())
                                                                    .asVar("stationProcessStartTime")
                                                                    .fromVar("stationFilePath")
                                                                    .map(obj -> {
                                                                        String fileName = (String) obj;
                                                                        return fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
                                                                    }).asVar("stationName")
                                                                    //.println("{{stationName}}")
                                                                    .fromVar(CITY_NODE)
                                                                    .traverseIndex("stations", "name", "{{stationName}}")
                                                                    .ifThen((context -> context.result().size() == 0),
                                                                            then(context -> createStationNode(context))
                                                                                    .println("Station Created: {{stationName}}")
                                                                                    .asVar(STATION_NODE)
                                                                                    .fromVar(CITY_NODE)
                                                                                    .localIndex("stations", "name", STATION_NODE)
                                                                                    .fromVar(STATION_NODE)
                                                                    ).asVar(STATION_NODE)
                                                                    .fromVar("stationFilePath")
                                                                    .println("Processing file {{i}}:{{result}}")
                                                                    .subTask(processFile)
                                                                    .save()
                                                                    .clear()
                                                                    .then(context -> {
                                                                        long start = (long) context.variable("stationProcessStartTime").get(0);
                                                                        long duration = System.currentTimeMillis() - start;
                                                                        System.out.println("Station "+context.variable("stationName")+" process finished. Duration: " + duration / (60 * 1000) + "m" + (int) ((duration % (60 * 1000)) / 1000) + "s");
                                                                        context.continueTask();
                                                                    })
                                                    )
                                            ).then(context -> {
                                        long start = (long) context.variable("cityProcessStartTime").get(0);
                                        long duration = System.currentTimeMillis() - start;
                                        System.out.println("City "+context.variable("cityName")+" process finished. Duration: " + duration / (60 * 1000) + "m" + (int) ((duration % (60 * 1000)) / 1000) + "s");
                                        context.continueTask();
                                    })
                            )
                    ).then(context -> {
                        long start = (long) context.variable("processStartTime").get(0);
                        long duration = System.currentTimeMillis() - start;
                        System.out.println("All process finished. Duration: " + duration / (60 * 1000) + "m" + (int) ((duration % (60 * 1000)) / 1000) + "s");
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

    final Task processFile =
            loadJson("{{result}}")
                    .setTime("{{processTime}}")
                    .asVar("jsonFileContent")
                    .foreach(
                            asVar("jsonObject")
                                    .map(jsonValue -> ((JsonObject) jsonValue).get("last_update").asLong())
                                    .asVar("lastUpdateTime")
                                    .fromVar(STATION_NODE)
                                    //.println("{{result}}")
                                    .jump("{{lastUpdateTime}}")
                                    .asVar(STATION_NODE)
                                    .traverse(POSITION)
                                    .asVar(POSITION_NODE)
                                    .fromVar(STATION_NODE)
                                    .traverse(STATION_PROFILE)
                                    .asVar(STATION_PROFILE_NODE)
                                    .fromVar(STATION_NODE)
                                    .then(context -> updateStationValues(context))
                                    .action(NTreeInsertTo.NAME, POSITIONS_TREE)
                                    .traverse(AVAILABLE_BIKES)
                                    //.println("{{result}}")
                                    .setProperty("value", Type.INT, "{{" + AVAILABLE_BIKES_VALUE + "}}")
                                    .fromVar(STATION_NODE)
                                    .traverse(AVAILABLE_STANDS)
                                    //.println("{{result}}")
                                    .setProperty("value", Type.INT, "{{" + AVAILABLE_STANDS_VALUE + "}}")
                                    .save()
                                    .clear()
                    ).clear();


    public void createStationNode(TaskContext context) {

        Graph g = context.graph();

        //JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);
        //context.setTime(obj.getLong("last_update", context.time()));

        Node n = g.newNode(context.world(), Constants.BEGINNING_OF_TIME);
        n.set("name", context.variable("stationName").get(0));

        n.add(POSITION, g.newNode(context.world(), context.time()));

        Node stationProfile = g.newTypedNode(context.world(), context.time(), GaussianSlotNode.NAME);
        stationProfile.setProperty(GaussianSlotNode.SLOTS_NUMBER, Type.INT, 7 * 24);
        stationProfile.setProperty(GaussianSlotNode.PERIOD_SIZE, Type.LONG, 7 * 24 * 3600 * 1000);
        n.add(STATION_PROFILE, stationProfile);

        Node bikes = g.newNode(context.world(), context.time());
        n.add(AVAILABLE_BIKES, bikes);

        Node stands = g.newNode(context.world(), context.time());
        n.add(AVAILABLE_STANDS, stands);

        TaskResult<Node> res = context.newResult();
        res.add(n);
        context.continueWith(res);
    }

    private void updateStationValues(TaskContext context) {

        Node stationNode = (Node) context.variable("stationNode").get(0);
        Node positionNode = (Node) context.variable(POSITION_NODE).get(0);

        GaussianSlotNode stationProfileNode = (GaussianSlotNode) context.variable(STATION_PROFILE_NODE).get(0);

        JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);

        double[] profileValues = new double[2];
        profileValues[0] = obj.get(AVAILABLE_BIKES).asDouble();
        profileValues[1] = obj.get(AVAILABLE_STANDS).asDouble();
        stationProfileNode.learnArray(profileValues);

        Iterator<JsonObject.Member> mIt = obj.iterator();
        while (mIt.hasNext()) {
            JsonObject.Member m = mIt.next();
            JsonValue value = m.getValue();
            String name = m.getName();
            if (name.equals(POSITION)) {
                JsonObject posJson = obj.get(POSITION).asObject();
                Iterator<JsonObject.Member> mIt2 = posJson.iterator();
                while (mIt2.hasNext()) {
                    JsonObject.Member p = mIt2.next();
                    positionNode.set(p.getName(), p.getValue().asDouble());
                }
            } else if (name.equals(AVAILABLE_BIKES)) {
                context.setVariable(AVAILABLE_BIKES_VALUE, value.asInt());
            } else if (name.equals(AVAILABLE_STANDS)) {
                context.setVariable(AVAILABLE_STANDS_VALUE, value.asInt());
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
        context.continueTask();

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
        JCDecauxHistoryLoad test = new JCDecauxHistoryLoad();
        test.baseTest();
    }

}
