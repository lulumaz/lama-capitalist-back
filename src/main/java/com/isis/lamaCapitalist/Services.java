package com.isis.lamaCapitalist;


import generated.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lmazel
 */
public class Services {
    public World readWorldFromXml(){
        try {
            InputStream input =
                    getClass().getClassLoader().getResourceAsStream("world.xml");
            World w;
            JAXBContext jaxbContext = JAXBContext.newInstance(World.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            w = (World) unmarshaller.unmarshal(input);
            return w;
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public World readWorldFromXml(String pseudo){      
        try {
            InputStream input = new FileInputStream(getWorldFileName(pseudo));
            World w;
            JAXBContext jaxbContext;
            try {
                jaxbContext = JAXBContext.newInstance(World.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                w = (World) unmarshaller.unmarshal(input);
                return w;
            } catch (JAXBException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.WARNING, "Le monde de " +pseudo+" n'existe pas: création");
            World w = readWorldFromXml();
            saveWordlToXml(w,pseudo);   
            return w;            
        } 
    }
    
    public void saveWordlToXml(World world){
        //OutputStream output = new FileOutputStream(file);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(World.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(world, new File("world.xml"));
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
     public void saveWordlToXml(World world,String pseudo){
         //OutputStream output = new FileOutputStream(file);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(World.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(world, new File(getWorldFileName(pseudo)));
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
     
     private String getWorldFileName(String pseudo){
         return "./Worlds/world_" + pseudo + ".xml";
     }
}
