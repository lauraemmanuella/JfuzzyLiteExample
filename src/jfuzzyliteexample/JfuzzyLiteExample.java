package jfuzzyliteexample;

import com.fuzzylite.*;
import com.fuzzylite.activation.General;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class JfuzzyLiteExample {

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.setName("Tipper");

        InputVariable service = new InputVariable();
        service.setName("service");
        service.setRange(0.0, 10.0);
       
        service.addTerm(new Triangle("poor", -1, 0, 4)); 
        service.addTerm(new Trapezoid("good", 1, 4, 6, 9));
        service.addTerm(new Triangle("excelent", 6, 10, 11));
        engine.addInputVariable(service);
                
        InputVariable food = new InputVariable();
        food.setName("food");
        food.setRange(0.0, 10.0);
       
        food.addTerm(new Trapezoid("rancid", -1, 0, 1, 3)); 
        food.addTerm(new Triangle("delicious", 7, 10, 11));
        engine.addInputVariable(food);

        OutputVariable tip = new OutputVariable();
        tip.setName("tip");
        tip.setRange(0.0, 30.0);
        
        tip.setAggregation(new Maximum());
        tip.setDefuzzifier(new Centroid(100));
        tip.setDefaultValue(0);
        
        tip.addTerm(new Triangle("cheap", 0, 5, 10));
        tip.addTerm(new Triangle("average", 10, 15, 20));
        tip.addTerm(new Triangle("generous", 20, 25, 30));
        engine.addOutputVariable(tip);

        RuleBlock mamdani = new RuleBlock();
        mamdani.setName("mamdani");
        mamdani.setConjunction(new Minimum());
        mamdani.setDisjunction(new Maximum());
        mamdani.setImplication(new Minimum());
        mamdani.setActivation(new General());
        mamdani.addRule(Rule.parse("if service is poor or food is rancid then tip is cheap", engine));
        mamdani.addRule(Rule.parse("if service is good then tip is average", engine));
        mamdani.addRule(Rule.parse("if service is excelent and food is delicious then tip is generous", engine));
        engine.addRuleBlock(mamdani);

        InputVariable serviceinput = engine.getInputVariable("service");
        InputVariable foodinput = engine.getInputVariable("food");
        OutputVariable tipoutput = engine.getOutputVariable("tip");

        serviceinput.setValue(10);
        foodinput.setValue(10);
        engine.process();
        System.out.println("Service: " + serviceinput.getValue() + "\nFood:  " + foodinput.getValue() + "\nTip: " + tipoutput.getValue());
    }
}
