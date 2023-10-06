package GE;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected List<Node> children;
    protected String value = null;
    protected int level;//used for printing purposes

    public Node(String value){
        this.children = new ArrayList<Node>();
        this.value = value;
    }

    /**
     * Adds a node to the list of children
     * @param node
     */
    public void addChild(Node node){
        children.add(node);
    }

    /**
     * Getter method
     * @return list of children
     */
    public List<Node> getChildren(){
        return this.children;
    }

    public int getSizeOfChildren(){
        return this.children.size();
    }

    /**
     * 
     * @param level
     */
    public void setLevel(int level){
        this.level = level;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<level; i++){
            sb.append("\t");
        }

        sb.append(value).append("\n");

        for(Node n: children){
            n.level = this.level + 1;
            sb.append(n.toString());
        }

        return sb.toString();
    }

    String getValue(){
        return this.value;
    }
    public abstract void execute();
}
