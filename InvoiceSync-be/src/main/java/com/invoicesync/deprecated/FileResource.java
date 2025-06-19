// package com.invoicesync.resource;
//
// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.OutputStreamWriter;
// import java.nio.charset.StandardCharsets;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.invoicesync.controller.ImportController;
//
// @RestController
// @RequestMapping("/file")
// public class FileResource {
//
//     @Autowired
//     private PythonController pythonController;
//
//     //define a location
//     public static final String DIRECTORY = system.getProperty("user.home");
//
//     @Autowired
//     private JdbcTemplate jdbcTemplate;
//     @Autowired
//     private ImportController importController;
//
//     private static Process getProcess(File tempPythonScriptFile, String pythonScriptContent, File tempXmlFile) throws IOException {
//         try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempPythonScriptFile), StandardCharsets.UTF_8))) {
//             writer.write(pythonScriptContent);
//         }
//
//         String schemaPath = "C:/Users/Jano/Desktop/schema_xml.xml";
//
//         ProcessBuilder pb = new ProcessBuilder("python", tempPythonScriptFile.getAbsolutePath(), schemaPath, tempXmlFile.getAbsolutePath());
//         pb.redirectOutput(new File("output.zip"));
//         pb.redirectErrorStream(true);
//         return pb.start();
//     }
// }
