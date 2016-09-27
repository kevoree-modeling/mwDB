package org.mwg.importer.util;

import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

import java.io.*;

public class IterableLines implements TaskResult<String> {

    private final String _path;

    public IterableLines(String p_path) {
        this._path = p_path;
    }

    @Override
    public TaskResultIterator iterator() {
        BufferedReader _buffer = null;
        try {
            Reader reader;
            File file = new File(_path);
            if (file.exists()) {
                reader = new FileReader(file);
            } else {
                reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(_path));
            }
            _buffer = new BufferedReader(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final BufferedReader final_buffer = _buffer;
        return new TaskResultIterator() {

            private int _i = 0;

            @Override
            public synchronized Object next() {
                _i++;
                try {
                    String line = final_buffer.readLine();
                    if (line == null) {
                        final_buffer.close();
                    }
                    return line;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public synchronized Tuple nextWithIndex() {
                final int c_i = _i;
                _i++;
                try {
                    String line = final_buffer.readLine();
                    if (line == null) {
                        final_buffer.close();
                        return null;
                    } else {
                        return new Tuple(c_i, line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public void set(int index, String input) {

    }

    @Override
    public void allocate(int index) {

    }

    @Override
    public void add(String input) {

    }

    @Override
    public void clear() {
        //noop
    }

    @Override
    public TaskResult<String> clone() {
        return null;
    }

    @Override
    public void free() {

    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public Object[] asArray() {
        return new Object[0];
    }

}
