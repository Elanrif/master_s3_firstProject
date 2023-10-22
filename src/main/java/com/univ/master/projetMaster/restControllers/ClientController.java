package com.univ.master.projetMaster.restControllers;

import com.univ.master.projetMaster.entities.Client;
import com.univ.master.projetMaster.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository ;

    //changement de port dynamic voir dossier .properties
    @Value("${server.port}")
    private int port ;
    @Value("${pathImage}")
    private String path_image ;

    @GetMapping("/find-all")
    public List<Client> getAllClients(){

        return clientRepository.findAll();
    }
  @PostMapping("/add-client")
    public Client addClient(
          @RequestParam String name,
          @RequestParam Integer age,
          @RequestParam(required = false) MultipartFile file /* = false , pour ne pas avoir d'erreur si on envoie pas de photo*/
          ) throws IOException {

        Client client = new Client(null,name,age,null) ;
        client = clientRepository.save(client) ;

        /* cas ou on envoie ou pas de photo :
        * 1- file != null, si sur Postman ou autre ... le champ "file" n'existe pas
        * 2- file.isEmpty() si "file" exist mais on envoie rien (vide)   */
        if(file != null && !file.isEmpty()){
            String pathPhoto = path_image + client.getId() + ".png" ;
            file.transferTo(Path.of(pathPhoto));
            String urlPhoto = "http://localhost:"+port + "/client/photos/" + client.getId() ;
            client.setPhoto(urlPhoto);
        }

        return clientRepository.save(client) ;
    }

    @PutMapping("/update-client")
    public Client updateClient(
            @RequestParam Long id,
            @RequestParam(required = false) String name,/* peut être modifié ou pas*/
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) MultipartFile file /* = false , pour ne pas avoir d'erreur si on envoie pas de photo*/
    ) throws IOException {

        // or findById(id).get()
       Client client = clientRepository.findById(id).orElse(null) ;

        if(client!= null){
            String path = path_image + id + ".png";
             /*pour éviter une erreur : Cannot invoke \"org.springframework.web.multipart.MultipartFile.isEmpty()\" because \"file\" is null
              1- file != null, si sur Postman ou autre ... le champ "file" n'existe pas
              2- file.isEmpty() si "file" exist mais on envoie rien (vide)
              */
            if(file!=null && !file.isEmpty()) {

                File f = new File(path) ;
                if(f.exists()){
                    f.delete() ;
                }
                file.transferTo(Path.of(path));
                String urlPhoto = "http://localhost:"+port + "/client/photos/" + client.getId() ;
                client.setPhoto(urlPhoto);
            }

            /*Optinal.ofNullable... vérifier si la valeur est présente et de l'appliquer uniquement si c'est le cas*/
           /* Optional.ofNullable(name).ifPresent(client::setName);
            Optional.ofNullable(age).ifPresent(client::setAge);*/
            client.setName(name);
            client.setAge(age);
        }
        return clientRepository.save(client) ;
    }

    @GetMapping("/photos/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Long id){
        String path = path_image + id + ".png";
        FileSystemResource file = new FileSystemResource(path);
        if(!file.exists()){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file) ;
    }
    @DeleteMapping("/delete/{id}")
    public void deleteClient(@PathVariable("id") Long id){
        String path = path_image + id +".png" ;
        File f = new File(path) ;
        if(f.exists())
            f.delete() ;
        clientRepository.deleteById(id);
    }

}
