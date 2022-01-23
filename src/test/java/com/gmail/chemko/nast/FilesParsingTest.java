package com.gmail.chemko.nast;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @Test
    void zipTest() throws Exception {
        ZipFile zipFile = new ZipFile(new File(cl.getResource("files/archive.zip").toURI()));

        //Проверяем XLSX файл в архиве
        ZipEntry xlsxFile = zipFile.getEntry("sample-xlsx-file.xlsx");
        try (InputStream xlsxStream = zipFile.getInputStream(xlsxFile)) {
            XLS parsedXLS = new XLS(xlsxStream);
            assertThat(parsedXLS.excel.getSheetAt(0).getRow(2).getCell(3).getStringCellValue())
                    .isEqualTo("Germany");
        }

        //Проверяем PDF файл в архиве
        ZipEntry pdfFile = zipFile.getEntry("sample-pdf-file.pdf");
        try (InputStream pdfStream = zipFile.getInputStream(pdfFile)) {
            PDF parsedPDF = new PDF(pdfStream);
            assertThat(parsedPDF.text).contains("PROCEDURE OF ETYMOLOGICAL ANALYSIS");
        }

        //Проверяем CSV файл в архиве
        ZipEntry csvFile = zipFile.getEntry("sample-csv-file.csv");
        try (InputStream stream = zipFile.getInputStream(csvFile)) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list)
                    .hasSize(4)
                    .contains(
                            new String[] {"Name", "Profession"},
                            new String[] {"Olivia", "Judge"},
                            new String[] {"Ilse", "Lawyer"},
                            new String[] {"Gabriel", "Engineer"}
                    );
        }
    }
}
