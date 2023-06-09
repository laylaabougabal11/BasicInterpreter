package Components;

public class ProcessControlBlock {
    private String processID;
    private ProcessState state;
    private int programCounter;
    private int[] memoryBoundaries;
    private boolean inDisk;

    public ProcessControlBlock(String processID, ProcessState state, int programCounter, int[] memoryBoundaries) {
        this.processID = processID;
        this.state = state;
        this.programCounter = programCounter;
        this.memoryBoundaries = memoryBoundaries;
        this.inDisk = false;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int[] getMemoryBoundaries() {
        return memoryBoundaries;
    }

    public void setMemoryBoundaries(int[] memoryBoundaries) {
        this.memoryBoundaries = memoryBoundaries;
    }

    public void printPCB() {
        System.out.println("Process ID: " + processID);
        System.out.println("State: " + state);
        System.out.println("Program Counter: " + programCounter);
        System.out.println("Memory Boundaries: " + memoryBoundaries[0] + " - " + memoryBoundaries[1] + "\n");
    }

    @Override
    public String toString() {
        return "Process ID = " + processID + " , " + "State = " + state + " , " + "Program Counter = " + programCounter
                + " , "
                + "Memory Boundaries = " + memoryBoundaries[0] + " - " + memoryBoundaries[1] + "\n";
    }

    public boolean isInDisk() {
        return inDisk;
    }

    public void setInDisk(boolean inDisk) {
        this.inDisk = inDisk;
    }

}
