package client.main;
/**@author Grzegorz M�ka*/
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.Callable;

import shared.Odpowiedzi;
import shared.Pytanie;

public class Klient implements Callable<String> {
	
	/** Metoda pobierajaca aktualn� dat� */
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
		
		//Obiekty przechowuj�ce dane ankiety
		Pytanie pytanie = new Pytanie("","");
		Odpowiedzi odpowiedzi= new Odpowiedzi();
		
		//Obiekty do wymiany danych z serwerem
		Socket clientSocket = new Socket("localhost", port); 
		ObjectInputStream nadchodzacePytanie;
        ObjectOutputStream wychodzacaOdpowiedz;
        
        //Flagi steruj�ce trybem dzia�ania programu
		boolean zakonczono = false;
		boolean dostarczonoPytanie = false;
		boolean pytanieJestWielokrotne = false;
		boolean przygotowanoOdpowiedz = false;
		
		//Obiekty do obs�ugi odpowiedzi wprowadzanych przez u�ytkownika
		Scanner zKlawiatury = new Scanner(System.in); 
		String klawiatura;
		
		while(!zakonczono){ 
			//Gdy pytanie nie zosta�o jeszcze przes�ane
			if (!dostarczonoPytanie && !przygotowanoOdpowiedz){
				try{
					//Pobranie pytania
					nadchodzacePytanie = new ObjectInputStream (clientSocket.getInputStream());
					pytanie = (Pytanie) nadchodzacePytanie.readObject();
					/*Po otrzymaniu pytania ustawienie flagi gotowo�ci pytania 
					 * oraz ustalenie warto�ci flagi oznaczaj�cej pytanie wielokrotnego wyboru*/
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
			//Gdy pytanie zosta�o przes�ane, ale u�ytkownik nie udzieli� odpowiedzi
			if (dostarczonoPytanie && !przygotowanoOdpowiedz){
				//Udzielenie pierwszej odpowiedzi
				klawiatura = zKlawiatury.nextLine();
				System.out.println("Wpisana warto��: "+klawiatura);
				while (!odpowiedzi.sprawdzOdpowiedz(klawiatura, pytanie)){
					System.out.println("Podana odpowied� jest nieprawid�owa.");
					klawiatura = zKlawiatury.nextLine();
				}
				/*Ustawienie flagi gotowo�ci odpowiedzi 
				 * albo 
				 * ��danie kolejnych odpowiedzi, je�li pytanie dopuszcza udzielenie kilku.*/
				if (!pytanieJestWielokrotne){
					przygotowanoOdpowiedz = true;
				}
				else{
					
					while (!odpowiedzi.zero(klawiatura)){
						System.out.println("Podaj kolejn� odpowied�: ");
						klawiatura = zKlawiatury.nextLine();
						odpowiedzi.sprawdzOdpowiedz(klawiatura, pytanie);
					}
					przygotowanoOdpowiedz = true;
				}
			}
			//Gdy u�ytkownik udzieli� odpowiedzi i ma zosta� ona przes�ana do ThreadServer servera
			if (przygotowanoOdpowiedz){
				try{
					wychodzacaOdpowiedz = new ObjectOutputStream(clientSocket.getOutputStream());  
					wychodzacaOdpowiedz.writeObject(odpowiedzi);    
					
					przygotowanoOdpowiedz=false;
					dostarczonoPytanie=false;
					odpowiedzi=new Odpowiedzi();
					
					//Ustawienie flagi decyduj�cej o wyj�ciu z g��wnej p�tli programu, je�li pytanie by�o ostatnim
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
		/*Zako�czenie dzia�ania programu*/
		clientSocket.close();
		zKlawiatury.close();
		System.out.println("Zako�czono przeprowadzanie ankiety. Dzi�kujemy!");
		return "Dzia�anie serwera zako�czono "+currentDateStr();
	}

}
