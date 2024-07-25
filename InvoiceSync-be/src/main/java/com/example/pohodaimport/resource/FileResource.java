package com.example.pohodaimport.resource;

import com.example.pohodaimport.controller.ImportController;
import com.example.pohodaimport.controller.ScriptSchemaController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/file")
public class FileResource {

    @Autowired
    private PythonController pythonController;

    //define a location
    public static final String DIRECTORY = System.getProperty("user.home");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ScriptSchemaController scriptSchemaController;
    @Autowired
    private ImportController importController;


    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles) throws IOException, SQLException {
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path fileStorage = Paths.get(DIRECTORY, filename).toAbsolutePath().normalize();
            file.transferTo(fileStorage.toFile());
            filenames.add(filename);

            String filePath = fileStorage.toString();

            // Čtení obsahu XML souboru
            String xmlContent = new String(Files.readAllBytes(Paths.get(filePath)));

            // Post filename and XML content to database
            postFilenameAndContent(filename, xmlContent);
        }

        return ResponseEntity.ok().body(filenames);
    }


    @PostMapping("/post/filenameAndContent")
    public ResponseEntity<String> postFilenameAndContent(@RequestParam("filename") String filename, @RequestParam("xmlContent") String xmlContent) throws SQLException {
        int credentialId = 1;
        Timestamp create = new Timestamp(System.currentTimeMillis());
        int companyId = 8;
        long unixTime = create.getTime() / 1000L;

        String sql = "INSERT INTO invoice_sync.import_file (credential_id, filename, created_at, company_id, xml_content) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, credentialId, filename, unixTime, companyId, xmlContent);

        return ResponseEntity.ok().body("Filename and XML content posted successfully");
    }

    @GetMapping("/generateZip/{import_id}")
    public ResponseEntity<?> generateZip(@PathVariable("import_id") int importId) {

        try {
            String xmlContent = importController.getXmlContentById(importId);
            String pythonScriptContent = scriptSchemaController.getScriptByCompanyId(1);

            // Create a temporary file to hold XML content
            File tempXmlFile = File.createTempFile("input", ".xml");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempXmlFile), StandardCharsets.UTF_8))) {
                writer.write(xmlContent);
            }

            // Create a temporary Python script file
            File tempPythonScriptFile = File.createTempFile("script", ".py");
            Process process = getProcess(tempPythonScriptFile, pythonScriptContent, tempXmlFile);
            process.waitFor();


            // Return the ZIP file
            File zipFile = new File("output.zip");
            if (zipFile.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.zip\"")
                        .body(Files.readAllBytes(zipFile.toPath()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating ZIP file");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    private static Process getProcess(File tempPythonScriptFile, String pythonScriptContent, File tempXmlFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempPythonScriptFile), StandardCharsets.UTF_8))) {
            writer.write(pythonScriptContent);
        }

        String schemaPath = "C:/Users/Jano/Desktop/schema_xml.xml";

        ProcessBuilder pb = new ProcessBuilder("python", tempPythonScriptFile.getAbsolutePath(), schemaPath, tempXmlFile.getAbsolutePath());
        pb.redirectOutput(new File("output.zip"));
        pb.redirectErrorStream(true);
        return pb.start();
    }
}
