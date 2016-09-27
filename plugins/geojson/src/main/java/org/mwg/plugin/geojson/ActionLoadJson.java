package org.mwg.plugin.geojson;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;

import java.io.IOException;
import java.io.InputStream;
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
        URI uri = URI.create((path.contains("://") ? path : "file://" + path));
        // System.out.println(uri.toString());

        InputStream is = null;
        InputStreamReader isr = null;
        JsonValue firstElem = null;

        try {
            is = uri.toURL().openStream();
            isr = new InputStreamReader(is);
            firstElem = Json.parse(isr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        if(firstElem != null) {
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
        }

        if (result == null) {
            result = new JsonResult(new JsonValue[]{});
        }
        context.continueWith(result);

    }
}
