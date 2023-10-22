package com.univ.master.projetMaster.restControllers;

import com.univ.master.projetMaster.entities.Client;
import com.univ.master.projetMaster.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository ;

    //changement de port dynamic voir dossier .properties
    @Value("${server.port}")
    private int port ;

    @GetMapping("/find-all")
    public List<Client> getAllClients(){

        return clientRepository.findAll();
    }
  @PostMapping("/add-client")
    public Client addClient(
          @RequestParam Long id,
          @RequestParam String name,
          @RequestParam int age,
          @RequestParam(required = false) MultipartFile file
          ) throws IOException {

        String pathPhoto = "src/main/resources/static/photos" + id + ".png" ;
        file.transferTo(Path.of(pathPhoto));
        String urlPhoto = "http://localhost:"+port + "/photos/" + id ;
        Client client = new Client(id,name,age,urlPhoto);
        return clientRepository.save(client) ;
    }

}
