package Components;

// round robin scheduler with quantum of 2
public class Scheduler {

    private NormalQueue normalQueue;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private int quantum;

    public Scheduler(NormalQueue normalQueue, ReadyQueue readyQueue, BlockedQueue blockedQueue, int quantum) {
        this.normalQueue = normalQueue;
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.quantum = quantum;
    }

    public void schedule() {
        if (normalQueue.peek() != null
                && normalQueue.peek().getPCB().getProgramCounter() < normalQueue.peek().getProgram().getInstructions()
                        .size()
                && !containBlocked(normalQueue.peek())) {
            readyQueue.addProcess(normalQueue.dequeue());

            printScheduler();
        }
    }

    public void addNormalProcess(Process process) {
        normalQueue.addProcess(process);
    }

    public void addReadyProcess(Process process) {
        readyQueue.addProcess(process);
        printReadyQueue();
    }

    public void addBlockedProcess(Process process) {
        blockedQueue.addProcess(process);
        printBlockedQueue();
    }

    public void printScheduler() {
        System.out.println("\n" + "Scheduler:");
        readyQueue.printReadyQueue();
        blockedQueue.printBlockedQueue();
    }

    public void printReadyQueue() {
        readyQueue.printReadyQueue();
    }

    public void printBlockedQueue() {
        blockedQueue.printBlockedQueue();
    }

    public Process getNextProcess() {
        return readyQueue.dequeue();
    }

    public int getQuantum() {
        return quantum;
    }

    public boolean containNormal(Process process) {
        return normalQueue.containProcess(process);
    }

    public boolean containReady(Process process) {
        return readyQueue.containProcess(process);
    }

    public boolean containBlocked(Process process) {
        return blockedQueue.containProcess(process);
    }

    public void removeNormalProcess(Process process) {
        normalQueue.removeProcess(process);
    }

    public void removeBlockedProcess(Process process) {
        blockedQueue.removeProcess(process);
    }

}
