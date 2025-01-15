package org.example;

import com.opencsv.CSVReader;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DeviceSimulator {

    private static final String QUEUE_NAME = "energy_measurements";
    private static final String CONFIG_FILE = "config.properties"; // Path to the configuration file
    private static final String SENSOR_CSV = "sensor.csv"; // Path to the CSV file

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            // Load configuration
            Properties config = loadConfiguration();
            String rabbitMQHost = config.getProperty("rabbitmq_host", "localhost");
            factory.setHost(rabbitMQHost);

            // Get device IDs from config
            List<String> deviceIds = loadDeviceIdsFromConfig(config);
            if (deviceIds.isEmpty()) {
                System.err.println("No device IDs found in config.properties. Exiting.");
                return;
            }

            // Establish RabbitMQ connection
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel();
                 CSVReader csvReader = new CSVReader(new FileReader(SENSOR_CSV))) {

                // Declare the RabbitMQ queue
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                // Process each row in the CSV and calculate hourly consumption
                List<Double> window = new ArrayList<>();
                String[] nextLine;
                int deviceIndex = 0; // To cycle through device IDs
                long timestamp = System.currentTimeMillis(); // Simulated timestamp

                while ((nextLine = csvReader.readNext()) != null) {
                    double measurementValue = Double.parseDouble(nextLine[0]);
                    window.add(measurementValue);

                    // If window contains 6 values, calculate hourly consumption
                    if (window.size() == 6) {
                        double hourlyConsumption = window.get(5) - window.get(0);

                        // Select a device ID in a round-robin fashion
                        String deviceId = deviceIds.get(deviceIndex);
                        deviceIndex = (deviceIndex + 1) % deviceIds.size();

                        // Create a JSON message using JSONObject
                        JSONObject json = new JSONObject();
                        json.put("timestamp", timestamp);
                        json.put("device_id", deviceId);
                        json.put("measurement_value", hourlyConsumption);

                        String message = json.toString();

                        // Publish the message to RabbitMQ
                        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                        System.out.println("Sent: " + message);

                        // Remove the oldest value to maintain a sliding window
                        window.remove(0);

                        // Increment timestamp (simulate 1 hour per measurement)
                        timestamp += 3600000;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties loadConfiguration() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            properties.load(reader);
        }
        return properties;
    }

    private static List<String> loadDeviceIdsFromConfig(Properties config) {
        List<String> deviceIds = new ArrayList<>();

        // Assuming device IDs are stored in config as "device_id_1", "device_id_2", etc.
        int index = 1;
        while (true) {
            String deviceId = config.getProperty("device_id_" + index);
            if (deviceId == null) {
                break;
            }
            deviceIds.add(deviceId);
            index++;
        }

        return deviceIds;
    }
}
