package Components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {
    private Memory memory;
    private Mutex file;
    private Mutex userInput;
    private Mutex userOutput;
    private Scheduler scheduler;
    private int programsCounter;
    private int lowerMemoryBoundary;
    private int upperMemoryBoundary;
    private int clock;
    private Map<Program, Integer> programToProcess;

    public Interpreter() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter quantum: ");
        int quantum = scanner.nextInt();

        scheduler = new Scheduler(new NormalQueue(), new ReadyQueue(), new BlockedQueue(), quantum);
        memory = new Memory();
        file = new Mutex("fileMutex");
        userInput = new Mutex("inputMutex");
        userOutput = new Mutex("printMutex");

        programsCounter = 0;
        lowerMemoryBoundary = 0;
        upperMemoryBoundary = 4;

        programToProcess = new java.util.HashMap<Program, Integer>();
        clock = 0;

        // create the disk.txt file
        File fileDisk = new File(
                System.getProperty("user.dir") + File.separator + "Data" + File.separator + "disk.txt");
        // if file does not exists, then create it
        if (!fileDisk.exists()) {
            try {
                fileDisk.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter fw = new FileWriter(fileDisk.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // add program to the interpreter
    public void addProgram(String programPath, int timer) throws IOException {
        // get file from data folder
        BufferedReader file = new BufferedReader(new FileReader(programPath));
        ArrayList<String> instructions = addInstructions(file);

        programsCounter++;

        Program program = new Program("Program_" + programsCounter, instructions);

        programToProcess.put(program, timer);

    }

    // add instructions to the program
    private ArrayList<String> addInstructions(BufferedReader file) throws IOException {
        ArrayList<String> instructions = new ArrayList<String>();
        String line;
        while ((line = file.readLine()) != null) {
            instructions.add(line);
        }

        return instructions;
    }

    // change the program to process
    private void addProcess(Program program) throws IOException {
        if (upperMemoryBoundary >= 40) {
            unloadProcess();
        }

        writeToMemory(lowerMemoryBoundary, new Word(program.getName() + "_instructions", program.getInstructions()));
        writeToMemory(lowerMemoryBoundary + 1, new Word(program.getName() + "_a", null));
        writeToMemory(lowerMemoryBoundary + 2, new Word(program.getName() + "_b", null));
        writeToMemory(lowerMemoryBoundary + 3, new Word(program.getName() + "_c", null));
        ProcessControlBlock pcb = new ProcessControlBlock(program.getName(), ProcessState.NEW, 0,
                new int[] { lowerMemoryBoundary, upperMemoryBoundary });
        memory.writeMemory(lowerMemoryBoundary + 4, new Word(program.getName() + "_pcb", pcb));

        Process process = new Process(program, pcb);

        System.out.println("Allocated memory for process " + process.getPCB().getProcessID() + " from "
                + lowerMemoryBoundary + " to " + upperMemoryBoundary + "\n");

        lowerMemoryBoundary += 5;
        upperMemoryBoundary += 5;

        scheduler.addReadyProcess(process);

    }

    // load process from disk to 1st 5 memory locations
    private void loadProcess(Process process) throws FileNotFoundException, IOException {
        // read the process from disk.txt
        File file = new File(System.getProperty("user.dir") + File.separator + "Data" + File.separator + "disk.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            String wordName = line.split(": ")[0];
            String wordValue = line.split(": ")[1];

            if (wordName.equals(process.getPCB().getProcessID() + "_instructions")) {

                // string to arraylist<string>
                ArrayList<String> instructions = new ArrayList<String>();
                // remve the 1st bracket and the last bracket
                wordValue = wordValue.substring(1, wordValue.length() - 1);
                // split the string
                for (String instruction : wordValue.split(", ")) {
                    instructions.add(instruction);
                }

                memory.writeMemory(0,
                        new Word(wordName, instructions));

            } else if (wordName.equals(process.getPCB().getProcessID() + "_a")) {

                if (wordValue.equals("null")) {
                    memory.writeMemory(1,
                            new Word(wordName, null));
                } else {
                    memory.writeMemory(1,
                            new Word(wordName, wordValue));
                }
            } else if (wordName.equals(process.getPCB().getProcessID() + "_b")) {

                if (wordValue.equals("null")) {
                    memory.writeMemory(2,
                            new Word(wordName, null));
                } else {
                    memory.writeMemory(2,
                            new Word(wordName, wordValue));
                }

            } else if (wordName.equals(process.getPCB().getProcessID() + "_c")) {

                if (wordValue.equals("null")) {
                    memory.writeMemory(3,
                            new Word(wordName, null));
                } else {
                    memory.writeMemory(3,
                            new Word(wordName, wordValue));
                }

            } else if (wordName.equals(process.getPCB().getProcessID() + "_pcb")) {

                // string to pcb
                String[] pcbVariablesData = wordValue.split(" , ");
                String[] pcbValues = new String[5];
                int i = 0;
                for (String pcbVariableData : pcbVariablesData) {
                    pcbValues[i] = pcbVariableData.split(" = ")[1];
                    if (i == 3) {
                        // take the 1 and last number
                        String first = pcbValues[i].split(" - ")[0];
                        String last = pcbValues[i].split(" - ")[1];
                        pcbValues[i] = first;
                        pcbValues[i + 1] = last;
                        i++;
                    }
                    i++;
                }

                ProcessControlBlock pcb = new ProcessControlBlock(pcbValues[0],
                        ProcessState.valueOf(pcbValues[1]), Integer.parseInt(pcbValues[2]),
                        new int[] { Integer.parseInt(pcbValues[3]), Integer.parseInt(pcbValues[4]) });

                memory.writeMemory(4,
                        new Word(wordName, pcb));
            }
        }
        br.close();

        // delete the process from disk.txt
        File tempFile = new File(
                System.getProperty("user.dir") + File.separator + "Data" + File.separator + "disk.txt");
        File newFile = new File(
                System.getProperty("user.dir") + File.separator + "Data" + File.separator + "disk_temp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile, true));

        String lineSubStringToRemove = process.getPCB().getProcessID();

        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if (trimmedLine.startsWith(lineSubStringToRemove) || trimmedLine.equals("")) {
                continue;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
        }

        writer.close();
        reader.close();

        tempFile.delete();

        newFile.renameTo(tempFile);

        process.getPCB().setInDisk(false);
        writeToMemory(4, new Word(process.getPCB().getProcessID() + "_pcb", process.getPCB()));
    }

    // unload the 1st process from memory
    private void unloadProcess() throws IOException {
        System.out.println("Memory is full, will unload 1st Process" + "\n");
        // read the 1st process from memory
        Word instructions = memory.readMemory(0);
        Word a = memory.readMemory(1);
        Word b = memory.readMemory(2);
        Word c = memory.readMemory(3);
        Word pcb = memory.readMemory(4);
        ((ProcessControlBlock) pcb.get()).setInDisk(true);
        memory.writeMemory(4, pcb);
        pcb = memory.readMemory(4);

        // write the 1st process to disk.txt
        File file = new File(System.getProperty("user.dir") + File.separator + "Data" + File.separator + "disk.txt");
        FileWriter fr = new FileWriter(file, true);
        BufferedWriter br = new BufferedWriter(fr);
        if (instructions.get() == null)
            instructions.set("null");
        if (a.get() == null)
            a.set("null");
        if (b.get() == null)
            b.set("null");
        if (c.get() == null)
            c.set("null");
        if (pcb.get() == null)
            pcb.set("null");

        br.write(instructions.toString() + "\n");
        br.write(a.toString() + "\n");
        br.write(b.toString() + "\n");
        br.write(c.toString() + "\n");
        br.write(pcb.toString() + "\n");
        br.close();

        // remove the 1st process from memory

        memory.writeMemory(0, new Word("", null));
        memory.writeMemory(1, new Word("", null));
        memory.writeMemory(2, new Word("", null));
        memory.writeMemory(3, new Word("", null));
        memory.writeMemory(4, new Word("", null));

        // update the memory boundaries

        lowerMemoryBoundary = 0;
        upperMemoryBoundary = 4;
    }

    // run the interpreter
    public void execute() throws IOException {
        while (true) {
            System.out.println("Clock: " + clock + "\n");
            // check programToProcess integers against the clock
            for (Map.Entry<Program, Integer> entry : programToProcess.entrySet()) {
                if (entry.getValue() == clock) {
                    addProcess(entry.getKey());
                }
            }

            // check if there is a process in the ready queue
            Process process = scheduler.getNextProcess();
            if (process == null) {
                break;
            }

            // check if the process is in the disk
            if (process.getPCB().isInDisk()) {
                System.out.println("Process " + process.getPCB().getProcessID() + " is in the disk" + "\n");
                if (!memory.readMemory(0).getName().equals("") && !memory.readMemory(1).getName().equals("")
                        && !memory.readMemory(2).getName().equals("") && !memory.readMemory(3).getName().equals("")
                        && !memory.readMemory(4).getName().equals("")) {

                    unloadProcess();

                }

                loadProcess(process);

            }

            // execute the process
            executeProcess(process, scheduler.getQuantum());

            // schedule the next processes
            scheduler.schedule();

            // print the memory
            memory.print();

            clock++;
        }
    }

    // execute the process
    private void executeProcess(Process process, int quantum) throws IOException {

        System.out.println("Executing process " + process.getPCB().getProcessID() + " with quantum " + quantum + "\n");
        process.getPCB().setState(ProcessState.RUNNING);

        // get the process's information
        int programCounter = process.getPCB().getProgramCounter();
        int[] memoryBoundaries = process.getPCB().getMemoryBoundaries();
        int memoryStart = memoryBoundaries[0];
        int instructionsSize = process.getProgram().getInstructions().size();
        int instructionsLeft = instructionsSize - programCounter;
        int instructionsToRun = instructionsLeft < quantum ? instructionsLeft : quantum;
        int instructionsRan = 0;

        // run the process's instructions
        for (int i = programCounter; i < programCounter + instructionsToRun; i++) {
            Object a = readFromMemory(memoryStart + 1).get();
            Object b = readFromMemory(memoryStart + 2).get();
            Object c = readFromMemory(memoryStart + 3).get();
            String instruction = ((ArrayList<String>) readFromMemory(memoryStart).get()).get(i);

            System.out.println("Executing instruction '" + instruction + "' of process "
                    + process.getPCB().getProcessID());

            String[] instructionParts = instruction.split(" ");
            runInstruction(process, a, b, c, instructionParts);

            instructionsRan++;
        }

        // update the process's information
        process.getPCB().setProgramCounter(programCounter + instructionsRan);
        if (programCounter == instructionsSize - 1) {
            terminateProcess(process);
        }

        if (!scheduler.containBlocked(process) && !process.getPCB().getState().equals(ProcessState.TERMINATED))
            scheduler.addNormalProcess(process);

    }

    // run the instruction
    private void runInstruction(Process process, Object a, Object b, Object c, String[] instructionParts)
            throws FileNotFoundException, IOException {
        if (instructionParts[0].equals("print")) {
            if (userOutput.mutexIsLocked() && !userOutput.getOwner().equals(process)) {
                process.getPCB().setState(ProcessState.BLOCKED);
                scheduler.addBlockedProcess(process);
            } else {
                if (instructionParts[1].equals("a")) {
                    print(a);
                } else if (instructionParts[1].equals("b")) {
                    print(b);
                } else if (instructionParts[1].equals("c")) {
                    print(c);
                }
            }
        } else if (instructionParts[0].equals("assign")) {
            if (instructionParts[2].equals("readFile")) {
                String data = "";
                if (instructionParts[3].equals("a"))
                    data = readFile((String) a);
                else if (instructionParts[3].equals("b"))
                    data = readFile((String) b);
                else if (instructionParts[3].equals("c"))
                    data = readFile((String) c);

                assign(process, instructionParts[1], data);
            } else {
                if (userInput.mutexIsLocked() && !userInput.getOwner().equals(process)) {
                    process.getPCB().setState(ProcessState.BLOCKED);
                    scheduler.addBlockedProcess(process);
                } else {
                    System.out.println("Please Enter a value for " + instructionParts[1] + " of process "
                            + process.getPCB().getProcessID() + ": ");
                    assign(process, instructionParts[1], takeUserInput());
                }
            }
        } else if (instructionParts[0].equals("writeFile")) {
            if (file.mutexIsLocked() && !file.getOwner().equals(process)) {
                process.getPCB().setState(ProcessState.BLOCKED);
                scheduler.addBlockedProcess(process);
            } else {
                writeFile(a, b);
            }
        } else if (instructionParts[0].equals("printFromTo")) {
            printFromTo(process, Integer.parseInt((String) a), Integer.parseInt((String) b));

        } else if (instructionParts[0].equals("semWait")) {
            if (instructionParts[1].equals("file")) {
                semWait(file, process);
            } else if (instructionParts[1].equals("userInput")) {
                semWait(userInput, process);
            } else if (instructionParts[1].equals("userOutput")) {
                semWait(userOutput, process);
            }

        } else if (instructionParts[0].equals("semSignal")) {
            if (instructionParts[1].equals("file"))
                semSignal(file, process);
            else if (instructionParts[1].equals("userInput"))
                semSignal(userInput, process);
            else if (instructionParts[1].equals("userOutput"))
                semSignal(userOutput, process);
        }
    }

    // Print data on the screen
    private void print(Object a) {
        // print data
        System.out.println(a.toString() + "\n");
    }

    // assign data to a variable in the memory
    private void assign(Process process, String variable, Object data) {
        if (variable.equals("a")) {
            memory.writeMemory(process.getPCB().getMemoryBoundaries()[0] + 1,
                    new Word(process.getProgram().getName() + "_a", data));
        } else if (variable.equals("b")) {
            memory.writeMemory(process.getPCB().getMemoryBoundaries()[0] + 2,
                    new Word(process.getProgram().getName() + "_b", data));
        } else if (variable.equals("c")) {
            memory.writeMemory(process.getPCB().getMemoryBoundaries()[0] + 3,
                    new Word(process.getProgram().getName() + "_c", data));
        }
    }

    // Write data to any file on the disk
    private void writeFile(Object a, Object b) throws IOException {
        // create file
        File file = new File(System.getProperty("user.dir") + File.separator + "Data"
                + File.separator + a + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write((String) b);
        } catch (FileNotFoundException e) {
            throw e;
        }

        System.out.println("Data written to file successfully" + "\n");
    }

    // Read data of any file from the disk
    private String readFile(String filename) throws FileNotFoundException {
        File file = new File(System.getProperty("user.dir") + File.separator + "Data"
                + File.separator + filename + ".txt");
        String data = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                data += scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            throw e;
        }

        return data;
    }

    // Print numbers from a to b
    private void printFromTo(Process process, Object a, Object b) {
        for (int i = (int) a; i <= (int) b; i++) {
            if (i == (int) b)
                System.out.print(i);
            else
                System.out.print(i + " , ");

        }
    }

    // Take text input from the user
    private String takeUserInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input;
    }

    // Read data from the memory
    private Word readFromMemory(int address) {
        return memory.readMemory(address);
    }

    // Write data to the memory
    private void writeToMemory(int address, Word data) {
        memory.writeMemory(address, data);
    }

    // Wait for a mutex
    private void semWait(Mutex mutex, Process process) {
        if (mutex.mutexIsLocked() && !mutex.getOwner().equals(process)) {
            process.getPCB().setState(ProcessState.BLOCKED);
            scheduler.addBlockedProcess(process);
            mutex.getBlockedQueue().addProcess(process);
        } else {
            mutex.semWait();
            mutex.setOwner(process);
        }
    }

    // Signal a mutex
    private void semSignal(Mutex mutex, Process process) {
        if (mutex.getOwner().equals(process)) {
            mutex.semSignal();

            if (mutex.getBlockedQueue().peek() != null) {
                Process processNew = mutex.getBlockedQueue().dequeue();
                scheduler.removeBlockedProcess(processNew);

                processNew.getPCB().setState(ProcessState.READY);
                mutex.setOwner(processNew);
                scheduler.addReadyProcess(processNew);
            } else
                mutex.setOwner(null);
        } else
            System.out.println("You are not the owner of this mutex" + "\n");
    }

    // Terminate process
    private void terminateProcess(Process process) {
        process.getPCB().setState(ProcessState.TERMINATED);
        // Free memory
        int memoryStart = process.getPCB().getMemoryBoundaries()[0];
        int memoryEnd = process.getPCB().getMemoryBoundaries()[1];
        for (int i = memoryStart; i <= memoryEnd; i++) {
            memory.writeMemory(i, new Word("", null));
        }

        if (scheduler.containNormal(process))
            scheduler.removeNormalProcess(process);
        else if (scheduler.containBlocked(process))
            scheduler.removeBlockedProcess(process);

        System.out.println("Process " + process.getProgram().getName() + " terminated");
        System.out.println("Memory freed from " + process.getPCB().getMemoryBoundaries()[0] + " to "
                + process.getPCB().getMemoryBoundaries()[1] + "\n");
    }

}
