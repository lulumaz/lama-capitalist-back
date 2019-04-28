package com.isis.lamaCapitalist;


import generated.World;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
    
    public void saveWordlToXml(World world){
        //OutputStream output = new FileOutputStream(file);
    }
}
