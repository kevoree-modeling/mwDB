package org.mwg;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by gnain on 19/09/16.
 */
public class JCDecauxHistoryOrganizer {

    private static final String SRC_DIRECTORY = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/dataset";
    private static final String DEST_DIRECTORY = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedDataset";

    /*
    private static final String SRC_DIRECTORY = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/subset";
    private static final String DEST_DIRECTORY = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/sortedSubset";
    */

    public static void main(String[] args) {

        File recordsDirectory = new File(SRC_DIRECTORY);
        if (recordsDirectory.exists() && recordsDirectory.isDirectory()) {

            ExecutorService executor = Executors.newFixedThreadPool(200);


            HashMap<String, Long> lastRecord = new HashMap<>();


            File destDir = new File(DEST_DIRECTORY);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            for (final File f : recordsDirectory.listFiles()) {
                try {
                    LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(f.getName().substring(0, f.getName().lastIndexOf("."))) * 1000), ZoneId.systemDefault());
                    System.out.println("Sorting: " + f.getName() + " \t" + ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")));
                    JsonArray records = Json.parse(new InputStreamReader(new FileInputStream(f))).asArray();
                    for (int i = 0; i < records.size(); i++) {
                        final JsonObject record = records.get(i).asObject();
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //String cityName = Charset.forName("UTF-8").encode(record.get("contract_name").asString().replace(" ", "_").replace("/", "_")).toString();
                                    //String fileName = Charset.forName("UTF-8").encode(record.get("name").asString().replace(" ", "_").replace("/", "_")).toString();
                                    String cityName = record.get("contract_name").asString().replaceAll(whitespace_charclass,"_").replace("/", "_");
                                    String fileName = record.get("name").asString().replaceAll(whitespace_charclass,"_").replace("/", "_");
                                    String entry = cityName + "_" + fileName;

                                    if (lastRecord.getOrDefault(entry, 0L) < record.get("last_update").asLong()) {
                                        lastRecord.put(entry, record.get("last_update").asLong());

                                        File cityFolder = new File(DEST_DIRECTORY + File.separator + cityName);
                                        if (!cityFolder.exists()) {
                                            cityFolder.mkdirs();
                                        }
                                        File stationFile = new File(DEST_DIRECTORY + File.separator + cityName + File.separator + fileName + ".json");
                                        if (!stationFile.exists()) {
                                            stationFile.createNewFile();
                                        }
                                        try (PrintWriter pr = new PrintWriter(new FileOutputStream(stationFile, true))) {
                                            pr.append(record.toString() + "\n");
                                            pr.flush();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            executor.shutdown();
            try {
                while (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                    System.out.println("Not finished. In queue:");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (final File cities : destDir.listFiles()) {
                for (File f : cities.listFiles()) {
                    try {

                        File tempFile = new File(destDir + File.separator + "tmp_" + f.getName());
                        try (BufferedReader br = new BufferedReader(new FileReader(f)); PrintWriter pr = new PrintWriter(new FileWriter(tempFile))) {
                            pr.append("[");
                            String line = br.readLine();
                            while (line != null) {
                                pr.print(line);
                                line = br.readLine();
                                if (line != null) {
                                    pr.append(",\n");
                                }
                                pr.flush();
                            }
                            pr.append("]\n");
                            pr.flush();

                            f.delete();
                            tempFile.renameTo(f);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            System.err.println(SRC_DIRECTORY + " must be a directory !. Exit.");
        }

    }


    private static final String whitespace_chars =  ""       /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u002C" // COMMA
            + "\\u0085" // NEXT LINE (NEL)
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD
            + "\\u2001" // EM QUAD
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            ;
    /* A \s that actually works for Java’s native character set: Unicode */
    private static final String     whitespace_charclass = "["  + whitespace_chars + "]";
    /* A \S that actually works for  Java’s native character set: Unicode */
    private static final String not_whitespace_charclass = "[^" + whitespace_chars + "]";

}
