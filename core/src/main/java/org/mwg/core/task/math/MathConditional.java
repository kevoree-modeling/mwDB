package org.mwg.core.task.math;

import org.mwg.task.TaskContext;
import org.mwg.task.ConditionalFunction;

import java.util.HashMap;
import java.util.Map;

public class MathConditional {

    private MathExpressionEngine _engine;
    private String _expression;

    public MathConditional(String mathExpression) {
        this._expression = mathExpression;
        this._engine = CoreMathExpressionEngine.parse(mathExpression);
    }

    public ConditionalFunction conditional() {
        return new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                Map<String, Double> variables = new HashMap<String, Double>();
                variables.put("PI", Math.PI);
                variables.put("TRUE", 1.0);
                variables.put("FALSE", 0.0);
                return (_engine.eval(null, context, variables) >= 0.5);
            }
        };
    }

    @Override
    public String toString() {
        return "cond(\'" + _expression + "\')";
    }

}
