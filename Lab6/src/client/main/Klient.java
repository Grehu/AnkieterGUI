package client.main;
/**@author Grzegorz M¹ka*/
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.Callable;

import shared.Odpowiedzi;
import shared.Pytanie;

public class Klient implements Callable<String> {
	
	/** Metoda pobierajaca aktualn¹ datê */
	public String currentDateStr()
	{
	  String currDate;
	  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	  java.util.Date date = new java.util.Date();
	  currDate = dateFormat.format(date);

	  return (currDate);
	}

	public String call() throws Exception {
		
		int port = 10055;
		
		//Obiekty przechowuj¹ce dane ankiety
		Pytanie pytanie = new Pytanie("","");
		Odpowiedzi odpowiedzi= new Odpowiedzi();
		
		//Obiekty do wymiany danych z serwerem
		Socket clientSocket = new Socket("localhost", port); 
		ObjectInputStream nadchodzacePytanie;
        ObjectOutputStream wychodzacaOdpowiedz;
        
        //Flagi steruj¹ce trybem dzia³ania programu
		boolean zakonczono = false;
		boolean dostarczonoPytanie = false;
		boolean pytanieJestWielokrotne = false;
		boolean przygotowanoOdpowiedz = false;
		
		//Obiekty do obs³ugi odpowiedzi wprowadzanych przez u¿ytkownika
		Scanner zKlawiatury = new Scanner(System.in); 
		String klawiatura;
		
		while(!zakonczono){ 
			//Gdy pytanie nie zosta³o jeszcze przes³ane
			if (!dostarczonoPytanie && !przygotowanoOdpowiedz){
				try{
					//Pobranie pytania
					nadchodzacePytanie = new ObjectInputStream (clientSocket.getInputStream());
					pytanie = (Pytanie) nadchodzacePytanie.readObject();
					/*Po otrzymaniu pytania ustawienie flagi gotowoœci pytania 
					 * oraz ustalenie wartoœci flagi oznaczaj¹cej pytanie wielokrotnego wyboru*/
					if (pytanie != null){
						System.out.println("\n" + pytanie.piszWiadomosc());
						dostarczonoPytanie = true;
						pytanieJestWielokrotne = pytanie.wielokrotnosc();
					}
				}
				catch (Exception e){
					System.err.println(e);
					zakonczono = true;
				}
			}
			//Gdy pytanie zosta³o przes³ane, ale u¿ytkownik nie udzieli³ odpowiedzi
			if (dostarczonoPytanie && !przygotowanoOdpowiedz){
				//Udzielenie pierwszej odpowiedzi
				klawiatura = zKlawiatury.nextLine();
				System.out.println("Wpisana wartoœæ: "+klawiatura);
				while (!odpowiedzi.sprawdzOdpowiedz(klawiatura, pytanie)){
					System.out.println("Podana odpowiedŸ jest nieprawid³owa.");
					klawiatura = zKlawiatury.nextLine();
				}
				/*Ustawienie flagi gotowoœci odpowiedzi 
				 * albo 
				 * ¿¹danie kolejnych odpowiedzi, jeœli pytanie dopuszcza udzielenie kilku.*/
				if (!pytanieJestWielokrotne){
					przygotowanoOdpowiedz = true;
				}
				else{
					
					while (!odpowiedzi.zero(klawiatura)){
						System.out.println("Podaj kolejn¹ odpowiedŸ: ");
						klawiatura = zKlawiatury.nextLine();
						odpowiedzi.sprawdzOdpowiedz(klawiatura, pytanie);
					}
					przygotowanoOdpowiedz = true;
				}
			}
			//Gdy u¿ytkownik udzieli³ odpowiedzi i ma zostaæ ona przes³ana do ThreadServer servera
			if (przygotowanoOdpowiedz){
				try{
					wychodzacaOdpowiedz = new ObjectOutputStream(clientSocket.getOutputStream());  
					wychodzacaOdpowiedz.writeObject(odpowiedzi);    
					
					przygotowanoOdpowiedz=false;
					dostarczonoPytanie=false;
					odpowiedzi=new Odpowiedzi();
					
					//Ustawienie flagi decyduj¹cej o wyjœciu z g³ównej pêtli programu, jeœli pytanie by³o ostatnim
					if (pytanie.ostatnie){
						zakonczono = true;
					}
				}
				catch (Exception e){
					System.err.println(e);
					zakonczono = true;
				}
			}
		}
		/*Zakoñczenie dzia³ania programu*/
		clientSocket.close();
		zKlawiatury.close();
		System.out.println("Zakoñczono przeprowadzanie ankiety. Dziêkujemy!");
		return "Dzia³anie serwera zakoñczono "+currentDateStr();
	}

}
