package com.invoicesync.service;

import java.io.InputStream;

public interface ConvertService {

  byte[] convertToPohoda(InputStream excelInputStream);

}
