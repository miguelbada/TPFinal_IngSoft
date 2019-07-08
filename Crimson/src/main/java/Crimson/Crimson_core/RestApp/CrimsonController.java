package Crimson.Crimson_core.RestApp;


import Crimson.Crimson_core.*;
import Crimson.Crimson_core.JSON_Classes.DatosPeliUser;
import Crimson.Crimson_core.JSON_Holders.HPelicula;
import Crimson.Crimson_core.JSON_Holders.HSala;
import Crimson.Crimson_core.backend.repository.PeliculaRepository;
import Crimson.Crimson_core.Pelicula;
import Crimson.Crimson_core.backend.repository.PeliculaRepository;
import Crimson.Crimson_core.backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CrimsonController {

    private static final String template = "Esta es:";
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private JavaMailSender javaMailSender;

//    @PostConstruct
//    public void initialize() {
//        DataLoader loader = new DataLoader();
//        Cartelera cartelera = new Cartelera();
//        loader.crearSetDeDatos(cartelera);
//        DataManager dataManager = new DataManager(cartelera);
//        Intermodelo intermodelo = new Intermodelo(dataManager);
//    }

    @GetMapping(path = "/cartelera")
    public @ResponseBody Iterable<Pelicula> getAllPeliculas() {
        return peliculaRepository.findAll();
    }

    @GetMapping(path="/addPelicula") // Map ONLY GET Requests
    public @ResponseBody String addNewPelicula (@RequestParam String name
            , @RequestParam String genero, @RequestParam String clasificacion, @RequestParam String sinopsis) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Pelicula pelicula = new Pelicula(name, genero, clasificacion, sinopsis);

        peliculaRepository.save(pelicula);

        return "Saved";
    }

    @RequestMapping(path="/reservar",  method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity addReserva (@RequestBody Reserva reserva, @RequestParam String email, @RequestParam String nombrePelicula, @RequestParam String funcion) {
        reservaRepository.save(reserva);
        return new ResponseEntity(reserva, HttpStatus.CREATED);
    }

    @RequestMapping("/pelicula")
    public List<HPelicula> getPelicula() {
        HSala sala = new HSala(3, null, 30, 0, "2D");
        HPelicula peli = new HPelicula("Aladdin", 0001, "Aventura Romantica", "ATP", "Aladdin (Mena Massoud) es un adorable pero desafortunado ladronzuelo enamorado de la hija del Sultán, la princesa Jasmine (Naomi Scott). Para intentar conquistarla, acepta el desafío de Jafar (Marwan Kenzari), que consiste en entrar a una cueva en mitad del desierto para dar con una lámpara mágica que le concederá todos sus deseos. Allí es donde Aladdín conocerá al Genio (Will Smith), dando inicio a una aventura como nunca antes había imaginado", sala);
        List<HPelicula> lista = new ArrayList<>();
        lista.add(peli);
        return lista;
    }

    @RequestMapping("/<usuario>/peli/<codigo_peli>")
    public DatosPeliUser getDatosPelicula() {
        //TODO
        return null;
    }

    @RequestMapping(value = "/postPelicula", method = RequestMethod.POST)
    public HPelicula postPelicula(@RequestParam ("nombre") String nombre, @RequestParam("codigo") Integer codigo, @RequestParam("genero") String genero, @RequestParam("clasificacion") String clasificacion, @RequestParam ("sinopsis") String sinopsis) {
        return new HPelicula(nombre, codigo, genero, clasificacion, sinopsis, null);
    }

    @RequestMapping(value = "/agregarPelicula", method = RequestMethod.POST)
    public ResponseEntity createPelicula(@RequestBody Pelicula pelicula){
        peliculaRepository.save(pelicula);
        return new ResponseEntity(pelicula, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/emailTest", method = RequestMethod.PUT)
    public void enviarEmail(){

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("miguelenriquebada07@gmail.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }

    @RequestMapping(value = "/mailReserva", method = RequestMethod.PUT)
    public void sendSimpleMessage(@RequestParam String email, @RequestParam int dniUsuario, @RequestParam String nombrePelicula){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);

        msg.setSubject("Crimson reserva");

        Sala sala1 = new Sala(200, 1, "2D");
        Funcion funcion = new Funcion(sala1, "10-6-19 8:00:00");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
        String stringDate = sdf.format(funcion.getDate());

        msg.setText("Hola, gracias por confiar en nosotros. Su reserva para el dia y hora: " + stringDate + " para la pelicula " + nombrePelicula + " en la sala numero " + funcion.getNumeroSala() + " y la reserva esta vinculada al DNI: " + dniUsuario);

        javaMailSender.send(msg);

    }
}




