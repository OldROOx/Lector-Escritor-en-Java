class RecursoCompartido {
    private int lectoresActivos = 0;
    private boolean escritorActivo = false;
    private int dato = 0;


    public synchronized void iniciarLectura(int idLector) throws InterruptedException {

        while (escritorActivo) {
            wait();
        }

        lectoresActivos++;
        System.out.println("Lector " + idLector + " está LEYENDO. Lectores activos: " + lectoresActivos);
    }


    public synchronized void terminarLectura(int idLector) {
        lectoresActivos--;
        System.out.println("Lector " + idLector + " terminó de leer. Lectores activos: " + lectoresActivos);


        if (lectoresActivos == 0) {
            notifyAll();
        }
    }


    public synchronized void iniciarEscritura(int idEscritor) throws InterruptedException {

        while (lectoresActivos > 0 || escritorActivo) {
            wait();
        }

        escritorActivo = true;
        System.out.println("Escritor " + idEscritor + " está ESCRIBIENDO.");
    }

    public synchronized void terminarEscritura(int idEscritor) {
        escritorActivo = false;
        dato++;
        System.out.println("Escritor " + idEscritor + " terminó de escribir. Dato actualizado: " + dato);

       notifyAll();
    }


    public int leerDato() {
        return dato;
    }
}


class Lector extends Thread {
    private RecursoCompartido recurso;
    private int id;

    public Lector(RecursoCompartido recurso, int id) {
        this.recurso = recurso;
        this.id = id;
    }

    public void run() {
        try {
            for (int i = 0; i < 3; i++) {

                recurso.iniciarLectura(id);


                int valor = recurso.leerDato();
                System.out.println("  -> Lector " + id + " leyó el valor: " + valor);
                Thread.sleep(1000);


                recurso.terminarLectura(id);


                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Lector " + id + " interrumpido.");
        }
    }
}


class Escritor extends Thread {
    private RecursoCompartido recurso;
    private int id;

    public Escritor(RecursoCompartido recurso, int id) {
        this.recurso = recurso;
        this.id = id;
    }

    public void run() {
        try {
            for (int i = 0; i < 2; i++) {

                recurso.iniciarEscritura(id);


                Thread.sleep(1500);


                recurso.terminarEscritura(id);


                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Escritor " + id + " interrumpido.");
        }
    }
}


public class LectorEscritor {
    public static void main(String[] args) {
        System.out.println("=== Patrón Lector-Escritor (Prioridad Lectores) ===\n");


        RecursoCompartido recurso = new RecursoCompartido();


        Lector lector1 = new Lector(recurso, 1);
        Lector lector2 = new Lector(recurso, 2);
        Lector lector3 = new Lector(recurso, 3);


        Escritor escritor1 = new Escritor(recurso, 1);
        Escritor escritor2 = new Escritor(recurso, 2);


        lector1.start();
        escritor1.start();
        lector2.start();
        lector3.start();
        escritor2.start();


        try {
            lector1.join();
            lector2.join();
            lector3.join();
            escritor1.join();
            escritor2.join();
        } catch (InterruptedException e) {
            System.out.println("Hilo principal interrumpido.");
        }

        System.out.println("\n=== Programa terminado ===");
    }
}