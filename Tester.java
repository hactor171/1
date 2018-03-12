/*
 * Program Klient-Serwer
 * Autor: Roman Kovalchuk
 * Data: 28 stycznia 2017 r.
 */

class Tester {
    public static void main(String[] args) {
        new Serwer();
        try {
            Thread.sleep(1000);
        } catch (Exception i) {}

        
        new Klient("Ewa");
        new Klient("Adam");
    }

}
