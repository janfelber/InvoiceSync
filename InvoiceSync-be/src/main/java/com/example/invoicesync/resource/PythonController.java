package com.example.invoicesync.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class PythonController {

    @Autowired
    private DataSource dataSource;
    @GetMapping("/runPythonScript")
    public String runPythonScript(@RequestParam String username, @RequestParam String filePath, @RequestParam String schemaPath) {
        String script = "";
        try {

            Connection connection = dataSource.getConnection();

            String sql = "SELECT script FROM invoice_sync.credential WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                script = resultSet.getString("script");
                System.out.println("Script: " + script);
                //execute the script from database
                String result = executePythonScript(script, filePath, schemaPath);
                return result;
            } else {
                script = "Script not found for the user: " + username;
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return script;
    }

    private String executePythonScript(String script, String filePath, String schemaPath) {
        try {
            // Write script to a file
            Path tempScript = Files.createTempFile("temp_script", ".py");
            Files.write(tempScript, script.getBytes());

            // Here, we will return the result instead of printing it
            String[] cmd = {"python", tempScript.toString(), filePath, schemaPath};
            Process p = Runtime.getRuntime().exec(cmd);

            // Read script output
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // Wait for the script to finish and get the exit code
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                return "Script executed successfully.\nResult: " + result.toString();
            } else {
                return "Script execution failed with error code: " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error occurred while executing the script";
        }
    }
}
