package Components;

import java.util.LinkedList;
import java.util.Queue;

public class ReadyQueue {
    private Queue<Process> readyQueue;

    public ReadyQueue() {
        readyQueue = new LinkedList<Process>();
    }

    public void addProcess(Process process) {
        readyQueue.add(process);
        process.getPCB().setState(ProcessState.READY);
    }

    public Process dequeue() {
        return readyQueue.poll();
    }

    public void printReadyQueue() {
        System.out.println("Ready Queue:");
        if (readyQueue.isEmpty()) {
            System.out.println("Empty");
        } else
            for (Process process : readyQueue) {
                System.out.println(process.getPCB().getProcessID());
            }
    }

    public Process peek() {
        return readyQueue.peek();
    }

    public boolean containProcess(Process process) {
        return readyQueue.contains(process);
    }

}
