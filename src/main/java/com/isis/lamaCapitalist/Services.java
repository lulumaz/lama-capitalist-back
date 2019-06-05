package com.isis.lamaCapitalist;


import generated.PallierType;
import generated.PalliersType;
import generated.ProductType;
import generated.ProductsType;
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
            updatePoducts(w);
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
                updatePoducts(w);
                
                
                
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
     
     
    private ProductType findProductById(World world, int id  ){
        ProductsType products = world.getProducts();
        for (ProductType product : products.getProduct()) {
            if(product.getId() == id){
                return product;
            }
        }
        return null;
    }
     
    // prend en paramètre le pseudo du joueur et le produit
    // sur lequel une action a eu lieu (lancement manuel de production ou
    // achat d’une certaine quantité de produit)
    // renvoie false si l’action n’a pas pu être traitée
    public Boolean updateProduct(String username, ProductType newproduct) {
        // aller chercher le monde qui correspond au joueur
        World world;
        if(username!=null){
           world = readWorldFromXml(username);
        } else {
           world = readWorldFromXml();
        }
        // trouver dans ce monde, le produit équivalent à celui passé
        // en paramètre
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) { 
            System.err.println("Le produit du monde " + username + " n'a pas été trouvé : id "+newproduct.getId());
            return false;
        }
        
        // calculer la variation de quantité. Si elle est positive c'est
        // que le joueur a acheté une certaine quantité de ce produit
        // sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        System.out.println("qtchange"+qtchange);
        if (qtchange > 0) {
            // soustraire de l'argent du joueur le cout de la quantité
            // achetée et mettre à jour la quantité de product
            world.setMoney(world.getMoney() - qtchange * product.getCout());
            product.setQuantite(qtchange + product.getQuantite());
        } else {
            // initialiser product.timeleft à product.vitesse
            // pour lancer la production
             product.setTimeleft(product.getVitesse());
             world.setLastupdate(System.currentTimeMillis());
        }
        
        //System.err.println(world.getProducts().getProduct().get(0).getTimeleft());
        
        // sauvegarder les changements du monde
        saveWordlToXml(world, username);
        return true;
    }
     public Boolean updateProduct(ProductType newproduct) {
         return updateProduct(null,newproduct);
     }
    private PallierType findManagerByName(World world, String managerName){
        PalliersType managers = world.getManagers();
        for (PallierType manager : managers.getPallier()) {
            if(manager.getName()== managerName){
                return manager;
            }
        }
        return null;
    }
    
    // prend en paramètre le pseudo du joueur et le manager acheté.
    // renvoie false si l’action n’a pas pu être traitée
    public Boolean updateManager(String username, PallierType newmanager) {
        // aller chercher le monde qui correspond au joueur
        World world;
        if(username!=null){
           world = readWorldFromXml(username);
        } else {
           world = readWorldFromXml();
        }
        // trouver dans ce monde, le manager équivalent à celui passé
        // en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        // débloquer ce manager
        // trouver le produit correspondant au manager
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        // débloquer le manager de ce produit
        // soustraire de l'argent du joueur le cout du manager
        // sauvegarder les changements au monde
        saveWordlToXml(world,username);
        return true;
    }
    
    public Boolean updateManager(PallierType newmanager) {
        return updateManager(null, newmanager);
    }

    private void updatePoducts(World w) {//met à jours tous les produits
       float timeDiff = System.currentTimeMillis() - w .getLastupdate();
       for(ProductType p : w.getProducts().getProduct()){
           
           if(p.getTimeleft() <= timeDiff && p.getTimeleft()>0){
               //ajout du revenu du produit dans le score
               PallierType manager = findManager(w,p);
              
                w.setMoney(p.getRevenu()*p.getQuantite() + w.getMoney());
                p.setTimeleft(0);
                if(manager != null && manager.isUnlocked()){
                    w.setMoney(p.getRevenu()*p.getQuantite()*p.getVitesse()/timeDiff + w.getMoney());
                }
                   
                w.setLastupdate(System.currentTimeMillis());
                              
           }
           
       }
        
    }
    
    private PallierType findManager(World w, ProductType p){
        
        for ( PallierType manager : w.getManagers().getPallier()){
            if(manager.getIdcible() == p.getId()){
                return manager;
            }
        }
        
        return null;
    }
     
}
