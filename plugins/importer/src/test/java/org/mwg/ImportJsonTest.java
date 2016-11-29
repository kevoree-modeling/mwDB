package org.mwg;

import org.junit.Test;
import org.mwg.importer.ImporterActions;
import org.mwg.importer.ImporterPlugin;

import static org.mwg.core.task.Actions.defineAsVar;
import static org.mwg.core.task.Actions.print;
import static org.mwg.core.task.Actions.task;
import static org.mwg.importer.ImporterActions.readJson;

public class ImportJsonTest {

    @Test
    public void testReadJson() {
        final Graph g = new GraphBuilder()
                // .withPlugin(new VerbosePlugin())
                .withPlugin(new ImporterPlugin())
                .build();
        g.connect(result -> {

            task().then(readJson("sample.geojson"))
                    .forEach(
                            task()
                                    .then(ImporterActions.jsonMatch("features",
                                            task().forEach(
                                                    task()
                                                            .then(defineAsVar("jsonObj"))
                                                            .then(print("{{result}}"))
                                            )
                                    ))
                    ).execute(g, null);

            g.disconnect(null);
        });
    }


}
