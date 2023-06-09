package Components;

import java.util.ArrayList;

public class Program {
    private String name;
    private ArrayList<String> instructions;

    public Program(String name, ArrayList<String> instructions) {
        this.name = name;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    

}
