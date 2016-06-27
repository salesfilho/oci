/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

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
public class BodyPartDescriptor extends ImageDescriptor {

    private long id;
    private boolean nude;
    private String name;

    public BodyPartDescriptor (String name){
        this.name = name;
    }
    public BodyPartDescriptor (String name, boolean nude){
        this.name = name;
        this.nude = nude;
    }
}
