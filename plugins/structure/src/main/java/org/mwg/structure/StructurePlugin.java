package org.mwg.structure;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.base.BasePlugin;
import org.mwg.plugin.NodeFactory;
import org.mwg.structure.action.*;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.structure.tree.SparseNDTree;
import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;

import static org.mwg.core.task.Actions.newTask;

public class StructurePlugin extends BasePlugin {

    public StructurePlugin() {
        super();
        declareNodeType(KDTree.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new KDTree(world, time, id, graph);
            }
        });
        declareNodeType(NDTree.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new NDTree(world, time, id, graph);
            }
        });

        declareNodeType(SparseNDTree.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new SparseNDTree(world, time, id, graph);
            }
        });

        declareTaskAction(NTreeInsertTo.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new NTreeInsertTo(params[0]);
            }
        });
        declareTaskAction(TraverseById.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new TraverseById(params[0]);
            }
        });
        declareTaskAction(NTreeNearestN.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length < 2) {
                    throw new RuntimeException("Bad param number!");
                }
                int n = Integer.parseInt(params[params.length - 1]);
                double[] key = new double[params.length - 1];
                for (int i = 0; i < params.length - 1; i++) {
                    key[i] = Double.parseDouble(params[i]);

                }
                return new NTreeNearestN(key, n);
            }
        });
        declareTaskAction(NTreeNearestWithinRadius.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length < 2) {
                    throw new RuntimeException("Bad param number!");
                }
                double radius = Double.parseDouble(params[params.length - 1]);
                double[] key = new double[params.length - 1];
                for (int i = 0; i < params.length - 1; i++) {
                    key[i] = Double.parseDouble(params[i]);
                }
                return new NTreeNearestWithinRadius(key, radius);
            }
        });
        declareTaskAction(NTreeNearestNWithinRadius.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length < 3) {
                    throw new RuntimeException("Bad param number!");
                }
                double radius = Double.parseDouble(params[params.length - 1]);
                int n = Integer.parseInt(params[params.length - 2]);
                double[] key = new double[params.length - 2];
                for (int i = 0; i < params.length - 2; i++) {
                    key[i] = Double.parseDouble(params[i]);
                }
                return new NTreeNearestNWithinRadius(key, n, radius);
            }
        });
    }
}
