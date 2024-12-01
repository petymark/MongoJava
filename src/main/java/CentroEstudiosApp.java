import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class CentroEstudiosApp {
    public static void main(String[] args) {

        String connectionString = "mongodb+srv://PeterMark:1234@centro-estudios.1ew3u.mongodb.net/?retryWrites=true&w=majority&appName=centro-estudios";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();


        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {

                MongoDatabase database = mongoClient.getDatabase("centro-estudios");
                System.out.println("Conexión exitosa a la base de datos: centro-estudios");


                MongoCollection<Document> alumnosCollection = database.getCollection("alumnos");
                MongoCollection<Document> profesoresCollection = database.getCollection("profesores");

                Scanner scanner = new Scanner(System.in);
                int opcion;

                do {
                    mostrarMenu();
                    System.out.print("Elige una opción: ");
                    opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case 1 -> insertarProfesor(profesoresCollection, scanner);
                        case 2 -> insertarAlumno(alumnosCollection, scanner);
                        case 3 -> mostrarTodos(alumnosCollection, profesoresCollection);
                        case 4 -> mostrarProfesores(profesoresCollection);
                        case 5 -> mostrarAlumnos(alumnosCollection);
                        case 6 -> buscarAlumno(alumnosCollection, scanner);
                        case 7 -> buscarProfesorPorEdad(profesoresCollection, scanner);
                        case 8 -> actualizarProfesor(profesoresCollection, scanner);
                        case 9 -> darDeBajaAlumnos(alumnosCollection);
                        case 10 -> System.out.println("Saliendo del sistema...");
                        default -> System.out.println("Opción inválida. Intente de nuevo.");
                    }
                } while (opcion != 10);

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("Menú de opciones:");
        System.out.println("1. Insertar un profesor");
        System.out.println("2. Insertar un alumno");
        System.out.println("3. Mostrar todos los datos");
        System.out.println("4. Mostrar profesores");
        System.out.println("5. Mostrar alumnos");
        System.out.println("6. Buscar alumno por email");
        System.out.println("7. Buscar profesor por rango de edad");
        System.out.println("8. Actualizar calificación de un profesor");
        System.out.println("9. Dar de baja alumnos con calificación >= 5");
        System.out.println("10. Salir");
    }

    private static void insertarProfesor(MongoCollection<Document> collection, Scanner scanner) {
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Edad: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Género: ");
        String gender = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Teléfono: ");
        String phone = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Asignaturas (separadas por comas): ");
        String[] subjects = scanner.nextLine().split(",");

        Document profesor = new Document("name", name)
                .append("age", age)
                .append("gender", gender)
                .append("email", email)
                .append("phone", phone)
                .append("title", title)
                .append("subjects", Arrays.asList(subjects));

        collection.insertOne(profesor);
        System.out.println("Profesor insertado exitosamente.");
    }

    private static void insertarAlumno(MongoCollection<Document> collection, Scanner scanner) {
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Edad: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Género: ");
        String gender = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Teléfono: ");
        String phone = scanner.nextLine();
        System.out.print("Calificación: ");
        int calification = scanner.nextInt();
        System.out.print("Nota más alta: ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Ciclo: ");
        String higherGrade = scanner.nextLine();

        Document alumno = new Document("name", name)
                .append("age", age)
                .append("gender", gender)
                .append("email", email)
                .append("phone", phone)
                .append("calification", calification)
                .append("rating", rating)
                .append("higher_grade", higherGrade);

        collection.insertOne(alumno);
        System.out.println("Alumno insertado exitosamente.");
    }

    private static void mostrarTodos(MongoCollection<Document> alumnos, MongoCollection<Document> profesores) {
        System.out.println("-- Alumnos --");
        for (Document doc : alumnos.find()) {
            System.out.println(doc.toJson());
        }

        System.out.println("-- Profesores --");
        for (Document doc : profesores.find()) {
            System.out.println(doc.toJson());
        }
    }

    private static void mostrarProfesores(MongoCollection<Document> collection) {
        System.out.println("-- Profesores --");
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }

    private static void mostrarAlumnos(MongoCollection<Document> collection) {
        System.out.println("-- Alumnos --");
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }

    private static void buscarAlumno(MongoCollection<Document> collection, Scanner scanner) {
        System.out.print("Email del alumno: ");
        String email = scanner.nextLine();
        Document alumno = collection.find(eq("email", email)).first();
        if (alumno != null) {
            System.out.println(alumno.toJson());
        } else {
            System.out.println("Alumno no encontrado.");
        }
    }

    private static void buscarProfesorPorEdad(MongoCollection<Document> collection, Scanner scanner) {
        System.out.print("Edad mínima: ");
        int minAge = scanner.nextInt();
        System.out.print("Edad máxima: ");
        int maxAge = scanner.nextInt();

        for (Document doc : collection.find(and(gte("age", minAge), lte("age", maxAge)))) {
            System.out.println(doc.toJson());
        }
    }

    private static void actualizarProfesor(MongoCollection<Document> collection, Scanner scanner) {
        System.out.print("Email del profesor: ");
        String email = scanner.nextLine();
        System.out.print("Nueva calificación: ");
        double rating = scanner.nextDouble();

        collection.updateOne(eq("email", email), set("rating", rating));
        System.out.println("Calificación actualizada.");
    }

    private static void darDeBajaAlumnos(MongoCollection<Document> collection) {
        collection.deleteMany(gte("calification", 5));
        System.out.println("Alumnos con calificación >= 5 eliminados.");
    }
}
