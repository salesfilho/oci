/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.dao;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class BodyWomanDescriptorDAO {

    @Autowired
    private BodyWomanDescriptorDatabase database;

    /**
     * Create DatabaseDescriptor object from XML string
     *
     * @param xmlDatabase
     */
    public void create(String xmlDatabase) {
        XStream xs = new XStream();
        xs.alias("BodyWomanDescriptorDatabase", BodyWomanDescriptorDatabase.class);
        xs.alias("BodyWomanDescriptor", BodyWomanDescriptor.class);
        xs.alias("BodyPartDescriptor", BodyPartDescriptor.class);
        this.database = (BodyWomanDescriptorDatabase) xs.fromXML(xmlDatabase);
    }

    /**
     * Create DatabaseDescriptor object from XML file
     *
     * @param databaseFile
     */
    public void open(File databaseFile) {
        XStream xs = new XStream();
        xs.alias("BodyWomanDescriptorDatabase", BodyWomanDescriptorDatabase.class);
        xs.alias("BodyWomanDescriptor", BodyWomanDescriptor.class);
        xs.alias("BodyPartDescriptor", BodyPartDescriptor.class);
        if (databaseFile.exists()) {
            this.database = (BodyWomanDescriptorDatabase) xs.fromXML(databaseFile);
        } else {
            this.save(databaseFile);
            this.database = (BodyWomanDescriptorDatabase) xs.fromXML(databaseFile);
        }
    }

    public void save(File destination) {
        try {
            XStream xs = new XStream();
            xs.alias("BodyWomanDescriptorDatabase", BodyWomanDescriptorDatabase.class);
            xs.alias("BodyWomanDescriptor", BodyWomanDescriptor.class);
            xs.alias("BodyPartDescriptor", BodyPartDescriptor.class);
            xs.toXML(this.database, new FileOutputStream(destination));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BodyWomanDescriptorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void save(File destination, boolean isNude) {
        try {
            XStream xs = new XStream();
            xs.alias("BodyWomanDescriptorDatabase", BodyWomanDescriptorDatabase.class);
            xs.alias("BodyWomanDescriptor", BodyWomanDescriptor.class);
            xs.alias("BodyPartDescriptor", BodyPartDescriptor.class);
            if (isNude) {
                BodyWomanDescriptorDatabase nudeDatabase = new BodyWomanDescriptorDatabase();
                nudeDatabase.addAllBodyWomanDescriptor(this.database.getNudeBodyWomanDescriptor());
                xs.toXML(nudeDatabase, new FileOutputStream(destination));
            }else{
                BodyWomanDescriptorDatabase newDatabase = new BodyWomanDescriptorDatabase();
                newDatabase.addAllBodyWomanDescriptor(this.database.getNotNudeBodyWomanDescriptor());
                xs.toXML(newDatabase, new FileOutputStream(destination));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BodyWomanDescriptorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toXML() {
        XStream xs = new XStream();
        xs.alias("BodyWomanDescriptorDatabase", BodyWomanDescriptorDatabase.class);
        xs.alias("BodyWomanDescriptor", BodyWomanDescriptor.class);
        xs.alias("BodyPartDescriptor", BodyPartDescriptor.class);
        return xs.toXML(this.database);
    }

    /**
     * @param descriptor
     */
    public void add(BodyWomanDescriptor descriptor) {
        database.addBodyWomanDescriptor(descriptor);
    }

    public List<BodyWomanDescriptor> findAll() {
        return database.getBodyWomanDescriptors();
    }

    public List<BodyWomanDescriptor> findAllNotNudeBodyWomanDescriptor() {
        return this.database.getNotNudeBodyWomanDescriptor();
    }

    public List<BodyWomanDescriptor> findAllNudeBodyWomanDescriptor() {
        return this.database.getNudeBodyWomanDescriptor();
    }

    public List<BodyPartDescriptor> findAllBodyPartDescriptors() {
        return this.database.getAllBodyPartDescriptors();
    }

    public List<BodyPartDescriptor> findAllNudeBodyPartDescriptors() {
        return this.database.getAllNudeBodyPartDescriptors();
    }

    public List<BodyPartDescriptor> findAllNotNudeBodyPartDescriptors() {
        return this.database.getAllNotNudeBodyPartDescriptors();
    }

    public BodyPartDescriptor findNotNudeBodyPartDescriptorByName(String name) {
        return this.database.getNotNudeBodyPartDescriptorByName(name);
    }

    public BodyPartDescriptor findNudeBodyPartDescriptorByName(String name) {
        return this.database.getNudeBodyPartDescriptorByName(name);
    }

}
