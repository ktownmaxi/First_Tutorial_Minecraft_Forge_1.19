package net.maxi.tutorialmod.client;

public class ClientThirstData {
    private static int playerThirst;

    public static void setPlayerThirst(int thirst) {
        ClientThirstData.playerThirst = thirst;
    }

    public static int getPlayerThirst() {
        return playerThirst;
    }
}
