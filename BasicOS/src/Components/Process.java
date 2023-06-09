package Components;

public class Process {
    private Program program;
    private ProcessControlBlock pcb;
    

    public Process(Program program, ProcessControlBlock pcb) {
        this.program = program;
        this.pcb = pcb;
        
    }

    public Program getProgram() {
        return program;
    }

    public ProcessControlBlock getPCB() {
        return pcb;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void printProcess() {
        System.out.println("Process: " + program.getName());
        pcb.printPCB();
    }

}