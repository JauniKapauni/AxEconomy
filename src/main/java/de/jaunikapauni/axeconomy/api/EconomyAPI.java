package de.jaunikapauni.axeconomy.api;

import java.util.UUID;

public interface EconomyAPI {

    double getBalance(UUID uuid);

    boolean has(UUID uuid, double amount);

    boolean withdraw(UUID uuid, double amount);

    void deposit(UUID uuid, double amount);

    void setBalance(UUID uuid, double amount);
}
