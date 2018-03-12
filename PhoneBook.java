/*
 * Program Klient-Serwer
 * Autor: Roman Kovalchuk
 * Data: 28 stycznia 2017 r.
 */
import java.io.*;
import java.util.concurrent.*;

class PhoneBook {
     ConcurrentHashMap<String, String> phonebook = new ConcurrentHashMap<>();

    public PhoneBook() {
    }

    synchronized String get(String nick) {
        if (!phonebook.containsKey(nick)) return "Nick nie istnieje";
        return phonebook.get(nick);
    }

    synchronized String put(String nick, String numer) {
    	if(numer.length() == 0) return "Numer nie wpisany";
        if (numer.length() < 9) return "Wprowadzono mniej od potrzebnej ilości cyfr";
        if(numer.length() > 9) return "Wprowadzono więcej od potrzebnej ilości cyfr";
        if (phonebook.containsKey(nick)) return " Nick zajęty";
        phonebook.put(nick, numer);
        return Ok();
    }

    synchronized String replace(String nick, String numer) {
    	if(numer.length() == 0) return "Numer nie wpisany";
        if (numer.length() < 9) return "Wprowadzono mniej od potrzebnej ilości cyfr";
        if(numer.length() > 9) return "Wprowadzono więcej od potrzebnej ilości cyfr";
        if (!phonebook.containsKey(nick)) return "Nick nie istnieje";
        phonebook.replace(nick, numer);
        return Ok();
    }

    synchronized String delete(String nick) {
        if (!phonebook.containsKey(nick)) return "Nick nie istnieje";
        phonebook.remove(nick);
        return Ok();
    }
    synchronized String save(String fileName) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(phonebook);
        out.close();
        return Ok();
    }
    
    synchronized String load(String fileName) throws Exception {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
        phonebook = (ConcurrentHashMap<String, String>) input.readObject();
        input.close();
        return Ok();
    }
    synchronized String list() {
        StringBuilder builderlist = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : phonebook.entrySet()) {
        	builderlist.append("Nick:").append(entry.getKey()).append("\n");
        }
        return builderlist.toString();
    }

    private String Ok() {
        return "<Ok>";
    }

}
