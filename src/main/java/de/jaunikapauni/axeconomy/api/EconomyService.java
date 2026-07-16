package de.jaunikapauni.axeconomy.api;

import de.jaunikapauni.axeconomy.manager.EconomyManager;

import java.util.UUID;

public class EconomyService implements EconomyAPI{

    EconomyManager manager;
    public EconomyService(EconomyManager manager){
        this.manager = manager;
    }

    @Override
    public double getBalance(UUID uuid){
        return manager.getBalance(uuid);
    }

    @Override
    public boolean has(UUID uuid, double amount){
        if(amount <= 0){
            return false;
        }
        return getBalance(uuid) >= amount;
    }

    @Override
    public boolean withdraw(UUID uuid, double amount){
        if(amount <= 0){
            return false;
        }
        double balance = getBalance(uuid);
        if(balance < amount){
            return false;
        }
        manager.setBalance(uuid, balance - amount);
        return true;
    }

    @Override
    public void deposit(UUID uuid, double amount){
        if(amount <= 0){
            return;
        }
        manager.setBalance(uuid, getBalance(uuid) + amount);
    }

    @Override
    public void setBalance(UUID uuid, double amount){
        if(amount < 0){
            return;
        }
        manager.setBalance(uuid, amount);
    }
}
