package Components;

import java.util.LinkedList;
import java.util.Queue;

public class BlockedQueue {
    private Queue<Process> blockedQueue;

    public BlockedQueue() {
        blockedQueue = new LinkedList<Process>();
    }

    public void addProcess(Process process) {
        blockedQueue.add(process);
        process.getPCB().setState(ProcessState.BLOCKED);
    }

    public Process dequeue() {
        return blockedQueue.poll();
    }

    public void printBlockedQueue() {
        System.out.println("Blocked Queue:");
        if (blockedQueue.isEmpty()) {
            System.out.println("Empty");
        } else
            for (Process process : blockedQueue) {
                System.out.println(process.getPCB().getProcessID());
            }
    }

    public Object peek() {
        return blockedQueue.peek();
    }

    public boolean containProcess(Process process) {
        return blockedQueue.contains(process);
    }

    public void removeProcess(Process process) {
        blockedQueue.remove(process);
    }

}
