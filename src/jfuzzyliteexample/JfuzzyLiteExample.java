package jfuzzyliteexample;

import com.fuzzylite.*;
import com.fuzzylite.activation.General;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class JfuzzyLiteExample {

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.setName("ObstacleAvoidance");
        engine.setDescription("");

        InputVariable obstacle = new InputVariable();
        obstacle.setName("obstacle");
        obstacle.setDescription("");
        obstacle.setEnabled(true);
        obstacle.setRange(0.000, 1.000);
        obstacle.setLockValueInRange(false);
        //ao inves de Ramp poderia ser:
        //new Triangle("D", 0.500, 1.000, 1.500)
        //new Trapezoid("E", 1.000, 1.250, 1.750, 2.000)
        obstacle.addTerm(new Ramp("left", 1.000, 0.000)); 
        obstacle.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addInputVariable(obstacle);

        OutputVariable mSteer = new OutputVariable();
        mSteer.setName("mSteer");
        mSteer.setDescription("");
        mSteer.setEnabled(true);
        mSteer.setRange(0.000, 1.000);
        mSteer.setLockValueInRange(false);
        mSteer.setAggregation(new Maximum());
        mSteer.setDefuzzifier(new Centroid(100));
        mSteer.setDefaultValue(Double.NaN);
        mSteer.setLockPreviousValue(false);
        mSteer.addTerm(new Ramp("left", 1.000, 0.000));
        mSteer.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addOutputVariable(mSteer);

        RuleBlock mamdani = new RuleBlock();
        mamdani.setName("mamdani");
        mamdani.setDescription("");
        mamdani.setEnabled(true);
        mamdani.setConjunction(null);
        mamdani.setDisjunction(null);
        mamdani.setImplication(new AlgebraicProduct());
        mamdani.setActivation(new General());
        mamdani.addRule(Rule.parse("if obstacle is left then mSteer is right", engine));
        mamdani.addRule(Rule.parse("if obstacle is right then mSteer is left", engine));
        engine.addRuleBlock(mamdani);


        StringBuilder status = new StringBuilder();
        if (! engine.isReady(status))
            throw new RuntimeException("[engine error] engine is not ready:n" + status);

        InputVariable obstacle2 = engine.getInputVariable("obstacle");
        OutputVariable steer = engine.getOutputVariable("mSteer");

        for (int i = 0; i <= 50; ++i){
            double location = obstacle2.getMinimum() + i * (obstacle2.range() / 50);
            obstacle2.setValue(location);
            engine.process();
            FuzzyLite.logger().info(String.format(
                    "obstacle.input = %s -> steer.output = %s",
                    Op.str(location), Op.str(steer.getValue())));
        }
    }
}
