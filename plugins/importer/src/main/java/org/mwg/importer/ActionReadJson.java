package org.mwg.importer;

import com.eclipsesource.json.Json;
import org.mwg.importer.util.JsonValueResultBuilder;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

class ActionReadJson implements Action {

    private final String _pathOrTemplate;

    ActionReadJson(String _pathOrTemplate) {
        this._pathOrTemplate = _pathOrTemplate;
    }

    @Override
    public void eval(TaskContext context) {
        TaskResult result = null;
        final String path = context.template(_pathOrTemplate);
        InputStream foundStream;
        try {
            File file = new File(path);
            if (file.exists()) {
                foundStream = new FileInputStream(file);
            } else {
                foundStream = this.getClass().getClassLoader().getResourceAsStream(path);
            }
            if (foundStream.available() <= 0) {
                foundStream = null;
            }
        } catch (Exception e) {
            foundStream = null;
        }
        if (foundStream == null) {
            try {
                URI uri = URI.create(path);
                foundStream = uri.toURL().openStream();
            } catch (Exception e) {
                foundStream = null;
            }
        }
        if (foundStream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(foundStream);
                result = JsonValueResultBuilder.build(Json.parse(reader));
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    foundStream.close();
                } catch (Exception e) {

                }
            }
        }
        context.continueWith(result);
    }

}
