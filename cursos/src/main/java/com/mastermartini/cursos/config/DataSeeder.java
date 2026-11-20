package com.mastermartini.cursos.config;

import com.mastermartini.cursos.model.Curso;
import com.mastermartini.cursos.model.Leccion;
import com.mastermartini.cursos.model.Modulo;
import com.mastermartini.cursos.repository.CursoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Carga los 6 cursos iniciales en la tabla "capacitaciones" si está vacía.
// Los textos son EXACTAMENTE los de la app (CapacitacionRepository.kt): 3 básicos gratis y 3 del Plan Pro.
// Todos quedan a nombre del usuario instructor@example.com (id 2 en el microservicio usuarios).
// Los videos (videoResumenUrl y videoUrl de cada lección) quedan en null: después se pondrán los links de AWS S3.
// @Component: registra la clase en Spring para que la cree e inyecte sus dependencias.
// Al implementar CommandLineRunner, Spring ejecuta el método run() una vez, apenas arranca la aplicación.
@Component
public class DataSeeder implements CommandLineRunner {

    // Id del usuario instructor@example.com en el microservicio usuarios.
    private static final Long INSTRUCTOR_ID = 2L;

    private final CursoRepository cursoRepository;

    // Spring entrega el repositorio por el constructor (inyección de dependencias).
    public DataSeeder(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    // Inserta los 6 cursos, en orden, solo si la tabla está vacía (así no se duplican al reiniciar).
    // Al guardar cada curso también se guardan sus módulos y lecciones, gracias a cascade = ALL.
    // @Override: indica que este método implementa el run() de CommandLineRunner.
    @Override
    public void run(String... args) {
        if (cursoRepository.count() > 0) {
            return;
        }
        cursoRepository.saveAll(List.of(
            // 1. Chocolatería desde cero: templado y bombones (Básico, gratis)
            new Curso(
                "Chocolatería desde cero: templado y bombones",
                "Aprende a templar chocolate puro y a hacer tus primeros bombones rellenos, listos para vender.",
                "Chocolatería", "Básico", true,
                INSTRUCTOR_ID, "Chef Elena Quiroga",
                "2 h 5 min", "Chocolate Ariba",
                null, false,
                List.of(
                    "Templar chocolate sin termómetro usando el método de la tabla",
                    "Saber si el chocolate quedó bien templado con la prueba del papel",
                    "Moldear bombones y sellarlos con ganache adentro",
                    "Calcular cuántos bombones rinde 1 kg de chocolate",
                    "Guardar los bombones para que no pierdan su brillo"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Recetario descargable"
                ),
                List.of(
                    new Modulo("Fundamentos del chocolate", List.of(
                        leccion("Chocolate puro y cobertura: la diferencia",
                                "Por qué el chocolate puro necesita templado y la cobertura no.", "10 min"),
                        leccion("Derretir sin quemar",
                                "Baño maría y microondas en tandas de 30 segundos.", "12 min"),
                        leccion("Cómo guardar el chocolate",
                                "Temperatura y humedad para que no se ponga blanco.", "8 min")
                    )),
                    new Modulo("Templado paso a paso", List.of(
                        leccion("El método de la tabla",
                                "Enfriar el chocolate sobre mármol y volver a juntarlo.", "20 min"),
                        leccion("La prueba del papel",
                                "Comprobar el templado sin termómetro en 5 minutos.", "10 min"),
                        leccion("Errores comunes y cómo arreglarlos",
                                "Chocolate opaco, con vetas o que no se despega del molde.", "15 min")
                    )),
                    new Modulo("Tus primeros bombones", List.of(
                        leccion("Moldear los cascarones",
                                "Llenar, vaciar y raspar el molde para una capa pareja.", "18 min"),
                        leccion("Rellenar con ganache y sellar",
                                "Ganache firme que no se sale y tapa lisa.", "20 min"),
                        leccion("Rendimiento y costo de 1 kg",
                                "Cuántos bombones salen y cuánto cuesta cada uno.", "12 min")
                    ))
                )
            ),
            // 2. Masas dulces: brioche y pan de leche (Básico, gratis)
            new Curso(
                "Masas dulces: brioche y pan de leche",
                "Prepara masas dulces suaves y esponjosas para vender en tu panadería todos los días.",
                "Panadería", "Básico", true,
                INSTRUCTOR_ID, "Maestro panadero Hugo Retamal",
                "1 h 40 min", "Master Export Horneo",
                null, false,
                List.of(
                    "Reconocer cuándo la masa está lista con la prueba de la ventana",
                    "Hacer fermentar la masa en casa, sin cámara de fermentación",
                    "Formar un brioche trenzado y un pan de leche en molde",
                    "Calcular el costo de cada pan para ponerle precio"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Planilla de costos descargable"
                ),
                List.of(
                    new Modulo("La masa base", List.of(
                        leccion("Ingredientes y para qué sirve cada uno",
                                "Harina, levadura, azúcar, huevo y margarina de horneo.", "12 min"),
                        leccion("Amasar a mano y la prueba de la ventana",
                                "Cómo saber que la masa ya está lista para fermentar.", "18 min")
                    )),
                    new Modulo("Fermentar y formar", List.of(
                        leccion("Fermentar sin cámara",
                                "Usar el horno apagado con una olla de agua caliente.", "15 min"),
                        leccion("Trenzas, bollos y moldes",
                                "Tres formas de presentar la misma masa.", "20 min"),
                        leccion("Pincelar y hornear",
                                "Brillo dorado sin quemar la base.", "15 min")
                    )),
                    new Modulo("Vender en tu local", List.of(
                        leccion("Costo por unidad",
                                "Sumar ingredientes y dividir por las unidades que salen.", "12 min"),
                        leccion("Guardar y presentar el pan",
                                "Que el pan siga blando al día siguiente.", "8 min")
                    ))
                )
            ),
            // 3. Decoración de tortas con crema batida (Básico, gratis)
            new Curso(
                "Decoración de tortas con crema batida",
                "Bate, cubre y decora tortas con crema vegetal, con técnicas simples que se ven profesionales.",
                "Decoración", "Básico", true,
                INSTRUCTOR_ID, "Chef Paula Ibarra",
                "1 h 45 min", "Bravo Crem",
                null, false,
                List.of(
                    "Batir Bravo Crem hasta el punto firme sin que se corte",
                    "Cubrir y alisar una torta de 22 cm con espátula",
                    "Hacer rosetas, conchitas y bordes con tres boquillas",
                    "Teñir la crema con colorante sin que pierda firmeza",
                    "Decorar una torta de cumpleaños completa en 15 minutos"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Guía de boquillas descargable"
                ),
                List.of(
                    new Modulo("Batir la crema vegetal", List.of(
                        leccion("La temperatura antes de batir",
                                "Por qué la crema y el bol deben estar bien fríos.", "8 min"),
                        leccion("Batir hasta el punto firme",
                                "Reconocer el punto justo antes de que se corte.", "12 min"),
                        leccion("Teñir la crema",
                                "Cuánto colorante usar y cuándo agregarlo.", "10 min")
                    )),
                    new Modulo("Manga y boquillas", List.of(
                        leccion("Llenar y sostener la manga",
                                "Postura de la mano para no cansarse.", "10 min"),
                        leccion("Rosetas, conchitas y bordes",
                                "Tres boquillas básicas y lo que se hace con cada una.", "20 min")
                    )),
                    new Modulo("La torta terminada", List.of(
                        leccion("Cubrir y alisar",
                                "Capa delgada, frío y capa final lisa.", "18 min"),
                        leccion("Torta de cumpleaños en 15 minutos",
                                "Un diseño rápido que se puede repetir todos los días.", "15 min"),
                        leccion("Transportar la torta sin dañarla",
                                "Base, caja y temperatura durante el viaje.", "12 min")
                    ))
                )
            ),
            // 4. Hojaldre y croissants de vitrina (Intermedio, Plan Pro)
            new Curso(
                "Hojaldre y croissants de vitrina",
                "Domina el laminado para hacer croissants, palmeras y vol au vent con capas bien marcadas.",
                "Panadería", "Intermedio", false,
                INSTRUCTOR_ID, "Chef Tomás Riquelme",
                "2 h 20 min", "Gran Master Hoja",
                null, false,
                List.of(
                    "Laminar masa de hoja con 3 vueltas dobles sin que se rompa",
                    "Cortar y enrollar croissants de 60 g, todos del mismo tamaño",
                    "Hornear croissants con capas marcadas y sin que se derrame la margarina",
                    "Preparar palmeras y vol au vent con los recortes de masa",
                    "Congelar masa laminada para producir toda la semana"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Recetario descargable",
                    "Tabla de tiempos de laminado"
                ),
                List.of(
                    new Modulo("El laminado", List.of(
                        leccion("La masa base o empaste",
                                "Una masa firme que aguante las vueltas.", "15 min"),
                        leccion("Preparar la margarina para laminar",
                                "Darle forma de lámina a Gran Master Hoja.", "12 min"),
                        leccion("Vueltas simples y dobles",
                                "Cómo doblar y cuánto reposar entre vueltas.", "25 min")
                    )),
                    new Modulo("Croissants", List.of(
                        leccion("Cortar y enrollar",
                                "Triángulos parejos para croissants iguales.", "20 min"),
                        leccion("Fermentar y hornear",
                                "Temperaturas para que la margarina no se derrita antes de tiempo.", "18 min")
                    )),
                    new Modulo("Más productos de hojaldre", List.of(
                        leccion("Palmeras",
                                "Aprovechar los recortes con azúcar.", "15 min"),
                        leccion("Vol au vent dulces y salados",
                                "Cortar, hornear y rellenar.", "20 min"),
                        leccion("Congelar para producir por semana",
                                "Envolver, rotular y descongelar la masa.", "15 min")
                    ))
                )
            ),
            // 5. Heladería vegana: sorbetes sin leche (Intermedio, Plan Pro)
            new Curso(
                "Heladería vegana: sorbetes sin leche",
                "Prepara sorbetes cremosos de chocolate y de fruta, sin leche, para clientes veganos.",
                "Heladería", "Intermedio", false,
                INSTRUCTOR_ID, "Chef Carmen Sepúlveda",
                "1 h 50 min", "Ariba Zero Latto",
                null, true,
                List.of(
                    "Preparar sorbete vegano de chocolate amargo con Ariba Zero Latto",
                    "Hacer sorbete cremoso sin máquina, revolviendo cada 30 minutos",
                    "Ajustar el azúcar para que el sorbete no quede duro como hielo",
                    "Hacer sorbetes con la fruta de la temporada",
                    "Rotular tus helados con sus ingredientes para clientes veganos"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Recetario vegano descargable"
                ),
                List.of(
                    new Modulo("Bases veganas", List.of(
                        leccion("Cremoso sin leche: cómo se logra",
                                "El papel del azúcar, el agua y el chocolate.", "10 min"),
                        leccion("Equilibrar el azúcar",
                                "Cuánto azúcar lleva un sorbete para que no se congele como hielo.", "15 min")
                    )),
                    new Modulo("Sorbete de chocolate amargo", List.of(
                        leccion("La base con Ariba Zero Latto",
                                "Derretir el chocolate en el almíbar caliente.", "15 min"),
                        leccion("Madurar la mezcla",
                                "Enfriar en el refrigerador antes de congelar.", "15 min"),
                        leccion("Sin máquina heladera",
                                "Congelar y revolver cada 30 minutos.", "15 min")
                    )),
                    new Modulo("Fruta y venta", List.of(
                        leccion("Sorbetes de fruta de temporada",
                                "Frutilla, mango y frambuesa con la misma base.", "18 min"),
                        leccion("Rotular para clientes veganos",
                                "Qué escribir en la etiqueta del pote.", "10 min"),
                        leccion("Guardar y servir en vitrina",
                                "Temperatura de la vitrina y cómo servir la bola.", "12 min")
                    ))
                )
            ),
            // 6. Tortas mousse y entremets de vitrina (Avanzado, Plan Pro)
            new Curso(
                "Tortas mousse y entremets de vitrina",
                "Arma tortas mousse de varias capas con glaseado espejo, y organiza su producción para vender.",
                "Pastelería", "Avanzado", false,
                INSTRUCTOR_ID, "Chef Andrés Molina",
                "2 h 40 min", "Decor Up",
                null, false,
                List.of(
                    "Armar una torta mousse de 3 capas al revés, en molde con acetato",
                    "Preparar una mousse firme con Decor Up que se corte limpia",
                    "Bañar una torta congelada con glaseado espejo a 35 °C",
                    "Calcular el costo y el precio de venta de una torta de 20 porciones",
                    "Organizar la producción de 10 tortas en una semana"
                ),
                List.of(
                    "Certificado al aprobar la evaluación final",
                    "Acceso sin límite de tiempo",
                    "Recetario descargable",
                    "Planilla de producción semanal"
                ),
                List.of(
                    new Modulo("Planificar un entremet", List.of(
                        leccion("Las capas y el orden de armado",
                                "Bizcocho, mousse, inserto y glaseado.", "15 min"),
                        leccion("Moldes, acetato y tiempos de frío",
                                "Qué se prepara el día antes y qué el mismo día.", "12 min")
                    )),
                    new Modulo("Mousses y rellenos", List.of(
                        leccion("Mousse con Decor Up",
                                "Una mousse firme que no se desarma al cortar.", "20 min"),
                        leccion("Inserto de fruta",
                                "Un centro de fruta congelado que da sabor y color.", "18 min"),
                        leccion("Ganache para relleno",
                                "Proporciones para un relleno cremoso.", "15 min")
                    )),
                    new Modulo("Armado y terminación", List.of(
                        leccion("Armar al revés y congelar",
                                "Por qué la base queda arriba dentro del molde.", "20 min"),
                        leccion("Glaseado espejo",
                                "Bañar la torta congelada a la temperatura justa.", "25 min")
                    )),
                    new Modulo("Producir para vender", List.of(
                        leccion("Costear una torta de 20 porciones",
                                "Costo por porción y precio de venta.", "15 min"),
                        leccion("10 tortas en una semana",
                                "Un calendario de producción para no trabajar de noche.", "20 min")
                    ))
                )
            )
        ));
    }

    // Atajo para crear una lección sin video (videoUrl = null por ahora).
    private static Leccion leccion(String titulo, String descripcion, String duracion) {
        return new Leccion(titulo, descripcion, duracion, null);
    }
}
