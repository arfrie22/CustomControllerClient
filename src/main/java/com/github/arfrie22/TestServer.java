package com.github.arfrie22;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class TestServer {
    public static void main(String[] args) {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.setServerTeam(467);
        System.out.println("Starting server");
        inst.startServer();

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

        NetworkTable controllerTable = inst.getTable("controller");

        NetworkTableEntry robotConnectedEntry = controllerTable.getEntry("robotConnected"); // bool
        NetworkTableEntry clientConnectedEntry = controllerTable.getEntry("clientConnected"); // bool

        NetworkTableEntry commandEntry = controllerTable.getEntry("command"); // raw bytes
        NetworkTableEntry hasCommandEntry = controllerTable.getEntry("hasCommand"); // bool
        NetworkTableEntry responseEntry = controllerTable.getEntry("response"); // raw bytes
        NetworkTableEntry hasResponseEntry = controllerTable.getEntry("hasResponse"); // bool

        robotConnectedEntry.setBoolean(true);
        clientConnectedEntry.setBoolean(false);

        commandEntry.setRaw(new byte[0]);
        hasCommandEntry.setBoolean(false);
        responseEntry.setRaw(new byte[0]);
        hasResponseEntry.setBoolean(false);

        while (true) {}
    }
}
