package com.proyecto_backend.FoodHub.config;

import com.proyecto_backend.FoodHub.model.*;
import com.proyecto_backend.FoodHub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            KioskoRepository kioskoRepository,
            MenuRepository menuRepository,
            ResenaRepository resenaRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            System.out.println("🧹 Limpiando base de datos para la DEMO FINAL...");

            // 1. Limpieza total (orden importante por claves foráneas)
            resenaRepository.deleteAll();
            productoRepository.deleteAll();
            menuRepository.deleteAll();
            kioskoRepository.deleteAll();
            categoriaRepository.deleteAll();
            usuarioRepository.deleteAll();

            System.out.println("🌱 Sembrando datos...");

            // --- 2. USUARIOS ---

            // ADMIN
            Usuario admin = new Usuario();
            admin.setNombre("Admin Principal");
            admin.setCorreo("admin@foodhub.com");
            admin.setContrasena(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);

            // KIOSKERO (Vendedor general)
            Usuario vendedor = new Usuario();
            vendedor.setNombre("Doña Pepa");
            vendedor.setCorreo("ventas@kiosko.com");
            vendedor.setContrasena(passwordEncoder.encode("123456"));
            vendedor.setRol(Rol.KIOSKERO);
            usuarioRepository.save(vendedor);

            // CONCESIONARIO (Dueño del Comedor)
            Usuario concesionario = new Usuario();
            concesionario.setNombre("Concesionario UTEC");
            concesionario.setCorreo("menu@utec.edu.pe");
            concesionario.setContrasena(passwordEncoder.encode("123456"));
            concesionario.setRol(Rol.KIOSKERO);
            usuarioRepository.save(concesionario);

            // CLIENTE 1 (Aaron)
            Usuario cliente = new Usuario();
            cliente.setNombre("Aaron Navarro");
            cliente.setCorreo("aaron.navarro@foodhub.com");
            cliente.setContrasena(passwordEncoder.encode("123456"));
            cliente.setRol(Rol.CLIENTE);
            usuarioRepository.save(cliente);

            // CLIENTE 2 (Test solicitado)
            Usuario testUser = new Usuario();
            testUser.setNombre("Usuario Test");
            testUser.setCorreo("test@test.com");
            testUser.setContrasena(passwordEncoder.encode("123456"));
            testUser.setRol(Rol.CLIENTE);
            usuarioRepository.save(testUser);

            // --- 3. CATEGORÍAS ---
            Categoria catFondo = new Categoria(null, "Platos de Fondo", null);
            Categoria catBebidas = new Categoria(null, "Bebidas", null);
            Categoria catPostres = new Categoria(null, "Postres", null);
            Categoria catSnacks = new Categoria(null, "Snacks", null);
            Categoria catMenu = new Categoria(null, "Menú del Día", null);
            Categoria catEntrada = new Categoria(null, "Entradas", null);

            categoriaRepository.saveAll(Arrays.asList(catFondo, catBebidas, catPostres, catSnacks, catMenu, catEntrada));

            // --- 4. KIOSKOS ---
            Kiosko kiosko1 = new Kiosko();
            kiosko1.setNombre("Piatto");
            kiosko1.setUbicacion("Comedor Piso 1");
            kiosko1.setHorario("08:00 - 20:00");
            kiosko1.setDescripcion("Menús caseros y la mejor atención.");
            kioskoRepository.save(kiosko1);

            Kiosko kiosko2 = new Kiosko();
            kiosko2.setNombre("Café UTEC");
            kiosko2.setUbicacion("Piso 2");
            kiosko2.setHorario("09:00 - 18:00");
            kiosko2.setDescripcion("Café pasado, postres y sandwiches.");
            kioskoRepository.save(kiosko2);

            Kiosko kiosko3 = new Kiosko();
            kiosko3.setNombre("Kiosko 6to");
            kiosko3.setUbicacion("Piso 6");
            kiosko3.setHorario("09:00 - 18:00");
            kiosko3.setDescripcion("Almuerzos y bocadillos al paso.");
            kioskoRepository.save(kiosko3);

            Kiosko comedor = new Kiosko();
            comedor.setNombre("Comedor Central UTEC");
            comedor.setUbicacion("Piso 1");
            comedor.setHorario("12:00 - 15:00");
            comedor.setDescripcion("Menú variado y nutritivo para toda la comunidad universitaria.");
            kioskoRepository.save(comedor);

            // --- 5. PRODUCTOS INDEPENDIENTES (Con reseñas) ---

            Producto lomo = crearProducto(productoRepository, "Lomo Saltado Clásico", 22.50, "Jugoso lomo fino con papas fritas.", "https://www.eatperu.com/wp-content/uploads/2019/06/lomo-saltado-with-rice-and-cilantro-recipe.jpg?x28697", vendedor, catFondo, kiosko1);
            crearResena(resenaRepository, 5, "¡El mejor lomo de la UTEC! La carne super suave.", lomo, cliente);
            crearResena(resenaRepository, 4, "Muy bueno, pero me hubiera gustado más arroz.", lomo, testUser);

            Producto aji = crearProducto(productoRepository, "Ají de Gallina", 18.00, "Cremoso ají de gallina con papas amarillas.", "https://th.bing.com/th/id/R.3a8e647779b1d2d9ac1ac6e6764fafd5?rik=zHgNAUhpa82lVQ&pid=ImgRaw&r=0", vendedor, catFondo, kiosko1);
            crearResena(resenaRepository, 5, "Sabe como en casa, recomendado.", aji, testUser);

            Producto chaufa = crearProducto(productoRepository, "Arroz Chaufa", 16.50, "Chaufa de pollo con wantan frito.", "https://i0.wp.com/www.kusiperu.co/wp-content/uploads/2021/10/Chaufa-de-Pollo-con-Wantan.jpg?fit=600%2C400&ssl=1", vendedor, catFondo, kiosko1);

            Producto hamburguesa = crearProducto(productoRepository, "Hamburguesa Royal", 12.00, "Carne artesanal, huevo y queso.", "https://sumerlabs.com/sumer-app-90b8f.appspot.com/product_photos/288edb9df292e74c21e8e75d939e2f82/794d9b30-b236-11ec-a051-c51a15678625?alt=media&token=79e0a3ae-eb80-444b-9610-0d8041f3615e", vendedor, catFondo, kiosko2);
            crearResena(resenaRepository, 5, "¡Gigante! Vale totalmente la pena por el precio.", hamburguesa, cliente);

            // Bebidas y Postres
            crearProducto(productoRepository, "Jugo de Maracuyá (1L)", 8.00, "Jugo natural heladito.", "https://th.bing.com/th/id/OIP.lDPKkGPHR1KjKcshnukXJgHaEe?w=273&h=180&c=7&r=0&o=7&pid=1.7&rm=3", vendedor, catBebidas, kiosko1);
            crearProducto(productoRepository, "Chicha Morada", 5.00, "Chicha morada casera.", "https://th.bing.com/th/id/OIP.xxfY5erKQDcY-_r9pXNpeQHaEK?w=237&h=180&c=7&r=0&o=7&pid=1.7&rm=3", vendedor, catBebidas, kiosko1);
            crearProducto(productoRepository, "Café Americano", 4.50, "Café pasado caliente.", "https://th.bing.com/th/id/OIP.QLEr1MPoNgqptyBjUtIQQAHaE8?w=234&h=180&c=7&r=0&o=7&pid=1.7&rm=3", vendedor, catBebidas, kiosko2);
            crearProducto(productoRepository, "Pie de Limón", 6.50, "Porción generosa de pie de limón.", "https://th.bing.com/th/id/OIP.5TDUbxuTqeypf5rUh6lBPgHaFh?w=245&h=182&c=7&r=0&o=7&pid=1.7&rm=3", vendedor, catPostres, kiosko2);
            crearProducto(productoRepository, "Torta de Chocolate", 8.00, "Torta húmeda con fudge.", "https://th.bing.com/th/id/OIP.hxlsjxAioG2ZEnPpuf_USQHaHa?w=168&h=180&c=7&r=0&o=7&pid=1.7&rm=3", vendedor, catPostres, kiosko2);
            crearProducto(productoRepository, "Empanada de Pollo", 4.00, "Empanada horneada rellena de pollo.", "https://www.superpollo.cl/img/recetas/empanadas-de-pollo.jpg", vendedor, catSnacks, kiosko2);


            // --- 6. MENÚS SEMANALES (Llenado masivo para la demo) ---

            // === LUNES ===
            crearMenuDia(menuRepository, productoRepository, "LUNES",
                    "Seco de pollo con papas", "https://th.bing.com/th/id/R.e4f9959c0e0681cd27647af2ecb33a0b?rik=u7J938aB0N85Qw&pid=ImgRaw&r=0",
                    "Pollo al horno con lentejas", "https://tse3.mm.bing.net/th/id/OIP.F-nXdTThNk-RHVd5fONcrgHaEK?rs=1&pid=ImgDetMain&o=7&rm=3vi/B4z5Cg2pI9E/maxresdefault.jpg",
                    "Chicharrón de Pollo", "https://th.bing.com/th/id/OIP.Lhrks8dMEItQlndOB0itGQHaE8?w=209&h=180&c=7&r=0&o=7&pid=1.7&rm=3",
                    "Pollo en salsa oriental", "https://i.pinimg.com/originals/83/32/63/83326394557530458551932257336856.jpg",
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);

            // === MARTES ===
            crearMenuDia(menuRepository, productoRepository, "MARTES",
                    "Escabeche de pescado", "https://tse3.mm.bing.net/th/id/OIP.YXbf5RW0VUO91h6oQ0Qm8AHaFG?rs=1&pid=ImgDetMain&o=7&rm=3",
                    "Spaguetti a la boloñesa", "https://th.bing.com/th/id/OIP.lUm5vJOjpsw_XFKP_CnEjgHaE8?w=244&h=180&c=7&r=0&o=7&pid=1.7&rm=3",
                    "Pollo al sillao con puré", "https://buenazo.pe/files/recipes/pollo-al-sillao-1698423641.jpg",
                    "Cerdo al horno con ensalada", "https://cdn0.recetasgratis.net/es/images/8/5/0/img_cerdo_al_horno_jugoso_50558_600.jpg",
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);

            // === MIERCOLES ===
            crearMenuDia(menuRepository, productoRepository, "MIERCOLES",
                    "Picante de carne con arroz", "https://img-global.cpcdn.com/recipes/56d15643f4321705/680x482cq70/picante-de-carne-foto-principal.jpg",
                    "Ají de pollo con arroz", "https://comidasperuanas.net/wp-content/uploads/2015/06/Aji-de-gallina.jpg",
                    "Milanesa a lo pobre", "https://i.pinimg.com/736x/d8/6a/d2/d86ad2470205208015565d694c573519.jpg",
                    "Filete de pollo con champiñones", "https://t2.rg.ltmcdn.com/es/images/7/0/4/pechugas_de_pollo_en_salsa_de_champinones_7407_orig.jpg",
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);

            // === JUEVES ===
            crearMenuDia(menuRepository, productoRepository, "JUEVES",
                    "Pollo al horno con pallares", "https://i.ytimg.com/vi/q3RzGj8L9zU/maxresdefault.jpg",
                    "Saltado de pollo con papas", "https://i.ytimg.com/vi/1B5V6K1Q1gI/maxresdefault.jpg",
                    "Cerdo al horno con ensalada", "https://cdn0.recetasgratis.net/es/images/8/5/0/img_cerdo_al_horno_jugoso_50558_600.jpg",
                    "Pollo al horno con camote", "https://i.ytimg.com/vi/B4z5Cg2pI9E/maxresdefault.jpg",
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);

            // === VIERNES ===
            crearMenuDia(menuRepository, productoRepository, "VIERNES",
                    "Tallarín saltado de pollo", "https://i.ytimg.com/vi/8B6Z7Q7Q0Q0/maxresdefault.jpg",
                    "Pescado apanado con yuca", "https://i.ytimg.com/vi/9Q8Z7Q7Q0Q0/maxresdefault.jpg",
                    "Lasaña de carne", "https://t1.rg.ltmcdn.com/es/images/2/3/4/lasana_de_carne_picada_facil_y_rapida_53432_orig.jpg",
                    "Milanesa de pescado con ensalada", "https://i.ytimg.com/vi/7Q8Z7Q7Q0Q0/maxresdefault.jpg",
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);

            // === SABADO ===
            crearMenuDia(menuRepository, productoRepository, "SABADO",
                    "Menestrón de pollo", "https://comidasperuanas.net/wp-content/uploads/2020/09/Menestron.jpg",
                    "Pollada con papas doradas", "https://comidasperuanas.net/wp-content/uploads/2015/11/Pollada-peruana.jpg",
                    "Pollo al horno con ensalada rusa", "https://i.ytimg.com/vi/B4z5Cg2pI9E/maxresdefault.jpg",
                    null, null, // No hay saludable el sábado
                    concesionario, catMenu, catEntrada, catPostres, catBebidas, comedor);


            System.out.println("✅ ¡DATOS DE PRUEBA CARGADOS CORRECTAMENTE!");
            System.out.println("🔑 Login Demo: aaron.navarro@foodhub.com / 123456");
        };
    }

    // Helper para crear producto individual
    private Producto crearProducto(ProductoRepository repo, String nombre, double precio, String desc, String img, Usuario vendedor, Categoria cat, Kiosko kiosko) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setDescripcion(desc);
        p.setImageUrl(img);
        p.setVendedor(vendedor);
        p.setCategorias(List.of(cat));
        p.setKioskos(List.of(kiosko));
        return repo.save(p);
    }

    // Helper para crear reseña
    private void crearResena(ResenaRepository repo, int calificacion, String comentario, Producto producto, Usuario usuario) {
        Resena r = new Resena();
        r.setCalificacion(calificacion);
        r.setComentario(comentario);
        r.setProducto(producto);
        r.setUsuario(usuario);
        repo.save(r);
    }

    // Helper maestro para crear el menú de un día entero
    private void crearMenuDia(MenuRepository menuRepo, ProductoRepository prodRepo, String dia,
                              String nombreEco, String imgEco,
                              String nombreEst, String imgEst,
                              String nombreEje, String imgEje,
                              String nombreSal, String imgSal,
                              Usuario vendedor, Categoria catMenu, Categoria catEntrada, Categoria catPostre, Categoria catBebida, Kiosko kiosko) {

        // 1. Económico
        if (nombreEco != null) {
            Menu m = new Menu(); m.setDia(dia); m.setTipo(TipoMenu.ECONOMICO); m.setPrecio(10.60); menuRepo.save(m);
            crearPlatoMenu(prodRepo, nombreEco, "Menú Económico", 10.60, imgEco, vendedor, catMenu, kiosko, m);
            crearPlatoMenu(prodRepo, "Refresco", "Bebida", 0.0, null, vendedor, catBebida, kiosko, m);
        }

        // 2. Estudiantil
        if (nombreEst != null) {
            Menu m = new Menu(); m.setDia(dia); m.setTipo(TipoMenu.ESTUDIANTIL); m.setPrecio(13.60); menuRepo.save(m);
            crearPlatoMenu(prodRepo, nombreEst, "Menú Estudiantil", 13.60, imgEst, vendedor, catMenu, kiosko, m);
            crearPlatoMenu(prodRepo, "Sopa/Entrada", "Entrada del día", 0.0, null, vendedor, catEntrada, kiosko, m);
        }

        // 3. Ejecutivo
        if (nombreEje != null) {
            Menu m = new Menu(); m.setDia(dia); m.setTipo(TipoMenu.EJECUTIVO); m.setPrecio(15.40); menuRepo.save(m);
            crearPlatoMenu(prodRepo, nombreEje, "Menú Ejecutivo", 15.40, imgEje, vendedor, catMenu, kiosko, m);
            crearPlatoMenu(prodRepo, "Entrada Especial", "Entrada ejecutiva", 0.0, null, vendedor, catEntrada, kiosko, m);
        }

        // 4. Saludable
        if (nombreSal != null) {
            Menu m = new Menu(); m.setDia(dia); m.setTipo(TipoMenu.SALUDABLE); m.setPrecio(17.10); menuRepo.save(m);
            crearPlatoMenu(prodRepo, nombreSal, "Menú Saludable", 17.10, imgSal, vendedor, catMenu, kiosko, m);
            crearPlatoMenu(prodRepo, "Infusión", "Bebida caliente", 0.0, null, vendedor, catBebida, kiosko, m);
        }
    }

    private void crearPlatoMenu(ProductoRepository repo, String nombre, String desc, double precio, String img, Usuario vendedor, Categoria cat, Kiosko kiosko, Menu menu) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(desc);
        p.setPrecio(precio);
        p.setImageUrl(img != null ? img : "https://via.placeholder.com/150?text=Comedor+UTEC");
        p.setVendedor(vendedor);
        p.setCategorias(List.of(cat));
        p.setKioskos(List.of(kiosko));
        p.setMenu(menu);
        repo.save(p);
    }
}