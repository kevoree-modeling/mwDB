package lu.snt.pixel;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode;
import org.mwg.*;
import org.mwg.core.NoopScheduler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by assaad on 20/05/16.
 */
public class SmartgridTest {
    final static int SLOTS = 12;
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
    //final static String csvdir = "/Users/duke/Desktop/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = GraphBuilder.builder()
                .withFactory(new GaussianSlotProfilingNode.Factory())
                .withScheduler(new NoopScheduler())
                .withOffHeapMemory()
                .withMemorySize(10_000_000)
                .withAutoSave(1000)
                //.withStorage(new RocksDBStorage(csvdir + "rocksdb/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {

                System.out.println("Available space initially: " + graph.space().available());

                final int[] globaltotal = {0};
                long starttime = System.nanoTime();
                try {

                    final String[] line = {""};
                    int nuser = 0;
                    String cvsSplitBy = ",";
                    final int[] powerValue = new int[1];
                    String username;
                    final String[][] splitted = new String[1][1];
                    final long[] timestamp = new long[1];

                    long minTraining = Long.MAX_VALUE;
                    long maxTraining = Long.MIN_VALUE;

                    final long[] accumulator = new long[1];
                    long halfHour = 1800 * 1000;
                    int MAXCONC = 50;
                    int MAXUSER = 5000;

                    //Two main concentrator nodes
                    Node[] concentrator = new Node[MAXCONC];
                    Node[] profiles = new Node[MAXUSER];
                    Node[] users = new Node[MAXUSER];
                    int iconc = 0;
                    int connections = 0;


                    //Loading the training set
                    File dir = new File(csvdir + "trainingsim/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            username = file.getName().split("\\.")[0];
                            Node smartmeter = graph.newNode(0, 0);
                            final Node profiler = graph.newTypedNode(0, 0, GaussianSlotProfilingNode.NAME);
                            profiler.set(GaussianSlotProfilingNode.SLOTS_NUMBER, SLOTS); //one slot every hour
                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                            graph.index("nodes", smartmeter, "name", null);

                            //create the concentrator if null
                            if (concentrator[iconc] == null) {
                                concentrator[iconc] = graph.newNode(0, 0);
                            }

                            //add the current smart meter
                            concentrator[iconc].add("smartmeters", smartmeter);
                            connections++;

                            //if connections are full, increase concentrators
                            if (connections == 100) {
                                iconc++;
                                connections = 0;
                            }

                            //Fill electrical loads of one meter
                            while ((line[0] = br.readLine()) != null) {
                                try {

                                    splitted[0] = line[0].split(cvsSplitBy);
                                    if (splitted[0].length != 2) {
                                        continue;
                                    }
                                    timestamp[0] = Long.parseLong(splitted[0][0]);
                                    powerValue[0] = Integer.parseInt(splitted[0][1]);
                                    if (timestamp[0] < minTraining) {
                                        minTraining = timestamp[0];
                                    }
                                    if (timestamp[0] > maxTraining) {
                                        maxTraining = timestamp[0];
                                    }
                                    final int pv = powerValue[0];
                                    smartmeter.jump(timestamp[0], new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {
                                            result.set("power", pv);
                                            result.rel("profile", (profilers) -> {
                                                long s = System.nanoTime();
                                                ((GaussianSlotProfilingNode) profilers[0]).learnArray(new double[]{pv});
                                                long t = System.nanoTime();
                                                accumulator[0] += (t - s);
                                                profilers[0].free();
                                            });

                                            result.free();
                                        }
                                    });
                                    globaltotal[0]++;
                                    if (globaltotal[0] % 1000000 == 0) {
                                        long endtime = System.nanoTime();
                                        double restime = (globaltotal[0]) / ((endtime - starttime) / 1000000.0);
                                        System.out.println("Loaded " + globaltotal[0] / 1000000.0 + " m power records in " + restime + " kv/s users " + nuser + " space: " + graph.space().available());
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            profiles[nuser] = profiler;
                            users[nuser] = smartmeter;
                            nuser++;
//                            if (nuser % 1000 == 0) {
//                                System.out.println(nuser + " " + globaltotal);
//                            }
                            br.close();
                            //  System.out.println("File " + file.getName() + " parsed successfully");
                        }
                    }

                    final long[] endtime = {System.nanoTime()};
                    final double[] restime = {(endtime[0] - starttime) / 1000000000};
                    System.out.println("Loaded " + globaltotal[0] + " power records in " + restime[0] + " s !");
                    System.out.println("Profiling took: " + accumulator[0] + " ns");

                   /* for(int i=0;i<50;i++){
                        System.out.println(((long[])concentrator[i].get("smartmeters")).length);
                    }*/


                    int MAXWORLD = 10000000;
                    long calctime = 0;
                    long calctimeStart = 0;
                    long forktime = 0;
                    long forktimeStart = 0;
                    long calcCumul = 0;
                    long forkCumul = 0;

                    long worldList = 0;


                    HashMap<Long, Double> powers = new HashMap<Long, Double>(5000);

                    long[] countnull = new long[2];
                    long profileTime = System.nanoTime();
                    int totProf = 0;
                    for (int i = 0; i < MAXUSER; i++) {
                        double[] pp = new double[1];
                        for (long time = maxTraining; time < maxTraining + 48 * halfHour; time += halfHour) {
                            totProf++;
                            graph.lookup(0, time, users[i].id(), new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    result.rel("profile", new Callback<Node[]>() {
                                        @Override
                                        public void on(Node[] result) {
                                            ((GaussianSlotProfilingNode) result[0]).predict(new Callback<double[]>() {
                                                @Override
                                                public void on(double[] result) {
                                                    if (result == null) {
                                                        countnull[0]++;
                                                    } else {
                                                        countnull[1]++;
                                                        pp[0] += result[0];
                                                    }
                                                }
                                            });
                                            result[0].free();
                                        }
                                    });
                                    result.free();
                                }
                            });
                        }
                        pp[0] = pp[0] / 48;
                        powers.put(users[i].id(), pp[0]);
                    }
                    profileTime = System.nanoTime() - profileTime;
                    System.out.println("Profile exec time per user per time, in ns: " + profileTime + " total calls: " + totProf + " should divide ");
                    System.out.println("profile null " + countnull[0] + " non null " + countnull[1]);

                    double absolute = 0;
                    for (Long l : powers.keySet()) {
                        absolute += powers.get(l);
                    }
                    System.out.println("Absolute total power " + absolute);


                    PrintWriter outPowers = new PrintWriter(new File(csvdir + "worlds.csv"));
                    PrintWriter outGaussian = new PrintWriter(new File(csvdir + "gaussian.csv"));
                    PrintWriter outTime = new PrintWriter(new File(csvdir + "worldtime.csv"));
                    PrintWriter outPerm = new PrintWriter(new File(csvdir + "permutations.csv"));

                    outTime.println("world,calctime,forktime,calcCumul,forkCumul,min");

                    outGaussian.println(absolute);
                    outGaussian.println("Profile exec time per user per time, in ns: " + profileTime + " total calls: " + totProf + " should divide ");

                    double min = Double.MAX_VALUE;
                    int bestWorld = 0;

                    System.out.println("Available space after loading: " + graph.space().available());
                    final Random rand = new Random();

                    for (int world = 0; world < MAXWORLD; world++) {
                        GaussianProfile worldProfiles = new GaussianProfile();
                        int finalWorld = world;
                        final double[] totalPowers = new double[MAXCONC];


                        calctimeStart = System.nanoTime();
                        for (int conc = 0; conc < MAXCONC; conc++) {
                            int finalConc = conc;
                            graph.lookup(worldList, 0, concentrator[conc].id(), new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    long[] relations = (long[]) result.get("smartmeters");
                                    for (int k = 0; k < relations.length; k++) {
                                        outPerm.print(relations[k] + ",");
                                        totalPowers[finalConc] += powers.get(relations[k]);
                                    }
                                    result.free();
                                }
                            });

                        }
                        calctime = System.nanoTime() - calctimeStart;
                        calcCumul += calctime;

                        //System.out.println("Available space after calculation: "+graph.space().available());


                        for (int conc = 0; conc < MAXCONC; conc++) {
                            worldProfiles.learn(new double[]{totalPowers[conc]});
                            outPowers.print(totalPowers[conc] + ",");
                        }
                        outPowers.println();
                        outGaussian.println(worldProfiles.getMin()[0] + "," + worldProfiles.getMax()[0] + "," + worldProfiles.getAvg()[0] + "," + worldProfiles.getSum()[0] + "," + worldProfiles.getSumSquares()[0] + ",");

                        if (Math.abs(worldProfiles.getSum()[0] - absolute) > 0.1) {
                            System.out.println("ERROR: expected: " + absolute + " got: " + worldProfiles.getSum()[0]);
                        }

                        outPerm.print(worldProfiles.getSum()[0]);
                        outPerm.println();
                        outPerm.flush();

                        double vv = worldProfiles.getSumSquares()[0];
                        if (vv < min) {
                            min = vv;
                            bestWorld = world;
                        }


                        forktimeStart = System.nanoTime();
                        worldList = graph.diverge(worldList);
                        for (int conc = 0; conc < MAXCONC; conc++) {

                            int xx = 0;
                            do {
                                xx = rand.nextInt(MAXCONC);
                            }
                            while (xx == conc);

                            long finalWorldList = worldList;
                            int finalXx = xx;
                            graph.lookup(worldList, 0, concentrator[conc].id(), new Callback<Node>() {
                                @Override
                                public void on(Node world1) {
                                    graph.lookup(finalWorldList, 0, concentrator[finalXx].id(), new Callback<Node>() {
                                        @Override
                                        public void on(Node world2) {
                                            long[] p1 = (long[]) world1.get("smartmeters");
                                            long[] p2 = (long[]) world2.get("smartmeters");

                                            // int situation = xx.nextInt(3);
                                            // if (situation <= 1) {
                                            for (int i = 0; i < 3; i++) {
                                                int y = rand.nextInt(p1.length);
                                                int z = rand.nextInt(p2.length);
                                                long temp = p1[y];
                                                p1[y] = p2[z];
                                                p2[z] = temp;
                                            }
                                           /* } else if (p1.length > 1) {
                                                int y = xx.nextInt(p1.length);
                                                long[] p1t = new long[p1.length - 1];
                                                long[] p2t = new long[p2.length + 1];
                                                for (int i = 0; i < p1t.length; i++) {
                                                    if (i < y) {
                                                        p1t[i] = p1[i];
                                                    } else {
                                                        p1t[i] = p1[i + 1];
                                                    }
                                                }
                                                for (int i = 0; i < p2.length; i++) {
                                                    p2t[i] = p2[i];
                                                }
                                                p2t[p2.length] = p1[y];

                                                p1 = p1t;
                                                p2 = p2t;
                                            }*/

                                            world1.set("smartmeters", p1);
                                            world2.set("smartmeters", p2);
                                            world2.free();
                                        }
                                    });
                                    world1.free();
                                }
                            });

                            // System.out.println("Available space after permutation: "+graph.space().available());

                        }
                        forktime = System.nanoTime() - forktimeStart;
                        forkCumul += forktime;
                        outTime.println(world + "," + calctime + "," + forktime + "," + calcCumul + "," + forkCumul + "," + min);

                        outGaussian.flush();
                        outPowers.flush();
                        outTime.flush();
                        if (world % 1000 == 0) {
                            System.out.println("World " + world + " totalCalcTime: " + calcCumul + " totalForkTime: " + forkCumul + " Best world is " + bestWorld + " min sumsq " + min + " space: " + graph.space().available());
                        }

                    }
                    System.out.println("World " + MAXWORLD + " totalCalcTime: " + calctime + " totalForkTime: " + forktime);


                    System.out.println("Best world is " + bestWorld + " min sumsq " + min);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        System.out.println("test done");
    }
}
