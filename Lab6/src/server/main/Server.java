package server.main;
/**@author Grzegorz M¹ka*/
import java.io.*;
import java.util.concurrent.*;

import shared.ObslugaBazy;

import java.net.*;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Server implements Callable<String>{
	/**Pole zawieraj¹ce numer ostatniego pytania, decyduj¹ce o zakoñczeniu programu.*/
	public static int ostatnie;
	/**Numer portu, przez jaki ma ³¹czyæ siê serwer z klientem*/
	public static int port = 10055;
	/**Konstruktor nie modyfikuj¹cy portu*/
	public Server(){
	}
	/**Konstruktor zmieniaj¹cy port*/
	public Server(int port){
		Server.port=port;
	}
	
	/**
	 * Flaga decyduj¹ca o dzia³aniu pêtli servera.
	 */
	public static boolean on = false;
	
	/**
	 * Metoda tworz¹ca bazê danych z ankietami
	 * @param baza - Obiekt odpowiedzialny za komunikacjê z baz¹ danych
	 */
	public static void stworzBazeAnkiet(ObslugaBazy baza){
		System.out.println("Trwa przygotowywanie bazy danych, proszê czekaæ...");
		String nazwa = "ankiety";
		ArrayList<String> listaKomend = new ArrayList<String>();
		listaKomend.add("CREATE TABLE pytania (ID_pytania int(3) NOT NULL, ID_elementu int(2) NOT NULL, tresc varchar(200) NOT NULL, typ enum('pytanie','pytanie wielokrotnego wyboru', 'odpowiedz') NOT NULL);");
		listaKomend.add("ALTER TABLE pytania ADD PRIMARY KEY (ID_pytania, ID_elementu);");
		listaKomend.add("CREATE TABLE odpowiedzi (ID_uzytkownika int(5) NOT NULL, ID_pytania int(3) NOT NULL, ID_elementu int(2) NOT NULL);");
		listaKomend.add("ALTER TABLE odpowiedzi ADD FOREIGN KEY (ID_pytania, ID_elementu) REFERENCES pytania (ID_pytania, ID_elementu);");
		listaKomend.add("ALTER TABLE odpowiedzi ADD PRIMARY KEY (ID_pytania, ID_elementu, ID_uzytkownika);");
		listaKomend.add("CREATE TABLE wyniki (ID_wyniku int(0) primary key, ID_pytania int(3) NOT NULL, ID_elementu int(2) NOT NULL, licznik int(10) DEFAULT 0);");
		listaKomend.add("ALTER TABLE wyniki ADD FOREIGN KEY (ID_pytania, ID_elementu) REFERENCES pytania (ID_pytania, ID_elementu);");
		
		listaKomend.add("INSERT INTO pytania VALUES (1, 0, 'W jakim stopniu Twoj umysl okreslilbys jako umysl scisly? \nWybierz jedna odpowiedz i podaj jej numer. ', 'pytanie');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 1, 'Bardzo wysokim', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 2, 'Wysokim', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 3, 'Niskim', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 4, 'Bardzo niskim', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 5, 'Trudno powiedziec', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (1, 6, 'Moj umysl jest wszechstronny', 'odpowiedz');");
		
		listaKomend.add("INSERT INTO pytania VALUES (2, 0, 'Wybierz posiadane przez ciebie talenty artystyczne.  \nPodawaj numery kolejnych odpowiedzi i zatwierdzaj kazdorazowo klawiszem enter.  \nAby zakonczyc odpowiadanie na to pytanie, wybierz 0.', 'pytanie wielokrotnego wyboru') ;");
		listaKomend.add("INSERT INTO pytania VALUES (2, 1, 'Muzyczne', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 2, 'Plastyczne', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 3, 'Poetyckie', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (2, 4, 'Aktorskie', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 5, 'Taneczne', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 6, 'Rezyserskie/inne filmowe', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 7, 'Prozatorskie', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (2, 8, 'Inne', 'odpowiedz');");
		
		listaKomend.add("INSERT INTO pytania VALUES (3, 0, 'Wybierz posiadane przez ciebie talenty do nauk scislych.  \nPodawaj numery kolejnych odpowiedzi i zatwierdzaj kazdorazowo klawiszem enter.  \nAby zakonczyc odpowiadanie na to pytanie, wybierz 0.', 'pytanie wielokrotnego wyboru');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 1, 'Matematyka', 'odpowiedz'); ");
		listaKomend.add("INSERT INTO pytania VALUES (3, 2, 'Programowanie', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 3, 'Wyobraznia przestrzenna', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 4, 'Mechanika', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 5, 'Chemia', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 6, 'Fizyka', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 7, 'Biologia', 'odpowiedz');");
		listaKomend.add("INSERT INTO pytania VALUES (3, 8, 'Inne', 'odpowiedz');");
		
		listaKomend.add("INSERT INTO wyniki VALUES (1,1,1,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (2,1,2,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (3,1,3,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (4,1,4,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (5,1,5,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (6,1,6,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (7,2,1,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (8,2,2,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (9,2,3,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (10,2,4,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (11,2,5,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (12,2,6,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (13,2,7,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (14,2,8,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (15,3,1,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (16,3,2,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (17,3,3,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (18,3,4,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (19,3,5,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (20,3,6,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (21,3,7,0);");
		listaKomend.add("INSERT INTO wyniki VALUES (22,3,8,0);");
		
		baza.stworzBaze(nazwa, listaKomend);
		System.out.println("Zakoñczono przygotowywanie bazy danych.");
	}
	/** Metoda pobierajaca aktualn¹ datê */
	public static String currentDateStr()
	{
		SimpleDateFormat simpleDateHere = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss (Z)");
        return simpleDateHere.format(new Date());
	}

	/**Program g³ówny serwera*/
	public String call() throws IOException {
			//Przygotowanie obiektów i pól odpowiedzialnych za komunikacjê z klientem i baz¹ danych

			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			ObslugaBazy baza = new ObslugaBazy();
			
			/*Przygotowanie obs³ugi w¹tków*/
			ExecutorService executor= Executors.newCachedThreadPool();
			FutureTask<String> rezultaty;
			
			/*£adowanie informacji o bazie danych:
			 * - Pobieranie informacji o numerze ostatniego pytania
			 * - Jeœli baza nie istnieje/jest pusta, próba utworzenia/uzupe³nienia bazy + powy¿sze
			 * - Jeœli baza ju¿ istnieje, pobranie ID ostatnio zapisanego w niej klienta, aby kontynuowaæ numeracjê
			 */
			ostatnie = baza.numerOstatniegoPytania();
			if (ostatnie<0){
				stworzBazeAnkiet(baza);
				ostatnie=baza.numerOstatniegoPytania();
			}
			else{
				ThreadServer.nextID=baza.numerOstatniegoKlienta()+1;
			}
			
			System.out.println("Serwer jest gotowy i oczekuje na klientów...");
			//W³aœciwa czêœæ programu - oczekiwanie na klienta i tworzenie dla niego nowego w¹tku
			while(on){
				while (on) {
					try {
						final Socket connectionSocket = serverSocket.accept();
						System.out.println("Po³¹czenie nazwi¹zane");
						rezultaty =new FutureTask<String>(new ThreadServer(connectionSocket));
						executor.execute(rezultaty);
					} 
					catch (Exception exc) {
						exc.printStackTrace();
					}
					
				}
			}
			serverSocket.close();
			return "Dzia³anie serwera zakoñczono "+currentDateStr();
			
	}
}
