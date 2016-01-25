package server.main;
/**@author Grzegorz M¹ka*/
import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;

import shared.ObslugaBazy;
import shared.Odpowiedzi;
import shared.Pytanie;
import shared.Wyniki;


public class ThreadServer implements Callable<String>{
	Socket connectionSocket;
	public static int nextID;
	public int ID;

	/**Funkcja nadaj¹ca identyfikator*/
	public synchronized int nadajID(){
		int ID = nextID;
		nextID++;
		System.out.println("Nadano nowemu klientowi ID = "+Integer.toString(ID));
		return ID;
	}
	
	/**Funkcja podsumowuj¹ca i zapisuj¹ca wyniki ankiety (niedokoñczone)*/
	public static void wyniki () throws FileNotFoundException{
		PrintWriter zapis = new PrintWriter("wyniki.txt");
	      zapis.close();
	}
	
	 /**Konstruktor obiektu
	  * @param connectionSocket - gniazdo, przez które serwer ³¹czy siê z klientem, 
	  * przekazywane przez serwer w trakcie tworzenia w¹tku*/
	public ThreadServer(Socket connectionSocket)
	{
		super(); 
		this.connectionSocket = connectionSocket;
	}
 
	/**W¹tek serwera dla obs³ugi klienta*/
	public String call() 
	{
		String problemy="";
		
		ID = nadajID();
		int wskaznik=1;
		int ostatnie=Server.ostatnie;
		
		boolean nastepne=true;
		boolean zakonczono = false;
		
		ObslugaBazy baza = new ObslugaBazy();
		ObjectOutputStream wychodzacePytanie;
		ObjectInputStream nadchodzacaOdpowiedz;
		
		
		Pytanie pytanie;
		Odpowiedzi odpowiedzi;
		

		
		while(!zakonczono){
			try{
				if(nastepne){
					/*Pobieranie pytania z bazy i zmiana flagi sygnalizuj¹cej 
					 * potrzebê pobrania nastêpnego pytania.
					 */
					pytanie=baza.pobierzZBazy(wskaznik);
					wskaznik++;
					nastepne=false;
					
					/*Dopisanie do ostatniego pytania informacji o tym, ¿e jest ono ostatnie*/
					if (wskaznik-1==ostatnie){
						pytanie.ostatnie = true;
					}
					
					/*Wys³anie pobranego pytania klientowi*/
					wychodzacePytanie = new ObjectOutputStream(connectionSocket.getOutputStream());   
					wychodzacePytanie.writeObject(pytanie);
				}
			}
			catch (Exception e){
				System.err.println(e);
				problemy=problemy+"\n"+e.getMessage();
				zakonczono = true;
			}
			
			try{
				if (!nastepne){
					
					/*Przyjêcie odpowiedzi od klienta*/
					nadchodzacaOdpowiedz =  new ObjectInputStream(connectionSocket.getInputStream());
					odpowiedzi = (Odpowiedzi) nadchodzacaOdpowiedz.readObject();
					
					/*Zapis odpowiedzi w bazie danych*/
					System.out.println("Próba zapisu odpowiedzi klienta w bazie...");
					baza.zapiszPrzygotowaneOdpowiedzi(odpowiedzi.piszListeSQL(ID, wskaznik-1));
					
					/*Zwolnienie blokady nastêpnego pytania, lub blokady zakoñczenia w¹tku,
					 * jeœli pytanie by³o pytaniem ostatnim.
					 */
					nastepne = true;
					if (ostatnie==wskaznik-1){
						zakonczono = true;
					}
					
				}
				
			}
			catch (Exception e){
				System.err.println(e.getMessage());
				System.out.println("Zamykanie w¹tku obs³ugi klienta "+Integer.toString(ID)+" ");
				problemy=problemy+"\n"+e.getMessage();
				zakonczono = true;
			}
		}
		//Wyœwietlenie wyników
		Wyniki wyniki;
		System.out.println("Oto aktualne wyniki ankiety: ");
		for (int i=1; i<=ostatnie; i++){
			wyniki = new Wyniki(i, baza);
			System.out.println("\n"+wyniki.piszWyniki());
		}
		System.out.println("Zakoñczono przeprowadzanie ankiety.");
		return "Obs³uga klienta "+Integer.toString(ID)+" zakoñczona. Lista problemów: "+problemy;
	}
}
