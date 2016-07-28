/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.domain.ClassificationResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ExcelService {

    @Getter
    @Setter
    private String outputFile;

    @Getter
    @Setter
    private List<ClassificationResult> classificationResults;

    private HSSFWorkbook workbook;

    public void createWorkBook() {
        this.workbook = new HSSFWorkbook();
    }

    public void createSheet(String name, List<ClassificationResult> classificationResults) {
        HSSFSheet sheet = workbook.createSheet(name);

        HSSFRow rowhead = sheet.createRow((short) 0);
        rowhead.createCell(0).setCellValue("fileName");
        rowhead.createCell(1).setCellValue("finalClassification");
        rowhead.createCell(2).setCellValue("date");
        rowhead.createCell(3).setCellValue("nudeAvgScore");
        rowhead.createCell(4).setCellValue("notNudeAvgScore");
        rowhead.createCell(5).setCellValue("maybeNudeAvgScore");
        rowhead.createCell(6).setCellValue("kernelSize");
        rowhead.createCell(7).setCellValue("executionTime");
        rowhead.createCell(8).setCellValue("classificationLevel");

        int idx = 1;
        for (ClassificationResult classificationResult : classificationResults) {
            HSSFRow row = sheet.createRow((short) idx);
            row.createCell(0).setCellValue(classificationResult.getFileName());
            row.createCell(1).setCellValue(classificationResult.getFinalClassification());
            row.createCell(2).setCellValue(classificationResult.getDate());
            row.createCell(3).setCellValue(classificationResult.getNudeAvgScore());
            row.createCell(4).setCellValue(classificationResult.getNotNudeAvgScore());
            row.createCell(5).setCellValue(classificationResult.getMaybeNudeAvgScore());
            row.createCell(6).setCellValue(classificationResult.getKernelSize());
            row.createCell(7).setCellValue(classificationResult.getExecutionTime());
            row.createCell(8).setCellValue(classificationResult.getClassificationLevel());

            idx++;
        }
    }

    public void save() {
        //Write the workbook in file system
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(this.outputFile));
            workbook.write(out);
            out.close();
            System.out.println("Written successfully");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
