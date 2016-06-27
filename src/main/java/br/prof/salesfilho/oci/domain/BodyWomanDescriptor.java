/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter

public class BodyWomanDescriptor {

    private long id;
    private String name;
    private boolean nude;
    private List<BodyPartDescriptor> bodyPartDescriptors;

    
    public BodyWomanDescriptor() {
        bodyPartDescriptors = new ArrayList<>();
    }
    public BodyWomanDescriptor(String name) {
        bodyPartDescriptors = new ArrayList<>();
        this.name = name;
    }
    public BodyWomanDescriptor(String name, boolean nude) {
        bodyPartDescriptors = new ArrayList<>();
        this.name = name;
        this.nude = nude;
    }

    public void addBodyPart(BodyPartDescriptor bodyPartDescriptor) {
        bodyPartDescriptors.add(bodyPartDescriptor);
    }

    public void addAllBodyPart(List<BodyPartDescriptor> listBodyPartDescriptor) {
        bodyPartDescriptors.addAll(listBodyPartDescriptor);
    }

    public void removeBodyPart(BodyPartDescriptor bodyPartDescriptor) {
        bodyPartDescriptors.remove(bodyPartDescriptor);
    }

    public void removeAllBodyPart(List<BodyPartDescriptor> listBodyPartDescriptor) {
        bodyPartDescriptors.removeAll(listBodyPartDescriptor);
    }

}
