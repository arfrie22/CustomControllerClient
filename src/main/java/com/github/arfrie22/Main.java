package com.github.arfrie22;

import com.github.arfrie22.controller.CustomController;
import com.github.arfrie22.controller.ScanResults;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();


        inst.startClientTeam(467);
        inst.startDSClient();

        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            System.out.println("interrupted");
            return;
        }

        System.out.println(inst.isConnected());
        NetworkTable controllerTable = inst.getTable("Controller");

        NetworkTableEntry commandQueueEntry = controllerTable.getEntry("CommandQueue"); // raw bytes
        NetworkTableEntry commandQueueLengthEntry = controllerTable.getEntry("CommandQueueLength"); // double array
        NetworkTableEntry hasCommandEntry = controllerTable.getEntry("HasCommand"); // bool

        // TODO: Remove
        NetworkTableEntry responseEntry = controllerTable.getEntry("response"); // raw bytes
        NetworkTableEntry hasResponseEntry = controllerTable.getEntry("hasResponse"); // bool

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("Shutting down...");
                inst.close();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }));

        while (true) {
            ScanResults scan = CustomController.scan();
            if (scan.getValidControllers().length > 0) {
                CustomController controller = scan.getValidControllers()[0];
                controller.open(responseEntry, hasResponseEntry);
                while (controller.isOpen()) {
                    if (hasCommandEntry.getBoolean(false)) {
                        byte[] queue = commandQueueEntry.getRaw(new byte[0]);
                        double[] queueLength = commandQueueEntry.getDoubleArray(new double[0]);
                        int queuePointer = 0;
                        for (double length : queueLength) {
                            controller.send(Arrays.copyOfRange(queue, queuePointer, queuePointer + (int) length - 1));
                            queuePointer += (int) length;
                        }
                        commandQueueEntry.setRaw(new byte[0]);
                        commandQueueLengthEntry.setDoubleArray(new double[0]);
                        hasCommandEntry.setBoolean(false);
                    }
                }
            }

            System.out.println("No controller detected, trying again in 1 second");
            Thread.sleep(1000);
        }
    }
}