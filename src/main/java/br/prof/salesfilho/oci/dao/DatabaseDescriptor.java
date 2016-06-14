/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.dao;

import br.prof.salesfilho.oci.domain.ImageDescriptor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
@Component
public class DatabaseDescriptor {

    private List<ImageDescriptor> womanNudeBust;
    private List<ImageDescriptor> womanNonNudeBust;

    public static int DESCRIPTOR_TYPE_BUST_NUDE = 1;
    public static int DESCRIPTOR_TYPE_BUST_NON_NUDE = 2;

    public DatabaseDescriptor() {
        womanNudeBust = new ArrayList<>();
        womanNonNudeBust = new ArrayList<>();
    }

    public void addWomanNudeBust(ImageDescriptor nudeBust) {
        womanNudeBust.add(nudeBust);
    }

    public void addWomanNonNudeBust(ImageDescriptor nonNudeBust) {
        womanNonNudeBust.add(nonNudeBust);
    }

}
