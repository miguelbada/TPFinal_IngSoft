package Crimson.Crimson_core.RestApp;


import Crimson.Crimson_core.*;
import Crimson.Crimson_core.JSON_Classes.DatosPeliUser;
import Crimson.Crimson_core.JSON_Holders.HPelicula;
import Crimson.Crimson_core.JSON_Holders.HSala;
import Crimson.Crimson_core.backend.repository.FuncionRepository;
import Crimson.Crimson_core.backend.repository.PeliculaRepository;
import Crimson.Crimson_core.Pelicula;
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
@CrossOrigin
public class CrimsonController {

    private static final String template = "Esta es:";
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private FuncionRepository funcionRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping(path = "/cartelera")
    public @ResponseBody Iterable<Pelicula> getAllPeliculas() {
        return peliculaRepository.findAll();
    }

    @GetMapping(path="/addPelicula/{name}/{genero}/{clasificacion}/{trailer}/{imagen}/{sinopsis}")
    public String addNewPelicula (@PathVariable(value = "name") String name, @PathVariable(value = "genero") String genero, @PathVariable(value = "clasificacion") String clasificacion,@PathVariable(value = "trailer") String trailer, @PathVariable(value = "imagen") String imagen ,@PathVariable(value = "sinopsis") String sinopsis) {
        Pelicula pelicula = new Pelicula(name, genero, clasificacion, new ArrayList<Funcion>(), trailer, imagen, sinopsis);
        peliculaRepository.save(pelicula);
        return "Saved";
    }

    @PostMapping(path="/removerPelicula/{id}")
    public void removerPelicula(@PathVariable(value = "id") String peliculaId){
        Pelicula pelicula = peliculaRepository.findById(peliculaId).get();
        peliculaRepository.delete(pelicula);
    }

    @GetMapping("/reservar/{funcion}/{nombre}/{dniUsuario}/{emailReserva}/{asientos}")
    public ResponseEntity addReserva(@PathVariable(value = "funcion") String funcion, @PathVariable(value = "nombre") String nombre, @PathVariable(value = "dniUsuario") String dniUsuario, @PathVariable(value = "emailReserva") String emailReserva, @PathVariable(value = "asientos") String asientos) throws AsientosInsuficientesException {
        Funcion funcionReserva = funcionRepository.findById(Integer.parseInt(funcion)).get();
        Reserva reserva = new Reserva(Integer.parseInt(asientos), Integer.parseInt(dniUsuario), emailReserva, nombre, funcionReserva);
        int asientosAReservar = reserva.getAsientos();
        funcionReserva.reservarAsientos(asientosAReservar);
        reservaRepository.save(reserva);
        this.mailReserva(reserva);
        return new ResponseEntity(reserva, HttpStatus.CREATED);
    }

    @GetMapping("/pelicula/{id}")
    public Pelicula getPeliculaById(@PathVariable(value = "id") String peliculaId) {
        Pelicula pelicula = peliculaRepository.findById(peliculaId).get();
        pelicula.removerFuncionesLlenas();
        return pelicula;
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
    public void mailReserva(Reserva reserva ){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(reserva.getEmailReserva());

        msg.setSubject("Crimson reserva");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
        String stringDate = sdf.format(reserva.getFuncion().getDate());

        msg.setText("Hola, su reserva fue exitosa. La misma es para el dia y hora: " + stringDate + ", para la pelicula " + reserva.getNombrePelicula() + " en la sala numero " + reserva.getFuncion().getNumeroSala() + " y la reserva esta vinculada al DNI: " + reserva.getDniUsuario());

        javaMailSender.send(msg);

    }
}




