/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.dao;

import br.prof.salesfilho.oci.domain.ImageDescriptor;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
public class DescriptorDAO {

    @Autowired
    private DatabaseDescriptor database;

    /**
     * Create DatabaseDescriptor object from XML string
     *
     * @param xmlDatabase
     */
    public void create(String xmlDatabase) {
        XStream xs = new XStream();
        xs.alias("DatabaseDescriptor", DatabaseDescriptor.class);
        xs.alias("ImageDescriptor", ImageDescriptor.class);

        this.database = (DatabaseDescriptor) xs.fromXML(xmlDatabase);
    }

    /**
     * Create DatabaseDescriptor object from XML file
     *
     * @param databaseFile
     */
    public void open(File databaseFile) {
        XStream xs = new XStream();
        xs.alias("DatabaseDescriptor", DatabaseDescriptor.class);
        xs.alias("ImageDescriptor", ImageDescriptor.class);

        if (databaseFile.exists()) {
            this.database = (DatabaseDescriptor) xs.fromXML(databaseFile);
        } else {
            this.save(databaseFile);
            this.database = (DatabaseDescriptor) xs.fromXML(databaseFile);
        }
    }

    public void save(File destination) {
        try {
            XStream xs = new XStream();
            xs.alias("DatabaseDescriptor", DatabaseDescriptor.class);
            xs.alias("ImageDescriptor", ImageDescriptor.class);
            xs.toXML(this.database, new FileOutputStream(destination));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DescriptorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toXML() {
        XStream xs = new XStream();
        xs.alias("DatabaseDescriptor", DatabaseDescriptor.class);
        xs.alias("ImageDescriptor", ImageDescriptor.class);
        return xs.toXML(this.database);
    }

    /**
     * @param descriptor
     * @param descritorType DESCRIPTOR_TYPE_BUST_NUDE = 1,
     * DESCRIPTOR_TYPE_BUST_NON_NUDE = 2
     */
    public void add(ImageDescriptor descriptor, int descritorType) {

        if (descritorType == DatabaseDescriptor.DESCRIPTOR_TYPE_BUST_NUDE) {
            database.addWomanNudeBust(descriptor);
        } else if (descritorType == DatabaseDescriptor.DESCRIPTOR_TYPE_BUST_NON_NUDE) {
            database.addWomanNonNudeBust(descriptor);
        }
    }

    public List<ImageDescriptor> findAll() {
        List<ImageDescriptor> result = new ArrayList();
        result.addAll(this.database.getWomanNonNudeBust());
        result.addAll(this.database.getWomanNudeBust());
        return result;
    }

    public List<ImageDescriptor> findAllWomanNonNudeBust() {
        return this.database.getWomanNonNudeBust();
    }

    public List<ImageDescriptor> findAllWomanNudeBust() {
        return this.database.getWomanNudeBust();
    }

}
