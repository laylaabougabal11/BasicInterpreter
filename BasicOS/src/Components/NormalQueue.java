package Components;

import java.util.LinkedList;
import java.util.Queue;

public class NormalQueue {
    private Queue<Process> normalQueue;

    public NormalQueue() {
        normalQueue = new LinkedList<Process>();
    }

    public void addProcess(Process process) {
        normalQueue.add(process);
        process.getPCB().setState(ProcessState.READY);
    }

    public Process dequeue() {
        return normalQueue.poll();
    }

    public void printNormalQueue() {
        System.out.println("Ready Queue:");
        for (Process process : normalQueue) {
            process.printProcess();
        }
    }

    public Process peek() {
        return normalQueue.peek();
    }

    public boolean containProcess(Process process) {
        return normalQueue.contains(process);
    }

    public void removeProcess(Process process) {
        normalQueue.remove(process);
    }

}
