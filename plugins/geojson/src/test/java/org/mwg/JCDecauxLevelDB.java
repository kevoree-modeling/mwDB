package org.mwg;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;

public class JCDecauxLevelDB {

    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withStorage(new LevelDBStorage("fullTempStorage")).withMemorySize(1000000).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 9011);
            graphServer.start();

            Undertow server = Undertow.builder()
                    .addHttpListener(9010,"0.0.0.0")
                    .setHandler(Handlers.resource(new ClassPathResourceManager(this.getClass().getClassLoader()))).build();
            server.start();

        });
    }

    public static void main(String[] args) {
        JCDecauxLevelDB test = new JCDecauxLevelDB();
        test.baseTest();
    }

}
