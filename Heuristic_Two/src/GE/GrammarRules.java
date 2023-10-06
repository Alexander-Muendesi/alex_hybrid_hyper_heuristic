package GE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarRules {
    private Map<String,Map<Integer,List<String>>> grammar;

    public GrammarRules(){
        grammar = new HashMap<String,Map<Integer,List<String>>>();

        List<String> vals = new ArrayList<String>();
        Map<Integer,List<String>> temp = new HashMap<Integer,List<String>>();

        //<Start>
        vals.add("<accept>"); vals.add("<heuristic>");
        temp.put(0, vals);
        grammar.put("<start>",temp);

        //<accept>
        temp = new HashMap<Integer,List<String>>(); vals = new ArrayList<String>();
        vals.add("IO"); 
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("AM");
        temp.put(1,vals);
        grammar.put("<accept>",temp);

        //<heuristic>
        temp = new HashMap<Integer,List<String>>();vals = new ArrayList<String>();
        vals.add("swap"); vals.add("<n>"); vals.add("<compSel>");
        temp.put(0, vals); vals = new ArrayList<String>();

        vals.add("move"); vals.add("<n>"); vals.add("<compSel>");
        temp.put(1, vals); vals = new ArrayList<String>();

        // vals.add("add"); vals.add("<n>"); vals.add("<compSel>");
        // temp.put(2,vals); vals = new ArrayList<String>();

        // vals.add("delete"); vals.add("<n>"); vals.add("<compSel>");
        // temp.put(3,vals); vals = new ArrayList<String>();

        vals.add("shuffle"); vals.add("<n>"); vals.add("<compSel>");
        temp.put(2, vals); vals = new ArrayList<String>();

        vals.add("<heuristic>"); vals.add("<cop>"); vals.add("<heuristic>");
        temp.put(3, vals); vals = new ArrayList<String>();

        vals.add("<heuristic>");
        temp.put(4, vals); vals = new ArrayList<String>();

        vals.add("if"); vals.add("<cond>"); vals.add("<heuristic>"); vals.add("<heuristic>");
        temp.put(5,vals); vals = new ArrayList<String>();

        grammar.put("<heuristic>", temp); temp = new HashMap<Integer,List<String>>();

        //<cond>
        vals.add("<rop>"); vals.add("<h_value>"); vals.add("<h_value>");
        temp.put(0, vals); vals = new ArrayList<String>();

        vals.add("if"); vals.add("<cond>"); vals.add("<cond>"); vals.add("<cond>");
        temp.put(1, vals); vals = new ArrayList<String>();

        grammar.put("<cond>", temp); temp = new HashMap<Integer,List<String>>();

        //h_value
        vals.add("prevFitness");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("currFitness");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add("diffFitness");
        temp.put(2, vals); vals = new ArrayList<String>();

        vals.add("currIteration");
        temp.put(3, vals); vals = new ArrayList<String>();

        vals.add("totalIterations");
        temp.put(4,vals); vals = new ArrayList<String>();

        grammar.put("<h_value>", temp); temp = new HashMap<Integer,List<String>>();

        //<compSel>
        vals.add("lowestCost"); vals.add("<comp>");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("highestCost"); vals.add("<comp>");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add("smallestSize"); vals.add("<comp>");
        temp.put(2, vals); vals = new ArrayList<String>();

        vals.add("largestSize"); vals.add("<comp>");
        temp.put(3, vals); vals = new ArrayList<String>();

        vals.add("random"); vals.add("<comp>");
        temp.put(4, vals); vals = new ArrayList<String>();

        vals.add("if"); vals.add("<prob>"); vals.add("<compSel>"); vals.add("<compSel>");
        temp.put(5, vals); vals = new ArrayList<String>();

        grammar.put("<compSel>", temp); temp = new HashMap<Integer,List<String>>();

        //<comp>
        vals.add("lecture");
        temp.put(0, vals); vals = new ArrayList<String>();

        vals.add("period");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add("room");
        temp.put(2, vals); vals = new ArrayList<String>();

        grammar.put("<comp>", temp); temp = new HashMap<Integer,List<String>>();

        //cop
        vals.add("union");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("rGradient");
        temp.put(1, vals); vals = new ArrayList<String>();

        grammar.put("<cop>", temp); temp = new HashMap<Integer,List<String>>();

        //rop
        vals.add("<=");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("<");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add(">");
        temp.put(2,vals); vals = new ArrayList<String>();

        vals.add(">=");
        temp.put(3,vals); vals = new ArrayList<String>();

        grammar.put("<rop>",temp); temp = new HashMap<Integer,List<String>>();

        //prob
        vals.add("25");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("50");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add("75");
        temp.put(2,vals); vals = new ArrayList<String>();

        grammar.put("<prob>", temp); temp = new HashMap<Integer,List<String>>();

        //<n>

        vals.add("1");
        temp.put(0,vals); vals = new ArrayList<String>();

        vals.add("2");
        temp.put(1, vals); vals = new ArrayList<String>();

        vals.add("3");
        temp.put(2, vals); vals = new ArrayList<String>();

        vals.add("4");
        temp.put(3, vals); vals = new ArrayList<String>();

        vals.add("5");
        temp.put(4, vals); vals = new ArrayList<String>();

        vals.add("6");
        temp.put(5, vals); vals = new ArrayList<String>();

        vals.add("7");
        temp.put(6,vals); vals = new ArrayList<String>();

        vals.add("8");
        temp.put(7, vals); vals = new ArrayList<String>();

        vals.add("9");
        temp.put(8, vals); vals = new ArrayList<String>();

        vals.add("10");
        temp.put(9,vals); vals = new ArrayList<String>();

        vals.add("<n>");
        temp.put(10, vals); vals = new ArrayList<String>();

        // vals.add("<n>");
        // vals.add("<n>");
        // temp.put(11, vals); vals = new ArrayList<String>();

        grammar.put("<n>", temp); temp = new HashMap<Integer,List<String>>();
    }

    /**
     * 
     * @param key
     * @return The list of rules associated with the key.
     */
    public Map<Integer,List<String>> getRhs(String key){
        return this.grammar.get(key);
    }

    /**
     * Checks to see whether the passed in input is a terminal or function node
     * @param val
     * @return
     */
    public boolean containsKey(String val){
        return this.grammar.containsKey(val);
    }

    public int getSize(){
        return this.grammar.size();
    }
}
