package Components;

public class Mutex {
    private String resourceName;
    private Process owner;
    private boolean isAvailable;
    private BlockedQueue blockedQueue = new BlockedQueue();

    public Mutex(String resourceName) {
        this.resourceName = resourceName;
        this.isAvailable = true;
    }

    // Acquire the mutex resource
    public void semWait() {
        if (isAvailable) {
            isAvailable = false;
        }
    }

    // Release the mutex resource
    public void semSignal() {
        if (!isAvailable) {
            isAvailable = true;
        }

    }

    public String getResourceName() {
        return resourceName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean mutexIsLocked() {
        return !isAvailable;
    }

    public Process getOwner() {
        return owner;
    }

    public void setOwner(Process owner) {
        this.owner = owner;
    }

    public BlockedQueue getBlockedQueue() {
        return blockedQueue;
    }
}
