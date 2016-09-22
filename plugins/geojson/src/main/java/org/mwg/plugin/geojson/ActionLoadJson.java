package org.mwg.plugin.geojson;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by gnain on 01/09/16.
 */
public class ActionLoadJson extends AbstractTaskAction {

    private final String _pathOrTemplate;

    ActionLoadJson(String _pathOrTemplate) {
        this._pathOrTemplate = _pathOrTemplate;
    }

    @Override
    public void eval(TaskContext context) {
        JsonResult result = null;
        final String path = context.template(_pathOrTemplate);
        try {
            URI uri = URI.create((path.contains("://")?path:"file://" + path));
           // System.out.println(uri.toString());
            try (InputStreamReader isr = new InputStreamReader(uri.toURL().openStream())) {
                JsonValue firstElem = Json.parse(isr);
                if (firstElem.isArray()) {
                    JsonArray array = firstElem.asArray();
                    JsonValue[] values = new JsonValue[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        values[i] = array.get(i);
                    }
                    //result = new JsonResult(Arrays.copyOf(values, 500));
                    result = new JsonResult(values);
                } else {
                    result = new JsonResult(new JsonValue[]{firstElem});
                }
                isr.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = new JsonResult(new JsonValue[]{});
            }
            context.continueWith(result);
        }
    }
}
