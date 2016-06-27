/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.dao;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@Getter
@Setter
public class BodyWomanDescriptorDatabase {

    private List<BodyWomanDescriptor> bodyWomanDescriptors;

    public BodyWomanDescriptorDatabase() {
        bodyWomanDescriptors = new ArrayList<>();
    }

    public void addBodyWomanDescriptor(BodyWomanDescriptor bodyWomanDescriptor) {
        bodyWomanDescriptors.add(bodyWomanDescriptor);
    }

    public void addAllBodyWomanDescriptor(List<BodyWomanDescriptor> listbodyWomanDescriptors) {
        bodyWomanDescriptors.addAll(listbodyWomanDescriptors);
    }

    public void removeBodyWomanDescriptor(BodyWomanDescriptor bodyWomanDescriptor) {
        bodyWomanDescriptors.remove(bodyWomanDescriptor);
    }

    public void removeAllBodyWomanDescriptor(List<BodyWomanDescriptor> listbodyWomanDescriptors) {
        bodyWomanDescriptors.removeAll(listbodyWomanDescriptors);
    }

    public List<BodyWomanDescriptor> getNudeBodyWomanDescriptor() {
        List<BodyWomanDescriptor> result = new ArrayList();

        this.bodyWomanDescriptors.stream().filter((descriptor) -> (descriptor.isNude())).forEach((descriptor) -> {
            result.add(descriptor);
        });

        return result;
    }

    public List<BodyWomanDescriptor> getNotNudeBodyWomanDescriptor() {
        List<BodyWomanDescriptor> result = new ArrayList();

        this.bodyWomanDescriptors.stream().filter((descriptor) -> (!descriptor.isNude())).forEach((descriptor) -> {
            result.add(descriptor);
        });

        return result;
    }

    public List<BodyPartDescriptor> getAllBodyPartDescriptors() {
        List<BodyPartDescriptor> result = new ArrayList();

        for (BodyWomanDescriptor bodyWomanDescriptor : this.bodyWomanDescriptors) {
            result.addAll(bodyWomanDescriptor.getBodyPartDescriptors());
        }

        return result;
    }

    public List<BodyPartDescriptor> getAllNudeBodyPartDescriptors() {
        List<BodyPartDescriptor> result = new ArrayList();

        for (BodyWomanDescriptor bodyWomanDescriptor : this.bodyWomanDescriptors) {
            if (bodyWomanDescriptor.isNude()) {
                result.addAll(bodyWomanDescriptor.getBodyPartDescriptors());
            }
        }

        return result;
    }

    public List<BodyPartDescriptor> getAllNotNudeBodyPartDescriptors() {
        List<BodyPartDescriptor> result = new ArrayList();

        for (BodyWomanDescriptor bodyWomanDescriptor : this.bodyWomanDescriptors) {
            if (!bodyWomanDescriptor.isNude()) {
                result.addAll(bodyWomanDescriptor.getBodyPartDescriptors());
            }
        }

        return result;
    }

    public BodyPartDescriptor getNudeBodyPartDescriptorByName(String name) {

        for (BodyPartDescriptor bodyPartDescriptor : getAllNudeBodyPartDescriptors()) {
            if (bodyPartDescriptor.isNude() && bodyPartDescriptor.getName().equalsIgnoreCase(name)) {
                return bodyPartDescriptor;
            }
        }
        return null;
    }
    public BodyPartDescriptor getNotNudeBodyPartDescriptorByName(String name) {

        for (BodyPartDescriptor bodyPartDescriptor : getAllNotNudeBodyPartDescriptors()) {
            if (!bodyPartDescriptor.isNude() && bodyPartDescriptor.getName().equalsIgnoreCase(name)) {
                return bodyPartDescriptor;
            }
        }
        return null;
    }

}
