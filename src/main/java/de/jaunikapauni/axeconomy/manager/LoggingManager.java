package de.jaunikapauni.axeconomy.manager;

import de.jaunikapauni.axeconomy.AxEconomy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingManager {

    AxEconomy reference;
    File logsDirectory;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");

    public LoggingManager(AxEconomy reference){
        this.reference = reference;
        this.logsDirectory = new File(reference.getDataFolder(), "logs");
        if(!logsDirectory.exists()){
            logsDirectory.mkdirs();
        }
    }

    public void log(String message){
        new Thread(() -> {
            String timestamp = dateFormat.format(new Date());
            String fileName = fileFormat.format(new Date()) + ".log";
            try(FileWriter fileWriter = new FileWriter(new File(logsDirectory, fileName), true)){
                fileWriter.write(timestamp + " | " + message + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
