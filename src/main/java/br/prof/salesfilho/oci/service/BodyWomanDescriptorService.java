/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.dao.BodyWomanDescriptorDAO;
import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class BodyWomanDescriptorService {

    @Autowired
    private BodyWomanDescriptorDAO dao;

    public void openDatabase(String xmlDatabase) {
        dao.create(xmlDatabase);
    }

    public void openDatabase(File xmlDatabase) {
        dao.open(xmlDatabase);
    }

    public void save(File xmlDatabase) {
        dao.save(xmlDatabase);
    }

    public String toXML() {
        return dao.toXML();
    }

    public void add(BodyWomanDescriptor descriptor) {
        dao.add(descriptor);
    }

    public List<BodyWomanDescriptor> findAll() {
        return dao.findAll();
    }

    public List<BodyWomanDescriptor> findAllNotNudeBodyWomanDescriptor() {
        return dao.findAllNotNudeBodyWomanDescriptor();
    }

    public List<BodyWomanDescriptor> findAllNudeBodyWomanDescriptor() {
        return dao.findAllNudeBodyWomanDescriptor();
    }

    public List<BodyPartDescriptor> findAllBodyPartDescriptors() {
        return this.dao.findAllBodyPartDescriptors();
    }

    public List<BodyPartDescriptor> findAllNudeBodyPartDescriptors() {
        return this.dao.findAllNudeBodyPartDescriptors();
    }

    public List<BodyPartDescriptor> findAllNotNudeBodyPartDescriptors() {
        return this.dao.findAllNotNudeBodyPartDescriptors();
    }

    public BodyPartDescriptor findNotNudeBodyPartDescriptorByName(String name) {
        return this.dao.findNotNudeBodyPartDescriptorByName(name);
    }

    public BodyPartDescriptor findNudeBodyPartDescriptorByName(String name) {
        return this.dao.findNudeBodyPartDescriptorByName(name);
    }
}
