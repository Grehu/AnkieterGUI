package shared;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**@author Grzegorz M¹ka*/
public class Wyniki {
	private int numerPytania;
	private int wszystkie;
	
	private Pytanie pytanie;
	private ObslugaBazy baza;
	
	public ArrayList<Wynik> wyniki;
	
	private boolean gotowe;
	
	public class Wynik{
		
		protected String tresc;
		protected int licznik;
		private int wszystkie;
		
		Wynik(String tresc, int licznik, int wszystkie){
			this.tresc=tresc;
			this.licznik=licznik;
			this.wszystkie=wszystkie;
		}
		
		public int procent(){
			return 100*licznik/wszystkie;
		}
		
		public double procent (int precyzja){
			double procent = 100*(double)licznik/(double)wszystkie;
			double temp = (double) Math.pow(10.0, (double)precyzja);
			return Math.round(procent*temp)/temp ;
		}
		
		public String podajTresc(){
			return tresc;
		}
	}
	
	public Wyniki (int numerPytania, ObslugaBazy baza){
		this.numerPytania=numerPytania;
		this.baza=baza;
		wyniki = new ArrayList<Wynik>();
		gotowe = false;
		try{
			przygotujWyniki();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	private String zapytanie_wszystkie(){
		return "SELECT "
				+ "SUM(licznik) "
				+ "FROM wyniki "
				+ "WHERE ID_pytania="
				+Integer.toString(numerPytania)
				+ ";";
	}
	
	private String zapytanie_konkretne(){
		return "SELECT "
				+ "licznik "
				+ "FROM wyniki "
				+ "WHERE ID_pytania="
				+Integer.toString(numerPytania)
				+ " ORDER BY ID_elementu "
				+ ";";
	}
	
	private void przygotujWyniki() throws SQLException{
		pytanie = baza.pobierzZBazy(numerPytania);
		ResultSet wszystkie = baza.pobierzInne(zapytanie_wszystkie());
		ResultSet konkretne = baza.pobierzInne(zapytanie_konkretne());
		if (wszystkie == null || konkretne == null){
			System.out.println("Nie uda³o siê pobraæ wyniku zapytania.");
			return;
		}
		wszystkie.next();
		this.wszystkie=wszystkie.getInt(1);
		Wynik wynik;
		
		for (int i=0; konkretne.next(); i++){
			wynik = new Wynik(pytanie.pobierzOdpowiedz(i+1), konkretne.getInt(1), this.wszystkie);
			wyniki.add(wynik);
		}
		gotowe = true;
	}
	
	public String piszWyniki(){
		if (!gotowe)
			return "Wyniki nie s¹ jeszcze gotowe";
		String wiadomosc = "Treœæ pytania: "+pytanie.tresc;
		wiadomosc = wiadomosc.split("\n")[0];
		for (int i=0; i<wyniki.size(); i++){
			wiadomosc = wiadomosc + "\n" + wyniki.get(i).podajTresc() + " : " + Double.toString(wyniki.get(i).procent(2)) + "%";
		}
		return wiadomosc;
	}
	
}
