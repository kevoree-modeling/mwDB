package org.mwg;

import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;

import static org.mwg.core.task.Actions.defineAsVar;
import static org.mwg.core.task.Actions.print;
import static org.mwg.core.task.Actions.newTask;
import static org.mwg.importer.ImporterActions.readJson;

public class ImportJsonTest {

    @Test
    public void testReadJson() {
        final Graph g = new GraphBuilder()
                // .withPlugin(new VerbosePlugin())
                .withPlugin(new ImporterPlugin())
                .build();
        g.connect(result -> {

            newTask().then(readJson("sample.geojson"))
                    .forEach(
                            newTask()
                                    .then(ImporterActions.jsonMatch("features",
                                            newTask().forEach(
                                                    newTask()
                                                            .then(defineAsVar("jsonObj"))
                                                            .then(print("{{result}}"))
                                            )
                                    ))
                    ).execute(g, null);

            g.disconnect(null);
        });
    }


}
