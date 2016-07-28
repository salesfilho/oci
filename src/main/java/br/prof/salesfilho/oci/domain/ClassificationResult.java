/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationResult {
    
    private String fileName;
    private String finalClassification;
    private String databaseName;
    private Date date;
    private double nudeAvgScore;
    private double notNudeAvgScore;
    private double maybeNudeAvgScore;
    private double kernelSize;
    private long executionTime;
    private int classificationLevel;
    
}
