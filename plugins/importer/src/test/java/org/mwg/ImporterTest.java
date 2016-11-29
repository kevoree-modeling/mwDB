package org.mwg;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;
import org.mwg.task.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.mwg.core.task.Actions.*;
import static org.mwg.importer.ImporterActions.readFiles;
import static org.mwg.importer.ImporterActions.readLines;

public class ImporterTest {


    @Test
    public void testReadLines() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy|HH:mm");
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(connectionResult -> {

            Node newNode = g.newNode(0, 0);
            //final Task t = readLines("/Users/duke/dev/mwDB/plugins/importer/src/test/resources/smarthome/smarthome_1.T15.txt")
            final Task t = task().then(readLines("smarthome/smarthome_mini_1.T15.txt"))
                    .forEach(
                            task().ifThen(ctx -> !ctx.result().get(0).toString().startsWith("1:Date"),
                                    task().thenDo(context -> {
                                        String[] line = context.result().get(0).toString().split(" ");
                                        try {
                                            long time = dateFormat.parse(line[0] + "|" + line[1]).getTime();
                                            double value = Double.parseDouble(line[2]);
                                            newNode.travelInTime(time, timedNode -> {
                                                timedNode.set("value", Type.DOUBLE, value);
                                                context.continueWith(context.wrap(timedNode));
                                            });
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            context.continueWith(null);
                                        }
                                    })
                            ));
            t.execute(g, null);
        });
    }

    @Test
    public void testReadFilesStaticMethod() {
        File fileChecked = new File(this.getClass().getClassLoader().getResource("smarthome").getPath());
        final File[] subFiles = fileChecked.listFiles();

        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                final int[] nbFile = new int[1];
                Task t = task().then(readFiles("smarthome")).forEach(task().thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        String filePath = (String) context.result().get(0);
                        Assert.assertEquals(subFiles[nbFile[0]].getAbsolutePath(), filePath);
                        nbFile[0]++;
                        context.continueWith(null);
                    }
                }));
                t.execute(g, null);
                Assert.assertEquals(subFiles.length, nbFile[0]);
            }
        });
    }

    @Test
    public void testReadFilesActionWithTemplate() {
        File fileChecked = new File(this.getClass().getClassLoader().getResource("smarthome").getPath());
        final File[] subFiles = fileChecked.listFiles();

        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                final int[] nbFile = new int[1];
                Task t = task()
                        .then(inject("smarthome"))
                        .then(defineAsGlobalVar("fileName"))
                        .then(pluginAction(ImporterActions.READFILES, "{{fileName}}"))
                        .forEach(task().thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                String file = (String) context.result().get(0);
                                Assert.assertEquals(subFiles[nbFile[0]].getAbsolutePath(), file);
                                nbFile[0]++;
                                context.continueWith(null);
                            }
                        }));
                t.execute(g, null);

                Assert.assertEquals(subFiles.length, nbFile[0]);
            }
        });
    }

    @Test
    public void testReadFilesOnFile() throws UnsupportedEncodingException, MalformedURLException, URISyntaxException {
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        URL urlFIle = this.getClass().getClassLoader().getResource("smarthome/readme.md");
        URL urlFIle2 = this.getClass().getClassLoader().getResource(URLDecoder.decode("folder with spaces in name/aFile.txt", "UTF-8"));

        File expectecFile = new File(urlFIle.toURI());
        File expectedFile2 = new File(urlFIle2.toURI());

        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                final int[] nbFile = new int[1];
                Task t = task()
                        .then(readFiles(urlFIle.getPath()))
                        .forEach(
                                task().thenDo(new ActionFunction() {
                                    @Override
                                    public void eval(TaskContext context) {
                                        String file = (String) context.result().get(0);
                                        Assert.assertEquals(expectecFile.getAbsolutePath(), file);
                                        nbFile[0]++;
                                        context.continueWith(null);
                                    }
                                })
                        )
                        .then(pluginAction(ImporterActions.READFILES, urlFIle2.getPath()))
                        .forEach(
                                task().thenDo(new ActionFunction() {
                                    @Override
                                    public void eval(TaskContext context) {
                                        String file = (String) context.result().get(0);
                                        try {
                                            Assert.assertEquals(URLDecoder.decode(expectedFile2.getAbsolutePath(), "UTF-8"), file);
                                        } catch (UnsupportedEncodingException ex) {
                                            Assert.fail(ex.getMessage());
                                        }
                                        nbFile[0]++;
                                        context.continueWith(null);
                                    }
                                })
                        );
                t.execute(g, null);

                Assert.assertEquals(2, nbFile[0]);
            }
        });
    }

    @Test
    public void testReadFileOnUnknowFile() {
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(connectionResult -> {
            Task t = task().then(readFiles("nonexistent-file.txt"));
            boolean exceptionCaught = false;
            try {
                t.execute(g, null);
            } catch (RuntimeException exception) {
                exceptionCaught = true;
            }

            Assert.assertTrue(exceptionCaught);
        });
    }


    @Test
    public void testReadFileOnIncorrectVar() {
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                Task t = task().then(pluginAction(ImporterActions.READFILES, "{{incorrectVarName}}"));

                boolean exceptionCaught = false;
                try {
                    t.execute(g, null);
                } catch (RuntimeException ex) {
                    exceptionCaught = true;
                }
                Assert.assertTrue(exceptionCaught);

            }
        });
    }


    /*
    @Test
    public void testV2() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy|HH:mm");
        final Graph g = new GraphBuilder().withPlugin(new ImporterPlugin()).build();
        g.connect(connectionResult -> {
            Node newNode = g.newNode(0, 0);
            final Task t = task().then(readLines("smarthome/smarthome_mini_1.T15.txt"))
                    .forEach(
                            task().ifThen(ctx -> !ctx.result().get(0).toString().startsWith("1:Date"),
                                    split(" ")
                                            .then(context -> {
                                                TaskResult<String> line = context.result();
                                                try {
                                                    context.setGlobalVariable("time", context.wrap(dateFormat.parse(line.get(0) + "|" + line.get(1)).getTime()));
                                                    context.setGlobalVariable("value", context.wrap(Double.parseDouble(line.get(2))));
                                                    context.continueWith(null);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    context.continueWith(null);
                                                }
                                            })
                                            .setTime("{{time}}")
                                            .lookup("" + newNode.id())
                                            .setProperty("value", Type.DOUBLE, "{{value}}")
                                    //.print("insertedNode: {{result}} {{value}}")
                            ));
            t.execute(g, null);
            //t.executeWith(g, null, null, true, null); //with debug
        });
    }*/

}
