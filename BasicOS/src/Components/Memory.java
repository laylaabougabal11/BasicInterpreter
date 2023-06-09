package Components;

public class Memory {
    Word[] memory;

    public Memory() {
        memory = new Word[40];
        for (int i = 0; i < memory.length; i++) {
            memory[i] = new Word("", 0);
        }
    }

    // Read data from memory
    public Word readMemory(int address) {
        return memory[address];
    }

    // Write data to memory
    public void writeMemory(int address, Word data) {
        memory[address] = data;
    }

    public Word get(int index) {
        return memory[index];
    }

    public void print() {
        for (int i = 0; i < memory.length; i++) {
            if (!memory[i].getName().equals("") && memory[i].get() != null)
                System.out.println("memory[" + i + "] = " + memory[i].toString() + "\n");
        }
    }

}
