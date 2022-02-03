package com.github.arfrie22;

import com.fazecast.jSerialComm.SerialPort;
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
        NetworkTable controllerTable = inst.getTable("controller");

        NetworkTableEntry robotConnectedEntry = controllerTable.getEntry("robotConnected"); // bool
        NetworkTableEntry clientConnectedEntry = controllerTable.getEntry("clientConnected"); // bool

        NetworkTableEntry commandEntry = controllerTable.getEntry("command"); // raw bytes
        NetworkTableEntry hasCommandEntry = controllerTable.getEntry("hasCommand"); // bool
        NetworkTableEntry responseEntry = controllerTable.getEntry("response"); // raw bytes
        NetworkTableEntry hasResponseEntry = controllerTable.getEntry("hasResponse"); // bool

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                clientConnectedEntry.setBoolean(false);
                Thread.sleep(200);
                System.out.println("Shutting down...");
                inst.close();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }));

        clientConnectedEntry.setBoolean(true);

        while (true) {
            CustomController controller = CustomController.scan();
            if (controller != null) {
                controller.open(responseEntry, hasResponseEntry);
                while (controller.isOpen()) {
                    if (hasCommandEntry.getBoolean(false)) {
                        controller.send(commandEntry.getRaw(new byte[0]));
                        commandEntry.setRaw(new byte[0]);
                        hasCommandEntry.setBoolean(false);
                    }
                }
            }

            System.out.println("No controller detected, trying again in 1 second");
            Thread.sleep(1000);
        }
    }
}